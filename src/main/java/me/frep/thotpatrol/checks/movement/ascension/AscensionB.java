package me.frep.thotpatrol.checks.movement.ascension;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;

public class AscensionB extends Check {

    private Map<Player, Integer> verbose = new HashMap<Player, Integer>();
    private Map<Player, Double> lastYMovement = new HashMap<Player, Double>();

    public AscensionB(ThotPatrol ThotPatrol) {
        super("AscensionB", "Ascension (Type B)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(5);
    }

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		int verbose = this.verbose.getOrDefault(p, 0);
		double yDelta = e.getTo().getY() - e.getFrom().getY();
		if (p.getAllowFlight()
				|| !lastYMovement.containsKey(p)
				|| p.hasPermission("thotpatrol.bypass")
				|| Math.abs(yDelta - lastYMovement.get(p)) > 0.002) {
			return;
		}
		verbose++;
		double tps = getThotPatrol().getLag().getTPS();
		double ping = getThotPatrol().getLag().getPing(p);
		if (verbose > 3) {
			verbose = 0;
			getThotPatrol().logCheat(this, p, Math.abs(yDelta - lastYMovement.get(p)) + "<-" + 0.002 + " | Ping: " + ping + " | TPS: " + tps);
        	getThotPatrol().logToFile(p, this, "Invalid Y Movement", "Movement: + " + (Math.abs(yDelta - lastYMovement.get(p))
        			+ " | TPS: " + tps + " | Ping: " + ping));
		}
		lastYMovement.put(p, yDelta);
		this.verbose.put(p, verbose);
	}
}