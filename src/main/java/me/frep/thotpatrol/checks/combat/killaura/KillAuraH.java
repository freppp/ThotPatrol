package me.frep.thotpatrol.checks.combat.killaura;

import com.comphenix.protocol.wrappers.EnumWrappers;
import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.events.PacketUseEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillAuraH extends Check {

	public KillAuraH(ThotPatrol ThotPatrol) {
		super("KillAuraH", "Kill Aura (Type H)", ThotPatrol);
		setEnabled(true);
		setBannable(true);
		setMaxViolations(9);
		setViolationResetTime(120000);
	}
}