package com.jhw.adm.server.util;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.regex.Pattern;

public class Tools {
	public final static int LOGOUT_SUCCEED = 0;    //��¼�ɹ�
	public final static int SHUTDOWN_NORMAL = 1;   //�����ر�
	public final static int ABORTING_EXIT = 2;     //�쳣�˳�
	
	/**
	 * ���ñ�־λ���ж�ǰ���Ƿ�����
	 */
	public static boolean isFepOnline = false;
	
	/**
	 * �õ�����IP
	 * @return
	 */
	public static String getLocalIP(){
		String localIp = "";
		try{
			Enumeration e1 = (Enumeration) NetworkInterface.getNetworkInterfaces();
			while (e1.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) e1.nextElement();
				String name = ni.getName();
				Enumeration e2 = ni.getInetAddresses();
				while (e2.hasMoreElements()) {
					InetAddress ia = (InetAddress) e2.nextElement();
					if (ia instanceof Inet6Address){
						continue; // omit IPv6 address
					}
					if (name.equals("eth0")){
						localIp = ia.getHostAddress();
						break;
					}
				}
			}
		}
		catch(Exception e){
			
		}
		return localIp;
	}
	
	/**
	 * �ж��Ƿ�������
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str){ 
	    Pattern pattern = Pattern.compile("[0-9]*"); 
	    return pattern.matcher(str).matches();    
	 } 
	
	
	public static void isNotNull(Object obj, String message) {
		if (obj == null) {
			throw new RuntimeException(message);
		}
	}
}
