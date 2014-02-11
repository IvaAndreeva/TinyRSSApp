package com.tinyrssapp.activities.actionbar;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.tinyrssapp.activities.LoginActivity;
import com.tinyrssapp.activities.ThemeUpdater;
import com.tinyrssapp.constants.TinyTinySpecificConstants;
import com.tinyrssapp.entities.Feed;
import com.tinyrssapp.entities.Headline;
import com.tinyrssapp.storage.InternalStorageUtil;
import com.tinyrssapp.storage.StoredPreferencesTinyRSSApp;

public abstract class TinyRSSAppActivity extends ActionBarActivity {
	public static final String ARTICLE_ID = "articleId";
	public static final String CONTENT = "content";
	public static final String HEADLINES_TO_LOAD = "headlines";
	public static final String FEED_ID_TO_LOAD = "feedId";
	public static final String FEED_TITLE_TO_LOAD = "feedTitle";

	private static final String SHOWING_UNREAD_MSG = "Showing only unread";
	private static final String SHOWING_ALL_MSG = "Showing all";

	public String sessionId;
	public String host;
	public static boolean showAll = false;
	public Menu menu;
	public boolean isMenuInflated = false;
	private ProgressDialog progressDialog;
	protected boolean menuLoadingShouldWait = true;

	@SuppressLint("NewApi")
	public void onCreate(Bundle savedInstanceState) {
		ThemeUpdater.updateTheme(this);
		super.onCreate(savedInstanceState);
		setContentView(getLayout());

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
		    getActionBar().setHomeButtonEnabled(true);
		}
		initialize();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		boolean res = super.onCreateOptionsMenu(menu);
		if (!menuLoadingShouldWait) {
			inflateMenu();
		}
		return res;
	}

	public void inflateMenu() {
		if (!isMenuInflated) {
			isMenuInflated = true;
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(getMenu(), menu);
		}
	}

	public void logout() {
		Intent intent = new Intent(this, LoginActivity.class);
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
		showAll = StoredPreferencesTinyRSSApp.getShowAllPref(this);
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
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		StoredPreferencesTinyRSSApp.putShowAllPref(this, showAll);
	}

	public void startAllFeedsActivity(String host, String sessionId) {
		Intent intent = new Intent(this, FeedsActivity.class);
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
		Intent intent = new Intent(this, ArticleActivity.class);
		Bundle b = new Bundle();
		b.putString(LoginActivity.HOST_PROP, host);
		b.putString(TinyTinySpecificConstants.RESPONSE_LOGIN_SESSIONID_PROP,
				sessionId);
		b.putLong(ARTICLE_ID, headline.id);
		b.putString(CONTENT, headline.content);
		b.putString(FEED_TITLE_TO_LOAD, feedTitle);
		b.putInt(FEED_ID_TO_LOAD, headline.feedId);
		InternalStorageUtil.saveHeadlines(this, headlines, headline.feedId);
		intent.putExtras(b);
		startActivity(intent);
		finish();
	}

	public void startHeadlinesActivity(Feed feed) {
		Intent intent = new Intent(this, HeadlinesActivity.class);
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

	public List<Headline> loadHeadlinesFromFile(int feedId) {
		List<Headline> allHeadlines = InternalStorageUtil.getHeadlines(this,
				feedId);
		List<Headline> resultHeadlines = allHeadlines;
		if (!StoredPreferencesTinyRSSApp.getShowAllPref(this)) {
			resultHeadlines = new ArrayList<Headline>();
			for (Headline headline : allHeadlines) {
				if (headline.unread) {
					resultHeadlines.add(headline);
				}
			}
		}
		return resultHeadlines;
	}

	public void showProgress(String title, String body) {
		if (progressDialog == null) {
			progressDialog = ProgressDialog.show(this, title, body);
		}
	}

	public void hideProgress() {
		if (progressDialog != null) {
			progressDialog.cancel();
		}
		progressDialog = null;
	}

	public void progressNull() {
		progressDialog = null;
	}

	public abstract int getMenu();

	public abstract int getLayout();

	public abstract void initialize();

	public abstract void onToggleShowUnread();
}
