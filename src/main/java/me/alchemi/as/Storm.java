package me.alchemi.as;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.RegisteredServiceProvider;

import me.alchemi.al.configurations.Messenger;
import me.alchemi.al.configurations.SexyConfiguration;
import me.alchemi.al.objects.base.PluginBase;
import me.alchemi.al.objects.handling.UpdateChecker;
import me.alchemi.as.listeners.commands.CommandAdmin;
import me.alchemi.as.listeners.commands.CommandBid;
import me.alchemi.as.listeners.commands.CommandPlayer;
import me.alchemi.as.listeners.events.AdminTabComplete;
import me.alchemi.as.listeners.events.BaseTabComplete;
import me.alchemi.as.listeners.events.BidTabComplete;
import me.alchemi.as.listeners.events.UserLoginHandler;
import me.alchemi.as.objects.AuctionMessenger;
import me.alchemi.as.objects.Config;
import me.alchemi.as.objects.GiveQueue;
import me.alchemi.as.objects.Logging;
import me.alchemi.as.objects.Messages;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Storm extends PluginBase implements Listener {
	public String pluginname;
	public Economy econ;
	private Permission perm;
	
	public Config config;
	
	private static Storm instance;
	public static Logging logger;
	public static GiveQueue gq;
	public SexyConfiguration giveQueue;
	
	public UpdateChecker uc;
	
	public static List<Material> banned_items = new ArrayList<Material>();

	@Override
	public void onEnable() {
		
		for (String s : getDescription().getDepend()) {
			if (Bukkit.getPluginManager().getPlugin(s) == null 
					|| !Bukkit.getPluginManager().isPluginEnabled(s)) {
				Bukkit.getLogger().log(Level.SEVERE, ChatColor.translateAlternateColorCodes('&', "&4&lDependency %depend% not found, disabling plugin...".replace("%depend%", s)));
				getServer().getPluginManager().disablePlugin(this);
			}
		}
		
		instance = this;
		pluginname = getDescription().getName();
		
		SPIGOT_ID = 62778;
		
		setMessenger(new AuctionMessenger(this));
		messenger.setMessages(Messages.values());
		
		messenger.print("Enabling AuctionStorm...");
		
		try {
			config = new Config();
			messenger.print("Configs enabled.");
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
			getServer().getPluginManager().disablePlugin(this);
			Messenger.printS("Configs enabling errored, disabling plugin.", "&4[AuctionStorm]");
		}
		
		if (Config.ConfigEnum.CONFIG.getConfig().getBoolean("update-checker", true)) uc = new UpdateChecker(this);
		
		if (!new File(getDataFolder(), "queue.yml").exists()) saveResource("queue.yml", false);
		giveQueue = SexyConfiguration.loadConfiguration(new File(getDataFolder(), "queue.yml"));
		
		if (Config.AuctionOptions.LOGAUCTIONS.asBoolean()) logger = new Logging("log.yml");
		
		if (!setupEconomy() ) {
			messenger.print("Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
		if (!setupPermission()) {
			messenger.print("No Vault dependency found, silence command disabled!");
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
	
	private boolean setupPermission() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) return false;
		
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		
		if (rsp == null) return false;
		
		perm = rsp.getProvider();
		return perm != null;
	}
	
	public boolean permsEnabled() {
		return perm != null;
	}
	
	public static Storm getInstance() {
		return instance;
	}
	
	@Override
	public Messenger getMessenger() {
		return messenger;
	}
	
	public static <T> T getMetadata(Class<T> metaClass, String metaKey, Player player){
		
		if (!player.hasMetadata(metaKey)) return null;
		for (MetadataValue meta : player.getMetadata(metaKey)) {
			if (metaClass.isInstance(meta)) {
				return metaClass.cast(meta);
			}
		}
		return null;		
	}
}
