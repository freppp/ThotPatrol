package me.frep.thotpatrol.checks.movement.timer;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.events.SharedEvents;
import me.frep.thotpatrol.packets.PacketCore;
import me.frep.thotpatrol.packets.events.PacketPlayerEvent;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;

public class TimerA extends Check {
	
    private Map<UUID, Map.Entry<Integer, Long>> packets;
    private Map<UUID, Integer> verbose;
    private Map<UUID, Long> lastPacket;
    private List<Player> toCancel;

    public TimerA(ThotPatrol ThotPatrol) {
        super("TimerA", "Timer (Type A)", ThotPatrol);

        packets = new HashMap<>();
        verbose = new HashMap<>();
        toCancel = new ArrayList<>();
        lastPacket = new HashMap<>();

        setEnabled(true);
        setBannable(true);
        setMaxViolations(3);
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent e) {
        packets.remove(e.getPlayer().getUniqueId());
        verbose.remove(e.getPlayer().getUniqueId());
        lastPacket.remove(e.getPlayer().getUniqueId());
        toCancel.remove(e.getPlayer());
    }

    @EventHandler
    public void PacketPlayer(PacketPlayerEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        if (!this.getThotPatrol().isEnabled()) return;
        if (p.hasPermission("thotpatrol.bypass")) return;
        if (getThotPatrol().getLag().getTPS() < getThotPatrol().getTPSCancel()) return;
        long lastPacket = this.lastPacket.getOrDefault(uuid, 0L);
        int packets = 0;
        long Time = System.currentTimeMillis();
        int verbose = this.verbose.getOrDefault(uuid, 0);
        if (this.packets.containsKey(uuid)) {
            packets = this.packets.get(uuid).getKey();
            Time = this.packets.get(uuid).getValue();
        }
        if (System.currentTimeMillis() - lastPacket < 5) {
            this.lastPacket.put(uuid, System.currentTimeMillis());
            return;
        }
        if (SharedEvents.worldChange.contains(uuid) || !UtilTime.elapsed(SharedEvents.lastTeleport.getOrDefault(p.getUniqueId(), 0L), 4000)) return;
        double tps = getThotPatrol().getLag().getTPS();
        int ping = getThotPatrol().getLag().getPing(p);
        if (ping > 10000) return;
        double threshold = 21;
        if (UtilTime.elapsed(Time, 1000L)) {
            if (toCancel.remove(p) && packets <= 13) {
                return;
            }
            if (packets > threshold + PacketCore.movePackets.getOrDefault(uuid, 0) && PacketCore.movePackets.getOrDefault(uuid, 0) < 5) {
                verbose = (packets - threshold) > 10 ? verbose + 2 : verbose + 1;
            } else {
                verbose = 0;
            }
            if (verbose > 2) {
            	getThotPatrol().logCheat(this, p, "Packets: " + packets + " | Ping: " + ping + " | TPS: " + tps);
            	getThotPatrol().logToFile(p, this, "Packet", "Packets: " + packets
            			+ " | TPS: " + tps + " | Ping: " + ping);
            	dumplog(p, "Packets: " + packets + " | TPS: " + tps + " | Ping: " + ping);
            }
            if (getThotPatrol().getConfig().getBoolean("instantBans.TimerA.enabled") 
            		&& packets > getThotPatrol().getConfig().getInt("instantBans.TimerA.maxPackets")
            		&& isBannable()
                    && !getThotPatrol().NamesBanned.containsKey(p.getName())
                    && !getThotPatrol().getNamesBanned().containsKey(p.getName())
            		&& getThotPatrol().getLag().getTPS() > getThotPatrol().getConfig().getDouble("instantBans.TimerA.minTPS")
            		&& ping > 1
            		&& ping < getThotPatrol().getConfig().getInt("instantBans.TimerA.maxPing")
            		&& verbose > 2) {
            	String banAlertMessage = getThotPatrol().getConfig().getString("instantBans.TimerA.banAlertMessage");
            	getThotPatrol().alert(ChatColor.translateAlternateColorCodes('&', banAlertMessage.replaceAll("%player%", p.getName())
            			.replaceAll("%packets%", Integer.toString(packets))));
            	dumplog(p, "[Instant Ban] Packets: " + packets + " | TPS: " + tps + " | Ping: " + ping);
            	getThotPatrol().logToFile(p, this, "Packets [Instant Ban]", "Packets: " + packets + " | TPS: " + tps + " | Ping: " + ping);
                getThotPatrol().banPlayer(p, this);
            }
            packets = 0;
            Time = UtilTime.nowlong();
            PacketCore.movePackets.remove(uuid);
        }
        packets++;
        this.lastPacket.put(uuid, System.currentTimeMillis());
        this.packets.put(uuid, new SimpleEntry<>(packets, Time));
        this.verbose.put(uuid, verbose);
    }
}