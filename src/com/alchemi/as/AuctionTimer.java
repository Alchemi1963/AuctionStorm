package com.alchemi.as;

public class AuctionTimer implements Runnable{

	public int time;
	
	public AuctionTimer(int duration) {
		
		
		time = duration;
	}
	
	@Override
	public void run() {
		
		if (time == 0) {
			AuctionStorm.instance.getServer().getScheduler().cancelTask(Queue.current_auction.task_id);
			Queue.current_auction.endAuction();
		} else if (time == 2) {
			AuctionStorm.instance.messenger.broadcast("&6Going once...", AuctionStorm.instance.pluginname);
		} else if (time == 1) {
			AuctionStorm.instance.messenger.broadcast("&6Going twice...", AuctionStorm.instance.pluginname);
		
		} else if (AuctionStorm.config.getIntegerList("Auction.Notify").contains(time)) {
			AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.Time.Notify"), null, null, String.valueOf(time));
			AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.Info.Get"));
		} else if (time == Queue.current_auction.getDuration()/2) {
			AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.Time.Halftime"), null, null, String.valueOf(time));
			AuctionStorm.instance.messenger.broadcast(AuctionStorm.instance.messenger.getMessage("Auction.Info.Get"));
		}
		
		time--;
		
	}

}
