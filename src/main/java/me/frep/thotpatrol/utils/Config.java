package me.frep.thotpatrol.utils;

import me.frep.thotpatrol.ThotPatrol;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class Config {
	
	File userFile;
	FileConfiguration userConfig;

	public Config(String name){
		userFile = new File(ThotPatrol.Instance.getDataFolder() + File.separator, name + ".yml");
		userConfig = YamlConfiguration.loadConfiguration(userFile);
	}

	public void makeConfigFile(){
		if ( !userFile.exists() ) {
			try {
				userConfig.save(userFile);
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	public FileConfiguration getConfigFile(){
		return userConfig;
	}

	public void saveConfigFile(){
		try {
			getConfigFile().save(userFile);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
