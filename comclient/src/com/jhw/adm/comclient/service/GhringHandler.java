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
import com.jhw.adm.server.entity.ports.RingConfig;
import com.jhw.adm.server.entity.ports.RingLinkBak;
import com.jhw.adm.server.entity.ports.RingPortInfo;

/**
 * Ghring
 * 
 * @author xiongbo
 * 
 */
public class GhringHandler extends BaseHandler {
	private static Logger log = Logger.getLogger(GhringHandler.class);
	private AbstractSnmp snmpV2;
	private DataBufferBuilder dataBufferBuilder;
	private static final int SUBLINK = 0;
	private static final int MAINLINK = 1;

	public boolean createRing(String ip, RingConfig ringConfig) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(5 * 1000);

		PDU response = null;
		try {
			int ringId = ringConfig.getRingID();
			byte[] portMember = new byte[2];
			portMember[0] = (byte) ringConfig.getPort1();
			portMember[1] = (byte) ringConfig.getPort2();
			String systemType = ringConfig.getSystemType();
			String port1Type = ringConfig.getPort1Type();
			String port2Type = ringConfig.getPort2Type();
			int ringType = ringConfig.getRingType();

			if (!checkType(systemType, port1Type, port2Type)) {
				return false;
			}

			byte[] buf = dataBufferBuilder.ghringRing(ringId, portMember,
					ringType);

			// byte[] buf = { 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 1, 2, 0, 0, 0,
			// 0, 0, 0 };
			response = snmpV2.snmpSet(OID.GHRING_BATCH_OPERATE, buf);
			if (!checkResponse(response)) {
				return false;
			}

			if (!"Transfer".equalsIgnoreCase(systemType)
					&& !"trans".equalsIgnoreCase(systemType)) {

				buf = dataBufferBuilder.ghringType(ringId, systemType);
				response = snmpV2.snmpSet(OID.GHRING_BATCH_OPERATE, buf);
				if (!checkResponse(response)) {
					return false;
				}

				buf = dataBufferBuilder.ghringPortRole(ringConfig.getPort1(),
						port1Type);
				response = snmpV2.snmpSet(OID.GHRING_BATCH_OPERATE, buf);
				if (!checkResponse(response)) {
					return false;
				}
				buf = dataBufferBuilder.ghringPortRole(ringConfig.getPort2(),
						port2Type);
				response = snmpV2.snmpSet(OID.GHRING_BATCH_OPERATE, buf);
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
		}
	}

