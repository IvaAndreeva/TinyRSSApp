package com.tinyrssreader.request;

import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tinyrssreader.activities.actionbar.TinyRSSReaderListActivity;
import com.tinyrssreader.response.ResponseHandler;
import com.tinyrssreader.storage.prefs.PrefsSettings;

public class RequestBuilder {

	public static void makeRequest(Context context, String host,
			HttpEntity params, final ResponseHandler responseHandler) {
		AsyncHttpClient client = null;

		boolean useSSL = !PrefsSettings.hasSSLIgnoreUrl(context, host);
		if (!useSSL) {
			try {
				HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
				HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
				SchemeRegistry registry = new SchemeRegistry();
				KeyStore trustStore = KeyStore.getInstance(KeyStore
						.getDefaultType());
				trustStore.load(null, null);
				CustomSSLSocketFactory socketFactory = new CustomSSLSocketFactory(
						trustStore);
				socketFactory
						.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
				registry.register(new Scheme("http", PlainSocketFactory
						.getSocketFactory(), 80));
				registry.register(new Scheme("https", socketFactory, 443));
				client = new AsyncHttpClient(registry);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (client == null) {
			client = new AsyncHttpClient();
		}
		try {
			client.post(context, host, params, "application/json",
					new JsonHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers,
								JSONObject response) {
							responseHandler.onSuccess(statusCode, headers,
									response);
							super.onSuccess(statusCode, headers, response);
						}

						public void onFailure(Throwable e,
								JSONObject errorResponse) {
							responseHandler.onFailure(e, errorResponse);
							super.onFailure(e, errorResponse);
						};

						@Override
						public void onFailure(Throwable e,
								JSONArray errorResponse) {
							responseHandler.onFailure(null, null);
							super.onFailure(e, errorResponse);
						}

						@Override
						public void onFailure(String responseBody,
								Throwable error) {
							responseHandler.onFailure(null, null);
							super.onFailure(responseBody, error);
						}

						@Override
						public void onFailure(int arg0, Header[] arg1,
								byte[] arg2, Throwable arg3) {
							responseHandler.onFailure(null, null);
							super.onFailure(arg0, arg1, arg2, arg3);
						}

						@Override
						public void onFailure(int statusCode, Header[] headers,
								Throwable e, JSONArray errorResponse) {
							responseHandler.onFailure(null, null);
							super.onFailure(statusCode, headers, e,
									errorResponse);
						}

						@Override
						public void onFailure(int statusCode, Throwable e,
								JSONArray errorResponse) {
							responseHandler.onFailure(null, null);
							super.onFailure(statusCode, e, errorResponse);
						}

						@Override
						public void onFinish() {
							responseHandler.onFinish();
							super.onFinish();
						}
					});
		} catch (Exception e) {

			responseHandler.onFailure(e, null);
		}
	}

	public static void makeRequestWithProgress(
			final TinyRSSReaderListActivity activity, String host,
			HttpEntity params, final ResponseHandler responseHandler) {
		final String msg = "Waiting for server response...";
		activity.progress.show(msg);
		makeRequest(activity, host, params, new ResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				activity.progress.hide(msg);
				responseHandler.onSuccess(statusCode, headers, response);
			}

			@Override
			public void onFinish() {
				responseHandler.onFinish();
			}

			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				activity.progress.hide(msg);
			}
		});
	}
}