package me.frep.thotpatrol.checks.movement.fly;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.utils.UtilCheat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlyD extends Check {
	
    public static Map<UUID, Long> flyTicks = new HashMap<>();

    public FlyD(ThotPatrol ThotPatrol) {
        super("FlyD", "Fly (Type D)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(6);
    }

    @EventHandler
    public void onLog(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        flyTicks.remove(uuid);
    }
    
    @EventHandler
    public void CheckGlide(PlayerMoveEvent event) {
        if (!getThotPatrol().isEnabled()) return;
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (event.isCancelled()
                || !(event.getTo().getX() == event.getFrom().getX() && event.getTo().getZ() == event.getFrom().getZ())
                || player.getVehicle() != null
                || player.hasPermission("thotpatrol.bypass")
                || player.getAllowFlight()
                || getThotPatrol().getLag().getTPS() < getThotPatrol().getTPSCancel()
                || UtilCheat.isInWeb(player)) return;
        if (UtilCheat.blocksNear(player)) {
            flyTicks.remove(uuid);
            return;
        }
        double OffsetY = event.getFrom().getY() - event.getTo().getY();
        if (OffsetY <= 0.0 || OffsetY > 0.16) {
            flyTicks.remove(uuid);
            return;
        }
        long Time = System.currentTimeMillis();
        if (flyTicks.containsKey(uuid)) {
            Time = flyTicks.get(uuid);
        }
        long MS = System.currentTimeMillis() - Time;
		double tps = getThotPatrol().getLag().getTPS();
		double ping = getThotPatrol().getLag().getPing(player);
        if (MS > 1000) {
            dumplog(player, "Logged. MS: " + MS);
            flyTicks.remove(uuid);
            if (getThotPatrol().getLag().getPing(player) < 275) {
                getThotPatrol().logCheat(this, player, "Ping: " + ping + " | TPS: " + tps);
	        	getThotPatrol().logToFile(player, this, "Ticks", "TPS: " + tps + " | Ping: " + ping);
            }
            return;
        }
        flyTicks.put(uuid, Time);
    }
}