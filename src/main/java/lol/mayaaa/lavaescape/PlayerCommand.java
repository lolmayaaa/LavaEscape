package lol.mayaaa.lavaescape;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerCommand implements CommandExecutor {

    private final LavaEscape plugin;

    public PlayerCommand(LavaEscape plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "join":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /lavaescape join <arenaname>");
                    return true;
                }
                joinArena(player, args[1]);
                break;

            case "start":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /lavaescape start <arenaname>");
                    return true;
                }
                startArena(player, args[1]);
                break;

            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void joinArena(Player player, String arenaName) {
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            player.sendMessage("§cArena '" + arenaName + "' not found!");
            return;
        }

        Game game = arena.getGame();
        if (game == null) {
            game = plugin.getGameManager().createGame(arena);
        }

        if (game.getState() != Game.GameState.WAITING) {
            player.sendMessage("§cThis game is already running!");
            return;
        }

        game.addPlayer(player);
        player.teleport(arena.getLobbySpawn());
        player.sendMessage("§aJoined arena '" + arenaName + "'!");
    }

    private void startArena(Player player, String arenaName) {
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            player.sendMessage("§cArena '" + arenaName + "' not found!");
            return;
        }

        Game game = arena.getGame();
        if (game == null || game.getState() != Game.GameState.WAITING) {
            player.sendMessage("§cNo waiting game found for this arena!");
            return;
        }

        if (game.getPlayerCount() < 2) {
            player.sendMessage("§cNeed at least 2 players to start!");
            return;
        }

        game.start();
        player.sendMessage("§aGame starting!");
    }

    private void sendHelp(Player player) {
        player.sendMessage("§6§lLavaEscape Commands:");
        player.sendMessage("§e/lavaescape join <arena> §7- Join an arena");
        player.sendMessage("§e/lavaescape start <arena> §7- Start a game");
    }
}