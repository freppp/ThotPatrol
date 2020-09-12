package me.frep.thotpatrol.checks.combat.killaura;

import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.events.PacketAttackEvent;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillAuraL extends Check {

    private Map<UUID, Integer> verbose = new HashMap<>();

    public KillAuraL(me.frep.thotpatrol.ThotPatrol ThotPatrol) {
        super("KillAuraL", "Kill Aura (Type L)", ThotPatrol);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(8);
    }

    //simple wall check

    @EventHandler
    public void onAttack(PacketAttackEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = e.getPlayer();
        Player victim = (Player)e.getEntity();
        int count = verbose.getOrDefault(p.getUniqueId(), 0);
        int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
        if (victim.getLocation().getBlock().getRelative(BlockFace.NORTH).getType().isSolid()
                || victim.getLocation().getBlock().getRelative(BlockFace.SOUTH).getType().isSolid()
                || victim.getLocation().getBlock().getRelative(BlockFace.EAST).getType().isSolid()
                || victim.getLocation().getBlock().getRelative(BlockFace.WEST).getType().isSolid()
                || victim.getEyeLocation().clone().add(0, .5, 0).getBlock().getType().isSolid()
                || victim.getEyeLocation().getBlock().getRelative(BlockFace.NORTH).getType().isSolid()
                || victim.getEyeLocation().getBlock().getRelative(BlockFace.EAST).getType().isSolid()
                || victim.getEyeLocation().getBlock().getRelative(BlockFace.WEST).getType().isSolid()
                || victim.getEyeLocation().getBlock().getRelative(BlockFace.SOUTH).getType().isSolid()) {
            return;
        }
        if (!p.hasLineOfSight(victim)) {
            count++;
            getThotPatrol().verbose(this, p, ping, tps, Integer.toString(count));
        } else {
            if (count > 0) {
                count--;
            }
        }
        if (count >= 6) {
            count = 0;
            getThotPatrol().logCheat(this, p, "Wall | Ping: " + ping + " | TPS: " + tps);
            getThotPatrol().logToFile(p, this, "Wall", "TPS: " + tps + " | Ping: " + ping);
        }
        verbose.put(p.getUniqueId(), count);
    }
}