package com.tinyrssreader.entities;

import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.tinyrssreader.R;

public class CustomAdapter<T extends Entity> extends ArrayAdapter<T> {

	private int noUnreadColor;
	private int unreadColor;
	private Context context;
	private int layoutId;
	private List<T> entities;
	private int entityTitleTextViewId;
	private int entityUnreadTextViewId = -1;

	public CustomAdapter(Context context, int layoutId,
			int entityTitleTextViewId, List<T> entities) {
		super(context, entityTitleTextViewId, entities);
		this.context = context;
		this.layoutId = layoutId;
		this.entities = entities;
		this.entityTitleTextViewId = entityTitleTextViewId;
		resolveColors();
	}

	public CustomAdapter(Context context, int layoutId,
			int entityTitleTextViewId, int entityUnreadTextViewId,
			List<T> entities) {
		super(context, entityTitleTextViewId, entityUnreadTextViewId, entities);
		this.context = context;
		this.layoutId = layoutId;
		this.entities = entities;
		this.entityTitleTextViewId = entityTitleTextViewId;
		this.entityUnreadTextViewId = entityUnreadTextViewId;
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

		T entity = entities.get(position);

		if (entity != null) {
			TextView title = (TextView) v.findViewById(entityTitleTextViewId);
			if (title != null) {
				title.setText(entity.getTitle());
				if (entity.isUnread() || entity.alwaysShow) {
					title.setTextColor(unreadColor);
				} else {
					title.setTextColor(noUnreadColor);
				}
			}
			if (entityUnreadTextViewId != -1) {
				TextView unread = (TextView) v
						.findViewById(entityUnreadTextViewId);
				if (unread != null) {
					if (entity.isUnread()) {
						unread.setText(String.valueOf(entity.getUnread()));
					} else {
						unread.setText("");
					}
				}
			}
		}

		return v;
	}
}
