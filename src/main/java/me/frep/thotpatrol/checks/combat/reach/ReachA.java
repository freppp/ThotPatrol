package me.frep.thotpatrol.checks.combat.reach;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReachA extends Check {

	public static Map<UUID, Integer> verbose = new HashMap<UUID, Integer>();

    public ReachA(ThotPatrol ThotPatrol) {
        super("ReachA", "Reach (Type A)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(8);
    }
    
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
    	verbose.remove(e.getPlayer().getUniqueId());
    }
    
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player)
				|| !(e.getEntity() instanceof Player)
				|| (!e.getCause().equals(DamageCause.ENTITY_ATTACK))) {
			return;
		}
		Player d = (Player)e.getDamager();
		Player v = (Player)e.getEntity();
		if (d.getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		double reach = Math.hypot(d.getLocation().getX() - v.getLocation().getX(), d.getLocation().getZ() - v.getLocation().getZ()) - .32;
		double maxReach = 3.1;
		double yawDiff = Math.abs(180.0 - Math.abs(d.getLocation().getYaw() - v.getLocation().getYaw()));
		double yDiff = Math.abs(d.getEyeLocation().getY() - v.getEyeLocation().getY());
		int pingD = getThotPatrol().getLag().getPing(d);
		int pingV = getThotPatrol().getLag().getPing(v);
		double tps = getThotPatrol().getLag().getTPS();
		UUID uuid = d.getUniqueId();
		int count = verbose.getOrDefault(uuid, 0);
		if (d.hasPermission("thotpatrol.bypass")) {
			return;
		}
		if (d.isSprinting()) {
			maxReach += .52;
		}
		if (d.getLocation().getBlock().isLiquid() || v.getLocation().getBlock().isLiquid()) {
			maxReach += .25;
		}
		if (yDiff > 5) {
			return;
		}
        for (PotionEffect effect : d.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.SPEED)) {
                 maxReach += 0.40 * (effect.getAmplifier() + 1);
            }
        }
        maxReach += (pingD + pingV) / 2 * 0.005;
		maxReach += ((yawDiff > 100.0 && v.getVelocity().getY() < .2) ? (yawDiff * .011) : (yawDiff * .001));
		maxReach += yDiff / .25;
		maxReach += v.getVelocity().length() * 2.8;
		maxReach += v.getVelocity().getY() * .9;
		maxReach += v.getVelocity().getX() * .34;
		maxReach += ((d.getWalkSpeed() <= 0.2) ? 0.0 : (d.getWalkSpeed() - 0.2));
        if (reach > maxReach + 1.25) {
            count += 3;
			getThotPatrol().verbose(this, d, pingD, tps, "Blatant | " + reach + " > " + maxReach);
            dumplog(d, "[Count Increase (Blatant)]: " + reach + " > " + maxReach + " | TPS: " + tps + " | Ping: " + pingD);
        } else {
        	if (reach > maxReach) {
        		count += 1;
				getThotPatrol().verbose(this, d, pingD, tps, reach + " > " + maxReach);
    			dumplog(d, "[Count Increase] Reach: " + reach + " > " + maxReach + " | TPS: " + tps + " | Ping: " + pingD);
        	}
        }
        if (count > 5 && reach > maxReach) {
			count = 0;
			getThotPatrol().logCheat(this, d, reach + " > " + maxReach + " | Ping:" + pingD + " | TPS: " + tps);
			dumplog(d, "[Violation] Reach: " + reach + " > " + maxReach);
        	getThotPatrol().logToFile(d, this, "Damage", "Reach: " + reach 
        			+ " > " + maxReach + " | Yaw Difference: " + yawDiff + " | Y Difference: " + yDiff
        			+ " | TPS: " + tps + " | Ping: " + pingD);
		}
        verbose.put(uuid, count);
	}
}