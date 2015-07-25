package com.tinyrssreader.storage.prefs;

import android.content.Context;

import com.tinyrssreader.R;

public class PrefsTheme extends StoredPreferencesTinyRSSReader {
	public static final int DAY_THEME = R.style.CustomLightTheme;
	public static final int NIGHT_THEME = R.style.CustomDarkTheme;


	public static final String SELECTED_THEME = "selectedTheme";

	public static int getSelectedTheme(Context context) {
		int theme = getIntFromSavedPrefs(context, SELECTED_THEME);
		return theme == 0? DAY_THEME : theme;
	}

	public static void putSelectedTheme(Context context, int theme) {
		putIntInSavedPrefs(context, SELECTED_THEME, theme);
	}
}
