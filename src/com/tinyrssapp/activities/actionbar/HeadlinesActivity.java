package com.tinyrssapp.activities.actionbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.TinyRSSApp.R;
import com.tinyrssapp.constants.TinyTinySpecificConstants;
import com.tinyrssapp.entities.CustomAdapter;
import com.tinyrssapp.entities.Feed;
import com.tinyrssapp.entities.Headline;
import com.tinyrssapp.errorhandling.ErrorAlertDialog;
import com.tinyrssapp.menu.CommonMenu;
import com.tinyrssapp.request.RequestBuilder;
import com.tinyrssapp.request.RequestParamsBuilder;
import com.tinyrssapp.response.ResponseHandler;
import com.tinyrssapp.storage.internal.StorageCategoriesUtil;
import com.tinyrssapp.storage.internal.StorageFeedsUtil;
import com.tinyrssapp.storage.internal.StorageHeadlinesUtil;
import com.tinyrssapp.storage.prefs.PrefsSettings;
import com.tinyrssapp.storage.prefs.PrefsUpdater;

/**
 * Created by iva on 2/7/14.
 */
public class HeadlinesActivity extends TinyRSSAppActivity {
	public static final String NO_HEADLINES_MSG = "There are no available headlines in here";
	public static final int MINUTES_WITHOUT_HEADLINES_REFRESH = 10;
	private static final long MILISECS_WITHOUT_HEADLINES_REFRESH = MINUTES_WITHOUT_HEADLINES_REFRESH * 60 * 1000;

	private ListView listView;
	private int feedId;
	private Feed feed;
	private List<Feed> feeds;

	@Override
	protected void onStart() {
		super.onStart();
		loadHeadlines();
	}

	public void initialize() {
		Bundle b = getIntent().getExtras();
		initSessionAndHost(b);
		if (b != null) {
			feedId = b.getInt(FEED_ID_TO_LOAD);
			feed = getParentFeed();
		} else {
			feedId = 0;
			feed = new Feed();
		}
		listView = (ListView) findViewById(R.id.listView);
	}

	private Feed getParentFeed() {
		if (PrefsSettings.getCategoryMode(this) == PrefsSettings.CATEGORY_NO_FEEDS_MODE) {
			feeds = StorageCategoriesUtil.get(this, sessionId);
		} else {
			feeds = StorageFeedsUtil.get(this, sessionId,
					PrefsSettings.getCurrntCategoryId(this));
		}
		for (Feed feed : feeds) {
			if (feed.id == feedId) {
				return feed;
			}
		}
		return new Feed();
	}

