package me.frep.thotpatrol.commands;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.utils.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AutobanCommand implements CommandExecutor {
	
    private ThotPatrol ThotPatrol;

    public AutobanCommand(ThotPatrol ThotPatrol) {
        this.ThotPatrol = ThotPatrol;
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String alias,
                             final String[] args) {
        if (!sender.hasPermission("thotpatrol.staff")) {
            sender.sendMessage(Color.Red + "No permission.");
            return true;
        }
        if (args.length == 2) {
            final String type = args[0];
            final String playerName = args[1];
            final Player player = Bukkit.getServer().getPlayer(playerName);
            if (player == null || !player.isOnline()) {
                sender.sendMessage(Color.Red + "This player does not exist.");
                return true;
            }
            if (this.ThotPatrol.getAutoBanQueue().contains(player)) {
                @SuppressWarnings("unused")
				String lowerCase;
                switch (lowerCase = type.toLowerCase()) {
                    case "cancel": {
                        System.out.println("[" + player.getUniqueId().toString() + "] " + sender.getName()
                                + "'s auto-ban has been cancelled by " + sender.getName());
                        Bukkit.broadcast(
                                ChatColor.translateAlternateColorCodes('&',
                                		ThotPatrol.PREFIX + ThotPatrol.getConfig().getString("alerts.secondary")
                                                + player.getName() + ThotPatrol.getConfig().getString("alerts.primary")
                                                + "'s auto-ban has been cancelled by "
                                                + ThotPatrol.getConfig().getString("alerts.secondary") + sender.getName()),
                                "daedalus.staff");
                        break;
                    }
                    case "ban": {
                        if (this.ThotPatrol.getConfig().getBoolean("testmode")) {
                            sender.sendMessage(ChatColor.RED + "Test mode is enabled therefore this is disabled!");
                        } else {
                            System.out.println("[" + player.getUniqueId().toString() + "] " + sender.getName()
                                    + "'s auto-ban has been forced by " + sender.getName());
                            Bukkit.broadcast(ChatColor.translateAlternateColorCodes('&',
                            		ThotPatrol.PREFIX + ThotPatrol.getConfig().getString("alerts.secondary") + player.getName()
                                            + ThotPatrol.getConfig().getString("alerts.primary")
                                            + "'s auto-ban has been forced by "
                                            + ThotPatrol.getConfig().getString("alerts.secondary") + sender.getName()),
                                    "daedalus.staff");
                            this.ThotPatrol.autoBanOver(player);
                        }
                        break;
                    }
                    default:
                        break;
                }
                this.ThotPatrol.removeFromAutoBanQueue(player);
                this.ThotPatrol.removeViolations(player);
            } else {
                sender.sendMessage(String.valueOf(Color.Red) + "This player is not in the autoban queue!");
            }
        }
        return true;
    }
}