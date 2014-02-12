package com.tinyrssapp.storage.prefs;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public abstract class StoredPreferencesTinyRSSApp {
	protected static String PREFS;

	protected static long getDateFromSavedPrefs(Context context, String prefName) {
		SharedPreferences savedPrefs = context.getSharedPreferences(PREFS,
				Context.MODE_PRIVATE);
		long defaultDate = (new Date(0)).getTime();
		if (savedPrefs != null) {
			return savedPrefs.getLong(prefName, defaultDate);
		}
		return defaultDate;
	}

	protected static boolean getBooleanFromSavedPrefs(Context context,
			String prefName) {
		SharedPreferences savedPrefs = context.getSharedPreferences(PREFS,
				Context.MODE_PRIVATE);
		if (savedPrefs != null) {
			return savedPrefs.getBoolean(prefName, false);
		}
		return false;
	}

	protected static String getStringFromSavedPrefs(Context context,
			String prefName) {
		SharedPreferences savedPrefs = context.getSharedPreferences(PREFS,
				Context.MODE_PRIVATE);
		if (savedPrefs != null) {
			return savedPrefs.getString(prefName, "");
		}
		return "";
	}

	protected static int getIntFromSavedPrefs(Context context, String prefName) {
		SharedPreferences savedPrefs = context.getSharedPreferences(PREFS,
				Context.MODE_PRIVATE);
		if (savedPrefs != null) {
			return savedPrefs.getInt(prefName, 0);
		}
		return 0;
	}

	protected static void putDateInSavedPrefs(Context context, String prefName,
			Date date) {
		SharedPreferences savedPrefs = context.getSharedPreferences(PREFS,
				Context.MODE_PRIVATE);
		Editor editor = savedPrefs.edit();
		editor.putLong(prefName, date.getTime());
		editor.commit();
	}

	protected static void putBooleanInSavedPrefs(Context context,
			String prefName, boolean bool) {
		SharedPreferences savedPrefs = context.getSharedPreferences(PREFS,
				Context.MODE_PRIVATE);
		Editor editor = savedPrefs.edit();
		editor.putBoolean(prefName, bool);
		editor.commit();
	}

	protected static void putStringInSavedPrefs(Context context,
			String prefName, String str) {
		SharedPreferences savedPrefs = context.getSharedPreferences(PREFS,
				Context.MODE_PRIVATE);
		Editor editor = savedPrefs.edit();
		editor.putString(prefName, str);
		editor.commit();
	}

	protected static void putIntInSavedPrefs(Context context, String prefName,
			int val) {
		SharedPreferences savedPrefs = context.getSharedPreferences(PREFS,
				Context.MODE_PRIVATE);
		Editor editor = savedPrefs.edit();
		editor.putInt(prefName, val);
		editor.commit();
	}
}
