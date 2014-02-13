package com.tinyrssapp.activities.actionbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.TinyRSSApp.R;
import com.tinyrssapp.constants.TinyTinySpecificConstants;
import com.tinyrssapp.entities.Entity;
import com.tinyrssapp.entities.Feed;
import com.tinyrssapp.errorhandling.ErrorAlertDialog;
import com.tinyrssapp.menu.CommonMenu;
import com.tinyrssapp.request.RequestBuilder;
import com.tinyrssapp.request.RequestParamsBuilder;
import com.tinyrssapp.response.ResponseHandler;
import com.tinyrssapp.storage.internal.InternalStorageUtil;
import com.tinyrssapp.storage.internal.StorageCategoriesUtil;
import com.tinyrssapp.storage.internal.StorageFeedsUtil;
import com.tinyrssapp.storage.internal.StorageParams;
import com.tinyrssapp.storage.prefs.PrefsSettings;
import com.tinyrssapp.storage.prefs.PrefsUpdater;

/**
 * Created by iva on 2/7/14.
 */
public class FeedsActivity extends TinyRSSAppListActivity {
	public static final String ARTICLE_ID = "articleId";
	public static final String CONTENT = "content";
	public static final String NO_FEEDS_MSG = "There are no available feeds in here";
	public static final int MINUTES_WITHOUT_FEEDS_REFRESH = 10;
	private static final long MILISECS_WITHOUT_FEEDS_REFRESH = MINUTES_WITHOUT_FEEDS_REFRESH * 60 * 1000;
	private Feed category;
	private List<Feed> categories;
	private StorageFeedsUtil util = new StorageFeedsUtil();

	@Override
	protected void onStart() {
		super.onStart();
		load();
	}

	public void initialize() {
		Bundle b = getIntent().getExtras();
		initSessionAndHost(b);
		categories = StorageCategoriesUtil.get(this, sessionId);
		category = initParent();
		if (PrefsSettings.getCategoryMode(this) == PrefsSettings.CATEGORY_NO_MODE
				&& PrefsSettings.getCurrentCategoryId(this) != TinyTinySpecificConstants.FRESH_FEED_ID) {
			categoryChanged = true;
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
			refresh();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private ResponseHandler getFeedsResponseHandler() {
		return new ResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				AsyncTask<JSONObject, Void, Void> task = new AsyncTask<JSONObject, Void, Void>() {
					private List<Feed> feeds = new ArrayList<Feed>();

					@Override
					protected Void doInBackground(JSONObject... params) {
						try {
							if (params.length < 1
									|| !(params[0] instanceof JSONObject)) {
								ErrorAlertDialog.showError(FeedsActivity.this,
										R.string.error_something_went_wrong);
								return null;
							}
							PrefsUpdater.putLastFeedsRefreshTime(
									FeedsActivity.this, new Date());
							JSONObject response = (JSONObject) params[0];
							JSONArray contentArray = response
									.getJSONArray(TinyTinySpecificConstants.RESPONSE_CONTENT_PROP);
							for (int i = 0; i < contentArray.length(); i++) {
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
						StorageFeedsUtil.save(FeedsActivity.this, sessionId,
								feeds, category.id);
						show(feeds);
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
				ErrorAlertDialog.showError(FeedsActivity.this,
						R.string.error_refresh_feeds);
			}
		};
	}

	@Override
	public void onBackPressed() {
		if (PrefsSettings.getCategoryMode(this) != PrefsSettings.CATEGORY_NO_MODE) {
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
		refresh();
	}

	@Override
	public long getMilisecsWithoutRefresh() {
		return MILISECS_WITHOUT_FEEDS_REFRESH;
	}

	@Override
	public InternalStorageUtil getUtil() {
		return util;
	}

	@Override
	public StorageParams getParamsLoadFromFile() {
		return new StorageParams().setSessId(sessionId).setCatId(category.id);
	}

	@Override
	public StorageParams getParamsHasInFile() {
		return new StorageParams().setSessId(sessionId).setCatId(category.id);
	}

	@Override
	public StorageParams getParamsHasPosInFile() {
		return new StorageParams().setSessId(sessionId).setCatId(category.id);
	}

	@Override
	public StorageParams getParamsGetPosFromFile() {
		return new StorageParams().setSessId(sessionId).setCatId(category.id);
	}

	@Override
	public String getEmptyListMsg() {
		return NO_FEEDS_MSG;
	}

	@Override
	public <T extends Entity> void onShow(List<T> entities) {
		if (PrefsSettings.getCategoryMode(this) != PrefsSettings.CATEGORY_NO_MODE) {
			StorageCategoriesUtil.save(this, sessionId, categories);
		}
	}

	@Override
	public int getListItemLayout() {
		return R.layout.feed_layout;
	}

	@Override
	public int getListItemDataId() {
		return R.id.feed_data;
	}

	@Override
	public int getListItemCountId() {
		return R.id.feed_unread_count;
	}

	@Override
	public <T extends Entity> void onListItemClick(int position, T selected) {
		StorageFeedsUtil.savePos(FeedsActivity.this, sessionId, position,
				category.id);
		startHeadlinesActivity((Feed) selected);
	}

	@Override
	public void refresh() {
		showProgress("Loading feeds...", "");
		StorageFeedsUtil.savePos(this, sessionId, 0, category.id);
		ResponseHandler handler = getFeedsResponseHandler();
		RequestBuilder.makeRequest(this, host, RequestParamsBuilder
				.paramsGetFeeds(sessionId, showAll,
						PrefsSettings.getCurrentCategoryId(this)), handler);
	}

	private Feed initParent() {
		for (Feed category : categories) {
			if (category.id == PrefsSettings.getCurrentCategoryId(this)) {
				return category;
			}
		}
		return new Feed().setId(TinyTinySpecificConstants.FRESH_FEED_ID);
	}

	public Feed getParentFeed() {
		return category;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Feed getEmtpyObj() {
		return new Feed();
	}
}