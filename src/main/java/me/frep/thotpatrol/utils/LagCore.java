package me.frep.thotpatrol.utils;

import me.frep.thotpatrol.ThotPatrol;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class LagCore implements Listener {
	
    public ThotPatrol ThotPatrol;
    private double tps;

    public LagCore(ThotPatrol ThotPatrol) {
        this.ThotPatrol = ThotPatrol;
        new BukkitRunnable() {
            long sec;
            long currentSec;
            int ticks;

            public void run() {
                this.sec = (System.currentTimeMillis() / 1000L);
                if (this.currentSec == this.sec) {
                    this.ticks += 1;
                } else {
                    this.currentSec = this.sec;
                    LagCore.this.tps = (LagCore.this.tps == 0.0D ? this.ticks : (LagCore.this.tps + this.ticks) / 2.0D);
                    this.ticks = 0;
                }
            }
        }.runTaskTimerAsynchronously(ThotPatrol, 1L, 1L);
        ThotPatrol.RegisterListener(this);
    }

    public static Object getNmsPlayer(Player p) throws Exception {
        Method getHandle = p.getClass().getMethod("getHandle");
        return getHandle.invoke(p);
    }

    public static Object getFieldValue(Object instance, String fieldName) throws Exception {
        Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }

    public double getTPS() {
        return Math.min(this.tps + 1.0D, 20.0D);
    }

    public double getLag() {
        return Math.round((1.0D - tps / 20.0D) * 100.0D);
    }

    public double getFreeRam() {
        return Math.round(Runtime.getRuntime().freeMemory() / 1000000);
    }

    public double getMaxRam() {
        return Math.round(Runtime.getRuntime().maxMemory() / 1000000);
    }

    public int getPing(Player who) {
        try {
            String bukkitversion = Bukkit.getServer().getClass().getPackage().getName().substring(23);
            Class<?> craftPlayer = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".entity.CraftPlayer");
            Object handle = craftPlayer.getMethod("getHandle").invoke(who);
            return (Integer) handle.getClass().getDeclaredField("ping").get(handle);
        } catch (Exception e) {
            return 404;
        }
    }
}