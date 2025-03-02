package io.mckenz.template.api.examples;

import io.mckenz.template.api.PluginAPI;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Example class showing how to use the plugin API
 */
public class PluginAPIExample {
    
    /**
     * Example method showing how to get and use the plugin API
     * 
     * @param plugin Your plugin instance
     * @return True if the API was found and used successfully
     */
    public static boolean usePluginAPI(JavaPlugin plugin) {
        // Get the API from the services manager
        RegisteredServiceProvider<PluginAPI> provider = 
            plugin.getServer().getServicesManager().getRegistration(PluginAPI.class);
            
        if (provider == null) {
            plugin.getLogger().warning("PluginTemplate API not found!");
            return false;
        }
        
        PluginAPI api = provider.getProvider();
        
        // Check if the plugin is enabled
        if (!api.isPluginEnabled()) {
            plugin.getLogger().info("PluginTemplate is currently disabled.");
            return false;
        }
        
        // TODO: Use the API to interact with the plugin
        
        return true;
    }
}