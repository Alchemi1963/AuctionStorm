package me.alchemi.as.objects.placeholder;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.alchemi.al.Library;
import me.alchemi.al.configurations.IMessage;
import me.alchemi.al.configurations.Messenger;
import me.alchemi.al.objects.placeholder.IStringParser;
import me.alchemi.as.objects.Messages;

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
		string = string.replace("%player%", player.getName());
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
	
	public StringParser sender(CommandSender sender) {
		string = string.replace("%sender%", sender.getName());
		return this;
	}
	
	public StringParser sender(Player sender) {
		string = string.replace("%sender%", sender.getName());
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
