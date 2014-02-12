package com.tinyrssapp.storage.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;

import com.tinyrssapp.activities.actionbar.TinyRSSAppActivity;

public class InternalStorageUtil {

	public static void clearFiles(Context context, String currSessionId) {
		if (context.getFilesDir().listFiles() != null) {
			for (File f : context.getFilesDir().listFiles()) {
				if (!f.getName().contains(currSessionId)) {
					f.delete();
				}
			}
		}
	}

	protected static void saveObjInFile(TinyRSSAppActivity context,
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

	protected static Object readObjFromFile(TinyRSSAppActivity context,
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
}
