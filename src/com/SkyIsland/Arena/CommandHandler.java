package com.SkyIsland.Arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class CommandHandler {
	
	private WorldEditPlugin weplugin;
	private ArenaPlugin plugin;
	
	public CommandHandler(JavaPlugin plugin) {
		this.plugin = (ArenaPlugin) plugin;
		this.weplugin = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
		if (this.weplugin == null) {
			plugin.getLogger().info("\n\nCould not hook into WorldEdit!\n"
					+ "Plugin performance is no longer defined!\n\n");
			
		}
	}
	
	public boolean doCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (cmd.getName().equalsIgnoreCase("setStartOne")) {
			Selection selection = this.weplugin.getSelection((Player) sender);
			if (selection.getArea() > 1) {
				//start center should be one single block
				sender.sendMessage("Invalid selection! The center of spawning should be ONE BLOOCK");
				return true;
			}
			Location loc;
			loc = selection.getMaximumPoint();
			
			plugin.getConfig().set("teamOne.spawn.X", loc.getBlockX());
			plugin.getConfig().set("teamOne.spawn.Y", loc.getBlockY());
			plugin.getConfig().set("teamOne.spawn.Z", loc.getBlockZ());
			
			//update Arena instead of making a new one
			plugin.getArena().setTeamOneSpawn(loc);
			
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("setStartTwo")) {
			Selection selection = this.weplugin.getSelection((Player) sender);
			if (selection.getArea() > 1) {
				//start center should be one single block
				sender.sendMessage("Invalid selection! The center of spawning should be ONE BLOOCK");
				return true;
			}
			Location loc;
			loc = selection.getMaximumPoint();
			
			plugin.getConfig().set("teamTwo.spawn.X", loc.getBlockX());
			plugin.getConfig().set("teamTwo.spawn.Y", loc.getBlockY());
			plugin.getConfig().set("teamTwp.spawn.Z", loc.getBlockZ());
			
			//update Arena instead of making a new one
			plugin.getArena().setTeamTwoSpawn(loc);
			
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("setButtonOne")) {
			Selection selection = this.weplugin.getSelection((Player) sender);
			if (selection.getArea() > 1) {
				//start center should be one single block
				sender.sendMessage("Invalid selection! The button can only be in one block");
				return true;
			}
			Location loc;
			loc = selection.getMaximumPoint();
			
			plugin.getConfig().set("teamOne.button.X", loc.getBlockX());
			plugin.getConfig().set("teamOne.button.Y", loc.getBlockY());
			plugin.getConfig().set("teamOne.button.Z", loc.getBlockZ());
			
			//update Arena instead of making a new one
			plugin.getArena().setTeamOneButton(loc);
			
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("setButtonTwo")) {
			Selection selection = this.weplugin.getSelection((Player) sender);
			if (selection.getArea() > 1) {
				//start center should be one single block
				sender.sendMessage("Invalid selection! The button can only be in one block");
				return true;
			}
			Location loc;
			loc = selection.getMaximumPoint();
			
			plugin.getConfig().set("teamTwo.button.X", loc.getBlockX());
			plugin.getConfig().set("teamTwo.button.Y", loc.getBlockY());
			plugin.getConfig().set("teamTwo.button.Z", loc.getBlockZ());
			
			//update Arena instead of making a new one
			plugin.getArena().setTeamTwoButton(loc);
			
			return true;
		}
		
		
		
		if (cmd.getName().equalsIgnoreCase("setCool")) {
			Selection selection = this.weplugin.getSelection((Player) sender);
			if (selection.getArea() > 1) {
				//start center should be one single block
				sender.sendMessage("Invalid selection! The cool-down spawn point should be ONE BLOOCK");
				return true;
			}
			Location loc;
			loc = selection.getMaximumPoint();
			
			plugin.getConfig().set("cool.X", loc.getBlockX());
			plugin.getConfig().set("cool.Y", loc.getBlockY());
			plugin.getConfig().set("cool.Z", loc.getBlockZ());
			
			//update Arena instead of making a new one
			plugin.getArena().setCoolLocation(loc);
			
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("setExit")) {
			Selection selection = this.weplugin.getSelection((Player) sender);
			if (selection.getArea() > 1) {
				//start center should be one single block
				sender.sendMessage("Invalid selection! The exit point should be ONE BLOOCK");
				return true;
			}
			Location loc;
			loc = selection.getMaximumPoint();
			
			plugin.getConfig().set("exit.X", loc.getBlockX());
			plugin.getConfig().set("exit.Y", loc.getBlockY());
			plugin.getConfig().set("exit.Z", loc.getBlockZ());
			
			//update Arena instead of making a new one
			plugin.getArena().setExitLocation(loc);
			
			return true;
		}
		
		if (cmd.getName().equalsIgnoreCase("leave")) {
			if (sender instanceof Player) {
				plugin.getArena().playerLeave((Player) sender);
			}
			//console
			sender.sendMessage("Only players can use this command");
		}
		
		return false;
	}
	
}
