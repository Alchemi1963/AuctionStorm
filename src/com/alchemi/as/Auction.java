package com.alchemi.as;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitScheduler;

import com.alchemi.al.Library;


public class Auction {

	private PlayerInventory inventory;
	private ItemStack object;
	private Player seller;
	private Player highest_bidder;
	
	private Player secret_bidder;
	private int secret_bid = 0;
	
	private int price;
	private int duration;
	private int amount;
	private int increment;
	
	private BukkitScheduler timer;
	private AuctionTimer atimer;
	public int task_id;
	
	public int current_bid = 0;
	
	public Auction(Player seller, int price, int duration, int amount, int increment) {
		this.seller = seller;
		this.price = price;
		this.duration = duration;
		this.amount = amount;
		this.increment = increment;
		
		this.inventory = seller.getInventory();
		this.timer = AuctionStorm.instance.getServer().getScheduler();
		startAuction();
	}
	
	public Player getSeller() {
		return seller;
	}
	
	private boolean startAuction() {
		
		
		object = inventory.getItemInMainHand();
		if (object.getType().name().equalsIgnoreCase("air")) {
			
			Library.sendMsg("&4You need to hold an item to start an auction.", seller, null);
			AuctionStorm.instance.current_auction = null;
			return false;
			
		} else if (seller.getGameMode().equals(GameMode.CREATIVE) && !seller.hasPermission("as.creative") && !seller.isOp()) {
			
			AuctionStorm.instance.current_auction = null;
			Library.sendMsg("&4You have no permission to start an auction in creative.", seller, null);
			return false;
			
		}
		inventory.setItemInMainHand(new ItemStack(Material.AIR));
		
		Library.broadcast(seller.getDisplayName() + "&6 has started an auction!", AuctionStorm.instance.pluginname);
		Library.broadcast("&6Use &9/auc info &6to get information about it.", AuctionStorm.instance.pluginname);
		
		//20 ticks/second * 60 seconds/minute = 1200 ticks/minute
		atimer = new AuctionTimer(duration);
		task_id = timer.runTaskTimer(AuctionStorm.instance, atimer, 0, 20).getTaskId();
		
		return true;
	}
	public boolean bid(int bid, Player bidder) {bid(bid, bidder, false); return true;}
	
	public boolean bid(int bid, Player bidder, boolean secret) {
		if (bidder.equals(seller)) {
			Library.sendMsg("&4You cannot bid on your own auction...", seller, null);
			return false;
		}
		
		//check if player has enough money for bid
		if (!AuctionStorm.econ.has(bidder, bid)) {
			Library.sendMsg("&4You do not have enough to make this bid.", bidder, null);
			return false;
		}
		
		if (current_bid+increment > bid && current_bid > 0) {
			Library.sendMsg("&4Your bid must be higher than " + (current_bid + increment) + ".", bidder, null);
			return false;
		} else if (price > bid) {
			Library.sendMsg("&4Your bid must be higher than " + price + ".", bidder, null);
			return false;
		}
		
		if (secret) {
			
			secret_bid = bid;
			secret_bidder = bidder;
			
		}
		
		if (secret_bid != 0) {
			
			if (secret_bid > bid) {
				Library.sendMsg("&9You have been automatically outbit by " + secret_bidder.getDisplayName(), bidder, null);
				bid = secret_bid;
				bidder = secret_bidder;
				
				secret_bid = 0;
				secret_bidder = null;
			}
			
		}
		
		current_bid = bid;
		highest_bidder = bidder;
		
		if (object.getAmount() > 1) {
			if ("aieouy".contains(object.getType().name().toLowerCase().substring(object.getType().name().toLowerCase().length() - 1))) Library.broadcast(bidder.getDisplayName() + "&6 has bid " + bid + " on the " + object.getAmount() + " " + object.getType().name().toLowerCase().replaceAll("_", " ") + "'s.", AuctionStorm.instance.pluginname);
			else Library.broadcast(bidder.getDisplayName() + "&6 has bid " + bid + " on the " + object.getAmount() + " " + object.getType().name().toLowerCase().replaceAll("_", " ") + "s.", AuctionStorm.instance.pluginname);
			return true;
		}
		Library.broadcast(bidder.getDisplayName() + "&6 has bid " + bid + " on the " + object.getType().name().toLowerCase().replaceAll("_", " ") + ".", AuctionStorm.instance.pluginname);
		
		if (atimer.time < 5.0F) {
			atimer.time += 30.0F;
			Library.broadcast("&930 seconds have been added", AuctionStorm.instance.pluginname);
		}
		
		return true;
	}
	
	public boolean getInfo(Player p) {
		String msg = "&l&9=====Current Auction=====\n"
				+ "&r&9Item: &6" + object.getType().name().toLowerCase().replaceAll("_", " ")
				+ "\n&9Amount: &6" + amount
				+ "\n&9Starting Bid: &6" + price + " credits"
				+ "\n&9Time Remaining: &6" + atimer.time + " seconds";
		
		if (highest_bidder != null) msg = msg + "\n&9Highest Bidder: &6" + highest_bidder.getDisplayName();
		if (current_bid != 0) msg = msg + "\nCurrent Bid: &6" + current_bid + " credits"; 
		
		msg = msg + "\n&l&9=========================";
		//show enchantments if present?
		
		Library.sendMsg(msg, p, null);
		return true;
	}
	
	public void endAuction() {
		
		if (highest_bidder != null) {
			highest_bidder.playSound(highest_bidder.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 0.0F);
			highest_bidder.getInventory().addItem(object);
			//take money from highest bidder
			AuctionStorm.econ.withdrawPlayer(highest_bidder, current_bid);
			Library.sendMsg("You have paid " + current_bid + " credits" + " to " + seller.getDisplayName(), highest_bidder, null);
			
			//give money to seller
			AuctionStorm.econ.depositPlayer(seller, current_bid);
			Library.sendMsg("You have been paid " + current_bid + " credits" + " by " + highest_bidder.getDisplayName(), seller, null);
			
			Library.broadcast("&9Sold! To the lovely " + highest_bidder.getDisplayName() + "&9 for " + current_bid + " credits.", AuctionStorm.instance.pluginname);
		} else {
			seller.playSound(seller.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 0.0F);
			inventory.addItem(object);
			Library.broadcast("&9Sold! To no one...", AuctionStorm.instance.pluginname);
		}
		
		AuctionStorm.instance.current_auction = null;
	}
	
	public void forceEndAuction() { forceEndAuction(""); }
	
	public void forceEndAuction(String reason) { forceEndAuction(reason, seller); }
	
	public void forceEndAuction(String reason, Player ender) {

		AuctionStorm.instance.current_auction = null;
		inventory.addItem(object);
		
		if (ender.hasPermission("as.cancel") || ender == null || ender.isOp() || ender == seller) {
		
			String displayname;
			if (ender == null) displayname = "the server"; 
			else displayname = ender.getDisplayName();
			
			if (reason != "") Library.broadcast("&9The current auction was ended by " + displayname + " for " + reason + ".", AuctionStorm.instance.pluginname);
			else Library.broadcast("&9The current auction was ended by " + displayname + ".", AuctionStorm.instance.pluginname);
			
		} else Library.sendMsg("&4You have no permission to cancel an auction started by someone else.", ender, null);
		
	}

	public static void noAuction(Player player) {
		Library.sendMsg("&6There is currently no auction, you can use &9/as start &6to start one.", player, null);
	}
	
}
