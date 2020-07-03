package me.frep.thotpatrol.checks.combat.misc;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;

public class FastBowA extends Check {
	
    public static Map<Player, Long> bowPull = new HashMap<>();
    public static Map<Player, Integer> count = new HashMap<>();

    public FastBowA(ThotPatrol ThotPatrol) {
        super("FastBow", "Fast Bow (Type A)", ThotPatrol);
        setMaxViolations(3);
        setEnabled(true);
        setBannable(true);
        bowPull = new HashMap<>();
        count = new HashMap<>();
    }

    @EventHandler
    public void Interact(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if (p.getItemInHand() != null && p.getItemInHand().getType().equals(Material.BOW)) {
            bowPull.put(p, System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onLogout(PlayerQuitEvent e) {
        bowPull.remove(e.getPlayer());
        count.remove(e.getPlayer());
    }

    @EventHandler
    public void onShoot(final ProjectileLaunchEvent e) {
        if (!this.isEnabled()) return;
        if (e.getEntity() instanceof Arrow) {
            Arrow arrow = (Arrow) e.getEntity();
            if (arrow.getShooter() != null && arrow.getShooter() instanceof Player) {
                Player player = (Player) arrow.getShooter();
                if (player.hasPermission("thotpatrol.bypass")) {
                    return;
                }
                if (bowPull.containsKey(player)) {
                    Long time = System.currentTimeMillis() - bowPull.get(player);
                    double power = arrow.getVelocity().length();
                    int ping = getThotPatrol().getLag().getPing(player);
                    double tps = getThotPatrol().getLag().getTPS();
                    Long timeLimit = 300L;
                    int Count = 0;
                    if (count.containsKey(player)) {
                        Count = count.get(player);
                    }
                    if (power > 2.5 && time < timeLimit) {
                        count.put(player, Count + 1);
                        getThotPatrol().verbose(this, player, ping, tps, power + " > 2.5");
                    } else {
                        count.put(player, Count > 0 ? Count - 1 : Count);
                    }
                    if (Count > 8) {
                    	getThotPatrol().logCheat(this, player, time + " ms | Ping:" + ping + " | TPS: " + tps);
                    	getThotPatrol().logToFile(player, this, "Illegal Time", "TPS: " + tps + " | Ping: " + ping);
                        count.remove(player);
                    }
                }
            }
        }
    }
}