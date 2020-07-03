package me.frep.thotpatrol.events;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.data.DataPlayer;
import me.frep.thotpatrol.utils.UtilBlock;
import me.frep.thotpatrol.utils.UtilMath;
import me.frep.thotpatrol.utils.UtilPlayer;
import me.frep.thotpatrol.utils.UtilTimer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public class MoveEvent implements Listener {
	

	public static int defaultWait = 15;
	public static Map<UUID, Long> lastMove = new WeakHashMap<>();

	public static HashMap<String, Integer> ticksLeft = new HashMap<String, Integer>();
	public static HashMap<String, BukkitRunnable> cooldownTask = new HashMap<String, BukkitRunnable>();

	public boolean inTimer(Player player) {
		if(ticksLeft.isEmpty() || !ticksLeft.containsKey(player.getName().toString())) {
			return false;
		}
		if(ticksLeft.containsKey(player.getName().toString())) {
			return true;
		}
		return false;
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		lastMove.put(p.getUniqueId(), System.currentTimeMillis());
		if(inTimer(p) == true) {
			return;
		} else {
			ThotPatrol.Instance.startTimer(p);
		}
		if (e.getFrom().getX() != e.getTo().getX()
				|| e.getFrom().getY() != e.getTo().getY()
				|| e.getFrom().getZ() != e.getTo().getZ()) {
			final DataPlayer data = ThotPatrol.Instance.getDataManager().getDataPlayer(p);
			if (data != null) {
				data.setOnGround(UtilPlayer.isOnGround(p));
				data.onGround = UtilPlayer.isOnGround4(p);
				data.setOnClimbable(UtilPlayer.isOnClimbable(p));
				data.onClimbable = UtilPlayer.isOnClimbable(p);
				if(data.isOnGround()) {
					data.groundTicks++;
					data.airTicks = 0;
				} else {
					data.airTicks++;
					data.groundTicks = 0;
				}
				data.iceTicks = Math.max(0, data.isOnIce() ? data.iceTicks + 1  : data.iceTicks - 1);
				data.liquidTicks = Math.max(0, data.isInLiquid() ? data.liquidTicks + 1  : data.liquidTicks - 1);
				data.blockTicks = Math.max(0, data.isUnderBlock() ? data.blockTicks + 1  : data.blockTicks - 1);
			}
		}
		DataPlayer data = ThotPatrol.Instance.getDataManager().getData(p);
		if (data.isNearIce()) {
			if (UtilTimer.elapsed(data.getIsNearIceTicks(), 500L)) {
				if (!UtilPlayer.isNearIce(p)) {
					data.setNearIce(false);
				} else {
					data.setIsNearIceTicks(UtilTimer.nowlong());
				}
			}
		}
		final Location l = p.getLocation();
		final int x = l.getBlockX();
		final int y = l.getBlockY();
		final int z = l.getBlockZ();
		final Location loc1 = new Location(p.getWorld(), x, y + 1, z);
		if (loc1.getBlock().getType() != Material.AIR) {
			if (!data.isBlockAbove_Set()) {
				data.setBlockAbove_Set(true);
				data.setBlockAbove(UtilTimer.nowlong());
			} else {
				if (UtilTimer.elapsed(data.getBlockAbove(),1000L)) {
					if (loc1.getBlock().getType() == Material.AIR) {
						data.setBlockAbove_Set(false);
					} else {
						data.setBlockAbove_Set(true);
						data.setBlockAbove(UtilTimer.nowlong());
					}
				}
			}
		} else {
			if (data.isBlockAbove_Set()) {
				if (UtilTimer.elapsed(data.getBlockAbove(), 1000L)) {
					if (loc1.getBlock().getType() == Material.AIR) {
						data.setBlockAbove_Set(false);
					} else {
						data.setBlockAbove_Set(true);
						data.setBlockAbove(UtilTimer.nowlong());
					}
				}
			}
		}

		if (UtilPlayer.isNearIce(p)) {
			if(data.getIceTicks() < 60) {
				data.setIceTicks(data.getIceTicks() + 1);
			}
		} else if(data.getIceTicks() > 0) {
			data.setIceTicks(data.getIceTicks() - 1);
		}
		final Location loc = p.getPlayer().getLocation();
		loc.setY(loc.getY() -1);

		final Block block = loc.getWorld().getBlockAt(loc);
		if(block.getType().equals(Material.AIR)) {
			if (!(DataPlayer.lastAir.contains(p.getPlayer().getName().toString()))) {
				DataPlayer.lastAir.add(p.getPlayer().getName().toString());
			}
		}
		if(!(block.getType().equals(Material.AIR))) {
			if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()) {
				if (DataPlayer.lastAir.contains(p.getPlayer().getName().toString())) {
					DataPlayer.lastAir.remove(p.getPlayer().getName().toString());
				}
			}
		}
		if(UtilPlayer.isNearSlime(p.getLocation())) {
			if (!(DataPlayer.lastNearSlime.contains(p.getPlayer().getName().toString()))) {
				DataPlayer.lastNearSlime.add(p.getPlayer().getName().toString());
			}
		}
		if(!(UtilPlayer.isNearSlime(p.getLocation()))) {
			if (e.getFrom().getX() != e.getTo().getX() || e.getFrom().getZ() != e.getTo().getZ()) {
				if (DataPlayer.lastNearSlime.contains(p.getPlayer().getName().toString())) {
					DataPlayer.lastNearSlime.remove(p.getPlayer().getName().toString());
				}
			}
		}
		if(DataPlayer.lastAir.contains(p.getPlayer().getName().toString())) {
			if (DataPlayer.getWasSpider() < 2) {
				DataPlayer.setWasSpider(2);
			}
		}
		if(!DataPlayer.lastAir.contains(p.getPlayer().getName().toString())) {
			if (DataPlayer.getWasSpider() > 0) {
				DataPlayer.setWasSpider(DataPlayer.getWasSpider() - 1);
			}
		}
		if(p.getAllowFlight()) {
			if (DataPlayer.getWasFlying() < 5) {
				DataPlayer.setWasFlying(5);
			}
		}
		if(!p.getAllowFlight()) {
			if (DataPlayer.getWasFlying() > 0) {
				DataPlayer.setWasFlying(DataPlayer.getWasFlying() - 1);
			}
		}
		if (UtilBlock.isHalfBlock(p.getLocation().add(0,-0.50,0).getBlock())|| UtilBlock.isLessThanBlock(p.getLocation().add(0,-0.50,0).getBlock()) || UtilPlayer.isNearHalfBlock(p)) {
			if (!data.isHalfBlocks_MS_Set()) {
				data.setHalfBlocks_MS_Set(true);
				data.setHalfBlocks_MS(UtilTimer.nowlong());
			} else {
				if (UtilTimer.elapsed(data.getHalfBlocks_MS(),900L)) {
					if (UtilBlock.isHalfBlock(p.getLocation().add(0,-0.50,0).getBlock()) || UtilPlayer.isNearHalfBlock(p)) {
						data.setHalfBlocks_MS_Set(true);
						data.setHalfBlocks_MS(UtilTimer.nowlong());
					} else {
						data.setHalfBlocks_MS_Set(false);
					}
				}
			}
		} else {
			if (UtilTimer.elapsed(data.getHalfBlocks_MS(),900L)) {
				if (UtilBlock.isHalfBlock(p.getLocation().add(0,-0.50,0).getBlock()) || UtilPlayer.isNearHalfBlock(p)) {
					data.setHalfBlocks_MS_Set(true);
					data.setHalfBlocks_MS(UtilTimer.nowlong());
				} else {
					data.setHalfBlocks_MS_Set(false);
				}
			}
		}
		if (UtilPlayer.isNearIce(p) && !data.isNearIce()) {
			data.setNearIce(true);
			data.setIsNearIceTicks(UtilTimer.nowlong());
		} else if (UtilPlayer.isNearIce(p)) {
			data.setIsNearIceTicks(UtilTimer.nowlong());
		}

		final double distance = UtilMath.getVerticalDistance(e.getFrom(), e.getTo());

		final boolean onGround = UtilPlayer.isOnGround4(p);
		if(!onGround
				&& e.getFrom().getY() > e.getTo().getY()) {
			data.setFallDistance(data.getFallDistance() + distance);
		} else {
			data.setFallDistance(0);
		}

		if(onGround) {
			data.setGroundTicks(data.getGroundTicks() + 1);
			data.setAirTicks(0);
		} else {
			data.setAirTicks(data.getAirTicks() + 1);
			data.setGroundTicks(0);
		}

		if(UtilPlayer.isOnGround(p.getLocation().add(0, 2, 0))) {
			data.setAboveBlockTicks(data.getAboveBlockTicks() + 2);
		} else if(data.getAboveBlockTicks() > 0) {
			data.setAboveBlockTicks(data.getAboveBlockTicks() - 1);
		}
		if(UtilPlayer.isInWater(p.getLocation())
				|| UtilPlayer.isInWater(p.getLocation().add(0, 1, 0))) {
			data.setWaterTicks(data.getWaterTicks() + 1);
		} else if(data.getWaterTicks() > 0) {
			data.setWaterTicks(data.getWaterTicks() - 1);
		}
	}
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onVelocity(PlayerVelocityEvent e) {
		final DataPlayer data = ThotPatrol.Instance.getDataManager().getDataPlayer(e.getPlayer());
		if(data == null) {
			return;
		}
		if(e.getVelocity().getY() > -0.078 || e.getVelocity().getY() < -0.08) {
			data.lastVelocityTaken = System.currentTimeMillis();
		}
	}

	public static Map<UUID, Long> getLastMove() {
		return lastMove;
	}

	public static void setLastMove(Map<UUID, Long> lastMove) {
		MoveEvent.lastMove = lastMove;
	}
}
