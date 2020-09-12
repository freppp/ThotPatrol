package me.frep.thotpatrol;

import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.checks.combat.aimpattern.AimPatternA;
import me.frep.thotpatrol.checks.combat.autoclicker.AutoClickerA;
import me.frep.thotpatrol.checks.combat.autoclicker.AutoClickerB;
import me.frep.thotpatrol.checks.combat.autoclicker.AutoClickerC;
import me.frep.thotpatrol.checks.combat.criticals.CriticalsA;
import me.frep.thotpatrol.checks.combat.criticals.CriticalsB;
import me.frep.thotpatrol.checks.combat.criticals.CriticalsC;
import me.frep.thotpatrol.checks.combat.improbable.ImprobableA;
import me.frep.thotpatrol.checks.combat.killaura.*;
import me.frep.thotpatrol.checks.combat.misc.FastBowA;
import me.frep.thotpatrol.checks.combat.misc.NoSwingA;
import me.frep.thotpatrol.checks.combat.reach.*;
import me.frep.thotpatrol.checks.movement.ascension.AscensionA;
import me.frep.thotpatrol.checks.movement.ascension.AscensionB;
import me.frep.thotpatrol.checks.movement.ascension.AscensionC;
import me.frep.thotpatrol.checks.movement.ascension.AscensionD;
import me.frep.thotpatrol.checks.movement.fastclimb.FastClimbA;
import me.frep.thotpatrol.checks.movement.fly.*;
import me.frep.thotpatrol.checks.movement.invalidmove.InvalidMoveA;
import me.frep.thotpatrol.checks.movement.invalidmove.InvalidMoveB;
import me.frep.thotpatrol.checks.movement.invalidmove.InvalidMoveC;
import me.frep.thotpatrol.checks.movement.invalidmove.InvalidMoveD;
import me.frep.thotpatrol.checks.movement.jesus.JesusA;
import me.frep.thotpatrol.checks.movement.misc.GravityA;
import me.frep.thotpatrol.checks.movement.misc.VClipA;
import me.frep.thotpatrol.checks.movement.nofall.NoFallA;
import me.frep.thotpatrol.checks.movement.nofall.NoFallB;
import me.frep.thotpatrol.checks.movement.noslowdown.NoSlowdownA;
import me.frep.thotpatrol.checks.movement.sneak.SneakA;
import me.frep.thotpatrol.checks.movement.speed.*;
import me.frep.thotpatrol.checks.movement.spider.SpiderA;
import me.frep.thotpatrol.checks.movement.spider.SpiderB;
import me.frep.thotpatrol.checks.movement.spider.SpiderC;
import me.frep.thotpatrol.checks.movement.sprint.SprintA;
import me.frep.thotpatrol.checks.movement.step.StepA;
import me.frep.thotpatrol.checks.movement.timer.TimerA;
import me.frep.thotpatrol.checks.player.badpackets.*;
import me.frep.thotpatrol.checks.player.client.HackedClientA;
import me.frep.thotpatrol.checks.player.client.HackedClientB;
import me.frep.thotpatrol.checks.player.client.HackedClientC;
import me.frep.thotpatrol.checks.player.scaffold.ScaffoldA;
import me.frep.thotpatrol.checks.player.scaffold.ScaffoldB;
import me.frep.thotpatrol.commands.*;
import me.frep.thotpatrol.data.DataManager;
import me.frep.thotpatrol.events.MoveEvent;
import me.frep.thotpatrol.events.SharedEvents;
import me.frep.thotpatrol.packets.PacketCore;
import me.frep.thotpatrol.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;

public class ThotPatrol extends JavaPlugin implements Listener {

    public static ThotPatrol Instance;
    public String PREFIX;
    public PacketCore packet;
    public LagCore lag;
    private DataManager dataManager;
    public List<Check> Checks;
    public Map<UUID, Map<Check, Integer>> Violations;
    public Map<UUID, Map<Check, Long>> ViolationReset;
    public List<Player> AlertsOn;
    public List<Player> verboseOn;
    public Map<Player, Map.Entry<Check, Long>> AutoBan;
    public Map<String, Check> NamesBanned;
    public Map<UUID, Long> LastVelocity;
    public Map<UUID, Long> lastDamage;
    public Map<UUID, Long> lastKnockback;
    public Integer pingToCancel = getConfig().getInt("settings.latency.ping");
    public Integer tpsToCancel = getConfig().getInt("settings.latency.tps");

