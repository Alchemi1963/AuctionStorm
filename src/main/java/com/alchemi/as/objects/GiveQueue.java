package com.alchemi.as.objects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.alchemi.as.Auction;
import com.alchemi.as.main;

public class GiveQueue {

	private Map<String, ItemStack> queue = new HashMap<String, ItemStack>();
	private List<String> players = new ArrayList<String>();
	
	private final FileConfiguration config;
	
	public GiveQueue(FileConfiguration gq) {
		config = gq;
		players = gq.getStringList("Queue");
		
		for (String p : players) {
			queue.put(gq.getString("Queue." + p), gq.getItemStack("Queue." + p + ".item"));
		}
		
	}
	
	public void give(Player p) {
		if (queue.containsKey(p.getName())) {
			Auction.giveItemStack(queue.get(p.getName()), p);
			
			main.messenger.sendMessage(Config.MESSAGES.COMMAND_GIVEN.value().replace("$sender$", main.instance.pluginname)
					.replace("$amount$", String.valueOf(queue.get(p.getName()).getAmount()))
					.replace("$item$", Auction.getItemName(queue.get(p.getName())))
					.replace("$name$", Auction.getDisplayName(queue.get(p.getName())))
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString()), p);
			
			queue.remove(p.getName());
			players.remove(p.getName());
			
			for (String pl : players) {
				config.set("Queue." + pl, queue.get(pl));
			}
			try {
				main.instance.giveQueue.save();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean isPlayerQueued(Player p) {
		return queue.containsKey(p.getName()) && config.contains(p.getName()) && config.contains(p.getName() + ".item");
	}
	
	public void addPlayer(OfflinePlayer seller, ItemStack i) {
		queue.put(seller.getName(), i);
		players.add(seller.getName());
		main.instance.giveQueue.set("Queue." + seller.getName() + ".item", i);
		try {
			main.instance.giveQueue.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
