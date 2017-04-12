package com.jhw.adm.server.util;

/**
 * Title:             StringResource
 * Description:       资源获取类，根据本地语言，从不同的资源文件读取字符串
 * Copyright:    Copyright (c) 2001
 * Company:           yangxiao
 * @author:           huyongfei
 * @date:             01/31/2002
 * @version 1.0
 */

import java.util.*;
import java.io.FileOutputStream;
import java.io.*;

public class StringResource implements Serializable {
	private ResourceBundle bundle = ResourceBundle.getBundle(
			"com.jhw.adm.server.util.xpath", Locale.getDefault());
	Properties properties;
	FileInputStream in;
	FileOutputStream out;
	private static StringResource sr = null;
	String settingFile = null;

	private StringResource() {
		properties = new Properties();
		try {
			in = new FileInputStream(settingFile);
			properties.load(in);
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		}
	}

	/** 得到资源类唯一的实例 */
	public static StringResource getInstance() {
		if (null == sr) {
			sr = new StringResource();
		}
		return sr;
	}

	public String getString(String key) {
		try {
			String message = sr.bundle.getString(key);
			return new String(message);
		} catch (Exception e) {
			return null;
		}
	}

	public String getSettingString(String key) {
		String message = new String(key);// .getBytes("ISO8859-1"), "gb2312");
		return properties.getProperty(message);

	}

	public static void main(String args[]) {
		StringResource sr = StringResource.getInstance();
		String tem = sr.getSettingString("beginstyle");
		// sr.setSettingString("xxx", "1000");
		tem = sr.getSettingString("beginstyle");
	}
}
