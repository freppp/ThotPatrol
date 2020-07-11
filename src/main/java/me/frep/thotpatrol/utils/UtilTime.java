package me.frep.thotpatrol.utils;

public class UtilTime {
    public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_DAY = "yyyy-MM-dd";

    public static long nowlong() {
        return System.currentTimeMillis();
    }

    public static long a(String a) {
        final long l = Long.parseLong(a.substring(0, a.length() - 1));
        if (a.endsWith("s")) {
            return l * 1000L;
        }
        if (a.endsWith("m")) {
            return l * 60000L;
        }
        if (a.endsWith("h")) {
            return l * 3600000L;
        }
        if (a.endsWith("d")) {
            return l * 86400000L;
        }
        if (a.endsWith("M")) {
            return l * 2592000000L;
        }
        if (a.endsWith("y")) {
            return l * 31104000000L;
        }
        return -1L;
    }

    public static boolean elapsed(long from, long required) {
        return System.currentTimeMillis() - from > required;
    }

    public static long elapsed(long starttime) {
        return System.currentTimeMillis() - starttime;
    }
}