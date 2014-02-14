package com.tinyrssreader.entities;

import java.io.Serializable;

public abstract class Entity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5498250520337996533L;

	public abstract String getTitle();

	public abstract boolean isUnread();

	public abstract int getUnread();

	public abstract <T extends Entity> T setTitle(String title);

	public abstract <T extends Entity> T setUnread(int unread);

}
