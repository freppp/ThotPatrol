package me.frep.thotpatrol.checks.combat.velocity;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.PacketPlayerType;
import me.frep.thotpatrol.packets.events.PacketPlayerEvent;
import me.frep.thotpatrol.utils.UtilMath;
import me.frep.thotpatrol.utils.UtilPlayer;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VelocityA extends Check {

    private Map<UUID, Long> lastPacket = new HashMap<>();
    private Map<UUID, Integer> verbose = new HashMap<>();

    public VelocityA(ThotPatrol ThotPatrol) {
        super("VelocityA", "Velocity (Type A) [#]", ThotPatrol);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(6);
    }

    @EventHandler
    public void onPacket(PacketPlayerEvent e) {
        if (e.getType().equals(PacketPlayerType.ARM_SWING) || e.getType().equals(PacketPlayerType.POSITION) || e.getType().equals(PacketPlayerType.POSLOOK)
                || e.getType().equals(PacketPlayerType.USE)) {
            lastPacket.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
        }
        if (e.getType().equals(PacketPlayerType.FLYING) || e.getPlayer().getEyeLocation().clone().add(0, .5, 0).getBlock().getType().isSolid()) {
            lastPacket.put(e.getPlayer().getUniqueId(), 0L);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (UtilTime.elapsed(getThotPatrol().lastDamage.getOrDefault(p.getUniqueId(), 0L), 500)
            || p.getAllowFlight()
            || p.hasPermission("thotpatrol.bypass")) {
            return;
        }
        if (p.getLocation().getBlock().getRelative(BlockFace.NORTH).getType().isSolid()
            || p.getLocation().getBlock().getRelative(BlockFace.SOUTH).getType().isSolid()
            || p.getLocation().getBlock().getRelative(BlockFace.EAST).getType().isSolid()
            || p.getLocation().getBlock().getRelative(BlockFace.WEST).getType().isSolid()) {
            return;
        }
        long lastPacket = this.lastPacket.getOrDefault(p.getUniqueId(), 0L);
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        double delta =  System.currentTimeMillis() - lastPacket;
        double deltaX = UtilMath.getHorizontalDistance(e.getFrom(), e.getTo());
        double deltaY = UtilMath.getVerticalDistance(e.getFrom(), e.getTo());
        if (delta < 40 && deltaX < 0.00 && UtilPlayer.isOnGround(p) && deltaY < .01) {
            count++;
        }
        else {
            if (count > 0) {
                count--;
            }
        }
        if (count > 2) {
            count = 0;
            getThotPatrol().logCheat(this, p, "dX=" + deltaX + " deltaY " + deltaY + " delta= " + delta);
        }
        verbose.put(p.getUniqueId(), count);
    }
}
