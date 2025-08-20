package lol.mayaaa.lavaescape;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AdminCommand implements CommandExecutor {

    private final LavaEscape plugin;

    public AdminCommand(LavaEscape plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("lavaescape.admin")) {
            player.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "createarena":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /adminlavaescape createarena <arenaname>");
                    return true;
                }
                if (plugin.getArenaManager().createArena(args[1])) {
                    player.sendMessage("§aArena '" + args[1] + "' created successfully!");
                } else {
                    player.sendMessage("§cArena '" + args[1] + "' already exists!");
                }
                break;

            case "setlobbyspawn":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /adminlavaescape setlobbyspawn <arenaname>");
                    return true;
                }
                setLobbySpawn(player, args[1]);
                break;

            case "setspawn":
                if (args.length < 2) {
                    player.sendMessage("§cUsage: /adminlavaescape setspawn <arenaname>");
                    return true;
                }
                setSpawn(player, args[1]);
                break;

            case "debug":
                toggleDebug(player);
                break;

            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void setLobbySpawn(Player player, String arenaName) {
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            player.sendMessage("§cArena '" + arenaName + "' not found!");
            return;
        }

        arena.setLobbySpawn(player.getLocation());

        plugin.getConfigManager().getArenasConfig().set(arenaName + ".lobbyspawn", player.getLocation());
        plugin.getConfigManager().saveArenasConfig();

        player.sendMessage("§aLobby spawn set for arena '" + arenaName + "'!");
    }

    private void setSpawn(Player player, String arenaName) {
        Arena arena = plugin.getArenaManager().getArena(arenaName);
        if (arena == null) {
            player.sendMessage("§cArena '" + arenaName + "' not found!");
            return;
        }

        arena.setSpawn(player.getLocation());

        plugin.getConfigManager().getArenasConfig().set(arenaName + ".spawn", player.getLocation());
        plugin.getConfigManager().saveArenasConfig();

        player.sendMessage("§aSpawn set for arena '" + arenaName + "'!");
    }

    private void sendHelp(Player player) {
        player.sendMessage("§6§lLavaEscape Admin Commands:");
        player.sendMessage("§e/adminlavaescape createarena <name> §7- Create a new arena");
        player.sendMessage("§e/adminlavaescape setlobbyspawn <name> §7- Set lobby spawn");
        player.sendMessage("§e/adminlavaescape setspawn <name> §7- Set game spawn");
        player.sendMessage("§7§oRequires permission: lavaescape.admin");
    }

    private void toggleDebug(Player player) {
        Arena arena = null;
        for (Arena a : plugin.getArenaManager().getArenas().values()) {
            if (a.getGame() != null) {
                arena = a;
                break;
            }
        }

        if (arena != null && arena.getGame() != null) {
            arena.getGame().toggleDebug();
            player.sendMessage("§aDebug mode toggled!");
        } else {
            player.sendMessage("§cNo active game found to toggle debug!");
        }
    }
}