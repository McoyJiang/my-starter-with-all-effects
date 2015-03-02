package com.android.Mylauncher2;

/**
 * 
 */
public class CustomedMenuItem {

	private String title;
	
	private int iconID;

	public CustomedMenuItem(String title, int iconID) {
		super();
		this.title = title;
		this.iconID = iconID;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getIconID() {
		return iconID;
	}

	public void setIconID(int iconID) {
		this.iconID = iconID;
	}
	
	
	
}
