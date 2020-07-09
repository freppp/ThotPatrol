package me.frep.thotpatrol.utils;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

public class UtilMath {
    public static final String DEFAULT_ENCODING = "UTF-8";
    public static Random random;

    static {
        UtilMath.random = new Random();
    }

    public static double getFraction(final double value) {
        return value % 1.0;
    }

    public static double getVerticalDistance(Location to, Location from) {
        final double y = Math.abs(Math.abs(to.getY()) - Math.abs(from.getY()));

        return Math.sqrt(y * y);
    }

    public static double getHorizontalDistance(Location to, Location from) {
        final double x = Math.abs(Math.abs(to.getX()) - Math.abs(from.getX()));
        final double z = Math.abs(Math.abs(to.getZ()) - Math.abs(from.getZ()));

        return Math.sqrt(x * x + z * z);
    }

    public static double trim(int degree, double d) {
        String format = "#.#";
        for (int i = 1; i < degree; ++i) {
            format = format + "#";
        }
        DecimalFormat twoDForm = new DecimalFormat(format);
        return Double.parseDouble(twoDForm.format(d).replaceAll(",", "."));
    }

    public static double abs(double a) {
        return (a <= 0.0) ? (0.0 - a) : a;
    }

    public static boolean elapsed(long from, long required) {
        return System.currentTimeMillis() - from > required;
    }

    public static double offset(Entity a, Entity b) {
        return offset(a.getLocation().toVector(), b.getLocation().toVector());
    }

    public static double offset(Location a, Location b) {
        return offset(a.toVector(), b.toVector());
    }

    public static int floor(double var0) {
        final int var2 = (int) var0;
        return var0 < var2 ? var2 - 1 : var2;
    }

    public static double offset(final Vector a, final Vector b) {
        return a.subtract(b).length();
    }

    public static Vector getHorizontalVector(final Vector v) {
        v.setY(0);
        return v;
    }

    public static double getStandardDeviation(double[] numberArray) {
        double sum = 0.0, deviation = 0.0;
        int length = numberArray.length;
        for (double num : numberArray)
            sum += num;
        double mean = sum / length;
        for (double num : numberArray)
            deviation += Math.pow(num - mean, 2);

        return Math.sqrt(deviation / length);
    }

    public static double getStandardDeviation(List<Double> numberArray) {
        double sum = 0.0, deviation = 0.0;
        int length = numberArray.size();
        for (double num : numberArray)
            sum += num;
        double mean = sum / length;
        for (double num : numberArray)
            deviation += Math.pow(num - mean, 2);

        return Math.sqrt(deviation / length);
    }

    public static Vector getVerticalVector(final Vector v) {
        v.setX(0);
        v.setZ(0);
        return v;
    }
}