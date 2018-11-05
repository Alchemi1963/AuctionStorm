package com.alchemi.as;

import java.util.ArrayList;

public class Queue {

	private static ArrayList<Auction> queue = new ArrayList<Auction>();
	public static Auction current_auction;
	
	public static void addAuction(Auction a) { 
		
		queue.add(a);
		if (queue.size() == 1) {
			current_auction = a;
			a.startAuction();
		}
		
	}
	
	public static void removeAuction(Auction a) {
		
		queue.remove(a);
		
	}
	
	public static void nextAuction() {
		
		if (queue.size() > 1) {
		
			current_auction = null;
			current_auction = queue.get(1);
			queue.get(1).startAuction();
			queue.remove(0);
			
		}
		
	}
	
}
