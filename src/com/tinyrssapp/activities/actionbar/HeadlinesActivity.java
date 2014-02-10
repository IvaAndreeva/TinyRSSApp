package com.tinyrssapp.activities.actionbar;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
import com.tinyrssapp.entities.Feed;
import com.tinyrssapp.entities.Headline;

/**
 * Created by iva on 2/7/14.
 */
public class HeadlinesActivity extends TinyRSSAppActivity {
	public static final String NO_HEADLINES_MSG = "There are no available headlines in here";

	private ListView listView;
	private int feedId;
	private String title;

	@Override
	public void onBackPressed() {
		startAllFeedsActivity(host, sessionId);
		super.onBackPressed();
	}

	public void initialize() {
		Bundle b = getIntent().getExtras();
		initSessionAndHost(b);
		if (b != null) {
			feedId = b.getInt(FEED_ID_TO_LOAD);
			title = b.getString(FEED_TITLE_TO_LOAD);
		} else {
			feedId = 0;
			title = "";
		}
		listView = (ListView) findViewById(R.id.listView);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (checkIsCommonMenuItemSelected(item)) {
			return true;
		}
		switch (item.getItemId()) {
		case R.id.list_action_refresh:
			getHeadlines((new Feed()).setId(feedId).setTitle(title));
			return true;
		case R.id.list_action_mark_all_as_read:
			markFeedAsRead(feedId, host, sessionId, getApplicationContext());
			startAllFeedsActivity(host, sessionId);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void markFeedAsRead(int feedId, String host, String sessionId,
			Context context) {
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
						public void onFinish() {
							hideProgress();
							super.onFinish();
						}
					});
		} catch (JSONException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		getHeadlines((new Feed()).setId(feedId).setTitle(title));
	}

	private void getHeadlines(Feed feed) {
		try {
			showProgress("Loading headlines...", "");
			feedId = feed.id;
			setTitle(feed.title);
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
											// TODO ERROR MSG
											return null;
										}
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
		inflateMenu();
		if (headlines.size() == 0) {
			headlines.add((new Headline()).setTitle(NO_HEADLINES_MSG));
		}
		ArrayAdapter<Headline> headlinesAdapter = new ArrayAdapter<Headline>(
				this, android.R.layout.simple_list_item_1, headlines);
		listView.setAdapter(headlinesAdapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Headline currHeadline = (Headline) parent.getAdapter().getItem(
						position);
				startArticleActivity(currHeadline, headlines, getTitle()
						.toString());
			}
		});
		headlinesAdapter.notifyDataSetChanged();
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
		getHeadlines((new Feed()).setId(feedId).setTitle(title));
	}
}