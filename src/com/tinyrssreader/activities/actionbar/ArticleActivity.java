package com.tinyrssreader.activities.actionbar;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.tinyrssreader.R;
import com.tinyrssreader.constants.TinyTinySpecificConstants;
import com.tinyrssreader.entities.Feed;
import com.tinyrssreader.entities.Headline;
import com.tinyrssreader.errorhandling.ErrorAlertDialog;
import com.tinyrssreader.menu.CommonMenu;
import com.tinyrssreader.request.RequestBuilder;
import com.tinyrssreader.request.RequestParamsBuilder;
import com.tinyrssreader.response.ResponseHandler;
import com.tinyrssreader.storage.internal.StorageHeadlinesUtil;
import com.tinyrssreader.storage.prefs.PrefsSettings;

/**
 * Created by iva on 2/8/14.
 */
public class ArticleActivity extends TinyRSSReaderActivity {
	public static final String MARKED_AS_READ_STR = "Marked as read";
	public static final String MARKED_AS_UNREAD_STR = "Marked as unread";
	public static final String MARKED_AS_STARRED_STR = "Marked as starred";
	public static final String MARKED_AS_NOT_STARRED_STR = "Unmarked as starred";
	public static final String BLANK_PAGE = "about:blank";

	private long articleId;
	private String content;
	private List<Headline> headlines;
	private String feedTitle;
	private int feedId;
	private int markReadIcon = 0;
	private int markUneadIcon = 0;
	private int markStarredIcon = 0;
	private int unmarkStarredIcon = 0;

