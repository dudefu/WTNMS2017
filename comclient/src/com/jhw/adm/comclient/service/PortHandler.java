package com.jhw.adm.comclient.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.DataBufferBuilder;
import com.jhw.adm.comclient.data.OID;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.comclient.service.topology.TopologyHandler;
import com.jhw.adm.server.entity.epon.OLTPort;
import com.jhw.adm.server.entity.level3.SwitchPortLevel3;
import com.jhw.adm.server.entity.ports.SwitchPortEntity;

public class PortHandler extends BaseHandler {
	private static Logger log = Logger.getLogger(PortHandler.class);
	private AbstractSnmp snmpV2;
	private DataBufferBuilder dataBufferBuilder;
	private TopologyHandler topologyHandler;
	//
	private Map<String, Integer> portMap = new HashMap<String, Integer>();

	public boolean configPort(String ip, List<SwitchPortEntity> portList) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		Map<String, String> modeMap = new HashMap<String, String>();
		modeMap.put("Auto", "auto");
		modeMap.put("auto", "auto");
		modeMap.put("100fdx", "100_full");
		modeMap.put("100hdx", "100_half");
		modeMap.put("10fdx", "10_full");
		modeMap.put("10hdx", "10_half");
		modeMap.put("no", "no");
		Map<String, Object> oidMap = new HashMap<String, Object>();
		PDU response = null;
		int i = 1;
		try {
			for (SwitchPortEntity switchPortEntity : portList) {
				String portState = null;
				if (switchPortEntity.isWorked()) {
					portState = "enable";
				} else {
					portState = "disable";
				}
				String flowCtl = null;
				if (switchPortEntity.isFlowControl()) {
					flowCtl = "enable";
				} else {
					flowCtl = "disable";
				}
				// TODO
				// byte[] dataBuffer =
				// dataBufferBuilder.portConfig(switchPortEntity.getPortNO(),
				// portState,
				// modeMap.get(switchPortEntity.getConfigMode().trim()),
				// flowCtl,
				// switchPortEntity.getAbandonSetting());

				oidMap.put(OID.PORTSTATE + "." + i, portState);
				oidMap.put(OID.PORTSPEED + "." + i, modeMap.get(switchPortEntity.getConfigMode().trim()));
				oidMap.put(OID.PORTS_FLOWCTL + "." + i, flowCtl);
				oidMap.put(OID.PORTSDISCARD + "." + i, switchPortEntity.getAbandonSetting());

				// response = snmpV2.snmpSet(OID.PORTS_BATCH_OPERATE,
				// dataBuffer);

				i++;
			}
			response = snmpV2.snmpSet(oidMap);
			// if (!checkResponse(response)) {
			// return false;
			// }
			return true;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
			modeMap.clear();
		}
	}

	public Set<SwitchPortEntity> getPortConfig(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		Vector<String> vbs = null;
		PDU response = null;
		try {
			response = snmpV2.snmpGet(OID.PORT_NUM);
			int portNum = 0;
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				portNum = responseVar.get(0).getVariable().toInt();
				// For Common invoker
				portMap.put(ip, portNum);
			}
			if (portNum == 0) {
				return null;
			}

			vbs = new Vector<String>();
			vbs.add(OID.PORTSID);
			vbs.add(OID.PORTSTATE);
			vbs.add(OID.PORTSPEED);
			vbs.add(OID.PORTS_FLOWCTL);
			vbs.add(OID.PORTS_TYPE);
			vbs.add(OID.PORTS_LINK);
			vbs.add(OID.PORTSDISCARD);
			vbs.add(OID.Vendorname);
			vbs.add(OID.VendorPN);
			vbs.add(OID.Vendorrev);
			vbs.add(OID.VendorSN);
			vbs.add(OID.Datecode);
			vbs.add(OID.BRNominal);
			vbs.add(OID.Wavelength);
			vbs.add(OID.TransMedia);
			vbs.add(OID.LengthSM);
			vbs.add(OID.Temperature);
			vbs.add(OID.Voltage);
			vbs.add(OID.TxBias);
			vbs.add(OID.TxPower);
			vbs.add(OID.RxPower);
			vbs.add(OID.TemperatureHighAlarm);
			vbs.add(OID.TemperatureLowAlarm);
			vbs.add(OID.TemperatureHighWarning);
			vbs.add(OID.TemperatureLowWarning);
			vbs.add(OID.VoltageHighAlarm);
			vbs.add(OID.VoltageLowAlarm);
			vbs.add(OID.VoltageHighWarning);
			vbs.add(OID.VoltageLowWarning);
			vbs.add(OID.TxBiasHighAlarm);
			vbs.add(OID.TxBiasLowAlarm);
			vbs.add(OID.TxBiasHighWarning);
			vbs.add(OID.TxBiasLowWarning);
			vbs.add(OID.TxPowerHighAlarm);
			vbs.add(OID.TxPowerLowAlarm);
			vbs.add(OID.TxPowerHighWarning);
			vbs.add(OID.TxPowerLowWarning);
			vbs.add(OID.RxPowerHighAlarm);
			vbs.add(OID.RxPowerLowAlarm);
			vbs.add(OID.RxPowerHighWarning);
			vbs.add(OID.RxPowerLowWarning);

			response = snmpV2.snmpGetBulk(vbs, 0, portNum);
			responseVar = checkResponseVar(response);

			if (responseVar != null) {
				int vbsSize = responseVar.size();
				int n = 0;
				Set<SwitchPortEntity> portSet = new HashSet<SwitchPortEntity>();
				for (int i = 0; i < vbsSize; i++)
					// 7 is size of vbs
					if (i % 41 == 0) {
						SwitchPortEntity switchPortEntity = new SwitchPortEntity();
						switchPortEntity.setPortNO(responseVar.elementAt(i + 0).getVariable().toInt());
						if (ENABLED.equalsIgnoreCase(responseVar.elementAt(i + 1).getVariable().toString().trim())) {
							switchPortEntity.setWorked(true);
						} else {
							switchPortEntity.setWorked(false);
						}
						// Here 'i+5' no wrong
						if (!"Down".equalsIgnoreCase(responseVar.elementAt(i + 5).getVariable().toString().trim())) {
							switchPortEntity.setCurrentMode(responseVar.elementAt(i + 5).getVariable().toString());

						} else {
							switchPortEntity.setCurrentMode("100fdx");
						}

						switchPortEntity.setConfigMode(responseVar.elementAt(i + 2).getVariable().toString());

						// if (!"Auto".equalsIgnoreCase(responseVar.elementAt(
						// i + 2).getVariable().toString().trim())) {
						// switchPortEntity.setCurrentMode(responseVar
						// .elementAt(i + 2).getVariable().toString());
						// } else {
						// switchPortEntity.setCurrentMode("100fdx");
						// }

						if (ENABLED.equalsIgnoreCase(responseVar.elementAt(i + 3).getVariable().toString().trim())) {
							switchPortEntity.setFlowControl(true);
						} else {
							switchPortEntity.setFlowControl(false);
						}
						if ("copper".equalsIgnoreCase(responseVar.elementAt(i + 4).getVariable().toString().trim())) {
							switchPortEntity.setType(com.jhw.adm.server.entity.util.Constants.TX);
						} else {
							switchPortEntity.setType(com.jhw.adm.server.entity.util.Constants.FX);
							switchPortEntity
									.setVendorname(responseVar.elementAt(n + 7).getVariable().toString().trim());
							switchPortEntity.setVendorPN(responseVar.elementAt(n + 8).getVariable().toString().trim());
							switchPortEntity.setVendorrev(responseVar.elementAt(n + 9).getVariable().toString().trim());
							switchPortEntity.setVendorSN(responseVar.elementAt(n + 10).getVariable().toString().trim());
							switchPortEntity.setDatecode(responseVar.elementAt(n + 11).getVariable().toString().trim());
							switchPortEntity
									.setBRNominal(responseVar.elementAt(n + 12).getVariable().toString().trim());
							switchPortEntity
									.setWavelength(responseVar.elementAt(n + 13).getVariable().toString().trim());
							switchPortEntity
									.setTransMedia(responseVar.elementAt(n + 14).getVariable().toString().trim());
							switchPortEntity.setLengthSM(responseVar.elementAt(n + 15).getVariable().toString().trim());
							switchPortEntity
									.setTemperature(responseVar.elementAt(n + 16).getVariable().toString().trim());
							switchPortEntity.setVoltage(responseVar.elementAt(n + 17).getVariable().toString().trim());
							switchPortEntity.setTxBias(responseVar.elementAt(n + 18).getVariable().toString().trim());
							switchPortEntity.setTxPower(responseVar.elementAt(n + 19).getVariable().toString().trim());
							switchPortEntity.setRxPower(responseVar.elementAt(n + 20).getVariable().toString().trim());
							switchPortEntity.setTemperatureHighAlarm(
									responseVar.elementAt(n + 21).getVariable().toString().trim());
							switchPortEntity.setTemperatureLowAlarm(
									responseVar.elementAt(n + 22).getVariable().toString().trim());
							switchPortEntity.setTemperatureHighWarning(
									responseVar.elementAt(n + 23).getVariable().toString().trim());
							switchPortEntity.setTemperatureLowWarning(
									responseVar.elementAt(n + 24).getVariable().toString().trim());
							switchPortEntity
									.setVoltageHighAlarm(responseVar.elementAt(n + 25).getVariable().toString().trim());
							switchPortEntity
									.setVoltageLowAlarm(responseVar.elementAt(n + 26).getVariable().toString().trim());
							switchPortEntity.setVoltageHighWarning(
									responseVar.elementAt(n + 27).getVariable().toString().trim());
							switchPortEntity.setVoltageLowWarning(
									responseVar.elementAt(n + 28).getVariable().toString().trim());
							switchPortEntity
									.setTxBiasHighAlarm(responseVar.elementAt(n + 29).getVariable().toString().trim());
							switchPortEntity
									.setTxBiasLowAlarm(responseVar.elementAt(n + 30).getVariable().toString().trim());
							switchPortEntity.setTxBiasHighWarning(
									responseVar.elementAt(n + 31).getVariable().toString().trim());
							switchPortEntity
									.setTxBiasLowWarning(responseVar.elementAt(n + 32).getVariable().toString().trim());
							switchPortEntity
									.setTxPowerHighAlarm(responseVar.elementAt(n + 33).getVariable().toString().trim());
							switchPortEntity
									.setTxPowerLowAlarm(responseVar.elementAt(n + 34).getVariable().toString().trim());
							switchPortEntity.setTxPowerHighWarning(
									responseVar.elementAt(n + 35).getVariable().toString().trim());
							switchPortEntity.setTxPowerLowWarning(
									responseVar.elementAt(n + 36).getVariable().toString().trim());
							switchPortEntity
									.setRxPowerHighAlarm(responseVar.elementAt(n + 37).getVariable().toString().trim());
							switchPortEntity
									.setRxPowerLowAlarm(responseVar.elementAt(n + 38).getVariable().toString().trim());
							switchPortEntity.setRxPowerHighWarning(
									responseVar.elementAt(n + 39).getVariable().toString().trim());
							switchPortEntity.setRxPowerLowWarning(
									responseVar.elementAt(n + 40).getVariable().toString().trim());
							n = n + 41;
						}
						if (!"Down".equalsIgnoreCase(responseVar.elementAt(i + 5).getVariable().toString().trim())) {
							switchPortEntity.setConnected(true);
						} else {
							switchPortEntity.setConnected(false);
						}

						// switchPortEntity.setDuplexSetting(responseVar
						// .elementAt(i + 2).getVariable().toString());
						// switchPortEntity.setStatus(responseVar.elementAt(i +
						// 3)
						// .getVariable().toInt());
						// switchPortEntity.setStatus(responseVar.elementAt(i +
						// 4)
						// .getVariable().toInt());

						switchPortEntity
								.setAbandonSetting(responseVar.elementAt(i + 6).getVariable().toString().trim());
						portSet.add(switchPortEntity);
					}
				return portSet;
			} // else {
			return null;
			// }
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

	// For common invoker
	public int getPortNum(String ip) {
		Object obj = portMap.get(ip);
		if (obj != null) {
			return (Integer) obj;
		} else {
			log.info("Query PortNum");
			snmpV2.setAddress(ip, Constants.SNMP_PORT);
			snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
			snmpV2.setTimeout(2 * 1000);
			PDU response = null;
			try {
				response = snmpV2.snmpGet(OID.PORT_NUM);
				int portNum = 0;
				Vector<VariableBinding> responseVar = checkResponseVar(response);
				if (responseVar != null) {
					portNum = responseVar.get(0).getVariable().toInt();
					portMap.put(ip, portNum);

					return portNum;
				} // else {
				return 0;
				// }
			} catch (RuntimeException e) {
				log.error(getTraceMessage(e));
				return 0;
			} finally {
				if (response != null) {
					response.clear();
				}
			}
		}
	}

	public List<SwitchPortLevel3> getPortState_layer3(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(2 * 1000);
		// String[] columnOIDs = { "1.3.6.1.2.1.2.2.1.3", "1.3.6.1.2.1.2.2.1.2",
		// "1.3.6.1.2.1.2.2.1.7", "1.3.6.1.2.1.2.2.1.8",
		// "1.3.6.1.2.1.2.2.1.6", "1.3.6.1.2.1.31.1.1.1.15" };
		String[] columnOIDs = { "1.3.6.1.2.1.2.2.1.3", "1.3.6.1.2.1.2.2.1.2", "1.3.6.1.2.1.2.2.1.7",
				"1.3.6.1.2.1.2.2.1.8", "1.3.6.1.2.1.2.2.1.6", "1.3.6.1.2.1.2.2.1.5" };
		List<TableEvent> tableEventList = null;
		List<SwitchPortLevel3> portStateList = null;
		Map<Integer, Object> portIPMap = new HashMap<Integer, Object>();
		Map<Integer, String> vlanIFMap = new HashMap<Integer, String>();
		int i = 0;
		int j = 0;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				portStateList = new ArrayList<SwitchPortLevel3>();
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					int ifType = variableBinding[0].getVariable().toInt();
					String ifDescr = variableBinding[1].getVariable().toString();
					int ifAdminStatus = variableBinding[2].getVariable().toInt();
					int ifOperStatus = variableBinding[3].getVariable().toInt();
					String ifPhysAddress = variableBinding[4].getVariable().toString();
					String ifHighSpeed = variableBinding[5].getVariable().toString();
					j++;
					if (ifType == 6) {
						i++;
						portIPMap.put(i,
								topologyHandler.getSingleNode(ip, community, "1.3.6.1.2.1.17.7.1.4.5.1.1." + i));

						SwitchPortLevel3 switchPortLevel3 = new SwitchPortLevel3();
						switchPortLevel3.setPortName(ifDescr);
						switchPortLevel3.setEnableStr("使能");
						switchPortLevel3.setConnect(ifOperStatus);
						switchPortLevel3.setMacValue(ifPhysAddress);
						if (ifOperStatus == 1) {
							int totalLength = ifHighSpeed.length();
							String ifSpeed = ifHighSpeed.substring(0, totalLength - 6);// 6
																						// is
																						// length
																						// of
																						// '000000'
							switchPortLevel3.setRate(ifSpeed + "Mb/s");
						} else {
							switchPortLevel3.setRate("---");
						}
						portStateList.add(switchPortLevel3);
					} else {
						if (ifDescr.startsWith("V")) {
							vlanIFMap.put(j, ifDescr);
						}
					}
				}
			}
			int listLength = 0;
			if (portStateList != null && (listLength = portStateList.size()) > 0) {
				Map<Integer, String> addrTableMap = topologyHandler.getAddrTableWithTable_all(ip, community);
				if (addrTableMap != null) {
					for (int k = 0; k < listLength; k++) {
						Object obj = portIPMap.get((k + 1));
						if (obj != null) {
							for (int portIndex : vlanIFMap.keySet()) {
								String ifDesVlan = vlanIFMap.get(portIndex);
								if (("VLAN" + obj).equalsIgnoreCase(ifDesVlan.trim())) {
									Object portIP = addrTableMap.get(portIndex);
									if (portIP != null) {
										SwitchPortLevel3 switchPortLevel3 = portStateList.get(k);
										switchPortLevel3.setIpValue(portIP.toString());
									}
								}
							}
						}
					}
				}
			}
			return portStateList;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
			portIPMap.clear();
			vlanIFMap.clear();
		}
	}

	public List<OLTPort> getPortState_olt(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(2 * 1000);
		// String[] columnOIDs = { "1.3.6.1.2.1.2.2.1.3", "1.3.6.1.2.1.2.2.1.2",
		// "1.3.6.1.2.1.2.2.1.7", "1.3.6.1.2.1.2.2.1.8",
		// "1.3.6.1.2.1.2.2.1.6", "1.3.6.1.2.1.31.1.1.1.15" };
		String[] columnOIDs = { "1.3.6.1.2.1.2.2.1.3", "1.3.6.1.2.1.2.2.1.2", "1.3.6.1.2.1.2.2.1.7",
				"1.3.6.1.2.1.2.2.1.8", "1.3.6.1.2.1.2.2.1.6", "1.3.6.1.2.1.2.2.1.5" };
		List<TableEvent> tableEventList = null;
		List<OLTPort> portStateList = null;
		Map<Integer, Object> portIPMap = new HashMap<Integer, Object>();
		Map<Integer, String> vlanIFMap = new HashMap<Integer, String>();
		int i = 0;
		int j = 0;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				portStateList = new ArrayList<OLTPort>();
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					int ifType = variableBinding[0].getVariable().toInt();
					String ifDescr = variableBinding[1].getVariable().toString();
					int ifAdminStatus = variableBinding[2].getVariable().toInt();
					int ifOperStatus = variableBinding[3].getVariable().toInt();
					String ifPhysAddress = variableBinding[4].getVariable().toString();
					String ifHighSpeed = variableBinding[5].getVariable().toString();
					j++;
					if (ifType == 6) {
						i++;
						portIPMap.put(i,
								topologyHandler.getSingleNode(ip, community, "1.3.6.1.2.1.17.7.1.4.5.1.1." + i));

						OLTPort oltPort = new OLTPort();
						oltPort.setPortName(ifDescr);
						oltPort.setEnableStr("使能");
						oltPort.setConnect(ifOperStatus);
						oltPort.setMacValue(ifPhysAddress);
						if (ifOperStatus == 1) {
							int totalLength = ifHighSpeed.length();
							String ifSpeed = ifHighSpeed.substring(0, totalLength - 6);
							oltPort.setRate(ifSpeed + "Mb/s");
						} else {
							oltPort.setRate("---");
						}

						portStateList.add(oltPort);
					} else {
						if (ifDescr.startsWith("V")) {
							vlanIFMap.put(j, ifDescr);
						}
					}
				}
			}
			int listLength = 0;
			if (portStateList != null && (listLength = portStateList.size()) > 0) {
				Map<Integer, String> addrTableMap = topologyHandler.getAddrTableWithTable_all(ip, community);
				if (addrTableMap != null) {
					for (int k = 0; k < listLength; k++) {
						Object obj = portIPMap.get((k + 1));
						if (obj != null) {
							for (int portIndex : vlanIFMap.keySet()) {
								String ifDesVlan = vlanIFMap.get(portIndex);
								if (("VLAN" + obj).equalsIgnoreCase(ifDesVlan.trim())) {
									Object portIP = addrTableMap.get(portIndex);
									if (portIP != null) {
										OLTPort oltPort = portStateList.get(k);
										oltPort.setPortIP(portIP.toString());
									}
								}
							}
						}
					}
				}
			}
			return portStateList;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
			portIPMap.clear();
			vlanIFMap.clear();
		}
	}

	private void clear() {
		portMap.clear();
		portMap = null;
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

	public void setTopologyHandler(TopologyHandler topologyHandler) {
		this.topologyHandler = topologyHandler;
	}

}
