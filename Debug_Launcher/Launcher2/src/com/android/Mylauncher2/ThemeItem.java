package com.android.Mylauncher2;

import android.graphics.drawable.Drawable;

public class ThemeItem {

	private String title;
	private Drawable imageId;
	
	private boolean isCurrentTheme = false;

	public ThemeItem() {
		super();
	}

	public ThemeItem(String title, Drawable imageId) {
		super();
		this.title = title;
		this.imageId = imageId;
	}

	public boolean isCurrentTheme() {
		return isCurrentTheme;
	}

	public void setCurrentTheme(boolean isCurrentTheme) {
		this.isCurrentTheme = isCurrentTheme;
	}
	
	public String getTitle() {
		return title;
	}

	public Drawable getImageId() {
		return imageId;
	}
}
