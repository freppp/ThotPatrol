package me.frep.thotpatrol.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;

import java.util.*;

public class ChecksGUI implements Listener {

    public static Inventory ThotPatrolMain = Bukkit.createInventory(null, 36, Color.Gold + "Home");

    public static HashMap<Integer, Inventory> ThotPatrolChecks = new HashMap<>();
    public static HashMap<Integer, Inventory> ThotPatrolBannable = new HashMap<>();
    public static HashMap<Integer, Inventory> ThotPatrolTimer = new HashMap<>();

    public static Inventory ThotPatrolStatus = Bukkit.createInventory(null, 27, Color.Gold + "Status");
    private static ItemStack back = createItem(Material.REDSTONE, 1, "&6Back");
    private static ThotPatrol ThotPatrol;

    public ChecksGUI(ThotPatrol ThotPatrol) {
        ChecksGUI.ThotPatrol = ThotPatrol;
        ItemStack checks = createItem(Material.COMPASS, 1, "&cChecks");
        ItemStack bannable = createItem(Material.REDSTONE, 1, "&cAuto Bans");
        ItemStack timers = createItem(Material.WATCH, 1, "&cTimers");
        ItemStack resetVio = createItem(Material.PAPER, 1, "&cReset Violations");
        ItemStack reload = createItem(Material.LAVA_BUCKET, 1, "&cReload");
        ItemStack info = createItem(Material.BOOK, 1, "&aInfo");
        ItemStack checkered = createItem(Material.COAL_BLOCK, 1, ThotPatrol.getConfig().getBoolean("settings.gui.checkered") ? "&aCheckered" : "&cCheckered");
        ItemMeta infom = info.getItemMeta();
        infom.setLore(infoLore());
        info.setItemMeta(infom);
        ThotPatrolMain.setItem(9, checks);
        ThotPatrolMain.setItem(13, timers);
        ThotPatrolMain.setItem(11, bannable);
        ThotPatrolMain.setItem(15, reload);
        ThotPatrolMain.setItem(17, resetVio);
        ThotPatrolMain.setItem(1, grayGlass());
        ThotPatrolMain.setItem(3, grayGlass());
        ThotPatrolMain.setItem(5, grayGlass());
        ThotPatrolMain.setItem(7, grayGlass());
        ThotPatrolMain.setItem(19, grayGlass());
        ThotPatrolMain.setItem(21, grayGlass());
        ThotPatrolMain.setItem(23, grayGlass());
        ThotPatrolMain.setItem(25, grayGlass());
        ThotPatrolMain.setItem(27, checkered);
        ThotPatrolMain.setItem(29, grayGlass());
        ThotPatrolMain.setItem(31, grayGlass());
        ThotPatrolMain.setItem(33, grayGlass());
        ThotPatrolMain.setItem(35, checkered);
        ThotPatrolMain.setItem(31, info);
        if (ThotPatrol.getConfig().contains("settings.gui.checkered")) {
            if (ThotPatrol.getConfig().getBoolean("settings.gui.checkered")) {
                ThotPatrolMain.setItem(0, whiteGlass());
                ThotPatrolMain.setItem(2, whiteGlass());
                ThotPatrolMain.setItem(4, whiteGlass());
                ThotPatrolMain.setItem(6, whiteGlass());
                ThotPatrolMain.setItem(8, whiteGlass());
                ThotPatrolMain.setItem(10, whiteGlass());
                ThotPatrolMain.setItem(12, whiteGlass());
                ThotPatrolMain.setItem(14, whiteGlass());
                ThotPatrolMain.setItem(16, whiteGlass());
                ThotPatrolMain.setItem(18, whiteGlass());
                ThotPatrolMain.setItem(20, whiteGlass());
                ThotPatrolMain.setItem(22, whiteGlass());
                ThotPatrolMain.setItem(24, whiteGlass());
                ThotPatrolMain.setItem(26, whiteGlass());
                ThotPatrolMain.setItem(28, whiteGlass());
                ThotPatrolMain.setItem(30, whiteGlass());
                ThotPatrolMain.setItem(32, whiteGlass());
                ThotPatrolMain.setItem(34, whiteGlass());
            } else {
                ThotPatrolMain.setItem(0, grayGlass());
                ThotPatrolMain.setItem(2, grayGlass());
                ThotPatrolMain.setItem(4, grayGlass());
                ThotPatrolMain.setItem(6, grayGlass());
                ThotPatrolMain.setItem(8, grayGlass());
                ThotPatrolMain.setItem(10, grayGlass());
                ThotPatrolMain.setItem(12, grayGlass());
                ThotPatrolMain.setItem(14, grayGlass());
                ThotPatrolMain.setItem(16, grayGlass());
                ThotPatrolMain.setItem(18, grayGlass());
                ThotPatrolMain.setItem(20, grayGlass());
                ThotPatrolMain.setItem(22, grayGlass());
                ThotPatrolMain.setItem(24, grayGlass());
                ThotPatrolMain.setItem(26, grayGlass());
                ThotPatrolMain.setItem(28, grayGlass());
                ThotPatrolMain.setItem(30, grayGlass());
                ThotPatrolMain.setItem(32, grayGlass());
                ThotPatrolMain.setItem(34, grayGlass());
            }
        } else {
            ThotPatrol.getConfig().set("settings.gui.checkered", true);
            ThotPatrolMain.setItem(0, whiteGlass());
            ThotPatrolMain.setItem(2, whiteGlass());
            ThotPatrolMain.setItem(4, whiteGlass());
            ThotPatrolMain.setItem(6, whiteGlass());
            ThotPatrolMain.setItem(8, whiteGlass());
            ThotPatrolMain.setItem(10, whiteGlass());
            ThotPatrolMain.setItem(12, whiteGlass());
            ThotPatrolMain.setItem(14, whiteGlass());
            ThotPatrolMain.setItem(16, whiteGlass());
            ThotPatrolMain.setItem(18, whiteGlass());
            ThotPatrolMain.setItem(20, whiteGlass());
            ThotPatrolMain.setItem(22, whiteGlass());
            ThotPatrolMain.setItem(24, whiteGlass());
            ThotPatrolMain.setItem(26, whiteGlass());
            ThotPatrolMain.setItem(28, whiteGlass());
            ThotPatrolMain.setItem(30, whiteGlass());
            ThotPatrolMain.setItem(32, whiteGlass());
            ThotPatrolMain.setItem(34, whiteGlass());
        }
    }

