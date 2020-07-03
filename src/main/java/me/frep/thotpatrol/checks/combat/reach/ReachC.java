package me.frep.thotpatrol.checks.combat.reach;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.PacketPlayerType;
import me.frep.thotpatrol.packets.events.PacketAttackEvent;
import me.frep.thotpatrol.utils.UtilMath;
import me.frep.thotpatrol.utils.UtilPlayer;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class ReachC extends Check {
	
    private Map<UUID, Map.Entry<Double, Double>> offsets = new HashMap<>();
    private Map<UUID, Long> reachTicks = new HashMap<>();
    private HashSet<UUID> projectileHit = new HashSet<>();
	
    public ReachC(ThotPatrol ThotPatrol) {
        super("ReachC", "Reach (Type C)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(8);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ()) return;
        double OffsetXZ = UtilMath.offset(UtilMath.getHorizontalVector(e.getFrom().toVector()),
                UtilMath.getHorizontalVector(e.getTo().toVector()));
        double horizontal = Math.sqrt(Math.pow(e.getTo().getX() - e.getFrom().getX(), 2.0)
                + Math.pow(e.getTo().getZ() - e.getFrom().getZ(), 2.0));
        this.offsets.put(e.getPlayer().getUniqueId(), new AbstractMap.SimpleEntry<>(OffsetXZ, horizontal));
    }

    @EventHandler
    public void onDmg(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)
                || e.getCause() != DamageCause.PROJECTILE) return;
        Player player = (Player) e.getDamager();
        this.projectileHit.add(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLogout(PlayerQuitEvent e) {
        offsets.remove(e.getPlayer().getUniqueId());
        reachTicks.remove(e.getPlayer().getUniqueId());
        projectileHit.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onDamage(PacketAttackEvent e) {
		Player damager = e.getPlayer();
		if (damager == null) {
			return;
		}
		Entity player = e.getEntity();
		if(e.getType() != PacketPlayerType.USE) {
			return;
		}
        if (damager.getAllowFlight()) {
        	return;
        }
        double ydist = Math.abs(damager.getEyeLocation().getY() - player.getLocation().getY());
        double Reach = (UtilPlayer.getEyeLocation(damager).distance(player.getLocation()) - ydist) - 0.32;
        int PingD = getThotPatrol().getLag().getPing(damager);
        double tps = getThotPatrol().getLag().getTPS();
		if (damager.hasPermission("thotpatrol.bypass")) {
			return;
		}
        long attackTime = System.currentTimeMillis();
        if (this.reachTicks.containsKey(damager.getUniqueId())) {
            attackTime = reachTicks.get(damager.getUniqueId());
        }
        if (Reach > 7) {
        	return;
        }
        double yawdif = Math.abs(180 - Math.abs(damager.getLocation().getYaw() - player.getLocation().getYaw()));
        double offsetsp = 0.0D;
        double lastHorizontal = 0.0D;
        double offsetsd = 0.0D;
        if (this.offsets.containsKey(damager.getUniqueId())) {
            offsetsd = (this.offsets.get(damager.getUniqueId())).getKey();
            lastHorizontal = (this.offsets.get(damager.getUniqueId())).getValue();
        }
        if (this.offsets.containsKey(player.getUniqueId())) {
            offsetsp = (this.offsets.get(player.getUniqueId())).getKey();
            lastHorizontal = (this.offsets.get(player.getUniqueId())).getValue();
        }
        Reach -= UtilMath.trim(2, offsetsd);
        Reach -= UtilMath.trim(2, offsetsp);
        double maxReach2 = 3.4;
        if (yawdif > 90) {
            maxReach2 += 0.75;
        }
        maxReach2 += lastHorizontal * 0.98;
        maxReach2 += PingD * 0.005;
        if (damager.isSprinting()) {
        	maxReach2 += .55;
        }
        if (Reach > maxReach2 && UtilTime.elapsed(attackTime, 1100) && !projectileHit.contains(player.getUniqueId())) {
        	getThotPatrol().logCheat(this, damager, Reach + " > " + maxReach2 + " | Ping: " + PingD + " | TPS: " + tps);
        	getThotPatrol().logToFile(damager, this, "First Hit [Packet]", "Reach: " + Reach 
        			+ " > " + maxReach2 + " | TPS: " + tps + " | Ping: " + PingD);
        }
        reachTicks.put(damager.getUniqueId(), UtilTime.nowlong());
        projectileHit.remove(player.getUniqueId());
    }
}