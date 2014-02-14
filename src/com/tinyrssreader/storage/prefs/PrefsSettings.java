package com.tinyrssreader.storage.prefs;

import android.content.Context;

public class PrefsSettings extends StoredPreferencesTinyRSSReader {
	public static final int CATEGORY_SHOW_FEEDS_MODE = 0;
	public static final int CATEGORY_NO_FEEDS_MODE = 1;
	public static final int CATEGORY_NO_MODE = 2;

	public static final String SHOW_ALL = "showAll";
	public static final String CATEGORY_MODE_USED = "categoryMode";
	public static final String CURRENT_CATEGORY_ID = "categoryId";

	static {
		PREFS = PREFS_PREFIX + "settings";
	}

	public static int getCategoryMode(Context context) {
		return getIntFromSavedPrefs(context, CATEGORY_MODE_USED);
	}

	public static void putCategoryMode(Context context, int mode) {
		putIntInSavedPrefs(context, CATEGORY_MODE_USED, mode);
	}

	public static int getCurrentCategoryId(Context context) {
		return getIntFromSavedPrefs(context, CURRENT_CATEGORY_ID);
	}

	public static void putCurrentCategoryId(Context context, int categoryId) {
		putIntInSavedPrefs(context, CURRENT_CATEGORY_ID, categoryId);
	}

	public static boolean getShowAllPref(Context context) {
		return getBooleanFromSavedPrefs(context, SHOW_ALL);
	}

	public static void putShowAllPref(Context context, boolean showAll) {
		putBooleanInSavedPrefs(context, SHOW_ALL, showAll);
	}

}
