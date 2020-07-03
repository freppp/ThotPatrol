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
	
	private static final String version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	
    public static ArrayList<Player> getOnlinePlayers() {
		return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    public static List<Entity> getEntities(final World world) {
        return world.getEntities();
    }

	public static boolean isBukkitVerison(String version) {
		final String bukkit = Bukkit.getServer().getClass().getPackage().getName().substring(23);

		return bukkit.contains(version);
	}

	public static Class<?> getCBClass(String string) {
		return getClass("org.bukkit.craftbukkit." + version + "." + string);
	}

	public static Class<?> getClass(String string) {
		try {
			return Class.forName(string);
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}


	public static Field getFieldByName(Class<?> clazz, String fieldName) {
		try {
			final Field field = clazz.getDeclaredField(fieldName);
			field.setAccessible(true);
			return field;
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Method getMethod(Class<?> clazz, String methodName, Class<?>... args) {
		try {
			final Method method = clazz.getMethod(methodName, args);
			method.setAccessible(true);
			return method;
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object getMethodValue(Method method, Object object, Object... args) {
		try {
			return method.invoke(object, args);
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Object getFieldValue(Field field, Object object) {
		try {
			field.setAccessible(true);
			return field.get(object);
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
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
		} else return d4 < 0.3 ? UtilBlock.containsBlockType(arrmaterial, world.getBlockAt(n2, n3, n4 - 1)) : d4 > 0.7 && UtilBlock.containsBlockType(arrmaterial, world.getBlockAt(n2, n3, n4 + 1));
		return false;
	}

	public static boolean isOnBlock(Player player, int n, Material[] arrmaterial) {
		return isOnBlock(player.getLocation(), n, arrmaterial);
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
		} else return d4 < 0.3 ? UtilBlock.isLiquid(world.getBlockAt(n2, n3, n4 - 1)) : d4 > 0.7 && UtilBlock.isLiquid(world.getBlockAt(n2, n3, n4 + 1));
		return false;
	}

	public static boolean isHoveringOverWater(Player player, int n) {
		return isHoveringOverWater(player.getLocation(), n);
	}
}