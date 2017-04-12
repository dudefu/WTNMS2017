package com.jhw.adm.comclient.service;

import java.util.Timer;
import java.util.TimerTask;

import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.comclient.system.Configuration;
import com.jhw.adm.server.entity.util.MessageNoConstants;

/**
 * 
 * @author xiongbo
 * 
 */
public class HeartbeatService extends BaseService {
	private MessageSend messageSend;
	private Timer timer;

	public void start() {
		timer = new Timer(true);
		timer.schedule(new DoTask(), 1000, 12 * 1000);
	}

	public void stop() {
		timer.cancel();
		timer.purge();
	}

	private class DoTask extends TimerTask {
		@Override
		public void run() {
			log.info("Sended heartbeat");
			messageSend.sendTextMessageForHeartbeat("HeartbeatFEP",
					MessageNoConstants.FEPHEARTBEAT, Configuration.fepCode, "");
		}
	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

}
