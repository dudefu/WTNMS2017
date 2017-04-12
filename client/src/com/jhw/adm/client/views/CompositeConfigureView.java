package com.jhw.adm.client.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.border.BevelBorder;

public class CompositeConfigureView extends ViewPart {

	@PostConstruct
	protected void initialize() {
		setTitle(configureView.getTitle());
		setLayout(new BorderLayout());
		
		JPanel configurePanel = new JPanel(new BorderLayout());
		JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				getExplorerView(), configurePanel);
		splitPanel.setDividerLocation(getExplorerView().getViewWidth() - WIDTH_OFFSET);
//		splitPanel.setResizeWeight(0.05);
		
		add(splitPanel, BorderLayout.CENTER);
		setViewSize(
				getExplorerView().getViewWidth() + configureView.getViewWidth(),
				configureView.getViewHeight());
		
		configurePanel.add(configureView, BorderLayout.CENTER);
	}
	
	@Override
	public List<JButton> getCloseButtons() {
		return configureView.getCloseButtons();
	}
	
	public void setConfigureView(ViewPart viewPart) {
		configureView = viewPart;
	}

	
	public ViewPart getExplorerView() {
		return explorerView;
	}

	public void setExplorerView(ViewPart explorerView) {
		this.explorerView = explorerView;
	}

	@Override
	public void dispose() {
		super.dispose();
		if (configureView != null) {
			configureView.close();
		}
	}
	
//	@Resource(name=SwitcherExplorerView.ID)
	private ViewPart explorerView;
	private ViewPart configureView;
	private static final int WIDTH_OFFSET = 15;
	private static final long serialVersionUID = 1L;
}