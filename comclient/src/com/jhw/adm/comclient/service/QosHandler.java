package com.jhw.adm.comclient.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.snmp4j.PDU;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.TableEvent;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.DataBufferBuilder;
import com.jhw.adm.comclient.data.OID;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.server.entity.ports.QOSSpeedConfig;
import com.jhw.adm.server.entity.ports.QOSStormControl;
import com.jhw.adm.server.entity.ports.QOSSysConfig;
import com.jhw.adm.server.entity.ports.SpeedEntity;
import com.jhw.adm.server.entity.switchs.Priority802D1P;
import com.jhw.adm.server.entity.switchs.PriorityDSCP;
import com.jhw.adm.server.entity.switchs.PriorityTOS;

/**
 * QOS
 * 
 * 
 * 端口限速
 * 
 * 
 * 风暴控制
 * 
 * @author xiongbo
 * 
 */
public class QosHandler extends BaseHandler {
	private static Logger log = Logger.getLogger(QosHandler.class);
	private AbstractSnmp snmpV2;
	private DataBufferBuilder dataBufferBuilder;
	private PortHandler portHandler;

	public QOSSysConfig getQosConfig(String ip) {
		snmpV2.instanceV2(ip, Constants.SNMP_PORT, Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		Vector<String> vbs = new Vector<String>();
		vbs.add(OID.QOSSTATE);
		vbs.add(OID.QOSMODE);
		vbs.add(OID.QOSQUEUENUMBER);
		vbs.add(OID.QOSPRIORITYTRUST);
		vbs.add(OID.QOS_Q0WEIGHT);
		vbs.add(OID.QOS_Q1WEIGHT);
		vbs.add(OID.QOS_Q2WEIGHT);
		vbs.add(OID.QOS_Q3WEIGHT);
		PDU response = null;
		try {
			response = snmpV2.snmpGet(vbs);
			if (response != null
					&& SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
				Vector<VariableBinding> responseVar = response
						.getVariableBindings();
				QOSSysConfig qOSSysConfig = new QOSSysConfig();
				if (ENABLED.trim().equalsIgnoreCase(
						responseVar.elementAt(0).getVariable().toString()
								.trim())) {
					qOSSysConfig.setApplied(true);
				} else {
					qOSSysConfig.setApplied(false);
				}
				qOSSysConfig.setDispatchAlgorithms(responseVar.elementAt(1)
						.getVariable().toString());
				qOSSysConfig.setQueueValue(responseVar.elementAt(2)
						.getVariable().toInt());
				if ("dot1p".equalsIgnoreCase(responseVar.elementAt(3)
						.getVariable().toString())) {

					qOSSysConfig.setPriorityMode(0);
				} else if ("dscp".equalsIgnoreCase(responseVar.elementAt(3)
						.getVariable().toString())) {
					qOSSysConfig.setPriorityMode(1);
				} else {
					qOSSysConfig.setPriorityMode(2);
				}
				String dispatchConfig = responseVar.elementAt(4).getVariable()
						+ "," + responseVar.elementAt(5).getVariable() + ","
						+ responseVar.elementAt(6).getVariable() + ","
						+ responseVar.elementAt(7).getVariable();
				qOSSysConfig.setDispatchConfig(dispatchConfig);

				return qOSSysConfig;
			}
			return null;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
			vbs.clear();
		}

	}

	public boolean configQos(String ip, QOSSysConfig qOSSysConfig) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		Map<String, Object> oidMap = new HashMap<String, Object>();
		PDU response = null;
		try {
			if (qOSSysConfig.isApplied()) {
				oidMap.put(OID.QOSSTATE, enable);
			} else {
				oidMap.put(OID.QOSSTATE, disable);
			}
			oidMap.put(OID.QOSMODE, qOSSysConfig.getDispatchAlgorithms());
			oidMap.put(OID.QOSQUEUENUMBER, qOSSysConfig.getQueueValue());
			if (qOSSysConfig.getPriorityMode() == 0) {
				oidMap.put(OID.QOSPRIORITYTRUST, "dot1p");
			} else if (qOSSysConfig.getPriorityMode() == 1) {
				oidMap.put(OID.QOSPRIORITYTRUST, "dscp");
			} else {
				oidMap.put(OID.QOSPRIORITYTRUST, "tos");
			}
			String[] dispatchConfigs = qOSSysConfig.getDispatchConfig().split(
					",");

			oidMap.put(OID.QOS_Q0WEIGHT, Integer.parseInt(dispatchConfigs[0]));
			oidMap.put(OID.QOS_Q1WEIGHT, Integer.parseInt(dispatchConfigs[1]));
			oidMap.put(OID.QOS_Q2WEIGHT, Integer.parseInt(dispatchConfigs[2]));
			oidMap.put(OID.QOS_Q3WEIGHT, Integer.parseInt(dispatchConfigs[3]));

			response = snmpV2.snmpSet(oidMap);
			return checkResponse(response);
		} catch (RuntimeException e) {
			log.error(this.getTraceMessage(e));
			return false;
		} finally {
			if (response != null) {
				response.clear();
			}
			oidMap.clear();
		}
	}

