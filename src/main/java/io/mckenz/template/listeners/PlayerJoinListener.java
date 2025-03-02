package io.mckenz.template.listeners;

import io.mckenz.template.PluginTemplate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Listener for player join events
 */
public class PlayerJoinListener implements Listener {
    
    private final PluginTemplate plugin;
    
    /**
     * Constructor for the player join listener
     * 
     * @param plugin Reference to the main plugin instance
     */
    public PlayerJoinListener(PluginTemplate plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Handles player join events
     * 
     * @param event The player join event
     */
    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Skip if plugin functionality is disabled
        if (!plugin.isPluginFunctionalityEnabled()) {
            return;
        }
        
        Player player = event.getPlayer();
        
        // TODO: Add your plugin's player join logic here
        plugin.logDebug("Player " + player.getName() + " joined the server");
    }
}