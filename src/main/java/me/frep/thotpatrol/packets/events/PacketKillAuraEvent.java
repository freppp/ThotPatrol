package me.frep.thotpatrol.packets.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.frep.thotpatrol.packets.PacketPlayerType;

public class PacketKillAuraEvent extends Event {
	
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    private Player Player;
    private PacketPlayerType type;

    public PacketKillAuraEvent(final Player Player, final PacketPlayerType type) {
        super();
        this.Player = Player;
        this.type = type;
    }

    public static HandlerList getHandlerList() {
        return PacketKillAuraEvent.handlers;
    }

    public Player getPlayer() {
        return this.Player;
    }

    public PacketPlayerType getType() {
        return this.type;
    }

    public HandlerList getHandlers() {
        return PacketKillAuraEvent.handlers;
    }
}
