package me.frep.thotpatrol.events;

import me.frep.thotpatrol.ThotPatrol;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;

public class SharedEvents implements Listener {
	
	private static Map<Player, Long> lastSprintStart = new HashMap<>();
	private static Map<Player, Long> lastJoin = new HashMap<>();
	private static Map<Player, Long> lastSprintStop = new HashMap<>();
	public static Set<UUID> teleported = new HashSet<>();
	public static Set<UUID> worldChange = new HashSet<>();
	public static Map<Player, Long> placedBlock = new HashMap<>();

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onPlace(BlockPlaceEvent e) {
		final Player p = e.getPlayer();
		placedBlock.put(p, System.currentTimeMillis());
	}
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
	public void onInteract(PlayerInteractEvent e) {
		final Player p = e.getPlayer();
		if (e.isCancelled()) {
			if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
				return;
			}
			placedBlock.put(p, System.currentTimeMillis());
			Bukkit.getScheduler().scheduleAsyncDelayedTask(ThotPatrol.getInstance(), new Runnable() {
				@Override
				public void run() {
					placedBlock.remove(p);
				}
			}, 100);
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent e) {
		final Player p = e.getPlayer();
		final UUID u = p.getUniqueId();
		lastJoin.put(e.getPlayer(), System.currentTimeMillis());
		teleported.add(u);
		lastSprintStart.remove(p);
		lastSprintStop.remove(p);
		ThotPatrol.getInstance().getDataManager().addPlayerData(p);
		ThotPatrol.getInstance().getDataManager().add(p);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		worldChange.add(uuid);
    	Bukkit.getScheduler().scheduleAsyncDelayedTask(ThotPatrol.getInstance(), new Runnable() {
    		@Override
    		public void run() {
    			worldChange.remove(uuid);
    		}
    	}, 100);
	}

	public static Map<Player, Long> getLastJoin() {
		return lastJoin;
	}
	public void setLastJoin(Map<Player, Long> lastJoin) {
		SharedEvents.lastJoin = lastJoin;
	}
	public static Map<Player, Long> getLastSprintStart() {
		return lastSprintStart;
	}
	public void setLastSprintStart(Map<Player, Long> lastSprintStart) {
		SharedEvents.lastSprintStart = lastSprintStart;
	}
	public static Map<Player, Long> getLastSprintStop() {
		return lastSprintStop;
	}
	public void setLastSprintStop(Map<Player, Long> lastSprintStop) {
		SharedEvents.lastSprintStop = lastSprintStop;
	}
}