package com.alchemi.as.objects.meta;

import com.alchemi.al.objects.meta.BaseMeta;
import com.alchemi.as.main;

public class SilentMeta extends BaseMeta {

	public SilentMeta(boolean silence) {
		super(main.getInstance(), silence);
	}

}
