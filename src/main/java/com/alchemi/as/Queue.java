package com.alchemi.as;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.alchemi.as.objects.Config;

public class Queue {

	private static ArrayList<Auction> queue = new ArrayList<Auction>();
	public static ArrayList<Auction> getQueue() {
		return queue;
	}

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
				System.out.println(a.getInfo(false));
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
			main.messenger.broadcast(Auction.getItemName(a.getObject()));
		}
		
	}
	
	public static int getQueueLength() {
		return queue.size();
	}
	
	public static void removeAuction(Auction a) {
		
		queue.remove(a);
		
	}
	
	public static void cancelAuction(int auctionID, Player ender) {cancelAuction(auctionID, ender, "");}
	
	public static void cancelAuction(int auctionID, Player ender, String reason) {
		
		if (auctionID < getQueueLength()) {
			queue.get(auctionID).forceEndAuction(reason, ender);
			queue.remove(auctionID);
		}
		
	}
	
	public static void nextAuction() {
		
		try { main.instance.getServer().getScheduler().cancelTask(current_auction.task_id); }
		catch (Exception e) { e.printStackTrace(); }
			
		current_auction = null;
				
		if (queue.size() > 1) {
		
			current_auction = queue.get(1);
			main.instance.getServer().getScheduler().runTaskLater(main.instance, new Runnable() {
				
				@Override
				public void run() {
					Queue.current_auction.startAuction();
					
				}
			}, Config.AUCTION.START_DELAY.asInt() * 20);
			
			
		}
		queue.remove(0);
		
	}
	
}
