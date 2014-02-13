package com.tinyrssapp.activities;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;

import com.example.TinyRSSApp.R;
import com.tinyrssapp.activities.actionbar.TinyRSSAppActivity;
import com.tinyrssapp.storage.prefs.PrefsTheme;

public final class ThemeUpdater {
	public static final int DAY_THEME = R.style.CustomLightTheme;
	public static final int NIGHT_THEME = R.style.CustomDarkTheme;

	public static final void setThemeManually(TinyRSSAppActivity context,
			int theme) {
		PrefsTheme.putSelectedTheme(context, theme);
		PrefsTheme.putThemeMode(context, PrefsTheme.MANUAL_THEME);
		updateContext(context);
	}

	public static final void setThemeAuto(TinyRSSAppActivity context) {
		PrefsTheme.putThemeMode(context, PrefsTheme.AUTO_THEME);
		updateContext(context);
	}

	private static final void updateContext(TinyRSSAppActivity context) {
		updateTheme(context);
		context.finish();
		context.startActivity(context.getIntent());
	}

	public static final void updateTheme(Context context) {
		if (PrefsTheme.getThemeMode(context) == PrefsTheme.MANUAL_THEME) {
			context.setTheme(PrefsTheme.getSelectedTheme(context));
			return;
		}
		Date now = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(now);
		if (c.get(Calendar.HOUR_OF_DAY) >= 19
				|| c.get(Calendar.HOUR_OF_DAY) <= 7) {
			context.setTheme(NIGHT_THEME);
		} else {
			context.setTheme(DAY_THEME);
		}
	}
}
