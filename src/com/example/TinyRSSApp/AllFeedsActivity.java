package com.example.TinyRSSApp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by iva on 2/7/14.
 */
public class AllFeedsActivity extends Activity {
    private ListView feedsListView;
    private String sessionId;
    private String host;
    private ArrayAdapter<Feed> feedsAdapter;
    private List<Feed> feeds;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_feeds);

        initialize();
    }

    private void initialize() {
        Bundle b = getIntent().getExtras();
        if (b != null) {
            sessionId = b.getString(TinyTinySpecificConstants.LOGIN_SESSIONID_PROP);
            host = b.getString(LoginActivity.HOST_PROP);
        } else {
            sessionId = "";
            host = "";
        }
        feeds = new ArrayList<Feed>();
        feedsListView = (ListView) findViewById(R.id.feedsListView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadFeeds();
    }

    private void loadFeeds() {
        AsyncHttpClient client = new AsyncHttpClient();

        try {
            JSONObject jsonParams = new JSONObject();
            jsonParams.put(TinyTinySpecificConstants.CAT_ID_PROP, "-3");
            jsonParams.put(TinyTinySpecificConstants.OP_PROP, TinyTinySpecificConstants.GET_FEEDS_OP_VALUE);
            jsonParams.put(TinyTinySpecificConstants.SESSION_ID_PROP, sessionId);
            jsonParams.put(TinyTinySpecificConstants.UNREAD_ONLY_PROP, "true");
            StringEntity entity = new StringEntity(jsonParams.toString());
            client.post(getApplicationContext(), host, entity, "application/json",
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            AsyncTask task = new AsyncTask<JSONObject, Void, Void>() {
                                @Override
                                protected Void doInBackground(JSONObject... params) {
                                    try {
                                        if (params.length < 1 || !(params[0] instanceof JSONObject)) {
                                            //TODO ERROR MSG
                                            return null;
                                        }
                                        JSONObject response = (JSONObject) params[0];
                                        JSONArray contentArray = response.getJSONArray(TinyTinySpecificConstants.LOGIN_CONTENT_PROP);
                                        for (int i = 0; i < contentArray.length(); i++) {
                                            JSONObject feedJson = contentArray.getJSONObject(i);
                                            Feed feed = (new Feed()).setFeedUrl(feedJson.getString(TinyTinySpecificConstants.FEED_URL_PROP))
                                                    .setCatId(feedJson.getInt(TinyTinySpecificConstants.CAT_ID_PROP))
                                                    .setHasIcon(feedJson.getBoolean(TinyTinySpecificConstants.FEED_HAS_ICON_PROP))
                                                    .setId(feedJson.getInt(TinyTinySpecificConstants.FEED_ID_PROP))
                                                    .setLastUpdated(feedJson.getLong(TinyTinySpecificConstants.FEED_LAST_UPDATED_PROP))
                                                    .setOrderId(feedJson.getInt(TinyTinySpecificConstants.FEED_ORDER_ID_PROP))
                                                    .setTitle(feedJson.getString(TinyTinySpecificConstants.FEED_TITLE_PROP))
                                                    .setUndread(feedJson.getInt(TinyTinySpecificConstants.FEED_UNREAD_PROP));
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
                                    showFeeds();
                                }
                            };
                            task.execute(new JSONObject[]{response});
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void showFeeds() {
        feedsAdapter = new ArrayAdapter<Feed>(this, android.R.layout.simple_list_item_1, feeds);
        feedsListView.setAdapter(feedsAdapter);
        feedsAdapter.notifyDataSetChanged();
    }
}