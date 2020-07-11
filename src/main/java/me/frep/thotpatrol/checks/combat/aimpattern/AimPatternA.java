package me.frep.thotpatrol.checks.combat.aimpattern;

import me.frep.thotpatrol.checks.Check;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AimPatternA extends Check {

    private float lastYaw;
    private Map<UUID, Integer> verbose = new HashMap<>();

    public AimPatternA(me.frep.thotpatrol.ThotPatrol ThotPatrol) {
        super("AimPatternA", "Aim Pattern (Type A) [#]", ThotPatrol);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(7);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (e.getFrom().getYaw() == e.getTo().getYaw()
            || p.getAllowFlight()
            || p.hasPermission("thotpatrol.bypass")
            || p.isBlocking()
            || p.getNearbyEntities(5, 5, 5).isEmpty()
            || p.getItemInHand().getType().equals(Material.BOW)) {
            return;
        }
        double tps = getThotPatrol().getLag().getTPS();
        int ping = getThotPatrol().getLag().getPing(p);
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        float yawDiff = Math.abs(e.getFrom().getYaw() - e.getTo().getYaw()) % 180;
        if (yawDiff > 1 && lastYaw > 1 && yawDiff == lastYaw) {
            count++;
        } else {
            if (count > 0) {
                count--;
            }
        }
        if (count > 5) {
            getThotPatrol().logCheat(this, p, "Ping: " + ping + " | TPS: " + tps);
            getThotPatrol().logToFile(p, this, "Invalid Yaw", "TPS: " + tps + " | Ping: " + ping);
            count = 0;
        }
        verbose.put(p.getUniqueId(), count);
        lastYaw = yawDiff;
    }
}