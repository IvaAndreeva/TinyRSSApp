package com.tinyrssreader.storage.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.tinyrssreader.activities.actionbar.TinyRSSReaderActivity;
import com.tinyrssreader.activities.actionbar.TinyRSSReaderListActivity;
import com.tinyrssreader.entities.Feed;

public class StorageFeedsUtil extends InternalStorageUtil {
	public static final String FILE_WITH_FEEDS = "feeds";
	public static final String SELECTED_FEED_POS = "scroll_feeds";

	public static boolean hasInFile(TinyRSSReaderActivity context,
			String sessionId, int catId) {
		File file = new File(context.getFilesDir(), getFileNameFeeds(sessionId,
				catId));
		return file.exists();
	}

	public static void save(TinyRSSReaderActivity context, String sessionId,
			List<Feed> feeds, int catId) {
		saveObjInFile(context, getFileNameFeeds(sessionId, catId), feeds);
	}

	@SuppressWarnings("unchecked")
	public static List<Feed> get(TinyRSSReaderActivity context, String sessionId,
			int catId) {
		if (!hasInFile(context, sessionId, catId)) {
			return new ArrayList<Feed>();
		}
		Object feedsObj = InternalStorageUtil.readObjFromFile(context,
				getFileNameFeeds(sessionId, catId));
		List<Feed> feeds = new ArrayList<Feed>();
		if (feedsObj instanceof List<?> && ((List<?>) feedsObj).size() > 0
				&& ((List<?>) feedsObj).get(0) instanceof Feed) {
			feeds = (List<Feed>) feedsObj;
		}
		return feeds;
	}

	public static boolean hasPosInFile(TinyRSSReaderActivity context,
			String sessionId, int catId) {
		File file = new File(context.getFilesDir(), getFileNameFeedsPos(
				sessionId, catId));
		return file.exists();
	}

	public static void savePos(TinyRSSReaderActivity context, String sessionId,
			int feedPos, int catId) {
		saveObjInFile(context, getFileNameFeedsPos(sessionId, catId), feedPos);
	}

	public static int getPos(TinyRSSReaderActivity context, String sessionId,
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

	@SuppressWarnings("unchecked")
	@Override
	public List<Feed> get(TinyRSSReaderListActivity context, StorageParams params) {
		return get(context, params.sessionId, params.catId);
	}

	@Override
	public boolean hasPosInFile(TinyRSSReaderListActivity context,
			StorageParams params) {
		return hasPosInFile(context, params.sessionId, params.catId);
	}

	@Override
	public int getPos(TinyRSSReaderListActivity context, StorageParams params) {
		return getPos(context, params.sessionId, params.catId);
	}

	@Override
	public boolean hasInFile(TinyRSSReaderListActivity context,
			StorageParams params) {
		return hasInFile(context, params.sessionId, params.catId);
	}
}
