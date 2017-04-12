package com.jhw.adm.comclient.service;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.OID;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.server.entity.util.EmulationEntity;
import com.jhw.adm.server.entity.util.PortSignal;

/**
 * 
 * @author xiongbo
 * 
 */
public class EmulateHandler extends BaseHandler {
	private final String IFNUMBER = "1.3.6.1.2.1.2.1";
	private final String IFINDEX = "1.3.6.1.2.1.2.2.1.1";
	private final String IFTYPE = "1.3.6.1.2.1.2.2.1.3";
	private final String IFOPERSTATUS = "1.3.6.1.2.1.2.2.1.8";
	// TODO
	private final String IFINUCASTPKTS = "1.3.6.1.2.1.2.2.1.11";
	//
	private AbstractSnmp snmpV2;
	private PortHandler portHandler;

	/**
	 * @deprecated
	 * @param ip
	 * @return
	 */
	public EmulationEntity getEmulateData(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(1 * 1000);
		PDU response = null;
		Vector<String> oids = null;
		try {
			response = snmpV2.snmpGet(IFNUMBER);
			int ifNumber = 0;
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				ifNumber = responseVar.elementAt(0).getVariable().toInt();
			}
			if (ifNumber == 0) {
				return null;
			}
			snmpV2.setTimeout(2 * 1000);
			oids = new Vector<String>();
			oids.add(IFINDEX);
			oids.add(IFTYPE);
			oids.add(IFOPERSTATUS);
			oids.add(IFINUCASTPKTS);
			response = snmpV2.snmpGetBulk(oids, 0, ifNumber);
			responseVar = checkResponseVar(response);
			EmulationEntity emulationEntity = null;
			if (responseVar != null) {
				int vbsSize = responseVar.size();
				emulationEntity = new EmulationEntity();
				Set<PortSignal> portSignalSet = new HashSet<PortSignal>();
				for (int i = 0; i < vbsSize; i++) {
					if (i % 4 == 0) {
						int ifType = responseVar.get(i + 1).getVariable()
								.toInt();
						// TODO
						if (ifType == 6) {
							PortSignal portSignal = new PortSignal();
							portSignal.setPortNo((byte) responseVar.get(i + 0)
									.getVariable().toInt());
							portSignal.setPortType((byte) ifType);
							int ifOperStatus = responseVar.get(i + 2)
									.getVariable().toInt();
							if (ifOperStatus == 1 || ifOperStatus == 3) {
								portSignal
										.setWorkingSignal(com.jhw.adm.server.entity.util.Constants.L_ON);
							} else {
								portSignal
										.setWorkingSignal(com.jhw.adm.server.entity.util.Constants.L_OFF);
							}
							int ifInUcastPkts = responseVar.get(i + 3)
									.getVariable().toInt();
							// TODO
							if (ifInUcastPkts > 10) {
								portSignal
										.setDataSingal(com.jhw.adm.server.entity.util.Constants.L_ON);
							} else {
								portSignal
										.setDataSingal(com.jhw.adm.server.entity.util.Constants.L_OFF);
							}
							portSignalSet.add(portSignal);
						}
					}
				}
				emulationEntity
						.setPowerSingal(com.jhw.adm.server.entity.util.Constants.L_ON);
				emulationEntity.setPortSignals(portSignalSet);
			}
			return emulationEntity;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
			if (oids != null) {
				oids.clear();
			}
		}
	}

	public EmulationEntity getEmulate(String ip) {
		int portNum = portHandler.getPortNum(ip);
		if (portNum == 0) {
			return null;
		}
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		Vector<String> oids = null;
		try {
			oids = new Vector<String>();
			oids.add(OID.PORTSID);
			oids.add(OID.PORTS_TYPE);
			oids.add(OID.PORTS_LINK);
			response = snmpV2.snmpGetBulk(oids, 0, portNum);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			EmulationEntity emulationEntity = null;
			if (responseVar != null) {
				int vbsSize = responseVar.size();
				emulationEntity = new EmulationEntity();
				Set<PortSignal> portSignalSet = new HashSet<PortSignal>();
				for (int i = 0; i < vbsSize; i++) {
					if (i % 3 == 0) {
						PortSignal portSignal = new PortSignal();
						portSignal.setPortNo((byte) responseVar.get(i + 0)
								.getVariable().toInt());
						String ifType = responseVar.get(i + 1).getVariable()
								.toString().trim();
						String portLink = responseVar.get(i + 2).getVariable()
								.toString().trim();
						if ("copper".equalsIgnoreCase(ifType)) {
							portSignal.setPortType((byte) 1);
						} else if ("fiber".equalsIgnoreCase(ifType)) {
							portSignal.setPortType((byte) 2);
						} else {
							portSignal.setPortType((byte) 3);
						}

						if ("Down".equalsIgnoreCase(portLink)) {
							portSignal
									.setWorkingSignal(com.jhw.adm.server.entity.util.Constants.L_OFF);
							portSignal
									.setDataSingal(com.jhw.adm.server.entity.util.Constants.L_OFF);
						} else {
							portSignal
									.setWorkingSignal(com.jhw.adm.server.entity.util.Constants.L_ON);
							portSignal
									.setDataSingal(com.jhw.adm.server.entity.util.Constants.L_ON);
						}
						portSignalSet.add(portSignal);
					}
				}
				emulationEntity
						.setPowerSingal(com.jhw.adm.server.entity.util.Constants.L_ON);
				emulationEntity.setPortSignals(portSignalSet);
			}
			return emulationEntity;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
			if (oids != null) {
				oids.clear();
			}
		}
	}

	public AbstractSnmp getSnmpV2() {
		return snmpV2;
	}

	public void setSnmpV2(AbstractSnmp snmpV2) {
		this.snmpV2 = snmpV2;
	}

	public PortHandler getPortHandler() {
		return portHandler;
	}

	public void setPortHandler(PortHandler portHandler) {
		this.portHandler = portHandler;
	}

}
