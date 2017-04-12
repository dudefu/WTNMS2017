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
import com.jhw.adm.server.entity.switchs.TrunkConfig;

public class TrunkHandler extends BaseHandler {
	private static Logger log = Logger.getLogger(TrunkHandler.class);
	private AbstractSnmp snmpV2;
	private DataBufferBuilder dataBufferBuilder;
	private PortHandler portHandler;

	public boolean configTrunk(String ip, List<TrunkConfig> trunkList) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		int portnum = portHandler.getPortNum(ip);
		if (portnum == 0) {
			return false;
		}
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		byte[] portb = new byte[portnum];
		try {
			for (TrunkConfig trunkConfig : trunkList) {
				int groupId = trunkConfig.getGroupId();
				String[] ports = trunkConfig.getPorts().split(",");
				for (String port : ports) {
					portb[Integer.parseInt(port) - 1] = 1;
				}
				byte[] dataBuffer = dataBufferBuilder.trunkConfig(groupId,
						portb);
				response = snmpV2.snmpSet(OID.TRUNK_BATCH_OPERATE, dataBuffer);
				if (!checkResponse(response)) {
					return false;
				}
				// Resume
				for (int i = 0; i < portnum; i++) {
					portb[i] = 0;
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

	public boolean deleteTrunk(String ip, List<TrunkConfig> trunkList) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		int portnum = portHandler.getPortNum(ip);
		if (portnum == 0) {
			return false;
		}
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		byte[] portb = new byte[portnum];
		try {
			for (TrunkConfig trunkConfig : trunkList) {
				int groupId = trunkConfig.getGroupId();
				String[] ports = trunkConfig.getPorts().split(",");
				for (String port : ports) {
					portb[Integer.parseInt(port) - 1] = 1;
				}
				byte[] dataBuffer = dataBufferBuilder.trunkConfigDelete(
						groupId, portb);
				response = snmpV2.snmpSet(OID.TRUNK_BATCH_OPERATE, dataBuffer);
				if (!checkResponse(response)) {
					return false;
				}
				// Resume
				for (int i = 0; i < portnum; i++) {
					portb[i] = 0;
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

	public List<TrunkConfig> getTrunk(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		Vector<String> oids = null;
		PDU response = null;
		try {
			response = snmpV2.snmpGet(OID.LINKAGGREGATIONNUM);
			int linkAggregationNum = 0;
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			List<TrunkConfig> trunkList = null;
			if (responseVar != null) {
				trunkList = new ArrayList<TrunkConfig>();
				linkAggregationNum = responseVar.get(0).getVariable().toInt();
			}
			if (linkAggregationNum == 0) {
				return trunkList;
			}
			oids = new Vector<String>();
			oids.add(OID.LINKAGGREGATIONID);
			oids.add(OID.LINKAGGREGATIONTYPE);
			oids.add(OID.LINKAGGREGATIONPORTS);
			snmpV2.setTimeout(4 * 1000);
			response = snmpV2.snmpGetBulk(oids, 0, linkAggregationNum);
			responseVar = checkResponseVar(response);
			if (responseVar != null) {
				int vbsSize = responseVar.size();

				for (int i = 0; i < vbsSize; i++) {
					if (i % 3 == 0) {
						TrunkConfig trunkConfig = new TrunkConfig();
						trunkConfig.setGroupId(responseVar.get(i + 0)
								.getVariable().toInt());
						// if (!isEmpty(responseVar.get(i + 1).getVariable())) {
						// // TODO
						// if (ENABLED.equalsIgnoreCase(responseVar.get(i + 1)
						// .getVariable().toString())) {
						// trunkConfig.setPorts(responseVar.get(i + 2)
						// .getVariable().toString());
						// }
						// }
						String ports = responseVar.get(i + 2).getVariable()
								.toString().trim();
						String[] portss = ports.split(" ");
						ports = "";
						for (String port : portss) {
							ports += port + ",";
						}
						ports = ports.substring(0, ports.length() - 1);
						trunkConfig.setPorts(ports);

						trunkList.add(trunkConfig);
					}
				}
				return trunkList;
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
