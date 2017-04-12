package com.jhw.adm.comclient.jms;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.carrier.BaseOperateService;
import com.jhw.adm.comclient.carrier.PortQueryService;
import com.jhw.adm.comclient.carrier.RoutingQueryService;
import com.jhw.adm.comclient.carrier.RoutingSetService;
import com.jhw.adm.comclient.carrier.WaveBandService;
import com.jhw.adm.comclient.service.EmulateService;
import com.jhw.adm.comclient.service.GhringService;
import com.jhw.adm.comclient.service.IgmpService;
import com.jhw.adm.comclient.service.LacpService;
import com.jhw.adm.comclient.service.LldpService;
import com.jhw.adm.comclient.service.MacService;
import com.jhw.adm.comclient.service.MirrorService;
import com.jhw.adm.comclient.service.PingService;
import com.jhw.adm.comclient.service.PortService;
import com.jhw.adm.comclient.service.QosService;
import com.jhw.adm.comclient.service.RmonService;
import com.jhw.adm.comclient.service.SerialService;
import com.jhw.adm.comclient.service.SnmpService;
import com.jhw.adm.comclient.service.SntpService;
import com.jhw.adm.comclient.service.StpService;
import com.jhw.adm.comclient.service.SwitcherUserService;
import com.jhw.adm.comclient.service.SystemUpgradeService;
import com.jhw.adm.comclient.service.TrunkService;
import com.jhw.adm.comclient.service.VlanService;
import com.jhw.adm.comclient.service.epon.OltBaseConfigService;
import com.jhw.adm.comclient.service.epon.OltBusinessConfigService;
import com.jhw.adm.comclient.service.performance.PerformanceService;
import com.jhw.adm.comclient.service.switch3.BaseConfig3Service;
import com.jhw.adm.comclient.service.switch3.LLDP3Service;
import com.jhw.adm.comclient.service.switch3.STP3Service;
import com.jhw.adm.comclient.service.switch3.Vlan3Service;
import com.jhw.adm.comclient.service.synEquipment.SynEquipmentService;
import com.jhw.adm.comclient.service.topology.TopologyService;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

/**
 * 消息队列处理
 * 
 * @author xiongbo
 * 
 */
public class MessageQueueHandler {
	private static Logger log = Logger.getLogger(MessageQueueHandler.class);
	private LinkedList<Message> messageQueue = new LinkedList<Message>();
	private boolean flag = true;
	private boolean loop = true;
	//
	private TopologyService topologyService;
	private SynEquipmentService synEquipmentService;
	private VlanService vlanService;
	private MacService macService;
	private MirrorService mirrorService;
	private PortService portService;
	private StpService stpService;
	private SntpService sntpService;
	private PerformanceService performanceService;
	private PingService pingService;
	private SerialService serialService;
	private IgmpService igmpService;
	private QosService qosService;
	private LacpService lacpService;
	private TrunkService trunkService;
	private SwitcherUserService switcherUserService;
	// PLC
	private BaseOperateService baseOperateService;
	private PortQueryService portQueryService;
	private RoutingQueryService routingQueryService;
	private RoutingSetService routingSetService;
	private SystemUpgradeService systemUpgradeService;
	private WaveBandService waveBandService;
	private GhringService ghringService;
	private RmonService rmonService;
	private LldpService lldpService;
	private SnmpService snmpService;
	private EmulateService emulateService;
	// OLT
	private OltBusinessConfigService oltBusinessConfigService;
	private OltBaseConfigService oltBaseConfigService;
	//
	private final int QUEUE_THREAD_NUM = 2;

	// Swithch3
	private Vlan3Service vlan3Service;
	private BaseConfig3Service baseConfig3Service;
	private STP3Service stp3Service;
	private LLDP3Service lldp3Service;

	private final ExecutorService executorService;
	private final Map<Integer, MessageHandleIF> handlerMap;
	private final Map<Integer, String> msgMap;

	public MessageQueueHandler() {
		executorService = Executors.newCachedThreadPool();
		handlerMap = new ConcurrentHashMap<Integer, MessageHandleIF>();
		msgMap = new HashMap<Integer, String>();

		for (Field classField : MessageNoConstants.class.getDeclaredFields()) {
			try {
				int classModifier = classField.getModifiers();
				if (Modifier.isStatic(classModifier)
						&& Modifier.isPublic(classModifier)) {
					msgMap.put(classField.getInt(classField), classField
							.getName());
				}
			} catch (IllegalArgumentException e) {
				log.error(e);
			} catch (IllegalAccessException e) {
				log.error(e);
			}
		}
	}

