package me.frep.thotpatrol.checks.combat.killaura;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.PacketPlayerType;
import me.frep.thotpatrol.packets.events.PacketAttackEvent;
import me.frep.thotpatrol.utils.UtilCheat;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillAuraB extends Check {
    public static Map<UUID, Map.Entry<Integer, Long>> AuraTicks;

    public KillAuraB(ThotPatrol ThotPatrol) {
        super("KillAuraB", "Kill Aura (Type B)", ThotPatrol);
        AuraTicks = new HashMap<>();
        setEnabled(true);
        setBannable(true);
        setMaxViolations(12);
        setViolationResetTime(120000);
    }

    @EventHandler
    public void onLog(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        AuraTicks.remove(uuid);
    }

    // hitbox check

    @EventHandler
    public void UseEntity(PacketAttackEvent e) {
		Player damager = e.getPlayer();
		if (damager == null) {
			return;
		}
		if (!(e.getEntity() instanceof Player)) {
			return;
		}
		if(e.getType() != PacketPlayerType.USE) {
			return;
		}
		Player player = (Player)e.getEntity();
		if (damager.getAllowFlight()) {
			return;
		}
        int Count = 0;
        long Time = System.currentTimeMillis();
        if (AuraTicks.containsKey(damager.getUniqueId())) {
            Count = AuraTicks.get(damager.getUniqueId()).getKey();
            Time = AuraTicks.get(damager.getUniqueId()).getValue();
        }
		if (damager.hasPermission("thotpatrol.bypass")) {
			return;
		}
        double OffsetXZ = UtilCheat.getAimbotoffset(damager.getLocation(), damager.getEyeHeight(), player);
        double LimitOffset = 200.0;
        if (damager.getVelocity().length() > 0.08
                || this.getThotPatrol().LastVelocity.containsKey(damager.getUniqueId())) {
            LimitOffset += 600.0;
        }
        int Ping = getThotPatrol().getLag().getPing(damager);
        if (Ping >= 100 && Ping < 200) {
            LimitOffset += 50.0;
        } else if (Ping >= 200 && Ping < 250) {
            LimitOffset += 75.0;
        } else if (Ping >= 250 && Ping < 300) {
            LimitOffset += 150.0;
        } else if (Ping >= 300 && Ping < 350) {
            LimitOffset += 300.0;
        } else if (Ping >= 350 && Ping < 400) {
            LimitOffset += 400.0;
        } else if (Ping > 400) return;
        if (OffsetXZ > LimitOffset * 4.0) {
            Count += 8;
        } else if (OffsetXZ > LimitOffset * 3.0) {
            Count += 6;
        } else if (OffsetXZ > LimitOffset * 2.0) {
            Count += 4;
        } else if (OffsetXZ > LimitOffset) {
            Count += 2;
        }
        if (OffsetXZ > 4000) {
        	dumplog(player, "Would fail Kill Aura (Type B) but OffSet" + OffsetXZ + " is too high.");
        	return;
        }
        if (AuraTicks.containsKey(damager.getUniqueId()) && UtilTime.elapsed(Time, 60000L)) {
            Count = 0;
            Time = UtilTime.nowlong();
        }
        int ping = getThotPatrol().getLag().getPing(damager);
        double tps = getThotPatrol().getLag().getTPS();
        if (Count >= 16) {
        	getThotPatrol().logCheat(this, damager, "Hitbox" + " | Ping: " + ping + " | TPS: " + tps);
        	getThotPatrol().logToFile(damager, this, "Hitbox", "Offset: " 
                    + OffsetXZ + " > " + LimitOffset + " | Count: " + Count + " | TPS: " + tps + " | Ping: " + ping);
            dumplog(damager, "Offset: " + OffsetXZ + ", Ping: " + Ping + ", Max Offset: " + LimitOffset + " | Count: " + Count);
            Count = 0;
        }
        AuraTicks.put(damager.getUniqueId(), new AbstractMap.SimpleEntry<>(Count, Time));
    }
}