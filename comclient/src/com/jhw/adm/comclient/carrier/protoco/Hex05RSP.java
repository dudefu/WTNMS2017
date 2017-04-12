package com.jhw.adm.comclient.carrier.protoco;

public class Hex05RSP extends DataPacket {

	public Hex05RSP() {
		;
	}

	private int returnSeqNum;
	private int returnCommand;

	public int getReturnSeqNum() {
		return returnSeqNum;
	}

	public void setReturnSeqNum(int returnSeqNum) {
		this.returnSeqNum = returnSeqNum;
	}

	public int getReturnCommand() {
		return returnCommand;
	}

	public void setReturnCommand(int returnCommand) {
		this.returnCommand = returnCommand;
	}
}