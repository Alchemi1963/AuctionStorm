package com.alchemi.as;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import com.alchemi.al.Messenger;


public class Auction {

	private ItemStack object;
	private Player seller;
	private Player highest_bidder;
	
	private Player secret_bidder;
	private int secret_bid = 0;
	
	private int price;
	private int duration;
	private int amount;
	private int increment;
	
	private final String priceS;
	private final String durationS;
	private final String amountS;
	private final String incrementS;
	
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
		
		priceS = String.valueOf(price);
		durationS = String.valueOf(duration);
		amountS = String.valueOf(amount);
		incrementS = String.valueOf(increment);
		
		this.timer = AuctionStorm.instance.getServer().getScheduler();
		
		object = seller.getInventory().getItemInMainHand();
		seller.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
		
		if (object.getType().name().equalsIgnoreCase("air")) {
			
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.No-Item"), seller, seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), priceS, AuctionStorm.valutaP, durationS, incrementS);
			seller.getInventory().setItemInMainHand(object);
			return;
						
		} else if (seller.getGameMode().equals(GameMode.CREATIVE) && !seller.hasPermission("as.creative") && !seller.isOp()) {
			
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Not-Creative"), seller, seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), priceS, AuctionStorm.valutaP, durationS, incrementS);
			seller.getInventory().setItemInMainHand(object);
			return;
			
		} 
		if (object.getAmount() < this.amount) {
			
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Not-Enough"), seller, seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), priceS, AuctionStorm.valutaP, durationS, incrementS);
			seller.getInventory().setItemInMainHand(object);
			return;
			
		}
		
