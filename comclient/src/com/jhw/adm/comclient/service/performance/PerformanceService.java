package com.jhw.adm.comclient.service.performance;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;

import com.jhw.adm.comclient.data.Constants;
import com.jhw.adm.comclient.data.OID;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.comclient.service.BaseService;
import com.jhw.adm.comclient.service.RmonHandler;
import com.jhw.adm.comclient.service.SystemHandler;
import com.jhw.adm.server.entity.warning.RTMonitorConfig;
import com.jhw.adm.server.entity.warning.RmonCount;

/**
 * ÐÔÄÜ¼à¿Ø
 * 
 * @author xiongbo
 * 
 */
public class PerformanceService extends BaseService {
	private static Logger log = Logger.getLogger(PerformanceService.class);
	private RmonHandler rmonHandler;
	// private MessageSend messageSend;
	private SystemHandler systemHandler;
	//
	private Scheduler quartzScheduler;
	private JobDetail jobDetail;

	private MessageSend messageSend;
	//
	private Map<String, String> parameterOidMap = new HashMap<String, String>(
			16);

	private List<MonitorTask> timingMonitorList = new ArrayList<MonitorTask>();

	private final static long INTERVAL = 1 * 5 * 1000L;

	// private Map<String, Integer> taskMap = new HashMap<String, Integer>();

	public PerformanceService() {
		parameterOidMap.put(com.jhw.adm.server.entity.util.Constants.octets,
				OID.RMON_STOCTETS);
		parameterOidMap.put(com.jhw.adm.server.entity.util.Constants.packets,
				OID.RMON_STPACKETS);
		parameterOidMap.put(
				com.jhw.adm.server.entity.util.Constants.bcast_pkts,
				OID.RMON_STBCASTPKTS);
		parameterOidMap.put(
				com.jhw.adm.server.entity.util.Constants.mcast_pkts,
				OID.RMON_STMCASTPKTS);
		parameterOidMap.put(com.jhw.adm.server.entity.util.Constants.crc_align,
				OID.RMON_STCRCALIGN);
		parameterOidMap.put(com.jhw.adm.server.entity.util.Constants.undersize,
				OID.RMON_STUNDERSIZE);
		parameterOidMap.put(com.jhw.adm.server.entity.util.Constants.oversize,
				OID.RMON_STOVERSIZE);
		parameterOidMap.put(com.jhw.adm.server.entity.util.Constants.fragments,
				OID.RMON_STFRAGMENTS);
		parameterOidMap.put(com.jhw.adm.server.entity.util.Constants.jabbers,
				OID.RMON_STJABBERS);
		parameterOidMap.put(
				com.jhw.adm.server.entity.util.Constants.collisions,
				OID.RMON_STCOLLISIONS);
		parameterOidMap.put(com.jhw.adm.server.entity.util.Constants.pkts_64,
				OID.RMON_STPKTS64);
		parameterOidMap.put(
				com.jhw.adm.server.entity.util.Constants.pkts_65_127,
				OID.RMON_STPKTS65TO127);
		parameterOidMap.put(
				com.jhw.adm.server.entity.util.Constants.pkts_128_255,
				OID.RMON_STPKTS128TO255);
		parameterOidMap.put(
				com.jhw.adm.server.entity.util.Constants.pkts_256_511,
				OID.RMON_STPKTS256TO511);
		parameterOidMap.put(
				com.jhw.adm.server.entity.util.Constants.pkts_512_1023,
				OID.RMON_STPKTS512TO1023);
		parameterOidMap.put(
				com.jhw.adm.server.entity.util.Constants.pkts_1024_1518,
				OID.RMON_STPKTS1024TO1518);
	}

