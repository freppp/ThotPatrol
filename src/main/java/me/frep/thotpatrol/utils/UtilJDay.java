package me.frep.thotpatrol.utils;

import me.frep.thotpatrol.ThotPatrol;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

public class UtilJDay {

	@SuppressWarnings("unused")
	public static void signalTimer(){
		final Config pending = new Config("pendingusers");
		new BukkitRunnable(){
			@Override
			public void run(){
				executeBanWave();
				ThotPatrol.getInstance().saveConfig();
			}
		}.runTaskTimer(ThotPatrol.getInstance(), 20 * 86400, 20 * 86400);
	}

	public static void executeBanWave() {
		final Config pending = new Config("pendingusers");
		final Config banned = new Config("bannedusers");
		final String commands = ThotPatrol.getInstance().getConfig().getString("jday-bancmd");
		final String broadcast = ThotPatrol.getInstance().getConfig().getString("jday-broadcastmsg");
		for (final String s : pending.getConfigFile().getConfigurationSection("PendingUsers").getKeys(false)) {
			final String name = pending.getConfigFile().getString("PendingUsers." + s + ".Name");
			final String uuid = pending.getConfigFile().getString("PendingUsers." + s + ".UUID");
			final String reason = pending.getConfigFile().getString("PendingUsers." + s + ".Reason");
			final String executedby = pending.getConfigFile().getString("PendingUsers." + s + ".ExecutedBy");
			final String wasonline = pending.getConfigFile().getString("PendingUsers." + s + ".wasOnline");
			final String date = pending.getConfigFile().getString("PendingUsers." + s + ".Date");
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commands.replace("%player%", name));
			Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', broadcast.replaceAll("%player%", name)));
			banned.getConfigFile().set("BannedUsers." + s + ".Name", name);
			banned.getConfigFile().set("BannedUsers." + s + ".UUID", uuid);
			banned.getConfigFile().set("BannedUsers." + s + ".Date", date);
			banned.getConfigFile().set("BannedUsers." + s + ".Reason", reason);
			banned.getConfigFile().set("BannedUsers." + s + ".ExecutedBy", executedby);
			banned.getConfigFile().set("BannedUsers." + s + ".wasOnline", wasonline);
			banned.saveConfigFile();
		}
		pending.getConfigFile().set("PendingUsers", null);
		pending.saveConfigFile();
		ThotPatrol.getInstance().saveConfig();
	}

	@SuppressWarnings("unused")
	public static int getAmountToBan(){
		final Config pending = new Config("pendingusers");
		int count = 0;
		if ( pending.getConfigFile().getConfigurationSection("PendingUsers") == null ) {
			return 0;
		}
		for ( final String s : pending.getConfigFile().getConfigurationSection("PendingUsers").getKeys(false) ) {
			count++;
		}
		return count;
	}
}