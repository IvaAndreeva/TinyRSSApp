package com.tinyrssreader.request;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.tinyrssreader.activities.actionbar.TinyRSSReaderListActivity;
import com.tinyrssreader.response.ResponseHandler;

public class RequestBuilder {

	public static void makeRequest(Context context, String host,
			HttpEntity params, final ResponseHandler responseHandler) {
		AsyncHttpClient client = new AsyncHttpClient();

		client.post(context, host, params, "application/json",
				new JsonHttpResponseHandler() {
					@Override
					public void onSuccess(int statusCode, Header[] headers,
							JSONObject response) {
						responseHandler
								.onSuccess(statusCode, headers, response);
						super.onSuccess(statusCode, headers, response);
					}

					public void onFailure(Throwable e, JSONObject errorResponse) {
						responseHandler.onFailure(e, errorResponse);
						super.onFailure(e, errorResponse);
					};

					@Override
					public void onFailure(Throwable e, JSONArray errorResponse) {
						responseHandler.onFailure(null, null);
						super.onFailure(e, errorResponse);
					}

					@Override
					public void onFailure(String responseBody, Throwable error) {
						responseHandler.onFailure(null, null);
						super.onFailure(responseBody, error);
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						responseHandler.onFailure(null, null);
						super.onFailure(arg0, arg1, arg2, arg3);
					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							Throwable e, JSONArray errorResponse) {
						responseHandler.onFailure(null, null);
						super.onFailure(statusCode, headers, e, errorResponse);
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
