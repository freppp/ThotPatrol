package me.frep.thotpatrol.checks.combat.criticals;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.events.PacketAttackEvent;
import me.frep.thotpatrol.utils.UtilCheat;
import me.frep.thotpatrol.utils.UtilMath;
import me.frep.thotpatrol.utils.UtilPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CriticalsC extends Check {

    private Map<UUID, Double> lastDeltaY = new HashMap<>();
    private Map<UUID, Integer> verbose = new HashMap<>();

    public CriticalsC(ThotPatrol ThotPatrol) {
        super("CriticalsC", "Criticals (Type C) [#]", ThotPatrol);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(4);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        double deltaY = UtilMath.getVerticalDistance(e.getFrom(), e.getTo());
        if (!e.getPlayer().isOnGround() || !UtilPlayer.isOnGround(e.getPlayer())
                || !(e.getPlayer().getLocation().getY() % 1 == 0)
                || !(e.getTo().getY() % 1 == 0)
                || e.getPlayer().getEyeLocation().clone().add(0, .5, 0).getBlock().getType().isSolid()
                || e.getPlayer().getEyeLocation().clone().add(0, 1, 0).getBlock().getType().isSolid()) {
            lastDeltaY.put(e.getPlayer().getUniqueId(), 0D);
        }
        lastDeltaY.put(e.getPlayer().getUniqueId(), deltaY);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent e) {
        if (!e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)
            || !(e.getDamager() instanceof Player)) {
            return;
        }
        Player p = (Player)e.getDamager();
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        double lastY = lastDeltaY.getOrDefault(p.getUniqueId(), 0D);
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
        if (UtilCheat.slabsNear(p.getLocation())
            || p.getAllowFlight()
            || p.hasPermission("thotpatrol.bypass")
            || p.getEyeLocation().clone().add(0, .5, 0).getBlock().getType().isSolid()
            || p.getEyeLocation().clone().add(0, 1, 0).getBlock().getType().isSolid()) {
            return;
        }
        if (p.isOnGround() && UtilPlayer.isOnGround(p) && lastY > 0 && lastY <= .35 && p.getLocation().getY() % 1 == 0) {
            count++;
        }
        if (count > 1) {
            count = 0;
            getThotPatrol().logCheat(this, p, "Packet | Ping: " + ping + " | TPS: " + tps);
            getThotPatrol().logToFile(p, this, "Packet", "Ping: " + ping + " | TPS: " + tps);
        }
        verbose.put(p.getUniqueId(), count);
    }
}