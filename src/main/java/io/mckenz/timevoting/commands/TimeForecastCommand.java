package io.mckenz.timevoting.commands;

import io.mckenz.timevoting.TimeVoting;

import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Command handler for the timeforecast command
 */
public class TimeForecastCommand implements CommandExecutor, TabCompleter {
    private final TimeVoting plugin;

    /**
     * Creates a new time forecast command handler
     * 
     * @param plugin The plugin instance
     */
    public TimeForecastCommand(TimeVoting plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.isPluginFunctionalityEnabled()) {
            sender.sendMessage(plugin.getMessageWithPrefix("plugin-disabled"));
            return true;
        }
        
        if (!sender.hasPermission("timevoting.forecast")) {
            sender.sendMessage(plugin.getMessageWithPrefix("no-permission"));
            return true;
        }
        
        World world;
        
        if (sender instanceof Player) {
            world = ((Player) sender).getWorld();
        } else {
            world = plugin.getServer().getWorlds().get(0);
        }
        
        long time = world.getTime();
        String timeString = getTimeString(time);
        
        boolean daylightCycle = world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE) != null &&
                                Boolean.parseBoolean(world.getGameRuleValue(GameRule.DO_DAYLIGHT_CYCLE).toString());
        
        sender.sendMessage(plugin.getMessageWithPrefix("forecast")
                .replace("%time%", timeString)
                .replace("%enabled%", daylightCycle ? "enabled" : "disabled"));
        
        return true;
    }

    /**
     * Gets a human-readable string for the given time
     * 
     * @param time The time in ticks
     * @return A human-readable string
     */
    private String getTimeString(long time) {
        // Convert to 24-hour format
        long hours = (time / 1000 + 6) % 24;
        long minutes = (time % 1000) * 60 / 1000;
        
        String ampm = hours >= 12 ? "PM" : "AM";
        hours = hours % 12;
        if (hours == 0) {
            hours = 12;
        }
        
        String timeString = String.format("%d:%02d %s", hours, minutes, ampm);
        
        // Add a description
        if (time >= 0 && time < 1000) {
            return timeString + " (Sunrise)";
        } else if (time >= 1000 && time < 6000) {
            return timeString + " (Day)";
        } else if (time >= 6000 && time < 12000) {
            return timeString + " (Day)";
        } else if (time >= 12000 && time < 13000) {
            return timeString + " (Sunset)";
        } else if (time >= 13000 && time < 18000) {
            return timeString + " (Night)";
        } else {
            return timeString + " (Night)";
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // No tab completions for this command
        return new ArrayList<>();
    }
} 