	@Override
	public void onBackPressed() {
		if (PrefsSettings.getCategoryMode(this) == PrefsSettings.CATEGORY_NO_FEEDS_MODE) {
			startCategoriesActivity();
		} else {
			startAllFeedsActivity();
		}
		super.onBackPressed();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (CommonMenu.checkIsCommonMenuItemSelected(this, item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.list_action_refresh:
			refreshHeadlines();
			return true;
		case R.id.list_action_mark_all_as_read:
			markFeedAsRead(feedId, host, sessionId, getApplicationContext());
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void loadHeadlines() {
		Date now = new Date();
		long lastHeadlinesUpdate = PrefsUpdater
				.getLastHeadlinesRefreshTime(this);
		if (now.getTime() - lastHeadlinesUpdate >= MILISECS_WITHOUT_HEADLINES_REFRESH
				|| !StorageHeadlinesUtil.hasInFile(this, feedId)) {
			menuLoadingShouldWait = true;
			refreshHeadlines();
		} else {
			menuLoadingShouldWait = false;
			showHeadlines(loadHeadlinesFromFile(feedId));
		}
	}

	private void refreshHeadlines() {
		showProgress("Loading headlines...", "");
		StorageHeadlinesUtil.savePos(this, feedId, 0);
		feedId = feed.id;
		ResponseHandler handler = getHeadlinesResponseHandler();
		boolean parentIsCat = PrefsSettings.getCategoryMode(this) == PrefsSettings.CATEGORY_NO_FEEDS_MODE;
		RequestBuilder.makeRequest(this, host, RequestParamsBuilder
				.paramsGetHeadlines(sessionId, feedId, parentIsCat,
						getViewMode()), handler);
	}

	private void showHeadlines(final List<Headline> headlines) {
		if (menuLoadingShouldWait) {
			inflateMenu();
		}
		setTitle("");
		feed.unread = 0;
		for (Headline headline : headlines) {
			if (headline.unread) {
				feed.unread++;
			}
		}
		updateHeadlinesAndFeedsFiles(headlines);
		if (headlines.size() == 0) {
			headlines.add((new Headline()).setTitle(NO_HEADLINES_MSG));
			listView.setEnabled(false);
		} else {
			listView.setEnabled(true);
		}
		ArrayAdapter<Headline> headlinesAdapter = new CustomAdapter<Headline>(
				this, R.layout.headline_layout, R.id.headline_data, headlines);
		listView.setAdapter(headlinesAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Headline currHeadline = (Headline) parent.getAdapter().getItem(
						position);
				StorageHeadlinesUtil.savePos(HeadlinesActivity.this, feedId,
						position);
				startArticleActivity(currHeadline, headlines, getTitle()
						.toString());
			}
		});
		if (StorageHeadlinesUtil.hasPosInFile(this, feedId)) {
			listView.setSelection(StorageHeadlinesUtil.getPos(this, feedId));
		}
		headlinesAdapter.notifyDataSetChanged();
	}

	private ResponseHandler getHeadlinesResponseHandler() {
		return new ResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {

				AsyncTask<JSONObject, Void, Void> task = new AsyncTask<JSONObject, Void, Void>() {
					private List<Headline> headlines = new ArrayList<Headline>();

					@Override
					protected Void doInBackground(JSONObject... params) {
						try {
							if (params.length < 1
									|| !(params[0] instanceof JSONObject)) {
								ErrorAlertDialog.showError(
										HeadlinesActivity.this,
										R.string.error_something_went_wrong);
								return null;
							}
							PrefsUpdater.putLastHeadlinesRefreshTime(
									HeadlinesActivity.this, new Date());
							JSONObject response = (JSONObject) params[0];
							JSONArray contentArray = response
									.getJSONArray(TinyTinySpecificConstants.RESPONSE_CONTENT_PROP);
							for (int i = 0; i < contentArray.length(); i++) {
								JSONObject headlineJson = contentArray
										.getJSONObject(i);
								Headline headline = (new Headline())
										.setContent(
												headlineJson
														.getString(TinyTinySpecificConstants.RESPONSE_HEADLINE_CONTENT_PROP))
										.setFeedId(
												headlineJson
														.getInt(TinyTinySpecificConstants.REQUEST_HEADLINES_FEED_ID_PROP))
										.setId(headlineJson
												.getLong(TinyTinySpecificConstants.RESPONSE_HEADLINE_ID_PROP))
										.setIsUpdated(
												headlineJson
														.getBoolean(TinyTinySpecificConstants.RESPONSE_HEADLINE_IS_UPDATED_PROP))
										.setLink(
												headlineJson
														.getString(TinyTinySpecificConstants.RESPONSE_HEADLINE_LINK_PROP))
										.setMarked(
												headlineJson
														.getBoolean(TinyTinySpecificConstants.RESPONSE_HEADLINE_MARKED_PROP))
										.setPublished(
												headlineJson
														.getBoolean(TinyTinySpecificConstants.RESPONSE_HEADLINE_PUBLISHED_PROP))
										.setTitle(
												headlineJson
														.getString(TinyTinySpecificConstants.RESPONSE_FEED_TITLE_PROP))
										.setUnread(
												headlineJson
														.getBoolean(TinyTinySpecificConstants.RESPONSE_FEED_UNREAD_PROP))
										.setUpdated(
												headlineJson
														.getLong(TinyTinySpecificConstants.RESPONSE_HEADLINE_UPDATED_PROP));
								headlines.add(headline);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void aVoid) {
						super.onPostExecute(aVoid);
						showHeadlines(headlines);
					}
				};
				task.execute(new JSONObject[] { response });
			}

			@Override
			public void onFinish() {
				hideProgress();
			}

			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				ErrorAlertDialog.showError(HeadlinesActivity.this,
						R.string.error_refresh_headlines);
			}
		};
	}

	private void markFeedAsRead(final int feedId, final String host,
			final String sessionId, Context context) {
		// showProgress("Marking feed as read...", "");
		feed.unread = 0;
		updateHeadlinesAndFeedsFiles(markAllHeadlinesAsRead());
		if (PrefsSettings.getCategoryMode(HeadlinesActivity.this) == PrefsSettings.CATEGORY_NO_FEEDS_MODE) {
			startCategoriesActivity();
		} else {
			startAllFeedsActivity();
		}
		boolean parentIsCat = PrefsSettings.getCategoryMode(this) == PrefsSettings.CATEGORY_NO_FEEDS_MODE;
		RequestBuilder.makeRequest(this, host, RequestParamsBuilder
				.paramsMarkFeedAsRead(sessionId, feedId, parentIsCat),
				getMarkFeedAsReadHandler());
	}

	private ResponseHandler getMarkFeedAsReadHandler() {
		return new ResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
			}

			@Override
			public void onFinish() {
				hideProgress();
				progressNull();
				// feed.unread = 0;
				// updateHeadlinesAndFeedsFiles(markAllHeadlinesAsRead());
				// if (PrefsSettings.getCategoryMode(HeadlinesActivity.this) ==
				// PrefsSettings.CATEGORY_NO_FEEDS_MODE) {
				// startCategoriesActivity();
				// } else {
				// startAllFeedsActivity();
				// }
			}

			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				ErrorAlertDialog.showError(HeadlinesActivity.this,
						R.string.error_mark_feed_as_read);
			}
		};
	}

	protected List<Headline> markAllHeadlinesAsRead() {
		List<Headline> headlines = loadHeadlinesFromFile(feedId);
		for (Headline headline : headlines) {
			headline.unread = false;
		}
		return headlines;
	}

	private void updateHeadlinesAndFeedsFiles(List<Headline> headlines) {
		StorageHeadlinesUtil.save(HeadlinesActivity.this, headlines, feedId);
		if (PrefsSettings.getCategoryMode(this) != PrefsSettings.CATEGORY_NO_FEEDS_MODE) {
			StorageFeedsUtil.save(HeadlinesActivity.this, sessionId, feeds,
					PrefsSettings.getCurrntCategoryId(this));
		} else {
			StorageCategoriesUtil.save(this, sessionId, feeds);
			PrefsUpdater.invalidateFeedsRefreshTime(this);
		}
	}

	@Override
	public int getMenu() {
		return R.menu.headlines_actions;
	}

	@Override
	public int getLayout() {
		return R.layout.list_view;
	}

	@Override
	public void onToggleShowUnread() {
		refreshHeadlines();
	}
}