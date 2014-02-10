package com.example.TinyRSSApp;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * Created by iva on 2/8/14.
 */
public class ArticleActivity extends TinyRSSAppActivity {
	private long articleId;
	private String content;
	private List<Headline> headlines;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.article);
		
		initialize();
	}

	@Override
	public void onBackPressed() {
		Headline currArticle = getCurrentArticle();
		startHeadlinesActivity(
				(new Feed()).setId(currArticle.feedId).setTitle(
						currArticle.title), ArticleActivity.this);
		super.onBackPressed();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.article_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (logoutIfChosen(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.article_action_toggle_unread:
			markArticleFieldAsMode(
					TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_FIELD_UNREAD_VALUE,
					TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_MODE_TOGGLE_VALUE);
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

	public void initialize() {
		Bundle b = getIntent().getExtras();
		if (b != null) {
			b.setClassLoader(getClassLoader());
			sessionId = b
					.getString(TinyTinySpecificConstants.RESPONSE_LOGIN_SESSIONID_PROP);
			host = b.getString(LoginActivity.HOST_PROP);
			articleId = b.getLong(FeedsActivity.ARTICLE_ID);
			content = b.getString(FeedsActivity.CONTENT);
			headlines = new ArrayList<Headline>();
			Parcelable[] headlinesBundle = b
					.getParcelableArray(HeadlinesActivity.HEADLINES_TO_LOAD);
			for (Parcelable headlineBundle : headlinesBundle) {
				if (headlineBundle instanceof Headline) {
					headlines.add((Headline) headlineBundle);
				}
			}
		} else {
			sessionId = "";
			host = "";
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
		markArticleFieldAsMode(
				TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_FIELD_UNREAD_VALUE,
				TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_MODE_FALSE_VALUE);
		loadWebView();
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
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							System.out.println("DONE!");
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
}