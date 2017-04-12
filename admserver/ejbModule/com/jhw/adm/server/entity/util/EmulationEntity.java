package com.jhw.adm.server.entity.util;

import java.io.Serializable;
import java.util.Set;

public class EmulationEntity implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Set<PortSignal> portSignals;
	private byte powerSingal;
	private byte alarmSignal;
	public Set<PortSignal> getPortSignals() {
		return portSignals;
	}
	public void setPortSignals(Set<PortSignal> portSignals) {
		this.portSignals = portSignals;
	}
	public byte getPowerSingal() {
		return powerSingal;
	}
	public void setPowerSingal(byte powerSingal) {
		this.powerSingal = powerSingal;
	}
	public byte getAlarmSignal() {
		return alarmSignal;
	}
	public void setAlarmSignal(byte alarmSignal) {
		this.alarmSignal = alarmSignal;
	}
	
}
