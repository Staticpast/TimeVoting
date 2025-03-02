package io.mckenz.template.api;

import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

/**
 * API interface for plugin functionality
 * This allows other plugins to interact with this plugin
 */
public interface PluginAPI {
    
    /**
     * Checks if the plugin functionality is enabled
     * 
     * @return True if enabled, false otherwise
     */
    boolean isPluginEnabled();
    
    /**
     * Registers a listener for plugin events
     * This is the recommended way to listen for events
     * 
     * @param plugin The plugin registering the listener
     * @param listener The listener to register
     */
    void registerEvents(Plugin plugin, Listener listener);
    
    /**
     * TODO: Add your plugin-specific API methods here
     */
}