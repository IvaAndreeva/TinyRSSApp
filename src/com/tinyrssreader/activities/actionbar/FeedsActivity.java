package com.tinyrssreader.activities.actionbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;

import com.tinyrssreader.R;
import com.tinyrssreader.constants.TinyTinySpecificConstants;
import com.tinyrssreader.entities.Entity;
import com.tinyrssreader.entities.Feed;
import com.tinyrssreader.errorhandling.ErrorAlertDialog;
import com.tinyrssreader.menu.CommonMenu;
import com.tinyrssreader.request.RequestBuilder;
import com.tinyrssreader.request.RequestParamsBuilder;
import com.tinyrssreader.response.ResponseHandler;
import com.tinyrssreader.storage.internal.InternalStorageUtil;
import com.tinyrssreader.storage.internal.StorageCategoriesUtil;
import com.tinyrssreader.storage.internal.StorageFeedsUtil;
import com.tinyrssreader.storage.internal.StorageParams;
import com.tinyrssreader.storage.prefs.PrefsSettings;
import com.tinyrssreader.storage.prefs.PrefsUpdater;

/**
 * Created by iva on 2/7/14.
 */
public class FeedsActivity extends TinyRSSReaderListActivity {
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
		if (PrefsSettings.getCategoryMode(this) != PrefsSettings.CATEGORY_NO_MODE) {
			setTitle(category.title);
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
				try {
					List<Feed> feeds = new ArrayList<Feed>();
					PrefsUpdater.putLastFeedsRefreshTime(FeedsActivity.this,
							new Date());
					JSONArray contentArray = response
							.getJSONArray(TinyTinySpecificConstants.RESPONSE_CONTENT_PROP);
					for (int i = 0; i < contentArray.length(); i++) {
						JSONObject feedJson = contentArray.getJSONObject(i);
						Feed feed = (new Feed())
								.setCatId(
										feedJson.getInt(TinyTinySpecificConstants.REQUEST_CAT_ID_PROP))
								.setId(feedJson
										.getInt(TinyTinySpecificConstants.RESPONSE_FEED_ID_PROP))
								.setTitle(
										feedJson.getString(TinyTinySpecificConstants.RESPONSE_FEED_TITLE_PROP))
								.setUnread(
										feedJson.getInt(TinyTinySpecificConstants.RESPONSE_FEED_UNREAD_PROP));
						setOptionalFeedParams(feed, feedJson);
						if (!feedIsSpecial(feed.id)) {
							feeds.add(feed);
						}
					}
					StorageFeedsUtil.save(FeedsActivity.this, sessionId, feeds,
							category.id);
					show(feeds);
					hideProgress();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			private boolean feedIsSpecial(int id) {
				return id <= 0
						&& id != TinyTinySpecificConstants.STARRED_FEED_ID;
			}

			@Override
			public void onFinish() {
			}

			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				ErrorAlertDialog.showError(FeedsActivity.this,
						R.string.error_refresh_feeds);
			}
		};
	}

	private void setOptionalFeedParams(Feed feed, JSONObject feedJson)
			throws JSONException {
		if (feedJson.has(TinyTinySpecificConstants.RESPONSE_FEED_URL_PROP)) {
			feed.setFeedUrl(feedJson
					.getString(TinyTinySpecificConstants.RESPONSE_FEED_URL_PROP));
		}
		if (feedJson.has(TinyTinySpecificConstants.RESPONSE_FEED_HAS_ICON_PROP)) {
			feed.setHasIcon(feedJson
					.getBoolean(TinyTinySpecificConstants.RESPONSE_FEED_HAS_ICON_PROP));
		}
		if (feedJson
				.has(TinyTinySpecificConstants.RESPONSE_FEED_LAST_UPDATED_PROP)) {
			feed.setLastUpdated(feedJson
					.getLong(TinyTinySpecificConstants.RESPONSE_FEED_LAST_UPDATED_PROP));
		}
		if (feedJson.has(TinyTinySpecificConstants.RESPONSE_FEED_ORDER_ID_PROP)) {
			feed.setOrderId(feedJson
					.getInt(TinyTinySpecificConstants.RESPONSE_FEED_ORDER_ID_PROP));
		}
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
			if (category.id == TinyTinySpecificConstants.STARRED_FEED_ID) {
				category.unread = 0;
			}
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
		PrefsUpdater.invalidateHeadlinesRefreshTime(this);
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

	@Override
	public long getLastRefreshTime() {
		return PrefsUpdater.getLastFeedsRefreshTime(this);
	}
}