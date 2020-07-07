package me.frep.thotpatrol.checks.player.badpackets;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.PacketPlayerType;
import me.frep.thotpatrol.packets.events.PacketAttackEvent;
import me.frep.thotpatrol.packets.events.PacketPlayerEvent;
import me.frep.thotpatrol.packets.events.PacketSwingArmEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BadPacketsE extends Check {

    public BadPacketsE(ThotPatrol ThotPatrol) {
        super("BadPacketsE", "Bad Packets (Type E) [#]", ThotPatrol);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(5);
    }

    @EventHandler
    public void onAttack(PacketAttackEvent e) {
        Player p = e.getPlayer();
        if (!(e.getEntity() instanceof Player)) return;
        if (e.getPlayer() == e.getEntity()) {
            getThotPatrol().logCheat(this, p, null);
        }
    }
}