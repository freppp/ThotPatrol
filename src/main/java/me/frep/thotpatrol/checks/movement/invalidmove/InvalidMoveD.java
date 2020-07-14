package me.frep.thotpatrol.checks.movement.invalidmove;

import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.checks.movement.speed.SpeedH;
import me.frep.thotpatrol.checks.movement.speed.SpeedI;
import me.frep.thotpatrol.utils.UtilBlock;
import me.frep.thotpatrol.utils.UtilMath;
import me.frep.thotpatrol.utils.UtilPlayer;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InvalidMoveD extends Check {

    private Map<UUID, Integer> verbose = new HashMap<>();
    private Map<UUID, Long> nearSlime = new HashMap<>();

    public InvalidMoveD(me.frep.thotpatrol.ThotPatrol ThotPatrol) {
        super("InvalidMoveD", "Invalid Move (Type D) [#]", ThotPatrol);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(6);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        double tps = getThotPatrol().getLag().getTPS();
        int ping = getThotPatrol().getLag().getPing(p);
        if (!UtilPlayer.isOnGround(p)
            || p.getAllowFlight()
            || p.hasPermission("thotpatrol.bypass")
            || !UtilTime.elapsed(SpeedI.belowBlock.getOrDefault(p.getUniqueId(), 0L), 1250L)
            || !UtilTime.elapsed(getThotPatrol().lastDamage.getOrDefault(p.getUniqueId(), 0L), 1500L)
            || !UtilTime.elapsed(SpeedH.airTicks.getOrDefault(p.getUniqueId(), 0L), 1000)) {
            return;
        }
        if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().toString().contains("ICE")) {
            double speed = UtilMath.getHorizontalDistance(e.getTo(), e.getFrom());
            double maxSpeed = .29;
            for (PotionEffect effect : p.getActivePotionEffects()) {
                if (effect.getType().equals(PotionEffectType.SPEED)) {
                    maxSpeed += (effect.getAmplifier() + 1) * .15;
                }
            }
            for (Block b: UtilBlock.getNearbyBlocks(p.getLocation(), 2)) {
                if (b.getType().toString().contains("PISTON_") || b.getType().toString().contains("SLIME")) {
                    nearSlime.put(p.getUniqueId(), System.currentTimeMillis());
                }
            }
            if (!UtilTime.elapsed(nearSlime.getOrDefault(p.getUniqueId(), 0L), 1500L)) {
                maxSpeed += .75;
            }
            if (p.getWalkSpeed() > .21) {
                maxSpeed += p.getWalkSpeed() * 1.1;
            }
            if (speed > maxSpeed) {
                count++;
            } else {
                if (count > 0) {
                    count--;
                }
            }
            if (count > 5) {
                count = 0;
                getThotPatrol().logCheat(this, p, "Ice Speed | Ping: " + ping + " | TPS: " + tps);
                getThotPatrol().logToFile(p, this, "Ice Speed", "TPS: " + tps + " | Ping: " + ping);
            }
        }
        verbose.put(p.getUniqueId(), count);
    }
}