	public void startMonitor(String ip, String client, String clientIp,
			Message message, int messageType) {
		RTMonitorConfig rtMonitorConfig = getRTMonitorConfig(message);
		// if (taskMap.get(clientIp) == null) {
		if (rtMonitorConfig != null) {
			int index = rmonHandler.getPortIndex(rtMonitorConfig.getIpValue(),
					rtMonitorConfig.getPortNo());
			Vector<String> oids = new Vector<String>();
			for (String parm : rtMonitorConfig.getParms()) {
				oids.add(parameterOidMap.get(parm) + "." + index);
			}
			if (index != 0 && oids.size() != 0) {
				String mac = systemHandler.getMAC(rtMonitorConfig.getIpValue());
				SimpleTrigger simpleTrigger = new SimpleTrigger();
				JobDataMap jobDataMap = simpleTrigger.getJobDataMap();
				jobDataMap.put(Constants.QUARTZ_SERVICE_TYPE,
						Constants.QUARTZ_PERFORMANCE);
				//
				jobDataMap.put(Constants.QUARTZ_PERFORMANCE_IP,
						rtMonitorConfig.getIpValue());
				jobDataMap.put(Constants.QUARTZ_PERFORMANCE_PORTNO,
						rtMonitorConfig.getPortNo());
				jobDataMap.put(Constants.QUARTZ_PERFORMANCE_OIDS, oids);
				jobDataMap.put(Constants.QUARTZ_PERFORMANCE_MAC, mac);
				jobDataMap.put(Constants.QUARTZ_PERFORMANCE_CLIENT, client);
				jobDataMap.put(Constants.QUARTZ_PERFORMANCE_CLIENTIP, clientIp);

				long clientInterval = rtMonitorConfig.getEndTime()
						- rtMonitorConfig.getBeginTime();
				Calendar c = Calendar.getInstance();
				long nowLong = System.currentTimeMillis();
				c.setTimeInMillis(nowLong);
				Date startTime = c.getTime();
				c.setTimeInMillis(nowLong + clientInterval + 1000);// 1000 is
				// Correct
				Date endTime = c.getTime();

				int interval = rtMonitorConfig.getTimeStep();
				this.schedule(clientIp, simpleTrigger, startTime, endTime,
						interval * 1000, interval * 1000);

				log.info("start:" + startTime + " endTime:" + endTime);

				// Timer timer = new Timer();
				// RunTask runTask = new
				// RunTask(rtMonitorConfig.getIpValue(),
				// rtMonitorConfig.getPortNo(), oids, mac, client,
				// clientIp);
				// timer.schedule(runTask,
				// rtMonitorConfig.getTimeStep() * 1000,
				// rtMonitorConfig.getTimeStep() * 1000);
				// taskMap.put(clientIp, timer);
			}
		}
		// } else {
		// log.warn("Have monitored");
		// }
	}

	public void startTimingMonitor(String ip, String client, String clientIp,
			Message message, int messageType) {
		RTMonitorConfig rtMonitorConfig = getRTMonitorConfig(message);
		if (!timingMonitorList.contains(rtMonitorConfig)) {
			Timer timer = new Timer();
			MonitorTask monitorTask = new MonitorTask(ip, client, clientIp,
					rtMonitorConfig, messageType, rtMonitorConfig.getId());
			timer.schedule(monitorTask, 0L, INTERVAL);

			timingMonitorList.add(monitorTask);
		}
	}

