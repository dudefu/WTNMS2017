package com.jhw.adm.client.test;

import java.awt.Toolkit;

import javax.swing.JOptionPane;

import com.jhw.adm.client.snmp.SnmpData;

public class Dialog {
	public static void main(String[] args) {
		//Toolkit.getDefaultToolkit().beep();
		//JOptionPane.showMessageDialog(null, "           保存配置成功！", "保存成功",JOptionPane.INFORMATION_MESSAGE);
		//JOptionPane.showConfirmDialog(null, "ERROR_MESSAGE", "ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
		//JOptionPane.showInputDialog(null, "ERROR_MESSAGE", "ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
//		try {
//			Thread.sleep(20000);
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//		String str ="54:68:65:20:63:75:72:72:65:6e:74:20:74:65:6d:70:65:72:61:74:75:72:65:20:6f:66:20:73:77:69:74:63:68:20:69:73:20:68:69:67:68:65:72:20:74:68:61:6e:20:34:32:20:a1:e6:20:21";
//		Boolean b = "54:68:65:20:63:75:72:72:65:6e:74:20:74:65:6d:70:65:72:61:74:75:72:65:20:6f:66:20:73:77:69:74:63:68:20:69:73:20:68:69:67:68:65:72:20:74:68:61:6e:20".equals(str.substring(0,146));
//		System.out.println(b);
//		SnmpData.snmpSet("192.168.1.222","private","1.3.6.1.4.1.44405.71.2.23.2.0");
		String TFTP = "tftp\\Tftpd32.exe";
		try {
			// tftpServer = new TftpServiceThread();
			// tftpServer.start();
			Runtime.getRuntime().exec("cmd /c " + TFTP);
		} catch (Exception e) {
			// ("启动TFTP服务失败" + e.getMessage());
			e.printStackTrace();
		}
	}
}
