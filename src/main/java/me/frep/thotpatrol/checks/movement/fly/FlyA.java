package me.frep.thotpatrol.checks.movement.fly;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.checks.movement.ascension.AscensionA;
import me.frep.thotpatrol.checks.movement.ascension.AscensionD;
import me.frep.thotpatrol.events.SharedEvents;
import me.frep.thotpatrol.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class FlyA extends Check {

    public static Map<UUID, Long> flyTicksA = new HashMap<>();
    public static List<UUID> blockPlaced = new ArrayList<>();

    public FlyA(ThotPatrol ThotPatrol) {
        super("FlyA", "Fly (Type A)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(6);
    }

    @EventHandler
    public void onLog(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        flyTicksA.remove(uuid);
    }

	@EventHandler
    public void CheckFlyA(PlayerMoveEvent event) {
        if (!getThotPatrol().isEnabled()) return;
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (event.isCancelled()
                || (event.getTo().getX() == event.getFrom().getX()) && (event.getTo().getZ() == event.getFrom().getZ())
                || player.getAllowFlight()
                || player.getVehicle() != null
                || !UtilTime.elapsed(AscensionD.explosionTicks.getOrDefault(player.getUniqueId(), 0L), 3000L)
                || !UtilTime.elapsed(AscensionA.toggleFlight.getOrDefault(uuid, 0L), 5000L)
                || player.hasPermission("thotpatrol.bypass")
                || getThotPatrol().getLag().getTPS() < getThotPatrol().getTPSCancel()
                || UtilPlayer.isInWater(player)
                || UtilCheat.nearWeb(player, 2)
                || UtilCheat.isInWeb(player)
                || Latency.getLag(player) > 92) return;
        if (UtilCheat.blocksNear(player.getLocation())) {
            flyTicksA.remove(uuid);
            return;
        }
        if (SharedEvents.placedBlock.containsKey(player)) {
            if (System.currentTimeMillis() - SharedEvents.placedBlock.get(player) < 3000) {
                return;
            }
        }
		double tps = getThotPatrol().getLag().getTPS();
		int ping = getThotPatrol().getLag().getPing(player);
        if (blockPlaced.contains(uuid) && ping > 500) {
        	return;
        }
        if (Math.abs(event.getTo().getY() - event.getFrom().getY()) > 0.06) {
            flyTicksA.remove(uuid);
            return;
        }
        long Time = System.currentTimeMillis();
        if (flyTicksA.containsKey(uuid)) {
            Time = flyTicksA.get(uuid);
        }
        long MS = System.currentTimeMillis() - Time;
        if (MS > 200L) {
            dumplog(player, "Logged Fly. MS: " + MS);
            getThotPatrol().logCheat(this, player,
                    "Hovering for " + UtilMath.trim(1, (double) (MS / 1000)) + " second(s) | Ping: " + ping + " | TPS: " + tps);
        	getThotPatrol().logToFile(player, this, "Hover", "Time: + " + (MS/1000) + " Seconds"
        			+ " | TPS: " + tps + " | Ping: " + ping);
            flyTicksA.remove(uuid);
            return;
        }
        flyTicksA.put(uuid, Time);
    }
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		blockPlaced.add(p.getUniqueId());
    	Bukkit.getScheduler().scheduleAsyncDelayedTask(ThotPatrol.Instance, new Runnable() {
    		@Override
    		public void run() {
    			blockPlaced.remove(p.getUniqueId());
    		}
    	}, 50);
    }
}