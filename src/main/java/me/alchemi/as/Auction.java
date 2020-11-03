package me.alchemi.as;

import java.io.File;
import java.util.Map.Entry;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.scheduler.BukkitScheduler;

import me.alchemi.al.util.ItemUtil;
import me.alchemi.as.listeners.commands.CommandPlayer;
import me.alchemi.as.objects.AuctionLog;
import me.alchemi.as.objects.AuctionMessenger;
import me.alchemi.as.objects.AuctionTimer;
import me.alchemi.as.objects.Config;
import me.alchemi.as.objects.Config.AuctionOptions;
import me.alchemi.as.objects.Messages;
import me.alchemi.as.objects.RomanNumber;
import me.alchemi.as.objects.placeholder.StringParser;

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
		
		object = seller.getInventory().getItemInMainHand();
				
		//check values
		if (Storm.banned_items.contains(object.getType())) {
			((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.AUCTION_WRONG_BANNED)
					.player(seller)
					.amount(amount)
					.item(object)
					.name(object)
					.price(price)
					.duration(duration)
					.incr(increment)
					.currencyPlural()
					.parse(seller), seller);
			return;
		}
		if (!(price >= Config.AuctionOptions.MINIMUM_VALUES_PRICE.asInt() && price <= Config.AuctionOptions.MAXIMUM_VALUES_PRICE.asInt())) {
			if (Config.AuctionOptions.MAXIMUM_VALUES_PRICE.asInt() != -1) {
				((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage( new StringParser(Messages.AUCTION_WRONG_PRICE)
						.player(seller)
						.amount(AuctionOptions.MINIMUM_VALUES_PRICE.asInt())
						.item(object)
						.name(object)
						.price(AuctionOptions.MAXIMUM_VALUES_PRICE.asInt())
						.duration(AuctionOptions.MAXIMUM_VALUES_DURATION.asInt())
						.incr(AuctionOptions.MAXIMUM_VALUES_INCREMENT.asInt())
						.currencyPlural()
						.parse(seller), seller);
				return;
			} else if (price < Config.AuctionOptions.MINIMUM_VALUES_PRICE.asInt()) {
				((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.AUCTION_WRONG_PRICEINF)
						.player(seller)
						.amount(AuctionOptions.MINIMUM_VALUES_PRICE.asInt())
						.item(object)
						.name(object)
						.price(AuctionOptions.MAXIMUM_VALUES_PRICE.asInt())
						.duration(AuctionOptions.MAXIMUM_VALUES_DURATION.asInt())
						.incr(AuctionOptions.MAXIMUM_VALUES_INCREMENT.asInt())
						.currencyPlural()
						.parse(seller), seller);
				return;
			}
		}
		if (!(duration >= Config.AuctionOptions.MINIMUM_VALUES_DURATION.asInt() && duration <= Config.AuctionOptions.MAXIMUM_VALUES_DURATION.asInt())) {
			if (Config.AuctionOptions.MAXIMUM_VALUES_DURATION.asInt() != -1) {
				((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.AUCTION_WRONG_DURATION)
						.player(seller)
						.amount(Config.AuctionOptions.MINIMUM_VALUES_DURATION.asInt())
						.item(object)
						.name(object)
						.price(Config.AuctionOptions.MAXIMUM_VALUES_PRICE.asInt())
						.currencyPlural()
						.duration(Config.AuctionOptions.MAXIMUM_VALUES_DURATION.asInt())
						.incr(Config.AuctionOptions.MAXIMUM_VALUES_INCREMENT.asInt())
						.parse(seller), seller);
				return;
			} else if (price < Config.AuctionOptions.MINIMUM_VALUES_DURATION.asInt()) {
				((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.AUCTION_WRONG_DURATIONINF)
						.player(seller)
						.amount(Config.AuctionOptions.MINIMUM_VALUES_DURATION.asInt())
						.item(object)
						.name(object)
						.price(Config.AuctionOptions.MAXIMUM_VALUES_PRICE.asInt())
						.currencyPlural()
						.duration(Config.AuctionOptions.MAXIMUM_VALUES_DURATION.asInt())
						.incr(Config.AuctionOptions.MAXIMUM_VALUES_INCREMENT.asInt())
						.parse(seller), seller);
				return;
			}
		}
		if (!(increment >= Config.AuctionOptions.MINIMUM_VALUES_INCREMENT.asInt() && increment <= Config.AuctionOptions.MAXIMUM_VALUES_INCREMENT.asInt())) {
			if (Config.AuctionOptions.MAXIMUM_VALUES_INCREMENT.asInt() != -1) {
				((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.AUCTION_WRONG_INCREMENT)
						.player(seller)
						.amount(Config.AuctionOptions.MINIMUM_VALUES_AMOUNT.asInt())
						.item(object)
						.name(object)
						.price(Config.AuctionOptions.MAXIMUM_VALUES_PRICE.asInt())
						.currencyPlural()
						.duration(Config.AuctionOptions.MAXIMUM_VALUES_DURATION.asInt())
						.incr(Config.AuctionOptions.MAXIMUM_VALUES_INCREMENT.asInt())
						.parse(seller), seller);
				return;
			} else if (price < Config.AuctionOptions.MINIMUM_VALUES_INCREMENT.asInt()) {
				((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.AUCTION_WRONG_INCREMENTINF)
						.player(seller)
						.amount(Config.AuctionOptions.MINIMUM_VALUES_INCREMENT.asInt())
						.item(object)
						.name(object)
						.price(Config.AuctionOptions.MAXIMUM_VALUES_PRICE.asInt())
						.currencyPlural()
						.duration(Config.AuctionOptions.MAXIMUM_VALUES_DURATION.asInt())
						.incr(Config.AuctionOptions.MAXIMUM_VALUES_INCREMENT.asInt())
						.parse(seller), seller);
				return;
			}
		}
		if (amount < Config.AuctionOptions.MINIMUM_VALUES_AMOUNT.asInt()) {
			((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.AUCTION_WRONG_AMOUNT)
					.player(seller)
					.amount(Config.AuctionOptions.MINIMUM_VALUES_AMOUNT.asInt())
					.item(object)
					.name(object)
					.price(Config.AuctionOptions.MAXIMUM_VALUES_PRICE.asInt())
					.currencyPlural()
					.duration(Config.AuctionOptions.MAXIMUM_VALUES_DURATION.asInt())
					.incr(Config.AuctionOptions.MAXIMUM_VALUES_INCREMENT.asInt())
					.parse(seller), seller);
			return;
		}
		
		
		this.timer = Storm.getInstance().getServer().getScheduler();
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
			((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.AUCTION_WRONG_ITEM)
					.player(seller)
					.amount(amount)
					.item(object)
					.name(object)
					.price(price)
					.currencyPlural()
					.duration(duration)
					.incr(increment)
					.parse(seller), seller);
			
			return;
						
		} else if (seller.getGameMode().equals(GameMode.CREATIVE) && !Storm.hasPermission(seller, "as.creative") && !Config.AuctionOptions.ALLOWCREATIVE.asBoolean()) {
			
			((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.AUCTION_WRONG_CREATIVE)
					.player(seller)
					.amount(amount)
					.item(object)
					.name(object)
					.price(price)
					.currencyPlural()
					.duration(duration)
					.incr(increment)
					.parse(seller), seller);
			giveItemStack(object, seller);
			return;
			
		} 
		if (object.getAmount() < this.amount) {
			
			((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.AUCTION_WRONG_ENOUGH)
					.player(seller)
					.amount(amount)
					.item(object)
					.name(object)
					.price(price)
					.currencyPlural()
					.duration(duration)
					.incr(increment)
					.parse(seller), seller);
			giveItemStack(object, seller);
			return;
			
		}
		if (Queue.getQueueLength() >= 1) ((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.AUCTION_QUEUED)
				.player(seller)
				.amount(amount)
				.item(object)
				.name(object)
				.price(price)
				.currencyPlural()
				.duration(duration)
				.incr(increment)
				.parse(seller), seller);
		
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
		task_id = timer.runTaskTimer(Storm.getInstance(), atimer, 0, 20).getTaskId();
		
		if (Config.AuctionOptions.LOGAUCTIONS.asBoolean()) {
			log = new AuctionLog(seller, null, price, object);
			Storm.logger.saveAuctionLog(log);
		}
		
		if (getDisplayName(object) != "") {
			
			if (!Config.AuctionOptions.HOVERITEM.asBoolean()) {
				((AuctionMessenger)Storm.getInstance().getMessenger()).broadcast(new StringParser(Messages.AUCTION_STARTNAMED)
						.player(seller)
						.amount(amount)
						.item(object)
						.name(object)
						.price(price)
						.currencyPlural()
						.duration(duration)
						.incr(increment)
						.parse(seller));
			} else {
				if (!Config.AuctionOptions.HOVERITEMMINECRAFTTOOLTIP.asBoolean()) {
					((AuctionMessenger)Storm.getInstance().getMessenger()).broadcastHover(new StringParser(Messages.AUCTION_STARTNAMED)
							.player(seller)
							.amount(amount)
							.item(object)
							.name(object)
							.price(price)
							.currencyPlural()
							.duration(duration)
							.incr(increment)
							.parse(seller)
							.create(), getInfo(false));
				} else {
					((AuctionMessenger)Storm.getInstance().getMessenger()).broadcastITEM(new StringParser(Messages.AUCTION_STARTNAMED)
							.player(seller)
							.amount(amount)
							.item(object)
							.name(object)
							.price(price)
							.currencyPlural()
							.duration(duration)
							.incr(increment)
							.parse(seller)
							.create(), object);
				}
			}
		} else {
			System.out.println(YamlConfiguration.loadConfiguration(new File(Storm.getInstance().getDataFolder(), "messages.yml")).getString(Messages.AUCTION_START.key()));
			if (!Config.AuctionOptions.HOVERITEM.asBoolean()) {
				((AuctionMessenger)Storm.getInstance().getMessenger()).broadcast(new StringParser(Messages.AUCTION_START)
						.player(seller)
						.amount(amount)
						.item(object)
						.price(price)
						.currencyPlural()
						.duration(duration)
						.incr(increment)
						.parse(seller));
			} else {
				if (!Config.AuctionOptions.HOVERITEMMINECRAFTTOOLTIP.asBoolean()) {
					((AuctionMessenger)Storm.getInstance().getMessenger()).broadcastHover(new StringParser(Messages.AUCTION_START)
							.player(seller)
							.amount(amount)
							.item(object)
							.price(price)
							.currencyPlural()
							.duration(duration)
							.incr(increment)
							.parse(seller)
							.create(), getInfo(false));
				} else {
					((AuctionMessenger)Storm.getInstance().getMessenger()).broadcastITEM(new StringParser(Messages.AUCTION_START)
							.player(seller)
							.amount(amount)
							.item(object)
							.price(price)
							.currencyPlural()
							.duration(duration)
							.incr(increment)
							.parse(seller)
							.create(), object);
				}
			}
		}
		
		if (!Config.AuctionOptions.HOVERITEM.asBoolean()) {
			((AuctionMessenger)Storm.getInstance().getMessenger()).broadcast(Messages.AUCTION_INFO_GET);
		} else {
			if (!Config.AuctionOptions.HOVERITEMMINECRAFTTOOLTIP.asBoolean()) {
				((AuctionMessenger)Storm.getInstance().getMessenger()).broadcastHover(new StringParser(Messages.AUCTION_INFO_GET)
						.player(seller)
						.amount(amount)
						.item(object)
						.name(object)
						.price(price)
						.currencyPlural()
						.duration(duration)
						.incr(increment)
						.parse(seller)
						.create(), getInfo(false));
			} else {
				((AuctionMessenger)Storm.getInstance().getMessenger()).broadcastITEM(new StringParser(Messages.AUCTION_INFO_GET)
						.player(seller)
						.amount(amount)
						.item(object)
						.name(object)
						.price(price)
						.currencyPlural()
						.duration(duration)
						.incr(increment)
						.parse(seller)
						.create(), object);
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
			((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.AUCTION_BID_OWN_AUCTION)
					.player(seller)
					.amount(amount)
					.item(object)
					.name(object)
					.price(price)
					.currencyPlural()
					.duration(duration)
					.incr(increment)
					.parse(seller), seller);
			
			return;
		} else if (!Storm.getInstance().econ.has(bidder, bid + Math.abs(AuctionOptions.BIDTAX.asInt()))) {
			((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.AUCTION_BID_NO_MONEY)
					.player(bidder)
					.amount(amount)
					.item(object)
					.name(object)
					.price(price)
					.currencyPlural()
					.duration(duration)
					.incr(increment)
					.parse(bidder), bidder);
			return;
		} else if (current_bid+increment > bid && current_bid > 0) {
			((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.AUCTION_BID_LOW)
					.player(bidder)
					.amount(amount)
					.item(object)
					.name(object)
					.price(current_bid + increment)
					.currencyPlural()
					.duration(duration)
					.incr(increment)
					.parse(bidder), bidder);
			return;
		} else if (price > bid) {
			((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.AUCTION_BID_LOW)
					.player(bidder)
					.amount(amount)
					.item(object)
					.name(object)
					.price(price)
					.currencyPlural()
					.duration(duration)
					.incr(increment)
					.parse(bidder), bidder);
			
			return;
		} else if (bid > Config.AuctionOptions.MAXIMUM_VALUES_BID.asInt() && Config.AuctionOptions.MAXIMUM_VALUES_BID.asInt() != -1) {
			((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.AUCTION_BID_MAX)
					.player(seller)
					.amount(Config.AuctionOptions.MAXIMUM_VALUES_BID.asInt())
					.item(object)
					.name(object)
					.price(current_bid)
					.currencyPlural()
					.duration(duration)
					.incr(increment)
					.parse(bidder), bidder);
			return;
		} 
		
		if (secret) {
			
			secret_bid = bid;
			secret_bidder = bidder;
			
			return;
			
		}
		if (bid <= secret_bid) {
			((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.AUCTION_BID_OUTBID)
					.player(secret_bidder)
					.amount(amount)
					.item(object)
					.name(object)
					.price(price)
					.currencyPlural()
					.duration(duration)
					.incr(increment)
					.parse(bidder), bidder);
			
			bid = secret_bid;
			bidder = secret_bidder;
			
			secret_bid = 0;
			secret_bidder = null;
		}
		
		current_bid = bid;
		highest_bidder = bidder;
		
		((AuctionMessenger)Storm.getInstance().getMessenger()).broadcast(new StringParser(Messages.AUCTION_BID_BID)
				.player(bidder)
				.amount(amount)
				.item(object)
				.name(object)
				.price(current_bid)
				.currencyPlural()
				.duration(duration)
				.incr(increment)
				.parse(bidder));
		
		if (atimer.time < Config.AuctionOptions.ANTISNIPE_TRESHOLD.asInt() && AuctionOptions.ANTISNIPE_TRESHOLD.asInt() > 0) {
			atimer.time += Config.AuctionOptions.ANTISNIPE_TIME_ADDED.asInt();
			((AuctionMessenger)Storm.getInstance().getMessenger()).broadcast(new StringParser(Messages.AUCTION_TIME_ADDED)
					.player(bidder)
					.amount(Config.AuctionOptions.ANTISNIPE_TIME_ADDED.asInt())
					.item(object)
					.name(object)
					.price(price)
					.currencyPlural()
					.duration(duration)
					.incr(increment)
					.parse(bidder));
			
		}
		if (AuctionOptions.BIDTAX.asInt() != 0) Storm.getInstance().econ.withdrawPlayer(bidder, Math.abs(AuctionOptions.BIDTAX.asInt()));
	}
	
	public String getInfo(boolean headers) {
		
		String msg = "";
		if (headers) msg = Messages.AUCTION_INFO_HEADER.value();
		
		if (getDisplayName(object) != "") {
			
			msg = msg + new StringParser(Messages.AUCTION_INFO_ITEMNAMED)
				.player(seller)
				.amount(amount)
				.item(object)
				.name(object)
				.price(price)
				.currencyPlural()
				.incr(increment)
				.create();
			if (atimer != null) msg = new StringParser(msg).duration(atimer.time).create();
			
		}
		else {
			
			msg = msg + new StringParser(Messages.AUCTION_INFO_ITEM)
				.player(seller)
				.amount(amount)
				.item(object)
				.name(object)
				.price(price)
				.currencyPlural()
				.incr(increment)
				.create();
			if (atimer != null) msg = new StringParser(msg).duration(atimer.time).create();
			
		}
		if(Config.AuctionOptions.DISPLAYLORE.asBoolean() && object.hasItemMeta() && object.getItemMeta().hasLore()) {
			msg = msg + new StringParser(Messages.AUCTION_INFO_LORE)
				.player(seller)
				.amount(amount)
				.item(object)
				.name(object)
				.price(price)
				.currencyPlural()
				.incr(increment)
				.create();
		
			if (atimer != null) msg = new StringParser(msg).duration(atimer.time).create();
			
			for (String s : object.getItemMeta().getLore()) {
				msg += "\n&5&o" + s;
			}
			
		}
		
		msg = msg + new StringParser(Messages.AUCTION_INFO_AMOUNT)
			.player(seller)
			.amount(amount)
			.item(object)
			.name(object)
			.price(price)
			.currencyPlural()
			.incr(increment)
			.create();
	
		if (atimer != null) msg = new StringParser(msg).duration(atimer.time).create();
		
		//show durability if present
		if (object.getItemMeta() instanceof Damageable && ((Damageable)object.getItemMeta()).hasDamage()) {
			msg = msg + new StringParser(Messages.AUCTION_INFO_DURABILITY)
					.player(seller)
					.amount(object.getType().getMaxDurability() - ((Damageable)object.getItemMeta()).getDamage())
					.item(object)
					.name(object)
					.price(price)
					.currencyPlural()
					.incr(increment)
					.durability(object.getType().getMaxDurability())
					.create();
			if (atimer != null) msg = new StringParser(msg).duration(atimer.time).create();
					
		}
		
		//show enchantments if present
		if (!object.getEnchantments().isEmpty()) {
			msg = msg + new StringParser(Messages.AUCTION_INFO_ENCHANTMENTHEADER)
					.player(seller)
					.amount(amount)
					.item(object)
					.name(object)
					.price(price)
					.currencyPlural()
					.incr(increment)
					.create();
			
			if (atimer != null) msg = new StringParser(msg).duration(atimer.time).create();
			
			for (Entry<Enchantment, Integer> ench : object.getEnchantments().entrySet()) {
				msg = msg + new StringParser(Messages.AUCTION_INFO_ENCHANTMENT)
						.player(seller)
						.amount(RomanNumber.toRoman(ench.getValue()))
						.item(object)
						.name(ench.getKey().getKey().getKey().replaceAll("_", " "))
						.price(price)
						.currencyPlural()
						.incr(increment)
						.create();
				
				if (atimer != null) msg = new StringParser(msg).duration(atimer.time).create();
			}
		}
		
		msg = msg + new StringParser(Messages.AUCTION_INFO_STARTINGBID)
			.player(seller)
			.amount(amount)
			.item(object)
			.name(object)
			.price(price)
			.currencyPlural()
			.incr(increment)
			.create();
		
		if (atimer != null) msg = new StringParser(msg).duration(atimer.time).create();
		
		if (highest_bidder != null && headers) {
			msg = msg + new StringParser(Messages.AUCTION_INFO_BIDDER)
				.player(highest_bidder)
				.amount(amount)
				.item(object)
				.name(object)
				.price(current_bid)
				.currencyPlural()
				.incr(increment)
				.create();
		
			if (atimer != null) msg = new StringParser(msg).duration(atimer.time).create();
		}
		
		if (current_bid > 0 && headers) { 
			msg = msg + new StringParser(Messages.AUCTION_INFO_CURRENTBID)
				.player(highest_bidder)
				.amount(amount)
				.item(object)
				.name(object)
				.price(current_bid)
				.currencyPlural()
				.incr(increment)
				.create();
				
				if (atimer != null) msg = new StringParser(msg).duration(atimer.time).create();
		}
		if (headers) {
			msg = msg + new StringParser(Messages.AUCTION_INFO_TIME)
				.player(seller)
				.amount(amount)
				.item(object)
				.name(object)
				.price(price)
				.currencyPlural()
				.incr(increment)
				.create();
			
			if (atimer != null) msg = new StringParser(msg).duration(atimer.time).create();
		
			msg = msg + new StringParser(Messages.AUCTION_INFO_FOOTER).parse(seller).create();
		}
		
		return msg;
	}
	
	public void endAuction() {		
		if (highest_bidder != null) {
			if (Storm.getInstance().econ.getBalance(highest_bidder) < current_bid) {
				
				giveItemStack(object, seller);
				((AuctionMessenger)Storm.getInstance().getMessenger()).broadcast(new StringParser(Messages.AUCTION_END_NO_MONEY)
						.player(highest_bidder)
						.amount(amount)
						.item(object)
						.name(object)
						.price(current_bid)
						.currencyPlural()
						.duration(duration)
						.incr(increment)
						.parse(highest_bidder));
				
				((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.AUCTION_END_NO_MONEY_BIDDER)
						.player(seller)
						.amount(amount)
						.item(object)
						.name(object)
						.price(current_bid)
						.currencyPlural()
						.duration(duration)
						.incr(increment)
						.parse(highest_bidder), highest_bidder);
				
				((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.AUCTION_END_NO_MONEY_SELLER)
						.player(highest_bidder)
						.amount(amount)
						.item(object)
						.name(object)
						.price(current_bid)
						.currencyPlural()
						.duration(duration)
						.incr(increment)
						.parse(highest_bidder), seller);
				
				if (Config.AuctionOptions.SOUND_PLAY.asBoolean()) {
					try {
						seller.playSound(seller.getLocation(), Config.AuctionOptions.SOUND_FAILED.asSound(), 1.0F, 1.0F);
						highest_bidder.playSound(seller.getLocation(), Config.AuctionOptions.SOUND_FAILED.asSound(), 1.0F, 1.0F);
					} catch (Exception e) {
						e.printStackTrace();
						seller.playSound(seller.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
						highest_bidder.playSound(seller.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
					}
				}
				Queue.nextAuction();
				return;
			}
			
			if (Config.AuctionOptions.LOGAUCTIONS.asBoolean()) {
				log.setBuyer(highest_bidder);
				log.setPrice(current_bid);
				Storm.logger.updateAuctionLog(log);
			}
			((AuctionMessenger)Storm.getInstance().getMessenger()).broadcast(new StringParser(Messages.AUCTION_END_END)
					.player(highest_bidder)
					.amount(amount)
					.item(object)
					.name(object)
					.price(current_bid)
					.currencyPlural()
					.duration(duration)
					.incr(increment)
					.parse(highest_bidder));
			
			if (Config.AuctionOptions.SOUND_PLAY.asBoolean()) {
				try {
					highest_bidder.playSound(highest_bidder.getLocation(), Config.AuctionOptions.SOUND_PAY.asSound(), 1.0F, 1.0F);
					seller.playSound(seller.getLocation(), Config.AuctionOptions.SOUND_PAID.asSound(), 1.0F, 1.0F);
				} catch (Exception e) {
					e.printStackTrace();
					highest_bidder.playSound(highest_bidder.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
					seller.playSound(seller.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				}
			}
			giveItemStack(object, highest_bidder);

			//take money from highest bidder
			Storm.getInstance().econ.withdrawPlayer(highest_bidder, current_bid);
			((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.AUCTION_END_PAID_TO)
					.player(seller)
					.amount(amount)
					.item(object)
					.name(object)
					.price(current_bid)
					.currencyPlural()
					.duration(duration)
					.incr(increment)
					.parse(seller), highest_bidder);
			
			//give money to seller
			Storm.getInstance().econ.depositPlayer(seller, current_bid * (1-AuctionOptions.SELLTAX.asDouble()));
			((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.AUCTION_END_PAID_BY)
					.player(highest_bidder)
					.amount(amount)
					.item(object)
					.name(object)
					.price(current_bid)
					.currencyPlural()
					.duration(duration)
					.incr(increment)
					.parse(highest_bidder), seller);

		} else {
			

			if (Config.AuctionOptions.SOUND_PLAY.asBoolean()) {
				try {
					seller.playSound(seller.getLocation(), Config.AuctionOptions.SOUND_FAILED.asSound(), 1.0F, 1.0F);
				} catch (Exception e) {
					e.printStackTrace();
					seller.playSound(seller.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				}
			}
			
			giveItemStack(object, seller);
			((AuctionMessenger)Storm.getInstance().getMessenger()).broadcast(new StringParser(Messages.AUCTION_END_NO_BIDS)
					.player(seller)
					.amount(amount)
					.item(object)
					.name(object)
					.price(price)
					.currencyPlural()
					.duration(atimer.time)
					.incr(increment)
					.parse(seller));
		}
		Queue.nextAuction();
	}
	
	public void forceEndAuction() { forceEndAuction(""); }
	
	public void forceEndAuction(String reason) { forceEndAuction(reason, seller, false); }
	
	public void forceEndAuction(String reason, Player ender) { forceEndAuction(reason, ender, false); }
	
	public void forceEndAuction(String reason, Player ender, boolean all) {

		timer.cancelTask(task_id);
		
		giveItemStack(object, seller);
		
		if ((ender == null || Storm.hasPermission(ender, "as.cancel") || ender == seller) && Queue.current_auction.equals(this)) {
			
			String displayname;
			if (ender == null) displayname = "the server"; 
			else displayname = ender.getName();
			if (reason != "") ((AuctionMessenger)Storm.getInstance().getMessenger()).broadcast(new StringParser(Messages.AUCTION_END_FORCEDREASON)
					.sender(displayname)
					.amount(amount)
					.item(object)
					.name(object)
					.price(price)
					.currencyPlural()
					.incr(increment)
					.reason(reason));
			else ((AuctionMessenger)Storm.getInstance().getMessenger()).broadcast(new StringParser(Messages.AUCTION_END_FORCED)
					.sender(displayname)
					.amount(amount)
					.item(object)
					.name(object)
					.price(price)
					.currencyPlural()
					.incr(increment));
			
		} else if (ender == null) { 
			
			StringParser msg = reason == "" ? new StringParser(Messages.AUCTION_END_FORCEDSELLER) : new StringParser(Messages.AUCTION_END_FORCEDREASONSELLER);
			
			((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(msg
					.id(Queue.getQueue().indexOf(this))
					.player(seller)
					.sender("the server")
					.amount(amount)
					.item(object)
					.name(object)
					.price(price)
					.currencyPlural()
					.reason(reason), seller);
			
		} else if (Storm.hasPermission(ender, "as.cancel") || ender == seller) {
			
			((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.AUCTION_END_CANCELLED)
				.id(Queue.getQueue().indexOf(this))
				.player(seller)
				.sender("/as cancel")
				.amount(amount)
				.item(object)
				.name(object)
				.price(price)
				.currencyPlural(), ender);
			
			if (ender != seller) {
				
				StringParser msg = reason == "" ? new StringParser(Messages.AUCTION_END_FORCEDSELLER) : new StringParser(Messages.AUCTION_END_FORCEDREASONSELLER);
				
				((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(msg
						.id(Queue.getQueue().indexOf(this))
						.player(seller)
						.sender("/as cancel")
						.amount(amount)
						.item(object)
						.name(object)
						.price(price)
						.currencyPlural()
						.reason(reason), seller);
				
			}
			
		}
		
		else ((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.COMMAND_NO_PERMISSION)
				.player(ender)
				.sender("/as cancel")
				.amount(amount)
				.item(object)
				.name(object)
				.price(price)
				.currencyPlural()
				.incr(increment)
				.reason(reason), ender);
		
		if (!all && Queue.current_auction.equals(this)) Queue.nextAuction();
		
	}

	public static void noAuction(Player player) {
		((AuctionMessenger)Storm.getInstance().getMessenger()).sendMessage(new StringParser(Messages.AUCTION_WRONG_NONE).sender(player), player);
	}
	
	public static String getItemName(ItemStack item) {
		if (item == null) throw new NullPointerException("ItemStack cannot be null!");
		else if (!item.hasItemMeta()) return item.getType().getKey().getKey().toLowerCase().replace("_", " ");
		return item.getItemMeta().hasLocalizedName() ? item.getItemMeta().getLocalizedName() : item.getType().getKey().getKey().toLowerCase().replaceAll("_", " ");
	}
	
	public static String getDisplayName(ItemStack item) {
		if (item == null) throw new NullPointerException("ItemStack cannot be null!");
		return item.hasItemMeta() ? item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : "" : "";
	}
	
	public static void giveItemStack(ItemStack item, OfflinePlayer seller) {
		if (!seller.isOnline()) {
			Storm.gq.addPlayer(seller, item);
			return;
		}
		
		ItemUtil.giveItemStack(item, seller.getPlayer());
	}
}
