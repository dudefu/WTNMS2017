package com.jhw.adm.comclient.jms;

import javax.jms.Message;

/**
 * 
 * @author xiongbo
 * 
 */
public interface MessageHandleIF {
	void doHandle(String ip, String client, String clientIp, Message message);
}
