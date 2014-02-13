package com.tinyrssapp.storage.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.tinyrssapp.activities.actionbar.TinyRSSAppActivity;
import com.tinyrssapp.activities.actionbar.TinyRSSAppListActivity;
import com.tinyrssapp.entities.Headline;

public class StorageHeadlinesUtil extends InternalStorageUtil {
	public static final String FILE_WITH_HEADLINES = "headlines";
	public static final String SELECTED_HEADLINE_POS = "scroll_headlines";

	public static boolean hasInFile(TinyRSSAppActivity context, int feedId) {
		File file = new File(context.getFilesDir(),
				getFileNameHeadlines(feedId));
		return file.exists();
	}

	public static void save(TinyRSSAppActivity context,
			List<Headline> headlines, int feedId) {
		saveObjInFile(context, getFileNameHeadlines(feedId), headlines);
	}

	@SuppressWarnings("unchecked")
	public static List<Headline> get(TinyRSSAppActivity context, int feedId) {
		if (!hasInFile(context, feedId)) {
			return new ArrayList<Headline>();
		}
		List<Headline> headlines = new ArrayList<Headline>();
		Object headlinesObj = InternalStorageUtil.readObjFromFile(context,
				getFileNameHeadlines(feedId));
		if (headlinesObj instanceof List<?>
				&& ((List<?>) headlinesObj).size() > 0
				&& ((List<?>) headlinesObj).get(0) instanceof Headline) {
			headlines = (List<Headline>) headlinesObj;
		}
		return headlines;
	}

	public static boolean hasPosInFile(TinyRSSAppActivity context, int feedId) {
		File file = new File(context.getFilesDir(),
				getFileNameHeadlinePos(feedId));
		return file.exists();
	}

	public static void savePos(TinyRSSAppActivity context, int feedId,
			int headlinePos) {
		saveObjInFile(context, getFileNameHeadlinePos(feedId), headlinePos);
	}

	public static int getPos(TinyRSSAppActivity context, int feedId) {
		Object scrollObj = InternalStorageUtil.readObjFromFile(context,
				getFileNameHeadlinePos(feedId));
		if (scrollObj instanceof Integer) {
			return (Integer) scrollObj;
		}
		return 0;
	}

	private static String getFileNameHeadlines(int feedId) {
		return FILE_WITH_HEADLINES + feedId;
	}

	private static String getFileNameHeadlinePos(int feedId) {
		return SELECTED_HEADLINE_POS + feedId;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Headline> get(TinyRSSAppListActivity context,
			StorageParams params) {
		return get(context, params.feedId);
	}

	@Override
	public boolean hasPosInFile(TinyRSSAppListActivity context,
			StorageParams params) {
		return hasPosInFile(context, params.feedId);
	}

	@Override
	public int getPos(TinyRSSAppListActivity context, StorageParams params) {
		return getPos(context, params.feedId);
	}

	@Override
	public boolean hasInFile(TinyRSSAppListActivity context,
			StorageParams params) {
		return hasInFile(context, params.feedId);
	}
}
