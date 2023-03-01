package com.github.minersstudios.mscore.utils;

import com.github.minersstudios.msblock.MSBlock;
import com.github.minersstudios.msdecor.MSDecor;
import com.github.minersstudios.msitems.MSItems;

@SuppressWarnings("unused")
public final class MSPluginUtils {

	private MSPluginUtils() {
		throw new IllegalStateException("Utility class");
	}

	public static boolean isLoadedCustoms() {
		return MSDecor.getInstance().isLoadedCustoms()
				&& MSBlock.getInstance().isLoadedCustoms()
				&& MSItems.getInstance().isLoadedCustoms();
	}
}
