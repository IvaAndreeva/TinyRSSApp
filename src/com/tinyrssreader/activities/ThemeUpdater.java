package com.tinyrssreader.activities;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MenuItem;

import com.tinyrssreader.R;
import com.tinyrssreader.activities.actionbar.TinyRSSReaderActivity;
import com.tinyrssreader.storage.prefs.PrefsTheme;

public final class ThemeUpdater {
	public static final int DAY_THEME = R.style.CustomLightTheme;
	public static final int NIGHT_THEME = R.style.CustomDarkTheme;

	public static final void setThemeManually(TinyRSSReaderActivity context,
			int theme) {
		PrefsTheme.putSelectedTheme(context, theme);
		PrefsTheme.putThemeMode(context, PrefsTheme.MANUAL_THEME);
		updateContext(context);
	}

	public static final void setThemeAuto(TinyRSSReaderActivity context) {
		PrefsTheme.putThemeMode(context, PrefsTheme.AUTO_THEME);
		updateContext(context);
	}

	private static final void updateContext(TinyRSSReaderActivity context) {
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
			PrefsTheme.putSelectedTheme(context, NIGHT_THEME);
		} else {
			PrefsTheme.putSelectedTheme(context, DAY_THEME);
		}
		context.setTheme(PrefsTheme.getSelectedTheme(context));
	}

	public static void updateRefreshIcon(Context context, MenuItem item, boolean enabled) {
		Drawable disabledIcon;
		Drawable enabledIcon;
		if (PrefsTheme.getSelectedTheme(context) == DAY_THEME){
			disabledIcon = context.getResources().getDrawable(
					R.drawable.dark_list_action_refresh);
			enabledIcon = context.getResources().getDrawable(
					R.drawable.light_list_action_refresh);
		} else {
			disabledIcon = context.getResources().getDrawable(
					R.drawable.light_list_action_refresh);
			enabledIcon = context.getResources().getDrawable(
					R.drawable.dark_list_action_refresh);
		}
		if (enabled) {
			item.setIcon(enabledIcon);
		} else {
			item.setIcon(disabledIcon);
		}
	}
}
