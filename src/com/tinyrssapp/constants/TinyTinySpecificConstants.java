package com.tinyrssapp.constants;

/**
 * Created by iva on 2/7/14.
 */
public final class TinyTinySpecificConstants {
	public static final String OP_PROP = "op";

	public static final String REQUEST_LOGIN_OP_VALUE = "login";
	public static final String REQUEST_LOGIN_USERNAME_PROP = "user";
	public static final String REQUEST_LOGIN_PASSWORD_PROP = "password";
	public static final String RESPONSE_LOGIN_STATUS_PROP = "status";
	public static final int RESPONSE_LOGIN_STATUS_FAIL_VALUE = 1;
	public static final String RESPONSE_CONTENT_PROP = "content";
	public static final String RESPONSE_LOGIN_SESSIONID_PROP = "session_id";

	public static final String REQUEST_GET_FEEDS_OP_VALUE = "getFeeds";
	public static final String REQUEST_GET_FEEDS_UNREAD_ONLY_PROP = "unread_only";
	public static final String REQUEST_SESSION_ID_PROP = "sid";
	public static final String REQUEST_CAT_ID_PROP = "cat_id";

	public static final String RESPONSE_FEED_URL_PROP = "feed_url";
	public static final String RESPONSE_FEED_TITLE_PROP = "title";
	public static final String RESPONSE_FEED_UNREAD_PROP = "unread";
	public static final String RESPONSE_FEED_ID_PROP = "id";
	public static final String RESPONSE_FEED_HAS_ICON_PROP = "has_icon";
	public static final String RESPONSE_FEED_LAST_UPDATED_PROP = "last_updated";
	public static final String RESPONSE_FEED_ORDER_ID_PROP = "order_id";

	public static final String REQUEST_HEADLINES_GET_HEADLINES_OP_VALUE = "getHeadlines";
	public static final String REQUEST_HEADLINES_FEED_ID_PROP = "feed_id";
	public static final String REQUEST_HEADLINES_LIMIT_PROP = "limit";
	public static final String REQUEST_HEADLINES_LIMIT_UNDEFINED_VALUE = "undefined";
	public static final String REQUEST_HEADLINES_SHOW_CONTENT_PROP = "show_content";
	public static final String REQUEST_HEADLINES_VIEW_MODE_PROP = "view_mode";
	public static final String REQUEST_HEADLINES_VIEW_MODE_UNREAD_VALUE = "unread";
	public static final String REQUEST_HEADLINES_VIEW_MODE_ALL_ARTICLES_VALUE = "all_articles";

	public static final String RESPONSE_HEADLINE_ID_PROP = "id";
	public static final String RESPONSE_HEADLINE_MARKED_PROP = "marked";
	public static final String RESPONSE_HEADLINE_PUBLISHED_PROP = "published";
	public static final String RESPONSE_HEADLINE_UPDATED_PROP = "updated";
	public static final String RESPONSE_HEADLINE_IS_UPDATED_PROP = "is_updated";
	public static final String RESPONSE_HEADLINE_LINK_PROP = "link";
	public static final String RESPONSE_HEADLINE_CONTENT_PROP = "content";

	public static final String REQUEST_UPDATE_ARTICLE_OP_VALUE = "updateArticle";
	public static final String REQUEST_UPDATE_ARTICLE_ARTILE_IDS_PROP = "article_ids";
	public static final String REQUEST_UPDATE_ARTICLE_FIELD_PROP = "field";
	public static final String REQUEST_UPDATE_ARTICLE_FIELD_STARRED_VALUE = "0";
	public static final String REQUEST_UPDATE_ARTICLE_FIELD_PUBLISHED_VALUE = "1";
	public static final String REQUEST_UPDATE_ARTICLE_FIELD_UNREAD_VALUE = "2";
	public static final String REQUEST_UPDATE_ARTICLE_MODE_PROP = "mode";
	public static final String REQUEST_UPDATE_ARTICLE_MODE_FALSE_VALUE = "0";
	public static final String REQUEST_UPDATE_ARTICLE_MODE_TRUE_VALUE = "1";
	public static final String REQUEST_UPDATE_ARTICLE_MODE_TOGGLE_VALUE = "2";

	public static final String REQUEST_MARK_FEED_AS_READ_OP_VALUE = "catchupFeed";
	
	public static final String REQUEST_GET_CATEGORIES_OP_VALUE = "getCategories";
	
	public static final String REQUEST_IS_CAT_PROP = "is_cat";

	public static final int STARRED_FEED_ID = -1;
	public static final int PUBLISHED_FEED_ID = -2;
	public static final int FRESH_FEED_ID = -3;
	public static final int ALL_ARTICLES_FEED_ID = -4;
	public static final int ARCHIVED_FEED_ID = -5;
}
