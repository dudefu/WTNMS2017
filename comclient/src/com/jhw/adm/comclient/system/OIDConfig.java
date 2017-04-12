package com.jhw.adm.comclient.system;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * OID≈‰÷√
 * 
 * @author xiongbo
 * 
 */
public class OIDConfig {
	private static Logger log = Logger.getLogger(OIDConfig.class);

	// public static void main(String[] args) {
	// XStream xStream = new XStream(new DomDriver());
	// xStream.alias("oidList", OIDList.class);
	// OIDList oidList = new OIDList();
	// oidList.setLLDPLOCMANADDR("1.3.6.1.4.1.16001.127.1.3.8.1.2");
	// FileOutputStream out = null;
	// try {
	// out = new FileOutputStream("conf\\oidConfig.xml");
	// xStream.toXML(oidList, out);
	// } catch (FileNotFoundException e) {
	// e.printStackTrace();
	// } finally {
	// if (out != null) {
	// try {
	// // out.flush();
	// out.close();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	// xStream = null;
	// }
	// }

	public static OIDList readOIDConfig() {
		XStream xStream = null;
		FileInputStream fis = null;
		OIDList oidList = null;
		try {
			xStream = new XStream(new DomDriver());
			xStream.alias("oidList", OIDList.class);
			oidList = new OIDList();
			fis = new FileInputStream("conf\\oidConfig.xml");
			xStream.fromXML(fis, oidList);
		} catch (Exception e) {
			log.error(e);
			return null;
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			xStream = null;
		}

		// setOIDList(oidList);
		OIDConfig.oidList = oidList;

		return oidList;
	}

	private static OIDList oidList;

	// private static void setOIDList(OIDList oids) {
	// oidList = oids;
	// }

	public static OIDList getOIDList() {
		return oidList;
	}
}
