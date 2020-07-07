package me.frep.thotpatrol.packets.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import me.frep.thotpatrol.packets.PacketPlayerType;

public class PacketAttackEvent extends Event {
	
	private final Player player;
	private final Entity entity;
	private final PacketPlayerType type;
	private static final HandlerList handlers = new HandlerList();

	public PacketAttackEvent(Player player, Entity entity, PacketPlayerType type) {
		this.player = player;
		this.entity = entity;
		this.type = type;
	}

	public Player getPlayer() {
		return this.player;
	}

	public Entity  getEntity() {
		return entity;
	}

	public PacketPlayerType getType() {
		return type;
	}
	
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}