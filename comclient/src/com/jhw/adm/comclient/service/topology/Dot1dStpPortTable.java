package com.jhw.adm.comclient.service.topology;

/**
 * 
 * @author xiongbo
 * 
 */
public class Dot1dStpPortTable {
	// private String bridgeAddress;// local mac
	// private int pvid;
	private int dot1dStpPort;// local port
	private int dot1dStpPortState;
	// private String dot1dStpPortDesignatedRoot;// root mac
	private int dot1dStpPortPathCost;
	private String dot1dStpPortDesignatedBridge;// peer mac

	// private int dot1dStpPortDesignatedPort;// peer port

	// public String getBridgeAddress() {
	// return bridgeAddress;
	// }
	//
	// public void setBridgeAddress(String bridgeAddress) {
	// this.bridgeAddress = bridgeAddress;
	// }
	//
	// public int getPvid() {
	// return pvid;
	// }
	//
	// public void setPvid(int pvid) {
	// this.pvid = pvid;
	// }

	public int getDot1dStpPort() {
		return dot1dStpPort;
	}

	public void setDot1dStpPort(int dot1dStpPort) {
		this.dot1dStpPort = dot1dStpPort;
	}

	// public String getDot1dStpPortDesignatedRoot() {
	// return dot1dStpPortDesignatedRoot;
	// }
	//
	// public void setDot1dStpPortDesignatedRoot(String
	// dot1dStpPortDesignatedRoot) {
	// this.dot1dStpPortDesignatedRoot = dot1dStpPortDesignatedRoot;
	// }

	public String getDot1dStpPortDesignatedBridge() {
		return dot1dStpPortDesignatedBridge;
	}

	public void setDot1dStpPortDesignatedBridge(
			String dot1dStpPortDesignatedBridge) {
		this.dot1dStpPortDesignatedBridge = dot1dStpPortDesignatedBridge;
	}

	// public int getDot1dStpPortDesignatedPort() {
	// return dot1dStpPortDesignatedPort;
	// }
	//
	// public void setDot1dStpPortDesignatedPort(int dot1dStpPortDesignatedPort)
	// {
	// this.dot1dStpPortDesignatedPort = dot1dStpPortDesignatedPort;
	// }

	public int getDot1dStpPortPathCost() {
		return dot1dStpPortPathCost;
	}

	public void setDot1dStpPortPathCost(int dot1dStpPortPathCost) {
		this.dot1dStpPortPathCost = dot1dStpPortPathCost;
	}

	public int getDot1dStpPortState() {
		return dot1dStpPortState;
	}

	public void setDot1dStpPortState(int dot1dStpPortState) {
		this.dot1dStpPortState = dot1dStpPortState;
	}

}
