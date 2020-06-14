package me.alchemi.as.listeners.commands;

import java.util.Arrays;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.alchemi.al.Library;
import me.alchemi.al.configurations.Messenger;
import me.alchemi.al.objects.meta.PersistentMeta;
import me.alchemi.as.Auction;
import me.alchemi.as.Queue;
import me.alchemi.as.Storm;
import me.alchemi.as.objects.Config;
import me.alchemi.as.objects.Messages;
import me.alchemi.as.objects.meta.CooldownMeta;
import me.alchemi.as.objects.meta.SilentMeta;
import me.alchemi.as.objects.placeholder.StringParser;


public class CommandPlayer implements CommandExecutor{

	public static final String start_usage = "&9/auc start <price> [amount] [increment] [duration]";
	public static final String bid_usage = "&9/bid [bid] [secret bid]";
	public static final String help_usage = "&9/auc help";
	public static final String info_usage = "&9/auc info";
	public static final String cancel_usage = "&9/auc cancel [id]";
	public static final String queue_usage = "&9/auc listqueue [page]";
	
	public static final String start_desc = "&9Start an auction using the item in hand.";
	public static final String bid_desc = "&9Bid on the current auction.";
	public static final String help_desc = "&9Display the AuctionStorm help page.";
	public static final String info_desc = "&9Get info about the current auction.";
	public static final String cancel_desc = "&9Cancel the current auction.";
	public static final String queue_desc = "&9List the current queue.";
	
