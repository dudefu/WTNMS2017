package com.jhw.adm.comclient.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.jms.Message;

import com.jhw.adm.comclient.jms.MessageHandleIF;
import com.jhw.adm.comclient.jms.MessageQueueHandler;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.SwitchBaseConfig;
import com.jhw.adm.server.entity.switchs.SwitchBaseInfo;
import com.jhw.adm.server.entity.util.MessageNoConstants;

/**
 * 系统信息配置,ip信息配置
 * 
 * @author xiongbo
 * 
 */
public class SystemService extends BaseService {
	private MessageSend messageSend;
	private SystemHandler systemHandler;
	private MessageQueueHandler messageQueueHandler;

	public void init() {
		// Only single parameter upload
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHIP,
				getIpHandle);
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHINFO,
				getSysInfoHandle);
	}

	private MessageHandleIF getSysInfoHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();
					HashMap<Class, List> synMap = new HashMap<Class, List>();
					SwitchBaseInfo switchBaseInfo = systemHandler
							.getSysInfo(deviceIp);
					// String mac = systemHandler.getMAC(deviceIp);
					List list = new ArrayList();
					list.add(switchBaseInfo);
					String STATE = SUCCESS;
					if (switchBaseInfo != null) {
						synMap.put(SwitchBaseInfo.class, list);
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
	private MessageHandleIF getIpHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();
					HashMap<Class, List> synMap = new HashMap<Class, List>();
					SwitchBaseConfig switchBaseConfig = systemHandler
							.getIp(deviceIp);
					// String mac = systemHandler.getMAC(deviceIp);
					List list = new ArrayList();
					list.add(switchBaseConfig);
					String STATE = SUCCESS;
					if (switchBaseConfig != null) {
						synMap.put(SwitchBaseConfig.class, list);
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

	//
	public SystemHandler getSystemHandler() {
		return systemHandler;
	}

	public void setSystemHandler(SystemHandler systemHandler) {
		this.systemHandler = systemHandler;
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

}
