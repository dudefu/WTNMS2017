package com.jhw.adm.comclient.service;

import javax.jms.Message;

import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.server.entity.util.EmulationEntity;
import com.jhw.adm.server.entity.util.MessageNoConstants;

/**
 * 
 * @author xiongbo
 * 
 */
public class EmulateService extends BaseService {
	private EmulateHandler emulateHandler;
	private MessageSend messageSend;

	public void getEmulate(String ip, String client, String clientIp,
			Message message) {
		EmulationEntity emulationEntity = emulateHandler.getEmulate(ip);
		if (emulationEntity != null) {
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					emulationEntity, SUCCESS,
					MessageNoConstants.LIGHT_SIGNAL_REP, ip, client, clientIp);
		}
	}

	public EmulateHandler getEmulateHandler() {
		return emulateHandler;
	}

	public void setEmulateHandler(EmulateHandler emulateHandler) {
		this.emulateHandler = emulateHandler;
	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

}
