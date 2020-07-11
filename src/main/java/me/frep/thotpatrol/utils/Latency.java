package me.frep.thotpatrol.utils;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.packets.PacketPlayerType;
import me.frep.thotpatrol.packets.events.PacketPlayerEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.*;

public class Latency implements Listener {

    public static Map<UUID, Map.Entry<Integer, Long>> packetTicks;
    public static Map<UUID, Long> lastPacket;
    private static Map<UUID, Integer> packets;
    public List<UUID> blacklist;
    private me.frep.thotpatrol.ThotPatrol ThotPatrol;

    public Latency(ThotPatrol ThotPatrol) {
        this.ThotPatrol = ThotPatrol;

        packetTicks = new HashMap<>();
        lastPacket = new HashMap<>();
        blacklist = new ArrayList<>();
        packets = new HashMap<>();
    }

    public static Integer getLag(Player p) {
        if (packets.containsKey(p.getUniqueId())) {
            return packets.get(p.getUniqueId());
        }
        return 0;
    }

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent event) {
        this.blacklist.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent e) {
        packetTicks.remove(e.getPlayer().getUniqueId());
        lastPacket.remove(e.getPlayer().getUniqueId());
        blacklist.remove(e.getPlayer().getUniqueId());
        packets.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void PlayerChangedWorld(PlayerChangedWorldEvent event) {
        this.blacklist.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void PlayerRespawn(PlayerRespawnEvent event) {
        blacklist.add(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void PacketPlayer(PacketPlayerEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        if (!ThotPatrol.isEnabled()) return;
        if (p.getGameMode().equals(GameMode.CREATIVE)) return;
        if (ThotPatrol.lag.getTPS() > 21.0D || ThotPatrol.lag.getTPS() < ThotPatrol.getTPSCancel()) return;
        if (e.getType() != PacketPlayerType.FLYING) return;
        int Count = 0;
        long Time = System.currentTimeMillis();
        if (Latency.packetTicks.containsKey(uuid)) {
            Count = Latency.packetTicks.get(uuid).getKey();
            Time = Latency.packetTicks.get(uuid).getValue();
        }
        if (Latency.lastPacket.containsKey(uuid)) {
            long MS = System.currentTimeMillis() - Latency.lastPacket.get(uuid);
            if (MS >= 100L) {
                this.blacklist.add(uuid);
            } else if ((MS > 1L)) {
                this.blacklist.remove(uuid);
            }
        }
        if (!this.blacklist.contains(uuid)) {
            Count++;
            if ((Latency.packetTicks.containsKey(uuid)) && (UtilTime.elapsed(Time, 1000L))) {
                packets.put(uuid, Count);
                Count = 0;
                Time = UtilTime.nowlong();
            }
        }
        Latency.packetTicks.put(uuid, new AbstractMap.SimpleEntry<>(Count, Time));
        Latency.lastPacket.put(uuid, System.currentTimeMillis());
    }

}