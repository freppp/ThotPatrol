package me.frep.thotpatrol.checks.combat.killaura;

import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.PacketPlayerType;
import me.frep.thotpatrol.packets.events.PacketAttackEvent;
import me.frep.thotpatrol.utils.UtilBlock;
import me.frep.thotpatrol.utils.UtilMath;
import me.frep.thotpatrol.utils.UtilPlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillAuraK extends Check {

    private Map<UUID, Double> lastAirSpeed = new HashMap<>();
    private Map<UUID, Double> lastLastAirSpeed = new HashMap<>();
    private Map<UUID, Integer> verbose = new HashMap<>();

    public KillAuraK(me.frep.thotpatrol.ThotPatrol ThotPatrol) {
        super("KillAuraK", "Kill Aura (Type K)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(8);
        setViolationResetTime(120000);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (e.getTo().getX() == e.getFrom().getX()
                && e.getFrom().getZ() == e.getTo().getZ()
                || e.getFrom().getPitch() == e.getTo().getPitch()
                || e.getFrom().getYaw() == e.getTo().getYaw()
                || UtilPlayer.isOnGround(p)
                || UtilPlayer.isOnGround4(p)
                || p.getAllowFlight()
                || nearIce(p)) {
            return;
        }
        double airSpeed = UtilMath.offset(getHV(e.getTo().toVector()), getHV(e.getFrom().toVector()));
        lastAirSpeed.put(p.getUniqueId(), airSpeed);
    }

    @EventHandler
    public void onAttack(PacketAttackEvent e) {
        Player p = e.getPlayer();
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        //TODO calc walkSpeed valeus
        double lastSpeed = lastAirSpeed.getOrDefault(p.getUniqueId(), 0D);
        double lastLastSpeed = lastLastAirSpeed.getOrDefault(p.getUniqueId(), 0D);
        if (p.getWalkSpeed() > .25
            || p.getAllowFlight()
            || lastSpeed == lastLastSpeed
            || e.getType() != PacketPlayerType.USE) {
            return;
        }
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
        if (!UtilPlayer.isOnGround(p)) {
            if (lastSpeed > .29) {
                count++;
            }
            if (count >= 3) {
                count = 0;
                getThotPatrol().logCheat(this, p, "Ping: " + ping + " | TPS: " + tps);
                getThotPatrol().logToFile(p, this, "Packet", "Speed: " + lastSpeed
                        + " | Ping: " + ping + " | TPS: " + tps);
            }
        } else {
            if (count > 0) {
                count--;
            }
        }
        lastLastAirSpeed.put(p.getUniqueId(), lastSpeed);
        verbose.put(p.getUniqueId(), count);
    }

    private boolean isActuallySprinting(Player p) {
        double lastSpeed = lastAirSpeed.getOrDefault(p.getUniqueId(), 0D);
        double maxAirSpeed = .27;
        for (PotionEffect e: p.getActivePotionEffects()) {
            if (e.getType().equals(PotionEffectType.SPEED)) {
                int level = getPotionEffectLevel(p, PotionEffectType.SPEED);
                maxAirSpeed = (maxAirSpeed * (((level * 20) * 0.05) + 1));
            }
        }
        return lastSpeed > maxAirSpeed;
    }

    private int getPotionEffectLevel(Player p, PotionEffectType pet) {
        for (PotionEffect pe : p.getActivePotionEffects()) {
            if (pe.getType().getName().equals(pet.getName())) {
                return pe.getAmplifier() + 1;
            }
        }
        return 0;
    }

    private boolean nearIce(Player p) {
        for (Block b : UtilBlock.getNearbyBlocks(p.getLocation(), 2) ){
            if (b.getType().toString().contains("ICE")) {
                return true;
            }
        }
        return false;
    }

    private Vector getHV(Vector V) {
        V.setY(0);
        return V;
    }
}