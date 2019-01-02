package com.alchemi.as.util;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.alchemi.al.CarbonDating;
import com.alchemi.al.Messenger;
import com.alchemi.as.Auction;
import com.alchemi.as.AuctionStorm;

public class AuctionLog{

	private final OfflinePlayer seller;
	private OfflinePlayer buyer;
	private String sellerName;
	private String buyerName;
	private UUID sellerID = null;
	private UUID buyerID = null;
	
	private int price;
	private final ItemStack object;
	private final boolean refunded;
	private final CarbonDating identifier;
	
	
	public AuctionLog(Player seller, Player buyer, int price, ItemStack object) {
		
		this.seller = seller;
		this.sellerID = seller.getUniqueId();
		this.buyer = buyer;
		if (buyer != null) this.buyerID = buyer.getUniqueId();
		this.price = price;
		this.object = object;
		this.refunded = false;
		this.identifier = CarbonDating.getCurrentDateTime();
		
		if (seller.isOnline()) sellerName = seller.getDisplayName();
		else sellerName = this.seller.getName();
		
		if (buyer != null && buyer.isOnline()) buyerName = buyer.getDisplayName();
		else if (buyer != null) buyerName = this.buyer.getName();
		
	}
	
	public AuctionLog(String seller, String sellerID, String buyer, String buyerID, int price, ItemStack object, boolean refunded, CarbonDating id) {
		if (buyer != null) this.buyer = AuctionStorm.instance.getServer().getPlayer(buyer);
		else this.buyer = null;
		this.price = price;
		this.object = object;
		this.refunded = refunded;
		this.identifier = id;
		
		try {
			this.sellerID = UUID.fromString(sellerID);
		} catch (IllegalArgumentException ignored) {}
		try {
			this.buyerID = UUID.fromString(buyerID);
		} catch (IllegalArgumentException ignored) {}
		
		if (AuctionStorm.instance.getServer().getPlayer(seller) == null && this.sellerID != null) this.seller = AuctionStorm.instance.getServer().getOfflinePlayer(this.sellerID);
		else if (AuctionStorm.instance.getServer().getPlayer(seller) != null) this.seller = AuctionStorm.instance.getServer().getPlayer(seller);
		else this.seller = null;
		if (this.buyer == null && this.buyerID != null) this.buyer = AuctionStorm.instance.getServer().getOfflinePlayer(this.buyerID);
		
		if (this.seller.isOnline()) sellerName = this.seller.getPlayer().getDisplayName();
		else sellerName = this.seller.getName();
		
		if (this.buyer != null && this.buyer.isOnline()) buyerName = this.buyer.getPlayer().getDisplayName();
		else if (this.buyer != null) buyerName = this.buyer.getName();
	}
	
	public String getBuyer() {
		return buyer != null ? buyer.getName() : null;
	}
	
	public String getBuyerUUID() {
		return buyer != null ? buyerID.toString() : "";
	}
	
	public ItemStack getObject() {
		return object;
	}
	
	public int getPrice() {
		return price;
	}
	
	public String getSeller() {
		return seller.getName();
	}
	
	public String getSellerUUID() {
		return sellerID.toString();
	}
	
	public CarbonDating getIdentifier() {
		return identifier;
	}
	
	public boolean hasBeenRefunded() {
		return refunded;
	}
	
	public void setBuyer(Player buyer) {
		this.buyer = buyer;
		if (buyer != null) this.buyerID = buyer.getUniqueId();
	}
	
	public void setPrice(int price) {
		this.price = price;
	}
	
