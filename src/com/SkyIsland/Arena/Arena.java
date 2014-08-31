package com.SkyIsland.Arena;

//import java.util.Random;

import java.util.Random;



import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.SkyIsland.Arena.Team.Team;
import com.SkyIsland.Arena.Team.TeamPlayer;

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
	 * the location where players are teleported after/at the start of the battle
	 */
	private Location exitLocation, teamOneSpawn, teamTwoSpawn, teamOneButton, teamTwoButton;
	
	/**
	 * The X and Y radius of the spawn rectangle centered on {@link com.SkyIsland.Arena.Arena#teamOneSpawn teamOneSpawn}.
	 */
	private int spawnRadiusOneX, spawnRadiusOneY, spawnRadiusTwoX, spawnRadiusTwoY;
	
	
	/**
	 * default constructor.
	 */
//	public Arena(){
//		this("Red Team", "Blue Team", new Location(Bukkit.getWorld("HomeWorld"), -106, 71, 9136));
////		redTeam = new Team("Red Team");
////		blueTeam = new Team("Blue Team");
////		currentFight = false;
////		exitLocation = new Location(Bukkit.getWorld("HomeWorld"), -106, 71, 9136);
//	}
	
	public Arena(YamlConfiguration config) {
		redTeam = new Team(config.getString("teamOneName", "Red Team"));
		blueTeam = new Team(config.getString("teamTwoName", "Blue Team"));
		currentFight = false;
		this.exitLocation = (Location) config.get("exitLocation");
		this.teamOneSpawn = new Location(Bukkit.getWorld(UUID.fromString(config.getString("world"))), config.getInt("redSpawnCenterX"), config.getInt("redSpawnCenterY"), config.getInt("redSpawnCenterZ"));
		//this.teamTwoSpawn = (Location) config.get("blueSpawnCenter");
		this.teamTwoSpawn = new Location(Bukkit.getWorld(UUID.fromString(config.getString("world"))), config.getInt("blueSpawnCenterX"), config.getInt("blueSpawnCenterY"), config.getInt("blueSpawnCenterZ"));
		this.spawnRadiusOneX = config.getInt("redSpawnRadiusX", 0);
		this.spawnRadiusOneY = config.getInt("redSpawnRadiusY", 0);
		this.spawnRadiusTwoX = config.getInt("blueSpawnRadiusX", 0);
		this.spawnRadiusTwoY = config.getInt("blueSpawnRadiusY", 0);
		this.teamOneButton = (Location) config.get("redButton");
		this.teamTwoButton = (Location) config.get("blueButton");
		
		

//		config.set("blueSpawnCenterX", -102);
//		config.set("blueSpawnCenterY", 69);
//		config.set("blueSpawnCenterZ", 9136);
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
				blueTeam.alertPlayers(player.getDisplayName() + "has joined the " + redTeam.getName());
			}
			
			//pressing the switch while already on the team signals that the player is ready.
			//We first chest if they are already on the team to prevent spamming messages
			else if (!redTeam.playerReady(player)){
				redTeam.setReady(player);
				player.sendMessage("You are now ready.");
				
				//check if everyone is ready
				fightReady();
			}
			else {
				//player is already on the team and already ready
				player.sendMessage("You are already registered as ready!");
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
				redTeam.alertPlayers(player.getDisplayName() + "has joined the " + blueTeam.getName());
			}
			
			//pressing the switch while already on the team signals that the player is ready
			else if (!blueTeam.playerReady(player)){
				blueTeam.setReady(player);
				player.sendMessage("You are now ready.");
				
				//check if everyone is ready
				fightReady();
			}
			else {
				//player is already on the team and already ready
				player.sendMessage("You are already registered as ready!");
			}
		}
    }
	
	/**
	 * Check if both teams are ready. Start that fight if they are both ready.
	 */
	public void fightReady(){
		//if the whole team is ready, let the other team know about it
		boolean red, blue;
		red = redTeam.isReady();
		blue = blueTeam.isReady();
		if (red) {
			blueTeam.alertPlayers("The " + redTeam.getName() + " is now ready to battle!");
		}
		if (blue) {
			redTeam.alertPlayers("The " + blueTeam.getName() + " is now ready to battle!");
		}
		
		//if both teams are ready...
		if (red && blue){
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
		if (checkTeam(player)) {
			player.teleport(exitLocation);
			player.setHealth(20);
		}
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
	 * Returns if a player was on a team. 
	 * <p />If the player was on a team, they will be removed from it and the teams updated.
	 * @param player
	 * @return Whether or not that player was on an Arena team.
	 */
	public boolean checkTeam(Player player){
		//check if player died in the arena
		if (redTeam.contains(player)){
			//remove the dead player from the red team
			redTeam.setDead(player);
			if (redTeam.getNumberPlayers() != 0) {
				redTeam.alertPlayers(player.getDisplayName() + " has been slain! " + redTeam.getNumberPlayers() + " left!");
				blueTeam.alertPlayers(player.getDisplayName() + " has been slain!");
			}
			
			//check if the fight is now over
			fightOver();
			return true;
		}
		else if (blueTeam.contains(player)){
			//remove the dead player from the red team
			blueTeam.setDead(player);
			if (blueTeam.getNumberPlayers() != 0) {
				blueTeam.alertPlayers(player.getDisplayName() + " has been slain! " + blueTeam.getNumberPlayers() + " left!");
				redTeam.alertPlayers(player.getDisplayName() + " has been slain!");
			}
			
			//check if the fight is now over
			fightOver();
			return true;
		}
		//let it know we didn't find that player in either team
		return false;
	}

	/**
	 * Start the fight. teleport players into the arena
	 */
	private void startfight() {
		
		//teleport both teams
		//red
		Location actualLocation;
		Random rand = new Random();
		
		for (TeamPlayer p: redTeam.getPlayers()){
			//int rand = new Random().nextInt(8);
			actualLocation = teamOneSpawn.clone();
			actualLocation.add(rand.nextInt(spawnRadiusOneX), 0, rand.nextInt(spawnRadiusOneY));
			p.getPlayer().teleport(actualLocation);
			p.getPlayer().sendMessage("The fight has begun.");
		}
		//blue
		for (TeamPlayer p: blueTeam.getPlayers()){
			//int rand = new Random().nextInt(8);
			actualLocation = teamTwoSpawn.clone();
			actualLocation.add(rand.nextInt(spawnRadiusTwoX), 0, rand.nextInt(spawnRadiusTwoY));
			p.getPlayer().teleport(actualLocation);
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
		
		if (event.getClickedBlock().getLocation().equals(this.teamTwoButton)){
			return true;
		}
		return false;
	}

	private boolean redSwitchPressed(PlayerInteractEvent event) {
		if (event.getClickedBlock() == null){
			return false;
		}
		
		if (event.getClickedBlock().getLocation().equals(this.teamOneButton)){
			return true;
		}
		return false;
	}

}
