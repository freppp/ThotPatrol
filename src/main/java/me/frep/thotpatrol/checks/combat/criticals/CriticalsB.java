package me.frep.thotpatrol.checks.combat.criticals;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.data.DataPlayer;
import me.frep.thotpatrol.utils.UtilBlock;
import me.frep.thotpatrol.utils.UtilCheat;
import me.frep.thotpatrol.utils.UtilPlayer;

public class CriticalsB extends Check {
	
    public CriticalsB(ThotPatrol ThotPatrol) {
        super("CriticalsB", "Criticals (Type B)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(4);
    }
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	private void onAttack(EntityDamageByEntityEvent e) {
		try {
			if(!(e.getDamager() instanceof Player)) {
				return;
			}
			final Player p = (Player) e.getDamager();
			if (p == null) {
				return;
			}
			if(getThotPatrol().getLag().getTPS() < getThotPatrol().getTPSCancel()
					|| getThotPatrol().getLag().getPing(p) > getThotPatrol().getPingCancel()) {
				return;
			}
			final DataPlayer data = ThotPatrol.Instance.getDataManager().getData(p);
			if(data.getAboveBlockTicks() > 0
					|| UtilCheat.isInWeb(p)
					|| p.getAllowFlight()
					|| p.isFlying()
					|| p.hasPermission("thotpatrol.bypass")
					|| UtilPlayer.isNearPiston(p)
					|| data.getWaterTicks() > 0
					|| UtilCheat.slabsNear(p.getLocation())) {
				return;
			}
			if (UtilBlock.isNearLiquid(p) && UtilBlock.isNearFence(p)) {
				return;
			}
			int verbose = data.getCriticalsVerbose();
	        int ping = getThotPatrol().getLag().getPing(p);
	        double tps = getThotPatrol().getLag().getTPS();
			if(p.getFallDistance() > 0 && data.getFallDistance() == 0) {
				if(++verbose > 6) {
					getThotPatrol().logCheat(this, p, "Packet Criticals" + " | Ping:" + ping + " | TPS: " + tps);
		        	getThotPatrol().logToFile(p, this, "Packet", "Fall Distance: " 
		                    + p.getFallDistance() + " | TPS: " + tps + " | Ping: " + ping);
					verbose = 0;
				}
			} else {
				verbose--;
			}
			data.setCriticalsVerbose(verbose);
		} catch (final Exception ex) {
		}
	}
}
