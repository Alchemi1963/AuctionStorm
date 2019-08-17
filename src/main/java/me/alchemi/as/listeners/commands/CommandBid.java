package me.alchemi.as.listeners.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.alchemi.as.Auction;
import me.alchemi.as.Queue;
import me.alchemi.as.Storm;
import me.alchemi.as.objects.Config;

public class CommandBid implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (Storm.hasPermission(sender, "as.base") && sender instanceof Player && cmd.getName().equals("bid")) {
			
			if (Queue.current_auction == null) {
				Auction.noAuction((Player) sender);
				return true;
			}
			
			if (args.length == 0) {
				Queue.current_auction.bid((Player) sender);
				return true;
			}
			
			try {
				if (args.length >= 1 && args[0] != "0") Queue.current_auction.bid(Integer.valueOf(args[0]), (Player) sender);
				else {
					String send = Config.MESSAGES.COMMAND_WRONG_FORMAT.value().replace("$sender$", cmd.getName()).replace("$format$", CommandPlayer.bid_usage).replace("$player$", ((Player) sender).getDisplayName());
					
					Storm.getInstance().getMessenger().sendMessage(send, sender);
				}
				
				if (args.length == 2 && args[1] != "0") Queue.current_auction.bid(Integer.valueOf(args[1]), (Player) sender, true);
			} catch(NumberFormatException e) {
				
				String send = Config.MESSAGES.COMMAND_WRONG_FORMAT.value().replace("$sender$", cmd.getName()).replace("$format$", CommandPlayer.bid_usage).replace("$player$", ((Player) sender).getDisplayName());
				
				Storm.getInstance().getMessenger().sendMessage(send, sender);
				
			}
			
			return true;
		}
		if (sender instanceof Player) {
			String send = Config.MESSAGES.COMMAND_NO_PERMISSION.value().replace("$sender$", cmd.getName()).replace("$player$", ((Player) sender).getDisplayName());
			
			Storm.getInstance().getMessenger().sendMessage(send, sender);
		}
		return true;
	}

}
