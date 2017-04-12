package com.jhw.adm.comclient.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.DataBufferBuilder;
import com.jhw.adm.comclient.data.OID;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.server.entity.nets.VlanConfig;
import com.jhw.adm.server.entity.nets.VlanEntity;
import com.jhw.adm.server.entity.nets.VlanPort;
import com.jhw.adm.server.entity.nets.VlanPortConfig;

/**
 * Vlan通信处理
 * 
 * @author xiongbo
 * 
 */
public class VlanHandler extends BaseHandler {
	private static Logger log = Logger.getLogger(VlanHandler.class);
	private AbstractSnmp snmpV2;
	private DataBufferBuilder dataBufferBuilder;
	private PortHandler portHandler;

	public boolean createVlanId(String ip, VlanEntity vlanEntity) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		PDU response = null;
		try {
			Object obj = dataBufferBuilder.createVlanId(vlanEntity.getVlanID());
			response = snmpV2.snmpSet(OID.VLAN_CREATE, obj);
			return checkResponse(response);
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	public boolean createVlanPorts(String ip, VlanEntity vlanEntity) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		int portNum = portHandler.getPortNum(ip);
		if (portNum == 0) {
			return false;
		}
		snmpV2.setTimeout(5 * 1000);
		PDU response = null;
		String strU = null;
		String strT = null;
		Object strUobj = null;
		Object strTobj = null;
		int[] vlanVlue = null;
		String strBu = null;
		String strBt = null;
		StringBuilder strbu = new StringBuilder();
		StringBuilder strbt = new StringBuilder();

		try {
			response = snmpV2.snmpGet(OID.VLANNUM);
			int vlanNum = 0;
			if (response != null
					&& SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
				Vector<VariableBinding> responseVar = response
						.getVariableBindings();
				vlanNum = responseVar.get(0).getVariable().toInt();
			}
			if (vlanNum == 0) {
				return false;
			}
			System.out.println(vlanNum);
			vlanVlue = new int[30];
			for (int i = 1; i <= vlanNum; i++) {
				String oid = OID.VLANID + "." + i;
				response = snmpV2.snmpGet(oid);
				if (response != null
						&& SUCCESS.equalsIgnoreCase(response
								.getErrorStatusText())) {
					Vector<VariableBinding> responseVar = response
							.getVariableBindings();
					int vlanvlue = responseVar.get(0).getVariable().toInt();
					vlanVlue[i - 1] = vlanvlue;
				}
			}

			for (VlanPortConfig vlan_portConfig : vlanEntity.getPortConfig()) {
				String vlanTag = String.valueOf(vlan_portConfig.getPortTag());
				if (!"".equals(vlanTag)) {
					if ("U".equals(vlanTag)) {
						strU = vlan_portConfig.getPortNo() + ",";
						strBu = strbu.append(strU).toString();
						System.out.println(strBu);
					}
					if ("T".equals(vlanTag)) {
						strT = vlan_portConfig.getPortNo() + ",";
						strBt = strbt.append(strT).toString();

					}

				} else {
					return false;
				}
			}
			System.out.println(strBu);
			System.out.println(strBt);
			if (strBu != null) {
				String[] stru = strBu.split(",");
				strUobj = dataBufferBuilder.vlanCreate(stru);
			}
			if (strBt != null) {
				String[] strt = strBt.split(",");
				strTobj = dataBufferBuilder.vlanCreate(strt);
			}
			int vlanID = vlanEntity.getVlanID();
			// boolean flag = false;
			// for (int i = 0; i < vlanNum; i++) {
			// if (vlanID == vlanVlue[i]) {
			// flag = true;
			// }
			// }
			// if (flag == false) {
			// createVlanId();
			// }
			for (int i = 0; i < vlanNum; i++) {
				int a = i + 1;
				if (vlanID == vlanVlue[i]) {
					if (strUobj != null) {
						response = snmpV2.snmpSet(OID.VLAN_CREATEportU + "."
								+ a, strUobj);
					}
					if (strTobj != null) {
						response = snmpV2.snmpSet(OID.VLAN_CREATEportT + "."
								+ a, strTobj);
					}
					break;
				}
			}
			return checkResponse(response);
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	public boolean updateVlan(String ip, List<VlanEntity> vlanList) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		int portNum = portHandler.getPortNum(ip);
		if (portNum == 0) {
			return false;
		}
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		try {
			for (VlanEntity vlanEntity : vlanList) {
				int vlanID = vlanEntity.getVlanID();
				char[] portChar = generateChar(ip, portNum);
				// log.info("vlanID:" + vlanID);
				String portValue = "";
				for (VlanPortConfig vlan_portConfig : vlanEntity
						.getPortConfig()) {
					// TODO
					// portValue += vlan_portConfig.getPortTag();
					// log.info("vlan_portConfig:" + vlan_portConfig.getPortNo()
					// + " " + vlan_portConfig.getPortTag());
					portChar[vlan_portConfig.getPortNo()] = vlan_portConfig
							.getPortTag();
				}
				portValue = generateString(portChar);
				byte[] dataBuffer = dataBufferBuilder.vlanUpdate(vlanID,
						portValue);
				response = snmpV2.snmpSet(OID.VLAN_CREATE, dataBuffer);
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

	// 判断是否存在vlan
	public boolean vlanIsOfNo(String ip, VlanEntity vlanEntity) {

		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		Vector<String> vlanvbs = null;
		PDU response = null;
		int vlanId;
		try {
			response = snmpV2.snmpGet(OID.VLANNUM);
			int vlanNum = 0;
			if (response != null
					&& SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
				Vector<VariableBinding> responseVar = response
						.getVariableBindings();
				vlanNum = responseVar.get(0).getVariable().toInt();
			}
			if (vlanNum == 0) {
				return false;
			}

			vlanvbs = new Vector<String>();
			for (int i = 1; i <= vlanNum; i++) {
				vlanvbs.add(OID.VLANID + "." + i);
			}
			vlanId = vlanEntity.getVlanID();
			response = snmpV2.snmpGet(vlanvbs);
			Vector<VariableBinding> responseVar1 = response
					.getVariableBindings();
			int vbsSize1 = responseVar1.size();
			for (int i = 0; i < vlanNum; i++) {
				int vlanID = Integer.parseInt(responseVar1.elementAt(i)
						.getVariable().toString());
				if (vlanId == vlanID) {
					return true;
				}
			}
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
		return false;
	}

	public boolean deleteVlan(String ip, VlanEntity vlanEntity) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		PDU response = null;
		try {
			Object obj = dataBufferBuilder.vlanDelete(vlanEntity.getVlanID());
			response = snmpV2.snmpSet(OID.VLAN_DELETE, obj);
			return checkResponse(response);
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	public Set<VlanEntity> getVlan(VlanConfig vlanConfig, String ip) {
		snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
		int portNum = portHandler.getPortNum(ip);
		if (portNum == 0) {
			return null;
		}
		snmpV2.setTimeout(10 * 1000);
		Vector<String> vlanvbs = null;
		PDU response = null;
		try {
			response = snmpV2.snmpGet(OID.VLANNUM);
			int vlanNum = 0;
			if (response != null
					&& SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
				Vector<VariableBinding> responseVar = response
						.getVariableBindings();
				vlanNum = responseVar.get(0).getVariable().toInt();
			}
			if (vlanNum == 0) {
				return null;
			}

			vlanvbs = new Vector<String>();
			for (int i = 1; i <= vlanNum; i++) {
				vlanvbs.add(OID.VLANID + "." + i);
			}
			// 修改部分
			// ***********************************
			response = snmpV2.snmpGet(vlanvbs);
			Vector<VariableBinding> responseVar1 = response
					.getVariableBindings();
			int vbsSize1 = responseVar1.size();
			// ********************************

			Set<VlanEntity> vlanSet = new HashSet<VlanEntity>();

			for (int iVlan = 1; iVlan <= vlanNum; iVlan++) {
				vlanvbs.clear();
				for (int port = 2; port <= portNum + 1; port++) { // oid
					vlanvbs.add(OID.VLAN_PORT + "." + port + "." + iVlan);
				}

				response = snmpV2.snmpGet(vlanvbs);

				if (response != null
						&& SUCCESS.equalsIgnoreCase(response
								.getErrorStatusText())) {
					Vector<VariableBinding> responseVar = response
							.getVariableBindings();
					int vbsSize = responseVar.size();

					for (int i = 0; i < vbsSize; i++) {
						// Because of include 'vlanID'
						if (i % (portNum + 1) == 0) {
							VlanEntity vlanEntity = new VlanEntity();
							// 修改部分
							vlanEntity.setVlanID(Integer.parseInt(responseVar1
									.elementAt(iVlan - 1).getVariable()
									.toString()));
							Set<VlanPortConfig> portConfig = new HashSet<VlanPortConfig>();
							int k = 1;
							for (int j = i; j < portNum; j++) {
								VlanPortConfig vlan_portConfig = new VlanPortConfig();
								vlan_portConfig.setPortNo(k++);
								vlan_portConfig.setIpVlue(ip);
								vlan_portConfig.setVlanID(vlanEntity
										.getVlanID()
										+ "");
								if (!isEmpty(responseVar.elementAt(j)
										.getVariable())) {
									vlan_portConfig.setPortTag(responseVar
											.elementAt(j).getVariable()
											.toString().trim().charAt(0));
									portConfig.add(vlan_portConfig);
								}
							}
							vlanEntity.setPortConfig(portConfig);
							vlanEntity.setVlanConfig(vlanConfig);
							vlanSet.add(vlanEntity);
						}
					}
				} else {
					continue;
				}
			}
			return vlanSet;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
			if (vlanvbs != null) {
				vlanvbs.clear();
			}
		}

	}

	public Set<VlanPort> getVlanPort(VlanConfig vlanConfig, String ip) {
		snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
		int portNum = portHandler.getPortNum(ip);
		if (portNum == 0) {
			return null;
		}
		snmpV2.setTimeout(3 * 1000);
		Vector<String> vlanPortvbs = new Vector<String>();
		vlanPortvbs.add(OID.PORTSINFOID);
		vlanPortvbs.add(OID.PORTSPVID);
		vlanPortvbs.add(OID.PORTSPRIORITY);
		PDU response = null;
		try {
			response = snmpV2.snmpGetBulk(vlanPortvbs, 0, portNum);
			if (response != null
					&& SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
				Vector<VariableBinding> responseVar = response
						.getVariableBindings();
				int vbsSize = responseVar.size();
				Set<VlanPort> vlanPortSet = new HashSet<VlanPort>();
				for (int i = 0; i < vbsSize; i++) {
					if (i % 3 == 0) {
						VlanPort vlanPort = new VlanPort();
						vlanPort.setPortNO(responseVar.elementAt(i + 0)
								.getVariable().toInt());
						vlanPort.setPvid(responseVar.elementAt(i + 1)
								.getVariable().toInt());
						vlanPort.setPriority(responseVar.elementAt(i + 2)
								.getVariable().toInt());
						vlanPort.setVlanConfig(vlanConfig);
						vlanPortSet.add(vlanPort);
					}
				}

				return vlanPortSet;
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
			vlanPortvbs.clear();
		}

	}

	public boolean updateVlanPort(String ip, List<VlanPort> vlanPortList) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		PDU response = null;
		try {
			for (VlanPort vlanPort : vlanPortList) {
				int portNo = vlanPort.getPortNO();
				int pvid = vlanPort.getPvid();
				int priority = vlanPort.getPriority();
				Object[] obj = dataBufferBuilder.vlanPortConfigObj(pvid,
						priority);
				response = snmpV2.snmpSet(OID.VLAN_PORTS_UPDATE, obj, portNo);
				// response = snmpV2.snmpSet(OID.PORTS_BATCH_OPERATE,
				// dataBuffer);
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

	private char[] generateChar(String ip, int portNum) {
		// int portNum = portHandler.getPortNum(ip);
		char[] portChar = new char[portNum + 1];
		for (int i = 1; i <= portNum; i++) {
			portChar[i] = '-';
		}
		return portChar;
	}

	private String generateString(char[] portChar) {
		String portValueStr = "";
		int len = portChar.length - 1;
		for (int i = 1; i <= len; i++) {
			portValueStr += portChar[i] + "";
		}
		return portValueStr;
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
