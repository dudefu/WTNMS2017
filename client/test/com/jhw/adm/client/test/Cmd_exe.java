package com.jhw.adm.client.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class Cmd_exe {
	public static void main(String[] args) throws IOException {
//		String command = String.format("cmd.exe /c start iexplore.exe http://%s", "192.168.1.202");
//		Runtime.getRuntime().exec( command);

		//Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "+"http://192.168.1.202");
		//Runtime.getRuntime().exec(command);
//		final String ip = "192.168.1.38";
//		pingDevice(ip);
//		String oid = "1.3.6.1.4.1.44405.71.2.21.1.3.1.4.1";
//		int a = oid.lastIndexOf(".");
//		oid = oid.substring(0, a);
//		System.out.println(a);
//		System.out.println(oid);
//		String path = System.getProperty("user.dir");
//		System.out.println(path+"/com/jhw/adm/client/resources/button/download.png");
		//String command = String.format("cmd.exe /c start Telnet.exe %s", selectedAddress);
		//Runtime.getRuntime().exec("cmd.exe /c start Telnet.exe 192.168.1.38");
		Runtime.getRuntime().exec("D:\\jboss-5.1.0.GA\\bin\\service.bat uninstall");
	}
//	private static void pingDevice(final String ip) {
//		final int MAX_TIMEOUT = 50000;
//		Thread thread = new Thread(new Runnable() {
//			public void run() {
//				long t = 0l;
//				BufferedReader in = null;
//				Runtime r = Runtime.getRuntime();
//				String osName = System.getProperty("os.name").trim();
//				System.out.println(osName);
//				String pingCommand = "";
//				boolean isReceive = false;// 判断是否接收到交换机重启成功
//
//				while (true) {
//					try {
//						if (t > MAX_TIMEOUT || isReceive) {
//							break;
//						}
//						if (osName.startsWith("Windows")) {
//							pingCommand = "ping " + ip + " -n " + 1 + " -w "
//							+ 1000;
//							System.out.println(pingCommand);
//						} else {
//							pingCommand = "ping " + ip + " -c " + 1 + " -w "
//									+ 1000;
//							System.out.println(pingCommand);
//						}
//
//						Process p = r.exec(pingCommand);
//						System.out.println(p);
//						if (p != null) {
//							in = new BufferedReader(new InputStreamReader(p
//									.getInputStream()));
//							String line = null;
//							while ((line = in.readLine()) != null) {
//								System.out.println(line);
//								if (line.endsWith("TTL=64")) {
//									isReceive = true;
//									System.out.println(isReceive);
//									break;
//								}
//							}
//						}
//
//						t = t + 1000;
//						Thread.sleep(1000);
//					} catch (IOException e) {
//						e.printStackTrace();
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					} finally {
//						if (in != null) {
//							try {
//								in.close();
//							} catch (IOException e) {
//								e.printStackTrace();
//							}
//						}
//					}
//				}
//			}
//		});
//		
//		thread.start();
//	}

}

