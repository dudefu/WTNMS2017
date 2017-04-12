package com.jhw.adm.comclient.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.DataBufferBuilder;
import com.jhw.adm.comclient.data.OID;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.server.entity.ports.PC8021x;
import com.jhw.adm.server.entity.ports.SC8021x;

/**
 * 
 * @author xiongbo
 * 
 */
public class Dot1xHandler extends BaseHandler {
	private static Logger log = Logger.getLogger(Dot1xHandler.class);
	private AbstractSnmp snmpV2;
	private DataBufferBuilder dataBufferBuilder;
	private PortHandler portHandler;

	public List<PC8021x> getPort(String ip) {
		snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
		Vector<String> vbs = new Vector<String>();
		vbs.add(OID.DOT1XPORT);
		vbs.add(OID.DOT1X_ADMINSTATE);
		vbs.add(OID.DOT1X_PORTSTATE);
		int portNum = portHandler.getPortNum(ip);
		if (portNum == 0) {
			return null;
		}
		snmpV2.setTimeout(3 * 1000);
		PDU response = null;
		try {
			// Here PORTNUM is dot1xNum.So needn't snmpget for OID of dot1xNum
			response = snmpV2.snmpGetBulk(vbs, 0, portNum);
			if (response != null
					&& SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
				Vector<VariableBinding> responseVar = response
						.getVariableBindings();
				int vbsSize = responseVar.size();
				List<PC8021x> pC8021xList = new ArrayList<PC8021x>();
				for (int i = 0; i < vbsSize; i++)
					// 3 is size of vbs
					if (i % 3 == 0) {
						PC8021x pC8021x = new PC8021x();
						pC8021x.setPortNo(responseVar.elementAt(i + 0)
								.getVariable().toInt());
						// if (!isEmpty(responseVar.elementAt(i + 1))) {
						pC8021x.setAuthoMode(responseVar.elementAt(i + 1)
								.getVariable().toInt());
						// }
						// if (!isEmpty(responseVar.elementAt(i + 2))) {
						if (responseVar.elementAt(i + 2).getVariable()
								.toString().endsWith(ENABLED)) {
							pC8021x.setPortStatus(true);
						} else {
							pC8021x.setPortStatus(false);
						}
						// }
						pC8021xList.add(pC8021x);
					}

				return pC8021xList;
			}// else {
			return null;
			// }
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
			vbs.clear();
		}
	}

	public SC8021x getSys(String ip) {
		snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		Vector<String> vbs = new Vector<String>();
		vbs.add(OID.DOT1XSTATE);
		vbs.add(OID.DOT1X_RADIUSSERVER);
		vbs.add(OID.DOT1X_REAUTHENTICATION);
		vbs.add(OID.DOT1XPERIOD);
		vbs.add(OID.DOT1XTIMEOUT);
		vbs.add(OID.DOT1X_RADUDPPORT);
		vbs.add(OID.DOT1XSECRET);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(vbs);
			if (response != null
					&& SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
				Vector<VariableBinding> responseVar = response
						.getVariableBindings();
				SC8021x sC8021x = new SC8021x();
				if (!isEmpty(responseVar.elementAt(0).getVariable())) {
					if (ENABLED.equalsIgnoreCase(responseVar.elementAt(0)
							.getVariable().toString())) {
						sC8021x.setApplied(true);
					} else {
						sC8021x.setApplied(false);
					}
				}
				if (!isEmpty(responseVar.elementAt(1).getVariable())) {
					sC8021x.setRadiusIP(responseVar.elementAt(1).getVariable()
							.toString());
				}
				if (!isEmpty(responseVar.elementAt(2).getVariable())) {
					if (ENABLED.equalsIgnoreCase(responseVar.elementAt(2)
							.getVariable().toString())) {
						sC8021x.setReCertified(true);
					} else {
						sC8021x.setReCertified(false);
					}
				}
				sC8021x.setReCertifiedScyleTime(responseVar.elementAt(3)
						.getVariable().toInt());
				sC8021x.setEapOutTime(responseVar.elementAt(4).getVariable()
						.toInt());
				sC8021x.setCertifyPort(responseVar.elementAt(5).getVariable()
						.toString());
				if (!isEmpty(responseVar.elementAt(6).getVariable())) {
					sC8021x.setCertifyPW(responseVar.elementAt(6).getVariable()
							.toString());
				}

				return sC8021x;
			}// else {
			return null;
			// }
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
			vbs.clear();
		}
	}

	public boolean portConfig(String ip, List<PC8021x> dot1xPortList) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		PDU response = null;
		try {
			for (PC8021x pC8021x : dot1xPortList) {
				byte[] dataBuffer = dataBufferBuilder.dot1xPortConfig(pC8021x
						.getPortNo(), pC8021x.getAuthoMode() + "");
				response = snmpV2.snmpSet(OID.DOT1X_BATCH_OPERATE, dataBuffer);
				if (!checkResponse(response)) {
					return false;
				}
			}
			return true;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
			dot1xPortList.clear();
		}
	}

	public boolean sysConfig(String ip, SC8021x sC8021x) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		Map<String, Object> oids = new HashMap<String, Object>();
		PDU response = null;
		try {
			if (sC8021x.isApplied()) {
				oids.put(OID.DOT1XSTATE, ENABLED);
			} else {
				oids.put(OID.DOT1XSTATE, DISABLED);
			}
			oids.put(OID.DOT1X_RADIUSSERVER, sC8021x.getRadiusIP());
			if (sC8021x.isReCertified()) {
				oids.put(OID.DOT1X_REAUTHENTICATION, ENABLED);
			} else {
				oids.put(OID.DOT1X_REAUTHENTICATION, DISABLED);
			}
			oids.put(OID.DOT1XPERIOD, sC8021x.getReCertifiedScyleTime());
			oids.put(OID.DOT1XTIMEOUT, sC8021x.getEapOutTime());
			oids.put(OID.DOT1X_RADUDPPORT, sC8021x.getCertifyPort());
			oids.put(OID.DOT1XSECRET, sC8021x.getCertifyPW());
			response = snmpV2.snmpSet(oids);

			return checkResponse(response);
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
			oids.clear();
		}
	}

	// Spring inject
	public AbstractSnmp getSnmpV2() {
		return snmpV2;
	}

	public void setSnmpV2(AbstractSnmp snmpV2) {
		this.snmpV2 = snmpV2;
	}

	public DataBufferBuilder getDataBufferBuilder() {
		return dataBufferBuilder;
	}

	public void setDataBufferBuilder(DataBufferBuilder dataBufferBuilder) {
		this.dataBufferBuilder = dataBufferBuilder;
	}

	public PortHandler getPortHandler() {
		return portHandler;
	}

	public void setPortHandler(PortHandler portHandler) {
		this.portHandler = portHandler;
	}
}
