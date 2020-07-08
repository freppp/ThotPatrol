package me.frep.thotpatrol.checks.combat.autoclicker;

import com.comphenix.protocol.wrappers.EnumWrappers;
import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.events.PacketUseEntityEvent;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AutoClickerA extends Check {
	
    public static Map<UUID, Map.Entry<Integer, Long>> attackTicks = new HashMap<>();

    public AutoClickerA(ThotPatrol ThotPatrol) {
        super("AutoClickerA", "Auto Clicker (Type A)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(7);
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
    	if (attackTicks.containsKey(e.getPlayer().getUniqueId())) {
        }
    }
    
	@EventHandler(ignoreCancelled=true, priority=EventPriority.HIGH)
    public void UseEntity(PacketUseEntityEvent e) {
        if (e.getAction() != EnumWrappers.EntityUseAction.ATTACK
        		|| e.getAttacker() == null) {
            return;
        }
        Player p = e.getAttacker();
        UUID uuid = p.getUniqueId();
        int CPS = 0;
        long Time = System.currentTimeMillis();
        if (attackTicks.containsKey(uuid)) {
            CPS = attackTicks.get(uuid).getKey();
            Time = attackTicks.get(uuid).getValue();
        }
		if (p.hasPermission("thotpatrol.bypass")) {
			return;
		}
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
        CPS++;
        if ((attackTicks.containsKey(uuid)) && (UtilTime.elapsed(Time, 1000L))) {
            if (CPS >= getThotPatrol().getConfig().getInt("settings.autoClickerAClickSpeed")) {
            	dumplog(p, "CPS: " + CPS + " | TPS: " + tps + " | Ping: " + ping);
            	getThotPatrol().logCheat(this, p, "CPS: " + CPS + " | Ping: " + ping + " | TPS: " + tps);
            	getThotPatrol().logToFile(p, this, "Click Speed", "CPS: " + CPS + " | TPS: " + tps + " | Ping: " + ping);
            }
            CPS = 0;
            Time = UtilTime.nowlong();
        }
        if (CPS > getThotPatrol().getConfig().getInt("instantBans.AutoClickerA.maxCPS")
        		&& getThotPatrol().getConfig().getBoolean("instantBans.AutoClickerA.enabled")
        		&& isBannable()
        		&& !getThotPatrol().getNamesBanned().containsKey(p.getName())
        		&& tps > getThotPatrol().getConfig().getDouble("instantBans.AutoClickerA.minTPS")
        		&& ping < getThotPatrol().getConfig().getInt("instantBans.AutoClickerA.maxPing")
        		&& ping > 1) {
        	String banAlertMessage = getThotPatrol().getConfig().getString("instantBans.AutoClickerA.banAlertMessage");
        	getThotPatrol().alert(ChatColor.translateAlternateColorCodes('&', banAlertMessage.replaceAll("%player%", p.getName())
        			.replaceAll("%CPS%", Integer.toString(CPS))));
        	dumplog(p, "[Instant Ban] CPS: " + CPS + " | TPS: " + tps + " | Ping: " + ping);
        	getThotPatrol().logToFile(p, this, "Click Speed [Instant Ban]", "CPS: " + CPS + " | TPS: " + tps + " | Ping: " + ping);
            getThotPatrol().banPlayer(p, this);
        }
        attackTicks.put(uuid, new AbstractMap.SimpleEntry<Integer, Long>(CPS, Time));
    }
}