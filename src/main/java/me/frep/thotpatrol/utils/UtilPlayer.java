package me.frep.thotpatrol.utils;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.data.DataPlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class UtilPlayer {

	private static ImmutableSet<Material> ground = Sets.immutableEnumSet(Material.SUGAR_CANE, Material.SUGAR_CANE_BLOCK,
			Material.TORCH, Material.ACTIVATOR_RAIL, Material.AIR, Material.CARROT, Material.CROPS, Material.DEAD_BUSH,
			Material.DETECTOR_RAIL, Material.DIODE_BLOCK_OFF, Material.DIODE_BLOCK_ON, Material.DOUBLE_PLANT,
			Material.FIRE, Material.GOLD_PLATE, Material.IRON_PLATE, Material.LAVA, Material.LEVER, Material.LONG_GRASS,
			Material.MELON_STEM, Material.NETHER_WARTS, Material.PORTAL, Material.POTATO, Material.POWERED_RAIL,
			Material.PUMPKIN_STEM, Material.RAILS, Material.RED_ROSE, Material.REDSTONE_COMPARATOR_OFF,
			Material.REDSTONE_COMPARATOR_ON, Material.REDSTONE_TORCH_OFF, Material.REDSTONE_TORCH_ON,
			Material.REDSTONE_WIRE, Material.SAPLING, Material.SEEDS, Material.SIGN, Material.SIGN_POST,
			Material.STATIONARY_LAVA, Material.STATIONARY_WATER, Material.STONE_BUTTON, Material.STONE_PLATE,
			Material.SUGAR_CANE_BLOCK, Material.TORCH, Material.TRIPWIRE, Material.TRIPWIRE_HOOK, Material.WALL_SIGN,
			Material.WATER, Material.WEB, Material.WOOD_BUTTON, Material.WOOD_PLATE, Material.YELLOW_FLOWER);

	public static boolean isIce(Block block) {
		return block.getType().equals(Material.ICE)
				|| block.getType().equals(Material.PACKED_ICE)
				|| block.getType().equals(Material.getMaterial("FROSTED_ICE"));
	}

	public static boolean isFlying2(PlayerMoveEvent e, Player p) {
		final Location loc = p.getLocation();
		loc.setY(loc.getY() - 1);

		final Block block = loc.getWorld().getBlockAt(loc);
		return block.getType().equals(Material.AIR);
	}

	@SuppressWarnings("deprecation")
	public static boolean wasOnSlime(Player player) {
		final DataPlayer user = ThotPatrol.getInstance().getDataManager().getData(player);
		final Location location = player.getLocation().clone().subtract(0.0D, 1.0D, 0.0D);

		return location.getBlock().getType().getId() == 165;
	}

	public static boolean isNearIce(Player p) {
		boolean out = false;
		for (final Block b : UtilBlock.getNearbyBlocks(p.getLocation(), 1)) {
			if (isIce(b)) {
				out = true;
			}
		}
		return out;
	}

	public static int getDistanceToGround(Player p) {
		final Location loc = p.getLocation().clone();
		final double y = loc.getBlockY();
		int distance = 0;
		for (double i = y; i >= 0; i--) {
			loc.setY(i);
			if (loc.getBlock().getType().isSolid()) {
				break;
			}
			distance++;
		}
		return distance;
	}

	public static boolean isOnSlime(Location loc) {
		final double diff = .3;
		return UtilBlock.isSlime(loc.clone().add(0, 0D, 0).getBlock())
				|| UtilBlock.isSlime(loc.clone().add(diff, 0D, 0).getBlock())
				|| UtilBlock.isSlime(loc.clone().add(-diff, 0D, 0).getBlock())
				|| UtilBlock.isSlime(loc.clone().add(0, 0D, diff).getBlock())
				|| UtilBlock.isSlime(loc.clone().add(0, 0D, -diff).getBlock())
				|| UtilBlock.isSlime(loc.clone().add(diff, 0D, diff).getBlock())
				|| UtilBlock.isSlime(loc.clone().add(diff, 0D, -diff).getBlock())
				|| UtilBlock.isSlime(loc.clone().add(-diff, 0D, diff).getBlock())
				|| UtilBlock.isSlime(loc.clone().add(-diff, 0D, -diff).getBlock());
	}

	public static boolean isNearWeb(Player p) {
		boolean out = false;
		for (final Block b : UtilBlock.getNearbyBlocks(p.getLocation(), 1)) {
			if (UtilBlock.isWeb(b)) {
				out = true;
			}
		}
		return out;
	}

	public static boolean isOnGround(PlayerMoveEvent e, Player p) {
		final Location loc = p.getLocation();
		loc.setY(loc.getY());
		final Block block = loc.getWorld().getBlockAt(loc);
		return !block.getType().equals(Material.AIR);
	}

	public static boolean isAir(Player player) {
		final Block b = player.getLocation().getBlock().getRelative(BlockFace.DOWN);
		return b.getType().equals(Material.AIR)
				&& b.getRelative(BlockFace.WEST).getType().equals(Material.AIR)
				&& b.getRelative(BlockFace.NORTH).getType().equals(Material.AIR)
				&& b.getRelative(BlockFace.EAST).getType().equals(Material.AIR)
				&& b.getRelative(BlockFace.SOUTH).getType().equals(Material.AIR);
	}

	public static boolean isFlying(PlayerMoveEvent e, Player p) {
		final Location loc = p.getLocation();
		loc.setY(loc.getY() - 2);

		final Block block = loc.getWorld().getBlockAt(loc);
		return block.getType().equals(Material.AIR);
	}

	public static boolean onGround2(Player p) {
		return p.getLocation().getBlock().getType() != Material.AIR;
	}

	public static boolean isNearPiston(Player p) {
		boolean out = false;
		for (final Block b : UtilBlock.getNearbyBlocks(p.getLocation(), 1)) {
			if (UtilBlock.isPiston(b)) {
				out = true;
			}
		}
		return out;
	}

	public static int getPotionEffectLevel(Player player, PotionEffectType pet) {
		for (final PotionEffect pe : player.getActivePotionEffects()) {
			if (pe.getType().getName().equals(pet.getName())) {
				return pe.getAmplifier() + 1;
			}
		}
		return 0;
	}

	public static boolean isNearSolid(Player p) {
		boolean out = false;
		for (final Block b : UtilBlock.getNearbyBlocks(p.getLocation(), 1)) {
			if (UtilBlock.isSolid(b)) {
				out = true;
			}
		}
		return out;
	}

	public static int hasDepthStrider(Player player) {
		if (player.getInventory().getBoots() != null && player.getInventory().getBoots().containsEnchantment(Enchantment.getByName("DEPTH_STRIDER"))) {
			return player.getInventory().getBoots().getEnchantments().get(Enchantment.getByName("DEPTH_STRIDER"));
		}
		return 0;
	}

	public static boolean isNearHalfBlock(Player p) {
		boolean out = false;
		for (final Block b : UtilBlock.getNearbyBlocks(p.getLocation(), 1)) {
			if (UtilBlock.isHalfBlock(b)) {
				out = true;
			}
		}
		return out;
	}

	public static Location getEyeLocation(Player player) {
		final Location eye = player.getLocation();
		eye.setY(eye.getY() + player.getEyeHeight());
		return eye;
	}

	public static boolean isInWater(Player player) {
		final Material m = player.getLocation().getBlock().getType();
		return m == Material.STATIONARY_WATER || m == Material.WATER;
	}

	public static boolean isNearSlime(Location loc) {
		boolean out = false;
		for (final Block b : UtilBlock.getNearbyBlocks(loc, 3)) {
			if (UtilBlock.isSlime(b)) {
				out = true;
			}
		}
		return out;
	}

	public static boolean isNearSlime(Player p) {
		boolean out = false;
		for (final Block b : UtilBlock.getNearbyBlocks(p.getLocation(), 1)) {
			if (UtilBlock.isSlime(b)) {
				out = true;
			}
		}
		return out;
	}

	public static boolean isOnClimbable(Player player, int blocks) {
		if (blocks == 0) {
			for (Block block : UtilBlock.getSurrounding(player.getLocation().getBlock(), false)) {
				if (block.getType() == Material.LADDER || block.getType() == Material.VINE) {
					return true;
				}
			}
		} else {
			for (Block block : UtilBlock.getSurrounding(player.getLocation().clone().add(0.0D, 1.0D, 0.0D).getBlock(),
					false)) {
				if (block.getType() == Material.LADDER || block.getType() == Material.VINE) {
					return true;
				}
			}
		}
		return player.getLocation().getBlock().getType() == Material.LADDER
				|| player.getLocation().getBlock().getType() == Material.VINE;
	}

	public static boolean isOnClimbable(Player player) {
		return UtilBlock.isClimbableBlock(player.getLocation().getBlock())
				|| UtilBlock.isClimbableBlock(player.getLocation().add(0, 1, 0).getBlock());
	}

	public static boolean isInAir(Player player) {
		for (Block block : UtilBlock.getSurrounding(player.getLocation().getBlock(), false)) {
			if (block.getType() == Material.AIR) {
				return true;
			}
		}
		return player.getLocation().getBlock().getType() == Material.AIR;
	}

	public static boolean isOnSlab(Location loc) {
		final double diff = .3;
		return UtilBlock.isSlab(loc.clone().add(0, 0D, 0).getBlock())
				|| UtilBlock.isSlab(loc.clone().add(diff, 0D, 0).getBlock())
				|| UtilBlock.isSlab(loc.clone().add(-diff, 0D, 0).getBlock())
				|| UtilBlock.isSlab(loc.clone().add(0, 0D, diff).getBlock())
				|| UtilBlock.isSlab(loc.clone().add(0, 0D, -diff).getBlock())
				|| UtilBlock.isSlab(loc.clone().add(diff, 0D, diff).getBlock())
				|| UtilBlock.isSlab(loc.clone().add(diff, 0D, -diff).getBlock())
				|| UtilBlock.isSlab(loc.clone().add(-diff, 0D, diff).getBlock())
				|| UtilBlock.isSlab(loc.clone().add(-diff, 0D, -diff).getBlock());
	}

	public static boolean isOnStair(Location loc) {
		final double diff = 0.3;
		return UtilBlock.isStair(loc.clone().add(0, 0D, 0).getBlock())
				|| UtilBlock.isStair(loc.clone().add(diff, 0D, 0).getBlock())
				|| UtilBlock.isStair(loc.clone().add(-diff, 0D, 0).getBlock())
				|| UtilBlock.isStair(loc.clone().add(0, 0D, diff).getBlock())
				|| UtilBlock.isStair(loc.clone().add(0, 0D, -diff).getBlock())
				|| UtilBlock.isStair(loc.clone().add(diff, 0D, diff).getBlock())
				|| UtilBlock.isStair(loc.clone().add(diff, 0D, -diff).getBlock())
				|| UtilBlock.isStair(loc.clone().add(-diff, 0D, diff).getBlock())
				|| UtilBlock.isStair(loc.clone().add(-diff, 0D, -diff).getBlock());
	}

	public static boolean isPartiallyStuck(Player player) {
		if (player.getLocation().clone().getBlock() == null) {
			return false;
		}
		Block block = player.getLocation().clone().getBlock();
		if (UtilCheat.isSlab(block) || UtilCheat.isStair(block)) {
			return false;
		}
		if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()
				|| player.getLocation().getBlock().getRelative(BlockFace.UP).getType().isSolid()) {
			return true;
		}
		if (player.getLocation().clone().add(0.0D, 1.0D, 0.0D).getBlock().getRelative(BlockFace.DOWN).getType()
				.isSolid()
				|| player.getLocation().clone().add(0.0D, 1.0D, 0.0D).getBlock().getRelative(BlockFace.UP).getType()
				.isSolid()) {
			return true;
		}
		return block.getType().isSolid();
	}

	public static boolean isBlock(Block block, Material[] materials) {
		final Material type = block.getType();
		Material[] arrayOfMaterial;
		final int j = (arrayOfMaterial = materials).length;
		for (int i = 0; i < j; i++) {
			final Material m = arrayOfMaterial[i];
			if (m == type) {
				return true;
			}
		}
		return false;
	}

	public static boolean isOnGround4(Player player) {
		if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) {
			return true;
		}
		Location a = player.getLocation().clone();
		a.setY(a.getY() - 0.5);
		if (a.getBlock().getType() != Material.AIR) {
			return true;
		}
		a = player.getLocation().clone();
		a.setY(a.getY() + 0.5);
		return a.getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR || isBlock(player.getLocation().getBlock().getRelative(BlockFace.DOWN),
				new Material[]{Material.FENCE, Material.FENCE_GATE, Material.COBBLE_WALL, Material.LADDER});
	}

	public static boolean isFullyStuck(Player player) {
		Block block1 = player.getLocation().clone().getBlock();
		Block block2 = player.getLocation().clone().add(0.0D, 1.0D, 0.0D).getBlock();
		if (block1.getType().isSolid() && block2.getType().isSolid()) {
			return true;
		}
		return block1.getRelative(BlockFace.DOWN).getType().isSolid()
				|| block1.getLocation().getBlock().getRelative(BlockFace.UP).getType().isSolid()
				&& block2.getRelative(BlockFace.DOWN).getType().isSolid()
				|| block2.getLocation().getBlock().getRelative(BlockFace.UP).getType().isSolid();
	}

	public static boolean isOnGround(Player player) {
		if (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) {
			return true;
		}
		Location a = player.getLocation().clone();
		a.setY(a.getY() - 0.5);
		if (a.getBlock().getType() != Material.AIR) {
			return true;
		}
		a = player.getLocation().clone();
		a.setY(a.getY() + 0.5);
		return a.getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR
				|| UtilCheat.isBlock(player.getLocation().getBlock().getRelative(BlockFace.DOWN),
				new Material[]{Material.FENCE, Material.FENCE_GATE, Material.COBBLE_WALL, Material.LADDER});
	}

	private static boolean isGround(Material material) {
		return ground.contains(material);
	}

	public static boolean isOnGround(Location loc) {
		final double diff = .3;

		return !isGround(loc.clone().add(0, -.1, 0).getBlock().getType())
				|| !isGround(loc.clone().add(diff, -.1, 0).getBlock().getType())
				|| !isGround(loc.clone().add(-diff, -.1, 0).getBlock().getType())
				|| !isGround(loc.clone().add(0, -.1, diff).getBlock().getType())
				|| !isGround(loc.clone().add(0, -.1, -diff).getBlock().getType())
				|| !isGround(loc.clone().add(diff, -.1, diff).getBlock().getType())
				|| !isGround(loc.clone().add(diff, -.1, -diff).getBlock().getType())
				|| !isGround(loc.clone().add(-diff, -.1, diff).getBlock().getType())
				|| !isGround(loc.clone().add(-diff, -.1, -diff).getBlock().getType())
				|| (UtilBlock.getBlockHeight(loc.clone().subtract(0.0D, 0.5D, 0.0D).getBlock()) != 0.0D &&
				(!isGround(loc.clone().add(diff, UtilBlock.getBlockHeight(loc.getBlock()) - 0.1, 0).getBlock().getType())
						|| !isGround(loc.clone().add(-diff, UtilBlock.getBlockHeight(loc.getBlock()) - 0.1, 0).getBlock().getType())
						|| !isGround(loc.clone().add(0, UtilBlock.getBlockHeight(loc.getBlock()) - 0.1, diff).getBlock().getType())
						|| !isGround(loc.clone().add(0, UtilBlock.getBlockHeight(loc.getBlock()) - 0.1, -diff).getBlock().getType())
						|| !isGround(loc.clone().add(diff, UtilBlock.getBlockHeight(loc.getBlock()) - 0.1, diff).getBlock().getType())
						|| !isGround(loc.clone().add(diff, UtilBlock.getBlockHeight(loc.getBlock()) - 0.1, -diff).getBlock().getType())
						|| !isGround(loc.clone().add(-diff, UtilBlock.getBlockHeight(loc.getBlock()) - 0.1, diff).getBlock().getType())
						|| !isGround(loc.clone().add(-diff, UtilBlock.getBlockHeight(loc.getBlock()) - 0.1, -diff).getBlock().getType())));
	}

	public static boolean isInWater(Location loc) {
		final double diff = .3;
		return UtilBlock.isLiquid(loc.clone().add(0, 0D, 0).getBlock())
				|| UtilBlock.isLiquid(loc.clone().add(diff, 0D, 0).getBlock())
				|| UtilBlock.isLiquid(loc.clone().add(-diff, 0D, 0).getBlock())
				|| UtilBlock.isLiquid(loc.clone().add(0, 0D, diff).getBlock())
				|| UtilBlock.isLiquid(loc.clone().add(0, 0D, -diff).getBlock())
				|| UtilBlock.isLiquid(loc.clone().add(diff, 0D, diff).getBlock())
				|| UtilBlock.isLiquid(loc.clone().add(diff, 0D, -diff).getBlock())
				|| UtilBlock.isLiquid(loc.clone().add(-diff, 0D, diff).getBlock())
				|| UtilBlock.isLiquid(loc.clone().add(-diff, 0D, -diff).getBlock())
				|| (UtilBlock.getBlockHeight(loc.clone().subtract(0.0D, 0.5D, 0.0D).getBlock()) != 0.0D &&
				(UtilBlock.isLiquid(loc.clone().add(diff, 0D, 0).getBlock())
						|| UtilBlock.isLiquid(loc.clone().add(-diff, 0D, 0).getBlock())
						|| UtilBlock.isLiquid(loc.clone().add(0, 0D, diff).getBlock())
						|| UtilBlock.isLiquid(loc.clone().add(0, 0D, -diff).getBlock())
						|| UtilBlock.isLiquid(loc.clone().add(diff, 0D, diff).getBlock())
						|| UtilBlock.isLiquid(loc.clone().add(diff, 0D, -diff).getBlock())
						|| UtilBlock.isLiquid(loc.clone().add(-diff, 0D, diff).getBlock())
						|| UtilBlock.isLiquid(loc.clone().add(-diff, 0D, -diff).getBlock())));
	}
}