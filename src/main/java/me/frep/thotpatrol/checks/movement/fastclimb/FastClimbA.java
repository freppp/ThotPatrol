package me.frep.thotpatrol.checks.movement.fastclimb;

import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.utils.UtilMath;
import me.frep.thotpatrol.utils.UtilPlayer;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class FastClimbA extends Check {

    private static List<Double> jumpedValues = new ArrayList<>();
    private Map<UUID, Integer> verbose = new HashMap<>();

    static {
        jumpedValues.add(.41999998688697815);
        jumpedValues.add(.16477328182606676);
        jumpedValues.add(.33319999363422337);
        jumpedValues.add(.24813599859094548);
        jumpedValues.add(.164773281826065);
        jumpedValues.add(.33319999363422426);
        jumpedValues.add(.24813599859094637);
    }

    public FastClimbA(me.frep.thotpatrol.ThotPatrol ThotPatrol) {
        super("FastClimbA", "Fast Climb (Type A)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(5);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        double yDiff = e.getTo().getY() - e.getFrom().getY();
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        if (e.isCancelled()
                || e.getFrom().getY() == e.getTo().getY()
                || p.getAllowFlight()
                || !UtilTime.elapsed(getThotPatrol().LastVelocity.getOrDefault(p.getUniqueId(), 0L), 1500)
                || p.hasPermission("thotpatrol.bypass")
                || !UtilPlayer.isOnClimbable(p, 1)
                || p.hasPotionEffect(PotionEffectType.JUMP) // todo <--- fix
                || !UtilPlayer.isOnClimbable(p, 0)
                || p.getEyeLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.AIR)
                || yDiff <= 0
                || jumpedValues.contains(yDiff)) {
            return;
        }
        double offset = UtilMath.offset(UtilMath.getVerticalVector(e.getFrom().toVector()), UtilMath.getVerticalVector(e.getTo().toVector()));
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
        double limit = .13;
        if (offset > limit + .2) {
            count += 3;
        } else {
            if (offset > limit) {
                count++;
            } else {
                if (count > 0) {
                    count--;
                }
            }
        }
        if (count > 6
                && getThotPatrol().getConfig().getBoolean("instantBans.FastClimbA.enabled")
                && offset > getThotPatrol().getConfig().getDouble("instantBans.FastClimbA.maxSpeed") && isBannable()
                && !getThotPatrol().NamesBanned.containsKey(p.getName()) && ping > 1
                && !getThotPatrol().getNamesBanned().containsKey(p.getName())
                && tps > getThotPatrol().getConfig().getDouble("instantBans.FastClimbA.minTPS")
                && ping < getThotPatrol().getConfig().getInt("instantBans.FastClimbA.maxPing")) {
            String banAlertMessage = getThotPatrol().getConfig().getString("instantBans.FastClimbA.banAlertMessage");
            getThotPatrol().alert(ChatColor.translateAlternateColorCodes('&', banAlertMessage.replaceAll("%player%", p.getName())
                    .replaceAll("%speed%", Double.toString(UtilMath.trim(2, offset)))));
            dumplog(p, "[Instant Ban] Climb Speed: " + offset + " | TPS: " + tps + " | Ping: " + ping);
            getThotPatrol().logToFile(p, this, "Invalid Offset [Instant Ban]", "Offset: " + offset + " | TPS: " + tps + " | Ping: " + ping);
            getThotPatrol().banPlayer(p, this);
        }
        if (count > 6) {
            count = 0;
            getThotPatrol().logCheat(this, p, offset + " > " + limit + " | Ping: " + ping + " | TPS: " + tps);
            getThotPatrol().logToFile(p, this, "Invalid Offset", "Offset: " + offset + " > " + limit + " | TPS: " + tps + " | Ping: " + ping);
        }
        verbose.put(p.getUniqueId(), count);
    }
}