	public void initialize() {
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		Bundle b = getIntent().getExtras();
		initSessionAndHost(b);
		if (b != null) {
			b.setClassLoader(getClassLoader());
			articleId = b.getLong(ARTICLE_ID);
			content = b.getString(CONTENT);
			feedTitle = b.getString(FEED_TITLE_TO_LOAD);
			feedId = b.getInt(FEED_ID_TO_LOAD);
			headlines = loadHeadlinesFromFile(feedId);
		} else {
			feedId = 0;
			articleId = 0;
			content = "";
			headlines = new ArrayList<Headline>();
		}
		((Button) findViewById(R.id.previousButton))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						reloadArticle(getPrevHeadline());
					}
				});
		((Button) findViewById(R.id.nextButton))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						reloadArticle(getNextHeadline());
					}
				});

		((Button) findViewById(R.id.openArticleButton))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent browserIntent = new Intent(Intent.ACTION_VIEW,
								Uri.parse(getCurrentArticle().link));
						startActivity(browserIntent);
					}
				});
		loadArticle();
	}

	private List<Headline> loadHeadlinesFromFile(int feedId) {
		List<Headline> allHeadlines = StorageHeadlinesUtil.get(this, feedId);
		List<Headline> resultHeadlines = allHeadlines;
		if (!PrefsSettings.getShowAllPref(this)) {
			resultHeadlines = new ArrayList<Headline>();
			for (Headline headline : allHeadlines) {
				if (headline.isUnread()) {
					resultHeadlines.add(headline);
				}
			}
		}
		return resultHeadlines;
	}

	@Override
	protected void updateAllItemTitles() {
		if (markReadIcon == 0 || unmarkStarredIcon == 0 || markStarredIcon == 0
				|| markUneadIcon == 0) {
			resolveIcons();
		}
		super.updateAllItemTitles();
		updateMarkUnreadItem();
		updateMarkStarredItem();
	}

	private void resolveIcons() {
		Resources.Theme themes = getTheme();
		TypedValue storedValueInTheme = new TypedValue();
		if (themes.resolveAttribute(R.attr.article_mark_read_icon,
				storedValueInTheme, true)) {
			markReadIcon = storedValueInTheme.resourceId;
		}
		if (themes.resolveAttribute(R.attr.article_mark_unread_icon,
				storedValueInTheme, true)) {
			markUneadIcon = storedValueInTheme.resourceId;
		}
		if (themes.resolveAttribute(R.attr.article_mark_starred_icon,
				storedValueInTheme, true)) {
			markStarredIcon = storedValueInTheme.resourceId;
		}
		if (themes.resolveAttribute(R.attr.article_unmark_starred_icon,
				storedValueInTheme, true)) {
			unmarkStarredIcon = storedValueInTheme.resourceId;
		}
	}

	private void updateMarkUnreadItem() {
		MenuItem markUnread = menu.findItem(R.id.article_action_toggle_unread);

		if (getCurrentArticle().unread) {
			markUnread.setTitle(R.string.article_mark_read_msg);
			markUnread.setIcon(markReadIcon);
		} else {
			markUnread.setTitle(R.string.article_mark_unread_msg);
			markUnread.setIcon(markUneadIcon);
		}
	}

	private void updateMarkStarredItem() {
		MenuItem markStarred = menu
				.findItem(R.id.article_action_toggle_starred);
		if (getCurrentArticle().marked) {
			markStarred.setTitle(R.string.article_mark_unstar_msg);
			markStarred.setIcon(unmarkStarredIcon);
		} else {
			markStarred.setTitle(R.string.article_mark_star_msg);
			markStarred.setIcon(markStarredIcon);
		}
	}

	@Override
	public void onBackPressed() {
		int feedIdToLoad = getCurrentArticle().feedId;
		if (PrefsSettings.getCategoryMode(this) == PrefsSettings.CATEGORY_NO_FEEDS_MODE
				|| PrefsSettings.getCurrentCategoryId(this) == TinyTinySpecificConstants.STARRED_FEED_ID) {
			feedIdToLoad = PrefsSettings.getCurrentCategoryId(this);
		}
		startHeadlinesActivity((new Feed()).setId(feedIdToLoad).setTitle(
				feedTitle));
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean res = super.onCreateOptionsMenu(menu);
		inflateMenu();
		return res;
	}

	private List<Headline> updateOldHeadlinesWithModifiedNewOne() {
		List<Headline> newHeadlines = new ArrayList<Headline>();
		Headline currArticle = getCurrentArticle();
		for (Headline oldHeadline : headlines) {
			if (oldHeadline.id == currArticle.id) {
				newHeadlines.add(currArticle);
			} else {
				newHeadlines.add(oldHeadline);
			}
		}
		return newHeadlines;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (CommonMenu.checkIsCommonMenuItemSelected(this, item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.article_action_toggle_unread:
			Headline currArticle = getCurrentArticle();
			currArticle.unread = !currArticle.unread;
			headlines = updateOldHeadlinesWithModifiedNewOne();
			StorageHeadlinesUtil.save(this, headlines, feedId);

			String msg = MARKED_AS_UNREAD_STR;
			if (!currArticle.unread) {
				msg = MARKED_AS_READ_STR;
			}
			markArticleFieldAsMode(
					TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_FIELD_UNREAD_VALUE,
					TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_MODE_TOGGLE_VALUE);
			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
			forceInflateMenu();
			return true;
		case R.id.article_action_toggle_starred:
			currArticle = getCurrentArticle();
			currArticle.marked = !currArticle.marked;
			headlines = updateOldHeadlinesWithModifiedNewOne();
			StorageHeadlinesUtil.save(this, headlines, feedId);

			msg = MARKED_AS_STARRED_STR;
			if (!currArticle.marked) {
				msg = MARKED_AS_NOT_STARRED_STR;
			}
			markArticleFieldAsMode(
					TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_FIELD_STARRED_VALUE,
					TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_MODE_TOGGLE_VALUE);

			Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
			updateAllItemTitles();
			return true;
		case R.id.article_action_share:
			startShareIntent(getShareContent(getCurrentArticle()));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private String getShareContent(Headline article) {
		return article.title + " " + article.link;
	}

	private Headline getCurrentArticle() {
		for (Headline headline : headlines) {
			if (headline.id == articleId) {
				return headline;
			}
		}
		return headlines.get(0);
	}

	protected void reloadArticle(Headline headline) {
		if (headline == null) {
			return;
		}
		articleId = headline.id;
		content = headline.content;
		loadArticle();
		forceInflateMenu();
	}

	private void loadArticle() {
		Headline currArticle = getCurrentArticle();
		currArticle.unread = false;
		markArticleFieldAsMode(
				TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_FIELD_UNREAD_VALUE,
				TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_MODE_FALSE_VALUE);
		updateOldHeadlinesWithModifiedNewOne();
		StorageHeadlinesUtil.save(this, headlines, feedId);
		loadWebView();
	}

	protected Headline getPrevHeadline() {
		for (int i = 0; i < headlines.size(); i++) {
			Headline headline = headlines.get(i);
			if (headline.id == articleId) {
				return i > 0 ? headlines.get(i - 1) : null;
			}
		}
		return null;
	}

	protected Headline getNextHeadline() {
		for (int i = 0; i < headlines.size(); i++) {
			Headline headline = headlines.get(i);
			if (headline.id == articleId) {
				return i < headlines.size() - 1 ? headlines.get(i + 1) : null;
			}
		}
		return null;
	}

	private void markArticleFieldAsMode(String fieldValue, String modeValue) {
		RequestBuilder.makeRequest(this, host, RequestParamsBuilder
				.paramsMarkArticleFieldAsMode(sessionId, articleId, fieldValue,
						modeValue), getMarkArticleResponseHandler());
	}

	private ResponseHandler getMarkArticleResponseHandler() {
		return new ResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
			}

			@Override
			public void onFinish() {
			}

			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				ErrorAlertDialog.showError(ArticleActivity.this,
						R.string.error_mark_article);
			}
		};
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void loadWebView() {
		showProgress("Loading article", "");
		WebView webView = (WebView) findViewById(R.id.articleWebView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl(BLANK_PAGE);
		injectHtml();
		// use loadDataWithBaseURL as workaround for webview bug:
		// https://code.google.com/p/android/issues/detail?id=1733
		webView.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null);
		hideProgress();
	}

	private void injectHtml() {
		content = content.replaceAll("^<html><body>", "");
		content = content.replaceAll("</body>\\s*</html>$", "");
		String headlineLink = TextUtils.replace(
				getString(R.string.tpl_article_title),
				new String[] { "{{link}}", "{{title}}" },
				new String[] { getCurrentArticle().link,
						TextUtils.htmlEncode(getCurrentArticle().title) })
				.toString();
		content = "<html><head>" + getString(R.string.tpl_article_style)
				+ "</head><body><div id='ttrss-article-container'>"
				+ headlineLink + content + "</div>"
				+ getString(R.string.tpl_article_script) + "</body></html>";
	}

	@Override
	public int getMenu() {
		return R.menu.article_actions;
	}

	@Override
	public int getLayout() {
		return R.layout.article;
	}

	@Override
	public void onToggleShowUnread() {
	}
}