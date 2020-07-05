package me.frep.thotpatrol.checks.movement.ascension;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.data.DataPlayer;
import me.frep.thotpatrol.utils.*;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AscensionC extends Check {
	
	public static Map<UUID, Map.Entry<Integer, Long>> flyTicks = new HashMap<>();
	public static Map<UUID, Double> velocity = new HashMap<>();
	
    public AscensionC(ThotPatrol ThotPatrol) {
        super("AscensionC", "Ascension (Type C)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(5);
    }

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		if (p.getVehicle() != null
				|| getThotPatrol().getLag().getTPS() < getThotPatrol().getTPSCancel()
				|| e.getFrom().getY() >= e.getTo().getY()
				|| p.getAllowFlight()
				|| p.getWorld().getHighestBlockAt(p.getLocation()).getType().toString().contains("SLIME")
				|| p.getGameMode().equals(GameMode.CREATIVE)
				|| UtilPlayer.isNearSlime(e.getFrom())
				|| UtilPlayer.isNearSlime(e.getTo())
				|| !UtilTime.elapsed(getThotPatrol().LastVelocity.getOrDefault(uuid, 0L), 4200L)
				|| Latency.getLag(p) > 75
				|| p.hasPermission("thotpatrol.bypass")
				|| getThotPatrol().getLastVelocity().containsKey(uuid)) {
			return;
		}
		if (!UtilServer.isBukkitVerison("1_8")
				&&!UtilServer.isBukkitVerison("1_7")) {
			if (p.hasPotionEffect(PotionEffectType.JUMP)) {
				return;
			}
		}
		final Location to = e.getTo();
		final Location from = e.getFrom();
		int Count = 0;
		long Time = UtilTime.nowlong();
		if (flyTicks.containsKey(uuid)) {
			Count = flyTicks.get(uuid).getKey();
			Time = flyTicks.get(uuid).getValue();
		}
		if (flyTicks.containsKey(uuid)) {
			final double Offset = to.getY() - from.getY();
			double Limit = 0.5D;
			double TotalBlocks = Offset;
			if (UtilCheat.blocksNear(p)) {
				TotalBlocks = 0.0D;
			}
			final Location a = p.getLocation().subtract(0.0D, 1.0D, 0.0D);
			if (UtilCheat.blocksNear(a)) {
				TotalBlocks = 0.0D;
			}
			if (p.hasPotionEffect(PotionEffectType.JUMP)) {
				for (final PotionEffect effect : p.getActivePotionEffects()) {
					if (effect.getType().equals(PotionEffectType.JUMP)) {
						final int level = effect.getAmplifier() + 1;
						Limit += Math.pow(level + 4.1D, 2.0D) / 16.0D;
						break;
					}
				}
			}
			if (TotalBlocks >= Limit) {
				Count += 2;
			} else {
				if (Count > 0) {
					Count--;
				}
			}
		}
		if ((flyTicks.containsKey(uuid)) && (UtilTime.elapsed(Time, 30000L))) {
			Count = 0;
			Time = UtilTime.nowlong();
		}
		double tps = getThotPatrol().getLag().getTPS();
		double ping = getThotPatrol().getLag().getPing(p);
		if (Count >= 4) {
			Count = 0;
			dumplog(p, "Logged for Ascension Type C");
			getThotPatrol().logCheat(this, p, "Count: " + Count + "| Ping: " + ping + " | TPS: " + tps);
        	getThotPatrol().logToFile(p, this, "Flew Upwards", "TPS: " + tps + " | Ping: " + ping);

		}
		flyTicks.put(uuid, new AbstractMap.SimpleEntry<>(Count, Time));
	}
}