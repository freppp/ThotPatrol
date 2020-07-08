package me.frep.thotpatrol.checks.movement.noslowdown;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.utils.UtilMath;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NoSlowdownA extends Check {

    public static Map<UUID, Map.Entry<Integer, Long>> speedTicks;

    public NoSlowdownA(ThotPatrol ThotPatrol) {
        super("NoSlowdownA", "NoSlowdown (Type A)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(6);
        speedTicks = new HashMap<>();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent e) {
        speedTicks.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.hasPermission("thotpatrol.bypass")) {
        	return;
        }
        double OffsetXZ = UtilMath.offset(UtilMath.getHorizontalVector(e.getFrom().toVector()), UtilMath.getHorizontalVector(e.getTo().toVector()));
        if (e.getTo().getX() == e.getFrom().getX()
                && e.getFrom().getY() == e.getTo().getY()
                && e.getTo().getZ() == e.getFrom().getZ()) return;
        if (player.getAllowFlight()
                || !player.getLocation().getBlock().getType().equals(Material.WEB)
                || OffsetXZ < 0.2) return;
		double tps = getThotPatrol().getLag().getTPS();
		int ping = getThotPatrol().getLag().getPing(player);
        getThotPatrol().logCheat(this, player, "[0] Offset: " + OffsetXZ);
    	getThotPatrol().logToFile(player, this, "Ticks [B]", "Offset: " + 
    			OffsetXZ + " | TPS: " + tps + " | Ping: " + ping);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
    	Player player = event.getPlayer();
    	UUID uuid = player.getUniqueId();
    	if (event.getAction().equals(Action.LEFT_CLICK_AIR)
    			|| event.getAction().equals(Action.LEFT_CLICK_BLOCK)) {
    		return;
    	}
    	if (event.getItem() == null
    			|| event.getItem().getType().equals(Material.MUSHROOM_SOUP)
    			|| event.getItem().getType().equals(Material.EXP_BOTTLE)
    			|| event.getItem().getType().equals(Material.GLASS_BOTTLE)
    			|| event.getItem().getType().equals(Material.POTION)
    			|| !event.getItem().getType().toString().contains("SWORD")
    			|| player.getAllowFlight()
    			|| player.hasPermission("thotpatrol.bypass")) {
    		return;
    	}
    	long Time = System.currentTimeMillis();
    	int level = 0;
    	if (speedTicks.containsKey(uuid)) {
    		level = speedTicks.get(uuid).getKey();
    		Time = speedTicks.get(uuid).getValue();
    	}
		double tps = getThotPatrol().getLag().getTPS();
		int ping = getThotPatrol().getLag().getPing(player);
    	double diff = System.currentTimeMillis() - Time;
    	level = diff >= 2.0 ? (diff <= 51.0 ? (level += 2) : (diff <= 100.0 ? (level += 0) : (diff <= 500.0 ? (level -= 6) : (level -= 12)))) : ++level;
    	int max = 50;
    	if (level > max * 0.9D && diff <= 100.0D) {
    		getThotPatrol().logCheat(this, player, "[1] Level: " + level + " | Ping: " + ping + " | TPS: " + tps);
        	getThotPatrol().logToFile(player, this, "Ticks [B]", "Level: " + 
			level + " | TPS: " + tps + " | Ping: " + ping);
    		if (level > max) {
    			level = max / 4;
    		}
    	} else if (level < 0) {
    		level = 0;
    	}
    	speedTicks.put(uuid, new AbstractMap.SimpleEntry<>(level, System.currentTimeMillis()));
    }
}