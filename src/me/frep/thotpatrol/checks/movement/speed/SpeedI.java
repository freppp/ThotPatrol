package me.frep.thotpatrol.checks.movement.speed;

import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.checks.movement.ascension.AscensionA;
import me.frep.thotpatrol.events.SharedEvents;
import me.frep.thotpatrol.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpeedI extends Check {

    private Map<UUID, Integer> verbose = new HashMap<>();
    private Map<UUID, Long> onStairs = new HashMap<>();
    private Map<UUID, Long> belowBlock = new HashMap<>();

    public SpeedI(me.frep.thotpatrol.ThotPatrol ThotPatrol) {
        super("SpeedI", "Speed (Type I) [#]", ThotPatrol);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(8);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        double delta = UtilMath.getHorizontalDistance(e.getFrom(), e.getTo());
        double maxDelta = .35;
        if (UtilBlock.isStair(p.getLocation().clone().subtract(0, 1, 0).getBlock())) {
            onStairs.put(p.getUniqueId(), System.currentTimeMillis());
        }
        if (UtilBlock.isSolid(p.getEyeLocation().clone().add(0, 1, 0).getBlock())
            || UtilBlock.isSolid(p.getEyeLocation().clone().add(0, 2, 0).getBlock())
            || p.getLocation().add(0, 1, 0).getBlock().getType().equals(Material.TRAP_DOOR)
            || p.getLocation().add(0 , 1, 0).getBlock().getType().equals(Material.IRON_TRAPDOOR)) {
            belowBlock.put(p.getUniqueId(), System.currentTimeMillis());
        }
        if (e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getFrom().getZ()
            || UtilPlayer.isOnGround(p)
            || SpeedC.highKb.contains(p.getUniqueId())
            || p.getAllowFlight()
            || p.getVehicle() != null
            || e.isCancelled()
            || p.hasPermission("thotpatrol.bypass")
            || SpeedC.jumpingOnIce.contains(p.getUniqueId())
            || UtilPlayer.isOnClimbable(p, 0)
            || UtilPlayer.isOnClimbable(p, 1)
            || UtilPlayer.isOnClimbable(p)
            || !UtilTime.elapsed(belowBlock.getOrDefault(p.getUniqueId(), 0L), 1000L)
            || !UtilTime.elapsed(onStairs.getOrDefault(p.getUniqueId(), 0L), 1000L)
            || !UtilTime.elapsed(SharedEvents.getLastJoin().getOrDefault(p.getUniqueId(), 0L), 1500)
            || !UtilTime.elapsed(AscensionA.toggleFlight.getOrDefault(p.getUniqueId(), 0L), 5000L)
            || !UtilTime.elapsed(getThotPatrol().LastVelocity.getOrDefault(p.getUniqueId(), 0L), 2000)) {
            return;
        }
        if (p.getMaximumNoDamageTicks() < 15) {
            maxDelta += .04;
        }
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        if (delta > maxDelta) {
            count++;
            Bukkit.broadcastMessage("count increase " + p.getName());
        }
        if (count > 3) {
            count = 0;
            getThotPatrol().logCheat(this, p, delta + " > .35");
        }
        verbose.put(p.getUniqueId(), count);
    }
}