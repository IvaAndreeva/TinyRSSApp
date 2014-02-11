package com.tinyrssapp.menu;

import android.view.MenuItem;
import android.widget.Toast;

import com.example.TinyRSSApp.R;
import com.tinyrssapp.activities.ThemeUpdater;
import com.tinyrssapp.activities.actionbar.TinyRSSAppActivity;

public class CommonMenu {
	public static boolean checkIsCommonMenuItemSelected(
			TinyRSSAppActivity context, MenuItem item) {
		if (logoutIfChosen(context, item)) {
			return true;
		}
		if (toggleShowUnreadIfChosen(context, item)) {
			context.onToggleShowUnread();
			return true;
		}
		if (switchThemeIfChosen(context, item)) {
			return true;
		}

		if (homeIsChosen(context, item)) {
			return true;
		}
		return false;
	}

	private static boolean homeIsChosen(TinyRSSAppActivity context,
			MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			Toast.makeText(context, context.getTitle(), Toast.LENGTH_LONG)
					.show();
			return true;
		}
		return false;
	}

	private static boolean logoutIfChosen(TinyRSSAppActivity context,
			MenuItem item) {
		if (item.getItemId() == R.id.logout_action) {
			context.logout();
			return true;
		}
		return false;
	}

	private static boolean toggleShowUnreadIfChosen(TinyRSSAppActivity context,
			MenuItem item) {
		if (item.getItemId() == R.id.toggle_show_unread) {
			context.toggleShowUnread();
			return true;
		}
		return false;
	}

	private static boolean switchThemeIfChosen(TinyRSSAppActivity context,
			MenuItem item) {
		if (item.getItemId() == R.id.switch_to_dark_theme) {
			ThemeUpdater.setThemeManually(context, ThemeUpdater.NIGHT_THEME);
			return true;
		}
		if (item.getItemId() == R.id.switch_to_light_theme) {
			ThemeUpdater.setThemeManually(context, ThemeUpdater.DAY_THEME);
			return true;
		}
		return false;
	}
}
