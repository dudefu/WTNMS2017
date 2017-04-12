package com.jhw.adm.comclient.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.DataBufferBuilder;
import com.jhw.adm.comclient.data.OID;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.comclient.service.topology.TopologyHandler;
import com.jhw.adm.comclient.system.AutoIncreaseConstants;
import com.jhw.adm.comclient.system.IDiagnose;
import com.jhw.adm.comclient.ui.DiagnoseView;
import com.jhw.adm.server.entity.ports.LLDPConfig;
import com.jhw.adm.server.entity.ports.LLDPInofEntity;
import com.jhw.adm.server.entity.ports.LLDPPortConfig;
import com.jhw.adm.server.entity.ports.SwitchPortEntity;

/**
 * LLDP
 * 
 * @author xiongbo
 * 
 */
public class LldpHandler extends BaseHandler {
	private static Logger log = Logger.getLogger(LldpHandler.class);
	private AbstractSnmp snmpV2;
	private PortHandler portHandler;
	private DataBufferBuilder dataBufferBuilder;
	//
	private LacpHandler lacpHandler;
	private Map<String, Integer> lacpCountMap = new HashMap<String, Integer>();
	private final int[] lldpWorkState = { 1, 2, 3, 4 };
	private Pattern portPattern = Pattern
			.compile("\\s*(\\d+)\\s*\\S*\\s*\\d\\s*\\/\\s*\\d+\\s*");
	private DiagnoseView diagnoseView;
	private TopologyHandler topologyHandler;

