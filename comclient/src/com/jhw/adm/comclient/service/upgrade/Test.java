package com.jhw.adm.comclient.service.upgrade;

public class Test {
	public static void main(String[] args) {
		// SimpleTftpClient sfc = new SimpleTftpClient();
		TftpServiceThread t = new TftpServiceThread();
		t.start();
	}
}
