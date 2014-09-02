package com.SkyIsland.Arena.Menu;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Wool;

import com.SkyIsland.Arena.Arena;
import com.SkyIsland.Arena.ArenaPlugin;
import com.SkyIsland.Arena.Team.Team;
import com.m0pt0pmatt.menuservice.api.Action;
import com.m0pt0pmatt.menuservice.api.ActionListener;
import com.m0pt0pmatt.menuservice.api.Component;
import com.m0pt0pmatt.menuservice.api.ComponentAttribute;
import com.m0pt0pmatt.menuservice.api.ComponentType;
import com.m0pt0pmatt.menuservice.api.Menu;
import com.m0pt0pmatt.menuservice.api.MenuAttribute;
import com.m0pt0pmatt.menuservice.api.MenuService;
import com.m0pt0pmatt.menuservice.api.Renderer;
import com.m0pt0pmatt.menuservice.api.SupportedRenderer;

/**
 * The portal to the menu system for the Arena
 * @author Skyler
 *
 */
public class MenuHandle implements ActionListener {
	
	private static final HashMap<Color, DyeColor> colorToDye = new HashMap<Color, DyeColor>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put(Color.RED, DyeColor.RED);
			put(Color.BLUE, DyeColor.BLUE);
			put(Color.WHITE, DyeColor.WHITE);
			put(Color.BLACK, DyeColor.BLACK);
			put(Color.PURPLE, DyeColor.PURPLE);
			put(Color.GRAY, DyeColor.GRAY);
			put(Color.YELLOW, DyeColor.YELLOW);
			put(Color.FUCHSIA, DyeColor.MAGENTA);
		}
	};
	
	private ArenaPlugin plugin;
	private String name = "Arena Action Listener";
	private Menu readyMenu, acceptMenu;
	private Renderer renderer;
	private MenuService menuService;
	
	public MenuHandle(ArenaPlugin plugin, MenuService menuService) {
		this.plugin = plugin;
		this.menuService = menuService;
		
		//get inventory renderer
		this.renderer = this.menuService.getRenderer(SupportedRenderer.InventoryRenderer.getName());
		
		readyMenu = setupReadyMenu();
		//acceptMenu = setupAcceptMenu();
		
	}
	
	/**
	 * Creates the menu used to ready-up. Creates all the components and sets the attributes, and then return the menu
	 * @return the menu to use to ready up
	 */
	private Menu setupReadyMenu() {
		Menu menu = new Menu();
		
		menu.addAttribute(MenuAttribute.SIZE, 6);
		menu.addAttribute(MenuAttribute.CANBECLOSE, false);
		
		Component comp;
		
		//create 'READY!' block
		comp = new Component();
		//set it's block type to wool, color it green
		//(create a green wool itemstack first)
		Wool wool = new Wool(DyeColor.LIME);
		comp.addAttribute(ComponentAttribute.ITEM, wool.toItemStack());
		
		//register it as abutton
		comp.setType(ComponentType.BUTTON);
		
		//set this handle as the actionlistener
		comp.setListener(this);
		
		//set
		comp.setName("ReadyButton");
		
		//set display title
		comp.addAttribute(ComponentAttribute.TITLE, "Ready");
		
		//set X and Y
		comp.addAttribute(ComponentAttribute.X, 8);
		comp.addAttribute(ComponentAttribute.Y, 5);
		
		menu.addComponent(comp);
		
		comp = new Component();
		wool = new Wool(DyeColor.RED);
		comp.setType(ComponentType.BUTTON);
		comp.addAttribute(ComponentAttribute.ITEM, wool.toItemStack());
		comp.addAttribute(ComponentAttribute.TITLE, "Leave");
		comp.setListener(this);
		comp.setName("LeaveButton");
		comp.addAttribute(ComponentAttribute.X, 7);
		comp.addAttribute(ComponentAttribute.Y, 5);
		
		menu.addComponent(comp);
		
		return menu;
	}
	
	private Menu setupAcceptMenu() {
		Menu menu = new Menu();
		
		Component comp;
		
		comp = new Component();
		
		//setup first indication block
		comp.setName("AcceptIndicatorOne");
		
		Wool wool = new Wool(DyeColor.WHITE);
		comp.addAttribute(ComponentAttribute.ITEM, wool.toItemStack());
		
		comp.addAttribute(ComponentAttribute.TITLE, "Team One Status");
		
		comp.addAttribute(ComponentAttribute.X, 0);
		comp.addAttribute(ComponentAttribute.Y, 2);
		
		menu.addComponent(comp);
		
		//start second indication block
		comp.setName("AcceptIndicatorTwo");
		
		wool = new Wool(DyeColor.WHITE);
		comp.addAttribute(ComponentAttribute.ITEM, wool.toItemStack());
		
		comp.addAttribute(ComponentAttribute.TITLE, "Team Two Status");
		
		comp.addAttribute(ComponentAttribute.X, 0);
		comp.addAttribute(ComponentAttribute.Y, 3);
		
		menu.addComponent(comp);
		
		//start Accept block
		comp.setName("AcceptButton");
		
		wool = new Wool(DyeColor.LIME);
		comp.addAttribute(ComponentAttribute.ITEM, wool.toItemStack());
		
		comp.addAttribute(ComponentAttribute.TITLE, "Accept");
		
		comp.addAttribute(ComponentAttribute.X, 8);
		comp.addAttribute(ComponentAttribute.Y, 3);
		
		//regist as listener
		comp.setListener(this);
		
		menu.addComponent(comp);
		
		//start Refuse block
		comp.setName("WithdrawButton");
		
		wool = new Wool(DyeColor.RED);
		comp.addAttribute(ComponentAttribute.ITEM, wool.toItemStack());
		
		comp.addAttribute(ComponentAttribute.TITLE, "Withdraw Items");
		
		comp.addAttribute(ComponentAttribute.X, 8);
		comp.addAttribute(ComponentAttribute.Y, 2);
		
		//register listener
		comp.setListener(this);
		
		menu.addComponent(comp);
		
		
		//set brown wool inbetween
		int i, j;
		i = 7;
		j = 3;
		
		wool = new Wool(DyeColor.BROWN);
		
		for (; i > 0; i--)
		for (; j >= 2; j--) {
			comp = new Component();
			comp.setName("BrownWool_" + (((i-1) * 2) + j));
			comp.addAttribute(ComponentAttribute.ITEM, wool.toItemStack());
			comp.addAttribute(ComponentAttribute.X, i);
			comp.addAttribute(ComponentAttribute.Y, j);
		}
		
		
		
		
		return menu;
	}
	
	
	
	private void updateReadyMenu() {
		readyMenu.update();
	}
	
	private void updateAcceptMenu() {
		
		Map<String, Component> comps = acceptMenu.getComponents();
		Component comp;
		Wool wool;
		
		//first we get our first indicator block
		comp = comps.get("AcceptIndicatorOne");
		
		//check if teamOne (redteam) is 'acknowledges'
		if (plugin.getArena().redTeam.isAcknowledge()) {
			wool = new Wool(DyeColor.BLACK);
		}
		else {
			wool = new Wool(DyeColor.WHITE);
		}
		comp.addAttribute(ComponentAttribute.ITEM, wool.toItemStack());
		
		//do it again for second indicator
		comp = comps.get("AcceptIndicatorOne");
		
		if (plugin.getArena().blueTeam.isAcknowledge()) {
			wool = new Wool(DyeColor.BLACK);
		}
		else {
			wool = new Wool(DyeColor.WHITE);
		}
		
		comp.addAttribute(ComponentAttribute.ITEM, wool.toItemStack());
		
		
		acceptMenu.update();
	}
	
	@Override
	public void handleAction(Action action, UUID playerName, Menu menu,
			Component component) {
		if (action.equals(Action.LEFT_CLICK)) {
			//left click actions
			if (component.getName().equalsIgnoreCase("ReadyButton")) {
				//they hit the ready button
				
				
				
				Component c = menu.getComponents().get("PReady_" + playerName.toString());
				Team team;
				Arena arena;
				
				arena = plugin.getArena();
				
				//get the team the player who clicked is from
				team = arena.getTeam(Bukkit.getPlayer(playerName));
				

				//check that the other side has players
				if (team == arena.blueTeam) {
					if (arena.redTeam.getNumberPlayers() == 0) {
						Bukkit.getPlayer(playerName).sendMessage("You can't ready up until there are players on both sides!");
						this.updateReadyMenu();
						return;
					}
				}
				else if (team == arena.redTeam){
					if (arena.blueTeam.getNumberPlayers() == 0) {
						Bukkit.getPlayer(playerName).sendMessage("You can't ready up until there are players on both sides!");
						this.updateReadyMenu();
						return;
					}
				}
				
				if (arena.getTeamPlayer(Bukkit.getPlayer(playerName)).isReady()) {
					Bukkit.getPlayer(playerName).sendMessage("You are already registered as ready!");
					return;
				}
				

				
				DyeColor color;
				Color oColor = team.getTeamColor();
				color = colorToDye.get(oColor);
				if (color == null)	{
					System.out.println("Team color does not have a dye color associated: " + oColor.toString());
					color = DyeColor.MAGENTA;
				}
				
				
				Wool wool = new Wool(color);
				
				//Color color = this.plugin.getArena().getTeam(Bukkit.getPlayer(playerName)).getTeamColor();
				
				//ItemStack glass = new ItemStack(Material.THIN_GLASS, 1, DyeColor.getByColor(color).getData());
				c.addAttribute(ComponentAttribute.ITEM, wool.toItemStack());
				

				updateReadyMenu();
				this.plugin.getArena().getTeamPlayer(Bukkit.getPlayer(playerName)).setReady(true);
				this.plugin.getArena().fightReady();
			}
			
			else if (component.getName().equalsIgnoreCase("LeaveButton")) {
				//they hit the back button
				Arena arena = plugin.getArena();
				arena.playerLeave(Bukkit.getPlayer(playerName));
			}
			else if (component.getName().startsWith("PlayerItem_")) {
				//it's a player item being left click'ed, meaning withdrawn
				acceptMenu.removeComponent(component);
				updateAcceptMenu();
			}
			else if (component.getName().equalsIgnoreCase("AcceptButton")) {
				//this player has accepted the barter
				Arena arena;
				
				arena = plugin.getArena();
				

				//make sure this isn't a spam click				
				if (arena.getTeamPlayer(Bukkit.getPlayer(playerName)).isAcknowledge()) {
					Bukkit.getPlayer(playerName).sendMessage("You have already agreed!");
					return;
				}
				
				arena.getTeamPlayer(Bukkit.getPlayer(playerName)).setAcknowledge(true);
				
				updateAcceptMenu();
				
			}
			else if (component.getName().equalsIgnoreCase("WithdrawButton")) {
				Map<String, Component> comps = acceptMenu.getComponents();
				
				//cycle through components that start with PlayerItem_[uuid]
				for (String compName : comps.keySet()) {
					if (compName.startsWith("PlayerItem_" + playerName)) {
						//the comp with name compName belongs to the current player
						acceptMenu.removeComponent(comps.get(compName));
					}
				}
				
				updateAcceptMenu();
			}
		}
	}
	
	public void addPlayerReady(UUID player, int team) {
		Component comp;
		
		comp = new Component();
		
		//set display name
		comp.addAttribute(ComponentAttribute.TITLE, Bukkit.getPlayer(player).getName());
		
		//create their component item -- glass. it'll start out as clear bcause they shouldn't be ready yet		
		comp.addAttribute(ComponentAttribute.ITEM, new ItemStack(Material.THIN_GLASS));
		
		comp.addAttribute(ComponentAttribute.X, 0);
		
		comp.addAttribute(ComponentAttribute.Y, (team - 1) * 3);
		
		//set the title
		comp.setName("PReady_" + player.toString());
		
		readyMenu.addComponent(comp);
		
		
		updateReadyMenu();
		readyMenu.addPlayer(player, this.renderer);
	}
	
	public void removePlayerReady(UUID player) {
		readyMenu.removePlayer(player);
	}
	
	public void addPlayerAccept(UUID player, int team) {
		acceptMenu.addPlayer(player, renderer);
	}
	
	public void addItemAccept(UUID player, ItemStack item, int team) {
		Component comp;
		
		comp = new Component();
		comp.setListener(this);
		Random rand = new Random();
		comp.setName("PlayerItem_" + player.toString() + (Bukkit.getMaxPlayers() * rand.nextInt())); //generate SOMETHING that starts with PlayerItem_
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		if (lore == null) {
			lore = new LinkedList<String>();
		}
		
		lore.add(0, meta.getDisplayName());
		meta.setLore(lore);
		//sets the changes
		
		meta.setDisplayName( Bukkit.getPlayer(player).getDisplayName()  );
		//changes the display name to be which player put it up
		item.setItemMeta(meta);
		
		//finally, set the item
		comp.addAttribute(ComponentAttribute.ITEM, item);
		
		comp.addAttribute(ComponentAttribute.TITLE, Bukkit.getPlayer(player).getDisplayName());
		
		comp.addAttribute(ComponentAttribute.X, 0);
		comp.addAttribute(ComponentAttribute.Y, 4 * (team - 1));
		
		
		acceptMenu.addComponent(comp);
		updateAcceptMenu();
	}

	@Override
	public void playerAdded(UUID uuid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerRemoved(UUID uuid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerCountZero(UUID uuid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getPlugin() {
		return this.plugin.getName();
	}
	
	public void closeMenus() {
		this.readyMenu.closeAll();
		this.acceptMenu.closeAll();
	}
	
	public void reInit() {
		closeMenus();
		this.readyMenu = this.setupReadyMenu();
		this.acceptMenu = this.setupAcceptMenu();
	}

	@Override
	public void inventoryClick(Action action, UUID playerName, Menu menu,
			int slot) {
		if (menu == acceptMenu)
		if (action == Action.LEFT_CLICK) {
			//TODO WHAT IF INVENTORY FILLS UP?
			
			Player player = Bukkit.getPlayer(playerName);
			
			if (player.getInventory().getItem(slot) == null) {
				return;
				//nothing in that spot
			}
			
			if (plugin.getArena().getTeam(player) == plugin.getArena().redTeam) {
				//:( it hurts me ---^
				//on red team
				addItemAccept(playerName, player.getInventory().getItem(slot), 1);
				return;
			}
			if (plugin.getArena().getTeam(player) == plugin.getArena().blueTeam) {
				addItemAccept(playerName, player.getInventory().getItem(slot), 2);				
				return;
			}
		}
		
	}

}
