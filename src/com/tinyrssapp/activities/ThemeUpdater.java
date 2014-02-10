package com.tinyrssapp.activities;

import java.util.Calendar;
import java.util.Date;

import com.example.TinyRSSApp.R;
import com.tinyrssapp.activities.actionbar.TinyRSSAppActivity;

import android.content.Context;

public final class ThemeUpdater {
	public static final int DAY_THEME = R.style.CustomLightTheme;
	public static final int NIGHT_THEME = R.style.CustomDarkTheme;
	private static boolean manualThemeUsed = false;
	private static int manualTheme = DAY_THEME;

	public static final void updateTheme(Context context) {
		if (manualThemeUsed) {
			context.setTheme(manualTheme);
			return;
		}
		Date now = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		if (c.get(Calendar.HOUR_OF_DAY) >= 19) {
			context.setTheme(NIGHT_THEME);
		}
	}

	public static final void setThemeManually(TinyRSSAppActivity context,
			int theme) {
		manualTheme = theme;
		manualThemeUsed = true;
		context.setTheme(theme);
		context.finish();
		context.startActivity(context.getIntent());
	}
}
