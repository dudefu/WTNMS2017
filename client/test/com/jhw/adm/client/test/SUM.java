package com.jhw.adm.client.test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;

public class SUM {
	public static void main(String[] args) throws UnknownHostException {
//		int sum = 0;
//		for(int i=11;i<=100;i+=2){
//			sum += i;
//		}
//		System.out.println(sum);
//		InetAddress localHost = InetAddress.getLocalHost();
//		String localAddress = localHost.getHostAddress();
//		System.out.println(localAddress);
		LinkedList<Integer> link = new LinkedList<Integer>();
		link.add(1);
		link.add(2);
		link.add(3);
		link.add(4);
		link.add(5);
		
		for(int i = link.size()-1;i<link.size();i--){
			System.out.println(1);
			System.out.println(link.get(i));
		}
	}
	
	
}
