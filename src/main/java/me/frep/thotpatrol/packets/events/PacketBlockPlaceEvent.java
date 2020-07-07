package me.frep.thotpatrol.packets.events;

import org.bukkit.event.HandlerList;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class PacketBlockPlaceEvent extends Event {
    public Player Player;
    public PacketEvent Event;
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    public PacketBlockPlaceEvent(final PacketEvent Event, final Player Player) {
        super();
        this.Player = Player;
        this.Event = Event;
    }

    public PacketEvent getPacketEvent() {
        return this.Event;
    }

    public Player getPlayer() {
        return this.Player;
    }

    public HandlerList getHandlers() {
        return PacketBlockPlaceEvent.handlers;
    }

    public static HandlerList getHandlerList() {
        return PacketBlockPlaceEvent.handlers;
    }
}