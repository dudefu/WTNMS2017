package com.jhw.adm.client.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.ui.ClientUI;

@Component(MinimizeAllWindowAction.ID)
//@Scope("desktop")
public class MinimizeAllWindowAction extends AbstractAction {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		ClientUI.getDesktopWindow().minimizeAllWindow();
	}

	private static final long serialVersionUID = -1L;
	public static final String ID = "minimizeAllWindowAction";
}