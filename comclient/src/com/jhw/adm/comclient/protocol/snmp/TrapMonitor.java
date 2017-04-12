package com.jhw.adm.comclient.protocol.snmp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.snmp4j.CommandResponderEvent;
import org.snmp4j.PDU;
import org.snmp4j.PDUv1;
import org.snmp4j.smi.VariableBinding;

import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.comclient.service.SystemHandler;
import com.jhw.adm.comclient.service.topology.TopologyHandler;
import com.jhw.adm.comclient.service.topology.epon.EponHandler;
import com.jhw.adm.comclient.service.upgrade.SystemRestartInter;
import com.jhw.adm.comclient.system.Configuration;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.warning.TrapWarningEntity;

/**
 * Trap事件监听
 * 
 * @author xiongbo
 * 
 */
public class TrapMonitor implements Monitor {
	private static Logger log = Logger.getLogger(TrapMonitor.class);
	private Trap snmpTrap;

	private MessageSend messageSend;
	private SystemHandler systemHandler;
	private EponHandler eponHandler;
	private TopologyHandler topologyHandler;
	private SystemRestartInter systemRestartInter;

	private final Pattern ptnSpecificType = Pattern
			.compile("\\s*port\\s*(\\d)\\s*(\\S*)\\s*\\[\\s*(\\d+)\\s*");

	private LinkedList<CommandResponderEvent> trapQueue = new LinkedList<CommandResponderEvent>();
	private String localIp;
	private Map<String, String> vbsMap = new HashMap<String, String>();

	private Map<String, TrapWarningEntity> layer2Trap = new ConcurrentHashMap<String, TrapWarningEntity>();
	private Map<String, TrapWarningEntity> layer2TrapFirst = new ConcurrentHashMap<String, TrapWarningEntity>();
	private Map<Integer, String> trapTypeCorrespond = new HashMap<Integer, String>(
			4);
	private final String snmpTrapOID = "1.3.6.1.6.3.1.1.4.1.0";
	private final String snmpTrapEnterpriseOID = "1.3.6.1.6.3.1.1.4.3.0";
	private final String layer2Flag = "1.3.6.1.4.1.44405";
	private final String OLT_3Layer_Flag = "1.3.6.1.4.1.44405";
	private final String rmonTrap = "1.3.6.1.4.1.44405";

	private final String coldStart = "1.3.6.1.6.3.1.1.5.1";
	private final String linkDown = "1.3.6.1.6.3.1.1.5.3";
	private final String linkUp = "1.3.6.1.6.3.1.1.5.4";

	private final String ifDescrOID = "1.3.6.1.2.1.2.2.1.2";
	private boolean consumeLoop = false;
	private boolean layer2HandleLoop = false;
	private Date rmon_last_time;

	public void startTrap() {
		snmpTrap.deliverTrap(this);
		snmpTrap.start();
		// Start consume
		consumeLoop = true;
		new ConsumeTrap().start();
		//
		getLocalIp();
		//
		layer2HandleLoop = true;
		new Layer2Trap().start();
		//
		trapTypeCorrespond.put(0, "冷启动");
		trapTypeCorrespond.put(1, "热启动");
		trapTypeCorrespond.put(2, "断开");
		trapTypeCorrespond.put(3, "连接上");
	}