	public void addQueue(Message message) {
		if (flag) {
			for (int i = 0; i < QUEUE_THREAD_NUM; i++) {
				new QueueHandler(i + "").start();
			}
			flag = false;
		}
		synchronized (messageQueue) {
			if (messageQueue.add(message)) {
				messageQueue.notify();
			}
		}
	}

	private class QueueHandler extends Thread {
		public QueueHandler(String index) {
			setDaemon(true);
			setName(index);
		}

		public void run() {
			try {
				Message message = null;
				while (loop) {
					synchronized (messageQueue) {
						while (messageQueue.size() == 0) {
							log.warn("messageQueue empty!Thread：" + getName());
							messageQueue.wait();
						}
						message = messageQueue.removeFirst();
					}
					doHandler(message);
				}
			} catch (InterruptedException e) {
				log.error(e);
			}
		}
	}

	private void close() {
		loop = false;
		messageQueue.clear();
		//
		this.handlerMap.clear();
		this.executorService.shutdownNow();
		this.msgMap.clear();
	}

	public void addHandler(int subType, MessageHandleIF handler) {
		handlerMap.put(subType, handler);
	}

	private void doHandler(final Message message) {
		// From which client(login name)
		String client = null;
		// Form which client ip
		String clientIp = null;
		int messageType = 0;
		// To which switch
		String ip = null;
		try {
			messageType = message.getIntProperty(Constants.MESSAGETYPE);
			ip = message.getStringProperty(Constants.MESSAGETO);
			client = message.getStringProperty(Constants.MESSAGEFROM);
			clientIp = message.getStringProperty(Constants.CLIENTIP);
		} catch (JMSException e) {
			log.error(e);
		}
		// log.info("***Device ip:" + ip + " messageID:" + messageType);
		if (messageType == MessageNoConstants.FEPHEARTBEAT) {
			log.info("Heartbeat Message:" + messageType);
		} else if (messageType == MessageNoConstants.TOPOSEARCH) {
			this.threadHandle(message, client, clientIp, ip, messageType);
		} else if (messageType == MessageNoConstants.VLANGET) {

		} else if (messageType == MessageNoConstants.VLANSET) {

		} else if (messageType == MessageNoConstants.ALL_VLAN) {
			vlanService.batchConfigVlan(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.VLANNEW) {
			vlanService.addVlan(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.VLANDELETE) {
			vlanService.deleteVlan(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.VLANUPDATE) {
			vlanService.configVlan(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.VLANPORTUPDATE) {
			vlanService.configVlanPort(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.SYNCHDEVICE) {
			this.threadHandle(message, client, clientIp, ip, messageType);
		} else if (messageType == MessageNoConstants.MACUNINEW) {
			macService.createUnicast(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.MACMUTINEW) {
			macService.createMulticast(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.MACUNIDEL) {
			macService.deleteUnicast(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.MACMUTIDEL) {
			macService.deleteMulticast(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.IGMP_PORTSET) {
			igmpService.configIgmp(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.IGMP_VLANSET) {
			igmpService.configVlanIgmp(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.MIRRORUPDATE) {
			mirrorService.configMirror(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.PORTSET) {
			portService.configPort(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.STPSYSCONFIGSET) {
			stpService.stpSysConfig(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.STPPORTCONFIGSET) {
			stpService.stpPortConfig(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.SNTPSET) {
			sntpService.configSntpServer(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.RTMONITORSTART) {
			performanceService.startMonitor(ip, client, clientIp, message,
					messageType);
		} else if (messageType == MessageNoConstants.MONITORRING_TIMER) {
			performanceService.startTimingMonitor(ip, client, clientIp,
					message, messageType);
		} else if (messageType == MessageNoConstants.MONITORRING_DEL) {
			performanceService.deleteTimingMonitor(ip, client, clientIp,
					message, messageType);
		} else if (messageType == MessageNoConstants.RTMONITORSTOP) {
			performanceService.stopMonitor(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.PINGSTART) {
			pingService.startPing(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.PINGEND) {
			pingService.stopRealPing(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.PING_TIMER) {
			pingService.startTimingPing(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.ALL_RING) {
			ghringService.batchConfigRing(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.RINGNEW) {
			ghringService.createRing(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.RINGDELETE) {
			ghringService.deleteRing(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.LINKBAKNEW) {
			ghringService.createRingLinkBak(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.LINKBAKDELETE) {
			ghringService.deleteRingLinkBak(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.QOS_SYSCONFIG) {
			qosService.configSysQos(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.QOS_PRIORITYCONFIG) {
			qosService.configQosDot1Priority(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.TOS_PRIORITYCONFIG) {
			qosService.configTosPriority(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.DSCP_PRIORITYCONFIG) {
			qosService.configDscpPriority(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.QOS_LIMITPORT) {
			int deviceType = getDeviceType(message);
			qosService.configTrafficLimit(ip, client, clientIp, message);
		}

		else if (messageType == MessageNoConstants.QOS_STORMCONTROL) {
			int deviceType = getDeviceType(message);
			qosService.configStormControl_80series(ip, client, clientIp,
					message, deviceType);
		} else if (messageType == MessageNoConstants.SERIALCOFIG) {
			serialService.configSerial(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.REMONTHINGNEW) {
			rmonService.addRmonEvent(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.REMONTHINGDEL) {
			rmonService.deleteRmonEvent(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.PWARNINGCONFIG) {
			rmonService.addRmonAlarm(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.PWARNINGCONFIGDELETE) {
			rmonService.deleteRmonAlarm(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.REMONPORTNEW) {
			rmonService.addRmonStatic(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.REMONPORTDEL) {
			rmonService.deleteRmonStatic(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.LLDPCONFIG) {
			lldpService.configLldp(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.SNMPHOSTADD) {
			snmpService.configSnmpHost(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.ALL_SNMPHOST) {
			snmpService.batchConfigSnmpHost(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.LIGHT_SIGNAL) {
			int type = 0;
			try {
				type = message.getIntProperty(Constants.DEVTYPE);
			} catch (JMSException e) {
				e.printStackTrace();
			}
			if (type == Constants.DEV_SWITCHER2) {
				emulateService.getEmulate(ip, client, clientIp, message);
			} else if (type == Constants.DEV_OLT) {
				oltBaseConfigService.getEmulate(ip, client, clientIp, message);
			}

		} else if (messageType == MessageNoConstants.LACPCONFIG) {
			lacpService.configLacp(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.AGGREGATEPORT_ADD) {
			trunkService.configTrunk(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.AGGREGATEPORT_DEL) {
			trunkService.deleteTrunk(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.SWITCHERUPGRATE) {
			systemUpgradeService.upgradeSwitcher(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.SWITCHUSERMANAGE) {
			int operateType = -1;
			try {
				operateType = message.getIntProperty(Constants.SWITCHUSERS);
				if (operateType == Constants.SWITCHUSERADD) {
					switcherUserService.addSwitcherUser(ip, client, clientIp,
							message);
				} else if (operateType == Constants.SWITCHUSERUPDATE) {
					switcherUserService.modifyUserPwd(ip, client, clientIp,
							message);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (messageType == MessageNoConstants.SWITCHUSERDEL) {
			switcherUserService.deleteSwitcherUser(ip, client, clientIp,
					message);
		}
		// Parameters Upload
		else if (messageType == MessageNoConstants.SINGLESYNCHDEVICE) {
			// Parameters Type
			int messParmType = 0;
			try {
				messParmType = message.getIntProperty(Constants.MESSPARMTYPE);
			} catch (JMSException e) {
				log.error(e);
			}
			threadHandle(message, client, clientIp, ip, messParmType);
		}

		// PLC Carrier
		else if (messageType == MessageNoConstants.CARRIERTEST) {
			baseOperateService.testPLC(client, clientIp, message);
		} else if (messageType == MessageNoConstants.CARRIERRESTART) {
			baseOperateService.restartPLC(client, clientIp, message);
		} else if (messageType == MessageNoConstants.CARRIERMARKINGCONFIG) {
			baseOperateService.configPLCMarking(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.CARRIERWAVEBANDCONFIG) {
			waveBandService.setWaveBand(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.CARRIERWAVEBANDQUERY) {
			waveBandService.queryWaveBand(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.CARRIERROUTECONFIG) {
			routingSetService.sync(client, clientIp, message);
		} else if (messageType == MessageNoConstants.CARRIERROUTECONFIGS) {
			routingSetService.syncAll(client, clientIp, message);
		} else if (messageType == MessageNoConstants.CARRIERROUTEQUERY) {
			routingQueryService.queryRouting(client, clientIp, message);
		} else if (messageType == MessageNoConstants.CARRIERROUTECLEAR) {
			routingSetService.clearRouting(client, clientIp, message);
		} else if (messageType == MessageNoConstants.CARRIERPORTCONFIG) {
			portQueryService.setPort(client, clientIp, message);
		} else if (messageType == MessageNoConstants.CARRIERPORTQUERY) {
			portQueryService.queryPort(client, clientIp, message);
		} else if (messageType == MessageNoConstants.CARRIERSYSTEMUPGRADE) {

		}
		// OLT
		else if (messageType == MessageNoConstants.OLTVLANADD) {
			oltBusinessConfigService.createOLTVlan(ip, client, clientIp,
					message);
		} else if (messageType == MessageNoConstants.OLTVLANDEL) {
			oltBusinessConfigService.deleteOLTVlan(ip, client, clientIp,
					message);
		} else if (messageType == MessageNoConstants.DBACONFIG) {
			oltBusinessConfigService.configDBA(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.OLTSTPCONFIG) {
			oltBusinessConfigService.configSTP(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.LLIDCONFIG) {
			oltBusinessConfigService.configLLID(ip, client, clientIp, message);
		}
		// Switch3
		else if (messageType == MessageNoConstants.SWITCH3VLANADD) {
			vlan3Service.createVlan(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.SWITCH3VLANUPDATE) {
			vlan3Service.updateVlan(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.SWITCH3VLANDEL) {
			vlan3Service.deleteVlan(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.SWITCH3VLANPORT) {
			vlan3Service.updateVlanPort(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.SWITCH3STP) {
			stp3Service.updateSTP(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.SWITCH3STPPORT) {
			stp3Service.updateSTPPort(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.SWITCH3LLDP_P) {
			lldp3Service.updateLLDP(ip, client, clientIp, message);
		} else if (messageType == MessageNoConstants.SWITCH3LLDPPORT) {
			lldp3Service.updateLLDPPort(ip, client, clientIp, message);
		}
		// sys log
		else if (messageType == MessageNoConstants.SINGLESYSLOGHOST) {
			this.threadHandle(message, client, clientIp, ip, messageType);
		} else if (messageType == MessageNoConstants.SYSLOGHOSTSAVE) {
			this.threadHandle(message, client, clientIp, ip, messageType);
		}
		// delete log
		else if (messageType == MessageNoConstants.SYSLOGHOSTDELETE) {
			this.threadHandle(message, client, clientIp, ip, messageType);
		}
		// Layer3/olt port state
		else if (messageType == MessageNoConstants.SINGLESWITCHLAYER3PORT) {
			this.threadHandle(message, client, clientIp, ip, messageType);
		} else if (messageType == MessageNoConstants.SINGLEOLTPORT) {
			this.threadHandle(message, client, clientIp, ip, messageType);
		} else {
			log.error("Error MessageType.");
		}
	}

	private void threadHandle(final Message message, String client,
			String clientIp, String ip, int messParmType) {
		log.info(String.format("MsgName：%s,msgID：%s", msgMap.get(messParmType),
				messParmType));
		if (handlerMap.containsKey(messParmType)) {
			MessageHandleIF handler = handlerMap.get(messParmType);
			if (handler != null) {
				final String client2 = client;
				final String clientIp2 = clientIp;
				final String ip2 = ip;
				final MessageHandleIF handle = handler;
				Runnable notify = new Runnable() {
					@Override
					public void run() {
						handle.doHandle(ip2, client2, clientIp2, message);
					}
				};
				executorService.execute(notify);
			}
		}
	}

	private int getDeviceType(Message message) {
		int deviceType = 0;
		try {
			deviceType = message.getIntProperty(Constants.DEVTYPE);
		} catch (JMSException e) {
			log.error(e);
		}
		return deviceType;
	}

	public TopologyService getTopologyService() {
		return topologyService;
	}

	public void setTopologyService(TopologyService topologyService) {
		this.topologyService = topologyService;
	}

	public SynEquipmentService getSynEquipmentService() {
		return synEquipmentService;
	}

	public void setSynEquipmentService(SynEquipmentService synEquipmentService) {
		this.synEquipmentService = synEquipmentService;
	}

	public VlanService getVlanService() {
		return vlanService;
	}

	public void setVlanService(VlanService vlanService) {
		this.vlanService = vlanService;
	}

	public MacService getMacService() {
		return macService;
	}

	public void setMacService(MacService macService) {
		this.macService = macService;
	}

	public MirrorService getMirrorService() {
		return mirrorService;
	}

	public void setMirrorService(MirrorService mirrorService) {
		this.mirrorService = mirrorService;
	}

	public PortService getPortService() {
		return portService;
	}

	public void setPortService(PortService portService) {
		this.portService = portService;
	}

	public StpService getStpService() {
		return stpService;
	}

	public void setStpService(StpService stpService) {
		this.stpService = stpService;
	}

	public SntpService getSntpService() {
		return sntpService;
	}

	public void setSntpService(SntpService sntpService) {
		this.sntpService = sntpService;
	}

	public PerformanceService getPerformanceService() {
		return performanceService;
	}

	public void setPerformanceService(PerformanceService performanceService) {
		this.performanceService = performanceService;
	}

	public PingService getPingService() {
		return pingService;
	}

	public void setPingService(PingService pingService) {
		this.pingService = pingService;
	}

	public BaseOperateService getBaseOperateService() {
		return baseOperateService;
	}

	public void setBaseOperateService(BaseOperateService baseOperateService) {
		this.baseOperateService = baseOperateService;
	}

	public PortQueryService getPortQueryService() {
		return portQueryService;
	}

	public void setPortQueryService(PortQueryService portQueryService) {
		this.portQueryService = portQueryService;
	}

	public RoutingQueryService getRoutingQueryService() {
		return routingQueryService;
	}

	public void setRoutingQueryService(RoutingQueryService routingQueryService) {
		this.routingQueryService = routingQueryService;
	}

	public RoutingSetService getRoutingSetService() {
		return routingSetService;
	}

	public void setRoutingSetService(RoutingSetService routingSetService) {
		this.routingSetService = routingSetService;
	}

	public SystemUpgradeService getSystemUpgradeService() {
		return systemUpgradeService;
	}

	public void setSystemUpgradeService(
			SystemUpgradeService systemUpgradeService) {
		this.systemUpgradeService = systemUpgradeService;
	}

	public WaveBandService getWaveBandService() {
		return waveBandService;
	}

	public void setWaveBandService(WaveBandService waveBandService) {
		this.waveBandService = waveBandService;
	}

	public GhringService getGhringService() {
		return ghringService;
	}

	public void setGhringService(GhringService ghringService) {
		this.ghringService = ghringService;
	}

	public SerialService getSerialService() {
		return serialService;
	}

	public void setSerialService(SerialService serialService) {
		this.serialService = serialService;
	}

	public RmonService getRmonService() {
		return rmonService;
	}

	public void setRmonService(RmonService rmonService) {
		this.rmonService = rmonService;
	}

	public LldpService getLldpService() {
		return lldpService;
	}

	public void setLldpService(LldpService lldpService) {
		this.lldpService = lldpService;
	}

	public SnmpService getSnmpService() {
		return snmpService;
	}

	public void setSnmpService(SnmpService snmpService) {
		this.snmpService = snmpService;
	}

	public EmulateService getEmulateService() {
		return emulateService;
	}

	public void setEmulateService(EmulateService emulateService) {
		this.emulateService = emulateService;
	}

	public IgmpService getIgmpService() {
		return igmpService;
	}

	public void setIgmpService(IgmpService igmpService) {
		this.igmpService = igmpService;
	}

	public QosService getQosService() {
		return qosService;
	}

	public void setQosService(QosService qosService) {
		this.qosService = qosService;
	}

	public LacpService getLacpService() {
		return lacpService;
	}

	public void setLacpService(LacpService lacpService) {
		this.lacpService = lacpService;
	}

	public TrunkService getTrunkService() {
		return trunkService;
	}

	public void setTrunkService(TrunkService trunkService) {
		this.trunkService = trunkService;
	}

	public SwitcherUserService getSwitcherUserService() {
		return switcherUserService;
	}

	public void setSwitcherUserService(SwitcherUserService switcherUserService) {
		this.switcherUserService = switcherUserService;
	}

	// OLT
	public OltBusinessConfigService getOltBusinessConfigService() {
		return oltBusinessConfigService;
	}

	public void setOltBusinessConfigService(
			OltBusinessConfigService oltBusinessConfigService) {
		this.oltBusinessConfigService = oltBusinessConfigService;
	}

	public OltBaseConfigService getOltBaseConfigService() {
		return oltBaseConfigService;
	}

	public void setOltBaseConfigService(
			OltBaseConfigService oltBaseConfigService) {
		this.oltBaseConfigService = oltBaseConfigService;
	}

	public Vlan3Service getVlan3Service() {
		return vlan3Service;
	}

	public void setVlan3Service(Vlan3Service vlan3Service) {
		this.vlan3Service = vlan3Service;
	}

	public BaseConfig3Service getBaseConfig3Service() {
		return baseConfig3Service;
	}

	public void setBaseConfig3Service(BaseConfig3Service baseConfig3Service) {
		this.baseConfig3Service = baseConfig3Service;
	}

	public STP3Service getStp3Service() {
		return stp3Service;
	}

	public void setStp3Service(STP3Service stp3Service) {
		this.stp3Service = stp3Service;
	}

	public LLDP3Service getLldp3Service() {
		return lldp3Service;
	}

	public void setLldp3Service(LLDP3Service lldp3Service) {
		this.lldp3Service = lldp3Service;
	}

}
