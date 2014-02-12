package com.tinyrssapp.entities;

/**
 * Created by iva on 2/7/14.
 */
public class Feed extends Entity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2632196177217492919L;
	public String feedUrl;
	public String title;
	public int id;
	public int unread;
	public boolean hasIcon;
	public int catId;
	public long lastUpdated;
	public int orderId;

	public Feed setFeedUrl(String feedUrl) {
		this.feedUrl = feedUrl;
		return this;
	}

	public Feed setTitle(String title) {
		this.title = title;
		return this;
	}

	public Feed setId(int id) {
		this.id = id;
		return this;
	}

	public Feed setUnread(int unread) {
		this.unread = unread;
		return this;
	}

	public Feed setHasIcon(boolean hasIcon) {
		this.hasIcon = hasIcon;
		return this;
	}

	public Feed setCatId(int catId) {
		this.catId = catId;
		return this;
	}

	public Feed setLastUpdated(long lastUpdated) {
		this.lastUpdated = lastUpdated;
		return this;
	}

	public Feed setOrderId(int orderId) {
		this.orderId = orderId;
		return this;
	}

	@Override
	public String toString() {
		return title;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public boolean isUnread() {
		return unread > 0;
	}

	@Override
	public int getUnread() {
		return unread;
	}
}
