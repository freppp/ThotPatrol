package me.frep.thotpatrol.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public final class UtilCheat {

    public static double getDistance3D(Location one, Location two) {
        double toReturn = 0.0D;
        double xSqr = (two.getX() - one.getX()) * (two.getX() - one.getX());
        double ySqr = (two.getY() - one.getY()) * (two.getY() - one.getY());
        double zSqr = (two.getZ() - one.getZ()) * (two.getZ() - one.getZ());
        double sqrt = Math.sqrt(xSqr + ySqr + zSqr);
        toReturn = Math.abs(sqrt);
        return toReturn;
    }

    public static double getVerticalDistance(Location one, Location two) {
        double toReturn = 0.0D;
        double ySqr = (two.getY() - one.getY()) * (two.getY() - one.getY());
        double sqrt = Math.sqrt(ySqr);
        toReturn = Math.abs(sqrt);
        return toReturn;
    }

    public static double getHorizontalDistance(Location one, Location two) {
        double toReturn = 0.0D;
        double xSqr = (two.getX() - one.getX()) * (two.getX() - one.getX());
        double zSqr = (two.getZ() - one.getZ()) * (two.getZ() - one.getZ());
        double sqrt = Math.sqrt(xSqr + zSqr);
        toReturn = Math.abs(sqrt);
        return toReturn;
    }

    public static boolean cantStandAtWater(Block block) {
        Block otherBlock = block.getRelative(BlockFace.DOWN);

        boolean isHover = block.getType() == Material.AIR;
        boolean n = (otherBlock.getRelative(BlockFace.NORTH).getType() == Material.WATER)
                || (otherBlock.getRelative(BlockFace.NORTH).getType() == Material.STATIONARY_WATER);
        boolean s = (otherBlock.getRelative(BlockFace.SOUTH).getType() == Material.WATER)
                || (otherBlock.getRelative(BlockFace.SOUTH).getType() == Material.STATIONARY_WATER);
        boolean e = (otherBlock.getRelative(BlockFace.EAST).getType() == Material.WATER)
                || (otherBlock.getRelative(BlockFace.EAST).getType() == Material.STATIONARY_WATER);
        boolean w = (otherBlock.getRelative(BlockFace.WEST).getType() == Material.WATER)
                || (otherBlock.getRelative(BlockFace.WEST).getType() == Material.STATIONARY_WATER);
        boolean ne = (otherBlock.getRelative(BlockFace.NORTH_EAST).getType() == Material.WATER)
                || (otherBlock.getRelative(BlockFace.NORTH_EAST).getType() == Material.STATIONARY_WATER);
        boolean nw = (otherBlock.getRelative(BlockFace.NORTH_WEST).getType() == Material.WATER)
                || (otherBlock.getRelative(BlockFace.NORTH_WEST).getType() == Material.STATIONARY_WATER);
        boolean se = (otherBlock.getRelative(BlockFace.SOUTH_EAST).getType() == Material.WATER)
                || (otherBlock.getRelative(BlockFace.NORTH).getType() == Material.STATIONARY_WATER);
        boolean sw = (otherBlock.getRelative(BlockFace.SOUTH_WEST).getType() == Material.WATER)
                || (otherBlock.getRelative(BlockFace.SOUTH_WEST).getType() == Material.STATIONARY_WATER);

        return (n) && (s) && (e) && (w) && (ne) && (nw) && (se) && (sw) && (isHover);
    }

    public static float getAngleDistance(float alpha, float beta) {
        final float phi = Math.abs(beta - alpha) % 360;
        return phi > 180 ? 360 - phi : phi;
    }

    public static boolean canStandWithin(Block block) {
        boolean isSand = block.getType() == Material.SAND;
        boolean isGravel = block.getType() == Material.GRAVEL;
        boolean solid = (block.getType().isSolid()) && (!block.getType().name().toLowerCase().contains("door"))
                && (!block.getType().name().toLowerCase().contains("fence"))
                && (!block.getType().name().toLowerCase().contains("bars"))
                && (!block.getType().name().toLowerCase().contains("sign"));

        return (!isSand) && (!isGravel) && (!solid);
    }

    public static Vector getRotation(Location one, Location two) {
        double dx = two.getX() - one.getX();
        double dy = two.getY() - one.getY();
        double dz = two.getZ() - one.getZ();
        double distanceXZ = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.atan2(dz, dx) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float) -(Math.atan2(dy, distanceXZ) * 180.0D / 3.141592653589793D);
        return new Vector(yaw, pitch, 0.0F);
    }

    public static double clamp180(double theta) {
        theta %= 360.0D;
        if (theta >= 180.0D) {
            theta -= 360.0D;
        }
        if (theta < -180.0D) {
            theta += 360.0D;
        }
        return theta;
    }

    public static boolean cantStandAt(Block block) {
        return (!canStand(block)) && (cantStandClose(block)) && (cantStandFar(block));
    }

    public static boolean cantStandClose(Block block) {
        return (!canStand(block.getRelative(BlockFace.NORTH))) && (!canStand(block.getRelative(BlockFace.EAST)))
                && (!canStand(block.getRelative(BlockFace.SOUTH))) && (!canStand(block.getRelative(BlockFace.WEST)));
    }

    public static boolean cantStandFar(Block block) {
        return (!canStand(block.getRelative(BlockFace.NORTH_WEST)))
                && (!canStand(block.getRelative(BlockFace.NORTH_EAST)))
                && (!canStand(block.getRelative(BlockFace.SOUTH_WEST)))
                && (!canStand(block.getRelative(BlockFace.SOUTH_EAST)));
    }

    public static boolean canStand(Block block) {
        return (!block.isLiquid()) && (block.getType() != Material.AIR);
    }

    public static boolean isFullyInWater(Location player) {
        double touchedX = fixXAxis(player.getX());

        return (new Location(player.getWorld(), touchedX, player.getY(), player.getBlockZ()).getBlock().isLiquid())
                && (new Location(player.getWorld(), touchedX, Math.round(player.getY()), player.getBlockZ()).getBlock()
                .isLiquid());
    }

    public static double fixXAxis(double x) {
        double touchedX = x;
        double rem = touchedX - Math.round(touchedX) + 0.01D;
        if (rem < 0.3D) {
            touchedX = NumberConversions.floor(x) - 1;
        }
        return touchedX;
    }

    public static boolean isOnGround(final Location location, final int down) {
        final double posX = location.getX();
        final double posZ = location.getZ();
        final double fracX = (UtilMath.getFraction(posX) > 0.0) ? Math.abs(UtilMath.getFraction(posX))
                : (1.0 - Math.abs(UtilMath.getFraction(posX)));
        final double fracZ = (UtilMath.getFraction(posZ) > 0.0) ? Math.abs(UtilMath.getFraction(posZ))
                : (1.0 - Math.abs(UtilMath.getFraction(posZ)));
        final int blockX = location.getBlockX();
        final int blockY = location.getBlockY() - down;
        final int blockZ = location.getBlockZ();
        final World world = location.getWorld();
        if (UtilBlock.isSolid(world.getBlockAt(blockX, blockY, blockZ))) {
            return true;
        }
        if (fracX < 0.3) {
            if (UtilBlock.isSolid(world.getBlockAt(blockX - 1, blockY, blockZ))) {
                return true;
            }
            if (fracZ < 0.3) {
                if (UtilBlock.isSolid(world.getBlockAt(blockX - 1, blockY, blockZ - 1))) {
                    return true;
                }
                if (UtilBlock.isSolid(world.getBlockAt(blockX, blockY, blockZ - 1))) {
                    return true;
                }
                return UtilBlock.isSolid(world.getBlockAt(blockX + 1, blockY, blockZ - 1));
            } else if (fracZ > 0.7) {
                if (UtilBlock.isSolid(world.getBlockAt(blockX - 1, blockY, blockZ + 1))) {
                    return true;
                }
                if (UtilBlock.isSolid(world.getBlockAt(blockX, blockY, blockZ + 1))) {
                    return true;
                }
                return UtilBlock.isSolid(world.getBlockAt(blockX + 1, blockY, blockZ + 1));
            }
        } else if (fracX > 0.7) {
            if (UtilBlock.isSolid(world.getBlockAt(blockX + 1, blockY, blockZ))) {
                return true;
            }
            if (fracZ < 0.3) {
                if (UtilBlock.isSolid(world.getBlockAt(blockX - 1, blockY, blockZ - 1))) {
                    return true;
                }
                if (UtilBlock.isSolid(world.getBlockAt(blockX, blockY, blockZ - 1))) {
                    return true;
                }
                return UtilBlock.isSolid(world.getBlockAt(blockX + 1, blockY, blockZ - 1));
            } else if (fracZ > 0.7) {
                if (UtilBlock.isSolid(world.getBlockAt(blockX - 1, blockY, blockZ + 1))) {
                    return true;
                }
                if (UtilBlock.isSolid(world.getBlockAt(blockX, blockY, blockZ + 1))) {
                    return true;
                }
                return UtilBlock.isSolid(world.getBlockAt(blockX + 1, blockY, blockZ + 1));
            }
        } else if (fracZ < 0.3) {
            return UtilBlock.isSolid(world.getBlockAt(blockX, blockY, blockZ - 1));
        } else return fracZ > 0.7 && UtilBlock.isSolid(world.getBlockAt(blockX, blockY, blockZ + 1));
        return false;
    }

    public static boolean isHoveringOverWater(Location player, int blocks) {
        for (int i = player.getBlockY(); i > player.getBlockY() - blocks; i--) {
            Block newloc = new Location(player.getWorld(), player.getBlockX(), i, player.getBlockZ()).getBlock();
            if (newloc.getType() != Material.AIR) {
                return newloc.isLiquid() && newloc.getData() == 0;
            }
        }
        return false;
    }

    public static boolean isHoveringOverWater(Location player) {
        return isHoveringOverWater(player, 25);
    }

    @SuppressWarnings("incomplete-switch")
    public static boolean isSlab(Block block) {
        Material type = block.getType();
        switch (type) {
            case STEP:
            case WOOD_STEP:
                return true;
        }
        return false;
    }

    @SuppressWarnings("incomplete-switch")
    public static boolean isStair(Block block) {
        Material type = block.getType();
        switch (type) {
            case COMMAND:
            case COBBLESTONE_STAIRS:
            case BRICK_STAIRS:
            case ACACIA_STAIRS:
            case BIRCH_WOOD_STAIRS:
            case DARK_OAK_STAIRS:
            case JUNGLE_WOOD_STAIRS:
            case NETHER_BRICK_STAIRS:
            case QUARTZ_STAIRS:
            case SANDSTONE_STAIRS:
            case SMOOTH_STAIRS:
            case SPRUCE_WOOD_STAIRS:
            case WOOD_STAIRS:
                return true;
        }
        return false;
    }

    public static boolean isOnLilyPad(Player player) {
        Block block = player.getLocation().getBlock();
        Material lily = Material.WATER_LILY;

        return (block.getType() == lily) || (block.getRelative(BlockFace.NORTH).getType() == lily)
                || (block.getRelative(BlockFace.SOUTH).getType() == lily)
                || (block.getRelative(BlockFace.EAST).getType() == lily)
                || (block.getRelative(BlockFace.WEST).getType() == lily);
    }

    public static boolean isInWater(Player player) {
        return (player.getLocation().getBlock().isLiquid())
                || (player.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid())
                || (player.getLocation().getBlock().getRelative(BlockFace.UP).isLiquid());
    }

    @SuppressWarnings("unlikely-arg-type")
    public static boolean isInWeb(Player player) {
        if (UtilBlock.getBlocksAroundCenter(player.getLocation(), 1).contains(Material.WEB)) {
            return true;
        }
        return (player.getLocation().getBlock().getType() == Material.WEB)
                || (player.getLocation().clone().add(0.0D, 1.0D, 0.0D).getBlock().getType() == Material.WEB)
                || (player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.WEB)
                || (player.getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.WEB);
    }

    public static boolean blocksNear(Player player) {
        return blocksNear(player.getLocation());
    }

    public static boolean blocksNear(final Location loc) {
        boolean nearBlocks = false;
        for (Block block : UtilBlock.getSurrounding(loc.getBlock(), true)) {
            if (block.getType() != Material.AIR) {
                nearBlocks = true;
                break;
            }
        }
        for (final Block block : UtilBlock.getSurrounding(loc.getBlock(), false)) {
            if (block.getType() != Material.AIR) {
                nearBlocks = true;
                break;
            }
        }
        loc.setY(loc.getY() - 0.5);
        if (loc.getBlock().getType() != Material.AIR) {
            nearBlocks = true;
        }
        if (isBlock(loc.getBlock().getRelative(BlockFace.DOWN),
                new Material[]{Material.FENCE, Material.FENCE_GATE, Material.COBBLE_WALL, Material.LADDER})) {
            nearBlocks = true;
        }
        return nearBlocks;
    }

    public static boolean slabsNear(Location loc) {
        boolean nearBlocks = false;
        for (Block bl : UtilBlock.getSurrounding(loc.getBlock(), true)) {
            if ((bl.getType().equals(Material.STEP)) || (bl.getType().equals(Material.DOUBLE_STEP))
                    || (bl.getType().equals(Material.WOOD_DOUBLE_STEP)) || (bl.getType().equals(Material.WOOD_STEP))) {
                nearBlocks = true;
                break;
            }
        }
        for (Block bl : UtilBlock.getSurrounding(loc.getBlock(), false)) {
            if ((bl.getType().equals(Material.STEP)) || (bl.getType().equals(Material.DOUBLE_STEP))
                    || (bl.getType().equals(Material.WOOD_DOUBLE_STEP)) || (bl.getType().equals(Material.WOOD_STEP))) {
                nearBlocks = true;
                break;
            }
        }
        if (isBlock(loc.getBlock().getRelative(BlockFace.DOWN), new Material[]{Material.STEP, Material.DOUBLE_STEP,
                Material.WOOD_DOUBLE_STEP, Material.WOOD_STEP})) {
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

    public static double getAimbotoffset(Location playerLocLoc, double playerEyeHeight, LivingEntity entity) {
        Location entityLoc = entity.getLocation().add(0.0D, entity.getEyeHeight(), 0.0D);
        Location playerLoc = playerLocLoc.add(0.0D, playerEyeHeight, 0.0D);

        Vector playerRotation = new Vector(playerLoc.getYaw(), playerLoc.getPitch(), 0.0F);
        Vector expectedRotation = getRotation(playerLoc, entityLoc);

        double deltaYaw = clamp180(playerRotation.getX() - expectedRotation.getX());

        double horizontalDistance = getHorizontalDistance(playerLoc, entityLoc);
        double distance = getDistance3D(playerLoc, entityLoc);

        double offsetX = deltaYaw * horizontalDistance * distance;

        return offsetX;
    }
}
