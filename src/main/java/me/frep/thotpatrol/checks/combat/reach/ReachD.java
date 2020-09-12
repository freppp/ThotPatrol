package me.frep.thotpatrol.checks.combat.reach;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.PacketPlayerType;
import me.frep.thotpatrol.packets.events.PacketAttackEvent;
import me.frep.thotpatrol.utils.UtilMath;
import me.frep.thotpatrol.utils.UtilPlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ReachD extends Check {
	
	public static Map<UUID, Integer> verbose = new HashMap<UUID, Integer>();
	private Map<UUID, Double> lastDist = new HashMap<>();
	private Map<UUID, Integer> beforeVL = new HashMap<>();
	
    public ReachD(ThotPatrol ThotPatrol) {
        super("ReachD", "Reach (Type D)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(7);
		setViolationResetTime(120000);
    }

    //todo this is a somewhat decent blatant reach check, it shouldnt false too much, but obviously using PacketPlayOutRelEntityMove and AABB is much more accurate than anything that will be found in here
    
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
    	verbose.remove(e.getPlayer().getUniqueId());
    	lastDist.remove(e.getPlayer().getUniqueId());
    	beforeVL.remove(e.getPlayer().getUniqueId());
    }
    
    @EventHandler
    public void onAttack(PacketAttackEvent e) {
    	if (!(e.getEntity() instanceof Player)) {
    		return;
		}
		Player d = e.getPlayer();
		Player p = (Player)e.getEntity();
		if(e.getType() != PacketPlayerType.USE
			|| d.hasPermission("thotpatrol.bypass")
			|| d.getAllowFlight()
			|| p.getAllowFlight()
			|| d.getGameMode().equals(GameMode.CREATIVE)) {
			return;
		}
		int count = verbose.getOrDefault(d.getUniqueId(), 0);
		int preVL = beforeVL.getOrDefault(d.getUniqueId(), 0);
		double yDist = Math.abs(UtilPlayer.getEyeLocation(d).getY() - UtilPlayer.getEyeLocation(p).getY()) > 0.5 
				? Math.abs(UtilPlayer.getEyeLocation(d).getY() - UtilPlayer.getEyeLocation(p).getY()) : 0;
		double yawDiff = Math.abs(180 - Math.abs(d.getLocation().getYaw() - p.getLocation().getYaw()));
        double reach = (UtilPlayer.getEyeLocation(d).distance(p.getEyeLocation()) - yDist) - 0.32;
        if (reach > 6.5) return;
		double maxReach = 3.1;
		double speed = lastDist.getOrDefault(d.getUniqueId(), 0D);
		double tps = getThotPatrol().getLag().getTPS();
		int ping = getThotPatrol().getLag().getPing(d);
		int ping2 = getThotPatrol().getLag().getPing(p);
		if (!UtilPlayer.isOnGround(d)) {
			maxReach += .23;
		}
		maxReach += yawDiff > 100 && yDist < 0.1 ? yawDiff * 0.01 : yawDiff * 0.001;
		maxReach += speed * .58;
		maxReach += yDist * .75;
		maxReach += ((ping + ping2) / 2) * 0.0034;
		for (PotionEffect effect : d.getActivePotionEffects()) {
			if (effect.getType().equals(PotionEffectType.SPEED)) {
				int level = effect.getAmplifier() + 1;
				maxReach += level * .125;
			}
		}
		if (reach > maxReach) {
			count++;
		} else {
			if (count > 0) {
				count--;
			}
		}
		if (count > 3) {
			getThotPatrol().verbose(this, d, ping, tps, reach + " > " + maxReach);
			preVL++;
		}
		if (preVL >= 2) {
			count = 0;
			preVL = 0;
			getThotPatrol().logCheat(this, d, "Reach: " + reach + " > " + maxReach + " | Ping: " + ping + " | TPS: " + tps);
        	getThotPatrol().logToFile(d, this, "Reach (Consecutive)", "Reach: " + reach 
        			+ " > " + maxReach + " | TPS: " + tps + " | Ping: " + ping);
		}
		beforeVL.put(d.getUniqueId(), preVL);
		verbose.put(d.getUniqueId(), count);
    }

    
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
    	Player p = e.getPlayer();
    	double dist = UtilMath.getHorizontalDistance(e.getTo(), e.getFrom());
    	lastDist.put(p.getUniqueId(), dist);
    }
}