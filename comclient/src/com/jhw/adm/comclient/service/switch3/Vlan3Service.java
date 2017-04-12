package com.jhw.adm.comclient.service.switch3;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.Switch3OID;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.comclient.service.BaseService;
import com.jhw.adm.comclient.system.Configuration;
import com.jhw.adm.server.entity.level3.Switch3VlanEnity;
import com.jhw.adm.server.entity.level3.Switch3VlanPortEntity;
import com.jhw.adm.server.entity.util.ParmRep;

/**
 * 
 * @author xiongbo
 * 
 */
public class Vlan3Service extends BaseService {
	private MessageSend messageSend;
	private AbstractSnmp snmpV2;

	/**
	 * For Synchronization
	 * 
	 * @param ip
	 * @param community
	 * @return
	 */
	public List<Switch3VlanEnity> getVlanList(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(5 * 1000);
		String[] columnOIDs = { Switch3OID.DOT1QVLANSTATICNAME };
		int columnOIDLength = columnOIDs.length;
		List<TableEvent> tableEventList = null;
		List<Switch3VlanEnity> vlanList = null;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				vlanList = new ArrayList<Switch3VlanEnity>();
				//
				// List<String> ifDescrList = getIfTable(ip, community);
				// if (ifDescrList == null || ifDescrList.size() == 0) {
				// return null;
				// }
				// int portSize = ifDescrList.size();
				// int mod = portSize % 8;// 8-8 bytes
				// int quotient = portSize / 8;
				// int hexNum = mod == 0 ? quotient : quotient + 1;

				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					int vbLength = variableBinding.length;
					for (int i = 0; i < vbLength; i++) {
						if (i % columnOIDLength == 0) {
							Switch3VlanEnity switch3Vlan = new Switch3VlanEnity();

							String vlanIndex = variableBinding[i + 0].getOid()
									.toString();
							vlanIndex = vlanIndex.replaceAll(
									Switch3OID.DOT1QVLANSTATICNAME, "");
							vlanIndex = vlanIndex.substring(1);
							switch3Vlan.setVlanID(Integer.parseInt(vlanIndex));
							switch3Vlan
									.setVlanName(toStringHex(variableBinding[i + 0]
											.getVariable().toString()));
							// TODO
							switch3Vlan.setIpValue(ip);

							vlanList.add(switch3Vlan);
						}
					}
				}
			}
			return vlanList;

		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
		}

	}

	/**
	 * For Synchronization
	 * 
	 * @param ip
	 * @param community
	 * @return
	 */
	public List<Switch3VlanPortEntity> getVlanPortList(String ip,
			String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(5 * 1000);
		String[] columnOIDs = { Switch3OID.DOT1QPVID,
				Switch3OID.DOT1QPORTACCEPTABLEFRAMETYPES,
				Switch3OID.DOT1QPORTINGRESSFILTERING,
				Switch3OID.DOT1QPORTGVRPSTATUS };
		int columnOIDLength = columnOIDs.length;
		List<TableEvent> tableEventList = null;
		List<Switch3VlanPortEntity> vlanPortList = null;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				vlanPortList = new ArrayList<Switch3VlanPortEntity>();

				Map<Integer, String> ifDescrMap = getIfDescr(ip, community);
				if (ifDescrMap == null) {
					return null;
				}

				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					int vbLength = variableBinding.length;
					for (int i = 0; i < vbLength; i++) {
						if (i % columnOIDLength == 0) {
							Switch3VlanPortEntity switch3VlanPort = new Switch3VlanPortEntity();

							String oidIndex = variableBinding[i + 0].getOid()
									.toString();
							oidIndex = oidIndex.replaceAll(Switch3OID.DOT1QPVID
									+ ".", "");
							int portID = Integer.parseInt(oidIndex);
							String portName = ifDescrMap.get(portID);
							switch3VlanPort.setPortID(portID);
							switch3VlanPort.setPortName(portName);
							int vlanID = variableBinding[i + 0].getVariable()
									.toInt();
							switch3VlanPort.setVlanID(vlanID);
							int model = variableBinding[i + 1].getVariable()
									.toInt();
							switch3VlanPort.setModel(model);
							int delTag = variableBinding[i + 2].getVariable()
									.toInt();
							switch3VlanPort.setDelTag(delTag);
							int allowTag = variableBinding[i + 3].getVariable()
									.toInt();
							switch3VlanPort.setAllowTag(allowTag);
							// TODO
							switch3VlanPort.setIpValue(ip);

							vlanPortList.add(switch3VlanPort);
						}
					}
				}
			}
			return vlanPortList;

		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
		}

	}

	public void createVlan(String ip, String client, String clientIp,
			Message message) {
		List<Switch3VlanEnity> vlans = getVlans(message);
		if (vlans != null) {
			Switch3VlanEnity vlan = vlans.get(0);
			boolean result = createVlan(ip,
					Configuration.three_layer_community, vlan);
			ParmRep parmRep = handleVlan(vlans, result);
			parmRep.setDescs(INSERT);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER3,
					parmRep, ip, client, clientIp);
		}
	}

	public void updateVlan(String ip, String client, String clientIp,
			Message message) {
		List<Switch3VlanEnity> vlans = getVlans(message);
		if (vlans != null) {
			Switch3VlanEnity vlan = vlans.get(0);
			boolean result = updateVlan(ip,
					Configuration.three_layer_community, vlan);
			ParmRep parmRep = handleVlan(vlans, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER3,
					parmRep, ip, client, clientIp);
		}
	}

	public void updateVlanPort(String ip, String client, String clientIp,
			Message message) {
		List<Switch3VlanPortEntity> vlanPorts = getVlanPorts(message);
		if (vlanPorts != null) {
			boolean result = updateVlanPort(ip,
					Configuration.three_layer_community, vlanPorts);
			ParmRep parmRep = handleVlanPort(vlanPorts, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER3,
					parmRep, ip, client, clientIp);
		}
	}

	public void deleteVlan(String ip, String client, String clientIp,
			Message message) {
		List<Switch3VlanEnity> vlans = getVlans(message);
		if (vlans != null) {
			// Switch3VlanEnity vlan = vlans.get(0);
			boolean result = false;
			for (Switch3VlanEnity vlan : vlans) {
				result = deleteVlan(ip, Configuration.three_layer_community,
						vlan);
			}
			ParmRep parmRep = handleVlan(vlans, result);
			parmRep.setDescs(DELETE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER3,
					parmRep, ip, client, clientIp);
		}
	}

	private ParmRep handleVlan(List<Switch3VlanEnity> vlans, boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (Switch3VlanEnity vlan : vlans) {
			parmIds.add(vlan.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(Switch3VlanEnity.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private List<Switch3VlanEnity> getVlans(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<Switch3VlanEnity> vlans = null;
		try {
			vlans = (List<Switch3VlanEnity>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return vlans;
	}

	private ParmRep handleVlanPort(List<Switch3VlanPortEntity> vlanPorts,
			boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (Switch3VlanPortEntity vlanPort : vlanPorts) {
			parmIds.add(vlanPort.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(Switch3VlanPortEntity.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private List<Switch3VlanPortEntity> getVlanPorts(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<Switch3VlanPortEntity> vlanPorts = null;
		try {
			vlanPorts = (List<Switch3VlanPortEntity>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return vlanPorts;
	}

	private boolean createVlan(String ip, String community,
			Switch3VlanEnity vlan) {
		if (vlan == null || vlan.getVlanID() == 0 || vlan.getVlanName() == null) {
			return false;
		}
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 5000);
		Map<String, Object> variableBindings = new HashMap<String, Object>();
		variableBindings
				.put(Switch3OID.DOT1QVLANSTATICNAME, vlan.getVlanName());
		PDU response = null;
		try {
			response = snmpV2.snmpTableAddRow(variableBindings,
					Switch3OID.DOT1QVLANSTATICROWSTATUS, vlan.getVlanID() + "");
			return checkResponse(response);
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
			if (variableBindings != null) {
				variableBindings.clear();
			}
		}
	}

	private boolean updateVlan(String ip, String community,
			Switch3VlanEnity vlan) {
		if (vlan == null || vlan.getVlanID() == 0 || vlan.getVlanName() == null) {
			return false;
		}
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 5000);
		PDU response = null;
		try {
			response = snmpV2.snmpSet(Switch3OID.DOT1QVLANSTATICNAME + "."
					+ vlan.getVlanID(), vlan.getVlanName());
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

	/**
	 * Batch update vlan port
	 * 
	 * @param ip
	 * @param community
	 * @param vlanPortList
	 * @return
	 */
	private boolean updateVlanPort(String ip, String community,
			List<Switch3VlanPortEntity> vlanPortList) {
		if (vlanPortList == null) {
			return false;
		}
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 5000);

		Map<String, Object> vbsMap = new HashMap<String, Object>();
		for (Switch3VlanPortEntity vlanPort : vlanPortList) {
			vbsMap.put(Switch3OID.DOT1QPVID + "." + vlanPort.getPortID(),
					vlanPort.getVlanID());
			// vbsMap.put(Switch3OID.DOT1QPORTACCEPTABLEFRAMETYPES + "."
			// + vlanPort.getPortID(), vlanPort.getModel());
			// vbsMap.put(Switch3OID.DOT1QPORTINGRESSFILTERING + "."
			// + vlanPort.getPortID(), vlanPort.getDelTag());
			// vbsMap.put(Switch3OID.DOT1QPORTGVRPSTATUS + "."
			// + vlanPort.getPortID(), vlanPort.getAllowTag());
			log.info(vlanPort.getVlanID() + " " + vlanPort.getModel() + " "
					+ vlanPort.getDelTag() + " " + vlanPort.getAllowTag());
		}

		PDU response = null;
		try {
			response = snmpV2.snmpSet(vbsMap);
			return checkResponse(response);
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
			vbsMap.clear();
		}
	}

	private boolean deleteVlan(String ip, String community,
			Switch3VlanEnity vlan) {
		if (vlan == null || vlan.getVlanID() == 0) {
			return false;
		}
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 5000);
		// Map<String, Object> variableBindings = new HashMap<String, Object>();
		// variableBindings
		// .put(EponOID.DOT1QVLANSTATICNAME, oltVlan.getVlanName());
		PDU response = null;
		try {
			response = snmpV2.snmpTableDeleteRow(
					Switch3OID.DOT1QVLANSTATICROWSTATUS, vlan.getVlanID() + "");
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

	private String getIfDescrs(List<String> ifDescrList, int portSize,
			int hexNum, String hexStrings) {
		String[] hexStringArray = hexStrings.split(":");
		String hex = "";
		for (int i = 0; i < hexNum; i++) {
			hex += hexStringArray[i];
		}
		String binary = convertHexToBinary(hex);
		hexStrings = "";
		for (int j = 0; j < binary.length(); j++) {
			if (j >= portSize) {
				break;
			}
			if ("1".equals(binary.substring(j, j + 1))) {
				hexStrings += ifDescrList.get(j) + ",";
			}
		}
		return hexStrings;
	}

	private String convertHexToBinary(String hexString) {
		long l = Long.parseLong(hexString, 16);
		String binaryString = Long.toBinaryString(l);
		int shouldBinaryLen = hexString.length() * 4;
		StringBuffer addZero = new StringBuffer();
		int addZeroNum = shouldBinaryLen - binaryString.length();
		for (int i = 1; i <= addZeroNum; i++) {
			addZero.append("0");
		}
		return addZero.toString() + binaryString;

	}

	private String toStringHex(String hexString) {
		String character = "";
		try {
			String[] hexArray = hexString.split(":");
			if (hexArray != null) {
				for (String hex : hexArray) {
					if (!"00".equals(hex)) {
						byte[] baKeyword = new byte[hex.length() / 2];
						for (int i = 0; i < baKeyword.length; i++) {
							baKeyword[i] = (byte) (0xff & Integer.parseInt(hex
									.substring(i * 2, i * 2 + 2), 16));
						}
						character += new String(baKeyword, "utf-8");// UTF-16le:Not
					}
				}
			}
		} catch (NumberFormatException e) {
			log.error(e);
			return null;
		} catch (UnsupportedEncodingException e) {
			log.error(e);
			return null;
		}
		return character;
	}

	// For Vlan
	private List<String> getIfTable(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 5000);
		String[] columnOIDs = { "1.3.6.1.2.1.2.2.1.2", "1.3.6.1.2.1.2.2.1.3" };
		List<TableEvent> tableEventList = null;
		List<String> ifDescrList = null;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				ifDescrList = new ArrayList<String>();
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					for (int i = 0; i < variableBinding.length; i++) {
						if (i % columnOIDs.length == 0) {
							int type = variableBinding[i + 1].getVariable()
									.toInt();
							String ifDescr = variableBinding[i + 0]
									.getVariable().toString();
							if (type == 6 || type == 1) {
								ifDescrList.add(ifDescr);
							}
						}
					}
				}
			}
			return ifDescrList;

		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
		}

	}

	/**
	 * For vlan port
	 * 
	 * @param ip
	 * @param community
	 * @return
	 */
	public Map<Integer, String> getIfDescr(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 2000);
		String[] columnOIDs = { "1.3.6.1.2.1.2.2.1.1", "1.3.6.1.2.1.2.2.1.2" };
		List<TableEvent> tableEventList = null;
		Map<Integer, String> ifDescrMap = null;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				ifDescrMap = new HashMap<Integer, String>();
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					for (int i = 0; i < variableBinding.length; i++) {
						if (i % columnOIDs.length == 0) {
							int index = variableBinding[i + 0].getVariable()
									.toInt();
							String ifDescr = variableBinding[i + 1]
									.getVariable().toString();
							// if (type == 6 || type == 1) {
							// ifDescrList.add(ifDescr);
							// }

							ifDescrMap.put(index, ifDescr);
						}
					}
				}
			}
			return ifDescrMap;

		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
		}

	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

	public AbstractSnmp getSnmpV2() {
		return snmpV2;
	}

	public void setSnmpV2(AbstractSnmp snmpV2) {
		this.snmpV2 = snmpV2;
	}

}
