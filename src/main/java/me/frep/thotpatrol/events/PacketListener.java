package me.frep.thotpatrol.events;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.data.DataPlayer;
import me.frep.thotpatrol.packets.events.PacketPlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PacketListener implements Listener {
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onPacketPlayerEvent(PacketPlayerEvent e) {
		final Player p = e.getPlayer();
		final DataPlayer data = ThotPatrol.Instance.getDataManager().getData(p);
		if (data != null) {
			if (data.getLastPlayerPacketDiff() > 200) {
				data.setLastDelayedPacket(System.currentTimeMillis());
			}
			data.setLastPlayerPacket(System.currentTimeMillis());
		}
	}
}