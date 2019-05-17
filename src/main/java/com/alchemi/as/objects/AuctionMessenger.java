package com.alchemi.as.objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.alchemi.al.Library;
import com.alchemi.al.configurations.Messenger;
import com.alchemi.al.objects.handling.nmsutils.ItemStacks;
import com.alchemi.al.objects.meta.PersistentMeta;
import com.alchemi.as.main;
import com.alchemi.as.objects.meta.SilentMeta;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

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
//			if (r.hasPermission("as.silence")) continue;
			if (PersistentMeta.hasMeta(r, SilentMeta.class) && PersistentMeta.getMeta(r, SilentMeta.class).asBoolean()) continue;
			
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
//			if (r.hasPermission("as.silence")) continue;
			if (PersistentMeta.hasMeta(r, SilentMeta.class) && PersistentMeta.getMeta(r, SilentMeta.class).asBoolean()) continue;
			
			sendHoverMsg(r, tag + " " + mainText, hoverText);
			
		}
	}
	
	public void broadcastITEM(String mainText, ItemStack item) {
		mainText = colourMessage(mainText);
		
		if (mainText.contains("\n")) {
			for (String msg : mainText.split("\n")) {
				broadcastITEM(msg, item);
			}
			return;
		}
		
		BaseComponent[] comps = new BaseComponent[] {new TextComponent(ItemStacks.itemStackToJSON(item))};
		HoverEvent ev = new HoverEvent( HoverEvent.Action.SHOW_ITEM, comps);
		
		TextComponent mainComponent;
		if (tag.endsWith(" ")) {
			mainComponent = new TextComponent(cc(tag + mainText));
		} else {
			mainComponent = new TextComponent(cc(tag + " " + mainText));
		}
		 
		mainComponent.setHoverEvent(ev);
		
		
		for (Player r : Library.instance.getServer().getOnlinePlayers()) {
//			if (r.hasPermission("as.silence")) continue;
			if (PersistentMeta.hasMeta(r, SilentMeta.class) && PersistentMeta.getMeta(r, SilentMeta.class).asBoolean()) continue;
			
			r.spigot().sendMessage(mainComponent);
		}
		
	}
	
}
