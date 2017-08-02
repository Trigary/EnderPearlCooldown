package hu.trigary.enderpearlcooldown;

import org.bukkit.ChatColor;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Main extends JavaPlugin implements Listener {
	@Override
	public void onEnable() {
		saveDefaultConfig();
		FileConfiguration config = getConfig();
		duration = config.getInt("duration") * 1000;
		sound = config.getString("sound");
		message = ChatColor.translateAlternateColorCodes('&', config.getString("message"));
		
		cooldowns = new HashMap<>();
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	private long duration;
	private String sound;
	private String message;
	private Map<UUID, Long> cooldowns;
	
	
	
	@EventHandler(ignoreCancelled = true)
	void onEnderPearlThrow(ProjectileLaunchEvent event) {
		if (!(event.getEntity() instanceof EnderPearl) || !(event.getEntity().getShooter() instanceof Player)) {
			return;
		}
		
		Player player = (Player)event.getEntity().getShooter();
		if (player.hasPermission("enderpearlcooldown.bypass")) {
			return;
		}
		
		Long cooldown = cooldowns.get(player.getUniqueId());
		if (cooldown != null && cooldown > System.currentTimeMillis()) {
			event.setCancelled(true);
			executeAction(player, cooldown);
		} else {
			cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + duration);
		}
	}
	
	
	
	private void executeAction(Player player, long cooldown) {
		if (!sound.isEmpty()) {
			player.playSound(player.getLocation(), sound, SoundCategory.MASTER, 1, 1);
		}
		
		if (!message.isEmpty()) {
			player.sendMessage(message.replace("%time%", String.valueOf(Math.floorDiv(cooldown - System.currentTimeMillis(), 1000) + 1)));
		}
	}
}
