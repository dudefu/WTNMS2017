package com.jhw.adm.client.views.carrier;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.util.Constants;

public class CarrierTools {
	private static final String FIEL_NAME = "conf/subnet.xml";
	/**
	 * 写入subnet.xml配置文件
	 * @param obj
	 */
	public static void writeXml(Object obj) {
		File fo = new File(FIEL_NAME);
		FileOutputStream fos = null;
		XMLEncoder encoder = null;
		try {
			fos = new FileOutputStream(fo);
			encoder = new XMLEncoder(fos);
			encoder.writeObject(obj);
			encoder.flush();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (encoder != null) {
				encoder.close();
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException exx) {
					exx.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 读取subnet.xml配置文件
	 * @param obj
	 */
	public static ArrayList<List> readXml() {
		File fin = new File(FIEL_NAME);
		if (fin == null || !(fin.exists())){
			return null;
		}
		FileInputStream fis = null;
		XMLDecoder decoder = null;
		ArrayList<List> list = null;
		try {
			fis = new FileInputStream(fin);
			decoder = new XMLDecoder(fis);
			list = (ArrayList<List>) decoder.readObject();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (decoder != null) {
				decoder.close();
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException exx) {
					exx.printStackTrace();
				}
			}
		}
		
		return list;
	}
}
