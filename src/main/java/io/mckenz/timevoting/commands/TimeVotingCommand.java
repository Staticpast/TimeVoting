package io.mckenz.timevoting.commands;

import io.mckenz.timevoting.TimeVoting;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Command handler for the timevoting admin command
 */
public class TimeVotingCommand implements CommandExecutor, TabCompleter {
    private final TimeVoting plugin;
    private final List<String> validSubcommands = Arrays.asList("status", "toggle", "reload", "debug");

    /**
     * Creates a new time voting command handler
     * 
     * @param plugin The plugin instance
     */
    public TimeVotingCommand(TimeVoting plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "status":
                return handleStatusCommand(sender);
            case "toggle":
                return handleToggleCommand(sender);
            case "reload":
                return handleReloadCommand(sender);
            case "debug":
                return handleDebugCommand(sender);
            default:
                showHelp(sender);
                return true;
        }
    }

    /**
     * Handles the status subcommand
     * 
     * @param sender The command sender
     * @return true if the command was handled
     */
    private boolean handleStatusCommand(CommandSender sender) {
        if (!sender.hasPermission("timevoting.status")) {
            sender.sendMessage(plugin.getMessageWithPrefix("no-permission"));
            return true;
        }
        
        sender.sendMessage(plugin.getMessageWithPrefix("status-header"));
        sender.sendMessage(plugin.getMessage("status-enabled")
                .replace("%enabled%", plugin.isPluginFunctionalityEnabled() ? "enabled" : "disabled"));
        sender.sendMessage(plugin.getMessage("status-debug")
                .replace("%debug%", plugin.isDebugEnabled() ? "enabled" : "disabled"));
        
        // Show vote counts
        sender.sendMessage(plugin.getMessage("status-votes-header"));
        List<String> timeTypes = Arrays.asList("day", "night", "sunrise", "sunset");
        for (String timeType : timeTypes) {
            sender.sendMessage(plugin.getMessage("status-votes-entry")
                    .replace("%time%", timeType)
                    .replace("%votes%", String.valueOf(plugin.getVoteCount(timeType)))
                    .replace("%required%", String.valueOf(plugin.getRequiredVotes())));
        }
        
        return true;
    }

    /**
     * Handles the toggle subcommand
     * 
     * @param sender The command sender
     * @return true if the command was handled
     */
    private boolean handleToggleCommand(CommandSender sender) {
        if (!sender.hasPermission("timevoting.toggle")) {
            sender.sendMessage(plugin.getMessageWithPrefix("no-permission"));
            return true;
        }
        
        boolean newState = !plugin.isPluginFunctionalityEnabled();
        plugin.setPluginFunctionalityEnabled(newState);
        
        sender.sendMessage(plugin.getMessageWithPrefix("toggle-success")
                .replace("%state%", newState ? "enabled" : "disabled"));
        
        return true;
    }

    /**
     * Handles the reload subcommand
     * 
     * @param sender The command sender
     * @return true if the command was handled
     */
    private boolean handleReloadCommand(CommandSender sender) {
        if (!sender.hasPermission("timevoting.reload")) {
            sender.sendMessage(plugin.getMessageWithPrefix("no-permission"));
            return true;
        }
        
        plugin.loadConfig();
        sender.sendMessage(plugin.getMessageWithPrefix("reload-success"));
        
        return true;
    }

    /**
     * Handles the debug subcommand
     * 
     * @param sender The command sender
     * @return true if the command was handled
     */
    private boolean handleDebugCommand(CommandSender sender) {
        if (!sender.hasPermission("timevoting.debug")) {
            sender.sendMessage(plugin.getMessageWithPrefix("no-permission"));
            return true;
        }
        
        boolean newState = !plugin.isDebugEnabled();
        plugin.setDebugEnabled(newState);
        
        sender.sendMessage(plugin.getMessageWithPrefix("debug-success")
                .replace("%state%", newState ? "enabled" : "disabled"));
        
        return true;
    }

    /**
     * Shows the help message
     * 
     * @param sender The command sender
     */
    private void showHelp(CommandSender sender) {
        sender.sendMessage(plugin.getMessageWithPrefix("help-header"));
        
        if (sender.hasPermission("timevoting.status")) {
            sender.sendMessage(plugin.getMessage("help-status"));
        }
        
        if (sender.hasPermission("timevoting.toggle")) {
            sender.sendMessage(plugin.getMessage("help-toggle"));
        }
        
        if (sender.hasPermission("timevoting.reload")) {
            sender.sendMessage(plugin.getMessage("help-reload"));
        }
        
        if (sender.hasPermission("timevoting.debug")) {
            sender.sendMessage(plugin.getMessage("help-debug"));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String partialArg = args[0].toLowerCase();
            
            for (String subCommand : validSubcommands) {
                if (subCommand.startsWith(partialArg)) {
                    // Check permissions
                    if (subCommand.equals("status") && !sender.hasPermission("timevoting.status")) {
                        continue;
                    }
                    if (subCommand.equals("toggle") && !sender.hasPermission("timevoting.toggle")) {
                        continue;
                    }
                    if (subCommand.equals("reload") && !sender.hasPermission("timevoting.reload")) {
                        continue;
                    }
                    if (subCommand.equals("debug") && !sender.hasPermission("timevoting.debug")) {
                        continue;
                    }
                    
                    completions.add(subCommand);
                }
            }
            
            return completions;
        }
        
        return new ArrayList<>();
    }
} 