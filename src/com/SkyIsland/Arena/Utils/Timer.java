package com.SkyIsland.Arena.Utils;

import org.bukkit.scheduler.BukkitRunnable;

import com.SkyIsland.Arena.ArenaPlugin;

public class Timer {
	
	private BukkitRunnable job;
	private boolean verbose;
	private int increment, elapsed, total, stepSize;
	private ArenaPlugin plugin;
	private BukkitRunnable timer;
	
	/**
	 * Creates a timer event tied to a plugin. It runs the RunnableTask after [ticks] ticks. If this timer is set to verbose, it will print out
	 * the time remaining to all teams every [step] ticks.
	 * @param plug
	 * @param task
	 * @param ticks
	 * @param verbose
	 * @param step
	 */
	public Timer(ArenaPlugin plug, BukkitRunnable task, int ticks, boolean verbose, int step) {
		this.plugin = plug;
		this.job = task;
		this.stepSize = step;
		this.total = ticks;
		this.elapsed = 0;
		this.verbose = verbose;
		this.timer = null;
		
		if (!this.verbose) {
			//we can do it silenty; we can do all of the delay at once
			job.runTaskLater(plugin, ticks);
		}
		else {
			//it's verbose, meaning we have to stop every division and print out time remaining
			
			//stepSize is how many we WANT to increment by. increment is how mayn we are actually going to increment by. This may change if the first step
			//is a smaller number so that we land on a number that's evenly divisible by it
			BukkitRunnable timer = new BukkitRunnable() {

				@Override
				public void run() {
					//we set increment to a value that's not 0 only if we need to offset the count from the start.
					if (increment != 0) {
						elapsed += increment;
						increment = 0;
					}
					else {
						elapsed += stepSize;
					}
					if (elapsed >= total) {
						job.run();
						cancel();
					}
					else {
						plugin.getArena().broadcastMessage((total - elapsed)/20 + " seconds left!");
					}
				}
				
			};
			
			increment = total % stepSize; //0 if it's a multiple. Anything else
			timer.runTaskTimer(plugin, increment, step);
			
		}
	}
	
	//get
	
	public void cancel() {
		try {
			this.job.cancel();
		}
		catch (IllegalStateException e) {
			//do nothing, because we don't care
		}
		
		if (timer != null) {
			try {
				this.timer.cancel();
			}
			catch (IllegalStateException e) {
				//stil don't care
			}
		}
	}
}
