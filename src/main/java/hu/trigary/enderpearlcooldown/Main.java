package hu.trigary.enderpearlcooldown;

import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

@SuppressWarnings ("unused")
public class Main extends JavaPlugin implements Listener {
	@Override
	public void onEnable () {
		saveDefaultConfig ();
		FileConfiguration config = getConfig ();
		duration = config.getInt ("duration") * 1000;
		sound = config.getString ("sound");
		playSound = (sound != null && !sound.equals (""));
		
		cooldowns = new HashMap<> ();
		getServer ().getPluginManager ().registerEvents (this, this);
	}
	
	private long duration;
	private String sound;
	private boolean playSound;
	private Map<UUID, Long> cooldowns;
	
	@SuppressWarnings ("unused")
	@EventHandler (ignoreCancelled = true)
	public void onProjectileLaunch (ProjectileLaunchEvent event) {
		if (event.getEntity () instanceof EnderPearl && event.getEntity ().getShooter () instanceof Player) {
			Player shooter = (Player)event.getEntity ().getShooter ();
			if (!shooter.hasPermission ("enderpearlcooldown.bypass")) {
				cooldowns.entrySet ().removeIf ((entry) -> entry.getValue () < System.currentTimeMillis ());
				if (cooldowns.containsKey (shooter.getUniqueId ())) {
					event.setCancelled (true);
					if (playSound) {
						shooter.playSound (shooter.getLocation (), sound, SoundCategory.MASTER, 1, 1);
					}
				} else {
					cooldowns.put (shooter.getUniqueId (), System.currentTimeMillis () + duration);
				}
			}
		}
	}
}
