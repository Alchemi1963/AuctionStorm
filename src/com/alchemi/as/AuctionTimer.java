package com.alchemi.as;

import com.alchemi.al.Library;
import com.alchemi.as.cmds.Commando;

public class AuctionTimer implements Runnable{

	public int time;
	
	public AuctionTimer(int duration) {
		
		
		time = duration;
	}
	
	@Override
	public void run() {
		
		if (time == 0) {
			AuctionStorm.instance.getServer().getScheduler().cancelTask(AuctionStorm.instance.current_auction.task_id);
			AuctionStorm.instance.current_auction.endAuction();
		} else if (time == 2) {
			Library.broadcast("&6Going once...", AuctionStorm.instance.pluginname);
		} else if (time == 1) {
			Library.broadcast("&6Going twice...", AuctionStorm.instance.pluginname);
		}
		
		else if (time == 120) {
			Library.broadcast("&92 minutes remaining on the current auction.", AuctionStorm.instance.pluginname);
			Library.broadcast("&6Use " + Commando.info_usage + "&6 to get information.", AuctionStorm.instance.pluginname);
		} else if (time == 60) {
			Library.broadcast("&91 minute remaining on the current auction.", AuctionStorm.instance.pluginname);
			Library.broadcast("&6Use " + Commando.info_usage + "&6 to get information.", AuctionStorm.instance.pluginname);
		}
		
		time--;
		
	}

}