	/**
	 * @deprecated
	 * @param ip
	 * @param lldpConfig
	 * @return
	 */
	public boolean configParameter(String ip, LLDPConfig lldpConfig) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		Map<String, Object> oidMap = new HashMap<String, Object>();
		PDU response = null;
		try {
			oidMap.put(OID.LLDPINTERVAL, lldpConfig.getTx_Interval());
			oidMap.put(OID.LLDPHOLD, lldpConfig.getTx_Hold());
			oidMap.put(OID.LLDPTXDELAY, lldpConfig.getTx_Delay());
			oidMap.put(OID.LLDPREINITDELAY, lldpConfig.getTx_Reinit());
			response = snmpV2.snmpSet(oidMap);

			return checkResponse(response);
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
			oidMap.clear();
		}
	}

	/**
	 * @deprecated
	 * @param ip
	 * @param lldpPortList
	 * @return
	 */
	public boolean configPort(String ip, List<SwitchPortEntity> lldpPortList) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		int portNum = portHandler.getPortNum(ip);
		if (portNum == 0) {
			return false;
		}
		PDU response = null;
		try {
			byte[] portMember = new byte[portNum];
			byte[] stateMember = new byte[portNum];
			for (SwitchPortEntity lldpPort : lldpPortList) {
				int portId = lldpPort.getPortNO();
				// int lldpState = lldpPort.getLldpWork();
				int lldpState = 0;
				portMember[portId - 1] = 1;
				// stateMember[portId - 1] = (byte) (lldpState + 1);
				stateMember[portId - 1] = (byte) (lldpState);
			}
			byte[] buf = dataBufferBuilder.lldpConfig(portMember, stateMember);
			response = snmpV2.snmpSet(OID.LLDP_BATCH_OPERATE, buf);
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

	public boolean configLLDP(String ip, LLDPConfig lldpConfig) {
		// 由于LLDPINTERVAL和LLDPTXDELAY互相依赖，
		// 当第一次下发成功时，就不下第二次了，以第一次的结果为准
		// 当第一次失败时，下发第二次，以第二次的结果为准
		boolean result1 = configLLDPInfo(ip, lldpConfig);
		boolean result2 = true;
		boolean result = true;
		if (!result1) {
			result2 = configLLDPInfo(ip, lldpConfig);
			result = result2;
		} else {
			result = result1;
		}
		if (!result) {
			return result;
		}

		// 配置LLDP端口
		result = configLLDPPort(ip, lldpConfig);

		return result;
	}

	public boolean configLLDPInfo(String ip, LLDPConfig lldpConfig) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		Map<String, Object> oidMap = new HashMap<String, Object>();
		PDU response = null;
		try {
			oidMap.put(OID.LLDPINTERVAL, lldpConfig.getTx_Interval());
			oidMap.put(OID.LLDPHOLD, lldpConfig.getTx_Hold());
			oidMap.put(OID.LLDPTXDELAY, lldpConfig.getTx_Delay());
			oidMap.put(OID.LLDPREINITDELAY, lldpConfig.getTx_Reinit());
			response = snmpV2.snmpSet(oidMap);
			return checkResponse(response);

		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
			oidMap.clear();
		}
	}

	public boolean configLLDPPort(String ip, LLDPConfig lldpConfig) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		int portNum = portHandler.getPortNum(ip);
		if (portNum == 0) {
			return false;
		}
		Map<String, Object> oidMap = new HashMap<String, Object>();
		PDU response = null;
		try {
			byte[] portMember = new byte[portNum];
			byte[] stateMember = new byte[portNum];
			// Set<SwitchPortEntity> lldpPorts = lldpConfig.getSwitchNode()
			// .getPorts();
			Set<LLDPPortConfig> lldpPorts = lldpConfig.getLldpPortConfigs();
			// 从客户端得到的值为disable=0,enable=1,tx=2 发送,rx=3 接受
			// 当下发到设备上时，所有的值加1
			for (LLDPPortConfig lldpPort : lldpPorts) {
				int portId = lldpPort.getPortNum();
				int lldpState = lldpPort.getLldpWork();
				portMember[portId - 1] = 1;
				stateMember[portId - 1] = (byte) (lldpState + 1);
			}
			byte[] buf = dataBufferBuilder.lldpConfig(portMember, stateMember);
			response = snmpV2.snmpSet(OID.LLDP_BATCH_OPERATE, buf);
			return checkResponse(response);

		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
			oidMap.clear();
		}
	}

	public LLDPConfig getLldpParameter(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		Vector<String> oids = new Vector<String>();
		oids.add(OID.LLDPINTERVAL);
		oids.add(OID.LLDPHOLD);
		oids.add(OID.LLDPTXDELAY);
		oids.add(OID.LLDPREINITDELAY);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(oids);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				LLDPConfig lldpConfig = new LLDPConfig();
				lldpConfig.setTx_Interval(responseVar.elementAt(0)
						.getVariable().toInt());
				lldpConfig.setTx_Hold(responseVar.elementAt(1).getVariable()
						.toInt());
				lldpConfig.setTx_Delay(responseVar.elementAt(2).getVariable()
						.toInt());
				lldpConfig.setTx_Reinit(responseVar.elementAt(3).getVariable()
						.toInt());
				return lldpConfig;
			}
			return null;
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

	// When Topology
	public Map<Integer, Integer> getLldpPortCfg(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		int portn = portHandler.getPortNum(ip);
		if (portn == 0) {
			return null;
		}
		snmpV2.setTimeout(4 * 1000);
		Vector<String> oids = new Vector<String>();
		oids.add(OID.LLDPPORT);
		oids.add(OID.LLDPPORTSTATE);
		PDU response = null;
		try {
			response = snmpV2.snmpGetBulk(oids, 0, portn);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				int vbsSize = responseVar.size();
				Map<Integer, Integer> tempMap = new HashMap<Integer, Integer>(
						portn);
				for (int i = 0; i < vbsSize; i++) {
					if (i % 2 == 0) {
						int portNo = responseVar.elementAt(i + 0).getVariable()
								.toInt();
						int lldpWork = 0;
						if (ENABLED.equalsIgnoreCase(responseVar.elementAt(
								i + 1).getVariable().toString())) {
							lldpWork = 1;
						} else if (DISABLED.equalsIgnoreCase(responseVar
								.elementAt(i + 1).getVariable().toString())) {
							lldpWork = 0;
						} else if ("Tx".equalsIgnoreCase(responseVar.elementAt(
								i + 1).getVariable().toString())) {// Only
							// Send
							lldpWork = 2;
						} else {
							lldpWork = 3;
						}
						tempMap.put(portNo, lldpWork);
						// log.info("LLDP port state: " + portNo + " " +
						// lldpWork);
					}
				}
				return tempMap;
			}
			return null;

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

	// The MAP also used to Topology discovery
	public Set<LLDPInofEntity> getLldpInfo(String ip,
			Map<String, String> layer3Ips) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(OID.LLDPINFONUM);
			int lldpInfoNum = 0;
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				lldpInfoNum = responseVar.elementAt(0).getVariable().toInt();
			}
			if (lldpInfoNum == 0) {
				return null;
			}
			Map<String, String[]> lacpGroup = lacpHandler.getLacpGroup(ip);
			// No significance
			// if (lacpGroup != null) {
			lacpCountMap.clear();
			// }

			snmpV2.setTimeout(10 * 1000);
			// When Time Out,may adjust the 'pagesize'
			int pagesize = 10;
			int mod = lldpInfoNum % pagesize;
			int pages = lldpInfoNum / pagesize;
			pages = mod == 0 ? pages : pages + 1;
			Vector<String> oids = new Vector<String>();
			Set<LLDPInofEntity> lldpSet = new HashSet<LLDPInofEntity>();
			for (int i = 0; i < pages; i++) {
				int step = i * pagesize;
				oids.add(OID.LLDP_LOCALPORT + "." + step);
				oids.add(OID.LLDP_CHASSISID + "." + step);
				oids.add(OID.LLDPPORTID + "." + step);
				oids.add(OID.LLDP_PORTDESCRIPTION + "." + step);
				oids.add(OID.LLDP_SYSTEMNAME + "." + step);
				oids.add(OID.LLDP_SYSTEMDESCRIPTION + "." + step);
				oids.add(OID.LLDP_SYSTEMCAPABILITIES + "." + step);
				oids.add(OID.LLDP_MANAGEMENTADDRESS + "." + step);
				if (lldpInfoNum - (i * pagesize) > mod) {
					response = snmpV2.snmpGetBulk(oids, 0, pagesize);
				} else {
					response = snmpV2.snmpGetBulk(oids, 0, mod);
				}
				responseVar = checkResponseVar(response);
				if (responseVar != null) {
					int vbsSize = responseVar.size();
					for (int j = 0; j < vbsSize; j++) {
						if (j % 3 == 0) {
							LLDPInofEntity lLDPInofEntity = new LLDPInofEntity();
							lLDPInofEntity.setLocalIP(ip);
							lLDPInofEntity.setLocalPortNo(getLocalPortForLacp(
									responseVar.elementAt(j + 0).getVariable()
											.toString(), lacpGroup));
							lLDPInofEntity
									.setLocalDeviceType(com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2);
							//
							String remotePort = responseVar.elementAt(j + 1)
									.getVariable().toString();
							String remoteIp = substrIp(responseVar.elementAt(
									j + 2).getVariable().toString());
							// if (remotePort.startsWith("Fas")
							// || remotePort.startsWith("Gig")) {
							if (remotePort.indexOf("/") != -1) {
								int position = remotePort.indexOf("/");
								int peerSlot = Integer.parseInt(remotePort
										.substring(position - 1, position)
										.trim());
								int peerPort = Integer.parseInt(remotePort
										.substring(position + 1).trim());

								lLDPInofEntity.setRemoteSlot(peerSlot);
								lLDPInofEntity.setRemotePortNo(peerPort);
								lLDPInofEntity
										.setRemoteDeviceType(com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER3);

								for (String ips : layer3Ips.keySet()) {
									if (ips.indexOf(remoteIp) != -1) {
										lLDPInofEntity.setRemoteIP(layer3Ips
												.get(ips));
									}
								}
								if (lLDPInofEntity.getRemoteIP() == null) {
									lLDPInofEntity
											.setRemoteIP(getRemoteIp(remoteIp));
									log.info("remotePort: " + remotePort);
									Matcher matcher = portPattern
											.matcher(remotePort);
									if (matcher.find()) {
										lLDPInofEntity.setRemotePortNo(Integer
												.parseInt(matcher.group(1)));
									}
								}
							} else {
								lLDPInofEntity.setRemotePortNo(Integer
										.parseInt(remotePort));
								log.info("remoteIp: " + remoteIp);
								remoteIp = getRemoteIp(remoteIp);
								lLDPInofEntity.setRemoteIP(remoteIp.trim());
								lLDPInofEntity
										.setRemoteDeviceType(com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2);
							}

							lldpSet.add(lLDPInofEntity);
							log.info("2layer LLDP: "
									+ lLDPInofEntity.getLocalIP() + " "
									+ lLDPInofEntity.getLocalPortNo() + " - "
									+ lLDPInofEntity.getRemoteIP() + " "
									+ lLDPInofEntity.getRemoteSlot() + " "
									+ +lLDPInofEntity.getRemotePortNo());
						}
					}
				}
				oids.clear();
			}
			// Map<Integer, Set<LLDPInofEntity>> map = new HashMap<Integer,
			// Set<LLDPInofEntity>>(
			// 1);
			// map.put(lldpInfoNum, lldpSet);
			return lldpSet;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
		}

	}

	public Set<LLDPInofEntity> getLldpInfoTable(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		String[] columnOIDs = { OID.LLDP_LOCALPORT, OID.LLDPPORTID,
				OID.LLDP_MANAGEMENTADDRESS };
		// int columnOIDLength = columnOIDs.length;
		List<TableEvent> tableEventList = null;
		Set<LLDPInofEntity> lldpSet = null;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				Map<String, String[]> lacpGroup = lacpHandler.getLacpGroup(ip);
				lacpCountMap.clear();
				lldpSet = new HashSet<LLDPInofEntity>();
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					// int vbLength = variableBinding.length;
					// for (int i = 0; i < vbLength; i++) {
					// if (i % columnOIDLength == 0) {
					//
					if ((variableBinding[2] == null || variableBinding[1] == null)
							|| (variableBinding[2].getVariable() == null || variableBinding[1]
									.getVariable() == null)) {
						continue;
					}
					//
					int localPortNo = getLocalPortForLacp(variableBinding[0]
							.getVariable().toString(), lacpGroup);
					String remotePort = variableBinding[1].getVariable()
							.toString().trim();
					if (remotePort.length() == 0) {
						continue;
					}
					String remoteIp = substrIp(variableBinding[2].getVariable()
							.toString());
					LLDPInofEntity lldpInofEntity = new LLDPInofEntity();
					lldpInofEntity.setLocalIP(ip);
					lldpInofEntity.setLocalPortNo(localPortNo);
					Object ifTypeObj = topologyHandler.getSingleNode(ip,
							Constants.COMMUNITY_PRIVATE,
							"1.3.6.1.4.1.44405.5.2.11.1.2.1.6." + localPortNo);
					lldpInofEntity
							.setLocalPortType(com.jhw.adm.server.entity.util.Constants.TX);
					if (ifTypeObj != null) {
						String ifType = ifTypeObj.toString();
						if ("fiber".equalsIgnoreCase(ifType.trim())) {
							lldpInofEntity
									.setLocalPortType(com.jhw.adm.server.entity.util.Constants.FX);
						}
					}
					lldpInofEntity
							.setLocalDeviceType(com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2);

					if (remotePort.indexOf("/") != -1) {
						int position = remotePort.indexOf("/");
						int peerSlot = Integer.parseInt(remotePort.substring(
								position - 1, position).trim());
						int peerPort = Integer.parseInt(remotePort.substring(
								position + 1).trim());

						lldpInofEntity.setRemoteSlot(peerSlot);
						lldpInofEntity.setRemotePortNo(peerPort);
						lldpInofEntity
								.setRemoteDeviceType(com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER3);
						remoteIp = getRemoteIp(remoteIp);
						lldpInofEntity.setRemoteIP(remoteIp);
						log.info("remotePort: " + remotePort);
						Matcher matcher = portPattern.matcher(remotePort);
						if (matcher.find()) {
							lldpInofEntity.setRemoteSlot(0);
							lldpInofEntity.setRemotePortNo(Integer
									.parseInt(matcher.group(1)));
						}
					} else {
						log.info("remotePort：" + remotePort);
						try {
							lldpInofEntity.setRemotePortNo(Integer
									.parseInt(remotePort));
						} catch (Exception e) {
							log.error(e);
							continue;
						}
						log.info("remoteIp: " + remoteIp);
						remoteIp = getRemoteIp(remoteIp);
						lldpInofEntity.setRemoteIP(remoteIp.trim());
						lldpInofEntity
								.setRemoteDeviceType(com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2);
					}
					if (remotePort.startsWith("E")
							|| remotePort.startsWith("e")) {
						lldpInofEntity
								.setRemotePortType(com.jhw.adm.server.entity.util.Constants.FX);
					} else {
						lldpInofEntity
								.setRemotePortType(com.jhw.adm.server.entity.util.Constants.TX);
						if (lldpInofEntity.getLocalPortType() == com.jhw.adm.server.entity.util.Constants.FX) {
							lldpInofEntity
									.setRemotePortType(com.jhw.adm.server.entity.util.Constants.FX);
						}
					}

					lldpSet.add(lldpInofEntity);

					// log.info("2layer LLDP: "
					// + lldpInofEntity.getLocalIP() + " "
					// + lldpInofEntity.getLocalPortType() + " - "
					// + lldpInofEntity.getRemoteIP() + " "
					// // + lldpInofEntity.getRemoteSlot() + " "
					// + +lldpInofEntity.getRemotePortType());
					// }
					// }
				}

				// For Diagnose
				final List<TableEvent> tableEventListDiagnose = tableEventList;
				final Set<LLDPInofEntity> lldpSetDiagnose = lldpSet;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						IDiagnose iDiagnose = diagnoseView
								.getDiagnoseReference(AutoIncreaseConstants.LAYER2LLDP);
						if (iDiagnose != null) {
							StringBuilder ssb = new StringBuilder();
							StringBuilder tsb = new StringBuilder();
							for (TableEvent tableEvent : tableEventListDiagnose) {
								VariableBinding[] variableBinding = tableEvent
										.getColumns();
								if (variableBinding == null) {
									continue;
								}
								ssb
										.append(variableBinding[0])
										.append(
												System
														.getProperty("line.separator"))
										.append(variableBinding[1])
										.append(
												System
														.getProperty("line.separator"))
										.append(variableBinding[2])
										.append(
												System
														.getProperty("line.separator"));
							}
							ssb.append("******").append(
									System.getProperty("line.separator"));
							tableEventListDiagnose.clear();
							for (LLDPInofEntity lldp : lldpSetDiagnose) {
								tsb
										.append("LocalIP：")
										.append(lldp.getLocalIP())
										.append(
												System
														.getProperty("line.separator"))
										.append("LocalDeviceType：")
										.append(lldp.getLocalDeviceType())
										.append(
												System
														.getProperty("line.separator"))
										.append("LocalSlot：")
										.append(lldp.getLocalSlot())
										.append(
												System
														.getProperty("line.separator"))
										.append("LocalPortNo：")
										.append(lldp.getLocalPortNo())
										.append(
												System
														.getProperty("line.separator"))
										.append("LocalPortType：")
										.append(lldp.getLocalPortType())
										.append(
												System
														.getProperty("line.separator"))
										.append("RemoteIP：")
										.append(lldp.getRemoteIP())
										.append(
												System
														.getProperty("line.separator"))
										.append("RemoteDeviceType：")
										.append(lldp.getRemoteDeviceType())
										.append(
												System
														.getProperty("line.separator"))
										.append("RemoteSlot：")
										.append(lldp.getRemoteSlot())
										.append(
												System
														.getProperty("line.separator"))
										.append("RemotePortNo：")
										.append(lldp.getRemotePortNo())
										.append(
												System
														.getProperty("line.separator"))
										.append("RemotePortType：")
										.append(lldp.getRemotePortType())
										.append(
												System
														.getProperty("line.separator"));
							}
							tsb.append("******").append(
									System.getProperty("line.separator"));
							iDiagnose.receiveInfo(ssb.toString(), tsb
									.toString());
						}
					}
				});
				//
			}
			return lldpSet;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return lldpSet;
		} finally {
			// if (tableEventList != null) {
			// tableEventList.clear();
			// }
		}

	}

	/**
	 * For Diagnose
	 * 
	 * @param ip
	 * @return
	 */
	// public List<LLDPDiagnose> getLldpInfoForDiagnose(String ip) {
	// snmpV2.setAddress(ip, Constants.SNMP_PORT);
	// snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
	// snmpV2.setTimeout(2 * 1000);
	// String[] columnOIDs = { OID.LLDP_LOCALPORT, OID.LLDP_CHASSISID,
	// OID.LLDPPORTID, OID.LLDP_PORTDESCRIPTION, OID.LLDP_SYSTEMNAME,
	// OID.LLDP_SYSTEMDESCRIPTION, OID.LLDP_MANAGEMENTADDRESS };
	// // int columnOIDLength = columnOIDs.length;
	// List<TableEvent> tableEventList = null;
	// List<LLDPDiagnose> lldpList = null;
	// try {
	// tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
	// if (tableEventList != null) {
	// lldpList = new ArrayList<LLDPDiagnose>();
	// for (TableEvent tableEvent : tableEventList) {
	// VariableBinding[] variableBinding = tableEvent.getColumns();
	// if (variableBinding == null) {
	// continue;
	// }
	// // int vbLength = variableBinding.length;
	// // for (int i = 0; i < vbLength; i++) {
	// // if (i % columnOIDLength == 0) {
	//
	// String localPortDesc = substrIp(variableBinding[0]
	// .getVariable().toString());
	// String peerMAC = substrIp(variableBinding[1].getVariable()
	// .toString());
	// String peerPort = substrIp(variableBinding[2].getVariable()
	// .toString());
	// String peerPortDesc = substrIp(variableBinding[3]
	// .getVariable().toString());
	// String peerSystemName = substrIp(variableBinding[4]
	// .getVariable().toString());
	// String peerSystemDesc = substrIp(variableBinding[5]
	// .getVariable().toString());
	// String peerIPDesc = substrIp(variableBinding[6]
	// .getVariable().toString());
	// LLDPDiagnose lldpDiagnose = new LLDPDiagnose();
	// lldpDiagnose.setLocalPortDesc(localPortDesc);
	// lldpDiagnose.setPeerMAC(peerMAC);
	// lldpDiagnose.setPeerPort(peerPort);
	// lldpDiagnose.setPeerPortDesc(peerPortDesc);
	// lldpDiagnose.setPeerSystemName(peerSystemName);
	// lldpDiagnose.setPeerSystemDesc(peerSystemDesc);
	// lldpDiagnose.setPeerIPDesc(peerIPDesc);
	// lldpList.add(lldpDiagnose);
	// // }
	// // }
	// }
	// }
	// return lldpList;
	// } catch (RuntimeException e) {
	// log.error(getTraceMessage(e));
	// return lldpList;
	// } finally {
	// if (tableEventList != null) {
	// tableEventList.clear();
	// }
	// }
	//
	// }
	private String getRemoteIp(String remoteIp) {
		int position = remoteIp.indexOf("(");
		if (position != -1) {
			remoteIp = remoteIp.substring(0, position - 1);
		}
		return remoteIp;
	}

	private int getLocalPortForLacp(String localPort,
			Map<String, String[]> lacpGroup) {
		int port = 0;
		if (localPort.trim().startsWith("LLAG")) {
			String llag = localPort.replace(" ", "");
			String[] lacpPortMember = lacpGroup.get(llag);
			if (lacpPortMember != null) {
				Object obj = lacpCountMap.get(llag);
				if (obj != null) {
					int count = (Integer) obj;
					count++;
					lacpCountMap.put(llag, count);
					port = Integer.parseInt(lacpPortMember[count]);
				} else {
					lacpCountMap.put(llag, 0);
					port = Integer.parseInt(lacpPortMember[0]);
				}
			}// else {
			// port = 0;
			// }
		} else {
			port = Integer.parseInt(subPort(localPort));
		}
		// port = Integer.parseInt(subPort(localPort));
		return port;
	}

	private String substrIp(String ip) {
		if (ip.length() <= 15) {
			return ip.trim();
		}
		int position = ip.indexOf("(");
		return (ip.substring(0, position)).trim();
	}

	private String subPort(String localPort) {
		localPort = localPort.trim();
		localPort = localPort.substring(5, 6);
		return localPort;
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

	public LacpHandler getLacpHandler() {
		return lacpHandler;
	}

	public void setLacpHandler(LacpHandler lacpHandler) {
		this.lacpHandler = lacpHandler;
	}

	public DataBufferBuilder getDataBufferBuilder() {
		return dataBufferBuilder;
	}

	public void setDataBufferBuilder(DataBufferBuilder dataBufferBuilder) {
		this.dataBufferBuilder = dataBufferBuilder;
	}

	public void setDiagnoseView(DiagnoseView diagnoseView) {
		this.diagnoseView = diagnoseView;
	}

	public void setTopologyHandler(TopologyHandler topologyHandler) {
		this.topologyHandler = topologyHandler;
	}

}
