package com.example.TinyRSSApp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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
public class ListActivity extends Activity {
    public static final String ARTICLE_ID = "articleId";
    public static final String CONTENT = "content";

    private ListView listView;
    private String sessionId;
    private String host;
    private List<Feed> feeds;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_view);

        initialize();
    }

    private void initialize() {
        Bundle b = getIntent().getExtras();
        if (b != null) {
            sessionId = b.getString(TinyTinySpecificConstants.RESPONSE_LOGIN_SESSIONID_PROP);
            host = b.getString(LoginActivity.HOST_PROP);
        } else {
            sessionId = "";
            host = "";
        }
        listView = (ListView) findViewById(R.id.listView);
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
            jsonParams.put(TinyTinySpecificConstants.OP_PROP, TinyTinySpecificConstants.REQUEST_GET_FEEDS_OP_VALUE);
            jsonParams.put(TinyTinySpecificConstants.REQUEST_SESSION_ID_PROP, sessionId);
            jsonParams.put(TinyTinySpecificConstants.REQUEST_GET_FEEDS_UNREAD_ONLY_PROP, "true");
            StringEntity entity = new StringEntity(jsonParams.toString());
            client.post(getApplicationContext(), host, entity, "application/json",
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            AsyncTask task = new AsyncTask<JSONObject, Void, Void>() {
                                private List<Feed> feeds = new ArrayList<Feed>();
                                @Override
                                protected Void doInBackground(JSONObject... params) {
                                    try {
                                        if (params.length < 1 || !(params[0] instanceof JSONObject)) {
                                            //TODO ERROR MSG
                                            return null;
                                        }
                                        JSONObject response = (JSONObject) params[0];
                                        JSONArray contentArray = response.getJSONArray(TinyTinySpecificConstants.RESPONSE_CONTENT_PROP);
                                        for (int i = 0; i < contentArray.length(); i++) {
                                            JSONObject feedJson = contentArray.getJSONObject(i);
                                            Feed feed = (new Feed()).setFeedUrl(feedJson.getString(TinyTinySpecificConstants.RESPONSE_FEED_URL_PROP))
                                                    .setCatId(feedJson.getInt(TinyTinySpecificConstants.REQUEST_CAT_ID_PROP))
                                                    .setHasIcon(feedJson.getBoolean(TinyTinySpecificConstants.RESPONSE_FEED_HAS_ICON_PROP))
                                                    .setId(feedJson.getInt(TinyTinySpecificConstants.RESPONSE_FEED_ID_PROP))
                                                    .setLastUpdated(feedJson.getLong(TinyTinySpecificConstants.RESPONSE_FEED_LAST_UPDATED_PROP))
                                                    .setOrderId(feedJson.getInt(TinyTinySpecificConstants.RESPONSE_FEED_ORDER_ID_PROP))
                                                    .setTitle(feedJson.getString(TinyTinySpecificConstants.RESPONSE_FEED_TITLE_PROP))
                                                    .setUndread(feedJson.getInt(TinyTinySpecificConstants.RESPONSE_FEED_UNREAD_PROP));
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
                            task.execute(new JSONObject[]{response});
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void showFeeds(List<Feed> feeds) {
        ArrayAdapter<Feed> feedsAdapter = new ArrayAdapter<Feed>(this, android.R.layout.simple_list_item_1, feeds);
        listView.setAdapter(feedsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getHeadlines((Feed) parent.getAdapter().getItem(position));
            }
        });
        feedsAdapter.notifyDataSetChanged();
    }

    private void getHeadlines(Feed feed) {
        try {
            AsyncHttpClient client = new AsyncHttpClient();
            JSONObject jsonParams = new JSONObject();
            jsonParams.put(TinyTinySpecificConstants.REQUEST_HEADLINES_FEED_ID_PROP, feed.id);
            jsonParams.put(TinyTinySpecificConstants.REQUEST_HEADLINES_LIMIT_PROP, TinyTinySpecificConstants.REQUEST_HEADLINES_LIMIT_UNDEFINED_VALUE);
            jsonParams.put(TinyTinySpecificConstants.OP_PROP, TinyTinySpecificConstants.REQUEST_HEADLINES_GET_HEADLINES_OP_VALUE);
            jsonParams.put(TinyTinySpecificConstants.REQUEST_HEADLINES_SHOW_CONTENT_PROP, "true");
            jsonParams.put(TinyTinySpecificConstants.REQUEST_SESSION_ID_PROP, sessionId);
            jsonParams.put(TinyTinySpecificConstants.REQUEST_HEADLINES_VIEW_MODE_PROP, TinyTinySpecificConstants.REQUEST_HEADLINES_VIEW_MODE_UNREAD_VALUE);
            StringEntity entity = new StringEntity(jsonParams.toString());
            client.post(getApplicationContext(), host, entity, "application/json",
                    new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            AsyncTask task = new AsyncTask<JSONObject, Void, Void>() {
                                private List<Headline> headlines = new ArrayList<Headline>();
                                @Override
                                protected Void doInBackground(JSONObject... params) {
                                    try {
                                        if (params.length < 1 || !(params[0] instanceof JSONObject)) {
                                            //TODO ERROR MSG
                                            return null;
                                        }
                                        JSONObject response = (JSONObject) params[0];
                                        JSONArray contentArray = response.getJSONArray(TinyTinySpecificConstants.RESPONSE_CONTENT_PROP);
                                        for (int i = 0; i < contentArray.length(); i++) {
                                            JSONObject headlineJson = contentArray.getJSONObject(i);
                                            Headline headline = (new Headline())
                                                    .setContent(headlineJson.getString(TinyTinySpecificConstants.RESPONSE_HEADLINE_CONTENT_PROP))
                                                    .setFeedId(headlineJson.getInt(TinyTinySpecificConstants.REQUEST_HEADLINES_FEED_ID_PROP))
                                                    .setId(headlineJson.getLong(TinyTinySpecificConstants.RESPONSE_HEADLINE_ID_PROP))
                                                    .setIsUpdated(headlineJson.getBoolean(TinyTinySpecificConstants.RESPONSE_HEADLINE_IS_UPDATED_PROP))
                                                    .setLink(headlineJson.getString(TinyTinySpecificConstants.RESPONSE_HEADLINE_LINK_PROP))
                                                    .setMarked(headlineJson.getBoolean(TinyTinySpecificConstants.RESPONSE_HEADLINE_MARKED_PROP))
                                                    .setPublished(headlineJson.getBoolean(TinyTinySpecificConstants.RESPONSE_HEADLINE_PUBLISHED_PROP))
                                                    .setTitle(headlineJson.getString(TinyTinySpecificConstants.RESPONSE_FEED_TITLE_PROP))
                                                    .setUnread(headlineJson.getBoolean(TinyTinySpecificConstants.RESPONSE_FEED_UNREAD_PROP))
                                                    .setUpdated(headlineJson.getLong(TinyTinySpecificConstants.RESPONSE_HEADLINE_UPDATED_PROP));
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
                            task.execute(new JSONObject[]{response});
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void showHeadlines(List<Headline> headlines) {
        ArrayAdapter<Headline> headlinesAdapter = new ArrayAdapter<Headline>(this, android.R.layout.simple_list_item_1, headlines);
        listView.setAdapter(headlinesAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getArticle((Headline) parent.getAdapter().getItem(position));
            }
        });
        headlinesAdapter.notifyDataSetChanged();
    }

    private void getArticle(Headline headline) {
        Intent intent = new Intent(ListActivity.this, ArticleActivity.class);
        Bundle b = new Bundle();
        b.putString(LoginActivity.HOST_PROP, host);
        b.putString(TinyTinySpecificConstants.RESPONSE_LOGIN_SESSIONID_PROP, sessionId);
        b.putLong(ARTICLE_ID, headline.id);
        b.putString(CONTENT, headline.content);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }
}