package com.jhw.adm.client.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.ui.ClientUI;

@Component(CloseAllWindowAction.ID)
//@Scope("desktop")
public class CloseAllWindowAction extends AbstractAction {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		ClientUI.getDesktopWindow().closeAllWindow();
	}

	private static final long serialVersionUID = 104670888580587031L;
	public static final String ID = "closeAllWindowAction";
}