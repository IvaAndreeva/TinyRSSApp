package com.tinyrssreader.activities.actionbar;

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
import android.widget.ListView;

import com.tinyrssreader.R;
import com.tinyrssreader.constants.TinyTinySpecificConstants;
import com.tinyrssreader.entities.Entity;
import com.tinyrssreader.entities.Feed;
import com.tinyrssreader.entities.Headline;
import com.tinyrssreader.errorhandling.ErrorAlertDialog;
import com.tinyrssreader.menu.CommonMenu;
import com.tinyrssreader.request.RequestBuilder;
import com.tinyrssreader.request.RequestParamsBuilder;
import com.tinyrssreader.response.ResponseHandler;
import com.tinyrssreader.storage.internal.InternalStorageUtil;
import com.tinyrssreader.storage.internal.StorageCategoriesUtil;
import com.tinyrssreader.storage.internal.StorageFeedsUtil;
import com.tinyrssreader.storage.internal.StorageHeadlinesUtil;
import com.tinyrssreader.storage.internal.StorageParams;
import com.tinyrssreader.storage.prefs.PrefsSettings;
import com.tinyrssreader.storage.prefs.PrefsUpdater;

/**
 * Created by iva on 2/7/14.
 */
public class HeadlinesActivity extends TinyRSSReaderListActivity {
	public static final String NO_HEADLINES_MSG = "There are no available headlines in here";
	public static final int MINUTES_WITHOUT_HEADLINES_REFRESH = 10;
	private static final long MILISECS_WITHOUT_HEADLINES_REFRESH = MINUTES_WITHOUT_HEADLINES_REFRESH * 60 * 1000;

	private int feedId;
	private Feed feed;
	private List<Feed> feeds;
	private StorageHeadlinesUtil util = new StorageHeadlinesUtil();

	@Override
	protected void onStart() {
		super.onStart();
		load();
	}

