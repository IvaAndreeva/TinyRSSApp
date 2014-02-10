package com.example.TinyRSSApp;

import java.util.List;

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
	public static final String ARTICLE_ID = "articleId";
	public static final String CONTENT = "content";
	public static final String HEADLINES_TO_LOAD = "headlines";
	public static final String FEED_ID_TO_LOAD = "feedId";
	public static final String FEED_TITLE_TO_LOAD = "feedTitle";

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

	public void startAllFeedsActivity(String host, String sessionId,
			Context context) {
		Intent intent = new Intent(context, FeedsActivity.class);
		Bundle b = new Bundle();
		b.putString(LoginActivity.HOST_PROP, host);
		b.putString(TinyTinySpecificConstants.RESPONSE_LOGIN_SESSIONID_PROP,
				sessionId);
		intent.putExtras(b);
		startActivity(intent);
		finish();
	}

	public void startArticleActivity(Headline headline,
			List<Headline> headlines, Context context) {
		Intent intent = new Intent(context, ArticleActivity.class);
		Bundle b = new Bundle();
		b.putString(LoginActivity.HOST_PROP, host);
		b.putString(TinyTinySpecificConstants.RESPONSE_LOGIN_SESSIONID_PROP,
				sessionId);
		b.putLong(ARTICLE_ID, headline.id);
		b.putString(CONTENT, headline.content);
		b.putParcelableArray(HEADLINES_TO_LOAD,
				headlines.toArray(new Headline[headlines.size()]));
		intent.putExtras(b);
		startActivity(intent);
		finish();
	}

	public void startHeadlinesActivity(Feed feed, Context context) {
		Intent intent = new Intent(context, HeadlinesActivity.class);
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
}
