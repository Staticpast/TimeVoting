package io.mckenz.timevoting.api;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * API for the TimeVoting plugin
 */
public interface TimeVotingAPI {
    
    /**
     * Checks if the plugin is enabled
     * 
     * @return True if enabled, false otherwise
     */
    boolean isPluginEnabled();
    
    /**
     * Registers an event listener
     * 
     * @param plugin The plugin registering the listener
     * @param listener The listener to register
     */
    void registerEvents(Plugin plugin, Listener listener);
} 