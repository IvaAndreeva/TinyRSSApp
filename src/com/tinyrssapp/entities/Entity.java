package com.tinyrssapp.entities;

import java.io.Serializable;

public abstract class Entity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8849636733746237432L;

	public abstract String getTitle();
	public abstract boolean isUnread();
	public abstract int getUnread();
	
}
