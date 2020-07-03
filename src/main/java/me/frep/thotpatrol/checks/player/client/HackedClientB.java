package me.frep.thotpatrol.checks.player.client;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.json.simple.parser.JSONParser;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import me.frep.thotpatrol.ThotPatrol;
import me.frep.thotpatrol.checks.Check;

public class HackedClientB extends Check implements PluginMessageListener, Listener {
	
	private static String type;
	private final JSONParser parser = new JSONParser();
	public final static Map<UUID, Map<String, String>> forgeMods = new HashMap<>();
		
	public HackedClientB(ThotPatrol ThotPatrol) {
		super("HackedClientB", "Hacked Client (Type B)", ThotPatrol);
        setEnabled(true);
        setBannable(true);
		setMaxViolations(0);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
	public void onJoin(PlayerJoinEvent e) {
		getClientType(e.getPlayer());
		}
		@Override
		@SuppressWarnings("unchecked")
		public void onPluginMessageReceived(String string, Player p, byte[] arrby) {
			final ByteArrayDataInput byteArrayDataInput = ByteStreams.newDataInput(arrby);
			if ("ForgeMods".equals(byteArrayDataInput.readUTF())) {
				final String string2 = byteArrayDataInput.readUTF();
				try {
					@SuppressWarnings("rawtypes")
					final
					Map map = (Map)this.parser.parse(string2);
					forgeMods.put(p.getUniqueId(), map);
					final String string3 = getClientType(p);
			        double ping = getThotPatrol().getLag().getPing(p);
			        double tps = getThotPatrol().getLag().getTPS();
					if (string3 != null) {
						type = string3;
						getThotPatrol().logCheat(this, p, "[1] Hacked Client | Ping:" + ping + " | TPS: " + tps);
			        	getThotPatrol().logToFile(p, this, "Illegal Forge Mod", "TPS: " + tps + " | Ping: " + ping);
					}
				}
				catch (final Exception exception) {
					exception.printStackTrace();
				}
			}
		}

		public String getClientType(Player p) {
			final Map<String, String> map = forgeMods.get(p.getUniqueId());
	        double ping = getThotPatrol().getLag().getPing(p);
	        double tps = getThotPatrol().getLag().getTPS();
			if (map != null) {
				if (map.containsKey("gc")) {
					type = "gc";
					getThotPatrol().logCheat(this, p, "[2] Type: " + type);
		        	getThotPatrol().logToFile(p, this, "GC", "TPS: " + tps + " | Ping: " + ping);
					return "gc";
				}
				if (map.containsKey("ethylene")) {
					type = "ethylene";
					getThotPatrol().logCheat(this, p, "[3] Type: " + type);
		        	getThotPatrol().logToFile(p, this, "Ethylene", "TPS: " + tps + " | Ping: " + ping);
					return "ethylene";
				}
				if ("1.0".equals(map.get("OpenComputers"))) {
					type = "OpenComputers 1.0";
					getThotPatrol().logCheat(this, p, "[4] Type: " + type);
		        	getThotPatrol().logToFile(p, this, "OpenComputers", "TPS: " + tps + " | Ping: " + ping);
					return "C";
				}
				if ("1.7.6.git".equals(map.get("Schematica"))) {
					type = "Schematica 1.7.6.git";
					getThotPatrol().logCheat(this, p, "[5] Type: " + type);
		        	getThotPatrol().logToFile(p, this, "Schematica (Reach)", "TPS: " + tps + " | Ping: " + ping);
					return "Schematica 1.7.6.git";
				}
			}
			return null;
		}
}