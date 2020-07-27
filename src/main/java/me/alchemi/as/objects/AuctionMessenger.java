package me.alchemi.as.objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.alchemi.al.Library;
import me.alchemi.al.configurations.Messenger;
import me.alchemi.al.objects.meta.MetaUtil;
import me.alchemi.al.util.ItemUtil;
import me.alchemi.as.Storm;
import me.alchemi.as.objects.meta.SilentMeta;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;
import net.md_5.bungee.api.chat.hover.content.Text;

public class AuctionMessenger extends Messenger{

	public AuctionMessenger(Storm plug) {
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
				broadcast(msg2.trim(), useTag);
			}
			return;
		}
		
		for (Player r : Bukkit.getOnlinePlayers()) {
			if (MetaUtil.hasMeta(r, SilentMeta.KEY, SilentMeta.class) && MetaUtil.getMeta(r, SilentMeta.KEY, SilentMeta.class).asBoolean()) continue;
			
			if (useTag) {
				if (tag.endsWith(" ") || msg.startsWith(" ")) {
					r.sendMessage(formatString(tag + msg));
				} else {
					r.sendMessage(formatString(tag + " " + msg));
				}
			}
			else r.sendMessage(formatString(msg));
		}
	}
	
	@Override
	public void broadcastHover(String mainText, String hoverText) {
		mainText = colourMessage(mainText);
		if (mainText.contains("\n")) {
			for (String msg : mainText.split("\n")) {
				broadcastHover(msg.trim(), hoverText);
			}
			return;
		}
		for (Player r : Library.getInstance().getServer().getOnlinePlayers()) {

			if (MetaUtil.hasMeta(r, SilentMeta.KEY, SilentMeta.class) && MetaUtil.getMeta(r, SilentMeta.KEY, SilentMeta.class).asBoolean()) continue;
			if (tag.endsWith(" ") || mainText.startsWith(" ")) {
				sendMessageHover(r, tag + " " + mainText, hoverText);
			} else {
				sendMessageHover(r, tag + " " + mainText, hoverText);
			}
			
		}
	}
	
	public void broadcastITEM(String mainText, ItemStack item) {
		mainText = colourMessage(mainText);
		if (mainText.contains("\n")) {
			for (String msg : mainText.split("\n")) {
				broadcastITEM(msg.trim(), item);
			}
			return;
		}
		
		Content[] comps = new Content[] {new Text(ItemUtil.itemStackToJson(item))};
		HoverEvent ev = new HoverEvent( HoverEvent.Action.SHOW_ITEM, comps);
		
		TextComponent mainComponent;
		if (tag.endsWith(" ") || mainText.startsWith(" ")) {
			mainComponent = new TextComponent(formatString(tag + mainText));
		} else {
			mainComponent = new TextComponent(formatString(tag + " " + mainText));
		}
		 
		mainComponent.setHoverEvent(ev);
		
		
		for (Player r : Library.getInstance().getServer().getOnlinePlayers()) {
			if (MetaUtil.hasMeta(r, SilentMeta.KEY, SilentMeta.class) && MetaUtil.getMeta(r, SilentMeta.KEY, SilentMeta.class).asBoolean()) continue;
			
			r.spigot().sendMessage(mainComponent);
		}
		
	}
	
}
