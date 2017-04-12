package com.jhw.adm.comclient.carrier;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;

import com.jhw.adm.comclient.carrier.protoco.SendDataBulder;
import com.jhw.adm.comclient.carrier.tools.XmlTool;

public class PlcFrameNumXmlOprater {
	// private static String fileName = "mdata" + File.separator + "plc"
	// + File.separator + "PlcDataSendFrameNum.xml";; // xml文件的路径及文件名
	private static String fileName = "conf" + File.separator
			+ "PlcDataSendFrameNum.xml";; // xml文件的路径及文件名

	public PlcFrameNumXmlOprater(String fileName) {
		this.fileName = fileName;
	}

	public static void getFrameNum() {
		// byte frameNum = 0;
		XmlTool xmlTool = new XmlTool();

		try {
			xmlTool.read(fileName);
		} catch (JDOMException e) {
			writeFrameNum(SendDataBulder.frameNum);
			e.printStackTrace();
			return;
		}

		if (xmlTool.getRootElement() == null) {
			writeFrameNum(SendDataBulder.frameNum);
			return;
		}

		Element elementRoot = xmlTool.getRootElement().getChild("Frame");
		if (elementRoot == null) {
			writeFrameNum(SendDataBulder.frameNum);
			return;
		}

		// Iterator ichildren = elementRoot.getChildren().iterator();
		Iterator iattributes = elementRoot.getAttributes().iterator();
		Attribute attribute = (Attribute) iattributes.next();

		if (attribute.getName().equals("Num")) {
			Byte num = new Byte(attribute.getValue());
			SendDataBulder.setFrameNum(num.byteValue());

		}
		/*
		 * while (ichildren.hasNext()) { Element child = (Element)
		 * ichildren.next(); iattributes = child.getAttributes().iterator();
		 * 
		 * while (iattributes.hasNext()) { Attribute attribute = (Attribute)
		 * iattributes.next(); if(attribute.getName().equals("Num")) { Byte num =
		 * new Byte(attribute.getValue()); SendDataBulder.frameNum =
		 * num.byteValue(); } } }
		 */

	}

	public static byte nextSeqNum() {
		XmlTool xmlTool = new XmlTool();
		byte seqNum = 0x00;
		try {
			xmlTool.read(fileName);
		} catch (JDOMException e) {
			writeFrameNum(SendDataBulder.frameNum);
			e.printStackTrace();
			return seqNum;
		}

		if (xmlTool.getRootElement() == null) {
			writeFrameNum(SendDataBulder.frameNum);
			return seqNum;
		}

		Element elementRoot = xmlTool.getRootElement().getChild("Frame");
		if (elementRoot == null) {
			writeFrameNum(SendDataBulder.frameNum);
			return seqNum;
		}

		Iterator iattributes = elementRoot.getAttributes().iterator();
		Attribute attribute = (Attribute) iattributes.next();

		if (attribute.getName().equals("Num")) {
			Byte num = new Byte(attribute.getValue());
			seqNum = num.byteValue();
		}
		writeFrameNum((byte) (seqNum + 1));
		return seqNum;

	}

	public static void writeFrameNum(byte frameNum) {
		XmlTool xmltool = new XmlTool();
		xmltool.createDocument("Mobject");
		Element rootElement = xmltool.getRootElement();

		Element deviesElement = new Element("Frame");
		deviesElement.setAttribute("Num", frameNum + "");

		rootElement.addContent(deviesElement);

		try {
			xmltool.write(fileName);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}
}
