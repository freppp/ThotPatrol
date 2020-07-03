package me.frep.thotpatrol.checks.movement.sneak;
import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.events.PacketEntityActionEvent;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SneakA extends Check {

    public static Map<UUID, Map.Entry<Integer, Long>> sneakTicks;

    public SneakA(ThotPatrol ThotPatrol) {
        super("SneakA", "Sneak (Type A)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(4);
        sneakTicks = new HashMap<>();
    }

    @EventHandler
    public void onLog(PlayerQuitEvent e) {
        sneakTicks.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    private void EntityAction(PacketEntityActionEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        if (getThotPatrol().getLag().getTPS() < getThotPatrol().getTPSCancel()
                || getThotPatrol().getLag().getPing(p) > getThotPatrol().getPingCancel()
                || !p.isSneaking()) {
            return;
        }
        int Count = 0;
        long Time = -1L;
        if (sneakTicks.containsKey(uuid)) {
            Count = sneakTicks.get(uuid).getKey().intValue();
            Time = sneakTicks.get(uuid).getValue().longValue();
        }
        Count++;
        if (sneakTicks.containsKey(uuid)) {
            if (UtilTime.elapsed(Time, 100L)) {
                Count = 0;
                Time = System.currentTimeMillis();
            } else {
                Time = System.currentTimeMillis();
            }
        }
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
        if (Count > 50) {
            Count = 0;
            getThotPatrol().logCheat(this, p, null);
            getThotPatrol().logToFile(p, this, "Multi", "TPS: " + tps + " | Ping: " + ping);
        }
        sneakTicks.put(uuid, new AbstractMap.SimpleEntry<>(Count, Time));
    }
}