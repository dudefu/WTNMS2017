package com.jhw.adm.comclient.carrier.protoco;

public class Hex77REQ extends DataPacket {

	public Hex77REQ() {
	}

	public int getBodyLen() {
		return calcPacketLen() + 2;
	}

	private int packetCount;
	private int packetIndex;
	private byte[] fileBuffer;

	public static final int PACKET_LEN = 100;

	public int calcPacketLen() {
		int bodyLen = PACKET_LEN;
		if (getPacketIndex() == (getPacketCount() - 1)) {
			bodyLen = fileBuffer.length - (getPacketCount() - 1) * PACKET_LEN;
		}
		return bodyLen;
	}

	public int getPacketCount() {
		return packetCount;
	}

	public void setPacketCount(int packetCount) {
		this.packetCount = packetCount;
	}

	public int getPacketIndex() {
		return packetIndex;
	}

	public void setPacketIndex(int packetIndex) {
		this.packetIndex = packetIndex;
	}

	public byte[] getFileBuffer() {
		return fileBuffer;
	}

	public void setFileBuffer(byte[] fileBuffer) {
		this.fileBuffer = fileBuffer;
	}
}