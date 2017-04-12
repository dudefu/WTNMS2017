package com.jhw.adm.comclient.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.DataBufferBuilder;
import com.jhw.adm.comclient.data.OID;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.server.entity.warning.PortRemon;
import com.jhw.adm.server.entity.warning.RmonCount;
import com.jhw.adm.server.entity.warning.RmonThing;
import com.jhw.adm.server.entity.warning.RmonWarningConfig;
import com.jhw.adm.server.entity.warning.ThresholdValue;

/**
 * Rmon
 * 
 * @author xiongbo
 * 
 */
public class RmonHandler extends BaseHandler {
	private static Logger log = Logger.getLogger(RmonHandler.class);
	private AbstractSnmp snmpV2;
	private DataBufferBuilder dataBufferBuilder;
	//
	private Map<String, String> oidParameterMap = new HashMap<String, String>(
			16);
	private final int DOT_END_POSITION = 29;

	public RmonHandler() {
		oidParameterMap.put(OID.RMON_STOCTETS,
				com.jhw.adm.server.entity.util.Constants.octets);
		oidParameterMap.put(OID.RMON_STPACKETS,
				com.jhw.adm.server.entity.util.Constants.packets);
		oidParameterMap.put(OID.RMON_STBCASTPKTS,
				com.jhw.adm.server.entity.util.Constants.bcast_pkts);
		oidParameterMap.put(OID.RMON_STMCASTPKTS,
				com.jhw.adm.server.entity.util.Constants.mcast_pkts);
		oidParameterMap.put(OID.RMON_STCRCALIGN,
				com.jhw.adm.server.entity.util.Constants.crc_align);
		oidParameterMap.put(OID.RMON_STUNDERSIZE,
				com.jhw.adm.server.entity.util.Constants.undersize);
		oidParameterMap.put(OID.RMON_STOVERSIZE,
				com.jhw.adm.server.entity.util.Constants.oversize);
		oidParameterMap.put(OID.RMON_STFRAGMENTS,
				com.jhw.adm.server.entity.util.Constants.fragments);
		oidParameterMap.put(OID.RMON_STJABBERS,
				com.jhw.adm.server.entity.util.Constants.jabbers);
		oidParameterMap.put(OID.RMON_STCOLLISIONS,
				com.jhw.adm.server.entity.util.Constants.collisions);
		oidParameterMap.put(OID.RMON_STPKTS64,
				com.jhw.adm.server.entity.util.Constants.pkts_64);
		oidParameterMap.put(OID.RMON_STPKTS65TO127,
				com.jhw.adm.server.entity.util.Constants.pkts_65_127);
		oidParameterMap.put(OID.RMON_STPKTS128TO255,
				com.jhw.adm.server.entity.util.Constants.pkts_128_255);
		oidParameterMap.put(OID.RMON_STPKTS256TO511,
				com.jhw.adm.server.entity.util.Constants.pkts_256_511);
		oidParameterMap.put(OID.RMON_STPKTS512TO1023,
				com.jhw.adm.server.entity.util.Constants.pkts_512_1023);
		oidParameterMap.put(OID.RMON_STPKTS1024TO1518,
				com.jhw.adm.server.entity.util.Constants.pkts_1024_1518);
		oidParameterMap.put(OID.RMON_IFINDISCARDS,
				com.jhw.adm.server.entity.util.Constants.ifInDiscards);
		oidParameterMap.put(OID.RMON_IFOUTDISCARDS,
				com.jhw.adm.server.entity.util.Constants.ifOutDiscards);
		oidParameterMap.put(OID.RMON_TXPACKETS,
				com.jhw.adm.server.entity.util.Constants.txPackets);
		oidParameterMap.put(OID.RMON_TXBCASTPKTS,
				com.jhw.adm.server.entity.util.Constants.txBcastPkts);
		oidParameterMap.put(OID.RMON_TXMCASTPKTS,
				com.jhw.adm.server.entity.util.Constants.txMcastPkts);
	}

