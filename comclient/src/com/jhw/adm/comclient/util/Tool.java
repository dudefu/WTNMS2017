package com.jhw.adm.comclient.util;

/**
 * 
 * @author xiongbo
 * 
 */
public class Tool {
	public static String removeBlank(String str) {
		str = str.replaceAll("¡¡", "").replaceAll(" ", "").trim();
		return str;
	}
}
