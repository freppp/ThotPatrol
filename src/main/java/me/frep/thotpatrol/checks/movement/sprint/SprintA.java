package me.frep.thotpatrol.checks.movement.sprint;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.checks.movement.speed.SpeedC;
import me.frep.thotpatrol.utils.UtilCheat;
import me.frep.thotpatrol.utils.UtilPlayer;
import me.frep.thotpatrol.utils.UtilTime;
import me.frep.thotpatrol.utils.UtilVelocity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SprintA extends Check {
	
	public static Map<UUID, Integer> verbose = new HashMap<UUID, Integer>();
	
	public SprintA(ThotPatrol ThotPatrol) {
        super("SprintA", "Sprint (Type A)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(8);
        setViolationResetTime(120000);
    }

    // credits @funkemunky
    
    @EventHandler
    public void onMove(PlayerMoveEvent e) {
    	Player p = e.getPlayer();
    	UUID uuid = p.getUniqueId();
    	int count = verbose.getOrDefault(uuid, 0);
    	if (UtilCheat.isInWeb(p)
    			|| UtilVelocity.didTakeVelocity(p)
    			|| !UtilPlayer.isOnGround(p)
				|| SpeedC.jumpingOnIce.contains(p.getUniqueId())
				|| p.getLocation().getBlock().isLiquid()
    			|| p.hasPermission("thotpatrol.bypass")
    			|| p.getAllowFlight()
				|| !UtilTime.elapsed(getThotPatrol().LastVelocity.getOrDefault(p.getUniqueId(), 0L), 500)
    			|| p.isFlying()
				|| e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ()) {
    		return;
    	}
    	double tps = getThotPatrol().getLag().getTPS();
    	double ping = getThotPatrol().getLag().getPing(p);
    	Vector movement = new Vector(e.getTo().getX() - e.getFrom().getX(), 0, e.getTo().getZ() - e.getFrom().getZ()),
    			direction = new Vector(-Math.sin(e.getPlayer().getEyeLocation().getYaw() * 3.141592653589F / 180.0F) * (float) 1 * 0.5F, 0, Math.cos(e.getPlayer().getEyeLocation().getYaw() * 3.1415927F / 180.0F) * (float) 1 * 0.5F);
    	double delta = movement.distanceSquared(direction);
    	double maxDelta = .35;
    	if (ping > 250) {
    		maxDelta += .04;
    	}
    	//todo calc walkspeed values
    	if (p.getWalkSpeed() > .22) {
    		return;
		}
    	if (delta > maxDelta && p.isSprinting() && UtilPlayer.isOnGround(p)) {
			count++;
		} else {
    		if (count > 0) {
    			count--;
			}
		}
    	if (count > 8) {
			getThotPatrol().logCheat(this, p, "Omnisprint | Delta: " + delta + " | Ping: " + ping + " | TPS: " + tps);
			getThotPatrol().logToFile(p, this, "Omnisprint", "Delta : " + delta + " | TPS: " + tps + " | Ping: " + ping);
			count = 0;
    	}
    	verbose.put(uuid,  count);
    }
}