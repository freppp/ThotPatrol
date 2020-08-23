package me.frep.thotpatrol.checks.movement.speed;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.checks.movement.ascension.AscensionA;
import me.frep.thotpatrol.data.DataPlayer;
import me.frep.thotpatrol.events.SharedEvents;
import me.frep.thotpatrol.utils.UtilBlock;
import me.frep.thotpatrol.utils.UtilMath;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SpeedC extends Check {

    public static List<UUID>  highKb = new ArrayList<>();

    public SpeedC(ThotPatrol ThotPatrol) {
        super("SpeedC", "Speed (Type C)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(10);
    }
    
    @SuppressWarnings("deprecation")
	public static boolean isReallyOnGround(Player p) {
        Location l = p.getLocation();
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();
        Location b = new Location(p.getWorld(), x, y - 1, z);
        return p.isOnGround() && b.getBlock().getType() != Material.AIR && b.getBlock().getType() != Material.WEB
                && !b.getBlock().isLiquid();
    }

	@SuppressWarnings("deprecation")
	@EventHandler
    public void onMove(PlayerMoveEvent e) {
        Location from = e.getFrom().clone();
        Location to = e.getTo().clone();
        Player p = e.getPlayer();
        Location l = p.getLocation();
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();
        Location blockLoc = new Location(p.getWorld(), x, y - 1, z);
        Location loc = new Location(p.getWorld(), x, y, z);
        Location above = new Location(p.getWorld(), x, y + 2, z);
        Location above3 = new Location(p.getWorld(), x - 1, y + 2, z - 1);
        if (SharedEvents.worldChange.contains(p.getUniqueId())) {
        	return;        
        }
        if ((e.getTo().getX() == e.getFrom().getX()) && (e.getTo().getZ() == e.getFrom().getZ())
                && (e.getTo().getY() == e.getFrom().getY())
                || p.getNoDamageTicks() != 0
                || p.getVehicle() != null
                || e.isCancelled()
                || !UtilTime.elapsed(AscensionA.toggleFlight.getOrDefault(p.getUniqueId(), 0L), 5000L)
                || p.hasPermission("thotpatrol.bypass")
                || !UtilTime.elapsed(SharedEvents.lastTeleport.getOrDefault(p.getUniqueId(), 0L), 4000)
                || p.getGameMode().equals(GameMode.CREATIVE)
                || p.getAllowFlight()) return;
        double Airmaxspeed = 0.40;
        if (!UtilTime.elapsed(getThotPatrol().lastDamage.getOrDefault(p.getUniqueId(), 0L), 3000)) {
            Airmaxspeed += .85;
        }
        if (!UtilTime.elapsed(SpeedI.bowBoost.getOrDefault(p.getUniqueId(), 0L), 3000)) return;
        if (p.getMaximumNoDamageTicks() < 15) {
        	Airmaxspeed += .05;
        }
        for (Block b: UtilBlock.getNearbyBlocks(p.getLocation(), 2)) {
            if (b.getType().toString().contains("PISTON")) {
                return;
            }
            if (b.getType().equals(Material.TRAP_DOOR) || b.getType().toString().equals("IRON_TRAPDOOR")) {
                Airmaxspeed += .03;
            }
        }
        if (SpeedB.hadSpeed.contains(p.getUniqueId())) {
            Airmaxspeed += .5;
            Bukkit.getScheduler().scheduleAsyncDelayedTask(ThotPatrol.Instance, () -> {
                SpeedB.hadSpeed.remove(p.getUniqueId());
            }, 40);
        }
        double tps = getThotPatrol().getLag().getTPS();
        int ping = getThotPatrol().getLag().getPing(p);
        double speed = UtilMath.offset(getHV(to.toVector()), getHV(from.toVector()));
        Material below = p.getLocation().subtract(0, 1.5, 0).getBlock().getType();
        if (!UtilTime.elapsed(SpeedA.nearIce.getOrDefault(p.getUniqueId(), 0L), 4000)) {
            Airmaxspeed += .65;
        }
		if (p.getWalkSpeed() > 0.25) {
			Airmaxspeed += p.getWalkSpeed() * 1;
		}
        if (p.hasPotionEffect(PotionEffectType.SPEED)) {
            int level = getPotionEffectLevel(p);
            if (level > 0) {
                Airmaxspeed = (Airmaxspeed * (((level * 20) * 0.011) + 1));
            }
        }
        if (getThotPatrol().getConfig().getBoolean("instantBans.SpeedC.enabled")
        		&& isBannable() && ping > 1
        		&& speed > getThotPatrol().getConfig().getDouble("instantBans.SpeedC.maxSpeed") 
        		&& tps > getThotPatrol().getConfig().getDouble("instantBans.SpeedC.minTPS")
        		&& ping < getThotPatrol().getConfig().getDouble("instantBans.SpeedC.maxPing")
                && !getThotPatrol().getNamesBanned().containsKey(p.getName())
        		&& !isReallyOnGround(p) && speed >= Airmaxspeed && !isOnIce(p) && !below.equals(Material.ICE) 
        		&& !below.equals(Material.PACKED_ICE) && blockLoc.getBlock().getType() != Material.ICE 
        		&& !blockLoc.getBlock().isLiquid() && !loc.getBlock().isLiquid() 
        		&& blockLoc.getBlock().getType() != Material.PACKED_ICE
                && above.getBlock().getType() == Material.AIR && above3.getBlock().getType() == Material.AIR
                && blockLoc.getBlock().getType() != Material.AIR) {
        	String banAlertMessage = getThotPatrol().getConfig().getString("instantBans.SpeedC.banAlertMessage");
        	getThotPatrol().alert(ChatColor.translateAlternateColorCodes('&', banAlertMessage.replaceAll("%player%", p.getName())
        			.replaceAll("%speed%", Double.toString(Math.round(speed)))));
        	dumplog(p, "[Instant Ban] Air Speed: " + speed + " | TPS: " + tps + " | Ping: " + ping);
        	getThotPatrol().logToFile(p, this, "Air [Instant Ban]", "Speed: " + speed + " | TPS: " + tps + " | Ping: " + ping);
            getThotPatrol().banPlayer(p, this);
        }
        if (!isReallyOnGround(p) && speed >= Airmaxspeed && !isOnIce(p) && !below.equals(Material.ICE) 
        		&& !below.equals(Material.PACKED_ICE) && blockLoc.getBlock().getType() != Material.ICE 
        		&& !blockLoc.getBlock().isLiquid() && !loc.getBlock().isLiquid() 
        		&& blockLoc.getBlock().getType() != Material.PACKED_ICE
                && above.getBlock().getType() == Material.AIR && above3.getBlock().getType() == Material.AIR
                && blockLoc.getBlock().getType() != Material.AIR && !highKb.contains(p.getUniqueId())) {
        	getThotPatrol().logCheat(this, p, "Type: Air | " + speed + " > " + Airmaxspeed + " | Ping: " + ping + " | TPS: " + tps);
        	getThotPatrol().logToFile(p, this, "Air", "Speed: " + UtilMath.trim(5, speed) + " > " + Airmaxspeed
        			+ " | TPS: " + UtilMath.trim(5, tps) + " | Ping: " + ping);
        }
    }
    
    public boolean isOnIce(final Player player) {
        final Location a = player.getLocation();
        a.setY(a.getY() - 1.0);
        if (a.getBlock().getType().equals(Material.ICE)
        		|| a.getBlock().getType().equals(Material.PACKED_ICE)) {
            return true;
        }
        a.setY(a.getY() - 1.0);
        return a.getBlock().getType().equals(Material.ICE);
    }


    private int getPotionEffectLevel(Player p) {
        for (PotionEffect pe : p.getActivePotionEffects()) {
            if (pe.getType().getName().equals(PotionEffectType.SPEED.getName())) {
                return pe.getAmplifier() + 1;
            }
        }
        return 0;
    }

    private Vector getHV(Vector V) {
        V.setY(0);
        return V;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)
                || !(e.getEntity() instanceof Player
                || !e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK))) {
            return;
        }
        Player p = (Player)e.getDamager();
        Player v = (Player)e.getEntity();
        if (p.getItemInHand().getEnchantmentLevel(Enchantment.KNOCKBACK) > 1) {
            highKb.add(v.getUniqueId());
            Bukkit.getScheduler().scheduleAsyncDelayedTask(ThotPatrol.Instance, () -> highKb.remove(v.getUniqueId()), 100);
        }
    }
}