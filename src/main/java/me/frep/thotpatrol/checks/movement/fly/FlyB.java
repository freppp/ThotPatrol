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
import me.frep.thotpatrol.events.SharedEvents;
import me.frep.thotpatrol.utils.UtilBlock;
import me.frep.thotpatrol.utils.UtilMath;
import me.frep.thotpatrol.utils.UtilPlayer;
import me.frep.thotpatrol.utils.UtilServer;
import me.frep.thotpatrol.utils.UtilVelocity;
import me.frep.thotpatrol.utils.UtilVelocityNew;

public class FlyB extends Check {
	
    public FlyB(ThotPatrol ThotPatrol) {
        super("FlyB", "Fly (Type B)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(6);
    }
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onMove(PlayerMoveEvent e) {
		final Location from = e.getFrom();
		final Location to = e.getTo();
		final Player p = e.getPlayer();
		if (SharedEvents.placedBlock.containsKey(p)) {
			if (System.currentTimeMillis() - SharedEvents.placedBlock.get(p) < 2000) {
				return;
			}
		}
		if (p.getGameMode().equals(GameMode.CREATIVE)
				|| p.isSneaking()
				|| p.getAllowFlight()
				|| DataPlayer.getWasFlying() > 0
				|| e.getPlayer().getVehicle() != null
				|| p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SPONGE
				|| UtilPlayer.isOnClimbable(p, 0)
				|| UtilBlock.isNearLiquid(p)
				|| UtilPlayer.isOnClimbable(p, 1)
				|| UtilVelocity.didTakeVelocity(p)
				|| p.hasPermission("thotpatrol.bypass")
				|| getThotPatrol().getLag().getTPS() < getThotPatrol().getTPSCancel()
				|| getThotPatrol().getLag().getPing(p) > getThotPatrol().getPingCancel()) {
			return;
		}
		for (Block b: UtilBlock.getNearbyBlocks(p.getLocation(), 2)) {
			if (b.getType().toString().contains("SLAB") || b.getType().toString().contains("STAIR")
					|| b.getType().toString().contains("STEP")) {
				return;
			}
		}
		if (!UtilServer.isBukkitVerison("1_8")
				&&!UtilServer.isBukkitVerison("1_7")) {
			if (p.hasPotionEffect(PotionEffectType.JUMP)) {
				return;
			}
		}
		else {
			if (UtilBlock.isNearStair(p)) {
				return;
			}
		}

		final DataPlayer data = ThotPatrol.getInstance().getDataManager().getData(p);
		if (data == null) {
			return;
		}
		double tps = getThotPatrol().getLag().getTPS();
		double ping = getThotPatrol().getLag().getPing(p);
		if (!UtilVelocityNew.didTakeVel(p)) {
			final Vector vec = new Vector(to.getX(), to.getY(), to.getZ());
			final double Distance = vec.distance(new Vector(from.getX(), from.getY(), from.getZ()));
			if (p.getFallDistance() == 0.0f && p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR && p.getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.AIR) {
				if (!UtilServer.isBukkitVerison("1_13") && !UtilServer.isBukkitVerison("1_7")) {
					if (Distance > 3.5 && !UtilPlayer.isOnGround(p) && e.getTo().getY() > e.getFrom().getY() && e.getTo().getX() == e.getFrom().getX() && e.getTo().getZ() == e.getFrom().getZ()) {
						getThotPatrol().logCheat(this, p, "[1] Distance: " + Distance + " | Ping: " + ping + " | TPS: " + tps);
			        	getThotPatrol().logToFile(p, this, "Distance", "Blocks: + " + Distance
			        			+ " | TPS: " + tps + " | Ping: " + ping);
					}
				}
				else {
					if (Distance > 3.5 && !UtilPlayer.isOnGround(e, p) && e.getTo().getY() > e.getFrom().getY() && e.getTo().getX() == e.getFrom().getX() && e.getTo().getZ() == e.getFrom().getZ()) {
						getThotPatrol().logCheat(this, p, "[2] Distance: " + Distance + " | Ping: " + ping + " | TPS: " + tps);
			        	getThotPatrol().logToFile(p, this, "Distance [2]", "Blocks: + " + Distance
			        			+ " | TPS: " + tps + " | Ping: " + ping);
					}
				}
			}
		}
		if (!UtilServer.isBukkitVerison("1_13") && !UtilServer.isBukkitVerison("1_7")) {
			if (!UtilVelocityNew.didTakeVel(p)) {
				if (e.getTo().getY() > e.getFrom().getY() && data.getAirTicks() > 2 && !UtilVelocity.didTakeVelocity(p)) {
					if (!UtilPlayer.isOnGround4(p) && !UtilPlayer.onGround2(p) && !UtilPlayer.isOnGround(p)) {
						if (UtilPlayer.getDistanceToGround(p) > 2) {
							if (data.getGoingUp_Blocks() >= 3 && data.getAirTicks() >= 10) {
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
		else {
			if (!UtilVelocityNew.didTakeVel(p)) {
				if (e.getTo().getY() > e.getFrom().getY() && data.getAirTicks() > 2 && !UtilVelocity.didTakeVelocity(p)) {
					if (!UtilPlayer.isOnGround4(p) && !UtilPlayer.onGround2(p) && !UtilPlayer.isOnGround(e, p)) {
						if (UtilPlayer.getDistanceToGround(p) > 2) {
							if (data.getGoingUp_Blocks() >= 3 && data.getAirTicks() >= 10) {
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
		if (!UtilServer.isBukkitVerison("1_13") && !UtilServer.isBukkitVerison("1_7")) {
			if(!UtilPlayer.isOnGround(p)) {
				final double distanceToGround = getDistanceToGround(p);
				final double yDiff = UtilMath.getVerticalDistance(e.getFrom(), e.getTo());
				int verbose = data.getFlyHoverVerbose();
				if (UtilPlayer.isNearWeb(p)) {
					return;
				}
				if (p.isSneaking()) {
					return;
				}
				for (Block b : UtilBlock.getNearbyBlocks(p.getLocation(), 2)) {
					if (UtilBlock.isSolid(b)) {
						return;
					}
				}
				if(distanceToGround > 2) {
					verbose = yDiff == 0 ? verbose + 6 : yDiff < 0.06 ? verbose + 4 : 0;
				} else if(data.getAirTicks() > 7
						&& yDiff < 0.001) {
					verbose+= 2;
				} else {
					verbose = 0;
				}

				if(verbose > 20) {
					getThotPatrol().logCheat(this, p, "[3] Ping: " + ping + " | TPS: " + tps);
		        	getThotPatrol().logToFile(p, this, "Distance [3]", "TPS: " + tps + " | Ping: " + ping);
					verbose = 0;
				}
				data.setFlyHoverVerbose(verbose);
			}
		} else {
			if(!UtilPlayer.isOnGround(e, p)) {
				final double distanceToGround = getDistanceToGround(p);
				final double yDiff = UtilMath.getVerticalDistance(e.getFrom(), e.getTo());
				int verbose = data.getFlyHoverVerbose();
				if (UtilPlayer.isNearWeb(p)
						|| p.isSneaking()
						|| UtilBlock.isSolid(p.getLocation().getBlock())
						|| UtilPlayer.isNearSolid(p)
						|| !UtilPlayer.isFlying(e, p)
						|| UtilServer.isBukkitVerison("1_13")
						|| !UtilPlayer.isFlying2(e, p)) {
					return;
				}
				if(distanceToGround > 2) {
					verbose = yDiff == 0 ? verbose + 6 : yDiff < 0.06 ? verbose + 4 : 0;
				} else if(data.getAirTicks() > 7
						&& yDiff < 0.001) {
					verbose+= 2;
				} else {
					verbose = 0;
				}

				if(verbose > 20) {
					getThotPatrol().logCheat(this, p, "[4] Ping: " + ping + " | TPS: " + tps);
		        	getThotPatrol().logToFile(p, this, "Distance [4]", "TPS: " + tps + " | Ping: " + ping);
					verbose = 0;
				}
				data.setFlyHoverVerbose(verbose);
			}
		else {
			if (UtilPlayer.getDistanceToGround(p) >  3) {
				final double OffSet = e.getFrom().getY() - e.getTo().getY();
				long Time = System.currentTimeMillis();
				if (OffSet <= 0.0 || OffSet > 0.16) {
					data.setGlideTicks(0);
					return;
				}
				if (data.getGlideTicks() != 0) {
					Time = data.getGlideTicks();
				}
				final long Millis = System.currentTimeMillis() - Time;
				if (Millis > 200L) {
					if (UtilBlock.isNearLiquid(p)
							|| UtilPlayer.isNearWeb(p)) {
						return;
					}
					if (!UtilBlock.isNearLiquid(p)) {
						getThotPatrol().logCheat(this, p, "[5] Ping: " + ping + " | TPS: " + tps);
			        	getThotPatrol().logToFile(p, this, "Distance [5]", "TPS: " + tps + " | Ping: " + ping);
						data.setGlideTicks(0);
					}
				}
				data.setGlideTicks(Time);
			} else {
				data.setGlideTicks(0);
			}
		}
		final double diffY = Math.abs(from.getY() - to.getY());
		final double lastDiffY = data.getLastVelocityFlyY();
		int verboseC = data.getFlyVelocityVerbose();
		final double finalDifference = Math.abs(diffY - lastDiffY);
		if (!UtilServer.isBukkitVerison("1_13") && !UtilServer.isBukkitVerison("1_7")) {
			if(finalDifference < 0.08
					&& e.getFrom().getY() < e.getTo().getY()
					&& !UtilPlayer.isOnGround(p) && !p.getLocation().getBlock().isLiquid() && !UtilBlock.isNearLiquid(p)
					&& !UtilVelocityNew.didTakeVel(p) && !UtilVelocity.didTakeVelocity(p)) {
				if(++verboseC > 8) {
					if (!UtilPlayer.wasOnSlime(p)) {
						verboseC = 0;
					}
					else {
						getThotPatrol().logCheat(this, p, "[6] Ping: " + ping + " | TPS: " + tps);
			        	getThotPatrol().logToFile(p, this, "Distance [6]", "TPS: " + tps + " | Ping: " + ping);
						verboseC = 0;
					}
				}
			} else {
				verboseC = verboseC > 0 ? verboseC - 1 : 0;
			}
			data.setLastVelocityFlyY(diffY);
			data.setFlyVelocityVerbose(verboseC);
		}
		else {
			if(finalDifference < 0.08
					&& e.getFrom().getY() < e.getTo().getY()
					&& !UtilPlayer.isOnGround(e, p) && !p.getLocation().getBlock().isLiquid() && !UtilBlock.isNearLiquid(p)
					&& !UtilVelocityNew.didTakeVel(p) && !UtilVelocity.didTakeVelocity(p)) {
				if(++verboseC > 8) {
					if (!UtilPlayer.wasOnSlime(p)) {
						verboseC = 0;
					}
					else {
						getThotPatrol().logCheat(this, p, "[7] Ping: " + ping + " | TPS: " + tps);
			        	getThotPatrol().logToFile(p, this, "Distance [7]", "TPS: " + tps + " | Ping: " + ping);
						verboseC = 0;
					}
				}
			} else {
				verboseC = verboseC > 0 ? verboseC - 1 : 0;
			}
			data.setLastVelocityFlyY(diffY);
			data.setFlyVelocityVerbose(verboseC);
		}
		}
	}

	private int getDistanceToGround(Player p){
		final Location loc = p.getLocation().clone();
		final double y = loc.getBlockY();
		int distance = 0;
		for (double i = y; i >= 0; i--){
			loc.setY(i);
			if(loc.getBlock().getType().isSolid() || loc.getBlock().isLiquid()) {
				break;
			}
			distance++;
		}
		return distance;
	}
}