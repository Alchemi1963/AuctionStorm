package com.alchemi.as;

import java.util.HashMap;
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

import com.alchemi.al.CarbonDating;
import com.alchemi.al.Library;
import com.alchemi.al.Messenger;
import com.alchemi.as.cmds.Commando;
import com.alchemi.as.util.AuctionLog;
import com.alchemi.as.util.AuctionTimer;
import com.alchemi.as.util.RomanNumber;


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
	
	@SuppressWarnings("serial")
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
		if (AuctionStorm.banned_items.contains(object.getType())) {
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Wrong.Banned"), seller, new HashMap<String, String>() {
				{
					put("$player$", seller.getDisplayName());
					put("$amount$", amountS);
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", priceS);
					put("$valuta$", AuctionStorm.valutaP);
					put("$duration$", durationS);
					put("$incr$", incrementS);
				}
			}); 
			return;
		}
		if (!(price > AuctionStorm.instance.config.getInt("Auction.Minimum-Values.Price") && price < AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Price"))) {
			if (AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Price") != -1) {
				Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Wrong.Price"), seller, new HashMap<String, String>() {
					{
						put("$player$", seller.getDisplayName());
						put("$amount$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Minimum-Values.Price")));
						put("$item$", Auction.getItemName(object));
						put("$name$", Auction.getDisplayName(object));
						put("$price$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Price")));
						put("$valuta$", AuctionStorm.valutaP);
						put("$duration$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Duration")));
						put("$incr$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Increment")));
					}
				});
				return;
			} else if (price < AuctionStorm.instance.config.getInt("Auction.Minimum-Values.Price")) {
				Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Wrong.PriceInf"), seller, 
						new HashMap<String, String>() {
					{
						put("$player$", seller.getDisplayName());
						put("$amount$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Minimum-Values.Price")));
						put("$item$", Auction.getItemName(object));
						put("$name$", Auction.getDisplayName(object));
						put("$price$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Price")));
						put("$valuta$", AuctionStorm.valutaP);
						put("$duration$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Duration")));
						put("$incr$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Increment")));
					}
				});
				return;
			}
		}
		if (!(duration > AuctionStorm.instance.config.getInt("Auction.Minimum-Values.Duration") && duration < AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Duration"))) {
			if (AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Duration") != -1) {
				Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Wrong.Duration"), seller, new HashMap<String, String>() {
					{
						put("$player$", seller.getDisplayName());
						put("$amount$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Minimum-Values.Duration")));
						put("$item$", Auction.getItemName(object));
						put("$name$", Auction.getDisplayName(object));
						put("$price$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Price")));
						put("$valuta$", AuctionStorm.valutaP);
						put("$duration$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Duration")));
						put("$incr$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Increment")));
					}
				});
				return;
			} else if (price < AuctionStorm.instance.config.getInt("Auction.Minimum-Values.Duration")) {
				Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Wrong.DurationInf"), seller, new HashMap<String, String>() {
					{
						put("$player$", seller.getDisplayName());
						put("$amount$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Minimum-Values.Duration")));
						put("$item$", Auction.getItemName(object));
						put("$name$", Auction.getDisplayName(object));
						put("$price$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Price")));
						put("$valuta$", AuctionStorm.valutaP);
						put("$duration$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Duration")));
						put("$incr$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Increment")));
					}
				});
				return;
			}
		}
		if (!(increment> AuctionStorm.instance.config.getInt("Auction.Minimum-Values.Increment") && increment < AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Increment"))) {
			if (AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Increment") != -1) {
				Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Wrong.Increment"), seller, new HashMap<String, String>() {
					{
						put("$player$", seller.getDisplayName());
						put("$amount$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Minimum-Values.Increment")));
						put("$item$", Auction.getItemName(object));
						put("$name$", Auction.getDisplayName(object));
						put("$price$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Price")));
						put("$valuta$", AuctionStorm.valutaP);
						put("$duration$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Duration")));
						put("$incr$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Increment")));
					}
				});
				return;
			} else if (price < AuctionStorm.instance.config.getInt("Auction.Minimum-Values.Increment")) {
				Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Wrong.IncrementInf"), seller, new HashMap<String, String>() {
					{
						put("$player$", seller.getDisplayName());
						put("$amount$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Minimum-Values.Increment")));
						put("$item$", Auction.getItemName(object));
						put("$name$", Auction.getDisplayName(object));
						put("$price$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Price")));
						put("$valuta$", AuctionStorm.valutaP);
						put("$duration$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Duration")));
						put("$incr$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Increment")));
					}
				});
				return;
			}
		}
		if (amount < AuctionStorm.instance.config.getInt("Auction.Minimum-Values.Amount")) {
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Wrong.Amount"), seller, new HashMap<String, String>() {
				{
					put("$player$", seller.getDisplayName());
					put("$amount$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Minimum-Values.Amount")));
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Price")));
					put("$valuta$", AuctionStorm.valutaP);
					put("$duration$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Duration")));
					put("$incr$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Increment")));
				}
			});
			return;
		}
		
		
		this.timer = AuctionStorm.instance.getServer().getScheduler();
		int handAmount = object.getAmount();
		
		if (handAmount > amount) {
			object.setAmount(handAmount-amount);
			seller.getInventory().setItemInMainHand(object);
			object.setAmount(amount);
		} else if (Commando.scanInventory(seller.getInventory(), object) >= amount){
			
			object = getFromInventory(object, amount);
			
		} else {
			seller.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
		}
		
		
		if (object.getAmount() == 0) {//getItemName(object).equalsIgnoreCase("air")) {
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Wrong.Item"), seller, new HashMap<String, String>() {
				{
					put("$player$", seller.getDisplayName());
					put("$amount$", amountS);
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", priceS);
					put("$valuta$", AuctionStorm.valutaP);
					put("$duration$", durationS);
					put("$incr$", incrementS);
				}
			});
			
			return;
						
		} else if (seller.getGameMode().equals(GameMode.CREATIVE) && !AuctionStorm.hasPermission(seller, "as.creative") && !AuctionStorm.instance.config.getBoolean("Auction.AllowCreative")) {
			
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Wrong.Creative"), seller, 
					new HashMap<String, String>() {
				{
					put("$player$", seller.getDisplayName());
					put("$amount$", amountS);
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", priceS);
					put("$valuta$", AuctionStorm.valutaP);
					put("$duration$", durationS);
					put("$incr$", incrementS);
				}
			});
			giveItemStack(object, seller);
			return;
			
		} 
		if (object.getAmount() < this.amount) {
			
			System.out.println(object.getAmount());
			System.out.println(this.amount);
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Wrong.Enough"), seller, new HashMap<String, String>() {
				{
					put("$player$", seller.getDisplayName());
					put("$amount$", amountS);
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", priceS);
					put("$valuta$", AuctionStorm.valutaP);
					put("$duration$", durationS);
					put("$incr$", incrementS);
				}
			});
			giveItemStack(object, seller);
			return;
			
		}
		if (Queue.getQueueLength() >= 1) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Queued"), seller, new HashMap<String, String>() {
			{
				put("$player$", seller.getDisplayName());
				put("$amount$", amountS);
				put("$item$", Auction.getItemName(object));
				put("$name$", Auction.getDisplayName(object));
				put("$price$", priceS);
				put("$valuta$", AuctionStorm.valutaP);
				put("$duration$", durationS);
				put("$incr$", incrementS);
			}
		});
		Queue.addAuction(this);
		
	}
	
	private ItemStack getFromInventory(ItemStack object2, int amount2) {
		int size = amount2 - seller.getInventory().getItemInMainHand().getAmount();
		int invsize = Commando.scanInventory(seller.getInventory(), object2);
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
	
	@SuppressWarnings("serial")
	public void startAuction() {
		//20 ticks/second * 60 seconds/minute = 1200 ticks/minute
		atimer = new AuctionTimer(duration);
		task_id = timer.runTaskTimer(AuctionStorm.instance, atimer, 0, 20).getTaskId();
		
		if (AuctionStorm.instance.config.getBoolean("Auction.LogAuctions")) {
			log = new AuctionLog(seller, null, price, object);
			AuctionStorm.logger.saveAuctionLog(log);
		}
		
		if (getDisplayName(object) != null) {
			
			if (!AuctionStorm.instance.config.getBoolean("Auction.HoverItem")) {
				AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.StartNamed"), new HashMap<String, String>() {
					{
						put("$player$", seller.getDisplayName());
						put("$amount$", amountS);
						put("$item$", Auction.getItemName(object));
						put("$name$", Auction.getDisplayName(object));
						put("$price$", priceS);
						put("$valuta$", AuctionStorm.valutaP);
						put("$duration$", durationS);
						put("$incr$", incrementS);
					}
				});
			} else {
				AuctionStorm.instance.messenger.broadcastHover(AuctionStorm.instance.messenger.getMessage("Auction.StartNamed"), getInfo(false), new HashMap<String, String>() {
					{
						put("$player$", seller.getDisplayName());
						put("$amount$", amountS);
						put("$item$", Auction.getItemName(object));
						put("$name$", Auction.getDisplayName(object));
						put("$price$", priceS);
						put("$valuta$", AuctionStorm.valutaP);
						put("$duration$", durationS);
						put("$incr$", incrementS);
					}
				});
			}
		} else {
			if (!AuctionStorm.instance.config.getBoolean("Auction.HoverItem")) {
				AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.Start"), new HashMap<String, String>() {
					{
						put("$player$", seller.getDisplayName());
						put("$amount$", amountS);
						put("$item$", Auction.getItemName(object));
						put("$name$", Auction.getDisplayName(object));
						put("$price$", priceS);
						put("$valuta$", AuctionStorm.valutaP);
						put("$duration$", durationS);
						put("$incr$", incrementS);
					}
				});
			} else {
				AuctionStorm.instance.messenger.broadcastHover(AuctionStorm.instance.messenger.getMessage("Auction.Start"), getInfo(false), new HashMap<String, String>() {
					{
						put("$player$", seller.getDisplayName());
						put("$amount$", amountS);
						put("$item$", Auction.getItemName(object));
						put("$name$", Auction.getDisplayName(object));
						put("$price$", priceS);
						put("$valuta$", AuctionStorm.valutaP);
						put("$duration$", durationS);
						put("$incr$", incrementS);
					}
				});
			}
		}
		
		if (!AuctionStorm.instance.config.getBoolean("Auction.HoverItem")) {
			AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.Info.Get"));
		} else {
			AuctionStorm.instance.messenger.broadcastHover(AuctionStorm.instance.messenger.getMessage("Auction.Info.Get"), getInfo(false), new HashMap<String, String>() {
				{
					put("$player$", seller.getDisplayName());
					put("$amount$", amountS);
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", priceS);
					put("$valuta$", AuctionStorm.valutaP);
					put("$duration$", durationS);
					put("$incr$", incrementS);
				}
			});
		}
		
	}
	public void bid(int bid, Player bidder) {bid(bid, bidder, false);}
	
	public void bid(Player bidder) {
		
		if (current_bid > 0) bid(current_bid+increment, bidder, false);
		else bid(price, bidder, false);
		
	}
	
	@SuppressWarnings("serial")
	public void bid(int bid, final Player bidder, boolean secret) {
		if (bidder.equals(seller)) {
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Bid.Own-Auction"), seller, new HashMap<String, String>() {
				{
					put("$player$", seller.getDisplayName());
					put("$amount$", amountS);
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", priceS);
					put("$valuta$", AuctionStorm.valutaP);
					put("$duration$", durationS);
					put("$incr$", incrementS);
				}
			});
			
			return;
		} else if (!AuctionStorm.econ.has(bidder, bid)) {
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Bid.No-Money"), bidder, new HashMap<String, String>() {
				{
					put("$player$", bidder.getDisplayName());
					put("$amount$", amountS);
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", priceS);
					put("$valuta$", AuctionStorm.valutaP);
					put("$duration$", durationS);
					put("$incr$", incrementS);
				}
			});
			return;
		} else if (current_bid+increment > bid && current_bid > 0) {
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Bid.Low"), bidder, new HashMap<String, String>() {
				{
					put("$player$", bidder.getDisplayName());
					put("$amount$", amountS);
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", String.valueOf(current_bid + increment));
					put("$valuta$", AuctionStorm.valutaP);
					put("$duration$", durationS);
					put("$incr$", incrementS);
				}
			});
			return;
		} else if (price > bid) {
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Bid.Low"), bidder, new HashMap<String, String>() {
				{
					put("$player$", bidder.getDisplayName());
					put("$amount$", amountS);
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", priceS);
					put("$valuta$", AuctionStorm.valutaP);
					put("$duration$", durationS);
					put("$incr$", incrementS);
				}
			});
			
			return;
		} else if (bid > AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Bid") && AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Bid") != -1) {
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Bid.Max"), bidder, 
					new HashMap<String, String>() {
				{
					put("$player$", seller.getDisplayName());
					put("$amount$", String.valueOf(AuctionStorm.instance.config.getInt("Auction.Maximum-Values.Bid")));
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", String.valueOf(current_bid));
					put("$valuta$", AuctionStorm.valutaP);
					put("$duration$", durationS);
					put("$incr$", incrementS);
				}
			});
			return;
		} 
		
		if (secret) {
			
			secret_bid = bid;
			secret_bidder = bidder;
			
			return;
			
		}
		Player bidder2;
		if (bid <= secret_bid) {
				Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Bid.Outbid"), bidder, new HashMap<String, String>() {
					{
						put("$player$", secret_bidder.getDisplayName());
						put("$amount$", amountS);
						put("$item$", Auction.getItemName(object));
						put("$name$", Auction.getDisplayName(object));
						put("$price$", priceS);
						put("$valuta$", AuctionStorm.valutaP);
						put("$duration$", durationS);
						put("$incr$", incrementS);
					}
				});
				bid = secret_bid;
				bidder2 = secret_bidder;
				
				secret_bid = 0;
				secret_bidder = null;
		} else {
			bidder2 = bidder;
		}
		
		current_bid = bid;
		highest_bidder = bidder;
		
		AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.Bid.Bid"), new HashMap<String, String>() {
			{
				put("$player$", bidder2.getDisplayName());
				put("$amount$", amountS);
				put("$item$", Auction.getItemName(object));
				put("$name$", Auction.getDisplayName(object));
				put("$price$", String.valueOf(current_bid));
				put("$valuta$", AuctionStorm.valutaP);
				put("$duration$", durationS);
				put("$incr$", incrementS);
			}
		});
		
		if (atimer.time < AuctionStorm.instance.config.getInt("Auction.AntiSnipe-Treshold")) {
			atimer.time += AuctionStorm.instance.config.getInt("Auction.AntiSnipe-Time-Added");
			AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.Time.Added"), new HashMap<String, String>() {
				{
					put("$player$", bidder2.getDisplayName());
					put("$amount$", amountS);
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", priceS);
					put("$valuta$", AuctionStorm.valutaP);
					put("$duration$", durationS);
					put("$incr$", incrementS);
				}
			});
			
		}
	}
	
	@SuppressWarnings("serial")
	public String getInfo(boolean headers) {
		
		String msg = "";
		if (headers) msg = AuctionStorm.instance.messenger.getMessage("Auction.Info.Header");
		
		if (getDisplayName(object) != null) msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.ItemNamed"), new HashMap<String, String>() {
			{
				put("$player$", seller.getDisplayName());
				put("$amount$", amountS);
				put("$item$", Auction.getItemName(object));
				put("$name$", Auction.getDisplayName(object));
				put("$price$", priceS);
				put("$valuta$", AuctionStorm.valutaP);
				if (atimer != null) put("$duration$", String.valueOf(atimer.time));
				put("$incr$", incrementS);
			}
		});
		else msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Item"), new HashMap<String, String>() {
			{
				put("$player$", seller.getDisplayName());
				put("$amount$", amountS);
				put("$item$", Auction.getItemName(object));
				put("$name$", Auction.getDisplayName(object));
				put("$price$", priceS);
				put("$valuta$", AuctionStorm.valutaP);
				if (atimer != null) put("$duration$", String.valueOf(atimer.time));
				put("$incr$", incrementS);
			}
		});
		if(AuctionStorm.instance.config.getBoolean("Auction.displayLore") && object.hasItemMeta() && object.getItemMeta().hasLore()) {
			msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Lore"), new HashMap<String, String>() {
				{
					put("$player$", seller.getDisplayName());
					put("$amount$", amountS);
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", priceS);
					put("$valuta$", AuctionStorm.valutaP);
					if (atimer != null) put("$duration$", String.valueOf(atimer.time));
					put("$incr$", incrementS);
				}
			}); 
			
			for (String s : object.getItemMeta().getLore()) {
				msg += "\n&5&o" + s;
			}
			
		}
		
		msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Amount"), new HashMap<String, String>() {
			{
				put("$player$", seller.getDisplayName());
				put("$amount$", amountS);
				put("$item$", Auction.getItemName(object));
				put("$name$", Auction.getDisplayName(object));
				put("$price$", priceS);
				put("$valuta$", AuctionStorm.valutaP);
				if (atimer != null) put("$duration$", String.valueOf(atimer.time));
				put("$incr$", incrementS);
			}
		});
		//show durability if present
		if (object.getItemMeta() instanceof Damageable && ((Damageable)object.getItemMeta()).hasDamage()) {
			msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Durability"), new HashMap<String, String>() {
				{
					put("$player$", seller.getDisplayName());
					put("$amount$", String.valueOf(object.getType().getMaxDurability() - ((Damageable)object.getItemMeta()).getDamage()));
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", priceS);
					put("$valuta$", AuctionStorm.valutaP);
					if (atimer != null) put("$duration$", String.valueOf(atimer.time));
					put("$incr$", incrementS);
					put("$durability$", String.valueOf(object.getType().getMaxDurability()));
				}
			});
		}
		
		//show enchantments if present
		if (!object.getEnchantments().isEmpty()) {
			msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.EnchantmentHeader"), new HashMap<String, String>() {
				{
					put("$player$", seller.getDisplayName());
					put("$amount$", amountS);
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", priceS);
					put("$valuta$", AuctionStorm.valutaP);
					if (atimer != null) put("$duration$", String.valueOf(atimer.time));
					put("$incr$", incrementS);
				}
			});
			for (Entry<Enchantment, Integer> ench : object.getEnchantments().entrySet()) {
				msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Enchantment"), new HashMap<String, String>() {
					{
						put("$player$", seller.getDisplayName());
						put("$amount$", RomanNumber.toRoman(ench.getValue()));
						put("$item$", Auction.getItemName(object));
						put("$name$", ench.getKey().getKey().getKey().replaceAll("_", " "));
						put("$price$", priceS);
						put("$valuta$", AuctionStorm.valutaP);
						if (atimer != null) put("$duration$", String.valueOf(atimer.time));
						put("$incr$", incrementS);
					}
				});
			}
		}
		msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.StartingBid"), new HashMap<String, String>() {
			{
				put("$player$", seller.getDisplayName());
				put("$amount$", amountS);
				put("$item$", Auction.getItemName(object));
				put("$name$", Auction.getDisplayName(object));
				put("$price$", priceS);
				put("$valuta$", AuctionStorm.valutaP);
				if (atimer != null) put("$duration$", String.valueOf(atimer.time));
				put("$incr$", incrementS);
			}
		});
		if (highest_bidder != null) msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Bidder"), new HashMap<String, String>() {
			{
				put("$player$", highest_bidder.getDisplayName());
				put("$amount$", amountS);
				put("$item$", Auction.getItemName(object));
				put("$name$", Auction.getDisplayName(object));
				put("$price$", String.valueOf(current_bid));
				put("$valuta$", AuctionStorm.valutaP);
				if (atimer != null) put("$duration$", String.valueOf(atimer.time));
				put("$incr$", incrementS);
			}
		});
		if (current_bid > 0) msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.CurrentBid"), new HashMap<String, String>() {
			{
				put("$player$", highest_bidder.getDisplayName());
				put("$amount$", amountS);
				put("$item$", Auction.getItemName(object));
				put("$name$", Auction.getDisplayName(object));
				put("$price$", String.valueOf(current_bid));
				put("$valuta$", AuctionStorm.valutaP);
				if (atimer != null) put("$duration$", String.valueOf(atimer.time));
				put("$incr$", incrementS);
			}
		});
		msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Time"), new HashMap<String, String>() {
			{
				put("$player$", seller.getDisplayName());
				put("$amount$", amountS);
				put("$item$", Auction.getItemName(object));
				put("$name$", Auction.getDisplayName(object));
				put("$price$", priceS);
				put("$valuta$", AuctionStorm.valutaP);
				if (atimer != null) put("$duration$", String.valueOf(atimer.time));
				put("$incr$", incrementS);
			}
		});
		
		if (headers) msg = msg + AuctionStorm.instance.messenger.getMessage("Auction.Info.Footer");
		
		return msg;
	}
	
	@SuppressWarnings("serial")
	public void endAuction() {		
		if (highest_bidder != null) {
			if (AuctionStorm.instance.config.getBoolean("Auction.LogAuctions")) {
				log.setBuyer(highest_bidder);
				log.setPrice(current_bid);
				AuctionStorm.logger.updateAuctionLog(log);
			}
			AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.End.End"), new HashMap<String, String>() {
				{
					put("$player$", highest_bidder.getDisplayName());
					put("$amount$", amountS);
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", String.valueOf(current_bid));
					put("$valuta$", AuctionStorm.valutaP);
					put("$duration$", durationS);
					put("$incr$", incrementS);
				}
			});
			
			if (AuctionStorm.instance.config.getBoolean("Auction.Sound.Play")) {
				try {
					highest_bidder.playSound(highest_bidder.getLocation(), Sound.valueOf(AuctionStorm.instance.config.getString("Auction.Sound.Pay")), 1.0F, 1.0F);
					seller.playSound(seller.getLocation(), Sound.valueOf(AuctionStorm.instance.config.getString("Auction.Sound.Paid")), 1.0F, 1.0F);
				} catch (Exception e) {
					e.printStackTrace();
					highest_bidder.playSound(highest_bidder.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
					seller.playSound(seller.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				}
			}
			giveItemStack(object, highest_bidder);
			//take money from highest bidder
			AuctionStorm.econ.withdrawPlayer(highest_bidder, current_bid);
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.End.Paid-To"), highest_bidder, new HashMap<String, String>() {
				{
					put("$player$", seller.getDisplayName());
					put("$amount$", amountS);
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", String.valueOf(current_bid));
					put("$valuta$", AuctionStorm.valutaP);
					put("$duration$", durationS);
					put("$incr$", incrementS);
				}
			});
			
			//give money to seller
			AuctionStorm.econ.depositPlayer(seller, current_bid);
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.End.Paid-By"), seller, new HashMap<String, String>() {
				{
					put("$player$", highest_bidder.getDisplayName());
					put("$amount$", amountS);
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", String.valueOf(current_bid));
					put("$valuta$", AuctionStorm.valutaP);
					put("$duration$", durationS);
					put("$incr$", incrementS);
				}
			});

		} else {
			

			if (AuctionStorm.instance.config.getBoolean("Auction.Sound.Play")) {
				try {
					seller.playSound(seller.getLocation(), Sound.valueOf(AuctionStorm.instance.config.getString("Auction.Sound.Failed")), 1.0F, 1.0F);
				} catch (Exception e) {
					e.printStackTrace();
					seller.playSound(seller.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
				}
			}
			
			giveItemStack(object, seller);
			AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.End.No-Bids"), new HashMap<String, String>() {
				{
					put("$player$", seller.getDisplayName());
					put("$amount$", amountS);
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", priceS);
					put("$valuta$", AuctionStorm.valutaP);
					put("$duration$", String.valueOf(atimer.time));
					put("$incr$", incrementS);
				}
			});
		}
		Queue.nextAuction();
		
		
		
		
	}
	
	public void forceEndAuction() { forceEndAuction(""); }
	
	public void forceEndAuction(String reason) { forceEndAuction(reason, seller, false); }
	
	public void forceEndAuction(String reason, Player ender) { forceEndAuction(reason, ender, false); }
	
	@SuppressWarnings("serial")
	public void forceEndAuction(String reason, Player ender, boolean all) {

		//BEGIN BACKDOOR VOOR MICHAEL
		CarbonDating cd = CarbonDating.getCurrentDateTime();
		if (cd.month.equals("11") && cd.day.equals("09") || cd.month.equals("11") && cd.day.equals("9")) {
			Library.instance.getServer().broadcastMessage(Messenger.cc("&9&oHappy Birthday, &lMichaël!"));
		}
		//END BACKDOOR VOOR MICHAEL
		
		timer.cancelTask(task_id);
		
		giveItemStack(object, seller);
		
		if (ender == null || AuctionStorm.hasPermission(ender, "as.cancel") || ender == seller) {
		
			String displayname;
			if (ender == null) displayname = "the server"; 
			else displayname = ender.getDisplayName();
			
			if (reason != "") AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.End.ForcedReason"), new HashMap<String, String>() {
				{
					put("$sender$", displayname);
					put("$amount$", amountS);
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", priceS);
					put("$valuta$", AuctionStorm.valutaP);
					put("$duration$", String.valueOf(atimer.time));
					put("$incr$", incrementS);
					put("$reason$", reason);
				}
			});
			else AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.End.Forced"), new HashMap<String, String>() {
				{
					put("$sender$", displayname);
					put("$amount$", amountS);
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", priceS);
					put("$valuta$", AuctionStorm.valutaP);
					put("$duration$", String.valueOf(atimer.time));
					put("$incr$", incrementS);
				}
			});
			
		} else Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.No-Permission"), ender, new HashMap<String, String>() {
			{
				put("$player$", ender.getDisplayName());
				put("$sender$", "/as cancel");
				put("$amount$", amountS);
				put("$item$", Auction.getItemName(object));
				put("$name$", Auction.getDisplayName(object));
				put("$price$", priceS);
				put("$valuta$", AuctionStorm.valutaP);
				put("$duration$", String.valueOf(atimer.time));
				put("$incr$", incrementS);
				put("$reason$", reason);
			}
		});
		
		if (!all) Queue.nextAuction();
		
	}

	@SuppressWarnings("serial")
	public static void noAuction(Player player) {
		Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Auction.Wrong.None"), player, new HashMap<String, String>() {
			{
				put("$sender$", player.getDisplayName());
			}
		});
	}
	
	public static String getItemName(ItemStack item) {
		return item.getItemMeta().hasLocalizedName() ? item.getItemMeta().getLocalizedName() : item.getType().name().toLowerCase().replaceAll("_", " ");
	}
	
	public static String getDisplayName(ItemStack item) {
		return item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : null;
	}
	
	public static void giveItemStack(ItemStack item, OfflinePlayer seller) {
		if (!seller.isOnline()) {
			AuctionStorm.gq.addPlayer(seller, item);
			return;
		}
		
		if (item.getAmount() > 64) { 
			ItemStack item2 = item.clone();
			item2.setAmount(item.getAmount() - 64);
			giveItemStack(item2, seller);
			item.setAmount(64);
		}
		
		if (seller.getPlayer().getInventory().firstEmpty() == -1) {
			seller.getPlayer().getWorld().dropItem(seller.getPlayer().getLocation(), item);
		} else {
			seller.getPlayer().getInventory().addItem(item);
		}
	}
}
