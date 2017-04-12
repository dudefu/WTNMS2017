package com.jhw.adm.comclient.protocol.snmp;

/**
 * 
 * @author xiongbo
 * 
 */
public interface Trap {

	void start();

	void stop();

	void deliverTrap(Monitor monitor);

}
