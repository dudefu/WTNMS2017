package com.jhw.adm.comclient.gprs.hongdian.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import com.jhw.adm.comclient.gprs.hongdian.DDPData;
import com.jhw.adm.comclient.gprs.hongdian.DDPServer;
import com.jhw.adm.comclient.gprs.hongdian.DSCUser;
import com.jhw.adm.comclient.gprs.hongdian.IDDPListener;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.comclient.system.Configuration;
import com.jhw.adm.server.entity.util.GPRSEntity;
import com.jhw.adm.server.entity.util.MessageNoConstants;

/**
 * 
 * @author xiongbo
 * 
 */
public class HongdianService implements InitializingBean {
	private final Logger log = Logger.getLogger(this.getClass().getName());
	private DDPServer ddpServer;
	// 0-Success other-Fail
	private int result;
	private MessageSend messageSend;

	// public HongdianService() {
	// ddpServer = new DDPServer();
	// ddpServer.addDDPListener(new HongdianServiceListener());
	// }

	@Override
	public void afterPropertiesSet() throws Exception {
		ddpServer = new DDPServer(messageSend);
		ddpServer.addDDPListener(new HongdianServiceListener());
	}

	public void start() {
		result = ddpServer.start(Configuration.gprsPort);
	}

	public void stop() {
		if (result == 0) {
			ddpServer.stop();
		}
	}

	private class HongdianServiceListener implements IDDPListener {

		@Override
		public void heartbeat(DSCUser user) {
			// log.info("Register:" + user.getAddress() + " " +
			// user.getUserId());
			GPRSEntity gprsEntity = new GPRSEntity();
			gprsEntity.setUserId(user.getUserId());
			gprsEntity.setInternateAddress(user.getAddress());
			gprsEntity.setPort(user.getPort());
			gprsEntity.setLocalAddress(user.getLocalAddress());
			gprsEntity.setLocalPort(user.getLocalPort());
			gprsEntity.setLogonDate(user.getLogonDate());
			gprsEntity.setStatus(user.getStatus());

			messageSend.sendObjectMessage(0, gprsEntity,
					MessageNoConstants.GPRSMESSAGE, user.getUserId(), "");
		}

		@Override
		public void received(DDPData data) {
			log.info("Received:" + data.getAddress() + " " + data.getUserId());

		}
	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

}