    private static ArrayList<String> infoLore() {
        ArrayList<String> list = new ArrayList<>();
        list.add(" ");
        list.add(ChatColor.translateAlternateColorCodes('&', "&7You can do &f/ThotPatrol help &7to see your"));
        list.add(ChatColor.translateAlternateColorCodes('&', "&7options for other &fcommands&7/&ffunctions&7!"));
        list.add(" ");
        list.add(ChatColor.translateAlternateColorCodes('&', "&7Current Version: &fv" + ThotPatrol.getDescription().getVersion()));
        return list;
    }

    public static void openThotPatrolMain(Player player) {
        player.openInventory(ThotPatrolMain);
    }

    public static void openStatus(Player player, Player target) {
        ThotPatrolStatus = Bukkit.createInventory(player, 27, Color.Gold + "Status");
        Map<Check, Integer> Checks = ThotPatrol.getViolations(target);
        if ((Checks == null) || (Checks.isEmpty())) {
            player.sendMessage(Color.Gray + "This player set off 0 checks.");
        } else {
            int slot = 0;
            for (Check Check : Checks.keySet()) {
                Integer Violations = Checks.get(Check);
                ItemStack vl = createItem(Material.PAPER, 1, Color.Aqua + Check.getName() + Color.DGray + " [" + Color.Red + Violations + Color.DGray + "]");
                ThotPatrolStatus.setItem(slot, vl);
                slot++;
            }
        }
        player.openInventory(ThotPatrolStatus);
    }

