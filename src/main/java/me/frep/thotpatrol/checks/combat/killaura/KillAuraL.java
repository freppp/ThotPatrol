package me.frep.thotpatrol.checks.combat.killaura;

import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.events.PacketAttackEvent;
import me.frep.thotpatrol.utils.UtilCheat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillAuraL extends Check {

    private Map<UUID, Integer> verbose = new HashMap<>();

    public KillAuraL(me.frep.thotpatrol.ThotPatrol ThotPatrol) {
        super("KillAuraL", "Kill Aura (Type L)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(8);
    }

    @EventHandler
    public void onAttack(PacketAttackEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = e.getPlayer();
        Player victim = (Player)e.getEntity();
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
        //todo fix this
        if (!p.hasLineOfSight(victim) && !UtilCheat.blocksNear(victim.getLocation())) {
            count++;
            getThotPatrol().verbose(this, p, ping, tps, Integer.toString(count));
        } else {
            if (count > 0) {
                count--;
            }
        }
        if (count >= 4) {
            count = 0;
            getThotPatrol().logCheat(this, p, "Wall | Ping: " + ping + " | TPS: " + tps);
            getThotPatrol().logToFile(p, this, "Wall", "TPS: " + tps + " | Ping: " + ping);
        }
        verbose.put(p.getUniqueId(), count);
    }
}