package com.tinyrssapp.storage.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.tinyrssapp.activities.actionbar.TinyRSSAppActivity;
import com.tinyrssapp.entities.Feed;

public class StorageFeedsUtil extends InternalStorageUtil {
	public static final String FILE_WITH_FEEDS = "feeds";
	public static final String SELECTED_FEED_POS = "scroll_feeds";

	public static boolean hasInFile(TinyRSSAppActivity context,
			String sessionId, int catId) {
		File file = new File(context.getFilesDir(), getFileNameFeeds(sessionId,
				catId));
		return file.exists();
	}

	public static void save(TinyRSSAppActivity context, String sessionId,
			List<Feed> feeds, int catId) {
		saveObjInFile(context, getFileNameFeeds(sessionId, catId), feeds);
	}

	@SuppressWarnings("unchecked")
	public static List<Feed> get(TinyRSSAppActivity context, String sessionId,
			int catId) {
		Object feedsObj = InternalStorageUtil.readObjFromFile(context,
				getFileNameFeeds(sessionId, catId));
		List<Feed> feeds = new ArrayList<Feed>();
		if (feedsObj instanceof List<?> && ((List<?>) feedsObj).size() > 0
				&& ((List<?>) feedsObj).get(0) instanceof Feed) {
			feeds = (List<Feed>) feedsObj;
		}
		return feeds;
	}

	public static boolean hasPosInFile(TinyRSSAppActivity context,
			String sessionId, int catId) {
		File file = new File(context.getFilesDir(), getFileNameFeedsPos(
				sessionId, catId));
		return file.exists();
	}

	public static void savePos(TinyRSSAppActivity context, String sessionId,
			int feedPos, int catId) {
		saveObjInFile(context, getFileNameFeedsPos(sessionId, catId), feedPos);
	}

	public static int getPos(TinyRSSAppActivity context, String sessionId,
			int catId) {
		Object feedPos = InternalStorageUtil.readObjFromFile(context,
				getFileNameFeedsPos(sessionId, catId));
		if (feedPos instanceof Integer) {
			return (Integer) feedPos;
		}
		return 0;
	}

	private static String getFileNameFeeds(String sessionId, int catId) {
		return FILE_WITH_FEEDS + sessionId + catId;
	}

	private static String getFileNameFeedsPos(String sessionId, int catId) {
		return SELECTED_FEED_POS + sessionId + catId;
	}
}
