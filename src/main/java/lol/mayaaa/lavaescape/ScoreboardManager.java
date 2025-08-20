package lol.mayaaa.lavaescape;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ScoreboardManager {

    private final LavaEscape plugin;

    public ScoreboardManager(LavaEscape plugin) {
        this.plugin = plugin;
    }

    public void updateScoreboard(Player player, Game game) {
        if (game == null || game.getState() != Game.GameState.RUNNING) {
            return;
        }

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("lavaescape", "dummy");
        objective.setDisplayName("§c§lLavaEscape");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        String date = dateFormat.format(new Date());

        long elapsed = System.currentTimeMillis() - game.getStartTime();
        String time = formatTime(elapsed);

        long timeSinceLastRise = elapsed % 15000;
        int secondsUntilNextRise = 15 - (int)(timeSinceLastRise / 1000);
        String nextRise = secondsUntilNextRise + "s";

        if (secondsUntilNextRise == 0) {
            nextRise = "0s";
        }

        objective.getScore("§7serverip").setScore(0);
        objective.getScore("    ").setScore(1);
        objective.getScore("§7Level: §a" + game.getLavaLevel() + "§8/§2150").setScore(2);
        objective.getScore("§7Next Lava Rise: §e" + nextRise).setScore(3);
        objective.getScore("   ").setScore(4);
        objective.getScore("§7Difficulty: §bEasy").setScore(5);
        objective.getScore("§7Players: §a" + game.getPlayerCount()).setScore(6);
        objective.getScore("  ").setScore(7);
        objective.getScore("§7Time: §e" + time).setScore(8);
        objective.getScore(" ").setScore(9);
        objective.getScore("§7§o" + date).setScore(10);

        player.setScoreboard(board);
    }

    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        return String.format("%dm %02ds", minutes, seconds);
    }
}