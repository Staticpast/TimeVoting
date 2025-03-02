package io.mckenz.timevoting.listeners;

import io.mckenz.timevoting.TimeVoting;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

/**
 * Listener for player join events
 */
public class PlayerJoinListener implements Listener {
    private final TimeVoting plugin;

    /**
     * Creates a new player join listener
     * 
     * @param plugin The plugin instance
     */
    public PlayerJoinListener(TimeVoting plugin) {
        this.plugin = plugin;
    }

    /**
     * Handles player join events
     * 
     * @param event The player join event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // This is handled by the UpdateChecker class
        // Additional player join logic can be added here if needed
        plugin.logDebug("Player joined: " + event.getPlayer().getName());
    }
} 