package com.jhw.adm.comclient.ui;

import java.awt.Container;
import java.io.IOException;
import java.net.DatagramSocket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.jhw.adm.comclient.system.Configuration;

/**
 * FEP Login UI
 * 
 * @author xiongbo
 * 
 */
public class Login {
	public static void main(String[] args) {
		DatagramSocket udpSocket = null;
		try {
			udpSocket = new DatagramSocket(162);
		} catch (IOException e) {
			System.out.println(e);
			JOptionPane.showMessageDialog(null,
					"����ǰ����ر�SNMPc��MG-SOFT��SNMP���������ر������е�ǰ�û���", "ϵͳ��ʾ",
					JOptionPane.NO_OPTION);
			System.exit(0);
		} finally {
			if (udpSocket != null) {
				udpSocket.close();
			}
		}

		Configuration.loadSetupConf();

		if (checkPortIsUsed(Configuration.trap_port,
				"����ǰ����ر�SNMPc��MG-SOFT��SNMP�������������Ƿ�����ǰ�û������У�")) {
			System.exit(0);
		}

		AbstractApplicationContext context = new FileSystemXmlApplicationContext(
				"conf/comclientContext.xml");
		context.registerShutdownHook();

		// Log init
		Configuration.initLoggerConf();

		if (checkPortIsUsed(Configuration.devicelog_listenport, "�豸��־�����˿� "
				+ Configuration.devicelog_listenport + " ��ռ�ã�����������˿ڣ�")) {
			System.exit(0);
		}

		LoginUI loginUI = (LoginUI) context.getBean("loginUI");
		loginUI.login(true);

	}

	private static boolean checkPortIsUsed(int port, String msg) {
		boolean isUsed = false;
		DatagramSocket udpSocket = null;
		try {
			udpSocket = new DatagramSocket(port);
		} catch (IOException e) {
			isUsed = true;
			System.out.println(e);
			JOptionPane.showMessageDialog(null, msg, "ϵͳ��ʾ",
					JOptionPane.NO_OPTION);
		} finally {
			if (udpSocket != null) {
				udpSocket.close();
			}
		}
		return isUsed;
	}

	void Simple() {
		JFrame frame = new JFrame();
		Container contentPane = frame.getContentPane();
		JButton button = new JButton();
		contentPane.add(button);
	}
}
