package com.tinyrssapp.errorhandling;

import com.example.TinyRSSApp.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class ErrorAlertDialog {

	public static void showError(Context context, int msg, int title,
			int positiveButtonMsg) {
		if(((Activity) context).isFinishing())
		{
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(msg).setTitle(title);

		builder.setPositiveButton(positiveButtonMsg,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		builder.create().show();
	}

	public static void showError(Context context, int msg) {
		showError(context, msg, R.string.error_title, R.string.error_button);
	}
}
