package com.jhw.adm.comclient.carrier;

import java.util.Properties;

import javax.jms.Message;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.comclient.util.PropertyFileUtil;

/**
 * 
 * @author xiongbo
 * 
 */
public class AutoMonitorSetService {
	private final Logger log = Logger.getLogger(this.getClass().getName());
	public static final String SEND_INTERVAL = "SEND_INTERVAL";
	public static final int INTERVAL = 5;
	public static final String TIME_OUT = "TIMEOUT";
	public static final int TIMEOT = 5;
	public static final String MONITOR_FREQUENCY = "MONITOR_FREQUENCY";
	public static final int FREQUENCY = 5;
	public static final String CONFIG_FILE = "conf/carrier-autoMonitor.ini";
	//
	private MessageSend messageSend;

	public void setMonitorParameter(String client, String clientIp,
			Message message) {

		int monitorFrequency = 0;
		int sendInterval = 0;
		int timeOut = 0;
		// TODO Change Object
		Properties autoMonitorProperties = new Properties();
		autoMonitorProperties.setProperty(MONITOR_FREQUENCY, Integer
				.toString(monitorFrequency));
		autoMonitorProperties.setProperty(SEND_INTERVAL, Integer
				.toString(sendInterval));
		autoMonitorProperties.setProperty(TIME_OUT, Integer.toString(timeOut));
		PropertyFileUtil.save(autoMonitorProperties, CONFIG_FILE,
				"autoMonitor config");
	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

}
