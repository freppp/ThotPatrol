package me.frep.thotpatrol.checks.movement.nofall;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.data.DataPlayer;
import me.frep.thotpatrol.utils.UtilBlock;
import me.frep.thotpatrol.utils.UtilPlayer;
import me.frep.thotpatrol.utils.UtilServer;
import me.frep.thotpatrol.utils.UtilTime;
import me.frep.thotpatrol.utils.UtilVelocity;

public class NoFallB extends Check {
	
    public NoFallB(ThotPatrol ThotPatrol) {
        super("NoFallB", "No Fall (Type B)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(3);
    }
	
	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		DataPlayer data = ThotPatrol.Instance.getDataManager().getData(p);
		if (data != null) {
			if (e.getTo().getY() > e.getFrom().getY()
					|| getThotPatrol().getLag().getTPS() < getThotPatrol().getTPSCancel()
					|| getThotPatrol().getLag().getPing(p) > getThotPatrol().getPingCancel()) {
				return;
			}
			if (DataPlayer.lastNearSlime !=null) {
				if (DataPlayer.lastNearSlime.contains(p.getPlayer().getName().toString())) {
					return;
				}
			}
			if (data.isLastBlockPlaced_GroundSpoof()) {
				if (UtilTime.elapsed(data.getLastBlockPlacedTicks(),500L)) {
					data.setLastBlockPlaced_GroundSpoof(false);
				}
				return;
			}
			if (UtilServer.isBukkitVerison("1_13")) {
				return;
			}
			if (p.hasPermission("thotpatrol.bypass")) {
				return;
			}
			if (!UtilServer.isBukkitVerison("1_8")
					&&!UtilServer.isBukkitVerison("1_7")) {
				if (p.hasPotionEffect(PotionEffectType.JUMP)) {
					return;
				}
			}
    		double tps = getThotPatrol().getLag().getTPS();
    		double ping = getThotPatrol().getLag().getPing(p);
			final Location to = e.getTo();
			final Location from = e.getFrom();
			final double diff = to.toVector().distance(from.toVector());
			final int dist = UtilPlayer.getDistanceToGround(p);
	        for (final Block b : UtilBlock.getNearbyBlocks(p.getLocation(), 3)) {
	        	if (b.getType().toString().contains("PISTON")) {
	        		return;
	        	}
	        }
			if (p.getLocation().add(0,-1.50,0).getBlock().getType() != Material.AIR) {
				data.setGroundSpoofVL(0);
				return;
			}
	        if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().toString().contains("leaves")) {
	        	return;
	        }
			if (e.getTo().getY() > e.getFrom().getY() || UtilPlayer.isOnGround4(p) || UtilVelocity.didTakeVelocity(p)) {
				data.setGroundSpoofVL(0);
				return;
			}
			if (!UtilServer.isBukkitVerison("1_13") && !UtilServer.isBukkitVerison("1_7") ) {
				if (p.isOnGround() && diff > 0.0 && !UtilPlayer.isOnGround(p) && dist >= 2 && e.getTo().getY() < e.getFrom().getY()) {
					if (data.getGroundSpoofVL() >= 4) {
						if (data.getAirTicks() >= 10) {
							getThotPatrol().logCheat(this, p, "Ground Spoof [1] Ping: " + ping + " | TPS: " + tps);
				        	getThotPatrol().logToFile(p, this, "Ground Spoof [1]", "TPS: " + tps + " | Ping: " + ping);
						} else {
							getThotPatrol().logCheat(this, p, "Ground Spoof [2] Ping: " + ping + " | TPS: " + tps);
				        	getThotPatrol().logToFile(p, this, "Ground Spoof [2]", "TPS: " + tps + " | Ping: " + ping);
						}
					} else {
						data.setGroundSpoofVL(data.getGroundSpoofVL()+1);
					}
				}
			}
			else {
				if (UtilBlock.isSolid(p.getLocation().getBlock())
						|| UtilPlayer.isNearSolid(p)) {
					return;
				}
				if (p.isOnGround() && diff > 0.0 && !UtilPlayer.isOnGround(e,p) && dist >= 2 && e.getTo().getY() < e.getFrom().getY()) {
					if (data.getGroundSpoofVL() >= 4) {
						if (data.getAirTicks() >= 10) {
							getThotPatrol().logCheat(this, p, "Ground Spoof [3]");
				        	getThotPatrol().logToFile(p, this, "Ground Spoof [3]", "TPS: " + tps + " | Ping: " + ping);
						} else {
							getThotPatrol().logCheat(this, p, "Ground Spoof [4]");
				        	getThotPatrol().logToFile(p, this, "Ground Spoof [4]", "TPS: " + tps + " | Ping: " + ping);
						}
					} else {
						data.setGroundSpoofVL(data.getGroundSpoofVL()+1);
					}
				}
			}
		}
	}
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onBlockPlace(BlockPlaceEvent e) {
		final Player p = e.getPlayer();
		final DataPlayer data = ThotPatrol.Instance.getDataManager().getData(p);
		if (!UtilServer.isBukkitVerison("1_8")
				&& !UtilServer.isBukkitVerison("1_7")) {
			if (p.hasPotionEffect(PotionEffectType.JUMP)) {
				return;
			}
		}
		if (data != null) {
			if (!data.isLastBlockPlaced_GroundSpoof()) {
				data.setLastBlockPlaced_GroundSpoof(true);
				data.setLastBlockPlacedTicks(UtilTime.nowlong());
			}
		}
	}
}