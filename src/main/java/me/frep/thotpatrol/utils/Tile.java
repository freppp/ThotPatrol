/*
 * Decompiled with CFR 0_121.
 */
package me.frep.thotpatrol.utils;

import java.beans.ConstructorProperties;
import java.util.Objects;

public final class Tile {
	
    private final int x;
    private final int y;
    private final int z;
    private boolean passable = true;
    private double g = Double.MAX_VALUE;
    private double h;
    private Tile parent;

    @ConstructorProperties(value = {"x", "y", "z"})
    public Tile(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getF() {
        return this.h + this.g;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getZ() {
        return this.z;
    }

    public boolean isPassable() {
        return this.passable;
    }

    public void setPassable(boolean passable) {
        this.passable = passable;
    }

    public double getG() {
        return this.g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getH() {
        return this.h;
    }

    public void setH(double h) {
        this.h = h;
    }

    public Tile getParent() {
        return this.parent;
    }

    public void setParent(Tile parent) {
        this.parent = parent;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Tile)) {
            return false;
        }
        Tile other = (Tile) o;
        if (this.getX() != other.getX()) {
            return false;
        }
        if (this.getY() != other.getY()) {
            return false;
        }
        if (this.getZ() != other.getZ()) {
            return false;
        }
        if (this.isPassable() != other.isPassable()) {
            return false;
        }
        if (Double.compare(this.getG(), other.getG()) != 0) {
            return false;
        }
        if (Double.compare(this.getH(), other.getH()) != 0) {
            return false;
        }
        Tile this$parent = this.getParent();
        Tile other$parent = other.getParent();
        return Objects.equals(this$parent, other$parent);
    }

    public int hashCode() {
        int result = 1;
        result = result * 59 + this.getX();
        result = result * 59 + this.getY();
        result = result * 59 + this.getZ();
        result = result * 59 + (this.isPassable() ? 79 : 97);
        long $g = Double.doubleToLongBits(this.getG());
        result = result * 59 + (int) ($g >>> 32 ^ $g);
        long $h = Double.doubleToLongBits(this.getH());
        result = result * 59 + (int) ($h >>> 32 ^ $h);
        Tile $parent = this.getParent();
        result = result * 59 + ($parent == null ? 43 : $parent.hashCode());
        return result;
    }

    public String toString() {
        return "Tile(x=" + this.getX() + ", y=" + this.getY() + ", z=" + this.getZ() + ", passable=" + this.isPassable()
                + ", g=" + this.getG() + ", h=" + this.getH() + ", parent=" + this.getParent() + ")";
    }
}
