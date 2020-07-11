package me.frep.thotpatrol.packets.events;

import me.frep.thotpatrol.packets.PacketPlayerType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PacketPlayerEvent extends Event {
    private static final HandlerList handlers;

    static {
        handlers = new HandlerList();
    }

    private Player Player;
    private double x;
    private double y;
    private double z;
    private float pitch;
    private float yaw;
    private PacketPlayerType type;

    public PacketPlayerEvent(final Player Player, final double x, final double y, final double z, final float yaw,
                             final float pitch, final PacketPlayerType type) {
        super();
        this.Player = Player;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.type = type;
    }

    public static HandlerList getHandlerList() {
        return PacketPlayerEvent.handlers;
    }

    public Player getPlayer() {
        return this.Player;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public PacketPlayerType getType() {
        return this.type;
    }

    public HandlerList getHandlers() {
        return PacketPlayerEvent.handlers;
    }
}