package com.jhw.adm.comclient.quartz;

import java.util.ArrayList;
import java.util.Vector;

import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.comclient.service.RmonHandler;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.warning.RmonCount;

/**
 * 
 * @author xiongbo
 * 
 */
public class QuartzJobBeanImpl extends QuartzJobBean {
	private final Logger log = Logger.getLogger(this.getClass().getName());

	// public QuartzJobBeanImpl() {
	// }

	private final String SUCCESS = "S";
	private final String FAIL = "F";

	private MessageSend messageSend;
	private RmonHandler rmonHandler;

	@Override
	protected void executeInternal(JobExecutionContext jec)
			throws JobExecutionException {
		// JobDataMap jobDataMap = jec.getMergedJobDataMap();
		JobDataMap jobDataMap = jec.getTrigger().getJobDataMap();
		int serviceType = jobDataMap.getIntValue(Constants.QUARTZ_SERVICE_TYPE);
		if (serviceType == Constants.QUARTZ_PERFORMANCE) {
			String ip = (String) jobDataMap
					.get(Constants.QUARTZ_PERFORMANCE_IP);
			int portNo = jobDataMap
					.getIntValue(Constants.QUARTZ_PERFORMANCE_PORTNO);
			Vector<String> oids = (Vector<String>) jobDataMap
					.get(Constants.QUARTZ_PERFORMANCE_OIDS);
			String mac = (String) jobDataMap
					.get(Constants.QUARTZ_PERFORMANCE_MAC);
			String client = (String) jobDataMap
					.get(Constants.QUARTZ_PERFORMANCE_CLIENT);
			String clientIp = (String) jobDataMap
					.get(Constants.QUARTZ_PERFORMANCE_CLIENTIP);

			ArrayList<RmonCount> rmonCounts = rmonHandler.getRmonStatData(ip,
					portNo, oids);
			if (rmonCounts != null) {
				int messageType = MessageNoConstants.RTMONITORRES;
				messageSend
						.sendObjectMessageRes(
								com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
								rmonCounts, SUCCESS, messageType, mac, client,
								clientIp);
			}
		} else if (serviceType == Constants.QUARTZ_PING) {
			String ipvlaues = (String) jobDataMap
					.get(Constants.QUARTZ_PING_IPVLAUES);
			int pinglv = jobDataMap.getIntValue(Constants.QUARTZ_PING__PINGLV);
			int pingtimes = jobDataMap
					.getIntValue(Constants.QUARTZ_PING_PINGTIMES);
			int jiange = jobDataMap.getIntValue(Constants.QUARTZ_PING_JIANGE);
			String client = (String) jobDataMap
					.get(Constants.QUARTZ_PING_CLIENT);
			String clientIp = (String) jobDataMap
					.get(Constants.QUARTZ_PING_CLIENTIP);

		}
	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

	public RmonHandler getRmonHandler() {
		return rmonHandler;
	}

	public void setRmonHandler(RmonHandler rmonHandler) {
		this.rmonHandler = rmonHandler;
	}

}
