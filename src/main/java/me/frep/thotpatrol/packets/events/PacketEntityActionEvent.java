package me.frep.thotpatrol.packets.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PacketEntityActionEvent extends Event {
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    public int Action;
    public Player Player;

    public PacketEntityActionEvent(final Player Player, final int Action) {
        super();
        this.Player = Player;
        this.Action = Action;
    }

    public static HandlerList getHandlerList() {
        return PacketEntityActionEvent.handlers;
    }

    public Player getPlayer() {
        return this.Player;
    }

    public int getAction() {
        return this.Action;
    }

    public HandlerList getHandlers() {
        return PacketEntityActionEvent.handlers;
    }

    public class PlayerAction {
    }
}