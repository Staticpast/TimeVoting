package io.mckenz.template;

import io.mckenz.template.api.PluginAPI;
import io.mckenz.template.commands.PluginCommand;
import io.mckenz.template.listeners.PlayerJoinListener;
import io.mckenz.template.util.UpdateChecker;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main class for the plugin
 */
public class PluginTemplate extends JavaPlugin implements PluginAPI {
    private FileConfiguration config;
    private boolean enabled;
    private boolean debug;
    private UpdateChecker updateChecker;

    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();
        loadConfig();
        
        // Register events
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        
        // Register commands
        PluginCommand commandExecutor = new PluginCommand(this);
        getCommand("plugintemplate").setExecutor(commandExecutor);
        getCommand("plugintemplate").setTabCompleter(commandExecutor);
        
        // Register API
        getServer().getServicesManager().register(
            PluginAPI.class, 
            this, 
            this, 
            org.bukkit.plugin.ServicePriority.Normal
        );
        
        // Initialize update checker if enabled
        if (config.getBoolean("update-checker.enabled", true)) {
            int resourceId = config.getInt("update-checker.resource-id", 0);
            boolean notifyAdmins = config.getBoolean("update-checker.notify-admins", true);
            
            updateChecker = new UpdateChecker(this, resourceId, notifyAdmins);
            updateChecker.checkForUpdates();
            logDebug("Update checker initialized with resource ID: " + resourceId);
        }
        
        getLogger().info("PluginTemplate has been enabled!");
        logDebug("Debug mode is enabled");
    }

    /**
     * Loads configuration from config.yml
     */
    public void loadConfig() {
        reloadConfig();
        config = getConfig();
        enabled = config.getBoolean("enabled", true);
        debug = config.getBoolean("debug", false);
        
        // TODO: Load your plugin's specific configuration here
        
        logDebug("Configuration loaded");
    }

    /**
     * Logs a debug message if debug mode is enabled
     * 
     * @param message The message to log
     */
    public void logDebug(String message) {
        if (debug) {
            getLogger().info("[DEBUG] " + message);
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("PluginTemplate has been disabled!");
    }

    // API Methods
    
    @Override
    public boolean isPluginEnabled() {
        return isPluginFunctionalityEnabled();
    }
    
    @Override
    public void registerEvents(Plugin plugin, Listener listener) {
        getServer().getPluginManager().registerEvents(listener, plugin);
    }
    
    /**
     * Checks if the plugin functionality is enabled
     * 
     * @return True if enabled, false otherwise
     */
    public boolean isPluginFunctionalityEnabled() {
        return enabled;
    }
    
    /**
     * Sets whether the plugin functionality is enabled
     * 
     * @param enabled True to enable, false to disable
     */
    public void setPluginFunctionalityEnabled(boolean enabled) {
        this.enabled = enabled;
        config.set("enabled", enabled);
        saveConfig();
    }
    
    /**
     * Checks if debug mode is enabled
     * 
     * @return True if debug mode is enabled, false otherwise
     */
    public boolean isDebugEnabled() {
        return debug;
    }
    
    /**
     * Sets whether debug mode is enabled
     * 
     * @param debug True to enable debug mode, false to disable
     */
    public void setDebugEnabled(boolean debug) {
        this.debug = debug;
        config.set("debug", debug);
        saveConfig();
    }
    
    /**
     * Gets the update checker instance
     * 
     * @return The update checker instance, or null if update checking is disabled
     */
    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }
}