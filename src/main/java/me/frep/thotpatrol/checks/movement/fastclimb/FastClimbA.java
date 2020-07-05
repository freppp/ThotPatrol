package me.frep.thotpatrol.checks.movement.fastclimb;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.utils.UtilMath;
import me.frep.thotpatrol.utils.UtilPlayer;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class FastClimbA extends Check {

    private static List<Double> jumpedValues = new ArrayList<>();
    private Map<UUID, Integer> verbose = new HashMap<>();

    static {
        jumpedValues.add(.41999998688697815);
        jumpedValues.add(.16477328182606676);
        jumpedValues.add(.33319999363422337);
        jumpedValues.add(.24813599859094548);
        jumpedValues.add(.164773281826065);
        jumpedValues.add(.33319999363422426);
        jumpedValues.add(.24813599859094637);
    }

    public FastClimbA(me.frep.thotpatrol.ThotPatrol ThotPatrol) {
        super("FastClimbA", "Fast Climb (Type A) [#]", ThotPatrol);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(6);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        double yDiff = e.getTo().getY() - e.getFrom().getY();
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        if (e.isCancelled()
            || e.getFrom().getY() == e.getTo().getY()
            || p.getAllowFlight()
            || !UtilTime.elapsed(getThotPatrol().LastVelocity.getOrDefault(p.getUniqueId(), 0L), 1500)
            || p.hasPermission("thotpatrol.bypass")
            || !UtilPlayer.isOnClimbable(p, 1)
            || !UtilPlayer.isOnClimbable(p, 0)
            || p.getEyeLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR)
            || yDiff <= 0
            || jumpedValues.contains(yDiff)) {
            return;
        }
        double offset = UtilMath.offset(UtilMath.getVerticalVector(e.getFrom().toVector()), UtilMath.getVerticalVector(e.getTo().toVector()));
        double limit = .13;
        if (offset > limit) {
            count++;
        } else {
            if (count > 0) {
                count--;
            }
        }
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
        if (count > 6) {
            count = 0;
            getThotPatrol().logCheat(this, p, offset + " > " + limit + " | Ping: " + ping + " | TPS: " + tps);
            getThotPatrol().logToFile(p, this, "Invalid Offset", "Offset: " + offset + " > " + limit + " | TPS: " + tps + " | Ping: " + ping);
        }
        verbose.put(p.getUniqueId(), count);
    }
}