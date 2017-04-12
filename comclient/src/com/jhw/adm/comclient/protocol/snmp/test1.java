package com.jhw.adm.comclient.protocol.snmp;

public class test1 {
	public static void main(String[] args) {
		String str = "1,5,8,6,7,";
		String[] strT = str.split(",");
		Object obj = vlanCreate(strT);
		System.out.println(obj);
	}

	public static String vlanCreate(String[] str) {
		Object obj = null;
		StringBuilder strb = new StringBuilder();
		String result = null;
		for (int i = 0; i < str.length - 1; i++) {
			obj = strb.append(str[i] + ",");
			result = obj.toString();
			System.out.println(result);
		}
		obj = strb.append(str[str.length - 1]);
		result = obj.toString();
		return result;
	}
}
