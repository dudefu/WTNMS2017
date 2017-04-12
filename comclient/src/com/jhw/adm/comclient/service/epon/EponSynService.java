package com.jhw.adm.comclient.service.epon;

import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.comclient.service.BaseService;
import com.jhw.adm.comclient.service.topology.epon.EponHandler;

/**
 * 
 * @author xiongbo
 * 
 */
public class EponSynService extends BaseService {
	private MessageSend messageSend;
	private EponHandler eponHandler;

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

	public EponHandler getEponHandler() {
		return eponHandler;
	}

	public void setEponHandler(EponHandler eponHandler) {
		this.eponHandler = eponHandler;
	}

}
