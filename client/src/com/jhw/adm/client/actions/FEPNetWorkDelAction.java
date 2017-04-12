package com.jhw.adm.client.actions;

import java.awt.event.ActionEvent;

import javax.annotation.PostConstruct;
import javax.swing.Action;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.views.FrontEndManagementView;

@Component(FEPNetWorkDelAction.ID)
public class FEPNetWorkDelAction extends DesktopAction {
	private FrontEndManagementView frontEndManagementView;
	public static final String ID = "fepNetWorkDelAction";
	
	@PostConstruct
	protected void initialize() {
		putValue(Action.NAME, "É¾³ý");
		putValue(Action.SMALL_ICON, getImageRegistry().getImageIcon(ButtonConstants.DELETE));
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		frontEndManagementView = (FrontEndManagementView)getBeanFactory().getBean(FrontEndManagementView.ID);
		
	}
}