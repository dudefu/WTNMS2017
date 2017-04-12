package com.jhw.adm.client.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.ui.ClientUI;

@Component(ExitAction.ID)
@Scope(Scopes.PROTOTYPE)
public class ExitAction extends AbstractAction {

	@Override
	public void actionPerformed(ActionEvent e) {
		ClientUI.getApplication().shutdown();
	}

	private static final long serialVersionUID = -1L;

	public static final String ID = "exitAction";
}