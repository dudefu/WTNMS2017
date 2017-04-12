package com.jhw.adm.server.xmlbuild;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jms.StreamMessage;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.jhw.adm.server.util.StringResource;

public class XMLBuilder {

	private static String path = System.getProperty("user.dir")
			+ System.getProperty("path.separator") + "datas"
			+ System.getProperty("path.separator");

//	public static void main(String[] args) throws IOException,
//			DocumentException {
//
//		BufferedReader reader = new BufferedReader(new FileReader("ew.xml"));
//		String tempStr;
//		String ewXml = "";
//		while ((tempStr = reader.readLine()) != null) {
//			ewXml = ewXml + tempStr;
//			logger.debug(tempStr);
//		}
//		Element root = null;
//		root = DocumentHelper.parseText(ewXml).getRootElement();
//		Attribute rootCmd = root.attribute("cmd");
//		Attribute rootVersion = root.attribute("version");
//		logger.debug("rootNmae = " + root.getName());
//		logger.debug("EW'cmd = " + rootCmd.getValue());
//		logger.debug("EW'version = " + rootVersion.getValue());
//		Element usrName = root.element("Username");
//		logger.debug("EW.Username value = " + usrName.getTextTrim());
//		Element source = root.element("Source");
//		Attribute sourceUns = source.attribute("uns");
//		logger.debug("EW.Source'uns＝" + sourceUns.getValue());
//		Attribute sourceType = source.attribute("type");
//		logger.debug("EW.Source'type = " + sourceType.getValue());
//		// 创建一个Xml文件
//		Element user = DocumentHelper.createElement("User");
//		user.addAttribute("type", "user");
//		user.addElement("name").addAttribute("type", "PinYin").setText(
//				"Julysea");
//		user.addElement("age").setText("29");
//		String oneXml = user.asXML();
//
//		BufferedWriter out = new BufferedWriter(new FileWriter("oneXml.xml"));
//		out.write(oneXml);
//		out.close();
//	}

	public File loadConfigByMac(String macvalue) throws Exception {
		File patchfile = new File(path);
		if (patchfile.exists() && patchfile.isDirectory()) {
			File configFile = new File(path + macvalue);
			if (configFile.exists()) {
				return configFile;
			}
		}
		return null;
	}

	public void newConfigFile(StreamMessage message, String macvalue)
			throws Exception {
		File configFile = isfileExists(macvalue);
		if (configFile != null) {
			boolean del = configFile.delete();
			if (del) {
				configFile = new File(path + macvalue);
				FileOutputStream fos = new FileOutputStream(configFile);
				byte[] datas = new byte[100];
				while (message.readBytes(datas) != -1) {
					fos.write(datas);
				}
				fos.flush();
				fos.close();
			}
		} else {
			configFile = new File(path + macvalue);
			FileOutputStream fos = new FileOutputStream(configFile);
			byte[] datas = new byte[100];
			while (message.readBytes(datas) != -1) {
				fos.write(datas);
			}
			fos.flush();
			fos.close();
		}
	}

	public List<?> getData(String macvalue, Class<?> model) {
		List datas = new ArrayList();
		String name = model.getName();
		String xmlpath = StringResource.getInstance().getString(name);
		Document document = null;
		File file = isfileExists(macvalue);
		SAXReader saxReader = new SAXReader();
		try {
			document = saxReader.read(file);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
//		XStream xs = new XStream();
//		List<Element> elements = document.selectNodes(xmlpath);
//		for (Element element : elements) {
//			Document document1 = DocumentHelper.createDocument();
//			document1.add(element);
//			String xmlcontent = document1.asXML();// write(out);
//			Object obj = xs.fromXML(xmlcontent);
//			datas.add(obj);
//		}
		return datas;
	}

	public void updateData(String macvalue, Class clzz,Object model) {
		String name = clzz.getName();
		String xmlpath = StringResource.getInstance().getString(name);		
		File file = isfileExists(macvalue);
		Document document = null;
		SAXReader saxReader = new SAXReader();
		try {
			document = saxReader.read(file);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		List<Element> elements = document.selectNodes(xmlpath);
		
		
	}

	public void deleteData(String macvalue, Object model) {

	}

	public void delete(String macvalue) {
		File file = isfileExists(macvalue);
		file.delete();
	}

	private File isfileExists(String macvalue) {
		File patchfile = new File(path);
		if (patchfile.exists() && patchfile.isDirectory()) {
			File configFile = new File(path + macvalue+".xml");
			if (configFile.exists()) {
				return configFile;
			} else {
				return null;
			}
		} else {
			patchfile.mkdirs();
			return null;
		}
	}
}
