package me.alchemi.as.objects.meta;

import me.alchemi.al.objects.meta.BaseMeta;
import me.alchemi.as.Storm;

public class SilentMeta extends BaseMeta {

	public SilentMeta(boolean silence) {
		super(Storm.getInstance(), silence);
	}

}
