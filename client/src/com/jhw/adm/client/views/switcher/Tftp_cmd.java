package com.jhw.adm.client.views.switcher;

public class Tftp_cmd {
	public static void StartTftp() {
		String TFTP = "tftp\\Tftpd32.exe";
		try {
			// tftpServer = new TftpServiceThread();
			// tftpServer.start();
			Runtime.getRuntime().exec("cmd /c " + TFTP);
		} catch (Exception e) {
			// ("Æô¶¯TFTP·þÎñÊ§°Ü" + e.getMessage());
			e.printStackTrace();
		}
	}
}
