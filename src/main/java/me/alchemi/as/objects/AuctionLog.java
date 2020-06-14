package me.alchemi.as.objects;

import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.alchemi.al.configurations.Messenger;
import me.alchemi.al.objects.handling.CarbonDating;
import me.alchemi.as.Auction;
import me.alchemi.as.Storm;
import me.alchemi.as.objects.placeholder.StringParser;

public class AuctionLog{

	private final OfflinePlayer seller;
	private OfflinePlayer buyer;
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
		
		if (buyer != null && buyer.isOnline()) buyerName = buyer.getDisplayName();
		else if (buyer != null) buyerName = this.buyer.getName();
		
	}
	
	public AuctionLog(String seller, String sellerID, String buyer, String buyerID, int price, ItemStack object, boolean refunded, CarbonDating id) {
		if (buyer != null) this.buyer = Storm.getInstance().getServer().getPlayer(buyer);
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
		
		if (Storm.getInstance().getServer().getPlayer(seller) == null && this.sellerID != null) this.seller = Storm.getInstance().getServer().getOfflinePlayer(this.sellerID);
		else if (Storm.getInstance().getServer().getPlayer(seller) != null) this.seller = Storm.getInstance().getServer().getPlayer(seller);
		else this.seller = null;
		if (this.buyer == null && this.buyerID != null) this.buyer = Storm.getInstance().getServer().getOfflinePlayer(this.buyerID);
		
		if (this.seller.isOnline()) seller = this.seller.getPlayer().getDisplayName();
		else seller = this.seller.getName();
		
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
		String msg = new StringParser(Messages.AUCTION_INFO_LOGHEADER).name(identifier.getCarbonDate()).create();
		
		if (object != null) msg = msg + new StringParser(Messages.AUCTION_INFO_ITEMNAMED)
			.player(seller.getName())
			.amount(object.getAmount())
			.item(object)
			.name(object)
			.price(price)
			.currencyPlural()
			.create();
		else msg = msg + new StringParser(Messages.AUCTION_INFO_ITEM)
			.player(seller.getName())
			.amount(object.getAmount())
			.item(object)
			.name(object)
			.price(price)
			.currencyPlural()
			.create();
		msg = msg + new StringParser(Messages.AUCTION_INFO_AMOUNT)
				.player(seller.getName())
				.amount(object.getAmount())
				.item(object)
				.name(object)
				.price(price)
				.currencyPlural()
				.create();
		
		//show enchantments if present
		if (!object.getEnchantments().isEmpty()) {
			msg = msg + new StringParser(Messages.AUCTION_INFO_ENCHANTMENTHEADER)
				.player(seller.getName())
				.amount(object.getAmount())
				.item(object)
				.name(object)
				.price(price)
				.currencyPlural()
				.create();
			for (Entry<Enchantment, Integer> ench : object.getEnchantments().entrySet()) {
				msg = msg + new StringParser(Messages.AUCTION_INFO_ENCHANTMENT)
					.player(seller.getName())
					.amount(RomanNumber.toRoman(ench.getValue()))
					.item(object)
					.name(ench.getKey().getKey().getKey())
					.price(price)
					.currencyPlural()
					.create();
			}
		}
		msg = msg + new StringParser(Messages.AUCTION_INFO_PRICE)
			.player(seller.getName())
			.amount(object.getAmount())
			.item(object)
			.name(object)
			.price(price)
			.currencyPlural()
			.create();
		if (buyer != null) msg = msg + new StringParser(Messages.AUCTION_INFO_BIDDER)
			.player(buyer.getPlayer().getName())
			.amount(object.getAmount())
			.item(object)
			.name(object)
			.price(price)
			.currencyPlural()
			.create();
		msg = msg + Messages.AUCTION_INFO_FOOTER.toString().substring(0, Messages.AUCTION_INFO_FOOTER.toString().length() - 1);
		
		if (sender instanceof Player) {
			sender.sendMessage(Messenger.formatString(msg));
		} else {
			Storm.getInstance().getMessenger().print( "\n" + msg, false);
		}
	}

	
	public void returnAll(CommandSender sender) {
		
		if (refunded) {
			String send = new StringParser(Messages.COMMAND_ADMIN_ALREADY_REFUNDED)					
					.player(seller.getName())
					.amount(object.getAmount())
					.item(object)
					.name(object)
					.price(price)
					.currencyPlural()
					.create();
				
			if (sender instanceof Player) Storm.getInstance().getMessenger().sendMessage(send, sender);
			else Storm.getInstance().getMessenger().print(send);
				
			return;
		} else if (buyer != null) {
			if (buyer.isOnline()) {
				String send = new StringParser(Messages.COMMAND_ADMIN_MONEY_RETURNED)
						.player(seller.getName())
						.amount(object.getAmount())
						.item(object)
						.name(object)
						.price(price)
						.currencyPlural()
						.create();
				Storm.getInstance().getMessenger().sendMessage(send, buyer.getPlayer());
			}
					
			if (seller.isOnline()) {
				String send = new StringParser(Messages.COMMAND_ADMIN_MONEY_TAKEN)
						.player(buyerName)
						.amount(object.getAmount())
						.item(object)
						.name(object)
						.price(price)
						.currencyPlural()
						.create();
				Storm.getInstance().getMessenger().sendMessage(send, seller.getPlayer());
			}
			Storm.getInstance().econ.depositPlayer(buyer, price);
			Storm.getInstance().econ.withdrawPlayer(seller, price);
			
			
		} else {
			String send = new StringParser(Messages.COMMAND_ADMIN_NO_BUYER)
					.player(seller.getName())
					.amount(object.getAmount())
					.item(object)
					.name(object)
					.price(price)
					.currencyPlural()
					.create();
			
		if (sender instanceof Player) Storm.getInstance().getMessenger().sendMessage(send, sender);
		else Storm.getInstance().getMessenger().print(send);
		}
		
		if (seller.isOnline()) {
			String send = new StringParser(Messages.COMMAND_ADMIN_ITEMS_RETURNED)
					.player(seller.getName())
					.amount(object.getAmount())
					.item(object)
					.name(object)
					.price(price)
					.currencyPlural()
					.create();
			Storm.getInstance().getMessenger().sendMessage(send, seller.getPlayer());
		}
		
		Auction.giveItemStack(object, seller);
		
		Storm.logger.setRefunded(this, identifier);
	}
	
	
	public void returnItemToSeller(CommandSender sender) {
		if (refunded) {
			String send = new StringParser(Messages.COMMAND_ADMIN_ALREADY_REFUNDED)
					.player(seller.getName())
					.amount(object.getAmount())
					.item(object)
					.name(object)
					.price(price)
					.currencyPlural()
					.create();
			
		if (sender instanceof Player) Storm.getInstance().getMessenger().sendMessage(send, sender);
		else Storm.getInstance().getMessenger().print(send);
			return;
		}
		
		if (seller.isOnline()) {
			String send = new StringParser(Messages.COMMAND_ADMIN_ITEMS_RETURNED)
					.player(seller.getName())
					.amount(object.getAmount())
					.item(object)
					.name(object)
					.price(price)
					.currencyPlural()
					.create();
			Storm.getInstance().getMessenger().sendMessage(send, seller.getPlayer());
		}
		Auction.giveItemStack(object, seller);
		
		Storm.logger.setRefunded(this, identifier);
	}
	
	
	public void returnMoneyToBuyer(CommandSender sender) {
		if (refunded) {
			String send = new StringParser(Messages.COMMAND_ADMIN_ALREADY_REFUNDED)
					.player(seller.getName())
					.amount(object.getAmount())
					.item(object)
					.name(object)
					.price(price)
					.currencyPlural()
					.create();
			
		if (sender instanceof Player) Storm.getInstance().getMessenger().sendMessage(send, sender);
		else Storm.getInstance().getMessenger().print(send);
			return;
		}
		
		if (buyer != null) {
			if (buyer.isOnline()) {
				String send = new StringParser(Messages.COMMAND_ADMIN_MONEY_TAKEN)
						.player(seller.getName())
						.amount(object.getAmount())
						.item(object)
						.name(object)
						.price(price)
						.currencyPlural()
						.create();
				Storm.getInstance().getMessenger().sendMessage(send, buyer.getPlayer());
			}
			if (seller.isOnline()) {
				String send = new StringParser(Messages.COMMAND_ADMIN_MONEY_TAKEN)
						.player(buyerName)
						.amount(object.getAmount())
						.item(object)
						.name(object)
						.price(price)
						.currencyPlural()
						.create();
				Storm.getInstance().getMessenger().sendMessage(send, seller.getPlayer());
			}
			Storm.getInstance().econ.depositPlayer(buyer, price);
			Storm.getInstance().econ.withdrawPlayer(seller, price);
			Storm.logger.setRefunded(this, identifier);
		} else {
			String send = new StringParser(Messages.COMMAND_ADMIN_NO_BUYER)
					.player(seller.getName())
					.amount(object.getAmount())
					.item(object)
					.name(object)
					.price(price)
					.currencyPlural()
					.create();
			
		if (sender instanceof Player) Storm.getInstance().getMessenger().sendMessage(send, sender);
		else Storm.getInstance().getMessenger().print(send);
		}
		
	}
	
}
