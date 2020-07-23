package me.alchemi.as.objects.meta;

import org.bukkit.metadata.MetadataValueAdapter;
import me.alchemi.as.Storm;

public class SilentMeta extends MetadataValueAdapter {

	public static final String KEY = "silent_auctions";
	
	private final boolean silence;
	
	public SilentMeta(boolean silence) {
		super(Storm.getInstance());
		this.silence = silence;
	}
	
	@Override
	public Object value() {
		return silence;
	}
	
	@Override
	public void invalidate() {}

}
