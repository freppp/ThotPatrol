package me.frep.thotpatrol.packets.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PacketKeepAliveEvent extends Event {
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    public Player Player;

    public PacketKeepAliveEvent(final Player Player) {
        super();
        this.Player = Player;
    }

    public static HandlerList getHandlerList() {
        return PacketKeepAliveEvent.handlers;
    }

    public Player getPlayer() {
        return this.Player;
    }

    public HandlerList getHandlers() {
        return PacketKeepAliveEvent.handlers;
    }
}