package com.jhw.adm.comclient.service.epon;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.EponOID;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.comclient.service.BaseHandler;
import com.jhw.adm.server.entity.epon.OLTChipInfo;
import com.jhw.adm.server.entity.epon.OLTSlotConfig;
import com.jhw.adm.server.entity.util.EmulationEntity;
import com.jhw.adm.server.entity.util.PortSignal;

/**
 * Include Olt slot,Chip,Encryp,Multicast
 * 
 * @author xiongbo
 * 
 */
public class OltBaseConfigHandler extends BaseHandler {
	private AbstractSnmp snmpV2;

	public List<OLTSlotConfig> getSlot(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 1500);
		String[] columnOIDs = { EponOID.OLTSLOTINDEX,
				EponOID.OLTSLOTHELLOINTERVAL, EponOID.OLTSLOTDEADINTERVAL,
				EponOID.OLTSLOTCHIPSREGISTEREDNUMBER };
		// PDU response = null;
		List<TableEvent> tableEventList = null;
		List<OLTSlotConfig> slotList = null;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				slotList = new ArrayList<OLTSlotConfig>();
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					for (int i = 0; i < variableBinding.length; i++) {
						if (i % columnOIDs.length == 0) {
							OLTSlotConfig oltSlotConfig = new OLTSlotConfig();
							oltSlotConfig.setSlotID(variableBinding[i + 0]
									.getVariable().toInt());
							oltSlotConfig
									.setHelloMsgTimeOut(variableBinding[i + 1]
											.getVariable().toInt());
							oltSlotConfig
									.setConnectTimerOut(variableBinding[i + 2]
											.getVariable().toInt());
							oltSlotConfig
									.setRegisteredNum(variableBinding[i + 3]
											.getVariable().toInt());
							slotList.add(oltSlotConfig);
						}
					}
				}
			}
			return slotList;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
			// if (response != null) {
			// response.clear();
			// }
		}
	}

	public List<OLTChipInfo> getChip(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 1500);
		String[] columnOIDs = { EponOID.OLTCHIPINDEX, EponOID.OLTCHIPSLOTID,
				EponOID.OLTCHIPMODULEID, EponOID.OLTCHIPDEVICEID,
				EponOID.OLTCHIPMACADDRESS, EponOID.OLTCHIPSTATUS };
		// PDU response = null;
		List<TableEvent> tableEventList = null;
		List<OLTChipInfo> chipList = null;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				chipList = new ArrayList<OLTChipInfo>();
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					for (int i = 0; i < variableBinding.length; i++) {
						if (i % columnOIDs.length == 0) {
							OLTChipInfo oltChipInfo = new OLTChipInfo();
							oltChipInfo.setChipIndex(variableBinding[i + 0]
									.getVariable().toInt());
							oltChipInfo.setSlotID(variableBinding[i + 1]
									.getVariable().toInt());
							oltChipInfo.setModuleID(variableBinding[i + 2]
									.getVariable().toInt());
							oltChipInfo.setDeviceID(variableBinding[i + 3]
									.getVariable().toInt());
							oltChipInfo.setMac(variableBinding[i + 4]
									.getVariable().toString());
							oltChipInfo.setChipStatus(variableBinding[i + 5]
									.getVariable().toInt());

							chipList.add(oltChipInfo);
						}
					}
				}
			}
			return chipList;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
			// if (response != null) {
			// response.clear();
			// }
		}
	}

	public EmulationEntity getEmulate(String ip, String community) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(community);
		snmpV2.setTimeout(1 * 1500);
		String[] columnOIDs = { "1.3.6.1.2.1.2.2.1.2", "1.3.6.1.2.1.2.2.1.3",
				"1.3.6.1.2.1.2.2.1.8" };
		int columOIDLength = columnOIDs.length;
		List<TableEvent> tableEventList = null;
		EmulationEntity emulationEntity = null;
		try {
			tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
			if (tableEventList != null) {
				emulationEntity = new EmulationEntity();
				Set<PortSignal> portSignalSet = new HashSet<PortSignal>();
				for (TableEvent tableEvent : tableEventList) {
					VariableBinding[] variableBinding = tableEvent.getColumns();
					if (variableBinding == null) {
						continue;
					}
					int vbLength = variableBinding.length;
					for (int i = 0; i < vbLength; i++) {
						if (i % columOIDLength == 0) {
							String desc = variableBinding[i + 0].getVariable()
									.toString();
							int type = variableBinding[i + 1].getVariable()
									.toInt();
							int status = variableBinding[i + 2].getVariable()
									.toInt();
							if (type == 6 || type == 1) {
								desc = desc.trim();
								int position = desc.indexOf("/");
								int additional = desc.indexOf(":");
								if (position != -1 && additional == -1) {
									int slot = Integer.parseInt(desc.substring(
											position - 1, position).trim());
									// int port = 0;
									// if (additional == -1) {
									// port =
									// Integer.parseInt(desc.substring(
									// position + 1, additional)
									// .trim());
									// } else {
									int port = Integer.parseInt(desc.substring(
											position + 1).trim());

									// }

									PortSignal portSignal = new PortSignal();
									portSignal.setSlotNum((byte) slot);
									portSignal.setPortNo((byte) port);
									if (desc.startsWith("GigaEthernet")
											|| desc.startsWith("FastEthernet")) {
										portSignal
												.setPortType((byte) com.jhw.adm.server.entity.util.Constants.TX);
										if (desc.startsWith("FastEthernet")) {
											portSignal
													.setPortName((byte) com.jhw.adm.server.entity.util.Constants.FAST_PORT);
										} else {
											portSignal
													.setPortName((byte) com.jhw.adm.server.entity.util.Constants.GIGA_PORT);
										}
									} else {
										portSignal
												.setPortType((byte) com.jhw.adm.server.entity.util.Constants.PX);
									}
									if (status == 1) {
										portSignal
												.setWorkingSignal(com.jhw.adm.server.entity.util.Constants.L_ON);
										portSignal
												.setDataSingal(com.jhw.adm.server.entity.util.Constants.L_ON);
									} else {
										portSignal
												.setWorkingSignal(com.jhw.adm.server.entity.util.Constants.L_OFF);
										portSignal
												.setDataSingal(com.jhw.adm.server.entity.util.Constants.L_OFF);
									}
									portSignalSet.add(portSignal);
								}
							}
						}
					}
				}
				emulationEntity
						.setPowerSingal(com.jhw.adm.server.entity.util.Constants.L_ON);
				emulationEntity.setPortSignals(portSignalSet);
			}
			return emulationEntity;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (tableEventList != null) {
				tableEventList.clear();
			}
		}
	}

	public AbstractSnmp getSnmpV2() {
		return snmpV2;
	}

	public void setSnmpV2(AbstractSnmp snmpV2) {
		this.snmpV2 = snmpV2;
	}

}
