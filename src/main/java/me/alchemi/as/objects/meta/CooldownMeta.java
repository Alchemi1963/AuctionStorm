package me.alchemi.as.objects.meta;

import org.bukkit.metadata.MetadataValueAdapter;

import me.alchemi.as.Storm;
import me.alchemi.as.objects.Config.AuctionOptions;

public class CooldownMeta extends MetadataValueAdapter {

	private long currentTicks;
	
	public static final String key = "AUCTION_COOLDOWN";
	
	protected CooldownMeta() {
		super(Storm.getInstance());
		currentTicks = Storm.getInstance().getServer().getWorlds().get(0).getFullTime();
	}

	public boolean isCooldownOver() {
		return currentTicks + AuctionOptions.COOLDOWN.asInt() <= Storm.getInstance().getServer().getWorlds().get(0).getFullTime();
	}
	
	@Override
	public Object value() {
		return currentTicks;
	}
	
	public long remainingTicks() {
		return Storm.getInstance().getServer().getWorlds().get(0).getFullTime() - (currentTicks + AuctionOptions.COOLDOWN.asInt());
	}

	@Override
	public void invalidate() {
		currentTicks = -1;
	}

}
