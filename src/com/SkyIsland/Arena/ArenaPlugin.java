package com.SkyIsland.Arena;


import java.io.File;
import java.io.IOException;

import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import com.SkyIsland.Arena.Menu.MenuHandle;

public class ArenaPlugin extends JavaPlugin {
	
	private Arena arena;
	
	private final String configFileName = "config.yml";
	private File configFile;
	private YamlConfiguration config;
	
	public double version = 0.16;
	private CommandHandler handler;
	public MenuHandle menuHandle;
	
	public void onEnable() {
		load();
		menuHandle = new MenuHandle(this);
		arena = new Arena(config, menuHandle);
		getServer().getPluginManager().registerEvents(arena, this);
		this.handler = new CommandHandler(this);
		
	}
	
	public void onDisable() {
		save();
		menuHandle.closeMenus();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		return this.handler.doCommand(sender, cmd, label, args);
	}
	
	@Override
	public YamlConfiguration getConfig() {
		return this.config;
	}
	
	public Arena getArena() {
		return arena;
	}
	
	public void load() {
		
		//Create data folder
		if (!this.getDataFolder().exists()) {
			this.getDataFolder().mkdirs();
		}
		
		//load file
		configFile = new File(this.getDataFolder(), configFileName);
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			createDefaultConfiguration();
			
		}
		
		config = new YamlConfiguration();

		try {
			config.load(configFile);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		//test version
		if (config.getDouble("version", 0.0) != this.version) {
			this.getLogger().info("Found an old Arena config file (version " + config.getDouble("version") + ")!\nDeleting old file and creating default...");
			configFile.delete();
			this.load(); //call it again to set up defaults
		}
	}
	
	public void save() {
		if (this.config == null) {
			this.getLogger().info("No config to save.......Skipping!");
			return;
		}
		
		if (!this.getDataFolder().exists()) {
			this.getDataFolder().mkdirs();
		}
		File configFile = new File(this.getDataFolder(), "config.yml");
		
		try {
			this.config.save(configFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createDefaultConfiguration(){
		
		//setup default
		config = new YamlConfiguration();
		config.set("version", this.version);
		config.set("world", "HomeWorld");
		
		ConfigurationSection teamOneSection = config.createSection("teamOne");
		teamOneSection.set("name", "Red Team");
		teamOneSection.set("max", 16);
		config.set("teamTwo.name", "Blue Team");
		
		ConfigurationSection teamTwoSection = config.createSection("teamTwo");
		teamTwoSection.set("name", "Blue Team");
		teamTwoSection.set("max", 16);
		
		//config.set("exitLocation", new Location(Bukkit.getWorld("HomeWorld"), -106, 71, 9136));
		config.set("exit.X", -106);
		config.set("exit.Y", 71);
		config.set("exit.Z", 9136);
		
		//set the cooldown room, where you go after you die but while the fight is still going on
		config.set("cool.X", -103);
		config.set("cool.Y", 69);
		config.set("cool.Z", 9132);
		
		//config.set("redButton", new Location(Bukkit.getWorld("HomeWorld"), -105, 71, 9140));
		config.set("teamOne.button.X", -105);
		config.set("teamOne.button.Y", 71);
		config.set("teamOne.button.Z", 9140);
		//config.set("blueButton", new Location(Bukkit.getWorld("HomeWorld"), -103, 71, 9140));
		config.set("teamTwo.button.X", -103);
		config.set("teamTwo.button.Y", 71);
		config.set("teamTwo.button.Z", 9140);
		//config.set("redSpawnCenter", new Location(Bukkit.getWorld("HomeWorld"), -105, 69, 9136, 0, 0));
		config.set("teamOne.spawn.X", -105);
		config.set("teamOne.spawn.Y", 69);
		config.set("teamOne.spawn.Z", 9136);
		config.set("teamOne.spawn.radius.X", 0);
		config.set("teamOne.spawn.radius.Y", 0);
		//config.set("blueSpawnCenter", new Location(Bukkit.getWorld("HomeWorld"), -102, 69, 9136, 180, 0));
		config.set("teamTwo.spawn.X", -102);
		config.set("teamTwo.spawn.Y", 69);
		config.set("teamTwo.spawn.Z", 9136);
		config.set("teamTwo.spawn.radius.X", 0);
		config.set("teamTwo.spawn.radius.Y", 0);
		
		//colors
		config.set("teamOne.color", Color.RED.asRGB());
		config.set("teamTwo.color", Color.BLUE.asRGB());
		
		//settings
		config.set("settings.KEEPARMOR", false);
		config.set("settings.TEAMATTACK", false);
		try {
			config.save(configFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
