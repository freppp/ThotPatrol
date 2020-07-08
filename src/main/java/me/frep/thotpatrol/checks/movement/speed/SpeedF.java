package me.frep.thotpatrol.checks.movement.speed;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.data.DataPlayer;
import me.frep.thotpatrol.utils.UtilBlock;
import me.frep.thotpatrol.utils.UtilMath;
import me.frep.thotpatrol.utils.UtilPlayer;
import me.frep.thotpatrol.utils.UtilTime;
import me.frep.thotpatrol.utils.UtilVelocity;

public class SpeedF extends Check {
	
    public SpeedF(ThotPatrol ThotPatrol) {
        super("SpeedF", "Speed (Type F)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(3);
    }
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onMove(PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		final DataPlayer data = ThotPatrol.getInstance().getDataManager().getData(p);
		final Location to = e.getTo();
		final Location from = e.getFrom();
        double tps = getThotPatrol().getLag().getTPS();
        int ping = getThotPatrol().getLag().getPing(p);
		if (((to.getX() == from.getX() && to.getY() == from.getY() && to.getZ() == from.getZ()))
				|| p.getGameMode().equals(GameMode.CREATIVE)
				|| e.getPlayer().getVehicle() != null
				|| UtilPlayer.isNearIce(p)
				|| DataPlayer.getWasFlying() > 0
				|| UtilPlayer.isNearPiston(p)
				|| getThotPatrol().getLag().getTPS() < getThotPatrol().getTPSCancel()
				|| getThotPatrol().getLag().getPing(p) > getThotPatrol().getPingCancel()) {
			return;
		}
		if (data != null) {
			if (data.isSpeed_PistonExpand_Set()) {
				if (UtilTime.elapsed(data.getSpeed_PistonExpand_MS(), 9900L)) {
					data.setSpeed_PistonExpand_Set(false);
				}
			}
			final double speed = UtilMath.getHorizontalDistance(from, to);
			if(UtilMath.elapsed(data.getLastVelMS(), 3000)) {
				int verbose = data.getSpeedAVerbose();
				final double speedEffect = UtilPlayer.getPotionEffectLevel(p, PotionEffectType.SPEED);
				final double x = 0;
				final double depth = UtilBlock.isNearLiquid(p) ? 0.34 + (UtilPlayer.hasDepthStrider(p) * 0.02) : x;
				double speedAThreshold = (data.getAirTicks() > 0 ? data.getAirTicks() >= 6
						? data.getAirTicks() == 13 ? 0.466 : 0.35 : (0.345 * Math.pow(0.986938064, data.getAirTicks()))
								: data.getGroundTicks() > 5 ? 0.362 : data.getGroundTicks() == 3 ? 0.62 : 0.4)
						+ (data.getAirTicks() > 0 ? (-0.001 * data.getAirTicks() + 0.014) : (0.018 - (data.getGroundTicks() >= 6 ? 0 : data.getGroundTicks() * 0.001)) * speedEffect
								+ depth);
				speedAThreshold = data.getAboveBlockTicks() > 0 ? speedAThreshold + 0.25 : speedAThreshold;
				speedAThreshold = data.getIceTicks() > 0 ? speedAThreshold + 0.14 : speedAThreshold;
				speedAThreshold = data.getIceTicks() > 0 && data.getAboveBlockTicks() > 0 ? speedAThreshold + 0.24 : speedAThreshold;
				speedAThreshold += Math.abs((p.getWalkSpeed()));
				if (DataPlayer.lastNearSlime !=null) {
					if (DataPlayer.lastNearSlime.contains(p.getPlayer().getName().toString())) {
						speedAThreshold += 0.1;
					}
				}
				if(UtilPlayer.isOnStair(p.getLocation())
						|| UtilPlayer.isOnSlab(p.getLocation())) {
					speedAThreshold+= 0.12;
				}
				if (p.getAllowFlight()) {
					speedAThreshold += p.getFlySpeed();
				}
				if (speed > speedAThreshold + 0.0631) {
					verbose += 8;
				} else {
					verbose = verbose > 0 ? verbose - 1 : 0;
				}

				if (verbose > 40) {
					if (((to.getX() == from.getX() && to.getY() == from.getY() && to.getZ() == from.getZ()))
							|| p.getGameMode().equals(GameMode.CREATIVE)
							|| e.getPlayer().getVehicle() != null
							|| UtilPlayer.isNearIce(p)
							|| p.hasPermission("thotpatrol.bypass")
							|| p.hasPotionEffect(PotionEffectType.JUMP)
							|| p.hasPotionEffect(PotionEffectType.SPEED)
							|| p.isSprinting()){
						return;
					} else {
						if (!p.getAllowFlight()) {
							getThotPatrol().logCheat(this, p, "[0] Ping: " + ping + " | TPS: " + tps);
				        	getThotPatrol().logToFile(p, this, "Overall [0]", "Speed: " + speed + " > " + speedAThreshold
				        			+ " | TPS: " + tps + " | Ping: " + ping);
							verbose = 0;
						}
					}
				}
				data.setSpeedAVerbose(verbose);
			} else {
				data.setSpeedAVerbose(0);
			}
			final Location l = p.getLocation();
			final int x = l.getBlockX();
			final int y = l.getBlockY();
			final int z = l.getBlockZ();
			final Location blockLoc = new Location(p.getWorld(), x, y - 1, z);
			final Location loc = new Location(p.getWorld(), x, y, z);
			final Location loc2 = new Location(p.getWorld(), x, y + 1, z);
			final Location above = new Location(p.getWorld(), x, y + 2, z);
			final Location above3 = new Location(p.getWorld(), x - 1, y + 2, z - 1);
			double MaxAirSpeed = 0.4;
			double maxSpeed = 0.42;
			double MaxSpeedNEW = 0.75;
			if (data.isNearIce()) {
				MaxSpeedNEW = 1.0;
			}
			final double Max = 0.28;
			if (p.hasPotionEffect(PotionEffectType.SPEED)) {
				final int level = UtilPlayer.getPotionEffectLevel(p, PotionEffectType.SPEED);
				if (level > 0) {
					maxSpeed = (maxSpeed * (((level * 20) * 0.011) + 1));
					MaxAirSpeed = (MaxAirSpeed * (((level * 20) * 0.011) + 1));
					maxSpeed = (maxSpeed * (((level * 20) * 0.011) + 1));
					MaxSpeedNEW = (MaxSpeedNEW * (((level * 20) * 0.011) + 1));
				}
			}
			MaxAirSpeed += p.getWalkSpeed() > 0.2 ? p.getWalkSpeed() * 0.8 : 0;
			maxSpeed += p.getWalkSpeed() > 0.2 ? p.getWalkSpeed() * 0.8 : 0;
			if (!UtilPlayer.isOnGround4(p) && speed >= MaxAirSpeed && !data.isNearIce()
					&& blockLoc.getBlock().getType() != Material.ICE && !blockLoc.getBlock().isLiquid()
					&& !loc.getBlock().isLiquid() && blockLoc.getBlock().getType() != Material.PACKED_ICE
					&& above.getBlock().getType() == Material.AIR && above3.getBlock().getType() == Material.AIR
					&& blockLoc.getBlock().getType() != Material.AIR && !UtilVelocity.didTakeVelocity(p) && !UtilBlock.isNearStair(p)) {
				if (!UtilVelocity.didTakeVelocity(p) && UtilPlayer.getDistanceToGround(p) <= 4) {
					if (data.getSpeed2Verbose() >= 8 || p.getNoDamageTicks() == 0 == false && !UtilVelocity.didTakeVelocity(p) && !UtilVelocity.didTakeVelocity(p)
							&& p.getLocation().add(0, 1.94, 0).getBlock().getType() != Material.AIR) {
						getThotPatrol().logCheat(this, p, "Check [1] | Ping: " + ping + " TPS: " + tps);
			        	getThotPatrol().logToFile(p, this, "Overall [1]", "Speed: " + speed + " > " + MaxAirSpeed
			        			+ " | TPS: " + tps + " | Ping: " + ping);
					} else {
						data.setSpeed2Verbose(data.getSpeed2Verbose() + 1);
					}
				} else {
					data.setSpeed2Verbose(0);
				}
			}
			final double onGroundDiff = (to.getY() - from.getY());
			if (speed > Max && !UtilPlayer.isAir(p) && onGroundDiff <= -0.4 && p.getFallDistance() <= 0.4 && blockLoc.getBlock().getType() != Material.ICE
					&& e.getTo().getY() != e.getFrom().getY() && blockLoc.getBlock().getType() != Material.PACKED_ICE
					&& loc2.getBlock().getType() != Material.TRAP_DOOR && above.getBlock().getType() == Material.AIR
					&& above3.getBlock().getType() == Material.AIR && data.getAboveBlockTicks() != 0) {
				getThotPatrol().logCheat(this, p, "Check [2] | Ping: " + ping + " TPS: " + tps);
	        	getThotPatrol().logToFile(p, this, "Overall [2]", "Speed: " + speed + " > " + Max
	        			+ " | TPS: " + tps + " | Ping: " + ping);
			}

			if (speed > 0.7 && !UtilPlayer.isAir(p) && onGroundDiff <= -0.4 && p.getFallDistance() <= 0.4 && blockLoc.getBlock().getType() != Material.ICE
					&& e.getTo().getY() != e.getFrom().getY() && blockLoc.getBlock().getType() != Material.PACKED_ICE
					&& loc2.getBlock().getType() != Material.TRAP_DOOR && above.getBlock().getType() == Material.AIR
					&& above3.getBlock().getType() == Material.AIR && !UtilVelocity.didTakeVelocity(p) && !UtilVelocity.didTakeVelocity(p) &&
					p.getLocation().getBlock().getType() != Material.PISTON_MOVING_PIECE && p.getLocation().getBlock().getType() != Material.PISTON_BASE
					&& p.getLocation().getBlock().getType() != Material.PISTON_STICKY_BASE && !UtilBlock.isNearPiston(p) && !data.isSpeed_PistonExpand_Set()) {
				if (!data.isSpeed_PistonExpand_Set()) {
					if (data.getSpeed_C_3_Verbose() > 1) {
						getThotPatrol().logCheat(this, p, "Check [3] | Ping: " + ping + " TPS: " + tps);
			        	getThotPatrol().logToFile(p, this, "Overall [3]", "Speed: " + speed + " > .7"
			        			+ " | TPS: " + tps + " | Ping: " + ping);
					} else {
						data.setSpeed_C_3_Verbose(data.getSpeed_C_3_Verbose() + 1);
					}

				}
				if (p.hasPotionEffect(PotionEffectType.SPEED)) {
					final int level = UtilPlayer.getPotionEffectLevel(p, PotionEffectType.SPEED);
					if (level > 0) {
						maxSpeed = (maxSpeed * (((level * 20) * 0.011) + 1));
						MaxAirSpeed = (MaxAirSpeed * (((level * 20) * 0.011) + 1));
						maxSpeed = (maxSpeed * (((level * 20) * 0.011) + 1));
						MaxSpeedNEW = (MaxSpeedNEW * (((level * 20) * 0.011) + 1));
					}
				}
				MaxAirSpeed += p.getWalkSpeed() > 0.2 ? p.getWalkSpeed() * 0.8 : 0;
				maxSpeed += p.getWalkSpeed() > 0.2 ? p.getWalkSpeed() * 0.8 : 0;
				if (!UtilPlayer.isOnGround4(p) && speed >= MaxAirSpeed && !data.isNearIce()
						&& blockLoc.getBlock().getType() != Material.ICE && !blockLoc.getBlock().isLiquid()
						&& !loc.getBlock().isLiquid() && blockLoc.getBlock().getType() != Material.PACKED_ICE
						&& above.getBlock().getType() == Material.AIR && above3.getBlock().getType() == Material.AIR
						&& blockLoc.getBlock().getType() != Material.AIR && !UtilVelocity.didTakeVelocity(p) && !UtilBlock.isNearStair(p)) {
					if (!UtilVelocity.didTakeVelocity(p) && UtilPlayer.getDistanceToGround(p) <= 4) {
						if (data.getSpeed2Verbose() >= 8 || p.getNoDamageTicks() == 0 == false && !UtilVelocity.didTakeVelocity(p) && !UtilVelocity.didTakeVelocity(p)
								&& p.getLocation().add(0, 1.94, 0).getBlock().getType() != Material.AIR) {
							getThotPatrol().logCheat(this, p, "Check [4] | Ping: " + ping + " TPS: " + tps);
				        	getThotPatrol().logToFile(p, this, "Overall [4]", "Speed: " + speed + " > " + MaxAirSpeed
				        			+ " | TPS: " + tps + " | Ping: " + ping);
						} else {
							data.setSpeed2Verbose(data.getSpeed2Verbose() + 1);
						}
					} else {
						data.setSpeed2Verbose(0);
					}
					boolean speedPot = false;
					for (final PotionEffect effect : p.getActivePotionEffects()) {
						if (effect.getType().equals(PotionEffectType.SPEED)) {
							speedPot = true;
						}
					}
					if (speed > 0.29 && UtilPlayer.isOnGround(p) && !data.isNearIce() && !UtilBlock.isNearStair(p) && !UtilVelocity.didTakeVelocity(p) && !speedPot) {
						if (speed > Max && !UtilPlayer.isAir(p) && onGroundDiff <= -0.4 && p.getFallDistance() <= 0.4 && blockLoc.getBlock().getType() != Material.ICE
								&& e.getTo().getY() != e.getFrom().getY() && blockLoc.getBlock().getType() != Material.PACKED_ICE
								&& loc2.getBlock().getType() != Material.TRAP_DOOR && above.getBlock().getType() == Material.AIR
								&& above3.getBlock().getType() == Material.AIR && data.getIceTicks() == 0 && !UtilPlayer.isNearIce(p)) {
							getThotPatrol().logCheat(this, p, "Check [5] | Ping: " + ping + " TPS: " + tps);
				        	getThotPatrol().logToFile(p, this, "Overall [5]", "Speed: " + speed + " > " + Max
				        			+ " | TPS: " + tps + " | Ping: " + ping);
						}
						if (speed > 0.7 && !UtilPlayer.isAir(p) && onGroundDiff <= -0.4 && p.getFallDistance() <= 0.4 && blockLoc.getBlock().getType() != Material.ICE
								&& e.getTo().getY() != e.getFrom().getY() && blockLoc.getBlock().getType() != Material.PACKED_ICE
								&& loc2.getBlock().getType() != Material.TRAP_DOOR && above.getBlock().getType() == Material.AIR
								&& above3.getBlock().getType() == Material.AIR && !UtilVelocity.didTakeVelocity(p) && !UtilVelocity.didTakeVelocity(p) &&
								p.getLocation().getBlock().getType() != Material.PISTON_MOVING_PIECE && p.getLocation().getBlock().getType() != Material.PISTON_BASE
								&& p.getLocation().getBlock().getType() != Material.PISTON_STICKY_BASE && !UtilBlock.isNearPiston(p)) {
							getThotPatrol().logCheat(this, p, "Check [6] | Ping: " + ping + " TPS: " + tps);
				        	getThotPatrol().logToFile(p, this, "Overall [6]", "Speed: " + speed + " > .7" 
				        			+ " | TPS: " + tps + " | Ping: " + ping);
						}
					}
				}
			}
		}
	}
}
