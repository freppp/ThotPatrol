package me.frep.thotpatrol.checks.movement.jesus;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.checks.movement.speed.SpeedI;
import me.frep.thotpatrol.utils.UtilBlock;
import me.frep.thotpatrol.utils.UtilCheat;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class JesusA extends Check {
	
    public static Map<Player, Integer> onWater;
    public static List<Player> placedBlockOnWater;
    public static Map<Player, Integer> count;
    public static Map<Player, Long> velocity;
    private List<UUID> fuckingSwimming;
	
    public JesusA(ThotPatrol ThotPatrol) {
        super("Jesus", "Jesus (Type A)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(6);
        count = new WeakHashMap<>();
        placedBlockOnWater = new ArrayList<>();
        onWater = new WeakHashMap<>();
        velocity = new WeakHashMap<>();
        fuckingSwimming = new ArrayList<>();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLeave(PlayerQuitEvent e) {
        placedBlockOnWater.remove(e.getPlayer());
        onWater.remove(e.getPlayer());
        count.remove(e.getPlayer());
        velocity.remove(e.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent e) {
        onWater.remove(e.getEntity());
        placedBlockOnWater.remove(e.getEntity());
        count.remove(e.getEntity());
    }

    @EventHandler
    public void OnPlace(BlockPlaceEvent e) {
        if (e.getBlockReplacedState().getBlock().getType() == Material.WATER) {
            placedBlockOnWater.add(e.getPlayer());
        }
    }

    @EventHandler
    public void CheckJesus(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        double yDiff = Math.abs(event.getFrom().getY() - event.getTo().getY());
        if (event.isCancelled()
                || (event.getFrom().getX() == event.getTo().getX()) && (event.getFrom().getZ() == event.getTo().getZ())
                || p.getAllowFlight()
                || p.getVehicle() != null
                || p.hasPermission("thotpatrol.bypass")
                || UtilCheat.isOnLilyPad(p)
                || !UtilTime.elapsed(SpeedI.bowBoost.getOrDefault(p.getUniqueId(), 0L), 2000)
                || p.getLocation().clone().add(0.0D, 0.4D, 0.0D).getBlock().getType().isSolid()
                || placedBlockOnWater.remove(p)
                || p.isDead()
                || fuckingSwimming.contains(p.getUniqueId())) return;
        //todo fix this
        if (yDiff > .04) {
            fuckingSwimming.add(p.getUniqueId());
            Bukkit.getScheduler().scheduleAsyncDelayedTask(ThotPatrol.Instance, new Runnable() {
                @Override
                public void run() {
                    fuckingSwimming.remove(p.getUniqueId());
                }
            }, 20);
        }
        for (Block b : UtilBlock.getNearbyBlocks(p.getLocation(), 1)) {
            if (b.getType().equals(Material.WATER_LILY)) {
                return;
            }
        }
        int Count = count.getOrDefault(p, 0);
        if ((UtilCheat.cantStandAtWater(p.getWorld().getBlockAt(p.getLocation()))) && !p.isSneaking() //todo <-------
                && (UtilCheat.isHoveringOverWater(p.getLocation())) && (!UtilCheat.isFullyInWater(p.getLocation()))) {
            Count += 2;
        }
        for (Block b : UtilBlock.getNearbyBlocks(p.getLocation(), 2)) {
            if (b.getType().equals(Material.CARPET) || b.getType().toString().contains("SNOW")) {
                return;
            }
        }
        for (Entity e : p.getNearbyEntities(1.5, 1.5, 1.5)) {
            if (e instanceof Boat) {
                return;
            }
        }
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
        if (Count > 9) {
            Count = 0;
            getThotPatrol().logCheat(this, p, "Ping: " + ping + " | TPS: " + tps);
            getThotPatrol().logToFile(p, this, "Water", "TPS: " + tps + " | Ping: " + ping);
        }
        count.put(p, Count);
    }
}