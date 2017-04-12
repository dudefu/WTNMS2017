package com.jhw.adm.client.views;

/**
 * ÊÓÍ¼ÈÝÆ÷
 */
public interface ViewContainer {
	public void open();
	public void close();
	
	public void addView(ViewPart viewPart);
	public int getPlacement();
	
	public int getLayer();
	
	public boolean isModal();
	
	public String getViewTitle();
	public void setViewTitle(String title);
	
	public int getViewWidth();
	public void setViewWidth(int viewWidth);

	public int getViewHeight();
	public void setViewHeight(int viewHeight);
	
	public static final int CENTER = 1;
	public static final int RIGHT_BOTTOM = 2;
	public static final int RIGHT_TOP = 3;
	public static final int TOP_CENTER = 4;
	public static final int LEFT_TOP = 5;
	
	public static final int TOPOLOGY_FRAME_LAYER = 1;
	public static final int VIEW_LAYER = 2;
	public static final int MODEL_VIEW_LAYER = 3;
	public static final int DIALOG_LAYER = 4;
	/**
	 * ×î¶¥²ã
	 */
	public static final int TOP_LAYER = 5;
}