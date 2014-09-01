package com.SkyIsland.Arena;

//import java.util.Random;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;



import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import com.SkyIsland.Arena.Team.Team;
import com.SkyIsland.Arena.Team.TeamPlayer;
import com.SkyIsland.Arena.Utils.ArmorSet;

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
	private Location coolLocation, exitLocation, teamOneSpawn, teamTwoSpawn, teamOneButton, teamTwoButton;
	
	/**
	 * The X and Y radius of the spawn rectangle centered on {@link com.SkyIsland.Arena.Arena#teamOneSpawn teamOneSpawn}.
	 */
	private int spawnRadiusOneX, spawnRadiusOneY, spawnRadiusTwoX, spawnRadiusTwoY;
	
	/**
	 * Stores the players inventories, if the gamemode forces it. This way they can get their inventories back
	 */
	private HashMap<UUID, Set<ItemStack>> inventories;
	
	private HashMap<UUID, ArmorSet> armors;
	
	private Settings settings;
	
	private class Settings {
		
		public boolean KEEPARMOR;
		
		protected Settings(ConfigurationSection config) {
			KEEPARMOR = config.getBoolean("KEEPARMOR");
		}

	}

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
		inventories = new HashMap<UUID, Set<ItemStack>>();
		armors = new HashMap<UUID, ArmorSet>();
		
		
		World world = Bukkit.getWorld(UUID.fromString((String) config.get("world")));
		redTeam = new Team(config.getString("teamOne.name", "Red Team"), Color.fromRGB(config.getInt("teamOne.color", Color.RED.asRGB())));
		blueTeam = new Team(config.getString("teamTwo.name", "Blue Team"), Color.fromRGB(config.getInt("teamTwo.color", Color.BLUE.asRGB())));
		currentFight = false;
		//this.exitLocation = (Location) config.get("exitLocation");
		this.exitLocation = new Location(world, config.getInt("exit.X"), config.getInt("exit.Y"), config.getInt("exit.Z"));
		this.coolLocation = new Location(world, config.getInt("cool.X"), config.getInt("cool.Y"), config.getInt("cool.Z"));
		this.teamOneSpawn = new Location(world, config.getInt("teamOne.spawn.X"), config.getInt("teamOne.spawn.Y"), config.getInt("teamOne.spawn.Z"));
		//this.teamTwoSpawn = (Location) config.get("blueSpawnCenter");
		this.teamTwoSpawn = new Location(world, config.getInt("teamTwo.spawn.X"), config.getInt("teamTwo.spawn.Y"), config.getInt("teamTwo.spawn.Z"));
		this.spawnRadiusOneX = config.getInt("teamOne.spawn.radius.X", 0);
		this.spawnRadiusOneY = config.getInt("teamOne.spawn.radius.Y", 0);
		this.spawnRadiusTwoX = config.getInt("teamTwo.spawn.radius.X", 0);
		this.spawnRadiusTwoY = config.getInt("teamTwo.spawn.radius.Y", 0);
		//this.teamOneButton = (Location) config.get("redButton");
		//this.teamTwoButton = (Location) config.get("blueButton");
		this.teamOneButton = new Location(world, config.getInt("teamOne.button.X"), config.getInt("teamOne.button.Y"), config.getInt("teamOne.button.Z"));
		this.teamTwoButton = new Location(world, config.getInt("teamTwo.button.X"), config.getInt("teamTwo.button.Y"), config.getInt("teamTwo.button.Z"));
		
		
		this.settings = new Settings(config.createSection("settings"));
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
    public void removePlayer(EntityDamageByEntityEvent event) {
		
		if (event.getEntityType() != EntityType.PLAYER) {
			return;
		}
		
		Player player = (Player) event.getEntity();
		
		//are they about to die?
		if (event.getDamage() >= player.getHealth())		
		if (checkTeam(player)) {
			player.teleport(coolLocation);
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
		PlayerInventory inv;
		HashSet<ItemStack> set;
		
		for (TeamPlayer p: redTeam.getPlayers()){
			if (!settings.KEEPARMOR) {
				//save their inventory and suit them up in a custom style
				inv = p.getPlayer().getInventory();
				set = new HashSet<ItemStack>();
				set.addAll(Arrays.asList(inv.getContents()));
				this.inventories.put(p.getPlayer().getUniqueId(), set);
				
//				//armor slots
//				set = new HashSet<ItemStack>();
//				//we start with head
//				set.add(inv.getHelmet());
//				//then chest
//				set.add(inv.getChestplate());
//				//then legs
//				set.add(inv.getLeggings());
//				//and lastly boots
//				set.add(inv.getBoots());
				ArmorSet armor = new ArmorSet(inv.getHelmet(), inv.getChestplate(), inv.getLeggings(), inv.getBoots(), null);
				
				//we do them one at a time so we know which order they wre put in and because we have to parse it ourselves incase of null values
				
				//set.addAll(Arrays.asList(inv.getArmorContents()));
				armors.put(p.getPlayer().getUniqueId(), armor);
				inv.clear();
				inv.setArmorContents(null);
				
				suitUp(p, redTeam);
			}
			
			//int rand = new Random().nextInt(8);
			actualLocation = teamOneSpawn.clone();
			actualLocation.add(Math.floor(rand.nextInt(spawnRadiusOneX + 1)), 0, Math.floor(rand.nextInt(spawnRadiusOneY + 1)));
			p.getPlayer().teleport(actualLocation);
			p.getPlayer().sendMessage("The fight has begun.");
		}
		//blue
		for (TeamPlayer p: blueTeam.getPlayers()){
			if (!settings.KEEPARMOR) {
				inv = p.getPlayer().getInventory();
				set = new HashSet<ItemStack>();
				set.addAll(Arrays.asList(inv.getContents()));
				this.inventories.put(p.getPlayer().getUniqueId(), set);
				
//				//armor slots
//				set = new HashSet<ItemStack>();
//				//we start with head
//				set.add(inv.getHelmet());
//				//then chest
//				set.add(inv.getChestplate());
//				//then legs
//				set.add(inv.getLeggings());
//				//and lastly boots
//				set.add(inv.getBoots());
				ArmorSet armor = new ArmorSet(inv.getHelmet(), inv.getChestplate(), inv.getLeggings(), inv.getBoots(), null);
				
				//we do them one at a time so we know which order they wre put in and because we have to parse it ourselves incase of null values
				
				//set.addAll(Arrays.asList(inv.getArmorContents()));
				armors.put(p.getPlayer().getUniqueId(), armor);
				inv.clear();
				inv.setArmorContents(null);
				
				suitUp(p, blueTeam);
			}
			
			//int rand = new Random().nextInt(8);
			actualLocation = teamTwoSpawn.clone();
			actualLocation.add(Math.floor(rand.nextInt(spawnRadiusTwoX+1)), 0, Math.floor(rand.nextInt(spawnRadiusTwoY+1)));
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
			redTeam.alertPlayers("Congratulations, your team won!");
			blueTeam.alertPlayers("Your team lost. Better luck next time.");
		}
		else{
			blueTeam.alertPlayers("Congratulations, your team won!");
			redTeam.alertPlayers("Your team lost. Better luck next time.");
		}
		
		//teleport the teams out
		for(TeamPlayer p: redTeam.getPlayers()){
			if (!settings.KEEPARMOR) {
				//we gotta give them their stuff back
				PlayerInventory inv = p.getPlayer().getInventory();
				
				inv.clear();
				inv.setArmorContents(null);
				
				//give it back
				Set<ItemStack> set;
				ArmorSet armor;
				
				armor = armors.get(p.getPlayer().getUniqueId());
				//inv.setArmorContents(set.toArray(new ItemStack[set.size()]));
				//we have to go one by one so that it doesn't stop at a null value?
				
				inv.setHelmet(armor.getHelmet());
				inv.setChestplate(armor.getChestplate());
				inv.setLeggings(armor.getLeggings());
				inv.setBoots(armor.getBoots());
				
				set = inventories.get(p.getPlayer().getUniqueId());
				inv.setContents(set.toArray(new ItemStack[set.size()]));
			}
			
			p.getPlayer().teleport(exitLocation);
		}
		
		for(TeamPlayer p: blueTeam.getPlayers()){
			if (!settings.KEEPARMOR) {
				//we gotta give them their stuff back
				PlayerInventory inv = p.getPlayer().getInventory();
				inv.clear();
				inv.setArmorContents(null);
				
				//give it back
				Set<ItemStack> set;
				ArmorSet armor;
				
				armor = armors.get(p.getPlayer().getUniqueId());
				//inv.setArmorContents(set.toArray(new ItemStack[set.size()]));
				//we have to go one by one so that it doesn't stop at a null value?
				
				inv.setHelmet(armor.getHelmet());
				inv.setChestplate(armor.getChestplate());
				inv.setLeggings(armor.getLeggings());
				inv.setBoots(armor.getBoots());
				
				set = inventories.get(p.getPlayer().getUniqueId());
				inv.setContents(set.toArray(new ItemStack[set.size()]));
			}
			
			
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
	
	/**
	 * Sets up armor slots to resemble the team uniform.
	 * <p /><b>This method DOES NOT SAVE the current armor before over-writing it!!!</b>
	 * @param p the TeamPlayer to suit up
	 */
	private void suitUp(TeamPlayer p, Team team) {
		PlayerInventory inv = p.getPlayer().getInventory();
		ItemStack armor;
		LeatherArmorMeta meta;
		
		//figure out what team they're on
		
		//first we do helm...
		armor = new ItemStack(Material.LEATHER_HELMET);
		meta = ((LeatherArmorMeta) armor.getItemMeta());
		meta.setColor(team.getTeamColor());
		armor.setItemMeta(meta);
		
		inv.setHelmet(armor);
		
		//next chestplate
		armor = new ItemStack(Material.LEATHER_CHESTPLATE);
		meta = ((LeatherArmorMeta) armor.getItemMeta());
		meta.setColor(team.getTeamColor());
		armor.setItemMeta(meta);
		
		inv.setChestplate(armor);
		
		//last we set their weapon
		inv.setItemInHand(new ItemStack(Material.STONE_SWORD));
	}
	
	/**
	 * Issues a request to leave the team.
	 * <p>
	 * This only works if there is nobody on the other team yet and the fight hasn't started.
	 * </p>
	 * @param p - The player trying to chicken out
	 */
	public void playerLeave(Player p) {
		if (this.currentFight) {
			//nobody can leave anyway, even if they are on the teams
			return;
		}
		
		if (this.redTeam.contains(p)) {
			//on red team...
			if (this.blueTeam.getNumberPlayers() != 0) {
				//there are players on both sides
				p.sendMessage("You can only leave the arena if nobody has joined the other side!");
				return;
			}
			
			//the player is on the red team and nobody is on the blue team. It's okay to leave
			redTeam.removePlayer(p);
			p.teleport(exitLocation);
			p.sendMessage("You have fled battle!");
			return;
		}
		if (this.blueTeam.contains(p)) {
			//on red team...
			if (this.redTeam.getNumberPlayers() != 0) {
				//there are players on both sides
				p.sendMessage("You can only leave the arena if nobody has joined the other side!");
				return;
			}
			
			//the player is on the red team and nobody is on the blue team. It's okay to leave
			blueTeam.removePlayer(p);
			p.teleport(exitLocation);
			p.sendMessage("You have fled battle!");
			return;
		}
	}
	
	

	protected Location getExitLocation() {
		return exitLocation;
	}

	protected void setExitLocation(Location exitLocation) {
		this.exitLocation = exitLocation;
	}

	protected Location getTeamOneSpawn() {
		return teamOneSpawn;
	}

	protected void setTeamOneSpawn(Location teamOneSpawn) {
		this.teamOneSpawn = teamOneSpawn;
	}

	protected Location getTeamTwoSpawn() {
		return teamTwoSpawn;
	}

	protected void setTeamTwoSpawn(Location teamTwoSpawn) {
		this.teamTwoSpawn = teamTwoSpawn;
	}

	protected Location getTeamOneButton() {
		return teamOneButton;
	}

	protected void setTeamOneButton(Location teamOneButton) {
		this.teamOneButton = teamOneButton;
	}

	protected Location getTeamTwoButton() {
		return teamTwoButton;
	}

	protected void setTeamTwoButton(Location teamTwoButton) {
		this.teamTwoButton = teamTwoButton;
	}

	protected int getSpawnRadiusOneX() {
		return spawnRadiusOneX;
	}

	protected void setSpawnRadiusOneX(int spawnRadiusOneX) {
		this.spawnRadiusOneX = spawnRadiusOneX;
	}

	protected int getSpawnRadiusOneY() {
		return spawnRadiusOneY;
	}

	protected void setSpawnRadiusOneY(int spawnRadiusOneY) {
		this.spawnRadiusOneY = spawnRadiusOneY;
	}

	protected int getSpawnRadiusTwoX() {
		return spawnRadiusTwoX;
	}

	protected void setSpawnRadiusTwoX(int spawnRadiusTwoX) {
		this.spawnRadiusTwoX = spawnRadiusTwoX;
	}

	protected int getSpawnRadiusTwoY() {
		return spawnRadiusTwoY;
	}

	protected void setSpawnRadiusTwoY(int spawnRadiusTwoY) {
		this.spawnRadiusTwoY = spawnRadiusTwoY;
	}

	protected Location getCoolLocation() {
		return coolLocation;
	}

	protected void setCoolLocation(Location coolLocation) {
		this.coolLocation = coolLocation;
	}
	
	
	
	
	
	
	

}
