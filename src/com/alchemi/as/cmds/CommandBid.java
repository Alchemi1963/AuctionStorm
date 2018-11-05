package com.alchemi.as.cmds;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.alchemi.al.Library;
import com.alchemi.as.AuctionStorm;

public class CommandBid implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (Library.checkCmdPermission(cmd, sender, "as.base", "bid") && sender instanceof Player) {
			
			if (args.length >= 1 && Library.containsAny(args[0], "0123456789")) AuctionStorm.instance.current_auction.bid(Integer.valueOf(args[0]), (Player)sender);
			else Library.sendMsg("&8Usage: &9" + Commando.bid_usage, (Player)sender, null);
			
			if (args.length == 2 && Library.containsAny(args[1], "0123456789")) AuctionStorm.instance.current_auction.bid(Integer.valueOf(args[1]), (Player)sender, true);
			
			return true;
		}
		
		return false;
	}

	
	
}
