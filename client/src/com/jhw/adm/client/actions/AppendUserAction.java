package com.jhw.adm.client.actions;

import java.awt.event.ActionEvent;

import javax.annotation.PostConstruct;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.ui.ClientUI;

@Component(AppendUserAction.ID)
public class AppendUserAction  extends AbstractAction {

	@PostConstruct
	protected void initialize() {
		putValue(Action.NAME, "添加");
		putValue(Action.SMALL_ICON, imageRegistry.getImageIcon(ButtonConstants.APPEND));
		putValue(Action.SHORT_DESCRIPTION, "添加");
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
//		JOptionPane.showMessageDialog(ClientUI.getRootFrame(),
//				"添加用户",
//				"温馨提示",
//				JOptionPane.YES_NO_OPTION);
	}

	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;

	private static final long serialVersionUID = -1L;

	public static final String ID = "appendUserAction";
}