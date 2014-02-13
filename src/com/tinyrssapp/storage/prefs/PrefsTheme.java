package com.tinyrssapp.storage.prefs;

import android.content.Context;

public class PrefsTheme extends StoredPreferencesTinyRSSApp {
	public static final int AUTO_THEME = 0;
	public static final int MANUAL_THEME = 1;

	public static final String THEME_MODE = "sthemeMode";
	public static final String SELECTED_THEME = "selectedTheme";

	static {
		PREFS = "settings";
	}

	public static int getThemeMode(Context context) {
		return getIntFromSavedPrefs(context, THEME_MODE);
	}

	public static void putThemeMode(Context context, int mode) {
		putIntInSavedPrefs(context, THEME_MODE, mode);
	}
	
	public static int getSelectedTheme(Context context) {
		return getIntFromSavedPrefs(context, SELECTED_THEME);
	}

	public static void putSelectedTheme(Context context, int theme) {
		putIntInSavedPrefs(context, SELECTED_THEME, theme);
	}
}
