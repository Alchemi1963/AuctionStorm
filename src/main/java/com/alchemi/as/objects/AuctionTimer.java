package com.alchemi.as.objects;

import org.bukkit.Bukkit;

import com.alchemi.as.Queue;
import com.alchemi.as.main;

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
			main.messenger.broadcast("&6Going once...");
		} else if (time == 1) {
			main.messenger.broadcast("&6Going twice...");
		
		} else if (Config.AUCTION.NOTIFY.asIntList().contains(time)) { //custom time notification
			
			main.messenger.broadcast(Config.MESSAGES.AUCTION_TIME_NOTIFY.value().replace("$amount$", String.valueOf(time)));
			
			if (!Config.AUCTION.HOVERITEM.asBoolean()) {
				main.messenger.broadcast(Config.MESSAGES.AUCTION_INFO_GET.value());
			} else {
				main.messenger.broadcastHover(Config.MESSAGES.AUCTION_INFO_GET.value(), Queue.current_auction.getInfo(false));
			}
		} 
		else if (time == Queue.current_auction.getDuration()/2) { //half-time notification
			
			main.messenger.broadcast(Config.MESSAGES.AUCTION_TIME_HALFTIME.value().replace("$amount$", String.valueOf(time))); 
			
			
			if (!Config.AUCTION.HOVERITEM.asBoolean()) {
				main.messenger.broadcast(Config.MESSAGES.AUCTION_INFO_GET.value());
			} else {
				main.messenger.broadcastHover(Config.MESSAGES.AUCTION_INFO_GET.value(), Queue.current_auction.getInfo(false));
			}
		}
		
		time--;
		
	}

}
