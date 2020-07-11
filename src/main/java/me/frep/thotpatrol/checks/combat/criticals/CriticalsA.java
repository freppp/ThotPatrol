package me.frep.thotpatrol.checks.combat.criticals;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.utils.UtilCheat;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CriticalsA extends Check {
	
    public static Map<UUID, Map.Entry<Integer, Long>> CritTicks = new HashMap<>();
    public static Map<UUID, Double> FallDistance = new HashMap<>();

    public CriticalsA(ThotPatrol ThotPatrol) {
        super("CriticalsA", "Criticals (Type A)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(4);
    }
    
    @EventHandler
    public void onLog(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        CritTicks.remove(uuid);
        if (FallDistance.containsKey(uuid)) {
            CritTicks.remove(uuid);
        }
    }

    @SuppressWarnings("deprecation")
	@EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) {
            return;
        }
        if (!(e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK))) {
            return;
        }
        Player p = (Player)e.getDamager();
        UUID uuid = p.getUniqueId();
        if (p.getAllowFlight() 
        		|| !UtilTime.elapsed(getThotPatrol().LastVelocity.getOrDefault(p.getUniqueId(), 0L), 2000)
                || p.getEyeLocation().clone().add(0, .5, 0).getBlock().getType().isSolid()
                || p.getEyeLocation().clone().add(0, 1, 0).getBlock().getType().isSolid()
        		|| UtilCheat.slabsNear(p.getLocation()) 
        		|| p.hasPermission("thotpatrol.bypass")) {
        	return;
        }
        int Count = 0;
        long Time = System.currentTimeMillis();
        if (CritTicks.containsKey(uuid)) {
            Count = CritTicks.get(uuid).getKey();
            Time = CritTicks.get(uuid).getValue();
        }
        if (!FallDistance.containsKey(uuid)) return;
        double realFallDistance = FallDistance.get(p.getUniqueId());
        Count = p.getFallDistance() > 0.0 && !p.isOnGround() && realFallDistance == 0.0 ? ++Count : 0;
        if (CritTicks.containsKey(uuid) && UtilTime.elapsed(Time, 10000)) {
            Count = 0;
            Time = UtilTime.nowlong();
        }
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
        if (Count >= 2) {
            dumplog(p, "[Flag] Count: " + Count + " | TPS: " + tps + " | Ping: " + ping);
            getThotPatrol().logCheat(this, p, "Packet Criticals" + " | Ping:" + ping + " | TPS: " + tps);
        	getThotPatrol().logToFile(p, this, "Packet", "Count: " 
                    + Count + " | TPS: " + tps + " | Ping: " + ping);
            Count = 0;
        }
        CritTicks.put(uuid, new AbstractMap.SimpleEntry<>(Count, Time));
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        double Falling = 0.0;
        if (!p.isOnGround() && e.getFrom().getY() > e.getTo().getY()) {
            if (FallDistance.containsKey(uuid)) {
                Falling = FallDistance.get(uuid);
            }
            Falling += e.getFrom().getY() - e.getTo().getY();
        }
        FallDistance.put(uuid, Falling);
    }
}