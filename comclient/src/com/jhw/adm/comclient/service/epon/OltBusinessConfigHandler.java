package com.jhw.adm.comclient.service.epon;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.EponOID;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.comclient.service.BaseHandler;
import com.jhw.adm.comclient.service.topology.epon.EponHandler;
import com.jhw.adm.server.entity.epon.OLTDBA;
import com.jhw.adm.server.entity.epon.OLTSTP;
import com.jhw.adm.server.entity.epon.OLTVlan;
import com.jhw.adm.server.entity.epon.OLTVlanPort;
import com.jhw.adm.server.entity.epon.ONULLID;

/**
 * 
 * @author xiongbo
 * 
 */
public class OltBusinessConfigHandler extends BaseHandler {
	private AbstractSnmp snmpV2;
	private EponHandler eponHandler;

	public List<OLTVlan> getVlan(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 2000);
		String[] columnOIDs = { EponOID.DOT1QVLANSTATICNAME,
				EponOID.DOT1QVLANSTATICEGRESSPORTS,
				EponOID.DOT1QVLANFORBIDDENEGRESSPORTS,
				EponOID.DOT1QVLANSTATICUNTAGGEDPORTS };
		List<TableEvent> tableEventList = null;
		List<OLTVlan> vlanList = null;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				vlanList = new ArrayList<OLTVlan>();
				//
				List<String> ifDescrList = getIfTable(ip, community);
				if (ifDescrList == null || ifDescrList.size() == 0) {
					return null;
				}
				int portSize = ifDescrList.size();
				int mod = portSize % 8;// 8-8 bytes
				int quotient = portSize / 8;
				int hexNum = mod == 0 ? quotient : quotient + 1;
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					for (int i = 0; i < variableBinding.length; i++) {
						if (i % columnOIDs.length == 0) {
							OLTVlan oltVlan = new OLTVlan();

							String vlanIndex = variableBinding[i + 0].getOid()
									.toString();
							vlanIndex = vlanIndex.replaceAll(
									EponOID.DOT1QVLANSTATICNAME, "");
							vlanIndex = vlanIndex.substring(1);
							oltVlan.setVlanID(vlanIndex);
							oltVlan
									.setVlanName(toStringHex(variableBinding[i + 0]
											.getVariable().toString()));

							// *
							String egressPorts = getIfDescrs(ifDescrList,
									portSize, hexNum, variableBinding[i + 1]
											.getVariable().toString());
							if (egressPorts.length() > 0) {
								egressPorts = egressPorts.substring(0,
										egressPorts.length() - 1);
								oltVlan.setEgressPort(egressPorts);
								String[] egressPortsArray = egressPorts
										.split(",");
								Set<OLTVlanPort> oltVlanPortSet = new HashSet<OLTVlanPort>();
								for (String egressPort : egressPortsArray) {
									int position = egressPort.indexOf("/");
									int additional = egressPort.indexOf(":");
									if (position != -1) {
										int slot = Integer.parseInt(egressPort
												.substring(position - 1,
														position).trim());
										int port = 0;
										if (additional != -1) {
											port = Integer.parseInt(egressPort
													.substring(position + 1,
															additional).trim());
										} else {
											port = Integer.parseInt(egressPort
													.substring(position + 1)
													.trim());
										}
										OLTVlanPort oltVlanPort = new OLTVlanPort();
										oltVlanPort.setIpvalue(ip);
										oltVlanPort.setSlotNum(slot);
										oltVlanPort.setPortNo(port);
										oltVlanPortSet.add(oltVlanPort);
									}
								}
								oltVlan.setPorts(oltVlanPortSet);
							}
							// *

							// **
							String forbiddenEgressPorts = getIfDescrs(
									ifDescrList, portSize, hexNum,
									variableBinding[i + 2].getVariable()
											.toString());
							if (forbiddenEgressPorts.length() > 0) {
								oltVlan
										.setForbiddenEgressPort(forbiddenEgressPorts
												.substring(0,
														forbiddenEgressPorts
																.length() - 1));
							}
							// **

							// ***
							String untaggedPort = getIfDescrs(ifDescrList,
									portSize, hexNum, variableBinding[i + 3]
											.getVariable().toString());
							if (untaggedPort.length() > 0) {
								oltVlan.setUntaggedPort(untaggedPort.substring(
										0, untaggedPort.length() - 1));
							}
							// ***

							vlanList.add(oltVlan);
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

	public boolean createVlan(String ip, String community, OLTVlan oltVlan) {
		if (oltVlan == null || oltVlan.getVlanID() == null
				|| oltVlan.getVlanName() == null) {
			return false;
		}
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 2000);
		Map<String, Object> variableBindings = new HashMap<String, Object>();
		variableBindings
				.put(EponOID.DOT1QVLANSTATICNAME, oltVlan.getVlanName());
		PDU response = null;
		try {
			response = snmpV2.snmpTableAddRow(variableBindings,
					EponOID.DOT1QVLANSTATICROWSTATUS, oltVlan.getVlanID());
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

	public boolean deleteVlan(String ip, String community, OLTVlan oltVlan) {
		if (oltVlan == null || oltVlan.getVlanID() == null) {
			return false;
		}
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 2000);
		// Map<String, Object> variableBindings = new HashMap<String, Object>();
		// variableBindings
		// .put(EponOID.DOT1QVLANSTATICNAME, oltVlan.getVlanName());
		PDU response = null;
		try {
			response = snmpV2.snmpTableDeleteRow(
					EponOID.DOT1QVLANSTATICROWSTATUS, oltVlan.getVlanID());
			return checkResponse(response);
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
			// variableBindings.clear();
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

	// For Vlan
	public List<String> getIfTable(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 2000);
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

	public OLTDBA getDBA(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 2000);
		Vector<String> oids = new Vector<String>();
		oids.add(EponOID.OLTDBAMODE);
		oids.add(EponOID.OLTDBAALGORITHM);
		oids.add(EponOID.OLTIFDBAPARAMCYCLETIME);
		oids.add(EponOID.OLTIFDBAPARAMDISCFREQ);
		oids.add(EponOID.OLTIFDBAPARAMDISCTIME);
		OLTDBA oltDBA = null;
		PDU response = null;
		try {
			response = snmpV2.snmpGet(oids);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				oltDBA = new OLTDBA();
				oltDBA.setDbaMoble(responseVar.elementAt(0).getVariable()
						.toInt());
				oltDBA.setDbaAlgorithm(responseVar.elementAt(1).getVariable()
						.toInt());
				oltDBA.setDbaCycleTime(responseVar.elementAt(2).getVariable()
						.toInt());
				oltDBA.setDbafrequency(responseVar.elementAt(3).getVariable()
						.toInt());
				oltDBA.setDbaFindTime(responseVar.elementAt(4).getVariable()
						.toInt());
			}
			return oltDBA;
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

	public boolean configDBA(String ip, String community, OLTDBA oltDBA) {
		if (oltDBA == null || oltDBA.getDbaMoble() == 0
				|| oltDBA.getDbaCycleTime() == 0
				|| oltDBA.getDbafrequency() == 0
				|| oltDBA.getDbaFindTime() == 0) {
			return false;
		}
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 2000);
		Map<String, Object> variableBindings = new HashMap<String, Object>();
		variableBindings.put(EponOID.OLTDBAMODE, oltDBA.getDbaMoble());
		variableBindings.put(EponOID.OLTIFDBAPARAMCYCLETIME, oltDBA
				.getDbaCycleTime());
		variableBindings.put(EponOID.OLTIFDBAPARAMDISCFREQ, oltDBA
				.getDbafrequency());
		variableBindings.put(EponOID.OLTIFDBAPARAMDISCTIME, oltDBA
				.getDbaFindTime());
		PDU response = null;
		try {
			response = snmpV2.snmpSet(variableBindings);
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

	public List<ONULLID> getLLID(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 2000);
		String[] columnOIDs = { EponOID.LLIDIFINDEX,
				EponOID.LLIDTOEPONPORTDIID, EponOID.LLIDSEQUENCENO,
				EponOID.LLIDIFPIR, EponOID.LLIDIFCIR, EponOID.LLIDIFFIR };
		List<TableEvent> tableEventList = null;
		List<ONULLID> llidList = null;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				llidList = new ArrayList<ONULLID>();
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					for (int i = 0; i < variableBinding.length; i++) {
						if (i % columnOIDs.length == 0) {
							ONULLID oltLlid = new ONULLID();

							oltLlid.setDiID(variableBinding[i + 0]
									.getVariable().toInt());
							int diid = variableBinding[i + 1].getVariable()
									.toInt();
							int sequenceNo = variableBinding[i + 2]
									.getVariable().toInt();
							String portName = eponHandler.getPortDescByIfDiid(
									ip, community, diid + sequenceNo);
							oltLlid.setPortName(portName);
							oltLlid.setPeakBW(variableBinding[i + 3]
									.getVariable().toInt());
							oltLlid.setAssuredBW(variableBinding[i + 4]
									.getVariable().toInt());
							oltLlid.setStaticBW(variableBinding[i + 5]
									.getVariable().toInt());
							String onuMac = eponHandler.getOnuMac(ip,
									community, portName, sequenceNo);
							oltLlid.setMacValue(onuMac);
							oltLlid.setOltIp(ip);

							llidList.add(oltLlid);
						}
					}
				}
			}
			return llidList;

		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
		}

	}

	public boolean configLLID(String ip, String community, ONULLID onuLLID) {
		if (onuLLID == null || onuLLID.getDiID() == 0) {
			return false;
		}
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 2500);
		LinkedHashMap<String, Object> variableBindings = new LinkedHashMap<String, Object>();
		// variableBindings.put(EponOID.LLIDIFCONFIGROWSTATUS + "."
		// + onuLLID.getDiID(), 2);

		//
		variableBindings.put(EponOID.LLIDIFPIR + "." + onuLLID.getDiID(),
				onuLLID.getPeakBW());
		variableBindings.put(EponOID.LLIDIFCIR + "." + onuLLID.getDiID(),
				onuLLID.getAssuredBW());
		if (onuLLID.getStaticBW() != 0) {
			variableBindings.put(EponOID.LLIDIFFIR + "." + onuLLID.getDiID(),
					onuLLID.getStaticBW());
		}
		//

		// variableBindings.put(EponOID.LLIDIFCONFIGROWSTATUS + "."
		// + onuLLID.getDiID(), 1);

		PDU response = null;
		try {
			response = snmpV2.snmpSet(variableBindings,
					EponOID.LLIDIFCONFIGROWSTATUS + "." + onuLLID.getDiID());
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

	public OLTSTP getSTP(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 2000);
		Vector<String> oids = new Vector<String>();
		oids.add(EponOID.DOT1DSTPPROTOCOLSPECIFICATION);
		oids.add(EponOID.DOT1DSTPPRIORITY);
		oids.add(EponOID.DOT1DSTPTIMESINCETOPOLOGYCHANGE);
		oids.add(EponOID.DOT1DSTPTOPCHANGES);
		oids.add(EponOID.DOT1DSTPDESIGNATEDROOT);
		oids.add(EponOID.DOT1DSTPROOTCOST);
		oids.add(EponOID.DOT1DSTPROOTPORT);
		oids.add(EponOID.DOT1DSTPMAXAGE);
		oids.add(EponOID.DOT1DSTPHELLOTIME);
		oids.add(EponOID.DOT1DSTPHOLDTIME);
		oids.add(EponOID.DOT1DSTPFORWARDDELAY);
		oids.add(EponOID.DOT1DSTPBRIDGEMAXAGE);
		oids.add(EponOID.DOT1DSTPBRIDGEHELLOTIME);
		oids.add(EponOID.DOT1DSTPBRIDGEFORWARDDELAY);
		OLTSTP oltSTP = null;
		PDU response = null;
		try {
			response = snmpV2.snmpGet(oids);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				oltSTP = new OLTSTP();

				oltSTP.setSpecifacation(responseVar.elementAt(0).getVariable()
						.toInt());
				oltSTP.setPriority(responseVar.elementAt(1).getVariable()
						.toInt());
				oltSTP.setTopologyChange(responseVar.elementAt(2).getVariable()
						.toString());
				oltSTP.setTopChanges(responseVar.elementAt(3).getVariable()
						.toInt());
				oltSTP.setDesignatedRoot(responseVar.elementAt(4).getVariable()
						.toString());
				oltSTP.setRootExpenses(responseVar.elementAt(5).getVariable()
						.toInt());
				oltSTP.setStpRootPort(responseVar.elementAt(6).getVariable()
						.toInt());
				oltSTP.setStpMaxAge(responseVar.elementAt(7).getVariable()
						.toInt());
				oltSTP.setHelloTime(responseVar.elementAt(8).getVariable()
						.toInt());
				oltSTP.setHoldTime(responseVar.elementAt(9).getVariable()
						.toInt());
				oltSTP.setForwardDelay(responseVar.elementAt(10).getVariable()
						.toInt());
				oltSTP.setBridgeMaxAge(responseVar.elementAt(11).getVariable()
						.toInt());
				oltSTP.setBridgeHelloTime(responseVar.elementAt(12)
						.getVariable().toInt());
				oltSTP.setBridgewardDelay(responseVar.elementAt(13)
						.getVariable().toInt());
			}
			return oltSTP;
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

	public boolean configSTP(String ip, String community, OLTSTP oltSTP) {
		if (oltSTP == null || oltSTP.getBridgeMaxAge() == 0
				|| oltSTP.getBridgeHelloTime() == 0
				|| oltSTP.getForwardDelay() == 0) {
			return false;
		}
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 2500);
		Map<String, Object> variableBindings = new HashMap<String, Object>();
		variableBindings.put(EponOID.DOT1DSTPPRIORITY, oltSTP.getPriority());
		variableBindings.put(EponOID.DOT1DSTPBRIDGEMAXAGE, oltSTP
				.getBridgeMaxAge());
		variableBindings.put(EponOID.DOT1DSTPBRIDGEHELLOTIME, oltSTP
				.getBridgeHelloTime());
		variableBindings.put(EponOID.DOT1DSTPBRIDGEFORWARDDELAY, oltSTP
				.getBridgewardDelay());
		PDU response = null;
		try {
			response = snmpV2.snmpSet(variableBindings);
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

	public AbstractSnmp getSnmpV2() {
		return snmpV2;
	}

	public void setSnmpV2(AbstractSnmp snmpV2) {
		this.snmpV2 = snmpV2;
	}

	public EponHandler getEponHandler() {
		return eponHandler;
	}

	public void setEponHandler(EponHandler eponHandler) {
		this.eponHandler = eponHandler;
	}

}
