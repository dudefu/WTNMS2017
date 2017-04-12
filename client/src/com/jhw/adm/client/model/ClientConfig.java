package com.jhw.adm.client.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClientConfig implements Serializable {
	public ClientConfig() {
		servers = new ArrayList<ServerInfo>();
	}
	
	public void addServer(ServerInfo server) {
		servers.add(server);
	}
	
	public void removeServer(int index) {
		servers.remove(index);
	}
	
	public ServerInfo getServer(int index) {
		return servers.get(index);
	}
	
	public int getServerCount() {
		return servers.size();
	}
	
	public List<ServerInfo> getServers() {
		return servers;
	}

	public void setServers(List<ServerInfo> servers) {
		this.servers = servers;
	}
	
	public String getLastUser() {
		return lastUser;
	}

	public void setLastUser(String lastUser) {
		this.lastUser = lastUser;
	}

	/**
	 * GIS地图文件
	 */
	public String getMapFileName() {
		return mapFileName;
	}

	public void setMapFileName(String mapFileName) {
		this.mapFileName = mapFileName;
	}

	public int getLastServerIndex() {
		return lastServerIndex;
	}

	public void setLastServerIndex(int lastServerIndex) {
		this.lastServerIndex = lastServerIndex;
	}

	public String getWavFileName() {
		return wavFileName;
	}

	public void setWavFileName(String wavFileName) {
		this.wavFileName = wavFileName;
	}

	public int getRePeatNum() {
		return rePeatNum;
	}

	public void setRePeatNum(int rePeatNum) {
		this.rePeatNum = rePeatNum;
	}
	
	public boolean isDefaultWarningAudio() {
		return defaultWarningAudio;
	}

	public void setDefaultWarningAudio(boolean defaultWarningAudio) {
		this.defaultWarningAudio = defaultWarningAudio;
	}

	private int lastServerIndex;
	private String lastUser;
	private String mapFileName;
	private List<ServerInfo> servers;
	private String wavFileName; //用于告警
	private int rePeatNum;//用于告警
	private boolean defaultWarningAudio = true;
	private static final long serialVersionUID = -1109980515113689636L;
}