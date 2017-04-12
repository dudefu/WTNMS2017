package com.jhw.adm.comclient.service;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;

/**
 * Ping
 * 
 * @author xiongbo
 * 
 */
public class PingHandler extends BaseHandler {
	private static Logger log = Logger.getLogger(PingHandler.class);
	private AbstractSnmp snmpV2;
	// For detect
	private final String SYSDESCR = "1.3.6.1.2.1.1.1";

	//

	public AbstractSnmp getSnmpV2() {
		return snmpV2;
	}

	public void setSnmpV2(AbstractSnmp snmpV2) {
		this.snmpV2 = snmpV2;
	}

}
