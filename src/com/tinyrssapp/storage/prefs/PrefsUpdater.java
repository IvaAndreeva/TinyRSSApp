package com.tinyrssapp.storage.prefs;

import java.util.Date;

import android.content.Context;

public class PrefsUpdater extends StoredPreferencesTinyRSSApp {
	public static final String LAST_TIME_CATEGORIES_UPDATED = "feedsUpdated";
	public static final String LAST_TIME_FEEDS_UPDATED = "feedsUpdated";
	public static final String LAST_TIME_HEADLINES_UPDATED = "headlinesUpdated";
	public static final String LAST_TIME_CLEANED = "lastCleaned";

	static {
		PREFS = "updater";
	}

	public static long getLastCleanedTime(Context context) {
		return getDateFromSavedPrefs(context, LAST_TIME_CLEANED);
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

	public static void putLastCleanedTime(Context context, Date date) {
		putDateInSavedPrefs(context, LAST_TIME_CLEANED, date);
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

	public static void invalidateCategoriesRefreshTime(Context context) {
		putLastCategoriesRefreshTime(context, new Date(0));
	}

	public static void invalidateFeedsRefreshTime(Context context) {
		putLastFeedsRefreshTime(context, new Date(0));
	}

	public static void invalidateHeadlinesRefreshTime(Context context) {
		putLastHeadlinesRefreshTime(context, new Date(0));
	}

	public static void invalidateRefreshTimes(Context context) {
		invalidateCategoriesRefreshTime(context);
		invalidateFeedsRefreshTime(context);
		invalidateHeadlinesRefreshTime(context);
	}
}
