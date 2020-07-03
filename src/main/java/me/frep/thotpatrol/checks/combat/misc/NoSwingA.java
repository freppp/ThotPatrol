package me.frep.thotpatrol.checks.combat.misc;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.events.PacketSwingArmEvent;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NoSwingA extends Check {
	
	private Map<UUID, Long> LastArmSwing = new HashMap<UUID, Long>();
	
    public NoSwingA(ThotPatrol ThotPatrol) {
        super("NoSwingA", "No Swing (Type A)", ThotPatrol);
        setMaxViolations(5);
        setEnabled(true);
        setBannable(true);
    }


    //todo recode this
    @EventHandler(ignoreCancelled=true, priority=EventPriority.MONITOR)
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        if (!e.getCause().equals((Object)EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            return;
        }
        if (getThotPatrol().getLag().getTPS() < 17.0) {
            return;
        }
        Player p = (Player)e.getDamager();
		double tps = getThotPatrol().getLag().getTPS();
		double ping = getThotPatrol().getLag().getPing(p);
		for (Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins()) {
            if (plugin.getName().equals("StrikePractice")) {
                if (p.getName().toString().startsWith("BOT_") && p.getName().endsWith("Bot")) {
                    return;
                }
            }
        }
        if (getThotPatrol().isEnabled()) {
            Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)getThotPatrol(), new Runnable(){
                @Override
                public void run() {
                    if (!hasSwung(p, 1500L)) {
                        getThotPatrol().logCheat(NoSwingA.this, p, null);
            			getThotPatrol().logToFile(p, NoSwingA.this, "Packet", "Ping: " 
            					+ ping + " | TPS: " + tps);
                    }
                }
            }, 10);
        }
    }

    public boolean hasSwung(Player p, Long time) {
        if (!this.LastArmSwing.containsKey(p.getUniqueId())) {
            return false;
        }
        if (UtilTime.nowlong() < this.LastArmSwing.get(p.getUniqueId()) + time) {
            return true;
        }
        return false;
    }

    @EventHandler
    public void ArmSwing(PacketSwingArmEvent e) {
        LastArmSwing.put(e.getPlayer().getUniqueId(), UtilTime.nowlong());
    }
}
