package com.jhw.adm.comclient.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.DataBufferBuilder;
import com.jhw.adm.comclient.data.OID;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.server.entity.switchs.IGMPEntity;
import com.jhw.adm.server.entity.switchs.Igmp_vsi;

/**
 * 
 * @author xiongbo
 * 
 */
public class IgmpHandler extends BaseHandler {
	private static Logger log = Logger.getLogger(IgmpHandler.class);
	private AbstractSnmp snmpV2;
	private DataBufferBuilder dataBufferBuilder;
	private PortHandler portHandler;

	public boolean configIgmp(String ip, IGMPEntity igmpEntity) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		int portn = portHandler.getPortNum(ip);
		if (portn == 0) {
			return false;
		}
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		try {
			byte[] portState = new byte[portn];
			String[] ports = igmpEntity.getPorts().split(",");
			for (String port : ports) {
				if (null != port && !("".equals(port))) {
					int porti = Integer.parseInt(port);
					portState[porti - 1] = 1;
				} else {
					for (int i = 0; i < portn; i++) {
						portState[i] = 0;
					}
				}
			}
			int mode = 0;
			boolean isEnable = igmpEntity.isApplied();
			if (isEnable) {
				mode = 1;
			} else {
				mode = 0;
			}
			byte[] dataBuffer = dataBufferBuilder.igmpRouterConfig(portState,
					mode);
			response = snmpV2.snmpSet(OID.IGMP_BATCH_OPERATE, dataBuffer);
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

	public boolean configVlanIgmp(String ip, List<Igmp_vsi> igmpList) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		try {
			boolean result = false;
			for (Igmp_vsi igmpVlan : igmpList) {
				int vlanId = Integer.parseInt(igmpVlan.getVlanId());
				int querier = 0;
				if (igmpVlan.isQuerier()) {
					querier = 1;
				}
				int snooping = 0;
				if (igmpVlan.isSnooping()) {
					snooping = 1;
				}
				byte[] buf = dataBufferBuilder.igmpQuerierConfig(vlanId,
						querier);
				response = snmpV2.snmpSet(OID.IGMP_BATCH_OPERATE, buf);
				boolean querierBool = checkResponse(response);
				// if (!querierBool) {
				// return false;
				// }

				buf = dataBufferBuilder.igmpSnoopingConfig(vlanId, snooping);
				response = snmpV2.snmpSet(OID.IGMP_BATCH_OPERATE, buf);
				boolean snoopBool = checkResponse(response);
				// if (!snoopBool) {
				// return false;
				// }

				result = (result || (querierBool || snoopBool));
				// if (!(querierBool || snoopBool)) {
				// return false;
				// }

			}
			return result;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	public IGMPEntity getIgmp(String ip) {
		snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
		Vector<String> vbs = new Vector<String>();
		// vbs.add(OID.IGMPMODE);
		vbs.add(OID.IGMP_RTIFID);
		vbs.add(OID.IGMP_RTIFSTATE);
		int portn = portHandler.getPortNum(ip);
		if (portn == 0) {
			return null;
		}
		snmpV2.setTimeout(3 * 1000);
		PDU response = null;
		// first one not join circle
		try {
			response = snmpV2.snmpGet(OID.IGMPMODE);
			IGMPEntity iGMPEntity = null;
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				iGMPEntity = new IGMPEntity();
				if (ENABLED.equalsIgnoreCase(responseVar.elementAt(0)
						.getVariable().toString().trim())) {
					iGMPEntity.setApplied(true);
				} else {
					iGMPEntity.setApplied(false);
				}
			}
			response = snmpV2.snmpGetBulk(vbs, 0, portn);
			responseVar = checkResponseVar(response);
			if (responseVar != null) {
				int vbsSize = responseVar.size();
				String ports = "";
				for (int i = 0; i < vbsSize; i++) {
					if (i % 2 == 0) {
						if (responseVar.elementAt(i + 1).getVariable().toInt() == 1) {
							ports += responseVar.elementAt(i + 0).getVariable()
									+ ",";
						}
					}
				}
				if (!isEmpty(ports)) {
					ports = ports.substring(0, ports.length() - 1);
					iGMPEntity.setPorts(ports);
				}
			}

			// if (response != null
			// && SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
			// Vector<VariableBinding> responseVar = response
			// .getVariableBindings();
			// IGMPEntity iGMPEntity = new IGMPEntity();
			// int vbsSize = responseVar.size();
			// if (ENABLED.equalsIgnoreCase(responseVar.elementAt(0)
			// .getVariable().toString())) {
			// iGMPEntity.setApplied(true);
			// } else {
			// iGMPEntity.setApplied(false);
			// }
			// String ports = "";
			// for (int i = 1; i < vbsSize; i++) {
			// if (i % 3 == 0) {
			// if (responseVar.elementAt(i + 1).getVariable().toInt() == 1) {
			// ports += responseVar.elementAt(i + 0).getVariable()
			// + ",";
			// }
			// }
			// }
			// if (!isEmpty(ports)) {
			// ports = ports.substring(0, ports.length() - 1);
			// iGMPEntity.setPorts(ports);
			// }

			return iGMPEntity;
			// }
			// return null;
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

	public List<Igmp_vsi> getVlanIgmp(String ip) {
		snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		Vector<String> vbs = null;
		PDU response = null;
		try {
			response = snmpV2.snmpGet(OID.IGMP_VIDNUM);
			int igmp_vidNum = 0;
			if (response != null
					&& SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
				Vector<VariableBinding> responseVar = response
						.getVariableBindings();
				igmp_vidNum = responseVar.elementAt(0).getVariable().toInt();
			}
			if (igmp_vidNum == 0) {
				return null;
			}

			vbs = new Vector<String>();
			vbs.add(OID.IGMP_VID);
			vbs.add(OID.IGMPSTATE);
			vbs.add(OID.IGMPQUERIER);
			response = snmpV2.snmpGetBulk(vbs, 0, igmp_vidNum);
			if (response != null
					&& SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
				Vector<VariableBinding> responseVar = response
						.getVariableBindings();
				int vbsSize = responseVar.size();
				List<Igmp_vsi> igmpVsiList = new ArrayList<Igmp_vsi>();
				for (int i = 0; i < vbsSize; i++) {
					// 3 is size of oid
					if (i % 3 == 0) {
						Igmp_vsi igmp_vsi = new Igmp_vsi();
						igmp_vsi.setVlanId(responseVar.elementAt(i + 0)
								.getVariable().toString());
						if (ENABLED.equalsIgnoreCase(responseVar.elementAt(
								i + 1).getVariable().toString().trim())) {
							igmp_vsi.setSnooping(true);
						} else {
							igmp_vsi.setSnooping(false);
						}
						if (ENABLED.equalsIgnoreCase(responseVar.elementAt(
								i + 2).getVariable().toString().trim())) {
							igmp_vsi.setQuerier(true);
						} else {
							igmp_vsi.setQuerier(false);
						}
						igmpVsiList.add(igmp_vsi);
					}
				}
				return igmpVsiList;
			}
			return null;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
			if (vbs != null) {
				vbs.clear();
			}
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
