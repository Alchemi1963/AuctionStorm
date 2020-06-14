package me.alchemi.as.objects;

import org.bukkit.Bukkit;

import me.alchemi.as.Queue;
import me.alchemi.as.Storm;
import me.alchemi.as.objects.placeholder.StringParser;

public class AuctionTimer implements Runnable{

	public int time;
	private int duration;
	
	public AuctionTimer(int duration) {
		
		
		time = duration;
		this.duration = duration;
	}
	
	@Override
	public void run() {
		
		if (time == duration);
		else if (time == 0) {
			Bukkit.getScheduler().cancelTask(Queue.current_auction.task_id);
			Queue.current_auction.endAuction();
			
		} else if (time == 2) {
			Storm.getInstance().getMessenger().broadcast("&6Going once...");
		} else if (time == 1) {
			Storm.getInstance().getMessenger().broadcast("&6Going twice...");
		
		} else if (Config.AuctionOptions.NOTIFY.asBoolean() && Config.AuctionOptions.NOTIFYTIMES.asIntList().contains(time)) { //custom time notification
			
			Storm.getInstance().getMessenger().broadcast(new StringParser(Messages.AUCTION_TIME_NOTIFY).amount(String.valueOf(time)));
			
			if (!Config.AuctionOptions.HOVERITEM.asBoolean()) {
				Storm.getInstance().getMessenger().broadcast(Messages.AUCTION_INFO_GET.value());
			} else {
				if (!Config.AuctionOptions.HOVERITEMMINECRAFTTOOLTIP.asBoolean()) Storm.getInstance().getMessenger().broadcastHover(Messages.AUCTION_INFO_GET.value(), Queue.current_auction.getInfo(false));
				else ((AuctionMessenger)Storm.getInstance().getMessenger()).broadcastITEM(Messages.AUCTION_INFO_GET.value(), Queue.current_auction.getObject());
			}
		} 
		else if (Config.AuctionOptions.NOTIFY.asBoolean() && time == Queue.current_auction.getDuration()/2) { //half-time notification
			
			Storm.getInstance().getMessenger().broadcast(new StringParser(Messages.AUCTION_TIME_HALFTIME).amount(String.valueOf(time))); 
			
			
			if (!Config.AuctionOptions.HOVERITEM.asBoolean()) {
				Storm.getInstance().getMessenger().broadcast(Messages.AUCTION_INFO_GET.value());
			} else {
				if (!Config.AuctionOptions.HOVERITEMMINECRAFTTOOLTIP.asBoolean()) Storm.getInstance().getMessenger().broadcastHover(Messages.AUCTION_INFO_GET.value(), Queue.current_auction.getInfo(false));
				else ((AuctionMessenger)Storm.getInstance().getMessenger()).broadcastITEM(Messages.AUCTION_INFO_GET.value(), Queue.current_auction.getObject());
			}
		}
		
		time--;
		
	}

}
