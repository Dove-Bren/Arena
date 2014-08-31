package com.SkyIsland.Arena.Team;

import java.util.LinkedList;

import org.bukkit.Color;
import org.bukkit.entity.Player;

public class Team {

	private String teamName;
	private LinkedList<TeamPlayer> players;
	private String teamInfo;
	private Color teamColor;
	
	
	public Team(String teamName, Color color){
		players = new LinkedList<TeamPlayer>();
		this.teamName = teamName;
		this.teamInfo = teamName;
		this.teamColor = color;
	}
	
	
	
	public Color getTeamColor() {
		return teamColor;
	}



	public void setTeamColor(Color teamColor) {
		this.teamColor = teamColor;
	}



	public String getName(){
		return teamName;
	}
	
	public String getInfo(){
		return teamInfo;
	}
	
	public int getNumberPlayers() {
		return this.players.size();
	}
	
	public LinkedList<TeamPlayer> getPlayers(){
		return players;
	}
	
	public boolean isReady(){
		if (players.size() == 0){
			return false;
		}
		for (TeamPlayer p: players){
			if (! p.isReady()){
				return false;
			}
		}
		return true;
	}
	
	public boolean contains(TeamPlayer p){
		return players.contains(p);
	}
	
	public boolean contains(Player p){
		for (TeamPlayer tp: players){
			if (tp.getPlayer().equals(p)){
				return true;
			}
		}
		return false;
	}
	
	public void addPlayer(Player p){
		players.add(new TeamPlayer(p));
	}
	
	public void addPlayer(TeamPlayer p){
		players.add(p);
	}
	
	public void removePlayer(Player p){
		for (TeamPlayer tp: players){
			if (tp.getPlayer().equals(p)){
				players.remove(tp);
			}
		}
	}
	
	public void removePlayer(TeamPlayer p){
		players.remove(p);
	}

	public void setReady(Player p) {
		for (TeamPlayer tp: players){
			if (tp.getPlayer().equals(p)){
				tp.setReady(true);
				return;
			}
		}
	}

	public void setDead(Player player) {
		for (TeamPlayer tp: players){
			if (tp.getPlayer().equals(player)){
				tp.setDead();
				return;
			}
		}
	}
	
	public void setAlive(Player player) {
		for (TeamPlayer tp: players){
			if (tp.getPlayer().equals(player)){
				tp.setAlive();
				return;
			}
		}
	}

	public boolean isAlive() {
		for (TeamPlayer tp: players){
			if (tp.isAlive()){
				return true;
			}
		}
		return false;
	}

	public void alertPlayers(String message) {
		for (TeamPlayer tp: players){
			tp.getPlayer().sendMessage(message);
		}
	}
	

	/**
	 * Asks the team if the given player is ready.
	 * @param player The player we are inquiring about
	 * @return true if the player is in the team and ready and false otherwise
	 */
	public boolean playerReady(Player player) {
		for (TeamPlayer p : players) {
			if (p.getPlayer().equals(player)) {
				//player is in the list
				return p.isReady();
			}
		}
		//player is not in the list
		return false;
	}
}
