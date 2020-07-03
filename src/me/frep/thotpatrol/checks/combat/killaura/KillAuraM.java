package me.frep.thotpatrol.checks.combat.killaura;

import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.PacketPlayerType;
import me.frep.thotpatrol.packets.events.PacketAttackEvent;
import me.frep.thotpatrol.packets.events.PacketPlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;

public class KillAuraM extends Check {

    // credits @funkemunky <3

    private Map<UUID, Long> lastPacket = new ConcurrentSkipListMap<>();
    private Map<UUID, Integer> verbose = new HashMap<>();

    public KillAuraM(me.frep.thotpatrol.ThotPatrol ThotPatrol) {
        super("KillAuraM", "Kill Aura (Type M)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(8);
        setViolationResetTime(120000);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        lastPacket.remove(e.getPlayer().getUniqueId());
        verbose.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPacket(PacketPlayerEvent e) {
        if (e.getType().equals(PacketPlayerType.FLYING) || e.getType().equals(PacketPlayerType.POSLOOK) || e.getType().equals(PacketPlayerType.POSITION)) {
            lastPacket.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onAttack(PacketAttackEvent e) {
        Player p = e.getPlayer();
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        long delta = System.currentTimeMillis() - lastPacket.getOrDefault(p.getUniqueId(), 0L).longValue();
        if (delta < 8 && ping < 400) {
            count++;
            getThotPatrol().verbose(this, p, ping, tps, delta + " < 10");
            dumplog(p, "Delta: " + delta + " | Ping: " + ping + " | TPS: " + tps);
        } else {
            if (count > 0) {
                count--;
            }
        }
        if (count >= 6) {
            count = 0;
            getThotPatrol().logCheat(this, p, "Packet | Ping " + ping + " | TPS: " + tps);
            getThotPatrol().logToFile(p, this, "Packet", "Delta: " + delta + " | Ping: " + ping + " | TPS: " + tps);
        }
        verbose.put(p.getUniqueId(), count);
    }
}