package com.alchemi.as.listeners.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.alchemi.al.objects.handling.CarbonDating;
import com.alchemi.as.Queue;
import com.alchemi.as.main;
import com.alchemi.as.objects.Config;

public class CommandAdmin implements CommandExecutor{
	
	public final String return_usage = main.getInstance().getCommand("asadmin").getUsage();
	public final String admin_usage = main.getInstance().getCommand("asadmin").getUsage();
	public final String info_usage = main.getInstance().getCommand("asadmin").getUsage();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (main.hasPermission(sender, "as.admin") && cmd.getName().equals("asadmin")) {
			CarbonDating datetime = null;
			
			
			
			if (args.length == 0) {
				String send = Config.MESSAGES.COMMAND_WRONG_FORMAT.value().replace("$sender$", cmd.getName()).replace("$format$", admin_usage);
				if (sender instanceof Player) send = send.replace("$player$", ((Player) sender).getDisplayName());
				else send = send.replace("$player$", main.getInstance().pluginname);
				
				main.getInstance().getMessenger().sendMessage(send, sender);
				
				return true;
			}
			
			if (args[0].equalsIgnoreCase("return")) { //return command
				
				if (!main.hasPermission(sender, "as.return")) {
					String send = Config.MESSAGES.COMMAND_NO_PERMISSION.value().replace("$sender$", cmd.getName());
					if (sender instanceof Player) send = send.replace("$player$", ((Player) sender).getDisplayName());
					else send = send.replace("$player$", main.getInstance().pluginname);
					
					main.getInstance().getMessenger().sendMessage(send, sender);
					return true;
				
				} else if (!Config.AUCTION.LOGAUCTIONS.asBoolean()) {
					String send = Config.MESSAGES.COMMAND_ADMIN_LOGGING_DISABLED.value().replace("$sender$", cmd.getName());
					if (sender instanceof Player) send = send.replace("$player$", ((Player) sender).getDisplayName());
					else send = send.replace("$player$", main.getInstance().pluginname);
					
					main.getInstance().getMessenger().sendMessage(send, sender);
					
					return true;
				}
				
				if (args.length < 4) {
					String send = Config.MESSAGES.COMMAND_WRONG_FORMAT.value().replace("$sender$", cmd.getName()).replace("$format$", return_usage);
					if (sender instanceof Player) send = send.replace("$player$", ((Player) sender).getDisplayName());
					else send = send.replace("$player$", main.getInstance().pluginname);
					
					main.getInstance().getMessenger().sendMessage(send, sender);
					return true;
				}
				
				if (args.length == 4) datetime = new CarbonDating(args[3]);
				else if (args.length == 8){
					datetime = new CarbonDating(args[3], args[4], args[5], args[6], args[7]);
				}
				
				if (datetime == null || datetime.getCarbonDate() == null) {
					String send = Config.MESSAGES.COMMAND_WRONG_FORMAT.value().replace("$sender$", cmd.getName()).replace("$format$", return_usage);
					if (sender instanceof Player) send = send.replace("$player$", ((Player) sender).getDisplayName());
					else send = send.replace("$player$", main.getInstance().pluginname);
					
					main.getInstance().getMessenger().sendMessage(send, sender);
					return true;
				}
				
				if (args[1].equalsIgnoreCase("all")) {
					main.logger.readLog(args[2], datetime).returnAll(sender);
				}
				else if (args[1].equalsIgnoreCase("item")) {
					main.logger.readLog(args[2], datetime).returnItemToSeller(sender);
				}
				else if (args[1].equalsIgnoreCase("money")) {
					main.logger.readLog(args[2], datetime).returnMoneyToBuyer(sender);
				} 
				else {
					String send = Config.MESSAGES.COMMAND_WRONG_FORMAT.value().replace("$sender$", cmd.getName()).replace("$format$", return_usage);
					if (sender instanceof Player) send = send.replace("$player$", ((Player) sender).getDisplayName());
					else send = send.replace("$player$", main.getInstance().pluginname);
					
					main.getInstance().getMessenger().sendMessage(send, sender);
					return true;
				}
			}
			
			else if (args[0].equalsIgnoreCase("info")) { //info command
				if (!Config.AUCTION.LOGAUCTIONS.asBoolean()) {
					String send = Config.MESSAGES.COMMAND_ADMIN_LOGGING_DISABLED.value().replace("$sender$", cmd.getName());
					if (sender instanceof Player) send = send.replace("$player$", ((Player) sender).getDisplayName());
					else send = send.replace("$player$", main.getInstance().pluginname);
					
					main.getInstance().getMessenger().sendMessage(send, sender);
					return true;
				}
				if (args.length == 2 && args[1].equalsIgnoreCase("latest")) {
					
					if (main.logger.hasLatestLog()) {
						main.logger.getLastAuction().getInfo(sender);
						return true;
					}
					else {
						String send = Config.MESSAGES.COMMAND_ADMIN_NO_LOGS.value().replace("$sender$", cmd.getName()).replace("$format$", info_usage);
						if (sender instanceof Player) send = send.replace("$player$", ((Player) sender).getDisplayName());
						else send = send.replace("$player$", main.getInstance().pluginname);
						
						main.getInstance().getMessenger().sendMessage(send, sender);
						return true;
					}
				}
				
				if (args.length < 3) {
					String send = Config.MESSAGES.COMMAND_WRONG_FORMAT.value().replace("$sender$", cmd.getName()).replace("$format$", info_usage);
					if (sender instanceof Player) send = send.replace("$player$", ((Player) sender).getDisplayName());
					else send = send.replace("$player$", main.getInstance().pluginname);
					
					main.getInstance().getMessenger().sendMessage(send, sender);
					return true;
				}
				
				if (args.length == 3) datetime = new CarbonDating(args[2]);
				else if (args.length == 7){
					datetime = new CarbonDating(args[2], args[3], args[4], args[5], args[6]);
				}
				
				if (datetime == null || datetime.getCarbonDate() == null) {
					String send = Config.MESSAGES.COMMAND_WRONG_FORMAT.value().replace("$sender$", cmd.getName()).replace("$format$", info_usage);
					if (sender instanceof Player) send = send.replace("$player$", ((Player) sender).getDisplayName());
					else send = send.replace("$player$", main.getInstance().pluginname);
					
					main.getInstance().getMessenger().sendMessage(send, sender);
					return true;
				}
				
				main.logger.readLog(args[1], datetime).getInfo(sender);
			}
			
			else if (args[0].equalsIgnoreCase("reload")) { //reload command
				if (!main.hasPermission(sender, "as.reload")) return true;
				
				else {
					main.getInstance().config.reload();
					if (Queue.getQueueLength() != 0) {
							Queue.clearQueue(true, "config reload");
						}
				
				}
				
				main.getInstance().getMessenger().reloadMessages();
				main.getInstance().getMessenger().getTag();
				main.getInstance().getMessenger().sendMessage("&9Configs have been reloaded!", sender);
				
			}
		}
		return true;
	}
}
