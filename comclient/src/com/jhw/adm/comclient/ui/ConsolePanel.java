package com.jhw.adm.comclient.ui;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.service.LacpHandler;

public class ConsolePanel {
	private static Logger log = Logger.getLogger(LacpHandler.class);
	private TabbedPanel tabbedPanel;
	private LoginUI loginUI;
	private JFrame frontEndFrame;

	public void show() {
		frontEndFrame = new JFrame();
		frontEndFrame.setTitle("前置机控制台");
		Image image = Toolkit.getDefaultToolkit().getImage(
				ConsolePanel.class.getResource(ICON));
		frontEndFrame.setIconImage(image);
		frontEndFrame.getContentPane().add(tabbedPanel.init());
		frontEndFrame.setSize(1200, 700);

		Dimension srcSize = Toolkit.getDefaultToolkit().getScreenSize();
		int srcX = srcSize.width;
		int srcY = srcSize.height;
		int frmX = frontEndFrame.getWidth();
		int frmY = frontEndFrame.getHeight();
		frontEndFrame.setLocation((srcX - frmX) / 2, (srcY - frmY) / 2);

		frontEndFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frontEndFrame.setVisible(false);

		frontEndFrame
				.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frontEndFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				loginUI.reShowloginUI(false);
			}

			@Override
			public void windowIconified(WindowEvent e) {
				frontEndFrame.setVisible(false);
			}
		});

		systemTray();
	}

	private TrayIcon trayIcon;
	private SystemTray tray;
	private final String ICON = "icon.png";

	private void systemTray() {
		try {
			if (trayIcon == null) {
				if (SystemTray.isSupported()) {

					tray = SystemTray.getSystemTray();
					Image trayImage = Toolkit.getDefaultToolkit().getImage(
							ConsolePanel.class.getResource(ICON));

					PopupMenu trayMenu = new PopupMenu();
					MenuItem showMenu = new MenuItem("显示控制台");
					MenuItem exitMenu = new MenuItem("退出控制台");
					showMenu.addActionListener(showActionListener);
					exitMenu.addActionListener(exitActionListener);
					trayMenu.add(showMenu);
					trayMenu.add(exitMenu);

					trayIcon = new TrayIcon(trayImage, "前置机控制台", trayMenu);
					trayIcon.addActionListener(showActionListener);
					tray.add(trayIcon);
					trayIcon.displayMessage("前置机控制台", "前置机已启动。双击打开主界面。",
							TrayIcon.MessageType.INFO);
				} else {
					log.warn("System don't Support Tray!");
				}
			}
		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	ActionListener showActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			frontEndFrame.setVisible(true);
			frontEndFrame.setExtendedState(JFrame.NORMAL);
			frontEndFrame.toFront();
			frontEndFrame.requestFocus();
		}
	};
	ActionListener exitActionListener = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			loginUI.reShowloginUI(false);
			// tray.remove(trayIcon);
		}
	};

	public TabbedPanel getTabbedPanel() {
		return tabbedPanel;
	}

	public void setTabbedPanel(TabbedPanel tabbedPanel) {
		this.tabbedPanel = tabbedPanel;
	}

	public LoginUI getLoginUI() {
		return loginUI;
	}

	public void setLoginUI(LoginUI loginUI) {
		this.loginUI = loginUI;
	}

}