	public boolean deleteRing(String ip, RingConfig ringConfig) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);

		PDU response = null;
		try {
			int ringId = ringConfig.getRingID();
			byte[] buf = dataBufferBuilder.ghringDel(ringId);
			response = snmpV2.snmpSet(OID.GHRING_BATCH_OPERATE, buf);
			if (!checkResponse(response)) {
				return false;
			}
			return true;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	public List<RingConfig> getRingConfig(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		Vector<String> oid = null;
		PDU response = null;
		List<RingConfig> ringConfigList = null;
		try {
			response = snmpV2.snmpGet(OID.GHRINGINFONUM);
			int ringNum = 0;
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				ringConfigList = new ArrayList<RingConfig>();
				ringNum = responseVar.elementAt(0).getVariable().toInt();
			}
			if (ringNum == 0) {
				return ringConfigList;
			}
			oid = new Vector<String>();
			oid.add(OID.GHRINGID);
			oid.add(OID.GHSWITCHTYPE);
			oid.add(OID.GHRINGENABLE);
			oid.add(OID.GHRINGSTATE);
			oid.add(OID.GHRINGAUTO);
			oid.add(OID.GHPORTMEMBERS);
			snmpV2.setTimeout(4 * 1000);
			response = snmpV2.snmpGetBulk(oid, 0, ringNum);
			responseVar = checkResponseVar(response);
			if (responseVar != null) {
				int vbsSize = responseVar.size();
				for (int i = 0; i < vbsSize; i++) {
					if (i % 6 == 0) {
						RingConfig ringConfig = new RingConfig();
						ringConfig.setRingID(responseVar.elementAt(i + 0)
								.getVariable().toInt());
						ringConfig.setSystemType(responseVar.elementAt(i + 1)
								.getVariable().toString());
						if (enable.equalsIgnoreCase(responseVar
								.elementAt(i + 2).getVariable().toString())) {
							ringConfig.setRingEnable(true);
						} else {
							ringConfig.setRingEnable(false);
						}

						if (enable.equalsIgnoreCase(responseVar
								.elementAt(i + 4).getVariable().toString())) {
							ringConfig.setRingType(1);
						} else {
							ringConfig.setRingType(0);
						}

						// ringConfig.set
						String ports = responseVar.elementAt(i + 5)
								.getVariable().toString().trim();
						if (!"".equals(ports)) {
							String[] portss = ports.split(" ");
							if (portss.length == 2) {
								ringConfig
										.setPort1(Integer.parseInt(portss[0]));
								ringConfig
										.setPort2(Integer.parseInt(portss[1]));
							} else {
								// 当交换机版本错误，不能读取端口的值时。默认端口值为0
								ringConfig.setPort1(0);
								ringConfig.setPort2(0);
							}
						}
						ringConfigList.add(ringConfig);
					}
				}

				return ringConfigList;
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
			if (oid != null) {
				oid.clear();
			}
		}

	}

	public boolean modifyRingState(String ip, String state) {
		// TODO Auto-generated method stub
		return false;
	}

	public RingLinkBak getRingLinkBak(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		Vector<String> oid = new Vector<String>();
		oid.add(OID.GHRINGLINKID);
		oid.add(OID.GHRINGLINKPORT);
		oid.add(OID.GHRINGLINKROLE);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(oid);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				RingLinkBak ringLinkBak = new RingLinkBak();
				int linkId = responseVar.elementAt(0).getVariable().toInt();
				if (linkId == 0) {
					return ringLinkBak;
				}
				ringLinkBak.setLinkId(responseVar.elementAt(0).getVariable()
						.toInt());
				ringLinkBak.setPortNo(responseVar.elementAt(1).getVariable()
						.toInt());
				ringLinkBak.setLinkRole(responseVar.elementAt(2).getVariable()
						.toString());

				return ringLinkBak;
			}
			return null;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
			oid.clear();
		}
	}

	public boolean createRingLinkBak(String ip, RingLinkBak ringLinkBak) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		try {
			int linkId = ringLinkBak.getLinkId();
			int portId = ringLinkBak.getPortNo();

			int iRole = SUBLINK;
			String role = ringLinkBak.getLinkRole();
			if (role.equalsIgnoreCase("mainlink")) {
				iRole = MAINLINK;
			} else {
				iRole = SUBLINK;
			}
			byte[] buf = dataBufferBuilder.ghringLinkAdd(linkId, portId, iRole);
			response = snmpV2.snmpSet(OID.GHRING_BATCH_OPERATE, buf);
			if (!checkResponse(response)) {
				return false;
			}
			return true;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	public boolean deleteRingLinkBak(String ip, RingLinkBak ringLinkBak) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);

		PDU response = null;
		try {
			int linkId = ringLinkBak.getLinkId();
			int portId = ringLinkBak.getPortNo();
			String role = ringLinkBak.getLinkRole();
			byte[] buf = dataBufferBuilder.ghringLinkDel(linkId, portId, role);
			response = snmpV2.snmpSet(OID.GHRING_BATCH_OPERATE, buf);
			if (!checkResponse(response)) {
				return false;
			}
			return true;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	// For Topology Discovery
	public Map<Integer, String> getRingType(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		Vector<String> oid = null;
		PDU response = null;
		try {
			response = snmpV2.snmpGet(OID.GHRINGINFONUM);
			int ringNum = 0;
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				ringNum = responseVar.elementAt(0).getVariable().toInt();
			}
			if (ringNum == 0) {
				return null;
			}
			oid = new Vector<String>();
			oid.add(OID.GHRINGID);
			oid.add(OID.GHSWITCHTYPE);
			snmpV2.setTimeout(2 * 1000);
			response = snmpV2.snmpGetBulk(oid, 0, ringNum);
			responseVar = checkResponseVar(response);
			if (responseVar != null) {
				int vbsSize = responseVar.size();
				Map<Integer, String> ringTypeMap = new HashMap<Integer, String>();
				for (int i = 0; i < vbsSize; i++) {
					if (i % 2 == 0) {
						int ringID = responseVar.elementAt(i + 0).getVariable()
								.toInt();
						String ringType = responseVar.elementAt(i + 1)
								.getVariable().toString();
						ringTypeMap.put(ringID, ringType);
					}
				}
				return ringTypeMap;
			}
			return null;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
			if (oid != null) {
				oid.clear();
			}
		}
	}

	public List<RingPortInfo> getRingPort(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		Vector<String> oid = null;
		PDU response = null;
		List<RingPortInfo> ringPortList = null;
		try {
			response = snmpV2.snmpGet(OID.GHRINGPORTINFONUM);
			int ringPortInfoNum = 0;
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				ringPortList = new ArrayList<RingPortInfo>();
				ringPortInfoNum = responseVar.elementAt(0).getVariable()
						.toInt();
			}
			if (ringPortInfoNum == 0) {
				return ringPortList;
			}

			snmpV2.setTimeout(2 * 1000);
			oid = new Vector<String>();
			oid.add(OID.GHRINGPORT);
			oid.add(OID.GHRINGPORTSTATE);
			oid.add(OID.GHRINGPORTRINGID);
			oid.add(OID.GHRINGPORTRINGENABLE);
			oid.add(OID.GHRINGPORTRINGROLE);
			oid.add(OID.GHRINGFORSTATE);
			response = snmpV2.snmpGetBulk(oid, 0, ringPortInfoNum);
			responseVar = checkResponseVar(response);
			if (responseVar != null) {
				int vbsSize = responseVar.size();
				for (int i = 0; i < vbsSize; i++) {
					// 6 is size of oid
					if (i % 6 == 0) {
						RingPortInfo ringPortInfo = new RingPortInfo();
						ringPortInfo.setPortNo(responseVar.elementAt(i + 0)
								.getVariable().toInt());
						if ("up".equalsIgnoreCase(responseVar.elementAt(i + 1)
								.getVariable().toString())) {
							ringPortInfo.setStatus(true);
						} else {
							ringPortInfo.setStatus(false);
						}
						ringPortInfo.setRingID(responseVar.elementAt(i + 2)
								.getVariable().toInt());
						ringPortInfo.setRingMode(responseVar.elementAt(i + 3)
								.getVariable().toString());
						ringPortInfo.setPortRole(responseVar.elementAt(i + 4)
								.getVariable().toString());
						ringPortInfo.setTransferStatus(responseVar.elementAt(
								i + 5).getVariable().toString());
						ringPortList.add(ringPortInfo);
					}
				}
				return ringPortList;
			}
			return null;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
			if (oid != null) {
				oid.clear();
			}
		}

	}

	private boolean checkType(String systemType, String port1Type,
			String port2Type) {
		if ("Master".equalsIgnoreCase(systemType)) {
			if (!(("Master".equalsIgnoreCase(port1Type) && "Subsidiary"
					.equalsIgnoreCase(port2Type)) || ("Master"
					.equalsIgnoreCase(port2Type) && "Subsidiary"
					.equalsIgnoreCase(port1Type)))) {
				log.warn("Parameter Master not true");
				return false;
			}
		} else if ("Assitant".equalsIgnoreCase(systemType)
				|| "Edge".equalsIgnoreCase(systemType)) {
			if (!(("Edgeport".equalsIgnoreCase(port1Type) && "None"
					.equalsIgnoreCase(port2Type)) || ("None"
					.equalsIgnoreCase(port2Type) && "Edgeport"
					.equalsIgnoreCase(port1Type)))) {
				log.warn("Parameter Assitant or Edge are not true");
				return false;
			}
		}
		return true;
	}

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
}
