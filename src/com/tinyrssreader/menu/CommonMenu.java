package com.tinyrssreader.menu;

import android.view.MenuItem;
import android.widget.Toast;

import com.tinyrssreader.R;
import com.tinyrssreader.activities.ThemeUpdater;
import com.tinyrssreader.activities.actionbar.TinyRSSReaderActivity;
import com.tinyrssreader.storage.prefs.PrefsSettings;

public class CommonMenu {
	public static boolean checkIsCommonMenuItemSelected(
			TinyRSSReaderActivity context, MenuItem item) {
		if (logoutIfChosen(context, item)) {
			return true;
		}
		if (toggleShowUnreadIfChosen(context, item)) {
			context.onToggleShowUnread();
			return true;
		}
		if (showCategoriesIfChosen(context, item)) {
			context.onShowCategories();
			return true;
		}
		if (hideCategoriesIfChosen(context, item)) {
			context.onHideCategories();
			return true;
		}
		if (oldestFirstIfChosen(context, item)) {
			context.onOldestFirstChosen();
			return true;
		}
		if (defaultOrderIfChosen(context, item)) {
			context.onDefaultOrderChosen();
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

	private static boolean hideCategoriesIfChosen(
			TinyRSSReaderActivity context, MenuItem item) {
		if (item.getItemId() == R.id.toggle_show_categories
				&& PrefsSettings.getCategoryMode(context) != PrefsSettings.CATEGORY_NO_MODE) {
			Toast.makeText(context, "Hiding categories", Toast.LENGTH_LONG)
					.show();
			return true;
		}
		return false;
	}

	private static boolean showCategoriesIfChosen(
			TinyRSSReaderActivity context, MenuItem item) {
		if (item.getItemId() == R.id.toggle_show_categories
				&& PrefsSettings.getCategoryMode(context) == PrefsSettings.CATEGORY_NO_MODE) {
			Toast.makeText(context, "Showing categories", Toast.LENGTH_LONG)
					.show();
			return true;
		}
		return false;
	}

	private static boolean defaultOrderIfChosen(TinyRSSReaderActivity context,
			MenuItem item) {
		if (item.getItemId() == R.id.toggle_oldest_first
				&& PrefsSettings.getOrderByMode(context) != PrefsSettings.ORDER_BY_DEFAULT) {
			Toast.makeText(context, "Default order chosen", Toast.LENGTH_LONG)
					.show();
			return true;
		}
		return false;
	}

	private static boolean oldestFirstIfChosen(TinyRSSReaderActivity context,
			MenuItem item) {
		if (item.getItemId() == R.id.toggle_oldest_first
				&& PrefsSettings.getOrderByMode(context) != PrefsSettings.ORDER_BY_OLDEST_FIRST) {
			Toast.makeText(context, "Oldest first chosen", Toast.LENGTH_LONG)
					.show();
			return true;
		}
		return false;
	}

	private static boolean homeIsChosen(TinyRSSReaderActivity context,
			MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			Toast.makeText(context, context.getTitle(), Toast.LENGTH_LONG)
					.show();
			return true;
		}
		return false;
	}

	private static boolean logoutIfChosen(TinyRSSReaderActivity context,
			MenuItem item) {
		if (item.getItemId() == R.id.logout_action) {
			context.logout();
			return true;
		}
		return false;
	}

	private static boolean toggleShowUnreadIfChosen(
			TinyRSSReaderActivity context, MenuItem item) {
		if (item.getItemId() == R.id.toggle_show_unread) {
			context.toggleShowUnread();
			return true;
		}
		return false;
	}

	private static boolean switchThemeIfChosen(TinyRSSReaderActivity context,
			MenuItem item) {
		if (item.getItemId() == R.id.switch_to_dark_theme) {
			ThemeUpdater.setThemeManually(context, ThemeUpdater.NIGHT_THEME);
			return true;
		}
		if (item.getItemId() == R.id.switch_to_light_theme) {
			ThemeUpdater.setThemeManually(context, ThemeUpdater.DAY_THEME);
			return true;
		}
		if (item.getItemId() == R.id.switch_to_auto_theme) {
			ThemeUpdater.setThemeAuto(context);
			return true;
		}
		return false;
	}
}
