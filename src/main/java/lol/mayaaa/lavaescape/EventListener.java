package lol.mayaaa.lavaescape;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

public class EventListener implements Listener {

    private final LavaEscape plugin;

    public EventListener(LavaEscape plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            for (Arena arena : plugin.getArenaManager().getArenas().values()) {
                Game game = arena.getGame();
                if (game != null && game.isPlaying(player)) {
                    if (game.getState() != Game.GameState.RUNNING) {
                        // Cancel ALL damage if game is not running (waiting/lobby)
                        event.setCancelled(true);
                        return;
                    }

                    // Remove PVP damage but allow fall damage and environmental damage during gameplay
                    if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
                        event.setDamage(0); // No damage from PVP, but keep knockback
                    }
                    // Allow lava damage, fall damage, etc. during gameplay
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // Check if player was in any game
        for (Arena arena : plugin.getArenaManager().getArenas().values()) {
            Game game = arena.getGame();
            if (game != null && game.isPlaying(player)) {
                // Custom death message
                event.setDeathMessage("§8[§cLavaEscape§8] §c" + player.getName() + " §7has fucking died.");

                // Remove player from game without showing leave message
                game.getPlayers().remove(player.getUniqueId()); // Direct access to remove silently

                // Check for winner immediately after death
                if (game.getState() == Game.GameState.RUNNING && game.getPlayerCount() == 1) {
                    game.endGame();
                }
                break;
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // Remove player from any game they're in
        for (Arena arena : plugin.getArenaManager().getArenas().values()) {
            Game game = arena.getGame();
            if (game != null && game.isPlaying(player)) {
                // Remove silently without broadcast message
                game.getPlayers().remove(player.getUniqueId());

                // Check for winner when player leaves during running game
                if (game.getState() == Game.GameState.RUNNING && game.getPlayerCount() == 1) {
                    game.endGame();
                }
                break;
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        // Allow block breaking in active games regardless of OP status
        for (Arena arena : plugin.getArenaManager().getArenas().values()) {
            Game game = arena.getGame();
            if (game != null && game.isPlaying(player) && game.getState() == Game.GameState.RUNNING) {
                // Allow breaking blocks during gameplay
                return;
            }
        }
        // If not in a running game, check if they're in waiting state
        for (Arena arena : plugin.getArenaManager().getArenas().values()) {
            Game game = arena.getGame();
            if (game != null && game.isPlaying(player)) {
                // Cancel breaking in lobby/waiting areas
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        // Allow block placing in active games regardless of OP status
        for (Arena arena : plugin.getArenaManager().getArenas().values()) {
            Game game = arena.getGame();
            if (game != null && game.isPlaying(player) && game.getState() == Game.GameState.RUNNING) {
                // Allow placing blocks during gameplay
                return;
            }
        }
        // If not in a running game, check if they're in waiting state
        for (Arena arena : plugin.getArenaManager().getArenas().values()) {
            Game game = arena.getGame();
            if (game != null && game.isPlaying(player)) {
                // Cancel placing in lobby/waiting areas
                event.setCancelled(true);
                return;
            }
        }
    }
}