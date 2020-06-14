package me.alchemi.as.objects.placeholder;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.alchemi.al.Library;
import me.alchemi.al.configurations.IMessage;
import me.alchemi.al.configurations.Messenger;
import me.alchemi.al.objects.placeholder.IStringParser;
import me.alchemi.as.Auction;
import me.alchemi.as.objects.Messages;
import me.alchemi.as.objects.Config.Vault;

public class StringParser implements IStringParser{

private String string;
	
	public StringParser(String initialString) {
		string = initialString;
	}
	
	public StringParser(Messages message) {
		string = message.value();
	}
	
	@Override
	public StringParser command(String command) {
		string = string.replace("%command%", command);
		return this;
	}

	@Override
	public StringParser player(Player player) {
		string = string.replace("%player%", player.getDisplayName());
		return this;
	}
	
	@Override	
	public StringParser player(String player) {
		string = string.replace("%player%", player);
		return this;
	}
	
	@Override
	public StringParser amount(int amount) {
		string = string.replace("%amount%", String.valueOf(amount));
		return this;
	}
	
	public StringParser amount(String amount) {
		string = string.replace("%amount%", amount);
		return this;
	}
	
	public StringParser sender(Player sender) {
		string = string.replace("%sender%", sender.getDisplayName());
		return this;
	}
	
	public StringParser sender(String sender) {
		string = string.replace("%sender%", sender);
		return this;
	}
	
	public StringParser item(ItemStack item) {
		string = string.replace("%item%", Auction.getItemName(item));
		return this;
	}
	
	public StringParser name(ItemStack namedItem) {
		string = string.replace("%name%", Auction.getDisplayName(namedItem));
		return this;
	}

	public StringParser name(String name) {
		string = string.replace("%name%", name);
		return this;
	}
	
	public StringParser price(int price) {
		string = string.replace("%price%", String.valueOf(price));
		return this;
	}
	
	public StringParser currencySingular() {
		string = string.replace("%currency%", Vault.CURRENCY_SINGULAR.asString());
		return this;
	}
	
	public StringParser currencyPlural() {
		string = string.replace("%currency%", Vault.CURRENCY_PLURAL.asString());
		return this;
	}
	
	public StringParser duration(int duration) {
		string = string.replace("%duration%", String.valueOf(duration));
		return this;
	}

	public StringParser incr(int incr) {
		string = string.replace("%incr%", String.valueOf(incr));
		return this;
	}
	
	public StringParser reason(String reason) {
		string = string.replace("%reason%", reason);
		return this;
	}
	
	public StringParser durability(int durability) {
		string = string.replace("%durability%", String.valueOf(durability));
		return this;
	}
	
	public StringParser id(String id) {
		string = string.replace("%id%", id);
		return this;
	}
	
	public StringParser id(int id) {
		string = string.replace("%id%", String.valueOf(id));
		return this;
	}
	
	public StringParser format(String format) {
		string = string.replace("%format%", format);
		return this;
	}

	public StringParser total(int total) {
		string = string.replace("%total%", String.valueOf(total));
		return this;
	}
	
	public StringParser seller(Player seller) {
		string = string.replace("%seller%", seller.getDisplayName());
		return this;
	}
	
	@Override
	public StringParser parse(Player player) {
		string = Library.getParser().parse(player, string);
		return this;
	}
	
	@Override
	public StringParser parse(OfflinePlayer player) {
		string = Library.getParser().parse(player, string);
		return this;
	}
	
	@Override
	public StringParser parse(CommandSender sender) {
		string = Library.getParser().parse(sender, string);
		return this;
	}

	@Override
	public String create() {
		return Messenger.formatString(string);
	}

	@Override
	public IStringParser message(IMessage message) {
		string = message.value();
		return this;
	}

	
}
