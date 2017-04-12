package com.jhw.adm.comclient.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.DataBufferBuilder;
import com.jhw.adm.comclient.data.OID;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.server.entity.ports.LACPConfig;
import com.jhw.adm.server.entity.ports.LACPInfoEntity;

/**
 * Lacp
 * 
 * @author xiongbo
 * 
 */
public class LacpHandler extends BaseHandler {
	private static Logger log = Logger.getLogger(LacpHandler.class);
	private AbstractSnmp snmpV2;
	private DataBufferBuilder dataBufferBuilder;
	private PortHandler portHandler;

	public boolean configLacp(String ip, List<LACPConfig> lacpList) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		int portNum = portHandler.getPortNum(ip);
		if (portNum == 0) {
			return false;
		}
		snmpV2.setTimeout(4 * 1000);
		PDU response = null;
		try {
			byte[] portId = new byte[portNum];
			int[] mode = new int[portNum];
			String[] key = new String[portNum];
			String[] role = new String[portNum];

			for (LACPConfig lacpConfig : lacpList) {
				int port = lacpConfig.getPortNo();
				boolean isStart = lacpConfig.isApplied();
				String keyVlaue = lacpConfig.getApplyKey();
				int roleVlaue = lacpConfig.getApplyRole();

				port = port - 1;

				portId[port] = 1;
				if (isStart) {
					mode[port] = 1;
				}
				key[port] = keyVlaue;
				if (roleVlaue == 0) {
					role[port] = "active";
				} else {
					role[port] = "passive";
				}

				// String portState = null;
				// if (switchPortEntity.isWorked()) {
				// portState = ENABLED;
				// } else {
				// portState = DISABLED;
				// }
				// String flowCtl = null;
				// if (switchPortEntity.isFlowControl()) {
				// flowCtl = ENABLED;
				// } else {
				// flowCtl = DISABLED;
				// }
				// // TODO
				// byte[] dataBuffer = dataBufferBuilder.lacpModeConfig(
				// portChoice, keyValue);
				// dataBuffer = dataBufferBuilder.lacpKeyConfig(portChoice,
				// keyValue);
				// dataBuffer = dataBufferBuilder.lacpRoleConfig(portChoice,
				// keyValue);
				//
				// response = snmpV2.snmpSet(OID.PORTS_BATCH_OPERATE,
				// dataBuffer);

				// if (!checkResponse(response)) {
				// return false;
				// }
			}
			byte[] dataBuffer = dataBufferBuilder.lacpModeConfig(portId, mode);
			response = snmpV2.snmpSet(OID.LACP_BATCH_OPERATE, dataBuffer);
			if (!checkResponse(response)) {
				return false;
			}
			dataBuffer = dataBufferBuilder.lacpKeyConfig(portId, key);
			response = snmpV2.snmpSet(OID.LACP_BATCH_OPERATE, dataBuffer);
			if (!checkResponse(response)) {
				return false;
			}
			dataBuffer = dataBufferBuilder.lacpRoleConfig(portId, role);
			response = snmpV2.snmpSet(OID.LACP_BATCH_OPERATE, dataBuffer);
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

	public boolean configLacps(String ip, List<LACPConfig> lacpList) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		int portNum = portHandler.getPortNum(ip);
		if (portNum == 0) {
			return false;
		}
		snmpV2.setTimeout(4 * 1000);
		PDU response = null;
		try {

			for (LACPConfig lacpConfig : lacpList) {
				int port = lacpConfig.getPortNo(); // 端口

				boolean isStart = lacpConfig.isApplied();// 模式
				int mode = 0;
				if (isStart) {
					mode = 1;
				}

				int key = 0;
				String keyValue = lacpConfig.getApplyKey();// 密钥
				if (StringUtils.isNumeric(keyValue)) {
					key = NumberUtils.toInt(keyValue);
				} else {
					if (keyValue.equalsIgnoreCase("auto")) {
						key = 0;
					} else {
						return false;
					}
				}

				int roleVlaue = lacpConfig.getApplyRole();// 角色
				String role = "";
				if (roleVlaue == 0) {
					role = "active";
				} else {
					role = "passive";
				}

				byte[] dataBuffer = dataBufferBuilder.lacpModeConfigs(port,
						mode);
				response = snmpV2.snmpSet(OID.LACP_BATCH_OPERATE, dataBuffer);
				if (!checkResponse(response)) {
					return false;
				}
				System.err.println("*********模式**************："
						+ ArrayUtils.toString(dataBuffer));

				dataBuffer = dataBufferBuilder.lacpKeyConfigs(port, key);
				response = snmpV2.snmpSet(OID.LACP_BATCH_OPERATE, dataBuffer);
				if (!checkResponse(response)) {
					return false;
				}
				System.err.println("*********密钥**************："
						+ ArrayUtils.toString(dataBuffer));

				dataBuffer = dataBufferBuilder.lacpRoleConfigs(port, role);
				response = snmpV2.snmpSet(OID.LACP_BATCH_OPERATE, dataBuffer);
				if (!checkResponse(response)) {
					return false;
				}
				System.err.println("*********角色**************："
						+ ArrayUtils.toString(dataBuffer));
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

	public List<LACPConfig> getLacpConfig(String ip) {
		// Here lacpNum also is portnum
		int lacpNum = portHandler.getPortNum(ip);
		if (lacpNum == 0) {
			return null;
		}
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		Vector<String> oids = new Vector<String>();
		oids.add(OID.LACPPORT);
		oids.add(OID.LACPSTATE);
		oids.add(OID.LACPKEY);
		oids.add(OID.LACPROLE);
		PDU response = null;
		try {
			response = snmpV2.snmpGetBulk(oids, 0, lacpNum);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				int vbsSize = responseVar.size();
				List<LACPConfig> lacpList = new ArrayList<LACPConfig>();
				for (int i = 0; i < vbsSize; i++) {
					if (i % 4 == 0) {
						LACPConfig lacpConfig = new LACPConfig();
						lacpConfig.setPortNo(responseVar.elementAt(i + 0)
								.getVariable().toInt());
						if (ENABLED.equalsIgnoreCase(responseVar.elementAt(
								i + 1).getVariable().toString().trim())) {
							lacpConfig.setApplied(true);
						} else {
							lacpConfig.setApplied(false);
						}
						lacpConfig.setApplyKey(responseVar.elementAt(i + 2)
								.getVariable().toString());

						String role = responseVar.elementAt(i + 3)
								.getVariable().toString();
						if ("Passive".equalsIgnoreCase(role)) {
							lacpConfig.setApplyRole(1); // 被动
						} else if ("Active".equalsIgnoreCase(role)) {
							lacpConfig.setApplyRole(0); // 主动
						}
						// TODO

						lacpList.add(lacpConfig);
					}
				}

				return lacpList;
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
			oids.clear();
		}
	}

	public List<LACPInfoEntity> getLacpInfo(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(OID.LACPINFONUM);
			int lacpInfoNum = 0;
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				lacpInfoNum = responseVar.elementAt(0).getVariable().toInt();
			}
			if (lacpInfoNum == 0) {
				return null;
			}
			// When Time Out,may adjust the 'pagesize'
			snmpV2.setTimeout(5 * 1000);
			int pagesize = 10;
			int mod = lacpInfoNum % pagesize;
			int pages = lacpInfoNum / pagesize;
			pages = mod == 0 ? pages : pages + 1;
			Vector<String> oids = new Vector<String>();
			List<LACPInfoEntity> lacpInfoList = new ArrayList<LACPInfoEntity>();
			for (int i = 0; i < pages; i++) {
				int step = i * pagesize;
				oids.add(OID.LACPGROUP + "." + step);
				oids.add(OID.LACP_PEERID + "." + step);
				oids.add(OID.LACP_PEERKEY + "." + step);
				oids.add(OID.LASTCHANGETIME + "." + step);
				oids.add(OID.LACPPORTMEMBER + "." + step);
				if (lacpInfoNum - (i * pagesize) > mod) {
					response = snmpV2.snmpGetBulk(oids, 0, pagesize);
				} else {
					response = snmpV2.snmpGetBulk(oids, 0, mod);
				}
				responseVar = checkResponseVar(response);
				if (responseVar != null) {
					int vbsSize = responseVar.size();
					for (int j = 0; j < vbsSize; j++) {
						if (j % 5 == 0) {
							LACPInfoEntity lacpInfoEntity = new LACPInfoEntity();
							lacpInfoEntity.setGroupName(responseVar.elementAt(
									j + 0).getVariable().toString());
							lacpInfoEntity.setAimSystemID(responseVar
									.elementAt(j + 1).getVariable().toString());
							lacpInfoEntity.setAimApplyKey(responseVar
									.elementAt(j + 2).getVariable().toString());
							lacpInfoEntity.setPortNo(responseVar.elementAt(
									j + 3).getVariable().toString());
							lacpInfoList.add(lacpInfoEntity);
						}
					}
				}
				oids.clear();
			}
			return lacpInfoList;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	// For LLDP
	public Map<String, String[]> getLacpGroup(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(OID.LACPINFONUM);
			int lacpInfoNum = 0;
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				lacpInfoNum = responseVar.elementAt(0).getVariable().toInt();
			}
			if (lacpInfoNum == 0) {
				return null;
			}
			snmpV2.setTimeout(5 * 1000);
			// When Time Out,may adjust the 'pagesize'
			int pagesize = 10;
			int mod = lacpInfoNum % pagesize;
			int pages = lacpInfoNum / pagesize;
			pages = mod == 0 ? pages : pages + 1;
			Vector<String> oids = new Vector<String>();
			Map<String, String[]> lacpGroupMap = new HashMap<String, String[]>();
			for (int i = 0; i < pages; i++) {
				int step = i * pagesize;
				oids.add(OID.LACPGROUP + "." + step);
				oids.add(OID.LACPPORTMEMBER + "." + step);
				if (lacpInfoNum - (i * pagesize) > mod) {
					response = snmpV2.snmpGetBulk(oids, 0, pagesize);
				} else {
					response = snmpV2.snmpGetBulk(oids, 0, mod);
				}
				responseVar = checkResponseVar(response);
				if (responseVar != null) {
					int vbsSize = responseVar.size();
					for (int j = 0; j < vbsSize; j++) {
						if (j % 2 == 0) {
							String group = responseVar.elementAt(j + 0)
									.getVariable().toString();
							String[] portMember = responseVar.elementAt(j + 1)
									.getVariable().toString().trim().split(",");
							lacpGroupMap.put(group, portMember);
						}
					}
				}
				oids.clear();
			}
			return lacpGroupMap;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
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
