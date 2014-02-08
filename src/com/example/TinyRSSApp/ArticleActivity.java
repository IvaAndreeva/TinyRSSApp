package com.example.TinyRSSApp;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ListView;
import com.example.TinyRSSApp.R;

import java.util.List;

/**
 * Created by iva on 2/8/14.
 */
public class ArticleActivity extends Activity {
    private String sessionId;
    private String host;
    private long articleId;
    private String content;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.article);

        initialize();
    }

    private void initialize() {
        Bundle b = getIntent().getExtras();
        if (b != null) {
            sessionId = b.getString(TinyTinySpecificConstants.RESPONSE_LOGIN_SESSIONID_PROP);
            host = b.getString(LoginActivity.HOST_PROP);
            articleId = b.getLong(ListActivity.ARTICLE_ID);
            content = b.getString(ListActivity.CONTENT);
        } else {
            sessionId = "";
            host = "";
            articleId = 0;
            content = "";
        }
        loadWebView();
    }

    private void loadWebView() {
        WebView webView = (WebView) findViewById(R.id.articleWebView);
        webView.loadData(content, "text/html", "utf-8");
    }
}