	public boolean configQosDot1Priority(String ip,
			List<Priority802D1P> dot1PriorityList) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		int type = 1;
		try {
			for (Priority802D1P priority802D1P : dot1PriorityList) {
				int value = priority802D1P.getPriorityLevel();
				int queue = priority802D1P.getQueueValue();
				byte[] buf = dataBufferBuilder.qosPriorityCfg(type, value,
						queue);
				response = snmpV2.snmpSet(OID.QOS_PRIORITY_BATCH_OPERATE, buf);
				if (!this.checkResponse(response)) {
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

	public boolean configTosPriority(String ip, List<PriorityTOS> priorityTOSs) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		int type = 2;
		try {
			for (PriorityTOS priorityTOS : priorityTOSs) {
				int value = priorityTOS.getPriorityLevel();
				int queue = priorityTOS.getQueueValue();
				byte[] buf = dataBufferBuilder.qosPriorityCfg(type, value,
						queue);
				response = this.snmpV2.snmpSet(OID.QOS_PRIORITY_BATCH_OPERATE,
						buf);
				if (!this.checkResponse(response)) {
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

	public boolean configDscpPriority(String ip,
			List<PriorityDSCP> priorityDSCPs) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		int type = 3;
		try {
			for (PriorityDSCP priorityDSCP : priorityDSCPs) {
				// TODO
				// int value = priorityDSCP.g
				int value = priorityDSCP.getDscp();
				int queue = priorityDSCP.getQueueValue();
				byte[] buf = dataBufferBuilder.qosPriorityCfg(type, value,
						queue);
				response = snmpV2.snmpSet(OID.QOS_PRIORITY_BATCH_OPERATE, buf);
				if (!this.checkResponse(response)) {
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

	public List<Priority802D1P> getQosDot1Priority(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		Vector<String> oids = new Vector<String>();
		oids.add(OID.QOS_DOT1PRIORITYVALUE);
		oids.add(OID.QOS_DOTQUEUENUMBER);
		PDU response = null;
		try {
			response = snmpV2.snmpGetBulk(oids, 0, 8);// TODO '8'
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				int vbsSize = responseVar.size();
				List<Priority802D1P> priority802D1Ps = new ArrayList<Priority802D1P>();
				for (int i = 0; i < vbsSize; i++) {
					if (i % 2 == 0) {
						Priority802D1P priority802D1P = new Priority802D1P();
						priority802D1P.setPriorityLevel(responseVar.elementAt(
								i + 0).getVariable().toInt());
						priority802D1P.setQueueValue(responseVar.elementAt(
								i + 1).getVariable().toInt());
						priority802D1Ps.add(priority802D1P);
					}
				}
				return priority802D1Ps;
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

	public List<PriorityTOS> getTosPriority(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		Vector<String> oids = new Vector<String>();
		oids.add(OID.QOS_TOSPRIORITYVALUE);
		oids.add(OID.QOS_TOSQUEUENUMBER);
		PDU response = null;
		try {
			response = snmpV2.snmpGetBulk(oids, 0, 8);// TODO '8'
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			if (responseVar != null) {
				int vbsSize = responseVar.size();
				List<PriorityTOS> priorityTOSs = new ArrayList<PriorityTOS>();
				for (int i = 0; i < vbsSize; i++) {
					if (i % 2 == 0) {
						PriorityTOS priorityTOS = new PriorityTOS();
						priorityTOS.setPriorityLevel(responseVar.elementAt(
								i + 0).getVariable().toInt());
						priorityTOS.setQueueValue(responseVar.elementAt(i + 1)
								.getVariable().toInt());
						priorityTOSs.add(priorityTOS);
					}
				}
				return priorityTOSs;
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

	public List<PriorityDSCP> getDscpPriority(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(10 * 1000);
		Vector<String> oids = new Vector<String>();
		List<PriorityDSCP> priorityDSCPs = new ArrayList<PriorityDSCP>();
		PDU response = null;
		try {
			// When time out,may adjust the 'pagesize'
			int pagesize = 32;
			int priorityDSCPNum = 64;
			int mod = priorityDSCPNum % pagesize;
			int pages = priorityDSCPNum / pagesize;
			pages = mod == 0 ? pages : pages + 1;

			for (int i = 0; i < pages; i++) {
				int step = i * pagesize;
				oids.add(OID.QOS_DSCPPRIORITYVALUE + "." + step);
				oids.add(OID.QOS_DSCPQUEUENUMBER + "." + step);
				if (priorityDSCPNum - (i * pagesize) > mod) {
					response = snmpV2.snmpGetBulk(oids, 0, pagesize);
				} else {
					response = snmpV2.snmpGetBulk(oids, 0, mod);
				}
				Vector<VariableBinding> responseVar = checkResponseVar(response);
				if (responseVar != null) {
					int vbsSize = responseVar.size();
					for (int j = 0; j < vbsSize; j++) {
						if (j % 2 == 0) {
							PriorityDSCP priorityDSCP = new PriorityDSCP();
							priorityDSCP.setDscp(responseVar.elementAt(j + 0)
									.getVariable().toInt());
							priorityDSCP.setQueueValue(responseVar.elementAt(
									j + 1).getVariable().toInt());
							priorityDSCPs.add(priorityDSCP);
						}
					}
				}
				oids.clear();
			}
			return priorityDSCPs;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
		}
	}

	public boolean configStormControl(String ip,
			List<QOSStormControl> qosStormControls) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		PDU response = null;
		try {
			for (QOSStormControl qosStormControl : qosStormControls) {
				String typeStr = "";
				if (qosStormControl.isBroadcast()
						&& qosStormControl.isMutilUnicast()
						&& qosStormControl.isUnknowUnicast()) {
					typeStr = "all";// 全选
				} else if (!(qosStormControl.isBroadcast()
						|| qosStormControl.isMutilUnicast() || qosStormControl
						.isUnknowUnicast())) {
					typeStr = "none";// 全不选
				} else {
					String type = "";
					if (qosStormControl.isBroadcast()) {
						type = type + "bcast" + "_";
					}
					if (qosStormControl.isMutilUnicast()) {
						type = type + "mcast" + "_";
					}
					if (qosStormControl.isUnknowUnicast()) {
						type = type + "dlf" + "_";
					}

					type = type.substring(0, type.length() - 1);
					typeStr = type;
				}
				String percent = qosStormControl.getPercentNum() + "%";
				byte[] dataBuffer = dataBufferBuilder.stormControlConfig(
						qosStormControl.getPortNo(), percent, typeStr);
				response = snmpV2.snmpSet(OID.STORMCONTROL_BATCH_OPERATE,
						dataBuffer);
				if (!checkResponse(response)) {
					return false;
				}

				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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

	public boolean configStormControl_80series(String ip,
			List<QOSStormControl> qosStormControls) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(3 * 1000);
		PDU response = null;
		try {
			int mcast = 0;
			int bcast = 0;
			int dlf = 0;
			for (QOSStormControl qosStormControl : qosStormControls) {
				if (qosStormControl.isBroadcast()
						&& qosStormControl.isMutilUnicast()
						&& qosStormControl.isUnknowUnicast()) {
					mcast = 1;
					bcast = 1;
					dlf = 1;
				} else if (!(qosStormControl.isBroadcast()
						|| qosStormControl.isMutilUnicast() || qosStormControl
						.isUnknowUnicast())) {
				} else {
					if (qosStormControl.isBroadcast()) {
						bcast = 1;
					}
					if (qosStormControl.isMutilUnicast()) {
						mcast = 1;
					}
					if (qosStormControl.isUnknowUnicast()) {
						dlf = 1;
					}
				}
				break;
			}
			byte[] dataBuffer = dataBufferBuilder.stormControl_type_80series(
					mcast, bcast, dlf);
			response = snmpV2.snmpSet(OID.STORMCONTROL_BATCH_OPERATE_80SERIES,
					dataBuffer);
			if (!checkResponse(response)) {
				return false;
			}
			//
			for (QOSStormControl qosStormControl : qosStormControls) {
				dataBuffer = dataBufferBuilder.stormControl_rate_80series(
						qosStormControl.getPortNo(), qosStormControl
								.getPercentNum());
				response = snmpV2.snmpSet(
						OID.STORMCONTROL_BATCH_OPERATE_80SERIES, dataBuffer);
				if (!checkResponse(response)) {
					return false;
				}

				try {
					TimeUnit.MILLISECONDS.sleep(100);
				} catch (InterruptedException e) {
					log.error(e);
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

	public boolean configTrafficControl(String ip,
			List<QOSSpeedConfig> qosSpeedConfigs) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(4 * 1000);
		PDU response = null;
		// TODO
		try {
			for (QOSSpeedConfig qosSpeedConfig : qosSpeedConfigs) {
				int portId = qosSpeedConfig.getPortNo();
				boolean in_isStart = qosSpeedConfig.getInSpeed().isApplied();
				int ingress = 0;
				if (in_isStart) {
					ingress = 1;
				}
				String rxRate = "";
				if (qosSpeedConfig.getInSpeed().getUnit().equalsIgnoreCase(
						"mbps")) {
					rxRate = (int) qosSpeedConfig.getInSpeed().getSpeed()
							* 1024 + "K";
				} else {
					rxRate = (int) qosSpeedConfig.getInSpeed().getSpeed() + "K";
				}

				boolean out_isStart = qosSpeedConfig.getOutSpeed().isApplied();
				int egress = 0;
				if (out_isStart) {
					egress = 1;
				}
				String txRate = "";
				if (qosSpeedConfig.getOutSpeed().getUnit().equalsIgnoreCase(
						"mbps")) {
					txRate = (int) qosSpeedConfig.getOutSpeed().getSpeed()
							* 1024 + "K";
				} else {
					txRate = (int) qosSpeedConfig.getOutSpeed().getSpeed()
							+ "K";
				}
				byte[] dataBuffer = dataBufferBuilder.trafficControlConfig(
						portId, ingress, rxRate, egress, txRate);
				response = snmpV2.snmpSet(OID.TRAFFICCONTROL_BATCH_OPERATE,
						dataBuffer);
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

	public List<QOSStormControl> getStormControl(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		int portNum = portHandler.getPortNum(ip);
		if (portNum == 0) {
			return null;
		}
		snmpV2.setTimeout(3 * 1000);
		Vector<String> vbs = new Vector<String>();
		vbs.add(OID.STORMPORT);
		vbs.add(OID.STORM_TYPE);
		vbs.add(OID.STORM_LEVEL);
		PDU response = null;
		try {
			response = snmpV2.snmpGetBulk(vbs, 0, portNum);
			if (response != null
					&& SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
				Vector<VariableBinding> responseVar = response
						.getVariableBindings();
				int vbsSize = responseVar.size();
				List<QOSStormControl> stormList = new ArrayList<QOSStormControl>();
				for (int i = 0; i < vbsSize; i++) {
					// 3 is size of oid
					if (i % 3 == 0) {
						QOSStormControl qOSStormControl = new QOSStormControl();
						qOSStormControl.setPortNo(responseVar.elementAt(i + 0)
								.getVariable().toInt());

						String type = responseVar.elementAt(i + 1)
								.getVariable().toString().trim();
						if ("all".equalsIgnoreCase(type)) {
							qOSStormControl.setBroadcast(true);
							qOSStormControl.setMutilUnicast(true);
							qOSStormControl.setUnknowUnicast(true);
						} else if ("none".equalsIgnoreCase(type)) {
							qOSStormControl.setBroadcast(false);
							qOSStormControl.setMutilUnicast(false);
							qOSStormControl.setUnknowUnicast(false);
						} else if ("dlf".equalsIgnoreCase(type)) {
							qOSStormControl.setUnknowUnicast(true);
						} else if ("bcast".equalsIgnoreCase(type)) {
							qOSStormControl.setBroadcast(true);
						} else if ("mcast".equalsIgnoreCase(type)) {
							qOSStormControl.setMutilUnicast(true);
						} else if ("bcast_dlf".equalsIgnoreCase(type)) {
							qOSStormControl.setUnknowUnicast(true);
							qOSStormControl.setBroadcast(true);
						} else if ("mcast_dlf".equalsIgnoreCase(type)) {
							qOSStormControl.setUnknowUnicast(true);
							qOSStormControl.setMutilUnicast(true);
						} else if ("bcast_mcast".equalsIgnoreCase(type)) {
							qOSStormControl.setBroadcast(true);
							qOSStormControl.setMutilUnicast(true);
						}

						String level = responseVar.elementAt(i + 2)
								.getVariable().toString().trim();
						if ("3%".equalsIgnoreCase(level)) {
							qOSStormControl.setPercentNum(3);
						} else if ("5%".equalsIgnoreCase(level)) {
							qOSStormControl.setPercentNum(5);
						} else if ("10%".equalsIgnoreCase(level)) {
							qOSStormControl.setPercentNum(10);
						} else {
							qOSStormControl.setPercentNum(20);
						}
						stormList.add(qOSStormControl);
					}
				}
				return stormList;
			}
			return null;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
			vbs.clear();
		}
	}

	public List<QOSStormControl> getStormControl_80series(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		snmpV2.setTimeout(2 * 1000);
		PDU response = null;
		List<QOSStormControl> stormList = null;
		List<TableEvent> tableEventList = null;
		String[] columnOIDs = { OID.STORM_STORMIF_80SERIES,
				OID.STORM_IFRATE_80SERIES };
		int columnOIDLength = columnOIDs.length;
		try {
			response = snmpV2.snmpGet(OID.STORM_TYPE_80SERIES);
			Vector<VariableBinding> responseVar = checkResponseVar(response);
			String type = null;
			if (responseVar != null) {
				type = responseVar.elementAt(0).getVariable().toString();
			}
			if (type != null) {
				tableEventList = snmpV2
						.snmpTableDisplay(columnOIDs, null, null);
				if (tableEventList != null) {
					type = type.trim();
					String[] types = type.split(" ");
					stormList = new ArrayList<QOSStormControl>();
					for (TableEvent tableEvent : tableEventList) {
						VariableBinding[] variableBinding = tableEvent
								.getColumns();
						if (variableBinding == null) {
							continue;
						}
						int vbLength = variableBinding.length;
						for (int i = 0; i < vbLength; i++) {
							if (i % columnOIDLength == 0) {
								int portNo = variableBinding[i + 0]
										.getVariable().toInt();
								String ifRate = variableBinding[i + 1]
										.getVariable().toString();
								QOSStormControl qOSStormControl = new QOSStormControl();
								for (String deviceType : types) {
									if ("all".equalsIgnoreCase(deviceType)) {
										qOSStormControl.setBroadcast(true);
										qOSStormControl.setMutilUnicast(true);
										qOSStormControl.setUnknowUnicast(true);
									} else if ("none"
											.equalsIgnoreCase(deviceType)) {
										qOSStormControl.setBroadcast(false);
										qOSStormControl.setMutilUnicast(false);
										qOSStormControl.setUnknowUnicast(false);
									} else if ("dlf"
											.equalsIgnoreCase(deviceType)) {
										qOSStormControl.setUnknowUnicast(true);
									} else if ("bcast"
											.equalsIgnoreCase(deviceType)) {
										qOSStormControl.setBroadcast(true);
									} else if ("mcast"
											.equalsIgnoreCase(deviceType)) {
										qOSStormControl.setMutilUnicast(true);
									} else if ("bcast_dlf"
											.equalsIgnoreCase(deviceType)) {
										qOSStormControl.setUnknowUnicast(true);
										qOSStormControl.setBroadcast(true);
									} else if ("mcast_dlf"
											.equalsIgnoreCase(deviceType)) {
										qOSStormControl.setUnknowUnicast(true);
										qOSStormControl.setMutilUnicast(true);
									} else if ("bcast_mcast"
											.equalsIgnoreCase(deviceType)) {
										qOSStormControl.setBroadcast(true);
										qOSStormControl.setMutilUnicast(true);
									}
								}
								qOSStormControl.setPortNo(portNo);
								ifRate = ifRate.trim();
								String[] ifRates = ifRate.split(" ");
								if (ifRates.length > 1) {
									qOSStormControl.setPercentNum(Integer
											.parseInt(ifRates[0]));
									if ("M".equalsIgnoreCase(ifRates[1])) {
										qOSStormControl.setUnit("Mbps");
									} else {
										qOSStormControl.setUnit("Kbps");
									}
								}
								stormList.add(qOSStormControl);
							}
						}
					}
				}
			}
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return stormList;
		} finally {
			if (response != null) {
				response.clear();
			}
			if (tableEventList != null) {
				tableEventList.clear();
			}
		}
		return stormList;
	}

	public List<QOSSpeedConfig> getTrafficControl(String ip) {
		snmpV2.setAddress(ip, Constants.SNMP_PORT);
		snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
		int portNum = portHandler.getPortNum(ip);
		if (portNum == 0) {
			return null;
		}
		snmpV2.setTimeout(4 * 1000);
		Vector<String> vbs = new Vector<String>();
		vbs.add(OID.TFCPORT);
		vbs.add(OID.TFC_EGRESS);
		vbs.add(OID.TFC_TXRATE);
		vbs.add(OID.TFC_INGRESS);
		vbs.add(OID.TFC_RXRATE);
		PDU response = null;
		try {
			response = snmpV2.snmpGetBulk(vbs, 0, portNum);
			if (response != null
					&& SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
				Vector<VariableBinding> responseVar = response
						.getVariableBindings();
				int vbsSize = responseVar.size();
				List<QOSSpeedConfig> speedList = new ArrayList<QOSSpeedConfig>();
				for (int i = 0; i < vbsSize; i++) {
					// 5 is size of oid
					if (i % 5 == 0) {
						QOSSpeedConfig qOSSpeedConfig = new QOSSpeedConfig();
						qOSSpeedConfig.setPortNo(responseVar.elementAt(i + 0)
								.getVariable().toInt());
						SpeedEntity outSpeed = new SpeedEntity();
						if (ENABLED.equalsIgnoreCase(responseVar.elementAt(
								i + 1).getVariable().toString().trim())) {
							outSpeed.setApplied(true);
						} else {
							outSpeed.setApplied(false);
						}
						outSpeed.setFlowType(1);
						if (!isEmpty(responseVar.elementAt(i + 2).getVariable())) {
							String speed = responseVar.elementAt(i + 2)
									.getVariable().toString().trim();
							String unit = speed.substring(speed.length() - 1,
									speed.length());
							speed = speed.substring(0, speed.length() - 1)
									.trim();
							outSpeed.setSpeed(Float.parseFloat(speed.trim()));
							// TODO
							if (unit.equalsIgnoreCase("K")) {
								outSpeed.setUnit("kbps");
							} else {
								outSpeed.setUnit("mbps");
							}

						}

						SpeedEntity inSpeed = new SpeedEntity();
						if (ENABLED.equalsIgnoreCase(responseVar.elementAt(
								i + 3).getVariable().toString().trim())) {
							inSpeed.setApplied(true);
						} else {
							inSpeed.setApplied(false);
						}
						inSpeed.setFlowType(0);
						if (!isEmpty(responseVar.elementAt(i + 4).getVariable())) {
							String speed = responseVar.elementAt(i + 4)
									.getVariable().toString().trim();
							String unit = speed.substring(speed.length() - 1,
									speed.length());
							speed = speed.substring(0, speed.length() - 1)
									.trim();
							inSpeed.setSpeed(Float.parseFloat(speed.trim()));
							// TODO
							if (unit.equalsIgnoreCase("K")) {
								inSpeed.setUnit("kbps");
							} else {
								inSpeed.setUnit("mbps");
							}
						}
						qOSSpeedConfig.setOutSpeed(outSpeed);
						qOSSpeedConfig.setInSpeed(inSpeed);
						speedList.add(qOSSpeedConfig);
					}
				}
				return speedList;
			}
			return null;
		} catch (RuntimeException e) {
			log.error(getTraceMessage(e));
			return null;
		} finally {
			if (response != null) {
				response.clear();
			}
			vbs.clear();
		}

	}

	// public List<QOSSpeedConfig> getTrafficControl_80series(String ip) {
	// snmpV2.setAddress(ip, Constants.SNMP_PORT);
	// snmpV2.setCommunity(Constants.COMMUNITY_PRIVATE);
	// snmpV2.setTimeout(2 * 1000);
	//
	// String[] columnOIDs = { OID.TFCPORT, OID.TFC_EGRESS, OID.TFC_TXRATE,
	// OID.TFC_INGRESS, OID.TFC_RXRATE };
	// int columnOIDLength = columnOIDs.length;
	// List<TableEvent> tableEventList = null;
	// Map<String, String> lldpRemoteTable = null;
	// List<QOSSpeedConfig> speedList = null;
	// try {
	// tableEventList = snmpV2.snmpTableDisplay(columnOIDs, null, null);
	// if (tableEventList != null) {
	// speedList = new ArrayList<QOSSpeedConfig>();
	// for (TableEvent tableEvent : tableEventList) {
	// VariableBinding[] variableBinding = tableEvent.getColumns();
	// if (variableBinding == null) {
	// continue;
	// }
	// int vbLength = variableBinding.length;
	// for (int i = 0; i < vbLength; i++) {
	// if (i % columnOIDLength == 0) {
	// String lldpRemManAddr = variableBinding[i + 0]
	// .getVariable().toString();
	// String lldpRemLocalPortNum = variableBinding[i + 1]
	// .getVariable().toString();
	// String lldpRemPortId = variableBinding[i + 2]
	// .getVariable().toString();
	//
	// if (lldpRemoteTable.get(lldpRemManAddr) == null)
	// lldpRemoteTable.put(lldpRemManAddr,
	// lldpRemLocalPortNum + ","
	// + lldpRemPortId);
	// }
	// }
	// }
	// }
	// return lldpRemoteTable;
	// } catch (RuntimeException e) {
	// log.error(getTraceMessage(e));
	// return null;
	// } finally {
	// if (tableEventList != null) {
	// tableEventList.clear();
	// }
	// }
	//
	// int portNum = portHandler.getPortNum(ip);
	// if (portNum == 0) {
	// return null;
	// }
	// snmpV2.setTimeout(4 * 1000);
	// Vector<String> vbs = new Vector<String>();
	// vbs.add(OID.TFCPORT);
	// vbs.add(OID.TFC_EGRESS);
	// vbs.add(OID.TFC_TXRATE);
	// vbs.add(OID.TFC_INGRESS);
	// vbs.add(OID.TFC_RXRATE);
	// PDU response = null;
	// try {
	// response = snmpV2.snmpGetBulk(vbs, 0, portNum);
	// if (response != null
	// && SUCCESS.equalsIgnoreCase(response.getErrorStatusText())) {
	// Vector<VariableBinding> responseVar = response
	// .getVariableBindings();
	// int vbsSize = responseVar.size();
	// List<QOSSpeedConfig> speedList = new ArrayList<QOSSpeedConfig>();
	// for (int i = 0; i < vbsSize; i++) {
	// // 5 is size of oid
	// if (i % 5 == 0) {
	// QOSSpeedConfig qOSSpeedConfig = new QOSSpeedConfig();
	// qOSSpeedConfig.setPortNo(responseVar.elementAt(i + 0)
	// .getVariable().toInt());
	// SpeedEntity outSpeed = new SpeedEntity();
	// if (ENABLED.equalsIgnoreCase(responseVar.elementAt(
	// i + 1).getVariable().toString().trim())) {
	// outSpeed.setApplied(true);
	// } else {
	// outSpeed.setApplied(false);
	// }
	// outSpeed.setFlowType(1);
	// if (!isEmpty(responseVar.elementAt(i + 2).getVariable())) {
	// String speed = responseVar.elementAt(i + 2)
	// .getVariable().toString().trim();
	// String unit = speed.substring(speed.length() - 1,
	// speed.length());
	// speed = speed.substring(0, speed.length() - 1)
	// .trim();
	// outSpeed.setSpeed(Float.parseFloat(speed.trim()));
	// // TODO
	// if (unit.equalsIgnoreCase("K")) {
	// outSpeed.setUnit("kbps");
	// } else {
	// outSpeed.setUnit("mbps");
	// }
	//
	// }
	//
	// SpeedEntity inSpeed = new SpeedEntity();
	// if (ENABLED.equalsIgnoreCase(responseVar.elementAt(
	// i + 3).getVariable().toString().trim())) {
	// inSpeed.setApplied(true);
	// } else {
	// inSpeed.setApplied(false);
	// }
	// inSpeed.setFlowType(0);
	// if (!isEmpty(responseVar.elementAt(i + 4).getVariable())) {
	// String speed = responseVar.elementAt(i + 4)
	// .getVariable().toString().trim();
	// String unit = speed.substring(speed.length() - 1,
	// speed.length());
	// speed = speed.substring(0, speed.length() - 1)
	// .trim();
	// inSpeed.setSpeed(Float.parseFloat(speed.trim()));
	// // TODO
	// if (unit.equalsIgnoreCase("K")) {
	// inSpeed.setUnit("kbps");
	// } else {
	// inSpeed.setUnit("mbps");
	// }
	// }
	// qOSSpeedConfig.setOutSpeed(outSpeed);
	// qOSSpeedConfig.setInSpeed(inSpeed);
	// speedList.add(qOSSpeedConfig);
	// }
	// }
	// return speedList;
	// }
	// return null;
	// } catch (RuntimeException e) {
	// log.error(getTraceMessage(e));
	// return null;
	// } finally {
	// if (response != null) {
	// response.clear();
	// }
	// vbs.clear();
	// }
	//
	// }

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
