package com.jhw.adm.client.views;

import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class GroupView extends JInternalFrame implements ViewContainer {

	public GroupView() {
		setClosable(true);
		setResizable(true);
		setMaximizable(true);
		setIconifiable(true);
		setMaximize(false);
		setLayer(VIEW_LAYER);
	}

	/**
	 * 关闭
	 */
	@Override
	public void close() {
		fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_CLOSED);
	}
	
	/**
	 * 在添加新视图的时候是否需要关闭
	 */
//	public boolean closeRequired() {
//		return false;
//	}
	
	/**
	 * 在显示的时候是否需要阻塞
	 */
	public boolean blockRequired() {
		return false;
	}

	/**
	 * 还原窗口
	 */
	public void deiconify() {
		try {
			setIcon(false);
		} catch (PropertyVetoException e) {
			LOG.error("deiconify error", e);
		}
		fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_DEICONIFIED);
	}

	/**
	 * 最小化窗口
	 */
	public void iconify() {
		try {
			setIcon(true);
		} catch (PropertyVetoException e) {
			LOG.error("iconify error", e);
		}
		fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_ICONIFIED);
	}

	@Override
	public void addView(ViewPart viewPart) {
	}

	@Override
	public int getPlacement() {
		return CENTER;
	}
	
	public void setViewSize(int width, int height) {
		this.viewWidth = width;
		this.viewHeight = height;
	}

	/**
	 * @return the viewWidth
	 */
	@Override
	public int getViewWidth() {
		return viewWidth;
	}

	/**
	 * @param viewWidth
	 *            the viewWidth to set
	 */
	@Override
	public void setViewWidth(int viewWidth) {
		this.viewWidth = viewWidth;
	}

	/**
	 * @return the viewHeight
	 */
	@Override
	public int getViewHeight() {
		return viewHeight;
	}

	/**
	 * @param viewHeight
	 *            the viewHeight to set
	 */
	@Override
	public void setViewHeight(int viewHeight) {
		this.viewHeight = viewHeight;
	}

	public boolean isMaximize() {
		return maximize;
	}

	public void setMaximize(boolean maximize) {
		this.maximize = maximize;
	}
	
	private int viewWidth;
	private int viewHeight;
	
	private boolean maximize;

	private static Logger LOG = LoggerFactory.getLogger(GroupView.class);
	private static final long serialVersionUID = -1L;
}