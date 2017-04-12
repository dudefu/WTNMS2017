package com.jhw.adm.client.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.aop.LoggingRequired;
import com.jhw.adm.client.ui.ClientUI;
import com.jhw.adm.client.views.FormGroupView;

@Component(ShowViewAction.ID)
@Scope("prototype")
public class ShowViewAction extends AbstractAction {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	@LoggingRequired
	public void actionPerformed(ActionEvent e) {
		String showGroupViewId = FormGroupView.ID;
		if (getGroupId() != null && getGroupId().length() > 0) {
			showGroupViewId = getGroupId();
		}
		LOG.info(String.format("Enter view (%s)", viewId));
		ClientUI.getDesktopWindow().showView(viewId, showGroupViewId);
	}

	/**
	 * @return the viewId
	 */
	public String getViewId() {
		return viewId;
	}

	/**
	 * @param viewId
	 *            the viewId to set
	 */
	public void setViewId(String viewId) {
		this.viewId = viewId;
	}

	/**
	 * @return the groupId
	 */
	public String getGroupId() {
		return groupId;
	}

	/**
	 * @param groupId
	 *            the groupId to set
	 */
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	private String viewId;
	private String groupId;

	private static final Logger LOG = LoggerFactory.getLogger(ShowViewAction.class);
	private static final long serialVersionUID = -1L;
	public static final String ID = "showViewAction";
}