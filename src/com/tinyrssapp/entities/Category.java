package com.tinyrssapp.entities;

public class Category extends Entity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4446173471158061919L;
	public String title;
	public int unread;
	public int id;

	public Category setId(int id) {
		this.id = id;
		return this;
	}

	public Category setTitle(String title) {
		this.title = title;
		return this;
	}

	public Category setUnread(int unread) {
		this.unread = unread;
		return this;
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
