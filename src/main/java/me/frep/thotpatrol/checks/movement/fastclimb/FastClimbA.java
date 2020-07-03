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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FastClimbA extends Check {

    //todo find better fix for this
    double jumped = .41999998688697815;
    double jumped2 = .16477328182606676;
    double jumped3 = .33319999363422337;
    double jumped4 = 0.24813599859094548;
    double jumped5 = 0.164773281826065;
    double jumped6 = .33319999363422426;
    double jumped7 = .24813599859094637;

    private Map<UUID, Integer> verbose = new HashMap<>();

    public FastClimbA(me.frep.thotpatrol.ThotPatrol ThotPatrol) {
        super("FastClimbA", "Fast Climb (Type A) [#]", ThotPatrol);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(6);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (e.isCancelled()
            || e.getFrom().getY() == e.getTo().getY()
            || p.getAllowFlight()
            || !UtilTime.elapsed(getThotPatrol().LastVelocity.getOrDefault(p.getUniqueId(), 0L), 1500)
            || p.hasPermission("thotpatrol.bypass")
            || !UtilPlayer.isOnClimbable(p, 1)
            || !UtilPlayer.isOnClimbable(p, 0)) {
            return;
        }
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        double yDiff = e.getTo().getY() - e.getFrom().getY();
        if (yDiff <= 0) {
            return;
        }
        if (p.getEyeLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
            return;
        }
        double offset = UtilMath.offset(UtilMath.getVerticalVector(e.getFrom().toVector()), UtilMath.getVerticalVector(e.getTo().toVector()));
        if (offset == jumped || offset == jumped2 || offset == jumped3 || offset == jumped4 || offset == jumped5 || offset == jumped6 || offset == jumped7)  {
            return;
        }
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