	public void deleteTimingMonitor(String ip, String client, String clientIp,
			Message message, int messageType) {
		try {
			RTMonitorConfig rtMonitorConfig = getRTMonitorConfig(message);

			List<MonitorTask> middleList = new ArrayList<MonitorTask>(
					timingMonitorList);

			for (MonitorTask monitorTask : middleList) {
				if (rtMonitorConfig.getId() == monitorTask.getID()) {
					monitorTask.cancel();
					timingMonitorList.remove(monitorTask);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			log.info("Delete TimingMonitor Task is Exception" + ex.getMessage());
		}

	}

	class MonitorTask extends TimerTask {
		String ip;
		String client;
		String clientIp;
		RTMonitorConfig rtMonitorConfig;
		int messageType;
		long id;
		private long baseValue;

		boolean isFirst = true;

		public MonitorTask(String ip, String client, String clientIp,
				RTMonitorConfig rtMonitorConfig, int messageType, long id) {
			this.ip = ip;
			this.client = client;
			this.clientIp = clientIp;
			this.rtMonitorConfig = rtMonitorConfig;
			this.messageType = messageType;
			this.id = id;
		}

		public void run() {
			try {
				if (rtMonitorConfig != null) {
					int index = rmonHandler.getPortIndex(
							rtMonitorConfig.getIpValue(),
							rtMonitorConfig.getPortNo());
					Vector<String> oids = new Vector<String>();
					for (String parm : rtMonitorConfig.getParms()) {
						oids.add(parameterOidMap.get(parm) + "." + index);
					}
					if (index != 0 && oids.size() != 0) {
						String mac = systemHandler.getMAC(rtMonitorConfig
								.getIpValue());
						int portNo = rtMonitorConfig.getPortNo();
						String ipValue = rtMonitorConfig.getIpValue();
						ArrayList<RmonCount> rmonCounts = rmonHandler
								.getRmonStatData(ipValue, portNo, oids);
						if (rmonCounts != null) {
							for (RmonCount rmonCount : rmonCounts) {
								if (isFirst) {
									baseValue = rmonCount.getValue();
									isFirst = false;
								}
								long lastValue = rmonCount.getValue();
								long value = lastValue - baseValue;
								rmonCount.setValue(value);
								baseValue = lastValue;

								log.info("Performance Monitor£º"
										+ rmonCount.getSampleTime() + " "
										+ value);
							}
							messageSend
									.sendObjectMessageRes(
											com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
											rmonCounts, SUCCESS, messageType,
											mac, client, clientIp);
						}
					}
					System.err.println("id == " + id);
					long nowTime = new Date().getTime();
					long endTime = rtMonitorConfig.getEndTime();
					if (nowTime > endTime) {
						this.cancel();
						return;
					}
				}
			} catch (Exception ex) {
				log.info("Add TimingMonitor Task is Exception"
						+ ex.getMessage());
				return;
			}
		}

		public long getID() {
			return this.id;
		}
	}

	public void stopMonitor(String ip, String client, String clientIp,
			Message message) {
		// RTMonitorConfig rtMonitorConfig = getRTMonitorConfig(message);
		// Object obj = taskMap.get(clientIp);
		// if (obj != null) {
		// Timer timer = ((Timer) obj);
		// timer.cancel();
		// timer.purge();

		removeTrigger(clientIp);

		// taskMap.remove(clientIp);
		// } else {
		// log.warn("Not found Task");
		// }
	}

	private RTMonitorConfig getRTMonitorConfig(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		RTMonitorConfig rtMonitorConfig = null;
		try {
			rtMonitorConfig = (RTMonitorConfig) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return rtMonitorConfig;
	}

	// class RunTask extends TimerTask {
	// private String ip;
	// private int portNo;
	// private Vector<String> oids;
	// //
	// private String mac;
	// private String client;
	// private String clientIp;
	//
	// public RunTask(String ip, int portNo, Vector<String> oids, String mac,
	// String client, String clientIp) {
	// this.ip = ip;
	// this.portNo = portNo;
	// this.oids = oids;
	// //
	// this.mac = mac;
	// this.client = client;
	// this.clientIp = clientIp;
	// }
	//
	// public void run() {
	// ArrayList<RmonCount> rmonCounts = rmonHandler.getRmonStatData(ip,
	// portNo, oids);
	// if (rmonCounts != null) {
	// messageSend.sendObjectMessageRes(rmonCounts, SUCCESS,
	// MessageNoConstants.RTMONITORRES, mac, client, clientIp);
	// }
	// }
	// }

	// public void schedule(String name, Date startTime, Date endTime,
	// int repeatCount) {
	// schedule(name, startTime, endTime, 0, Scheduler.DEFAULT_GROUP);
	// }
	//
	// public void schedule(String name, Date startTime, Date endTime,
	// int repeatCount, String group) {
	// schedule(name, startTime, endTime, 0, 1L, group);
	// }

	public void schedule(String name, SimpleTrigger simpleTrigger,
			Date startTime, Date endTime, int repeatCount, long repeatInterval) {
		if (StringUtils.isNotEmpty(name)) {
			this.schedule(name, simpleTrigger, startTime, endTime, repeatCount,
					repeatInterval, Scheduler.DEFAULT_GROUP);
		} else {
			log.error("No name");
		}
	}

	public void schedule(String name, SimpleTrigger simpleTrigger,
			Date startTime, Date endTime, int repeatCount, long repeatInterval,
			String group) {
		try {
			log.info("Start a task!");
			quartzScheduler.addJob(jobDetail, true);

			// SimpleTrigger SimpleTrigger = new SimpleTrigger(name, group,
			// jobDetail.getName(), Scheduler.DEFAULT_GROUP, startTime,
			// endTime, repeatCount, repeatInterval);

			simpleTrigger.setName(name);
			simpleTrigger.setGroup(group);
			simpleTrigger.setJobName(jobDetail.getName());
			simpleTrigger.setJobGroup(Scheduler.DEFAULT_GROUP);
			simpleTrigger.setStartTime(startTime);
			simpleTrigger.setEndTime(endTime);
			simpleTrigger.setRepeatCount(repeatCount);
			simpleTrigger.setRepeatInterval(repeatInterval);

			quartzScheduler.scheduleJob(simpleTrigger);
			quartzScheduler.rescheduleJob(simpleTrigger.getName(),
					simpleTrigger.getGroup(), simpleTrigger);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean removeTrigger(String triggerName) {
		return this.removeTrigger(triggerName, Scheduler.DEFAULT_GROUP);
	}

	public boolean removeTrigger(String triggerName, String group) {
		try {
			log.info("Stop a task!");
			// TODO
			quartzScheduler.deleteJob(triggerName, group);

			quartzScheduler.pauseTrigger(triggerName, group);// Í£Ö¹´¥·¢Æ÷
			return quartzScheduler.unscheduleJob(triggerName, group);// ÒÆ³ý´¥·¢Æ÷
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public void pauseTrigger(String triggerName, String group) {
		try {
			quartzScheduler.pauseTrigger(triggerName, group);// Í£Ö¹´¥·¢Æ÷
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}

	public RmonHandler getRmonHandler() {
		return rmonHandler;
	}

	public void setRmonHandler(RmonHandler rmonHandler) {
		this.rmonHandler = rmonHandler;
	}

	// public MessageSend getMessageSend() {
	// return messageSend;
	// }
	//
	// public void setMessageSend(MessageSend messageSend) {
	// this.messageSend = messageSend;
	// }

	public SystemHandler getSystemHandler() {
		return systemHandler;
	}

	public void setSystemHandler(SystemHandler systemHandler) {
		this.systemHandler = systemHandler;
	}

	public Scheduler getQuartzScheduler() {
		return quartzScheduler;
	}

	public void setQuartzScheduler(Scheduler quartzScheduler) {
		this.quartzScheduler = quartzScheduler;
	}

	public JobDetail getJobDetail() {
		return jobDetail;
	}

	public void setJobDetail(JobDetail jobDetail) {
		this.jobDetail = jobDetail;
	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

}
