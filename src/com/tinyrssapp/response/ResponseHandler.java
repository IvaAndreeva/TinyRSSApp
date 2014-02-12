package com.tinyrssapp.response;

import org.apache.http.Header;
import org.json.JSONObject;

public interface ResponseHandler {
	public void onSuccess(int statusCode, Header[] headers, JSONObject response);

	public void onFailure(Throwable e, JSONObject errorResponse);

	public void onFinish();
}
