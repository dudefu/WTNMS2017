package com.jhw.adm.comclient.carrier.protoco;

public class Hex95RSP extends DataPacket {

	public Hex95RSP() {
	}

	private int waveBand1;
	private int waveBand2;
	private int timeout1;
	private int timeout2;

	public int getWaveBand1() {
		return waveBand1;
	}

	public void setWaveBand1(int waveBand1) {
		this.waveBand1 = waveBand1;
	}

	public int getWaveBand2() {
		return waveBand2;
	}

	public void setWaveBand2(int waveBand2) {
		this.waveBand2 = waveBand2;
	}

	public int getTimeout1() {
		return timeout1;
	}

	public void setTimeout1(int timeout1) {
		this.timeout1 = timeout1;
	}

	public int getTimeout2() {
		return timeout2;
	}

	public void setTimeout2(int timeout2) {
		this.timeout2 = timeout2;
	}

}