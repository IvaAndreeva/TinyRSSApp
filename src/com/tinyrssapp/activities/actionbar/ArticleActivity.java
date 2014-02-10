package com.tinyrssapp.activities.actionbar;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.Toast;

import com.example.TinyRSSApp.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tinyrssapp.constants.TinyTinySpecificConstants;
import com.tinyrssapp.entities.Feed;
import com.tinyrssapp.entities.Headline;

/**
 * Created by iva on 2/8/14.
 */
public class ArticleActivity extends TinyRSSAppActivity {
	public static final String MARKED_AS_READ_STR = "Marked as read";
	public static final String MARKED_AS_UNREAD_STR = "Marked as unread";

	private long articleId;
	private String content;
	private List<Headline> headlines;
	private String feedTitle;

	public void initialize() {
		Bundle b = getIntent().getExtras();
		initSessionAndHost(b);
		if (b != null) {
			b.setClassLoader(getClassLoader());
			articleId = b.getLong(ARTICLE_ID);
			content = b.getString(CONTENT);
			feedTitle = b.getString(FEED_TITLE_TO_LOAD);
			headlines = new ArrayList<Headline>();
			Parcelable[] headlinesBundle = b
					.getParcelableArray(HeadlinesActivity.HEADLINES_TO_LOAD);
			for (Parcelable headlineBundle : headlinesBundle) {
				if (headlineBundle instanceof Headline) {
					headlines.add((Headline) headlineBundle);
				}
			}
		} else {
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

	@Override
	public void onBackPressed() {
		startHeadlinesActivity((new Feed()).setId(getCurrentArticle().feedId)
				.setTitle(feedTitle));
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean res = super.onCreateOptionsMenu(menu);
		inflateMenu();
		return res;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (checkIsCommonMenuItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.article_action_toggle_unread:
			Headline currArticle = getCurrentArticle();
			currArticle.unread = !currArticle.unread;

			String msg = MARKED_AS_UNREAD_STR;
			if (!currArticle.unread) {
				msg = MARKED_AS_READ_STR;
			}
			markArticleFieldAsMode(
					TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_FIELD_UNREAD_VALUE,
					TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_MODE_TOGGLE_VALUE);
			Toast.makeText(ArticleActivity.this, msg, Toast.LENGTH_LONG).show();
			return true;
		case R.id.article_action_toggle_starred:
			markArticleFieldAsMode(
					TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_FIELD_STARRED_VALUE,
					TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_MODE_TOGGLE_VALUE);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
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
	}

	private void loadArticle() {
		Headline currArticle = getCurrentArticle();
		currArticle.unread = !currArticle.unread;
		markArticleFieldAsMode(
				TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_FIELD_UNREAD_VALUE,
				TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_MODE_FALSE_VALUE);
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
		showProgress("Loading article", "");
		AsyncHttpClient client = new AsyncHttpClient();
		try {
			JSONObject jsonParams = new JSONObject();
			jsonParams
					.put(TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_ARTILE_IDS_PROP,
							articleId);
			jsonParams.put(TinyTinySpecificConstants.OP_PROP,
					TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_OP_VALUE);
			jsonParams
					.put(TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_FIELD_PROP,
							fieldValue);
			jsonParams.put(
					TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_MODE_PROP,
					modeValue);
			jsonParams.put(TinyTinySpecificConstants.REQUEST_SESSION_ID_PROP,
					sessionId);
			StringEntity entity = new StringEntity(jsonParams.toString());
			client.post(getApplicationContext(), host, entity,
					"application/json", new JsonHttpResponseHandler() {
						@Override
						public void onFinish() {
							hideProgress();
							super.onFinish();
						}
					});
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void loadWebView() {
		setTitle(getCurrentArticle().title);
		WebView webView = (WebView) findViewById(R.id.articleWebView);
		webView.loadData(content, "text/html", "utf-8");
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