	public void initialize() {
		Bundle b = getIntent().getExtras();
		initSessionAndHost(b);
		if (b != null) {
			feedId = b.getInt(FEED_ID_TO_LOAD);
			feed = initParent();
		} else {
			feedId = 0;
			feed = new Feed();
		}
		setTitle(feed.title);
		listView = (ListView) findViewById(R.id.listView);
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
			refresh();
			return true;
		case R.id.list_action_mark_all_as_read:
			markFeedAsRead(feedId, host, sessionId, getApplicationContext());
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private ResponseHandler getHeadlinesResponseHandler() {
		final String msg = "Parsing headlines...";
		return new ResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				progress.show(msg);
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
							if (checkResponseForError((JSONObject) params[0])) {
								refresh();
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
							ErrorAlertDialog.showError(HeadlinesActivity.this,
									"Something went wrong when refreshing");
						}
						progress.hide(msg);
						return null;
					}

					@Override
					protected void onPostExecute(Void aVoid) {
						super.onPostExecute(aVoid);
						String msg = "Showing headlines ...";
						progress.show("Showing headlines...");
						show(headlines);
						progress.hide(msg);
					}
				};
				task.execute(new JSONObject[] { response });
			}

			@Override
			public void onFinish() {
				setEnabledRefresh(true);
				progress.hide(msg);
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
		feed.unread = 0;
		updateHeadlinesAndFeedsFiles(markAllHeadlinesAsRead());
		if (PrefsSettings.getCategoryMode(HeadlinesActivity.this) == PrefsSettings.CATEGORY_NO_FEEDS_MODE) {
			startCategoriesActivity();
		} else {
			startAllFeedsActivity();
		}
		boolean parentIsCat = PrefsSettings.getCategoryMode(this) == PrefsSettings.CATEGORY_NO_FEEDS_MODE;
		RequestBuilder.makeRequestWithProgress(this, host, RequestParamsBuilder
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
		List<Headline> headlines = loadFromFile(getParamsLoadFromFile());
		for (Headline headline : headlines) {
			headline.unread = false;
		}
		return headlines;
	}

	private void updateHeadlinesAndFeedsFiles(List<Headline> headlines) {
		// Need to update the feeds with the modified parent feed (something
		// goes wrong when updating with parent class, probably cause it uses
		// Entity)
		feeds = updateOldFeedsWithModifiedNewOne();
		if (PrefsSettings.getCategoryMode(this) != PrefsSettings.CATEGORY_NO_FEEDS_MODE) {
			StorageFeedsUtil.save(HeadlinesActivity.this, sessionId, feeds,
					PrefsSettings.getCurrentCategoryId(this));
		} else {
			StorageCategoriesUtil.save(this, sessionId, feeds);
			PrefsUpdater.invalidateFeedsRefreshTime(this);
		}
		StorageHeadlinesUtil.save(HeadlinesActivity.this, headlines, feedId);
	}

	private List<Feed> updateOldFeedsWithModifiedNewOne() {
		List<Feed> newFeeds = new ArrayList<Feed>();
		for (Feed oldFeed : feeds) {
			if (oldFeed.id == this.feed.id) {
				newFeeds.add(this.feed);
			} else {
				newFeeds.add(oldFeed);
			}
		}
		return newFeeds;
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
		refresh();
	}

	@Override
	public long getMilisecsWithoutRefresh() {
		return MILISECS_WITHOUT_HEADLINES_REFRESH;
	}

	@Override
	public InternalStorageUtil getUtil() {
		return util;
	}

	@Override
	public StorageParams getParamsLoadFromFile() {
		return new StorageParams().setFeedId(feedId);
	}

	@Override
	public StorageParams getParamsHasInFile() {
		return new StorageParams().setFeedId(feedId);
	}

	@Override
	public StorageParams getParamsHasPosInFile() {
		return new StorageParams().setFeedId(feedId);
	}

	@Override
	public StorageParams getParamsGetPosFromFile() {
		return new StorageParams().setFeedId(feedId);
	}

	@Override
	public String getEmptyListMsg() {
		return NO_HEADLINES_MSG;
	}

	@Override
	public <T extends Entity> void onShow(List<T> entities) {
		List<Headline> headlines = new ArrayList<Headline>();
		if (entities.size() > 0 && entities.get(0) instanceof Headline) {
			for (T entity : entities) {
				headlines.add((Headline) entity);
			}
		}
		if (feed.id == TinyTinySpecificConstants.STARRED_FEED_ID
				&& PrefsSettings.getCategoryMode(this) == PrefsSettings.CATEGORY_NO_FEEDS_MODE) {
			feed.unread = 0;
		}
		updateHeadlinesAndFeedsFiles(headlines);
	}

	@Override
	public int getListItemLayout() {
		return R.layout.headline_layout;
	}

	@Override
	public int getListItemDataId() {
		return R.id.headline_data;
	}

	@Override
	public int getListItemCountId() {
		return -1;
	}

	@Override
	public <T extends Entity> void onListItemClick(int position, T selected) {
		Headline currHeadline = (Headline) selected;
		StorageHeadlinesUtil.savePos(HeadlinesActivity.this, feedId, position);
		List<Headline> headlines = StorageHeadlinesUtil.get(this, feedId);
		startArticleActivity(currHeadline, headlines, getTitle().toString());
	}

	@Override
	public void refresh() {
		String msg = "Refreshing...";
		progress.show(msg);
		setEnabledRefresh(false);
		StorageHeadlinesUtil.savePos(this, feedId, 0);
		feedId = feed.id;
		ResponseHandler handler = getHeadlinesResponseHandler();
		progress.hide(msg);
		boolean parentIsCat = PrefsSettings.getCategoryMode(this) == PrefsSettings.CATEGORY_NO_FEEDS_MODE;
		RequestBuilder.makeRequestWithProgress(this, host, RequestParamsBuilder
				.paramsGetHeadlines(sessionId, feedId, parentIsCat,
						getViewMode(), feed.unread), handler);
	}

	private Feed initParent() {
		if (PrefsSettings.getCategoryMode(this) == PrefsSettings.CATEGORY_NO_FEEDS_MODE) {// ||
																							// feedId
																							// ==
			// TinyTinySpecificConstants.STARRED_FEED_ID)
			// {
			feeds = StorageCategoriesUtil.get(this, sessionId);
		} else {
			feeds = StorageFeedsUtil.get(this, sessionId,
					PrefsSettings.getCurrentCategoryId(this));
		}
		for (Feed feed : feeds) {
			if (feed.id == feedId) {
				return feed;
			}
		}
		return new Feed();
	}

	public Feed getParentFeed() {
		return feed;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Headline getEmtpyObj() {
		return new Headline();
	}

	@Override
	public long getLastRefreshTime() {
		return PrefsUpdater.getLastHeadlinesRefreshTime(this);
	}
}