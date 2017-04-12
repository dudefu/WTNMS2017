package com.jhw.adm.comclient.carrier.protoco;

import java.util.ArrayList;
import java.util.List;

public class Hex94REQ extends DataPacket {

	public Hex94REQ() {
		listOfPort = new ArrayList<PortInfo>();
	}

	public void addPort(PortInfo portInfo) {
		listOfPort.add(portInfo);
	}

	public List<PortInfo> getPorts() {
		return listOfPort;
	}

	public int getBodyLen() {
		return getPortCount() * PORT_LEN + COUNT_LEN;
	}

	public int getPortCount() {
		return listOfPort.size();
	}

	private List<PortInfo> listOfPort;

	public static final int PORT_LEN = 7;
	public static final int COUNT_LEN = 1;
}