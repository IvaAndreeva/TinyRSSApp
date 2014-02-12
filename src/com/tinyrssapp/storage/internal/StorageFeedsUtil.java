package com.tinyrssapp.storage.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.tinyrssapp.activities.actionbar.TinyRSSAppActivity;
import com.tinyrssapp.entities.Feed;

public class StorageFeedsUtil extends InternalStorageUtil {
	public static final String FILE_WITH_FEEDS = "feeds";
	public static final String SELECTED_FEED_POS = "scroll_feeds";

	public static boolean hasInFile(TinyRSSAppActivity context, String sessionId) {
		File file = new File(context.getFilesDir(), getFileNameFeeds(sessionId));
		return file.exists();
	}

	public static void save(TinyRSSAppActivity context, String sessionId,
			List<Feed> feeds) {
		saveObjInFile(context, getFileNameFeeds(sessionId), feeds);
	}

	@SuppressWarnings("unchecked")
	public static List<Feed> get(TinyRSSAppActivity context, String sessionId) {
		Object feedsObj = InternalStorageUtil.readObjFromFile(context,
				getFileNameFeeds(sessionId));
		List<Feed> feeds = new ArrayList<Feed>();
		if (feedsObj instanceof List<?> && ((List<?>) feedsObj).size() > 0
				&& ((List<?>) feedsObj).get(0) instanceof Feed) {
			feeds = (List<Feed>) feedsObj;
		}
		return feeds;
	}

	public static boolean hasPosInFile(TinyRSSAppActivity context,
			String sessionId) {
		File file = new File(context.getFilesDir(),
				getFileNameFeedsPos(sessionId));
		return file.exists();
	}

	public static void savePos(TinyRSSAppActivity context, String sessionId,
			int feedPos) {
		saveObjInFile(context, getFileNameFeedsPos(sessionId), feedPos);
	}

	public static int getPos(TinyRSSAppActivity context, String sessionId) {
		Object feedPos = InternalStorageUtil.readObjFromFile(context,
				getFileNameFeedsPos(sessionId));
		if (feedPos instanceof Integer) {
			return (Integer) feedPos;
		}
		return 0;
	}

	private static String getFileNameFeeds(String sessionId) {
		return FILE_WITH_FEEDS + sessionId;
	}

	private static String getFileNameFeedsPos(String sessionId) {
		return SELECTED_FEED_POS + sessionId;
	}
}
