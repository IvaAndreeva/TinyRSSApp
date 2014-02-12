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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.TinyRSSApp.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tinyrssapp.constants.TinyTinySpecificConstants;
import com.tinyrssapp.entities.Category;
import com.tinyrssapp.entities.CustomAdapter;
import com.tinyrssapp.errorhandling.ErrorAlertDialog;
import com.tinyrssapp.menu.CommonMenu;
import com.tinyrssapp.storage.InternalStorageUtil;
import com.tinyrssapp.storage.StoredPreferencesTinyRSSApp;

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
		loadCategories();
	}

	private void loadCategories() {
		Date now = new Date();
		long lastFeedUpdate = StoredPreferencesTinyRSSApp
				.getLastCategoriesRefreshTime(this);
		if (now.getTime() - lastFeedUpdate >= MILISECS_WITHOUT_CATEGORIES_REFRESH
				|| !InternalStorageUtil.hasCategoriesInFile(this, sessionId)) {
			menuLoadingShouldWait = true;
			refreshCategories();
		} else {
			menuLoadingShouldWait = false;
			showCategories(loadCategoriesFromFile());
		}
	}

	private void refreshCategories() {
		showProgress("Loading categories...", "");
		InternalStorageUtil.saveCategoryPos(this, sessionId, 0);
		AsyncHttpClient client = new AsyncHttpClient();
		try {
			JSONObject jsonParams = new JSONObject();
			jsonParams.put(TinyTinySpecificConstants.OP_PROP,
					TinyTinySpecificConstants.REQUEST_GET_CATEGORIES_OP_VALUE);
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

							ErrorAlertDialog.showError(CategoriesActivity.this,
									R.string.error_refresh_categories);
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
								private List<Category> categories = new ArrayList<Category>();

								@Override
								protected Void doInBackground(
										JSONObject... params) {
									try {
										if (params.length < 1
												|| !(params[0] instanceof JSONObject)) {
											ErrorAlertDialog
													.showError(
															CategoriesActivity.this,
															R.string.error_something_went_wrong);
											return null;
										}
										StoredPreferencesTinyRSSApp
												.putLastCategoriesRefreshTime(
														CategoriesActivity.this,
														new Date());
										JSONObject response = (JSONObject) params[0];
										JSONArray contentArray = response
												.getJSONArray(TinyTinySpecificConstants.RESPONSE_CONTENT_PROP);
										for (int i = 0; i < contentArray
												.length(); i++) {
											JSONObject catJson = contentArray
													.getJSONObject(i);
											Category cat = new Category()
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
									InternalStorageUtil.saveCategories(
											CategoriesActivity.this, sessionId,
											categories);
									showCategories(categories);
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

	private List<Category> loadCategoriesFromFile() {
		List<Category> allCategories = InternalStorageUtil.getCategories(this,
				sessionId);
		List<Category> resultCategories = allCategories;
		if (!StoredPreferencesTinyRSSApp.getShowAllPref(this)) {
			resultCategories = new ArrayList<Category>();
			for (Category cat : allCategories) {
				if (cat.unread > 0) {
					resultCategories.add(cat);
				}
			}
		}
		return resultCategories;
	}

	private void showCategories(List<Category> categories) {
		if (menuLoadingShouldWait) {
			inflateMenu();
		}
		if (categories.size() == 0) {
			categories.add((new Category()).setTitle(NO_CATEGORIES_MSG)
					.setUnread(0).setId(-3));
			listView.setEnabled(false);
		} else {
			listView.setEnabled(true);
		}
		ArrayAdapter<Category> categoriesAdapter = new CustomAdapter<Category>(
				this, R.layout.feed_layout, R.id.feed_data,
				R.id.feed_unread_count, categories);
		listView.setAdapter(categoriesAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				InternalStorageUtil.saveCategoryPos(CategoriesActivity.this,
						sessionId, position);
				Category selectedCategory = (Category) parent.getAdapter()
						.getItem(position);
				startAllFeedsActivity(selectedCategory.id);
			}
		});
		if (InternalStorageUtil.hasCategoryPosInFile(this, sessionId)) {
			listView.setSelection(InternalStorageUtil.getCategoryPos(this,
					sessionId));
		}
		categoriesAdapter.notifyDataSetChanged();
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
		default:
			return super.onOptionsItemSelected(item);
		}
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
		refreshCategories();
	}

	@Override
	public void onHideCategories() {
		super.onHideCategories();
		startAllFeedsActivity();
	}

}
