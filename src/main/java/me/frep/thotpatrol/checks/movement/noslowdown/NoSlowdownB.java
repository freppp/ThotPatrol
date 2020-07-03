package me.frep.thotpatrol.checks.movement.noslowdown;

import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.utils.UtilMath;
import me.frep.thotpatrol.utils.UtilPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class NoSlowdownB extends Check {

    private Map<UUID, Integer> verbose = new HashMap<>();
    private List<UUID> blocking = new ArrayList<>();
    private Map<UUID, Double> lastDist = new HashMap<>();

    public NoSlowdownB(me.frep.thotpatrol.ThotPatrol ThotPatrol) {
        super("NoSlowdownB", "NoSlowdown (Type B)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(6);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        verbose.remove(e.getPlayer().getUniqueId());
        blocking.remove(e.getPlayer().getUniqueId());
        lastDist.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!UtilPlayer.isOnGround(e.getPlayer())) {
            return;
        }
        if (e.getItem() == null) {
            return;
        }
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getItem().getType().toString().contains("SWORD")) {
                blocking.add(e.getPlayer().getUniqueId());
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        double tps = getThotPatrol().getLag().getTPS();
        int ping = getThotPatrol().getLag().getPing(p);
        //todo calc walkspeed values
        if (e.getTo().getX() == e.getFrom().getX()
                || e.getFrom().getZ() == e.getTo().getZ()
                || !UtilPlayer.isOnGround(p)
                || !blocking.contains(p.getUniqueId())
                || p.getWalkSpeed() > .25) {
            return;
        }
        double dist = UtilMath.getHorizontalDistance(e.getTo(), e.getFrom());
        lastDist.put(p.getUniqueId(), dist);
        if (isActuallySprinting(p)) {
            count++;
        } else {
            if (count > 0) {
                count--;
            }
        }
        if (count > 5 && isActuallySprinting(p)) {
            count = 0;
            getThotPatrol().logCheat(this, p, "Ping: " + ping + " | TPS: " + tps);
            getThotPatrol().logToFile(p, this, "Blocking while Running", "Delta: " + lastDist.getOrDefault(p.getUniqueId(), 0D) + " |  TPS: " + tps + " | Ping: " + ping);
        }
        verbose.put(p.getUniqueId(), count);
        blocking.remove(p.getUniqueId());
    }

    private boolean isActuallySprinting(Player p) {
        //todo calc walkSpeed values
        if (!UtilPlayer.isOnGround(p) || p.getWalkSpeed() > .23) {
            return false;
        }
        double lastAccel = lastDist.getOrDefault(p.getUniqueId(), 0D);
        double maxAccel = .21;
        for (PotionEffect e: p.getActivePotionEffects()) {
            if (e.getType().equals(PotionEffectType.SPEED)) {
                maxAccel += (e.getAmplifier() + 1) * .0585;
            }
        }
        if (lastAccel > maxAccel) {
            blocking.remove(p.getUniqueId());
            return true;
        }
        return false;
    }
}