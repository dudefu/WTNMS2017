package com.jhw.adm.client.views.switcher;

import java.io.IOException;

public class StopTftp {
	public static void stopTftp(){
		String TFTP = "Tftpd32.exe";
		try {
			Runtime.getRuntime().exec("Taskkill /IM " + TFTP);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
}
