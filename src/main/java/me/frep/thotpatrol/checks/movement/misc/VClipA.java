package me.frep.thotpatrol.checks.movement.misc;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.data.DataPlayer;
import me.frep.thotpatrol.events.SharedEvents;
import me.frep.thotpatrol.utils.UtilBlock;
import me.frep.thotpatrol.utils.UtilCheat;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class VClipA extends Check {

    public static List<Material> allowed = new ArrayList<>();
    public static ArrayList<UUID> teleported = new ArrayList<>();
    public static HashMap<Player, Location> lastLocation = new HashMap<>();

    static {
        allowed.add(Material.PISTON_EXTENSION);
        allowed.add(Material.PISTON_STICKY_BASE);
        allowed.add(Material.PISTON_BASE);
        allowed.add(Material.SIGN_POST);
        allowed.add(Material.WALL_SIGN);
        allowed.add(Material.STRING);
        allowed.add(Material.AIR);
        allowed.add(Material.FENCE_GATE);
        allowed.add(Material.CHEST);
        allowed.add(Material.PISTON_MOVING_PIECE);
    }

    public VClipA(ThotPatrol ThotPatrol) {
        super("VClipA", "VClip (Type A)", ThotPatrol);
        setBannable(true);
        setEnabled(true);
        setMaxViolations(6);
    }

    @SuppressWarnings("deprecation")
	@EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getCause() != TeleportCause.UNKNOWN) {
        	teleported.add(e.getPlayer().getUniqueId());
        	Bukkit.getScheduler().scheduleAsyncDelayedTask(ThotPatrol.Instance, new Runnable() {
        		@Override
        		public void run() {
        			teleported.remove(e.getPlayer().getUniqueId());
        		}
        	}, 100);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (p.hasPermission("thotpatrol.bypass")) return;
        Location to = e.getTo().clone();
        Location from = e.getFrom().clone();
        if (p.getWorld().getHighestBlockAt(p.getLocation()).getType().toString().contains("SLIME")) {
            return;
        }
        if (from.getY() == to.getY()
                || p.getAllowFlight()
                || p.getVehicle() != null
                || teleported.contains(p.getUniqueId())
                || e.getTo().getY() <= 0 || e.getTo().getY() >= p.getWorld().getMaxHeight()
                || !UtilCheat.blocksNear(p)
                || (p.getLocation().getY() < 0.0D)
                || (p.getLocation().getY() > p.getWorld().getMaxHeight())) return;
		double tps = getThotPatrol().getLag().getTPS();
		int ping = getThotPatrol().getLag().getPing(p);
        double yDist = from.getBlockY() - to.getBlockY();
        if (SharedEvents.worldChange.contains(p.getUniqueId())) {
        	return;        
        }
		for (Block b : UtilBlock.getNearbyBlocks(p.getLocation(), 5)) {
			if (b.getType().toString().contains("SLIME")) {
				return;
			}
		}
		if (DataPlayer.lastNearSlime !=null) {
			if (DataPlayer.lastNearSlime.contains(p.getPlayer().getName())) {
				return;
			}
		}
        for (double y = 0; y < Math.abs(yDist); y++) {
            Location l = yDist < -0.2 ? from.getBlock().getLocation().clone().add(0.0D, y, 0.0D) : to.getBlock().getLocation().clone().add(0.0D, y, 0.0D);
            if ((yDist > 20 || yDist < -20) && l.getBlock().getType() != Material.AIR
                    && l.getBlock().getType().isSolid() && !allowed.contains(l.getBlock().getType())) {
                getThotPatrol().logCheat(this, p, "Blocks " + yDist + " | Ping: " + ping + " | TPS: " + tps);
            	getThotPatrol().logToFile(p, this, "Distance", "Blocks: " + 
				yDist + " | TPS: " + tps + " | Ping: " + ping);
                p.teleport(lastLocation.get(p));
            }
            if (l.getBlock().getType() != Material.AIR && Math.abs(yDist) > 1.0 && l.getBlock().getType().isSolid()
                    && !allowed.contains(l.getBlock().getType())) {
            	getThotPatrol().logCheat(this, p, "Blocks " + y + " | Ping: " + ping + " | TPS: " + tps);
            	getThotPatrol().logToFile(p, this, "Distance", "Blocks: " + 
				yDist + " | TPS: " + tps + " | Ping: " + ping);
                p.teleport(lastLocation.get(p));
            } else {
                lastLocation.put(p, p.getLocation());
            }
        }
    }
}