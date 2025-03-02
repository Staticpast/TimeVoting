package io.mckenz.timevoting;

import io.mckenz.timevoting.api.TimeVotingAPI;
import io.mckenz.timevoting.commands.TimeVotingCommand;
import io.mckenz.timevoting.commands.VoteTimeCommand;
import io.mckenz.timevoting.commands.TimeForecastCommand;
import io.mckenz.timevoting.listeners.PlayerJoinListener;
import io.mckenz.timevoting.util.UpdateChecker;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Main class for the TimeVoting plugin
 */
public class TimeVoting extends JavaPlugin implements TimeVotingAPI {
    private FileConfiguration config;
    private boolean enabled;
    private boolean debug;
    private UpdateChecker updateChecker;
    
    // Voting system variables
    private final Map<UUID, String> playerVotes = new HashMap<>();
    private final Map<String, Integer> voteCount = new HashMap<>();
    private long lastTimeChange = 0;
    private BukkitTask timeResetTask;
    
    @Override
    public void onEnable() {
        // Save default config if it doesn't exist
        saveDefaultConfig();
        loadConfig();
        
        // Initialize vote counts
        resetVotes();
        
        // Register events
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        
        // Register commands
        VoteTimeCommand voteTimeCommand = new VoteTimeCommand(this);
        getCommand("votetime").setExecutor(voteTimeCommand);
        getCommand("votetime").setTabCompleter(voteTimeCommand);
        
        TimeVotingCommand timeVotingCommand = new TimeVotingCommand(this);
        getCommand("timevoting").setExecutor(timeVotingCommand);
        getCommand("timevoting").setTabCompleter(timeVotingCommand);
        
        TimeForecastCommand timeForecastCommand = new TimeForecastCommand(this);
        getCommand("timeforecast").setExecutor(timeForecastCommand);
        getCommand("timeforecast").setTabCompleter(timeForecastCommand);
        
        // Register API
        getServer().getServicesManager().register(
            TimeVotingAPI.class, 
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
        
        getLogger().info("TimeVoting has been enabled!");
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
        // Cancel any pending tasks
        if (timeResetTask != null) {
            timeResetTask.cancel();
        }
        
        getLogger().info("TimeVoting has been disabled!");
    }
    
    /**
     * Resets all votes
     */
    public void resetVotes() {
        playerVotes.clear();
        voteCount.clear();
        
        // Initialize vote counts for each time option
        voteCount.put("day", 0);
        voteCount.put("night", 0);
        voteCount.put("sunrise", 0);
        voteCount.put("sunset", 0);
        
        logDebug("Votes have been reset");
    }
    
    /**
     * Registers a vote for a specific time
     * 
     * @param player The player voting
     * @param timeType The time type being voted for
     * @return true if the vote was registered, false if the player has already voted for this time
     */
    public boolean registerVote(Player player, String timeType) {
        UUID playerId = player.getUniqueId();
        
        // Check if player has already voted for this time
        if (playerVotes.containsKey(playerId) && playerVotes.get(playerId).equals(timeType)) {
            return false;
        }
        
        // If player has voted for a different time, remove that vote
        if (playerVotes.containsKey(playerId)) {
            String previousVote = playerVotes.get(playerId);
            voteCount.put(previousVote, voteCount.get(previousVote) - 1);
        }
        
        // Register the new vote
        playerVotes.put(playerId, timeType);
        voteCount.put(timeType, voteCount.get(timeType) + 1);
        
        // Check if the vote threshold has been reached
        checkVoteThreshold(timeType);
        
        return true;
    }
    
    /**
     * Checks if the vote threshold has been reached for a specific time
     * 
     * @param timeType The time type to check
     * @return true if the threshold was reached and time was changed, false otherwise
     */
    public boolean checkVoteThreshold(String timeType) {
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int minimumPlayers = config.getInt("voting.minimum-players", 2);
        
        // Check if there are enough players online
        if (onlinePlayers < minimumPlayers) {
            logDebug("Not enough players online to change time: " + onlinePlayers + "/" + minimumPlayers);
            return false;
        }
        
        // Calculate the threshold
        int thresholdPercentage = config.getInt("voting.threshold-percentage", 50);
        int requiredVotes = Math.max(1, (int) Math.ceil((thresholdPercentage / 100.0) * onlinePlayers));
        
        // Check if the threshold has been reached
        if (voteCount.get(timeType) >= requiredVotes) {
            // Check cooldown
            long cooldownMillis = config.getInt("cooldowns.between-changes", 300) * 1000L;
            long currentTime = System.currentTimeMillis();
            
            if (currentTime - lastTimeChange < cooldownMillis) {
                logDebug("Time change cooldown is still active");
                return false;
            }
            
            // Change the time
            changeTime(timeType);
            
            // Reset votes
            resetVotes();
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Changes the time in all worlds
     * 
     * @param timeType The time type to change to
     */
    public void changeTime(String timeType) {
        long ticks;
        
        // Get the time in ticks for the specified time type
        switch (timeType) {
            case "day":
                ticks = config.getLong("time.day", 1000);
                break;
            case "night":
                ticks = config.getLong("time.night", 13000);
                break;
            case "sunrise":
                ticks = config.getLong("time.sunrise", 23000);
                break;
            case "sunset":
                ticks = config.getLong("time.sunset", 12000);
                break;
            default:
                logDebug("Invalid time type: " + timeType);
                return;
        }
        
        // Set the time in all worlds
        for (World world : Bukkit.getWorlds()) {
            world.setTime(ticks);
            logDebug("Set time to " + ticks + " in world " + world.getName());
        }
        
        // Update the last time change timestamp
        lastTimeChange = System.currentTimeMillis();
        
        // Schedule time reset if duration is set
        int duration = config.getInt("time.duration", 300);
        if (duration > 0) {
            // Cancel any existing reset task
            if (timeResetTask != null) {
                timeResetTask.cancel();
            }
            
            // Schedule a new reset task
            timeResetTask = Bukkit.getScheduler().runTaskLater(this, () -> {
                // Reset to normal time cycle by doing nothing
                // The game will continue its normal time cycle
                Bukkit.broadcastMessage(getMessageWithPrefix("time-reset"));
                logDebug("Time has been reset to normal cycle");
            }, duration * 20L); // Convert seconds to ticks
        }
        
        // Broadcast the time change
        Bukkit.broadcastMessage(getMessageWithPrefix("time-changed").replace("%time%", timeType));
    }
    
    /**
     * Gets a formatted message with the plugin prefix
     * 
     * @param key The message key
     * @return The formatted message
     */
    public String getMessageWithPrefix(String key) {
        String prefix = config.getString("messages.prefix", "&8[&bTimeVoting&8] ");
        String message = config.getString("messages." + key, "");
        
        return colorize(prefix + message);
    }
    
    /**
     * Gets a formatted message without the plugin prefix
     * 
     * @param key The message key
     * @return The formatted message
     */
    public String getMessage(String key) {
        String message = config.getString("messages." + key, "");
        
        return colorize(message);
    }
    
    /**
     * Converts color codes in a string
     * 
     * @param message The message to colorize
     * @return The colorized message
     */
    public String colorize(String message) {
        return message.replace("&", "§");
    }
    
    /**
     * Gets the number of votes for a specific time
     * 
     * @param timeType The time type
     * @return The number of votes
     */
    public int getVoteCount(String timeType) {
        return voteCount.getOrDefault(timeType, 0);
    }
    
    /**
     * Gets the time type a player has voted for
     * 
     * @param playerId The player's UUID
     * @return The time type, or null if the player hasn't voted
     */
    public String getPlayerVote(UUID playerId) {
        return playerVotes.get(playerId);
    }
    
    /**
     * Gets the required number of votes to change the time
     * 
     * @return The required number of votes
     */
    public int getRequiredVotes() {
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int thresholdPercentage = config.getInt("voting.threshold-percentage", 50);
        
        return Math.max(1, (int) Math.ceil((thresholdPercentage / 100.0) * onlinePlayers));
    }
    
    /**
     * Gets the time in seconds until the time change cooldown expires
     * 
     * @return The cooldown time in seconds, or 0 if the cooldown has expired
     */
    public int getTimeChangeCooldown() {
        long cooldownMillis = config.getInt("cooldowns.between-changes", 300) * 1000L;
        long currentTime = System.currentTimeMillis();
        long elapsedTime = currentTime - lastTimeChange;
        
        if (elapsedTime >= cooldownMillis) {
            return 0;
        }
        
        return (int) ((cooldownMillis - elapsedTime) / 1000);
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