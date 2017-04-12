package com.jhw.adm.comclient.service.topology;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.Message;

import org.apache.log4j.Logger;
import org.shortpasta.icmp.IcmpPingResponse;
import org.shortpasta.icmp.IcmpUtil;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.jhw.adm.comclient.jms.MessageHandleIF;
import com.jhw.adm.comclient.jms.MessageQueueHandler;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.comclient.protocol.snmp.AbstractSnmp;
import com.jhw.adm.comclient.service.BaseService;
import com.jhw.adm.comclient.service.GhringHandler;
import com.jhw.adm.comclient.service.LldpHandler;
import com.jhw.adm.comclient.service.MacHandler;
import com.jhw.adm.comclient.service.PortHandler;
import com.jhw.adm.comclient.service.SerialHandler;
import com.jhw.adm.comclient.service.SystemHandler;
import com.jhw.adm.comclient.service.topology.epon.EponHandler;
import com.jhw.adm.comclient.service.topology.epon.SlotPort;
import com.jhw.adm.comclient.system.Configuration;
import com.jhw.adm.comclient.util.Tool;
import com.jhw.adm.server.entity.epon.OLTBaseInfo;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.epon.OLTPort;
import com.jhw.adm.server.entity.epon.ONUEntity;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.level3.SwitchPortLevel3;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.nets.IPSegment;
import com.jhw.adm.server.entity.ports.LLDPInofEntity;
import com.jhw.adm.server.entity.ports.RingPortInfo;
import com.jhw.adm.server.entity.ports.SwitchPortEntity;
import com.jhw.adm.server.entity.switchs.SwitchBaseConfig;
import com.jhw.adm.server.entity.switchs.SwitchBaseInfo;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.switchs.SwitchRingInfo;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.SwitchSerialPort;

/**
 * 拓扑发现
 * 
 * @author xiongbo
 * 
 * 
 *         The Class handle discovery topology.
 * 
 *         1.Node discovery
 * 
 *         2.Subnet discovery
 * 
 */
public class TopologyService extends BaseService {
	private static Logger log = Logger.getLogger(TopologyService.class);
	private SystemHandler systemHandler;
	private PortHandler portHandler;
	private LldpHandler lldpHandler;
	private MacHandler macHandler;
	private GhringHandler ghringHandler;
	private TopologyHandler topologyHandler;
	private SerialHandler serialHandler;
	private EponHandler eponHandler;
	//
	private AbstractSnmp snmpV2;
	private MessageSend messageSend;
	//
	private String client;
	private String clientIp;
	//
	private String startIp;
	private String SUCCESS = "Success";

	private final Pattern ipPattern = Pattern
			.compile("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");

	private MessageQueueHandler messageQueueHandler;

	public void init() {
		messageQueueHandler.addHandler(MessageNoConstants.TOPOSEARCH, topoHandle);
	}

	private MessageHandleIF topoHandle = new MessageHandleIF() {

		@Override
		public void doHandle(String ip, String client, String clientIp, Message message) {
			startTopology(client, clientIp);
		}
	};

