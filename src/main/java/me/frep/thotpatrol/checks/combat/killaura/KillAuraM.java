package me.frep.thotpatrol.checks.combat.killaura;

import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.PacketPlayerType;
import me.frep.thotpatrol.packets.events.PacketAttackEvent;
import me.frep.thotpatrol.packets.events.PacketPlayerEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListMap;

public class KillAuraM extends Check {

    private final Map<UUID, Long> lastPacket = new HashMap<>();
    private final Map<UUID, Integer> verbose = new HashMap<>();

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
        if (e.getType().equals(PacketPlayerType.FLYING) || e.getType().equals(PacketPlayerType.POSLOOK) || e.getType().equals(PacketPlayerType.POSITION)
            || e.getType().equals(PacketPlayerType.LOOK)) {
            lastPacket.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onAttack(PacketAttackEvent e) {
        Player p = e.getPlayer();
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        long delta = System.currentTimeMillis() - lastPacket.getOrDefault(p.getUniqueId(), 0L);
        if (p.hasPermission("thotpatrol.bypass")
            || p.getAllowFlight()
            || p.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }
        if (delta < 8 && ping < 250 && tps > 19.5) {
            count++;
            getThotPatrol().verbose(this, p, ping, tps, delta + " < 10");
            dumplog(p, "Delta: " + delta + " | Ping: " + ping + " | TPS: " + tps);
        } else {
            if (count > 0) count -= 3;
        }
        if (count > 8) {
            count = 0;
            getThotPatrol().logCheat(this, p, "Packet | Ping " + ping + " | TPS: " + tps);
            getThotPatrol().logToFile(p, this, "Packet", "Delta: " + delta + " | TPS: " + tps + " | Ping: " + ping);
        }
        verbose.put(p.getUniqueId(), count);
    }
}