package io.mckenz.timevoting.commands;

import io.mckenz.timevoting.TimeVoting;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Command handler for the votetime command
 */
public class VoteTimeCommand implements CommandExecutor, TabCompleter {
    private final TimeVoting plugin;
    private final Map<UUID, Long> lastVoteTime = new HashMap<>();
    private final List<String> validTimeTypes = Arrays.asList("day", "night", "sunrise", "sunset");

    /**
     * Creates a new vote time command handler
     * 
     * @param plugin The plugin instance
     */
    public VoteTimeCommand(TimeVoting plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.isPluginFunctionalityEnabled()) {
            sender.sendMessage(plugin.getMessageWithPrefix("plugin-disabled"));
            return true;
        }
        
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessageWithPrefix("player-only"));
            return true;
        }
        
        Player player = (Player) sender;
        
        if (!player.hasPermission("timevoting.vote")) {
            player.sendMessage(plugin.getMessageWithPrefix("no-permission"));
            return true;
        }
        
        // Check if there are enough players online
        int onlinePlayers = Bukkit.getOnlinePlayers().size();
        int minimumPlayers = plugin.getConfig().getInt("voting.minimum-players", 2);
        
        if (onlinePlayers < minimumPlayers) {
            player.sendMessage(plugin.getMessageWithPrefix("not-enough-players")
                    .replace("%required%", String.valueOf(minimumPlayers)));
            return true;
        }
        
        // Check cooldown
        int voteCooldown = plugin.getConfig().getInt("cooldowns.between-votes", 60);
        long currentTime = System.currentTimeMillis();
        
        if (lastVoteTime.containsKey(player.getUniqueId())) {
            long lastVote = lastVoteTime.get(player.getUniqueId());
            long timeElapsed = (currentTime - lastVote) / 1000;
            
            if (timeElapsed < voteCooldown) {
                player.sendMessage(plugin.getMessageWithPrefix("vote-cooldown")
                        .replace("%seconds%", String.valueOf(voteCooldown - timeElapsed)));
                return true;
            }
        }
        
        // Check time change cooldown
        int timeChangeCooldown = plugin.getTimeChangeCooldown();
        if (timeChangeCooldown > 0) {
            player.sendMessage(plugin.getMessageWithPrefix("change-cooldown")
                    .replace("%seconds%", String.valueOf(timeChangeCooldown)));
            return true;
        }
        
        // If no arguments, show current vote status
        if (args.length == 0) {
            showVoteStatus(player);
            return true;
        }
        
        // Check if the time type is valid
        String timeType = args[0].toLowerCase();
        if (!validTimeTypes.contains(timeType)) {
            player.sendMessage(plugin.getMessageWithPrefix("invalid-time"));
            return true;
        }
        
        // Register the vote
        boolean isNewVote = plugin.registerVote(player, timeType);
        
        // Update the last vote time
        lastVoteTime.put(player.getUniqueId(), currentTime);
        
        // Send messages
        if (isNewVote) {
            String previousVote = plugin.getPlayerVote(player.getUniqueId());
            
            if (previousVote != null) {
                player.sendMessage(plugin.getMessageWithPrefix("vote-changed")
                        .replace("%time%", timeType));
            } else {
                player.sendMessage(plugin.getMessageWithPrefix("vote-cast")
                        .replace("%time%", timeType));
            }
            
            // Announce the vote to all players
            Bukkit.broadcastMessage(plugin.getMessageWithPrefix("vote-announcement")
                    .replace("%player%", player.getName())
                    .replace("%time%", timeType)
                    .replace("%votes%", String.valueOf(plugin.getVoteCount(timeType)))
                    .replace("%required%", String.valueOf(plugin.getRequiredVotes())));
        } else {
            player.sendMessage(plugin.getMessageWithPrefix("vote-already-cast")
                    .replace("%time%", timeType));
        }
        
        return true;
    }

    /**
     * Shows the current vote status to a player
     * 
     * @param player The player to show the status to
     */
    private void showVoteStatus(Player player) {
        player.sendMessage(plugin.getMessageWithPrefix("vote-status-header"));
        
        for (String timeType : validTimeTypes) {
            int votes = plugin.getVoteCount(timeType);
            player.sendMessage(plugin.getMessage("vote-status-entry")
                    .replace("%time%", timeType)
                    .replace("%votes%", String.valueOf(votes))
                    .replace("%required%", String.valueOf(plugin.getRequiredVotes())));
        }
        
        String playerVote = plugin.getPlayerVote(player.getUniqueId());
        if (playerVote != null) {
            player.sendMessage(plugin.getMessage("vote-status-your-vote")
                    .replace("%time%", playerVote));
        } else {
            player.sendMessage(plugin.getMessage("vote-status-no-vote"));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            String partialArg = args[0].toLowerCase();
            
            for (String timeType : validTimeTypes) {
                if (timeType.startsWith(partialArg)) {
                    completions.add(timeType);
                }
            }
            
            return completions;
        }
        
        return new ArrayList<>();
    }
} 