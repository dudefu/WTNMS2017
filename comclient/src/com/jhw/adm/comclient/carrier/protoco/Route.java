package com.jhw.adm.comclient.carrier.protoco;

public class Route {

	public Route() {
	}

	public Route(int id, int port) {
		this.id = id;
		this.port = port;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private int port;
	private int id;
}