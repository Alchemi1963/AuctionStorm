package com.alchemi.toa;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.alchemi.al.Library;
import com.alchemi.toa.cmds.Commando;

import net.minecraft.server.v1_13_R2.ItemStack;

public class AuctionStorm extends JavaPlugin implements Listener {
	public static String pluginname;
	public static FileConfiguration config;
	public static FileConfiguration data;
	public File datafile = new File(getDataFolder(), "as-data.yml");
	public File configfile = new File(getDataFolder(), "config.yml");
	
	private Map<String, Object> data_defaults = new HashMap<String, Object>();
	public static ArrayList<Player> banned_players = new ArrayList<Player>(); 

	@Override
	public void onEnable() {
		
		data_defaults.put("baddies", new ArrayList<Player>());
		data_defaults.put("current_auction", new Object[]{new ItemStack(null), 0, 0});
		
		pluginname = getDescription().getName();

		Library.print("Vworp vworp vworp", pluginname);
		
		//init resources
		config = getConfig();
		checkFileExists(configfile);
		
		data = Library.loadExtraConfig(datafile);
		data.addDefaults(data_defaults);
		
		//registry
		registerCommands();
		getServer().getPluginManager().registerEvents(this, this);
		
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		
		Player player = e.getPlayer();
		String playername = player.getName();
		
		if (data.getStringList("baddies").contains(playername)) banned_players.add(player);
		
	}
	
	@Override
	public void onDisable() {
		
		
		Library.saveExtraConfig(datafile, data);
		saveConfig();
		Library.print("I don't wanna go...", pluginname);
		
	}
	
	private void registerCommands() {
		getCommand("auctionstorm").setExecutor(new Commando());
	}
	
	public void checkFileExists(File file) {
		if (!file.exists()) {
			Library.print(file.getName() + " not found, creating!", pluginname);
			if (file.getName().equals("config.yml")) {
				saveDefaultConfig();
			}
			else {
				saveResource(file.getName(), true);
			}
			
		} else {
			
			Library.print(file.getName() + " found, loading!", pluginname);
			
		}
	}
}
