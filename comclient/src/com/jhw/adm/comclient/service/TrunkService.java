package com.jhw.adm.comclient.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import com.jhw.adm.comclient.jms.MessageHandleIF;
import com.jhw.adm.comclient.jms.MessageQueueHandler;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.TrunkConfig;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.ParmRep;

/**
 * 
 * @author xiongbo
 * 
 */
public class TrunkService extends BaseService {
	private TrunkHandler trunkHandler;
	private MessageSend messageSend;
	private SystemHandler systemHandler;
	private MessageQueueHandler messageQueueHandler;

	public void init() {
		// Only single parameter upload
		messageQueueHandler.addHandler(
				MessageNoConstants.SINGLESWITCHTRUNKPORT, getTrunkHandle);
	}

	private MessageHandleIF getTrunkHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();
					HashMap<Class, List> synMap = new HashMap<Class, List>();
					List<TrunkConfig> trunkList = trunkHandler
							.getTrunk(deviceIp);
					// String mac = systemHandler.getMAC(deviceIp);
					String STATE = SUCCESS;
					if (trunkList != null && trunkList.size() == 0) {
						synMap.put(TrunkConfig.class, null);
					} else if (trunkList != null) {
						synMap.put(TrunkConfig.class, trunkList);
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

	public void configTrunk(String ip, String client, String clientIp,
			Message message) {
		List<TrunkConfig> trunkConfigs = getTrunkConfigList(message);
		if (trunkConfigs != null) {
			boolean result = trunkHandler.configTrunk(ip, trunkConfigs);
			ParmRep parmRep = handleTrunkConfig(trunkConfigs, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	public void deleteTrunk(String ip, String client, String clientIp,
			Message message) {
		List<TrunkConfig> trunkConfigs = getTrunkConfigList(message);
		if (trunkConfigs != null) {
			boolean result = trunkHandler.deleteTrunk(ip, trunkConfigs);
			ParmRep parmRep = handleTrunkConfig(trunkConfigs, result);
			parmRep.setDescs(DELETE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	private List<TrunkConfig> getTrunkConfigList(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<TrunkConfig> trunkConfigs = null;
		try {
			trunkConfigs = (List<TrunkConfig>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return trunkConfigs;
	}

	private ParmRep handleTrunkConfig(List<TrunkConfig> trunkConfigs,
			boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (TrunkConfig trunkConfig : trunkConfigs) {
			parmIds.add(trunkConfig.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(TrunkConfig.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
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
