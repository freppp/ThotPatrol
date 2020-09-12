package me.frep.thotpatrol.checks.combat.autoclicker;

import com.comphenix.protocol.wrappers.EnumWrappers;
import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.events.PacketUseEntityEvent;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class AutoClickerC extends Check {
	
    public static Map<UUID, Long> LastMS;
    public static Map<UUID, List<Long>> Clicks;
    public static Map<UUID, Map.Entry<Integer, Long>> ClickTicks;

    public AutoClickerC(ThotPatrol ThotPatrol) {
        super("AutoClickerC", "Auto Clicker (Type C)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(8);
        LastMS = new HashMap<>();
        Clicks = new HashMap<>();
        ClickTicks = new HashMap<>();
    }

    // this is like the same thing as Kill Aura (Type A) im not even sure why its here

    @EventHandler
    public void onLog(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        LastMS.remove(uuid);
        Clicks.remove(uuid);
        if (ClickTicks.containsKey(uuid)) {
            Clicks.remove(uuid);
        }
    }

    @EventHandler
    public void UseEntity(PacketUseEntityEvent e) {
        // using arm animations/flyings for autoclickers is a lot more accurate
        if (e.getAction() != EnumWrappers.EntityUseAction.ATTACK) return;
        Player damager = e.getAttacker();
        UUID uuid = damager.getUniqueId();
        int Count = 0;
        int ping = getThotPatrol().getLag().getPing(damager);
        double tps = getThotPatrol().getLag().getTPS();
        long Time = System.currentTimeMillis();
        if (ClickTicks.containsKey(uuid)) {
            Count = ClickTicks.get(uuid).getKey();
            Time = ClickTicks.get(uuid).getValue();
        }
		if (damager.hasPermission("thotpatrol.bypass")) {
			return;
		}
        if (LastMS.containsKey(uuid)) {
            long MS = UtilTime.nowlong() - LastMS.get(uuid);
            if (MS > 500L || MS < 5L) {
                LastMS.put(uuid, UtilTime.nowlong());
                return;
            }
            if (Clicks.containsKey(uuid)) {
                List<Long> Clicks = AutoClickerC.Clicks.get(uuid);
                if (Clicks.size() == 3) {
                	AutoClickerC.Clicks.remove(uuid);
                    Collections.sort(Clicks);
                    long Range = Clicks.get(Clicks.size() - 1) - Clicks.get(0);
                    if (Range >= 0 && Range <= 2) {
                        ++Count;
                        Time = System.currentTimeMillis();
                        dumplog(damager, "Count Increase: " + Count + "| Range: " + Range + "| Ping: " + ping + "| TPS: " + tps);
                    }
                } else {
                    Clicks.add(MS);
                    AutoClickerC.Clicks.put(uuid, Clicks);
                }
            } else {
                List<Long> Clicks = new ArrayList<>();
                Clicks.add(MS);
                AutoClickerC.Clicks.put(uuid, Clicks);
            }
        }
        if (ClickTicks.containsKey(uuid) && UtilTime.elapsed(Time, 5000L)) {
            Count = 0;
            Time = UtilTime.nowlong();
        }
        if ((Count > 14 && ping < 100) || (Count > 22 && ping < 200)) {
            dumplog(damager, "Logged. Count: " + Count + " | Ping: " + ping + " | TPS: " + tps);
            Count = 0;
            getThotPatrol().logCheat(this, damager, "Continuous/Repeating Patterns" + " | Ping: " + ping + " | TPS: " + tps);
        	getThotPatrol().logToFile(damager, this, "Continuous Patterns", "Count: " 
            + Count + " | TPS: " + tps + " | Ping: " + ping);
            ClickTicks.remove(uuid);
        } else if (ping > 300) {
            dumplog(damager, "Latency is too high, did not flag. Ping: " + ping);
        }
        LastMS.put(uuid, UtilTime.nowlong());
        ClickTicks.put(uuid, new AbstractMap.SimpleEntry<>(Count, Time));
    }
}