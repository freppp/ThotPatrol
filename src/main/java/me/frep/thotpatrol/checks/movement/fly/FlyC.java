package me.frep.thotpatrol.checks.movement.fly;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.data.DataPlayer;
import me.frep.thotpatrol.utils.UtilBlock;
import me.frep.thotpatrol.utils.UtilPlayer;
import me.frep.thotpatrol.utils.UtilServer;
import me.frep.thotpatrol.utils.UtilVelocity;

public class FlyC extends Check {
	
    public FlyC(ThotPatrol ThotPatrol) {
        super("FlyC", "Fly (Type C)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(6);
    }
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onMove(PlayerMoveEvent e) {
		Location from = e.getFrom();
		Location to = e.getTo();
		Player p = e.getPlayer();
		DataPlayer data = ThotPatrol.getInstance().getDataManager().getData(p);
		if (p.getWorld().getHighestBlockAt(p.getLocation()).getType().toString().contains("SLIME")) {
			return;
		}
		if (p.getGameMode().equals(GameMode.CREATIVE)
				|| p.getAllowFlight()
				|| e.getPlayer().getVehicle() != null
				|| p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SPONGE
				|| UtilPlayer.isOnClimbable(p, 0)
				|| UtilPlayer.wasOnSlime(p)
				|| UtilPlayer.isNearSlime(p)
				|| UtilBlock.isNearLiquid(p)
				|| p.hasPermission("thotpatrol.bypass")
				|| data == null
				|| DataPlayer.getWasFlying() > 0
				|| UtilPlayer.isOnSlime(p.getLocation())
				|| UtilPlayer.isOnClimbable(p, 1) || UtilVelocity.didTakeVelocity(p)
				|| getThotPatrol().getLag().getTPS() < getThotPatrol().getTPSCancel()
				|| getThotPatrol().getLag().getPing(p) > getThotPatrol().getPingCancel()
				|| UtilPlayer.isNearSlime(e.getFrom())
				|| UtilPlayer.isNearSlime(e.getTo())) {
			return;
		}
        Material below2 = p.getLocation().subtract(0, 1, 0).getBlock().getType();
        Material below3 = p.getLocation().subtract(0, 2, 0).getBlock().getType();
        if (below2.toString().contains("PISTON") ||
        		below3.toString().contains("PISTON")) {
        	return;
        }
        for (final Block b : UtilBlock.getNearbyBlocks(p.getLocation(), 3)) {
        	if (b.getType().toString().contains("PISTON")) {
        		return;
        	}
        }
		if (DataPlayer.lastNearSlime !=null) {
			if (DataPlayer.lastNearSlime.contains(p.getPlayer().getName().toString())) {
				return;
			}
		}
		if (!UtilServer.isBukkitVerison("1_8")
				&&!UtilServer.isBukkitVerison("1_7")) {
			if (p.hasPotionEffect(PotionEffectType.JUMP)) {
				return;
			}
		}
		if (!UtilServer.isBukkitVerison("1_13")&& !UtilServer.isBukkitVerison("1_7")) {
			if (p.getLocation().getBlock().isLiquid()) {
				return;
			}
		}
		double tps = getThotPatrol().getLag().getTPS();
		double ping = getThotPatrol().getLag().getPing(p);
		final Vector vec = new Vector(to.getX(), to.getY(), to.getZ());
		final double Distance = vec.distance(new Vector(from.getX(), from.getY(), from.getZ()));
		if (!UtilVelocity.didTakeVelocity(p) && !UtilPlayer.wasOnSlime(p)) {
			if (p.getFallDistance() == 0.0f && p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR && p.getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.AIR) {
				if (UtilPlayer.isNearSlime(from)
						|| UtilPlayer.isNearSlime(to)) {
					return;
				}
				if (!UtilServer.isBukkitVerison("1_13") && !UtilServer.isBukkitVerison("1_7")) {
					if (Distance > 0.50 && !UtilPlayer.isOnGround(p) && e.getTo().getY() > e.getFrom().getY() && e.getTo().getX() == e.getFrom().getX() && e.getTo().getZ() == e.getFrom().getZ() && !UtilVelocity.didTakeVelocity(p)) {
						getThotPatrol().logCheat(this, p, "[1] Distance: " + Distance + " | Ping: " + ping + " | TPS: " + tps);
			        	getThotPatrol().logToFile(p, this, "Distance", "Blocks: + " + Distance
			        			+ " | TPS: " + tps + " | Ping: " + ping);
					} else if (Distance > 0.90 && !UtilPlayer.isOnGround(p) && e.getTo().getY() > e.getFrom().getY() && e.getTo().getX() == e.getFrom().getX() && e.getTo().getZ() == e.getFrom().getZ()) {
						getThotPatrol().logCheat(this, p, "[1] Distance: " + Distance + " | Ping: " + ping + " | TPS: " + tps);
			        	getThotPatrol().logToFile(p, this, "Distance", "Blocks: + " + Distance
			        			+ " | TPS: " + tps + " | Ping: " + ping);
					} else if (Distance > 1.0 && !UtilPlayer.isOnGround(p) && e.getTo().getY() > e.getFrom().getY() && e.getTo().getX() == e.getFrom().getX() && e.getTo().getZ() == e.getFrom().getZ()) {
						getThotPatrol().logCheat(this, p, "[1] Distance: " + Distance + " | Ping: " + ping + " | TPS: " + tps);
			        	getThotPatrol().logToFile(p, this, "Distance", "Blocks: + " + Distance
			        			+ " | TPS: " + tps + " | Ping: " + ping);
					}
				}
				else {
					if (Distance > 0.50 && !UtilPlayer.isOnGround(e,p) && e.getTo().getY() > e.getFrom().getY() && e.getTo().getX() == e.getFrom().getX() && e.getTo().getZ() == e.getFrom().getZ() && !UtilVelocity.didTakeVelocity(p)) {
						getThotPatrol().logCheat(this, p, "[1] Distance: " + Distance + " | Ping: " + ping + " | TPS: " + tps);
			        	getThotPatrol().logToFile(p, this, "Distance", "Blocks: + " + Distance
			        			+ " | TPS: " + tps + " | Ping: " + ping);
					} else if (Distance > 0.90 && !UtilPlayer.isOnGround(e,p) && e.getTo().getY() > e.getFrom().getY() && e.getTo().getX() == e.getFrom().getX() && e.getTo().getZ() == e.getFrom().getZ()) {
						getThotPatrol().logCheat(this, p, "[1] Distance: " + Distance + " | Ping: " + ping + " | TPS: " + tps);
			        	getThotPatrol().logToFile(p, this, "Distance", "Blocks: + " + Distance
			        			+ " | TPS: " + tps + " | Ping: " + ping);
					} else if (Distance > 1.0 && !UtilPlayer.isOnGround(e,p) && e.getTo().getY() > e.getFrom().getY() && e.getTo().getX() == e.getFrom().getX() && e.getTo().getZ() == e.getFrom().getZ()) {
						getThotPatrol().logCheat(this, p, "[1] Distance: " + Distance + " | Ping: " + ping + " | TPS: " + tps);
			        	getThotPatrol().logToFile(p, this, "Distance", "Blocks: + " + Distance
			        			+ " | TPS: " + tps + " | Ping: " + ping);
					}
				}
			}
		}
		if (!UtilServer.isBukkitVerison("1_13") && !UtilServer.isBukkitVerison("1_7")) {
			if (!UtilVelocity.didTakeVelocity(p) && !UtilPlayer.wasOnSlime(p)) {
				if (e.getTo().getY() > e.getFrom().getY() && data.getAirTicks() > 2 && !UtilVelocity.didTakeVelocity(p)) {
					if (!UtilPlayer.isOnGround4(p) && !UtilPlayer.onGround2(p) && !UtilPlayer.isOnGround(p)) {
						if (UtilPlayer.getDistanceToGround(p) > 2) {
							if (data.getGoingUp_Blocks() >= 3 && data.getAirTicks() >= 10) {
								if (p.getFallDistance() == 0.0f && p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR && p.getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.AIR) {
									if (UtilPlayer.isNearSlime(from)
											|| UtilPlayer.isNearSlime(to)
											|| Distance <= 3.5) {
										return;
									}
									getThotPatrol().logCheat(this, p, "[1] Distance: " + Distance + " | Ping: " + ping + " | TPS: " + tps);
						        	getThotPatrol().logToFile(p, this, "Distance", "Blocks: + " + Distance
						        			+ " | TPS: " + tps + " | Ping: " + ping);
								} else {
									data.setGoingUp_Blocks(data.getGoingUp_Blocks() + 1);
								}
							} else {
								data.setGoingUp_Blocks(0);
							}
						} else {
							data.setGoingUp_Blocks(0);
						}
					} else if (e.getTo().getY() < e.getFrom().getY()) {
						data.setGoingUp_Blocks(0);
					} else {
						data.setGoingUp_Blocks(0);
					}
				} else {
					data.setGoingUp_Blocks(0);
				}
			}
		}
		else {
			if (!UtilVelocity.didTakeVelocity(p) && !UtilPlayer.wasOnSlime(p)) {
				if (e.getTo().getY() > e.getFrom().getY() && data.getAirTicks() > 2 && !UtilVelocity.didTakeVelocity(p)) {
					if (!UtilPlayer.isOnGround4(p) && !UtilPlayer.onGround2(p) && !UtilPlayer.isOnGround(e, p)) {
						if (UtilPlayer.getDistanceToGround(p) > 2) {
							if (data.getGoingUp_Blocks() >= 3 && data.getAirTicks() >= 10) {
								if (p.getFallDistance() == 0.0f && p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR && p.getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.AIR) {
									if (UtilPlayer.isNearSlime(from)
											|| UtilPlayer.isNearSlime(to)
											|| Distance <= 3.5) {
										return;
									}
									getThotPatrol().logCheat(this, p, "[1] Distance: " + Distance + " | Ping: " + ping + " | TPS: " + tps);
						        	getThotPatrol().logToFile(p, this, "Distance", "Blocks: + " + Distance
						        			+ " | TPS: " + tps + " | Ping: " + ping);
								} else {
									data.setGoingUp_Blocks(data.getGoingUp_Blocks() + 1);
								}
							} else {
								data.setGoingUp_Blocks(0);
							}
						} else {
							data.setGoingUp_Blocks(0);
						}
					} else if (e.getTo().getY() < e.getFrom().getY()) {
						data.setGoingUp_Blocks(0);
					} else {
						data.setGoingUp_Blocks(0);
					}
				} else {
					data.setGoingUp_Blocks(0);
				}
			}
		}
	}
}
