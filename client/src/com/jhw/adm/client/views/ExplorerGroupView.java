package com.jhw.adm.client.views;

import javax.annotation.PostConstruct;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component(ExplorerGroupView.ID)
public class ExplorerGroupView extends TabbedGroupView implements ApplicationContextAware {

	@PostConstruct
	protected void initialize() {
		setTitle("‰Ø¿¿ ”Õº");
		setMaximizable(false);
		setViewSize(WIDTH, HEIGHT);
//		stickViewId = ConfigureGroupView.ID;
//		GroupView groupView = (GroupView)applicationContex.getBean(stickViewId);
//		groupView.addComponentListener(new ComponentAdapter() {
//			@Override
//			public void componentHidden(ComponentEvent e) {				
//			}
//			@Override
//			public void componentMoved(ComponentEvent e) {
//				Rectangle sourceRect = e.getComponent().getBounds();
//				setBounds(sourceRect.x - WIDTH, sourceRect.y, WIDTH, HEIGHT);
//			}
//			@Override
//			public void componentResized(ComponentEvent e) {
//			}
//			@Override
//			public void componentShown(ComponentEvent e) {
//			}
//			
//		});
	}
	
	@Override
	public int getPlacement() {
		return RIGHT_TOP;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContex)
			throws BeansException {
		this.applicationContex = applicationContex;
	}
	
	private String stickViewId;
	private ApplicationContext applicationContex;
	private static final int WIDTH = 240;
	private static final int HEIGHT = 640;
	private static final long serialVersionUID = 1L;
	public static final String ID = "explorerGroupView";
}