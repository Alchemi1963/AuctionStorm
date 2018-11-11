package com.alchemi.as.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.alchemi.al.CarbonDating;
import com.alchemi.al.Library;
import com.alchemi.al.Messenger;
import com.alchemi.as.AuctionStorm;

public class CommandAdmin implements CommandExecutor{
	
	public final String return_usage = AuctionStorm.instance.getCommand("asadmin return").getUsage();
	public final String admin_usage = AuctionStorm.instance.getCommand("asadmin").getUsage();
	public final String info_usage = AuctionStorm.instance.getCommand("asadmin info").getUsage();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (Library.checkCmdPermission(cmd, sender, "as.admin", "asadmin") && (sender instanceof Player)) {
			CarbonDating datetime = null;
			
			if (args.length == 0) {
				Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + admin_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
				return false;
			}
			
			if (args[0].equalsIgnoreCase("return")) { //return command
				
				if (args.length < 4) {
					Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + return_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					return false;
				}
				
				if (args.length == 4) datetime = new CarbonDating(args[3]);
				else if (args.length == 8){
					datetime = new CarbonDating(args[3], args[4], args[5], args[6], args[7]);
				}
				
				if (datetime == null || datetime.getCarbonDate() == null) {
					Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + return_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					return false;
				}
				
				if (args[1].equalsIgnoreCase("all")) {
					AuctionStorm.logger.readLog(args[2], datetime).returnAll();
				}
				else if (args[1].equalsIgnoreCase("item")) {
					AuctionStorm.logger.readLog(args[2], datetime).returnItemToSeller();
				}
				else if (args[1].equalsIgnoreCase("money")) {
					AuctionStorm.logger.readLog(args[2], datetime).returnMoneyToBuyer();
				} 
				else {
					Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + return_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					return false;
				}
			}
			
			else if (args[0].equalsIgnoreCase("info")) { //info command
				if (args.length < 3) {
					Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + return_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					return false;
				}
				
				if (args.length == 3) datetime = new CarbonDating(args[2]);
				else if (args.length == 7){
					datetime = new CarbonDating(args[2], args[3], args[4], args[5], args[6]);
				}
				
				if (datetime == null || datetime.getCarbonDate() == null) {
					Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + return_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					return false;
				}
				
				AuctionStorm.logger.readLog(args[1], datetime).getInfo(sender);
			}
			
		} else if (!(sender instanceof Player)){
			CarbonDating datetime = null;
			
			if (args.length == 0) {
				AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + admin_usage, AuctionStorm.instance.pluginname, cmd.getName());
				return false;
			}
			
			if (args[0].equalsIgnoreCase("return")) { //return command
				
				if (args.length < 4) {
					AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + return_usage, AuctionStorm.instance.pluginname, cmd.getName());
					return false;
				}
				
				if (args.length == 4) datetime = new CarbonDating(args[3]);
				else if (args.length == 8){
					datetime = new CarbonDating(args[3], args[4], args[5], args[6], args[7]);
				}
				
				if (datetime == null || datetime.getCarbonDate() == null) {
					AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + return_usage, AuctionStorm.instance.pluginname, cmd.getName());
					return false;
				}
				
				if (args[1].equalsIgnoreCase("all")) {
					AuctionStorm.logger.readLog(args[2], datetime).returnAll();
				}
				else if (args[1].equalsIgnoreCase("item")) {
					AuctionStorm.logger.readLog(args[2], datetime).returnItemToSeller();
				}
				else if (args[1].equalsIgnoreCase("money")) {
					AuctionStorm.logger.readLog(args[2], datetime).returnMoneyToBuyer();
				}
				else {
					AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + return_usage, AuctionStorm.instance.pluginname, cmd.getName());
					return false;
				}
				
			}
			
			else if (args[0].equalsIgnoreCase("info")) { //info command
				if (args.length < 3) {
					Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + return_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					return false;
				}
				
				if (args.length == 3) datetime = new CarbonDating(args[2]);
				else if (args.length == 7){
					datetime = new CarbonDating(args[2], args[3], args[4], args[5], args[6]);
				}
				
				if (datetime == null || datetime.getCarbonDate() == null) {
					Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + return_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					return false;
				}
				
				AuctionStorm.logger.readLog(args[1], datetime).getInfo(sender);
			}
		}
		
		return true;
	}

}
