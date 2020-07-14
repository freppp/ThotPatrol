package me.frep.thotpatrol.checks.movement.fly;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.checks.movement.speed.SpeedC;
import me.frep.thotpatrol.checks.movement.speed.SpeedI;
import me.frep.thotpatrol.utils.UtilBlock;
import me.frep.thotpatrol.utils.UtilCheat;
import me.frep.thotpatrol.utils.UtilPlayer;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FlyE extends Check {

    private Map<UUID, Integer> verbose = new HashMap<>();

    public FlyE(ThotPatrol ThotPatrol) {
        super("FlyE", "Fly (Type E) [#]", ThotPatrol);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(6);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        double deltaY = UtilCheat.getVerticalDistance(e.getFrom(), e.getTo());
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        int ping = getThotPatrol().getLag().getPing(p);
        int distanceToGround = UtilPlayer.getDistanceToGround(p);
        double tps = getThotPatrol().getLag().getTPS();
        if (p.getAllowFlight()
            || p.getVehicle() != null
            || UtilCheat.isInWeb(p)
            || e.isCancelled()
            || deltaY <= 0
            || UtilPlayer.isOnClimbable(p)
            || UtilPlayer.isFullyStuck(p)
            || UtilPlayer.isPartiallyStuck(p)
            || UtilPlayer.isOnClimbable(p, 1)
            || UtilPlayer.isOnClimbable(p, 0)
            || !UtilTime.elapsed(SpeedI.belowBlock.getOrDefault(p.getUniqueId(), 0L), 2000L)
            || SpeedC.highKb.contains(p.getUniqueId())
            || UtilCheat.isInWater(p)
            || UtilPlayer.isInWater(p)
            || UtilCheat.isFullyInWater(p.getLocation())
            || p.hasPermission("thotpatrol.bypass")) {
            return;
        }
        for (Block b : UtilBlock.getNearbyBlocks(p.getLocation(), 2)) {
            if (b.isLiquid()) {
                return;
            }
        }
        if (deltaY < .25 && distanceToGround > 5) {
            count++;
        } else {
            if (count > 0) {
                count -= 2;
            }
        }
        if (count > 10) {
            count = 0;
            getThotPatrol().logCheat(this, p, "Glide | Ping: " + ping + " | TPS: " + tps);
            getThotPatrol().logToFile(p, this, "Glide", "TPS: " + tps + " | Ping: " + ping);
        }
        verbose.put(p.getUniqueId(), count);
    }
}
