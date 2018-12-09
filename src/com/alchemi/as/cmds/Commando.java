package com.alchemi.as.cmds;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.alchemi.al.Messenger;
import com.alchemi.as.Auction;
import com.alchemi.as.AuctionStorm;
import com.alchemi.as.Queue;


public class Commando implements CommandExecutor{

	public static final String start_usage = "&9/auc start <price> [amount] [increment] [duration]";
	public static final String bid_usage = "&9/bid [bid] [secret bid]";
	public static final String help_usage = "&9/auc help";
	public static final String info_usage = "&9/auc info";
	public static final String cancel_usage = "&9/auc cancel";
	
	public static final String start_desc = "&9Start an auction using the item in hand.";
	public static final String bid_desc = "&9Bid on the current auction.";
	public static final String help_desc = "&9Display the AuctionStorm help page.";
	public static final String info_desc = "&9Get info about the current auction.";
	public static final String cancel_desc = "&9Cancel the current auction.";
	
	private static final String help_message = "&6---------- AuctionStorm Help ----------\n"
			+ start_usage + "&6\n    " + start_desc + "\n"
			+ help_usage + "\n     Display this page.\n"
			+ bid_usage + "&6\n    " + bid_desc +  "\n"
			+ info_usage + "&6\n    " + info_desc + "\n"
			+ cancel_usage + "&6\n    " + cancel_desc + "\n"
			+ "&6---------------------------------------";
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		Map<String, String> kaart = new HashMap<String, String>();
		if (sender instanceof Player) kaart.put("$player$", ((Player) sender).getDisplayName());
		else kaart.put("$player$", AuctionStorm.instance.pluginname);
		kaart.put("$sender$", cmd.getName());
		
		
		if (AuctionStorm.hasPermission(sender, "as.base") && sender instanceof Player && cmd.getName().equals("auc")) {
			Player player = (Player) sender;
						
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("help") || args[0].equals("?")) { //help command
				
					Messenger.sendMsg(help_message, player);
					return true;
					
				} else if (args[0].equalsIgnoreCase("start") && args.length < 2 || args[0].equalsIgnoreCase("s")  && args.length < 2) { 
					
					Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + start_usage, player, kaart);
					return true;
					
				} else if (args.length >= 1) { 
					
					if (args[0].equalsIgnoreCase("bid")) { //bid command
					
						if (Queue.current_auction == null) {
							Auction.noAuction(player);
							return true;
						}
						
						if (args.length == 1) {
							Queue.current_auction.bid(player);
							return true;
						}
						
						try {
							if (args.length >= 2 && args[1] != "0") Queue.current_auction.bid(Integer.valueOf(args[0]), player);
							else Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + bid_usage, player, kaart);
							
							if (args.length == 3 && args[2] != "0") Queue.current_auction.bid(Integer.valueOf(args[1]), player, true);
						
						} catch(NumberFormatException e) {
							Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + Commando.bid_usage, player, kaart);
						}
						return true;
					
					} else if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("i")) { //info command
						if (Queue.current_auction != null) Messenger.sendMsg(Queue.current_auction.getInfo(true), player);
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
							
						} else if (AuctionStorm.hasPermission(sender, "as.cancel")) {
							if (reason != "") Queue.current_auction.forceEndAuction(reason, player);
							else Queue.current_auction.forceEndAuction("", player);
							return true;
							
						} else {
							if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.No-Permission"), sender, kaart);
							return true;
						}
					}
					
					else if (args.length >= 2 && args[0].equalsIgnoreCase("start") 
						|| args.length >= 2 && args[0].equalsIgnoreCase("s")) { //start command
						int price = AuctionStorm.instance.config.getInt("Auction.Start-Defaults.Price");
						int amount = player.getInventory().getItemInMainHand().getAmount();
						int increment = AuctionStorm.instance.config.getInt("Auction.Start-Defaults.Increment");
						int duration = AuctionStorm.instance.config.getInt("Auction.Start-Defaults.Duration");
						
						try {
							price = Integer.valueOf(args[1]);
							if (args.length >= 4) increment = Integer.valueOf(args[3]);
							if (args.length == 5) duration = Integer.valueOf(args[4]);
						
						} catch(NumberFormatException e) {
							Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + start_usage, player, kaart);
						}
						
						
						try {
							if (args.length >= 3) {
								amount = Integer.valueOf(args[2]);
							}
						} catch (Exception e){
							if (args.length >= 3 && args[2].equalsIgnoreCase("all")) {
								amount = scanInventory(player.getInventory(), player.getInventory().getItemInMainHand());
							} else if (args.length >= 3 && args[2].equalsIgnoreCase("hand")) {
								amount = player.getInventory().getItemInMainHand().getAmount();
							}
						}
						
						new Auction(player, price, duration, amount, increment);
						return true;					
					}  
				}  
			}
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Unknown"), sender, kaart);
			return true;
		}
		if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.No-Permission"), sender, kaart);
		return true;
	}

	public static int scanInventory(PlayerInventory inventory, ItemStack itemInMainHand) {
		int size = 0;
		
		for (ItemStack stack : inventory) {
			if (stack == null) continue;
			if (stack.isSimilar(itemInMainHand)) {
				size += stack.getAmount();
			}
		}
		
		return size;
	}

	
	
}
