package me.frep.thotpatrol.checks.combat.reach;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.PacketPlayerType;
import me.frep.thotpatrol.packets.events.PacketAttackEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReachB extends Check {
	
    public static Map<UUID, Integer> verbose = new HashMap<UUID, Integer>();
	
    public ReachB(ThotPatrol ThotPatrol) {
        super("ReachB", "Reach (Type B)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(9);
    }
    
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
    	verbose.remove(e.getPlayer().getUniqueId());
    }
    
	@EventHandler
	public void onDamage(PacketAttackEvent e) {
		Player d = e.getPlayer();
		if (d == null) {
			return;
		}
		Entity v = e.getEntity();
		if(e.getType() != PacketPlayerType.USE) {
			return;
		}
		double reach = Math.hypot(d.getLocation().getX() - v.getLocation().getX(), d.getLocation().getZ() - v.getLocation().getZ()) - .28;
		double maxReach = 3.4;
		double yawDiff = Math.abs(180.0 - Math.abs(d.getLocation().getYaw() - v.getLocation().getYaw()));
		double yDiff = Math.abs(d.getEyeLocation().getY() - v.getLocation().getY());
		int pingD = getThotPatrol().getLag().getPing(d);
		double tps = getThotPatrol().getLag().getTPS();
		UUID uuid = d.getUniqueId();
		int count = verbose.getOrDefault(uuid, 0);
		if (d.isSprinting()) {
			maxReach += .45;
		}
		if (d.hasPermission("thotpatrol.bypass")) {
			return;
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
        maxReach += pingD * 0.008;
		maxReach += ((yawDiff > 100.0 && v.getVelocity().getY() < .2) ? (yawDiff * .011) : (yawDiff * .001));
		maxReach += yDiff / .70;
		maxReach += v.getVelocity().length() * 2;
		maxReach += v.getVelocity().getY() * .75;
		maxReach += v.getVelocity().getX() * .15;
		maxReach += ((d.getWalkSpeed() <= 0.2) ? 0.0 : (d.getWalkSpeed() - 0.2));
        if (reach > maxReach) {
            count += 1;
			getThotPatrol().verbose(this, d, pingD, tps, reach + " > " + maxReach);
        }
        if (count > 12 && reach > maxReach) {
			count = 0;
			getThotPatrol().logCheat(this, d, reach + " > " + maxReach + " | Ping: " + pingD + " | TPS: " + tps);
			getThotPatrol().logToFile(d, this, "Packet", "Reach: " + reach 
        			+ " > " + maxReach + " | Yaw Difference: " + yawDiff + " | Y Difference: " + yDiff
        			+ " | TPS: " + tps + " | Ping: " + pingD);
		}
        verbose.put(uuid, count);
	}
}