package com.jhw.adm.comclient.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;
import org.shortpasta.icmp.IcmpPingResponse;
import org.shortpasta.icmp.IcmpUtil;

import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.comclient.system.Configuration;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.PingResult;
import com.jhw.adm.server.entity.warning.FaultDetection;
import com.jhw.adm.server.entity.warning.FaultDetectionRecord;
import com.jhw.adm.server.entity.warning.TrapWarningEntity;

/**
 * Also Fault Detecte
 * 
 * @author xiongbo
 * 
 */
public class PingService extends BaseService {
	private final Logger log = Logger.getLogger(this.getClass().getName());

	//
	// private Scheduler quartzScheduler;
	// private JobDetail jobDetail;

	private MessageSend messageSend;
	//
	// private Map<String, List<RunThread>> threadMap = new HashMap<String,
	// List<RunThread>>();
	private ExecutorService executorService;

	/**
	 * 定时ping
	 * 
	 * @param ip
	 * @param client
	 * @param clientIp
	 * @param message
	 */
	public synchronized void startTimingPing(String ip, final String client,
			final String clientIp, Message message) {
		final List<String> ipList = getIpList(message);
		int times = 0;
		int delay = 0;
		boolean isWarning = true; // 是否把告警信息直接显示的客户端的控制台上
		try {
			times = message.getIntProperty(Constants.PING_TIMES);
			delay = message.getIntProperty(Constants.PING_JIANGE);
			isWarning = message.getBooleanProperty(Constants.PING_WARNING);
		} catch (JMSException e) {
			log.error(e);
			log.warn("Because of Exception,so RETURN.");
			return;
		}
		final int times2 = times;
		final int delay2 = delay;
		final boolean isWarning2 = isWarning;

		if (ipList != null && ipList.size() > 0) {
			Runnable runnable = new Runnable() {
				public void run() {
					executorService = Executors.newCachedThreadPool();
					log.info("TimingPing->timeCount：" + times2 + " delay："
							+ delay2 + "Second");
					timePing(ipList, times2, delay2, isWarning2, client,
							clientIp);
					executorService.shutdown();
					executorService = null;
					log.info("TimingPing finish.");
					ipList.clear();
				}
			};
			new Thread(runnable).start();
		}
	}

	// class TimingPingTask extends Thread {
	// int timeout = 1000;// 超时
	//
	// int times;
	// int delay;
	// boolean isWarning = true;
	// List<String> ipList = null;
	// String client;
	// String clientIp;
	//
	// public TimingPingTask(int times, int delay, List<String> ipList,
	// boolean isWarning, String client, String clientIp) {
	// this.times = times;
	// this.delay = delay;
	// this.isWarning = isWarning;
	// this.ipList = ipList;
	// this.client = client;
	// this.clientIp = clientIp;
	// }

