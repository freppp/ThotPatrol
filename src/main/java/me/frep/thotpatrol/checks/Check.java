package me.frep.thotpatrol.checks;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.utils.TxtFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Check implements Listener {
	
    public Map<String, List<String>> DumpLogs = new HashMap<>();
    private String Identifier;
    private String Name;
	private ThotPatrol ThotPatrol;
    private boolean Enabled = true;
    private boolean BanTimer = false;
    private boolean Bannable = false;
    private boolean JudgementDay = false;
    private Integer MaxViolations = 5;
    private Integer ViolationsToNotify = 1;
    private Long ViolationResetTime = 6000000L;

    public Check(String Identifier, String Name, ThotPatrol ThotPatrol) {
        this.Name = Name;
        this.ThotPatrol = ThotPatrol;
        this.Identifier = Identifier;
    }

    public void dumplog(Player player, String log) {
        if (!DumpLogs.containsKey(player.getName())) {
            List<String> logs = new ArrayList<>();
            logs.add(log);
            DumpLogs.put(player.getName(), logs);
        } else {
            DumpLogs.get(player.getName()).add(log);
        }
    }

    public boolean isEnabled() {
        return this.Enabled;
    }

    public void setEnabled(boolean Enabled) {
        if (ThotPatrol.getConfig().getBoolean("checks." + this.getIdentifier() + ".enabled") != Enabled
                && ThotPatrol.getConfig().get("checks." + this.getIdentifier() + ".enabled") != null) {
            this.Enabled = ThotPatrol.getConfig().getBoolean("checks." + this.getIdentifier() + ".enabled");
            return;
        }
        if (Enabled) {
            if (!isEnabled()) {
                ThotPatrol.RegisterListener(this);
            }
        } else if (isEnabled()) {
            HandlerList.unregisterAll(this);
        }
        this.Enabled = Enabled;
    }

    public boolean isBannable() {
        return this.Bannable;
    }

    public void setBannable(boolean Bannable) {
        if (ThotPatrol.getConfig().getBoolean("checks." + this.getIdentifier() + ".bannable") != Bannable
                && ThotPatrol.getConfig().get("checks." + this.getIdentifier() + ".bannable") != null) {
            this.Bannable = ThotPatrol.getConfig().getBoolean("checks." + this.getIdentifier() + ".bannable");
            return;
        }
        this.Bannable = Bannable;
    }

    public boolean hasBanTimer() {
        return this.BanTimer;
    }

    public boolean isJudgmentDay() {
        return this.JudgementDay;
    }

    public ThotPatrol getThotPatrol() {
        return this.ThotPatrol;
    }

    public boolean hasDump(Player player) {
        return DumpLogs.containsKey(player.getName());
    }

    public void clearDump(Player player) {
        DumpLogs.remove(player.getName());
    }

    public void clearDumps() {
        DumpLogs.clear();
    }

    public Integer getMaxViolations() {
        return this.MaxViolations;
    }

    public void setMaxViolations(int MaxViolations) {
        if (ThotPatrol.getConfig().getInt("checks." + this.getIdentifier() + ".maxViolations") != MaxViolations
                && ThotPatrol.getConfig().get("checks." + this.getIdentifier() + ".maxViolations") != null) {
            this.MaxViolations = ThotPatrol.getConfig().getInt("checks." + this.getIdentifier() + ".maxViolations");
            return;
        }
        this.MaxViolations = MaxViolations;
    }

    public Integer getViolationsToNotify() {
        return this.ViolationsToNotify;
    }

    public void setViolationsToNotify(int ViolationsToNotify) {
        this.ViolationsToNotify = ViolationsToNotify;
    }

    public Long getViolationResetTime() {
        return this.ViolationResetTime;
    }

    public void setViolationResetTime(long ViolationResetTime) {
        this.ViolationResetTime = ViolationResetTime;
    }
    public void checkValues() {
        if (ThotPatrol.getConfig().getBoolean("checks." + this.getIdentifier() + ".enabled")) {
            this.setEnabled(true);
        } else {
            this.setEnabled(false);
        }
        if (ThotPatrol.getConfig().getBoolean("checks." + this.getIdentifier() + ".bannable")) {
            this.setBannable(true);
        } else {
            this.setEnabled(false);
        }
    }

    public void setAutobanTimer(boolean BanTimer) {
        if ((ThotPatrol.getConfig().getBoolean("checks." + this.getIdentifier() + ".banTimer") != BanTimer
                && ThotPatrol.getConfig().get("checks." + this.getIdentifier() + ".banTimer") != null)) {
            this.BanTimer = ThotPatrol.getConfig().getBoolean("checks." + this.getIdentifier() + ".banTimer");
            return;
        }
        this.BanTimer = BanTimer;
    }

    public void setJudgementDay(boolean JudgementDay) {
        this.JudgementDay = JudgementDay;
    }
    
    public boolean isJudgementDay(boolean JudgementDay) {
    	return this.JudgementDay;
    }

    public String getName() {
        return this.Name;
    }

    public String getIdentifier() {
        return this.Identifier;
    }

    public List<String> getDump(Player player) {
        return this.DumpLogs.get(player.getName());
    }

    public String dump(String player) {
        if (!DumpLogs.containsKey(player)) {
            return null;
        }
        TxtFile file = new TxtFile(getThotPatrol(), "/Dumps", player);
        file.clear();
        for (String Line : DumpLogs.get(player)) {
            file.addLine(Line);
        }
        file.write();
        return file.getName();
    }
}