package com.alchemi.as;

import java.util.Map.Entry;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.scheduler.BukkitScheduler;

import com.alchemi.al.Library;
import com.alchemi.al.configurations.Messenger;
import com.alchemi.al.objects.handling.CarbonDating;
import com.alchemi.as.listeners.commands.CommandPlayer;
import com.alchemi.as.objects.AuctionLog;
import com.alchemi.as.objects.AuctionMessenger;
import com.alchemi.as.objects.AuctionTimer;
import com.alchemi.as.objects.Config;
import com.alchemi.as.objects.RomanNumber;
import com.alchemi.as.objects.Config.AUCTION;

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
	
	private AuctionLog log = null;
	
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
		
		object = seller.getInventory().getItemInMainHand();
		
		//check values
		if (main.banned_items.contains(object.getType())) {
			main.getInstance().getMessenger().sendMessage(Config.MESSAGES.AUCTION_WRONG_BANNED.value()
					.replace("$player$", seller.getDisplayName())
					.replace("$amount$", amountS)
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", priceS)
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
					.replace("$duration$", durationS)
					.replace("$incr$", incrementS), seller);
			return;
		}
		if (!(price >= Config.AUCTION.MINIMUM_VALUES_PRICE.asInt() && price <= Config.AUCTION.MAXIMUM_VALUES_PRICE.asInt())) {
			if (Config.AUCTION.MAXIMUM_VALUES_PRICE.asInt() != -1) {
				main.getInstance().getMessenger().sendMessage(Config.MESSAGES.AUCTION_WRONG_PRICE.value()
						.replace("$player$", seller.getDisplayName())
						.replace("$amount$", Config.AUCTION.MINIMUM_VALUES_PRICE.asString())
						.replace("$item$", Auction.getItemName(object))
						.replace("$name$", Auction.getDisplayName(object))
						.replace("$price$", Config.AUCTION.MAXIMUM_VALUES_PRICE.asString())
						.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
						.replace("$duration$", Config.AUCTION.MAXIMUM_VALUES_DURATION.asString())
						.replace("$incr$", Config.AUCTION.MAXIMUM_VALUES_INCREMENT.asString()), seller);
				return;
			} else if (price < Config.AUCTION.MINIMUM_VALUES_PRICE.asInt()) {
				main.getInstance().getMessenger().sendMessage(Config.MESSAGES.AUCTION_WRONG_PRICEINF.value()
						.replace("$player$", seller.getDisplayName())
						.replace("$amount$", Config.AUCTION.MINIMUM_VALUES_PRICE.asString())
						.replace("$item$", Auction.getItemName(object))
						.replace("$name$", Auction.getDisplayName(object))
						.replace("$price$", Config.AUCTION.MAXIMUM_VALUES_PRICE.asString())
						.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
						.replace("$duration$", Config.AUCTION.MAXIMUM_VALUES_DURATION.asString())
						.replace("$incr$", Config.AUCTION.MAXIMUM_VALUES_INCREMENT.asString()),seller);
				return;
			}
		}
		if (!(duration >= Config.AUCTION.MINIMUM_VALUES_DURATION.asInt() && duration <= Config.AUCTION.MAXIMUM_VALUES_DURATION.asInt())) {
			if (Config.AUCTION.MAXIMUM_VALUES_DURATION.asInt() != -1) {
				main.getInstance().getMessenger().sendMessage(Config.MESSAGES.AUCTION_WRONG_DURATION.value()
						.replace("$player$", seller.getDisplayName())
						.replace("$amount$", Config.AUCTION.MINIMUM_VALUES_DURATION.asString())
						.replace("$item$", Auction.getItemName(object))
						.replace("$name$", Auction.getDisplayName(object))
						.replace("$price$", Config.AUCTION.MAXIMUM_VALUES_PRICE.asString())
						.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
						.replace("$duration$", Config.AUCTION.MAXIMUM_VALUES_DURATION.asString())
						.replace("$incr$", Config.AUCTION.MAXIMUM_VALUES_INCREMENT.asString()),seller);
				return;
			} else if (price < Config.AUCTION.MINIMUM_VALUES_DURATION.asInt()) {
				main.getInstance().getMessenger().sendMessage(Config.MESSAGES.AUCTION_WRONG_DURATIONINF.value()
						.replace("$player$", seller.getDisplayName())
						.replace("$amount$", Config.AUCTION.MINIMUM_VALUES_DURATION.asString())
						.replace("$item$", Auction.getItemName(object))
						.replace("$name$", Auction.getDisplayName(object))
						.replace("$price$", Config.AUCTION.MAXIMUM_VALUES_PRICE.asString())
						.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
						.replace("$duration$", Config.AUCTION.MAXIMUM_VALUES_DURATION.asString())
						.replace("$incr$", Config.AUCTION.MAXIMUM_VALUES_INCREMENT.asString()),seller);
				return;
			}
		}
		if (!(increment >= Config.AUCTION.MINIMUM_VALUES_INCREMENT.asInt() && increment <= Config.AUCTION.MAXIMUM_VALUES_INCREMENT.asInt())) {
			if (Config.AUCTION.MAXIMUM_VALUES_INCREMENT.asInt() != -1) {
				main.getInstance().getMessenger().sendMessage(Config.MESSAGES.AUCTION_WRONG_INCREMENT.value()
						.replace("$player$", seller.getDisplayName())
						.replace("$amount$", Config.AUCTION.MINIMUM_VALUES_AMOUNT.asString())
						.replace("$item$", Auction.getItemName(object))
						.replace("$name$", Auction.getDisplayName(object))
						.replace("$price$", Config.AUCTION.MAXIMUM_VALUES_PRICE.asString())
						.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
						.replace("$duration$", Config.AUCTION.MAXIMUM_VALUES_DURATION.asString())
						.replace("$incr$", Config.AUCTION.MAXIMUM_VALUES_INCREMENT.asString()),seller);
				return;
			} else if (price < Config.AUCTION.MINIMUM_VALUES_INCREMENT.asInt()) {
				main.getInstance().getMessenger().sendMessage(Config.MESSAGES.AUCTION_WRONG_INCREMENTINF.value()
						.replace("$player$", seller.getDisplayName())
						.replace("$amount$", Config.AUCTION.MINIMUM_VALUES_INCREMENT.asString())
						.replace("$item$", Auction.getItemName(object))
						.replace("$name$", Auction.getDisplayName(object))
						.replace("$price$", Config.AUCTION.MAXIMUM_VALUES_PRICE.asString())
						.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
						.replace("$duration$", Config.AUCTION.MAXIMUM_VALUES_DURATION.asString())
						.replace("$incr$", Config.AUCTION.MAXIMUM_VALUES_INCREMENT.asString()),seller);
				return;
			}
		}
		if (amount < Config.AUCTION.MINIMUM_VALUES_AMOUNT.asInt()) {
			main.getInstance().getMessenger().sendMessage(Config.MESSAGES.AUCTION_WRONG_AMOUNT.value()
					.replace("$player$", seller.getDisplayName())
					.replace("$amount$", Config.AUCTION.MINIMUM_VALUES_AMOUNT.asString())
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", Config.AUCTION.MAXIMUM_VALUES_PRICE.asString())
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
					.replace("$duration$", Config.AUCTION.MAXIMUM_VALUES_DURATION.asString())
					.replace("$incr$", Config.AUCTION.MAXIMUM_VALUES_INCREMENT.asString()),seller);
			return;
		}
		
		
		this.timer = main.getInstance().getServer().getScheduler();
		int handAmount = object.getAmount();
		
		if (handAmount > amount) {
			object.setAmount(handAmount-amount);
			seller.getInventory().setItemInMainHand(object);
			object.setAmount(amount);
		} else if (CommandPlayer.scanInventory(seller.getInventory(), object) >= amount){
			
			object = getFromInventory(object, amount);
			
		} else {
			seller.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
		}
		
		
		if (getItemName(object).equalsIgnoreCase("air")) {
			main.getInstance().getMessenger().sendMessage(Config.MESSAGES.AUCTION_WRONG_ITEM.value()
					.replace("$player$", seller.getDisplayName())
					.replace("$amount$", amountS)
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", priceS)
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
					.replace("$duration$", durationS)
					.replace("$incr$", incrementS),seller);
			
			return;
						
		} else if (seller.getGameMode().equals(GameMode.CREATIVE) && !main.hasPermission(seller, "as.creative") && !Config.AUCTION.ALLOWCREATIVE.asBoolean()) {
			
			main.getInstance().getMessenger().sendMessage(Config.MESSAGES.AUCTION_WRONG_CREATIVE.value()
					.replace("$player$", seller.getDisplayName())
					.replace("$amount$", amountS)
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", priceS)
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
					.replace("$duration$", durationS)
					.replace("$incr$", incrementS),seller);
			giveItemStack(object, seller);
			return;
			
		} 
		if (object.getAmount() < this.amount) {
			
			main.getInstance().getMessenger().sendMessage(Config.MESSAGES.AUCTION_WRONG_ENOUGH.value()
					.replace("$player$", seller.getDisplayName())
					.replace("$amount$", amountS)
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", priceS)
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
					.replace("$duration$", durationS)
					.replace("$incr$", incrementS),seller);
			giveItemStack(object, seller);
			return;
			
		}
		if (Queue.getQueueLength() >= 1) main.getInstance().getMessenger().sendMessage(Config.MESSAGES.AUCTION_QUEUED.value()
				.replace("$player$", seller.getDisplayName())
				.replace("$amount$", amountS)
				.replace("$item$", Auction.getItemName(object))
				.replace("$name$", Auction.getDisplayName(object))
				.replace("$price$", priceS)
				.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
				.replace("$duration$", durationS)
				.replace("$incr$", incrementS),seller);
		
		Queue.addAuction(this);
		
	}
	
	private ItemStack getFromInventory(ItemStack object2, int amount2) {
		int size = amount2 - seller.getInventory().getItemInMainHand().getAmount();
		int invsize = CommandPlayer.scanInventory(seller.getInventory(), object2);
		object2.setAmount(amount2);
		seller.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
		for (ItemStack s : seller.getInventory()) {
			
			if (size <= 0) {
				
				ItemStack ret = object2.clone();
				int diff = invsize - amount2;
				while (diff > 64) {
					diff -= 64;
				}
				ret.setAmount(diff);
				for (ItemStack s2 : seller.getInventory()) {
					if (s2 != null && s2.isSimilar(ret)) {
						ret.setAmount(ret.getAmount() - s2.getAmount());
					}
				}
				giveItemStack(ret, seller);
				break;
			}
			
			if (s == null) continue;
			
			if (s.isSimilar(object2)) {
				
				
				size -= s.getAmount();
				int slot = seller.getInventory().first(s);
				s.setAmount(0);
				seller.getInventory().setItem(slot, s);
				
			}
		}
		if (size > 0) object2.setAmount(amount2 - size);
		return object2;
	}

	public int getDuration() {
		return duration;
	}
	
	public int getPrice() {
		return price;
	}
	
	public ItemStack getObject() {
		return object;
	}
	
	public Player getSeller() {
		return seller;
	}
	
	public int getCurrent_bid() {
		return current_bid;
	}
	
	public int getIncrement() {
		return increment;
	}
	
	
	public void startAuction() {
		//20 ticks/second * 60 seconds/minute = 1200 ticks/minute
		atimer = new AuctionTimer(duration);
		task_id = timer.runTaskTimer(main.getInstance(), atimer, 0, 20).getTaskId();
		
		if (Config.AUCTION.LOGAUCTIONS.asBoolean()) {
			log = new AuctionLog(seller, null, price, object);
			main.logger.saveAuctionLog(log);
		}
		
		if (getDisplayName(object) != "") {
			
			if (!Config.AUCTION.HOVERITEM.asBoolean()) {
				main.getInstance().getMessenger().broadcast(Config.MESSAGES.AUCTION_STARTNAMED.value()
						.replace("$player$", seller.getDisplayName())
						.replace("$amount$", amountS)
						.replace("$item$", Auction.getItemName(object))
						.replace("$name$", Auction.getDisplayName(object))
						.replace("$price$", priceS)
						.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
						.replace("$duration$", durationS)
						.replace("$incr$", incrementS));
			} else {
				if (!Config.AUCTION.HOVERITEMMINECRAFTTOOLTIP.asBoolean()) {
					main.getInstance().getMessenger().broadcastHover(Config.MESSAGES.AUCTION_STARTNAMED.value()
							.replace("$player$", seller.getDisplayName())
							.replace("$amount$", amountS)
							.replace("$item$", Auction.getItemName(object))
							.replace("$name$", Auction.getDisplayName(object))
							.replace("$price$", priceS)
							.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
							.replace("$duration$", durationS)
							.replace("$incr$", incrementS), getInfo(false));
				} else {
					((AuctionMessenger)main.getInstance().getMessenger()).broadcastITEM(Config.MESSAGES.AUCTION_STARTNAMED.value()
							.replace("$player$", seller.getDisplayName())
							.replace("$amount$", amountS)
							.replace("$item$", Auction.getItemName(object))
							.replace("$name$", Auction.getDisplayName(object))
							.replace("$price$", priceS)
							.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
							.replace("$duration$", durationS)
							.replace("$incr$", incrementS), object);
				}
			}
		} else {
			if (!Config.AUCTION.HOVERITEM.asBoolean()) {
				main.getInstance().getMessenger().broadcast(Config.MESSAGES.AUCTION_START.value()
						.replace("$player$", seller.getDisplayName())
						.replace("$amount$", amountS)
						.replace("$item$", Auction.getItemName(object))
						.replace("$price$", priceS)
						.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
						.replace("$duration$", durationS)
						.replace("$incr$", incrementS));
			} else {
				if (!Config.AUCTION.HOVERITEMMINECRAFTTOOLTIP.asBoolean()) {
					main.getInstance().getMessenger().broadcastHover(Config.MESSAGES.AUCTION_START.value()
							.replace("$player$", seller.getDisplayName())
							.replace("$amount$", amountS)
							.replace("$item$", Auction.getItemName(object))
							.replace("$price$", priceS)
							.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
							.replace("$duration$", durationS)
							.replace("$incr$", incrementS), getInfo(false));
				} else {
					((AuctionMessenger)main.getInstance().getMessenger()).broadcastITEM(Config.MESSAGES.AUCTION_START.value()
							.replace("$player$", seller.getDisplayName())
							.replace("$amount$", amountS)
							.replace("$item$", Auction.getItemName(object))
							.replace("$price$", priceS)
							.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
							.replace("$duration$", durationS)
							.replace("$incr$", incrementS), object);
				}
			}
		}
		
		if (!Config.AUCTION.HOVERITEM.asBoolean()) {
			main.getInstance().getMessenger().broadcast(Config.MESSAGES.AUCTION_INFO_GET.value());
		} else {
			if (!Config.AUCTION.HOVERITEMMINECRAFTTOOLTIP.asBoolean()) {
				main.getInstance().getMessenger().broadcastHover(Config.MESSAGES.AUCTION_INFO_GET.value()
						.replace("$player$", seller.getDisplayName())
						.replace("$amount$", amountS)
						.replace("$item$", Auction.getItemName(object))
						.replace("$name$", Auction.getDisplayName(object))
						.replace("$price$", priceS)
						.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
						.replace("$duration$", durationS)
						.replace("$incr$", incrementS), getInfo(false));
			} else {
				((AuctionMessenger)main.getInstance().getMessenger()).broadcastITEM(Config.MESSAGES.AUCTION_INFO_GET.value()
						.replace("$player$", seller.getDisplayName())
						.replace("$amount$", amountS)
						.replace("$item$", Auction.getItemName(object))
						.replace("$name$", Auction.getDisplayName(object))
						.replace("$price$", priceS)
						.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
						.replace("$duration$", durationS)
						.replace("$incr$", incrementS), object);
			}
		}
		
	}
	public void bid(int bid, Player bidder) {bid(bid, bidder, false);}
	
	public void bid(Player bidder) {
		
		if (current_bid > 0) bid(current_bid+increment, bidder, false);
		else bid(price, bidder, false);
		
	}
	
	public void bid(int bid, Player bidder, boolean secret) {
		if (bidder.equals(seller)) {
			main.getInstance().getMessenger().sendMessage(Config.MESSAGES.AUCTION_BID_OWN_AUCTION.value()
					.replace("$player$", seller.getDisplayName())
					.replace("$amount$", amountS)
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", priceS)
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
					.replace("$duration$", durationS)
					.replace("$incr$", incrementS),seller);
			
			return;
		} else if (!main.getInstance().econ.has(bidder, bid + Math.abs(AUCTION.BIDTAX.asInt()))) {
			main.getInstance().getMessenger().sendMessage(Config.MESSAGES.AUCTION_BID_NO_MONEY.value()
					.replace("$player$", bidder.getDisplayName())
					.replace("$amount$", amountS)
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", priceS)
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
					.replace("$duration$", durationS)
					.replace("$incr$", incrementS), bidder);
			return;
		} else if (current_bid+increment > bid && current_bid > 0) {
			main.getInstance().getMessenger().sendMessage(Config.MESSAGES.AUCTION_BID_LOW.value()
					.replace("$player$", bidder.getDisplayName())
					.replace("$amount$", amountS)
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", String.valueOf(current_bid + increment))
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
					.replace("$duration$", durationS)
					.replace("$incr$", incrementS), bidder);
			return;
		} else if (price > bid) {
			main.getInstance().getMessenger().sendMessage(Config.MESSAGES.AUCTION_BID_LOW.value()
					.replace("$player$", bidder.getDisplayName())
					.replace("$amount$", amountS)
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", priceS)
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
					.replace("$duration$", durationS)
					.replace("$incr$", incrementS), bidder);
			
			return;
		} else if (bid > Config.AUCTION.MAXIMUM_VALUES_BID.asInt() && Config.AUCTION.MAXIMUM_VALUES_BID.asInt() != -1) {
			main.getInstance().getMessenger().sendMessage(Config.MESSAGES.AUCTION_BID_MAX.value()
					.replace("$player$", seller.getDisplayName())
					.replace("$amount$", Config.AUCTION.MAXIMUM_VALUES_BID.asString())
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", String.valueOf(current_bid))
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
					.replace("$duration$", durationS)
					.replace("$incr$", incrementS), bidder);
			return;
		} 
		
		if (secret) {
			
			secret_bid = bid;
			secret_bidder = bidder;
			
			return;
			
		}
		if (bid <= secret_bid) {
			main.getInstance().getMessenger().sendMessage(Config.MESSAGES.AUCTION_BID_OUTBID.value()
					.replace("$player$", secret_bidder.getDisplayName())
					.replace("$amount$", amountS)
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", priceS)
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
					.replace("$duration$", durationS)
					.replace("$incr$", incrementS), bidder);
			bid = secret_bid;
			bidder = secret_bidder;
			
			secret_bid = 0;
			secret_bidder = null;
		}
		
		current_bid = bid;
		highest_bidder = bidder;
		
		main.getInstance().getMessenger().broadcast(Config.MESSAGES.AUCTION_BID_BID.value()
				.replace("$player$", bidder.getDisplayName())
				.replace("$amount$", amountS)
				.replace("$item$", Auction.getItemName(object))
				.replace("$name$", Auction.getDisplayName(object))
				.replace("$price$", String.valueOf(current_bid))
				.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
				.replace("$duration$", durationS)
				.replace("$incr$", incrementS));
		
		if (atimer.time < Config.AUCTION.ANTISNIPE_TRESHOLD.asInt()) {
			atimer.time += Config.AUCTION.ANTISNIPE_TIME_ADDED.asInt();
			main.getInstance().getMessenger().broadcast(Config.MESSAGES.AUCTION_TIME_ADDED.value()
					.replace("$player$", bidder.getDisplayName())
					.replace("$amount$", Config.AUCTION.ANTISNIPE_TIME_ADDED.asString())
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", priceS)
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
					.replace("$duration$", durationS)
					.replace("$incr$", incrementS));
			
		}
		if (AUCTION.BIDTAX.asInt() != 0) main.getInstance().econ.withdrawPlayer(bidder, Math.abs(AUCTION.BIDTAX.asInt()));
	}
	
	public String getInfo(boolean headers) {
		
		String msg = "";
		if (headers) msg = Config.MESSAGES.AUCTION_INFO_HEADER.value();
		
		if (getDisplayName(object) != "") {
			
			msg = msg + Config.MESSAGES.AUCTION_INFO_ITEMNAMED.value()
				.replace("$player$", seller.getDisplayName())
				.replace("$amount$", amountS)
				.replace("$item$", Auction.getItemName(object))
				.replace("$name$", Auction.getDisplayName(object))
				.replace("$price$", priceS)
				.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
				.replace("$incr$", incrementS);
			if (atimer != null) msg = msg.replace("$duration$", String.valueOf(atimer.time));
			
		}
		else {
			
			msg = msg + Config.MESSAGES.AUCTION_INFO_ITEM.value()
				.replace("$player$", seller.getDisplayName())
				.replace("$amount$", amountS)
				.replace("$item$", Auction.getItemName(object))
				.replace("$name$", Auction.getDisplayName(object))
				.replace("$price$", priceS)
				.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
				.replace("$incr$", incrementS);
			if (atimer != null) msg = msg.replace("$duration$", String.valueOf(atimer.time));
			
		}
		if(Config.AUCTION.DISPLAYLORE.asBoolean() && object.hasItemMeta() && object.getItemMeta().hasLore()) {
			msg = msg + Config.MESSAGES.AUCTION_INFO_LORE.value()
			.replace("$player$", seller.getDisplayName())
			.replace("$amount$", amountS)
			.replace("$item$", Auction.getItemName(object))
			.replace("$name$", Auction.getDisplayName(object))
			.replace("$price$", priceS)
			.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
			.replace("$incr$", incrementS);
		
			if (atimer != null) msg = msg.replace("$duration$", String.valueOf(atimer.time));
			
			for (String s : object.getItemMeta().getLore()) {
				msg += "\n&5&o" + s;
			}
			
		}
		
		msg = msg + Config.MESSAGES.AUCTION_INFO_AMOUNT.value()
		.replace("$player$", seller.getDisplayName())
		.replace("$amount$", amountS)
		.replace("$item$", Auction.getItemName(object))
		.replace("$name$", Auction.getDisplayName(object))
		.replace("$price$", priceS)
		.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
		.replace("$incr$", incrementS);
	
		if (atimer != null) msg = msg.replace("$duration$", String.valueOf(atimer.time));
		
		//show durability if present
		if (object.getItemMeta() instanceof Damageable && ((Damageable)object.getItemMeta()).hasDamage()) {
			msg = msg + Config.MESSAGES.AUCTION_INFO_DURABILITY.value()
					.replace("$player$", seller.getDisplayName())
					.replace("$amount$", String.valueOf(object.getType().getMaxDurability() - ((Damageable)object.getItemMeta()).getDamage()))
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", priceS)
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
					.replace("$incr$", incrementS)
					.replace("$durability$", String.valueOf(object.getType().getMaxDurability()));
			if (atimer != null) msg = msg.replace("$duration$", String.valueOf(atimer.time));
					
		}
		
		//show enchantments if present
		if (!object.getEnchantments().isEmpty()) {
			msg = msg + Config.MESSAGES.AUCTION_INFO_ENCHANTMENTHEADER.value()
			.replace("$player$", seller.getDisplayName())
			.replace("$amount$", amountS)
			.replace("$item$", Auction.getItemName(object))
			.replace("$name$", Auction.getDisplayName(object))
			.replace("$price$", priceS)
			.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
			.replace("$incr$", incrementS);
			
			if (atimer != null) msg = msg.replace("$duration$", String.valueOf(atimer.time));
			
			for (Entry<Enchantment, Integer> ench : object.getEnchantments().entrySet()) {
				msg = msg + Config.MESSAGES.AUCTION_INFO_ENCHANTMENT.value()
						.replace("$player$", seller.getDisplayName())
						.replace("$amount$", RomanNumber.toRoman(ench.getValue()))
						.replace("$item$", Auction.getItemName(object))
						.replace("$name$", ench.getKey().getKey().getKey().replaceAll("_", " "))
						.replace("$price$", priceS)
						.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
						.replace("$incr$", incrementS);
				
				if (atimer != null) msg = msg.replace("$duration$", String.valueOf(atimer.time));
			}
		}
		
		msg = msg + Config.MESSAGES.AUCTION_INFO_STARTINGBID.value()
		.replace("$player$", seller.getDisplayName())
		.replace("$amount$", amountS)
		.replace("$item$", Auction.getItemName(object))
		.replace("$name$", Auction.getDisplayName(object))
		.replace("$price$", priceS)
		.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
		.replace("$incr$", incrementS);
		
		if (atimer != null) msg = msg.replace("$duration$", String.valueOf(atimer.time));
		
		if (highest_bidder != null && headers) {
			msg = msg + Config.MESSAGES.AUCTION_INFO_BIDDER.value()
				.replace("$player$", highest_bidder.getDisplayName())
				.replace("$amount$", amountS)
				.replace("$item$", Auction.getItemName(object))
				.replace("$name$", Auction.getDisplayName(object))
				.replace("$price$", String.valueOf(current_bid))
				.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
				.replace("$incr$", incrementS);
		
			if (atimer != null) msg = msg.replace("$duration$", String.valueOf(atimer.time));
		}
		
		if (current_bid > 0 && headers) { 
			msg = msg + Config.MESSAGES.AUCTION_INFO_CURRENTBID.value()
				.replace("$player$", highest_bidder.getDisplayName())
				.replace("$amount$", amountS)
				.replace("$item$", Auction.getItemName(object))
				.replace("$name$", Auction.getDisplayName(object))
				.replace("$price$", String.valueOf(current_bid))
				.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
				.replace("$incr$", incrementS);
				
				if (atimer != null) msg = msg.replace("$duration$", String.valueOf(atimer.time));
		}
		if (headers) {
			msg = msg + Config.MESSAGES.AUCTION_INFO_TIME.value()
			.replace("$player$", seller.getDisplayName())
			.replace("$amount$", amountS)
			.replace("$item$", Auction.getItemName(object))
			.replace("$name$", Auction.getDisplayName(object))
			.replace("$price$", priceS)
			.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
			.replace("$incr$", incrementS);
			
			if (atimer != null) msg = msg.replace("$duration$", String.valueOf(atimer.time));
		
			msg = msg + Config.MESSAGES.AUCTION_INFO_FOOTER.value();
		}
		
		return msg;
	}
	
	public void endAuction() {		
		if (highest_bidder != null) {
			if (Config.AUCTION.LOGAUCTIONS.asBoolean()) {
				log.setBuyer(highest_bidder);
				log.setPrice(current_bid);
				main.logger.updateAuctionLog(log);
			}
			main.getInstance().getMessenger().broadcast(Config.MESSAGES.AUCTION_END_END.value()
					.replace("$player$", highest_bidder.getDisplayName())
					.replace("$amount$", amountS)
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", String.valueOf(current_bid))
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
					.replace("$duration$", durationS)
					.replace("$incr$", incrementS));
			
			if (Config.AUCTION.SOUND_PLAY.asBoolean()) {
				try {
					highest_bidder.playSound(highest_bidder.getLocation(), Config.AUCTION.SOUND_PAY.asSound(), 1.0F, 1.0F);
					seller.playSound(seller.getLocation(), Config.AUCTION.SOUND_PAID.asSound(), 1.0F, 1.0F);
				} catch (Exception e) {
					e.printStackTrace();
					highest_bidder.playSound(highest_bidder.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
					seller.playSound(seller.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				}
			}
			giveItemStack(object, highest_bidder);

			//take money from highest bidder
			main.getInstance().econ.withdrawPlayer(highest_bidder, current_bid);
			main.getInstance().getMessenger().sendMessage(Config.MESSAGES.AUCTION_END_PAID_TO.value()
					.replace("$player$", seller.getDisplayName())
					.replace("$amount$", amountS)
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", String.valueOf(current_bid))
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
					.replace("$duration$", durationS)
					.replace("$incr$", incrementS), highest_bidder);
			
			//give money to seller
			main.getInstance().econ.depositPlayer(seller, current_bid * (1-AUCTION.SELLTAX.asDouble()));
			main.getInstance().getMessenger().sendMessage(Config.MESSAGES.AUCTION_END_PAID_BY.value()
					.replace("$player$", highest_bidder.getDisplayName())
					.replace("$amount$", amountS)
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", String.valueOf(current_bid))
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
					.replace("$duration$", durationS)
					.replace("$incr$", incrementS), seller);

		} else {
			

			if (Config.AUCTION.SOUND_PLAY.asBoolean()) {
				try {
					seller.playSound(seller.getLocation(), Config.AUCTION.SOUND_FAILED.asSound(), 1.0F, 1.0F);
				} catch (Exception e) {
					e.printStackTrace();
					seller.playSound(seller.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				}
			}
			
			giveItemStack(object, seller);
			main.getInstance().getMessenger().broadcast(Config.MESSAGES.AUCTION_END_NO_BIDS.value()
					.replace("$player$", seller.getDisplayName())
					.replace("$amount$", amountS)
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", priceS)
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
					.replace("$duration$", String.valueOf(atimer.time))
					.replace("$incr$", incrementS));
		}
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
		
		timer.cancelTask(task_id);
		
		giveItemStack(object, seller);
		
		if ((ender == null || main.hasPermission(ender, "as.cancel") || ender == seller) && Queue.current_auction.equals(this)) {
		
			String displayname;
			if (ender == null) displayname = "the server"; 
			else displayname = ender.getDisplayName();
			
			if (reason != "") main.getInstance().getMessenger().broadcast(Config.MESSAGES.AUCTION_END_FORCEDREASON.value()
					.replace("$sender$", displayname)
					.replace("$amount$", amountS)
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", priceS)
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
					.replace("$incr$", incrementS)
					.replace("$reason$", reason));
			else main.getInstance().getMessenger().broadcast(Config.MESSAGES.AUCTION_END_FORCED.value()
					.replace("$sender$", displayname)
					.replace("$amount$", amountS)
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", priceS)
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
					.replace("$incr$", incrementS));
			
		} else if (ender == null) { 
			
			String msg = reason == "" ? Config.MESSAGES.AUCTION_END_FORCEDSELLER.value() : Config.MESSAGES.AUCTION_END_FORCEDREASONSELLER.value();
			
			main.getInstance().getMessenger().sendMessage(msg
					.replace("$id$", String.valueOf(Queue.getQueue().indexOf(this)))
					.replace("$player$", seller.getDisplayName())
					.replace("$sender$", "the server")
					.replace("$amount$", amountS)
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", priceS)
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
					.replace("$reason$", reason), seller);
			
		} else if (main.hasPermission(ender, "as.cancel") || ender == seller) {
			
			main.getInstance().getMessenger().sendMessage(Config.MESSAGES.AUCTION_END_CANCELLED.value()
				.replace("$id$", String.valueOf(Queue.getQueue().indexOf(this)))
				.replace("$player$", seller.getDisplayName())
				.replace("$sender$", "/as cancel")
				.replace("$amount$", amountS)
				.replace("$item$", Auction.getItemName(object))
				.replace("$name$", Auction.getDisplayName(object))
				.replace("$price$", priceS)
				.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString()), ender);
			
			if (ender != seller) {
				
				String msg = reason == "" ? Config.MESSAGES.AUCTION_END_FORCEDSELLER.value() : Config.MESSAGES.AUCTION_END_FORCEDREASONSELLER.value();
				
				main.getInstance().getMessenger().sendMessage(msg
						.replace("$id$", String.valueOf(Queue.getQueue().indexOf(this)))
						.replace("$player$", seller.getDisplayName())
						.replace("$sender$", "/as cancel")
						.replace("$amount$", amountS)
						.replace("$item$", Auction.getItemName(object))
						.replace("$name$", Auction.getDisplayName(object))
						.replace("$price$", priceS)
						.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
						.replace("$reason$", reason), seller);
				
			}
			
		}
		
		else main.getInstance().getMessenger().sendMessage(Config.MESSAGES.COMMAND_NO_PERMISSION.value()
				.replace("$player$", ender.getDisplayName())
				.replace("$sender$", "/as cancel")
				.replace("$amount$", amountS)
				.replace("$item$", Auction.getItemName(object))
				.replace("$name$", Auction.getDisplayName(object))
				.replace("$price$", priceS)
				.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString())
				.replace("$incr$", incrementS)
				.replace("$reason$", reason), ender);
		
		if (!all && Queue.current_auction.equals(this)) Queue.nextAuction();
		
	}

	public static void noAuction(Player player) {
		main.getInstance().getMessenger().sendMessage(Config.MESSAGES.AUCTION_WRONG_NONE.value().replace("$sender$", player.getDisplayName()), player);
	}
	
	public static String getItemName(ItemStack item) {
		if (item == null) return "INVALID ITEM";
		else if (!item.hasItemMeta()) return item.getType().name().toLowerCase().replaceAll("_", " ");
		return item.getItemMeta().hasLocalizedName() ? item.getItemMeta().getLocalizedName() : item.getType().name().toLowerCase().replaceAll("_", " ");
	}
	
	public static String getDisplayName(ItemStack item) {
		if (item == null) throw new NullPointerException("ItemStack cannot be null!");
		return item.hasItemMeta() ? item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : "" : "";
	}
	
	public static void giveItemStack(ItemStack item, OfflinePlayer seller) {
		if (!seller.isOnline()) {
			main.gq.addPlayer(seller, item);
			return;
		}
		
		Library.giveItemStack(item, seller.getPlayer());
	}
}
