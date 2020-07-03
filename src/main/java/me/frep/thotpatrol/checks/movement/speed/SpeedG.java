package me.frep.thotpatrol.checks.movement.speed;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.utils.UtilBlock;
import me.frep.thotpatrol.utils.UtilPlayer;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpeedG extends Check {

	public static Map<UUID, Integer> count = new HashMap<UUID, Integer>();
	private double a = .004999999999999893;
	private double b = .4050000011920929;
	private double c = .3852000000000002;
	private double d = .41000000000000014;
	private double eqe = .4199999868869684;
	private double gyp = .001599998474120845;
	private double gypp = .0015999984741199569;
	private double qqe = 0.0799999999999983;

	public SpeedG(ThotPatrol ThotPatrol) {
		super("SpeedG", "Speed (Type G)", ThotPatrol);
		setEnabled(true);
		setBannable(true);
		setMaxViolations(4);
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		count.remove(e.getPlayer().getUniqueId());
	}


	//todo make an actual yport check lool
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		if (p.isFlying()
				|| p.getAllowFlight()
				|| p.hasPermission("thotpatrol.bypass")
				|| e.getFrom().getX() == e.getTo().getX()
				&& e.getFrom().getY() == e.getTo().getY()
				&& e.getFrom().getZ() == e.getTo().getZ()) {
			return;
		}
		for (Block b : UtilBlock.getNearbyBlocks(p.getLocation(), 2)) {
			if (b.getType().toString().contains("LAVA")) {
				return;
			}
		}
		double yDiff = Math.abs(e.getFrom().getY() - e.getTo().getY());
		if (yDiff == 0 || yDiff > .42) {
			return;
		}
		int verbose = count.getOrDefault(uuid, 0);
		double ping = getThotPatrol().getLag().getPing(p);
		double tps = getThotPatrol().getLag().getTPS();
		if (yDiff == a
				|| yDiff == b
				|| yDiff == c
				|| yDiff == d
				|| yDiff == eqe
				|| yDiff == gyp
				|| yDiff == gypp
				|| yDiff == qqe) {
			verbose++;
		}
		if (verbose >= 3) {
			verbose = 0;
			getThotPatrol().logCheat(this, p, "Type: Multi | Ping: " + ping + " TPS: " + tps);
			getThotPatrol().logToFile(p, this, "Multi", "Delta: " + yDiff + " | TPS: " + tps + " | Ping: " + ping);
		}
		count.put(uuid, verbose);
	}
}