package me.frep.thotpatrol.checks.player.client;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;

public class HackedClientA extends Check implements PluginMessageListener {

    public HackedClientA(ThotPatrol ThotPatrol) {
        super("HackedClientA", "Hacked Client (Type A)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(0);
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent e) {
        e.getPlayer().sendMessage("§8 §8 §1 §3 §3 §7 §8 ");
    }
    
    @SuppressWarnings("unused")
    public void onPluginMessageReceived(String s, Player player, byte[] data) {
		String str;
        try {
            str = new String(data);
        } catch (Exception ex) {
            str = "";
        }
        double ping = getThotPatrol().getLag().getPing(player);
        double tps = getThotPatrol().getLag().getTPS();
        getThotPatrol().logCheat(this, player, "Vape [Cracked] | Ping:" + ping + " | TPS: " + tps);
    	getThotPatrol().logToFile(player, this, "Vape [Cracked]", "| TPS: " + tps + " | Ping: " + ping);
    }
}