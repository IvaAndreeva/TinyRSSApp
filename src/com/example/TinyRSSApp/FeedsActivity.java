package com.example.TinyRSSApp;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

/**
 * Created by iva on 2/7/14.
 */
public class FeedsActivity extends TinyRSSAppActivity {
	public static final String FEED_ID_TO_LOAD = "feedId";
	public static final String FEED_TITLE_TO_LOAD = "feedTitle";
	public static final String ARTICLE_ID = "articleId";
	public static final String CONTENT = "content";
	public static final String NO_FEEDS_MSG = "There are no available feeds in here";
	public static final String NO_HEADLINES_MSG = "There are no available headlines in here";

	private ListView listView;
	private Menu menu;
	private boolean isMenuInflated = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_view);

		initialize();
	}

	public void initialize() {
		super.initialize();
		listView = (ListView) findViewById(R.id.listView);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu = menu;
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (logoutIfChosen(item)) {
			return true;
		}
		if (toggleShowUnreadIfChosen(item)){
			getFeeds();
			return true;
		}
		switch (item.getItemId()) {
		case R.id.list_action_refresh:
			getFeeds();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void inflateMenu() {
		if (!isMenuInflated) {
			isMenuInflated = true;
			MenuInflater inflater = getMenuInflater();
			inflater.inflate(R.menu.feeds_actions, menu);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		getFeeds();
	}

	private void getFeeds() {
		AsyncHttpClient client = new AsyncHttpClient();
		try {
			JSONObject jsonParams = new JSONObject();
			jsonParams.put(TinyTinySpecificConstants.REQUEST_CAT_ID_PROP, "-3");
			jsonParams.put(TinyTinySpecificConstants.OP_PROP,
					TinyTinySpecificConstants.REQUEST_GET_FEEDS_OP_VALUE);
			jsonParams.put(TinyTinySpecificConstants.REQUEST_SESSION_ID_PROP,
					sessionId);
			jsonParams
					.put(TinyTinySpecificConstants.REQUEST_GET_FEEDS_UNREAD_ONLY_PROP,
							showUnread);
			StringEntity entity = new StringEntity(jsonParams.toString());
			client.post(getApplicationContext(), host, entity,
					"application/json", new JsonHttpResponseHandler() {
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
											// TODO ERROR MSG
											return null;
										}
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
													.setUndread(
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
		inflateMenu();
		if (feeds.size() == 0) {
			feeds.add((new Feed()).setTitle(NO_FEEDS_MSG).setUndread(0));
		}
		ArrayAdapter<Feed> feedsAdapter = new ArrayAdapter<Feed>(this,
				android.R.layout.simple_list_item_1, feeds);
		listView.setAdapter(feedsAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				getHeadlines((Feed) parent.getAdapter().getItem(position));
			}

			private void getHeadlines(Feed feed) {
				Intent intent = new Intent(FeedsActivity.this,
						HeadlinesActivity.class);
				Bundle b = new Bundle();
				b.putString(LoginActivity.HOST_PROP, host);
				b.putString(
						TinyTinySpecificConstants.RESPONSE_LOGIN_SESSIONID_PROP,
						sessionId);
				b.putInt(FEED_ID_TO_LOAD, feed.id);
				b.putString(FEED_TITLE_TO_LOAD, feed.title);
				intent.putExtras(b);
				startActivity(intent);
			}
		});
		feedsAdapter.notifyDataSetChanged();
	}

	public static void markAsRead(int feedId, String host, String sessionId,
			Context context) {
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
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
						}
					});
		} catch (JSONException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
	}
}