		Queue.addAuction(this);
		
	}
	
	public ItemStack getObject() {
		return object;
	}
	
	public Player getSeller() {
		return seller;
	}
	
	public boolean startAuction() {
		AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.Start"), seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), priceS, AuctionStorm.valutaP, durationS, incrementS);
		
		//20 ticks/second * 60 seconds/minute = 1200 ticks/minute
		atimer = new AuctionTimer(duration);
		task_id = timer.runTaskTimer(AuctionStorm.instance, atimer, 0, 20).getTaskId();
		
		return true;
	}
	public boolean bid(int bid, Player bidder) {bid(bid, bidder, false); return true;}
	
	public boolean bid(int bid, Player bidder, boolean secret) {
		if (bidder.equals(seller)) {
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.No-Bid-Self"), seller, seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), priceS, AuctionStorm.valutaP, durationS, incrementS);
			return false;
		}
		
		//check if player has enough money for bid
		if (!AuctionStorm.econ.has(bidder, bid)) {
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.No-Bid-Money"), bidder, bidder.getDisplayName(), "[SERVER]", amountS, getItemName(object), priceS, AuctionStorm.valutaP, durationS, incrementS);
			return false;
		}
		
		if (current_bid+increment > bid && current_bid > 0) {
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Low-Bid"), bidder, bidder.getDisplayName(), "[SERVER]", amountS, getItemName(object), String.valueOf(current_bid + increment), AuctionStorm.valutaP, durationS, incrementS);
			return false;
		} else if (price > bid) {
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Low-Bid"), bidder, bidder.getDisplayName(), "[SERVER]", amountS, getItemName(object), priceS, AuctionStorm.valutaP, durationS, incrementS);
			return false;
		}
		
		if (secret) {
			
			secret_bid = bid;
			secret_bidder = bidder;
			
			return true;
			
		}
		
		if (bid <= secret_bid) {
				Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Outbid"), bidder, secret_bidder.getDisplayName(), "[SERVER]", amountS, getItemName(object), priceS, AuctionStorm.valutaP, durationS, incrementS);
				bid = secret_bid;
				bidder = secret_bidder;
				
				secret_bid = 0;
				secret_bidder = null;
		}
		
		current_bid = bid;
		highest_bidder = bidder;
		
		AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.Bid"), bidder.getDisplayName(), "[SERVER]", amountS, getItemName(object), String.valueOf(bid), AuctionStorm.valutaP, durationS, incrementS);
		
		if (atimer.time < 5.0F) {
			atimer.time += 30.0F;
			AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.Time-Added"), bidder.getDisplayName(), "[SERVER]", amountS, getItemName(object), String.valueOf(bid), AuctionStorm.valutaP, durationS, incrementS);
		}
		
		return true;
	}
	
	public boolean getInfo(Player p) {
		String msg = Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info"), seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), priceS, AuctionStorm.valutaP, String.valueOf(atimer.time), incrementS);
		
		if (highest_bidder != null && current_bid > 0) {
			
			msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info2"), highest_bidder.getDisplayName(), "[SERVER]", amountS, getItemName(object), String.valueOf(current_bid), AuctionStorm.valutaP, durationS, incrementS);
			
		}
		
		msg = msg + "\n&l&9======================";
		//show enchantments if present?
		
		Messenger.sendMsg(msg, p);
		return true;
	}
	
	public void endAuction() {
		
		if (highest_bidder != null) {
			try {
				highest_bidder.playSound(highest_bidder.getLocation(), Sound.valueOf(AuctionStorm.config.getString("Auction.Sound.Pay")), 1.0F, 0.0F);
				seller.playSound(seller.getLocation(), Sound.valueOf(AuctionStorm.config.getString("Auction.Sound.Paid")), 1.0F, 0.0F);
			} catch (Exception e) {
				e.printStackTrace();
				highest_bidder.playSound(highest_bidder.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 0.0F);
				seller.playSound(seller.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 0.0F);
			}
			highest_bidder.getInventory().addItem(object);
			//take money from highest bidder
			AuctionStorm.econ.withdrawPlayer(highest_bidder, current_bid);
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Paid-To"), highest_bidder, seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), String.valueOf(current_bid), AuctionStorm.valutaP, durationS, incrementS);
			
			//give money to seller
			
			AuctionStorm.econ.depositPlayer(seller, current_bid);
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Paid-By"), seller, highest_bidder.getDisplayName(), "[SERVER]", amountS, getItemName(object), String.valueOf(current_bid), AuctionStorm.valutaP, durationS, incrementS);
			
			AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.End"), highest_bidder.getDisplayName(), "[SERVER]", amountS, getItemName(object), String.valueOf(current_bid), AuctionStorm.valutaP, durationS, incrementS);

		} else {
			try {
				seller.playSound(seller.getLocation(), Sound.valueOf(AuctionStorm.config.getString("Auction.Sound.Failed")), 1.0F, 0.0F);
			} catch (Exception e) {
				e.printStackTrace();
				seller.playSound(seller.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 0.0F);
			}
			seller.getInventory().addItem(object);
			AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.End-No-Bids"), seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), priceS, AuctionStorm.valutaP, durationS, incrementS);
		}
		Queue.nextAuction();
		
		
		
		
	}
	
	public void forceEndAuction() { forceEndAuction(""); }
	
	public void forceEndAuction(String reason) { forceEndAuction(reason, seller, false); }
	
	public void forceEndAuction(String reason, Player ender) { forceEndAuction(reason, ender, false); }
	
	public void forceEndAuction(String reason, Player ender, boolean all) {

		timer.cancelTask(task_id);
		
		seller.getInventory().addItem(object);
		
		if (ender == null || ender.hasPermission("as.cancel") || ender.isOp() || ender == seller) {
		
			String displayname;
			if (ender == null) displayname = "the server"; 
			else displayname = ender.getDisplayName();
			
			if (reason != "") AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Command.End-Auction-Reason"), null, displayname, amountS, getItemName(object), String.valueOf(current_bid), AuctionStorm.valutaP, durationS, incrementS, reason);
			else AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Command.End-Auction"), null, displayname, amountS, getItemName(object), String.valueOf(current_bid), AuctionStorm.valutaP, durationS, incrementS, reason);
			
		} else Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.No-Permission"), ender, ender.getDisplayName(), "/as cancel", amountS, getItemName(object), String.valueOf(current_bid), AuctionStorm.valutaP, durationS, incrementS);
		
		if (!all) Queue.nextAuction();
		
	}

	public static void noAuction(Player player) {
		Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.No-Auction"), player, player.getDisplayName());
	}
	
	String getItemName(ItemStack item) {
		return item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : object.getItemMeta().hasLocalizedName() ? object.getItemMeta().getLocalizedName() : item.getType().name().toLowerCase().replaceAll("_", " ");
	}
}
