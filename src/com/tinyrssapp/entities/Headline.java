package com.tinyrssapp.entities;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by iva on 2/7/14.
 */
public class Headline implements Parcelable, Serializable {
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
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeLong(this.id);
		dest.writeByte((byte) (this.unread ? 1 : 0));
		dest.writeByte((byte) (this.marked ? 1 : 0));
		dest.writeByte((byte) (this.published ? 1 : 0));
		dest.writeLong(this.updated);
		dest.writeByte((byte) (this.isUpdated ? 1 : 0));
		dest.writeString(this.title);
		dest.writeString(this.link);
		dest.writeInt(this.feedId);
		dest.writeString(this.content);
	}

	public static final Parcelable.Creator<Headline> CREATOR = new Parcelable.Creator<Headline>() {
		public Headline createFromParcel(Parcel in) {
			return new Headline(in);
		}

		public Headline[] newArray(int size) {
			return new Headline[size];
		}
	};

	private Headline(Parcel in) {
		id = in.readLong();
		unread = in.readByte() != 0;
		marked = in.readByte() != 0;
		published = in.readByte() != 0;
		updated = in.readLong();
		isUpdated = in.readByte() != 0;
		title = in.readString();
		link = in.readString();
		feedId = in.readInt();
		content = in.readString();
	}
}
