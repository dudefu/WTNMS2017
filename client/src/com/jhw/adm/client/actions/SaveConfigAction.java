package com.jhw.adm.client.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.springframework.stereotype.Component;

import com.jhw.adm.client.ui.ClientUI;

@Component(SaveConfigAction.ID)
public class SaveConfigAction extends DesktopAction{

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println(1);
	}

	private static final long serialVersionUID = -1L;

	public static final String ID = "SaveConfigAction";

}
