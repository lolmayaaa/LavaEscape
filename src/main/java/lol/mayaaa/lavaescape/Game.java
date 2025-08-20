package lol.mayaaa.lavaescape;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

public class Game {

    private final LavaEscape plugin;
    private final Arena arena;
    private GameState state = GameState.WAITING;
    private final Set<UUID> players = new HashSet<>();
    private int lavaLevel = 0;
    private BukkitTask lavaTask;
    private long startTime;
    private int countdown = 5;
    private BukkitTask scoreboardTask;
    private int lavaRiseCount = 0;
    private boolean debugMode = false;

    public Game(LavaEscape plugin, Arena arena) {
        this.plugin = plugin;
        this.arena = arena;
    }

    public void start() {
        if (state != GameState.WAITING) return;

        state = GameState.STARTING;
        startTime = System.currentTimeMillis();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (countdown <= 0) {
                    cancel();
                    startGame();
                    return;
                }

                Bukkit.broadcastMessage("§8[§cLavaEscape§8] §7Game starting in §c" + countdown + "§7...");
                countdown--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }

    private void startGame() {
        state = GameState.RUNNING;
        lavaLevel = 0;
        lavaRiseCount = 0;
        startTime = System.currentTimeMillis();

        World world = arena.getSpawn().getWorld();
        WorldBorder border = world.getWorldBorder();
        border.setCenter(arena.getSpawn().getX(), arena.getSpawn().getZ());
        border.setSize(100);
        border.setDamageAmount(0);
        border.setWarningDistance(5);
        border.setWarningTime(0);

        ScoreboardManager scoreboardManager = new ScoreboardManager(plugin);
        for (UUID playerId : players) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                player.teleport(arena.getSpawn());
                player.setGameMode(GameMode.SURVIVAL);
                scoreboardManager.updateScoreboard(player, this);
            }
        }

        scoreboardTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (state != GameState.RUNNING) {
                    cancel();
                    return;
                }
                updateAllScoreboards();
            }
        }.runTaskTimer(plugin, 20L, 20L);

        startLavaRising();
    }

    private void updateAllScoreboards() {
        ScoreboardManager scoreboardManager = new ScoreboardManager(plugin);
        for (UUID playerId : players) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                scoreboardManager.updateScoreboard(player, this);
            }
        }
    }

    private void clearLava() {
        World world = arena.getSpawn().getWorld();
        int centerX = arena.getSpawn().getBlockX();
        int centerZ = arena.getSpawn().getBlockZ();

        for (int y = 0; y <= lavaLevel + 5; y++) {
            for (int x = centerX - 50; x <= centerX + 50; x++) {
                for (int z = centerZ - 50; z <= centerZ + 50; z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() == Material.LAVA) {
                        block.setType(Material.AIR);
                    }
                }
            }
        }
    }

    public void endGame() {
        if (state == GameState.ENDED) return;

        state = GameState.ENDED;

        if (lavaTask != null) {
            lavaTask.cancel();
        }
        if (scoreboardTask != null) {
            scoreboardTask.cancel();
        }

        clearAllScoreboards();

        clearLava();

        for (UUID playerId : players) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null && arena.getLobbySpawn() != null) {
                player.teleport(arena.getLobbySpawn());
                player.setGameMode(GameMode.SURVIVAL);
            }
        }

        World world = arena.getSpawn().getWorld();
        WorldBorder border = world.getWorldBorder();
        border.reset();

        if (players.size() == 1) {
            UUID winnerId = players.iterator().next();
            Player winner = Bukkit.getPlayer(winnerId);
            if (winner != null) {
                Bukkit.broadcastMessage("§8[§cLavaEscape§8] §c§l" + winner.getName() + " §7has won the game!");
            }
        } else if (players.isEmpty()) {
            Bukkit.broadcastMessage("§8[§cLavaEscape§8] §7The game ended with no winner!");
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                reset();
            }
        }.runTaskLater(plugin, 200L);
    }

    public void stop() {
        if (lavaTask != null) {
            lavaTask.cancel();
        }
        if (scoreboardTask != null) {
            scoreboardTask.cancel();
        }
        reset();
    }

    public void reset() {
        players.clear();
        state = GameState.WAITING;
        countdown = 5;
        plugin.getGameManager().removeGame(arena.getName());
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
        Bukkit.broadcastMessage("§8[§cLavaEscape§8] §c" + player.getName() + " §7has §ajoined §7the game! (§c" + players.size() + "§8/§c16§7)");
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());

        if (arena.getLobbySpawn() != null) {
            player.teleport(arena.getLobbySpawn());
            player.setGameMode(GameMode.SURVIVAL);
        }

        if (state != GameState.RUNNING) {
            Bukkit.broadcastMessage("§8[§cLavaEscape§8] §c" + player.getName() + " §7has §cleft §7the game! (§c" + players.size() + "§8/§c16§7)");
        }
    }


    private void startLavaRising() {
        lavaTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (state != GameState.RUNNING) {
                    cancel();
                    return;
                }

                lavaLevel++;
                lavaRiseCount++;
                World world = arena.getSpawn().getWorld();

                int centerX = arena.getSpawn().getBlockX();
                int centerZ = arena.getSpawn().getBlockZ();

                for (int x = centerX - 50; x <= centerX + 50; x++) {
                    for (int z = centerZ - 50; z <= centerZ + 50; z++) {
                        Block block = world.getBlockAt(x, lavaLevel, z);
                        if (block.getType() != Material.AIR) {
                            block.setType(Material.LAVA);
                        }
                    }
                }

                Bukkit.broadcastMessage("§8[§cLavaEscape§8] §7The lava is on level §c" + lavaLevel);

                if (debugMode) {
                    Bukkit.broadcastMessage("§8[§cDEBUG§8] §7Lava rise #" + lavaRiseCount + " at Y=" + lavaLevel);
                }

                if (players.size() <= 1) {
                    endGame();
                }
            }
        }.runTaskTimer(plugin, 300L, 300L);
    }

    public void toggleDebug() {
        debugMode = !debugMode;
        Bukkit.broadcastMessage("§8[§cDEBUG§8] §7Debug mode " + (debugMode ? "§aenabled" : "§cdisabled"));
    }

    public boolean isPlaying(Player player) {
        return players.contains(player.getUniqueId());
    }

    public int getPlayerCount() { return players.size(); }
    public int getLavaLevel() { return lavaLevel; }
    public GameState getState() { return state; }
    public long getStartTime() { return startTime; }

    public Set<UUID> getPlayers() {
        return players;
    }

    public enum GameState {
        WAITING, STARTING, RUNNING, ENDED
    }

    private void clearAllScoreboards() {
        for (UUID playerId : players) {
            Player player = Bukkit.getPlayer(playerId);
            if (player != null) {
                // Clear scoreboard by setting a new empty one
                Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
                player.setScoreboard(board);
            }
        }
    }
}