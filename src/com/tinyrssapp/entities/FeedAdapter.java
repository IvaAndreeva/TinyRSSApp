package com.tinyrssapp.entities;

import java.util.List;

import com.example.TinyRSSApp.R;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FeedAdapter extends ArrayAdapter<Feed> {

	private List<Feed> feeds;
	private Context context;
	private int layoutId;
	private int feedTitleTextViewId;
	private int feedUnreadTextViewId;
	private int noUnreadColor;
	private int unreadColor;

	public FeedAdapter(Context context, int layoutId, int feedTitleTextViewId,
			int feedUnreadTextViewId, List<Feed> feeds) {
		super(context, feedTitleTextViewId, feeds);
		this.context = context;
		this.feeds = feeds;
		this.layoutId = layoutId;
		this.feedTitleTextViewId = feedTitleTextViewId;
		this.feedUnreadTextViewId = feedUnreadTextViewId;

		resolveColors();
	}

	private void resolveColors() {
		Resources.Theme themes = context.getTheme();
		TypedValue storedValueInTheme = new TypedValue();
		if (themes.resolveAttribute(R.attr.no_unread_feeds_color,
				storedValueInTheme, true)) {
			noUnreadColor = storedValueInTheme.data;
		}
		if (themes.resolveAttribute(R.attr.unread_feeds_color,
				storedValueInTheme, true)) {
			unreadColor = storedValueInTheme.data;
		}
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;

		if (v == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(layoutId, null);
		}

		Feed feed = feeds.get(position);

		if (feed != null) {
			TextView title = (TextView) v.findViewById(feedTitleTextViewId);
			TextView unread = (TextView) v.findViewById(feedUnreadTextViewId);
			if (title != null) {
				title.setText(feed.title);
				if (feed.unread == 0) {
					title.setTextColor(noUnreadColor);
				} else {
					title.setTextColor(unreadColor);
				}
			}
			if (unread != null) {
				if (feed.unread > 0) {
					unread.setText(String.valueOf(feed.unread));
				} else {
					unread.setText("");
				}
				// if (feed.unread == 0) {
				// unread.setTextColor(noUnreadColor);
				// } else {
				// unread.setTextColor(unreadColor);
				// }
			}
		}

		return v;
	}
}