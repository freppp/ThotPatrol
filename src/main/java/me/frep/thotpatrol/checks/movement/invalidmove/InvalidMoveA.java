package me.frep.thotpatrol.checks.movement.invalidmove;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.checks.movement.speed.SpeedC;
import me.frep.thotpatrol.utils.UtilBlock;
import me.frep.thotpatrol.utils.UtilMath;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InvalidMoveA extends Check {

    private Map<UUID, Integer> verbose = new HashMap<>();

    public InvalidMoveA(ThotPatrol ThotPatrol) {
        super("InvalidMoveA", "Invalid Move (Type A) [#]", ThotPatrol);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(6);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
        if (p.getAllowFlight()
            || SpeedC.highKb.contains(p.getUniqueId())
            || !UtilTime.elapsed(getThotPatrol().lastDamage.getOrDefault(p.getUniqueId(), 0L), 1500L)) {
            return;
        }
        if (UtilBlock.isStair(p.getLocation().getBlock().getRelative(BlockFace.DOWN))) {
            double dist = UtilMath.getHorizontalDistance(e.getTo(), e.getFrom());
            double maxDist = .42;
            if (p.getWalkSpeed() > .21) {
                maxDist += p.getWalkSpeed() * 1.1;
            }
            for (PotionEffect effect : p.getActivePotionEffects()) {
                if (effect.getType().equals(PotionEffectType.SPEED)) {
                    maxDist += (effect.getAmplifier() + 1) * .15;
                }
            }
            if (dist > maxDist) {
                count++;
            } else {
                if (count > 0) {
                    count -= 2;
                }
            }
            if (count > 6) {
                count = 0;
                getThotPatrol().logCheat(this, p, "Fast Stairs | TPS: " + tps + " | Ping: " + ping);
                getThotPatrol().logToFile(p, this, "Fast Stairs", "TPS: " + tps + " | Ping: " + ping);
            }
        }
        verbose.put(p.getUniqueId(), count);
    }
}
