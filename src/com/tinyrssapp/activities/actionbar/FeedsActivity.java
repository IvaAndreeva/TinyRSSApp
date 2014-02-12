package com.tinyrssapp.activities.actionbar;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.TinyRSSApp.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tinyrssapp.constants.TinyTinySpecificConstants;
import com.tinyrssapp.entities.CustomAdapter;
import com.tinyrssapp.entities.Feed;
import com.tinyrssapp.errorhandling.ErrorAlertDialog;
import com.tinyrssapp.menu.CommonMenu;
import com.tinyrssapp.storage.InternalStorageUtil;
import com.tinyrssapp.storage.StoredPreferencesTinyRSSApp;

/**
 * Created by iva on 2/7/14.
 */
public class FeedsActivity extends TinyRSSAppActivity {
	public static final String ARTICLE_ID = "articleId";
	public static final String CONTENT = "content";
	public static final String CAT_ID = "catId";
	public static final String NO_FEEDS_MSG = "There are no available feeds in here";
	public static final int MINUTES_WITHOUT_FEEDS_REFRESH = 10;
	private static final long MILISECS_WITHOUT_FEEDS_REFRESH = MINUTES_WITHOUT_FEEDS_REFRESH * 60 * 1000;

	private ListView listView;
	private int catId;
	private boolean categoryChanged = false;

	@Override
	protected void onStart() {
		super.onStart();
		loadFeeds();
	}

	private void loadFeeds() {
		Date now = new Date();
		long lastFeedUpdate = StoredPreferencesTinyRSSApp
				.getLastFeedsRefreshTime(this);
		if (now.getTime() - lastFeedUpdate >= MILISECS_WITHOUT_FEEDS_REFRESH
				|| !InternalStorageUtil.hasFeedsInFile(this, sessionId)
				|| categoryChanged) {
			menuLoadingShouldWait = true;
			refreshFeeds();
		} else {
			menuLoadingShouldWait = false;
			showFeeds(loadFeedsFromFile());
		}
	}

	private List<Feed> loadFeedsFromFile() {
		List<Feed> allFeeds = InternalStorageUtil.getFeeds(this, sessionId);
		List<Feed> resultFeeds = allFeeds;
		if (!StoredPreferencesTinyRSSApp.getShowAllPref(this)) {
			resultFeeds = new ArrayList<Feed>();
			for (Feed feed : allFeeds) {
				if (feed.unread > 0) {
					resultFeeds.add(feed);
				}
			}
		}
		return resultFeeds;
	}

