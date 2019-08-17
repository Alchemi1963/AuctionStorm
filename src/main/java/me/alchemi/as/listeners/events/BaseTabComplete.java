package me.alchemi.as.listeners.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.alchemi.as.Queue;
import me.alchemi.as.Storm;

public class BaseTabComplete implements TabCompleter {
    
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

		List<String> tabSuggest = new ArrayList<>();
		List<String> list = new ArrayList<>();
		
		if (!(sender instanceof Player))
			return tabSuggest;

		if (!Storm.hasPermission(sender, "as.base"))
			return tabSuggest;
		
		if (args.length == 1 && !Arrays.asList("start", "s", "help", "info", "i", "cancel", "bid", "listqueue").contains(args[0])) {
			
			list.add("start");
			list.add("help");
			list.add("info");
			list.add("listqueue");
			if (Queue.current_auction != null) list.add("bid");
			if (Queue.current_auction != null 
					&& Queue.current_auction.getSeller().equals((Player) sender) 
					|| Storm.hasPermission(sender, "as.cancel")) list.add("cancel");
			if (Queue.current_auction != null 
					&& Queue.current_auction.getSeller().equals((Player) sender) 
					|| sender.hasPermission("as.end")) list.add("end");
			if (Storm.getInstance().permsEnabled() 
					&& sender.hasPermission("as.togglesilence")) list.add("silence"); 
			
		} else if (args.length == 2) {
			
			if (args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("s")) {
				
				list.add("1");
				list.add("2");
				list.add("3");
				list.add("4");
				list.add("5");
				list.add("6");
				list.add("7");
				list.add("8");
				list.add("9");
				list.add("10");
				
			} else if (args[0].equalsIgnoreCase("bid") && Queue.current_auction != null) {
				list.add(String.valueOf(Queue.current_auction.getCurrent_bid() + Queue.current_auction.getIncrement()));
			} else if (args[0].equalsIgnoreCase("cancel") || args[0].equalsIgnoreCase("end") || args[0].equalsIgnoreCase("c")) {
				
				for (int i = 1; i < Queue.getQueueLength(); i++) {
					list.add(String.valueOf(i));
				}
				
			}
			
		} else if (args.length == 3) {
			
			if (args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("s")) {
				
				list.add("1");
				list.add("2");
				list.add("3");
				list.add("4");
				list.add("5");
				list.add("all");
				list.add("hand");
				
			} else if (args[0].equalsIgnoreCase("bid") && Queue.current_auction != null) {
				list.add(String.valueOf(Queue.current_auction.getCurrent_bid() + Queue.current_auction.getIncrement() + 25));
			}
			
		} else if (args.length >= 4 && args.length <= 5) {
			
			if (args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("s")) {
				
				list.add("1");
				list.add("2");
				list.add("3");
				list.add("4");
				list.add("5");
				list.add("6");
				list.add("7");
				list.add("8");
				list.add("9");
				list.add("10");
				
			}
			
		}

		for (int i = list.size() - 1; i >= 0; i--)
			if(list.get(i).startsWith(args[args.length - 1]))
				tabSuggest.add(list.get(i));

		Collections.sort(tabSuggest);
		return tabSuggest;
	}
	
}
