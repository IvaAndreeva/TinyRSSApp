package com.tinyrssreader.activities.actionbar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.tinyrssreader.entities.CustomAdapter;
import com.tinyrssreader.entities.Entity;
import com.tinyrssreader.entities.Feed;
import com.tinyrssreader.storage.internal.InternalStorageUtil;
import com.tinyrssreader.storage.internal.StorageParams;
import com.tinyrssreader.storage.prefs.PrefsSettings;

public abstract class TinyRSSReaderListActivity extends TinyRSSReaderActivity {
	protected ListView listView;
	protected boolean categoryChanged = false;

	protected void load() {
		Date now = new Date();
		long lastFeedUpdate = getLastRefreshTime();
		if (now.getTime() - lastFeedUpdate >= getMilisecsWithoutRefresh()
				|| !getUtil().hasInFile(this, getParamsHasInFile())
				|| categoryChanged) {
			menuLoadingShouldWait = true;
			refresh();
		} else {
			menuLoadingShouldWait = false;
			show(loadFromFile(getParamsLoadFromFile()));
		}
	}

	public <T extends Entity> List<T> loadFromFile(StorageParams params) {
		List<T> allEntities = getUtil().get(this, params);
		List<T> resultEntities = allEntities;
		if (!PrefsSettings.getShowAllPref(this)) {
			resultEntities = new ArrayList<T>();
			for (T entity : allEntities) {
				if (entity.isUnread() || entity.alwaysShow) {
					resultEntities.add(entity);
				}
			}
		}
		return resultEntities;
	}

	@SuppressWarnings("unchecked")
	public <T extends Entity> void show(List<T> entities) {
		if (menuLoadingShouldWait) {
			inflateMenu();
		}
		Feed parent = getParentFeed();
		if (parent != null) {
			parent.unread = 0;
			for (Entity entity : entities) {
				if (entity.isUnread()) {
					parent.unread += entity.getUnread();
				}
			}
		}
		onShow(entities);
		if (entities.size() == 0) {
			entities.add((T) (getEmtpyObj()).setTitle(getEmptyListMsg())
					.setUnread(0));
			listView.setEnabled(false);
		} else {
			listView.setEnabled(true);
		}
		ArrayAdapter<T> adapter;
		if (getListItemCountId() != -1) {
			adapter = new CustomAdapter<T>(this, getListItemLayout(),
					getListItemDataId(), getListItemCountId(), entities);
		} else {
			adapter = new CustomAdapter<T>(this, getListItemLayout(),
					getListItemDataId(), entities);
		}
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				onListItemClick(position,
						(T) parent.getAdapter().getItem(position));
			}
		});
		if (getUtil().hasPosInFile(this, getParamsHasPosInFile())) {
			listView.setSelection(getUtil().getPos(this,
					getParamsGetPosFromFile()));
		}
		adapter.notifyDataSetChanged();
	}

	public abstract long getMilisecsWithoutRefresh();

	public abstract InternalStorageUtil getUtil();

	public abstract StorageParams getParamsLoadFromFile();

	public abstract StorageParams getParamsHasInFile();

	public abstract StorageParams getParamsHasPosInFile();

	public abstract StorageParams getParamsGetPosFromFile();

	public abstract String getEmptyListMsg();

	public abstract <T extends Entity> void onShow(List<T> entities);

	public abstract int getListItemLayout();

	public abstract int getListItemDataId();

	public abstract int getListItemCountId();

	public abstract <T extends Entity> void onListItemClick(int position,
			T selected);

	public abstract void refresh();

	public abstract Feed getParentFeed();

	public abstract <T extends Entity> T getEmtpyObj();
	
	public abstract long getLastRefreshTime();
}
