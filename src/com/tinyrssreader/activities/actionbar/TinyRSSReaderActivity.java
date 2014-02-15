package com.tinyrssreader.activities.actionbar;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.tinyrssreader.R;
import com.tinyrssreader.activities.LoginActivity;
import com.tinyrssreader.activities.ThemeUpdater;
import com.tinyrssreader.constants.TinyTinySpecificConstants;
import com.tinyrssreader.entities.Feed;
import com.tinyrssreader.entities.Headline;
import com.tinyrssreader.storage.internal.StorageHeadlinesUtil;
import com.tinyrssreader.storage.prefs.PrefsCredentials;
import com.tinyrssreader.storage.prefs.PrefsSettings;
import com.tinyrssreader.storage.prefs.PrefsUpdater;

public abstract class TinyRSSReaderActivity extends ActionBarActivity {
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
			menu.clear();
			isMenuInflated = true;
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(getMenu(), menu);
			updateAllItemTitles();
		}
	}

	public void logout() {
		PrefsCredentials.putSessionIdPref(this, "");
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
		showAll = PrefsSettings.getShowAllPref(this);
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
		PrefsSettings.putShowAllPref(this, showAll);
		forceInflateMenu();
	}

	public void forceInflateMenu() {
		isMenuInflated = false;
		inflateMenu();
	}

	protected void updateAllItemTitles() {
		updateShowUnreadItemTitle();
		updateShowHideCategoriesItemTitle();
	}

	private void updateShowUnreadItemTitle() {
		MenuItem showUnreadItem = menu.findItem(R.id.toggle_show_unread);
		if (PrefsSettings.getShowAllPref(this)) {
			showUnreadItem.setTitle(R.string.show_unread_msg);
		} else {
			showUnreadItem.setTitle(R.string.show_all_msg);
		}
	}

	private void updateShowHideCategoriesItemTitle() {
		MenuItem showUnreadItem = menu.findItem(R.id.toggle_show_categories);
		if (PrefsSettings.getCategoryMode(this) == PrefsSettings.CATEGORY_NO_MODE) {
			showUnreadItem.setTitle(R.string.show_categories_msg);
		} else {
			showUnreadItem.setTitle(R.string.hide_categories_msg);
		}
	}

	public void onShowCategories() {
		PrefsSettings.putCategoryMode(this,
				PrefsSettings.CATEGORY_NO_FEEDS_MODE);
		PrefsUpdater.invalidateRefreshTimes(this);
		forceInflateMenu();
		startCategoriesActivity();
	}

	public void onHideCategories() {
		PrefsSettings.putCategoryMode(this, PrefsSettings.CATEGORY_NO_MODE);
		PrefsSettings.putCurrentCategoryId(this,
				TinyTinySpecificConstants.FRESH_FEED_ID);
		PrefsUpdater.invalidateRefreshTimes(this);
		forceInflateMenu();
		startAllFeedsActivity();
	}

	public void startCategoriesActivity() {
		startSimpleActivity(CategoriesActivity.class);
	}

	public void startAllFeedsActivity() {
		startSimpleActivity(FeedsActivity.class);
	}

	public void startArticleActivity(Headline headline,
			List<Headline> headlines, String feedTitle) {
		Bundle b = new Bundle();
		b.putString(LoginActivity.HOST_PROP, host);
		b.putString(TinyTinySpecificConstants.RESPONSE_LOGIN_SESSIONID_PROP,
				sessionId);
		b.putLong(ARTICLE_ID, headline.id);
		b.putString(CONTENT, headline.content);
		b.putString(FEED_TITLE_TO_LOAD, feedTitle);
		int feedIdToLoad = headline.feedId;
		if (PrefsSettings.getCategoryMode(this) == PrefsSettings.CATEGORY_NO_FEEDS_MODE
				|| PrefsSettings.getCurrentCategoryId(this) == TinyTinySpecificConstants.STARRED_FEED_ID) {
			feedIdToLoad = PrefsSettings.getCurrentCategoryId(this);
		}
		b.putInt(FEED_ID_TO_LOAD, feedIdToLoad);
		StorageHeadlinesUtil.save(this, headlines, feedIdToLoad);
		startActivity(b, ArticleActivity.class);
	}

	public void startHeadlinesActivity(Feed feed) {
		Bundle b = new Bundle();
		b.putString(LoginActivity.HOST_PROP, host);
		b.putString(TinyTinySpecificConstants.RESPONSE_LOGIN_SESSIONID_PROP,
				sessionId);
		b.putInt(FEED_ID_TO_LOAD, feed.id);
		b.putString(FEED_TITLE_TO_LOAD, feed.title);
		startActivity(b, HeadlinesActivity.class);
	}

	public void startShareIntent(String content) {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, content);
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
	}

	public void startSimpleActivity(
			Class<? extends TinyRSSReaderActivity> _class) {
		Bundle b = new Bundle();
		b.putString(LoginActivity.HOST_PROP, host);
		b.putString(TinyTinySpecificConstants.RESPONSE_LOGIN_SESSIONID_PROP,
				sessionId);
		startActivity(b, _class);
	}

	public void startActivity(Bundle b,
			Class<? extends TinyRSSReaderActivity> _class) {
		Intent intent = new Intent(this, _class);
		intent.putExtras(b);
		startActivity(intent);
		finish();
	}

	public void showProgress(String title, String body) {
		if (progressDialog == null && !this.isFinishing()) {
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
