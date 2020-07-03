package me.frep.thotpatrol.checks.movement.speed;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.utils.UtilBlock;
import me.frep.thotpatrol.utils.UtilMath;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class SpeedB extends Check {

    public Map<UUID, Map.Entry<Integer, Long>> speedTicks;
    public Map<UUID, Map.Entry<Integer, Long>> tooFastTicks;
    public Map<UUID, Long> lastHit;
    public List<UUID> jumpingOnIce = new ArrayList<>();

    public SpeedB(ThotPatrol ThotPatrol) {
        super("SpeedB", "Speed (Type B)", ThotPatrol);
        setEnabled(true);
        setMaxViolations(8);
        setBannable(true);
        lastHit = new HashMap<>();
        tooFastTicks = new HashMap<>();
        speedTicks = new HashMap<>();
    }

    @SuppressWarnings("deprecation")
	public static boolean isReallyOnGround(Player p) {
        Location l = p.getLocation();
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();
        Location b = new Location(p.getWorld(), x, y - 1, z);
        return p.isOnGround() && b.getBlock().getType() != Material.AIR && b.getBlock().getType() != Material.WEB
                && !b.getBlock().isLiquid();
    }
    public static boolean flaggyStuffNear(Location loc) {
        boolean nearBlocks = false;
        for (Block bl : UtilBlock.getSurrounding(loc.getBlock(), true)) {
            if ((bl.getType().equals(Material.STEP)) || (bl.getType().equals(Material.DOUBLE_STEP))
                    || (bl.getType().equals(Material.BED)) || (bl.getType().equals(Material.WOOD_DOUBLE_STEP))
                    || (bl.getType().equals(Material.WOOD_STEP))) {
                nearBlocks = true;
                break;
            }
        }
        for (Block bl : UtilBlock.getSurrounding(loc.getBlock(), false)) {
            if ((bl.getType().equals(Material.STEP)) || (bl.getType().equals(Material.DOUBLE_STEP))
                    || (bl.getType().equals(Material.BED)) || (bl.getType().equals(Material.WOOD_DOUBLE_STEP))
                    || (bl.getType().equals(Material.WOOD_STEP))) {
                nearBlocks = true;
                break;
            }
        }
        if (isBlock(loc.getBlock().getRelative(BlockFace.DOWN), new Material[]{Material.STEP, Material.BED,
                Material.DOUBLE_STEP, Material.WOOD_DOUBLE_STEP, Material.WOOD_STEP})) {
            nearBlocks = true;
        }
        return nearBlocks;
    }

    public static boolean isBlock(Block block, Material[] materials) {
        Material type = block.getType();
        Material[] arrayOfMaterial;
        int j = (arrayOfMaterial = materials).length;
        for (int i = 0; i < j; i++) {
            Material m = arrayOfMaterial[i];
            if (m == type) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAir(final Player player) {
        final Block b = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
        return b.getType().equals(Material.AIR)
                && b.getRelative(BlockFace.WEST).getType().equals(Material.AIR)
                && b.getRelative(BlockFace.NORTH).getType().equals(Material.AIR)
                && b.getRelative(BlockFace.EAST).getType().equals(Material.AIR)
                && b.getRelative(BlockFace.SOUTH).getType().equals(Material.AIR);
    }

    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Location from = e.getFrom().clone();
        Location to = e.getTo().clone();
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
		if (p.hasPermission("thotpatrol.bypass")) {
			return;
		}
        Location l = p.getLocation();
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();
        Location blockLoc = new Location(p.getWorld(), x, y - 1, z);
        Location loc2 = new Location(p.getWorld(), x, y + 1, z);
        Location above = new Location(p.getWorld(), x, y + 2, z);
        Location above3 = new Location(p.getWorld(), x - 1, y + 2, z - 1);
        long lastHitDiff = Math.abs(System.currentTimeMillis() - lastHit.getOrDefault(p.getUniqueId(), 0L));
        if ((e.getTo().getX() == e.getFrom().getX()) && (e.getTo().getZ() == e.getFrom().getZ())
                && (e.getTo().getY() == e.getFrom().getY())
                || lastHitDiff < 1500L
                || p.getNoDamageTicks() != 0
                || p.getVehicle() != null
                || p.getGameMode().equals(GameMode.CREATIVE)
                || p.getAllowFlight()) return;
        double maxSpeed = 0.42;
        double speed = UtilMath.offset(getHV(to.toVector()), getHV(from.toVector()));
        if (p.hasPotionEffect(PotionEffectType.SPEED)) {
            int level = getPotionEffectLevel(p);
            if (level > 0) {
                maxSpeed = (maxSpeed * (((level * 20) * 0.015) + 1));
            }
        }
        Material below = p.getLocation().subtract(0, 1.5, 0).getBlock().getType();
        Material below2 = p.getLocation().subtract(0, 1, 0).getBlock().getType();
        if (below.equals(Material.ICE) || below.equals(Material.PACKED_ICE) 
        		|| below2.equals(Material.ICE) || below2.equals(Material.PACKED_ICE)) {
        	jumpingOnIce.add(p.getUniqueId());
        	Bukkit.getScheduler().scheduleAsyncDelayedTask(ThotPatrol.Instance, new Runnable() {
        		@Override
        		public void run() {
        			jumpingOnIce.remove(uuid);
        		}
        	}, 40);
        }
        if (jumpingOnIce.contains(uuid)) {
        	return;
        }
        // todo fix this fucking mess
        if (blockLoc.getBlock().getType().toString().contains("SLAB")
        		|| blockLoc.getBlock().getType().toString().contains("STEP")
        		|| blockLoc.getBlock().getType().toString().contains("STAIRS")) {
        	return;
        }
        for (Block b : UtilBlock.getNearbyBlocks(p.getLocation(), 2)) {
            if (b.getType().toString().contains("SLAB") || b.getType().toString().contains("STEP")
                || b.getType().toString().contains("DOOR")) {
                return;
            }
        }
		double tps = getThotPatrol().getLag().getTPS();
		double ping = getThotPatrol().getLag().getPing(p);
        maxSpeed += p.getWalkSpeed() > 0.2 ? p.getWalkSpeed() * 1.5 : 0;
        if (isReallyOnGround(p) && to.getY() == from.getY()) {
            if (speed >= maxSpeed && p.isOnGround() && p.getFallDistance() < 0.15
                    && blockLoc.getBlock().getType() != Material.ICE
                    && blockLoc.getBlock().getType() != Material.PACKED_ICE
                    && loc2.getBlock().getType() != Material.TRAP_DOOR && above.getBlock().getType() == Material.AIR
                    && above3.getBlock().getType() == Material.AIR
                    && ping > 1) {
            	getThotPatrol().logCheat(this, p, "Type: Ground | " + speed + " > " + maxSpeed + " | Ping: " + ping + " TPS: " + tps);
            	getThotPatrol().logToFile(p, this, "Ground", "Speed: " + speed + " > " + maxSpeed
            	+ " | TPS: " + tps + " | Ping: " + ping);
            } 
            if (getThotPatrol().getConfig().getBoolean("instantBans.SpeedB.enabled")
            		&& isBannable()
            		&& speed > getThotPatrol().getConfig().getDouble("instantBans.SpeedB.maxSpeed") 
            		&& tps > getThotPatrol().getConfig().getDouble("instantBans.SpeedB.minTPS")
            		&& ping < getThotPatrol().getConfig().getDouble("instantBans.SpeedB.maxPing")
            		&& ping > 1
            		&& !getThotPatrol().getNamesBanned().containsKey(p.getName())
            		&& p.isOnGround() && p.getFallDistance() < 0.15
                    && blockLoc.getBlock().getType() != Material.ICE
                    && blockLoc.getBlock().getType() != Material.PACKED_ICE
                    && loc2.getBlock().getType() != Material.TRAP_DOOR && above.getBlock().getType() == Material.AIR
                    && above3.getBlock().getType() == Material.AIR) {
                getThotPatrol().banPlayer(p, this);
            	String banAlertMessage = getThotPatrol().getConfig().getString("instantBans.SpeedB.banAlertMessage");
            	getThotPatrol().alert(ChatColor.translateAlternateColorCodes('&', banAlertMessage.replaceAll("%player%", p.getName())
            			.replaceAll("%speed%", Double.toString(Math.round(speed)))));
            	dumplog(p, "[Instant Ban] Ground Speed: " + speed + " | TPS: " + tps + " | Ping: " + ping);
            	getThotPatrol().logToFile(p, this, "Ground [Instant Ban]", "Speed: " + speed + " | TPS: " + tps + " | Ping: " + ping);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent e) {
        speedTicks.remove(e.getPlayer().getUniqueId());
        tooFastTicks.remove(e.getPlayer().getUniqueId());
        lastHit.remove(e.getPlayer().getUniqueId());
    }

    private int getPotionEffectLevel(Player p) {
        for (PotionEffect pe : p.getActivePotionEffects()) {
            if (pe.getType().getName().equals(PotionEffectType.SPEED.getName())) {
                return pe.getAmplifier() + 1;
            }
        }
        return 0;
    }

    private Vector getHV(Vector V) {
        V.setY(0);
        return V;
    }
}