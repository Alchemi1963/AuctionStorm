package com.alchemi.as.util.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.alchemi.as.AuctionStorm;

public class UserLoginHandler implements Listener {

	@EventHandler
	public static void PlayerJoin(PlayerJoinEvent e) {
		if (AuctionStorm.gq.isPlayerQueued(e.getPlayer())) {
			AuctionStorm.gq.give(e.getPlayer());
		}
	}
	
}