	private void getLocalIp() {
		try {
			localIp = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void stopTrap() {
		snmpTrap.stop();
		consumeLoop = false;
		layer2HandleLoop = false;
		trapTypeCorrespond.clear();
	}

	@Override
	public void handleTrap(CommandResponderEvent e) {
		synchronized (trapQueue) {
			if (trapQueue.add(e)) {

				trapQueue.notify();
			}
		}
	}

	class ConsumeTrap extends Thread {
		public void run() {
			try {
				CommandResponderEvent e = null;
				while (consumeLoop) {
					synchronized (trapQueue) {
						while (trapQueue.size() == 0) {
							log.warn("TrapQueue empty!");
							trapQueue.wait();
						}
						e = trapQueue.removeFirst();
					}
					doHandle(e);
				}
			} catch (InterruptedException e) {
				log.error(e);
			}
		}
	}

	class Layer2Trap extends Thread {
		public void run() {
			try {
				while (layer2HandleLoop) {
					String removeKeys = "";
					Date date = new Date();
					for (String key : layer2Trap.keySet()) {
						TrapWarningEntity trapWarningEntity = layer2Trap
								.get(key);
						int interSeconds = (int) (date.getTime() - trapWarningEntity
								.getSampleTime().getTime()) / 1000;
						if (interSeconds > 3) {
							TrapWarningEntity firstTrapWarningEntity = layer2TrapFirst
									.get(key);
							if (firstTrapWarningEntity != null) {
								int firstWarningType = firstTrapWarningEntity
										.getWarningEvent();
								if (trapWarningEntity.getWarningEvent() != firstWarningType) {
									removeKeys += key + ",";

									// Send JMS
									messageSend.sendObjectMessage(
											trapWarningEntity.getDeviceType(),
											trapWarningEntity,
											MessageNoConstants.TRAPMESSAGE,
											localIp, null);

									log.info("******Send Layer2-after filter: "
											+ trapWarningEntity.getIpValue()
											+ " "
											+ trapWarningEntity.getPortNo()
											+ "端口 "
											+ trapTypeCorrespond
													.get(trapWarningEntity
															.getWarningEvent())
									// + " Time: "
											// + trapWarningEntity
											// .getSampleTime()
											);

								}
							}
						}
						if (interSeconds > 5) {
							removeKeys += key + ",";
						}
					}

					String[] removesStr = removeKeys.split(",");
					if (removesStr != null) {
						for (String removeKey : removesStr) {
							if (!"".equals(removeKey)) {
								layer2Trap.remove(removeKey);
								layer2TrapFirst.remove(removeKey);
								// log.warn("***Remove Layer2 Trap Key:"
								// + removeKey);
								log.info("layer2TrapFirst.size:"
										+ layer2TrapFirst.size()
										+ " layer2Trap.size:"
										+ layer2Trap.size());
							}
						}
					}

					TimeUnit.MILLISECONDS.sleep(1500);
				}
			} catch (InterruptedException e) {
				log.error(e);
			}
		}
	}

	private void doHandle(CommandResponderEvent e) {
		if (e == null || e.getPDU() == null) {
			return;
		}

		TrapWarningEntity trapWarningEntity = new TrapWarningEntity();
		trapWarningEntity.setCurrentStatus(Constants.NEW);
		String peerIp = getIp(e);
		trapWarningEntity.setIpValue(peerIp);
		trapWarningEntity.setSampleTime(new Date());

		vbsMap.clear();

		PDU pdu = e.getPDU();
		Vector<VariableBinding> responseVbs = pdu.getVariableBindings();
		String temp = null;
		// String tempMOXA = null;
		for (int i = 0; i < responseVbs.size(); i++) {
			VariableBinding vb = responseVbs.elementAt(i);
			String oid = vb.getOid().toString();
			String value = vb.getVariable().toString();
			log.info(oid + " : " + value);
			vbsMap.put(oid, value);
			if (i == 2) {
				temp = value;
			}
			// if (i == 0) {
			// tempMOXA = value;
			// }
		}
		log.info("***Trap IP:" + peerIp);
		String interfaceState = vbsMap.get(snmpTrapOID);

		if (interfaceState != null) {
			// Rmon Trap
			if (interfaceState.startsWith(rmonTrap)) {
				trapWarningEntity.setWarningEvent(Constants.REMONTHING);
				String desc = vbsMap.get(interfaceState);
				if (desc != null) {
					trapWarningEntity.setDeviceType(Constants.DEV_SWITCHER2);
					matcherSpecificType(trapWarningEntity, desc);
					trapWarningEntity.setDescs(desc);
					log.info("***Receive Rmon trap");
					if (rmon_last_time == null
							|| (((int) (new Date().getTime() - rmon_last_time
									.getTime()) / 1000) >= Configuration.rmon_report_interval)) {
						rmon_last_time = new Date();
						messageSend
								.sendObjectMessage(trapWarningEntity
										.getDeviceType(), trapWarningEntity,
										MessageNoConstants.TRAPMESSAGE,
										localIp, peerIp);
						log.info("******Send Rmon Trap");
					}
				}
				trapWarningEntity = null;
				return;
			}

			if ((linkDown.equals(interfaceState)
					|| linkUp.equals(interfaceState) || coldStart
					.equals(interfaceState))
					&& temp != null) {// 二层,三层,OLT

				String enterpriseTrap = vbsMap.get(snmpTrapEnterpriseOID);
				// Two Layer
				if (enterpriseTrap != null
						&& enterpriseTrap.startsWith(layer2Flag)) {

					trapWarningEntity.setDeviceType(Constants.DEV_SWITCHER2);
					if (linkDown.equals(interfaceState)) {
						trapWarningEntity.setWarningEvent(Constants.LINKDOWN);
					} else if (linkUp.equals(interfaceState)) {
						trapWarningEntity.setWarningEvent(Constants.LINKUP);
					} else if (coldStart.equals(interfaceState)) {
						trapWarningEntity.setWarningEvent(Constants.COLDSTART);
					}
					trapWarningEntity.setPortType(Constants.TX);
					if (trapWarningEntity.getWarningEvent() != Constants.COLDSTART) {
						trapWarningEntity.setPortNo(Integer.parseInt(temp));
					}
					if (trapWarningEntity.getWarningEvent() != Constants.COLDSTART) {
						trapWarningEntity.setDescs("Port "
								+ trapWarningEntity.getPortNo());
					} else {
						trapWarningEntity.setDescs("冷启动");
					}
					if (trapWarningEntity.getWarningEvent() != Constants.COLDSTART) {
						String key = trapWarningEntity.getIpValue() + ":"
								+ trapWarningEntity.getPortNo();
						if (!layer2Trap.containsKey(key)) {
							layer2TrapFirst.put(key, trapWarningEntity);
							// Send JMS
							messageSend.sendObjectMessage(trapWarningEntity
									.getDeviceType(), trapWarningEntity,
									MessageNoConstants.TRAPMESSAGE, localIp,
									peerIp);
							log.info("******Send Layer2-first:"
									+ trapWarningEntity.getIpValue()
									+ " "
									+ trapWarningEntity.getPortNo()
									+ "端口 "
									+ trapTypeCorrespond.get(trapWarningEntity
											.getWarningEvent())
							// + " Time: "
									// + trapWarningEntity.getSampleTime()
									);
						}
						layer2Trap.put(key, trapWarningEntity);
						// log.info("***Save Layer2 Trap Flag");
					} else {
						// Send JMS
						messageSend
								.sendObjectMessage(trapWarningEntity
										.getDeviceType(), trapWarningEntity,
										MessageNoConstants.TRAPMESSAGE,
										localIp, peerIp);
						log.info("******Send Layer2 Trap-Coldstart:"
								+ trapWarningEntity.getIpValue() + " "
								+ trapWarningEntity.getWarningEvent()
						// + " Time: "
								// + trapWarningEntity.getSampleTime()
								);
					}
					trapWarningEntity = null;
					return;
				}

				// KYLAND
				if (vbsMap.size() == 3) {
					trapWarningEntity.setDeviceType(Constants.DEV_SWITCHER2);
					if (linkDown.equals(interfaceState)) {
						trapWarningEntity.setWarningEvent(Constants.LINKDOWN);
					} else if (linkUp.equals(interfaceState)) {
						trapWarningEntity.setWarningEvent(Constants.LINKUP);
					} else if (coldStart.equals(interfaceState)) {
						trapWarningEntity.setWarningEvent(Constants.COLDSTART);
					}
					trapWarningEntity.setPortType(Constants.TX);
					if (trapWarningEntity.getWarningEvent() != Constants.COLDSTART) {
						trapWarningEntity.setPortNo(Integer.parseInt(temp));
					}
					if (trapWarningEntity.getWarningEvent() != Constants.COLDSTART) {
						trapWarningEntity.setDescs("Port "
								+ trapWarningEntity.getPortNo());
					} else {
						trapWarningEntity.setDescs("冷启动");
					}
					messageSend.sendObjectMessage(trapWarningEntity
							.getDeviceType(), trapWarningEntity,
							MessageNoConstants.TRAPMESSAGE, localIp, peerIp);
					log.info("******Layer2 send:"
							+ trapWarningEntity.getIpValue()
							+ " "
							+ trapTypeCorrespond.get(trapWarningEntity
									.getWarningEvent()));
					trapWarningEntity = null;
					return;
				}

				// 3Layer or OLT
				if (enterpriseTrap != null
						&& enterpriseTrap.startsWith(OLT_3Layer_Flag)) {
					String ifDescr = vbsMap.get(ifDescrOID + "." + temp);

					log.info("***3Layer or OLT trap portDesc:" + ifDescr);
					if (ifDescr == null || ifDescr.startsWith("VLAN")) {
						return;
					}

					// 3Layer
					if (checkBDdeviceType(enterpriseTrap,
							Configuration.layer3_type)) {
						// String peerMac = eponHandler.getMac(
						// trapWarningEntity.getIpValue(),
						// Configuration.three_layer_community, false);
						String peerMac = eponHandler.getLayer3Mac(
								trapWarningEntity.getIpValue(),
								Configuration.three_layer_community);
						if (peerMac == null) {
							log.warn("***3Layer mac is NULL");
							return;

						}
						log.info("***3Layer mac: " + peerMac);
						// Re-assignment
						trapWarningEntity.setIpValue(peerMac);

						trapWarningEntity
								.setDeviceType(Constants.DEV_SWITCHER3);
						if (linkDown.equals(interfaceState)) {
							trapWarningEntity
									.setWarningEvent(Constants.LINKDOWN);
						} else if (linkUp.equals(interfaceState)) {
							trapWarningEntity.setWarningEvent(Constants.LINKUP);
						}
						trapWarningEntity.setPortType(Constants.TX);

						if (ifDescr.startsWith("Fas")
								|| ifDescr.startsWith("Gig")) {
							int position = ifDescr.indexOf("/");
							int slot = Integer.parseInt(ifDescr.substring(
									position - 1, position).trim());
							int port = Integer.parseInt(ifDescr.substring(
									position + 1).trim());
							trapWarningEntity.setSlotNum(slot);
							trapWarningEntity.setPortNo(port);
							trapWarningEntity.setDescs(ifDescr);
							if (ifDescr.startsWith("Fas")) {
								trapWarningEntity
										.setPortType(com.jhw.adm.server.entity.util.Constants.FAST_PORT);
								trapWarningEntity.setDescs(ifDescr);
							} else {
								trapWarningEntity
										.setPortType(com.jhw.adm.server.entity.util.Constants.GIGA_PORT);
								trapWarningEntity.setDescs(ifDescr);
							}
						} else {
							trapWarningEntity.setPortNo(Integer.parseInt(temp));

							trapWarningEntity.setDescs(ifDescr);
						}
						// Send JMS
						messageSend.sendObjectMessage(trapWarningEntity
								.getDeviceType(), trapWarningEntity,
								MessageNoConstants.TRAPMESSAGE, localIp,
								peerMac);
						log.info("******Send Layer3 Trap:"
								+ trapWarningEntity.getIpValue()
								+ " "
								+ trapWarningEntity.getSlotNum()
								+ "插槽 "
								+ trapWarningEntity.getPortNo()
								+ "端口 "
								+ trapTypeCorrespond.get(trapWarningEntity
										.getWarningEvent()));
						trapWarningEntity = null;
						return;
					}

					// OLT or ONU
					if (ifDescr.startsWith("EPON")
							|| checkBDdeviceType(enterpriseTrap,
									Configuration.olt_type)) {
						int position = ifDescr.indexOf("/");
						int additional = ifDescr.indexOf(":");
						if (additional != -1) {
							trapWarningEntity.setDeviceType(Constants.DEV_ONU);
						} else {
							trapWarningEntity.setDeviceType(Constants.DEV_OLT);

						}
						if (linkDown.equals(interfaceState)) {
							trapWarningEntity
									.setWarningEvent(Constants.LINKDOWN);
						} else if (linkUp.equals(interfaceState)) {
							trapWarningEntity.setWarningEvent(Constants.LINKUP);
						}

						if (ifDescr.startsWith("E")) {
							trapWarningEntity.setPortType(Constants.PX);
						} else {
							trapWarningEntity.setPortType(Constants.TX);
						}

						if (position != -1) {
							int slot = Integer.parseInt(ifDescr.substring(
									position - 1, position).trim());
							int port = 0;
							int sequenceNo = 0;
							if (additional != -1) {
								port = Integer.parseInt(ifDescr.substring(
										position + 1, additional).trim());

								sequenceNo = Integer.parseInt(ifDescr
										.substring(additional + 1).trim());
							} else {
								port = Integer.parseInt(ifDescr.substring(
										position + 1).trim());
							}
							trapWarningEntity.setSlotNum(slot);
							trapWarningEntity.setPortNo(port);
							// trapWarningEntity.setDescs(port + "端口");
							trapWarningEntity.setDescs(ifDescr);
							trapWarningEntity.setOnuSequenceNo(sequenceNo);
							String onuMac = null;
							if (additional != -1) {
								onuMac = eponHandler.getOnuMac(peerIp,
										Configuration.olt_community, ifDescr,
										sequenceNo);
							}
							log.info("***onuMac:" + onuMac + " " + ifDescr);
							trapWarningEntity.setWarnOnuMac(onuMac);
						}

						// Send JMS
						messageSend
								.sendObjectMessage(trapWarningEntity
										.getDeviceType(), trapWarningEntity,
										MessageNoConstants.TRAPMESSAGE,
										localIp, peerIp);
						log.info("******Send OLT Trap:"
								+ trapWarningEntity.getIpValue()
								+ " "
								+ trapWarningEntity.getSlotNum()
								+ "插槽 "
								+ trapWarningEntity.getPortNo()
								+ "端口 "
								+ trapTypeCorrespond.get(trapWarningEntity
										.getWarningEvent()));
						// trapWarningEntity = null;
						// return;
					}
					trapWarningEntity = null;
					return;
				}

				// Other device，such as KYLAND and Virtual Net Element
				{
					trapWarningEntity.setDeviceType(Constants.DEV_SWITCHER2);
					if (linkDown.equals(interfaceState)) {
						trapWarningEntity.setWarningEvent(Constants.LINKDOWN);
					} else if (linkUp.equals(interfaceState)) {
						trapWarningEntity.setWarningEvent(Constants.LINKUP);
					} else if (coldStart.equals(interfaceState)) {
						trapWarningEntity.setWarningEvent(Constants.COLDSTART);
					}
					trapWarningEntity.setPortType(Constants.TX);
					if (trapWarningEntity.getWarningEvent() != Constants.COLDSTART) {
						trapWarningEntity.setPortNo(Integer.parseInt(temp));
					}
					if (trapWarningEntity.getWarningEvent() != Constants.COLDSTART) {
						trapWarningEntity.setDescs("Port "
								+ trapWarningEntity.getPortNo());
					} else {
						trapWarningEntity.setDescs("冷启动");
					}
					messageSend.sendObjectMessage(trapWarningEntity
							.getDeviceType(), trapWarningEntity,
							MessageNoConstants.TRAPMESSAGE, localIp, peerIp);
					log.info("******<<KYLAND and Virtual Net Element>> send:"
							+ trapWarningEntity.getIpValue()
							+ " "
							+ trapTypeCorrespond.get(trapWarningEntity
									.getWarningEvent()));
					trapWarningEntity = null;
				}
			}
		} else {
			// MOXA
			// if (vbsMap.size() == 2
			// && vbsMap.containsKey("1.3.6.1.2.1.2.2.1.1." + tempMOXA)) {
			// trapWarningEntity.setDeviceType(Constants.DEV_SWITCHER2);
			//
			// Object obj = topologyHandler.getSingleNode(peerIp, "public",
			// "1.3.6.1.2.1.2.2.1.8." + tempMOXA);
			// if (obj == null) {
			// return;
			// }
			// int state = 1;
			// try {
			// state = Integer.parseInt(obj.toString());
			// } catch (NumberFormatException e1) {
			// }
			// if (state == 2) {
			// trapWarningEntity.setWarningType(Constants.LINKDOWN);
			// } else if (state == 1) {
			// trapWarningEntity.setWarningType(Constants.LINKUP);
			// }
			// trapWarningEntity.setPortType(Constants.TX);
			// trapWarningEntity.setPortNo(Integer.parseInt(tempMOXA));
			// trapWarningEntity.setDescs("百兆端口"
			// + trapWarningEntity.getPortNo());
			// messageSend.sendObjectMessage(
			// trapWarningEntity.getDeviceType(), trapWarningEntity,
			// MessageNoConstants.TRAPMESSAGE, localIp, peerIp);
			// } else if (vbsMap.size() == 0) {
			// SystemInfoEntity systemInfoEntity = topologyHandler
			// .getSystemInfo(peerIp, "public");
			// if (systemInfoEntity != null
			// && systemInfoEntity.getSysObjectID().trim()
			// .indexOf(Configuration.moxa_objectid) != -1) {
			// trapWarningEntity.setDeviceType(Constants.DEV_SWITCHER2);
			// trapWarningEntity.setWarningType(Constants.COLDSTART);
			// trapWarningEntity.setDescs("冷启动");
			// messageSend.sendObjectMessage(
			// trapWarningEntity.getDeviceType(),
			// trapWarningEntity, MessageNoConstants.TRAPMESSAGE,
			// localIp, peerIp);
			// }
			// }

			String pduString = pdu.toString();
			if (pduString.startsWith("V1TRAP")) {// SNMP V1
				PDUv1 pduV1 = (PDUv1) pdu;

				// String enterprise = pduV1.getEnterprise().toString();
				// log.info("enterprise: " + enterprise);
				// if (enterprise.trim().indexOf(Configuration.moxa_objectid) !=
				// -1) {

				trapWarningEntity.setDeviceType(Constants.DEV_SWITCHER2);
				int genericTrap = pduV1.getGenericTrap();
				if (genericTrap == 0) {
					trapWarningEntity.setWarningEvent(Constants.COLDSTART);
					trapWarningEntity.setDescs(trapTypeCorrespond
							.get(trapWarningEntity.getWarningEvent()));
				} else if (genericTrap == 1) {
					trapWarningEntity.setWarningEvent(Constants.WARMSTART);
					trapWarningEntity.setDescs(trapTypeCorrespond
							.get(trapWarningEntity.getWarningEvent()));
				} else if (genericTrap == 2) {
					trapWarningEntity.setWarningEvent(Constants.LINKDOWN);
					Vector<VariableBinding> vbs = pduV1.getVariableBindings();
					VariableBinding vbPort = vbs.get(0);
					// VariableBinding vbDesc = vbs.get(1);
					trapWarningEntity.setPortNo(vbPort.getVariable().toInt());
					trapWarningEntity.setDescs("Port "
							+ trapWarningEntity.getPortNo());
				} else if (genericTrap == 3) {
					trapWarningEntity.setWarningEvent(Constants.LINKUP);
					Vector<VariableBinding> vbs = pduV1.getVariableBindings();
					VariableBinding vbPort = vbs.get(0);
					// VariableBinding vbDesc = vbs.get(1);
					trapWarningEntity.setPortNo(vbPort.getVariable().toInt());
					trapWarningEntity.setDescs("Port "
							+ trapWarningEntity.getPortNo());
				}
				messageSend.sendObjectMessage(
						trapWarningEntity.getDeviceType(), trapWarningEntity,
						MessageNoConstants.TRAPMESSAGE, localIp, peerIp);

				log.info("******SNMP <<V1>> device:"
						+ trapWarningEntity.getIpValue()
						+ " "
						+ trapTypeCorrespond.get(trapWarningEntity
								.getWarningEvent()));
				trapWarningEntity = null;
				// }
			}

		}
	}

	private boolean checkBDdeviceType(String trapType, String typeConstant) {
		String[] types = typeConstant.split(",");
		boolean flag = false;
		for (String type : types) {
			if (trapType.indexOf(type) != -1) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	private void matcherSpecificType(TrapWarningEntity trapWarningEntity,
			String content) {
		try {
			Matcher matcher = ptnSpecificType.matcher(content);
			if (matcher.find()) {
				trapWarningEntity.setPortNo(Integer.parseInt(matcher.group(1)));
				trapWarningEntity.setParamName(matcher.group(2));
				trapWarningEntity.setValue(Long.parseLong((matcher.group(3))));
			}
		} catch (NumberFormatException e) {
			log.error(e);
		}
	}

	private String getIp(CommandResponderEvent e) {
		String peerIp = e.getPeerAddress().toString();
		peerIp = peerIp.substring(0, peerIp.indexOf("/"));
		return peerIp;
	}

	public void registSystemRestartInter(SystemRestartInter systemRestartInter) {
		this.systemRestartInter = systemRestartInter;
	}

	// Spring inject
	public Trap getSnmpTrap() {
		return snmpTrap;
	}

	public void setSnmpTrap(Trap snmpTrap) {
		this.snmpTrap = snmpTrap;
	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

	public SystemHandler getSystemHandler() {
		return systemHandler;
	}

	public void setSystemHandler(SystemHandler systemHandler) {
		this.systemHandler = systemHandler;
	}

	public EponHandler getEponHandler() {
		return eponHandler;
	}

	public void setEponHandler(EponHandler eponHandler) {
		this.eponHandler = eponHandler;
	}

	public TopologyHandler getTopologyHandler() {
		return topologyHandler;
	}

	public void setTopologyHandler(TopologyHandler topologyHandler) {
		this.topologyHandler = topologyHandler;
	}

}
