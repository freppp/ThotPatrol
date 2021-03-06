package me.frep.thotpatrol.checks.combat.killaura;

import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.events.PacketAttackEvent;
import me.frep.thotpatrol.utils.UtilMath;
import me.frep.thotpatrol.utils.UtilPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class KillAuraJ extends Check {

    private Map<UUID, Integer> verbose = new HashMap<>();
    private List<UUID> blocking = new ArrayList<>();
    private Map<UUID, Double> lastDist = new HashMap<>();

    // this is an auto block check, again you can make these in about 3 lines using packets, rather than this mess

    public KillAuraJ(me.frep.thotpatrol.ThotPatrol ThotPatrol) {
        super("KillAuraJ", "Kill Aura (Type J)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(8);
        setViolationResetTime(120000);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        verbose.remove(e.getPlayer().getUniqueId());
        blocking.remove(e.getPlayer().getUniqueId());
        lastDist.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        blocking.remove(e.getPlayer().getUniqueId());
        verbose.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent e) {
        blocking.remove(e.getPlayer().getUniqueId());
        verbose.remove(e.getPlayer().getUniqueId());
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
        } else {
            blocking.remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onAttack(PacketAttackEvent e) {
        Player d = e.getPlayer();
        if (d.getWalkSpeed() > .5) {
            return;
        }
        double tps = getThotPatrol().getLag().getTPS();
        int ping = getThotPatrol().getLag().getPing(d);
        int count = verbose.getOrDefault(d.getUniqueId(), 0);
        if (blocking.contains(d.getUniqueId()) && isActuallySprinting(d) && UtilPlayer.isOnGround(d)) {
            count++;
            getThotPatrol().verbose(this, d, ping, tps, lastDist.getOrDefault(d.getUniqueId(), 0D).toString() + " > .28");
        } else {
            if (count > 0) {
                count--;
            }
        }
        if (count >= 3) {
            count = 0;
            getThotPatrol().logCheat(this, d, "Speed: " + lastDist.getOrDefault(d.getUniqueId(), 0D).toString() + " | TPS: " + tps + " | Ping: " + ping);
            getThotPatrol().logToFile(d, this, "Auto Block", "Speed: " +
                    (lastDist.getOrDefault(d.getUniqueId(), 0D) + " | TPS: " + tps + " | Ping: " + ping));
        }
        verbose.put(d.getUniqueId(), count);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (e.getTo().getX() == e.getFrom().getX()
                || e.getFrom().getZ() == e.getTo().getZ()
                || !UtilPlayer.isOnGround(p)
                || p.getLocation().clone().add(0, 1, 0).getBlock().getType().isSolid()
                || p.getEyeLocation().clone().add(0, 1,0).getBlock().getType().isSolid()
                || !blocking.contains(p.getUniqueId())) {
            lastDist.put(p.getUniqueId(), 0D);
        }
        double dist = UtilMath.getHorizontalDistance(e.getTo(), e.getFrom());
        lastDist.put(p.getUniqueId(), dist);
        blocking.remove(p.getUniqueId());
    }

    private boolean isActuallySprinting(Player p) {
        double lastAccel = lastDist.getOrDefault(p.getUniqueId(), 0D);
        double maxAccel = .21;
        if (p.getWalkSpeed() > .21) {
            maxAccel += p.getWalkSpeed() * 1.1;
        }
        for (PotionEffect e: p.getActivePotionEffects()) {
            if (e.getType().equals(PotionEffectType.SPEED)) {
                maxAccel += (e.getAmplifier() + 1) * .0585;
            }
        }
        return lastAccel > maxAccel;
    }
}