    public ThotPatrol() {
        super();
        Checks = new ArrayList<>();
        Violations = new HashMap<>();
        ViolationReset = new HashMap<>();
        AlertsOn = new ArrayList<>();
        verboseOn = new ArrayList<>();
        AutoBan = new HashMap<>();
        NamesBanned = new HashMap<>();
        LastVelocity = new HashMap<>();
        lastDamage = new HashMap<>();
        lastKnockback = new HashMap<>();
    }

    public void onEnable() {
        ThotPatrol.Instance = this;
        new UtilBlock();
        dataManager = new DataManager();
        registerListeners();
        addDataPlayers();
        PacketCore.init();
        packet = new PacketCore(this);
        lag = new LagCore(this);
        addChecks();
        HackedClientA vapers = new HackedClientA(this);
        getServer().getMessenger().registerIncomingPluginChannel(this, "LOLIMAHCKER", vapers);
        for (Check check : Checks) {
            if (check.isEnabled()) {
                RegisterListener(check);
            }
        }
        File file = new File(getDataFolder(), "config.yml");
        getCommand("jday").setExecutor(new JDayCommand(this));
        getCommand("alerts").setExecutor(new AlertsCommand(this));
        getCommand("autoban").setExecutor(new AutobanCommand(this));
        getCommand("thotpatrol").setExecutor(new ThotPatrolCommand(this));
        getCommand("banlog").setExecutor(new BanLogCommand(this));
        getCommand("logs").setExecutor(new LogsCommand(this));
        Bukkit.getServer().getPluginManager().registerEvents(new Latency(this), this);
        Bukkit.getServer().getPluginManager().registerEvents(new ChecksGUI(this), this);
        RegisterListener(this);
        if (!file.exists()) {
            getConfig().addDefault("prefix", "&8[&d&l!&8] ");
            getConfig().addDefault("alerts.primary", "&7");
            getConfig().addDefault("alerts.secondary", "&d");
            getConfig().addDefault("alerts.checkColor", "&b");
            getConfig().addDefault("jday-bancmd", "ban %player% &8[&d&lThot Patrol&8] &bJudgement Day v1 -s");
            getConfig().addDefault("bancmd", "ban %player% &8[&d&lThot Patrol&8] &bUnfair Advantage -s");
            getConfig().addDefault("jday-broadcastmsg", "&d%player% &7has been banned for &8[&d&lThot Patrol&8] &bJudgement Day v1");
            getConfig().addDefault("broadcastmsg", "&d&lThot Patrol &7has detected &d%player% &7to be cheating and has removed them from the network.");
            getConfig().addDefault("settings.resetViolationsAutomatically", false);
            getConfig().addDefault("settings.violationResetTime", 600);
            getConfig().addDefault("settings.gui.checkered", true);
            getConfig().addDefault("settings.bypassOP", false);
            getConfig().addDefault("settings.autoClickerAClickSpeed", 20);
            getConfig().addDefault("settings.disableLogFile", false);
            getConfig().addDefault("settings.disableAlertsToConsole", false);
            getConfig().addDefault("settings.autoEnableAlertsOnJoin", true);
            getConfig().addDefault("settings.bypassBelowOnePing", false);
            getConfig().addDefault("instantBans.AutoClickerA.enabled", true);
            getConfig().addDefault("instantBans.AutoClickerA.maxCPS", 30);
            getConfig().addDefault("instantBans.AutoClickerA.maxPing", 200);
            getConfig().addDefault("instantBans.AutoClickerA.minTPS", 19.75);
            getConfig().addDefault("instantBans.AutoClickerA.banAlertMessage", "&d%player% &7was banned for &bAuto Clicker (Type A) &7[&d%CPS% CPS&7]");
            getConfig().addDefault("instantBans.SpeedA.enabled", true);
            getConfig().addDefault("instantBans.SpeedA.maxSpeedPercentage", 50);
            getConfig().addDefault("instantBans.SpeedA.maxPing", 200);
            getConfig().addDefault("instantBans.SpeedA.minTPS", 19.75);
            getConfig().addDefault("instantBans.SpeedA.banAlertMessage", "&d%player% &7was banned for &bSpeed (Type A) &7[&d%speed%%&7]");
            getConfig().addDefault("instantBans.SpeedB.enabled", true);
            getConfig().addDefault("instantBans.SpeedB.maxSpeed", 1.2);
            getConfig().addDefault("instantBans.SpeedB.maxPing", 200);
            getConfig().addDefault("instantBans.SpeedB.minTPS", 19.75);
            getConfig().addDefault("instantBans.SpeedB.banAlertMessage", "&d%player% &7was banned for &bSpeed (Type B) &7[&d%speed%&7]");
            getConfig().addDefault("instantBans.SpeedC.enabled", true);
            getConfig().addDefault("instantBans.SpeedC.maxSpeed", .75);
            getConfig().addDefault("instantBans.SpeedC.maxPing", 200);
            getConfig().addDefault("instantBans.SpeedC.minTPS", 19.75);
            getConfig().addDefault("instantBans.SpeedC.banAlertMessage", "&d%player% &7was banned for &bSpeed (Type C) &7[&d%speed%&7]");
            getConfig().addDefault("instantBans.SpeedD.enabled", true);
            getConfig().addDefault("instantBans.SpeedD.maxSpeed", 1.4);
            getConfig().addDefault("instantBans.SpeedD.maxPing", 200);
            getConfig().addDefault("instantBans.SpeedD.minTPS", 19.75);
            getConfig().addDefault("instantBans.SpeedD.banAlertMessage", "&d%player% &7was banned for &bSpeed (Type D) &7[&d%speed%&7]");
            getConfig().addDefault("instantBans.SpeedE.enabled", true);
            getConfig().addDefault("instantBans.SpeedE.maxSpeed", .40);
            getConfig().addDefault("instantBans.SpeedE.maxPing", 200);
            getConfig().addDefault("instantBans.SpeedE.minTPS", 19.75);
            getConfig().addDefault("instantBans.SpeedE.banAlertMessage", "&d%player% &7was banned for &bSpeed (Type E) &7[&d%speed%&7]");
            getConfig().addDefault("instantBans.SpeedH.enabled", true);
            getConfig().addDefault("instantBans.SpeedH.maxSpeed", .75);
            getConfig().addDefault("instantBans.SpeedH.maxPing", 200);
            getConfig().addDefault("instantBans.SpeedH.minTPS", 19.75);
            getConfig().addDefault("instantBans.SpeedH.banAlertMessage", "&d%player% &7was banned for &bSpeed (Type H) &7[&d%speed%&7]");
            getConfig().addDefault("instantBans.SpeedI.enabled", true);
            getConfig().addDefault("instantBans.SpeedI.maxSpeed", .65);
            getConfig().addDefault("instantBans.SpeedI.maxPing", 200);
            getConfig().addDefault("instantBans.SpeedI.minTPS", 19.75);
            getConfig().addDefault("instantBans.SpeedI.banAlertMessage", "&d%player% &7was banned for &bSpeed (Type I) &7[&d%speed%&7]");
            getConfig().addDefault("instantBans.SprintA.enabled", true);
            getConfig().addDefault("instantBans.SprintA.maxDelta", 1.25);
            getConfig().addDefault("instantBans.SprintA.maxPing", 200);
            getConfig().addDefault("instantBans.SprintA.minTPS", 19.75);
            getConfig().addDefault("instantBans.SprintA.banAlertMessage", "&d%player% &7was banned for &bSprint (Type A) &7[&d%delta%&7]");
            getConfig().addDefault("instantBans.TimerA.enabled", true);
            getConfig().addDefault("instantBans.TimerA.maxPackets", 28);
            getConfig().addDefault("instantBans.TimerA.maxPing", 200);
            getConfig().addDefault("instantBans.TimerA.minTPS", 19.75);
            getConfig().addDefault("instantBans.TimerA.banAlertMessage", "&d%player% &7was banned for &bTimer (Type A) &7[&d%packets%&7]");
            getConfig().addDefault("instantBans.AscensionA.enabled", true);
            getConfig().addDefault("instantBans.AscensionA.maxHeight", 3.50);
            getConfig().addDefault("instantBans.AscensionA.maxPing", 200);
            getConfig().addDefault("instantBans.AscensionA.minTPS", 19.75);
            getConfig().addDefault("instantBans.AscensionA.banAlertMessage", "&d%player% &7was banned for &bAscension (Type A) &7[&d%height%&7]");
            getConfig().addDefault("instantBans.AscensionD.enabled", true);
            getConfig().addDefault("instantBans.AscensionD.maxHeight", .95);
            getConfig().addDefault("instantBans.AscensionD.maxPing", 200);
            getConfig().addDefault("instantBans.AscensionD.minTPS", 19.75);
            getConfig().addDefault("instantBans.AscensionD.banAlertMessage", "&d%player% &7was banned for &bAscension (Type D) &7[&d%height%&7]");
            getConfig().addDefault("instantBans.FastClimbA.enabled", true);
            getConfig().addDefault("instantBans.FastClimbA.maxSpeed", .23);
            getConfig().addDefault("instantBans.FastClimbA.maxPing", 200);
            getConfig().addDefault("instantBans.FastClimbA.minTPS", 19.75);
            getConfig().addDefault("instantBans.FastClimbA.banAlertMessage", "&d%player% &7was banned for &bFast Climb (Type A) &7[&d%speed%&7]");
            for (Check check : Checks) {
                getConfig().addDefault("checks." + check.getIdentifier() + ".enabled", check.isEnabled());
                getConfig().addDefault("checks." + check.getIdentifier() + ".bannable", check.isBannable());
                getConfig().addDefault("checks." + check.getIdentifier() + ".banTimer", check.hasBanTimer());
                getConfig().addDefault("checks." + check.getIdentifier() + ".maxViolations", check.getMaxViolations());
            }
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
        for (Check check : Checks) {
            if (!getConfig().isConfigurationSection("checks." + check.getIdentifier())) {
                getConfig().set("checks." + check.getIdentifier() + ".enabled", check.isEnabled());
                getConfig().set("checks." + check.getIdentifier() + ".bannable", check.isBannable());
                getConfig().set("checks." + check.getIdentifier() + ".banTimer", check.hasBanTimer());
                getConfig().set("checks." + check.getIdentifier() + ".maxViolations", check.getMaxViolations());
                saveConfig();
            }
        }
        PREFIX = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"));
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player online : Bukkit.getServer().getOnlinePlayers()) {
                    if (getConfig().getBoolean("settings.resetViolationsAutomatically")) {
                        if (online.hasPermission("thotpatrol.admin")) {
                            System.out.println("[Thot Patrol] Reset violations for all players.");
                            online.sendMessage(PREFIX + ChatColor.translateAlternateColorCodes('&', "&7Reset violations for all players!"));
                        }
                    }
                    resetAllViolations();
                }
            }
        }.runTaskTimer(this, 0L, getConfig().getLong("settings.violationResetTime") * 20);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("thotpatrol.alerts") || p.hasPermission("thotpatrol.admin")) {
                toggleAlerts(p);
            }
        }
    }

    public void onDisable() {
        Instance = null;
    }

    public void addChecks() {
        Checks.add(new ImprobableA(this));
        Checks.add(new AutoClickerA(this));
        Checks.add(new AutoClickerB(this));
        Checks.add(new AutoClickerC(this));
        Checks.add(new KillAuraA(this));
        Checks.add(new KillAuraB(this));
        Checks.add(new KillAuraC(this));
        Checks.add(new KillAuraD(this));
        Checks.add(new KillAuraE(this));
        Checks.add(new KillAuraF(this));
        Checks.add(new KillAuraG(this));
        Checks.add(new KillAuraH(this));
        Checks.add(new KillAuraI(this));
        Checks.add(new KillAuraJ(this));
        Checks.add(new KillAuraK(this));
        Checks.add(new KillAuraL(this));
        Checks.add(new KillAuraM(this));
        Checks.add(new KillAuraN(this));
        Checks.add(new AimPatternA(this));
        Checks.add(new NoSwingA(this));
        Checks.add(new ReachA(this));
        Checks.add(new ReachB(this));
        Checks.add(new ReachC(this));
        Checks.add(new ReachD(this));
        Checks.add(new ReachE(this));
        Checks.add(new BadPacketsA(this));
        Checks.add(new BadPacketsB(this));
        Checks.add(new BadPacketsC(this));
        Checks.add(new BadPacketsD(this));
        Checks.add(new BadPacketsE(this));
        Checks.add(new BadPacketsF(this));
        Checks.add(new FastClimbA(this));
        Checks.add(new SpiderA(this));
        Checks.add(new SpiderB(this));
        Checks.add(new SpiderC(this));
        Checks.add(new AscensionA(this));
        Checks.add(new AscensionB(this));
        Checks.add(new AscensionC(this));
        Checks.add(new AscensionD(this));
        Checks.add(new SpeedA(this));
        Checks.add(new SpeedB(this));
        Checks.add(new SpeedC(this));
        Checks.add(new SpeedD(this));
        Checks.add(new SpeedE(this));
        Checks.add(new SpeedF(this));
        Checks.add(new SpeedG(this));
        Checks.add(new SpeedH(this));
        Checks.add(new SpeedI(this));
        Checks.add(new FlyA(this));
        Checks.add(new FlyB(this));
        Checks.add(new FlyC(this));
        Checks.add(new FlyD(this));
        Checks.add(new FlyE(this));
        Checks.add(new InvalidMoveA(this));
        Checks.add(new InvalidMoveB(this));
        Checks.add(new InvalidMoveC(this));
        Checks.add(new InvalidMoveD(this));
        Checks.add(new StepA(this));
        Checks.add(new JesusA(this));
        Checks.add(new NoFallA(this));
        Checks.add(new NoFallB(this));
        Checks.add(new VClipA(this));
        Checks.add(new SprintA(this));
        Checks.add(new FastBowA(this));
        Checks.add(new NoSlowdownA(this));
        Checks.add(new CriticalsA(this));
        Checks.add(new CriticalsB(this));
        Checks.add(new CriticalsC(this));
        Checks.add(new TimerA(this));
        Checks.add(new SneakA(this));
        Checks.add(new HackedClientA(this));
        Checks.add(new HackedClientB(this));
        Checks.add(new HackedClientC(this));
        Checks.add(new GravityA(this));
        Checks.add(new ScaffoldA(this));
        Checks.add(new ScaffoldB(this));
    }

    public void resetAllViolations() {
        Violations.clear();
        ViolationReset.clear();
    }

    public String resetData() {
        try {
            resetAllViolations();
            clearNamesBanned();
            CriticalsA.CritTicks.clear();
            KillAuraA.ClickTicks.clear();
            KillAuraA.Clicks.clear();
            KillAuraA.LastMS.clear();
            KillAuraB.AuraTicks.clear();
            KillAuraC.Differences.clear();
            KillAuraC.LastLocation.clear();
            KillAuraC.AimbotTicks.clear();
            KillAuraD.lastAttack.clear();
            BadPacketsA.FastHealTicks.clear();
            BadPacketsA.LastHeal.clear();
            AscensionA.AscensionTicks.clear();
            FlyA.flyTicksA.clear();
            FlyD.flyTicks.clear();
            NoFallA.FallDistance.clear();
            NoFallA.NoFallTicks.clear();
            NoSlowdownA.speedTicks.clear();
            SpeedA.speedTicks.clear();
            SpeedA.tooFastTicks.clear();
            BadPacketsD.lastPacket.clear();
            BadPacketsD.packetTicks.clear();
            SneakA.sneakTicks.clear();
            FastBowA.count.clear();
            ReachA.verbose.clear();
            ReachB.verbose.clear();
        } catch (Exception e) {
            return ChatColor.translateAlternateColorCodes('&', PREFIX + Color.Red + "Unknown error occurred!");
        }
        return ChatColor.translateAlternateColorCodes('&', PREFIX + Color.Green + "Successfully reset data!");
    }

    public Integer getPingCancel() {
        return pingToCancel;
    }

    public Integer getTPSCancel() {
        return tpsToCancel;
    }

    public List<Check> getChecks() {
        return new ArrayList<>(Checks);
    }

    public String getVersion() {
        return getDescription().getVersion();
    }

    public Map<String, Check> getNamesBanned() {
        return new HashMap<>(NamesBanned);
    }

    public void clearNamesBanned() {
        NamesBanned.clear();
    }

    public List<Player> getAutoBanQueue() {
        return new ArrayList<>(AutoBan.keySet());
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new SharedEvents(), this);
        getServer().getPluginManager().registerEvents(new MoveEvent(), this);
        getServer().getPluginManager().registerEvents(new UtilVelocity(), this);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        getDataManager().addPlayerData(e.getPlayer());
        NamesBanned.remove(e.getPlayer().getName());
    }

    public Check getCheckByName(String identifier) {
        for (Check checkcheck : getChecks()) {
            if (checkcheck.getIdentifier().equalsIgnoreCase(identifier)) {
                return checkcheck;
            }
        }
        return null;
    }

    public void createLog(Player player, Check checkBanned) {
        TxtFile logFile = new TxtFile(this, File.separator + "banlogs", player.getName());
        Map<Check, Integer> Checks = getViolations(player);
        logFile.addLine("----- " + player.getName() + " was banned for " + checkBanned.getName() + " -----");
        logFile.addLine("Failed checks:");
        for (Check check : Checks.keySet()) {
            Integer vl = Checks.get(check);
            logFile.addLine(" - " + check.getName() + " (" + vl + " VL)");
        }
        logFile.addLine(" ");
        logFile.addLine("Dump Log for all checks set off:");
        for (Check check : Checks.keySet()) {
            logFile.addLine(" ");
            logFile.addLine(check.getName() + ":");
            if (check.getDump(player) != null) {
                for (String line : check.getDump(player)) {
                    logFile.addLine(line);
                }
            } else {
                logFile.addLine("Check had no dump logs!");
            }
            logFile.addLine(" ");
        }
        logFile.write();
    }

    public void logToFile(Player p, Check c, String checkType, String message) {
        try {
            if (p == null
                    || !p.isOnline()
                    || getNamesBanned().containsKey(p.getName())
                    || getAutoBanQueue().contains(p)
                    || NamesBanned.containsKey(p.getName())
                    || getConfig().getBoolean("settings.disableLogFile")) {
                return;
            }
            File dataFolder = getInstance().getDataFolder();
            Map<Check, Integer> checks = getViolations(p);
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd|HH:mm:ss");
            if (!checks.containsKey(c)) return;
            Integer vl = checks.getOrDefault(c, 0);
            if (!dataFolder.exists()) {
                dataFolder.mkdir();
            }
            File saveTo = new File(getInstance().getDataFolder(), "violations.txt");
            if (!saveTo.exists()) {
                saveTo.createNewFile();
            }
            FileWriter fw = new FileWriter(saveTo, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println("[" + sdf.format(cal.getTime()) + "]"
                    + "[" + p.getUniqueId().toString() + "] " + p.getName()
                    + " failed " + (c.isJudgmentDay() ? "[JD] " : "") + c.getName()
                    + (!c.getName().contains("#") ? " [@]" : "")
                    + " (Check: " + checkType + ")" + " [VL: " + vl + "] | " + message);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ThotPatrol getInstance() {
        return Instance;
    }

    public void removeFromAutoBanQueue(Player p) {
        AutoBan.remove(p);
    }

    public void removeViolations(Player p) {
        Violations.remove(p.getUniqueId());
    }

    public boolean hasAlertsOn(Player p) {
        return AlertsOn.contains(p);
    }

    public boolean hasVerboseOn(Player p) {
        return verboseOn.contains(p);
    }

    public void toggleAlerts(Player p) {
        if (hasAlertsOn(p)) {
            AlertsOn.remove(p);
        } else {
            AlertsOn.add(p);
        }
    }

    public void toggleVerbose(Player p) {
        if (hasVerboseOn(p)) {
            verboseOn.remove(p);
        } else {
            verboseOn.add(p);
        }
    }

    public LagCore getLag() {
        return lag;
    }

    @EventHandler
    public void Join(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (p.hasPermission("thotpatrol.alerts") || p.hasPermission("thotpatrol.admin")) {
            if (!getConfig().getBoolean("settings.autoEnableAlertsOnJoin")) {
                AlertsOn.add(p);
            }
        }
    }

    public void verbose(Check check, Player player, Integer ping, Double tps, String info) {
        if (player.hasPermission("thotpatrol.bypass")
                || player.isOp() && getConfig().getBoolean("settings.bypassOP")) {
            return;
        }
        for (Player p : verboseOn) {
            p.sendMessage(PREFIX + (ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.secondary")
                    + player.getName() + (ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.primary")
                    + " verbosed " + (ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.checkColor") + check.getName())
                    + (ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.primary") + " [Ping: "
                    + (ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.secondary") + ping
                    + (ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.primary") + "] [TPS: "
                    + (ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.secondary") + Math.round(tps)
                    + (ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.primary") + "] [" + info + "]"))))))))))))))));
        }
    }

    public Integer getViolations(Player player, Check check) {
        if (Violations.containsKey(player.getUniqueId())) {
            if (Violations.get(player.getUniqueId()).containsKey(check)) {
                return Violations.get(player.getUniqueId()).get(check);
            }
        }
        return 0;
    }

    public Map<Check, Integer> getViolations(Player player) {
        if (Violations.containsKey(player.getUniqueId())) {
            return new HashMap<>(Violations.get(player.getUniqueId()));
        }
        return null;
    }

    public void addViolation(Player player, Check check) {
        Map<Check, Integer> map = new HashMap<>();
        if (Violations.containsKey(player.getUniqueId())) {
            map = Violations.get(player.getUniqueId());
        }
        if (!map.containsKey(check)) {
            map.put(check, 1);
        } else {
            map.put(check, map.get(check) + 1);
        }
        Violations.put(player.getUniqueId(), map);
    }

    public void removeViolations(Player player, Check check) {
        if (Violations.containsKey(player.getUniqueId())) {
            Violations.get(player.getUniqueId()).remove(check);
        }
    }

    public void setViolationResetTime(Player player, Check check, long time) {
        Map<Check, Long> map = new HashMap<>();
        if (this.ViolationReset.containsKey(player.getUniqueId())) {
            map = this.ViolationReset.get(player.getUniqueId());
        }
        map.put(check, time);
        ViolationReset.put(player.getUniqueId(), map);
    }

    public void autoBanOver(Player player) {
        final Map<Player, Map.Entry<Check, Long>> AutoBan = new HashMap<>(this.AutoBan);
        if (AutoBan.containsKey(player)) {
            banPlayer(player, AutoBan.get(player).getKey());
            AutoBan.remove(player);
        }
    }

    public void autoBan(Check check, Player player) {
        if (lag.getTPS() < 17.0) return;
        if (NamesBanned.containsKey(player.getName())) {
            return;
        }
        if (check.hasBanTimer()) {
            if (AutoBan.containsKey(player)) {
                return;
            }
            AutoBan.put(player, new AbstractMap.SimpleEntry<>(check, System.currentTimeMillis() + 10000L));
            System.out.println("[" + player.getUniqueId().toString() + "] " + player.getName() + " will be banned in 15s for " + check.getName() + ".");
            final UtilActionMessage msg = new UtilActionMessage();
            msg.addText(PREFIX);
            msg.addText(ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.secondary") + player.getName())).addHoverText(Color.Gray + "(Click to teleport to " + Color.Red + player.getName() + Color.Gray + ")").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/tp " + player.getName());
            msg.addText(ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.primary") + " set off " + getConfig().getString("alerts.secondary") + check.getName() + getConfig().getString("alerts.primary") + " and will " + getConfig().getString("alerts.primary") + "be " + getConfig().getString("alerts.primary") + "banned" + getConfig().getString("alerts.primary") + " if you don't take action. " + Color.DGray + Color.Bold + "["));
            msg.addText(ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.secondary") + Color.Bold + "ban")).addHoverText(Color.Gray + "Autoban " + Color.Green + player.getName()).setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/autoban ban " + player.getName());
            msg.addText(ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.primary")) + " or ");
            msg.addText(Color.Green + Color.Bold + "cancel").addHoverText(Color.Gray + "Click to Cancel").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/autoban cancel " + player.getName());
            msg.addText(Color.DGray + Color.Bold + "]");
            ArrayList<Player> players;
            for (int length = (players = UtilServer.getOnlinePlayers()).size(), i = 0; i < length; ++i) {
                Player playerplayer = players.get(i);
                if (playerplayer.hasPermission("thotpatrol.alerts")) {
                    msg.sendToPlayer(playerplayer);
                }
            }
        } else {
            banPlayer(player, check);
            Bukkit.getScheduler().scheduleAsyncDelayedTask(ThotPatrol.Instance, new Runnable() {
                @Override
                public void run() {
                    NamesBanned.remove(player.getUniqueId());
                }
            }, 50);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void Velocity(PlayerVelocityEvent e) {
        LastVelocity.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)
            || !(e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK))
            || e.isCancelled()) {
            return;
        }
        lastDamage.put(e.getEntity().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onKnockBack(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)
                || !(e.getEntity() instanceof Player)
                || !(e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK))) {
            return;
        }
        Player p = (Player)e.getDamager();
        Player victim = (Player)e.getEntity();
        if (p.getItemInHand() == null) return;
        if (p.getItemInHand().getEnchantmentLevel(Enchantment.KNOCKBACK) > 0) {
            lastKnockback.put(victim.getUniqueId(), System.currentTimeMillis());
        }
    }

    public void banPlayer(Player p, Check check) {
        NamesBanned.put(p.getName(), check);
        createLog(p, check);
        removeViolations(p, check);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Latency.getLag(p) < 1000) {
                    if (getConfig().getBoolean("testmode")) {
                        p.sendMessage(PREFIX + Color.Gray + "You would be banned right now for: " + Color.Red + check.getName());
                    } else {
                        if (!getConfig().getString("broadcastmsg").equalsIgnoreCase("")) {
                            Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', getConfig().getString("broadcastmsg").replaceAll("%player%", p.getName())));
                        }
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), getConfig().getString("bancmd").replaceAll("%player%", p.getName()).replaceAll("%check%", check.getName()));
                    }
                }
                NamesBanned.put(p.getName(), check);
            }
        }.runTaskLater(this, 1L);
        Violations.remove(p.getUniqueId());
    }

    public void alert(String msg) {
        for (Player p : AlertsOn) {
            p.sendMessage(PREFIX + msg);
        }
    }

    public void RegisterListener(Listener l) {
        getServer().getPluginManager().registerEvents(l, this);
    }

    public Map<UUID, Long> getLastVelocity() {
        return this.LastVelocity;
    }

    @EventHandler
    public void onKick(PlayerKickEvent e) {
        if (e.getReason().equals("Flying is not enabled on this server")) {
            alert(Color.Purple + e.getPlayer().getName() + Color.Gray + " was kicked for flying!");
        }
        if (e.getReason().equals("Invalid move packet received")) {
            alert(Color.Purple + e.getPlayer().getName() + Color.Gray + " was kicked for invalid move packets!");
        }
        if (e.getReason().contains("Too many packet")) {
            alert(Color.Purple + e.getPlayer().getName() + Color.Gray + " was kicked for too many packets!");
        }
    }

    public void logCheat(Check check, Player player, String hoverableText, String... identifiers) {
        StringBuilder a = new StringBuilder();
        if (identifiers != null) {
            for (String b : identifiers) {
                a.append(" (").append(b).append(")");
            }
        }
        if (player.isOp() && getConfig().getBoolean("settings.bypassOP")) {
            return;
        }
        if (getLag().getPing(player) < 1 && getConfig().getBoolean("settings.bypassBelowOnePing")) return;
        addViolation(player, check);
        setViolationResetTime(player, check, System.currentTimeMillis() + check.getViolationResetTime());
        Integer violations = getViolations(player, check);
        if (!getConfig().getBoolean("settings.disableAlertsToConsole")) {
            System.out.println("[" + player.getUniqueId().toString() + "] " + player.getName() + " failed " + (check.isJudgmentDay() ? "[JD] " : "") + check.getName() + a + " [" + violations + " VL]");
        }
        if (violations >= check.getViolationsToNotify()) {
            UtilActionMessage msg = new UtilActionMessage();
            msg.addText(PREFIX);
            msg.addText(ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.secondary")) + player.getName()).addHoverText(ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.primary")) + "Click to teleport to " + ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.secondary")) + player.getName() + ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.primary")) + ")").setClickEvent(UtilActionMessage.ClickableType.RunCommand, "/tp " + player.getName());
            msg.addText(ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.primary")) + " failed " + (check.isJudgmentDay() ? Color.DRed + "[JD] " : ""));
            UtilActionMessage.AMText CheckText = msg.addText(ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.checkColor")) + check.getName());
            if (hoverableText != null) {
                CheckText.addHoverText(hoverableText);
            }
            msg.addText(ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.secondary")) + a + ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.primary")) + " ");
            msg.addText(ChatColor.translateAlternateColorCodes('&', ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.primary")) + "[VL: " + ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.secondary")) + violations + ChatColor.translateAlternateColorCodes('&', getConfig().getString("alerts.primary")) + "]"));
            if (violations % check.getViolationsToNotify() == 0) {
                if (getConfig().getBoolean("testmode")) {
                    msg.sendToPlayer(player);
                } else {
                    for (Player playerplayer : AlertsOn) {
                        if (check.isJudgmentDay() && !playerplayer.hasPermission("thotpatrol.alerts")) {
                            continue;
                        }
                        msg.sendToPlayer(playerplayer);
                    }
                }
            }
            if (check.isJudgmentDay()) {
                return;
            }
            if (violations > check.getMaxViolations() - 1 && check.isBannable()) {
                autoBan(check, player);
            }
        }
    }

    public void startTimer(Player p) {
        MoveEvent.ticksLeft.put(p.getName(), MoveEvent.defaultWait);
        MoveEvent.cooldownTask.put(p.getName(), new BukkitRunnable() {
            @Override
            public void run() {
                MoveEvent.ticksLeft.put(p.getName(), MoveEvent.ticksLeft.get(p.getName()) - 1);
                if (MoveEvent.ticksLeft.get(p.getName()) == 0) {
                    MoveEvent.ticksLeft.remove(p.getName());
                    MoveEvent.cooldownTask.remove(p.getName());
                    Bukkit.getServer().getScheduler().cancelTask(getTaskId());
                    cancel();
                }
            }
        });
        MoveEvent.cooldownTask.get(p.getName()).runTaskTimer(this, 0L, 1L);
    }

    private void addDataPlayers() {
        for (final Player playerLoop : Bukkit.getOnlinePlayers()) {
            Instance.getDataManager().addPlayerData(playerLoop);
        }
    }

    public DataManager getDataManager() {
        return dataManager;
    }
}