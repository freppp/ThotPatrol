package me.frep.thotpatrol.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import me.frep.thotpatrol.utils.UtilBlock;

public class UtilServer {

	public static ArrayList<Player> getOnlinePlayers() {
		return new ArrayList<>(Bukkit.getOnlinePlayers());
	}

	public static boolean isBukkitVerison(String version) {
		final String bukkit = Bukkit.getServer().getClass().getPackage().getName().substring(23);

		return bukkit.contains(version);
	}

	public static boolean isOnGround(Location location, int n) {
		final double d = location.getX();
		final double d2 = location.getZ();
		final double d3 = UtilMath.getFraction(d) > 0.0 ? Math.abs(UtilMath.getFraction(d)) : 1.0 - Math.abs(UtilMath.getFraction(d));
		final double d4 = UtilMath.getFraction(d2) > 0.0 ? Math.abs(UtilMath.getFraction(d2)) : 1.0 - Math.abs(UtilMath.getFraction(d2));
		final int n2 = location.getBlockX();
		final int n3 = location.getBlockY() - n;
		final int n4 = location.getBlockZ();
		final World world = location.getWorld();
		if (UtilBlock.isSolid(world.getBlockAt(n2, n3, n4))) {
			return true;
		}
		if (d3 < 0.3) {
			if (UtilBlock.isSolid(world.getBlockAt(n2 - 1, n3, n4))) {
				return true;
			}
			if (d4 < 0.3) {
				if (UtilBlock.isSolid(world.getBlockAt(n2 - 1, n3, n4 - 1))) {
					return true;
				}
				if (UtilBlock.isSolid(world.getBlockAt(n2, n3, n4 - 1))) {
					return true;
				}
				return UtilBlock.isSolid(world.getBlockAt(n2 + 1, n3, n4 - 1));
			} else if (d4 > 0.7) {
				if (UtilBlock.isSolid(world.getBlockAt(n2 - 1, n3, n4 + 1))) {
					return true;
				}
				if (UtilBlock.isSolid(world.getBlockAt(n2, n3, n4 + 1))) {
					return true;
				}
				return UtilBlock.isSolid(world.getBlockAt(n2 + 1, n3, n4 + 1));
			}
		} else if (d3 > 0.7) {
			if (UtilBlock.isSolid(world.getBlockAt(n2 + 1, n3, n4))) {
				return true;
			}
			if (d4 < 0.3) {
				if (UtilBlock.isSolid(world.getBlockAt(n2 - 1, n3, n4 - 1))) {
					return true;
				}
				if (UtilBlock.isSolid(world.getBlockAt(n2, n3, n4 - 1))) {
					return true;
				}
				return UtilBlock.isSolid(world.getBlockAt(n2 + 1, n3, n4 - 1));
			} else if (d4 > 0.7) {
				if (UtilBlock.isSolid(world.getBlockAt(n2 - 1, n3, n4 + 1))) {
					return true;
				}
				if (UtilBlock.isSolid(world.getBlockAt(n2, n3, n4 + 1))) {
					return true;
				}
				return UtilBlock.isSolid(world.getBlockAt(n2 + 1, n3, n4 + 1));
			}
		} else if (d4 < 0.3 ? UtilBlock.isSolid(world.getBlockAt(n2, n3, n4 - 1)) : d4 > 0.7 && UtilBlock.isSolid(world.getBlockAt(n2, n3, n4 + 1))) {
			return true;
		}
		return false;
	}

	public static boolean isOnGround(Player player, int n) {
		return isOnGround(player.getLocation(), n);
	}

