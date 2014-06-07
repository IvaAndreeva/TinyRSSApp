package com.tinyrssreader.storage.prefs;

import android.content.Context;

public class PrefsSettings extends StoredPreferencesTinyRSSReader {
	public static final int CATEGORY_SHOW_FEEDS_MODE = 0;
	public static final int CATEGORY_NO_FEEDS_MODE = 1;
	public static final int CATEGORY_NO_MODE = 2;
	public static final int ORDER_BY_DEFAULT = 0;
	public static final int ORDER_BY_OLDEST_FIRST = 1;

	public static final String SHOW_ALL = "showAll";
	public static final String CATEGORY_MODE_USED = "categoryMode";
	public static final String CURRENT_CATEGORY_ID = "categoryId";
	public static final String ORDER_BY_MODE = "orderBy";

	public static final String NO_SSL_URSL = "NoSSLUrls";

	public static int getCategoryMode(Context context) {
		return getIntFromSavedPrefs(context, CATEGORY_MODE_USED);
	}

	public static void putCategoryMode(Context context, int mode) {
		putIntInSavedPrefs(context, CATEGORY_MODE_USED, mode);
	}
	
	public static int getOrderByMode(Context context) {
		return getIntFromSavedPrefs(context, ORDER_BY_MODE);
	}

	public static void putOrderByMode(Context context, int mode) {
		putIntInSavedPrefs(context, ORDER_BY_MODE, mode);
	}

	public static int getCurrentCategoryId(Context context) {
		return getIntFromSavedPrefs(context, CURRENT_CATEGORY_ID);
	}

	public static boolean hasSSLIgnoreUrl(Context context, String url) {
		return hasPref(context, NO_SSL_URSL + url);
	}

	public static void putCurrentCategoryId(Context context, int categoryId) {
		putIntInSavedPrefs(context, CURRENT_CATEGORY_ID, categoryId);
	}

	public static void putUrlToNoSSLUrls(Context context, String url) {
		putStringInSavedPrefs(context, NO_SSL_URSL + url, "ignore this url");
	}

	public static boolean getShowAllPref(Context context) {
		return getBooleanFromSavedPrefs(context, SHOW_ALL);
	}

	public static void putShowAllPref(Context context, boolean showAll) {
		putBooleanInSavedPrefs(context, SHOW_ALL, showAll);
	}

}
