package com.jhw.adm.client.core;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.stereotype.Component;

@Component(DateFormatter.ID)
public class DateFormatter {

	public String format(Date date) {
		return format(date, DEF_PATTERN);
	}
	
	public String format(Date date, String pattern) {
		if (null == date){
			return StringUtils.EMPTY;
		}
		
		return DateFormatUtils.format(date, pattern);
	}

	public static final String DEF_PATTERN = "yyyy-MM-dd HH:mm:ss";
	public static final String ID = "dateFormatter";
}