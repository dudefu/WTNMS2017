package com.jhw.adm.comclient.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * 
 * @author xiongbo
 * 
 */
public class PropertyFileUtil {
	private static final Logger log = Logger.getLogger(PropertyFileUtil.class);

	public static Properties load(String name) {
		File file = new File(name);
		if (!file.exists())
			return null;

		Properties properties = new Properties();
		InputStream inputStream = null;

		try {
			inputStream = new FileInputStream(name);
			properties.load(inputStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return properties;
	}

	public static void save(Properties properties, String name, String comments) {
		File file = new File(name);
		FileOutputStream output = null;
		try {
			output = new FileOutputStream(file);
			properties.store(output, comments);
		} catch (FileNotFoundException e) {
			log.error(e);
		} catch (IOException e) {
			log.error(e);
		} finally {
			try {
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}