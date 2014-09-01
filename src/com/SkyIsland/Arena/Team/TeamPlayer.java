package com.SkyIsland.Arena.Team;

import org.bukkit.entity.Player;

public class TeamPlayer{
	
	/*
	 * the player which the TeamPlayer represents
	 */
	private Player player;
	
	/*
	 * whether or not the player is ready
	 */
	private boolean ready;
	
	/**
	 * whether or not a team player has achknoledged the stakes of the fight
	 */
	private boolean acknowledge;
	
	private boolean alive;
	
	
	public TeamPlayer(Player player, boolean ready, boolean acknowledge){
		this.player = player;
		this.ready = ready;
		this.alive = true;
		this.acknowledge = acknowledge;
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
	
	public void setReady(boolean ready){
		this.ready = ready;
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
	
}
