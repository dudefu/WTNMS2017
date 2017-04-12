package com.jhw.adm.comclient.carrier.protoco;

public class Hex76REQ extends DataPacket {

	public Hex76REQ() {
	}

	public int getBodyLen() {
		return BODY_LEN;
	}

	private int packetCount;
	private int version;
	private long fileSize;
	private long fileCRC32;
	private int interval;

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public static final int BODY_LEN = 13;

	public int getPacketCount() {
		return packetCount;
	}

	public void setPacketCount(int packetCount) {
		this.packetCount = packetCount;
	}

	public long getFileCRC32() {
		return fileCRC32;
	}

	public void setFileCRC32(long fileCRC32) {
		this.fileCRC32 = fileCRC32;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public long getFileSize() {
		return fileSize;
	}

	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public static int getBODY_LEN() {
		return BODY_LEN;
	}
}