package com.alchemi.as.objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.alchemi.al.Library;
import com.alchemi.al.configurations.Messenger;
import com.alchemi.as.main;

public class AuctionMessenger extends Messenger{

	public AuctionMessenger(main plug) {
		super(plug);
	}
	
	@Override
	public void broadcast(String msg) {
		broadcast(msg, true);
	}
	
	@Override
	public void broadcast(String msg, boolean useTag) {
		if (msg.contains("\n")) {
			for (String msg2 : msg.split("\n")) {
				broadcast(msg2, useTag);
			}
			return;
		}
		
		for (Player r : Bukkit.getOnlinePlayers()) {
			if (r.hasPermission("as.silence")) continue;
			
			if (useTag) r.sendMessage(cc(tag + " " + msg));
			else r.sendMessage(cc(msg));
		}
	}
	
	@Override
	public void broadcastHover(String mainText, String hoverText) {
		mainText = colourMessage(mainText);
		
		if (mainText.contains("\n")) {
			for (String msg : mainText.split("\n")) {
				broadcastHover(msg, hoverText);
			}
			return;
		}
		for (Player r : Library.instance.getServer().getOnlinePlayers()) {
			if (r.hasPermission("as.silence")) continue;
			
			sendHoverMsg(r, tag + " " + mainText, hoverText);
		}
	}
	
}
