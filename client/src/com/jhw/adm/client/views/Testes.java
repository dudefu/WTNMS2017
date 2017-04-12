package com.jhw.adm.client.views;

import java.util.ArrayList;
import java.util.List;

import com.jhw.adm.server.entity.ports.QOSStormControl;

public class Testes {
	
	public void save(){
		List<QOSStormControl> listss = new ArrayList<QOSStormControl>();
		QOSStormControl qosStormControl = new QOSStormControl();
		qosStormControl.setPortNo(1);
		qosStormControl.setBroadcast(true);
		qosStormControl.setMutilUnicast(true);
		qosStormControl.setUnknowUnicast(false);
		qosStormControl.setPercentNum(3);
		listss.add(qosStormControl);
		setaaa(listss);
	}
	
	private void setaaa(List<QOSStormControl> qosStormControls){
		for (QOSStormControl qosStormControl : qosStormControls) {
			String typeStr = "";
			if (qosStormControl.isBroadcast()
					&& qosStormControl.isMutilUnicast()
					&& qosStormControl.isUnknowUnicast()) {
				typeStr = "all";// 全选
			} else if (!(qosStormControl.isBroadcast()
					|| qosStormControl.isMutilUnicast() || qosStormControl
					.isUnknowUnicast())) {
				typeStr = "none";// 全不选
			} else {
				String type = "";
				if (qosStormControl.isBroadcast()) {
					type = type + "bcast" + "_";
				}
				if (qosStormControl.isMutilUnicast()) {
					type = type + "mcast" + "_";
				}
				if (qosStormControl.isUnknowUnicast()) {
					type = type + "dlf" + "_";
				}

				type = type.substring(0, type.length() - 1);
				typeStr = type;
			}
			String percent = qosStormControl.getPercentNum() + "%";
			byte[] dataBuffer = stormControlConfig(
					qosStormControl.getPortNo(), percent, typeStr);
		}
	}
	private byte[] stormControlConfig(int portid, String level, String type) {
		byte[] buffer = new byte[50];
		byte[] temp = tohl(portid);
		System.arraycopy(temp, 0, buffer, 0, temp.length);// portId
		temp = stringToByte(level);
		System.arraycopy(temp, 0, buffer, 4, temp.length);// level
		temp = stringToByte(type);
		System.arraycopy(temp, 0, buffer, 20, temp.length);// type

		return buffer;
	}
	public byte[] tohl(int n) {
		byte[] b = new byte[4];
		b[3] = (byte) (n & 0xff);
		b[2] = (byte) (n >> 8 & 0xff);
		b[1] = (byte) (n >> 16 & 0xff);
		b[0] = (byte) (n >> 24 & 0xff);
		return b;
	}
	private byte[] stringToByte(String str) {
		int strLeng = str.length();
		byte[] b = new byte[strLeng];
		for (int i = 0; i < strLeng; i++) {
			int ch = (int) str.charAt(i);
			b[i] = (byte) ch;
		}
		return b;
	}
	
	public static void main(String[] ar){
		new Testes().save();
	}
}
