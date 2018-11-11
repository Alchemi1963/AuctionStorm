package com.alchemi.as;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class Queue {

	private static ArrayList<Auction> queue = new ArrayList<Auction>();
	public static Auction current_auction;
	
	public static void addAuction(Auction a) { 
		
		queue.add(a);
		if (getQueueLength() == 1) {
			current_auction = a;
			a.startAuction();
		}
	}
	
	public static void clearQueue(boolean returnItems) { clearQueue(returnItems, null, ""); }
	
	public static void clearQueue(boolean returnItems, Player ender) { clearQueue(returnItems, ender, ""); }
	
	public static void clearQueue(boolean returnItems, Player ender, String reason) {
		if (returnItems) {
			
			for (Auction a : queue) {
				a.forceEndAuction(reason, ender, true);
			}
			
		}
		queue.clear();
	}
	
	public static void clearQueue(boolean returnItems, String reason) {
		if (returnItems) {
			
			for (Auction a : queue) {
				a.forceEndAuction(reason, null, true);
			}
			
		}
		queue.clear();
	}
	
	public static void printQueue() { 
		
		for (Auction a : queue) {
			AuctionStorm.instance.messenger.broadcast(Auction.getItemName(a.getObject()));
		}
		
	}
	
	public static int getQueueLength() {
		return queue.size();
	}
	
	public static void removeAuction(Auction a) {
		
		queue.remove(a);
		
	}
	
	public static void nextAuction() {
		
		try { AuctionStorm.instance.getServer().getScheduler().cancelTask(current_auction.task_id); }
		catch (Exception e) { e.printStackTrace(); }
		
		current_auction = null;
				
		if (queue.size() > 1) {
		
			current_auction = queue.get(1);
			queue.get(1).startAuction();
			
		}
		queue.remove(0);		
		
	}
	
}
