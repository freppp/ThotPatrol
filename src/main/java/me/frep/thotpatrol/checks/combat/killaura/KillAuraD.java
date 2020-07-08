package me.frep.thotpatrol.checks.combat.killaura;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class KillAuraD extends Check {
    public static Map<Player, Map.Entry<Integer, Long>> lastAttack;

    public KillAuraD(ThotPatrol ThotPatrol) {
        super("KillAuraD", "Kill Aura (Type D)", ThotPatrol);
        lastAttack = new HashMap<>();
        setEnabled(true);
        setBannable(true);
        setMaxViolations(6);
    }

    @EventHandler
    public void onLog(PlayerQuitEvent e) {
        lastAttack.remove(e.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void Damage(EntityDamageByEntityEvent e) {
        if (e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK
                || !(e.getDamager() instanceof Player)) return;
        Player player = (Player) e.getDamager();
		if (player.hasPermission("thotpatrol.bypass")) {
			return;
		}
        int ping = getThotPatrol().getLag().getPing(player);
        double tps = getThotPatrol().getLag().getTPS();
        if (lastAttack.containsKey(player)) {
            Integer entityid = lastAttack.get(player).getKey();
            Long time = lastAttack.get(player).getValue();
            if (entityid != e.getEntity().getEntityId() && System.currentTimeMillis() - time < 6L) {
                getThotPatrol().logCheat(this, player, "Multi Aura | Ping:" + ping + " | TPS: " + tps);
            	getThotPatrol().logToFile(player, this, "Multi Aura", "TPS: " + tps + " | Ping: " + ping);
            }
            lastAttack.remove(player);
        } else {
            lastAttack.put(player, new AbstractMap.SimpleEntry<>(e.getEntity().getEntityId(), System.currentTimeMillis()));
        }
    }
}