package com.alchemi.as;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.alchemi.al.FileManager;
import com.alchemi.al.Messenger;
import com.alchemi.as.cmds.CommandBid;
import com.alchemi.as.cmds.Commando;

import net.milkbowl.vault.economy.Economy;

public class AuctionStorm extends JavaPlugin implements Listener {
	public String pluginname;
	public static Economy econ;

	private FileManager fileManager;
	public Messenger messenger;
	
	public FileManager getFileManager() {
		return fileManager;
	}
	private final int MESSAGES_FILE_VERSION = 0;
	private final int CONFIG_FILE_VERSION = 0;
	
	public static String valutaS;
	public static String valutaP;
	
	public static AuctionStorm instance;
	public static FileConfiguration config;

	@Override
	public void onEnable() {
		instance = this;
		
		
		pluginname = getDescription().getName();

		Messenger.print("Vworp vworp vworp", pluginname);
		
		//init resources
		if (!setupEconomy() ) {
			Messenger.print("[%s] - Disabled due to no Vault dependency found!", pluginname);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		
		//registry
		registerCommands();

		//start martijnpu
		saveDefaultConfig();
		fileManager = new FileManager(this, new String[]{"config.yml", "messages.yml"}, null, null);
		fileManager.saveDefaultYML("config.yml");
		fileManager.saveDefaultYML("messages.yml");

		if(!fileManager.getFileConfig("messages.yml").isSet("File-Version-Do-Not-Edit") || !fileManager.getFileConfig("messages.yml").get("File-Version-Do-Not-Edit").equals(MESSAGES_FILE_VERSION)) {
			Messenger.print("Your messages file is outdated! Updating...", pluginname);
			fileManager.updateConfig("messages.yml");
			fileManager.getFileConfig("messages.yml").set("File-Version-Do-Not-Edit", MESSAGES_FILE_VERSION);
			fileManager.saveConfig("messages.yml");
			Messenger.print("File successfully updated!", pluginname);
		}
		if(!getConfig().isSet("File-Version-Do-Not-Edit") || !getConfig().get("File-Version-Do-Not-Edit").equals(CONFIG_FILE_VERSION)) {
			Messenger.print("Your messages file is outdated! Updating...", pluginname);
			fileManager.updateConfig("config.yml");
			getConfig().set("File-Version-Do-Not-Edit", CONFIG_FILE_VERSION);
			saveConfig();
			Messenger.print("File successfully updated!", pluginname);
		}
		
		//stop martijnpu
		messenger = new Messenger(this, fileManager);
		
		config = getConfig();
		
		valutaS = config.getString("Vault.valutaSingular");
		valutaP = config.getString("Vault.valutaPlural");
	}
	
	@Override
	public void onDisable() {
		
		if (Queue.current_auction != null) {
			Queue.current_auction.forceEndAuction("plugin reload", null);
		}
		saveConfig();
		Messenger.print("I don't wanna go...", pluginname);
		
	}
	
	private void registerCommands() {
		getCommand("auc").setExecutor(new Commando());
		getCommand("auc help").setExecutor(new Commando());
		getCommand("auc start").setExecutor(new Commando());
		getCommand("auc info").setExecutor(new Commando());
		getCommand("auc cancel").setExecutor(new Commando());
		getCommand("bid").setExecutor(new CommandBid());
	}
	
	public void checkFileExists(File file) {
		if (!file.exists()) {
			Messenger.print(file.getName() + " not found, creating!", pluginname);
			if (file.getName().equals("config.yml")) {
				saveDefaultConfig();
			}
			else {
				saveResource(file.getName(), true);
			}
			
		} else {
			
			Messenger.print(file.getName() + " found, loading!", pluginname);
			
		}
	}
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
}
