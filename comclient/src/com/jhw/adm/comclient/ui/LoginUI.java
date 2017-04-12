package com.jhw.adm.comclient.ui;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;

import javax.swing.ImageIcon;
import javax.swing.JDialog;

/**
 * 
 * @author xiongbo
 * 
 */
public class LoginUI {

	private LoginDialog loginDialog;

	public void login(boolean flag) {
		// String fileUri = LoginUI.class.getResource("").toString();
		// String uri = fileUri.substring(6, fileUri.length());
		// String path = uri.replaceAll("%20", " ") + LOGIN_BACKGROUND;
		ImageIcon imageIcon = new ImageIcon(LoginUI.class
				.getResource(LOGIN_BACKGROUND));

		loginDialog.init(imageIcon.getImage(), flag);
		centerDialog(loginDialog);
		loginDialog.setVisible(true);
	}

	public void reShowloginUI(boolean flag) {

		loginDialog.setFlag(flag);

		loginDialog.getOkButton().setText("х╥хо");
		loginDialog.setVisible(true);
	}

	private final String LOGIN_BACKGROUND = "login-background.png";

	private void centerDialog(JDialog dialog) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Insets scrInsets = Toolkit.getDefaultToolkit().getScreenInsets(
				GraphicsEnvironment.getLocalGraphicsEnvironment()
						.getDefaultScreenDevice().getDefaultConfiguration());

		Rectangle windowSize = new Rectangle();
		windowSize.x = scrInsets.left;
		windowSize.y = scrInsets.top;
		windowSize.width = screenSize.width - scrInsets.left - scrInsets.right;
		windowSize.height = screenSize.height - scrInsets.top
				- scrInsets.bottom;

		Dimension size = dialog.getSize();
		screenSize.height = windowSize.height / 2;
		screenSize.width = windowSize.width / 2;
		size.height = size.height / 2;
		size.width = size.width / 2;
		int y = screenSize.height - size.height;
		int x = screenSize.width - size.width;
		dialog.setLocation(x, y);
	}

	public LoginDialog getLoginDialog() {
		return loginDialog;
	}

	public void setLoginDialog(LoginDialog loginDialog) {
		this.loginDialog = loginDialog;
	}

}
