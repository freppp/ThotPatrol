package me.frep.thotpatrol.checks.combat.killaura;

import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.events.PacketAttackEvent;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillAuraN extends Check {

    private Map<UUID, Long> lastClick = new HashMap<>();
    private Map<UUID, Integer> verbose = new HashMap<>();

    // i dont know what this is either LOL

    public KillAuraN(me.frep.thotpatrol.ThotPatrol ThotPatrol) {
        super("KillAuraN", "Kill Aura (Type N)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(8);
        setViolationResetTime(120000);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        lastClick.put(e.getWhoClicked().getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onAttack(PacketAttackEvent e) {
        Player p = e.getPlayer();
        if (UtilTime.elapsed(lastClick.getOrDefault(p.getUniqueId(), 0L), 100)
            || p.hasPermission("thotpatrol.bypass")) {
            return;
        }
        long lastClickTime = lastClick.getOrDefault(p.getUniqueId(), 0L);
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        double tps = getThotPatrol().getLag().getTPS();
        double ping = getThotPatrol().getLag().getPing(p);
        double delta = System.currentTimeMillis() - lastClickTime;
        if (delta < 14) count++;
        if (count > 4) {
            count = 0;
            getThotPatrol().logCheat(this, p, "Delta: " + delta + " | Ping: " + ping + " | TPS: " + tps);
            getThotPatrol().logToFile(p, this, "Packet", "Delta: " + delta + " | TPS: " + tps + " | Ping: " + ping);
        }
        verbose.put(p.getUniqueId(), count);
    }
}