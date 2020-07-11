package me.frep.thotpatrol.checks.movement.nofall;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.utils.UtilPlayer;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.GameMode;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.*;

public class NoFallA extends Check {
	
    public static Map<UUID, Map.Entry<Long, Integer>> NoFallTicks;
    public static Map<UUID, Double> FallDistance;
    public static ArrayList<Player> cancel;

    public NoFallA(ThotPatrol ThotPatrol) {
        super("NoFallA", "No Fall (Type A)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(2);
        NoFallTicks = new HashMap<>();
        FallDistance = new HashMap<>();
        cancel = new ArrayList<>();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        cancel.add(e.getEntity());
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent e) {
    	UUID uuid = e.getPlayer().getUniqueId();
        FallDistance.remove(uuid);
        if (FallDistance.containsKey(uuid)) {
            FallDistance.containsKey(uuid);
        }
        cancel.remove(e.getPlayer());
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getCause() == TeleportCause.ENDER_PEARL) {
            cancel.add(e.getPlayer());
        }
    }

    @SuppressWarnings("deprecation")
	@EventHandler
    public void Move(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (player.getAllowFlight()
                || player.getGameMode().equals(GameMode.CREATIVE)
                || player.hasPermission("thotpatrol.bypass")
                || player.getVehicle() != null
                || cancel.remove(player)
                || UtilPlayer.isOnClimbable(player, 0)
                || UtilPlayer.isInWater(player)) return;
        Damageable dplayer = e.getPlayer();
        UUID uuid = dplayer.getUniqueId();
        if (dplayer.getHealth() <= 0.0D) return;
        if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().toString().contains("leaves")) {
        	return;
        }
        double Falling = 0.0D;
        if ((!UtilPlayer.isOnGround(player)) && (e.getFrom().getY() > e.getTo().getY())) {
            if (FallDistance.containsKey(uuid)) {
                Falling = FallDistance.get(uuid);
            }
            Falling += e.getFrom().getY() - e.getTo().getY();
        }
        FallDistance.put(uuid, Falling);
        if (Falling < 3.0D) return;
        long Time = System.currentTimeMillis();
        int Count = 0;
        if (NoFallTicks.containsKey(uuid)) {
            Time = NoFallTicks.get(uuid).getKey();
            Count = NoFallTicks.get(uuid).getValue();
        }
        if ((player.isOnGround()) || (player.getFallDistance() == 0.0F)) {
            dumplog(player, "NoFall. Real Fall Distance: " + Falling);
            Count += 2;
        } else {
            Count--;
        }
        if (NoFallTicks.containsKey(uuid) && UtilTime.elapsed(Time, 10000L)) {
            Count = 0;
            Time = System.currentTimeMillis();
        }
		double tps = getThotPatrol().getLag().getTPS();
		int ping = getThotPatrol().getLag().getPing(player);
        if (Count >= 4) {
            dumplog(player, "Logged. Count: " + Count);
            Count = 0;
            FallDistance.put(uuid, 0.0D);
            getThotPatrol().logCheat(this, player, "Packet NoFall | Ping: " + ping + " | TPS: " + tps);
        	getThotPatrol().logToFile(player, this, "Packet", "TPS: " + tps + " | Ping: " + ping);
        }
        NoFallTicks.put(uuid, new AbstractMap.SimpleEntry<>(Time, Count));
        return;
    }
}