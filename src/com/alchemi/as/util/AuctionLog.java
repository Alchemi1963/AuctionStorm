package com.alchemi.as.util;

import java.util.Map.Entry;

import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.alchemi.al.CarbonDating;
import com.alchemi.al.Messenger;
import com.alchemi.as.Auction;
import com.alchemi.as.AuctionStorm;

public class AuctionLog{

	private final Player seller;
	private Player buyer;
	private int price;
	private final ItemStack object;
	private final boolean refunded;
	private final CarbonDating identifier;
	
	public AuctionLog(Player seller, Player buyer, int price, ItemStack object) {
		this.seller = seller;
		this.buyer = buyer;
		this.price = price;
		this.object = object;
		this.refunded = false;
		this.identifier = CarbonDating.getCurrentDateTime();
		
	}
	
	public AuctionLog(String seller, String buyer, int price, ItemStack object, boolean refunded, CarbonDating id) {
		this.seller = AuctionStorm.instance.getServer().getPlayer(seller);
		if (buyer != null) this.buyer = AuctionStorm.instance.getServer().getPlayer(buyer);
		else this.buyer = null;
		this.price = price;
		this.object = object;
		this.refunded = refunded;
		this.identifier = id;
	}
	
	public String getBuyer() {
		return buyer != null ? buyer.getName() : null;
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
	
	public CarbonDating getIdentifier() {
		return identifier;
	}
	
	public boolean hasBeenRefunded() {
		return refunded;
	}
	
	public void setBuyer(Player buyer) {
		this.buyer = buyer;
	}
	
	public void setPrice(int price) {
		this.price = price;
	}
	
	public void getInfo(CommandSender sender) {
		String amountS = String.valueOf(object.getAmount());
		String priceS = String.valueOf(price);

		String msg = AuctionStorm.instance.messenger.getMessage("Auction.Info.LogHeader");
		if (Auction.getDisplayName(object) != null) msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.ItemNamed"), seller.getDisplayName(), "[SERVER]", amountS, Auction.getItemName(object), Auction.getDisplayName(object), priceS, AuctionStorm.valutaP);
		else msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Item"), seller.getDisplayName(), "[SERVER]", amountS, Auction.getItemName(object), Auction.getDisplayName(object), priceS, AuctionStorm.valutaP);
		msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Amount"), seller.getDisplayName(), "[SERVER]", amountS, Auction.getItemName(object), Auction.getDisplayName(object), priceS, AuctionStorm.valutaP);
		//show enchantments if present
		if (!object.getEnchantments().isEmpty()) {
			msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.EnchantmentHeader"), seller.getDisplayName(), "[SERVER]", amountS, Auction.getItemName(object), Auction.getDisplayName(object), priceS, AuctionStorm.valutaP);
			for (Entry<Enchantment, Integer> ench : object.getEnchantments().entrySet()) {
				msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Enchantment"), seller.getDisplayName(), "[SERVER]", RomanNumber.toRoman(ench.getValue()), Auction.getItemName(object), ench.getKey().getKey().getKey(), priceS, AuctionStorm.valutaP);
			}
		}
		msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Price"), seller.getDisplayName(), "[SERVER]", amountS, Auction.getItemName(object), Auction.getDisplayName(object), priceS, AuctionStorm.valutaP);
		if (buyer != null) msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Bidder"), buyer.getDisplayName(), "[SERVER]", amountS, Auction.getItemName(object), Auction.getDisplayName(object), priceS, AuctionStorm.valutaP);
		msg = msg + Messenger.parseVars(AuctionStorm.instance.messenger.getMessage("Auction.Info.Footer"), seller.getDisplayName(), "[SERVER]", amountS, Auction.getItemName(object), Auction.getDisplayName(object), priceS, AuctionStorm.valutaP);
		
		if (sender instanceof Player) {
			Messenger.sendMsg(msg, (Player) sender);
		} else {
			AuctionStorm.instance.messenger.print(msg, false);
		}
	}

	public void returnAll() {
		if (refunded) {
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Already-Refunded"), seller, seller.getDisplayName(), AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
			return;
		} else if (buyer != null) {
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Money-Returned"), buyer, seller.getDisplayName(), AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Money-Taken"), seller, buyer.getDisplayName(), AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
			AuctionStorm.econ.depositPlayer(buyer, price);
			AuctionStorm.econ.withdrawPlayer(seller, price);
			
		} else {
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.No-Buyer"), seller, seller.getDisplayName(), AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
		}
		
		Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Items-Returned"), seller, seller.getDisplayName(), AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
		seller.getInventory().addItem(object);
		
		AuctionStorm.logger.setRefunded(this, identifier);
	}
	
	public void returnItemToSeller() {
		if (refunded) {
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Already-Refunded"), seller, seller.getDisplayName(), AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
			return;
		}
		Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Items-Returned"), seller, seller.getDisplayName(), AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
		Auction.giveItemStack(object, seller);
		
		AuctionStorm.logger.setRefunded(this, identifier);
	}
	
	public void returnMoneyToBuyer() {
		if (refunded) {
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Already-Refunded"), seller, seller.getDisplayName(), AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
			return;
		}
		
		if (buyer != null) {
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Money-Returned"), buyer, seller.getDisplayName(), AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.Money-Taken"), seller, buyer.getDisplayName(), AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
			AuctionStorm.econ.depositPlayer(buyer, price);
			AuctionStorm.econ.withdrawPlayer(seller, price);
			AuctionStorm.logger.setRefunded(this, identifier);
		} else {
			Messenger.sendMsg(AuctionStorm.instance.messenger.getMessage("Command.Admin.No-Buyer"), seller, seller.getDisplayName(), AuctionStorm.instance.messenger.getTag(), String.valueOf(object.getAmount()), Auction.getDisplayName(object), String.valueOf(price));
		}
		
	}
	
}