	@SuppressWarnings("serial")
	public void getInfo(CommandSender sender) {
		String amountS = String.valueOf(object.getAmount());
		String priceS = String.valueOf(price);

		String msg = Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.LogHeader"), new HashMap<String, Object>() {
			{
				put("$name$", identifier.getCarbonDate());
			}
		});
		if (Auction.getDisplayName(object) != null) msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.ItemNamed"), new HashMap<String, Object>() {
			{
				put("$player$", sellerName);
				put("$amount$", amountS);
				put("$item$", Auction.getItemName(object));
				put("$name$", Auction.getDisplayName(object));
				put("$price$", priceS);
				put("$valuta$", AuctionStorm.valutaP);
			}
		}); 
		else msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Item"), new HashMap<String, Object>() {
			{
				put("$player$", sellerName);
				put("$amount$", amountS);
				put("$item$", Auction.getItemName(object));
				put("$name$", Auction.getDisplayName(object));
				put("$price$", priceS);
				put("$valuta$", AuctionStorm.valutaP);
			}
		});
		msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Amount"), new HashMap<String, Object>() {
			{
				put("$player$", sellerName);
				put("$amount$", amountS);
				put("$item$", Auction.getItemName(object));
				put("$name$", Auction.getDisplayName(object));
				put("$price$", priceS);
				put("$valuta$", AuctionStorm.valutaP);
			}
		}); 
		
		//show enchantments if present
		if (!object.getEnchantments().isEmpty()) {
			msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.EnchantmentHeader"), new HashMap<String, Object>() {
				{
					put("$player$", sellerName);
					put("$amount$", amountS);
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", priceS);
					put("$valuta$", AuctionStorm.valutaP);
				}
			}); 
			for (Entry<Enchantment, Integer> ench : object.getEnchantments().entrySet()) {
				msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Enchantment"), new HashMap<String, Object>() {
					{
						put("$player$", sellerName);
						put("$amount$", RomanNumber.toRoman(ench.getValue()));
						put("$item$", Auction.getItemName(object));
						put("$name$", ench.getKey().getKey().getKey());
						put("$price$", priceS);
						put("$valuta$", AuctionStorm.valutaP);
					}
				});
			}
		}
		msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Price"), new HashMap<String, Object>() {
			{
				put("$player$", sellerName);
				put("$amount$", amountS);
				put("$item$", Auction.getItemName(object));
				put("$name$", Auction.getDisplayName(object));
				put("$price$", priceS);
				put("$valuta$", AuctionStorm.valutaP);
			}
		}); 
		if (buyer != null) msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Bidder"), new HashMap<String, Object>() {
			{
				put("$player$", buyer.getPlayer().getDisplayName());
				put("$amount$", amountS);
				put("$item$", Auction.getItemName(object));
				put("$name$", Auction.getDisplayName(object));
				put("$price$", priceS);
				put("$valuta$", AuctionStorm.valutaP);
			}
		}); 
		msg = msg + AuctionStorm.instance.messenger.getMessage("Auction.Info.Footer").substring(0, AuctionStorm.instance.messenger.getMessage("Auction.Info.Footer").length() - 1);
		
		if (sender instanceof Player) {
			Messenger.sendMsg(msg, (Player) sender);
		} else {
			AuctionStorm.instance.messenger.print( "\n" + msg, false);
		}
	}

	@SuppressWarnings("serial")
	public void returnAll(CommandSender sender) {
		
		
		
		if (refunded) {
			if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Already-Refunded"), (Player) sender, new HashMap<String, Object>() {
				{
					put("$player$", sellerName);
					put("$amount$", String.valueOf(object.getAmount()));
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", String.valueOf(price));
					put("$valuta$", AuctionStorm.valutaP);
				}
			}); 
			else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Admin.Already-Refunded"), new HashMap<String, Object>() {
				{
					put("$player$", sellerName);
					put("$amount$", String.valueOf(object.getAmount()));
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", String.valueOf(price));
					put("$valuta$", AuctionStorm.valutaP);
				}
			}); 
			return;
		} else if (buyer != null) {
			if (buyer.isOnline()) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Money-Returned"), buyer.getPlayer(), new HashMap<String, Object>() {
				{
					put("$player$", sellerName);
					put("$amount$", String.valueOf(object.getAmount()));
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", String.valueOf(price));
					put("$valuta$", AuctionStorm.valutaP);
				}
			}); 
			if (seller.isOnline()) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Money-Taken"), seller.getPlayer(), new HashMap<String, Object>() {
				{
					put("$player$", buyerName);
					put("$amount$", String.valueOf(object.getAmount()));
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", String.valueOf(price));
					put("$valuta$", AuctionStorm.valutaP);
				}
			}); 
			AuctionStorm.econ.depositPlayer(buyer, price);
			AuctionStorm.econ.withdrawPlayer(seller, price);
			
			
		} else {
			if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.No-Buyer"), (Player) sender, new HashMap<String, Object>() {
				{
					put("$player$", sellerName);
					put("$amount$", String.valueOf(object.getAmount()));
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", String.valueOf(price));
					put("$valuta$", AuctionStorm.valutaP);
				}
			}); 
			else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Admin.No-Buyer"), new HashMap<String, Object>() {
				{
					put("$player$", sellerName);
					put("$amount$", String.valueOf(object.getAmount()));
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", String.valueOf(price));
					put("$valuta$", AuctionStorm.valutaP);
				}
			}); 
		}
		
		if (seller.isOnline()) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Items-Returned"), seller.getPlayer(), new HashMap<String, Object>() {
			{
				put("$player$", sellerName);
				put("$amount$", String.valueOf(object.getAmount()));
				put("$item$", Auction.getItemName(object));
				put("$name$", Auction.getDisplayName(object));
				put("$price$", String.valueOf(price));
				put("$valuta$", AuctionStorm.valutaP);
			}
		}); 
		
		Auction.giveItemStack(object, seller);
		
		AuctionStorm.logger.setRefunded(this, identifier);
	}
	
	@SuppressWarnings("serial")
	public void returnItemToSeller(CommandSender sender) {
		if (refunded) {
			if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Already-Refunded"), (Player) sender, new HashMap<String, Object>() {
				{
					put("$player$", sellerName);
					put("$amount$", String.valueOf(object.getAmount()));
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", String.valueOf(price));
					put("$valuta$", AuctionStorm.valutaP);
				}
			}); 
			else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Admin.Already-Refunded"), new HashMap<String, Object>() {
				{
					put("$player$", sellerName);
					put("$amount$", String.valueOf(object.getAmount()));
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", String.valueOf(price));
					put("$valuta$", AuctionStorm.valutaP);
				}
			}); 
			return;
		}
		
		if (seller.isOnline()) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Items-Returned"), seller.getPlayer(), new HashMap<String, Object>() {
			{
				put("$player$", sellerName);
				put("$amount$", String.valueOf(object.getAmount()));
				put("$item$", Auction.getItemName(object));
				put("$name$", Auction.getDisplayName(object));
				put("$price$", String.valueOf(price));
				put("$valuta$", AuctionStorm.valutaP);
			}
		}); 
		Auction.giveItemStack(object, seller);
		
		AuctionStorm.logger.setRefunded(this, identifier);
	}
	
	@SuppressWarnings("serial")
	public void returnMoneyToBuyer(CommandSender sender) {
		if (refunded) {
			if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Already-Refunded"), (Player) sender, new HashMap<String, Object>() {
				{
					put("$player$", sellerName);
					put("$amount$", String.valueOf(object.getAmount()));
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", String.valueOf(price));
					put("$valuta$", AuctionStorm.valutaP);
				}
			}); 
			else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Admin.Already-Refunded"), new HashMap<String, Object>() {
				{
					put("$player$", sellerName);
					put("$amount$", String.valueOf(object.getAmount()));
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", String.valueOf(price));
					put("$valuta$", AuctionStorm.valutaP);
				}
			}); 
			return;
		}
		
		if (buyer != null) {
			if (buyer.isOnline()) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Money-Returned"), buyer.getPlayer(), new HashMap<String, Object>() {
				{
					put("$player$", sellerName);
					put("$amount$", String.valueOf(object.getAmount()));
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", String.valueOf(price));
					put("$valuta$", AuctionStorm.valutaP);
				}
			}); 
			if (seller.isOnline()) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Money-Taken"), seller.getPlayer(), new HashMap<String, Object>() {
				{
					put("$player$", buyerName);
					put("$amount$", String.valueOf(object.getAmount()));
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", String.valueOf(price));
					put("$valuta$", AuctionStorm.valutaP);
				}
			}); 
			AuctionStorm.econ.depositPlayer(buyer, price);
			AuctionStorm.econ.withdrawPlayer(seller, price);
			AuctionStorm.logger.setRefunded(this, identifier);
		} else {
			if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.No-Buyer"), (Player) sender, new HashMap<String, Object>() {
				{
					put("$player$", sellerName);
					put("$amount$", String.valueOf(object.getAmount()));
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", String.valueOf(price));
					put("$valuta$", AuctionStorm.valutaP);
				}
			}); 
			else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Admin.No-Buyer"), new HashMap<String, Object>() {
				{
					put("$player$", sellerName);
					put("$amount$", String.valueOf(object.getAmount()));
					put("$item$", Auction.getItemName(object));
					put("$name$", Auction.getDisplayName(object));
					put("$price$", String.valueOf(price));
					put("$valuta$", AuctionStorm.valutaP);
				}
			}); 
		}
		
	}
	
}
