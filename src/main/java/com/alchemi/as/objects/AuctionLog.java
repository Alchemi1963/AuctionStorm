package com.alchemi.as.objects;

import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.alchemi.al.configurations.Messenger;
import com.alchemi.al.objects.handling.CarbonDating;
import com.alchemi.as.Auction;
import com.alchemi.as.main;

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
		if (buyer != null) this.buyer = main.getInstance().getServer().getPlayer(buyer);
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
		
		if (main.getInstance().getServer().getPlayer(seller) == null && this.sellerID != null) this.seller = main.getInstance().getServer().getOfflinePlayer(this.sellerID);
		else if (main.getInstance().getServer().getPlayer(seller) != null) this.seller = main.getInstance().getServer().getPlayer(seller);
		else this.seller = null;
		if (this.buyer == null && this.buyerID != null) this.buyer = main.getInstance().getServer().getOfflinePlayer(this.buyerID);
		
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
	
	
	public void getInfo(CommandSender sender) {
		String amountS = String.valueOf(object.getAmount());
		String priceS = String.valueOf(price);

		String msg = Config.MESSAGES.AUCTION_INFO_LOGHEADER.value().replace("$name$", identifier.getCarbonDate());
		
		if (Auction.getDisplayName(object) != null) msg = msg + Config.MESSAGES.AUCTION_INFO_ITEMNAMED.value()
			.replace("$player$", sellerName)
			.replace("$amount$", amountS)
			.replace("$item$", Auction.getItemName(object))
			.replace("$name$", Auction.getDisplayName(object))
			.replace("$price$", priceS)
			.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString());
		else msg = msg + Config.MESSAGES.AUCTION_INFO_ITEM.value()
			.replace("$player$", sellerName)
			.replace("$amount$", amountS)
			.replace("$item$", Auction.getItemName(object))
			.replace("$name$", Auction.getDisplayName(object))
			.replace("$price$", priceS)
			.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString());
		msg = msg + Config.MESSAGES.AUCTION_INFO_AMOUNT.value()
				.replace("$player$", sellerName)
				.replace("$amount$", amountS)
				.replace("$item$", Auction.getItemName(object))
				.replace("$name$", Auction.getDisplayName(object))
				.replace("$price$", priceS)
				.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString());
		
		//show enchantments if present
		if (!object.getEnchantments().isEmpty()) {
			msg = msg + Config.MESSAGES.AUCTION_INFO_ENCHANTMENTHEADER.value()
				.replace("$player$", sellerName)
				.replace("$amount$", amountS)
				.replace("$item$", Auction.getItemName(object))
				.replace("$name$", Auction.getDisplayName(object))
				.replace("$price$", priceS)
				.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString());
			for (Entry<Enchantment, Integer> ench : object.getEnchantments().entrySet()) {
				msg = msg + Config.MESSAGES.AUCTION_INFO_ENCHANTMENT.value()
					.replace("$player$", sellerName)
					.replace("$amount$", RomanNumber.toRoman(ench.getValue()))
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", ench.getKey().getKey().getKey())
					.replace("$price$", priceS)
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString());
			}
		}
		msg = msg + Config.MESSAGES.AUCTION_INFO_PRICE.value()
			.replace("$player$", sellerName)
			.replace("$amount$", amountS)
			.replace("$item$", Auction.getItemName(object))
			.replace("$name$", Auction.getDisplayName(object))
			.replace("$price$", priceS)
			.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString());
		if (buyer != null) msg = msg + Config.MESSAGES.AUCTION_INFO_BIDDER.value()
			.replace("$player$", buyer.getPlayer().getDisplayName())
			.replace("$amount$", amountS)
			.replace("$item$", Auction.getItemName(object))
			.replace("$name$", Auction.getDisplayName(object))
			.replace("$price$", priceS)
			.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString());
		msg = msg + Config.MESSAGES.AUCTION_INFO_FOOTER.value().substring(0, Config.MESSAGES.AUCTION_INFO_FOOTER.value().length() - 1);
		
		if (sender instanceof Player) {
			sender.sendMessage(Messenger.cc(msg));
		} else {
			main.getInstance().getMessenger().print( "\n" + msg, false);
		}
	}

	
	public void returnAll(CommandSender sender) {
		
		if (refunded) {
			String send = Config.MESSAGES.COMMAND_ADMIN_ALREADY_REFUNDED.value()
						.replace("$player$", sellerName)
						.replace("$amount$", String.valueOf(object.getAmount()))
						.replace("$item$", Auction.getItemName(object))
						.replace("$name$", Auction.getDisplayName(object))
						.replace("$price$", String.valueOf(price))
						.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString());
				
			if (sender instanceof Player) main.getInstance().getMessenger().sendMessage(send, sender);
			else main.getInstance().getMessenger().print(send);
				
			return;
		} else if (buyer != null) {
			if (buyer.isOnline()) {
				String send = Config.MESSAGES.COMMAND_ADMIN_MONEY_RETURNED.value()
						.replace("$player$", sellerName)
						.replace("$amount$", String.valueOf(object.getAmount()))
						.replace("$item$", Auction.getItemName(object))
						.replace("$name$", Auction.getDisplayName(object))
						.replace("$price$", String.valueOf(price))
						.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString());
				main.getInstance().getMessenger().sendMessage(send, buyer.getPlayer());
			}
					
			if (seller.isOnline()) {
				String send = Config.MESSAGES.COMMAND_ADMIN_MONEY_TAKEN.value()
						.replace("$player$", buyerName)
						.replace("$amount$", String.valueOf(object.getAmount()))
						.replace("$item$", Auction.getItemName(object))
						.replace("$name$", Auction.getDisplayName(object))
						.replace("$price$", String.valueOf(price))
						.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString());
				main.getInstance().getMessenger().sendMessage(send, seller.getPlayer());
			}
			main.getInstance().econ.depositPlayer(buyer, price);
			main.getInstance().econ.withdrawPlayer(seller, price);
			
			
		} else {
			String send = Config.MESSAGES.COMMAND_ADMIN_NO_BUYER.value()
					.replace("$player$", sellerName)
					.replace("$amount$", String.valueOf(object.getAmount()))
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", String.valueOf(price))
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString());
			
		if (sender instanceof Player) main.getInstance().getMessenger().sendMessage(send, sender);
		else main.getInstance().getMessenger().print(send);
		}
		
		if (seller.isOnline()) {
			String send = Config.MESSAGES.COMMAND_ADMIN_ITEMS_RETURNED.value()
					.replace("$player$", sellerName)
					.replace("$amount$", String.valueOf(object.getAmount()))
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", String.valueOf(price))
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString());
			main.getInstance().getMessenger().sendMessage(send, seller.getPlayer());
		}
		
		Auction.giveItemStack(object, seller);
		
		main.logger.setRefunded(this, identifier);
	}
	
	
	public void returnItemToSeller(CommandSender sender) {
		if (refunded) {
			String send = Config.MESSAGES.COMMAND_ADMIN_ALREADY_REFUNDED.value()
					.replace("$player$", sellerName)
					.replace("$amount$", String.valueOf(object.getAmount()))
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", String.valueOf(price))
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString());
			
		if (sender instanceof Player) main.getInstance().getMessenger().sendMessage(send, sender);
		else main.getInstance().getMessenger().print(send);
			return;
		}
		
		if (seller.isOnline()) {
			String send = Config.MESSAGES.COMMAND_ADMIN_ITEMS_RETURNED.value()
					.replace("$player$", sellerName)
					.replace("$amount$", String.valueOf(object.getAmount()))
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", String.valueOf(price))
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString());
			main.getInstance().getMessenger().sendMessage(send, seller.getPlayer());
		}
		Auction.giveItemStack(object, seller);
		
		main.logger.setRefunded(this, identifier);
	}
	
	
	public void returnMoneyToBuyer(CommandSender sender) {
		if (refunded) {
			String send = Config.MESSAGES.COMMAND_ADMIN_ALREADY_REFUNDED.value()
					.replace("$player$", sellerName)
					.replace("$amount$", String.valueOf(object.getAmount()))
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", String.valueOf(price))
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString());
			
		if (sender instanceof Player) main.getInstance().getMessenger().sendMessage(send, sender);
		else main.getInstance().getMessenger().print(send);
			return;
		}
		
		if (buyer != null) {
			if (buyer.isOnline()) {
				String send = Config.MESSAGES.COMMAND_ADMIN_MONEY_TAKEN.value()
						.replace("$player$", sellerName)
						.replace("$amount$", String.valueOf(object.getAmount()))
						.replace("$item$", Auction.getItemName(object))
						.replace("$name$", Auction.getDisplayName(object))
						.replace("$price$", String.valueOf(price))
						.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString());
				main.getInstance().getMessenger().sendMessage(send, buyer.getPlayer());
			}
			if (seller.isOnline()) {
				String send = Config.MESSAGES.COMMAND_ADMIN_MONEY_TAKEN.value()
						.replace("$player$", buyerName)
						.replace("$amount$", String.valueOf(object.getAmount()))
						.replace("$item$", Auction.getItemName(object))
						.replace("$name$", Auction.getDisplayName(object))
						.replace("$price$", String.valueOf(price))
						.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString());
				main.getInstance().getMessenger().sendMessage(send, seller.getPlayer());
			}
			main.getInstance().econ.depositPlayer(buyer, price);
			main.getInstance().econ.withdrawPlayer(seller, price);
			main.logger.setRefunded(this, identifier);
		} else {
			String send = Config.MESSAGES.COMMAND_ADMIN_NO_BUYER.value()
					.replace("$player$", sellerName)
					.replace("$amount$", String.valueOf(object.getAmount()))
					.replace("$item$", Auction.getItemName(object))
					.replace("$name$", Auction.getDisplayName(object))
					.replace("$price$", String.valueOf(price))
					.replace("$valuta$", Config.VAULT.VALUTA_PLURAL.asString());
			
		if (sender instanceof Player) main.getInstance().getMessenger().sendMessage(send, sender);
		else main.getInstance().getMessenger().print(send);
		}
		
	}
	
}
