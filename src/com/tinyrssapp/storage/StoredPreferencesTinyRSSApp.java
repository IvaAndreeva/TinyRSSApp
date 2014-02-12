package com.tinyrssapp.storage;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class StoredPreferencesTinyRSSApp {
	public static final String PREFS = "credentials";
	public static final String HOST = "host";
	public static final String USERNAME = "username";
	public static final String PASS = "pass";
	public static final String SHOW_ALL = "unread";
	public static final String LAST_TIME_CATEGORIES_UPDATED = "feedsUpdated";
	public static final String LAST_TIME_FEEDS_UPDATED = "feedsUpdated";
	public static final String LAST_TIME_HEADLINES_UPDATED = "headlinesUpdated";
	public static final String CATEGORY_USED = "useCat";
	public static final String SHOW_FEEDS_IN_CATEGORY = "showFeedsInCategory";

	public static boolean getCategoriesUsed(Context context) {
		return getBooleanFromSavedPrefs(context, CATEGORY_USED);
	}

	public static void putCategoriesUsed(Context context, boolean catUsed) {
		putBooleanInSavedPrefs(context, CATEGORY_USED, catUsed);
	}

	public static boolean getShowFeedsInCat(Context context) {
		return getBooleanFromSavedPrefs(context, SHOW_FEEDS_IN_CATEGORY);
	}

	public static void putShowFeedsInCat(Context context, boolean showFeedsInCat) {
		putBooleanInSavedPrefs(context, SHOW_FEEDS_IN_CATEGORY, showFeedsInCat);
	}

	public static String getUsernamePref(Context context) {
		return getStringFromSavedPrefs(context, USERNAME);
	}

	public static void putUsernamePref(Context context, String username) {
		putStringInSavedPrefs(context, USERNAME, username);
	}

	public static String getPasswordPref(Context context) {
		return getStringFromSavedPrefs(context, PASS);
	}

	public static void putPasswordPref(Context context, String pass) {
		putStringInSavedPrefs(context, PASS, pass);
	}

	public static String getHostPref(Context context) {
		return getStringFromSavedPrefs(context, HOST);
	}

	public static void putHostPref(Context context, String host) {
		putStringInSavedPrefs(context, HOST, host);
	}

	public static void putUserPassHost(Context context, String username,
			String pass, String host) {
		putUsernamePref(context, username);
		putPasswordPref(context, pass);
		putHostPref(context, host);
	}

	public static boolean getShowAllPref(Context context) {
		return getBooleanFromSavedPrefs(context, SHOW_ALL);
	}

	public static void putShowAllPref(Context context, boolean showAll) {
		putBooleanInSavedPrefs(context, SHOW_ALL, showAll);
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

	public static long getDateFromSavedPrefs(Context context, String prefName) {
		SharedPreferences savedPrefs = context.getSharedPreferences(PREFS,
				Context.MODE_PRIVATE);
		long defaultDate = (new Date(0)).getTime();
		if (savedPrefs != null) {
			return savedPrefs.getLong(prefName, defaultDate);
		}
		return defaultDate;
	}

	public static boolean getBooleanFromSavedPrefs(Context context,
			String prefName) {
		SharedPreferences savedPrefs = context.getSharedPreferences(PREFS,
				Context.MODE_PRIVATE);
		if (savedPrefs != null) {
			return savedPrefs.getBoolean(prefName, false);
		}
		return false;
	}

	public static String getStringFromSavedPrefs(Context context,
			String prefName) {
		SharedPreferences savedPrefs = context.getSharedPreferences(PREFS,
				Context.MODE_PRIVATE);
		if (savedPrefs != null) {
			return savedPrefs.getString(prefName, "");
		}
		return "";
	}

	public static void putDateInSavedPrefs(Context context, String prefName,
			Date date) {
		SharedPreferences savedPrefs = context.getSharedPreferences(PREFS,
				Context.MODE_PRIVATE);
		Editor editor = savedPrefs.edit();
		editor.putLong(prefName, date.getTime());
		editor.commit();
	}

	public static void putBooleanInSavedPrefs(Context context, String prefName,
			boolean bool) {
		SharedPreferences savedPrefs = context.getSharedPreferences(PREFS,
				Context.MODE_PRIVATE);
		Editor editor = savedPrefs.edit();
		editor.putBoolean(prefName, bool);
		editor.commit();
	}

	public static void putStringInSavedPrefs(Context context, String prefName,
			String str) {
		SharedPreferences savedPrefs = context.getSharedPreferences(PREFS,
				Context.MODE_PRIVATE);
		Editor editor = savedPrefs.edit();
		editor.putString(prefName, str);
		editor.commit();
	}
}
