package com.jhw.adm.comclient.service.epon;

import javax.jms.Message;

import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.comclient.service.BaseService;
import com.jhw.adm.comclient.system.Configuration;
import com.jhw.adm.server.entity.util.EmulationEntity;
import com.jhw.adm.server.entity.util.MessageNoConstants;

/**
 * 
 * @author xiongbo
 * 
 */
public class OltBaseConfigService extends BaseService {
	private MessageSend messageSend;
	private OltBaseConfigHandler oltBaseConfigHandler;

	public void getEmulate(String ip, String client, String clientIp,
			Message message) {
		EmulationEntity emulationEntity = oltBaseConfigHandler.getEmulate(ip,
				Configuration.olt_community);
		if (emulationEntity != null) {
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_OLT,
					emulationEntity, SUCCESS,
					MessageNoConstants.LIGHT_SIGNAL_REP, ip, client, clientIp);
		}
	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

	public OltBaseConfigHandler getOltBaseConfigHandler() {
		return oltBaseConfigHandler;
	}

	public void setOltBaseConfigHandler(
			OltBaseConfigHandler oltBaseConfigHandler) {
		this.oltBaseConfigHandler = oltBaseConfigHandler;
	}

}
