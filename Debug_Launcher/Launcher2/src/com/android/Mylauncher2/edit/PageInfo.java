package com.android.Mylauncher2.edit;

public class PageInfo implements Comparable<PageInfo> {
	// 未辑编前是哪个页面
	public int originPage = -1;
	// 辑编完成后是哪个页面
	public int currentPage = -1;

	@Override
	public int compareTo(PageInfo another) {
		return currentPage - another.currentPage;
	}
}
