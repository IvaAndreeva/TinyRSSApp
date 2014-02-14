package com.tinyrssreader.request;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.json.JSONObject;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
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
					public void onFinish() {
						responseHandler.onFinish();
						super.onFinish();
					}
				});
	}
}
