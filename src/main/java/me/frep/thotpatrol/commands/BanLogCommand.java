package me.frep.thotpatrol.commands;

import org.apache.commons.io.FileUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.utils.Color;

import java.io.File;
import java.util.List;

public class BanLogCommand implements CommandExecutor {
	
    private ThotPatrol ThotPatrol;

    public BanLogCommand(ThotPatrol ThotPatrol) {
        this.ThotPatrol = ThotPatrol;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("thotpatrol.log") && !sender.hasPermission("thotpatrol.admin")) {
            sender.sendMessage(Color.Red + "No permission.");
            return true;
        }
        if (args.length != 2) {
            sender.sendMessage(ThotPatrol.PREFIX + Color.Red + "Usage: /banlog <name> <page>");
            return true;
        }
        String player = args[0];
        int page = Integer.parseInt(args[1]);
        String path = ThotPatrol.getDataFolder() + File.separator + "banlogs" + File.separator + args[0] + ".txt";
        File file = new File(path);
        if (!file.exists()) {
            sender.sendMessage(ThotPatrol.PREFIX + Color.Red + "The player '" + Color.Bold + player + Color.Red + "' does not have a ban log! This is CASE SENSITIVE!");
            return true;
        }
        try {
            List<String> lines = FileUtils.readLines(file, "UTF-8");
            if ((lines.size() / (page * 10)) < 1) {
                sender.sendMessage(ThotPatrol.PREFIX + Color.Red + "There is no page " + page + " for this log!");
                return true;
            }
            sender.sendMessage(Color.DGray + Color.Strike + "----------------------------------------------------");
            sender.sendMessage(Color.Gray + "Log for " + Color.White + player + Color.Red + " Page " + page);
            sender.sendMessage("");
            for (int i = (page - 1) * 10; (i) < page * 10; i++) {
                if (i < lines.size()) {
                    sender.sendMessage(lines.get(i));
                }
            }
            sender.sendMessage(Color.DGray + Color.Strike + "----------------------------------------------------");
        } catch (Exception e) {
            sender.sendMessage(ThotPatrol.PREFIX + Color.Red + "Unknown error occurred when trying to read file and upload to PasteBin!");
            e.printStackTrace();
        }
        return true;
    }
}