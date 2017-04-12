package com.jhw.adm.client.views;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhw.adm.client.swing.ButtonTabComponent;

public class TabbedGroupView extends GroupView {

	public TabbedGroupView() {
		setClosable(true);
		setResizable(true);
		setMaximizable(true);
		setResizable(true);
		setIconifiable(true);
		setLayout(new BorderLayout());
		tabbedPane = new JTabbedPane();

		tabbedPane.setTabPlacement(SwingConstants.TOP);
		add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.getModel().addChangeListener(new TabbedPaneChangeListener());
		listOfViewPart = new ArrayList<ViewPart>();
	}

	@Override
	public void addView(ViewPart viewPart) {
		if (tabbedPane.indexOfComponent(viewPart) > -1) {
			tabbedPane.setSelectedComponent(viewPart);
		} else {
			listOfViewPart.add(viewPart);
			tabbedPane.addTab(viewPart.getTitle(), viewPart);
			tabbedPane.setSelectedComponent(viewPart);
			int index = tabbedPane.getTabCount() - 1;

			final ButtonTabComponent tabComponent = new ButtonTabComponent(
					tabbedPane);
			tabbedPane.setTabComponentAt(index, tabComponent);
			tabComponent.getTabButton().addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int indexOfTab = tabbedPane
							.indexOfTabComponent(tabComponent);
					if (indexOfTab != -1) {
						tabbedPane.remove(indexOfTab);
						listOfViewPart.get(indexOfTab).close();
						listOfViewPart.remove(indexOfTab);
					}
				}
			});
		}
	}

	@Override
	public void close() {
		super.close();
		LOG.info("TabbedGroupView close");
		for (ViewPart viewPart : listOfViewPart) {
			viewPart.close();
		}
		for (int index = 0; index < listOfViewPart.size(); index++) {
			listOfViewPart.remove(index);
		}
		tabbedPane.removeAll();
	}

	@Override
	public void open() {
	}

	private final JTabbedPane tabbedPane;
	private final List<ViewPart> listOfViewPart;
	private static final Logger LOG = LoggerFactory
			.getLogger(TabbedGroupView.class);
	private static final long serialVersionUID = -1L;

	private class TabbedPaneChangeListener implements ChangeListener {
		@Override
		public void stateChanged(ChangeEvent e) {
			if (tabbedPane.getSelectedComponent() instanceof ViewPart) {
				ViewPart viewPart = (ViewPart) tabbedPane
						.getSelectedComponent();
				setTitle(viewPart.getTitle());
			}
		}
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
}