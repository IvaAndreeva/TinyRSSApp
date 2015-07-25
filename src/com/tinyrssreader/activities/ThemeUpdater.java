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
	public static final void setThemeManually(TinyRSSReaderActivity context,
			int theme) {
		PrefsTheme.putSelectedTheme(context, theme);
		updateContext(context);
	}

	private static final void updateContext(TinyRSSReaderActivity context) {
		updateTheme(context);
		context.finish();
		context.startActivity(context.getIntent());
	}

	public static final void updateTheme(Context context) {
        context.setTheme(PrefsTheme.getSelectedTheme(context));
	}

	public static void updateRefreshIcon(Context context, MenuItem item, boolean enabled) {
		Drawable disabledIcon;
		Drawable enabledIcon;
		if (PrefsTheme.getSelectedTheme(context) == PrefsTheme.DAY_THEME){
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