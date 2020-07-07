package me.frep.thotpatrol.packets.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

public class PacketVelocityEvent extends Event {

    private org.bukkit.entity.Player Player;
    private Vector vector;
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    public PacketVelocityEvent(final Player Player, Vector vector) {
        super();
        this.Player = Player;
        this.vector = vector;
    }

    public Player getPlayer() {
        return this.Player;
    }

    public Vector getVelocity() {
        return this.vector;
    }

    public HandlerList getHandlers() {
        return PacketVelocityEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return PacketVelocityEvent.handlers;
    }
}
