package com.alchemi.as.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.alchemi.al.Messenger;
import com.alchemi.as.Auction;
import com.alchemi.as.AuctionStorm;

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
	
	@SuppressWarnings("serial")
	public void give(Player p) {
		if (queue.containsKey(p.getName())) {
			Auction.giveItemStack(queue.get(p.getName()), p);
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Given"), p, new HashMap<String, String>() {
				{
					put("$sender$", AuctionStorm.instance.pluginname);
					put("$amount$", String.valueOf(queue.get(p.getName()).getAmount()));
					put("$item$", Auction.getItemName(queue.get(p.getName())));
					put("$name$", Auction.getDisplayName(queue.get(p.getName())));
					put("$valuta$", AuctionStorm.valutaP);
				}
			});
			
			queue.remove(p.getName());
			players.remove(p.getName());
			
			for (String pl : players) {
				config.set("Queue." + pl, queue.get(pl));
			}
			AuctionStorm.instance.getFileManager().saveConfig("giveQueue.yml");
		}
	}
	
	public boolean isPlayerQueued(Player p) {
		return queue.containsKey(p.getName()) && config.contains(p.getName()) && config.contains(p.getName() + ".item");
	}
	
	public void addPlayer(OfflinePlayer seller, ItemStack i) {
		queue.put(seller.getName(), i);
		players.add(seller.getName());
		AuctionStorm.instance.getFileManager().getConfig("giveQueue.yml").set("Queue." + seller.getName() + ".item", i);
		AuctionStorm.instance.getFileManager().saveConfig("giveQueue.yml");
	}
	
}
