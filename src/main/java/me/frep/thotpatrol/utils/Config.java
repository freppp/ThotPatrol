package me.frep.thotpatrol.utils;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.frep.thotpatrol.ThotPatrol;

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
