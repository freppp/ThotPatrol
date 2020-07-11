package me.frep.thotpatrol.utils;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.data.DataPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;

public class UtilVelocity implements Listener {

	public static long VelTimeReset_1 = 999L;
	public static long VelTimeReset_1_FORCE_RESET = 1000L;

	@SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onMove(PlayerMoveEvent e) {
		final Player p = e.getPlayer();
		final DataPlayer data = ThotPatrol.Instance.getDataManager().getData(p);
		if (data != null) {
			if (data.isLastVelUpdateBoolean()) {
				if (UtilTime.elapsed(data.getLastVelUpdate(),VelTimeReset_1_FORCE_RESET)) {
					data.setLastVelUpdateBoolean(false);
				}
				if (UtilTime.elapsed(data.getLastVelUpdate(),VelTimeReset_1)) {
					if (!p.isOnGround()) {
						data.setLastVelUpdate(UtilTime.nowlong());
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
					data.setLastVelUpdate(UtilTime.nowlong());
				}
			}
		}
	}
	public static boolean didTakeVelocity(Player p) {
		final DataPlayer data = ThotPatrol.Instance.getDataManager().getData(p);
		if (data != null) {
			return data.isLastVelUpdateBoolean();
		} else {
			return false;
		}
	}
}