package me.frep.thotpatrol.checks.movement.step;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.checks.movement.ascension.AscensionA;
import me.frep.thotpatrol.utils.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;

public class StepA extends Check {
	
    double stepHeight;

    public StepA(ThotPatrol ThotPatrol) {
        super("StepA", "Step (Type A)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(7);
    }

    public boolean isOnGround(Player player) {
        if (UtilPlayer.isOnClimbable(player, 0)) {
            return false;
        }
        if (player.getVehicle() != null) {
            return false;
        }
        Material type = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
        if ((type != Material.AIR) && (type.isBlock()) && (type.isSolid()) && (type != Material.LADDER)
                && (type != Material.VINE)) {
            return true;
        }
        Location a = player.getLocation().clone();
        a.setY(a.getY() - 0.5D);
        type = a.getBlock().getType();
        if ((type != Material.AIR) && (type.isBlock()) && (type.isSolid()) && (type != Material.LADDER)
                && (type != Material.VINE)) {
            return true;
        }
        a = player.getLocation().clone();
        a.setY(a.getY() + 0.5D);
        type = a.getBlock().getRelative(BlockFace.DOWN).getType();
        if ((type != Material.AIR) && (type.isBlock()) && (type.isSolid()) && (type != Material.LADDER)
                && (type != Material.VINE)) {
            return true;
        }
        return UtilCheat.isBlock(player.getLocation().getBlock().getRelative(BlockFace.DOWN),
                new Material[]{Material.FENCE, Material.FENCE_GATE, Material.COBBLE_WALL, Material.LADDER});
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        double tps = getThotPatrol().getLag().getTPS();
        int ping = getThotPatrol().getLag().getPing(player);
        if (player.getWorld().getHighestBlockAt(player.getLocation()).getType().toString().contains("SLIME")) {
            return;
        }
        if (!getThotPatrol().isEnabled()
                || !isOnGround(player)
                || player.getAllowFlight()
                || player.hasPotionEffect(PotionEffectType.JUMP)
                || getThotPatrol().LastVelocity.containsKey(player.getUniqueId())
                || UtilPlayer.isOnClimbable(player, 0)
                || !UtilTime.elapsed(AscensionA.lastNearSlime.getOrDefault(player.getUniqueId(), 0l), 2000)
                || player.hasPermission("thotpatrol.bypass")
                || UtilCheat.slabsNear(player.getLocation())
                || player.getLocation().getBlock().getType().equals(Material.WATER)
                || player.getLocation().getBlock().getType().equals(Material.STATIONARY_WATER)) return;
        double yDist = event.getTo().getY() - event.getFrom().getY();
        double YSpeed = UtilMath.offset(UtilMath.getVerticalVector(event.getFrom().toVector()),
                UtilMath.getVerticalVector(event.getTo().toVector()));
        Material below2 = player.getLocation().subtract(0, 1, 0).getBlock().getType();
        Material below3 = player.getLocation().subtract(0, 2, 0).getBlock().getType();
        if (below2.toString().contains("PISTON") ||
        		below3.toString().contains("PISTON")) {
        	return;
        }
        for (final Block b : UtilBlock.getNearbyBlocks(player.getLocation(), 2)) {
        	if (b.getType().toString().contains("PISTON") || b.getType().toString().contains("SNOW")
                || b.getType().toString().contains("TABLE")) {
        		return;
        	}
        }
        ArrayList<Block> blocks = UtilBlock.getBlocksAroundCenter(player.getLocation(), 1);
        String below = player.getLocation().clone().subtract(0.0D, 0.1D, 0.0D).getBlock().getType().toString();
        if (yDist < 0) return;
        if (yDist > 0.95) {
            dumplog(player, "Height (Logged): " + yDist);
            getThotPatrol().logCheat(this, player, Math.round(yDist) + " blocks");
        	getThotPatrol().logToFile(player, this, "Height", "Distance: " + yDist
        			+ " | TPS: " + tps + " | Ping: " + ping);
        }
        for (Block block : blocks) {
            if (block.getType().isSolid()) {
                if ((YSpeed >= 0.321 && YSpeed < 0.322)) {
                    getThotPatrol().logCheat(this, player, "Speed: " + YSpeed + " | Ping: " + ping + " TPS: " + tps);
                	getThotPatrol().logToFile(player, this, "Speed", "Speed: " + YSpeed
                			+ " | TPS: " + tps + " | Ping: " + ping);
                    return;
                }
            }
        }
        if (((YSpeed == 0.25D || (YSpeed >= 0.58D && YSpeed < 0.581D)) && yDist > 0.0D
                || (YSpeed > 0.2457 && YSpeed < 0.24582) || (YSpeed > 0.329 && YSpeed < 0.33))
                && !player.getLocation().clone().subtract(0.0D, 0.1, 0.0D).getBlock().getType().equals(Material.SNOW)) {
            getThotPatrol().logCheat(this, player,
                    "Speed: " + YSpeed + " Block: " + below + " | Ping: " + ping + " TPS: " + tps);
        	getThotPatrol().logToFile(player, this, "Speed", "Speed: " + YSpeed + " | Below: " + below
        			+ " | TPS: " + tps + " | Ping: " + ping);
        }
    }
}