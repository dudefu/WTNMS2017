package com.jhw.adm.server.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateFormatter {
	public static String format(Date date) {
		return format(date, DEF_PATTERN);
	}
	
	public static String format(Date date, String pattern) {
		if (null == date){
			return "";
		}
		SimpleDateFormat dateFormat =new SimpleDateFormat(pattern);
		dateFormat.format(date);
		return dateFormat.format(date);
	}

	public static final String DEF_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String ID = "dateFormatter";

}
