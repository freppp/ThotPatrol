package me.frep.thotpatrol.checks.movement.invalidmove;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.checks.movement.speed.SpeedC;
import me.frep.thotpatrol.checks.movement.speed.SpeedH;
import me.frep.thotpatrol.utils.UtilMath;
import me.frep.thotpatrol.utils.UtilPlayer;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InvalidMoveB extends Check {

    private Map<UUID, Integer> verbose = new HashMap<>();

    public InvalidMoveB(ThotPatrol ThotPatrol) {
        super("InvalidMoveB", "Invalid Move (Type B) [#]", ThotPatrol);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(6);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (p.hasPermission("thotpatrol.bypass")
            || p.getAllowFlight()
            || SpeedC.highKb.contains(p.getUniqueId())
            || !UtilTime.elapsed(getThotPatrol().lastDamage.getOrDefault(p.getUniqueId(), 0L), 1500L)) {
            return;
        }
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        if (p.getLocation().clone().subtract(0, .75, 0).getBlock().getType().equals(Material.SOUL_SAND)
            && UtilPlayer.isOnGround(p) && p.getLocation().getY() % 1 == .87500) {
            double dist = UtilMath.getHorizontalDistance(e.getTo(), e.getFrom());
            double maxDist = .14;
            if (!UtilTime.elapsed(SpeedH.airTicks.getOrDefault(p.getUniqueId(), 0L), 1000)) {
                maxDist += .22;
            }
            for (PotionEffect effect : p.getActivePotionEffects()) {
                if (effect.getType().equals(PotionEffectType.SPEED)) {
                    maxDist += (effect.getAmplifier() + 1) * .15;
                }
            }
            if (p.getWalkSpeed() > .21) {
                maxDist += p.getWalkSpeed() * 1.1;
            }
            if (dist > maxDist) {
                count++;
            } else {
                if (count > 0) {
                    count--;
                }
            }
            if (count > 8) {
                count = 0;
                getThotPatrol().logCheat(this, p, "Soul Sand | Ping: " + ping + " | TPS: " + tps);
                getThotPatrol().logToFile(p, this, "Soul Sand", "TPS: " + tps + " | Ping: " + ping);
            }
        }
        verbose.put(p.getUniqueId(), count);
    }
}