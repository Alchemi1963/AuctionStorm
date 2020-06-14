package me.alchemi.as.listeners.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.alchemi.al.objects.handling.CarbonDating;
import me.alchemi.as.Queue;
import me.alchemi.as.Storm;
import me.alchemi.as.objects.Config;
import me.alchemi.as.objects.Messages;
import me.alchemi.as.objects.placeholder.StringParser;

public class CommandAdmin implements CommandExecutor{
	
	public final String return_usage = Storm.getInstance().getCommand("asadmin").getUsage();
	public final String admin_usage = Storm.getInstance().getCommand("asadmin").getUsage();
	public final String info_usage = Storm.getInstance().getCommand("asadmin").getUsage();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (Storm.hasPermission(sender, "as.admin") && cmd.getName().equals("asadmin")) {
			CarbonDating datetime = null;
			
			if (args.length == 0) {
				StringParser send = new StringParser(Messages.COMMAND_WRONG_FORMAT).sender(cmd.getName()).format(admin_usage);
				if (sender instanceof Player) send = send.player((Player) sender);
				else send = send.player(Storm.getInstance().pluginname);
				
				Storm.getInstance().getMessenger().sendMessage(send, sender);
				
				return true;
			}
			
			if (args[0].equalsIgnoreCase("return")) { //return command
				
				if (!Storm.hasPermission(sender, "as.return")) {
					StringParser send = new StringParser(Messages.COMMAND_NO_PERMISSION).sender(cmd.getName());
					if (sender instanceof Player) send = send.player(((Player) sender).getDisplayName());
					else send = send.player(Storm.getInstance().pluginname);
					
					Storm.getInstance().getMessenger().sendMessage(send, sender);
					return true;
				
				} else if (!Config.AuctionOptions.LOGAUCTIONS.asBoolean()) {
					StringParser send = new StringParser(Messages.COMMAND_ADMIN_LOGGING_DISABLED).sender(cmd.getName());
					if (sender instanceof Player) send = send.player(((Player) sender).getDisplayName());
					else send = send.player(Storm.getInstance().pluginname);
					
					Storm.getInstance().getMessenger().sendMessage(send, sender);
					
					return true;
				}
				
				if (args.length < 4) {
					StringParser send = new StringParser(Messages.COMMAND_WRONG_FORMAT).sender(cmd.getName()).format(return_usage);
					if (sender instanceof Player) send = send.player(((Player) sender).getDisplayName());
					else send = send.player(Storm.getInstance().pluginname);
					
					Storm.getInstance().getMessenger().sendMessage(send, sender);
					return true;
				}
				
				if (args.length == 4) datetime = new CarbonDating(args[3]);
				else if (args.length == 8){
					datetime = new CarbonDating(args[3], args[4], args[5], args[6], args[7], "0");
				}
				
				if (datetime == null || datetime.getCarbonDate() == null) {
					StringParser send = new StringParser(Messages.COMMAND_WRONG_FORMAT).sender(cmd.getName()).format(return_usage);
					if (sender instanceof Player) send = send.player(((Player) sender).getDisplayName());
					else send = send.player(Storm.getInstance().pluginname);
					
					Storm.getInstance().getMessenger().sendMessage(send, sender);
					return true;
				}
				
				if (args[1].equalsIgnoreCase("all")) {
					Storm.logger.readLog(args[2], datetime).returnAll(sender);
				}
				else if (args[1].equalsIgnoreCase("item")) {
					Storm.logger.readLog(args[2], datetime).returnItemToSeller(sender);
				}
				else if (args[1].equalsIgnoreCase("money")) {
					Storm.logger.readLog(args[2], datetime).returnMoneyToBuyer(sender);
				} 
				else {
					StringParser send = new StringParser(Messages.COMMAND_WRONG_FORMAT).sender(cmd.getName()).format(return_usage);
					if (sender instanceof Player) send = send.player(((Player) sender).getDisplayName());
					else send = send.player(Storm.getInstance().pluginname);
					
					Storm.getInstance().getMessenger().sendMessage(send, sender);
					return true;
				}
			}
			
			else if (args[0].equalsIgnoreCase("info")) { //info command
				if (!Config.AuctionOptions.LOGAUCTIONS.asBoolean()) {
					StringParser send = new StringParser(Messages.COMMAND_ADMIN_LOGGING_DISABLED).sender(cmd.getName());
					if (sender instanceof Player) send = send.player(((Player) sender).getDisplayName());
					else send = send.player(Storm.getInstance().pluginname);
					
					Storm.getInstance().getMessenger().sendMessage(send, sender);
					return true;
				}
				if (args.length == 2 && args[1].equalsIgnoreCase("latest")) {
					
					if (Storm.logger.hasLatestLog()) {
						Storm.logger.getLastAuction().getInfo(sender);
						return true;
					}
					else {
						StringParser send = new StringParser(Messages.COMMAND_ADMIN_NO_LOGS).sender(cmd.getName()).format(info_usage);
						if (sender instanceof Player) send = send.player(((Player) sender).getDisplayName());
						else send = send.player(Storm.getInstance().pluginname);
						
						Storm.getInstance().getMessenger().sendMessage(send, sender);
						return true;
					}
				}
				
				if (args.length < 3) {
					StringParser send = new StringParser(Messages.COMMAND_WRONG_FORMAT).sender(cmd.getName()).format(info_usage);
					if (sender instanceof Player) send = send.player(((Player) sender).getDisplayName());
					else send = send.player(Storm.getInstance().pluginname);
					
					Storm.getInstance().getMessenger().sendMessage(send, sender);
					return true;
				}
				
				if (args.length == 3) datetime = new CarbonDating(args[2]);
				else if (args.length == 7){
					datetime = new CarbonDating(args[2], args[3], args[4], args[5], args[6], "0");
				}
				
				if (datetime == null || datetime.getCarbonDate() == null) {
					StringParser send = new StringParser(Messages.COMMAND_WRONG_FORMAT).sender(cmd.getName()).format(info_usage);
					if (sender instanceof Player) send = send.player(((Player) sender).getDisplayName());
					else send = send.player(Storm.getInstance().pluginname);
					
					Storm.getInstance().getMessenger().sendMessage(send, sender);
					return true;
				}
				
				Storm.logger.readLog(args[1], datetime).getInfo(sender);
			}
			
			else if (args[0].equalsIgnoreCase("reload")) { //reload command
				if (!Storm.hasPermission(sender, "as.reload")) return true;
				
				else {
					Storm.getInstance().config.reload();
					if (Queue.getQueueLength() != 0) {
							Queue.clearQueue(true, "config reload");
						}
				
				}
				
				Storm.getInstance().getMessenger().reloadMessages();
				Storm.getInstance().getMessenger().getTag();
				Storm.getInstance().getMessenger().sendMessage("&9Configs have been reloaded!", sender);
				
			}
		}
		return true;
	}
}
