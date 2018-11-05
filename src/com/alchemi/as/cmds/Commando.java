package com.alchemi.as.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.alchemi.al.Library;
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
			+ info_usage + "&6\n    " + info_desc;
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		
		
		if (Library.checkCmdPermission(cmd, sender, "as.base", "auc") && sender instanceof Player) {
			Player player = (Player)sender;
			
			
			if (args.length == 0) {
				Library.sendMsg("&8Use " + help_usage + "&8 to get help", player, null);
				return true;
			}
			
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("help") || args[0].equals("?")) {
				
					Library.sendMsg(help_message, player, null);
					return true;
				
				} else if (args.length >= 2) { //auction start command
					if (args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("s")){
						int price = 0;
						int amount = player.getInventory().getItemInMainHand().getAmount();
						int increment = 10;
						int duration = 30;
						
						if (Library.containsAny(args[1], "0123456789")) price = Integer.valueOf(args[1]);
						if (args.length >= 3 && Library.containsAny(args[2], "0123456789") && Integer.valueOf(args[1]) > 0) amount = Integer.valueOf(args[2]);
						if (args.length >= 4 && Library.containsAny(args[3], "0123456789") && Integer.valueOf(args[2]) > 0) increment = Integer.valueOf(args[3]);
						if (args.length == 5 && Library.containsAny(args[4], "0123456789") && Integer.valueOf(args[4]) >= 30 && Integer.valueOf(args[4]) <= 240) duration = Integer.valueOf(args[4]);
						
						
						if (price == 0) {
							Library.sendMsg("Usage: " + start_usage, player, null);
							return false;
						}
						
						Queue.addAuction(new Auction(player, price, duration, amount, increment));
						return true;
					}
				} else if (args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("s")) { 
					
					Library.sendMsg("Usage: " + start_usage, player, null);
					
				} else if (args.length >= 1) { 
					
					if (args[0].equalsIgnoreCase("bid")) { //bid command
					
						if (Queue.current_auction == null) {
							Auction.noAuction(player);
							return false;
						}
						
						if (args.length >= 2 && Library.containsAny(args[1], "0123456789")) Queue.current_auction.bid(Integer.valueOf(args[0]), (Player)sender);
						else Library.sendMsg("&8Usage: &9" + Commando.bid_usage, (Player)sender, null);
						
						if (args.length == 3 && Library.containsAny(args[2], "0123456789")) Queue.current_auction.bid(Integer.valueOf(args[1]), (Player)sender, true);
						
						return true;
					
					} else if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("i")) { //info command
						if (Queue.current_auction != null) Queue.current_auction.getInfo(player);
						else Auction.noAuction(player);
					
					} else if (args[0].equalsIgnoreCase("end") || args[0].equalsIgnoreCase("cancel")) { //end command
						
						if (Queue.current_auction == null) {
							Auction.noAuction(player);
							return false;
						}
						
						if (player.equals(Queue.current_auction.getSeller())) {
							if (args.length == 2) Queue.current_auction.forceEndAuction(args[1]);
							else Queue.current_auction.forceEndAuction();
							return true;
							
						} else if (player.isOp() || player.hasPermission("as.cancel")) {
							if (args.length == 2) Queue.current_auction.forceEndAuction(args[1], player);
							else Queue.current_auction.forceEndAuction("", player);
							return true;
							
						} else {
							Library.sendMsg("You cannot do this!", player, null);
							return false;
						}
					}
					
				} 
			}
			Library.sendMsg("This command does not exist, use " + help_usage + " to get help.", player, null);
			return true;
		}
		
		return false;
		
		
	}

	
	
}
