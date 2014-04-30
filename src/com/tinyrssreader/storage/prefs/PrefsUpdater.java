package com.tinyrssreader.storage.prefs;

import java.util.Date;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefsUpdater extends StoredPreferencesTinyRSSReader {
	public static final String LAST_TIME_CATEGORIES_UPDATED = "categoriesUpdated";
	public static final String LAST_TIME_FEEDS_UPDATED = "feedsUpdated";
	public static final String LAST_TIME_HEADLINES_UPDATED = "headlinesUpdated";
	public static final String LAST_TIME_CLEANED = "lastCleaned";

	public static long getLastCleanedTime(Context context) {
		return getDateFromSavedPrefs(context, LAST_TIME_CLEANED);
	}

	public static long getLastCategoriesRefreshTime(Context context) {
		return getDateFromSavedPrefs(context, LAST_TIME_CATEGORIES_UPDATED);
	}

	public static long getLastFeedsRefreshTime(Context context, int categoryId) {
		return getDateFromSavedPrefs(context, LAST_TIME_FEEDS_UPDATED
				+ categoryId);
	}

	public static long getLastHeadlinesRefreshTime(Context context, int feedId) {
		return getDateFromSavedPrefs(context, LAST_TIME_HEADLINES_UPDATED
				+ feedId);
	}

	public static void putLastCleanedTime(Context context, Date date) {
		putDateInSavedPrefs(context, LAST_TIME_CLEANED, date);
	}

	public static void putLastCategoriesRefreshTime(Context context, Date date) {
		putDateInSavedPrefs(context, LAST_TIME_CATEGORIES_UPDATED, date);
	}

	public static void putLastFeedsRefreshTime(Context context, Date date,
			int categoryId) {
		putDateInSavedPrefs(context, LAST_TIME_FEEDS_UPDATED + categoryId, date);
	}

	public static void putLastHeadlinesRefreshTime(Context context, Date date,
			int feedId) {
		putDateInSavedPrefs(context, LAST_TIME_HEADLINES_UPDATED + feedId, date);
	}

	public static void invalidateCategoriesRefreshTime(Context context) {
		putLastCategoriesRefreshTime(context, new Date(0));
	}

	public static void invalidateFeedsRefreshTime(Context context,
			int categoryId) {
		putLastFeedsRefreshTime(context, new Date(0), categoryId);
	}

	public static void invalidateHeadlinesRefreshTime(Context context,
			int feedId) {
		putLastHeadlinesRefreshTime(context, new Date(0), feedId);
	}

	public static void invalidateRefreshTimes(Context context) {
		invalidateCategoriesRefreshTime(context);
		invalidateAllFeedsRefreshTime(context);
		invalidateAllHeadlinesRefreshTime(context);
	}

	public static void invalidateAllHeadlinesRefreshTime(Context context) {
		invalidateByStartingPref(context, LAST_TIME_HEADLINES_UPDATED);
	}

	public static void invalidateAllFeedsRefreshTime(Context context) {
		invalidateByStartingPref(context, LAST_TIME_FEEDS_UPDATED);
	}

	private static void invalidateByStartingPref(Context context, String pref) {
		SharedPreferences savedPrefs = context.getSharedPreferences(PREFS,
				Context.MODE_PRIVATE);
		Map<String, ?> keys = savedPrefs.getAll();
		for (Map.Entry<String, ?> entry : keys.entrySet()) {
			String key = entry.getKey();
			if (key.startsWith(pref) && key.length() > pref.length()) {
				String feedIdStr = key.substring(pref.length());
				Integer feedId = Integer.parseInt(feedIdStr);
				invalidateHeadlinesRefreshTime(context, feedId);
			}
		}
	}
}
