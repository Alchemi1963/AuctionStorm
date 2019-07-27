package me.alchemi.as.objects;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.alchemi.al.api.MaterialWrapper;
import me.alchemi.al.configurations.SexyConfiguration;
import me.alchemi.as.Auction;
import me.alchemi.as.main;

public class GiveQueue {

	private Map<String, List<ItemStack>> queue = new HashMap<String, List<ItemStack>>();
	
	private final SexyConfiguration config;
	
	@SuppressWarnings("unchecked")
	public GiveQueue(SexyConfiguration gq) {
		config = gq;
		
		for (String player : config.getValues(true).keySet()) {
			
			if (player.endsWith(".item") || player.equals("Queue")) continue;
			
			queue.put(player.replace("Queue.", ""), (List<ItemStack>) gq.getList(player + ".item", new ArrayList<ItemStack>()));
			
			if (queue.get(player.replace("Queue.", "")).isEmpty()) queue.put(player.replace("Queue.", ""), Arrays.asList(gq.getItemStack(player + ".item", new ItemStack(MaterialWrapper.AIR.getMaterial()))));
		
		}
	}
	
	public void give(Player player) {
		for (ItemStack stack : queue.get(player.getName())) {
			Auction.giveItemStack(stack, player);
			
			main.getInstance().getMessenger().sendMessage(Config.MESSAGES.COMMAND_GIVEN.value().replace("$sender$", main.getInstance().pluginname)
					.replace("$amount$", String.valueOf(stack.getAmount()))
					.replace("$item$", Auction.getItemName(stack))
					.replace("$name$", Auction.getDisplayName(stack))
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString()), player);
		}
		
		queue.remove(player.getName());
		
		config.set("Queue." + player.getName(), null);
		
		try {
			config.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isPlayerQueued(OfflinePlayer p) {
		return queue.containsKey(p.getName()) && config.contains("Queue." + p.getName()) && config.contains("Queue." + p.getName() + ".item");
	}
	
	public void addPlayer(OfflinePlayer player, ItemStack i) {
		if (!isPlayerQueued(player)) queue.put(player.getName(), Arrays.asList(i));
		else {
			List<ItemStack> stacks = queue.get(player.getName());
			stacks.add(i);
			queue.put(player.getName(), stacks);
		}
		
		main.getInstance().giveQueue.set("Queue." + player.getName() + ".item", queue.get(player.getName()));
		try {
			config.save();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
