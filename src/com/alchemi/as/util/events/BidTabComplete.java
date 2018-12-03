package com.alchemi.as.util.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.alchemi.as.AuctionStorm;
import com.alchemi.as.Queue;

public class BidTabComplete implements TabCompleter {
    
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

		List<String> tabSuggest = new ArrayList<>();
		List<String> list = new ArrayList<>();
		
		if (!(sender instanceof Player))
			return tabSuggest;

		if (!AuctionStorm.hasPermission(sender, "as.base"))
			return tabSuggest;
		
		if (Queue.current_auction == null) {
			return tabSuggest;
		}
		
		if (args.length == 1) {
			
			list.add(String.valueOf(Queue.current_auction.getCurrent_bid() + Queue.current_auction.getIncrement()));
				
		} else if (args.length == 2) {
			
			list.add(String.valueOf(Queue.current_auction.getCurrent_bid() + Queue.current_auction.getIncrement() + 25));
		}

		for (int i = list.size() - 1; i >= 0; i--)
			if(list.get(i).startsWith(args[args.length - 1]))
				tabSuggest.add(list.get(i));

		Collections.sort(tabSuggest);
		return tabSuggest;
	}
	
}
