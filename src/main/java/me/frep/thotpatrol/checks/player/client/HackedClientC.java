package me.frep.thotpatrol.checks.player.client;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.utils.UtilTime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HackedClientC extends Check {

    private Map<UUID, Long> lastViolation = new HashMap<>();

    public HackedClientC(ThotPatrol ThotPatrol) {
        super("HackedClientC", "Hacked Client (Type C)", ThotPatrol);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(25);
    }


    //this will detect every client ever

    @EventHandler //for the memes lool
    public void onTabComplete(PlayerChatTabCompleteEvent e) {
        if (e.getChatMessage().startsWith(".") && UtilTime.elapsed(lastViolation.getOrDefault(e.getPlayer().getUniqueId(), 0L), 5000)) {
            getThotPatrol().logCheat(this, e.getPlayer(), e.getChatMessage());
            lastViolation.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
        }
    }
}