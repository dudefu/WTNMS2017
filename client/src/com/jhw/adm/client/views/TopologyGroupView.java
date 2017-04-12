package com.jhw.adm.client.views;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(TopologyGroupView.ID)
public class TopologyGroupView extends GroupView {

	public TopologyGroupView() {
		setTitle("Õÿ∆À ”Õº");
		setViewSize(1024, 768);
		setClosable(true);
		setResizable(true);
		setMaximizable(true);
		setResizable(true);
		setIconifiable(true);
		setMaximize(true);
		setLayout(new BorderLayout());
		formPanel = new JPanel(new BorderLayout());
		this.add(formPanel, BorderLayout.CENTER);
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
		LOG.info("TopologyGroupView close");
		viewPart.close();
		formPanel.removeAll();
		formPanel.repaint();
	}
	
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

	private ViewPart viewPart;
	private final JPanel formPanel;
	private static final Logger LOG = LoggerFactory
			.getLogger(TopologyGroupView.class);
	private static final long serialVersionUID = -1L;	
	public static final String ID = "topologyGroupView";
}