package me.frep.thotpatrol.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class UtilBlock {

	public static HashSet<Byte> blockPassSet = new HashSet<Byte>();
	public static HashSet<Byte> blockAirFoliageSet = new HashSet<Byte>();
	public static HashSet<Byte> fullSolid = new HashSet<Byte>();
	public static HashSet<Byte> blockUseSet = new HashSet<Byte>();

	static String[] HalfBlocksArray = {"pot", "flower", "step", "slab", "snow", "detector", "daylight",
			"comparator", "repeater", "diode", "water", "lava", "ladder", "vine", "carpet", "sign", "pressure", "plate",
			"button", "mushroom", "torch", "frame", "armor", "banner", "lever", "hook", "redstone", "rail", "brewing",
			"rose", "skull", "enchantment", "cake", "bed"};

	public static List<Block> getNearbyBlocks(Location location, int radius) {
		final List<Block> blocks = new ArrayList<>();
		for (int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
			for (int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
				for (int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
					blocks.add(location.getWorld().getBlockAt(x, y, z));
				}
			}
		}
		return blocks;
	}

	public static boolean containsBlockType(Material[] arrmaterial, Block block) {
		final int n = arrmaterial.length;
		int n2 = 0;
		while (n2 < n) {
			final Material material = arrmaterial[n2];
			if (material == block.getType()) {
				return true;
			}
			++n2;
		}
		return false;
	}

	public static Block getBlockBehindPlayer(Player player) {
		if (player == null) {
			return null;
		}
		final Location location = player.getLocation().add(0, 1, 0);
		final Vector direction = location.getDirection().multiply(new Vector(-1, 0, -1));
		return player.getWorld().getBlockAt(location.add(direction));
	}

	public static boolean isPiston(Block block) {
		return block.getType().equals(Material.PISTON_MOVING_PIECE)
				|| block.getType().equals(Material.PISTON_EXTENSION)
				|| block.getType().equals(Material.PISTON_BASE)
				|| block.getType().equals(Material.PISTON_STICKY_BASE);
	}

	public static boolean isWeb(Block block) {
		return block.getType().equals(Material.WEB);
	}

	public static boolean isIce(Block block) {
		return block.getType().equals(Material.ICE)
				|| block.getType().equals(Material.PACKED_ICE)
				|| block.getType().equals(Material.getMaterial("FROSTED_ICE"));
	}

	@SuppressWarnings("deprecation")
	public static boolean isSign(Block block) {
		return block.getType().getId() == 63
				|| block.getType().getId() == 68
				|| block.getType().getId() == 323;
	}


	public static boolean isAir(Block block) {
		return block.getType().equals(Material.AIR);
	}

	public static boolean isNearStair(Player p) {
		boolean out = false;
		for (final Block b : getNearbyBlocks(p.getLocation(), 1)) {
			if (isStair(b)) {
				out = true;
			}
		}
		return out;
	}

	@SuppressWarnings("deprecation")
	public static boolean isSlime(Block block) {
		return block.getType().getId() == 165;
	}

	public static boolean isNearPiston(Player p) {
		boolean out = false;
		for (final Block b : getNearbyBlocks(p.getLocation(), 1)) {
			if (b.getType() == Material.PISTON_BASE || b.getType() == Material.PISTON_MOVING_PIECE || b.getType() == Material.PISTON_STICKY_BASE || b.getType() == Material.PISTON_EXTENSION) {
				out = true;
			}
		}
		return out;
	}

	public static boolean isHalfBlock(Block block) {
		final Material type = block.getType();
		for (final String types : HalfBlocksArray) {
			if (type.toString().toLowerCase().contains(types)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isChest(Block block) {
		return block.getType().equals(Material.CHEST) || block.getType().equals(Material.ENDER_CHEST) || block.getType().equals(Material.TRAPPED_CHEST);

	}

	public static boolean isNearLiquid(Player p) {
		boolean out = false;
		for (final Block b : getNearbyBlocks(p.getLocation(), 1)) {
			if (isLiquid(b)) {
				out = true;
			}
		}
		return out;
	}

	public static boolean isNearFence(Player p) {
		boolean out = false;
		for (final Block b : getNearbyBlocks(p.getLocation(), 1)) {
			if (isFence(b)) {
				out = true;
			}
		}
		return out;
	}

	public static double getBlockHeight(Block block) {
		if (isSlab(block) || isStair(block)) {
			return 0.5;
		}
		if (isFence(block)) {
			return 0.5;
		}
		if (isChest(block)) {
			return 0.125;
		}
		return 0;
	}

	@SuppressWarnings("deprecation")
	public static boolean isFence(Block block) {
		return block.getType().equals(Material.FENCE)
				|| block.getType().getId() == 85
				|| block.getType().getId() == 139
				|| block.getType().getId() == 113
				|| block.getType().getId() == 188
				|| block.getType().getId() == 189
				|| block.getType().getId() == 190
				|| block.getType().getId() == 191
				|| block.getType().getId() == 192
				|| block.getType().equals(Material.NETHER_FENCE);

	}

	public static Block getLowestBlockAt(Location Location) {
		Block Block = Location.getWorld().getBlockAt((int) Location.getX(), 0, (int) Location.getZ());
		if ((Block == null) || (Block.getType().equals(Material.AIR))) {
			Block = Location.getBlock();
			for (int y = (int) Location.getY(); y > 0; y--) {
				Block Current = Location.getWorld().getBlockAt((int) Location.getX(), y, (int) Location.getZ());
				Block Below = Current.getLocation().subtract(0.0D, 1.0D, 0.0D).getBlock();
				if ((Below == null) || (Below.getType().equals(Material.AIR))) {
					Block = Current;
				}
			}
		}
		return Block;
	}

	public static boolean isClimbableBlock(Block block) {
		return block.getType() == Material.LADDER || block.getType() == Material.VINE;
	}

	public static boolean isLiquid(Block block) {
		return block != null && (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER
				|| block.getType() == Material.LAVA || block.getType() == Material.STATIONARY_LAVA);
	}

	@SuppressWarnings("deprecation")
	public static boolean isSlab(Block block) {
		return block.getType().equals(Material.WOOD_STEP)
				|| block.getType().equals(Material.STEP)
				|| block.getType().equals(Material.getMaterial("PRISMARINE_BRICK_SLAB"))
				|| block.getType().equals(Material.getMaterial("PRISMARINE_SLAB"))
				|| block.getType().equals(Material.getMaterial("DARK_PRISMARINE_SLAB"))
				|| block.getType().getId() == 182
				|| block.getType().getId() == 44
				|| block.getType().getId() == 126
				|| block.getType().getId() == 205;
	}

	@SuppressWarnings("deprecation")
	public static boolean isSolid(Block material) {
		return material != null && isSolid(material.getTypeId());
	}

	public static boolean isSolid(int block) {
		return isSolid((byte) block);
	}

	public static boolean isSolid(final byte block) {
		if (UtilBlock.blockPassSet.isEmpty()) {
			UtilBlock.blockPassSet.add((byte) 0);
			UtilBlock.blockPassSet.add((byte) 6);
			UtilBlock.blockPassSet.add((byte) 8);
			UtilBlock.blockPassSet.add((byte) 9);
			UtilBlock.blockPassSet.add((byte) 10);
			UtilBlock.blockPassSet.add((byte) 11);
			UtilBlock.blockPassSet.add((byte) 27);
			UtilBlock.blockPassSet.add((byte) 28);
			UtilBlock.blockPassSet.add((byte) 30);
			UtilBlock.blockPassSet.add((byte) 31);
			UtilBlock.blockPassSet.add((byte) 32);
			UtilBlock.blockPassSet.add((byte) 37);
			UtilBlock.blockPassSet.add((byte) 38);
			UtilBlock.blockPassSet.add((byte) 39);
			UtilBlock.blockPassSet.add((byte) 40);
			UtilBlock.blockPassSet.add((byte) 50);
			UtilBlock.blockPassSet.add((byte) 51);
			UtilBlock.blockPassSet.add((byte) 55);
			UtilBlock.blockPassSet.add((byte) 59);
			UtilBlock.blockPassSet.add((byte) 63);
			UtilBlock.blockPassSet.add((byte) 66);
			UtilBlock.blockPassSet.add((byte) 68);
			UtilBlock.blockPassSet.add((byte) 69);
			UtilBlock.blockPassSet.add((byte) 70);
			UtilBlock.blockPassSet.add((byte) 72);
			UtilBlock.blockPassSet.add((byte) 75);
			UtilBlock.blockPassSet.add((byte) 76);
			UtilBlock.blockPassSet.add((byte) 77);
			UtilBlock.blockPassSet.add((byte) 78);
			UtilBlock.blockPassSet.add((byte) 83);
			UtilBlock.blockPassSet.add((byte) 90);
			UtilBlock.blockPassSet.add((byte) 104);
			UtilBlock.blockPassSet.add((byte) 105);
			UtilBlock.blockPassSet.add((byte) 115);
			UtilBlock.blockPassSet.add((byte) 119);
			UtilBlock.blockPassSet.add((byte) (-124));
			UtilBlock.blockPassSet.add((byte) (-113));
			UtilBlock.blockPassSet.add((byte) (-81));
			UtilBlock.blockPassSet.add((byte) (-85));
		}
		return !UtilBlock.blockPassSet.contains(block);
	}

	@SuppressWarnings("deprecation")
	public static boolean isStair(Block block) {
		if (block.getType().equals(Material.ACACIA_STAIRS)
				|| block.getType().equals(Material.BIRCH_WOOD_STAIRS)
				|| block.getType().equals(Material.BRICK_STAIRS)
				|| block.getType().equals(Material.COBBLESTONE_STAIRS)
				|| block.getType().equals(Material.DARK_OAK_STAIRS)
				|| block.getType().equals(Material.NETHER_BRICK_STAIRS)
				|| block.getType().equals(Material.JUNGLE_WOOD_STAIRS)
				|| block.getType().equals(Material.QUARTZ_STAIRS)
				|| block.getType().equals(Material.SMOOTH_STAIRS)
				|| block.getType().equals(Material.WOOD_STAIRS)
				|| block.getType().equals(Material.SANDSTONE_STAIRS)
				|| block.getType().equals(Material.SPRUCE_WOOD_STAIRS)
				|| block.getType().equals(Material.getMaterial("PRISMARINE_BRICK_STAIRS"))
				|| block.getType().equals(Material.getMaterial("PRISMARINE_STAIRS"))
				|| block.getType().equals(Material.getMaterial("DARK_PRISMARINE_STAIRS"))
				|| block.getType().getId() == 180
				|| block.getType().getId() == 203) {
			return true;
		}
		return false;
	}

	public static ArrayList<Block> getBlocksAroundCenter(Location loc, int radius) {
		ArrayList<Block> blocks = new ArrayList<Block>();
		for (int x = loc.getBlockX() - radius; x <= loc.getBlockX() + radius; x++) {
			for (int y = loc.getBlockY() - radius; y <= loc.getBlockY() + radius; y++) {
				for (int z = loc.getBlockZ() - radius; z <= loc.getBlockZ() + radius; z++) {
					Location l = new Location(loc.getWorld(), x, y, z);
					if (l.distance(loc) <= radius) {
						blocks.add(l.getBlock());
					}
				}
			}
		}
		return blocks;
	}

	@SuppressWarnings("deprecation")
	public static boolean solid(Block block) {
		if (block == null) {
			return false;
		}
		return solid(block.getTypeId());
	}

	public static boolean solid(int block) {
		return solid((byte) block);
	}

	public static boolean solid(byte block) {
		if (blockPassSet.isEmpty()) {
			blockPassSet.add((byte) 0);
			blockPassSet.add((byte) 6);
			blockPassSet.add((byte) 8);
			blockPassSet.add((byte) 9);
			blockPassSet.add((byte) 10);
			blockPassSet.add((byte) 11);
			blockPassSet.add((byte) 26);
			blockPassSet.add((byte) 27);
			blockPassSet.add((byte) 28);
			blockPassSet.add((byte) 30);
			blockPassSet.add((byte) 31);
			blockPassSet.add((byte) 32);
			blockPassSet.add((byte) 37);
			blockPassSet.add((byte) 38);
			blockPassSet.add((byte) 39);
			blockPassSet.add((byte) 40);
			blockPassSet.add((byte) 50);
			blockPassSet.add((byte) 51);
			blockPassSet.add((byte) 55);
			blockPassSet.add((byte) 59);
			blockPassSet.add((byte) 63);
			blockPassSet.add((byte) 64);
			blockPassSet.add((byte) 65);
			blockPassSet.add((byte) 66);
			blockPassSet.add((byte) 68);
			blockPassSet.add((byte) 69);
			blockPassSet.add((byte) 70);
			blockPassSet.add((byte) 71);
			blockPassSet.add((byte) 72);
			blockPassSet.add((byte) 75);
			blockPassSet.add((byte) 76);
			blockPassSet.add((byte) 77);
			blockPassSet.add((byte) 78);
			blockPassSet.add((byte) 83);
			blockPassSet.add((byte) 90);
			blockPassSet.add((byte) 92);
			blockPassSet.add((byte) 93);
			blockPassSet.add((byte) 94);
			blockPassSet.add((byte) 96);
			blockPassSet.add((byte) 101);
			blockPassSet.add((byte) 102);
			blockPassSet.add((byte) 104);
			blockPassSet.add((byte) 105);
			blockPassSet.add((byte) 106);
			blockPassSet.add((byte) 107);
			blockPassSet.add((byte) 111);
			blockPassSet.add((byte) 115);
			blockPassSet.add((byte) 116);
			blockPassSet.add((byte) 117);
			blockPassSet.add((byte) 118);
			blockPassSet.add((byte) 119);
			blockPassSet.add((byte) 120);
			blockPassSet.add((byte) -85);
		}
		return !blockPassSet.contains(block);
	}

	public static boolean airFoliage(int block) {
		return airFoliage((byte) block);
	}

	public static boolean airFoliage(byte block) {
		if (blockAirFoliageSet.isEmpty()) {
			blockAirFoliageSet.add((byte) 0);
			blockAirFoliageSet.add((byte) 6);
			blockAirFoliageSet.add((byte) 31);
			blockAirFoliageSet.add((byte) 32);
			blockAirFoliageSet.add((byte) 37);
			blockAirFoliageSet.add((byte) 38);
			blockAirFoliageSet.add((byte) 39);
			blockAirFoliageSet.add((byte) 40);
			blockAirFoliageSet.add((byte) 51);
			blockAirFoliageSet.add((byte) 59);
			blockAirFoliageSet.add((byte) 104);
			blockAirFoliageSet.add((byte) 105);
			blockAirFoliageSet.add((byte) 115);
			blockAirFoliageSet.add((byte) -115);
			blockAirFoliageSet.add((byte) -114);
		}
		return blockAirFoliageSet.contains(block);
	}

	@SuppressWarnings("deprecation")
	public static boolean fullSolid(Block block) {
		if (block == null) {
			return false;
		}
		return fullSolid(block.getTypeId());
	}

	public static boolean fullSolid(int block) {
		return fullSolid((byte) block);
	}

	public static boolean fullSolid(byte block) {
		if (fullSolid.isEmpty()) {
			fullSolid.add((byte) 1);
			fullSolid.add((byte) 2);
			fullSolid.add((byte) 3);
			fullSolid.add((byte) 4);
			fullSolid.add((byte) 5);
			fullSolid.add((byte) 7);
			fullSolid.add((byte) 12);
			fullSolid.add((byte) 13);
			fullSolid.add((byte) 14);
			fullSolid.add((byte) 15);
			fullSolid.add((byte) 16);
			fullSolid.add((byte) 17);
			fullSolid.add((byte) 19);
			fullSolid.add((byte) 20);
			fullSolid.add((byte) 21);
			fullSolid.add((byte) 22);
			fullSolid.add((byte) 23);
			fullSolid.add((byte) 24);
			fullSolid.add((byte) 25);
			fullSolid.add((byte) 29);
			fullSolid.add((byte) 33);
			fullSolid.add((byte) 35);
			fullSolid.add((byte) 41);
			fullSolid.add((byte) 42);
			fullSolid.add((byte) 43);
			fullSolid.add((byte) 44);
			fullSolid.add((byte) 45);
			fullSolid.add((byte) 46);
			fullSolid.add((byte) 47);
			fullSolid.add((byte) 48);
			fullSolid.add((byte) 49);
			fullSolid.add((byte) 56);
			fullSolid.add((byte) 57);
			fullSolid.add((byte) 58);
			fullSolid.add((byte) 60);
			fullSolid.add((byte) 61);
			fullSolid.add((byte) 62);
			fullSolid.add((byte) 73);
			fullSolid.add((byte) 74);
			fullSolid.add((byte) 79);
			fullSolid.add((byte) 80);
			fullSolid.add((byte) 82);
			fullSolid.add((byte) 84);
			fullSolid.add((byte) 86);
			fullSolid.add((byte) 87);
			fullSolid.add((byte) 88);
			fullSolid.add((byte) 89);
			fullSolid.add((byte) 91);
			fullSolid.add((byte) 95);
			fullSolid.add((byte) 97);
			fullSolid.add((byte) 98);
			fullSolid.add((byte) 99);
			fullSolid.add((byte) 100);
			fullSolid.add((byte) 103);
			fullSolid.add((byte) 110);
			fullSolid.add((byte) 112);
			fullSolid.add((byte) 121);
			fullSolid.add((byte) 123);
			fullSolid.add((byte) 124);
			fullSolid.add((byte) 125);
			fullSolid.add((byte) 126);
			fullSolid.add((byte) -127);
			fullSolid.add((byte) -123);
			fullSolid.add((byte) -119);
			fullSolid.add((byte) -118);
			fullSolid.add((byte) -104);
			fullSolid.add((byte) -103);
			fullSolid.add((byte) -101);
			fullSolid.add((byte) -98);
		}
		return fullSolid.contains(block);
	}

	public static HashMap<Block, Double> getInRadius(Location loc, double dR, double heightLimit) {
		HashMap<Block, Double> blockList = new HashMap<Block, Double>();
		int iR = (int) dR + 1;
		for (int x = -iR; x <= iR; x++) {
			for (int z = -iR; z <= iR; z++) {
				for (int y = -iR; y <= iR; y++) {
					if (Math.abs(y) <= heightLimit) {
						Block curBlock = loc.getWorld().getBlockAt((int) (loc.getX() + x), (int) (loc.getY() + y),
								(int) (loc.getZ() + z));

						double offset = UtilMath.offset(loc, curBlock.getLocation().add(0.5D, 0.5D, 0.5D));
						if (offset <= dR) {
							blockList.put(curBlock, 1.0D - offset / dR);
						}
					}
				}
			}
		}
		return blockList;
	}

	public static Block getHighest(Location location, HashSet<Material> ignore) {
		location.setY(0);
		for (int i = 0; i < 256; i++) {
			location.setY(256 - i);
			if (solid(location.getBlock())) {
				break;
			}
		}
		return location.getBlock().getRelative(BlockFace.UP);
	}

	public static boolean isInAir(Player player) {
		boolean nearBlocks = false;
		for (Block block : getSurrounding(player.getLocation().getBlock(), true)) {
			if (block.getType() != Material.AIR) {
				nearBlocks = true;
				break;
			}
		}
		return nearBlocks;
	}

	public static ArrayList<Block> getSurrounding(Block block, boolean diagonals) {
		ArrayList<Block> blocks = new ArrayList<Block>();
		if (diagonals) {
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					for (int z = -1; z <= 1; z++) {
						if ((x != 0) || (y != 0) || (z != 0)) {
							blocks.add(block.getRelative(x, y, z));
						}
					}
				}
			}
		} else {
			blocks.add(block.getRelative(BlockFace.UP));
			blocks.add(block.getRelative(BlockFace.DOWN));
			blocks.add(block.getRelative(BlockFace.NORTH));
			blocks.add(block.getRelative(BlockFace.SOUTH));
			blocks.add(block.getRelative(BlockFace.EAST));
			blocks.add(block.getRelative(BlockFace.WEST));
		}
		return blocks;
	}

	public static ArrayList<Block> getSurroundingB(Block block) {
		ArrayList<Block> blocks = new ArrayList<Block>();
		for (double x = -0.5; x <= 0.5; x += 0.5) {
			for (double y = -0.5; y <= 0.5; y += 0.5) {
				for (double z = -0.5; z <= 0.5; z += 0.5) {
					if ((x != 0) || (y != 0) || (z != 0)) {
						blocks.add(block.getLocation().add(x, y, z).getBlock());
					}
				}
			}
		}
		return blocks;
	}

	public static boolean isLessThanBlock(Block block) {
		final Material type = block.getType();
		for (final String types : HalfBlocksArray) {
			if (type.toString().toLowerCase().contains("chest")||type.toString().toLowerCase().contains("anvil")) {
				return true;
			}
		}
		return false;
	}
}