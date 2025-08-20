package lol.mayaaa.lavaescape;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import java.util.HashMap;
import java.util.Map;

public class ArenaManager {

    private final LavaEscape plugin;
    private final Map<String, Arena> arenas = new HashMap<>();

    public ArenaManager(LavaEscape plugin) {
        this.plugin = plugin;
    }

    public void loadArenas() {
        FileConfiguration config = plugin.getConfigManager().getArenasConfig();
        for (String arenaName : config.getKeys(false)) {
            Arena arena = new Arena(arenaName);
            arena.setLobbySpawn((Location) config.get(arenaName + ".lobbyspawn"));
            arena.setSpawn((Location) config.get(arenaName + ".spawn"));
            arenas.put(arenaName, arena);
        }
    }

    public boolean createArena(String arenaName) {
        if (arenas.containsKey(arenaName)) {
            return false;
        }

        Arena arena = new Arena(arenaName);
        arenas.put(arenaName, arena);

        FileConfiguration config = plugin.getConfigManager().getArenasConfig();
        config.set(arenaName + ".created", true);
        plugin.getConfigManager().saveArenasConfig();

        return true;
    }

    public Arena getArena(String arenaName) {
        return arenas.get(arenaName);
    }

    public Map<String, Arena> getArenas() {
        return arenas;
    }
}