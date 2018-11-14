package com.alchemi.as.util;

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
	
	public void getInfo(CommandSender sender) {
		String amountS = String.valueOf(object.getAmount());
		String priceS = String.valueOf(price);
		String displayName;
		try {
			displayName = ((Player) seller).getDisplayName();
		} catch (ClassCastException ignored) {
			displayName = seller.getName();
		}

		String msg = Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.LogHeader"), null, null, null, null, identifier.getCarbonDate());
		if (Auction.getDisplayName(object) != null) msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.ItemNamed"), displayName, "[SERVER]", amountS, Auction.getItemName(object), Auction.getDisplayName(object), priceS, AuctionStorm.valutaP);
		else msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Item"), displayName, "[SERVER]", amountS, Auction.getItemName(object), Auction.getItemName(object), priceS, AuctionStorm.valutaP);
		msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Amount"), displayName, "[SERVER]", amountS, Auction.getItemName(object), Auction.getDisplayName(object), priceS, AuctionStorm.valutaP);
		//show enchantments if present
		if (!object.getEnchantments().isEmpty()) {
			msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.EnchantmentHeader"), displayName, "[SERVER]", amountS, Auction.getItemName(object), Auction.getDisplayName(object), priceS, AuctionStorm.valutaP);
			for (Entry<Enchantment, Integer> ench : object.getEnchantments().entrySet()) {
				msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Enchantment"), displayName, "[SERVER]", RomanNumber.toRoman(ench.getValue()), Auction.getItemName(object), ench.getKey().getKey().getKey(), priceS, AuctionStorm.valutaP);
			}
		}
		msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Price"), displayName, "[SERVER]", amountS, Auction.getItemName(object), Auction.getDisplayName(object), priceS, AuctionStorm.valutaP);
		if (buyer != null) msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Bidder"), buyer.getPlayer().getDisplayName(), "[SERVER]", amountS, Auction.getItemName(object), Auction.getDisplayName(object), priceS, AuctionStorm.valutaP);
		msg = msg + AuctionStorm.instance.messenger.getMessage("Auction.Info.Footer").substring(0, AuctionStorm.instance.messenger.getMessage("Auction.Info.Footer").length() - 1);
		
		if (sender instanceof Player) {
			Messenger.sendMsg(msg, (Player) sender);
		} else {
			AuctionStorm.instance.messenger.print( "\n" + msg, false);
		}
	}

	public void returnAll(CommandSender sender) {
		String displayName;
		String buyerDisplayName;
		try {
			displayName = ((Player) seller).getDisplayName();
		} catch (ClassCastException ignored) {
			displayName = seller.getName();
		}
		
		
		
		if (refunded) {
			if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Already-Refunded"), (Player) sender, displayName, AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
			else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Admin.Already-Refunded"), displayName, AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
			return;
		} else if (buyer != null) {
			try {
				buyerDisplayName = ((Player) buyer).getDisplayName();
			} catch (ClassCastException ignored) {
				buyerDisplayName = buyer.getName();
			}
			if (buyer.isOnline()) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Money-Returned"), buyer.getPlayer(), displayName, AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
			if (seller.isOnline()) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Money-Taken"), seller.getPlayer(), buyerDisplayName, AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
			AuctionStorm.econ.depositPlayer(buyer, price);
			AuctionStorm.econ.withdrawPlayer(seller, price);
			
			
		} else {
			if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.No-Buyer"), (Player) sender, displayName, AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
			else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Admin.No-Buyer"), displayName, AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
		}
		
		if (seller.isOnline()) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Items-Returned"), seller.getPlayer(), displayName, AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
		
		Auction.giveItemStack(object, seller);
		
		AuctionStorm.logger.setRefunded(this, identifier);
	}
	
	public void returnItemToSeller(CommandSender sender) {
		if (refunded) {
			if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Already-Refunded"), (Player) sender, seller.getName(), AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
			else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Admin.Already-Refunded"), seller.getName(), AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
			return;
		}
		
		if (seller.isOnline()) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Items-Returned"), seller.getPlayer(), seller.getPlayer().getDisplayName(), AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
		Auction.giveItemStack(object, seller);
		
		AuctionStorm.logger.setRefunded(this, identifier);
	}
	
	public void returnMoneyToBuyer(CommandSender sender) {
		if (refunded) {
			if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Already-Refunded"), (Player) sender, seller.getName(), AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
			else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Admin.Already-Refunded"), seller.getName(), AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
			return;
		}
		
		if (buyer != null) {
			if (buyer.isOnline()) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Money-Returned"), buyer.getPlayer(), seller.getPlayer().getDisplayName(), AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
			if (seller.isOnline()) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Money-Taken"), seller.getPlayer(), buyer.getPlayer().getDisplayName(), AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
			AuctionStorm.econ.depositPlayer(buyer, price);
			AuctionStorm.econ.withdrawPlayer(seller, price);
			AuctionStorm.logger.setRefunded(this, identifier);
		} else {
			if (sender instanceof Player) Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.No-Buyer"), (Player) sender, seller.getName(), AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
			else AuctionStorm.instance.messenger.print(AuctionStorm.instance.messenger.getMessage("Command.Admin.No-Buyer"), seller.getName(), AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
		}
		
	}
	
}
