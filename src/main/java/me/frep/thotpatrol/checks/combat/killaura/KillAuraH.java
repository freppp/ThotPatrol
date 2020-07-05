package me.frep.thotpatrol.checks.combat.killaura;

import com.comphenix.protocol.wrappers.EnumWrappers;
import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.events.PacketUseEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillAuraH extends Check {

	private Map<UUID, Long> lastAttackPacket = new HashMap<>();
	private Map<UUID, Integer> verbose = new HashMap<>();

	public KillAuraH(ThotPatrol ThotPatrol) {
		super("KillAuraH", "Kill Aura (Type H)", ThotPatrol);
		setEnabled(true);
		setBannable(true);
		setMaxViolations(9);
		setViolationResetTime(120000);

	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		verbose.remove(e.getPlayer().getUniqueId());
		lastAttackPacket.remove(e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onUse(PacketUseEntityEvent e) {
		if (e.getAction().equals(EnumWrappers.EntityUseAction.ATTACK)) {
			Player p = e.getAttacker();
			long elapsed = System.currentTimeMillis() - lastAttackPacket.getOrDefault(p.getUniqueId(), 0L);
			int count = verbose.getOrDefault(p.getUniqueId(), 0);
			double tps = getThotPatrol().getLag().getTPS();
			int ping = getThotPatrol().getLag().getPing(p);
			if (elapsed < 2 && ping < 350) {
				count++;
				getThotPatrol().verbose(this, p, ping, tps, elapsed + " < 3");
			}
			if (count > 4) {
				count = 0;
				getThotPatrol().logCheat(this, p, "Packet | Ping: " + ping + " | TPS: " + tps);
				getThotPatrol().logToFile(p, this, "Packet", "Time: " + elapsed
						+ " | Ping: " + ping + " | TPS: " + tps);
			}
			verbose.put(p.getUniqueId(), count);
			lastAttackPacket.put(p.getUniqueId(), System.currentTimeMillis());
		}
	}
}