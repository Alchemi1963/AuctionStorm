package com.alchemi.as.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.alchemi.al.Library;
import com.alchemi.al.Messenger;
import com.alchemi.as.Auction;
import com.alchemi.as.AuctionStorm;
import com.alchemi.as.Queue;


public class Commando implements CommandExecutor{

	public static final String start_usage = "&9" + AuctionStorm.instance.getCommand("auctionstorm start").getUsage();
	public static final String bid_usage = "&9" + AuctionStorm.instance.getCommand("bid").getUsage();;
	public static final String help_usage = "&9" + AuctionStorm.instance.getCommand("auctionstorm help").getUsage();
	public static final String info_usage = "&9" + AuctionStorm.instance.getCommand("auctionstorm info").getUsage();
	
	public static final String start_desc = "&9" + AuctionStorm.instance.getCommand("auctionstorm start").getDescription();
	public static final String bid_desc = "&9" + AuctionStorm.instance.getCommand("bid").getDescription();
	public static final String help_desc = "&9" + AuctionStorm.instance.getCommand("auctionstorm help").getDescription();
	public static final String info_desc = "&9" + AuctionStorm.instance.getCommand("auctionstorm info").getDescription();
	
	private static final String help_message = "&6---------- AuctionStorm Help ----------\n"
			+ start_usage + "&6\n    " + start_desc + "\n"
			+ help_usage + "\n     Display this page.\n"
			+ bid_usage + "&6\n    " + bid_desc +  "\n"
			+ info_usage + "&6\n    " + info_desc
			+ "&6---------------------------------------";
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (AuctionStorm.hasPermission(sender, "as.base") && sender instanceof Player && cmd.getName().equals("auc")) {
			Player player = (Player)sender;
						
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("help") || args[0].equals("?")) { //help command
				
					Messenger.sendMsg(help_message, player);
					return true;
					
				} else if (args[0].equalsIgnoreCase("start") && args.length < 2 || args[0].equalsIgnoreCase("s")  && args.length < 2) { 
					
					Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + start_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
					return true;
					
				} else if (args.length >= 1) { 
					
					if (args[0].equalsIgnoreCase("bid")) { //bid command
					
						if (Queue.current_auction == null) {
							Auction.noAuction(player);
							return true;
						}
						
						if (args.length == 1) {
							Queue.current_auction.bid((Player) sender);
							return true;
						}
						
						try {
							if (args.length >= 2 && Library.containsAny(args[1], "0123456789")) Queue.current_auction.bid(Integer.valueOf(args[0]), (Player)sender);
							else Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + bid_usage, player, player.getDisplayName(), cmd.getName());
							
							if (args.length == 3 && Library.containsAny(args[2], "0123456789")) Queue.current_auction.bid(Integer.valueOf(args[1]), (Player)sender, true);
						
						} catch(NumberFormatException e) {
							Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + Commando.bid_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
						}
						return true;
					
					} else if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("i")) { //info command
						if (Queue.current_auction != null) Queue.current_auction.getInfo(player);
						else Auction.noAuction(player);
						
						return true;
					
					} else if (args[0].equalsIgnoreCase("end") || args[0].equalsIgnoreCase("cancel")) { //cancel command
						
						if (Queue.current_auction == null) {
							Auction.noAuction(player);
							return true;
						}
						String reason = "";
						if (args.length >= 2) {
							for (int x = 1 ; x < args.length; x++) {
								if (reason != "") reason = reason + " " + args[x];
								else reason = args[x];
							}
						}
						
						if (player.equals(Queue.current_auction.getSeller())) {
							if (reason != "") Queue.current_auction.forceEndAuction(reason);
							else Queue.current_auction.forceEndAuction();
							return true;
							
						} else if (AuctionStorm.hasPermission(player, "as.cancel")) {
							if (reason != "") Queue.current_auction.forceEndAuction(reason, player);
							else Queue.current_auction.forceEndAuction("", player);
							return true;
							
						} else {
							if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.No-Permission"), (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
							return true;
						}
					}
					
					else if (args.length >= 2 && args[0].equalsIgnoreCase("start") 
						|| args.length >= 2 && args[0].equalsIgnoreCase("s")) { //auction start command
						int price = AuctionStorm.config.getInt("Auction.Start-Defaults.Price");
						int amount = player.getInventory().getItemInMainHand().getAmount();
						int increment = AuctionStorm.config.getInt("Auction.Start-Defaults.Increment");
						int duration = AuctionStorm.config.getInt("Auction.Start-Defaults.Duration");
						
						try {
							price = Integer.valueOf(args[1]);
							if (args.length >= 3) amount = Integer.valueOf(args[2]);
							if (args.length >= 4) increment = Integer.valueOf(args[3]);
							if (args.length == 5) duration = Integer.valueOf(args[4]);
						
						} catch(NumberFormatException e) {
							Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + start_usage, player, player.getDisplayName(), cmd.getName());
						}
						
						new Auction(player, price, duration, amount, increment);
						return true;					
					}  
				}  
			}
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Unknown"), (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
			return true;
		}
		if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.No-Permission"), (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
		return true;
		
		
	}

	
	
}
