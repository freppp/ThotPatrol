package me.frep.thotpatrol.checks.player.badpackets;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.PacketPlayerType;
import me.frep.thotpatrol.packets.events.PacketPlayerEvent;
import org.bukkit.event.EventHandler;

public class BadPacketsB extends Check {
	
    public BadPacketsB(ThotPatrol ThotPatrol) {
        super("BadPacketsB", "Bad Packets (Type B)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(1);
    }
    
    @EventHandler
    public void Player(PacketPlayerEvent e) {
        if (e.getType() != PacketPlayerType.LOOK) return;
        if (e.getPlayer().hasPermission("thotpatrol.bypass")) {
        	return;
        }
        int ping = getThotPatrol().getLag().getPing(e.getPlayer());
        double tps = getThotPatrol().getLag().getTPS();
        if ((e.getPitch() > 90.1F) || (e.getPitch() < -90.1F)) {
        	getThotPatrol().logCheat(this, e.getPlayer(), "Invalid Look Packets | Ping:" + ping + " | TPS: " + tps);
        	getThotPatrol().logToFile(e.getPlayer(), this, "Twitch", "Pitch: " + e.getPitch() + 
        			" | TPS: " + tps + " | Ping: " + ping);
        }
    }
}