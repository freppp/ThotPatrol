package me.frep.thotpatrol.checks.movement.speed;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.utils.UtilMath;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class SpeedD extends Check {

    public SpeedD(ThotPatrol ThotPatrol) {
        super("SpeedD", "Speed (Type D)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(6);
    }

	@EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Location from = e.getFrom().clone();
        Location to = e.getTo().clone();
        Player p = e.getPlayer();
        Location l = p.getLocation();
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();
        Location loc2 = new Location(p.getWorld(), x, y + 1, z);
        Location above = new Location(p.getWorld(), x, y + 2, z);
        if ((e.getTo().getX() == e.getFrom().getX()) && (e.getTo().getZ() == e.getFrom().getZ())
                && (e.getTo().getY() == e.getFrom().getY())
                || p.getNoDamageTicks() != 0
                || p.getVehicle() != null
                || !UtilTime.elapsed(getThotPatrol().lastDamage.getOrDefault(p.getUniqueId(), 0L), 1500)
                || p.getGameMode().equals(GameMode.CREATIVE)
                || p.getAllowFlight()
                || p.hasPermission("thotpatrol.bypass")) return;
        double newmaxspeed = 0.75;
        if (!UtilTime.elapsed(SpeedA.nearIce.getOrDefault(p.getUniqueId(), 0L), 4000)) {
            newmaxspeed += .65;
        }
        double speed = UtilMath.offset(getHV(to.toVector()), getHV(from.toVector()));
        if (p.hasPotionEffect(PotionEffectType.SPEED)) {
            int level = getPotionEffectLevel(p, PotionEffectType.SPEED);
            if (level > 0) {
                newmaxspeed = (newmaxspeed * (((level * 20) * 0.011) + 1));
            }
        }
        if (SpeedB.hadSpeed.contains(p.getUniqueId())) {
            newmaxspeed += .5;
            Bukkit.getScheduler().scheduleAsyncDelayedTask(me.frep.thotpatrol.ThotPatrol.Instance, () -> {
                SpeedB.hadSpeed.remove(p.getUniqueId());
            }, 40);
        }
        if (p.getWalkSpeed() > .21) {
            newmaxspeed += p.getWalkSpeed() * 1.5;
        }
        double tps = getThotPatrol().getLag().getTPS();
        int ping = getThotPatrol().getLag().getPing(p);
        if (getThotPatrol().getConfig().getBoolean("instantBans.SpeedD.enabled")
                && isBannable()
                && speed > getThotPatrol().getConfig().getDouble("instantBans.SpeedD.maxSpeed")
                && tps > getThotPatrol().getConfig().getDouble("instantBans.SpeedD.minTPS")
                && ping < getThotPatrol().getConfig().getDouble("instantBans.SpeedD.maxPing")
                && !getThotPatrol().getNamesBanned().containsKey(p.getName())
                && speed >= newmaxspeed && p.getFallDistance() < 0.6
                && loc2.getBlock().getType() != Material.TRAP_DOOR && above.getBlock().getType() == Material.AIR
                && loc2.getBlock().getType() == Material.AIR) {
            String banAlertMessage = getThotPatrol().getConfig().getString("instantBans.SpeedD.banAlertMessage");
            getThotPatrol().alert(ChatColor.translateAlternateColorCodes('&', banAlertMessage.replaceAll("%player%", p.getName())
                    .replaceAll("%speed%", Double.toString(Math.round(speed)))));
            dumplog(p, "[Instant Ban] Limit: " + speed + " | TPS: " + tps + " | Ping: " + ping);
            getThotPatrol().logToFile(p, this, "Limit [Instant Ban]", "Speed: " + speed + " | TPS: " + tps + " | Ping: " + ping);
            getThotPatrol().banPlayer(p, this);
        }
        if (speed >= newmaxspeed && p.getFallDistance() < 0.6
                && loc2.getBlock().getType() != Material.TRAP_DOOR && above.getBlock().getType() == Material.AIR
                && loc2.getBlock().getType() == Material.AIR) {
        	getThotPatrol().logCheat(this, p, "Type: Limit | " + speed + " > " + newmaxspeed + " | Ping: " + ping + " TPS: " + tps);
        	getThotPatrol().logToFile(p, this, "Limit", "Speed: " + speed + " > " + newmaxspeed 
        			+ " | TPS: " + tps + " | Ping: " + ping);
        }
    }
    
    public boolean isOnIce(final Player player) {
        final Location a = player.getLocation();
        a.setY(a.getY() - 1.0);
        if (a.getBlock().getType().equals(Material.ICE)) {
            return true;
        }
        a.setY(a.getY() - 1.0);
        return a.getBlock().getType().equals(Material.ICE);
    }

    private int getPotionEffectLevel(Player p, PotionEffectType pet) {
        for (PotionEffect pe : p.getActivePotionEffects()) {
            if (pe.getType().getName().equals(pet.getName())) {
                return pe.getAmplifier() + 1;
            }
        }
        return 0;
    }

    private Vector getHV(Vector V) {
        V.setY(0);
        return V;
    }
}