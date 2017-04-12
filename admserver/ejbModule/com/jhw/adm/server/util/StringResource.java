package com.jhw.adm.server.util;

/**
 * Title:             StringResource
 * Description:       ��Դ��ȡ�࣬���ݱ������ԣ��Ӳ�ͬ����Դ�ļ���ȡ�ַ���
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

	/** �õ���Դ��Ψһ��ʵ�� */
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