	public void timePing(List<String> ipList, final int times, final int delay,
			final boolean isWarning, final String client, final String clientIp) {
		final CountDownLatch countDownLatch = new CountDownLatch(ipList.size());
		for (final String ip : ipList) {
			final Runnable runnable = new Runnable() {
				public void run() {
					for (int i = 0; i < times; i++) {
						IcmpPingResponse icmpPingResponse = null;
						try {
							icmpPingResponse = IcmpUtil.executeIcmpPingRequest(
									ip, 32, 1000);
						} catch (InterruptedException e) {
							log.error(e);
						}
						String line = "";
						int resultType = 0;
						if (icmpPingResponse != null
								&& icmpPingResponse.getResponseIpAddress() != null) {
							line = "Reply from " + ip
									+ ": bytes=32 time=1ms TTL=64";
							resultType = Constants.PINGIN;
						} else {
							resultType = Constants.PINGOUT;
							line = "Request timed out.";
						}
						sendPingMsg(resultType, line, ip, client, clientIp);
						if (isWarning) {
							sendPingResultWithTrap(ip, line, resultType);
						}
						try {
							Thread.sleep(delay * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						log.info("----------timePing-------------:" + i);
					}
					countDownLatch.countDown();
				}
			};
			executorService.execute(runnable);
		}
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
		}
	}

	public void realTimePing(List<PingResult> pingResults, final int times,
			final int delay, final String client, final String clientIp) {
		final CountDownLatch countDownLatch = new CountDownLatch(
				pingResults.size());
		for (final PingResult pingResult : pingResults) {
			final Runnable runnable = new Runnable() {
				public void run() {
					String ip = pingResult.getIpValue();
					for (int i = 0; i < times; i++) {
						IcmpPingResponse icmpPingResponse = null;
						try {
							icmpPingResponse = IcmpUtil.executeIcmpPingRequest(
									ip, 32, 1000);
						} catch (InterruptedException e) {
							log.error(e);
						}
						String line = "";
						int resultType = 0;
						if (icmpPingResponse != null
								&& icmpPingResponse.getResponseIpAddress() != null) {
							line = "Reply from " + ip
									+ ": bytes=32 time=1ms TTL=64";
							resultType = Constants.PINGIN;
						} else {
							resultType = Constants.PINGOUT;
							line = "Request timed out.";
						}
						sendMsg(client, clientIp, pingResult, resultType, line);
						sendPingResultWithTrap(ip, line, resultType);
						try {
							Thread.sleep(delay * 1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						log.info("----------realTimePing-------------:" + i);
					}
					countDownLatch.countDown();
				}
			};
			executorService.execute(runnable);
		}
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
		}
	}

	private void execute(String pingCommandTemplate, String ip,
			boolean isWarning, String client, String clientIp) {
		Runtime r = Runtime.getRuntime();
		BufferedReader in = null;
		String pingCommand = pingCommandTemplate.replace("ip", ip);
		try {
			// 进行ping
			Process p = r.exec(pingCommand);
			if (p != null) {
				in = new BufferedReader(new InputStreamReader(
						p.getInputStream()));
				String line = null;
				while ((line = in.readLine()) != null) {
					Date date = new Date();
					if (line.startsWith("Reply from")) {
						sendPingMsg(PING_SUCCESS, line, ip, client, clientIp);

						// 当为true时，向服务端发消息，并实时显示到客户端的控制台中
						// if (isWarning) {
						// TrapWarningEntity trapWarningEntity = new
						// TrapWarningEntity();
						// trapWarningEntity.setIpValue(ip);
						// trapWarningEntity.setSampleTime(date);
						// trapWarningEntity.setDescs(line);
						// trapWarningEntity.setWarningType(Constants.PINGIN);
						// messageSend.sendObjectMessage(0, trapWarningEntity,
						// MessageNoConstants.TRAPMESSAGE,
						// getLocalIp(), "");
						// }
					} else if (line.startsWith("Request timed")) {
						sendPingMsg(PING_BLOCK, line, ip, client, clientIp);

						// 当为true时，向服务端发消息，并实时显示到客户端的控制台中
						if (isWarning) {
							sendPingResultWithTrap(ip, line, Constants.PINGIN);
						}
					}
				}
			} else {
				sendMsg(client, clientIp, null, 2,
						"Ping request could not find host:" + ip);
			}
		} catch (Exception ex) {
			log.error(getTraceMessage(ex));
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendPingResultWithTrap(String ip, String line, int resultType) {
		TrapWarningEntity trapWarningEntity = new TrapWarningEntity();
		trapWarningEntity.setIpValue(ip);
		trapWarningEntity.setSampleTime(new Date());
		trapWarningEntity.setDescs(line);
		trapWarningEntity.setWarningEvent(resultType);
		messageSend.sendObjectMessage(0, trapWarningEntity,
				MessageNoConstants.TRAPMESSAGE, getLocalIp(), "");
	}

	private String getLocalIp() {
		String localIp = "";
		try {
			localIp = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return localIp;
	}

	/**
	 * 向服务端发ping结果信息
	 */
	private void sendPingMsg(int pingStatus, String line, String ip,
			String client, String clientIp) {
		FaultDetectionRecord faultDetectionRecord = new FaultDetectionRecord();
		faultDetectionRecord.setCreateDate(new Date());
		faultDetectionRecord.setDevice(ip);
		faultDetectionRecord.setStatus(pingStatus);
		faultDetectionRecord.setWarningDescs(line);

		messageSend.sendObjectMessageRes(
				com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
				faultDetectionRecord, "", MessageNoConstants.PING_TIMER, "",
				client, clientIp);
	}

	/**
	 * 实时ping
	 * 
	 * @param ip
	 * @param client
	 * @param clientIp
	 * @param message
	 */
	public synchronized void startPing(String ip, final String client,
			final String clientIp, Message message) {
		final List<PingResult> pingResults = getPingResultsEntity(message);
		if (pingResults != null && pingResults.size() > 0) {

			Runnable runnable = new Runnable() {
				public void run() {
					executorService = Executors.newCachedThreadPool();
					final int times = 5;
					final int delay = 1;
					log.info("TimingPing->timeCount：" + times + " delay："
							+ delay + "Second");
					realTimePing(pingResults, times, delay, client, clientIp);
					executorService.shutdown();
					executorService = null;
					log.info("RealTimePing finish.");
					pingResults.clear();
				}
			};
			new Thread(runnable).start();
		}

	}

	/**
	 * 实时ping停止
	 * 
	 * @param ip
	 * @param client
	 * @param clientIp
	 * @param message
	 */
	public void stopRealPing(String ip, String client, String clientIp,
			Message message) {
		// Object obj = threadMap.get(clientIp);
		// if (obj != null) {
		// List<RunThread> threadList = (List<RunThread>) obj;
		// for (RunThread runThread : threadList) {
		// if (runThread.getRun() == true) {
		// // runThread.interrupt();
		// runThread.setRun(false);
		// }
		// // this.status = false;
		// //
		// // timer.cancel();
		// // timer.purge();
		// }
		// // Timer timer = ((Timer) obj);
		// // timer.cancel();
		// // timer.purge();
		// threadMap.remove(clientIp);
		// } else {
		// log.warn("Not found Task");
		// }

		if (executorService != null) {
			executorService.shutdown();
			executorService = null;
			log.info("***Stop RealPing***");
		}
	}

	private List<PingResult> getPingResultsEntity(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<PingResult> pingResults = null;
		try {
			pingResults = (List<PingResult>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return pingResults;
	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

	private FaultDetection getFaultDetection(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		FaultDetection faultDetection = null;
		try {
			faultDetection = (FaultDetection) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return faultDetection;
	}

	private List<String> getIpList(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<String> ipList = null;
		try {
			ipList = (List<String>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return ipList;
	}

	// public void schedule(String name, SimpleTrigger simpleTrigger,
	// Date startTime) {
	// schedule(name, simpleTrigger, startTime, Scheduler.DEFAULT_GROUP);
	// }
	//
	// public void schedule(String name, SimpleTrigger simpleTrigger,
	// Date startTime, String group) {
	// schedule(name, simpleTrigger, startTime, null, group);
	// }
	//
	// public void schedule(String name, SimpleTrigger simpleTrigger,
	// Date startTime, Date endTime, String group) {
	// schedule(name, simpleTrigger, startTime, endTime, 0, group);
	// }
	//
	// public void schedule(String name, SimpleTrigger simpleTrigger,
	// Date startTime, Date endTime, int repeatCount, String group) {
	// schedule(name, simpleTrigger, startTime, endTime, 0, 1L, group);
	// }
	//
	// public void schedule(String name, SimpleTrigger simpleTrigger,
	// Date startTime, Date endTime, int repeatCount, long repeatInterval,
	// String group) {
	// try {
	// log.info("Start a task!");
	// quartzScheduler.addJob(jobDetail, true);
	//
	// // SimpleTrigger SimpleTrigger = new SimpleTrigger(name, group,
	// // jobDetail.getName(), Scheduler.DEFAULT_GROUP, startTime,
	// // endTime, repeatCount, repeatInterval);
	//
	// simpleTrigger.setName(name);
	// simpleTrigger.setGroup(group);
	// simpleTrigger.setJobName(jobDetail.getName());
	// simpleTrigger.setJobGroup(Scheduler.DEFAULT_GROUP);
	// simpleTrigger.setStartTime(startTime);
	// simpleTrigger.setEndTime(endTime);
	// simpleTrigger.setRepeatCount(repeatCount);
	// simpleTrigger.setRepeatInterval(repeatInterval);
	//
	// quartzScheduler.scheduleJob(simpleTrigger);
	// quartzScheduler.rescheduleJob(simpleTrigger.getName(),
	// simpleTrigger.getGroup(), simpleTrigger);
	// } catch (SchedulerException e) {
	// throw new RuntimeException(e);
	// }
	// }
	//
	// public boolean removeTrigger(String triggerName) {
	// return this.removeTrigger(triggerName, Scheduler.DEFAULT_GROUP);
	// }
	//
	// public boolean removeTrigger(String triggerName, String group) {
	// try {
	// log.info("Stop a task!");
	// // TODO
	// quartzScheduler.deleteJob(triggerName, group);
	//
	// quartzScheduler.pauseTrigger(triggerName, group);// 停止触发器
	// return quartzScheduler.unscheduleJob(triggerName, group);// 移除触发器
	// } catch (SchedulerException e) {
	// throw new RuntimeException(e);
	// }
	// }
	//
	// public Scheduler getQuartzScheduler() {
	// return quartzScheduler;
	// }
	//
	// public void setQuartzScheduler(Scheduler quartzScheduler) {
	// this.quartzScheduler = quartzScheduler;
	// }
	//
	// public JobDetail getJobDetail() {
	// return jobDetail;
	// }
	//
	// public void setJobDetail(JobDetail jobDetail) {
	// this.jobDetail = jobDetail;
	// }

	// interface RunInterface {
	// void setRun(boolean isRun);
	// }

	class RunThread extends Thread // implements RunInterface
	{
		private List<PingResult> pingResults;
		private String client;
		private String clientIp;
		private boolean isRun = true;
		private final int count = 5;
		private final int timeout = 1000;

		public RunThread(List<PingResult> pingResults, String client,
				String clientIp) {
			this.pingResults = pingResults;
			this.client = client;
			this.clientIp = clientIp;
		}

		public void run() {
			String osName = System.getProperty("os.name").trim();
			String pingCommandTemplate = null;
			if (osName.startsWith("Windows")) {
				pingCommandTemplate = "ping ip -n " + count + " -w " + timeout;
			} else {
				pingCommandTemplate = "ping ip -c " + count + " -w " + timeout;
			}
			// BufferedReader in = null;
			Runtime r = Runtime.getRuntime();
			for (PingResult pingResult : pingResults) {
				if (isRun) {
					ping(pingResult, pingCommandTemplate, r, 5, 500, client,
							clientIp);
				}
			}
			isRun = false;
		}

		public boolean getRun() {
			return isRun;
		}

		public void setRun(boolean isRun) {
			this.isRun = isRun;
		}

	}

	public void ping(PingResult pingResult, String pingCommandTemplate,
			Runtime r, int count, int timeout, String client, String clientIp) {
		// String osName = System.getProperty("os.name").trim();
		// String pingCommandTemplate = null;
		// if (osName.startsWith("Windows")) {
		// pingCommandTemplate = "ping ip -n " + count + " -w " + timeout;
		// } else {
		// pingCommandTemplate = "ping ip -c " + count + " -w " + timeout;
		// }
		BufferedReader in = null;
		// Runtime r = Runtime.getRuntime();
		try {
			// for (PingResult pingResult : pingResults) {
			String pingCommand = pingCommandTemplate.replace("ip",
					pingResult.getIpValue());
			Process p = r.exec(pingCommand);

			if (p != null) {
				in = new BufferedReader(new InputStreamReader(
						p.getInputStream()));
				String line = null;
				while ((line = in.readLine()) != null) {
					if (line.startsWith("Reply from")) {
						// log.info(line);
						sendMsg(client, clientIp, pingResult, 1, line);
					} else if (line.startsWith("Request timed")) {
						// log.info(line);
						sendMsg(client, clientIp, pingResult, 2, line);
					}
				}
				// in.close();
			} else {
				sendMsg(client,
						clientIp,
						pingResult,
						2,
						"Ping request could not find host:"
								+ pingResult.getIpValue());
			}
			// }
		} catch (Exception ex) {
			log.error(getTraceMessage(ex));
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void sendMsg(String client, String clientIp, PingResult pingResult,
			int result, String resultStr) {
		pingResult.setStatus(result);
		pingResult.setDescs(resultStr);
		pingResult.setTime(new Date());
		pingResult.setFepCode(Configuration.fepCode);
		messageSend.sendObjectMessageRes(
				com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
				pingResult, "", MessageNoConstants.PINGRES,
				pingResult.getIpValue(), client, clientIp);
	}
}
