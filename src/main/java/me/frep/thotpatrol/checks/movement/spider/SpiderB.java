package me.frep.thotpatrol.checks.movement.spider;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.checks.movement.ascension.AscensionA;
import me.frep.thotpatrol.utils.UtilBlock;
import me.frep.thotpatrol.utils.UtilMath;
import me.frep.thotpatrol.utils.UtilPlayer;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class SpiderB extends Check {

    private Map<UUID, Double> lastDiff = new HashMap<>();
    private Map<UUID, Integer> verbose = new HashMap<>();

    public SpiderB(ThotPatrol ThotPatrol) {
        super("SpiderB", "Spider (Type B) [#]", ThotPatrol);
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
                || UtilPlayer.isNearHalfBlock(p)
                || UtilBlock.nearSlime(p, 5)
                || !UtilTime.elapsed(AscensionA.lastNearSlime.getOrDefault(p.getUniqueId(), 0l), 2000)
                || p.getWorld().getHighestBlockAt(p.getLocation()).getType().toString().contains("SLIME")
                || UtilPlayer.isOnClimbable(p)
                || !UtilTime.elapsed(getThotPatrol().lastDamage.getOrDefault(p.getUniqueId(), 0L), 2000)
                || UtilPlayer.isOnClimbable(p, 1)
                || UtilPlayer.isOnClimbable(p, 0)
                || !p.getEyeLocation().clone().add(0, .5, 0).getBlock().getType().equals(Material.AIR)
                || p.hasPotionEffect(PotionEffectType.JUMP)) {
            return;
        }
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
        double delta = UtilMath.offset(UtilMath.getVerticalVector(e.getFrom().toVector()), UtilMath.getVerticalVector(e.getTo().toVector()));
        List<Material> blocks = new ArrayList<>();
        blocks.add(p.getLocation().getBlock().getRelative(BlockFace.NORTH).getType());
        blocks.add(p.getLocation().getBlock().getRelative(BlockFace.SOUTH).getType());
        blocks.add(p.getLocation().getBlock().getRelative(BlockFace.EAST).getType());
        blocks.add(p.getLocation().getBlock().getRelative(BlockFace.WEST).getType());
        for (Material m : blocks) {
            if (m.isSolid() && m != Material.LADDER && m != Material.VINE && m != Material.AIR) {
                if (delta == lastDiff.getOrDefault(p.getUniqueId(), 0D)) {
                    count++;
                }
                if (count > 5) {
                    count = 0;
                    getThotPatrol().logCheat(this, p, "Delta: " + delta + " | Ping: " + ping + " | TPS: " + tps);
                    getThotPatrol().logToFile(p, this, "Delta", "Delta: " + delta + " TPS: " + tps + " | Ping: " + ping);
                }
            }
        }
        verbose.put(p.getUniqueId(), count);
        lastDiff.put(p.getUniqueId(), delta);
    }
}