package com.tinyrssapp.activities.actionbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
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
import com.tinyrssapp.storage.internal.StorageParams;
import com.tinyrssapp.storage.prefs.PrefsSettings;
import com.tinyrssapp.storage.prefs.PrefsUpdater;

public class CategoriesActivity extends TinyRSSAppListActivity {
	public static final String NO_CATEGORIES_MSG = "There are no available categories in here";
	public static final int MINUTES_WITHOUT_CATEGORIES_REFRESH = 10;
	private static final long MILISECS_WITHOUT_CATEGORIES_REFRESH = MINUTES_WITHOUT_CATEGORIES_REFRESH * 60 * 1000;
	private StorageCategoriesUtil util = new StorageCategoriesUtil();

	@Override
	public void initialize() {
		initSessionAndHost(getIntent().getExtras());
		listView = (ListView) findViewById(R.id.listView);
	}

	@Override
	protected void onStart() {
		super.onStart();
		setTitle(R.string.categories_title);
		load();
	}

	private ResponseHandler getCategoriesResponseHandler() {
		return new ResponseHandler() {

			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				ErrorAlertDialog.showError(CategoriesActivity.this,
						R.string.error_refresh_categories);
			}

			@Override
			public void onFinish() {
				hideProgress();
			}

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				AsyncTask<JSONObject, Void, Void> task = new AsyncTask<JSONObject, Void, Void>() {
					private List<Feed> categories = new ArrayList<Feed>();

					@Override
					protected Void doInBackground(JSONObject... params) {
						try {
							if (params.length < 1
									|| !(params[0] instanceof JSONObject)) {
								ErrorAlertDialog.showError(
										CategoriesActivity.this,
										R.string.error_something_went_wrong);
								return null;
							}
							PrefsUpdater.putLastCategoriesRefreshTime(
									CategoriesActivity.this, new Date());
							JSONObject response = (JSONObject) params[0];
							JSONArray contentArray = response
									.getJSONArray(TinyTinySpecificConstants.RESPONSE_CONTENT_PROP);
							for (int i = 0; i < contentArray.length(); i++) {
								JSONObject catJson = contentArray
										.getJSONObject(i);
								Feed cat = new Feed()
										.setTitle(
												catJson.getString(TinyTinySpecificConstants.RESPONSE_FEED_TITLE_PROP))
										.setId(catJson
												.getInt(TinyTinySpecificConstants.RESPONSE_FEED_ID_PROP))
										.setUnread(
												catJson.getInt(TinyTinySpecificConstants.RESPONSE_FEED_UNREAD_PROP));
								categories.add(cat);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						return null;
					}

					@Override
					protected void onPostExecute(Void aVoid) {
						super.onPostExecute(aVoid);
						StorageCategoriesUtil.save(CategoriesActivity.this,
								sessionId, categories);
						show(categories);
					}
				};
				task.execute(new JSONObject[] { response });
			}
		};
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
		case R.id.show_feeds_in_cats:
			PrefsSettings.putCategoryMode(this,
					PrefsSettings.CATEGORY_SHOW_FEEDS_MODE);
			return true;
		case R.id.hide_feeds_in_cats:
			PrefsSettings.putCategoryMode(this,
					PrefsSettings.CATEGORY_NO_FEEDS_MODE);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public int getMenu() {
		return R.menu.categories_actions;
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
		return MILISECS_WITHOUT_CATEGORIES_REFRESH;
	}

	@Override
	public InternalStorageUtil getUtil() {
		return util;
	}

	@Override
	public StorageParams getParamsLoadFromFile() {
		return new StorageParams().setSessId(sessionId);
	}

	@Override
	public StorageParams getParamsHasInFile() {
		return new StorageParams().setSessId(sessionId);
	}

	@Override
	public StorageParams getParamsHasPosInFile() {
		return new StorageParams().setSessId(sessionId);
	}

	@Override
	public StorageParams getParamsGetPosFromFile() {
		return new StorageParams().setSessId(sessionId);
	}

	@Override
	public String getEmptyListMsg() {
		return NO_CATEGORIES_MSG;
	}

	@Override
	public <T extends Entity> void onShow(List<T> entities) {
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
		StorageCategoriesUtil.savePos(CategoriesActivity.this, sessionId,
				position);
		Feed selectedCategory = (Feed) selected;
		PrefsSettings.putCurrentCategoryId(CategoriesActivity.this,
				selectedCategory.id);
		if (PrefsSettings.getCategoryMode(CategoriesActivity.this) == PrefsSettings.CATEGORY_SHOW_FEEDS_MODE) {
			startAllFeedsActivity();
		} else {
			startHeadlinesActivity(selectedCategory);
		}
	}

	@Override
	public void refresh() {
		showProgress("Loading categories...", "");
		StorageCategoriesUtil.savePos(this, sessionId, 0);
		ResponseHandler handler = getCategoriesResponseHandler();
		RequestBuilder.makeRequest(this, host,
				RequestParamsBuilder.paramsGetCategories(sessionId, showAll),
				handler);
	}

	@Override
	public Feed getParentFeed() {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Feed getEmtpyObj() {
		return new Feed().setId(-3);
	}
}
