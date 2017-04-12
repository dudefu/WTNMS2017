package com.jhw.adm.comclient.carrier.protoco;

import java.util.ArrayList;
import java.util.List;

public class Hex92RSP extends DataPacket {

	public Hex92RSP() {
		listOfPortInfo = new ArrayList<PortInfo>();
	}

	public void addPortInfo(PortInfo portInfo) {
		listOfPortInfo.add(portInfo);
	}

	public int getPortCount() {
		return listOfPortInfo.size();
	}

	public List<PortInfo> getPortInfoList() {
		return listOfPortInfo;
	}

	private List<PortInfo> listOfPortInfo;
}