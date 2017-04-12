package com.jhw.adm.comclient.service.topology.epon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.comclient.service.BaseHandler;
import com.jhw.adm.comclient.service.topology.Dot1dStpPortTable;
import com.jhw.adm.comclient.service.topology.TwoLayerMacEntry;
import com.jhw.adm.comclient.system.AutoIncreaseConstants;
import com.jhw.adm.comclient.system.Configuration;
import com.jhw.adm.comclient.system.IDiagnose;
import com.jhw.adm.comclient.system.OIDConfig;
import com.jhw.adm.comclient.ui.DiagnoseView;
import com.jhw.adm.server.entity.epon.OLTBaseInfo;
import com.jhw.adm.server.entity.epon.OLTPort;
import com.jhw.adm.server.entity.epon.ONUEntity;
import com.jhw.adm.server.entity.ports.LLDPInofEntity;

/**
 * 
 * @author xiongbo
 * 
 */
public class EponHandler extends BaseHandler {
	private AbstractSnmp snmpV2;
	// For two or three layer
	private final String ipForwarding = "1.3.6.1.2.1.4.1.0";
	private final String sysObjectID = "1.3.6.1.2.1.1.2.0";

	public final String bdcom = "1.3.6.1.4.1.3320";
	// For three layer
	private final String ipNetToMediaIfIndex = "1.3.6.1.2.1.4.22.1.1";
	private final String ipNetToMediaPhysAddress = "1.3.6.1.2.1.4.22.1.2";
	private final String ipNetToMediaNetAddress = "1.3.6.1.2.1.4.22.1.3";
	private final String check_ipNetToMediaType = "1.3.6.1.2.1.4.22.1.4";
	// For three layer
	private final String ifNumber = "1.3.6.1.2.1.2.1.0";
	private final String ifIndex = "1.3.6.1.2.1.2.2.1.1";
	private final String ifDescr = "1.3.6.1.2.1.2.2.1.2";
	private final String ifType = "1.3.6.1.2.1.2.2.1.3";
	private final String ifPhysAddress = "1.3.6.1.2.1.2.2.1.6";
	private final String ifOperStatus = "1.3.6.1.2.1.2.2.1.8";
	// For two layer
	private final String macNum = "1.3.6.1.4.1.16001.2.8.1";
	private final String macAddr = "1.3.6.1.4.1.16001.2.8.2.1.2";
	private final String portIdx = "1.3.6.1.4.1.16001.2.8.2.1.3";
	// Epon
	private final String llidEponIfDiid = "1.3.6.1.4.1.3320.101.11.1.1.1";
	private final String llidSequenceNo = "1.3.6.1.4.1.3320.101.11.1.1.2";
	private final String onuMacAddressIndex = "1.3.6.1.4.1.3320.101.11.1.1.3";
	private final String llidOnuBindStatus = "1.3.6.1.4.1.3320.101.11.1.1.6";
	private final String llidOnuBindDistance = "1.3.6.1.4.1.3320.101.11.1.1.7";

	// TODO
	private final String dot1dBaseBridgeAddress = "1.3.6.1.2.1.17.1.1.0";

	// OLT Base Info
	private final String sysDescr = "1.3.6.1.2.1.1.1.0";
	private final String sysName = "1.3.6.1.2.1.1.5.0";

	// STP
	private final String dot1dStpPort = "1.3.6.1.2.1.17.2.15.1.1";
	private final String dot1dStpPortState = "1.3.6.1.2.1.17.2.15.1.3";
	private final String dot1dStpPortPathCost = "1.3.6.1.2.1.17.2.15.1.5";
	// private final String dot1dStpPortDesignatedRoot =
	// "1.3.6.1.2.1.17.2.15.1.6";
	private final String dot1dStpPortDesignatedBridge = "1.3.6.1.2.1.17.2.15.1.8";

	// private final String dot1dStpPortDesignatedPort =
	// "1.3.6.1.2.1.17.2.15.1.9";

	private DiagnoseView diagnoseView;

