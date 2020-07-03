package me.frep.thotpatrol.checks.movement.ascension;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.data.DataPlayer;
import me.frep.thotpatrol.utils.UtilBlock;
import me.frep.thotpatrol.utils.UtilCheat;
import me.frep.thotpatrol.utils.UtilMath;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class AscensionA extends Check {
	
    public static Map<UUID, Map.Entry<Long, Double>> AscensionTicks = new HashMap<>();
    public static Map<UUID, Double> velocity = new HashMap<>();
    public static Map<UUID, Long> toggleFlight = new HashMap<>();

    public AscensionA(ThotPatrol ThotPatrol) {
        super("AscensionA", "Ascension (Type A)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(5);
    }

    @EventHandler
	public void onFly(PlayerToggleFlightEvent e) {
    	toggleFlight.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onMove(PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		if (p.getWorld().getHighestBlockAt(p.getLocation()).getType().toString().contains("SLIME")) {
			return;
		}
		if (e.getFrom().getY() >= e.getTo().getY()
				|| p.getAllowFlight()
				|| p.getVehicle() != null
				|| !UtilTime.elapsed(toggleFlight.getOrDefault(uuid, 0L), 5000L)
				|| !UtilTime.elapsed(getThotPatrol().LastVelocity.getOrDefault(uuid, 0L), 4200L)) {
			return;
		}
		if (p.hasPermission("thotpatrol.bypass")) {
			return;
		}
		long Time = System.currentTimeMillis();
		double TotalBlocks = 0.0D;
		if (AscensionTicks.containsKey(uuid)) {
			Time = AscensionTicks.get(uuid).getKey();
			TotalBlocks = AscensionTicks.get(uuid).getValue();
		}
		long MS = System.currentTimeMillis() - Time;
		double OffsetY = UtilMath.offset(UtilMath.getVerticalVector(e.getFrom().toVector()), UtilMath.getVerticalVector(e.getTo().toVector()));
		if (OffsetY > 0.0D) {
			TotalBlocks += OffsetY;
		}
		final Location a = p.getLocation().subtract(0.0D, 1.0D, 0.0D);
		if (UtilCheat.blocksNear(a)) {
			TotalBlocks = 0.0D;
		}
		for (Block b : UtilBlock.getNearbyBlocks(p.getLocation(), 5)) {
			if (b.getType().equals(Material.SLIME_BLOCK)) {
				return;
			}
		}
		if (e.getTo().getBlock().getType().equals(Material.SLIME_BLOCK)
				|| e.getTo().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.SLIME_BLOCK)) {
			return;
		}
		if (DataPlayer.lastNearSlime !=null) {
			if (DataPlayer.lastNearSlime.contains(p.getPlayer().getName().toString())) {
				return;
			}
		}
		double Limit = 1.05D;
		if (p.hasPotionEffect(PotionEffectType.JUMP)) {
			for (PotionEffect effect : p.getActivePotionEffects()) {
				if (effect.getType().equals(PotionEffectType.JUMP)) {
					final int level = effect.getAmplifier() + 1;
					Limit += (Math.pow(level + 4.2D, 2.0D) / 16.0D) + 0.3;
					break;
				}
			}
		}
		double tps = getThotPatrol().getLag().getTPS();
		double ping = getThotPatrol().getLag().getPing(p);
		if (TotalBlocks > Limit) {
			if (MS > 250L) {
				if (!velocity.containsKey(p.getUniqueId())) {
					getThotPatrol().logCheat(this, p, "Flew up " + UtilMath.trim(1, TotalBlocks) + " blocks | Ping: " + ping + " | TPS: " + tps);
		        	getThotPatrol().logToFile(p, this, "Flew Upwards", "Blocks: " + TotalBlocks 
		        			+ " | TPS: " + tps + " | Ping: " + ping);
				}
				Time = System.currentTimeMillis();
			}
		} else {
			Time = System.currentTimeMillis();
		}
		AscensionTicks.put(uuid, new AbstractMap.SimpleEntry<>(Time, TotalBlocks));
	}
}