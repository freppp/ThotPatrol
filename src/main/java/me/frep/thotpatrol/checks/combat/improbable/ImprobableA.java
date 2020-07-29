package me.frep.thotpatrol.checks.combat.improbable;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;
import me.frep.thotpatrol.packets.PacketPlayerType;
import me.frep.thotpatrol.packets.events.PacketAttackEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ImprobableA extends Check {
	
	public static Map<UUID, Integer> lastTotalVL = new HashMap<UUID, Integer>();

    public ImprobableA(ThotPatrol ThotPatrol) {
        super("ImprobableA", "Improbable (Type A)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
        setMaxViolations(2);
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
    	lastTotalVL.remove(e.getPlayer().getUniqueId());
    }

	@EventHandler
	public void onAttack(PacketAttackEvent e) {
		Player p = e.getPlayer();
		UUID uuid = p.getUniqueId();
		if(e.getType() != PacketPlayerType.USE
            || p.hasPermission("thotpatrol.bypass")) {
			return;
		}
		int ping = getThotPatrol().getLag().getPing(p);
        double tps = getThotPatrol().getLag().getTPS();
		int totalVL = getTotalBlatantViolations(p);
		if (totalVL > 2 
				&& totalVL % 5 == 0
				&& totalVL != lastTotalVL.getOrDefault(p.getUniqueId(), 0)) {
			getThotPatrol().logCheat(this, p, "Improbable (Combat)" + " | Ping: " + ping + " | TPS: " + tps);
			getThotPatrol().logToFile(p, this, "Combined", "TPS: " + tps + " | Ping: " + ping);
		}
		lastTotalVL.put(uuid, totalVL);
	}
	
	private int getTotalBlatantViolations(Player p) {
        Check KillAuraA = getThotPatrol().getCheckByName("KillAuraA");
        Check KillAuraB = getThotPatrol().getCheckByName("KillAuraB");
        Check KillAuraC = getThotPatrol().getCheckByName("KillAuraC");
        Check KillAuraD = getThotPatrol().getCheckByName("KillAuraD");
        Check KillAuraE = getThotPatrol().getCheckByName("KillAuraE");
        Check KillAuraF = getThotPatrol().getCheckByName("KillAuraF");
        Check KillAuraG = getThotPatrol().getCheckByName("KillAuraG");
        Check KillAuraH = getThotPatrol().getCheckByName("KillAuraH");
        Check KillAuraI = getThotPatrol().getCheckByName("KillAuraI");
        Check KillAuraJ = getThotPatrol().getCheckByName("KillAuraJ");
        Check KillAuraK = getThotPatrol().getCheckByName("KillAuraK");
        Check KillAuraL = getThotPatrol().getCheckByName("KillAuraL");
        Check KillAuraM = getThotPatrol().getCheckByName("KillAuraM");
        Check ReachA = getThotPatrol().getCheckByName("ReachA");
        Check ReachB = getThotPatrol().getCheckByName("ReachB");
        Check ReachC = getThotPatrol().getCheckByName("ReachC");
        Check ReachD = getThotPatrol().getCheckByName("ReachD");
        Check AutoClickerA = getThotPatrol().getCheckByName("AutoClickerA");
        Check CriticalsA = getThotPatrol().getCheckByName("CriticalsA");
        Check CriticalsB = getThotPatrol().getCheckByName("CriticalsB");
        Check NoSlowdownA = getThotPatrol().getCheckByName("NoSlowdownA");
        Check NoSlowdownB = getThotPatrol().getCheckByName("NoSlowdownB");
        int killAuraAVL = getThotPatrol().getViolations(p, KillAuraA);
        int killAuraBVL = getThotPatrol().getViolations(p, KillAuraB);
        int killAuraCVL = getThotPatrol().getViolations(p, KillAuraC);
        int killAuraDVL = getThotPatrol().getViolations(p, KillAuraD);
        int killAuraEVL = getThotPatrol().getViolations(p, KillAuraE);
        int killAuraFVL = getThotPatrol().getViolations(p, KillAuraF);
        int killAuraGVL = getThotPatrol().getViolations(p, KillAuraG);
        int killAuraHVL = getThotPatrol().getViolations(p, KillAuraH);
        int killAuraIVL = getThotPatrol().getViolations(p, KillAuraI);
        int killAuraJVL = getThotPatrol().getViolations(p, KillAuraJ);
        int killAuraKVL = getThotPatrol().getViolations(p, KillAuraK);
        int killAuraLVL = getThotPatrol().getViolations(p, KillAuraL);
        int killAuraMVL = getThotPatrol().getViolations(p, KillAuraM);
        int reachAVL = getThotPatrol().getViolations(p, ReachA);
        int reachBVL = getThotPatrol().getViolations(p, ReachB);
        int reachCVL = getThotPatrol().getViolations(p, ReachC);
        int reachDVL = getThotPatrol().getViolations(p, ReachD);
        int autoClickerAVL = getThotPatrol().getViolations(p, AutoClickerA);
        int criticalsAVL = getThotPatrol().getViolations(p, CriticalsA);
        int criticalsBVL = getThotPatrol().getViolations(p, CriticalsB);
        int noSlowdownAVL = getThotPatrol().getViolations(p, NoSlowdownA);
        int noSlowdownBVL = getThotPatrol().getViolations(p, NoSlowdownB);
        return killAuraAVL + killAuraBVL + killAuraCVL 
        		+ killAuraDVL + killAuraEVL + killAuraFVL + killAuraGVL
                + killAuraHVL + killAuraIVL + killAuraJVL + killAuraKVL
                + killAuraLVL + killAuraMVL + noSlowdownAVL + noSlowdownBVL
        		+ reachAVL + reachBVL + reachCVL + reachDVL + autoClickerAVL
                + criticalsAVL + criticalsBVL;
	}
}