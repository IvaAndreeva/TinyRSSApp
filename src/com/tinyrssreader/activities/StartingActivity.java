package com.tinyrssreader.activities;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.tinyrssreader.activities.actionbar.CategoriesActivity;
import com.tinyrssreader.activities.actionbar.FeedsActivity;
import com.tinyrssreader.constants.TinyTinySpecificConstants;
import com.tinyrssreader.request.RequestBuilder;
import com.tinyrssreader.request.RequestParamsBuilder;
import com.tinyrssreader.response.ResponseHandler;
import com.tinyrssreader.storage.prefs.PrefsCredentials;
import com.tinyrssreader.storage.prefs.PrefsSettings;

public class StartingActivity extends Activity {
	public static final String HOST_PROP = "host";

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String sessionId = PrefsCredentials.getSessionIdPref(this);
		if (sessionId != "") {
			ResponseHandler handler = getResponseHandlerForCheckSessionId();
			RequestBuilder
					.makeRequest(this, PrefsCredentials.getHostPref(this),
							RequestParamsBuilder.paramsCheckSession(sessionId),
							handler);
		} else {
			startLogin();
		}
	}

	private ResponseHandler getResponseHandlerForCheckSessionId() {
		return new ResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					JSONObject response) {
				try {
					if (response
							.getInt(TinyTinySpecificConstants.RESPONSE_LOGIN_STATUS_PROP) != TinyTinySpecificConstants.RESPONSE_LOGIN_STATUS_FAIL_VALUE) {
						startNextActivity();
					} else {
						PrefsCredentials.putSessionIdPref(
								StartingActivity.this, "");
						startLogin();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFinish() {
			}

			@Override
			public void onFailure(Throwable e, JSONObject errorResponse) {
				startLogin();
			}
		};
	}

	protected void startNextActivity() {
		String host = PrefsCredentials.getHostPref(this);
		String sessionId = PrefsCredentials.getSessionIdPref(this);
		if (PrefsSettings.getCategoryMode(this) != PrefsSettings.CATEGORY_NO_MODE) {
			startSimpleIntent(new Intent(this, CategoriesActivity.class), host,
					sessionId);
		} else {
			startSimpleIntent(new Intent(this, FeedsActivity.class), host,
					sessionId);
		}
	}

	private void startSimpleIntent(Intent intent, String host, String sessionId) {
		Bundle b = new Bundle();
		b.putString(HOST_PROP, host);
		b.putString(TinyTinySpecificConstants.RESPONSE_LOGIN_SESSIONID_PROP,
				sessionId);
		intent.putExtras(b);
		startActivity(intent);
		finish();
	}

	private void startLogin() {
		Intent intent = new Intent(this, LoginActivity.class);
		Bundle b = new Bundle();
		b.putBoolean(LoginActivity.AUTO_CONNECT, true);
		intent.putExtras(b);
		startActivity(intent);
		finish();
	}
}
