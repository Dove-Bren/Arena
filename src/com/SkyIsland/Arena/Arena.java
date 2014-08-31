package com.SkyIsland.Arena;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * class for the arena at the spawn
 * @author Matthew Broomcloset
 *
 */
public class Arena implements Listener{
	
	/**
	 * the red team
	 */
	private Team redTeam;
	
	/**
	 * the blue team
	 */
	private Team blueTeam;
	
	/**
	 * whether or not a fight is currently happening
	 */
	private boolean currentFight;
	
	/**
	 * the location where players are teleported after the battle
	 */
	private Location exitLocation;
	
	/**
	 * default constructor.
	 */
	public Arena(){
		redTeam = new Team("Red Team");
		blueTeam = new Team("Blue Team");
		currentFight = false;
		exitLocation = new Location(Bukkit.getWorld("HomeWorld"), -106, 71, 9136);
	}
	
	/**
	 * Handler for when a switch is pressed that is related to the arena.
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST)
    public void addPlayer(PlayerInteractEvent event) {
    	
		//get the player
		Player player = (Player) event.getPlayer();
		
		//add player if the red switch was pressed
		if (redSwitchPressed(event)){
			
			//make sure there isn't a current fight going on
			if (currentFight){
				player.sendMessage("I'm sorry, but a fight is already in progress.");
				return;
			}
			
			//make sure the player isn't on the opposite team
			if (blueTeam.contains(player)){
				player.sendMessage("You cannot join the opposite team.");
				return;
			}
				
			//add the player if the player is not on the red team
			if (! redTeam.contains(player)){
				redTeam.addPlayer(player);
				player.sendMessage("You have joined The " + redTeam.getName());
			}
			
			//pressing the switch while already on the team signals that the player is ready
			else{
				redTeam.setReady(player);
				player.sendMessage("You are now ready.");
				
				//check if everyone is ready
				fightReady();
			}
		}
		
		
		//add player if the red switch was pressed
		else if (blueSwitchPressed(event)){
			
			//make sure there isn't a current fight going on
			if (currentFight){
				player.sendMessage("I'm sorry, but a fight is already in progress.");
				return;
			}
			
			//make sure the player isn't on the opposite team
			if (redTeam.contains(player)){
				player.sendMessage("You cannot join the opposite team.");
				return;
			}
				
			//add the player if the player is not on the blue team
			if (! blueTeam.contains(player)){
				blueTeam.addPlayer(player);
				player.sendMessage("You have joined The " + blueTeam.getName());
			}
			
			//pressing the switch while already on the team signals that the player is ready
			else{
				blueTeam.setReady(player);
				player.sendMessage("You are now ready.");
				fightReady();
			}
		}
    }
	
	/**
	 * Check if both teams are ready. Start that fight if they are both ready.
	 */
	public void fightReady(){
		//if both teams are ready...
		if (redTeam.isReady() && blueTeam.isReady()){
			//start the fight
			startfight();
		}
	}
	
	/**
	 * Check if a team has been killed. If so, end the fight
	 */
	public void fightOver(){
		//if one team is not alive...
		if (! (redTeam.isAlive() && blueTeam.isAlive())){
			//end the fight
			endFight();
		}
	}
	
	/**
	 * Handler for when a player dies in the arena
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST)
    public void removePlayer(PlayerDeathEvent event) {
		
		Player player = event.getEntity();
		checkTeam(player);
		player.teleport(exitLocation);
		player.setHealth(5);
		
	}
	
	/**
	 * Handler for when a player quits the game while in the arena
	 * @param event
	 */
	@EventHandler(priority = EventPriority.LOWEST)
    public void removePlayer(PlayerQuitEvent event) {
		
		Player player = event.getPlayer();
		checkTeam(player);
		
	}
	
	/**
	 * Checks if a player was on a team. If so, remove the player from that team.
	 * @param player
	 */
	public void checkTeam(Player player){
		//check if player died in the arena
		if (redTeam.contains(player)){
			//remove the dead player from the red team
			redTeam.setDead(player);
			
			//check if the fight is now over
			fightOver();
		}
		else if (blueTeam.contains(player)){
			//remove the dead player from the red team
			blueTeam.setDead(player);
			
			//check if the fight is now over
			fightOver();
		}
	}

	/**
	 * Start the fight. teleport players into the arena
	 */
	private void startfight() {
		
		//teleport both teams
		//red
		for (TeamPlayer p: redTeam.getPlayers()){
			int rand = new Random().nextInt(8);
			p.getPlayer().teleport(new Location(Bukkit.getWorld("HomeWorld"), 272 + rand + 1, 73, 187, 0, 0));
			p.getPlayer().sendMessage("The fight has begun.");
		}
		//blue
		for (TeamPlayer p: blueTeam.getPlayers()){
			int rand = new Random().nextInt(8);
			p.getPlayer().teleport(new Location(Bukkit.getWorld("HomeWorld"), 272 + rand + 1, 73, 173, 180, 0));
			p.getPlayer().sendMessage("The fight has begun.");
		}
		
		//start the fight
		currentFight = true;
		
	}
	
	/**
	 * stop the fight.
	 */
	private void endFight(){
		
		//only one team must stand
		if (redTeam.isAlive()){
			redTeam.alertPlayers("Congradulations, your team won!");
			blueTeam.alertPlayers("Your team lost. Better luck next time.");
		}
		else{
			blueTeam.alertPlayers("Congradulations, your team won!");
			redTeam.alertPlayers("Your team lost. Better luck next time.");
		}
		
		//teleport the teams out
		for(TeamPlayer p: redTeam.getPlayers()){
			p.getPlayer().teleport(exitLocation);
		}
		
		for(TeamPlayer p: blueTeam.getPlayers()){
			p.getPlayer().teleport(exitLocation);
		}
		
		//remove players from the teams
		redTeam.getPlayers().clear();
		blueTeam.getPlayers().clear();
		
		//end the fight
		currentFight = false;
	}

	private boolean blueSwitchPressed(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null){
			return false;
		}
		
		if (event.getClickedBlock().getLocation().equals(new Location(Bukkit.getWorld("HomeWorld"), -102, 70, 9140))){
			return true;
		}
		return false;
	}

	private boolean redSwitchPressed(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null){
			return false;
		}
		
		if (event.getClickedBlock().getLocation().equals(new Location(Bukkit.getWorld("HomeWorld"), -104, 70, 9140))){
			return true;
		}
		return false;
	}

}
