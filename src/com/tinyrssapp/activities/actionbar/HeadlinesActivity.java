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

import android.content.Context;
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
import com.tinyrssapp.entities.Headline;
import com.tinyrssapp.errorhandling.ErrorAlertDialog;
import com.tinyrssapp.menu.CommonMenu;
import com.tinyrssapp.storage.InternalStorageUtil;
import com.tinyrssapp.storage.StoredPreferencesTinyRSSApp;

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
		feeds = InternalStorageUtil.getFeeds(this, sessionId);
		for (Feed feed : feeds) {
			if (feed.id == feedId) {
				return feed;
			}
		}
		return new Feed();
	}

	@Override
	public void onBackPressed() {
		startAllFeedsActivity();
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
		long lastHeadlinesUpdate = StoredPreferencesTinyRSSApp
				.getLastHeadlinesRefreshTime(this);
		if (now.getTime() - lastHeadlinesUpdate >= MILISECS_WITHOUT_HEADLINES_REFRESH
				|| !InternalStorageUtil.hasHeadlinesInFile(this, feedId)) {
			menuLoadingShouldWait = true;
			refreshHeadlines();
		} else {
			menuLoadingShouldWait = false;
			showHeadlines(loadHeadlinesFromFile(feedId));
		}
	}

	private void refreshHeadlines() {
		try {
			showProgress("Loading headlines...", "");
			InternalStorageUtil.saveHeadlinePos(this, feedId, 0);
			feedId = feed.id;
			AsyncHttpClient client = new AsyncHttpClient();
			JSONObject jsonParams = new JSONObject();
			jsonParams.put(
					TinyTinySpecificConstants.REQUEST_HEADLINES_FEED_ID_PROP,
					feed.id);
			jsonParams
					.put(TinyTinySpecificConstants.REQUEST_HEADLINES_LIMIT_PROP,
							TinyTinySpecificConstants.REQUEST_HEADLINES_LIMIT_UNDEFINED_VALUE);
			jsonParams
					.put(TinyTinySpecificConstants.OP_PROP,
							TinyTinySpecificConstants.REQUEST_HEADLINES_GET_HEADLINES_OP_VALUE);
			jsonParams
					.put(TinyTinySpecificConstants.REQUEST_HEADLINES_SHOW_CONTENT_PROP,
							"true");
			jsonParams.put(TinyTinySpecificConstants.REQUEST_SESSION_ID_PROP,
					sessionId);
			jsonParams.put(
					TinyTinySpecificConstants.REQUEST_HEADLINES_VIEW_MODE_PROP,
					getViewMode());
			StringEntity entity = new StringEntity(jsonParams.toString());
			client.post(getApplicationContext(), host, entity,
					"application/json", new JsonHttpResponseHandler() {

						@Override
						public void onFailure(Throwable e,
								JSONObject errorResponse) {
							ErrorAlertDialog.showError(HeadlinesActivity.this,
									R.string.error_refresh_headlines);
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
								private List<Headline> headlines = new ArrayList<Headline>();

								@Override
								protected Void doInBackground(
										JSONObject... params) {
									try {
										if (params.length < 1
												|| !(params[0] instanceof JSONObject)) {
											ErrorAlertDialog
													.showError(
															HeadlinesActivity.this,
															R.string.error_something_went_wrong);
											return null;
										}
										StoredPreferencesTinyRSSApp
												.putLastHeadlinesRefreshTime(
														HeadlinesActivity.this,
														new Date());
										JSONObject response = (JSONObject) params[0];
										JSONArray contentArray = response
												.getJSONArray(TinyTinySpecificConstants.RESPONSE_CONTENT_PROP);
										for (int i = 0; i < contentArray
												.length(); i++) {
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
					});
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	private void showHeadlines(final List<Headline> headlines) {
		if (menuLoadingShouldWait) {
			inflateMenu();
		}
		setTitle(feed.title);
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
				InternalStorageUtil.saveHeadlinePos(HeadlinesActivity.this,
						feedId, position);
				startArticleActivity(currHeadline, headlines, getTitle()
						.toString());
			}
		});
		if (InternalStorageUtil.hasHeadlinePosInFile(this, feedId)) {
			listView.setSelection(InternalStorageUtil.getHeadlinePos(this,
					feedId));
		}
		headlinesAdapter.notifyDataSetChanged();
	}

	private void markFeedAsRead(final int feedId, final String host,
			final String sessionId, Context context) {
		showProgress("Marking feed as read...", "");
		AsyncHttpClient client = new AsyncHttpClient();
		JSONObject jsonParams = new JSONObject();
		try {
			jsonParams.put(
					TinyTinySpecificConstants.REQUEST_HEADLINES_FEED_ID_PROP,
					feedId);
			jsonParams
					.put(TinyTinySpecificConstants.OP_PROP,
							TinyTinySpecificConstants.REQUEST_MARK_FEED_AS_READ_OP_VALUE);
			jsonParams.put(TinyTinySpecificConstants.REQUEST_SESSION_ID_PROP,
					sessionId);
			jsonParams
					.put(TinyTinySpecificConstants.REQUEST_HEADLINES_VIEW_MODE_PROP,
							TinyTinySpecificConstants.REQUEST_HEADLINES_VIEW_MODE_UNREAD_VALUE);
			StringEntity entity = new StringEntity(jsonParams.toString());
			client.post(context, host, entity, "application/json",
					new JsonHttpResponseHandler() {

						@Override
						public void onFailure(Throwable e,
								JSONObject errorResponse) {
							ErrorAlertDialog.showError(HeadlinesActivity.this,
									R.string.error_mark_feed_as_read);
							super.onFailure(e, errorResponse);
						}

						@Override
						public void onFinish() {
							hideProgress();
							progressNull();
							feed.unread = 0;
							updateHeadlinesAndFeedsFiles(markAllHeadlinesAsRead());
							startAllFeedsActivity();
							super.onFinish();
						}
					});
		} catch (JSONException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
	}

	protected List<Headline> markAllHeadlinesAsRead() {
		List<Headline> headlines = loadHeadlinesFromFile(feedId);
		for (Headline headline : headlines) {
			headline.unread = false;
		}
		return headlines;
	}

	private void updateHeadlinesAndFeedsFiles(List<Headline> headlines) {
		InternalStorageUtil.saveHeadlines(HeadlinesActivity.this, headlines,
				feedId);
		InternalStorageUtil.saveFeeds(HeadlinesActivity.this, sessionId, feeds);
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