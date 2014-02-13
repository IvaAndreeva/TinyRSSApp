package com.tinyrssapp.storage.internal;

import java.util.List;

import com.tinyrssapp.entities.Feed;

public class StorageParams {
	public String sessionId;
	public int pos;
	public int catId;
	public List<Feed> feeds;
	public int feedId;

	public StorageParams setFeedId(int feedId) {
		this.feedId = feedId;
		return this;
	}

	public StorageParams setSessId(String sessionId) {
		this.sessionId = sessionId;
		return this;
	}

	public StorageParams setPos(int pos) {
		this.pos = pos;
		return this;
	}

	public StorageParams setCatId(int catId) {
		this.catId = catId;
		return this;
	}

	public StorageParams setFeeds(List<Feed> feeds) {
		this.feeds = feeds;
		return this;
	}
}
