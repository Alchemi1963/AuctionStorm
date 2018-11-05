package com.alchemi.as;

import java.io.File;

import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.alchemi.al.Library;
import com.alchemi.as.cmds.CommandBid;
import com.alchemi.as.cmds.Commando;
import com.alchemi.as.cmds.FileManager;

import net.milkbowl.vault.economy.Economy;

public class AuctionStorm extends JavaPlugin implements Listener {
	public String pluginname;
	public static Economy econ;

	private FileManager fileManager;
	public FileManager getFileManager() {
		return fileManager;
	}
	private final int MESSAGES_FILE_VERSION = 0;
	private final int CONFIG_FILE_VERSION = 0;
	
	public static AuctionStorm instance;

	@Override
	public void onEnable() {
		instance = this;
		
		pluginname = getDescription().getName();

		Library.print("Vworp vworp vworp", pluginname);
		
		//init resources
		if (!setupEconomy() ) {
            Library.print("[%s] - Disabled due to no Vault dependency found!", pluginname);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		
		//config = getConfig();
		//checkFileExists(configfile);
		
		
		//registry
		registerCommands();
		//getServer().getPluginManager().registerEvents(this, this);

//van martijnpu:
		saveDefaultConfig();
		fileManager = new FileManager(this);
		getFileManager().saveDefaultMessagesConfig();

		if(!fileManager.getMessagesConfig().isSet("File-Version-Do-Not-Edit") || !fileManager.getMessagesConfig().get("File-Version-Do-Not-Edit").equals(MESSAGES_FILE_VERSION)) {
			Library.print("Your messages file is outdated! Updating...", pluginname);
			fileManager.updateConfig("messages.yml");
			fileManager.getMessagesConfig().set("File-Version-Do-Not-Edit", MESSAGES_FILE_VERSION);
			fileManager.saveMessagesConfig();
			Library.print("File successfully updated!", pluginname);
		}
		if(!getConfig().isSet("File-Version-Do-Not-Edit") || !getConfig().get("File-Version-Do-Not-Edit").equals(CONFIG_FILE_VERSION)) {
			Library.print("Your messages file is outdated! Updating...", pluginname);
			fileManager.updateConfig("config.yml");
			getConfig().set("File-Version-Do-Not-Edit", CONFIG_FILE_VERSION);
			saveConfig();
			Library.print("File successfully updated!", pluginname);
		}
		
	}
	
	@Override
	public void onDisable() {
		
		if (Queue.current_auction != null) {
			Queue.current_auction.forceEndAuction("plugin reload", null);
		}
		saveConfig();
		Library.print("I don't wanna go...", pluginname);
		
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