	public void initialize() {
		Bundle b = getIntent().getExtras();
		initSessionAndHost(b);
		if (b != null && b.containsKey(CAT_ID)) {
			catId = b.getInt(CAT_ID);
		} else {
			if (catId != 0 && catId != TinyTinySpecificConstants.FRESH_FEED_ID) {
				categoryChanged = true;
			}
			catId = TinyTinySpecificConstants.FRESH_FEED_ID;
		}
		listView = (ListView) findViewById(R.id.listView);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (CommonMenu.checkIsCommonMenuItemSelected(this, item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.list_action_refresh:
			refreshFeeds();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void refreshFeeds() {
		showProgress("Loading feeds...", "");
		InternalStorageUtil.saveFeedPos(this, sessionId, 0);
		AsyncHttpClient client = new AsyncHttpClient();
		try {
			JSONObject jsonParams = new JSONObject();
			jsonParams
					.put(TinyTinySpecificConstants.REQUEST_CAT_ID_PROP, catId);
			jsonParams.put(TinyTinySpecificConstants.OP_PROP,
					TinyTinySpecificConstants.REQUEST_GET_FEEDS_OP_VALUE);
			jsonParams.put(TinyTinySpecificConstants.REQUEST_SESSION_ID_PROP,
					sessionId);
			jsonParams
					.put(TinyTinySpecificConstants.REQUEST_GET_FEEDS_UNREAD_ONLY_PROP,
							!showAll);
			StringEntity entity = new StringEntity(jsonParams.toString());
			client.post(getApplicationContext(), host, entity,
					"application/json", new JsonHttpResponseHandler() {

						@Override
						public void onFailure(Throwable e,
								JSONObject errorResponse) {

							ErrorAlertDialog.showError(FeedsActivity.this,
									R.string.error_refresh_feeds);
							super.onFailure(e, errorResponse);
						}

						@Override
						public void onFinish() {
							hideProgress();
							super.onFinish();
						}

						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							AsyncTask<JSONObject, Void, Void> task = new AsyncTask<JSONObject, Void, Void>() {
								private List<Feed> feeds = new ArrayList<Feed>();

								@Override
								protected Void doInBackground(
										JSONObject... params) {
									try {
										if (params.length < 1
												|| !(params[0] instanceof JSONObject)) {
											ErrorAlertDialog
													.showError(
															FeedsActivity.this,
															R.string.error_something_went_wrong);
											return null;
										}
										StoredPreferencesTinyRSSApp
												.putLastFeedsRefreshTime(
														FeedsActivity.this,
														new Date());
										JSONObject response = (JSONObject) params[0];
										JSONArray contentArray = response
												.getJSONArray(TinyTinySpecificConstants.RESPONSE_CONTENT_PROP);
										for (int i = 0; i < contentArray
												.length(); i++) {
											JSONObject feedJson = contentArray
													.getJSONObject(i);
											Feed feed = (new Feed())
													.setFeedUrl(
															feedJson.getString(TinyTinySpecificConstants.RESPONSE_FEED_URL_PROP))
													.setCatId(
															feedJson.getInt(TinyTinySpecificConstants.REQUEST_CAT_ID_PROP))
													.setHasIcon(
															feedJson.getBoolean(TinyTinySpecificConstants.RESPONSE_FEED_HAS_ICON_PROP))
													.setId(feedJson
															.getInt(TinyTinySpecificConstants.RESPONSE_FEED_ID_PROP))
													.setLastUpdated(
															feedJson.getLong(TinyTinySpecificConstants.RESPONSE_FEED_LAST_UPDATED_PROP))
													.setOrderId(
															feedJson.getInt(TinyTinySpecificConstants.RESPONSE_FEED_ORDER_ID_PROP))
													.setTitle(
															feedJson.getString(TinyTinySpecificConstants.RESPONSE_FEED_TITLE_PROP))
													.setUnread(
															feedJson.getInt(TinyTinySpecificConstants.RESPONSE_FEED_UNREAD_PROP));
											feeds.add(feed);
										}
									} catch (JSONException e) {
										e.printStackTrace();
									}
									return null;
								}

								@Override
								protected void onPostExecute(Void aVoid) {
									super.onPostExecute(aVoid);
									InternalStorageUtil.saveFeeds(
											FeedsActivity.this, sessionId,
											feeds);
									showFeeds(feeds);
								}
							};
							task.execute(new JSONObject[] { response });
						}
					});
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void showFeeds(List<Feed> feeds) {
		if (menuLoadingShouldWait) {
			inflateMenu();
		}
		if (feeds.size() == 0) {
			feeds.add((new Feed()).setTitle(NO_FEEDS_MSG).setUnread(0));
			listView.setEnabled(false);
		} else {
			listView.setEnabled(true);
		}
		ArrayAdapter<Feed> feedsAdapter = new CustomAdapter<Feed>(this,
				R.layout.feed_layout, R.id.feed_data, R.id.feed_unread_count,
				feeds);
		listView.setAdapter(feedsAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				InternalStorageUtil.saveFeedPos(FeedsActivity.this, sessionId,
						position);
				startHeadlinesActivity((Feed) parent.getAdapter().getItem(
						position));
			}
		});
		if (InternalStorageUtil.hasFeedPosInFile(this, sessionId)) {
			listView.setSelection(InternalStorageUtil.getFeedPos(this,
					sessionId));
		}
		feedsAdapter.notifyDataSetChanged();
	}

	@Override
	public void onBackPressed() {
		if (StoredPreferencesTinyRSSApp.getCategoriesUsed(this)) {
			startCategoriesActivity();
		}
		super.onBackPressed();
	}

	@Override
	public int getMenu() {
		return R.menu.feeds_actions;
	}

	@Override
	public int getLayout() {
		return R.layout.list_view;
	}

	@Override
	public void onToggleShowUnread() {
		refreshFeeds();
	}
}