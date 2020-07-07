package me.frep.thotpatrol.checks.combat.velocity;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.events.PacketVelocityEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

public class VelocityA extends Check {

    public VelocityA(ThotPatrol ThotPatrol) {
        super("VelocityA", "Velocity (Type A)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(8);
    }

    @EventHandler
    public void onVelocity(PacketVelocityEvent e) {
        Player p = e.getPlayer();
        Vector v = e.getVelocity();
        Bukkit.broadcastMessage(p.getName() + " " + v.toString());
    }
}
