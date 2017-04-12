package com.jhw.adm.client.ui;

public final class ClientUI  {

	private ClientUI() {
		application = new DesktopApplication();
	}

	public static void main(String args[]) {
		getInstance().launch(args);
	}

	private void launch(String[] args) { 
		application.start(args);
	}

	public static DesktopWindow getDesktopWindow() {
		return getInstance().application.getDesktopWindow();
	}
	
	public static DesktopApplication getApplication() {
		return getInstance().application;
	}

	private static ClientUI getInstance() {
		return LazyHolder.instance;
	}

	private static class LazyHolder {
		private static ClientUI instance = new ClientUI();
	}
	
	private final DesktopApplication application;
}