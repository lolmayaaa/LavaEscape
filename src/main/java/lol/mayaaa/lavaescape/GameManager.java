package lol.mayaaa.lavaescape;

import java.util.HashMap;
import java.util.Map;

public class GameManager {

    private final LavaEscape plugin;
    private final Map<String, Game> games = new HashMap<>();

    public GameManager(LavaEscape plugin) {
        this.plugin = plugin;
    }

    public Game createGame(Arena arena) {
        Game game = new Game(plugin, arena);
        games.put(arena.getName(), game);
        arena.setGame(game);
        return game;
    }

    public Game getGame(String arenaName) {
        return games.get(arenaName);
    }

    public void removeGame(String arenaName) {
        games.remove(arenaName);
    }

    public void stopAllGames() {
        for (Game game : games.values()) {
            game.stop();
        }
        games.clear();
    }
}