	public static boolean isOnBlock(Location location, int n, Material[] arrmaterial) {
		final double d = location.getX();
		final double d2 = location.getZ();
		final double d3 = UtilMath.getFraction(d) > 0.0 ? Math.abs(UtilMath.getFraction(d)) : 1.0 - Math.abs(UtilMath.getFraction(d));
		final double d4 = UtilMath.getFraction(d2) > 0.0 ? Math.abs(UtilMath.getFraction(d2)) : 1.0 - Math.abs(UtilMath.getFraction(d2));
		final int n2 = location.getBlockX();
		final int n3 = location.getBlockY() - n;
		final int n4 = location.getBlockZ();
		final World world = location.getWorld();
		if (UtilBlock.containsBlockType(arrmaterial, world.getBlockAt(n2, n3, n4))) {
			return true;
		}
		if (d3 < 0.3) {
			if (UtilBlock.containsBlockType(arrmaterial, world.getBlockAt(n2 - 1, n3, n4))) {
				return true;
			}
			if (d4 < 0.3) {
				if (UtilBlock.containsBlockType(arrmaterial, world.getBlockAt(n2 - 1, n3, n4 - 1))) {
					return true;
				}
				if (UtilBlock.containsBlockType(arrmaterial, world.getBlockAt(n2, n3, n4 - 1))) {
					return true;
				}
				return UtilBlock.containsBlockType(arrmaterial, world.getBlockAt(n2 + 1, n3, n4 - 1));
			} else if (d4 > 0.7) {
				if (UtilBlock.containsBlockType(arrmaterial, world.getBlockAt(n2 - 1, n3, n4 + 1))) {
					return true;
				}
				if (UtilBlock.containsBlockType(arrmaterial, world.getBlockAt(n2, n3, n4 + 1))) {
					return true;
				}
				return UtilBlock.containsBlockType(arrmaterial, world.getBlockAt(n2 + 1, n3, n4 + 1));
			}
		} else if (d3 > 0.7) {
			if (UtilBlock.containsBlockType(arrmaterial, world.getBlockAt(n2 + 1, n3, n4))) {
				return true;
			}
			if (d4 < 0.3) {
				if (UtilBlock.containsBlockType(arrmaterial, world.getBlockAt(n2 - 1, n3, n4 - 1))) {
					return true;
				}
				if (UtilBlock.containsBlockType(arrmaterial, world.getBlockAt(n2, n3, n4 - 1))) {
					return true;
				}
				return UtilBlock.containsBlockType(arrmaterial, world.getBlockAt(n2 + 1, n3, n4 - 1));
			} else if (d4 > 0.7) {
				if (UtilBlock.containsBlockType(arrmaterial, world.getBlockAt(n2 - 1, n3, n4 + 1))) {
					return true;
				}
				if (UtilBlock.containsBlockType(arrmaterial, world.getBlockAt(n2, n3, n4 + 1))) {
					return true;
				}
				if (UtilBlock.containsBlockType(arrmaterial, world.getBlockAt(n2 + 1, n3, n4 + 1))) {
					return true;
				}
			}
		} else
			return d4 < 0.3 ? UtilBlock.containsBlockType(arrmaterial, world.getBlockAt(n2, n3, n4 - 1)) : d4 > 0.7 && UtilBlock.containsBlockType(arrmaterial, world.getBlockAt(n2, n3, n4 + 1));
		return false;
	}

	public static boolean isHoveringOverWater(Location location, int n) {
		final double d = location.getX();
		final double d2 = location.getZ();
		final double d3 = UtilMath.getFraction(d) > 0.0 ? Math.abs(UtilMath.getFraction(d)) : 1.0 - Math.abs(UtilMath.getFraction(d));
		final double d4 = UtilMath.getFraction(d2) > 0.0 ? Math.abs(UtilMath.getFraction(d2)) : 1.0 - Math.abs(UtilMath.getFraction(d2));
		final int n2 = location.getBlockX();
		final int n3 = location.getBlockY() - n;
		final int n4 = location.getBlockZ();
		final World world = location.getWorld();
		if (UtilBlock.isLiquid(world.getBlockAt(n2, n3, n4))) {
			return true;
		}
		if (d3 < 0.3) {
			if (UtilBlock.isLiquid(world.getBlockAt(n2 - 1, n3, n4))) {
				return true;
			}
			if (d4 < 0.3) {
				if (UtilBlock.isLiquid(world.getBlockAt(n2 - 1, n3, n4 - 1))) {
					return true;
				}
				if (UtilBlock.isLiquid(world.getBlockAt(n2, n3, n4 - 1))) {
					return true;
				}
				if (UtilBlock.isLiquid(world.getBlockAt(n2 + 1, n3, n4 - 1))) {
					return true;
				}
			} else if (d4 > 0.7) {
				if (UtilBlock.isLiquid(world.getBlockAt(n2 - 1, n3, n4 + 1))) {
					return true;
				}
				if (UtilBlock.isLiquid(world.getBlockAt(n2, n3, n4 + 1))) {
					return true;
				}
				return UtilBlock.isLiquid(world.getBlockAt(n2 + 1, n3, n4 + 1));
			}
		} else if (d3 > 0.7) {
			if (UtilBlock.isLiquid(world.getBlockAt(n2 + 1, n3, n4))) {
				return true;
			}
			if (d4 < 0.3) {
				if (UtilBlock.isLiquid(world.getBlockAt(n2 - 1, n3, n4 - 1))) {
					return true;
				}
				if (UtilBlock.isLiquid(world.getBlockAt(n2, n3, n4 - 1))) {
					return true;
				}
				return UtilBlock.isLiquid(world.getBlockAt(n2 + 1, n3, n4 - 1));
			} else if (d4 > 0.7) {
				if (UtilBlock.isLiquid(world.getBlockAt(n2 - 1, n3, n4 + 1))) {
					return true;
				}
				if (UtilBlock.isLiquid(world.getBlockAt(n2, n3, n4 + 1))) {
					return true;
				}
				return UtilBlock.isLiquid(world.getBlockAt(n2 + 1, n3, n4 + 1));
			}
		} else
			return d4 < 0.3 ? UtilBlock.isLiquid(world.getBlockAt(n2, n3, n4 - 1)) : d4 > 0.7 && UtilBlock.isLiquid(world.getBlockAt(n2, n3, n4 + 1));
		return false;
	}
}