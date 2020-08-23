package me.frep.thotpatrol.checks.player.scaffold;

import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.utils.UtilMath;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScaffoldB extends Check {

    private Map<UUID, Long> lastPlaced = new HashMap<>();
    private Map<UUID, Double> lastSpeed = new HashMap<>();
    private Map<UUID, Double> lastDeltaY = new HashMap<>();
    private Map<UUID, Integer> verbose = new HashMap<>();

    public ScaffoldB(me.frep.thotpatrol.ThotPatrol ThotPatrol) {
        super("ScaffoldB", "Scaffold (Type B) [#]", ThotPatrol);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(3);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        double speed = UtilMath.getHorizontalDistance(e.getTo(), e.getFrom());
        double deltaY = UtilMath.getVerticalDistance(e.getTo(), e.getFrom());
        lastDeltaY.put(e.getPlayer().getUniqueId(), deltaY);
        lastSpeed.put(e.getPlayer().getUniqueId(), speed);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (p.getAllowFlight()
            || e.isCancelled()
            || p.hasPermission("thotpatrol.bypass")) {
            return;
        }
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        long time = System.currentTimeMillis();
        long lastPlace = lastPlaced.getOrDefault(p.getUniqueId(), time);
        double speed = lastSpeed.getOrDefault(p.getUniqueId(), 0D);
        long delta = time - lastPlace;
        if (delta < 400 && delta > 0 && speed == 0 && e.getBlockAgainst().getFace(e.getBlockPlaced()).equals(BlockFace.UP)
                && e.getBlockPlaced().getLocation().getBlockX() == p.getLocation().getBlockX() && e.getBlockPlaced().getLocation().getBlockZ()
                == p.getLocation().getBlockZ() && lastDeltaY.getOrDefault(p.getUniqueId(), 0D) > 0) {
            count++;
        } else {
            count = 0;
        }
        if (count > 2) {
            count = 0;
            getThotPatrol().logCheat(this, p, "Tower | Ping: " + ping + " | TPS: " + tps);
            getThotPatrol().logToFile(p, this, "Tower", "Delta: " + delta
                    + " < " + 400 + " | TPS: " + tps + " | Ping: " + ping);
        }
        verbose.put(p.getUniqueId(), count);
        lastPlaced.put(p.getUniqueId(), time);
    }
}