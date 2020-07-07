package me.frep.thotpatrol.checks.movement.speed;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.data.DataPlayer;
import me.frep.thotpatrol.utils.UtilBlock;
import me.frep.thotpatrol.utils.UtilMath;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class SpeedE extends Check {

    public SpeedE(ThotPatrol ThotPatrol) {
        super("SpeedE", "Speed (Type E)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(6);
    }

    public static boolean flaggyStuffNear(Location loc) {
        boolean nearBlocks = false;
        for (Block bl : UtilBlock.getSurrounding(loc.getBlock(), true)) {
            if ((bl.getType().equals(Material.STEP)) || (bl.getType().equals(Material.DOUBLE_STEP))
                    || (bl.getType().equals(Material.BED)) || (bl.getType().equals(Material.WOOD_DOUBLE_STEP))
                    || (bl.getType().equals(Material.WOOD_STEP) || bl.getType().toString().contains("SLIME"))) {
                nearBlocks = true;
                break;
            }
        }
        for (Block bl : UtilBlock.getSurrounding(loc.getBlock(), false)) {
            if ((bl.getType().equals(Material.STEP)) || (bl.getType().equals(Material.DOUBLE_STEP))
                    || (bl.getType().equals(Material.BED)) || (bl.getType().equals(Material.WOOD_DOUBLE_STEP))
                    || (bl.getType().equals(Material.WOOD_STEP) || bl.getType().toString().contains("SLIME"))) {
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

	@EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent e) {
        Location from = e.getFrom().clone();
        Location to = e.getTo().clone();
        Player p = e.getPlayer();
        Location l = p.getLocation();
        int x = l.getBlockX();
        int y = l.getBlockY();
        int z = l.getBlockZ();
        Location loc2 = new Location(p.getWorld(), x, y + 1, z);
        Location above = new Location(p.getWorld(), x, y + 2, z);
        Location above3 = new Location(p.getWorld(), x - 1, y + 2, z - 1);
        Location blockLoc = new Location(p.getWorld(), x, y - 1, z);
        double onGroundDiff = (to.getY() - from.getY());
        if ((e.getTo().getX() == e.getFrom().getX()) && (e.getTo().getZ() == e.getFrom().getZ())
                && (e.getTo().getY() == e.getFrom().getY())
                || p.getNoDamageTicks() != 0
                || p.getVehicle() != null
                || p.hasPermission("thotpatrol.bypass")
                || p.getGameMode().equals(GameMode.CREATIVE)
                || p.getAllowFlight()) return;
		if (DataPlayer.lastNearSlime !=null) {
			if (DataPlayer.lastNearSlime.contains(p.getPlayer().getName().toString())) {
				return;
			}
		}
        for (Block b: UtilBlock.getNearbyBlocks(p.getLocation() ,3)) {
            if (b.getType().toString().contains("PISTON")) {
                return;
            }
        }
        double ig = 0.28;
        double speed = UtilMath.offset(getHV(to.toVector()), getHV(from.toVector()));
        if (p.hasPotionEffect(PotionEffectType.SPEED)) {
            int level = getPotionEffectLevel(p, PotionEffectType.SPEED);
            if (level > 0) {
                ig = (ig * (((level * 20) * 0.011) + 1));
            }
        }
        double tps = getThotPatrol().getLag().getTPS();
        double ping = getThotPatrol().getLag().getPing(p);
        if (speed > ig && !isAir(p) && onGroundDiff <= -0.4 && p.getFallDistance() <= 0.4
                && !flaggyStuffNear(p.getLocation()) && blockLoc.getBlock().getType() != Material.ICE
                && e.getTo().getY() != e.getFrom().getY() && blockLoc.getBlock().getType() != Material.PACKED_ICE
                && loc2.getBlock().getType() != Material.TRAP_DOOR && above.getBlock().getType() == Material.AIR
                && above3.getBlock().getType() == Material.AIR
                && getThotPatrol().getConfig().getBoolean("instantBans.SpeedE.enabled") && isBannable()
                && speed > getThotPatrol().getConfig().getDouble("instantBans.SpeedE.maxSpeed")
                && tps > getThotPatrol().getConfig().getDouble("instantBans.SpeedE.minTPS")
                && ping < getThotPatrol().getConfig().getDouble("instantBans.SpeedE.maxPing") && ping > 1
                && !getThotPatrol().getNamesBanned().containsKey(p.getName())
                && !getThotPatrol().NamesBanned.containsKey(p.getName())) {
            String banAlertMessage = getThotPatrol().getConfig().getString("instantBans.SpeedE.banAlertMessage");
            getThotPatrol().alert(ChatColor.translateAlternateColorCodes('&', banAlertMessage.replaceAll("%player%", p.getName())
                    .replaceAll("%speed%", Double.toString(Math.round(speed)))));
            dumplog(p, "[Instant Ban] Vanilla: " + speed + " | TPS: " + tps + " | Ping: " + ping);
            getThotPatrol().logToFile(p, this, "Vanilla [Instant Ban]", "Speed: " + speed + " | TPS: " + tps + " | Ping: " + ping);
            getThotPatrol().banPlayer(p, this);
        }
        if (speed > ig && !isAir(p) && onGroundDiff <= -0.4 && p.getFallDistance() <= 0.4
                && !flaggyStuffNear(p.getLocation()) && blockLoc.getBlock().getType() != Material.ICE
                && e.getTo().getY() != e.getFrom().getY() && blockLoc.getBlock().getType() != Material.PACKED_ICE
                && loc2.getBlock().getType() != Material.TRAP_DOOR && above.getBlock().getType() == Material.AIR
                && above3.getBlock().getType() == Material.AIR) {
        	getThotPatrol().logCheat(this, p, "Type: Vanilla | " + speed + " > " + ig + " | Ping: " + ping + " | TPS: " + tps);
        	getThotPatrol().logToFile(p, this, "Vanilla", "Speed: " + speed + " > " + ig
        			+ " | TPS: " + tps + " | Ping: " + ping);
        }
    }

    private int getPotionEffectLevel(Player p, PotionEffectType pet) {
        for (PotionEffect pe : p.getActivePotionEffects()) {
            if (pe.getType().getName().equals(pet.getName())) {
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
