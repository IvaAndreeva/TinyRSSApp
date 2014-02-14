package com.tinyrssreader.storage.prefs;

import android.content.Context;

public class PrefsCredentials extends StoredPreferencesTinyRSSReader {
	public static final String HOST = "host";
	public static final String USERNAME = "username";
	public static final String PASS = "pass";

	static{
		PREFS = PREFS_PREFIX + "credentials";
	}
	
	public static String getUsernamePref(Context context) {
		return getStringFromSavedPrefs(context, USERNAME);
	}

	public static void putUsernamePref(Context context, String username) {
		putStringInSavedPrefs(context, USERNAME, username);
	}

	public static String getPasswordPref(Context context) {
		return getStringFromSavedPrefs(context, PASS);
	}

	public static void putPasswordPref(Context context, String pass) {
		putStringInSavedPrefs(context, PASS, pass);
	}

	public static String getHostPref(Context context) {
		return getStringFromSavedPrefs(context, HOST);
	}

	public static void putHostPref(Context context, String host) {
		putStringInSavedPrefs(context, HOST, host);
	}

	public static void putUserPassHost(Context context, String username,
			String pass, String host) {
		putUsernamePref(context, username);
		putPasswordPref(context, pass);
		putHostPref(context, host);
	}
}
