package me.alchemi.as.listeners.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import me.alchemi.as.Storm;

public class UserLoginHandler implements Listener {

	@EventHandler
	public static void PlayerJoin(PlayerJoinEvent e) {
		if (Storm.gq.isPlayerQueued(e.getPlayer())) {
			Storm.gq.give(e.getPlayer());
		}
		
		if (e.getPlayer().hasPermission("al.forcecheckupdate") && Storm.getInstance().uc != null) {
			Storm.getInstance().uc.check();
		}
	}	
}
