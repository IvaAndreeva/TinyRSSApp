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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.TinyRSSApp.R;
import com.tinyrssapp.constants.TinyTinySpecificConstants;
import com.tinyrssapp.entities.CustomAdapter;
import com.tinyrssapp.entities.Feed;
import com.tinyrssapp.errorhandling.ErrorAlertDialog;
import com.tinyrssapp.menu.CommonMenu;
import com.tinyrssapp.request.RequestBuilder;
import com.tinyrssapp.request.RequestParamsBuilder;
import com.tinyrssapp.response.ResponseHandler;
import com.tinyrssapp.storage.internal.StorageCategoriesUtil;
import com.tinyrssapp.storage.prefs.PrefsSettings;
import com.tinyrssapp.storage.prefs.PrefsUpdater;

public class CategoriesActivity extends TinyRSSAppActivity {
	public static final String NO_CATEGORIES_MSG = "There are no available categories in here";
	public static final int MINUTES_WITHOUT_CATEGORIES_REFRESH = 10;
	private static final long MILISECS_WITHOUT_CATEGORIES_REFRESH = MINUTES_WITHOUT_CATEGORIES_REFRESH * 60 * 1000;

	private ListView listView;

	@Override
	public void initialize() {
		initSessionAndHost(getIntent().getExtras());
		listView = (ListView) findViewById(R.id.listView);
	}

	@Override
	protected void onStart() {
		super.onStart();
		setTitle(R.string.categories_title);
		loadCategories();
	}

	private void loadCategories() {
		Date now = new Date();
		long lastFeedUpdate = PrefsUpdater.getLastCategoriesRefreshTime(this);
		if (now.getTime() - lastFeedUpdate >= MILISECS_WITHOUT_CATEGORIES_REFRESH
				|| !StorageCategoriesUtil.hasInFile(this, sessionId)) {
			menuLoadingShouldWait = true;
			refreshCategories();
		} else {
			menuLoadingShouldWait = false;
			showCategories(loadCategoriesFromFile());
		}
	}

	private void refreshCategories() {
		showProgress("Loading categories...", "");
		StorageCategoriesUtil.savePos(this, sessionId, 0);
		ResponseHandler handler = getCategoriesResponseHandler();
		RequestBuilder.makeRequest(this, host,
				RequestParamsBuilder.paramsGetCategories(sessionId, showAll),
				handler);
	}

	private List<Feed> loadCategoriesFromFile() {
		List<Feed> allCategories = StorageCategoriesUtil.get(this, sessionId);
		List<Feed> resultCategories = allCategories;
		if (!PrefsSettings.getShowAllPref(this)) {
			resultCategories = new ArrayList<Feed>();
			for (Feed cat : allCategories) {
				if (cat.unread > 0) {
					resultCategories.add(cat);
				}
			}
		}
		return resultCategories;
	}

	private void showCategories(List<Feed> categories) {
		if (menuLoadingShouldWait) {
			inflateMenu();
		}
		if (categories.size() == 0) {
			categories.add((new Feed()).setTitle(NO_CATEGORIES_MSG)
					.setUnread(0).setId(-3));
			listView.setEnabled(false);
		} else {
			listView.setEnabled(true);
		}
		ArrayAdapter<Feed> categoriesAdapter = new CustomAdapter<Feed>(this,
				R.layout.feed_layout, R.id.feed_data, R.id.feed_unread_count,
				categories);
		listView.setAdapter(categoriesAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				StorageCategoriesUtil.savePos(CategoriesActivity.this,
						sessionId, position);
				Feed selectedCategory = (Feed) parent.getAdapter().getItem(
						position);
				PrefsSettings.putCurrntCategoryId(CategoriesActivity.this,
						selectedCategory.id);
				if (PrefsSettings.getCategoryMode(CategoriesActivity.this) == PrefsSettings.CATEGORY_SHOW_FEEDS_MODE) {
					startAllFeedsActivity(selectedCategory.id);
				} else {
					startHeadlinesActivity(selectedCategory);
				}
			}
		});
		if (StorageCategoriesUtil.hasPosInFile(this, sessionId)) {
			listView.setSelection(StorageCategoriesUtil.getPos(this, sessionId));
		}
		categoriesAdapter.notifyDataSetChanged();
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
						showCategories(categories);
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
			refreshCategories();
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
		refreshCategories();
	}

	@Override
	public void onHideCategories() {
		super.onHideCategories();
		startAllFeedsActivity();
	}

}
