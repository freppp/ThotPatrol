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

    //TODO RECODE  THIS

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        verbose.remove(e.getPlayer().getUniqueId());
        blocking.remove(e.getPlayer().getUniqueId());
        lastDist.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (!UtilPlayer.isOnGround(e.getPlayer())
            || e.getItem() == null) {
            return;
        }
        if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getItem().getType().toString().contains("SWORD")) {
                blocking.add(e.getPlayer().getUniqueId());
            }
        }
        blocking.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        double tps = getThotPatrol().getLag().getTPS();
        int ping = getThotPatrol().getLag().getPing(p);
        double dist = UtilMath.getHorizontalDistance(e.getTo(), e.getFrom());
        if (e.getTo().getX() == e.getFrom().getX()
                || e.getFrom().getZ() == e.getTo().getZ()
                || !UtilPlayer.isOnGround(p)
                || p.getEyeLocation().clone().add(0, .5, 0).getBlock().getType().isSolid()
                || p.getLocation().clone().add(0, 1, 0).getBlock().getType().isSolid()
                || !blocking.contains(p.getUniqueId())
                || p.getWalkSpeed() > .25) {
            lastDist.put(p.getUniqueId(), 0D);
            return;
        }
        lastDist.put(p.getUniqueId(), dist);
        if (isActuallySprinting(p)) {
            count++;
            blocking.remove(p.getUniqueId());
        } else {
            if (count > 0) {
                count -= 2;
                blocking.remove(p.getUniqueId());
            }
        }
        if (count > 8 && isActuallySprinting(p)) {
            count = 0;
            blocking.remove(p.getUniqueId());
            getThotPatrol().logCheat(this, p, "Ping: " + ping + " | TPS: " + tps);
            getThotPatrol().logToFile(p, this, "Blocking while Running", "Delta: " + lastDist.getOrDefault(p.getUniqueId(), 0D) + " |  TPS: " + tps + " | Ping: " + ping);
        }
        verbose.put(p.getUniqueId(), count);
        blocking.remove(p.getUniqueId());
    }

    private boolean isActuallySprinting(Player p) {
        if (!UtilPlayer.isOnGround(p) || p.getWalkSpeed() > .23) {
            return false;
        }
        double lastAccel = lastDist.getOrDefault(p.getUniqueId(), 0D);
        double maxAccel = .26;
        for (PotionEffect e: p.getActivePotionEffects()) {
            if (e.getType().equals(PotionEffectType.SPEED)) {
                maxAccel += (e.getAmplifier() + 1) * .0885;
            }
        }
        if (lastAccel > maxAccel) {
            blocking.remove(p.getUniqueId());
            return true;
        }
        blocking.remove(p.getUniqueId());
        return false;
    }
}