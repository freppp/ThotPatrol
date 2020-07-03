package me.frep.thotpatrol.checks.movement.speed;

import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.checks.movement.ascension.AscensionA;
import me.frep.thotpatrol.checks.movement.ascension.AscensionD;
import me.frep.thotpatrol.events.SharedEvents;
import me.frep.thotpatrol.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpeedI extends Check {

    private Map<UUID, Integer> verbose = new HashMap<>();
    private Map<UUID, Long> invalidBlock = new HashMap<>();
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
        if (UtilBlock.isStair(p.getLocation().clone().subtract(0, 1, 0).getBlock())
                || (p.getLocation().subtract(0, 1, 0).getBlock().getType().toString().contains("SLIME")
                || p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().toString().contains("SLIME"))) {
            invalidBlock.put(p.getUniqueId(), System.currentTimeMillis());
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
                || p.getLocation().getBlock().isLiquid()
                || p.hasPermission("thotpatrol.bypass")
                || SpeedC.jumpingOnIce.contains(p.getUniqueId())
                || UtilPlayer.isOnClimbable(p, 0)
                || UtilPlayer.isOnClimbable(p, 1)
                || UtilPlayer.isOnClimbable(p)
                || !UtilTime.elapsed(AscensionD.explosionTicks.getOrDefault(p.getUniqueId(), 0L), 2500)
                || !UtilTime.elapsed(belowBlock.getOrDefault(p.getUniqueId(), 0L), 1000L)
                || !UtilTime.elapsed(invalidBlock.getOrDefault(p.getUniqueId(), 0L), 1000L)
                || !UtilTime.elapsed(SharedEvents.getLastJoin().getOrDefault(p.getUniqueId(), 0L), 1500)
                || !UtilTime.elapsed(AscensionA.toggleFlight.getOrDefault(p.getUniqueId(), 0L), 5000L)
                || !UtilTime.elapsed(getThotPatrol().LastVelocity.getOrDefault(p.getUniqueId(), 0L), 1200)) {
            return;
        }
        double delta = UtilMath.offset(getHV(e.getTo().toVector()), getHV(e.getFrom().toVector()));
        double maxDelta = .35;
        double tps = getThotPatrol().getLag().getTPS();
        int ping = getThotPatrol().getLag().getPing(p);
        if (p.getMaximumNoDamageTicks() < 15) {
            maxDelta += .04;
        }
        //todo
        if (p.getWalkSpeed() > .21) {
            maxDelta += p.getWalkSpeed() * 1;
        }
        for (PotionEffect effect : p.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.SPEED)) {
                maxDelta += (effect.getAmplifier() + 1) * .05;
            }
        }
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        if (delta > maxDelta + .6) {
            count += 4;
        } else {
            if (delta > maxDelta) {
                count++;
            } else {
                if (count > 0) {
                    count -= .5;
                }
            }
        }
        if (count > 4) {
            count = 0;
            getThotPatrol().logCheat(this, p, delta + " > .35 | Ping: " + ping + " | TPS: " + tps);
        }
        verbose.put(p.getUniqueId(), count);
    }

    private Vector getHV(Vector V) {
        V.setY(0);
        return V;
    }
}