	private static final String help_message = "&6---------- AuctionStorm Help ----------\n"
			+ start_usage + "&6\n    " + start_desc + "\n"
			+ help_usage + "\n     Display this page.\n"
			+ bid_usage + "&6\n    " + bid_desc +  "\n"
			+ info_usage + "&6\n    " + info_desc + "\n"
			+ cancel_usage + "&6\n    " + cancel_desc + "\n"
			+ queue_usage + "&6\n    " + queue_desc + "\n"
			+ "&6------------------------------------";
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		if (Storm.hasPermission(sender, "as.base") && sender instanceof Player && cmd.getName().equals("auc")) {
			Player player = (Player) sender;
						
			if (args.length > 0) {
				
				if (args[0].equalsIgnoreCase("start") && Config.AuctionOptions.COOLDOWN.asInt() > 0 
						&& !player.hasPermission("as.cooldown.bypass")
						&& player.hasMetadata(CooldownMeta.key) 
						&& !Storm.getMetadata(CooldownMeta.class, CooldownMeta.key, player).isCooldownOver()) {
					
					Storm.getInstance().getMessenger().sendMessage(new StringParser(Messages.AUCTION_STARTCOOLDOWN)
							.amount(Math.round(Storm.getMetadata(CooldownMeta.class, CooldownMeta.key, player).remainingTicks()/20.0F))
							,sender);
					return true;
					
				} else if (args[0].equalsIgnoreCase("help") || args[0].equals("?")) { //help command
				
					player.sendMessage(Messenger.formatString(Library.getParser().parse(player, help_message)));
					return true;
					
				} else if (args[0].equalsIgnoreCase("start") && args.length < 2 || args[0].equalsIgnoreCase("s")  && args.length < 2) { 
					
					StringParser send = new StringParser(Messages.COMMAND_WRONG_FORMAT)
							.sender(cmd.getName())
							.format(start_usage)
							.player((player).getDisplayName());
					
					Storm.getInstance().getMessenger().sendMessage(send, sender);
					return true;
					
				} else if (args.length >= 1) { 
					
					if (args[0].equalsIgnoreCase("bid")) { //bid command
					
						if (Queue.current_auction == null) {
							Auction.noAuction(player);
							return true;
						}
						
						if (args.length == 1) {
							Queue.current_auction.bid(player);
							return true;
						}
						
						try {
							if (args.length >= 2 && args[1] != "0") Queue.current_auction.bid(Integer.valueOf(args[0]), player);
							else {
								StringParser send = new StringParser(Messages.COMMAND_WRONG_FORMAT)
										.sender(cmd.getName())
										.format(bid_usage)
										.player((player).getDisplayName());
								
								Storm.getInstance().getMessenger().sendMessage(send, sender);
								return true;
							}
							
							if (args.length == 3 && args[2] != "0") Queue.current_auction.bid(Integer.valueOf(args[1]), player, true);
						
						} catch(NumberFormatException e) {
							StringParser send = new StringParser(Messages.COMMAND_WRONG_FORMAT)
									.sender(cmd.getName())
									.format(bid_usage)
									.player((player).getDisplayName());
							
							Storm.getInstance().getMessenger().sendMessage(send, sender);
							
						}
						return true;
					
					} else if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("i")) { //info command
						if (Queue.current_auction != null) player.sendMessage(Messenger.formatString(Queue.current_auction.getInfo(true)));
						else Auction.noAuction(player);
						
						return true;
					
					} else if (args[0].equalsIgnoreCase("cancel") || args[0].equalsIgnoreCase("c")) { //cancel command
						
						if (args.length > 1) {
							try {
								int id = Integer.valueOf(args[1]);
								if (id >= Queue.getQueueLength() ) {
									Storm.getInstance().getMessenger().sendMessage(new StringParser(Messages.AUCTION_QUEUE_NOTAUCTION).id(args[1]), sender);
									return true;
								}
								
								if (player.equals(Queue.getQueue().get(id).getSeller()) || player.isOp() || player.hasPermission("as.cancel")) {
									String reason = "";
									if (args.length >= 3) {
										for (int x = 2 ; x < args.length; x++) {
											if (reason != "") reason = reason + " " + args[x];
											else reason = args[x];
										}
									}
									Queue.cancelAuction(id, player, reason);
								}
								return true;
								
							} catch (NumberFormatException e) {
								
								
								String reason = "";
								for (int x = 1 ; x < args.length; x++) {
									if (reason != "") reason = reason + " " + args[x];
									else reason = args[x];
								}
								
								if (Queue.current_auction == null) {
									Auction.noAuction(player);
									return true;
								}
								
								if (player.equals(Queue.current_auction.getSeller())) {
									Queue.current_auction.forceEndAuction(reason);
									return true;
									
								} else if (Storm.hasPermission(sender, "as.cancel")) {
									Queue.current_auction.forceEndAuction(reason, player);
									return true;
									
								} else {
									if (sender instanceof Player) {
										StringParser send = new StringParser(Messages.COMMAND_NO_PERMISSION)
												.sender(cmd.getName())
												.player((player).getDisplayName());
										
										Storm.getInstance().getMessenger().sendMessage(send, sender);
									}
									return true;
								}
								
							}
							
						} else {
							
							if (Queue.current_auction == null) {
								Auction.noAuction(player);
								return true;
							}
							
							if (player.equals(Queue.current_auction.getSeller())) {
								Queue.current_auction.forceEndAuction();
								return true;
								
							} else if (Storm.hasPermission(sender, "as.cancel")) {
								Queue.current_auction.forceEndAuction("", player);
								return true;
								
							} else {
								if (sender instanceof Player) {
									StringParser send = new StringParser(Messages.COMMAND_NO_PERMISSION)
											.sender(cmd.getName())
											.player((player).getDisplayName());
									
									Storm.getInstance().getMessenger().sendMessage(send, sender);
								}
								return true;
							}
						}
						
					} else if (args[0].equalsIgnoreCase("end") 
							|| args[0].equalsIgnoreCase("e")) { //end command
						
						if (Queue.current_auction == null) {
							Auction.noAuction(player);
							return true;
						}
						
						if (player.equals(Queue.current_auction.getSeller())) {
							Queue.current_auction.endAuction();;
							return true;
							
						} else if (Storm.hasPermission(sender, "as.end")) {
							Queue.current_auction.endAuction();
							return true;
							
						} else {
							if (sender instanceof Player) {
								StringParser send = new StringParser(Messages.COMMAND_NO_PERMISSION)
										.sender(cmd.getName())
										.player((player).getDisplayName());
								
								Storm.getInstance().getMessenger().sendMessage(send, sender);
							}
							return true;
						}
						
					} else if (args[0].equalsIgnoreCase("listQueue") 
							|| args[0].equalsIgnoreCase("lq") 
							|| args[0].equalsIgnoreCase("queue")) { //listqueue command
						
						int page = 1;
						if (args.length == 2) {
							try{
								page = Integer.valueOf(args[1]);
							} catch (Exception e) {page = 1;}
						}
						
						if (Queue.getQueueLength() >= 2) {
							
							int pages = 1;
							if (Queue.getQueueLength() > 10) {
								Float div = Float.valueOf(Queue.getQueueLength())/10.0F;
								Integer i = Integer.valueOf(String.valueOf(div).replaceFirst("\\..*", ""));
								pages = i < div ? i+1 : i; 
							}
							
							String msg = new StringParser(Messages.AUCTION_QUEUE_HEADER)
									.amount(page)
									.total(pages)
									.create();
							int i = 1;
							int i2 = 10*(page-1);
							for (Auction a : Queue.getQueue()) {
								if (a.equals(Queue.current_auction)) continue;
								
								else if (i > i2 && i <= 10 * page) msg = msg + new StringParser(Messages.AUCTION_QUEUE_AUCTION)
										.id(i)
										.amount(a.getObject().getAmount())
										.item(a.getObject())
										.seller(a.getSeller())
										.create();
								i++;
							}
							msg = msg + new StringParser(Messages.AUCTION_QUEUE_FOOTER)
									.amount(page)
									.total(pages)
									.create();
							
							Storm.getInstance().getMessenger().sendMessage(msg, sender);
							return true;
						} else {
							Storm.getInstance().getMessenger().sendMessage(Messages.AUCTION_QUEUE_EMPTY, sender);
							return true;
						}
						
					} else if (Arrays.asList("hide", "hidebroadcasts", "silence").contains(args[0]) 
							&& Storm.getInstance().permsEnabled()
							&& player.hasPermission("as.togglesilence")) { //silence command
						
						if (PersistentMeta.hasMeta(player, SilentMeta.class) && PersistentMeta.getMeta(player, SilentMeta.class).asBoolean()) {//player.hasPermission("as.silence")) {
							//main.perm.playerRemove(null, player, "as.silence");
							PersistentMeta.setMeta(player, new SilentMeta(false));
							Storm.getInstance().getMessenger().sendMessage(Messages.COMMAND_UNSILENCED, player);
						} else {
							//main.perm.playerAdd(null, player, "as.silence");
							PersistentMeta.setMeta(player, new SilentMeta(true));
							Storm.getInstance().getMessenger().sendMessage(Messages.COMMAND_SILENCED, player);
						}
						
						return true;
					
					} else if (args.length >= 2 && args[0].equalsIgnoreCase("start") 
						|| args.length >= 2 && args[0].equalsIgnoreCase("s")) { //start command
						
						if (!player.hasPermission("as.cooldown.bypass")) {
							player.removeMetadata(CooldownMeta.key, Storm.getInstance());
						}
						
						int price = Config.AuctionOptions.START_DEFAULTS_PRICE.asInt();
						int amount = player.getInventory().getItemInMainHand().getAmount();
						int increment = Config.AuctionOptions.START_DEFAULTS_INCREMENT.asInt();
						int duration = Config.AuctionOptions.START_DEFAULTS_DURATION.asInt();
						
						try {
							price = Integer.valueOf(args[1]);
							if (args.length >= 4) increment = Integer.valueOf(args[3]);
							if (args.length == 5) duration = Integer.valueOf(args[4]);
						
						} catch(NumberFormatException e) {
							StringParser send = new StringParser(Messages.COMMAND_WRONG_FORMAT)
									.sender(cmd.getName())
									.format(start_usage)
									.player((player).getDisplayName());
							
							Storm.getInstance().getMessenger().sendMessage(send, sender);
						}
						
						try {
							if (args.length >= 3) {
								amount = Integer.valueOf(args[2]);
							}
						} catch (Exception e){
							if (args.length >= 3 && args[2].equalsIgnoreCase("all")) {
								amount = scanInventory(player.getInventory(), player.getInventory().getItemInMainHand());
							} else if (args.length >= 3 && args[2].equalsIgnoreCase("hand")) {
								amount = player.getInventory().getItemInMainHand().getAmount();
							}
						}
						
						new Auction(player, price, duration, amount, increment);
						return true;					
					}
				}  
			}
			StringParser send = new StringParser(Messages.COMMAND_UNKNOWN)
					.sender(cmd.getName())
					.player((player).getDisplayName());
			
			Storm.getInstance().getMessenger().sendMessage(send, sender);
			return true;
		}
		if (sender instanceof Player) {
			StringParser send = new StringParser(Messages.COMMAND_NO_PERMISSION)
					.sender(cmd.getName())
					.player(((Player) sender).getDisplayName());
			
			Storm.getInstance().getMessenger().sendMessage(send, sender);
		}
		return true;
	}

	public static int scanInventory(PlayerInventory inventory, ItemStack itemInMainHand) {
		int size = 0;
		
		for (ItemStack stack : inventory) {
			if (stack == null) continue;
			if (stack.isSimilar(itemInMainHand)) {
				size += stack.getAmount();
			}
		}
		
		return size;
	}

	
	
}
