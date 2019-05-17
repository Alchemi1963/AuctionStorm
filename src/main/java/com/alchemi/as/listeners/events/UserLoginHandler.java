package com.alchemi.as.listeners.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.alchemi.as.main;

public class UserLoginHandler implements Listener {

	@EventHandler
	public static void PlayerJoin(PlayerJoinEvent e) {
		if (main.gq.isPlayerQueued(e.getPlayer())) {
			main.gq.give(e.getPlayer());
		}
		
		if (e.getPlayer().hasPermission("al.forcecheckupdate") && main.getInstance().uc != null) {
			main.getInstance().uc.check();
		}
	}	
}
