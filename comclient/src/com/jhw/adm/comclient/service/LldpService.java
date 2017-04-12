package com.jhw.adm.comclient.service;

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
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.ports.LLDPConfig;
import com.jhw.adm.server.entity.ports.LLDPPortConfig;
import com.jhw.adm.server.entity.ports.SwitchPortEntity;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.ParmRep;

/**
 * 
 * @author xiongbo
 * 
 */
public class LldpService extends BaseService {
	private MessageSend messageSend;
	private LldpHandler lldpHandler;
	private SystemHandler systemHandler;
	private MessageQueueHandler messageQueueHandler;

	public void init() {
		// Only single parameter upload
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHLLDP,
				getLldpParameterHandle);
		// messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHLLDP,
		// getLldpPortCfgHandle);
	}

	private MessageHandleIF getLldpParameterHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();
					HashMap<Class, List> synMap = new HashMap<Class, List>();
					LLDPConfig lldpConfig = lldpHandler
							.getLldpParameter(deviceIp);
					List list = new ArrayList();
					list.add(lldpConfig);
					Map<Integer, Integer> lldpPortCfgMap = lldpHandler
							.getLldpPortCfg(deviceIp);
					if (lldpPortCfgMap != null) {
						Set<LLDPPortConfig> lldpPortConfigs = new HashSet<LLDPPortConfig>();
						for (int portNo : lldpPortCfgMap.keySet()) {
							LLDPPortConfig lldpPortConfig = new LLDPPortConfig();
							lldpPortConfig.setPortNum(portNo);
							lldpPortConfig.setLldpWork(lldpPortCfgMap
									.get(portNo));
							lldpPortConfigs.add(lldpPortConfig);
						}
						lldpConfig.setLldpPortConfigs(lldpPortConfigs);
					}
					// String mac = systemHandler.getMAC(deviceIp);
					String STATE = SUCCESS;
					if (lldpConfig != null) {
						synMap.put(LLDPConfig.class, list);
					} else {
						STATE = FAIL;
					}
					messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
							MessageNoConstants.SINGLESYNCHREP,
							synchDevice.getIpvalue(), client, clientIp);
				}
			}
		}

	};

	private MessageHandleIF getLldpPortCfgHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();
					HashMap<Class, List> synMap = new HashMap<Class, List>();
					Map<Integer, Integer> tempMap = lldpHandler
							.getLldpPortCfg(deviceIp);
					// String mac = systemHandler.getMAC(deviceIp);
					String STATE = SUCCESS;
					messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
							MessageNoConstants.SINGLESYNCHREP,
							synchDevice.getIpvalue(), client, clientIp);
				}
			}
		}

	};

	public void configLldp(String ip, String client, String clientIp,
			Message message) {
		List<LLDPConfig> lldpConfigList = getLldpConfig(message);
		if (lldpConfigList != null) {
			boolean result = true;
			for (LLDPConfig lldpConfig : lldpConfigList) {
				result = lldpHandler.configLLDP(ip, lldpConfig);
				if (!result) {
					break;
				}
			}
			// List<LLDPConfig> lldpConfigs = new ArrayList<LLDPConfig>(1);
			// lldpConfigs.add(lldpConfig);
			ParmRep parmRep = handleLldpConfig(lldpConfigList, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	/**
	 * @deprecated
	 * @param ip
	 * @param client
	 * @param clientIp
	 * @param message
	 */
	public void configLldpConfig(String ip, String client, String clientIp,
			Message message) {
		List<LLDPConfig> lldpConfigList = getLldpConfig(message);
		if (lldpConfigList != null) {
			boolean result = true;
			for (LLDPConfig lldpConfig : lldpConfigList) {
				result = lldpHandler.configParameter(ip, lldpConfig);
				if (!result) {
					break;
				}
			}
			// List<LLDPConfig> lldpConfigs = new ArrayList<LLDPConfig>(1);
			// lldpConfigs.add(lldpConfig);
			ParmRep parmRep = handleLldpConfig(lldpConfigList, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	/**
	 * @deprecated
	 * @param ip
	 * @param client
	 * @param clientIp
	 * @param message
	 */
	public void configLldpPortConfig(String ip, String client, String clientIp,
			Message message) {
		List<SwitchPortEntity> lldpPorts = getLldpPorts(message);
		if (lldpPorts != null) {
			boolean result = lldpHandler.configPort(ip, lldpPorts);
			ParmRep parmRep = handleLldpPorts(lldpPorts, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	private List<LLDPConfig> getLldpConfig(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<LLDPConfig> lldpConfiglist = null;
		try {
			lldpConfiglist = (List<LLDPConfig>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return lldpConfiglist;
	}

	private List<SwitchPortEntity> getLldpPorts(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<SwitchPortEntity> lldpPorts = null;
		try {
			lldpPorts = (List<SwitchPortEntity>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return lldpPorts;
	}

	private ParmRep handleLldpConfig(List<LLDPConfig> lldpConfigs,
			boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (LLDPConfig lldpConfig : lldpConfigs) {
			parmIds.add(lldpConfig.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(LLDPConfig.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private ParmRep handleLldpPorts(List<SwitchPortEntity> lldpPorts,
			boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (SwitchPortEntity switchPortEntity : lldpPorts) {
			parmIds.add(switchPortEntity.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(SwitchPortEntity.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	public LldpHandler getLldpHandler() {
		return lldpHandler;
	}

	public void setLldpHandler(LldpHandler lldpHandler) {
		this.lldpHandler = lldpHandler;
	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

	public MessageQueueHandler getMessageQueueHandler() {
		return messageQueueHandler;
	}

	public void setMessageQueueHandler(MessageQueueHandler messageQueueHandler) {
		this.messageQueueHandler = messageQueueHandler;
	}

	public SystemHandler getSystemHandler() {
		return systemHandler;
	}

	public void setSystemHandler(SystemHandler systemHandler) {
		this.systemHandler = systemHandler;
	}

}
