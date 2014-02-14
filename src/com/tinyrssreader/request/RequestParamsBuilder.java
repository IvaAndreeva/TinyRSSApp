package com.tinyrssreader.request;

import java.io.UnsupportedEncodingException;

import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import com.tinyrssreader.constants.TinyTinySpecificConstants;

public class RequestParamsBuilder {

	public static String formatHostAddress(String address) {
		if (!address.startsWith("http://") && !address.startsWith("https://")) {
			address = "http://" + address;
		}
		if (!address.endsWith("/api") && !address.endsWith("/api/")) {
			if (address.endsWith("/")) {
				address = address + "api/";
			} else {
				address = address + "/api/";
			}
		}
		if (!address.endsWith("/")) {
			address = address + "/";
		}
		return address;
	}

	public static StringEntity paramsLogin(String username, String pass) {
		try {
			JSONObject jsonParams = new JSONObject();
			jsonParams.put(TinyTinySpecificConstants.OP_PROP,
					TinyTinySpecificConstants.REQUEST_LOGIN_OP_VALUE);
			jsonParams.put(
					TinyTinySpecificConstants.REQUEST_LOGIN_USERNAME_PROP,
					username);
			jsonParams
					.put(TinyTinySpecificConstants.REQUEST_LOGIN_PASSWORD_PROP,
							pass);
			return new StringEntity(jsonParams.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static StringEntity paramsGetCategories(String sessionId,
			boolean showAll) {
		try {
			JSONObject jsonParams = new JSONObject();
			jsonParams.put(TinyTinySpecificConstants.OP_PROP,
					TinyTinySpecificConstants.REQUEST_GET_CATEGORIES_OP_VALUE);
			jsonParams.put(TinyTinySpecificConstants.REQUEST_SESSION_ID_PROP,
					sessionId);
			jsonParams
					.put(TinyTinySpecificConstants.REQUEST_GET_FEEDS_UNREAD_ONLY_PROP,
							!showAll);
			return new StringEntity(jsonParams.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static StringEntity paramsGetFeeds(String sessionId,
			boolean showAll, int catId) {
		JSONObject jsonParams = new JSONObject();
		try {
			jsonParams
					.put(TinyTinySpecificConstants.REQUEST_CAT_ID_PROP, catId);

			jsonParams.put(TinyTinySpecificConstants.OP_PROP,
					TinyTinySpecificConstants.REQUEST_GET_FEEDS_OP_VALUE);
			jsonParams.put(TinyTinySpecificConstants.REQUEST_SESSION_ID_PROP,
					sessionId);
			jsonParams
					.put(TinyTinySpecificConstants.REQUEST_GET_FEEDS_UNREAD_ONLY_PROP,
							!showAll);
			return new StringEntity(jsonParams.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static StringEntity paramsGetHeadlines(String sessionId, int feedId,
			boolean isCat, String viewMode) {
		try {
			JSONObject jsonParams = new JSONObject();
			jsonParams.put(
					TinyTinySpecificConstants.REQUEST_HEADLINES_FEED_ID_PROP,
					feedId);
			jsonParams
					.put(TinyTinySpecificConstants.REQUEST_HEADLINES_LIMIT_PROP,
							TinyTinySpecificConstants.REQUEST_HEADLINES_LIMIT_UNDEFINED_VALUE);
			jsonParams
					.put(TinyTinySpecificConstants.OP_PROP,
							TinyTinySpecificConstants.REQUEST_HEADLINES_GET_HEADLINES_OP_VALUE);
			jsonParams
					.put(TinyTinySpecificConstants.REQUEST_HEADLINES_SHOW_CONTENT_PROP,
							"true");
			jsonParams.put(TinyTinySpecificConstants.REQUEST_SESSION_ID_PROP,
					sessionId);
			jsonParams.put(
					TinyTinySpecificConstants.REQUEST_HEADLINES_VIEW_MODE_PROP,
					viewMode);
			jsonParams
					.put(TinyTinySpecificConstants.REQUEST_IS_CAT_PROP, isCat);
			return new StringEntity(jsonParams.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static StringEntity paramsMarkFeedAsRead(String sessionId,
			int feedId, boolean isCat) {
		try {
			JSONObject jsonParams = new JSONObject();
			jsonParams.put(
					TinyTinySpecificConstants.REQUEST_HEADLINES_FEED_ID_PROP,
					feedId);
			jsonParams
					.put(TinyTinySpecificConstants.OP_PROP,
							TinyTinySpecificConstants.REQUEST_MARK_FEED_AS_READ_OP_VALUE);
			jsonParams.put(TinyTinySpecificConstants.REQUEST_SESSION_ID_PROP,
					sessionId);
			jsonParams
					.put(TinyTinySpecificConstants.REQUEST_IS_CAT_PROP, isCat);
			jsonParams
					.put(TinyTinySpecificConstants.REQUEST_HEADLINES_VIEW_MODE_PROP,
							TinyTinySpecificConstants.REQUEST_HEADLINES_VIEW_MODE_UNREAD_VALUE);
			return new StringEntity(jsonParams.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static StringEntity paramsMarkArticleFieldAsMode(String sessionId,
			long articleId, String fieldValue, String modeValue) {
		try {
			JSONObject jsonParams = new JSONObject();
			jsonParams
					.put(TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_ARTILE_IDS_PROP,
							articleId);
			jsonParams.put(TinyTinySpecificConstants.OP_PROP,
					TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_OP_VALUE);
			jsonParams
					.put(TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_FIELD_PROP,
							fieldValue);
			jsonParams.put(
					TinyTinySpecificConstants.REQUEST_UPDATE_ARTICLE_MODE_PROP,
					modeValue);
			jsonParams.put(TinyTinySpecificConstants.REQUEST_SESSION_ID_PROP,
					sessionId);
			return new StringEntity(jsonParams.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
