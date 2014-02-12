package com.tinyrssapp.entities;


/**
 * Created by iva on 2/7/14.
 */
public class Headline extends Entity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5144624830514635974L;
	public long id;
	public boolean unread;
	public boolean marked;
	public boolean published;
	public long updated;
	public boolean isUpdated;
	public String title;
	public String link;
	public int feedId;
	public String content;

	public Headline() {
	}

	public Headline setId(long id) {
		this.id = id;
		return this;
	}

	public Headline setUnread(boolean unread) {
		this.unread = unread;
		return this;
	}

	public Headline setMarked(boolean marked) {
		this.marked = marked;
		return this;
	}

	public Headline setPublished(boolean published) {
		this.published = published;
		return this;
	}

	public Headline setUpdated(long updated) {
		this.updated = updated;
		return this;
	}

	public Headline setIsUpdated(boolean isUpdated) {
		this.isUpdated = isUpdated;
		return this;
	}

	public Headline setTitle(String title) {
		this.title = title;
		return this;
	}

	public Headline setLink(String link) {
		this.link = link;
		return this;
	}

	public Headline setFeedId(int feedId) {
		this.feedId = feedId;
		return this;
	}

	public Headline setContent(String content) {
		this.content = content;
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
		return unread;
	}

	@Override
	public int getUnread() {
		return 1;
	}
}
