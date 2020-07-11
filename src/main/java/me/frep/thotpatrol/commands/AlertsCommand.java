package me.frep.thotpatrol.commands;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.utils.Color;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AlertsCommand implements CommandExecutor {
	
    private ThotPatrol ThotPatrol;

    public AlertsCommand(ThotPatrol ThotPatrol) {
        this.ThotPatrol = ThotPatrol;
    }

    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You have to be a player to run this command!");
            return true;
        }
        Player player = (Player) sender;
        if (!player.hasPermission("thotpatrol.alerts")
        		|| !player.hasPermission("thotpatrol.admin")) {
            sender.sendMessage(Color.Red + "No permission.");
            return true;
        }
        if (this.ThotPatrol.hasAlertsOn(player)) {
            ThotPatrol.toggleAlerts(player);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
            		ThotPatrol.PREFIX + ThotPatrol.getConfig().getString("alerts.primary") + "Alerts toggled " + Color.Red
                            + "off" + ThotPatrol.getConfig().getString("alerts.primary") + "!"));
        } else {
            ThotPatrol.toggleAlerts(player);
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',
            		ThotPatrol.PREFIX + ThotPatrol.getConfig().getString("alerts.primary") + "Alerts toggled " + Color.Green
                            + "on" + ThotPatrol.getConfig().getString("alerts.primary") + "!"));
        }
        return true;
    }
}
