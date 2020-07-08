package me.frep.thotpatrol.checks.player.badpackets;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.checks.movement.speed.SpeedH;
import me.frep.thotpatrol.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BadPacketsF extends Check {

    private Map<UUID, Double> lastDist = new HashMap<>();
    private Map<UUID, Integer> verbose = new HashMap<>();

    public BadPacketsF(ThotPatrol ThotPatrol) {
        super("BadPacketsF", "Bad Packets (Type F) [#]", ThotPatrol);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(5);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player)e.getWhoClicked();
        if (p.getAllowFlight()
            || p.hasPermission("thotpatrol.bypasss")) {
            return;
        }
        double distance = lastDist.getOrDefault(p.getUniqueId(), 0D);
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        double maxDistance = .15;
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
        if (!UtilTime.elapsed(SpeedH.airTicks.getOrDefault(p.getUniqueId(), 0L), 1000)
            || !UtilTime.elapsed(getThotPatrol().getLastVelocity().getOrDefault(p.getUniqueId(), 0L), 1200)) {
            maxDistance += .3;
        }
        for (PotionEffect effect : p.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.SPEED)) {
                maxDistance += (effect.getAmplifier() + 1) * .125;
            }
        }
        if (p.getWalkSpeed() > .21) {
            maxDistance += p.getWalkSpeed() * 1.25;
        }
        if (distance > maxDistance) {
            count++;
        }
        if (count > 3) {
            count = 0;
            getThotPatrol().logCheat(this, p, "Inventory | Ping: " + ping + " | TPS: " + tps);
            getThotPatrol().logToFile(p, this, "Inventory", "Distance: " + distance + " > " + maxDistance +
                    " | TPS: " + tps + " | Ping: " + ping);
        }
        verbose.put(p.getUniqueId(), count);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        double distance = UtilMath.getHorizontalDistance(e.getTo(), e.getFrom());
        if (UtilPlayer.isOnGround(p)
                || UtilCheat.isInWeb(p)
                || UtilBlock.isNearLiquid(p)
                || UtilPlayer.isOnClimbable(p)) {
            lastDist.put(p.getUniqueId(), 0D);
        }
        lastDist.put(p.getUniqueId(), distance);
    }
}