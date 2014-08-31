package com.SkyIsland.Arena;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ArenaPlugin extends JavaPlugin {
	
	Arena arena;
	YamlConfiguration config;
	
	public void onEnable() {
		load();
		arena = new Arena(config);
		getServer().getPluginManager().registerEvents(arena, this);
	}
	
	public void load() {
		if (!this.getDataFolder().exists()) {
			this.getDataFolder().mkdirs();
		}
		File configFile = new File(this.getDataFolder(), "config.yml");
		if (configFile == null || !configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//setup default
			config = new YamlConfiguration();
			config.set("world", Bukkit.getWorld("HomeWorld").getUID());
			config.set("teamOneName", "Red Team");
			config.set("teamTwoName", "Blue Team");
			config.set("exitLocation", new Location(Bukkit.getWorld("HomeWorld"), -106, 71, 9136));
			config.set("redButton", new Location(Bukkit.getWorld("HomeWorld"), -105, 71, 9140));
			config.set("blueButton", new Location(Bukkit.getWorld("HomeWorld"), -103, 71, 9140));
			//config.set("redSpawnCenter", new Location(Bukkit.getWorld("HomeWorld"), -105, 69, 9136, 0, 0));
			config.set("redSpawnCenterX", -105);
			config.set("redSpawnCenterY", 69);
			config.set("redSpawnCenterZ", 9136);
			config.set("redSpawnRadiusX", 0);
			config.set("redSpawnRadiusY", 0);
			//config.set("blueSpawnCenter", new Location(Bukkit.getWorld("HomeWorld"), -102, 69, 9136, 180, 0));
			config.set("blueSpawnCenterX", -102);
			config.set("blueSpawnCenterY", 69);
			config.set("blueSpawnCenterZ", 9136);
			config.set("blueSpawnRadiusX", 0);
			config.set("blueSpawnRadiusY", 0);
			try {
				config.save(configFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			//config exists
			try {
				config = new YamlConfiguration();
				config.load(configFile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}
