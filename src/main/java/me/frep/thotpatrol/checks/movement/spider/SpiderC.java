package me.frep.thotpatrol.checks.movement.spider;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.checks.movement.ascension.AscensionA;
import me.frep.thotpatrol.checks.movement.ascension.AscensionD;
import me.frep.thotpatrol.checks.movement.speed.SpeedI;
import me.frep.thotpatrol.events.SharedEvents;
import me.frep.thotpatrol.utils.UtilBlock;
import me.frep.thotpatrol.utils.UtilMath;
import me.frep.thotpatrol.utils.UtilPlayer;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class SpiderC extends Check {

    private Map<UUID, Integer> verbose = new HashMap<>();

    public SpiderC(ThotPatrol ThotPatrol) {
        super("SpiderC", "Spider (Type C) [#]", ThotPatrol);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(4);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        verbose.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (p.getAllowFlight()
                || p.getVehicle() != null
                || UtilBlock.isNearFence(p)
                || UtilBlock.isNearPiston(p)
                || UtilBlock.isNearLiquid(p)
                || e.getTo().getY() <= e.getFrom().getY()
                || UtilBlock.nearSlime(p, 5)
                || p.getWorld().getHighestBlockAt(p.getLocation()).getType().toString().contains("SLIME")
                || UtilPlayer.isNearHalfBlock(p)
                || p.hasPermission("thotpatrol.bypass")
                || UtilBlock.isNearStair(p)
                || UtilPlayer.isOnClimbable(p)
                || !UtilTime.elapsed(SharedEvents.bucketEmpty.getOrDefault(p.getUniqueId(), 0L), 3000)
                || !UtilTime.elapsed(SpeedI.bowBoost.getOrDefault(p.getUniqueId(), 0L), 3000)
                || !UtilTime.elapsed(AscensionA.lastNearSlime.getOrDefault(p.getUniqueId(), 0l), 2000)
                || !UtilTime.elapsed(getThotPatrol().lastDamage.getOrDefault(p.getUniqueId(), 0L), 2000)
                || UtilPlayer.isOnClimbable(p, 1)
                || UtilPlayer.isOnClimbable(p, 0)
                || !UtilTime.elapsed(AscensionD.explosionTicks.getOrDefault(p.getUniqueId(), 0L), 5000)
                || !UtilTime.elapsed(SharedEvents.lastPearl.getOrDefault(p.getUniqueId(), 0L), 2000)
                || !p.getEyeLocation().clone().add(0, .5, 0).getBlock().getType().equals(Material.AIR)
                || !p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR)
                || p.hasPotionEffect(PotionEffectType.JUMP)) {
            return;
        }
        for (Block b : UtilBlock.getNearbyBlocks(p.getLocation(), 2)) {
            if (b.getType().toString().contains("CHEST")) return;
        }
        double delta = UtilMath.offset(UtilMath.getVerticalVector(e.getFrom().toVector()), UtilMath.getVerticalVector(e.getTo().toVector()));
        List<Material> blocks = new ArrayList<>();
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        blocks.add(p.getLocation().getBlock().getRelative(BlockFace.NORTH).getType());
        blocks.add(p.getLocation().getBlock().getRelative(BlockFace.SOUTH).getType());
        blocks.add(p.getLocation().getBlock().getRelative(BlockFace.EAST).getType());
        blocks.add(p.getLocation().getBlock().getRelative(BlockFace.WEST).getType());
        for (Material m : blocks) {
            if (m.isSolid() && m != Material.LADDER && m != Material.VINE && m != Material.AIR) {
                if (UtilPlayer.getDistanceToGround(p) > 2) {
                    if (delta > .1) {
                        count++;
                    } else {
                        if (count > 0) count--;
                    }
                    if (count > 6) {
                        count = 0;
                        getThotPatrol().logCheat(this, p, "Distance | Ping: " + ping + " | TPS: " + tps);
                        getThotPatrol().logToFile(p, this, "Distance", "Distance: " + UtilPlayer.getDistanceToGround(p) + " Delta: " + delta + " TPS: " + tps + " | Ping: " + ping);
                    }
                }
            }
        }
        verbose.put(p.getUniqueId(), count);
    }
}