package lol.mayaaa.lavaescape;

import org.bukkit.plugin.java.JavaPlugin;

public final class LavaEscape extends JavaPlugin {

    private ArenaManager arenaManager;
    private ConfigManager configManager;
    private GameManager gameManager;

    @Override
    public void onEnable() {
        this.configManager = new ConfigManager(this);
        this.arenaManager = new ArenaManager(this);
        this.gameManager = new GameManager(this);

        getCommand("adminlavaescape").setExecutor(new AdminCommand(this));
        getCommand("lavaescape").setExecutor(new PlayerCommand(this));

        getServer().getPluginManager().registerEvents(new EventListener(this), this);

        configManager.setup();
        arenaManager.loadArenas();
    }

    @Override
    public void onDisable() {
        gameManager.stopAllGames();
    }

    public ArenaManager getArenaManager() { return arenaManager; }
    public ConfigManager getConfigManager() { return configManager; }
    public GameManager getGameManager() { return gameManager; }
}