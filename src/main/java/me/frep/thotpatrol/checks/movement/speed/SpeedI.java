package me.frep.thotpatrol.checks.movement.speed;

import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.checks.movement.ascension.AscensionA;
import me.frep.thotpatrol.checks.movement.ascension.AscensionD;
import me.frep.thotpatrol.events.SharedEvents;
import me.frep.thotpatrol.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpeedI extends Check {

    private Map<UUID, Integer> verbose = new HashMap<>();
    public static Map<UUID, Long> invalidBlock = new HashMap<>();
    public static Map<UUID, Long> belowBlock = new HashMap<>();

    public SpeedI(me.frep.thotpatrol.ThotPatrol ThotPatrol) {
        super("SpeedI", "Speed (Type I) [#]", ThotPatrol);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(8);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        verbose.remove(e.getPlayer().getUniqueId());
        invalidBlock.remove(e.getPlayer().getUniqueId());
        belowBlock.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (UtilBlock.isStair(p.getLocation().clone().subtract(0, 1, 0).getBlock())
                || (p.getLocation().subtract(0, 1, 0).getBlock().getType().toString().contains("SLIME")
                || p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().toString().contains("SLIME"))) {
            invalidBlock.put(p.getUniqueId(), System.currentTimeMillis());
        }
        if (p.getEyeLocation().clone().add(0, 1, 0).getBlock().getType().isSolid()
                || p.getEyeLocation().clone().add(0, 2, 0).getBlock().getType().isSolid()
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
                || !UtilTime.elapsed(belowBlock.getOrDefault(p.getUniqueId(), 0L), 750L)
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
        if (p.getWalkSpeed() > .21) {
            maxDelta += p.getWalkSpeed() * 1;
        }
        for (PotionEffect effect : p.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.SPEED)) {
                maxDelta += (effect.getAmplifier() + 1) * .05;
            }
        }
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        if (delta > maxDelta + .4) {
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
        if (count > 4
                && getThotPatrol().getConfig().getBoolean("instantBans.SpeedI.enabled")
                && delta > getThotPatrol().getConfig().getInt("instantBans.SpeedI.maxSpeed")
                && isBannable() && ping > 1
                && !getThotPatrol().getNamesBanned().containsKey(p.getName())
                && !getThotPatrol().NamesBanned.containsKey(p.getName())
                && tps > getThotPatrol().getConfig().getDouble("instantBans.SpeedI.minTPS")
                && ping < getThotPatrol().getConfig().getDouble("instantBans.SpeedI.maxPing")) {
            count = 0;
            String banAlertMessage = getThotPatrol().getConfig().getString("instantBans.SpeedI.banAlertMessage");
            getThotPatrol().alert(ChatColor.translateAlternateColorCodes('&', banAlertMessage.replaceAll("%player%", p.getName())
                    .replaceAll("%speed%", Double.toString(Math.round(delta)))));
            dumplog(p, "[Instant Ban] Air Speed: " + delta + " | TPS: " + tps + " | Ping: " + ping);
            getThotPatrol().logToFile(p, this, "Air [Instant Ban]", "Speed: " + delta + " | TPS: " + tps + " | Ping: " + ping);
            getThotPatrol().banPlayer(p, this);
        }
        if (count > 4) {
            count = 0;
            getThotPatrol().logCheat(this, p, delta + " > .35 | Ping: " + ping + " | TPS: " + tps);
            getThotPatrol().logToFile(p, this, "Ground", "Speed: " + UtilMath.trim(5, delta) + " > " +  maxDelta + " | TPS: " + tps + " | Ping: " + ping);
            dumplog(p, "[Flag] Speed: " + delta + " > " + maxDelta + " | Ping: " + ping + " | TPS: " + tps);
        }
        verbose.put(p.getUniqueId(), count);
    }

    private Vector getHV(Vector V) {
        V.setY(0);
        return V;
    }
}