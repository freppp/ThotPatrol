package me.frep.thotpatrol.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.data.DataPlayer;

public class UtilVelocityNew implements Listener {
	
	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onMove(PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		final DataPlayer data = ThotPatrol.Instance.getDataManager().getData(p);
		if (data != null) {
			if (data.isLastVelUpdateBoolean()) {
				if (UtilTimer.elapsed(data.getLastVelUpdate(),Values.VelTimeReset_1_FORCE_RESET)) {
					data.setLastVelUpdateBoolean(false);
				}
				if (UtilTimer.elapsed(data.getLastVelUpdate(),Values.VelTimeReset_1)) {
					if (!p.isOnGround()) {
						data.setLastVelUpdate(UtilTimer.nowlong());
					} else {
						data.setLastVelUpdateBoolean(false);
					}
				}
			}
		}
	}
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onVelChange(PlayerVelocityEvent e) {
		final Player p = e.getPlayer();
		final DataPlayer data = ThotPatrol.Instance.getDataManager().getData(p);
		if (data != null) {
			if (p.getNoDamageTicks() <= 0) {
				if (!data.isLastVelUpdateBoolean()) {
					data.setLastVelUpdateBoolean(true);
					data.setLastVelUpdate(UtilTimer.nowlong());
				}
			}
		}
	}
	public static boolean didTakeVel(Player p) {
		final DataPlayer data = ThotPatrol.Instance.getDataManager().getData(p);
		if (data != null) {
			return data.isLastVelUpdateBoolean();
		} else {
			return false;
		}
	}
}
