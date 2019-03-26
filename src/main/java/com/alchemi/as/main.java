package com.alchemi.as;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.alchemi.al.configurations.Messenger;
import com.alchemi.al.configurations.SexyConfiguration;
import com.alchemi.as.listeners.commands.CommandAdmin;
import com.alchemi.as.listeners.commands.CommandBid;
import com.alchemi.as.listeners.commands.CommandPlayer;
import com.alchemi.as.listeners.events.AdminTabComplete;
import com.alchemi.as.listeners.events.BaseTabComplete;
import com.alchemi.as.listeners.events.BidTabComplete;
import com.alchemi.as.listeners.events.UserLoginHandler;
import com.alchemi.as.objects.Config;
import com.alchemi.as.objects.GiveQueue;
import com.alchemi.as.objects.Logging;

import net.milkbowl.vault.economy.Economy;

public class main extends JavaPlugin implements Listener {
	public String pluginname;
	public static Economy econ;
	
	public static Messenger messenger;
	
	public static final int MESSAGES_FILE_VERSION = 24;
	public static final int CONFIG_FILE_VERSION = 20;
	
	public static File MESSAGES_FILE;
	public static File CONFIG_FILE;
	
	public static main instance;
	public static Logging logger;
	public static GiveQueue gq;
	public SexyConfiguration giveQueue;
	
	public static List<Material> banned_items = new ArrayList<Material>();

	@Override
	public void onEnable() {
		instance = this;
		pluginname = getDescription().getName();

		//start files
		
		MESSAGES_FILE = new File(getDataFolder(), "messages.yml");
		CONFIG_FILE = new File(getDataFolder(), "config.yml");
		
		messenger = new Messenger(this);
		messenger.print("Enabling AuctionStorm...");
		
		try {
			Config.enable();
			messenger.print("Configs enabled.");
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
			getServer().getPluginManager().disablePlugin(this);
			Messenger.printStatic("Configs enabling errored, disabling plugin.", "&4[DodgeChallenger]");
		}
		
		if (!new File(getDataFolder(), "giveQueue.yml").exists()) saveResource("giveQueue.yml", false);
		giveQueue = SexyConfiguration.loadConfiguration(new File(getDataFolder(), "giveQueue.yml"));
		
		if (Config.AUCTION.LOGAUCTIONS.asBoolean()) logger = new Logging("log.yml");
		
		if (!setupEconomy() ) {
			messenger.print("[%s] - Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		
		gq = new GiveQueue(giveQueue);
		
		//registry
		registerCommands();
		getServer().getPluginManager().registerEvents(new UserLoginHandler(), this);
		
		messenger.print("&1Vworp vworp vworp");
	}
	
	@Override
	public void onDisable() {
		
		if (Queue.getQueueLength() != 0) {
			Queue.clearQueue(true, "a server restart");
		}
		
		messenger.print("&4I don't wanna go...");
		
	}
	
	private void registerCommands() {
		getCommand("auc").setExecutor(new CommandPlayer());
		getCommand("bid").setExecutor(new CommandBid());
		getCommand("asadmin").setExecutor(new CommandAdmin());

		getCommand("auc").setTabCompleter(new BaseTabComplete());
		getCommand("bid").setTabCompleter(new BidTabComplete());
		getCommand("asadmin").setTabCompleter(new AdminTabComplete());
	}
	
	public static boolean hasPermission(Player player, String perm) {
		
		return (player.isOp() || player.hasPermission(perm));
	}
	
	public static boolean hasPermission(CommandSender sender, String perm) {
		return ( !(sender instanceof Player) || sender.isOp() || sender.hasPermission(perm));
	}
	
	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
        
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return false;
        
        econ = rsp.getProvider();
        return econ != null;
    }
}
