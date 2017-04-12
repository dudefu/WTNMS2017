package com.jhw.adm.comclient.service.synEquipment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import com.jhw.adm.comclient.jms.MessageHandleIF;
import com.jhw.adm.comclient.jms.MessageQueueHandler;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.comclient.service.BaseService;
import com.jhw.adm.comclient.service.Dot1xHandler;
import com.jhw.adm.comclient.service.GhringHandler;
import com.jhw.adm.comclient.service.IgmpHandler;
import com.jhw.adm.comclient.service.LacpHandler;
import com.jhw.adm.comclient.service.LldpHandler;
import com.jhw.adm.comclient.service.MacHandler;
import com.jhw.adm.comclient.service.MirrorHandler;
import com.jhw.adm.comclient.service.QosHandler;
import com.jhw.adm.comclient.service.RmonHandler;
import com.jhw.adm.comclient.service.SerialHandler;
import com.jhw.adm.comclient.service.SnmpHandler;
import com.jhw.adm.comclient.service.SntpHandler;
import com.jhw.adm.comclient.service.StpHandler;
import com.jhw.adm.comclient.service.SwitcherUserHandle;
import com.jhw.adm.comclient.service.SystemHandler;
import com.jhw.adm.comclient.service.TrunkHandler;
import com.jhw.adm.comclient.service.VlanHandler;
import com.jhw.adm.comclient.service.epon.OltBaseConfigHandler;
import com.jhw.adm.comclient.service.epon.OltBusinessConfigHandler;
import com.jhw.adm.comclient.service.switch3.BaseConfig3Service;
import com.jhw.adm.comclient.service.switch3.LLDP3Service;
import com.jhw.adm.comclient.service.switch3.STP3Service;
import com.jhw.adm.comclient.service.switch3.Vlan3Service;
import com.jhw.adm.comclient.service.topology.epon.EponHandler;
import com.jhw.adm.comclient.system.Configuration;
import com.jhw.adm.server.entity.epon.OLTChipInfo;
import com.jhw.adm.server.entity.epon.OLTDBA;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.epon.OLTSTP;
import com.jhw.adm.server.entity.epon.OLTSlotConfig;
import com.jhw.adm.server.entity.epon.OLTVlan;
import com.jhw.adm.server.entity.epon.ONULLID;
import com.jhw.adm.server.entity.level3.Switch3LLDPPortEntity;
import com.jhw.adm.server.entity.level3.Switch3LLDP_PEntity;
import com.jhw.adm.server.entity.level3.Switch3STPEntity;
import com.jhw.adm.server.entity.level3.Switch3STPPortEntity;
import com.jhw.adm.server.entity.level3.Switch3VlanEnity;
import com.jhw.adm.server.entity.level3.Switch3VlanPortEntity;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.nets.VlanConfig;
import com.jhw.adm.server.entity.nets.VlanEntity;
import com.jhw.adm.server.entity.nets.VlanPort;
import com.jhw.adm.server.entity.ports.LACPConfig;
import com.jhw.adm.server.entity.ports.LLDPConfig;
import com.jhw.adm.server.entity.ports.LLDPPortConfig;
import com.jhw.adm.server.entity.ports.PC8021x;
import com.jhw.adm.server.entity.ports.QOSPriority;
import com.jhw.adm.server.entity.ports.QOSSpeedConfig;
import com.jhw.adm.server.entity.ports.QOSStormControl;
import com.jhw.adm.server.entity.ports.QOSSysConfig;
import com.jhw.adm.server.entity.ports.RingConfig;
import com.jhw.adm.server.entity.ports.RingLinkBak;
import com.jhw.adm.server.entity.ports.RingPortInfo;
import com.jhw.adm.server.entity.ports.SC8021x;
import com.jhw.adm.server.entity.switchs.IGMPEntity;
import com.jhw.adm.server.entity.switchs.MirrorEntity;
import com.jhw.adm.server.entity.switchs.Priority802D1P;
import com.jhw.adm.server.entity.switchs.PriorityDSCP;
import com.jhw.adm.server.entity.switchs.PriorityTOS;
import com.jhw.adm.server.entity.switchs.SNMPGroup;
import com.jhw.adm.server.entity.switchs.SNMPHost;
import com.jhw.adm.server.entity.switchs.SNMPMass;
import com.jhw.adm.server.entity.switchs.SNMPUser;
import com.jhw.adm.server.entity.switchs.SNMPView;
import com.jhw.adm.server.entity.switchs.SNTPConfigEntity;
import com.jhw.adm.server.entity.switchs.STPPortConfig;
import com.jhw.adm.server.entity.switchs.STPSysConfig;
import com.jhw.adm.server.entity.switchs.SwitchBaseConfig;
import com.jhw.adm.server.entity.switchs.SwitchBaseInfo;
import com.jhw.adm.server.entity.switchs.SwitchUser;
import com.jhw.adm.server.entity.switchs.TrunkConfig;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MACMutiCast;
import com.jhw.adm.server.entity.util.MACUniCast;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.warning.PortRemon;
import com.jhw.adm.server.entity.warning.RmonThing;
import com.jhw.adm.server.entity.warning.RmonWarningConfig;

