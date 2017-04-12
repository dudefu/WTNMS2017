package com.jhw.adm.comclient.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.jms.MessageHandleIF;
import com.jhw.adm.comclient.jms.MessageQueueHandler;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.STPPortConfig;
import com.jhw.adm.server.entity.switchs.STPSysConfig;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.ParmRep;

/**
 * Rstp
 * 
 * @author xiongbo
 * 
 */
public class StpService extends BaseService {
	private static Logger log = Logger.getLogger(StpService.class);
	private MessageSend messageSend;
	private StpHandler stpHandler;
	private SystemHandler systemHandler;
	private MessageQueueHandler messageQueueHandler;

	public void init() {
		// Only single parameter upload
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHSTPPORT,
				getStpPortConfigHandle);
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHSTPSYS,
				getStpSysConfigHandle);
	}

	public void stpSysConfig(String ip, String client, String clientIp,
			Message message) {
		List<STPSysConfig> stpSysConfigs = getSTPSysConfigs(message);
		if (stpSysConfigs != null) {
			STPSysConfig stpSysConfig = stpSysConfigs.get(0);
			boolean result = stpHandler.configStp(ip, stpSysConfig);

			ParmRep parmRep = handleSTPSysConfig(stpSysConfigs, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);

			// if (result) {
			// messageSend.sendTextMessageRes("STP系统配置成功", SUCCESS,
			// MessageNoConstants.STPSYSCONFIGSET, ip, client,
			// clientIp);
			// } else {
			// messageSend.sendTextMessageRes("STP系统配置失败", FAIL,
			// MessageNoConstants.STPSYSCONFIGSET, ip, client,
			// clientIp);
			// }

		}

	}

	public void stpPortConfig(String ip, String client, String clientIp,
			Message message) {
		List<STPPortConfig> stpPortList = getSTPPortConfigs(message);
		if (stpPortList != null) {
			boolean result = stpHandler.configPort(ip, stpPortList);

			ParmRep parmRep = handleSTPPortConfig(stpPortList, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);

			// if (result) {
			// messageSend.sendTextMessageRes("STP端口配置成功", SUCCESS,
			// MessageNoConstants.STPPORTCONFIGSET, ip, client,
			// clientIp);
			// } else {
			// messageSend.sendTextMessageRes("STP端口配置失败", FAIL,
			// MessageNoConstants.STPPORTCONFIGSET, ip, client,
			// clientIp);
			// }

		}
	}

	public void getStpSysConfig(String ip, String client, String clientIp,
			Message message) {
		Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
		if (synchDeviceSet != null) {
			for (SynchDevice synchDevice : synchDeviceSet) {
				String deviceIp = synchDevice.getIpvalue();
				int deviceType = synchDevice.getDeviceType();
				HashMap<Class, List> synMap = new HashMap<Class, List>();
				List list = new ArrayList();
				STPSysConfig stpSysConfig = stpHandler.getStp(deviceIp);
				list.add(stpSysConfig);
				// String mac = systemHandler.getMAC(deviceIp);
				String STATE = SUCCESS;
				if (stpSysConfig != null) {
					synMap.put(STPSysConfig.class, list);
				} else {
					STATE = FAIL;
				}

				messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
						MessageNoConstants.SINGLESYNCHREP,
						synchDevice.getIpvalue(), client, clientIp);
			}
		}
	}

	private MessageHandleIF getStpSysConfigHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			getStpSysConfig(ip, client, clientIp, message);
		}

	};

	public void getStpPortConfig(String ip, String client, String clientIp,
			Message message) {
		Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
		if (synchDeviceSet != null) {
			for (SynchDevice synchDevice : synchDeviceSet) {
				String deviceIp = synchDevice.getIpvalue();
				int deviceType = synchDevice.getDeviceType();
				HashMap<Class, List> synMap = new HashMap<Class, List>();
				List<STPPortConfig> portStpList = stpHandler.getPort(deviceIp);
				// String mac = systemHandler.getMAC(deviceIp);
				String STATE = SUCCESS;
				if (portStpList != null) {
					synMap.put(STPPortConfig.class, portStpList);
				} else {
					STATE = FAIL;
				}

				messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
						MessageNoConstants.SINGLESYNCHREP,
						synchDevice.getIpvalue(), client, clientIp);
			}
		}
	}

	private MessageHandleIF getStpPortConfigHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			getStpPortConfig(ip, client, clientIp, message);
		}

	};

	private List<STPPortConfig> getSTPPortConfigs(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<STPPortConfig> stpPortConfigs = null;
		try {
			stpPortConfigs = (List<STPPortConfig>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return stpPortConfigs;
	}

	/**
	 * Because of UPDATE,so a List.
	 * 
	 * @param message
	 * @return
	 */
	private List<STPSysConfig> getSTPSysConfigs(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<STPSysConfig> stpSysConfigs = null;
		try {
			stpSysConfigs = (List<STPSysConfig>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return stpSysConfigs;
	}

	private ParmRep handleSTPPortConfig(List<STPPortConfig> stpPortConfigs,
			boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (STPPortConfig stpPortConfig : stpPortConfigs) {
			parmIds.add(stpPortConfig.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(STPPortConfig.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private ParmRep handleSTPSysConfig(List<STPSysConfig> stpSysConfigs,
			boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (STPSysConfig stpSysConfig : stpSysConfigs) {
			parmIds.add(stpSysConfig.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(STPSysConfig.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	public StpHandler getStpHandler() {
		return stpHandler;
	}

	public void setStpHandler(StpHandler stpHandler) {
		this.stpHandler = stpHandler;
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

	public MessageQueueHandler getMessageQueueHandler() {
		return messageQueueHandler;
	}

	public void setMessageQueueHandler(MessageQueueHandler messageQueueHandler) {
		this.messageQueueHandler = messageQueueHandler;
	}

}
