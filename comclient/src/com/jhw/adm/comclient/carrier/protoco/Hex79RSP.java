package com.jhw.adm.comclient.carrier.protoco;

public class Hex79RSP extends DataPacket {

	public Hex79RSP() {
	}

	private int packetIndex;

	public int getPacketIndex() {
		return packetIndex;
	}

	public void setPacketIndex(int packetIndex) {
		this.packetIndex = packetIndex;
	}
}