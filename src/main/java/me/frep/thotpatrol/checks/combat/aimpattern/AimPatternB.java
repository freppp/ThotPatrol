package me.frep.thotpatrol.checks.combat.aimpattern;

import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.utils.UtilPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AimPatternB extends Check {

    private float lastYawDiff;
    private Map<UUID, Integer> verbose = new HashMap<>();

    public AimPatternB(me.frep.thotpatrol.ThotPatrol ThotPatrol) {
        super("AimPatternB", "Aim Pattern (Type B) [#]", ThotPatrol);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(7);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (e.getFrom().getX() == e.getTo().getX() && e.getFrom().getZ() == e.getTo().getZ()
            || p.hasPermission("thotpatrol.bypass")
            || p.isBlocking()
            || p.getItemInHand().getType().equals(Material.BOW)
            || !UtilPlayer.isOnGround(p)
            || e.isCancelled()
            || !p.isOnGround()
            || p.getNearbyEntities(5, 5, 5).isEmpty()) {
            return;
        }
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        float yawDiff = Math.abs(e.getFrom().getYaw() - e.getTo().getYaw()) % 180;
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
        if (yawDiff - 2 > lastYawDiff && yawDiff + 2 < lastYawDiff && yawDiff < 10 && yawDiff < 170 || yawDiff > 150 && yawDiff > 1) {
            return;
        }
        if (yawDiff > 9 && e.getFrom().getPitch() == e.getTo().getPitch()) {
            count++;
        } else {
            if (count > 0) {
                count--;
            }
        }
        if (count > 10) {
            getThotPatrol().logCheat(this, p, "Ping: " + ping + " | TPS: " + tps);
            getThotPatrol().logToFile(p, this, "Invalid Pitch Change", "Ping: " + ping + " | TPS: " + tps);
            count = 0;
        }
        verbose.put(p.getUniqueId(), count);
        lastYawDiff = yawDiff;
    }
}