	public List<PortRemon> getRmonStatEntry(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		Vector<String> oids = new Vector<String>();
		oids.add(OID.RMON_STCTRLINDEX);
		oids.add(OID.RMON_STIFINDEX);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(OID.RMONSTATICSNUM);
			int rmonStaticsNum = 0;
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			List<PortRemon> rmonCounts = null;
			if (responseVar != null) {
				rmonCounts = new ArrayList<PortRemon>();
				rmonStaticsNum = responseVar.elementAt(0).getVariable().toInt();
			}
			if (rmonStaticsNum == 0) {
				return rmonCounts;
			}
			snmpV2.setTimeout(5 * 1000);
			response = snmpV2.snmpGetBulk(oids, 0, rmonStaticsNum);
			responseVar = checkResponseVar(response);
			if (responseVar != null) {
				int vbsSize = responseVar.size();

				for (int i = 0; i < vbsSize; i++) {
					if (i % 2 == 0) {
						PortRemon portRemon = new PortRemon();
						portRemon.setCode(responseVar.elementAt(i + 0)
								.getVariable().toInt());
						portRemon.setPortNo(responseVar.elementAt(i + 1)
								.getVariable().toInt());
						rmonCounts.add(portRemon);
					}
				}
				return rmonCounts;
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

	/**
	 * @deprecated
	 * @param ip
	 * @return
	 */
	public List<Object> getRmonStatList(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		Vector<String> oids = null;
		try {
			response = snmpV2.snmpGet(OID.RMONSTATICSNUM);
			int rmonStaticsNum = 0;
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				rmonStaticsNum = responseVar.elementAt(0).getVariable().toInt();
			}
			if (rmonStaticsNum == 0) {
				return null;
			}
			// snmpV2.setTimeout(8 * 1000);
			// int pagesize = 8;
			// int mod = rmonStaticsNum % pagesize;
			// int pages = rmonStaticsNum / pagesize;
			// pages = mod == 0 ? pages : pages + 1;
			oids = new Vector<String>();
			oids.add(OID.RMON_STCTRLINDEX);
			oids.add(OID.RMON_STIFINDEX);
			oids.add(OID.RMON_STDROPEVENTS);
			oids.add(OID.RMON_STOCTETS);
			oids.add(OID.RMON_STPACKETS);
			oids.add(OID.RMON_STBCASTPKTS);
			oids.add(OID.RMON_STMCASTPKTS);
			oids.add(OID.RMON_STCRCALIGN);
			oids.add(OID.RMON_STUNDERSIZE);
			oids.add(OID.RMON_STOVERSIZE);
			oids.add(OID.RMON_STFRAGMENTS);
			oids.add(OID.RMON_STJABBERS);
			oids.add(OID.RMON_STCOLLISIONS);
			oids.add(OID.RMON_STPKTS64);
			oids.add(OID.RMON_STPKTS65TO127);
			oids.add(OID.RMON_STPKTS128TO255);
			oids.add(OID.RMON_STPKTS256TO511);
			oids.add(OID.RMON_STPKTS512TO1023);
			oids.add(OID.RMON_STPKTS1024TO1518);
			oids.add(OID.RMON_IFINDISCARDS);
			oids.add(OID.RMON_IFOUTDISCARDS);
			oids.add(OID.RMON_TXPACKETS);
			oids.add(OID.RMON_TXBCASTPKTS);
			oids.add(OID.RMON_TXMCASTPKTS);
			int i = 0;
			List<Object> rmonStats = new ArrayList<Object>();
			while (i < rmonStaticsNum) {
				response = snmpV2.snmpGetNext(oids);
				responseVar = checkResponseVar(response);
				if (responseVar != null) {
					// TODO

					oids.clear();
					oids.add(responseVar.elementAt(0).getOid().toString());
					oids.add(responseVar.elementAt(1).getOid().toString());
					oids.add(responseVar.elementAt(2).getOid().toString());
					oids.add(responseVar.elementAt(3).getOid().toString());
					oids.add(responseVar.elementAt(4).getOid().toString());
					oids.add(responseVar.elementAt(5).getOid().toString());
					oids.add(responseVar.elementAt(6).getOid().toString());
					oids.add(responseVar.elementAt(7).getOid().toString());
					oids.add(responseVar.elementAt(8).getOid().toString());
					oids.add(responseVar.elementAt(9).getOid().toString());
					oids.add(responseVar.elementAt(10).getOid().toString());
					oids.add(responseVar.elementAt(11).getOid().toString());
					oids.add(responseVar.elementAt(12).getOid().toString());
					oids.add(responseVar.elementAt(13).getOid().toString());
					oids.add(responseVar.elementAt(14).getOid().toString());
					oids.add(responseVar.elementAt(15).getOid().toString());
					oids.add(responseVar.elementAt(16).getOid().toString());
					oids.add(responseVar.elementAt(17).getOid().toString());
					oids.add(responseVar.elementAt(18).getOid().toString());
					oids.add(responseVar.elementAt(19).getOid().toString());
					oids.add(responseVar.elementAt(20).getOid().toString());
					oids.add(responseVar.elementAt(21).getOid().toString());
					oids.add(responseVar.elementAt(22).getOid().toString());
					oids.add(responseVar.elementAt(23).getOid().toString());
					oids.add(responseVar.elementAt(24).getOid().toString());
				} else {
					break;
				}
				i++;
			}

			// TODO
			// for (int i = 0; i < pages; i++) {
			// int step = i * pagesize;
			// oids.add(OID.RMON_STCTRLINDEX + "." + step);
			// oids.add(OID.RMON_STIFINDEX + "." + step);
			// oids.add(OID.RMON_STDROPEVENTS + "." + step);
			// oids.add(OID.RMON_STOCTETS + "." + step);
			// oids.add(OID.RMON_STPACKETS + "." + step);
			// oids.add(OID.RMON_STBCASTPKTS + "." + step);
			// oids.add(OID.RMON_STMCASTPKTS + "." + step);
			// oids.add(OID.RMON_STCRCALIGN + "." + step);
			// oids.add(OID.RMON_STUNDERSIZE + "." + step);
			// oids.add(OID.RMON_STOVERSIZE + "." + step);
			// oids.add(OID.RMON_STFRAGMENTS + "." + step);
			// oids.add(OID.RMON_STJABBERS + "." + step);
			// oids.add(OID.RMON_STCOLLISIONS + "." + step);
			// oids.add(OID.RMON_STPKTS64 + "." + step);
			// oids.add(OID.RMON_STPKTS65TO127 + "." + step);
			// oids.add(OID.RMON_STPKTS128TO255 + "." + step);
			// oids.add(OID.RMON_STPKTS256TO511 + "." + step);
			// oids.add(OID.RMON_STPKTS512TO1023 + "." + step);
			// oids.add(OID.RMON_STPKTS1024TO1518 + "." + step);
			// if (rmonStaticsNum - (i * pagesize) > mod) {
			// response = snmpV2.snmpGetBulk(oids, 0, pagesize);
			// } else {
			// response = snmpV2.snmpGetBulk(oids, 0, mod);
			// }
			// responseVar = checkResponseVar(response);
			// if (responseVar != null) {
			// int vbsSize = responseVar.size();
			// for (int j = 0; j < vbsSize; j++) {
			// if (j % 19 == 0) {
			//
			// }
			// }
			// }
			// oids.clear();
			// }
			// return null;

			// TODO
			return null;
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

	/**
	 * For performance monitor
	 * 
	 * @param ip
	 * @param portNum
	 * @return
	 */
	public int getPortIndex(String ip, int portNum) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		Vector<String> oid = null;
		try {
			response = snmpV2.snmpGet(OID.RMONSTATICSNUM);
			int rmonStaticsNum = 0;
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				rmonStaticsNum = responseVar.elementAt(0).getVariable().toInt();
			}
			if (rmonStaticsNum == 0) {
				return 0;
			}
			oid = new Vector<String>();
			oid.add(OID.RMON_STIFINDEX);
			snmpV2.setTimeout(5 * 1000);
			response = snmpV2.snmpGetBulk(oid, 0, rmonStaticsNum);
			responseVar = checkResponseVar(response);
			int index = 0;
			if (responseVar != null) {
				int vbsSize = responseVar.size();
				for (int i = 0; i < vbsSize; i++) {
					int pot = responseVar.elementAt(i).getVariable().toInt();
					if (pot == portNum) {
						index = i + 1;
						break;
					}
				}
			}
			return index;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return 0;
		} finally {
			if (response != null) {
				response.clear();
			}
			if (oid != null) {
				oid.clear();
			}
		}
	}

	/**
	 * Performance monitor
	 * 
	 * @param ip
	 * @param portIndex
	 * @return
	 */
	public ArrayList<RmonCount> getRmonStatData(String ip, int portNo,
			Vector<String> oids) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(oids);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			ArrayList<RmonCount> rmonCounts = null;
			if (responseVar != null) {
				Date date = new Date();
				int vbsSize = responseVar.size();
				rmonCounts = new ArrayList<RmonCount>();
				for (int i = 0; i < vbsSize; i++) {
					String oid = responseVar.elementAt(i).getOid().toString()
							.trim();
					oid = oid.substring(0, oid.lastIndexOf("."));
					long value = responseVar.elementAt(i).getVariable()
							.toLong();
					RmonCount rmonCount = new RmonCount();
					rmonCount.setParam(oidParameterMap.get(oid));
					rmonCount.setValue(value);
					rmonCount.setPortNo(portNo);
					rmonCount.setIpValue(ip);
					rmonCount.setSampleTime(date);
					rmonCounts.add(rmonCount);
				}
			}
			return rmonCounts;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	/**
	 * @deprecated
	 */
	public List<Object> getRmonStatHistory(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		Vector<String> oids = new Vector<String>();
		oids.add(OID.RMONSTATICSINDEX);
		oids.add(OID.RMONPORTINDEX);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(OID.RMONSTATICSNUM);
			int rmonStaticsNum = 0;
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				rmonStaticsNum = responseVar.elementAt(0).getVariable().toInt();
			}
			if (rmonStaticsNum == 0) {
				return null;
			}
			snmpV2.setTimeout(6 * 1000);
			response = snmpV2.snmpGetBulk(oids, 0, rmonStaticsNum);
			responseVar = checkResponseVar(response);
			if (responseVar != null) {
				int vbsSize = responseVar.size();
				List<Object> rmonCounts = new ArrayList<Object>();
				for (int i = 0; i < vbsSize; i++) {
					if (i % 2 == 0) {
						// TODO
						// RmonCount rmonCount = new RmonCount();
						responseVar.elementAt(i + 0).getVariable();
						responseVar.elementAt(i + 1).getVariable();
					}
				}
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

	public List<RmonWarningConfig> getRmonAlarmList(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(OID.RMONALARMNUM);
			int rmonAlarmNum = 0;
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			List<RmonWarningConfig> rmonAlarms = null;
			if (responseVar != null) {
				rmonAlarms = new ArrayList<RmonWarningConfig>();
				rmonAlarmNum = responseVar.elementAt(0).getVariable().toInt();
			}
			if (rmonAlarmNum == 0) {
				return rmonAlarms;
			}
			snmpV2.setTimeout(10 * 1000);
			int pagesize = 8;
			int mod = rmonAlarmNum % pagesize;
			int pages = rmonAlarmNum / pagesize;
			pages = mod == 0 ? pages : pages + 1;
			Vector<String> oids = new Vector<String>();

			for (int i = 1; i <= mod; i++) {
				// int step = i * pagesize;
				int step = i;
				oids.add(OID.RMON_HISCTRLINDEX + "." + step);
				oids.add(OID.RMON_HISIFINDEX + "." + step);
				oids.add(OID.RMON_HISINTERVAL + "." + step);
				oids.add(OID.RMON_HISVARIABLE + "." + step);
				oids.add(OID.RMON_HISSAMPLETYPE + "." + step);
				oids.add(OID.RMON_HISRISINGTHRESHOLD + "." + step);
				oids.add(OID.RMON_HISFALLINGTHRESHOLD + "." + step);
				oids.add(OID.RMON_HISRISINGEVENTINDEX + "." + step);
				oids.add(OID.RMON_HISFALLINGEVENTINDEX + "." + step);
				if (rmonAlarmNum - (i * pagesize) > mod) {
					response = snmpV2.snmpGet(oids);
				} else {
					response = snmpV2.snmpGet(oids);
				}
				responseVar = checkResponseVar(response);
				if (responseVar != null) {
					int vbsSize = responseVar.size();
					for (int j = 0; j < vbsSize; j++) {
						if (j % 9 == 0) {
							RmonWarningConfig rmonWarningConfig = new RmonWarningConfig();
							rmonWarningConfig.setItemCode(responseVar
									.elementAt(0 + j).getVariable().toInt());
							rmonWarningConfig.setPortNo(responseVar.elementAt(
									1 + j).getVariable().toInt());
							rmonWarningConfig.setSampleTime(responseVar
									.elementAt(2 + j).getVariable().toInt());
							ThresholdValue thresholdValue = new ThresholdValue();
							thresholdValue.setWarnParm(responseVar.elementAt(
									3 + j).getVariable().toString());
							thresholdValue.setSamplingType(responseVar
									.elementAt(4 + j).getVariable().toInt());
							thresholdValue.setK_Max(responseVar
									.elementAt(5 + j).getVariable().toInt());
							thresholdValue.setK_low(responseVar
									.elementAt(6 + j).getVariable().toInt());
							rmonWarningConfig.setMaxLimitcode(responseVar
									.elementAt(7 + j).getVariable().toString());
							rmonWarningConfig.setLowLimitcode(responseVar
									.elementAt(8 + j).getVariable().toString());
							rmonWarningConfig.setThreshold(thresholdValue);
							rmonAlarms.add(rmonWarningConfig);
						}
					}
				}
				oids.clear();
			}
			return rmonAlarms;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	public List<RmonThing> getRmonEventList(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		Vector<String> oids = null;
		PDU response = null;
		try {
			response = snmpV2.snmpGet(OID.RMONEVENTNUMBER);
			int rmonEventNumber = 0;
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			List<RmonThing> rmonEvents = null;
			if (responseVar != null) {
				rmonEvents = new ArrayList<RmonThing>();
				rmonEventNumber = responseVar.elementAt(0).getVariable()
						.toInt();
			}
			if (rmonEventNumber == 0) {
				return rmonEvents;
			}
			snmpV2.setTimeout(5 * 1000);
			oids = new Vector<String>();
			oids.add(OID.RMON_EVENTCTRLINDEX);
			oids.add(OID.RMON_EVENTDESCRIPTION);
			oids.add(OID.RMON_EVENTTYPE);
			oids.add(OID.RMON_EVENTCOMMUNITY);
			response = snmpV2.snmpGetBulk(oids, 0, rmonEventNumber);
			responseVar = checkResponseVar(response);
			if (responseVar != null) {
				int vbsSize = responseVar.size();
				for (int i = 0; i < vbsSize; i++) {
					if (i % 4 == 0) {
						RmonThing rmonThing = new RmonThing();
						rmonThing.setCode(responseVar.elementAt(0 + i)
								.getVariable().toString());
						if (!isEmpty(responseVar.elementAt(1 + i).getVariable())) {
							rmonThing.setDescs(responseVar.elementAt(1 + i)
									.getVariable().toString());
						}
						rmonThing.setWarningStyle(responseVar.elementAt(2 + i)
								.getVariable().toString());
						if (!isEmpty(responseVar.elementAt(3 + i).getVariable())) {
							rmonThing.setCommunityName(responseVar.elementAt(
									3 + i).getVariable().toString());
						}
						rmonEvents.add(rmonThing);
					}
				}
				return rmonEvents;
			}
			return null;
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

	public boolean addRmonStatic(String ip, PortRemon portRemon) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		PDU response = null;
		try {
			byte[] buf = dataBufferBuilder.rmonStaticAdd(portRemon.getCode(),
					portRemon.getPortNo());
			response = snmpV2.snmpSet(OID.RMON_BATCH_OPERATE, buf);
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

	// ±£´æ
	public boolean addRmonAlarm(String ip, RmonWarningConfig rmonWarningConfig) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		PDU response = null;
		try {
			int ctrl_index = rmonWarningConfig.getItemCode();
			int portId = rmonWarningConfig.getPortNo();
			int interval = rmonWarningConfig.getSampleTime();
			String variable = rmonWarningConfig.getThreshold().getWarnParm();
			int sample_type = rmonWarningConfig.getThreshold()
					.getSamplingType();
			String sample_type_str = null;
			if (sample_type == 0) {
				sample_type_str = "delta";
			} else {
				sample_type_str = "absolute";
			}
			int rising_threshold = rmonWarningConfig.getThreshold().getK_Max();
			int falling_threshold = rmonWarningConfig.getThreshold().getK_low();
			int rising_event_index = Integer.parseInt(rmonWarningConfig
					.getMaxLimitcode());
			int falling_event_index = Integer.parseInt(rmonWarningConfig
					.getLowLimitcode());

			byte[] buf = dataBufferBuilder.rmonAlarmAdd(ctrl_index, portId,
					interval, variable, sample_type_str, rising_threshold,
					falling_threshold, rising_event_index, falling_event_index);
			response = snmpV2.snmpSet(OID.RMON_BATCH_OPERATE, buf);
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

	public boolean addRmonEvent(String ip, RmonThing rmonThing) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		PDU response = null;
		try {
			// byte[] buf = dataBufferBuilder.rmonEventAdd(Integer
			// .parseInt(rmonThing.getCode()), rmonThing.getDescs(),
			// rmonThing.getWarningStyle(), rmonThing.getCommunityName());

			byte[] buf = dataBufferBuilder.rmonEventAdd(Integer
					.parseInt(rmonThing.getCode()), rmonThing.getDescs(),
					rmonThing.getWarningStyle(), rmonThing.getCommunityName());
			response = snmpV2.snmpSet(OID.RMON_BATCH_OPERATE, buf);
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

	public boolean deleteRmonStatic(String ip, PortRemon portRemon) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		PDU response = null;
		try {
			byte[] buf = dataBufferBuilder
					.rmonStaticDelete(portRemon.getCode());
			response = snmpV2.snmpSet(OID.RMON_BATCH_OPERATE, buf);
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

	public boolean deleteRmonAlarm(String ip,
			RmonWarningConfig rmonWarningConfig) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		PDU response = null;
		try {
			byte[] buf = dataBufferBuilder.rmonAlarmDelete(rmonWarningConfig
					.getItemCode());
			response = snmpV2.snmpSet(OID.RMON_BATCH_OPERATE, buf);
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

	public boolean deleteRmonEvent(String ip, RmonThing rmonThing) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		PDU response = null;
		try {
			byte[] buf = dataBufferBuilder.rmonEventDelete(Integer
					.parseInt(rmonThing.getCode()));
			response = snmpV2.snmpSet(OID.RMON_BATCH_OPERATE, buf);
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

}
