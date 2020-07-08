package me.frep.thotpatrol.commands;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.utils.Color;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LogsCommand implements CommandExecutor {

    private me.frep.thotpatrol.ThotPatrol ThotPatrol;

    public LogsCommand(ThotPatrol ThotPatrol) {
        this.ThotPatrol = ThotPatrol;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("thotpatrol.log") && !sender.hasPermission("thotpatrol.admin")) {
            sender.sendMessage(Color.Red + "No permission.");
            return true;
        }
        if (args.length != 2) {
            sender.sendMessage(ThotPatrol.PREFIX + Color.Red + "Usage: /logs <name> <page>");
            return true;
        }
        String player = args[0];
        int page = Integer.parseInt(args[1]);
        String path = ThotPatrol.getDataFolder() + "/violations.txt";
        File file = new File(path);
        if (!file.exists()) {
            sender.sendMessage(ThotPatrol.PREFIX + Color.Red + "There is no violations.txt file!");
            return true;
        } try {
            List <String> lines = new ArrayList<>();
            Scanner in = new Scanner(file);
            while (in.hasNext()) {
                String line = in.nextLine();
                if (line.contains(player)) {
                    lines.add(line);
                }
            }
            if (lines.isEmpty()) {
                sender.sendMessage(Color.translate(Color.Red + "There are no logs for this player!"));
                return true;
            }
            sender.sendMessage(Color.translate("&8&m-----&r&8[&7Logs for &d" + player + " &7(Page " + page + ")&8]&m-----"));
            for (int i = (page - 1) * 10; (i) < page * 10; i++) {
                if (i < lines.size()) {
                    String[] spl = lines.get(i).split("failed");
                    sender.sendMessage(Color.Gray + spl[1]);
                }
            }
            sender.sendMessage(Color.translate("&8&m-----&r&8[&7Logs for &d" + player + " &7(Page " + page + ")&8]&m-----"));
            if ((lines.size() / (page * 10)) < 1) {
                sender.sendMessage(ThotPatrol.PREFIX + Color.Red + "There is no page " + page + " for this log!");
                return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return true;
    }
}
