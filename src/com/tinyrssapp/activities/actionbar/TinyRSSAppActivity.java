package com.tinyrssapp.activities.actionbar;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.TinyRSSApp.R;
import com.tinyrssapp.activities.LoginActivity;
import com.tinyrssapp.activities.ThemeUpdater;
import com.tinyrssapp.constants.TinyTinySpecificConstants;
import com.tinyrssapp.entities.Feed;
import com.tinyrssapp.entities.Headline;

public abstract class TinyRSSAppActivity extends ActionBarActivity {
	public static final String PREFS = "credentials";
	public static final String SHOW_UNREAD_PREFS = "unread";
	public static final String ARTICLE_ID = "articleId";
	public static final String CONTENT = "content";
	public static final String HEADLINES_TO_LOAD = "headlines";
	public static final String FEED_ID_TO_LOAD = "feedId";
	public static final String FEED_TITLE_TO_LOAD = "feedTitle";

	private static final String SHOWING_UNREAD_MSG = "Showing only unread";
	private static final String SHOWING_ALL_MSG = "Showing all";

	public String sessionId;
	public String host;
	private SharedPreferences savedPrefs;
	public static boolean showAll = false;
	public Menu menu;
	public boolean isMenuInflated = false;
	private ProgressDialog progressDialog;

	public void onCreate(Bundle savedInstanceState) {
		ThemeUpdater.updateTheme(TinyRSSAppActivity.this);
		super.onCreate(savedInstanceState);
		setContentView(getLayout());

		initialize();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		return super.onCreateOptionsMenu(menu);
	}

	public void inflateMenu() {
		if (!isMenuInflated) {
			isMenuInflated = true;
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(getMenu(), menu);
		}
	}

	public boolean checkIsCommonMenuItemSelected(MenuItem item) {
		if (logoutIfChosen(item)) {
			return true;
		}
		if (toggleShowUnreadIfChosen(item)) {
			onToggleShowUnread();
			return true;
		}
		if (switchThemeIfChosen(item)) {
			return true;
		}
		return false;
	}

	private boolean logoutIfChosen(MenuItem item) {
		if (item.getItemId() == R.id.logout_action) {
			logout();
			return true;
		}
		return false;
	}

	private boolean toggleShowUnreadIfChosen(MenuItem item) {
		if (item.getItemId() == R.id.toggle_show_unread) {
			toggleShowUnread();
			return true;
		}
		return false;
	}

	private boolean switchThemeIfChosen(MenuItem item) {
		if (item.getItemId() == R.id.switch_to_dark_theme) {
			ThemeUpdater.setThemeManually(TinyRSSAppActivity.this,
					ThemeUpdater.NIGHT_THEME);
			return true;
		}
		if (item.getItemId() == R.id.switch_to_light_theme) {
			ThemeUpdater.setThemeManually(TinyRSSAppActivity.this,
					ThemeUpdater.DAY_THEME);
			return true;
		}
		return false;
	}

	private void logout() {
		Intent intent = new Intent(TinyRSSAppActivity.this, LoginActivity.class);
		Bundle b = new Bundle();
		b.putBoolean(LoginActivity.AUTO_CONNECT, false);
		intent.putExtras(b);
		startActivity(intent);
		finish();
	}

	public void initSessionAndHost(Bundle b) {
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
		return showAll ? TinyTinySpecificConstants.REQUEST_HEADLINES_VIEW_MODE_ALL_ARTICLES_VALUE
				: TinyTinySpecificConstants.REQUEST_HEADLINES_VIEW_MODE_UNREAD_VALUE;
	}

	public void toggleShowUnread() {
		showAll = !showAll;
		String msg = SHOWING_UNREAD_MSG;
		if (showAll) {
			msg = SHOWING_ALL_MSG;
		}
		Toast.makeText(TinyRSSAppActivity.this, msg, Toast.LENGTH_LONG).show();
		savePrefs();
	}

	private void loadSavedPrefs() {
		savedPrefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
		if (savedPrefs != null) {
			showAll = savedPrefs.getBoolean(SHOW_UNREAD_PREFS, false);
		}
	}

	private void savePrefs() {
		savedPrefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
		Editor editor = savedPrefs.edit();
		editor.putBoolean(SHOW_UNREAD_PREFS, showAll);
		editor.commit();
	}

	public void startAllFeedsActivity(String host, String sessionId) {
		Intent intent = new Intent(TinyRSSAppActivity.this, FeedsActivity.class);
		Bundle b = new Bundle();
		b.putString(LoginActivity.HOST_PROP, host);
		b.putString(TinyTinySpecificConstants.RESPONSE_LOGIN_SESSIONID_PROP,
				sessionId);
		intent.putExtras(b);
		startActivity(intent);
		finish();
	}

	public void startArticleActivity(Headline headline,
			List<Headline> headlines, String feedTitle) {
		Intent intent = new Intent(TinyRSSAppActivity.this,
				ArticleActivity.class);
		Bundle b = new Bundle();
		b.putString(LoginActivity.HOST_PROP, host);
		b.putString(TinyTinySpecificConstants.RESPONSE_LOGIN_SESSIONID_PROP,
				sessionId);
		b.putLong(ARTICLE_ID, headline.id);
		b.putString(CONTENT, headline.content);
		b.putString(FEED_TITLE_TO_LOAD, feedTitle);
		b.putParcelableArray(HEADLINES_TO_LOAD,
				headlines.toArray(new Headline[headlines.size()]));
		intent.putExtras(b);
		startActivity(intent);
		finish();
	}

	public void startHeadlinesActivity(Feed feed) {
		Intent intent = new Intent(TinyRSSAppActivity.this,
				HeadlinesActivity.class);
		Bundle b = new Bundle();
		b.putString(LoginActivity.HOST_PROP, host);
		b.putString(TinyTinySpecificConstants.RESPONSE_LOGIN_SESSIONID_PROP,
				sessionId);
		b.putInt(FEED_ID_TO_LOAD, feed.id);
		b.putString(FEED_TITLE_TO_LOAD, feed.title);
		intent.putExtras(b);
		startActivity(intent);
		finish();
	}

	public void showProgress(String title, String body) {
		if (progressDialog == null) {
			progressDialog = ProgressDialog.show(TinyRSSAppActivity.this,
					title, body);
		}
	}

	public void hideProgress() {
		progressDialog.cancel();
		progressDialog = null;
	}

	public abstract int getMenu();

	public abstract int getLayout();

	public abstract void initialize();

	public abstract void onToggleShowUnread();
}
