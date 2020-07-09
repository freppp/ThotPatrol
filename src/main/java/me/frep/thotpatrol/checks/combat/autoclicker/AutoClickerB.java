package me.frep.thotpatrol.checks.combat.autoclicker;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.WeakHashMap;

public class AutoClickerB extends Check {
	
    public static WeakHashMap<Player, ClickProfile> profiles = new WeakHashMap<>();
	
    public AutoClickerB(ThotPatrol ThotPatrol) {
        super("AutoClickerB", "Auto Clicker (Type B)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(8);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        if (e.getAction() != Action.LEFT_CLICK_AIR) {
            return;
        }
        Player p = e.getPlayer();
        if (p.hasPermission("thotpatrol.bypass")) return;
        ClickProfile clickProfile = null;
        if (!profiles.containsKey(p)) {
            clickProfile = new ClickProfile();
            profiles.put(p, clickProfile);
        } else {
            clickProfile = profiles.get(p);
        }
        clickProfile.analyzeClicks(p);
    }

    public class ClickProfile {
    	
        public double clicks;
        private long clickSprint;
        private double lastCPS;
        private double twoSecondsAgoCPS;
        private double threeSecondsAgoCPS;
        private int count;

        public ClickProfile() {
            clicks = 0.0;
            clickSprint = 0;
            lastCPS = 0.0;
            twoSecondsAgoCPS = 0.0;
            threeSecondsAgoCPS = 0.0;
        }

        public void analyzeClicks(Player p) {
            long l = System.currentTimeMillis();
            int ping = getThotPatrol().getLag().getPing(p);
            double tps = getThotPatrol().getLag().getTPS();
            if (l - this.clickSprint >= 1000) {
                shuffleDown();
                clickSprint = l;
                clicks = 0.0;
                if (isConstant()) {
                    ++count;
                    getThotPatrol().verbose(AutoClickerB.this, p, ping, tps, lastCPS + "/" + twoSecondsAgoCPS + "/" + threeSecondsAgoCPS);
                    if (count >= 4) {
                        count = 0;
                        getThotPatrol().logCheat(AutoClickerB.this, p, "Constant CPS: " + lastCPS + " | Ping:" + ping + " | TPS: " + tps);
                    	getThotPatrol().logToFile(p, AutoClickerB.this, "Patterns", "CPS[1s/2s/3s]: " 
                        + lastCPS + "/" + twoSecondsAgoCPS + "/" + threeSecondsAgoCPS 
                        + " | TPS: " + tps + " | Ping: " + ping);
                    	dumplog(p, "[Patterns] CPS[1s/2s/3s]: " 
                        + lastCPS + "/" + twoSecondsAgoCPS + "/" + threeSecondsAgoCPS 
                        + " | TPS: " + tps + " | Ping: " + ping);
                    }
                }
            }
            clicks += 1.0;
        }

        private void shuffleDown() {
            threeSecondsAgoCPS = twoSecondsAgoCPS;
            twoSecondsAgoCPS = lastCPS;
            lastCPS = clicks;
        }

        private boolean isConstant() {
            if (threeSecondsAgoCPS >= 9.0) {
                if (lastCPS == twoSecondsAgoCPS && twoSecondsAgoCPS == threeSecondsAgoCPS) {
                    return true;
                }
                return false;
            }
            return false;
        }
    }
}
