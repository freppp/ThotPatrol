package me.frep.thotpatrol.checks.player.scaffold;

import me.frep.thotpatrol.checks.Check;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class ScaffoldA extends Check {

    public ScaffoldA(me.frep.thotpatrol.ThotPatrol ThotPatrol) {
        super("ScaffoldA", "Scaffold (Type A) [#]", ThotPatrol);
        setEnabled(true);
        setBannable(false);
        setMaxViolations(3);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        double tps = getThotPatrol().getLag().getTPS();
        int ping = getThotPatrol().getLag().getPing(p);
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Block below = p.getLocation().subtract(0, 1, 0).getBlock();
            if (e.getClickedBlock().equals(below) && e.getBlockFace().equals(BlockFace.DOWN)) {
                getThotPatrol().logCheat(this, p, "Downwards | Ping: " + ping + " | TPS: " + tps);
                getThotPatrol().logToFile(p, this, "Impossible Block Place", "TPS: " + tps + " | Ping: " + ping);
            }
        }
    }
}