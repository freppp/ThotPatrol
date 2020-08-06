package me.frep.thotpatrol.checks.movement.invalidmove;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.checks.movement.speed.SpeedI;
import me.frep.thotpatrol.utils.UtilMath;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InvalidMoveC extends Check {

    private Map<UUID, Integer> verbose = new HashMap<>();

    public InvalidMoveC(ThotPatrol ThotPatrol) {
        super("InvalidMoveC", "Invalid Move (Type C) [#]", ThotPatrol);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(6);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        if (p.getAllowFlight()
                || p.getVehicle() != null
                || p.hasPermission("thotpatrol.bypass")
                || !UtilTime.elapsed(SpeedI.bowBoost.getOrDefault(p.getUniqueId(), 0L), 2000)
                || !UtilTime.elapsed(getThotPatrol().lastDamage.getOrDefault(p.getUniqueId(), 0L), 1500L)) {
            return;
        }
        if (p.getLocation().getBlock().isLiquid() && !p.getEyeLocation().getBlock().getType().equals(Material.AIR)) {
            double dist = UtilMath.getHorizontalDistance(e.getTo(), e.getFrom());
            double maxDist = .16;
            for (PotionEffect effect : p.getActivePotionEffects()) {
                if (effect.getType().equals(PotionEffectType.SPEED)) {
                    maxDist += (effect.getAmplifier() + 1) * .15;
                }
            }
            for (Entity entity : p.getNearbyEntities(2, 2, 2)) {
                if (entity instanceof Boat) {
                    return;
                }
            }
            if (p.getWalkSpeed() > .21) {
                maxDist += p.getWalkSpeed() * 1.1;
            }
            if (p.getInventory().getBoots() != null) {
                if (p.getInventory().getBoots().getEnchantmentLevel(Enchantment.getByName("DEPTH_STRIDER")) > 0) {
                    maxDist += .5 * p.getInventory().getBoots().getEnchantmentLevel(Enchantment.getByName("DEPTH_STRIDER"));
                }
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
                getThotPatrol().logCheat(this, p, "Water Speed | Ping: " + ping + " | TPS: " + tps);
                getThotPatrol().logToFile(p, this, "Water Speed", "TPS: " + tps + " | Ping: " + ping);
            }
        }
        verbose.put(p.getUniqueId(), count);
    }
}
