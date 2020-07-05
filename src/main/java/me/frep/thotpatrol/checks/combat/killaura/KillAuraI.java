package me.frep.thotpatrol.checks.combat.killaura;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.PacketPlayerType;
import me.frep.thotpatrol.packets.events.PacketAttackEvent;
import me.frep.thotpatrol.utils.UtilBlock;
import me.frep.thotpatrol.utils.UtilMath;
import me.frep.thotpatrol.utils.UtilPlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillAuraI extends Check {
	
	private Map<UUID, Integer> verbose = new HashMap<>();
	private Map<UUID, Float> lastYaw = new HashMap<>();
	private Map<UUID, Double> lastDist = new HashMap<>();
	private Map<UUID, Double> lastLastDist = new HashMap<>();
	
    public KillAuraI(ThotPatrol ThotPatrol) {
        super("KillAuraI", "Kill Aura (Type I)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(8);
		setViolationResetTime(120000);
	}
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
    	verbose.remove(e.getPlayer().getUniqueId());
    	lastYaw.remove(e.getPlayer().getUniqueId());
    	lastDist.remove(e.getPlayer().getUniqueId());
    }

	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if (e.getTo().getX() == e.getFrom().getX()
				&& e.getFrom().getZ() == e.getTo().getZ()
				|| !UtilPlayer.isOnGround(p)
				|| !UtilPlayer.isOnGround4(p)
				|| e.getFrom().getPitch() == e.getTo().getPitch()
				|| e.getFrom().getYaw() == e.getTo().getYaw()
				|| nearIce(p)) {
			return;
		}
		double dist = UtilMath.getHorizontalDistance(e.getTo(), e.getFrom());
		float yaw = p.getLocation().getYaw();
		lastDist.put(p.getUniqueId(), dist);
		lastYaw.put(p.getUniqueId(), yaw);
	}
    
    @EventHandler
    public void onAttack(PacketAttackEvent e) {
		Player p = e.getPlayer();
		if (p == null) {
			return;
		}
		double lastLastAccel = lastLastDist.getOrDefault(p.getUniqueId(), 0D);
		double lastAccel = lastDist.getOrDefault(p.getUniqueId(), 0D);
		if(e.getType() != PacketPlayerType.USE
				|| p.hasPermission("thotpatrol.bypass")
				|| e.getEntity().isDead()
				|| lastAccel == lastLastAccel
				|| !UtilPlayer.isOnGround(p)
				|| !UtilPlayer.isOnGround4(p)
				|| p.getWalkSpeed() > .22) {
			return;
		}
		//TODO calc walkSpeed values
		double tps = getThotPatrol().getLag().getTPS();
		int ping = getThotPatrol().getLag().getPing(p);
		int count = verbose.getOrDefault(p.getUniqueId(), 0);
		if (isActuallySprinting(p) && UtilPlayer.isOnGround(p)
				&& p.getLocation().getYaw() != lastYaw.get(p.getUniqueId())
				&& !p.getAllowFlight() && UtilPlayer.isOnGround4(p)) {
			count++;
		} else {
			if (count > 0) {
				count -= .75;
			}
		}
		if (count >= 5) {
			count = 0;
			getThotPatrol().logCheat(this, p, "Invalid Packet | Ping: " + ping + " | TPS: " + tps);
			getThotPatrol().logToFile(p, this, "Packet", "Speed: " + lastAccel 
					+ " | Ping: " + ping + " | TPS: " + tps);
		}
		lastLastDist.put(p.getUniqueId(), lastAccel);
		verbose.put(p.getUniqueId(), count);
    }
    
    private boolean isActuallySprinting(Player p) {
    	double lastAccel = lastDist.getOrDefault(p.getUniqueId(), 0D);
    	double maxAccel = .28;
    	if (lastAccel > .4) {
    		lastAccel -= .2;
		}
    	if (!UtilPlayer.isOnGround(p) || !UtilPlayer.isOnGround4(p)) {
    		return false;
		}
    	for (PotionEffect e: p.getActivePotionEffects()) {
    		if (e.getType().equals(PotionEffectType.SPEED)) {
    			maxAccel += (e.getAmplifier() + 1) * .0585;
    		}
    	}
		return lastAccel > maxAccel;
	}

    private boolean nearIce(Player p) {
    	for (Block b : UtilBlock.getNearbyBlocks(p.getLocation(), 2) ){
    		if (b.getType().toString().contains("ICE")) {
    			return true;
			}
		}
    	return false;
	}
}