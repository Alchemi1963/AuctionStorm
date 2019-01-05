package com.alchemi.as;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.alchemi.al.FileManager;
import com.alchemi.al.Messenger;
import com.alchemi.as.cmds.CommandAdmin;
import com.alchemi.as.cmds.CommandBid;
import com.alchemi.as.cmds.Commando;
import com.alchemi.as.util.GiveQueue;
import com.alchemi.as.util.Logging;
import com.alchemi.as.util.events.AdminTabComplete;
import com.alchemi.as.util.events.BaseTabComplete;
import com.alchemi.as.util.events.BidTabComplete;
import com.alchemi.as.util.events.UserLoginHandler;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class AuctionStorm extends JavaPlugin implements Listener {
	public String pluginname;
	public static Economy econ;
	public static Permission perms;
	
	public static boolean VaultPerms = false;

	public FileManager fileManager;
	public Messenger messenger;
	
	public FileManager getFileManager() {
		return fileManager;
	}
	private final int MESSAGES_FILE_VERSION = 20;
	private final int CONFIG_FILE_VERSION = 20;
	
	
	public static String valutaS;
	public static String valutaP;
	
	public static AuctionStorm instance;
	public FileConfiguration config;
	public static Logging logger;
	public static GiveQueue gq;
	
	public static List<Material> banned_items = new ArrayList<Material>();

	@Override
	public void onEnable() {
		instance = this;
		pluginname = getDescription().getName();

		//start files
		
		fileManager = new FileManager(this, new String[]{"config.yml", "messages.yml", "giveQueue.yml"}, null, null, null);
		fileManager.saveDefaultYML("config.yml");
		fileManager.saveDefaultYML("messages.yml");
		fileManager.saveDefaultYML("giveQueue.yml");
		
		messenger = new Messenger(fileManager);
		
		if(!fileManager.hasConfig("giveQueue.yml")) {
			messenger.print("No give queue found, creating yml...");
			fileManager.updateConfig("giveQueue.yml");
			fileManager.saveConfig("giveQueue.yml");
			messenger.print("Give queue successfully created!");
		}
		if(!fileManager.getConfig("messages.yml").isSet("File-Version-Do-Not-Edit") || !fileManager.getConfig("messages.yml").get("File-Version-Do-Not-Edit").equals(MESSAGES_FILE_VERSION)) {
			messenger.print("Your messages file is outdated! Updating...");
			fileManager.reloadConfig("messages.yml");
			fileManager.updateConfig("messages.yml");
			fileManager.getConfig("messages.yml").set("File-Version-Do-Not-Edit", MESSAGES_FILE_VERSION);
			fileManager.saveConfig("messages.yml");
			messenger.print("File successfully updated!");
		}
		if(!getConfig().isSet("File-Version-Do-Not-Edit") || !getConfig().get("File-Version-Do-Not-Edit").equals(CONFIG_FILE_VERSION)) {
			messenger.print("Your config file is outdated! Updating...");
			fileManager.reloadConfig("config.yml");
			fileManager.updateConfig("config.yml");
			fileManager.getConfig("config.yml").set("File-Version-Do-Not-Edit", CONFIG_FILE_VERSION);
			fileManager.saveConfig("config.yml");
			messenger.print("File successfully updated!");
		}

		//init resources
		config = getConfig();
		
		if (config.getBoolean("Auction.LogAuctions")) logger = new Logging("log.yml");
		
		if (!((List<?>) config.get("Auction.Banned-Items", new ArrayList<String>())).isEmpty()) {
			for (String mat : config.getStringList("Auction.Banned-Items")) {
				
				banned_items.add(Material.getMaterial(mat));
				
			}
		}
		
		if (!setupEconomy() ) {
			messenger.print("[%s] - Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		
		if (setupPermission()) {
			messenger.print("Using Vault based permissions!");
			VaultPerms = true;
		}
		
		gq = new GiveQueue(fileManager.getConfig("giveQueue.yml"));
		
		//registry
		registerCommands();
		getServer().getPluginManager().registerEvents(new UserLoginHandler(), this);
		
		
		
		valutaS = config.getString("Vault.valutaSingular");
		valutaP = config.getString("Vault.valutaPlural");
		
		messenger.print("&1Vworp vworp vworp");
	}
	
	@Override
	public void onDisable() {
		
		if (Queue.getQueueLength() != 0) {
			Queue.clearQueue(true, "server restarting");
		}
		
		messenger.print("&4I don't wanna go...");
		
	}
	
	private void registerCommands() {
		getCommand("auc").setExecutor(new Commando());
		getCommand("bid").setExecutor(new CommandBid());
		getCommand("asadmin").setExecutor(new CommandAdmin());

		getCommand("auc").setTabCompleter(new BaseTabComplete());
		getCommand("bid").setTabCompleter(new BidTabComplete());
		getCommand("asadmin").setTabCompleter(new AdminTabComplete());
	}
	
	public void checkFileExists(File file) {
		if (!file.exists()) {
			messenger.print(file.getName() + " not found, creating!");
			if (file.getName().equals("config.yml")) {
				saveDefaultConfig();
			}
			else {
				saveResource(file.getName(), true);
			}
			
		} else {
			
			messenger.print(file.getName() + " found, loading!");
			
		}
	}
	
	public static boolean hasPermission(Player player, String perm) {
		
		return VaultPerms ? perms.has(player, perm) || player.isOp() : player.hasPermission(perm) || player.isOp();
	}
	
	public static boolean hasPermission(CommandSender sender, String perm) {
		return sender instanceof Player ? sender.isOp() || hasPermission((Player) sender, perm) : true;
	}
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        
        econ = rsp.getProvider();
        return econ != null;
    }
	
	private boolean setupPermission() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		if (rsp == null) return false;
		
		perms = rsp.getProvider();
		return perms != null;
	}

	public void reloadConfigValues() {
		
		this.config = fileManager.getConfig("config.yml");
		valutaP = config.getString("Vault.valutaPlural");
		valutaS = config.getString("Vault.valutaSingular");
		banned_items = new ArrayList<Material>();
		if (!((List<?>) config.get("Auction.Banned-Items", new ArrayList<String>())).isEmpty()) {
			for (String mat : config.getStringList("Auction.Banned-Items")) {
				
				banned_items.add(Material.getMaterial(mat));
				
			}
		}
		
	}
}
