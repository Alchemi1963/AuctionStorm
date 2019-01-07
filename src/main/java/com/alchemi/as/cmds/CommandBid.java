package com.alchemi.as.cmds;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.alchemi.as.Auction;
import com.alchemi.as.AuctionStorm;
import com.alchemi.as.Queue;

public class CommandBid implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		Map<String, Object> kaart = new HashMap<String, Object>();
		if (sender instanceof Player) kaart.put("$player$", ((Player) sender).getDisplayName());
		else kaart.put("$player$", AuctionStorm.instance.pluginname);
		kaart.put("$sender$", cmd.getName());
		
		
		if (AuctionStorm.hasPermission(sender, "as.base") && sender instanceof Player && cmd.getName().equals("bid")) {
			
			if (Queue.current_auction == null) {
				Auction.noAuction((Player) sender);
				return true;
			}
			
			if (args.length == 0) {
				Queue.current_auction.bid((Player) sender);
				return true;
			}
			
			try {
				if (args.length >= 1 && args[0] != "0") Queue.current_auction.bid(Integer.valueOf(args[0]), (Player) sender);
				else {
					kaart.put("$format$", CommandPlayer.bid_usage);
					AuctionStorm.instance.messenger.sendMessage("Command.Wrong-Format", sender, kaart);
				}
				
				if (args.length == 2 && args[1] != "0") Queue.current_auction.bid(Integer.valueOf(args[1]), (Player) sender, true);
			} catch(NumberFormatException e) {
				kaart.put("$format$", CommandPlayer.bid_usage);
				AuctionStorm.instance.messenger.sendMessage("Command.Wrong-Format", sender, kaart);
			}
			
			return true;
		}
		if (sender instanceof Player) AuctionStorm.instance.messenger.sendMessage("Command.No-Permission", sender, kaart);
		return true;
	}

}
