package com.tinyrssapp.storage.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.tinyrssapp.activities.actionbar.TinyRSSAppActivity;
import com.tinyrssapp.entities.Feed;

public class StorageCategoriesUtil extends InternalStorageUtil {

	public static final String FILE_WITH_CATEGORIES = "categories";
	public static final String SELECTED_CATEGORY_POS = "scroll_category";

	public static boolean hasPosInFile(TinyRSSAppActivity context,
			String sessionId) {
		File file = new File(context.getFilesDir(),
				getFileNameCategoriesPos(sessionId));
		return file.exists();
	}

	public static void savePos(TinyRSSAppActivity context, String sessionId,
			int catPos) {
		saveObjInFile(context, getFileNameCategoriesPos(sessionId), catPos);
	}

	public static int getPos(TinyRSSAppActivity context, String sessionId) {
		Object catPos = InternalStorageUtil.readObjFromFile(context,
				getFileNameCategoriesPos(sessionId));
		if (catPos instanceof Integer) {
			return (Integer) catPos;
		}
		return 0;
	}

	public static boolean hasInFile(TinyRSSAppActivity context, String sessionId) {
		File file = new File(context.getFilesDir(),
				getFileNameCategories(sessionId));
		return file.exists();
	}

	public static void save(TinyRSSAppActivity context, String sessionId,
			List<Feed> categories) {
		saveObjInFile(context, getFileNameCategories(sessionId), categories);
	}

	@SuppressWarnings("unchecked")
	public static List<Feed> get(TinyRSSAppActivity context, String sessionId) {
		if (!hasInFile(context, sessionId)) {
			return new ArrayList<Feed>();
		}
		Object catsObj = InternalStorageUtil.readObjFromFile(context,
				getFileNameCategories(sessionId));
		List<Feed> categories = new ArrayList<Feed>();
		if (catsObj instanceof List<?> && ((List<?>) catsObj).size() > 0
				&& ((List<?>) catsObj).get(0) instanceof Feed) {
			categories = (List<Feed>) catsObj;
		}
		return categories;
	}

	private static String getFileNameCategories(String sessionId) {
		return FILE_WITH_CATEGORIES + sessionId;
	}

	private static String getFileNameCategoriesPos(String sessionId) {
		return SELECTED_CATEGORY_POS + sessionId;
	}
}
