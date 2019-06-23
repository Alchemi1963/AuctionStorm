package me.alchemi.as.objects.meta;

import me.alchemi.al.objects.meta.BaseMeta;
import me.alchemi.as.main;

public class SilentMeta extends BaseMeta {

	public SilentMeta(boolean silence) {
		super(main.getInstance(), silence);
	}

}