	public void startTopology(String client, String clientIp) {
		this.client = client;
		this.clientIp = clientIp;

		FEPEntity fepEntity = getTopologyParameter();
		String startTopologyIp = null;
		List<IPSegment> subnetworkList = null;
		if (fepEntity != null) {
			startTopologyIp = fepEntity.getDirectSwitchIp();
			subnetworkList = fepEntity.getSegment();
		}

		List<String> segmentAllIPs = new ArrayList<String>();
		// Only for switch2 temporarily
		Map<String, Integer> segmentAllIPsMap = new HashMap<String, Integer>();
		if (subnetworkList != null && startTopologyIp != null) {
			this.startIp = startTopologyIp;
			log.info("Start ip:" + startTopologyIp);

			for (IPSegment ipSegment : subnetworkList) {

				String beginIp = ipSegment.getBeginIp();
				String endIp = ipSegment.getEndIp();
				log.info("Ip Segment:" + beginIp + " - " + endIp);
				String[] beginIps = beginIp.split("\\.");
				String[] endIps = endIp.split("\\.");
				int begin = Integer.parseInt(beginIps[3]);
				int end = Integer.parseInt(endIps[3]);
				for (; begin <= end; begin++) {
					segmentAllIPs.add(beginIps[0] + "." + beginIps[1] + "." + beginIps[2] + "." + begin);
					segmentAllIPsMap.put(beginIps[0] + "." + beginIps[1] + "." + beginIps[2] + "." + begin, 0);
				}
			}
		}

		// Delete JRE system exception file
		// Iterator itFile = FileUtils.iterateFiles(new File("."),
		// new String[] { "log" }, false);
		// while (itFile.hasNext()) {
		// File file = (File) itFile.next();
		// boolean result = file.delete();
		// if (!result) {
		// log.warn("Failed to delete file of :" + file);
		// }
		// }

		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(Configuration.topology_threadpool_count,
				Configuration.topology_threadpool_count, 0L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
		log.info("TOPOLOGY_THREADPOOL_COUNT >>> " + Configuration.topology_threadpool_count);
		Map<String, Integer> dectectIPResultMap = new ConcurrentHashMap<String, Integer>();
		log.info("ICMP dectecting......");
		for (int i = 0; i < Configuration.ping_count; i++) {
			detectIP(segmentAllIPs, threadPoolExecutor, dectectIPResultMap);
		}
		log.info("ICMP dectect finish -> " + dectectIPResultMap.size());
		String localIP = null;
		try {
			localIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			log.error(e);
		}

		dectectIPResultMap.remove(localIP);

		Map<String, Integer> layer2Ip = new HashMap<String, Integer>();
		Map<String, Integer> portWeatherUse = new HashMap<String, Integer>();
		Map<String, Integer> layer3Mac = new HashMap<String, Integer>();
		Map<String, String> layer3IpAndIps = new HashMap<String, String>();
		List<SwitchLayer3> switch3List = new ArrayList<SwitchLayer3>();
		List<SwitchNodeEntity> switch2List = new ArrayList<SwitchNodeEntity>();
		// Avoid more than one OLT
		Map<String, String> oltMap = new HashMap<String, String>();
		Map<String, SlotPort> oltStpMap = new HashMap<String, SlotPort>();
		// Only one olt as usually,just use that FEP derecte the olt
		List<OLTEntity> oltList = new ArrayList<OLTEntity>();
		//
		Map<String, Integer> usedIp = new HashMap<String, Integer>();

		mainDiscovery(startTopologyIp, segmentAllIPsMap, usedIp, layer2Ip, portWeatherUse, layer3Mac, layer3IpAndIps,
				switch3List, switch2List, oltMap, oltList, oltStpMap);

		for (String ip : segmentAllIPs) {
			if (usedIp.containsKey(ip)) {
				continue;
			}
			if (!dectectIPResultMap.containsKey(ip)) {
				// log.info("new add dectectIPResultMap: " + ip);
				dectectIPResultMap.put(ip, 0);
			}
		}

		// Dectect separate network element or device of no lldp
		Map<String, Integer> dectectSnmpResultMap = new ConcurrentHashMap<String, Integer>();
		log.info("Snmp dectecting......");
		int snmpVersion = SnmpConstants.version2c;
		for (int i = 0; i < Configuration.snmp_count; i++) {
			if (i > 1) {// if value of 'i' be large 1, then use version1
				snmpVersion = SnmpConstants.version1;
			}
			this.detectSnmp(dectectIPResultMap, threadPoolExecutor, dectectSnmpResultMap, snmpVersion);
		}
		log.info("Snmp dectect finish -> " + dectectSnmpResultMap.size());
		for (String ip : dectectSnmpResultMap.keySet()) {
			mainDiscovery(ip, segmentAllIPsMap, usedIp, layer2Ip, portWeatherUse, layer3Mac, layer3IpAndIps,
					switch3List, switch2List, oltMap, oltList, oltStpMap);
		}

		threadPoolExecutor.shutdown();
		dectectIPResultMap.clear();
		dectectSnmpResultMap.clear();
		//
		segmentAllIPs.clear();
		segmentAllIPsMap.clear();
		layer3Mac.clear();
		layer2Ip.clear();
		portWeatherUse.clear();
		layer3IpAndIps.clear();
		usedIp.clear();
		//
		oltMap.clear();
		oltStpMap.clear();

		sleepBeforeEnd();
		messageSend.sendTextMessageRes("发现拓扑结束", SUCCESS, MessageNoConstants.TOPOFINISH, Configuration.fepCode, client,
				clientIp);
		log.info("***拓扑发现结束***");

	}

	private void mainDiscovery(String startTopologyIp, Map<String, Integer> segmentAllIPsMap,
			Map<String, Integer> usedIp, Map<String, Integer> layer2Ip, Map<String, Integer> portWeatherUse,
			Map<String, Integer> layer3Mac, Map<String, String> layer3IpAndIps, List<SwitchLayer3> switch3List,
			List<SwitchNodeEntity> switch2List, Map<String, String> oltMap, List<OLTEntity> oltList,
			Map<String, SlotPort> oltStpMap) {
		this.recursiveDiscovery(startTopologyIp, segmentAllIPsMap, layer3Mac, layer3IpAndIps, switch3List, switch2List,
				layer2Ip, oltMap, portWeatherUse, oltList);
		log.info("Finish discovery, now Arrangeing data......");
		for (SwitchLayer3 switch3 : switch3List) {
			if (checkNotIP(switch3.getIpValue())) {
				continue;
			}
			if (Configuration.layer3_loopback == 1) {
				// log.info("switch3.getIpValue():" + switch3.getIpValue());
				String loopbackIp = topologyHandler.getOspfRouterId(switch3.getIpValue(),
						Configuration.three_layer_community);
				if (loopbackIp != null) {
					Matcher matcher = ipPattern.matcher(loopbackIp);
					if (matcher.matches()) {
						switch3.setIpValue(loopbackIp);
					}
				}
			}
			Set<LLDPInofEntity> lldpSet = switch3.getLldps();
			if (lldpSet != null) {
				for (LLDPInofEntity lldpInfo : lldpSet) {
					String remoteIp = lldpInfo.getRemoteIP();
					if (checkNotIP(remoteIp)) {
						continue;
					}
					if (remoteIp != null) {
						int type = 0;
						if (!layer3IpAndIps.containsKey(remoteIp)) {
							// log.info("remoteIp:" + remoteIp);
							type = eponHandler.getDeviceType(remoteIp, Configuration.three_layer_community);
							if (type == 3) {
								for (String layer3Ip : layer3IpAndIps.keySet()) {
									String ips = layer3IpAndIps.get(layer3Ip);
									if (ips.indexOf(remoteIp) != -1) {
										lldpInfo.setRemoteIP(layer3Ip);
									}
								}
							}
						} else {
							type = 3;
						}
						if (Configuration.layer3_loopback == 1) {

							lldpInfo.setLocalIP(switch3.getIpValue());
							if (type == 3) {
								// log.info("lldpInfo.getRemoteIP():"
								// + lldpInfo.getRemoteIP());
								if (!checkNotIP(lldpInfo.getRemoteIP())) {
									String remoteLoopbackIp = topologyHandler.getOspfRouterId(lldpInfo.getRemoteIP(),
											Configuration.three_layer_community);
									if (remoteLoopbackIp != null) {
										Matcher matcher = ipPattern.matcher(remoteLoopbackIp);
										if (matcher.matches()) {
											lldpInfo.setRemoteIP(remoteLoopbackIp);
										}
									}
								}
							}
						}
					}
				}
			}

			messageSend.sendObjectMessageRes(com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER3, switch3, SUCCESS,
					MessageNoConstants.TOPONODE, "3Layer", client, clientIp);
			log.info("发现三层: " + switch3.getMacValue() + " " + switch3.getIpValue());
			//
			usedIp.put(switch3.getIpValue(), 0);
			String ips = layer3IpAndIps.get(switch3.getIpValue());
			if (ips != null) {
				String[] temps = ips.split(",");
				for (String temp : temps) {
					usedIp.put(temp, 0);
				}
			}
			//
			// Discovery OLT
			Map<String, String> ipNetToMediaMap = topologyHandler
					.getIpNetToMediaTableWithTableFilter(switch3.getIpValue(), Configuration.three_layer_community);
			if (ipNetToMediaMap != null) {
				//
				Map<String, Integer> filterIp = new HashMap<String, Integer>();
				for (SwitchLayer3 switch3Iter : switch3List) {
					// log.info("1:" + switch3Iter.getIpValue());
					Set<LLDPInofEntity> lldpSetIter = switch3Iter.getLldps();
					for (LLDPInofEntity lldpFor : lldpSetIter) {
						// log.info("11:" + lldpFor.getRemoteIP());
						filterIp.put(lldpFor.getRemoteIP(), 0);
					}
					filterIp.put(switch3Iter.getIpValue(), 0);
				}
				for (SwitchNodeEntity switch2Iter : switch2List) {
					// log.info("2:" +
					// switch2Iter.getBaseConfig().getIpValue());
					Set<LLDPInofEntity> lldpSetIter = switch2Iter.getLldpinfos();
					for (LLDPInofEntity lldpFor : lldpSetIter) {
						// log.info("22:" + lldpFor.getRemoteIP());
						filterIp.put(lldpFor.getRemoteIP(), 0);
					}
					filterIp.put(switch2Iter.getBaseConfig().getIpValue(), 0);
				}
				//
				Map<String, Integer> dot1qTpFdbTableMap = null;
				for (String physAddress : ipNetToMediaMap.keySet()) {
					String netAddress = ipNetToMediaMap.get(physAddress);
					if (filterIp.containsKey(netAddress)) {
						log.info(netAddress + " jump");
						continue;
					}
					int type = eponHandler.getDeviceType(netAddress, Configuration.three_layer_community);
					if (type == 31 || type == 21) {
						if (dot1qTpFdbTableMap == null) {
							log.info("Read dot1qTpFdbTable for Discoverying OLT.");
							dot1qTpFdbTableMap = topologyHandler.getDot1qTpFdbTable(switch3.getIpValue(),
									Configuration.three_layer_community);// TODO
						}
						if (dot1qTpFdbTableMap != null) {
							if (dot1qTpFdbTableMap.containsKey(physAddress)) {
								int portIndex = dot1qTpFdbTableMap.get(physAddress);

								String lldpLocPortDesc = topologyHandler.getLLDPLocPortDesc(switch3.getIpValue(),
										Configuration.three_layer_community, portIndex);
								if (lldpLocPortDesc != null) {
									int position = lldpLocPortDesc.indexOf("/");
									int layer3Slot = Integer
											.parseInt(lldpLocPortDesc.substring(position - 1, position).trim());
									int layer3Port = Integer.parseInt(lldpLocPortDesc.substring(position + 1).trim());
									int layer3PortType = com.jhw.adm.server.entity.util.Constants.TX;
									if (lldpLocPortDesc.startsWith("Fas") || lldpLocPortDesc.startsWith("Gig")) {
										layer3PortType = com.jhw.adm.server.entity.util.Constants.TX;
									} else {
										layer3PortType = com.jhw.adm.server.entity.util.Constants.FX;
									}

									if (oltMap.containsKey(netAddress)) {
										// check oltList
										boolean flag = true;
										for (OLTEntity oltEntity : oltList) {
											if (netAddress.equals(oltEntity.getIpValue())) {
												log.info("Check OLT(" + netAddress + ") whether setuped.");
												if (oltEntity.getLldpinfos() == null) {
													flag = false;
													log.info("OLT(" + netAddress
															+ ") have on LLDP info, so Go on MSTP handle.");
												}
												break;
											}
										}
										if (flag) {
											log.info("OLT(" + netAddress
													+ ") have LLDP info, so Interrupt/Or the OLT have been Discovery.");
											continue;
										}
									}
									if (segmentAllIPsMap.containsKey(netAddress)) {
										oltMap.put(netAddress, physAddress);
										discoveryEpon(netAddress, switch3.getIpValue(), switch3.getMacValue(),
												layer3Slot, layer3Port, layer3PortType,
												com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER3, oltStpMap,
												oltMap);
										//
										usedIp.put(netAddress, 0);
										//
									} else {
										log.info("*** OLT " + netAddress + "-" + physAddress + " be Excluded");
									}
								}

							}
						}
					}
				}
				if (dot1qTpFdbTableMap != null) {
					dot1qTpFdbTableMap.clear();
				}
				ipNetToMediaMap.clear();
				filterIp.clear();
			}
			// OLT end
		}
		for (SwitchNodeEntity switch2 : switch2List) {
			Set<LLDPInofEntity> lldpSet = switch2.getLldpinfos();
			if (lldpSet != null) {
				for (LLDPInofEntity lldpInfo : lldpSet) {
					String remoteIp = lldpInfo.getRemoteIP();
					if (checkNotIP(remoteIp)) {
						continue;
					}
					if (remoteIp != null) {
						int type = 0;
						if (!layer3IpAndIps.containsKey(remoteIp)) {
							type = eponHandler.getDeviceType(remoteIp, Configuration.three_layer_community);
							if (type == 3) {
								for (String layer3Ip : layer3IpAndIps.keySet()) {
									String ips = layer3IpAndIps.get(layer3Ip);
									if (ips.indexOf(remoteIp) != -1) {
										lldpInfo.setRemoteIP(layer3Ip);
									}
								}
							}
						} else {
							type = 3;
						}
						if (Configuration.layer3_loopback == 1) {
							if (type == 3) {
								if (!checkNotIP(lldpInfo.getRemoteIP())) {
									String remoteLoopbackIp = topologyHandler.getOspfRouterId(lldpInfo.getRemoteIP(),
											Configuration.three_layer_community);
									if (remoteLoopbackIp != null) {
										Matcher matcher = ipPattern.matcher(remoteLoopbackIp);
										if (matcher.matches()) {
											lldpInfo.setRemoteIP(remoteLoopbackIp);
										}
									}
								}
							}
						}
					}
				}
			}

			messageSend.sendObjectMessageRes(com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2, switch2, SUCCESS,
					MessageNoConstants.TOPONODE, switch2.getBaseInfo().getMacValue(), client, clientIp);
			log.info("发现二层: " + switch2.getBaseConfig().getIpValue());
			//
			usedIp.put(switch2.getBaseConfig().getIpValue(), 0);
			//
		}

		for (OLTEntity oltEntity : oltList) {
			String oltIP = oltEntity.getIpValue();
			// log.info("234543534523454355%%$$1");

			Set<LLDPInofEntity> lldpinfoSets = oltEntity.getLldpinfos();
			if (lldpinfoSets != null) {
				// continue;
				// }
				// log.info("234543534523454355%%$$12");
				for (LLDPInofEntity lldpInofEntity : lldpinfoSets) {
					String remoteIP = lldpInofEntity.getRemoteIP();
					// log.info("remoteIP：" + remoteIP + " " + lldpInofEntity);
					for (String layer3Ip : layer3IpAndIps.keySet()) {
						String ips = layer3IpAndIps.get(layer3Ip);
						if (ips.indexOf(remoteIP) != -1) {
							lldpInofEntity.setRemoteIP(layer3Ip);
							// log.info("ips：" + ips);
						} // else {
							// log.info("ips：" + ips);
							// }
					}
				}
			} else {
				log.info("OLT(" + oltIP + ") have no LLDP info.");
			}
			usedIp.put(oltIP, 0);
			// log.info("234543534523454355%%$$2");
			Object[] objArray = eponHandler.getOltPortStateTable(oltIP, Configuration.olt_community);
			if (objArray != null) {

				// continue;
				// }
				Set<OLTPort> oltPortSet = (Set<OLTPort>) objArray[1];
				oltEntity.setPorts(oltPortSet);
				if (oltPortSet != null) {
					for (OLTPort oltPort : oltPortSet) {
						oltPort.setOltEntity(oltEntity);
					}
				}
			} else {
				log.info("OLT(" + oltIP + ") have no Port info.");
			}
			// log.info("234543534523454355%%$$3");
			OLTBaseInfo oltBaseInfo = eponHandler.getOltBaseInfo(oltIP, Configuration.olt_community);
			oltEntity.setOltBaseInfo(oltBaseInfo);
			oltBaseInfo.setOltEntity(oltEntity);
			int deviceModel = com.jhw.adm.server.entity.util.DeviceType.IEL3010_HV;
			if (oltBaseInfo.getDeviceName() != null) {
				String deviceName = Tool.removeBlank(oltBaseInfo.getDeviceName());
				if ("IEL3010-HV".equalsIgnoreCase(deviceName) || deviceName.indexOf("3010") != -1) {
					deviceModel = com.jhw.adm.server.entity.util.DeviceType.IEL3010_HV;
				} else if ("8506".equalsIgnoreCase(deviceName) || deviceName.indexOf("8506") != -1) {
					deviceModel = com.jhw.adm.server.entity.util.DeviceType.EPON8506;
				} else if ("8000".equalsIgnoreCase(deviceName) || deviceName.indexOf("8000") != -1) {
					deviceModel = com.jhw.adm.server.entity.util.DeviceType.EPON8506;
				}
			}
			oltEntity.setDeviceModel(deviceModel);
			// log.info("234543534523454355%%$$4");
			log.info("发现 OLT:" + oltIP);
			messageSend.sendObjectMessageRes(com.jhw.adm.server.entity.util.Constants.DEV_OLT, oltEntity, SUCCESS,
					MessageNoConstants.TOPONODE, "OLT", client, clientIp);

			HashSet<ONUEntity> onuEntitySet = eponHandler.getOnu(oltIP, Configuration.olt_community);
			if (onuEntitySet != null) {
				// log.info("发现 ONU.");
				for (ONUEntity onu : onuEntitySet) {
					log.info("发现 ONU：" + onu.getMacValue());
					Set<LLDPInofEntity> onuLLDPset = onu.getLldpinfos();
					if (onuLLDPset != null) {
						for (LLDPInofEntity onuLLP : onuLLDPset) {
							log.info("ONU lldp remote：" + onuLLP.getRemoteIP());
						}
					}
				}
				messageSend.sendObjectMessageRes(com.jhw.adm.server.entity.util.Constants.DEV_ONU, onuEntitySet,
						SUCCESS, MessageNoConstants.TOPONODE, "ONU", client, clientIp);
			}
			// log.info("234543534523454355%%$$5");
			// } else {
			// log.info("*** OLT " + oltIP + " be Excluded");
			// }

			//
		}
		switch3List.clear();
		switch2List.clear();
		oltList.clear();
	}

	private void detectIP(List<String> sourceIPList, final ThreadPoolExecutor threadPoolExecutor,
			final Map<String, Integer> dectectIPResult) {
		for (final String ip : sourceIPList) {
			if (dectectIPResult.containsKey(ip)) {
				continue;
			}
			final Runnable runnable = new Runnable() {
				public void run() {
					IcmpPingResponse icmpPingResponse = null;
					try {
						icmpPingResponse = IcmpUtil.executeIcmpPingRequest(ip, 32, 1000);
					} catch (InterruptedException e) {
						log.error(e);
					}
					if (icmpPingResponse != null && icmpPingResponse.getResponseIpAddress() != null) {
						dectectIPResult.put(ip, 0);
					}
				}
			};
			threadPoolExecutor.execute(runnable);
		}
		try {
			while (threadPoolExecutor.getActiveCount() > 0) {
				TimeUnit.MILLISECONDS.sleep(399);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("<<<detect IP thread finish>>>");
	}

	private void detectSnmp(Map<String, Integer> dectectIPResult, final ThreadPoolExecutor threadPoolExecutor,
			final Map<String, Integer> dectectSnmpResult, final int snmpVersion) {
		for (final String ip : dectectIPResult.keySet()) {
			if (dectectSnmpResult.containsKey(ip)) {
				continue;
			}
			final Runnable runnable = new Runnable() {
				public void run() {
					Snmp snmp = null;
					try {
						snmp = new Snmp(new DefaultUdpTransportMapping());
						CommunityTarget target = new CommunityTarget();
						target.setCommunity(new OctetString("public"));
						target.setTimeout(1000);
						target.setVersion(snmpVersion);
						snmp.listen();

						PDU pdu = new PDU();
						target.setAddress(new UdpAddress(ip + "/161"));
						pdu.setType(PDU.GET);

						// pdu.add(new VariableBinding(new OID(new int[] { 1, 3,
						// 6, 1, 2, 1, 1, 1, 0 })));// sysDescr
						pdu.add(new VariableBinding(new OID(new int[] { 1, 3, 6, 1, 2, 1, 1, 3, 0 })));// sysUpTime
						// pdu.add(new VariableBinding(new OID(new int[] { 1, 3,
						// 6, 1, 2, 1, 1, 4, 0 })));// sysContact

						ResponseEvent responseEvent = snmp.send(pdu, target);
						if (responseEvent != null) {
							if (responseEvent.getPeerAddress() != null) {
								dectectSnmpResult.put(ip, 0);
							}
						}

					} catch (IOException e) {
						log.error(e);
					} finally {
						if (snmp != null) {
							try {
								snmp.close();
							} catch (IOException e) {
								log.error(e);
							}
						}
					}

				}
			};
			threadPoolExecutor.execute(runnable);
		}

		try {
			while (threadPoolExecutor.getActiveCount() > 0) {
				TimeUnit.MILLISECONDS.sleep(399);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("<<<detect snmp thread finish>>>");
	}

	private boolean checkNotIP(String ip) {
		return ip == null || !ipPattern.matcher(ip).matches();
	}

	private void recursiveDiscovery(String topologyIp, Map<String, Integer> segmentAllIPsMap,
			Map<String, Integer> layer3Mac, Map<String, String> layer3IpAndIps, List<SwitchLayer3> switch3List,
			List<SwitchNodeEntity> switch2List, Map<String, Integer> layer2Ip, Map<String, String> oltMap,
			Map<String, Integer> portWeatherUse, List<OLTEntity> oltList) {
		if (topologyIp == null || !ipPattern.matcher(topologyIp).matches()) {
			return;
		}
		int type = eponHandler.getDeviceType(topologyIp, Configuration.three_layer_community);
		if (type == 3) {// Layer 3
			String layer3mac = eponHandler.getLayer3Mac(topologyIp, Configuration.three_layer_community);
			if (layer3mac != null) {
				if (layer3Mac.containsKey(layer3mac)) {
					return;
				}
				layer3Mac.put(layer3mac, 0);
				String ips = topologyHandler.getAddrTableWithTable(topologyIp, Configuration.three_layer_community);
				layer3IpAndIps.put(topologyIp, ips);
				Map<String, String> layer3LLDPTable = topologyHandler.getLayer3LLDPTable(topologyIp,
						Configuration.three_layer_community);
				if (layer3LLDPTable != null) {
					Set<SwitchPortLevel3> switchPortLevel3Set = new HashSet<SwitchPortLevel3>();
					Set<LLDPInofEntity> layer3LLDPSet = new HashSet<LLDPInofEntity>();
					for (String layer3RemoteIp : layer3LLDPTable.keySet()) {
						// log.info("Remote ip: " + layer3RemoteIp);
						String value = layer3LLDPTable.get(layer3RemoteIp);
						String[] values = value.split(",");
						String localPortId = values[0];
						String remotePort = values[1];
						String lldpLocPort = topologyHandler.getLLDPLocPortDesc(topologyIp,
								Configuration.three_layer_community, Integer.parseInt(localPortId));
						if (lldpLocPort != null) {
							// log.info("lldpLocPortDesc:" + lldpLocPort);
							int position = lldpLocPort.indexOf("/");
							int localSlot = Integer.parseInt(lldpLocPort.substring(position - 1, position).trim());
							int localPort = Integer.parseInt(lldpLocPort.substring(position + 1).trim());

							position = remotePort.indexOf("/");
							int peerSlot = 0;
							int peerPort = 0;
							if (remotePort.startsWith("Fas") || remotePort.startsWith("Gig")) {
								position = remotePort.indexOf("/");
								peerSlot = Integer.parseInt(remotePort.substring(position - 1, position).trim());
								peerPort = Integer.parseInt(remotePort.substring(position + 1).trim());
							} else {
								peerPort = Integer.parseInt(remotePort);
							}
							SwitchPortLevel3 switchPortLevel3 = new SwitchPortLevel3();
							String locPortAddr = topologyHandler.getLLDPLocPortAddr(topologyIp,
									Configuration.three_layer_community, Integer.parseInt(localPortId));
							switchPortLevel3.setIpValue(locPortAddr);
							if (locPortAddr == null) {
								switchPortLevel3.setIpValue(topologyIp);
							}
							switchPortLevel3.setSlot(localSlot);
							switchPortLevel3.setPortNo(localPort);
							if (!"192.168.0.1".equals(switchPortLevel3.getIpValue())
									&& !"0.0.0.0".equals(switchPortLevel3.getIpValue())
									&& !"127.0.0.1".equals(switchPortLevel3.getIpValue())
									&& !"127.0.0.0".equals(switchPortLevel3.getIpValue())) {
								switchPortLevel3Set.add(switchPortLevel3);
							}

							LLDPInofEntity lldpInofEntity = new LLDPInofEntity();
							lldpInofEntity.setLocalDeviceType(com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER3);
							lldpInofEntity.setLocalIP(topologyIp);
							lldpInofEntity.setLocalSlot(localSlot);
							lldpInofEntity.setLocalPortNo(localPort);

							if (lldpLocPort.startsWith("Fas")) {
								switchPortLevel3.setPortType(com.jhw.adm.server.entity.util.Constants.FAST_PORT);
								lldpInofEntity.setLocalPortType(com.jhw.adm.server.entity.util.Constants.FAST_PORT);
							} else {
								switchPortLevel3.setPortType(com.jhw.adm.server.entity.util.Constants.GIGA_PORT);
								lldpInofEntity.setLocalPortType(com.jhw.adm.server.entity.util.Constants.GIGA_PORT);
							}

							lldpInofEntity.setRemoteIP(layer3RemoteIp);

							if (remotePort.startsWith("Fas")) {
								lldpInofEntity.setRemotePortType(com.jhw.adm.server.entity.util.Constants.FAST_PORT);
								// TODO
								lldpInofEntity
										.setRemoteDeviceType(com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER3);
							} else if (remotePort.startsWith("Gig")) {
								lldpInofEntity.setRemotePortType(com.jhw.adm.server.entity.util.Constants.GIGA_PORT);
								lldpInofEntity
										.setRemoteDeviceType(com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER3);
							} else {
								lldpInofEntity.setRemotePortType(com.jhw.adm.server.entity.util.Constants.FAST_PORT);
								lldpInofEntity
										.setRemoteDeviceType(com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2);
							}

							lldpInofEntity.setRemoteSlot(peerSlot);
							lldpInofEntity.setRemotePortNo(peerPort);

							String localUse = lldpInofEntity.getLocalIP() + ","
							// + lldpInofEntity.getLocalPortType() + ","
									+ lldpInofEntity.getLocalSlot() + "," + lldpInofEntity.getLocalPortNo();
							String remoteUse = lldpInofEntity.getRemoteIP()
									// + "," +
									// lldpInofEntity.getRemotePortType()
									+ "," + lldpInofEntity.getRemoteSlot() + "," + lldpInofEntity.getRemotePortNo();

							// log.info("外:"
							// + lldpInofEntity.getLocalIP() // +
							// // "-P:"
							// // + lldpInofEntity.getLocalPortType()
							// // + "-S:"
							// + "-" + lldpInofEntity.getLocalSlot()
							// + "-"
							// + +lldpInofEntity.getLocalPortNo()
							// + " "
							// + lldpInofEntity.getRemoteIP()// + "-P:"
							// // + lldpInofEntity.getRemotePortType()
							// // + "-S:"
							// + "-" + lldpInofEntity.getRemoteSlot()
							// + "-" + +lldpInofEntity.getRemotePortNo());
							if (portWeatherUse.get(localUse) == null && portWeatherUse.get(remoteUse) == null
									&& (!lldpInofEntity.getLocalIP().equals(lldpInofEntity.getRemoteIP()))) {
								portWeatherUse.put(localUse, 0);
								portWeatherUse.put(remoteUse, 0);

								layer3LLDPSet.add(lldpInofEntity);

								// log.info("内:"
								// + lldpInofEntity.getLocalIP()
								// + "-"
								// // + "-P:"
								// // + lldpInofEntity.getLocalPortType()
								// // + "-S:"
								// + lldpInofEntity.getLocalSlot()
								// + "-"
								// + +lldpInofEntity.getLocalPortNo()
								// + " "
								// + lldpInofEntity.getRemoteIP()
								// + "-"
								// // + "-P:"
								// // + lldpInofEntity.getRemotePortType()
								// // + "-S:"
								// + lldpInofEntity.getRemoteSlot() + "-"
								// + +lldpInofEntity.getRemotePortNo());
							}

						}
					}

					SwitchLayer3 base = topologyHandler.get3LayerBase(topologyIp, Configuration.three_layer_community);
					SwitchLayer3 switchLayer3 = new SwitchLayer3();
					int model = com.jhw.adm.server.entity.util.DeviceType.IETH3424;
					if (base != null) {
						switchLayer3.setName(base.getName());
						if (switchLayer3.getName() != null) {
							String name = Tool.removeBlank(switchLayer3.getName());
							if ("IETH3424".equalsIgnoreCase(name) || name.indexOf("3424") != -1) {
								model = com.jhw.adm.server.entity.util.DeviceType.IETH3424;
							}
							// ......
						}
					}
					switchLayer3.setDeviceModel(model);
					switchLayer3.setIpValue(topologyIp);
					switchLayer3.setMacValue(layer3mac);
					switchLayer3.setPorts(switchPortLevel3Set);
					switchLayer3.setLldps(layer3LLDPSet);

					switch3List.add(switchLayer3);

					for (String layer3RemoteIp : layer3LLDPTable.keySet()) {
						recursiveDiscovery(layer3RemoteIp, segmentAllIPsMap, layer3Mac, layer3IpAndIps, switch3List,
								switch2List, layer2Ip, oltMap, portWeatherUse, oltList);
					}
				}
			}
		} else if (type == 2) {// Layer 2
			if (!segmentAllIPsMap.containsKey(topologyIp)) {
				return;
			}
			if (layer2Ip.containsKey(topologyIp)) {
				return;
			}
			layer2Ip.put(topologyIp, 0);
			SwitchNodeEntity switchNodeEntity = new SwitchNodeEntity();
			getSwitchParameterInfo(topologyIp, switchNodeEntity);

			if (checkEntityNotNull(switchNodeEntity)) {
				switch2List.add(switchNodeEntity);
			} else {
				// if (startIp.equals(topologyIp)) {
				// messageSend.sendTextMessageRes("跟前置机直接相连的交换机之间的链路异常", FAIL,
				// MessageNoConstants.FEPSWITCHNETEXCEPTION,
				// topologyIp, client, clientIp);
				// log
				// .warn("Directly connected to the switch with the front-end
				// link between the abnormal");
				// } else {
				log.warn("When discovery: " + topologyIp + "Out Time");
				// }
				return;
			}

			if (switchNodeEntity.getLldpinfos() == null) {
				return;
			}
			for (LLDPInofEntity lldpInofEntity : switchNodeEntity.getLldpinfos()) {
				recursiveDiscovery(lldpInofEntity.getRemoteIP(), segmentAllIPsMap, layer3Mac, layer3IpAndIps,
						switch3List, switch2List, layer2Ip, oltMap, portWeatherUse, oltList);
			}
		} else if (type == 21 || type == 31) {// Olt
			// 能进入此分支，说明OLT开启LLDP协议，或独立发现的OLT(即没有开启LLDP和MSTP，或通过SNMP Detect)
			if (!segmentAllIPsMap.containsKey(topologyIp)) {
				return;
			}
			// log.info("234543534523454355%%$$6");
			if (oltMap.containsKey(topologyIp)) {
				return;
			}
			oltMap.put(topologyIp, "");
			// log.info("234543534523454355%%$$7");
			log.info("OLT setuped LLDP protocol when discovery.");
			Map<String, String> oltLLDPTable = topologyHandler.getLayer3LLDPTable(topologyIp,
					Configuration.three_layer_community);
			Set<LLDPInofEntity> oltLLDPSet = null;
			if (oltLLDPTable != null) {
				oltLLDPSet = new HashSet<LLDPInofEntity>();
				for (String oltRemoteIp : oltLLDPTable.keySet()) {
					String value = oltLLDPTable.get(oltRemoteIp);
					String[] values = value.split(",");
					String localPortId = values[0];
					String remotePort = values[1];
					String lldpLocPort = topologyHandler.getLLDPLocPortDesc(topologyIp,
							Configuration.three_layer_community, Integer.parseInt(localPortId));
					if (lldpLocPort != null) {
						// log.info("234543534523454355%%$$8");
						int position = lldpLocPort.indexOf("/");
						int localSlot = Integer.parseInt(lldpLocPort.substring(position - 1, position).trim());
						int localPort = Integer.parseInt(lldpLocPort.substring(position + 1).trim());

						position = remotePort.indexOf("/");
						int peerSlot = 0;
						int peerPort = 0;
						if (remotePort.startsWith("Fas") || remotePort.startsWith("Gig")) {
							position = remotePort.indexOf("/");
							peerSlot = Integer.parseInt(remotePort.substring(position - 1, position).trim());
							peerPort = Integer.parseInt(remotePort.substring(position + 1).trim());
						} else {
							peerPort = Integer.parseInt(remotePort);
						}
						//
						LLDPInofEntity lldpInofEntity = new LLDPInofEntity();
						lldpInofEntity.setLocalDeviceType(com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER3);
						lldpInofEntity.setLocalIP(topologyIp);
						lldpInofEntity.setLocalSlot(localSlot);
						lldpInofEntity.setLocalPortNo(localPort);

						if (lldpLocPort.startsWith("Fas")) {
							// switchPortLevel3
							// .setPortType(com.jhw.adm.server.entity.util.Constants.FAST_PORT);
							lldpInofEntity.setLocalPortType(com.jhw.adm.server.entity.util.Constants.FAST_PORT);
						} else {
							// switchPortLevel3
							// .setPortType(com.jhw.adm.server.entity.util.Constants.GIGA_PORT);
							lldpInofEntity.setLocalPortType(com.jhw.adm.server.entity.util.Constants.GIGA_PORT);
						}

						lldpInofEntity.setRemoteIP(oltRemoteIp);

						if (remotePort.startsWith("Fas")) {
							lldpInofEntity.setRemotePortType(com.jhw.adm.server.entity.util.Constants.FAST_PORT);
							// TODO
							lldpInofEntity.setRemoteDeviceType(com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER3);
						} else if (remotePort.startsWith("Gig")) {
							lldpInofEntity.setRemotePortType(com.jhw.adm.server.entity.util.Constants.GIGA_PORT);
							lldpInofEntity.setRemoteDeviceType(com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER3);
						} else {
							lldpInofEntity.setRemotePortType(com.jhw.adm.server.entity.util.Constants.FAST_PORT);
							lldpInofEntity.setRemoteDeviceType(com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER3);
						}

						lldpInofEntity.setRemoteSlot(peerSlot);
						lldpInofEntity.setRemotePortNo(peerPort);
						// log.info("234543534523454355%%$$9");
						oltLLDPSet.add(lldpInofEntity);
					}
				}
			}

			OLTEntity oltEntity = new OLTEntity();
			oltEntity.setIpValue(topologyIp);
			oltEntity.setLldpinfos(oltLLDPSet);
			oltList.add(oltEntity);
			// log.info("234543534523454355%%$$10");
			if (oltEntity.getLldpinfos() == null) {
				return;
			}
			// log.info("234543534523454355%%$$11");
			for (LLDPInofEntity lldpInofEntity : oltEntity.getLldpinfos()) {
				recursiveDiscovery(lldpInofEntity.getRemoteIP(), segmentAllIPsMap, layer3Mac, layer3IpAndIps,
						switch3List, switch2List, layer2Ip, oltMap, portWeatherUse, oltList);
			}
		}

	}

	private int checkDeviceType(int ipForwarding, String sysDescr) {
		if (sysDescr == null) {
			return 1;
		}
		if (sysDescr.indexOf("\n") != -1) {
			String[] desc = sysDescr.split("\n");
			if (desc != null) {
				String[] temp = desc[1].split(" ");
				sysDescr = temp[0];
			}
		}
		String oltType = Configuration.olt_type;
		String[] oltTypes = oltType.split(",");
		boolean flag = false;
		for (String otype : oltTypes) {
			if (sysDescr.indexOf(otype) != -1) {
				flag = true;
				break;
			}
		}
		if (ipForwarding == 1) {
			// 8506 is big EPON
			// 3010 is small OLT
			if (flag) {
				return 31;
			} else {
				return 3;
			}
		} else if (ipForwarding == 2) {
			if (flag) {
				return 21;
			} else {
				return 2;
			}
		}
		return 1;
	}

	private void discoveryEpon(String oltIP, String layer3Ip, String layer3Mac, int layer3Slot, int layer3Port,
			int layer3PortType, int layer3Type, Map<String, SlotPort> oltStpMap, Map<String, String> oltMap) {

		Object[] objArray = eponHandler.getOltPortStateTable(oltIP, Configuration.olt_community);
		// Map<String, Dot1dStpPortTable> compareMap = null;
		if (objArray != null) {
			SlotPort oltSlotPort = (SlotPort) objArray[0];
			Set<OLTPort> oltPortSet = (Set<OLTPort>) objArray[1];
			//
			stpRelationHandle(oltIP, layer3Mac, oltSlotPort, oltStpMap);
			//
			OLTEntity oltEntity = new OLTEntity();
			oltEntity.setIpValue(oltIP);
			oltEntity.setPorts(oltPortSet);
			if (oltPortSet != null) {
				for (OLTPort oltPort : oltPortSet) {
					oltPort.setOltEntity(oltEntity);
				}
			}

			Set<LLDPInofEntity> lldpinfos = null;
			if (layer3Ip != null && layer3Port != 0) {
				lldpinfos = new HashSet<LLDPInofEntity>();

				LLDPInofEntity lldpInofEntity = new LLDPInofEntity();
				lldpInofEntity.setLocalDeviceType(com.jhw.adm.server.entity.util.Constants.DEV_OLT);
				lldpInofEntity.setLocalIP(oltIP);
				lldpInofEntity.setLocalSlot(oltSlotPort.getSlot());
				lldpInofEntity.setLocalPortNo(oltSlotPort.getPort());
				lldpInofEntity.setLocalPortType(oltSlotPort.getPortType());
				lldpInofEntity.setRemoteDeviceType(layer3Type);
				lldpInofEntity.setRemoteIP(layer3Ip);
				lldpInofEntity.setRemoteSlot(layer3Slot);
				lldpInofEntity.setRemotePortNo(layer3Port);
				lldpInofEntity.setRemotePortType(layer3PortType);

				lldpinfos.add(lldpInofEntity);
			}
			//

			// for (String oIP : oltMap.keySet()) {
			String stpMac = null;
			SlotPort localSlotPort = null;
			for (String oltIpMac : oltStpMap.keySet()) {
				String[] temps = oltIpMac.split(",");
				if (oltIP.equals(temps[0])) {
					log.info("Find stp relation for mac: " + temps[0] + " " + temps[1]);
					stpMac = temps[1];
					localSlotPort = oltStpMap.get(oltIpMac);
					break;
				}
			}
			if (stpMac != null) {
				for (String oltIpMac : oltStpMap.keySet()) {
					String[] temps = oltIpMac.split(",");
					if (stpMac.equalsIgnoreCase(temps[1]) && (!oltIP.equals(temps[0]))) {
						if (lldpinfos == null) {
							lldpinfos = new HashSet<LLDPInofEntity>();
						}
						log.info("For STP,found OLT link OLT");
						SlotPort remoteSlotPort = oltStpMap.get(oltIpMac);

						LLDPInofEntity lldpInofEntity = new LLDPInofEntity();
						lldpInofEntity.setLocalDeviceType(com.jhw.adm.server.entity.util.Constants.DEV_OLT);
						lldpInofEntity.setLocalIP(oltIP);
						lldpInofEntity.setLocalSlot(localSlotPort.getSlot());
						lldpInofEntity.setLocalPortNo(localSlotPort.getPort());
						lldpInofEntity.setLocalPortType(localSlotPort.getPortType());
						lldpInofEntity.setRemoteDeviceType(com.jhw.adm.server.entity.util.Constants.DEV_OLT);
						lldpInofEntity.setRemoteIP(temps[0]);
						lldpInofEntity.setRemoteSlot(remoteSlotPort.getSlot());
						lldpInofEntity.setRemotePortNo(remoteSlotPort.getPort());
						lldpInofEntity.setRemotePortType(remoteSlotPort.getPortType());
						log.info("STP-OLT line: " + lldpInofEntity.getLocalIP() + " " + lldpInofEntity.getRemoteIP());
						lldpinfos.add(lldpInofEntity);

					}
				}
				// }
			}
			//
			if (lldpinfos != null) {
				oltEntity.setLldpinfos(lldpinfos);
			}

			OLTBaseInfo oltBaseInfo = eponHandler.getOltBaseInfo(oltIP, Configuration.olt_community);
			oltEntity.setOltBaseInfo(oltBaseInfo);
			oltBaseInfo.setOltEntity(oltEntity);
			int deviceModel = com.jhw.adm.server.entity.util.DeviceType.IEL3010_HV;
			if (oltBaseInfo.getDeviceName() != null) {
				String deviceName = Tool.removeBlank(oltBaseInfo.getDeviceName());
				if ("IEL3010-HV".equalsIgnoreCase(deviceName) || deviceName.indexOf("3010") != -1) {
					deviceModel = com.jhw.adm.server.entity.util.DeviceType.IEL3010_HV;
				} else if ("8506".equalsIgnoreCase(deviceName) || deviceName.indexOf("8506") != -1) {
					deviceModel = com.jhw.adm.server.entity.util.DeviceType.EPON8506;
				} else if ("8000".equalsIgnoreCase(deviceName) || deviceName.indexOf("8000") != -1) {
					deviceModel = com.jhw.adm.server.entity.util.DeviceType.EPON8506;
				}
			}
			oltEntity.setDeviceModel(deviceModel);

			log.info("发现 OLT:" + oltIP + " Peer:" + layer3Ip + " " + layer3Port);
			messageSend.sendObjectMessageRes(com.jhw.adm.server.entity.util.Constants.DEV_OLT, oltEntity, SUCCESS,
					MessageNoConstants.TOPONODE, "OLT", client, clientIp);

			// try {
			// Thread.sleep(1000);
			// } catch (InterruptedException e) {
			// }

			HashSet<ONUEntity> onuEntitySet = eponHandler.getOnu(oltIP, Configuration.olt_community);
			if (onuEntitySet != null) {
				// log.info("发现 ONU.");
				for (ONUEntity onu : onuEntitySet) {
					log.info("发现 ONU：" + onu.getMacValue());
					Set<LLDPInofEntity> onuLLDPset = onu.getLldpinfos();
					if (onuLLDPset != null) {
						for (LLDPInofEntity onuLLP : onuLLDPset) {
							log.info("ONU lldp remote：" + onuLLP.getRemoteIP());
						}
					}
				}
				messageSend.sendObjectMessageRes(com.jhw.adm.server.entity.util.Constants.DEV_ONU, onuEntitySet,
						SUCCESS, MessageNoConstants.TOPONODE, "ONU", client, clientIp);
			}
		}
		// return compareMap;
	}

	private void stpRelationHandle(String ip, String peerMac, SlotPort oltSlotPort, Map<String, SlotPort> oltStpMap) {
		List<Dot1dStpPortTable> dot1dStpPortTableList = eponHandler.getDot1dStpPortTable(ip,
				Configuration.olt_community);
		if (dot1dStpPortTableList != null && dot1dStpPortTableList.size() != 0) {
			log.info("OLT(" + ip + ") setuped STP protocol.");
			Map<String, Dot1dStpPortTable> compareStpMap = new HashMap<String, Dot1dStpPortTable>();
			for (Dot1dStpPortTable stpPortTable : dot1dStpPortTableList) {
				Object obj = compareStpMap.get(stpPortTable.getDot1dStpPortDesignatedBridge());
				if (obj != null) {
					Dot1dStpPortTable stpTable = (Dot1dStpPortTable) obj;
					if (stpPortTable.getDot1dStpPortPathCost() < stpTable.getDot1dStpPortPathCost()) {
						compareStpMap.put(stpPortTable.getDot1dStpPortDesignatedBridge(), stpPortTable);
					}
				} else {
					compareStpMap.put(stpPortTable.getDot1dStpPortDesignatedBridge(), stpPortTable);
				}
			}
			for (String stpPortDesignatedBridge : compareStpMap.keySet()) {
				int stpPortIndex = compareStpMap.get(stpPortDesignatedBridge).getDot1dStpPort();
				String ifDescr = eponHandler.getPortDescByIfDiid(ip, Configuration.olt_community, stpPortIndex);
				if (stpPortDesignatedBridge.startsWith("80:00:")) {// TODO
					stpPortDesignatedBridge = stpPortDesignatedBridge.substring(6);
				}
				if (stpPortDesignatedBridge.equalsIgnoreCase(peerMac)) {
					log.info("For STP,found layer3 link olt->slot,port:" + ifDescr);
					parseIfDesc(oltSlotPort, ifDescr);
				} else {
					// log
					// .info("For STP,found olt link olt->slot,port:"
					// + ifDescr);
					SlotPort oltSlot = new SlotPort();
					parseIfDesc(oltSlot, ifDescr);
					oltStpMap.put(ip + "," + stpPortDesignatedBridge, oltSlot);
					log.info("oltStpMap: " + ip + "," + stpPortDesignatedBridge);
				}
			}
			compareStpMap.clear();
		} else {
			log.info("OLT " + ip + " not setup STP");
		}
	}

	private void parseIfDesc(SlotPort oltSlotPort, String ifDescr) {
		if (ifDescr != null) {
			ifDescr = ifDescr.trim();
			int position = ifDescr.indexOf("/");
			int additional = ifDescr.indexOf(":");
			if (position != -1) {
				int slot = Integer.parseInt(ifDescr.substring(position - 1, position).trim());
				int port = 0;
				if (additional != -1) {
					port = Integer.parseInt(ifDescr.substring(position + 1, additional).trim());
				} else {
					port = Integer.parseInt(ifDescr.substring(position + 1).trim());
				}
				oltSlotPort.setSlot(slot);
				oltSlotPort.setPort(port);

				int oltPortType = com.jhw.adm.server.entity.util.Constants.GIGA_PORT;
				if (ifDescr.startsWith("Fas")) {
					oltPortType = com.jhw.adm.server.entity.util.Constants.FAST_PORT;
				} else if (ifDescr.startsWith("Gig")) {
					oltPortType = com.jhw.adm.server.entity.util.Constants.GIGA_PORT;
				} else {
					oltPortType = com.jhw.adm.server.entity.util.Constants.PX;
				}
				// oltSlotPort.setPortType(oltPortType);
				oltSlotPort.setPortType(com.jhw.adm.server.entity.util.Constants.TX);// TODO
			}
		}
	}

	private String guid() {
		String guid = UUID.randomUUID().toString();
		return guid;
	}

	private boolean checkEntityNotNull(SwitchNodeEntity switchNodeEntity) {
		// return switchNodeEntity.getBaseInfo() != null
		// && switchNodeEntity.getBaseConfig() != null
		// && switchNodeEntity.getPorts() != null;
		return switchNodeEntity.getBaseInfo() != null && switchNodeEntity.getBaseConfig() != null;
	}

	private void getSwitchParameterInfo(String ip, SwitchNodeEntity switchNodeEntity) {
		SwitchBaseInfo switchBaseInfo = systemHandler.getSysInfo(ip);
		if (switchBaseInfo == null) {
			log.warn(ip + " SwitchBaseInfo NULL,check other device");
			SystemInfoEntity systemInfoEntity = topologyHandler.getSystemInfo(ip, "public");
			if (systemInfoEntity == null) {
				return;
			}
			switchBaseInfo = new SwitchBaseInfo();
			switchBaseInfo.setAddress(systemInfoEntity.getSysLocation());
			switchBaseInfo.setContacts(systemInfoEntity.getSysContact());
			// switchBaseInfo.setDescs(systemInfoEntity.getSysDescr());
			switchBaseInfo.setDeviceName(systemInfoEntity.getSysName());
			switchBaseInfo.setDeviceAddress(ip);
			switchNodeEntity.setBaseInfo(switchBaseInfo);

			SwitchBaseConfig switchBaseConfig = new SwitchBaseConfig();
			// switchBaseConfig.setDescs(systemInfoEntity.getSysDescr());
			switchBaseConfig.setIpValue(ip);
			switchNodeEntity.setBaseConfig(switchBaseConfig);

			// if (systemInfoEntity.getSysObjectID() == null) {
			// return;
			// }
			if (systemInfoEntity.getSysName() == null) {
				return;
			}

			// String sObjectID = systemInfoEntity.getSysObjectID().trim();
			// Check MOXA
			String moxa_objectid = Configuration.moxa_objectid;
			String[] moxa_objectids = moxa_objectid.split(",");
			boolean moxaflag = false;
			// for (String moxaObjectid : moxa_objectids) {
			// if (sObjectID.indexOf(moxaObjectid) != -1) {
			// moxaflag = true;
			// break;
			// }
			// }
			// Check KYLAND
			String kyland_objectid = Configuration.kyland_objectid;
			String[] kyland_objectids = kyland_objectid.split(",");
			boolean kylandflag = false;
			// for (String kylandObjectid : kyland_objectids) {
			// if (sObjectID.indexOf(kylandObjectid) != -1) {
			// kylandflag = true;
			// break;
			// }
			// }
			if (moxaflag) {
				SwitchBaseConfig MOXAswitchBaseConfig = topologyHandler.getMOXABaseConfig(ip, "public");
				if (MOXAswitchBaseConfig != null) {
					switchBaseInfo.setMacValue(MOXAswitchBaseConfig.getUserNames());
					MOXAswitchBaseConfig.setUserNames(null);
					switchNodeEntity.setBaseConfig(MOXAswitchBaseConfig);
				}

				switchNodeEntity.setDeviceModel(com.jhw.adm.server.entity.util.DeviceType.MOXA8000);
				Set<LLDPInofEntity> lldpTable = topologyHandler.getMOXALLDPTable(ip, "public");
				if (lldpTable != null) {
					switchNodeEntity.setLldpinfos(lldpTable);
				}
			} else if (kylandflag) {
				switchNodeEntity.setDeviceModel(com.jhw.adm.server.entity.util.DeviceType.KYLAND3016);

			} else {// Other device
				switchNodeEntity.setDeviceModel(com.jhw.adm.server.entity.util.DeviceType.KYLAND3016);
			}

			return;
		}
		switchNodeEntity.setBaseInfo(switchBaseInfo);

		SwitchBaseConfig switchBaseConfig = systemHandler.getIp(ip);
		if (switchBaseConfig != null) {
			switchNodeEntity.setBaseConfig(switchBaseConfig);
			switchNodeEntity.setType(switchNodeEntity.getBaseInfo().getDeviceName());
			log.info("WT device Type:" + switchNodeEntity.getType());
			int deviceType = com.jhw.adm.server.entity.util.DeviceType.IETH802;
			if (switchNodeEntity.getType() != null) {
				String type = Tool.removeBlank(switchNodeEntity.getType());
				if ("RS608_2F6T".equalsIgnoreCase(type)) {
					deviceType = com.jhw.adm.server.entity.util.DeviceType.IETH802;
				} else if ("RS608_2GF6T".equalsIgnoreCase(type)) {
					deviceType = com.jhw.adm.server.entity.util.DeviceType.IETH802;
				} else if ("RS609_3GF6T".equalsIgnoreCase(type)) {
					deviceType = com.jhw.adm.server.entity.util.DeviceType.IETH802;
				} else if ("RS6010_3GF7T".equalsIgnoreCase(type)) {
					deviceType = com.jhw.adm.server.entity.util.DeviceType.IETH802;
				} else if ("RS1010_2GF8GT_AF".equalsIgnoreCase(type)) {
					deviceType = com.jhw.adm.server.entity.util.DeviceType.IETH802;
				} else if ("RS608_4F4T".equalsIgnoreCase(type)) {
					deviceType = com.jhw.adm.server.entity.util.DeviceType.IETH802;
				} else if ("RS606_2F4T4D".equalsIgnoreCase(type)) {
					deviceType = com.jhw.adm.server.entity.util.DeviceType.IETH802;
				} else if ("CM6028_4GF_8S_16T".equalsIgnoreCase(type)) {
					deviceType = com.jhw.adm.server.entity.util.DeviceType.IETH802;
				} else if ("IETH9307".equalsIgnoreCase(type)) {
					deviceType = com.jhw.adm.server.entity.util.DeviceType.IETH9307;
				} else if ("IETH8009".equalsIgnoreCase(type)) {
					deviceType = com.jhw.adm.server.entity.util.DeviceType.IETH8009;
				}
			}
			switchNodeEntity.setDeviceModel(deviceType);
		} else {
			return;
		}
		Set<LLDPInofEntity> lldpInfoSet = lldpHandler.getLldpInfoTable(ip);
		Set<SwitchPortEntity> portList = portHandler.getPortConfig(ip);
		if (portList == null) {
			return;
		}

		// Gh-Ring
		Map<Integer, String> ringTypeMap = ghringHandler.getRingType(ip);
		List<RingPortInfo> ringPortInfos = ghringHandler.getRingPort(ip);
		Set<SwitchRingInfo> switchRingInfoSet = new HashSet<SwitchRingInfo>();
		if (ringPortInfos != null) {
			for (RingPortInfo ringPortInfo : ringPortInfos) {
				SwitchRingInfo switchRingInfo = new SwitchRingInfo();
				switchRingInfo.setRingID(ringPortInfo.getRingID());
				switchRingInfo.setPortNo(ringPortInfo.getPortNo());
				switchRingInfo.setRing_type(ringTypeMap.get(ringPortInfo.getRingID()));
				if ("forwarding".equalsIgnoreCase(ringPortInfo.getTransferStatus())) {
					switchRingInfo.setForwarding(true);
				} else {
					switchRingInfo.setForwarding(false);
				}
				switchRingInfoSet.add(switchRingInfo);
			}
		}

		// Serial
		List<SwitchSerialPort> serialPorts = serialHandler.getSerialConfig(ip);
		Set<SwitchSerialPort> serialPortSet = new HashSet<SwitchSerialPort>();
		if (serialPorts != null) {
			for (SwitchSerialPort switchSerialPort : serialPorts) {
				serialPortSet.add(switchSerialPort);
			}
		}

		switchNodeEntity.setPorts(portList);
		switchNodeEntity.setLldpinfos(lldpInfoSet);
		switchNodeEntity.setRings(switchRingInfoSet);
		switchNodeEntity.setSerialPorts(serialPortSet);

		if (portList != null) {
			for (SwitchPortEntity port : portList) {
				port.setSwitchNode(switchNodeEntity);
			}
		}
		// Gh-Ring
		for (SwitchRingInfo switchRingInfo : switchRingInfoSet) {
			switchRingInfo.setSwitchNode(switchNodeEntity);
		}
		// Serial
		for (SwitchSerialPort switchSerialPort : serialPortSet) {
			switchSerialPort.setSwitchNode(switchNodeEntity);
		}

		switchNodeEntity.setGuid(guid());
	}

	private void sleepBeforeEnd() {
		try {
			Thread.sleep(Configuration.topology_sleep * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public FEPEntity getTopologyParameter() {
		FEPEntity fepEntity = messageSend.getLoginService().getFEPByCode(Configuration.fepCode);
		return fepEntity;
	}

	public SystemHandler getSystemHandler() {
		return systemHandler;
	}

	public void setSystemHandler(SystemHandler systemHandler) {
		this.systemHandler = systemHandler;
	}

	public PortHandler getPortHandler() {
		return portHandler;
	}

	public void setPortHandler(PortHandler portHandler) {
		this.portHandler = portHandler;
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

	public LldpHandler getLldpHandler() {
		return lldpHandler;
	}

	public void setLldpHandler(LldpHandler lldpHandler) {
		this.lldpHandler = lldpHandler;
	}

	public MacHandler getMacHandler() {
		return macHandler;
	}

	public void setMacHandler(MacHandler macHandler) {
		this.macHandler = macHandler;
	}

	public TopologyHandler getTopologyHandler() {
		return topologyHandler;
	}

	public void setTopologyHandler(TopologyHandler topologyHandler) {
		this.topologyHandler = topologyHandler;
	}

	public GhringHandler getGhringHandler() {
		return ghringHandler;
	}

	public void setGhringHandler(GhringHandler ghringHandler) {
		this.ghringHandler = ghringHandler;
	}

	public SerialHandler getSerialHandler() {
		return serialHandler;
	}

	public void setSerialHandler(SerialHandler serialHandler) {
		this.serialHandler = serialHandler;
	}

	// Epon
	public EponHandler getEponHandler() {
		return eponHandler;
	}

	public void setEponHandler(EponHandler eponHandler) {
		this.eponHandler = eponHandler;
	}

	public MessageQueueHandler getMessageQueueHandler() {
		return messageQueueHandler;
	}

	public void setMessageQueueHandler(MessageQueueHandler messageQueueHandler) {
		this.messageQueueHandler = messageQueueHandler;
	}

}
