package me.frep.thotpatrol.checks.combat.killaura;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.data.DataPlayer;
import me.frep.thotpatrol.packets.PacketPlayerType;
import me.frep.thotpatrol.packets.events.PacketAttackEvent;
import me.frep.thotpatrol.utils.UtilMath;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class KillAuraE extends Check {
	
    public KillAuraE(ThotPatrol ThotPatrol) {
        super("KillAuraE", "Kill Aura (Type E)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(13);
        setViolationResetTime(120000);
    }
	
	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	private void onAttack(PacketAttackEvent e) {
		Player p = e.getPlayer();
		if (p == null) {
			return;
		}
		final DataPlayer data = ThotPatrol.Instance.getDataManager().getData(p);
		if(e.getType() != PacketPlayerType.USE
				|| (data == null)) {
			return;
		}
		if (p.hasPermission("thotpatrol.bypass")) {
			return;
		}
		int verboseA = data.getKillauraAVerbose();
		long time = data.getLastAimTime();
		if(UtilMath.elapsed(time, 1100L)) {
			time = System.currentTimeMillis();
			verboseA = 0;
		}
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
		if ((Math.abs(data.getLastKillauraPitch() - e.getPlayer().getEyeLocation().getPitch()) > 1
				|| angleDistance((float) data.getLastKillauraYaw(), p.getEyeLocation().getYaw()) > 1
				|| Double.compare(p.getEyeLocation().getYaw(), data.getLastKillauraYaw()) != 0)
				&& !UtilMath.elapsed(data.getLastPacket(), 100L)) {
			if(angleDistance((float) data.getLastKillauraYaw(), p.getEyeLocation().getYaw()) != data.getLastKillauraYawDif()) {
				if(++verboseA > 15) {
					if (getThotPatrol().getLag().getTPS() < getThotPatrol().getTPSCancel()
							|| getThotPatrol().getLag().getPing(p) > getThotPatrol().getPingCancel()) {
						return;
					}
					getThotPatrol().logCheat(this, p, "Packet | Ping: " + ping + " | TPS: " + tps);
		        	getThotPatrol().logToFile(p, this, "Packet", "TPS: " + tps + " | Ping: " + ping);
				}
			}
			data.setLastKillauraYawDif(angleDistance((float) data.getLastKillauraYaw(), p.getEyeLocation().getYaw()));
		} else {
			verboseA = 0;
		}
		data.setKillauraAVerbose(verboseA);
		data.setLastAimTime(time);
	}

	private static float angleDistance(float alpha, float beta) {
		final float phi = Math.abs(beta - alpha) % 360;
		return phi > 180 ? 360 - phi : phi;
	}
}