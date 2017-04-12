package com.jhw.adm.comclient.carrier.protoco;

public class DataPacket {

	private int destId;
	private int srcId;
	private int seqNum;
	private int commandCode;
	private byte[] body;
	private String error;

	private static final int BODY_LEN = 0;

	public int getDestId() {
		return destId;
	}

	public void setDestId(int destId) {
		this.destId = destId;
	}

	public int getSrcId() {
		return srcId;
	}

	public void setSrcId(int srcId) {
		this.srcId = srcId;
	}

	public int getSeqNum() {
		return seqNum;
	}

	void setSeqNum(int seqNum) {
		this.seqNum = seqNum;
	}

	public int getCommandCode() {
		return commandCode;
	}

	public void setCommandCode(int commandCode) {
		this.commandCode = commandCode;
	}

	public int getBodyLen() {
		return BODY_LEN;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
}