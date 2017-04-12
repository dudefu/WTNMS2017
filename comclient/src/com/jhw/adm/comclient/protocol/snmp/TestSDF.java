package com.jhw.adm.comclient.protocol.snmp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TestSDF {
	public static void main(String[] args) {
		SimpleDateFormat format = new SimpleDateFormat("MM dd yyyy HH:mm:ss");
		Date date = null;
		String str = "0-Days 10-Hours 25-Minutes 19-Seconds ";
		String strArr = null;
		try {

			strArr = str.substring(0, 1);
			System.out.println(strArr);
			date = format.parse(str);
			System.out.println(date);
			String string = format.format(date);
			System.out.println(string);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
