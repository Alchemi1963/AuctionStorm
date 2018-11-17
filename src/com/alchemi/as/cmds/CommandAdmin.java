package com.alchemi.as.cmds;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.alchemi.al.CarbonDating;
import com.alchemi.al.Messenger;
import com.alchemi.as.AuctionStorm;
import com.alchemi.as.Queue;

public class CommandAdmin implements CommandExecutor{
	
	public final String return_usage = AuctionStorm.instance.getCommand("asadmin return").getUsage();
	public final String admin_usage = AuctionStorm.instance.getCommand("asadmin").getUsage();
	public final String info_usage = AuctionStorm.instance.getCommand("asadmin info").getUsage();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (AuctionStorm.hasPermission(sender, "as.admin") && cmd.getName().equals("asadmin")) {
			CarbonDating datetime = null;
			
			if (args.length == 0) {
				if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + admin_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
				else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + admin_usage, AuctionStorm.instance.pluginname, cmd.getName());
				return true;
			}
			
			if (args[0].equalsIgnoreCase("return")) { //return command
				
				if (!AuctionStorm.hasPermission(sender, "as.return")) {
					Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.No-Permission"), (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					return true;
				
				} else if (!AuctionStorm.config.getBoolean("Auction.LogAuctions")) {
					if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Logging-Disabled"), (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Admin.Logging-Disabled"), AuctionStorm.instance.pluginname, cmd.getName());
					return true;
				}
				
				if (args.length < 4) {
					if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + return_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + return_usage, AuctionStorm.instance.pluginname, cmd.getName());
					return true;
				}
				
				if (args.length == 4) datetime = new CarbonDating(args[3]);
				else if (args.length == 8){
					datetime = new CarbonDating(args[3], args[4], args[5], args[6], args[7]);
				}
				
				if (datetime == null || datetime.getCarbonDate() == null) {
					if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + return_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + return_usage, AuctionStorm.instance.pluginname, cmd.getName());
					return true;
				}
				
				if (args[1].equalsIgnoreCase("all")) {
					AuctionStorm.logger.readLog(args[2], datetime).returnAll(sender);
				}
				else if (args[1].equalsIgnoreCase("item")) {
					AuctionStorm.logger.readLog(args[2], datetime).returnItemToSeller(sender);
				}
				else if (args[1].equalsIgnoreCase("money")) {
					AuctionStorm.logger.readLog(args[2], datetime).returnMoneyToBuyer(sender);
				} 
				else {
					if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + return_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + return_usage, AuctionStorm.instance.pluginname, cmd.getName());
					return true;
				}
			}
			
			else if (args[0].equalsIgnoreCase("info")) { //info command
				if (!AuctionStorm.config.getBoolean("Auction.LogAuctions")) {
					if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Logging-Disabled"), (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Admin.Logging-Disabled"), AuctionStorm.instance.pluginname, cmd.getName());
					return true;
				}
				if (args.length == 2 && args[1].equalsIgnoreCase("latest")) {
					
					if (AuctionStorm.logger.hasLatestLog()) {
						AuctionStorm.logger.getLastAuction().getInfo(sender);
						return true;
					}
					else {
						if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.No-Logs") + info_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
						else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.No-Logs") + info_usage, AuctionStorm.instance.pluginname, cmd.getName());
						return true;
					}
				}
				
				if (args.length < 3) {
					if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + info_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + info_usage, AuctionStorm.instance.pluginname, cmd.getName());
					return true;
				}
				
				if (args.length == 3) datetime = new CarbonDating(args[2]);
				else if (args.length == 7){
					datetime = new CarbonDating(args[2], args[3], args[4], args[5], args[6]);
				}
				
				if (datetime == null || datetime.getCarbonDate() == null) {
					if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + info_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + info_usage, AuctionStorm.instance.pluginname, cmd.getName());
					return true;
				}
				
				AuctionStorm.logger.readLog(args[1], datetime).getInfo(sender);
			}
			
			else if (args[0].equalsIgnoreCase("reload")) { //reload command
				if (!AuctionStorm.hasPermission(sender, "as.reload")) {
					Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.No-Permission"), (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					return true;
				} else if (args.length == 2 && AuctionStorm.instance.getFileManager().hasConfig(args[1])) {
					AuctionStorm.instance.getFileManager().reloadConfig(args[1]);
					if (args[1].equalsIgnoreCase("config.yml")) {
						AuctionStorm.config = AuctionStorm.instance.getConfig();
						AuctionStorm.valutaP = AuctionStorm.config.getString("Vault.valutaPlural");
						AuctionStorm.valutaS = AuctionStorm.config.getString("Vault.valutaSingular");
						AuctionStorm.banned_items = new ArrayList<Material>();
						if (!AuctionStorm.config.getStringList("Auction.Banned-Items").isEmpty()) {
							for (String mat : AuctionStorm.config.getStringList("Auction.Banned-Items")) {
								
								AuctionStorm.banned_items.add(Material.getMaterial(mat));
								
							}
						}
						
						if (Queue.getQueueLength() != 0) {
							Queue.clearQueue(true, "server restarting");
						}
					}
				} else {
					for (String file : AuctionStorm.instance.getFileManager().getFiles().keySet()) {
						AuctionStorm.instance.getFileManager().reloadConfig(file);
						AuctionStorm.config = AuctionStorm.instance.getConfig();
						AuctionStorm.valutaP = AuctionStorm.config.getString("Vault.valutaPlural");
						AuctionStorm.valutaS = AuctionStorm.config.getString("Vault.valutaSingular");
						AuctionStorm.banned_items = new ArrayList<Material>();
						if (!AuctionStorm.config.getStringList("Auction.Banned-Items").isEmpty()) {
							for (String mat : AuctionStorm.config.getStringList("Auction.Banned-Items")) {
								
								AuctionStorm.banned_items.add(Material.getMaterial(mat));
								
							}
						}
						
						if (Queue.getQueueLength() != 0) {
							Queue.clearQueue(true, "config reload");
						}
					}
				}
				
				AuctionStorm.instance.messenger.broadcast("&6Configs have been reloaded!");
				
			}
			
			else if (args[0].equalsIgnoreCase("defaults")) {
				if (!AuctionStorm.hasPermission(sender, "as.default-reload")) {
					Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.No-Permission"), (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					return true;
				} else if (args.length == 2 && AuctionStorm.instance.getFileManager().hasConfig(args[1])) {
					AuctionStorm.instance.getFileManager().reloadDefaultConfig(args[1]);
					if (args[1].equalsIgnoreCase("config.yml")) {
						AuctionStorm.config = AuctionStorm.instance.getConfig();
						AuctionStorm.valutaP = AuctionStorm.config.getString("Vault.valutaPlural");
						AuctionStorm.valutaS = AuctionStorm.config.getString("Vault.valutaSingular");
						AuctionStorm.banned_items = new ArrayList<Material>();
						if (!AuctionStorm.config.getStringList("Auction.Banned-Items").isEmpty()) {
							for (String mat : AuctionStorm.config.getStringList("Auction.Banned-Items")) {
								
								AuctionStorm.banned_items.add(Material.getMaterial(mat));
								
							}
						}
						
						if (Queue.getQueueLength() != 0) {
							Queue.clearQueue(true, "server restarting");
						}
					}
				} else {
					AuctionStorm.instance.getFileManager().reloadDefaultConfig();
					AuctionStorm.config = null;
					
					AuctionStorm.config = AuctionStorm.instance.getFileManager().getConfig("config.yml");
					AuctionStorm.valutaP = AuctionStorm.config.getString("Vault.valutaPlural");
					AuctionStorm.valutaS = AuctionStorm.config.getString("Vault.valutaSingular");
					AuctionStorm.banned_items = new ArrayList<Material>();
					
					System.out.println(AuctionStorm.config.getStringList("Auction.Banned-Items"));
					
					if (!AuctionStorm.config.getStringList("Auction.Banned-Items").isEmpty()) {
						for (String mat : AuctionStorm.config.getStringList("Auction.Banned-Items")) {
							
							AuctionStorm.banned_items.add(Material.getMaterial(mat));
							
						}
					}
					
					if (Queue.getQueueLength() != 0) {
						Queue.clearQueue(true, "config reset");
					}
				}
				AuctionStorm.instance.messenger.broadcast("&6Configs have been reset to default!");
			}
		
		}
		return true;
	}
}
