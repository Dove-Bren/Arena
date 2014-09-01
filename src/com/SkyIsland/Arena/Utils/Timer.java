package com.SkyIsland.Arena.Utils;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Timer {
	
	private BukkitRunnable task;
	private boolean verbose;
	private int increment;
	
	public Timer(JavaPlugin plugin, BukkitRunnable task, int ticks, boolean verbose, int increment) {
		this.task = task;
		this.increment = increment;
		
		if (!verbose) {
			//we can do it silenty; we can do all of the delay at once
			task.runTaskLater(plugin, ticks);
		}
//		else {
//			//it's verbose, meaning we have to stop every division and print out time remaining
//			static int elapsed = 0;
//			BukkitRunnable timer = new BukkitRunnable() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					
//				}
//				
//			}
//		}
	}
	
}