	/**
	 * 3-Router or Three layer switch 31 or 21-OLT 2-Two layer switch 1-Other
	 * device
	 * 
	 * @param ip
	 * @param community
	 * @return
	 */
	public int getDeviceType(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(sysObjectID);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar == null) {
				return 1;
			}
			Object obj = responseVar.elementAt(0).getVariable();
			if (obj != null) {
				String sObjectID = obj.toString().trim();
				// Check MOXA
				// if (sObjectID.indexOf(Configuration.moxa_objectid) != -1) {
				// return 2;
				// }
				String moxa_objectid = Configuration.moxa_objectid;
				String[] moxa_objectids = moxa_objectid.split(",");
				boolean flag = false;
				for (String moxaObjectid : moxa_objectids) {
					if (sObjectID.indexOf(moxaObjectid) != -1) {
						flag = true;
						break;
					}
				}
				if (flag) {
					return 2;
				}
				// Check KYLAND
				// if (sObjectID.indexOf(Configuration.kyland_objectid) != -1) {
				// return 2;
				// }
				String kyland_objectid = Configuration.kyland_objectid;
				String[] kyland_objectids = kyland_objectid.split(",");
				flag = false;
				for (String kylandObjectid : kyland_objectids) {
					if (sObjectID.indexOf(kylandObjectid) != -1) {
						flag = true;
						break;
					}
				}
				if (flag) {
					return 2;
				}
				// Check JHW device
				// if (sObjectID.indexOf(Configuration.jhw_objectid) != -1) {
				// return 2;
				// }
				String jhw_objectid = Configuration.jhw_objectid;
				String[] jhw_objectids = jhw_objectid.split(",");
				flag = false;
				for (String jhwObjectid : jhw_objectids) {
					if (sObjectID.indexOf(jhwObjectid) != -1) {
						flag = true;
						break;
					}
				}
				if (flag) {
					return 2;
				}

				// Check Layer3
				String layer3Type = Configuration.layer3_type;
				String[] layer3Types = layer3Type.split(",");
				flag = false;
				for (String l3Type : layer3Types) {
					if (sObjectID.indexOf(l3Type) != -1) {
						flag = true;
						break;
					}
				}
				if (flag) {
					return 3;
				}
				// Check OLT
				String oltType = Configuration.olt_type;
				String[] oltTypes = oltType.split(",");
				flag = false;
				for (String otype : oltTypes) {
					if (sObjectID.indexOf(otype) != -1) {
						flag = true;
						break;
					}
				}
				if (flag) {
					return 21;
				}

			} else {

			}
			return 1;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return 0;
		} finally {
			if (response != null) {
				response.clear();
			}
			// oids.clear();
		}
	}

	/**
	 * Or Three Layer Mac
	 * 
	 * Notice:Flag for Three Layer Discovery
	 */
	public String getLayer3Mac(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(dot1dBaseBridgeAddress);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			String mac = null;
			if (responseVar != null) {
				mac = responseVar.elementAt(0).getVariable().toString();
			}

			return mac;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	public String getOnuMac(String ip, String community, String portDesc,
			int sequeNo) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 1200);

		int ifDi = this.getIndexByPortDesc(ip, community, portDesc);
		if (ifDi == 0) {
			return null;
		}

		Vector<String> oids = new Vector<String>();
		// oids.add(llidEponIfDiid);
		// oids.add(llidSequenceNo);
		// oids.add(onuMacAddressIndex);
		oids.add(OIDConfig.getOIDList().getLLIDEPONIFDIID());
		oids.add(OIDConfig.getOIDList().getLLIDSEQUENCENO());
		oids.add(OIDConfig.getOIDList().getONUMACADDRESSINDEX());
		PDU response = null;
		String onuMac = null;
		try {
			while (true) {
				response = snmpV2.snmpGetNext(oids);
				Vector<VariableBinding> responseVar = checkResponseVar(response);
				if (responseVar != null) {
					int ifDiid = responseVar.elementAt(0).getVariable().toInt();

					String check = responseVar.elementAt(0).getOid().toString()
							.trim();
					// if (check.startsWith(llidSequenceNo)) {
					// break;
					// }
					if (check.startsWith(OIDConfig.getOIDList()
							.getLLIDSEQUENCENO())) {
						break;
					}

					int sequenceNo = responseVar.elementAt(1).getVariable()
							.toInt();
					String mac = responseVar.elementAt(2).getVariable()
							.toString();
					if (ifDiid == ifDi && sequenceNo == sequeNo) {
						onuMac = mac;
						break;
					}

					oids.clear();
					oids.add(responseVar.elementAt(0).getOid().toString()
							.trim());
					oids.add(responseVar.elementAt(1).getOid().toString()
							.trim());
					oids.add(responseVar.elementAt(2).getOid().toString()
							.trim());

				} else {
					break;
				}
			}

			return onuMac;
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

	public OLTBaseInfo getOltBaseInfo(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		OLTBaseInfo oltBaseInfo = null;
		try {
			response = snmpV2.snmpGet(sysDescr);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			String sysNam = null;
			if (responseVar != null) {
				sysNam = responseVar.elementAt(0).getVariable().toString();
				String[] desc = sysNam.split("\n");
				if (desc != null) {
					String[] temp = desc[1].split(" ");
					if (temp != null) {
						oltBaseInfo = new OLTBaseInfo();
						oltBaseInfo.setDeviceName(temp[0]);
					}
				}
			}
			return oltBaseInfo;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	public String getPortDescByIfDiid(String ip, String community,
			int ifDiidAndSequenceNo) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 1200);
		String ifDescr_OID = "1.3.6.1.2.1.2.2.1.2" + "." + ifDiidAndSequenceNo;
		PDU response = null;
		try {
			response = snmpV2.snmpGet(ifDescr_OID);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			String port = null;
			if (responseVar != null) {
				port = responseVar.elementAt(0).getVariable().toString();
			}
			return port;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
		}

	}

	public int getIndexByPortDesc(String ip, String community, String portDesc) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 1200);
		Vector<String> oids = new Vector<String>();
		oids.add(ifIndex);
		oids.add(ifDescr);

		int position = portDesc.indexOf(":");
		portDesc = portDesc.substring(0, position);
		PDU response = null;
		int portIndex = 0;
		try {
			while (true) {
				response = snmpV2.snmpGetNext(oids);
				Vector<VariableBinding> responseVar = checkResponseVar(response);
				if (responseVar != null) {
					int index = responseVar.elementAt(0).getVariable().toInt();
					String check = responseVar.elementAt(0).getOid().toString()
							.trim();
					if (check.startsWith(ifDescr)) {
						break;
					}
					String desc = responseVar.elementAt(1).getVariable()
							.toString();
					if (portDesc.equalsIgnoreCase(desc)) {
						portIndex = index;
						break;
					}
					oids.clear();
					oids.add(responseVar.elementAt(0).getOid().toString()
							.trim());
					oids.add(responseVar.elementAt(1).getOid().toString()
							.trim());
				} else {
					break;
				}
			}
			return portIndex;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return 0;
		} finally {
			if (response != null) {
				response.clear();
			}
			oids.clear();
		}

	}

	public HashSet<ONUEntity> getOnu(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(2 * 1000);

		// String[] columnOIDs = { llidEponIfDiid, llidSequenceNo,
		// onuMacAddressIndex, llidOnuBindStatus, llidOnuBindDistance };
		String[] columnOIDs = { OIDConfig.getOIDList().getLLIDEPONIFDIID(),
				OIDConfig.getOIDList().getLLIDSEQUENCENO(),
				OIDConfig.getOIDList().getONUMACADDRESSINDEX(),
				OIDConfig.getOIDList().getLLIDONUBINDSTATUS(),
				OIDConfig.getOIDList().getLLIDONUBINDDISTANCE() };
		int columnOIDLength = columnOIDs.length;
		List<TableEvent> tableEventList = null;
		HashSet<ONUEntity> onuEntitySet = null;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				onuEntitySet = new HashSet<ONUEntity>();
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					int vbLength = variableBinding.length;
					for (int i = 0; i < vbLength; i++) {
						if (i % columnOIDLength == 0) {
							int ifDiid = variableBinding[i + 0].getVariable()
									.toInt();
							int sequenceNo = variableBinding[i + 1]
									.getVariable().toInt();
							String mac = variableBinding[i + 2].getVariable()
									.toString();
							int status = variableBinding[i + 3].getVariable()
									.toInt();
							int distance = variableBinding[i + 4].getVariable()
									.toInt();
							ONUEntity onuEntity = new ONUEntity();
							Set<LLDPInofEntity> lldpinfos = new HashSet<LLDPInofEntity>();
							String ifDescr = getPortDescByIfDiid(ip, community,
									ifDiid);
							log.info(ifDescr + " " + ifDiid + " " + sequenceNo
									+ " " + status);
							// ifDescr = "EPON0/1";
							if (ifDescr != null) {
								ifDescr = ifDescr.trim();
								int position = ifDescr.indexOf("/");
								int additional = ifDescr.indexOf(":");
								if (position != -1) {
									int slot = Integer.parseInt(ifDescr
											.substring(position - 1, position)
											.trim());
									int port = 0;
									if (additional != -1) {
										port = Integer.parseInt(ifDescr
												.substring(position + 1,
														additional).trim());
									} else {
										port = Integer
												.parseInt(ifDescr.substring(
														position + 1).trim());
									}

									LLDPInofEntity lldpinfo = new LLDPInofEntity();
									lldpinfo.setRemoteDeviceType(com.jhw.adm.server.entity.util.Constants.DEV_OLT);
									lldpinfo.setRemoteIP(ip);
									lldpinfo.setRemoteSlot(slot);
									lldpinfo.setRemotePortNo(port);
									lldpinfo.setRemotePortType(com.jhw.adm.server.entity.util.Constants.PX);
									lldpinfo.setLocalDeviceType(com.jhw.adm.server.entity.util.Constants.DEV_ONU);
									lldpinfo.setLocalPortType(com.jhw.adm.server.entity.util.Constants.PX);
									lldpinfo.setLocalIP(mac);
									lldpinfos.add(lldpinfo);

									if (status == 5) {
										lldpinfo.setConnected(true);
									} else {
										lldpinfo.setConnected(false);
									}
								}
								if (status == 5) {
									onuEntity
											.setNowStatus(com.jhw.adm.server.entity.util.Constants.CONNECTED);
								} else {
									onuEntity
											.setNowStatus(com.jhw.adm.server.entity.util.Constants.DISCONNECTED);
								}

								onuEntity.setLldpinfos(lldpinfos);

								onuEntity.setSequenceNo(sequenceNo);
								onuEntity.setMacValue(mac);
								// log
								// .info("OLT ip:" + ip + " ONU-seqNo:"
								// + sequenceNo + " mac:" + mac
								// + " status:"
								// + onuEntity.getNowStatus());
								onuEntity.setDistance(distance);
								onuEntitySet.add(onuEntity);

							}
						}
					}

				}

			}

			return onuEntitySet;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
		}
	}

	public Object[] getOltPortStateTable(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(2 * 1000);
		String[] columnOIDs = { ifDescr, ifType, ifPhysAddress, ifOperStatus };
		List<TableEvent> tableEventList = null;
		Object[] objArray = null;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				objArray = new Object[2];
				SlotPort slotPort = new SlotPort();
				Set<OLTPort> oltPortSet = new HashSet<OLTPort>();
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}

					String desc = variableBinding[0].getVariable().toString();
					int type = variableBinding[1].getVariable().toInt();
					Object obj = variableBinding[2].getVariable();
					int status = variableBinding[3].getVariable().toInt();
					if (type == 6 || type == 1) {

						desc = desc.trim();
						int position = desc.indexOf("/");
						int additional = desc.indexOf(":");
						OLTPort oltPort = new OLTPort();
						if (position != -1) {
							int slot = Integer.parseInt(desc.substring(
									position - 1, position).trim());
							int port = 0;
							if (additional != -1) {
								port = Integer.parseInt(desc.substring(
										position + 1, additional).trim());
							} else {
								port = Integer.parseInt(desc.substring(
										position + 1).trim());
							}

							oltPort.setPortName(desc);
							if (desc.startsWith("Giga")) {
								oltPort.setPortType("GigaEthernet");
							} else if (desc.startsWith("Fast")) {
								oltPort.setPortType("FastEthernet");
							} else {
								oltPort.setPortType("EPON");
							}

							if (status == 1
									&& (desc.startsWith("Gig") || desc
											.startsWith("Fas"))) {
								slotPort.setSlot(slot);
								slotPort.setPort(port);
								slotPort.setPortType(com.jhw.adm.server.entity.util.Constants.TX);
							}

							oltPort.setSlotNum(slot);
							oltPort.setProtNo(port);
							if (status == 1) {
								oltPort.setPortStatus(true);
							} else {
								oltPort.setPortStatus(false);
							}
						}
						// For Diagnose
						else {
							oltPort.setPortName(desc);
							oltPort.setPortType("°ÙÕ×¶Ë¿Ú");
						}
						oltPortSet.add(oltPort);
					}
				}
				objArray[0] = slotPort;
				objArray[1] = oltPortSet;
				// For Diagnose
				final List<TableEvent> tableEventListDiagnose = tableEventList;
				final Set<OLTPort> oltPortSetDiagnose = oltPortSet;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						IDiagnose iDiagnose = diagnoseView
								.getDiagnoseReference(AutoIncreaseConstants.IFTABLE);
						if (iDiagnose != null) {
							StringBuilder ssb = new StringBuilder();
							StringBuilder tsb = new StringBuilder();
							for (TableEvent tableEvent : tableEventListDiagnose) {
								VariableBinding[] variableBinding = tableEvent
										.getColumns();
								if (variableBinding == null) {
									continue;
								}
								ssb.append(variableBinding[0])
										.append(System
												.getProperty("line.separator"))
										.append(variableBinding[1])
										.append(System
												.getProperty("line.separator"))
										.append(variableBinding[2])
										.append(System
												.getProperty("line.separator"))
										.append(variableBinding[3])
										.append(System
												.getProperty("line.separator"));
							}
							ssb.append("******").append(
									System.getProperty("line.separator"));
							tableEventListDiagnose.clear();
							for (OLTPort oltPort : oltPortSetDiagnose) {
								tsb.append("PortName£º")
										.append(oltPort.getPortName())
										.append(System
												.getProperty("line.separator"))
										.append("PortType£º")
										.append(oltPort.getPortType())
										.append(System
												.getProperty("line.separator"))
										.append("SlotNum£º")
										.append(oltPort.getSlotNum())
										.append(System
												.getProperty("line.separator"))
										.append("ProtNo£º")
										.append(oltPort.getProtNo())
										.append(System
												.getProperty("line.separator"));
								// .append("Descs£º")
								// .append(oltPort.getDescs())
								// .append(
								// System
								// .getProperty("line.separator"))

							}
							tsb.append("******").append(
									System.getProperty("line.separator"));
							iDiagnose.receiveInfo(ssb.toString(),
									tsb.toString());
						}
					}
				});
				//
			}
			return objArray;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {

		}

	}

	/**
	 * @deprecated
	 * @param ip
	 * @param twoLayersMac
	 */
	public void getTwoLayerMac(String ip,
			Map<String, List<TwoLayerMacEntry>> twoLayersMac) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(1 * 1000);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(macNum);
			int mNum = 0;
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				mNum = responseVar.elementAt(0).getVariable().toInt();
			}
			if (mNum == 0) {
				return;
			}
			snmpV2.setTimeout(5 * 1000);
			int pagesize = 20;
			int mod = mNum % pagesize;
			int pages = mNum / pagesize;
			pages = mod == 0 ? pages : pages + 1;
			Vector<String> oids = new Vector<String>();
			List<TwoLayerMacEntry> twoLayerMacEntrytList = new ArrayList<TwoLayerMacEntry>();
			// int macPort = 0;
			for (int i = 0; i < pages; i++) {
				int step = i * pagesize;
				oids.add(macAddr + "." + step);
				oids.add(portIdx + "." + step);
				if (mNum - (i * pagesize) > mod) {
					response = snmpV2.snmpGetBulk(oids, 0, pagesize);
				} else {
					response = snmpV2.snmpGetBulk(oids, 0, mod);
				}
				responseVar = checkResponseVar(response);
				if (responseVar != null) {
					int vbsSize = responseVar.size();
					for (int j = 0; j < vbsSize; j++) {
						if (j % 2 == 0) {
							String mac = responseVar.elementAt(j + 0)
									.getVariable().toString();
							int port = responseVar.elementAt(j + 1)
									.getVariable().toInt();
							TwoLayerMacEntry twoLayerMacEntry = new TwoLayerMacEntry();
							twoLayerMacEntry.setMac(mac);
							twoLayerMacEntry.setPort(port);
							twoLayerMacEntrytList.add(twoLayerMacEntry);
						}
					}
				}
				oids.clear();
			}
			twoLayersMac.put(ip, twoLayerMacEntrytList);

			// return null;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			// return null;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	public List<Dot1dStpPortTable> getDot1dStpPortTable(String ip,
			String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(2000);
		String[] columnOIDs = { dot1dStpPort, dot1dStpPortState,
				dot1dStpPortPathCost, dot1dStpPortDesignatedBridge,
		// dot1dStpPortDesignatedPort
		};
		int columnOIDLength = columnOIDs.length;
		List<TableEvent> tableEventList = null;
		List<Dot1dStpPortTable> dot1dStpPortTableList = null;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				dot1dStpPortTableList = new ArrayList<Dot1dStpPortTable>();
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					int vbLength = variableBinding.length;
					for (int i = 0; i < vbLength; i++) {
						if (i % columnOIDLength == 0) {
							int stpPort = variableBinding[i + 0].getVariable()
									.toInt();
							int stpPortState = variableBinding[i + 1]
									.getVariable().toInt();
							int stpPortPathCost = variableBinding[i + 2]
									.getVariable().toInt();
							String stpPortDesignatedBridge = variableBinding[i + 3]
									.getVariable().toString();

							if (stpPortState == 2 || stpPortState == 5) {
								Dot1dStpPortTable dot1dStpPortTable = new Dot1dStpPortTable();
								dot1dStpPortTable.setDot1dStpPort(stpPort);
								dot1dStpPortTable
										.setDot1dStpPortState(stpPortState);
								dot1dStpPortTable
										.setDot1dStpPortPathCost(stpPortPathCost);
								dot1dStpPortTable
										.setDot1dStpPortDesignatedBridge(stpPortDesignatedBridge);
								dot1dStpPortTableList.add(dot1dStpPortTable);
							}
						}
					}
				}
				// For Diagnose
				final List<TableEvent> tableEventListDiagnose = tableEventList;
				final List<Dot1dStpPortTable> dot1dStpPortTableListDiagnose = dot1dStpPortTableList;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						IDiagnose iDiagnose = diagnoseView
								.getDiagnoseReference(AutoIncreaseConstants.DOT1DSTPPORTTABLE);
						if (iDiagnose != null) {
							StringBuilder ssb = new StringBuilder();
							StringBuilder tsb = new StringBuilder();
							for (TableEvent tableEvent : tableEventListDiagnose) {
								VariableBinding[] variableBinding = tableEvent
										.getColumns();
								if (variableBinding == null) {
									continue;
								}
								ssb.append(variableBinding[0])
										.append(System
												.getProperty("line.separator"))
										.append(variableBinding[1])
										.append(System
												.getProperty("line.separator"))
										.append(variableBinding[2])
										.append(System
												.getProperty("line.separator"))
										.append(variableBinding[3])
										.append(System
												.getProperty("line.separator"));
							}
							ssb.append("******").append(
									System.getProperty("line.separator"));
							tableEventListDiagnose.clear();
							for (Dot1dStpPortTable dot1dStpPortTable : dot1dStpPortTableListDiagnose) {
								tsb.append("dot1dStpPort£º")
										.append(dot1dStpPortTable
												.getDot1dStpPort())
										.append(System
												.getProperty("line.separator"))
										.append("dot1dStpPortState£º")
										.append(dot1dStpPortTable
												.getDot1dStpPortState())
										.append(System
												.getProperty("line.separator"))
										.append("dot1dStpPortPathCost£º")
										.append(dot1dStpPortTable
												.getDot1dStpPortPathCost())
										.append(System
												.getProperty("line.separator"))
										.append(dot1dStpPortTable
												.getDot1dStpPortDesignatedBridge())
										.append("dot1dStpPortDesignatedBridge£º")
										.append(System
												.getProperty("line.separator"));
							}
							tsb.append("******").append(
									System.getProperty("line.separator"));
							iDiagnose.receiveInfo(ssb.toString(),
									tsb.toString());
						}
					}
				});
				//
				return dot1dStpPortTableList;
			}
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
		} finally {

		}
		return null;
	}

	public AbstractSnmp getSnmpV2() {
		return snmpV2;
	}

	public void setSnmpV2(AbstractSnmp snmpV2) {
		this.snmpV2 = snmpV2;
	}

	public void setDiagnoseView(DiagnoseView diagnoseView) {
		this.diagnoseView = diagnoseView;
	}

}
