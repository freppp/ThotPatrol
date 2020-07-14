package me.frep.thotpatrol.checks.combat.reach;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.PacketPlayerType;
import me.frep.thotpatrol.packets.events.PacketAttackEvent;
import me.frep.thotpatrol.utils.UtilPlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ReachE extends Check {

    private HashMap<UUID, ArrayList<Double>> lastReaches = new HashMap<>();

    public ReachE(ThotPatrol ThotPatrol) {
        super("ReachE", "Reach (Type E) [#]", ThotPatrol);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(7);
        setViolationResetTime(120000);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        lastReaches.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onAttack(PacketAttackEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player d = e.getPlayer();
        Player p = (Player) e.getEntity();
        if (e.getType() != PacketPlayerType.USE
                || d.hasPermission("thotpatrol.bypass")
                || d.getAllowFlight()
                || d.getGameMode().equals(GameMode.CREATIVE)) return;
        double yDist = Math.abs(UtilPlayer.getEyeLocation(d).getY() - UtilPlayer.getEyeLocation(p).getY()) > 0.5
                ? Math.abs(UtilPlayer.getEyeLocation(d).getY() - UtilPlayer.getEyeLocation(p).getY()) : 0;
        double yawDiff = Math.abs(180 - Math.abs(d.getLocation().getYaw() - p.getLocation().getYaw()));
        double reach = (UtilPlayer.getEyeLocation(d).distance(p.getEyeLocation()) - yDist) - 0.32;
        if (reach > 6.5) return;
        double tps = getThotPatrol().getLag().getTPS();
        int ping = getThotPatrol().getLag().getPing(d);
        if (!UtilPlayer.isOnGround(d)) {
            reach -= .1;
        }
        reach -= yawDiff > 100 && yDist < 0.1 ? yawDiff * 0.005 : yawDiff * 0.0025;
        reach -= yDist * .25;
        for (PotionEffect effect : d.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.SPEED)) {
                int level = effect.getAmplifier() + 1;
                reach -= level * .06;
            }
        }
        if (lastReaches.get(d.getUniqueId()) == null) {
            lastReaches.put(d.getUniqueId(), new ArrayList<>());
        }
        lastReaches.get(d.getUniqueId()).add(reach);
        double sum = 0;
        for (Double reaches : lastReaches.get(d.getUniqueId())) {
            sum += reaches;
        }
        double average = sum / lastReaches.get(d.getUniqueId()).size();
        if (lastReaches.get(d.getUniqueId()).size() % 45 == 0) {
            if (average > 3.001) {
                getThotPatrol().logCheat(this, d, "Average: " + average + " | Ping: " + ping + " | TPS: " + tps);
                getThotPatrol().logToFile(d, this, "Reach (Average)", "Average: " + average
                        + " > " + 3.001 + " | TPS: " + tps + " | Ping: " + ping);
            }
            lastReaches.get(d.getUniqueId()).clear();
        }
    }
}