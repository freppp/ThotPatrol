package me.frep.thotpatrol.checks.combat.killaura;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;

public class KillAuraF extends Check {
	
	private float lastYaw;
	private float lastBad;
	private float lastYaw2;
	private float lastPitch;
	private int streak;
	private int min;
	
    public KillAuraF(ThotPatrol ThotPatrol) {
        super("KillAuraF", "Kill Aura (Type F)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(10);
    }
    
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onHit(EntityDamageByEntityEvent e) {
		if (!(e.getDamager() instanceof Player)) {
			return;
		}
		final Player player = (Player)e.getDamager();
		if (player.hasPermission("thotpatrol.bypass")) {
			return;
		}
		final float yaw = player.getLocation().getYaw();
		final float pitch = player.getLocation().getPitch();
		this.onAim(player, yaw);
		this.onAim2(player, yaw, pitch);
		this.onAim3(player, yaw);
	}
	public boolean onAim(Player p, float yaw) {
		if (getThotPatrol().getLag().getTPS() < getThotPatrol().getTPSCancel()
				|| getThotPatrol().getLag().getPing(p) > getThotPatrol().getPingCancel()) {
			return true;
		}
		final float pitch = Math.abs(yaw - this.lastYaw) % 180.0f;
		this.lastYaw = yaw;
		this.lastBad = Math.round(pitch * 10.0f) * 0.1f;
		if (yaw < 0.1) {
			return true;
		}
		if (p.hasPermission("thotpatrol.bypass")) {
			return false;
		}
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
		if (pitch > 1.0f && Math.round(pitch * 10.0f) * 0.1f == pitch && Math.round(pitch) != pitch) {
			if (pitch == this.lastBad) {
				getThotPatrol().logCheat(this, p, "[1] Yaw: " + yaw + " | Pitch: " + pitch + " | Ping:" + ping + " | TPS: " + tps);
	        	getThotPatrol().logToFile(p, this, "[1] Pitch/Yaw Patterns", "Pitch: " + pitch + " | Yaw:" +   
	        			yaw + " | TPS: " + tps + " | Ping: " + ping);
				return true;
			}
		} else {
			return true;
		}
		return false;
	}
	public int onAim2(Player p, float yaw, float pitch) {
		final float lastYaw = yaw - this.lastYaw2;
		final float lastPitch = pitch - this.lastPitch;
		if (Math.abs(lastPitch) >= 2.0f && lastYaw == 0.0f) {
			++this.streak;
		} else {
			return 0;
		}
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
		this.lastYaw2 = yaw;
		this.lastPitch = pitch;
		if (this.streak >= this.min) {
			getThotPatrol().logCheat(this, p, "[2] Yaw: " + yaw + " | Pitch: " + pitch);
        	getThotPatrol().logToFile(p, this, "[2] Pitch/Yaw Patterns", "Pitch: " + pitch + " | Yaw:" +   
        			yaw + " | TPS: " + tps + " | Ping: " + ping);
			return this.streak;
		}
		return 0;
	}
	
	public float onAim3(Player p, float yaw) {
		final float pitch = Math.abs(yaw - this.lastYaw) % 180.0f;
		this.lastYaw = yaw;
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
		if (pitch > 0.1f && Math.round(pitch) == pitch) {
			if (pitch == this.lastBad) {
				getThotPatrol().logCheat(this, p, "[3] Yaw: " + yaw + " | Pitch: " + pitch);
	        	getThotPatrol().logToFile(p, this, "[3] Pitch/Yaw Patterns", "Pitch: " + pitch + " | Yaw:" +   
	        			yaw + " | TPS: " + tps + " | Ping: " + ping);
				return pitch;
			}
			this.lastBad = Math.round(pitch);
		} else {
			this.lastBad = 0.0f;
		}
		return 0.0f;
	}
}