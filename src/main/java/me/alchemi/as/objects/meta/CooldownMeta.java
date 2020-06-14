package me.alchemi.as.objects.meta;

import org.bukkit.metadata.MetadataValueAdapter;

import me.alchemi.as.Storm;
import me.alchemi.as.objects.Config;

public class CooldownMeta extends MetadataValueAdapter {

	private long currentTicks;
	
	public static final String key = "AUCTION_COOLDOWN";
	
	protected CooldownMeta() {
		super(Storm.getInstance());
		currentTicks = Storm.getInstance().getServer().getWorlds().get(0).getFullTime();
	}

	public boolean isCooldownOver() {
		return currentTicks + Config.AUCTION.COOLDOWN.asInt() <= Storm.getInstance().getServer().getWorlds().get(0).getFullTime();
	}
	
	@Override
	public Object value() {
		return currentTicks;
	}

	@Override
	public void invalidate() {
		currentTicks = -1;
	}

}
