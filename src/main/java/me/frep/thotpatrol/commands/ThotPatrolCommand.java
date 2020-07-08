package me.frep.thotpatrol.commands;

import me.frep.thotpatrol.utils.TxtFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.utils.ChecksGUI;
import me.frep.thotpatrol.utils.Color;
import me.frep.thotpatrol.utils.UtilMath;

public class ThotPatrolCommand implements CommandExecutor {

    private ThotPatrol ThotPatrol;

    public ThotPatrolCommand(ThotPatrol ThotPatrol) {
        this.ThotPatrol = ThotPatrol;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("thotpatrol.admin")) {
            sender.sendMessage(Color.Red + "This server is running Thot Patrol v" + ThotPatrol.getVersion() + " by frep.");
            return true;
        }
        if (args.length == 0) {
            if (sender instanceof Player) {
                Player p = (Player) sender;
                ChecksGUI.openThotPatrolMain(p);
            } else {
                sender.sendMessage(Color.Red + "This is for players only! Do /thotpatrol help to find a command you can do here.");
            }
            return true;
        } else {
            if (args[0].equalsIgnoreCase("violations")) {
                if (sender instanceof Player) {
                    String playerName2 = args[1];
                    Player player = this.ThotPatrol.getServer().getPlayer(playerName2);
                    Player p = (Player) sender;
                    if (player == null || !player.isOnline()) {
                        sender.sendMessage(Color.Red + "This player is not online!");
                        return true;
                    }
                    ChecksGUI.openStatus(p, player);
                } else {
                    sender.sendMessage(Color.Red + "This is for players only!");
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("search")) {
                if (args.length == 1) {
                    sender.sendMessage(ThotPatrol.PREFIX + Color.Red + "Please specify a player name!");
                    return true;
                }
                String playerName2 = args[1];
                int totalLogs = TxtFile.getLogged(playerName2);
                if (totalLogs == 0) {
                    sender.sendMessage(ThotPatrol.PREFIX + Color.Red + "That player has no logs!");
                    return true;
                }
                int averagePing = TxtFile.averagePing(playerName2);
                double averageTps = TxtFile.averageTps(playerName2);
                if (args.length == 3) {
                    if (args[2].equalsIgnoreCase("KillAura")) {
                        int auraViolations = TxtFile.getViolations(playerName2, "Kill Aura");
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m------------------------"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Kill Aura results for &d" + playerName2 + "&7."));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&r"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Kill Aura Violations: &d" + auraViolations));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Average Ping: &d" + averagePing));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Average TPS: &d" + averageTps));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m------------------------"));
                        return true;
                    }
                    if (args[2].equalsIgnoreCase("Speed")) {
                        int speedViolations = TxtFile.getViolations(playerName2, "Speed");
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m------------------------"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Speed results for &d" + playerName2 + "&7."));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&r"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Speed Violations: &d" + speedViolations));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Average Ping: &d" + averagePing));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Average TPS: &d" + averageTps));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m------------------------"));
                        return true;
                    }
                    if (args[2].equalsIgnoreCase("Reach")) {
                        int reachViolations = TxtFile.getViolations(playerName2, "Reach");
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m------------------------"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Reach results for &d" + playerName2 + "&7."));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&r"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Reach Violations: &d" + reachViolations));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Average Ping: &d" + averagePing));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Average TPS: &d" + averageTps));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m------------------------"));
                        return true;
                    }
                    if (args[2].equalsIgnoreCase("Ascension")) {
                        int ascensionViolations = TxtFile.getViolations(playerName2, "Ascension");
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m------------------------"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Ascension results for &d" + playerName2 + "&7."));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&r"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Ascension Violations: &d" + ascensionViolations));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Average Ping: &d" + averagePing));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Average TPS: &d" + averageTps));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m------------------------"));
                        return true;
                    }
                    if (args[2].equalsIgnoreCase("AutoClicker")) {
                        int autoClickerViolations = TxtFile.getViolations(playerName2, "Auto Clicker");
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m------------------------"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Auto Clicker results for &d" + playerName2 + "&7."));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&r"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Auto Clicker Violations: &d" + autoClickerViolations));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Average Ping: &d" + averagePing));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Average TPS: &d" + averageTps));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m------------------------"));
                        return true;
                    }
                    if (args[2].equalsIgnoreCase("Fly")) {
                        int autoClickerViolations = TxtFile.getViolations(playerName2, "Fly");
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m------------------------"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Fly results for &d" + playerName2 + "&7."));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&r"));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Fly Violations: &d" + autoClickerViolations));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Average Ping: &d" + averagePing));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Average TPS: &d" + averageTps));
                        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m------------------------"));
                        return true;
                    }
                }
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m------------------------"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Search results for &d" + playerName2 + "&7."));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&r"));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Total Violations: &d" + totalLogs));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Average Ping: &d" + averagePing));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7Average TPS: &d" + averageTps));
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&m------------------------"));
                return true;
            }
            if (args[0].equalsIgnoreCase("ban")) {
                if (sender instanceof Player) {
                    String playerName2 = args[1];
                    if (args.length == 0) {
                        sender.sendMessage(Color.Red + "Usage: /thotpatrol ban (player)");
                        return true;
                    }
                    if (playerName2 == null) {
                        sender.sendMessage(Color.Red + "Invalid player!");
                    	return true;
                    }
                    String banCmd = this.ThotPatrol.getConfig().getString("bancmd");
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), banCmd.replaceAll("%player%", playerName2));
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', 
                    		ThotPatrol.getConfig().getString("broadcastmsg").replaceAll("%player%", playerName2)));
                } else {
                    sender.sendMessage(Color.Red + "This is for players only!");
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("resetdata")) {
                if (sender instanceof Player) {
                	ThotPatrol.resetData();
                	sender.sendMessage(Color.Green + "Data reset successfully!");
                } else {
                    sender.sendMessage(Color.Red + "This is for players only!");
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("dump")) {
                String playerName = args[1];
                String checkName = args[2];
                Check check = null;
                for (Check checkcheck : this.ThotPatrol.getChecks()) {
                    if (checkcheck.getIdentifier().equalsIgnoreCase(checkName)) {
                        check = checkcheck;
                    }
                }
                if (check == null) {
                    sender.sendMessage(Color.Red + "This check does not exist!");
                    return true;
                }
                String result = check.dump(playerName);
                if (result == null) {
                    sender.sendMessage(Color.Red + "Error creating dump file for player " + playerName + ".");
                }
                sender.sendMessage(ThotPatrol.PREFIX + Color.Gray + "Dropped dump thread at " + Color.Yellow + "/dumps/" + result + ".txt");
                return true;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                sender.sendMessage(ThotPatrol.PREFIX + Color.Gray + "Reloading ThotPatrol...");
                ThotPatrol.reloadConfig();
                sender.sendMessage(ThotPatrol.PREFIX + Color.Green + "Successfully reloaded ThotPatrol!");
                return true;
            }
            if (args[0].equalsIgnoreCase("clean") || args[0].equalsIgnoreCase("gc")) {
                sender.sendMessage(ThotPatrol.PREFIX + Color.Gray + "Forcing garbage collector..." + Color.Gray + "[" + Color.Aqua + ThotPatrol.getLag().getFreeRam() + Color.Gray + "/" + Color.Red + ThotPatrol.getLag().getMaxRam() + Color.Gray + "]");
                System.gc();
                sender.sendMessage(ThotPatrol.PREFIX + Color.Green + "Completed garbage collector! " + Color.Gray + "[" + Color.Aqua + UtilMath.trim(3, ThotPatrol.getLag().getFreeRam()) + Color.Gray + "/" + Color.Red + UtilMath.trim(3, ThotPatrol.getLag().getMaxRam()) + Color.Gray + "]");
                return true;
            }
            if (args[0].equalsIgnoreCase("lag") || args[0].equalsIgnoreCase("performance")) {
                sender.sendMessage(Color.DGray + Color.Strike + "----------------------------------------------------");
                sender.sendMessage(Color.Red + Color.Bold + "Performance Usage:");
                sender.sendMessage("");
                sender.sendMessage(Color.Gray + "TPS: " + Color.White + UtilMath.trim(2, ThotPatrol.getLag().getTPS()));
                sender.sendMessage(Color.Gray + "Free Ram: " + Color.White + ThotPatrol.getLag().getFreeRam() + "MB");
                sender.sendMessage(Color.Gray + "Max Ram: " + Color.White + ThotPatrol.getLag().getMaxRam() + "MB");
                sender.sendMessage(Color.Gray + "Used Ram: " + Color.White + Math.abs(ThotPatrol.getLag().getMaxRam() - ThotPatrol.getLag().getFreeRam()) + "MB");
                if (Math.abs(ThotPatrol.getLag().getMaxRam() - ThotPatrol.getLag().getFreeRam()) > ThotPatrol.getLag().getMaxRam() / 2.1) {
                    sender.sendMessage(Color.Aqua + Color.Italics + "It is recommended you do /thotpatrol clean to clear up some RAM.");
                }
                sender.sendMessage(ThotPatrol.getLag().getLag() > 20 ? Color.Red + "Server Usage: " + ThotPatrol.getLag().getLag() + "%" : Color.Green + "Server Usage: " + ThotPatrol.getLag().getLag() + "%");
                sender.sendMessage(Color.DGray + Color.Strike + "----------------------------------------------------");
                return true;
            }
            if (args[0].equalsIgnoreCase("verbose")) {
                if (!sender.hasPermission("thotpatrol.verbose")) {
                    sender.sendMessage(Color.Red + "No permission.");
                }
                Player p = (Player)sender;
                if (this.ThotPatrol.hasVerboseOn(p)) {
                    ThotPatrol.toggleVerbose(p);
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            ThotPatrol.PREFIX + ThotPatrol.getConfig().getString("alerts.primary") + "Verbose toggled " + Color.Red
                                    + "off" + ThotPatrol.getConfig().getString("alerts.primary") + "!"));
                } else {
                    ThotPatrol.toggleVerbose(p);
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&',
                            ThotPatrol.PREFIX + ThotPatrol.getConfig().getString("alerts.primary") + "Verbose toggled " + Color.Green
                                    + "on" + ThotPatrol.getConfig().getString("alerts.primary") + "!"));
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("help")) {
                sender.sendMessage(Color.DGray + Color.Strike + "----------------------------------------------------");
                sender.sendMessage(Color.Red + Color.Bold + "Thot Patrol Help:");
                sender.sendMessage(" ");
                sender.sendMessage(Color.Gray + "/thotpatrol" + Color.Reset + " help" + Color.Gray + "  - View the help page.");
                sender.sendMessage(Color.Gray + "/thotpatrol" + Color.Reset + " ping" + Color.Gray + "  - Get your ping.");
                sender.sendMessage(Color.Gray + "/thotpatrol" + Color.Reset + " reload" + Color.Gray + "   - Reload thotpatrol.");
                sender.sendMessage(Color.Gray + "/thotpatrol" + Color.Reset + " violations <player>" + Color.Gray + " - Gets the violations of a player.");
                sender.sendMessage(Color.Gray + "/thotpatrol" + Color.Reset + " lag" + Color.Gray + "  - Get server performance info.");
                sender.sendMessage(Color.DGray + Color.Strike + "----------------------------------------------------");
                return true;
            }
        }
        return true;
    }
}