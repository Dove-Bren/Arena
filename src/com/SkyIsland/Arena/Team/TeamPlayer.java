package com.SkyIsland.Arena.Team;

import org.bukkit.entity.Player;

public class TeamPlayer implements Comparable<TeamPlayer>{
	
	/*
	 * the player which the TeamPlayer represents
	 */
	private Player player;
	
	/*
	 * whether or not the player is ready
	 */
	private boolean ready;
	
	/**
	 * whether or not a team player has acknowledged the stakes of the fight
	 */
	private boolean acknowledge;
	
	private boolean alive;
	
	private boolean picking;
	
	private int kills;
	
	
	public TeamPlayer(Player player, boolean ready, boolean acknowledge){
		this.player = player;
		this.ready = ready;
		this.alive = true;
		this.acknowledge = acknowledge;
		this.kills = 0;
		this.picking = false;
	}
	
	public TeamPlayer(Player player, boolean ready) {
		this(player, ready, false);
	}
	
	public TeamPlayer(Player player){
		this(player, false);
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public boolean isReady(){
		return ready;
	}
	
	public boolean isPicking() {
		return picking;
	}
	
	public int getKills() {
		return kills;
	}
	
	public void setKills(int kills) {
		this.kills = kills;
	}
	
	/**
	 * Adds 1 to this player's kill count
	 */
	public void addKill() {
		kills += 1;
		this.player.sendMessage("Nice kill!");
		player.sendMessage("Current kill count: " + kills);
	}
	
	public void setReady(boolean ready){
		this.ready = ready;
	}
	
	public void setPicking(boolean picking) {
		this.picking = picking;
	}
	
	public boolean isAcknowledge() {
		return this.acknowledge;
	}
	
	public void setAcknowledge(boolean acknowledge) {
		this.acknowledge = acknowledge;
	}

	public boolean isAlive(){
		return alive;
	}
	
	public void setAlive(){
		alive = true;
	}
	
	public void setDead(){
		alive = false;
	}

	@Override
	/**
	 * compares two team players based on kill count
	 * @param o
	 * @return
	 */
	public int compareTo(TeamPlayer o) {
		
		return (this.kills - ((TeamPlayer) o).getKills());
	}
	
}
