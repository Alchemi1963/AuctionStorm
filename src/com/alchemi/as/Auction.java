package com.alchemi.as;

import java.util.Map.Entry;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitScheduler;

import com.alchemi.al.CarbonDating;
import com.alchemi.al.Library;
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
		
		if (object.getAmount() == 0) {//getItemName(object).equalsIgnoreCase("air")) {
			
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Wrong.Item"), seller, seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), priceS, AuctionStorm.valutaP, durationS, incrementS);
			return;
						
		} else if (seller.getGameMode().equals(GameMode.CREATIVE) && !seller.hasPermission("as.creative") && !seller.isOp()) {
			
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Wrong.Creative"), seller, seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), priceS, AuctionStorm.valutaP, durationS, incrementS);
			giveItemStack(object, seller);
			return;
			
		} 
		if (object.getAmount() < this.amount) {
			
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Wrong.Enough"), seller, seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), priceS, AuctionStorm.valutaP, durationS, incrementS);
			giveItemStack(object, seller);
			return;
			
		}
		if (Queue.getQueueLength() > 1) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Queued"), seller, seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), priceS, AuctionStorm.valutaP, durationS, incrementS);
		Queue.addAuction(this);
		
	}
	
	public int getDuration() {
		return duration;
	}
	
	public ItemStack getObject() {
		return object;
	}
	
	public Player getSeller() {
		return seller;
	}
	
	public boolean startAuction() {
		if (getDisplayName(object) != null) {
			AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.StartNamed"), seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), priceS, AuctionStorm.valutaP, durationS, incrementS);
			AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.Info.Get"));
		} else {
			AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.Start"), seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), priceS, AuctionStorm.valutaP, durationS, incrementS);
			AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.Info.Get"));
		}
		
		//20 ticks/second * 60 seconds/minute = 1200 ticks/minute
		atimer = new AuctionTimer(duration);
		task_id = timer.runTaskTimer(AuctionStorm.instance, atimer, 0, 20).getTaskId();
		
		return true;
	}
	public boolean bid(int bid, Player bidder) {bid(bid, bidder, false); return true;}
	
	public boolean bid(int bid, Player bidder, boolean secret) {
		if (bidder.equals(seller)) {
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Bid.Own-Auction"), seller, seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), priceS, AuctionStorm.valutaP, durationS, incrementS);
			return false;
		}
		
		//check if player has enough money for bid
		if (!AuctionStorm.econ.has(bidder, bid)) {
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Bid.No-Money"), bidder, bidder.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), priceS, AuctionStorm.valutaP, durationS, incrementS);
			return false;
		}
		
		if (current_bid+increment > bid && current_bid > 0) {
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Bid.Low"), bidder, bidder.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), String.valueOf(current_bid + increment), AuctionStorm.valutaP, durationS, incrementS);
			return false;
		} else if (price > bid) {
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Bid.Low"), bidder, bidder.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), priceS, AuctionStorm.valutaP, durationS, incrementS);
			return false;
		}
		
		if (secret) {
			
			secret_bid = bid;
			secret_bidder = bidder;
			
			return true;
			
		}
		
		if (bid <= secret_bid) {
				Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Bid.Outbid"), bidder, secret_bidder.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), priceS, AuctionStorm.valutaP, durationS, incrementS);
				bid = secret_bid;
				bidder = secret_bidder;
				
				secret_bid = 0;
				secret_bidder = null;
		}
		
		current_bid = bid;
		highest_bidder = bidder;
		
		AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.Bid.Bid"), bidder.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), String.valueOf(bid), AuctionStorm.valutaP, durationS, incrementS);
		
		if (atimer.time < 5.0F) {
			atimer.time += 30.0F;
			AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.Time.Added"), bidder.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), String.valueOf(bid), AuctionStorm.valutaP, durationS, incrementS);
		}
		
		return true;
	}
	
	public void getInfo(Player p) {
		
		String msg = AuctionStorm.instance.messenger.getMessage("Auction.Info.Header");
		if (getDisplayName(object) != null) msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.ItemNamed"), seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), priceS, AuctionStorm.valutaP, String.valueOf(atimer.time), incrementS);
		else msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Item"), seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), priceS, AuctionStorm.valutaP, String.valueOf(atimer.time), incrementS);
		msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Amount"), seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), priceS, AuctionStorm.valutaP, String.valueOf(atimer.time), incrementS);
		//show enchantments if present
		if (!object.getEnchantments().isEmpty()) {
			msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.EnchantmentHeader"), seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), priceS, AuctionStorm.valutaP, String.valueOf(atimer.time), incrementS);
			for (Entry<Enchantment, Integer> ench : object.getEnchantments().entrySet()) {
				msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Enchantment"), seller.getDisplayName(), "[SERVER]", RomanNumber.toRoman(ench.getValue()), getItemName(object), ench.getKey().getKey().getKey(), priceS, AuctionStorm.valutaP, String.valueOf(atimer.time), incrementS);
			}
		}
		msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.StartingBid"), seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), priceS, AuctionStorm.valutaP, String.valueOf(atimer.time), incrementS);
		if (highest_bidder != null) msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Bidder"), highest_bidder.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), String.valueOf(current_bid), AuctionStorm.valutaP, String.valueOf(atimer.time), incrementS);
		if (current_bid > 0) msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.CurrentBid"), highest_bidder.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), String.valueOf(current_bid), AuctionStorm.valutaP, String.valueOf(atimer.time), incrementS);
		msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Time"), seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), priceS, AuctionStorm.valutaP, String.valueOf(atimer.time), incrementS);
		msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Footer"), seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), priceS, AuctionStorm.valutaP, String.valueOf(atimer.time), incrementS);
		
		Messenger.sendMsg(msg, p);
	}
	
	public void endAuction() {
		
		AuctionLog log = null;
		
		
		if (highest_bidder != null) {
			log = new AuctionLog(seller, highest_bidder, current_bid, object);
			AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.End.End"), highest_bidder.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), String.valueOf(current_bid), AuctionStorm.valutaP, durationS, incrementS);
			
			try {
				highest_bidder.playSound(highest_bidder.getLocation(), Sound.valueOf(AuctionStorm.config.getString("Auction.Sound.Pay")), 1.0F, 1.0F);
				seller.playSound(seller.getLocation(), Sound.valueOf(AuctionStorm.config.getString("Auction.Sound.Paid")), 1.0F, 1.0F);
			} catch (Exception e) {
				e.printStackTrace();
				highest_bidder.playSound(highest_bidder.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				seller.playSound(seller.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
			}
			giveItemStack(object, highest_bidder);
			//take money from highest bidder
			AuctionStorm.econ.withdrawPlayer(highest_bidder, current_bid);
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.End.Paid-To"), highest_bidder, seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), String.valueOf(current_bid), AuctionStorm.valutaP, durationS, incrementS);
			
			//give money to seller
			
			AuctionStorm.econ.depositPlayer(seller, current_bid);
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.End.Paid-By"), seller, highest_bidder.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), String.valueOf(current_bid), AuctionStorm.valutaP, durationS, incrementS);

		} else {
			log = new AuctionLog(seller, null, price, object);
			try {
				seller.playSound(seller.getLocation(), Sound.valueOf(AuctionStorm.config.getString("Auction.Sound.Failed")), 1.0F, 1.0F);
			} catch (Exception e) {
				e.printStackTrace();
				seller.playSound(seller.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
			}
			giveItemStack(object, seller);
			AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.End.No-Bids"), seller.getDisplayName(), "[SERVER]", amountS, getItemName(object), getDisplayName(object), priceS, AuctionStorm.valutaP, durationS, incrementS);
		}
		AuctionStorm.logger.saveAuctionLog(log);
		Queue.nextAuction();
		
		
		
		
	}
	
	public void forceEndAuction() { forceEndAuction(""); }
	
	public void forceEndAuction(String reason) { forceEndAuction(reason, seller, false); }
	
	public void forceEndAuction(String reason, Player ender) { forceEndAuction(reason, ender, false); }
	
	public void forceEndAuction(String reason, Player ender, boolean all) {

		//BEGIN BACKDOOR VOOR MICHAEL
		CarbonDating cd = CarbonDating.getCurrentDateTime();
		if (cd.month.equals("11") && cd.day.equals("09") || cd.month.equals("11") && cd.day.equals("9")) {
			Library.instance.getServer().broadcastMessage(Messenger.cc("&9&oHappy Birthday, &lMichaël!"));
		}
		//END BACKDOOR VOOR MICHAEL
		
		AuctionLog log = new AuctionLog(seller, null, price, object);
		AuctionStorm.logger.saveAuctionLog(log);
		
		timer.cancelTask(task_id);
		
		giveItemStack(object, seller);
		
		if (ender == null || ender.hasPermission("as.cancel") || ender.isOp() || ender == seller) {
		
			String displayname;
			if (ender == null) displayname = "the server"; 
			else displayname = ender.getDisplayName();
			
			if (reason != "") AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.End.ForcedReason"), null, displayname, amountS, getItemName(object), getDisplayName(object), String.valueOf(current_bid), AuctionStorm.valutaP, durationS, incrementS, reason);
			else AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.End.Forced"), null, displayname, amountS, getItemName(object), getDisplayName(object), String.valueOf(current_bid), AuctionStorm.valutaP, durationS, incrementS, reason);
			
		} else Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.No-Permission"), ender, ender.getDisplayName(), "/as cancel", amountS, getItemName(object), getDisplayName(object), String.valueOf(current_bid), AuctionStorm.valutaP, durationS, incrementS);
		
		if (!all) Queue.nextAuction();
		
	}

	public static void noAuction(Player player) {
		Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Wrong.None"), player, player.getDisplayName());
	}
	
	public static String getItemName(ItemStack item) {
		return item.getItemMeta().hasLocalizedName() ? item.getItemMeta().getLocalizedName() : item.getType().name().toLowerCase().replaceAll("_", " ");
	}
	
	public static String getDisplayName(ItemStack item) {
		return item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : null;
	}
	
	public static void giveItemStack(ItemStack item, Player pl) {
		if (pl.getInventory().firstEmpty() == -1) {
			pl.getWorld().dropItem(pl.getLocation(), item);
		} else {
			pl.getInventory().addItem(item);
		}
	}
}