    public static ItemStack createItem(Material material, int amount, String name, String... lore) {
        ItemStack thing = new ItemStack(material, amount);
        ItemMeta thingm = thing.getItemMeta();
        thingm.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        thingm.setLore(Arrays.asList(lore));
        thing.setItemMeta(thingm);
        return thing;
    }

    public static ItemStack createGlass(Material material, int color, int amount, String name, String... lore) {
        ItemStack thing = new ItemStack(material, amount, (short) color);
        ItemMeta thingm = thing.getItemMeta();
        thingm.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        thingm.setLore(Arrays.asList(lore));
        thing.setItemMeta(thingm);
        return thing;
    }

    public static ItemStack grayGlass() {
        ItemStack thing = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);
        ItemMeta thingm = thing.getItemMeta();
        thingm.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b"));
        thing.setItemMeta(thingm);
        return thing;
    }

    public static ItemStack whiteGlass() {
        ItemStack thing = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0);
        ItemMeta thingm = thing.getItemMeta();
        thingm.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&b"));
        thing.setItemMeta(thingm);
        return thing;
    }

    public void openChecks(Player player, int pageSelect){
        int get = 0;
        List<Check> checks = ThotPatrol.getChecks();
        int totalChecks = checks.size();
        int maxP = (int) Math.ceil((double) totalChecks / (double) 45);

        for(int page = 1; page < maxP + 1; page++){
            Inventory inv = Bukkit.createInventory(null, 54, Color.Gold + "Checks: Toggle [" + page + "]");
            int slot = 0;
            for(int items = 0; items < 45; items++){
                if(get >= totalChecks){
                    break;
                }
                Check check = checks.get(get);
                if (ThotPatrol.getConfig().getBoolean("checks." + check.getIdentifier() + ".enabled")) {
                    ItemStack c = createGlass(Material.STAINED_GLASS_PANE, 5, 1, Color.Green + check.getName());
                    inv.setItem(slot, c);
                } else {
                    ItemStack c = createGlass(Material.STAINED_GLASS_PANE, 14, 1, Color.Red + check.getName());
                    inv.setItem(slot, c);
                }
                slot++;
                get++;
            }
            for (int i = slot; i < 45; i++) {
                ItemStack c = createGlass(Material.STAINED_GLASS_PANE, 15, 1, Color.Gray + "N/A");
                inv.setItem(i, c);
            }
            ItemStack previous = createItem(Material.ARROW, 1, ChatColor.GREEN  + "Previous Page");
            inv.setItem(46, previous);

            ItemStack next = createItem(Material.ARROW, 1, ChatColor.GREEN  + "Next Page");
            inv.setItem(51, next);

            inv.setItem(53, back);
            ThotPatrolChecks.put(page, inv);
        }
        player.openInventory(ThotPatrolChecks.get(pageSelect));
    }

    public void openAutoBans(Player player, int pageSelect) {
        int get = 0;
        List<Check> checks = ThotPatrol.getChecks();
        int totalChecks = checks.size();
        int maxP = (int) Math.ceil((double) totalChecks / (double) 45);
        for(int page = 1; page < maxP + 1; page++){
            Inventory inv = Bukkit.createInventory(null, 54, Color.Gold + "Checks: Bannable [" + page + "]");
            int slot = 0;
            for(int items = 0; items < 45; items++){
                if(get >= totalChecks){
                    break;
                }
                Check check = checks.get(get);
                if (ThotPatrol.getConfig().getBoolean("checks." + check.getIdentifier() + ".bannable")) {
                    ItemStack c = createGlass(Material.STAINED_GLASS_PANE, 5, 1, Color.Green + check.getName());
                    inv.setItem(slot, c);
                } else {
                    ItemStack c = createGlass(Material.STAINED_GLASS_PANE, 14, 1, Color.Red + check.getName());
                    inv.setItem(slot, c);
                }
                slot++;
                get++;
            }
            for (int i = slot; i < 45; i++) {
                ItemStack c = createGlass(Material.STAINED_GLASS_PANE, 15, 1, Color.Gray + "N/A");
                inv.setItem(i, c);
            }
            ItemStack previous = createItem(Material.ARROW, 1, ChatColor.GREEN  + "Previous Page");
            inv.setItem(46, previous);

            ItemStack next = createItem(Material.ARROW, 1, ChatColor.GREEN  + "Next Page");
            inv.setItem(51, next);

            inv.setItem(53, back);
            ThotPatrolBannable.put(page, inv);
        }
        player.openInventory(ThotPatrolBannable.get(pageSelect));
    }

    public void openTimer(Player player, int pageSelect) {
        int get = 0;
        List<Check> checks = ThotPatrol.getChecks();
        int totalChecks = checks.size();
        int maxP = (int) Math.ceil((double) totalChecks / (double) 45);
        for(int page = 1; page < maxP + 1; page++){
            Inventory inv = Bukkit.createInventory(null, 54, Color.Gold + "Checks: BanTimer [" + page + "]");
            int slot = 0;
            for(int items = 0; items < 45; items++){
                if(get >= totalChecks){
                    break;
                }
                Check check = checks.get(get);
                if (ThotPatrol.getConfig().getBoolean("checks." + check.getIdentifier() + ".banTimer")) {
                    ItemStack c = createGlass(Material.STAINED_GLASS_PANE, 5, 1, Color.Green + check.getName());
                    inv.setItem(slot, c);
                } else {
                    ItemStack c = createGlass(Material.STAINED_GLASS_PANE, 14, 1, Color.Red + check.getName());
                    inv.setItem(slot, c);
                }
                slot++;
                get++;
            }
            for (int i = slot; i < 45; i++) {
                ItemStack c = createGlass(Material.STAINED_GLASS_PANE, 15, 1, Color.Gray + "N/A");
                inv.setItem(i, c);
            }
            ItemStack previous = createItem(Material.ARROW, 1, ChatColor.GREEN  + "Previous Page");
            inv.setItem(46, previous);

            ItemStack next = createItem(Material.ARROW, 1, ChatColor.GREEN  + "Next Page");
            inv.setItem(51, next);

            inv.setItem(53, back);
            ThotPatrolTimer.put(page, inv);
        }
        player.openInventory(ThotPatrolTimer.get(pageSelect));
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getInventory().getName().equals(Color.Gold + "Home")) {
            Player player = (Player) e.getWhoClicked();
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
            if (e.getCurrentItem() == null) {
                return;
            }
            if (!e.getCurrentItem().hasItemMeta()) {
                return;
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().contains(ChatColor.translateAlternateColorCodes('&', "&cChecks"))) {
                openChecks(player, 1);
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&cAuto Bans"))) {
                openAutoBans(player, 1);
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&cTimers"))) {
                openTimer(player, 1);
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&cSoTW Mode"))) {
                if (ThotPatrol.getConfig().getBoolean("settings.sotwMode")) {
                    ThotPatrol.getConfig().set("settings.sotwMode", false);
                    ThotPatrol.saveConfig();
                    ItemStack sotwMode = createItem(ThotPatrol.getConfig().getBoolean("settings.sotwMode") ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK, 1, "&cSoTW Mode");
                    ThotPatrolMain.setItem(27, sotwMode);
                } else {
                    ThotPatrol.getConfig().set("settings.sotwMode", true);
                    ThotPatrol.saveConfig();
                    ItemStack sotwMode = createItem(ThotPatrol.getConfig().getBoolean("settings.sotwMode") ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK, 1, "&cSoTW Mode");
                    ThotPatrolMain.setItem(27, sotwMode);
                }
            }
            if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equals("Checkered")) {
                ThotPatrol.getConfig().set("settings.gui.checkered", !ThotPatrol.getConfig().getBoolean("settings.gui.checkered"));
                ThotPatrol.saveConfig();
                if (ThotPatrol.getConfig().contains("settings.gui.checkered")) {
                    if (ThotPatrol.getConfig().getBoolean("settings.gui.checkered")) {
                        ThotPatrolMain.setItem(0, whiteGlass());
                        ThotPatrolMain.setItem(2, whiteGlass());
                        ThotPatrolMain.setItem(4, whiteGlass());
                        ThotPatrolMain.setItem(6, whiteGlass());
                        ThotPatrolMain.setItem(8, whiteGlass());
                        ThotPatrolMain.setItem(10, whiteGlass());
                        ThotPatrolMain.setItem(12, whiteGlass());
                        ThotPatrolMain.setItem(14, whiteGlass());
                        ThotPatrolMain.setItem(16, whiteGlass());
                        ThotPatrolMain.setItem(18, whiteGlass());
                        ThotPatrolMain.setItem(20, whiteGlass());
                        ThotPatrolMain.setItem(22, whiteGlass());
                        ThotPatrolMain.setItem(24, whiteGlass());
                        ThotPatrolMain.setItem(26, whiteGlass());
                        ThotPatrolMain.setItem(28, whiteGlass());
                        ThotPatrolMain.setItem(30, whiteGlass());
                        ThotPatrolMain.setItem(32, whiteGlass());
                        ThotPatrolMain.setItem(34, whiteGlass());
                    } else {
                        ThotPatrolMain.setItem(0, grayGlass());
                        ThotPatrolMain.setItem(2, grayGlass());
                        ThotPatrolMain.setItem(4, grayGlass());
                        ThotPatrolMain.setItem(6, grayGlass());
                        ThotPatrolMain.setItem(8, grayGlass());
                        ThotPatrolMain.setItem(10, grayGlass());
                        ThotPatrolMain.setItem(12, grayGlass());
                        ThotPatrolMain.setItem(14, grayGlass());
                        ThotPatrolMain.setItem(16, grayGlass());
                        ThotPatrolMain.setItem(18, grayGlass());
                        ThotPatrolMain.setItem(20, grayGlass());
                        ThotPatrolMain.setItem(22, grayGlass());
                        ThotPatrolMain.setItem(24, grayGlass());
                        ThotPatrolMain.setItem(26, grayGlass());
                        ThotPatrolMain.setItem(28, grayGlass());
                        ThotPatrolMain.setItem(30, grayGlass());
                        ThotPatrolMain.setItem(32, grayGlass());
                        ThotPatrolMain.setItem(34, grayGlass());
                    }
                } else {
                    ThotPatrol.getConfig().set("settings.gui.checkered", true);
                    ThotPatrolMain.setItem(0, whiteGlass());
                    ThotPatrolMain.setItem(2, whiteGlass());
                    ThotPatrolMain.setItem(4, whiteGlass());
                    ThotPatrolMain.setItem(6, whiteGlass());
                    ThotPatrolMain.setItem(8, whiteGlass());
                    ThotPatrolMain.setItem(10, whiteGlass());
                    ThotPatrolMain.setItem(12, whiteGlass());
                    ThotPatrolMain.setItem(14, whiteGlass());
                    ThotPatrolMain.setItem(16, whiteGlass());
                    ThotPatrolMain.setItem(18, whiteGlass());
                    ThotPatrolMain.setItem(20, whiteGlass());
                    ThotPatrolMain.setItem(22, whiteGlass());
                    ThotPatrolMain.setItem(24, whiteGlass());
                    ThotPatrolMain.setItem(26, whiteGlass());
                    ThotPatrolMain.setItem(28, whiteGlass());
                    ThotPatrolMain.setItem(30, whiteGlass());
                    ThotPatrolMain.setItem(32, whiteGlass());
                    ThotPatrolMain.setItem(34, whiteGlass());
                }
            }
            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&cReset Violations"))) {
                ThotPatrol.resetAllViolations();
                ItemMeta meta = e.getCurrentItem().getItemMeta();
                meta.setDisplayName(Color.Green + Color.Italics + "Success!");
                e.getCurrentItem().setItemMeta(meta);
                new BukkitRunnable() {
                    public void run() {
                        ItemMeta meta = e.getCurrentItem().getItemMeta();
                        meta.setDisplayName(Color.Red + "Reset Violations");
                        e.getCurrentItem().setItemMeta(meta);
                    }
                }.runTaskLater(ThotPatrol, 40L);
            }

            if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&cReload"))) {
                ItemMeta meta = e.getCurrentItem().getItemMeta();
                meta.setDisplayName(Color.Red + Color.Italics + "Reloading...");
                e.getCurrentItem().setItemMeta(meta);
                ThotPatrol.reloadConfig();
                meta.setDisplayName(Color.Green + Color.Italics + "Success!");
                e.getCurrentItem().setItemMeta(meta);
                new BukkitRunnable() {
                    public void run() {
                        ItemMeta meta = e.getCurrentItem().getItemMeta();
                        meta.setDisplayName(Color.Red + "Reload");
                        e.getCurrentItem().setItemMeta(meta);
                        openThotPatrolMain(player);
                    }
                }.runTaskLater(ThotPatrol, 40L);
            }
        } else if (e.getInventory().getName().startsWith(Color.Gold + "Checks: Toggle")) {
            Player player = (Player) e.getWhoClicked();
            String[] spl = e.getInventory().getName().split("\\[");
            int page = Integer.parseInt(spl[1].replaceAll("\\]", ""));
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
            if (e.getCurrentItem() == null) {
                return;
            }
            if (e.getCurrentItem().hasItemMeta()) {
                String check_name = e.getCurrentItem().getItemMeta().getDisplayName();
                for (Check check : ThotPatrol.getChecks()) {
                    if (check.getName().equals(ChatColor.stripColor(check_name))) {
                        if (ThotPatrol.getConfig().getBoolean("checks." + check.getIdentifier() + ".enabled")) {
                            ThotPatrol.getConfig().set("checks." + check.getIdentifier() + ".enabled", false);
                            ThotPatrol.saveConfig();
                            ThotPatrol.reloadConfig();
                            check.setEnabled(false);
                            openChecks(player, page);
                            return;
                        }
                        ThotPatrol.getConfig().set("checks." + check.getIdentifier() + ".enabled", true);
                        ThotPatrol.saveConfig();
                        ThotPatrol.reloadConfig();
                        check.setEnabled(true);
                        openChecks(player,page);
                        return;
                    }
                }
                if (ChatColor.stripColor(check_name).equals("Back")) {
                    openThotPatrolMain(player);
                }
                if (ChatColor.stripColor(check_name).equals("Previous Page")) {
                    if(!ThotPatrolChecks.containsKey(page-1)){
                        player.sendMessage(Color.Red + "No previous page!");
                        return;
                    }
                    openChecks(player, page-1);
                }
                if (ChatColor.stripColor(check_name).equals("Next Page")) {
                    if(!ThotPatrolChecks.containsKey(page + 1)){
                        player.sendMessage(Color.Red + "No next page!");
                        return;
                    }
                    openChecks(player, page + 1);
                }
            }
        } else if (e.getInventory().getName().startsWith(Color.Gold + "Checks: Bannable")) {
            Player player = (Player) e.getWhoClicked();
            String[] spl = e.getInventory().getName().split("\\[");
            int page = Integer.parseInt(spl[1].replaceAll("\\]", ""));
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
            if (e.getCurrentItem() == null) {
                return;
            }
            if (e.getCurrentItem().hasItemMeta()) {
                String check_name = e.getCurrentItem().getItemMeta().getDisplayName();
                for (Check check : ThotPatrol.getChecks()) {
                    if (check.getName().contains(ChatColor.stripColor(check_name))) {
                        if (ThotPatrol.getConfig().getBoolean("checks." + check.getIdentifier() + ".bannable")) {
                            ThotPatrol.getConfig().set("checks." + check.getIdentifier() + ".banTimer", false);
                            ThotPatrol.getConfig().set("checks." + check.getIdentifier() + ".bannable", false);
                            ThotPatrol.saveConfig();
                            ThotPatrol.reloadConfig();
                            check.setBannable(false);
                            openAutoBans(player, page);
                            return;
                        }
                        ThotPatrol.getConfig().set("checks." + check.getIdentifier() + ".bannable", true);
                        ThotPatrol.saveConfig();
                        ThotPatrol.reloadConfig();
                        check.setBannable(true);
                        openAutoBans(player, page);
                        return;
                    }
                }
                if (ChatColor.stripColor(check_name).equals("Back")) {
                    openThotPatrolMain(player);
                }
                if (ChatColor.stripColor(check_name).equals("Previous Page")) {
                    if(!ThotPatrolBannable.containsKey(page-1)){
                        player.sendMessage(Color.Red + "No previous page!");
                        return;
                    }
                    openAutoBans(player, page-1);
                }
                if (ChatColor.stripColor(check_name).equals("Next Page")) {
                    if(!ThotPatrolBannable.containsKey(page + 1)){
                        player.sendMessage(Color.Red + "No next page!");
                        return;
                    }
                    openAutoBans(player, page + 1);
                }
            }
        } else if (e.getInventory().getName().startsWith(Color.Gold + "Checks: BanTimer")) {
            Player player = (Player) e.getWhoClicked();
            String[] spl = e.getInventory().getName().split("\\[");
            int page = Integer.parseInt(spl[1].replaceAll("\\]", ""));
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
            if (e.getCurrentItem() == null) {
                return;
            }
            if (e.getCurrentItem().hasItemMeta()) {
                String check_name = e.getCurrentItem().getItemMeta().getDisplayName();
                for (Check check : ThotPatrol.getChecks()) {
                    if (check.getName().equals(ChatColor.stripColor(check_name))) {
                        if (ThotPatrol.getConfig().getBoolean("checks." + check.getIdentifier() + ".bannable")) {
                            ThotPatrol.getConfig().set("checks." + check.getIdentifier() + ".banTimer", false);
                            ThotPatrol.getConfig().set("checks." + check.getIdentifier() + ".bannable", false);
                            ThotPatrol.saveConfig();
                            ThotPatrol.reloadConfig();
                            check.setAutobanTimer(false);
                            check.setBannable(false);
                            openTimer(player, page);
                            return;
                        }
                        ThotPatrol.getConfig().set("checks." + check.getIdentifier() + ".bannable", true);
                        ThotPatrol.getConfig().set("checks." + check.getIdentifier() + ".banTimer", true);
                        ThotPatrol.saveConfig();
                        ThotPatrol.reloadConfig();
                        check.setAutobanTimer(true);
                        check.setBannable(true);
                        openTimer(player, page);
                        return;
                    }
                }
                if (ChatColor.stripColor(check_name).equals("Back")) {
                    openThotPatrolMain(player);
                }
                if (ChatColor.stripColor(check_name).equals("Previous Page")) {
                    if(!ThotPatrolTimer.containsKey(page-1)){
                        player.sendMessage(Color.Red + "No previous page!");
                        return;
                    }
                    openTimer(player, page-1);
                }
                if (ChatColor.stripColor(check_name).equals("Next Page")) {
                    if(!ThotPatrolTimer.containsKey(page + 1)){
                        player.sendMessage(Color.Red + "No next page!");
                        return;
                    }
                    openTimer(player, page + 1);
                }
            }
        } else if (e.getInventory().getName().equals(Color.Gold + "Recent Bans")) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
        } else if (e.getInventory().getName().equals(Color.Gold + "Status")) {
            e.setCancelled(true);
            e.setResult(Event.Result.DENY);
        }
    }

    public String c(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }
}