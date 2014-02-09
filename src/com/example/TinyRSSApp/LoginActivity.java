package com.example.TinyRSSApp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class LoginActivity extends Activity {
    public static final String HOST_PROP = "host";
    public static final String PREFS = "credentials";
    public static final String PREFS_HOST = "host";
    public static final String PREFS_USERNAME = "username";
    public static final String PREFS_PASS = "pass";

    private EditText address;
    private EditText username;
    private EditText password;
    private SharedPreferences savedPrefs;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initialize();
        loadSavedPrefs();
    }

    private void loadSavedPrefs() {
    	savedPrefs=getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    	if (savedPrefs!=null){
    		username.setText(savedPrefs.getString(PREFS_USERNAME, ""));
    		password.setText(savedPrefs.getString(PREFS_PASS, ""));
    	}
	}

	private void initialize() {
        address = (EditText) findViewById(R.id.adressText);
        address.setText("http://yassen.info/reader/api/");
        username = (EditText) findViewById(R.id.usernameText);
        password = (EditText) findViewById(R.id.passwordText);
        ((Button) findViewById(R.id.connectButton)).setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                AsyncHttpClient client = new AsyncHttpClient();
                String host = address.getText().toString();
                final Button connectButton = ((Button) findViewById(R.id.connectButton));

                connectButton.setText(R.string.login_connecting_msg);
                connectButton.setEnabled(false);
                if (!host.startsWith("http://") && !host.startsWith("https://")) {
                    host = "http://" + host;
                }
                final String finalHost = host;

                try {
                    JSONObject jsonParams = new JSONObject();
                    jsonParams.put(TinyTinySpecificConstants.OP_PROP, TinyTinySpecificConstants.REQUEST_LOGIN_OP_VALUE);
                    jsonParams.put(TinyTinySpecificConstants.REQUEST_LOGIN_USERNAME_PROP, username.getText().toString());
                    jsonParams.put(TinyTinySpecificConstants.REQUEST_LOGIN_PASSWORD_PROP, password.getText().toString());
                    StringEntity entity = new StringEntity(jsonParams.toString());
                    client.post(getApplicationContext(), finalHost, entity, "application/json",
                            new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    try {
                                        if (response.getInt(TinyTinySpecificConstants.RESPONSE_LOGIN_STATUS_PROP) == TinyTinySpecificConstants.RESPONSE_LOGIN_STATUS_FAIL_VALUE) {
                                            showErrorMsg(R.string.login_error_msg);
                                        } else {
                                            ((TextView) findViewById(R.id.errorMsg)).setVisibility(View.INVISIBLE);
                                            connectButton.setText(R.string.login_success_msg);
                                            String sessionId = response.getJSONObject(TinyTinySpecificConstants.RESPONSE_CONTENT_PROP).getString(TinyTinySpecificConstants.RESPONSE_LOGIN_SESSIONID_PROP);
                                            if (((CheckBox)findViewById(R.id.saveCredentialsCheck)).isChecked()) {
                                            	savePrefs();
                                            }
                                            startAllFeedsActivity(finalHost, sessionId);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                private void savePrefs() {
                                	savedPrefs=getSharedPreferences(PREFS, Context.MODE_PRIVATE);
                                    Editor editor = savedPrefs.edit();
                                    editor.putString(PREFS_HOST, address.getText().toString());
                                    editor.putString(PREFS_USERNAME, username.getText().toString());
                                    editor.putString(PREFS_PASS, password.getText().toString());
                                    editor.commit();
								}

								@Override
								public void onFailure(String responseBody, Throwable error) {
                                	showErrorMsg(R.string.login_error_conn_failed_msg);
                                };
                                
                                private void showErrorMsg() {
                                	((TextView) findViewById(R.id.errorMsg)).setVisibility(View.VISIBLE);
                                    connectButton.setText(R.string.login_connect_button_text);
                                    connectButton.setEnabled(true);
								}
                                
                                private void showErrorMsg(int msg){
                                	((TextView) findViewById(R.id.errorMsg)).setText(msg);
                                	showErrorMsg();
                                }
                            });
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void startAllFeedsActivity(String host, String sessionId) {
        Intent intent = new Intent(LoginActivity.this, FeedsActivity.class);
        Bundle b = new Bundle();
        b.putString(HOST_PROP, host);
        b.putString(TinyTinySpecificConstants.RESPONSE_LOGIN_SESSIONID_PROP, sessionId);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }
}
