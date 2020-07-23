package me.frep.thotpatrol.checks.movement.misc;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.data.DataPlayer;
import me.frep.thotpatrol.events.SharedEvents;
import me.frep.thotpatrol.utils.*;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

public class GravityA extends Check {
	
    public GravityA(ThotPatrol ThotPatrol) {
        super("GravityA", "Gravity (Type A)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(4);
    }
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onMove(PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		final DataPlayer data = ThotPatrol.getInstance().getDataManager().getData(p);
		if (data != null) {
			double diff = UtilMath.getVerticalDistance(e.getFrom(), e.getTo());
			double LastY = data.getLastY_Gravity();
			double MaxG = 5;
			if (e.getTo().getY() < e.getFrom().getY()) {
				return;
			}
			if (p.getWorld().getHighestBlockAt(p.getLocation()).getType().toString().contains("SLIME")) {
				return;
			}
			if (SharedEvents.placedBlock.containsKey(p)) {
				return;
			}
			if (UtilBlock.isHalfBlock(p.getLocation().add(0, -1.50, 0).getBlock())
					|| UtilBlock.isStair(p.getLocation().add(0,1.50,0).getBlock())
					|| UtilPlayer.isNearHalfBlock(p)
					|| UtilBlock.isNearStair(p)
					|| p.getAllowFlight()
					|| p.hasPermission("thotpatrol.bypass")
					|| DataPlayer.getWasFlying() > 0
					|| e.getPlayer().getVehicle() != null
					|| p.getMaximumNoDamageTicks() < 16
					|| p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SPONGE
					|| UtilPlayer.isOnClimbable(p, 0)
					|| UtilBlock.isNearLiquid(p)
					|| UtilPlayer.isOnClimbable(p, 1) || UtilVelocity.didTakeVelocity(p)
					|| getThotPatrol().getLag().getTPS() < getThotPatrol().getTPSCancel()
					|| getThotPatrol().getLag().getPing(p) > getThotPatrol().getPingCancel()
					|| p.getGameMode().equals(GameMode.CREATIVE)
					|| UtilVelocity.didTakeVelocity(p)) {
				data.setGravity_VL(0);
				return;
			}
			if (DataPlayer.lastNearSlime !=null) {
				if (DataPlayer.lastNearSlime.contains(p.getPlayer().getName().toString())) {
					data.setGravity_VL(0);
					return;
				}
			}
			for (Block b : UtilBlock.getNearbyBlocks(p.getLocation(), 4)) {
				if (b.getType().toString().equals("SLIME_BLOCK")) {
					return;
				}
			}
			if (!UtilServer.isBukkitVerison("1_8")
					&&!UtilServer.isBukkitVerison("1_7")) {
				if (p.hasPotionEffect(PotionEffectType.JUMP)) {
					data.setGravity_VL(0);
					return;
				}
			}
			double tps = getThotPatrol().getLag().getTPS();
			int ping = getThotPatrol().getLag().getPing(p);
			if (p.getLocation().getBlock().getType() != Material.CHEST &&
					p.getLocation().getBlock().getType() != Material.TRAPPED_CHEST && p.getLocation().getBlock().getType() != Material.ENDER_CHEST && data.getAboveBlockTicks() == 0) {
				if (!UtilPlayer.onGround2(p) && !UtilPlayer.isOnGround(e, p) && UtilPlayer.isFlying(e,p)) {
					if ((((UtilServer.isBukkitVerison("1_7") || UtilServer.isBukkitVerison("1_8")) 
							&& Math.abs(p.getVelocity().getY() - LastY) > 0.000001)
							|| (!UtilServer.isBukkitVerison("1_7") && !UtilServer.isBukkitVerison("1_8") 
							&& Math.abs(p.getVelocity().getY() - diff) > 0.000001))
							&& !UtilPlayer.onGround2(p)
							&& e.getFrom().getY() < e.getTo().getY()
							&& (p.getVelocity().getY() >= 0 || p.getVelocity().getY() < (-0.0784 * 5)) && !UtilVelocity.didTakeVelocity(p) && p.getNoDamageTicks() == 0.0) {
						if (data.getGravity_VL() >= MaxG) {
							getThotPatrol().logCheat(this, p, "Invalid Movement | Ping: " + ping + " | TPS: " + tps);
			            	getThotPatrol().logToFile(p, this, "Invalid Movement", "TPS: " + tps + " | Ping: " + ping);
						} else {
							data.setGravity_VL(data.getGravity_VL() + 1);
						}
					} else {
						data.setGravity_VL(0);
					}
				}
			}
			data.setLastY_Gravity(diff);
		}
	}
}