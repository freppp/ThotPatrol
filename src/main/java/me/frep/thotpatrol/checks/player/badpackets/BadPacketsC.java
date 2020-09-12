package me.frep.thotpatrol.checks.player.badpackets;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.events.PacketHeldItemChangeEvent;
import me.frep.thotpatrol.packets.events.PacketSwingArmEvent;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.*;

public class BadPacketsC extends Check {
	
    public static Map<UUID, Map.Entry<Integer, Long>> crashTicks;
    public static Map<UUID, Map.Entry<Integer, Long>> crashTicks2;
    public List<UUID> crashers;

    public BadPacketsC(ThotPatrol ThotPatrol) {
        super("BadPacketsC", "Bad Packets (Type C)", ThotPatrol);
        setMaxViolations(1);
        setEnabled(true);
        setBannable(true);
        crashTicks = new HashMap<>();
        crashTicks2 = new HashMap<>();
        crashers = new ArrayList<>();
    }

    //catches some crashers
    
    @EventHandler
    public void Swing(final PacketSwingArmEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        if (crashers.contains(uuid)) {
            e.getPacketEvent().setCancelled(true);
            return;
        }
		if (p.hasPermission("thotpatrol.bypass")) {
			return;
		}
        int Count = 0;
        long Time = System.currentTimeMillis();
        if (crashTicks.containsKey(uuid)) {
            Count = crashTicks.get(uuid).getKey();
            Time = crashTicks.get(uuid).getValue();
        }
        ++Count;
        if (crashTicks.containsKey(uuid) && UtilTime.elapsed(Time, 100L)) {
            Count = 0;
            Time = UtilTime.nowlong();
        }
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
        if (Count > 2000) {
            this.getThotPatrol().logCheat(this, p, "[1] Crash Packets | Ping:" + ping + " | TPS: " + tps);
        	getThotPatrol().logToFile(p, this, "Crash", "Packets: " + Count + " > 2000" +   
        			" | TPS: " + tps + " | Ping: " + ping);
            this.crashers.add(p.getUniqueId());
        }
        crashTicks.put(uuid, new AbstractMap.SimpleEntry<>(Count, Time));
    }

    @EventHandler
    public void Switch(final PacketHeldItemChangeEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        if (crashers.contains(uuid)) {
            e.getPacketEvent().setCancelled(true);
            return;
        }
        int Count = 0;
        long Time = System.currentTimeMillis();
        if (crashTicks2.containsKey(uuid)) {
            Count = crashTicks2.get(uuid).getKey();
            Time = crashTicks2.get(uuid).getValue();
        }
        ++Count;
        if (crashTicks2.containsKey(uuid) && UtilTime.elapsed(Time, 100L)) {
            Count = 0;
            Time = UtilTime.nowlong();
        }
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
        if (Count > 2000) {
            getThotPatrol().logCheat(this, p, "[2] Crash Packets | Ping:" + ping + " | TPS: " + tps);
        	getThotPatrol().logToFile(p, this, "Crash", "Packets: " + Count + " > 2000" +   
        			" | TPS: " + tps + " | Ping: " + ping);
            crashers.add(uuid);
        }
        crashTicks2.put(uuid, new AbstractMap.SimpleEntry<>(Count, Time));
    }
}