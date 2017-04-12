package com.jhw.adm.client.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(ShowMibbrowserAction.ID)
public class ShowMibbrowserAction extends AbstractAction {

	private static final long serialVersionUID = 1L;
	public static final String ID = "showMibbrowserAction";
	
	private static final Logger LOG = LoggerFactory.getLogger(ShowMibbrowserAction.class);
	
	private static final String JAR = "MibBrowser\\MibBrowser.exe";

	@Override
	public void actionPerformed(ActionEvent e) {
		LOG.info("½øÈëmibä¯ÀÀÆ÷");
		try {
			System.out.println(JAR);
			Runtime.getRuntime().exec( "cmd /c "+JAR);
		} catch (IOException e1) {
			LOG.info("Runtime.exec error", e);
		}
	}
}
