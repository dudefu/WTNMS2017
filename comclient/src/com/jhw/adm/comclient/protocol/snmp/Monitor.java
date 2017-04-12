package com.jhw.adm.comclient.protocol.snmp;

import org.snmp4j.CommandResponderEvent;

/**
 * 
 * @author xiongbo
 * 
 */

public interface Monitor {
	void handleTrap(CommandResponderEvent e);
}
