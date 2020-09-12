package me.frep.thotpatrol.checks.player.badpackets;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.events.PacketPlayerEvent;
import me.frep.thotpatrol.utils.UtilPlayer;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.*;

public class BadPacketsD extends Check {
	
    public static Map<UUID, Map.Entry<Integer, Long>> packetTicks;
    public static Map<UUID, Long> lastPacket;
    public List<UUID> blacklist;

    public BadPacketsD(ThotPatrol ThotPatrol) {
        super("BadPacketsD", "Bad Packets (Type D)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(5);
        blacklist = new ArrayList<>();
        lastPacket = new HashMap<>();
        packetTicks = new HashMap<>();
    }

    //more packets check

    @EventHandler
    public void PlayerJoin(PlayerJoinEvent event) {
        blacklist.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent e) {
        packetTicks.remove(e.getPlayer().getUniqueId());
        lastPacket.remove(e.getPlayer().getUniqueId());
        blacklist.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void PlayerChangedWorld(PlayerChangedWorldEvent event) {
        blacklist.add(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void PlayerRespawn(PlayerRespawnEvent event) {
        blacklist.add(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void PacketPlayer(PacketPlayerEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission("thotpatrol.bypass")) return;
        if (!getThotPatrol().isEnabled()) return;
        if (player.getGameMode().equals(GameMode.CREATIVE)) return;
        if (getThotPatrol().lag.getTPS() > 21.0D || getThotPatrol().lag.getTPS() < getThotPatrol().getTPSCancel()) return;
        if (getThotPatrol().lag.getPing(player) > 200) return;
        int Count = 0;
        long Time = System.currentTimeMillis();
        if (packetTicks.containsKey(player.getUniqueId())) {
            Count = packetTicks.get(player.getUniqueId()).getKey();
            Time = packetTicks.get(player.getUniqueId()).getValue();
        }
        if (lastPacket.containsKey(player.getUniqueId())) {
            long MS = System.currentTimeMillis() - lastPacket.get(player.getUniqueId());
            if (MS >= 100L) {
                blacklist.add(player.getUniqueId());
            } else if ((MS > 1L) && (this.blacklist.contains(player.getUniqueId()))) {
                blacklist.remove(player.getUniqueId());
            }
        }
        int ping = getThotPatrol().getLag().getPing(player);
        double tps = getThotPatrol().getLag().getTPS();
        if (!blacklist.contains(player.getUniqueId())) {
            Count++;
            if ((packetTicks.containsKey(player.getUniqueId())) && (UtilTime.elapsed(Time, 1000L))) {
                int maxPackets = 80;
                if (Count > maxPackets) {
                    if (!UtilPlayer.isFullyStuck(player) && !UtilPlayer.isPartiallyStuck(player)) {
                    	getThotPatrol().logCheat(this, player, "More Packets: " + Count + " | Ping:" + ping + " | TPS: " + tps);
    		        	getThotPatrol().logToFile(player, this, "Packet Count", "Packets: " + Count + " > " + maxPackets + 
    		        			" | TPS: " + tps + " | Ping: " + ping);
                    }
                }
                if (Count > 400) {
                	getThotPatrol().logCheat(this, player, "More Packets: " + Count + " | Ping:" + ping + " | TPS: " + tps);
		        	getThotPatrol().logToFile(player, this, "Packet Count", "Packets: " + Count + " > " + maxPackets + 
		        			" | TPS: " + tps + " | Ping: " + ping);
                }
                Count = 0;
                Time = UtilTime.nowlong();
            }
        }
        packetTicks.put(player.getUniqueId(), new AbstractMap.SimpleEntry<>(Count, Time));
        lastPacket.put(player.getUniqueId(), System.currentTimeMillis());
    }
}
