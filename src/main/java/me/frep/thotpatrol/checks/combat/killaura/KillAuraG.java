package me.frep.thotpatrol.checks.combat.killaura;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.PacketPlayerType;
import me.frep.thotpatrol.packets.events.PacketAttackEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class KillAuraG extends Check {
	
    public KillAuraG(ThotPatrol ThotPatrol) {
        super("KillAuraG", "Kill Aura (Type G)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(6);
    }
	
	private List<UUID> inInventory = new ArrayList<UUID>();
	private Map<UUID, Integer> count = new HashMap<UUID, Integer>();
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		inInventory.remove(e.getPlayer().getUniqueId());
		count.remove(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onAttack(PacketAttackEvent e) {
		Player p = e.getPlayer();
		if (p == null) {
			return;
		}
		int verbose = count.getOrDefault(p.getUniqueId(), 0);
		if(e.getType() != PacketPlayerType.USE) {
			return;
		}
		double tps = getThotPatrol().getLag().getTPS();
		int ping = getThotPatrol().getLag().getPing(p);
		if (inInventory.contains(p.getUniqueId())) {
			verbose++;
			getThotPatrol().verbose(this, p, ping, tps, Integer.toString(verbose));
		}
		if (verbose > 3) {
			verbose = 0;
			getThotPatrol().logCheat(this, p, "Invalid Attack Packets "
					+ "| Ping: " + ping + " | TPS: " + tps);
			getThotPatrol().logToFile(p, this, "Inventory", "Ping: " 
					+ ping + " | TPS: " + tps);
		}
		count.put(p.getUniqueId(), verbose);
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e) {
		inInventory.add(e.getPlayer().getUniqueId());
	}
	
	@EventHandler
	public void onInventoryClose(InventoryCloseEvent e) {
		if (inInventory.contains(e.getPlayer().getUniqueId()) ) {
			inInventory.remove(e.getPlayer().getUniqueId());
		}
	}
}