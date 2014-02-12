package com.tinyrssapp.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import com.tinyrssapp.activities.actionbar.TinyRSSAppActivity;
import com.tinyrssapp.entities.Feed;
import com.tinyrssapp.entities.Headline;

public class InternalStorageUtil {
	public static final String FILE_WITH_HEADLINES = "headlines";
	public static final String FILE_WITH_FEEDS = "feeds";
	public static final String SELECTED_FEED_POS = "scroll_feeds";
	public static final String SELECTED_HEADLINE_POS = "scroll_headlines";

	public static boolean hasFeedPosInFile(TinyRSSAppActivity context) {
		File file = new File(context.getFilesDir(), SELECTED_FEED_POS);
		return file.exists();
	}

	public static boolean hasHeadlinePosInFile(TinyRSSAppActivity context,
			int feedId) {
		File file = new File(context.getFilesDir(),
				getFileNameHeadlinePos(feedId));
		return file.exists();
	}

	public static void saveFeedPos(TinyRSSAppActivity context, int feedPos) {
		saveObjInFile(context, SELECTED_FEED_POS, feedPos);
	}

	public static void saveHeadlinePos(TinyRSSAppActivity context,
			int feedId, int headlinePos) {
		saveObjInFile(context, getFileNameHeadlinePos(feedId), headlinePos);
	}

	public static boolean hasHeadlinesInFile(TinyRSSAppActivity context,
			int feedId) {
		File file = new File(context.getFilesDir(),
				getFileNameHeadlines(feedId));
		return file.exists();
	}

	public static boolean hasFeedsInFile(TinyRSSAppActivity context) {
		File file = new File(context.getFilesDir(), FILE_WITH_FEEDS);
		return file.exists();
	}

	public static void saveFeeds(TinyRSSAppActivity context, List<Feed> feeds) {
		saveObjInFile(context, FILE_WITH_FEEDS, feeds);
	}

	public static void saveHeadlines(TinyRSSAppActivity context,
			List<Headline> headlines, int feedId) {
		saveObjInFile(context, getFileNameHeadlines(feedId), headlines);
	}

	public static int getFeedPos(TinyRSSAppActivity context) {
		Object feedPos = InternalStorageUtil.readObjFromFile(context,
				SELECTED_FEED_POS);
		if (feedPos instanceof Integer) {
			return (Integer) feedPos;
		}
		return 0;
	}

	public static int getHeadlinePos(TinyRSSAppActivity context, int feedId) {
		Object scrollObj = InternalStorageUtil.readObjFromFile(context,
				InternalStorageUtil.getFileNameHeadlinePos(feedId));
		if (scrollObj instanceof Integer) {
			return (Integer) scrollObj;
		}
		return 0;
	}

	@SuppressWarnings("unchecked")
	public static List<Feed> getFeeds(TinyRSSAppActivity context) {
		Object feedsObj = InternalStorageUtil.readObjFromFile(context,
				InternalStorageUtil.FILE_WITH_FEEDS);
		List<Feed> feeds = new ArrayList<Feed>();
		if (feedsObj instanceof List<?> && ((List<?>) feedsObj).size() > 0
				&& ((List<?>) feedsObj).get(0) instanceof Feed) {
			feeds = (List<Feed>) feedsObj;
		}
		return feeds;
	}

	@SuppressWarnings("unchecked")
	public static List<Headline> getHeadlines(TinyRSSAppActivity context,
			int feedId) {
		List<Headline> headlines = new ArrayList<Headline>();
		Object headlinesObj = InternalStorageUtil.readObjFromFile(context,
				InternalStorageUtil.getFileNameHeadlines(feedId));
		if (headlinesObj instanceof List<?>
				&& ((List<?>) headlinesObj).size() > 0
				&& ((List<?>) headlinesObj).get(0) instanceof Headline) {
			headlines = (List<Headline>) headlinesObj;
		}
		return headlines;
	}

	private static void saveObjInFile(TinyRSSAppActivity context,
			String fileName, Object obj) {
		try {
			File file = new File(context.getFilesDir(), fileName);
			if (file.exists()) {
				file.delete();
			}

			FileOutputStream fos = context.openFileOutput(fileName,
					Context.MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(obj);
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static Object readObjFromFile(TinyRSSAppActivity context,
			String fileName) {
		FileInputStream fis;
		Object obj = null;
		try {
			fis = context.openFileInput(fileName);

			ObjectInputStream is = new ObjectInputStream(fis);
			obj = is.readObject();
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			context.finish();
		}
		return obj;
	}

	private static String getFileNameHeadlines(int feedId) {
		return FILE_WITH_HEADLINES + feedId;
	}

	private static String getFileNameHeadlinePos(int feedId) {
		return SELECTED_HEADLINE_POS + feedId;
	}
}
