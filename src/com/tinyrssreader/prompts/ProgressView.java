package com.tinyrssreader.prompts;

import java.util.HashSet;
import java.util.Set;
import com.tinyrssreader.R;

import android.app.Activity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ProgressView {
	private RelativeLayout progressLayout;
	private TextView tv;
	private Activity context;
	private Set<String> msgs;

	public ProgressView(Activity context) {
		this.context = context;
		this.progressLayout = (RelativeLayout) this.context
				.findViewById(R.id.progress_layout);
		this.tv = (TextView) progressLayout.findViewById(R.id.toast_text);
		this.msgs = new HashSet<String>();
	}

	public void show(final String msg) {
		System.out.println("SHOWING PROGRESS FOR " + msg);
		msgs.add(msg);
		this.context.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				tv.setText(getMsgs());
				progressLayout.setVisibility(View.VISIBLE);
			}
		});
	}

	private String getMsgs() {
		StringBuffer buff = new StringBuffer();
		for (String msg : msgs) {
			buff.append(msg);
		}
		return buff.toString();
	}

	public void hide(String msg) {
		System.out.println("HIDING PROGRESS FOR " + msg);
		msgs.remove(msg);
		this.context.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				tv.setText(getMsgs());
				if (msgs.size() == 0) {
					progressLayout.setVisibility(View.INVISIBLE);
				}
			}
		});
	}
}
