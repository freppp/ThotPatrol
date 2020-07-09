package me.frep.thotpatrol.checks.movement.speed;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.checks.movement.ascension.AscensionA;
import me.frep.thotpatrol.events.SharedEvents;
import me.frep.thotpatrol.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpeedH extends Check {

    public static Map <UUID, Long> airTicks = new HashMap<>();
    private Map<UUID, Integer> verbose = new HashMap<>();

    public SpeedH(me.frep.thotpatrol.ThotPatrol ThotPatrol) {
        super("SpeedH", "Speed (Type H) [#]", ThotPatrol);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(8);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        airTicks.remove(e.getPlayer().getUniqueId());
        verbose.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (!UtilPlayer.isOnGround(p)) {
            airTicks.put(p.getUniqueId(), System.currentTimeMillis());
        }
        if (e.getTo().getX() == e.getFrom().getX() && e.getFrom().getZ() == e.getTo().getZ()
                || e.getFrom().getY() != e.getTo().getY()
                || !UtilPlayer.isOnGround(p)
                || p.getVehicle() != null
                || p.getAllowFlight()
                || e.isCancelled()
                || !UtilTime.elapsed(airTicks.getOrDefault(p.getUniqueId(), 0L), 500)
                || !UtilTime.elapsed(SharedEvents.getLastJoin().getOrDefault(p.getUniqueId(), 0L), 1500)
                || !UtilTime.elapsed(AscensionA.toggleFlight.getOrDefault(p.getUniqueId(), 0L), 5000L)
                || p.hasPermission("thotpatrol.bypass")
                || SpeedC.highKb.contains(p.getUniqueId())
                || !UtilPlayer.isOnGround(p.getLocation())) {
            return;
        }
        for (Block b : UtilBlock.getNearbyBlocks(p.getLocation(), 2)) {
            if (b.getType().toString().contains("ICE")) {
                return;
            }
        }
        double maxSpeed = .29;
        if (!UtilTime.elapsed(getThotPatrol().lastDamage.getOrDefault(p.getUniqueId(), 0L), 1500)) {
            maxSpeed += .15;
        }
        if (SpeedB.hadSpeed.contains(p.getUniqueId())) {
            maxSpeed += .5;
            Bukkit.getScheduler().scheduleAsyncDelayedTask(me.frep.thotpatrol.ThotPatrol.Instance, () -> {
                SpeedB.hadSpeed.remove(p.getUniqueId());
            }, 40);
        }
        double tps = getThotPatrol().getLag().getTPS();
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        double speed = UtilMath.getHorizontalDistance(e.getFrom(), e.getTo());
        int ping = getThotPatrol().getLag().getPing(p);
        if (p.getLocation().clone().add(0, .5, 0).getBlock().getType().toString().contains("DOOR")
                || p.getLocation().clone().add(0, 1, 0).getBlock().getType().toString().contains("DOOR")
                || p.getEyeLocation().clone().add(0, 1, 0).getBlock().getType().isSolid()
                || p.getEyeLocation().clone().add(0, .5, 0).getBlock().getType().isSolid()
                || UtilBlock.isStair(p.getLocation().getBlock().getRelative(BlockFace.DOWN))) {
            maxSpeed += .15;
        }
        if (p.getWalkSpeed() > .21) {
            maxSpeed += p.getWalkSpeed() * 1.5;
        }
        for (PotionEffect effect : p.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.SPEED)) {
                maxSpeed += (effect.getAmplifier() + 1) * .06;
            }
        }
        if (speed > maxSpeed + .5) {
            count += 6;
            dumplog(p, "[Count Increase (High)] Speed: " + speed + " > " + maxSpeed + " | Ping: " + ping + " | TPS: " + tps);
        }
        if (speed > maxSpeed + .2) {
            count += 3;
            dumplog(p, "[Count Increase (High)] Speed: " + speed + " > " + maxSpeed + " | Ping: " + ping + " | TPS: " + tps);
        } else {
            if (speed > maxSpeed) {
                count++;
                dumplog(p, "[Count Increase] Speed: " + speed + " > " + maxSpeed + " | Ping: " + ping + " | TPS: " + tps);
            } else {
                if (count > 0) {
                    count -= 1.5;
                }
            }
        }
        if (count > 11
                && getThotPatrol().getConfig().getBoolean("instantBans.SpeedH.enabled")
                && speed > getThotPatrol().getConfig().getInt("instantBans.SpeedH.maxSpeed")
                && isBannable() && ping > 1 && !getThotPatrol().getNamesBanned().containsKey(p.getName())
                && tps > getThotPatrol().getConfig().getDouble("instantBans.SpeedH.minTPS")
                && ping < getThotPatrol().getConfig().getDouble("instantBans.SpeedH.maxPing")) {
            count = 0;
            String banAlertMessage = getThotPatrol().getConfig().getString("instantBans.SpeedH.banAlertMessage");
            getThotPatrol().alert(ChatColor.translateAlternateColorCodes('&', banAlertMessage.replaceAll("%player%", p.getName())
                    .replaceAll("%speed%", Double.toString(Math.round(speed)))));
            dumplog(p, "[Instant Ban] Ground Speed: " + speed + " | TPS: " + tps + " | Ping: " + ping);
            getThotPatrol().logToFile(p, this, "Average [Instant Ban]", "Speed: " + speed + " | TPS: " + tps + " | Ping: " + ping);
            getThotPatrol().banPlayer(p, this);
        }
        if (count > 11) {
            getThotPatrol().logCheat(this, p, speed + " > " + maxSpeed + " | Ping: " + ping + " | TPS: " + tps);
            getThotPatrol().logToFile(p, this, "Ground", "Speed: " + speed + " > " + maxSpeed + " | TPS: " + tps + " | Ping: " + ping);
            dumplog(p, "[Flag] Speed: " + speed + " > " + maxSpeed + " | Ping: " + ping + " | TPS: " + tps);
            count = 0;
        }
        verbose.put(p.getUniqueId(), count);
    }
}