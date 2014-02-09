package com.example.TinyRSSApp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

public abstract class TinyRSSAppActivity extends ActionBarActivity {
	public static final String PREFS = "credentials";
	public static final String SHOW_UNREAD_PREFS = "unread";

	public String sessionId;
	public String host;
	private SharedPreferences savedPrefs;
	public boolean showUnread = false;

	public boolean logoutIfChosen(MenuItem item) {
		if (item.getItemId() == R.id.logout_action) {
			logout();
			return true;
		}
		return false;
	}
	
	public boolean toggleShowUnreadIfChosen(MenuItem item) {
		if (item.getItemId() == R.id.toggle_show_unread) {
			toggleShowUnread();
			return true;
		}
		return false;
	}

	private void logout() {
		Intent intent = new Intent(TinyRSSAppActivity.this, LoginActivity.class);
		startActivity(intent);
		finish();
	}

	public void initialize() {
		Bundle b = getIntent().getExtras();
		if (b != null) {
			sessionId = b
					.getString(TinyTinySpecificConstants.RESPONSE_LOGIN_SESSIONID_PROP);
			host = b.getString(LoginActivity.HOST_PROP);
		} else {
			sessionId = "";
			host = "";
		}
		loadSavedPrefs();
	}

	public String getViewMode() {
		return showUnread ? TinyTinySpecificConstants.REQUEST_HEADLINES_VIEW_MODE_ALL_ARTICLES_VALUE
				: TinyTinySpecificConstants.REQUEST_HEADLINES_VIEW_MODE_UNREAD_VALUE;
	}

	public void toggleShowUnread() {
		showUnread = !showUnread;
		savePrefs();
	}

	private void loadSavedPrefs() {
		savedPrefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
		if (savedPrefs != null) {
			showUnread = savedPrefs.getBoolean(SHOW_UNREAD_PREFS, false);
		}
	}

	private void savePrefs() {
		savedPrefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
		Editor editor = savedPrefs.edit();
		editor.putBoolean(SHOW_UNREAD_PREFS, showUnread);
		editor.commit();
	}
}
