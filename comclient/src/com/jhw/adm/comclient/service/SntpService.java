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
import com.jhw.adm.server.entity.switchs.SNTPConfigEntity;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.ParmRep;

public class SntpService extends BaseService {
	private static Logger log = Logger.getLogger(SntpService.class);
	private SntpHandler sntpHandler;
	private MessageSend messageSend;
	private SystemHandler systemHandler;
	private MessageQueueHandler messageQueueHandler;

	public void init() {
		// Only single parameter upload
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHSNTP,
				getSntpSysInfoHandle);
	}

	public void configSntpServer(String ip, String client, String clientIp,
			Message message) {
		List<SNTPConfigEntity> sntpConfigEntitys = getSNTPConfigs(message);
		if (sntpConfigEntitys != null) {
			SNTPConfigEntity sntpConfigEntity = sntpConfigEntitys.get(0);
			boolean result = sntpHandler.configSntp(ip, sntpConfigEntity);

			ParmRep parmRep = handleSNTPConfigEntity(sntpConfigEntitys, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);

			if (result) {
				messageSend.sendTextMessageRes("SNTP服务器配置", SUCCESS,
						MessageNoConstants.SNTPSET, ip, client, clientIp);
			} else {
				messageSend.sendTextMessageRes("SNTP服务器配置", FAIL,
						MessageNoConstants.SNTPSET, ip, client, clientIp);
			}
		}
	}

	public void getSntpSysInfo(String ip, String client, String clientIp,
			Message message) {
		Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
		if (synchDeviceSet != null) {
			for (SynchDevice synchDevice : synchDeviceSet) {
				String deviceIp = synchDevice.getIpvalue();
				int deviceType = synchDevice.getDeviceType();
				HashMap<Class, List> synMap = new HashMap<Class, List>();
				List list = new ArrayList();
				SNTPConfigEntity sntpConfigEntity = sntpHandler
						.getSntp(deviceIp);
				list.add(sntpConfigEntity);
				String mac = systemHandler.getMAC(deviceIp);
				String STATE = SUCCESS;
				if (sntpConfigEntity != null) {
					synMap.put(SNTPConfigEntity.class, list);
				} else {
					STATE = FAIL;
				}

				messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
						MessageNoConstants.SINGLESYNCHREP, synchDevice
								.getIpvalue(), client, clientIp);
			}
		}
	}

	private MessageHandleIF getSntpSysInfoHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			getSntpSysInfo(ip, client, clientIp, message);
		}

	};

	/**
	 * Because of UPDATE,so a List.
	 * 
	 * @param message
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<SNTPConfigEntity> getSNTPConfigs(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<SNTPConfigEntity> sntpConfigEntitys = null;
		try {
			sntpConfigEntitys = (List<SNTPConfigEntity>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return sntpConfigEntitys;
	}

	private ParmRep handleSNTPConfigEntity(
			List<SNTPConfigEntity> sntpConfigEntitys, boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (SNTPConfigEntity sntpConfigEntity : sntpConfigEntitys) {
			parmIds.add(sntpConfigEntity.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(SNTPConfigEntity.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	public SntpHandler getSntpHandler() {
		return sntpHandler;
	}

	public void setSntpHandler(SntpHandler sntpHandler) {
		this.sntpHandler = sntpHandler;
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
