package me.frep.thotpatrol.checks.player.scaffold;

import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.utils.UtilPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScaffoldB extends Check {

    private Map<UUID, Long> lastPlaced = new HashMap<>();
    private Map<UUID, Integer> lastBlockX = new HashMap<>();
    private Map<UUID, Integer> lastBlockZ = new HashMap<>();
    private Map<UUID, Integer> verbose = new HashMap<>();

    public ScaffoldB(me.frep.thotpatrol.ThotPatrol ThotPatrol) {
        super("ScaffoldB", "Scaffold (Type B) [#]", ThotPatrol);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(3);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (p.getAllowFlight()) {
            return;
        }
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        long time = System.currentTimeMillis();
        long lastPlace = lastPlaced.getOrDefault(p.getUniqueId(), time);
        long delta = time - lastPlace;
        int lastX = lastBlockX.getOrDefault(p.getUniqueId(), 0);
        int lastZ = lastBlockZ.getOrDefault(p.getUniqueId(), 0);
        //todo check to make sure black face is up
        if (p.getLocation().clone().subtract(0, 1, 0).getBlock().getType().equals(Material.AIR)
            || p.getLocation().getY() % 1 == 0) {
            return;
        }
        if (p.getLocation().getBlockX() == lastX && p.getLocation().getBlockZ() == lastZ && delta > 0) {
            if (delta < 400) {
                count++;
            } else {
                if (count > 0) {
                    count -= .5;
                }
            }
        }
        if (count >= 3) {
            count = 0;
            getThotPatrol().logCheat(this, p, null);
        }
        verbose.put(p.getUniqueId(), count);
        lastPlaced.put(p.getUniqueId(), time);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        int lastX = p.getLocation().getBlockX();
        int lastZ = p.getLocation().getBlockZ();
        if (!UtilPlayer.isOnGround(p)) {
            return;
        }
        lastBlockX.put(p.getUniqueId(), lastX);
        lastBlockZ.put(p.getUniqueId(), lastZ);
    }
}