package com.jhw.adm.comclient.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SntpHandler1 {
	public static void main(String[] args) throws ParseException {
		String time = "2016-04-11 15:55:45";
		// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat df = new SimpleDateFormat("dd MM yyyy HH:mm:ss");
		Date date = df.parse(time);
		System.out.println(date);
	}
}