/**
 * 设备同步业务处理
 * 
 * @author xiongbo
 * 
 */
public class SynEquipmentService extends BaseService {
	// private static Logger log = Logger.getLogger(SynEquipmentService.class);
	private SystemHandler systemHandler;
	private Dot1xHandler dot1xHandler;
	private GhringHandler ghringHandler;
	private IgmpHandler igmpHandler;
	private MacHandler macHandler;
	private MirrorHandler mirrorHandler;
	private LacpHandler lacpHandler;
	private QosHandler qosHandler;
	private SerialHandler serialHandler;
	private SnmpHandler snmpHandler;
	private SntpHandler sntpHandler;
	private StpHandler stpHandler;
	private TrunkHandler trunkHandler;
	private VlanHandler vlanHandler;
	private RmonHandler rmonHandler;
	private LldpHandler lldpHandler;
	private SwitcherUserHandle switcherUserHandle;
	//
	private MessageSend messageSend;
	// Epon
	private OltBaseConfigHandler oltBaseConfigHandler;
	private OltBusinessConfigHandler oltBusinessConfigHandler;
	private EponHandler eponHandler;
	// Switch3
	private Vlan3Service vlan3Service;
	private BaseConfig3Service baseConfig3Service;
	private STP3Service stp3Service;
	private LLDP3Service lldp3Service;
	//
	private String client;
	private String clientIp;

	// private HashMap<Class, List> synMap = new HashMap<Class, List>();
	private MessageQueueHandler messageQueueHandler;

	public void init() {
		messageQueueHandler.addHandler(MessageNoConstants.SYNCHDEVICE,
				synAllParamHandle);
	}

