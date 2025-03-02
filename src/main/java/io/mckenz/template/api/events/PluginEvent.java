package io.mckenz.template.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Base event class for plugin events
 */
public class PluginEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    
    /**
     * Required method for Bukkit events
     * 
     * @return The handler list
     */
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    
    /**
     * Required static method for Bukkit events
     * 
     * @return The handler list
     */
    public static HandlerList getHandlerList() {
        return handlers;
    }
}