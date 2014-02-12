package com.tinyrssapp.storage.prefs;

import java.util.Date;

import android.content.Context;

public class PrefsUpdater extends StoredPreferencesTinyRSSApp {
	public static final String LAST_TIME_CATEGORIES_UPDATED = "feedsUpdated";
	public static final String LAST_TIME_FEEDS_UPDATED = "feedsUpdated";
	public static final String LAST_TIME_HEADLINES_UPDATED = "headlinesUpdated";

	static {
		PREFS = "updater";
	}

	public static long getLastCategoriesRefreshTime(Context context) {
		return getDateFromSavedPrefs(context, LAST_TIME_CATEGORIES_UPDATED);
	}

	public static long getLastFeedsRefreshTime(Context context) {
		return getDateFromSavedPrefs(context, LAST_TIME_FEEDS_UPDATED);
	}

	public static long getLastHeadlinesRefreshTime(Context context) {
		return getDateFromSavedPrefs(context, LAST_TIME_HEADLINES_UPDATED);
	}

	public static void putLastCategoriesRefreshTime(Context context, Date date) {
		putDateInSavedPrefs(context, LAST_TIME_CATEGORIES_UPDATED, date);
	}

	public static void putLastFeedsRefreshTime(Context context, Date date) {
		putDateInSavedPrefs(context, LAST_TIME_FEEDS_UPDATED, date);
	}

	public static void putLastHeadlinesRefreshTime(Context context, Date date) {
		putDateInSavedPrefs(context, LAST_TIME_HEADLINES_UPDATED, date);
	}
}