	private MessageHandleIF synAllParamHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			start(ip, client, clientIp, message);
		}
	};

	public void start(String ipend, String client, String clientIp,
			Message message) {
		this.client = client;
		this.clientIp = clientIp;
		Set<SynchDevice> synchDeviceSet = getSynchDeviceSet(message);
		if (synchDeviceSet != null) {
			for (SynchDevice synchDevice : synchDeviceSet) {
				// if (synchDevice.getDeviceType() == Constants.DEV_SWITCHER2) {
				if (synchDevice.getDeviceType() == Constants.DEV_SWITCHER2) {
					synSwitch2(synchDevice.getIpvalue(),
							synchDevice.getDeviceType());
					// } else if (synchDevice.getDeviceType() ==
					// Constants.DEV_OLT) {
				} else if (synchDevice.getDeviceType() == Constants.DEV_OLT
						|| synchDevice.getDeviceType() == Constants.DEV_ONU) {
					synEpon(synchDevice.getIpvalue(),
							synchDevice.getDeviceType());
					// } else if (synchDevice.getDeviceType() ==
					// Constants.DEV_SWITCHER3) {
				} else if (synchDevice.getDeviceType() == Constants.DEV_SWITCHER3) {
					synSwitch3(synchDevice.getIpvalue(),
							synchDevice.getDeviceType());
				}
			}
			sleepBeforeEnd();
			messageSend.sendTextMessageRes("上载完成", SUCCESS,
					MessageNoConstants.SYNCHFINISH, ipend, client, clientIp);
		}
	}

	private void synSwitch2(String ip, int type) {
		// String ip = synchDevice.getIpvalue();
		List list = new ArrayList();
		HashMap<Class, List> synMap = new HashMap<Class, List>();
		VlanConfig vlanConfig = new VlanConfig();
		Set<VlanEntity> vlanSet = vlanHandler.getVlan(vlanConfig, ip);
		vlanConfig.setVlanEntitys(vlanSet);
		list.add(vlanConfig);
		synMap.put(VlanConfig.class, list);
		if (vlanSet == null) {
			messageSend.sendObjectMessageRes(type, synMap, FAIL,
					MessageNoConstants.SYNCHREP, ip, client, clientIp);
			log.warn("SYN ip: " + ip + " out time!");
			synMap.clear();
			list.clear();
			return;
		}
		Set<VlanPort> portSet = vlanHandler.getVlanPort(vlanConfig, ip);
		vlanConfig.setVlanPorts(portSet);
		if (portSet == null) {
			messageSend.sendObjectMessageRes(type, synMap, SUCCESS,
					MessageNoConstants.SYNCHREP, ip, client, clientIp);
			log.warn("VlanPort NULL");
			synMap.clear();
			list.clear();
			return;
		}

		// vlanConfig.setVlanPorts(portSet);
		// vlanConfig.setVlanEntitys(vlanSet);
		// list.add(vlanConfig);
		// synMap.put(VlanConfig.class, list);

		List<PC8021x> dot1xList = dot1xHandler.getPort(ip);
		synMap.put(PC8021x.class, dot1xList);
		if (dot1xList == null) {
			messageSend.sendObjectMessageRes(type, synMap, SUCCESS,
					MessageNoConstants.SYNCHREP, ip, client, clientIp);
			log.warn("PC8021x NULL");
			synMap.clear();
			list.clear();
			return;
		}
		SC8021x sDot1x = dot1xHandler.getSys(ip);
		if (sDot1x != null) {
			list = new ArrayList();
			list.add(sDot1x);
			synMap.put(SC8021x.class, list);
		} else {
			synMap.put(SC8021x.class, null);
		}
		if (sDot1x == null) {
			messageSend.sendObjectMessageRes(type, synMap, SUCCESS,
					MessageNoConstants.SYNCHREP, ip, client, clientIp);
			log.warn("SC8021x NULL");
			synMap.clear();
			list.clear();
			return;
		}
		IGMPEntity iGMPEntity = igmpHandler.getIgmp(ip);
		if (iGMPEntity != null) {
			iGMPEntity.setVlanIds(igmpHandler.getVlanIgmp(ip));
			list = new ArrayList();
			list.add(iGMPEntity);
			synMap.put(IGMPEntity.class, list);
		}
		if (iGMPEntity == null) {
			messageSend.sendObjectMessageRes(type, synMap, SUCCESS,
					MessageNoConstants.SYNCHREP, ip, client, clientIp);
			log.warn("iGMPEntity NULL");
			synMap.clear();
			list.clear();
			return;
		}

		List<MACMutiCast> mutList = macHandler.getMulticast(ip);
		synMap.put(MACMutiCast.class, mutList);
		// Need long time
		List<MACUniCast> uniList = macHandler.getUnicast(ip);
		synMap.put(MACUniCast.class, uniList);

		MirrorEntity mirrorEntity = mirrorHandler.getMirrorConfig(ip);
		if (mirrorEntity != null) {
			list = new ArrayList();
			list.add(mirrorEntity);
			synMap.put(MirrorEntity.class, list);
		} else {
			synMap.put(MirrorEntity.class, null);
		}

		QOSSysConfig qOSSysConfig = qosHandler.getQosConfig(ip);
		if (qOSSysConfig != null) {
			list = new ArrayList();
			list.add(qOSSysConfig);
			synMap.put(QOSSysConfig.class, list);
		} else {
			synMap.put(QOSSysConfig.class, null);
		}
		List<Priority802D1P> priority802D1Ps = qosHandler
				.getQosDot1Priority(ip);
		List<PriorityTOS> priorityTOS = qosHandler.getTosPriority(ip);
		List<PriorityDSCP> priorityDSCPs = qosHandler.getDscpPriority(ip);
		QOSPriority qosPriority = new QOSPriority();
		qosPriority.setPriorityEOTs(priority802D1Ps);
		qosPriority.setPriorityTOSs(priorityTOS);
		qosPriority.setPriorityDSCPs(priorityDSCPs);
		list = new ArrayList();
		list.add(qosPriority);
		synMap.put(QOSPriority.class, list);
		// List<QOSStormControl> qosStormControls =
		// qosHandler.getStormControl(ip);
		List<QOSStormControl> qosStormControls = qosHandler
				.getStormControl_80series(ip);
		synMap.put(QOSStormControl.class, qosStormControls);
		List<QOSSpeedConfig> qosSpeedConfigs = qosHandler.getTrafficControl(ip);
		synMap.put(QOSSpeedConfig.class, qosSpeedConfigs);

		List<STPPortConfig> stpList = stpHandler.getPort(ip);
		synMap.put(STPPortConfig.class, stpList);
		STPSysConfig sTPSysConfig = stpHandler.getStp(ip);
		if (sTPSysConfig != null) {
			list = new ArrayList();
			list.add(sTPSysConfig);
			synMap.put(STPSysConfig.class, list);
		} else {
			synMap.put(STPSysConfig.class, null);
		}

		SNTPConfigEntity sntpConfigEntity = sntpHandler.getSntp(ip);
		if (sntpConfigEntity != null) {
			list = new ArrayList();
			list.add(sntpConfigEntity);
			synMap.put(SNTPConfigEntity.class, list);
		} else {
			synMap.put(SNTPConfigEntity.class, null);
		}

		List<RingConfig> rings = ghringHandler.getRingConfig(ip);
		synMap.put(RingConfig.class, rings);
		RingLinkBak ringLinkBak = ghringHandler.getRingLinkBak(ip);
		if (ringLinkBak != null && ringLinkBak.getLinkId() != 0) {
			list = new ArrayList();
			list.add(ringLinkBak);
			synMap.put(RingLinkBak.class, list);
		} else {
			synMap.put(RingLinkBak.class, null);
		}
		List<RingPortInfo> ringPorts = ghringHandler.getRingPort(ip);
		synMap.put(RingPortInfo.class, ringPorts);

		List<LACPConfig> lacpConfigs = lacpHandler.getLacpConfig(ip);
		synMap.put(LACPConfig.class, lacpConfigs);
		// List<LACPInfoEntity> lacpInfos = lacpHandler.getLacpInfo(ip);
		// synMap.put(LACPInfoEntity.class, lacpInfos);

		List<TrunkConfig> trunkConfigs = trunkHandler.getTrunk(ip);
		synMap.put(TrunkConfig.class, trunkConfigs);

		// * Because the data have uploaded when topology
		// List<SwitchSerialPort> serialPorts =
		// serialHandler.getSerialConfig(ip);
		// synMap.put(SwitchSerialPort.class, serialPorts);

		List<PortRemon> rmonCounts = rmonHandler.getRmonStatEntry(ip);
		List<RmonWarningConfig> rmonAlarms = rmonHandler.getRmonAlarmList(ip);
		List<RmonThing> rmonEvents = rmonHandler.getRmonEventList(ip);
		synMap.put(PortRemon.class, rmonCounts);
		synMap.put(RmonWarningConfig.class, rmonAlarms);
		synMap.put(RmonThing.class, rmonEvents);

		LLDPConfig lldpConfig = lldpHandler.getLldpParameter(ip);
		if (lldpConfig != null) {
			list = new ArrayList();
			list.add(lldpConfig);
			Map<Integer, Integer> lldpPortCfgMap = lldpHandler
					.getLldpPortCfg(ip);
			if (lldpPortCfgMap != null) {
				Set<LLDPPortConfig> lldpPortConfigs = new HashSet<LLDPPortConfig>();
				for (int portNo : lldpPortCfgMap.keySet()) {
					LLDPPortConfig lldpPortConfig = new LLDPPortConfig();
					lldpPortConfig.setPortNum(portNo);
					lldpPortConfig.setLldpWork(lldpPortCfgMap.get(portNo));
					lldpPortConfigs.add(lldpPortConfig);
				}
				lldpConfig.setLldpPortConfigs(lldpPortConfigs);
			}
			synMap.put(LLDPConfig.class, list);
		}

		// Snmp
		List<SNMPView> snmpVews = snmpHandler.getSnmpViews(ip);
		if (snmpVews != null) {
			synMap.put(SNMPView.class, snmpVews);
		}
		List<SNMPMass> snmpMasss = snmpHandler.getSnmpComms(ip);
		if (snmpMasss != null) {
			synMap.put(SNMPMass.class, snmpMasss);
		}
		List<SNMPGroup> snmpGroups = snmpHandler.getSnmpGroups(ip);
		if (snmpGroups != null) {
			synMap.put(SNMPGroup.class, snmpGroups);
		}
		List<SNMPUser> snmpUsers = snmpHandler.getSnmpUsers(ip);
		if (snmpUsers != null) {
			synMap.put(SNMPUser.class, snmpUsers);
		}
		List<SNMPHost> snmpHosts = snmpHandler.getSnmpHosts(ip);
		if (snmpHosts != null) {
			synMap.put(SNMPHost.class, snmpHosts);
		}

		// SwitcherUser
		List<SwitchUser> switcherUsers = switcherUserHandle.getSwitcherUser(ip);
		if (switcherUsers != null) {
			synMap.put(SwitchUser.class, switcherUsers);
		}
		// Switch Base info
		SwitchBaseConfig switchBaseConfig = systemHandler.getIp(ip);
		if (switchBaseConfig != null) {
			list = new ArrayList();
			list.add(switchBaseConfig);
			synMap.put(SwitchBaseConfig.class, list);
		}
		SwitchBaseInfo switchBaseInfo = systemHandler.getSysInfo(ip);
		if (switchBaseInfo != null) {
			list = new ArrayList();
			list.add(switchBaseInfo);
			synMap.put(SwitchBaseInfo.class, list);
		}
		//
		// String mac = systemHandler.getMAC(ip);
		// log.info("SYN mac:" + mac);
		messageSend.sendObjectMessageRes(type, synMap, SUCCESS,
				MessageNoConstants.SYNCHREP, ip, client, clientIp);

		synMap.clear();
		list.clear();
	}

	private void synEpon(String ip, int type) {
		List list = new ArrayList();
		HashMap<Class, List> synMap = new HashMap<Class, List>();
		List<OLTSlotConfig> slotList = oltBaseConfigHandler.getSlot(ip,
				Configuration.olt_community);
		synMap.put(OLTSlotConfig.class, slotList);
		List<OLTChipInfo> chipList = oltBaseConfigHandler.getChip(ip,
				Configuration.olt_community);
		synMap.put(OLTChipInfo.class, chipList);
		List<OLTVlan> vlanList = oltBusinessConfigHandler.getVlan(ip,
				Configuration.olt_community);
		OLTEntity oltEntity = new OLTEntity();
		oltEntity.setIpValue(ip);
		for (OLTVlan oltVlan : vlanList) {
			oltVlan.setOltEntity(oltEntity);
		}
		synMap.put(OLTVlan.class, vlanList);
		OLTDBA oltDBA = oltBusinessConfigHandler.getDBA(ip,
				Configuration.olt_community);
		list.add(oltDBA);
		synMap.put(OLTDBA.class, list);
		List<ONULLID> llidList = oltBusinessConfigHandler.getLLID(ip,
				Configuration.olt_community);
		synMap.put(ONULLID.class, llidList);
		OLTSTP oltSTP = oltBusinessConfigHandler.getSTP(ip,
				Configuration.olt_community);
		list = new ArrayList();
		list.add(oltSTP);
		synMap.put(OLTSTP.class, list);

		// HashSet<ONUEntity> onuEntitySet = eponHandler
		// .getOnu(ip, "public", null);
		// list = new ArrayList();
		// for (ONUEntity onuEntity : onuEntitySet) {
		// onuEntity.setLldpinfos(null);
		// list.add(onuEntity);
		// }
		// synMap.put(ONUEntity.class, list);

		// String mac = eponHandler.getOltMac(ip, "public");

		messageSend.sendObjectMessageRes(type, synMap, SUCCESS,
				MessageNoConstants.SYNCHREP, ip, client, clientIp);

		synMap.clear();
		list.clear();
	}

	private void synSwitch3(String ip, int type) {
		List list = null;
		HashMap<Class, List> synMap = new HashMap<Class, List>();
		// Vlan
		List<Switch3VlanEnity> vlanList = vlan3Service
				.getVlanList(ip, "public");
		List<Switch3VlanPortEntity> vlanPortList = vlan3Service
				.getVlanPortList(ip, "public");
		for (Switch3VlanEnity vlan : vlanList) {
			for (Switch3VlanPortEntity vlanPort : vlanPortList) {
				if (vlan.getVlanID() == vlanPort.getVlanID()) {
					vlan.getVlanPortEntities().add(vlanPort);
				}
			}
		}
		synMap.put(Switch3VlanEnity.class, vlanList);
		// STP
		Switch3STPEntity stp = this.stp3Service.getSTP(ip, "public");
		Set<Switch3STPPortEntity> stpPortSet = this.stp3Service.getSTPPortList(
				ip, "public");
		if (stp != null) {
			stp.setPortEntities(stpPortSet);
			list = new ArrayList();
			list.add(stp);
			synMap.put(Switch3STPEntity.class, list);
		}
		// LLDP
		Switch3LLDP_PEntity lldp = this.lldp3Service.getLLDP(ip, "public");
		Set<Switch3LLDPPortEntity> lldpPortList = this.lldp3Service
				.getLLDPPortList(ip, "public");
		if (lldp != null) {
			lldp.setPortEntities(lldpPortList);
			list = new ArrayList();
			list.add(lldp);
			synMap.put(Switch3LLDP_PEntity.class, list);
		}

		messageSend.sendObjectMessageRes(type, synMap, SUCCESS,
				MessageNoConstants.SYNCHREP, ip, client, clientIp);

		synMap.clear();
		list.clear();
	}

	private Set<SynchDevice> getSynchDeviceSet(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		Set<SynchDevice> synchDeviceSet = null;
		try {
			synchDeviceSet = (Set<SynchDevice>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		// List<String> ipList = synchSwitch.getIpvalues();
		return synchDeviceSet;
	}

	private void sleepBeforeEnd() {
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void synVlan(String ipend, String client, String clientIp,
			Message message) {
		Set<SynchDevice> synchDeviceSet = getSynchDeviceSet(message);
		if (synchDeviceSet != null) {
			for (SynchDevice synchDevice : synchDeviceSet) {
				if (synchDevice.getDeviceType() == Constants.DEV_SWITCHER2) {
					List list = new ArrayList();
					HashMap<Class, List> synMap = new HashMap<Class, List>();

					VlanConfig vlanConfig = new VlanConfig();
					Set<VlanEntity> vlanSet = vlanHandler.getVlan(vlanConfig,
							synchDevice.getIpvalue());
					Set<VlanPort> portSet = vlanHandler.getVlanPort(vlanConfig,
							synchDevice.getIpvalue());
					vlanConfig.setVlanPorts(portSet);
					vlanConfig.setVlanEntitys(vlanSet);
					list.add(vlanConfig);
					synMap.put(VlanConfig.class, list);

					String mac = systemHandler.getMAC(synchDevice.getIpvalue());
					messageSend
							.sendObjectMessageRes(
									com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
									synMap, SUCCESS,
									MessageNoConstants.SYNCHREP, mac, client,
									clientIp);
				}
				if (synchDevice.getDeviceType() == Constants.DEV_OLT) {

				}
			}
			sleepBeforeEnd();
			messageSend.sendTextMessageRes("上载完成", SUCCESS,
					MessageNoConstants.SYNCHFINISH, ipend, client, clientIp);
		}
	}

	public void synSC8021x(String ipend, String client, String clientIp,
			Message message) {
		Set<SynchDevice> synchDeviceSet = getSynchDeviceSet(message);
		if (synchDeviceSet != null) {
			for (SynchDevice synchDevice : synchDeviceSet) {
				if (synchDevice.getDeviceType() == Constants.DEV_SWITCHER2) {
					String ip = synchDevice.getIpvalue();

					List list = new ArrayList();
					HashMap<Class, List> synMap = new HashMap<Class, List>();

					List<PC8021x> dot1xList = dot1xHandler.getPort(ip);
					synMap.put(PC8021x.class, dot1xList);
					SC8021x sDot1x = dot1xHandler.getSys(ip);
					if (sDot1x != null) {
						list = new ArrayList();
						list.add(sDot1x);
						synMap.put(SC8021x.class, list);
					} else {
						synMap.put(SC8021x.class, null);
					}

					String mac = systemHandler.getMAC(synchDevice.getIpvalue());
					messageSend
							.sendObjectMessageRes(
									com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
									synMap, SUCCESS,
									MessageNoConstants.SYNCHREP, mac, client,
									clientIp);
				}
				if (synchDevice.getDeviceType() == Constants.DEV_OLT) {

				}
			}
			sleepBeforeEnd();
			messageSend.sendTextMessageRes("上载完成", SUCCESS,
					MessageNoConstants.SYNCHFINISH, ipend, client, clientIp);
		}
	}

	public void synIGMP(String ipend, String client, String clientIp,
			Message message) {
		Set<SynchDevice> synchDeviceSet = getSynchDeviceSet(message);
		if (synchDeviceSet != null) {
			for (SynchDevice synchDevice : synchDeviceSet) {
				if (synchDevice.getDeviceType() == Constants.DEV_SWITCHER2) {
					String ip = synchDevice.getIpvalue();

					List list = new ArrayList();
					HashMap<Class, List> synMap = new HashMap<Class, List>();

					IGMPEntity iGMPEntity = igmpHandler.getIgmp(ip);
					if (iGMPEntity != null) {
						iGMPEntity.setVlanIds(igmpHandler.getVlanIgmp(ip));
						list = new ArrayList();
						list.add(iGMPEntity);
						synMap.put(IGMPEntity.class, list);
					}

					String mac = systemHandler.getMAC(synchDevice.getIpvalue());
					messageSend
							.sendObjectMessageRes(
									com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
									synMap, SUCCESS,
									MessageNoConstants.SYNCHREP, mac, client,
									clientIp);
				}
				if (synchDevice.getDeviceType() == Constants.DEV_OLT) {

				}
			}
			sleepBeforeEnd();
			messageSend.sendTextMessageRes("上载完成", SUCCESS,
					MessageNoConstants.SYNCHFINISH, ipend, client, clientIp);
		}
	}

	public SystemHandler getSystemHandler() {
		return systemHandler;
	}

	public void setSystemHandler(SystemHandler systemHandler) {
		this.systemHandler = systemHandler;
	}

	public Dot1xHandler getDot1xHandler() {
		return dot1xHandler;
	}

	public void setDot1xHandler(Dot1xHandler dot1xHandler) {
		this.dot1xHandler = dot1xHandler;
	}

	public GhringHandler getGhringHandler() {
		return ghringHandler;
	}

	public void setGhringHandler(GhringHandler ghringHandler) {
		this.ghringHandler = ghringHandler;
	}

	public IgmpHandler getIgmpHandler() {
		return igmpHandler;
	}

	public void setIgmpHandler(IgmpHandler igmpHandler) {
		this.igmpHandler = igmpHandler;
	}

	public MacHandler getMacHandler() {
		return macHandler;
	}

	public void setMacHandler(MacHandler macHandler) {
		this.macHandler = macHandler;
	}

	public MirrorHandler getMirrorHandler() {
		return mirrorHandler;
	}

	public void setMirrorHandler(MirrorHandler mirrorHandler) {
		this.mirrorHandler = mirrorHandler;
	}

	public LacpHandler getLacpHandler() {
		return lacpHandler;
	}

	public void setLacpHandler(LacpHandler lacpHandler) {
		this.lacpHandler = lacpHandler;
	}

	public QosHandler getQosHandler() {
		return qosHandler;
	}

	public void setQosHandler(QosHandler qosHandler) {
		this.qosHandler = qosHandler;
	}

	public SerialHandler getSerialHandler() {
		return serialHandler;
	}

	public void setSerialHandler(SerialHandler serialHandler) {
		this.serialHandler = serialHandler;
	}

	public SnmpHandler getSnmpHandler() {
		return snmpHandler;
	}

	public void setSnmpHandler(SnmpHandler snmpHandler) {
		this.snmpHandler = snmpHandler;
	}

	public SntpHandler getSntpHandler() {
		return sntpHandler;
	}

	public void setSntpHandler(SntpHandler sntpHandler) {
		this.sntpHandler = sntpHandler;
	}

	public StpHandler getStpHandler() {
		return stpHandler;
	}

	public void setStpHandler(StpHandler stpHandler) {
		this.stpHandler = stpHandler;
	}

	public TrunkHandler getTrunkHandler() {
		return trunkHandler;
	}

	public void setTrunkHandler(TrunkHandler trunkHandler) {
		this.trunkHandler = trunkHandler;
	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

	public VlanHandler getVlanHandler() {
		return vlanHandler;
	}

	public void setVlanHandler(VlanHandler vlanHandler) {
		this.vlanHandler = vlanHandler;
	}

	public RmonHandler getRmonHandler() {
		return rmonHandler;
	}

	public void setRmonHandler(RmonHandler rmonHandler) {
		this.rmonHandler = rmonHandler;
	}

	public LldpHandler getLldpHandler() {
		return lldpHandler;
	}

	public void setLldpHandler(LldpHandler lldpHandler) {
		this.lldpHandler = lldpHandler;
	}

	// Epon
	public OltBaseConfigHandler getOltBaseConfigHandler() {
		return oltBaseConfigHandler;
	}

	public void setOltBaseConfigHandler(
			OltBaseConfigHandler oltBaseConfigHandler) {
		this.oltBaseConfigHandler = oltBaseConfigHandler;
	}

	public OltBusinessConfigHandler getOltBusinessConfigHandler() {
		return oltBusinessConfigHandler;
	}

	public void setOltBusinessConfigHandler(
			OltBusinessConfigHandler oltBusinessConfigHandler) {
		this.oltBusinessConfigHandler = oltBusinessConfigHandler;
	}

	public EponHandler getEponHandler() {
		return eponHandler;
	}

	public void setEponHandler(EponHandler eponHandler) {
		this.eponHandler = eponHandler;
	}

	public SwitcherUserHandle getSwitcherUserHandle() {
		return switcherUserHandle;
	}

	public void setSwitcherUserHandle(SwitcherUserHandle switcherUserHandle) {
		this.switcherUserHandle = switcherUserHandle;
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

	public MessageQueueHandler getMessageQueueHandler() {
		return messageQueueHandler;
	}

	public void setMessageQueueHandler(MessageQueueHandler messageQueueHandler) {
		this.messageQueueHandler = messageQueueHandler;
	}

}
