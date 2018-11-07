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

public class CommandBid implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (Library.checkCmdPermission(cmd, sender, "as.base", "bid") && sender instanceof Player) {
			
			if (Queue.current_auction == null) {
				Auction.noAuction((Player)sender);
				return false;
			}
			
			if (args.length >= 1 && Library.containsAny(args[0], "0123456789")) Queue.current_auction.bid(Integer.valueOf(args[0]), (Player)sender);
			else Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Wrong-Format") + Commando.bid_usage, (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
			
			if (args.length == 2 && Library.containsAny(args[1], "0123456789")) Queue.current_auction.bid(Integer.valueOf(args[1]), (Player)sender, true);
			
			return true;
		}
		if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.No-Permission"), (Player)sender, ((Player) sender).getDisplayName(), cmd.getName());
		return false;
	}

	
	
}
