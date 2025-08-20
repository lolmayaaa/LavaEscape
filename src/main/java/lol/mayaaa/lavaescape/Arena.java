package lol.mayaaa.lavaescape;

import org.bukkit.Location;

public class Arena {

    private final String name;
    private Location lobbySpawn;
    private Location spawn;
    private Game game;

    public Arena(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public Location getLobbySpawn() { return lobbySpawn; }
    public void setLobbySpawn(Location lobbySpawn) { this.lobbySpawn = lobbySpawn; }
    public Location getSpawn() { return spawn; }
    public void setSpawn(Location spawn) { this.spawn = spawn; }
    public Game getGame() { return game; }
    public void setGame(Game game) { this.game = game; }
}