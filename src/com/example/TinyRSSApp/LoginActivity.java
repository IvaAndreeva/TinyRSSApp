package com.example.TinyRSSApp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

    private EditText address;
    private EditText username;
    private EditText password;
    private TextView loginError;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        initialize();
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
                    jsonParams.put(TinyTinySpecificConstants.OP_PROP, TinyTinySpecificConstants.LOGIN_OP_VALUE);
                    jsonParams.put(TinyTinySpecificConstants.LOGIN_USERNAME_PROP, username.getText().toString());
                    jsonParams.put(TinyTinySpecificConstants.LOGIN_PASSWORD_PROP, password.getText().toString());
                    StringEntity entity = new StringEntity(jsonParams.toString());
                    client.post(getApplicationContext(), finalHost, entity, "application/json",
                            new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    try {
                                        if (response.getInt(TinyTinySpecificConstants.LOGIN_STATUS_PROP) == TinyTinySpecificConstants.LOGIN_STATUS_FAIL_VALUE) {
                                            ((TextView) findViewById(R.id.errorMsg)).setVisibility(View.VISIBLE);
                                            connectButton.setText(R.string.login_connect_button_text);
                                            connectButton.setEnabled(true);
                                        } else {
                                            ((TextView) findViewById(R.id.errorMsg)).setVisibility(View.INVISIBLE);
                                            connectButton.setText(R.string.login_success_msg);
                                            String sessionId = response.getJSONObject(TinyTinySpecificConstants.LOGIN_CONTENT_PROP).getString(TinyTinySpecificConstants.LOGIN_SESSIONID_PROP);
                                            startAllFeedsActivity(finalHost, sessionId);
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
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
        Intent intent = new Intent(LoginActivity.this, AllFeedsActivity.class);
        Bundle b = new Bundle();
        b.putString(HOST_PROP, host);
        b.putString(TinyTinySpecificConstants.LOGIN_SESSIONID_PROP, sessionId);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }
}
