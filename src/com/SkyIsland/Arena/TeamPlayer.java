package com.SkyIsland.Arena;

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
	
	private boolean alive;
	
	
	public TeamPlayer(Player player, boolean ready){
		this.player = player;
		this.ready = ready;
		this.alive = true;
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
