package com.jhw.adm.client.views;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


//事件通知台
@Component(ConsoleGroupView.ID)
public class ConsoleGroupView extends GroupView {

	@PostConstruct
	protected void initialize() {
		setTitle("控制台视图");
		setMaximizable(false);
		setViewSize(500, 200);
		setLayer(TOP_LAYER);
		setLayout(new BorderLayout());
		formPanel = new JPanel(new BorderLayout());
		this.add(formPanel, BorderLayout.CENTER);
	}
	
	@Override
	public int getPlacement() {
		return RIGHT_BOTTOM;
	}

	@Override
	public void addView(ViewPart viewPart) {
		this.viewPart = viewPart;
		setTitle(viewPart.getTitle());
		formPanel.add(viewPart, BorderLayout.CENTER);
	}

	@Override
	public void close() {
		super.close();
		LOG.info("ConsoleGroupView close");
		viewPart.close();
		formPanel.removeAll();
		formPanel.repaint();
	}

	private JPanel formPanel;
	private ViewPart viewPart;
	
	@Override
	public void open() {
	}

	@Override
	public boolean isModal() {
		return false;
	}

	@Override
	public String getViewTitle() {
		return StringUtils.EMPTY;
	}

	@Override
	public void setViewTitle(String title) {		
	}
	private static final Logger LOG = LoggerFactory
		.getLogger(ConsoleGroupView.class);
	private static final long serialVersionUID = -1L;
	public static final String ID = "consoleGroupView";
}