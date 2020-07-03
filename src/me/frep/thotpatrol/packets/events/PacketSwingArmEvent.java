package me.frep.thotpatrol.packets.events;

import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PacketSwingArmEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    public Player Player;
    public PacketEvent Event;

    public PacketSwingArmEvent(PacketEvent Event, Player Player) {
        this.Player = Player;
        this.Event = Event;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public PacketEvent getPacketEvent() {
        return this.Event;
    }

    public Player getPlayer() {
        return this.Player;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
