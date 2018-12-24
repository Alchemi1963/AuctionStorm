package com.alchemi.as.util.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.alchemi.as.AuctionStorm;

public class AdminTabComplete implements TabCompleter {
    
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

		List<String> tabSuggest = new ArrayList<>();
		List<String> list = new ArrayList<>();
		
		if (!(sender instanceof Player))
			return tabSuggest;

		if (!AuctionStorm.hasPermission(sender, "as.admin"))
			return tabSuggest;
		
		System.out.println(Arrays.asList(args));
		System.out.println(args[0].equalsIgnoreCase("reload"));
		
		if (args.length == 1 && !Arrays.asList(new String[] {"reload", "defaults", "return", "info"}).contains(args[0])) {
			
			if (AuctionStorm.hasPermission(sender, "as.reload")) list.add("reload");
			if (AuctionStorm.hasPermission(sender, "as.defaults")) list.add("defaults");
			if (AuctionStorm.hasPermission(sender, "as.return")) list.add("return");
			list.add("info");
			
		} else if (args.length == 2) {
			
			if (args[0].equalsIgnoreCase("reload")) {
				list.add("all");
				list.add("config.yml");
				list.add("giveQueue.yml");
				list.add("messages.yml");
			} else if (args[0].equalsIgnoreCase("return") && AuctionStorm.hasPermission(sender, "as.return")
					|| args[0].equalsIgnoreCase("info")) {
				
				for (OfflinePlayer op : Bukkit.getServer().getOfflinePlayers()) {
					if (AuctionStorm.logger.logger.contains(op.getName())) {
						list.add(op.getName());
					}
				}
				
				if (args[0].equalsIgnoreCase("info")) list.add("latest");
				
			}
			
		} else if (args.length == 3) {
			
			if (args[0].equalsIgnoreCase("return") && AuctionStorm.hasPermission(sender, "as.return")) {
				list.add("all");
				list.add("item");
				list.add("money");
			} else if (args[0].equalsIgnoreCase("info")) {
				if (AuctionStorm.logger.logger.contains(args[1])) {
					Set<String> set = AuctionStorm.logger.logger.getConfigurationSection(args[1]).getValues(false).keySet();
					set.remove("UUID");
					list.addAll(set);
				}
			}
		} else if (args.length == 4) {
			if (args[0].equalsIgnoreCase("return") && AuctionStorm.hasPermission(sender, "as.return")) {
				if (AuctionStorm.logger.logger.contains(args[1])) {
					Set<String> set = AuctionStorm.logger.logger.getConfigurationSection(args[1]).getValues(false).keySet();
					set.remove("UUID");
					list.addAll(set);
				}
			}
		}

		for (int i = list.size() - 1; i >= 0; i--)
			if(list.get(i).startsWith(args[args.length - 1]))
				tabSuggest.add(list.get(i));

		Collections.sort(tabSuggest);
		return tabSuggest;
	}
	
}
