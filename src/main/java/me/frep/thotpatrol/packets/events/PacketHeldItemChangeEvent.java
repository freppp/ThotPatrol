package me.frep.thotpatrol.packets.events;

import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PacketHeldItemChangeEvent extends Event {
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    public Player Player;
    public PacketEvent Event;

    public PacketHeldItemChangeEvent(final PacketEvent Event, final Player Player) {
        super();
        this.Player = Player;
        this.Event = Event;
    }

    public static HandlerList getHandlerList() {
        return PacketHeldItemChangeEvent.handlers;
    }

    public PacketEvent getPacketEvent() {
        return this.Event;
    }

    public Player getPlayer() {
        return this.Player;
    }

    public HandlerList getHandlers() {
        return PacketHeldItemChangeEvent.handlers;
    }
}