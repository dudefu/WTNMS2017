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
import com.jhw.adm.server.entity.ports.LACPConfig;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.ParmRep;

public class LacpService extends BaseService {
	private static Logger log = Logger.getLogger(LacpService.class);
	private MessageSend messageSend;
	private LacpHandler lacpHandler;
	private SystemHandler systemHandler;
	private MessageQueueHandler messageQueueHandler;

	public void init() {
		// Only single parameter upload
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHLACP,
				getLacpConfigHandle);
		// messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHLACP,
		// null);
	}

	private MessageHandleIF getLacpConfigHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();
					HashMap<Class, List> synMap = new HashMap<Class, List>();
					List<LACPConfig> lacpConfigList = lacpHandler
							.getLacpConfig(deviceIp);
					// String mac = systemHandler.getMAC(deviceIp);
					String STATE = SUCCESS;
					if (lacpConfigList != null) {
						synMap.put(LACPConfig.class, lacpConfigList);
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

	public void configLacp(String ip, String client, String clientIp,
			Message message) {
		List<LACPConfig> lacpConfigs = getLACPConfig(message);
		if (lacpConfigs != null) {
			boolean result = lacpHandler.configLacps(ip, lacpConfigs);
			ParmRep parmRep = handleLACPConfig(lacpConfigs, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	private List<LACPConfig> getLACPConfig(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<LACPConfig> lacpConfigs = null;
		try {
			lacpConfigs = (List<LACPConfig>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return lacpConfigs;
	}

	private ParmRep handleLACPConfig(List<LACPConfig> lacpConfigs,
			boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (LACPConfig lacpConfig : lacpConfigs) {
			parmIds.add(lacpConfig.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(LACPConfig.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	public LacpHandler getLacpHandler() {
		return lacpHandler;
	}

	public void setLacpHandler(LacpHandler lacpHandler) {
		this.lacpHandler = lacpHandler;
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
