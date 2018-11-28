package com.alchemi.as.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.alchemi.al.Messenger;
import com.alchemi.as.Auction;
import com.alchemi.as.AuctionStorm;
import com.alchemi.as.Queue;

public class CommandBid implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (AuctionStorm.hasPermission(sender, "as.base") && sender instanceof Player && cmd.getName().equals("bid")) {
			
			if (Queue.current_auction == null) {
				Auction.noAuction((Player)sender);
				return true;
			}
			
			if (args.length == 0) {
				Queue.current_auction.bid((Player) sender);
				return true;
			}
			
			try {
				if (args.length >= 1 && args[0] != "0") Queue.current_auction.bid(Integer.valueOf(args[0]), (Player)sender);
				else Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + Commando.bid_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
				
				if (args.length == 2 && args[1] != "0") Queue.current_auction.bid(Integer.valueOf(args[1]), (Player)sender, true);
			} catch(NumberFormatException e) {
				Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + Commando.bid_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
			}
			
			return true;
		}
		if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.No-Permission"), (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
		return true;
	}

	
	
}
