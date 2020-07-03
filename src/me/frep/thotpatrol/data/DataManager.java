package me.frep.thotpatrol.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.frep.thotpatrol.checks.Check;

public class DataManager {
	
	public static List<DataPlayer> dataObjects = new ArrayList<DataPlayer>();
	public static List<Check> checks = new ArrayList<Check>();
	public static Map<Player, Map<Check, Integer>> violations = new HashMap<Player, Map<Check, Integer>>();
	public static List<DataPlayer> players = new ArrayList<DataPlayer>();

	private final Set<DataPlayer> dataSet = new HashSet<>();

	public DataManager() {
		DataManager.dataObjects = new ArrayList<>();
		Bukkit.getOnlinePlayers().forEach(this::add);
		checks = new ArrayList<>();
		violations = new WeakHashMap<>();
		players = new ArrayList<>();
	}
	public void createDataObject(final Player player) {
		DataManager.dataObjects.add(new DataPlayer(player));
	}
	public List<DataPlayer> getDataObjects() {
		return DataManager.dataObjects;
	}
	public void removeDataObject(final DataPlayer playerData) {
		DataManager.dataObjects.remove(playerData);
	}
	public DataPlayer getPlayerData(final Player player) {
		for (final DataPlayer playerData : DataManager.dataObjects) {
			if (playerData.player == player) {
				return playerData;
			}
		}
		return null;
	}
	public DataPlayer getDataPlayer(Player p) {
		return dataSet.stream().filter(dataPlayer -> dataPlayer.player == p).findFirst().orElse(null);
	}

	public void add(Player p) {
		dataSet.add(new DataPlayer(p));
	}

	public void remove(Player p) {
		dataSet.removeIf(dataPlayer -> dataPlayer.player == p);
	}

	public void removeCheck(Check c) {
		if(checks.contains(c)) {
			checks.remove(c);
		}
	}

	public boolean isCheck(Check c) {
		return checks.contains(c);
	}

	public Check getCheckAyName(String cn) {
		for(final Check checkLoop : Collections.synchronizedList(checks)) {
			if(checkLoop.getName().equalsIgnoreCase(cn)) {
				return checkLoop;
			}
		}

		return null;
	}

	public Map<Player, Map<Check, Integer>> getViolationsMap() {
		return violations;
	}

	public int getViolatonsPlayer(Player p, Check c) {
		if(violations.containsKey(p)) {
			final Map<Check, Integer> vlMap = violations.get(p);

			return vlMap.getOrDefault(c, 0);
		}
		return 0;
	}

	public void addViolation(Player p, Check c) {
		if (violations.containsKey(p)) {
			final Map<Check, Integer> vlMap = violations.get(p);

			vlMap.put(c, vlMap.getOrDefault(c, 0) + 1);
			violations.put(p, vlMap);
		} else {
			final Map<Check, Integer> vlMap = new HashMap<>();

			vlMap.put(c, 1);

			violations.put(p, vlMap);
		}
	}

	public void addPlayerData(Player p) {
		players.add(new DataPlayer(p));
	}

	public DataPlayer getData(Player p) {
		for(final DataPlayer dataLoop : Collections.synchronizedList(players)) {
			if(dataLoop.getPlayer() == p) {
				return dataLoop;
			}
		}
		return null;
	}

	public void removePlayerData(Player p) {
		for(final DataPlayer dataLoop : Collections.synchronizedList(players)) {
			if(dataLoop.getPlayer() == p) {
				players.remove(dataLoop);
				break;
			}
		}
	}

	public void addCheck(Check c) {
		if(!checks.contains(c)) {
			checks.add(c);
		}
	}
	public List<Check> getChecks() {
		return checks;
	}
}