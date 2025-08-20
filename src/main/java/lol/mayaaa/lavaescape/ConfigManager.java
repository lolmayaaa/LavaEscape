package lol.mayaaa.lavaescape;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;

public class ConfigManager {

    private final LavaEscape plugin;
    private File arenasFile;
    private FileConfiguration arenasConfig;

    public ConfigManager(LavaEscape plugin) {
        this.plugin = plugin;
    }

    public void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        arenasFile = new File(plugin.getDataFolder(), "Arenas.yml");

        if (!arenasFile.exists()) {
            try {
                arenasFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create Arenas.yml!");
            }
        }

        arenasConfig = YamlConfiguration.loadConfiguration(arenasFile);
    }

    public FileConfiguration getArenasConfig() {
        return arenasConfig;
    }

    public void saveArenasConfig() {
        try {
            arenasConfig.save(arenasFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save Arenas.yml!");
        }
    }
}