package me.frep.thotpatrol.checks.movement.ascension;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.data.DataPlayer;
import me.frep.thotpatrol.utils.UtilBlock;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AscensionD extends Check {

    public static Map<UUID, Long> explosionTicks = new HashMap<>();

    public AscensionD(ThotPatrol ThotPatrol) {
        super("AscensionD", "Ascension (Type D)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(5);
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
                || e.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
                Player p = (Player)e.getEntity();
                explosionTicks.put(p.getUniqueId(), System.currentTimeMillis());
            }
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        double yDiff = e.getTo().getY() - e.getFrom().getY();
        if (p.getWorld().getHighestBlockAt(p.getLocation()).getType().toString().contains("SLIME")
            || p.hasPermission("thotpatrol.bypass")) {
            return;
        }
        if (!UtilTime.elapsed(explosionTicks.getOrDefault(p.getUniqueId(), 0L), 2500)) {
            return;
        }
        if (e.getTo().getBlock().getType().toString().contains("SLIME")
                || e.getTo().getBlock().getRelative(BlockFace.DOWN).getType().toString().contains("SLIME")) {
            return;
        }
        for (Block b : UtilBlock.getNearbyBlocks(p.getLocation(), 2)) {
            if (b.getType().toString().contains("SLIME") || b.getType().toString().contains("PISTON") || b.getType().toString().contains("BED")
                || b.getType().toString().contains("STAIR")) {
                return;
            }
        }
        if (DataPlayer.lastNearSlime != null) {
            if (DataPlayer.lastNearSlime.contains(p.getPlayer().getName().toString())) {
                return;
            }
        }
        if (p.getAllowFlight()
                || p.isFlying()
                || !inAir(p)) {
            return;
        }
        double tps = getThotPatrol().getLag().getTPS();
        int ping = getThotPatrol().getLag().getPing(p);
        double maxDist = .55;
        for (PotionEffect eff : p.getActivePotionEffects()) {
            if (eff.getType().equals(PotionEffectType.JUMP)) {
                maxDist += (eff.getAmplifier() + 1) + .1;
            }
        }
        if (yDiff > maxDist) {
            getThotPatrol().logCheat(this, p, "Invalid Y | Y-Diff: " + yDiff+ " | Ping: " + ping + " | TPS: " + tps);
            getThotPatrol().logToFile(p, this, "Multi", "Y-Dist: " + yDiff + " | TPS: " + tps + " | Ping: " + ping);
        }
        if (yDiff > getThotPatrol().getConfig().getDouble("instantBans.AscensionD.maxHeight")
                && getThotPatrol().getConfig().getBoolean("instantBans.AscensionD.enabled")
                && isBannable()
                && !getThotPatrol().NamesBanned.containsKey(p.getName())
                && !getThotPatrol().getNamesBanned().containsKey(p.getName())
                && tps > getThotPatrol().getConfig().getDouble("instantBans.AscensionD.minTPS")
                && ping < getThotPatrol().getConfig().getInt("instantBans.AscensionD.maxPing")) {
            getThotPatrol().banPlayer(p, this);
            String banAlertMessage = getThotPatrol().getConfig().getString("instantBans.AscensionD.banAlertMessage");
            getThotPatrol().alert(ChatColor.translateAlternateColorCodes('&', banAlertMessage.replaceAll("%player%", p.getName())
                    .replaceAll("%height%", Double.toString(yDiff))));
            dumplog(p, "[Instant Ban] Y-Diff: " + yDiff + " | TPS: " + tps + " | Ping: " + ping);
            getThotPatrol().logToFile(p, this, "Invalid deltaY [Instant Ban]", "Y-Diff: " + yDiff + " | TPS: " + tps + " | Ping: " + ping);
        }
    }

    private boolean inAir(Player p) {
        for (Block b : UtilBlock.getNearbyBlocks(p.getLocation(), 2)) {
            if (b.getType().equals(Material.AIR)) {
                return true;
            }
        }
        return false;
    }
}