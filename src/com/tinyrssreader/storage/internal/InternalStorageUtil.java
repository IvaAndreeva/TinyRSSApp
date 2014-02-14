package com.tinyrssreader.storage.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import android.content.Context;

import com.tinyrssreader.activities.actionbar.TinyRSSReaderActivity;
import com.tinyrssreader.activities.actionbar.TinyRSSReaderListActivity;
import com.tinyrssreader.entities.Entity;

public abstract class InternalStorageUtil {

	public static void clearFiles(Context context, String currSessionId) {
		if (context.getFilesDir().listFiles() != null) {
			for (File f : context.getFilesDir().listFiles()) {
				if (!f.getName().contains(currSessionId)) {
					f.delete();
				}
			}
		}
	}

	protected static void saveObjInFile(TinyRSSReaderActivity context,
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

	protected static Object readObjFromFile(TinyRSSReaderActivity context,
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

	public abstract <T extends Entity> List<T> get(
			TinyRSSReaderListActivity context, StorageParams params);

	public abstract boolean hasInFile(TinyRSSReaderListActivity context,
			StorageParams params);

	public abstract boolean hasPosInFile(TinyRSSReaderListActivity context,
			StorageParams params);

	public abstract int getPos(TinyRSSReaderListActivity context,
			StorageParams params);
}
