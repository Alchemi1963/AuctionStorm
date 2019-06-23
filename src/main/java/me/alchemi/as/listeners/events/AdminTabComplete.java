package me.alchemi.as.listeners.events;

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

import me.alchemi.as.main;

public class AdminTabComplete implements TabCompleter {
    
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {

		List<String> tabSuggest = new ArrayList<>();
		List<String> list = new ArrayList<>();
		
		if (!(sender instanceof Player))
			return tabSuggest;

		if (!main.hasPermission(sender, "as.admin"))
			return tabSuggest;
		
		System.out.println(Arrays.asList(args));
		System.out.println(args[0].equalsIgnoreCase("reload"));
		
		if (args.length == 1 && !Arrays.asList(new String[] {"reload", "return", "info"}).contains(args[0])) {
			
			if (main.hasPermission(sender, "as.reload")) list.add("reload");
			if (main.hasPermission(sender, "as.defaults")) list.add("defaults");
			if (main.hasPermission(sender, "as.return")) list.add("return");
			list.add("info");
			
		} else if (args.length == 2) {
			if (args[0].equalsIgnoreCase("return") && main.hasPermission(sender, "as.return")
					|| args[0].equalsIgnoreCase("info")) {
				
				for (OfflinePlayer op : Bukkit.getServer().getOfflinePlayers()) {
					if (main.logger.logger.contains(op.getName())) {
						list.add(op.getName());
					}
				}
				
				if (args[0].equalsIgnoreCase("info")) list.add("latest");
				
			}
			
		} else if (args.length == 3) {
			
			if (args[0].equalsIgnoreCase("return") && main.hasPermission(sender, "as.return")) {
				list.add("all");
				list.add("item");
				list.add("money");
			} else if (args[0].equalsIgnoreCase("info")) {
				if (main.logger.logger.contains(args[1])) {
					Set<String> set = main.logger.logger.getConfigurationSection(args[1]).getValues(false).keySet();
					set.remove("UUID");
					list.addAll(set);
				}
			}
		} else if (args.length == 4) {
			if (args[0].equalsIgnoreCase("return") && main.hasPermission(sender, "as.return")) {
				if (main.logger.logger.contains(args[1])) {
					Set<String> set = main.logger.logger.getConfigurationSection(args[1]).getValues(false).keySet();
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
