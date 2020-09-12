package me.frep.thotpatrol.checks.combat.killaura;

import com.comphenix.protocol.wrappers.EnumWrappers;
import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.events.PacketUseEntityEvent;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class KillAuraA extends Check {
	
    public static Map<UUID, Long> LastMS;
    public static Map<UUID, List<Long>> Clicks;
    public static Map<UUID, Map.Entry<Integer, Long>> ClickTicks;

    public KillAuraA(ThotPatrol ThotPatrol) {
        super("KillAuraA", "Kill Aura (Type A)", ThotPatrol);
        LastMS = new HashMap<>();
        Clicks = new HashMap<>();
        ClickTicks = new HashMap<>();

        setEnabled(true);
        setBannable(true);
        setViolationResetTime(300000);
        setMaxViolations(10);
    }

    @EventHandler
    public void onLog(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();

        LastMS.remove(uuid);
        Clicks.remove(uuid);
        ClickTicks.remove(uuid);
    }

    //click pattern check

    @EventHandler
    public void UseEntity(PacketUseEntityEvent e) {
        if (e.getAction() != EnumWrappers.EntityUseAction.ATTACK) {
            return;
        }
        Player damager = e.getAttacker();
        if (damager.hasPermission("thotpatrol.bypass")) {
        	return;
        }
        int ping = getThotPatrol().getLag().getPing(damager);
        double tps = getThotPatrol().getLag().getTPS();
        int Count = 0;
        long Time = System.currentTimeMillis();
        if (ClickTicks.containsKey(damager.getUniqueId())) {
            Count = ClickTicks.get(damager.getUniqueId()).getKey();
            Time = ClickTicks.get(damager.getUniqueId()).getValue();
        }
        if (LastMS.containsKey(damager.getUniqueId())) {
            long MS = UtilTime.nowlong() - LastMS.get(damager.getUniqueId());
            if (MS > 500L || MS < 5L) {
                LastMS.put(damager.getUniqueId(), UtilTime.nowlong());
                return;
            }
            if (Clicks.containsKey(damager.getUniqueId())) {
                List<Long> Clicks = KillAuraA.Clicks.get(damager.getUniqueId());
                if (Clicks.size() == 10) {
                    KillAuraA.Clicks.remove(damager.getUniqueId());
                    Collections.sort(Clicks);
                    final long Range = Clicks.get(Clicks.size() - 1) - Clicks.get(0);
                    if (Range < 30L) {
                        ++Count;
                        getThotPatrol().verbose(this, damager, ping, tps, Long.toString(Range));
                        Time = System.currentTimeMillis();
                        dumplog(damager, "New Range: " + Range);
                        dumplog(damager, "New Count: " + Count);
                    }
                } else {
                    Clicks.add(MS);
                    KillAuraA.Clicks.put(damager.getUniqueId(), Clicks);
                }
            } else {
                final List<Long> Clicks = new ArrayList<>();
                Clicks.add(MS);
                KillAuraA.Clicks.put(damager.getUniqueId(), Clicks);
            }
        }
        if (ClickTicks.containsKey(damager.getUniqueId()) && UtilTime.elapsed(Time, 5000L)) {
            Count = 0;
            Time = UtilTime.nowlong();
        }
        if ((Count > 2 && getThotPatrol().getLag().getPing(damager) < 100)
                || (Count > 4 && getThotPatrol().getLag().getPing(damager) <= 400)) {
            dumplog(damager, "Logged. Count: " + Count);
            getThotPatrol().logCheat(this, damager, "Patterns" + " | Ping: " + ping + " | TPS: " + tps);
        	getThotPatrol().logToFile(damager, this, "Click Pattern", "Count: " 
                    + Count + " | TPS: " + tps + " | Ping: " + ping);
            ClickTicks.remove(damager.getUniqueId());
            Count = 0;
        } else if (getThotPatrol().getLag().getPing(damager) > 400) {
            dumplog(damager, "Would set off Killaura (Click Pattern) but latency is too high!");
        }
        LastMS.put(damager.getUniqueId(), UtilTime.nowlong());
        ClickTicks.put(damager.getUniqueId(), new AbstractMap.SimpleEntry<>(Count, Time));
    }
}