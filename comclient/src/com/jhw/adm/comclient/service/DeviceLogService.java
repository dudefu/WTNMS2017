package com.jhw.adm.comclient.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.jms.MessageHandleIF;
import com.jhw.adm.comclient.jms.MessageQueueHandler;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.comclient.system.Configuration;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.SysLogHostToDevEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.ParmRep;
import com.jhw.adm.server.entity.warning.SysLogWarningEntity;

/**
 * 监听设备日志并处理
 * 
 * @author xiongbo
 * 
 */
public class DeviceLogService extends BaseService {
	private static Logger log = Logger.getLogger(DeviceLogService.class);
	private LinkedList<String> logList;
	private boolean receiveLoop;
	private boolean handelLoop;
	private final Pattern oldLogPattern = Pattern
			.compile("\\s*\\[\\s*(\\S+)\\s*\\]\\s*(.*)");
	// private final Pattern logPattern = Pattern
	// .compile("\\s*\\[\\s*(\\S+)\\s*\\]\\s*\\[\\s*(\\S+)\\s*\\]\\s*\\[\\s*(\\S+)\\s*\\]\\s*(.*)");
	private final Pattern logPattern = Pattern
			.compile("\\s*\\[\\s*(.*)\\s*\\]\\s*\\[\\s*(.*)\\s*\\]\\s*\\[\\s*(.*)\\s*\\]\\s*(.*)");
	private MessageSend messageSend;
	private DeviceLogHandler deviceLogHandler;
	private MessageQueueHandler messageQueueHandler;
	private SystemHandler systemHandler;

	public DeviceLogService() {
		logList = new LinkedList<String>();
	}

	public void init() {
		// syslog配置上载
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESYSLOGHOST,
				logServerConfigUP);
		// 在设备添加log服务器地址：先删后添加
		messageQueueHandler.addHandler(MessageNoConstants.SYSLOGHOSTSAVE,
				createLogServer);
		// 删除log服务器地址
		messageQueueHandler.addHandler(MessageNoConstants.SYSLOGHOSTDELETE,
				deleteLogServer);
	}

	public void launchLogListen() {
		receiveLoop = true;
		handelLoop = true;
		byte[] buffer = new byte[1024];
		try {
			final DatagramSocket udpSocket = new DatagramSocket(
					Configuration.devicelog_listenport);
			final DatagramPacket packet = new DatagramPacket(buffer,
					buffer.length);
			// Receive
			new Thread() {
				@Override
				public void run() {
					log.info("Launching device log listen thread.");
					while (receiveLoop) {
						try {
							udpSocket.receive(packet);
						} catch (IOException e) {
							log.error(e);
						}
						String logmsg = new String(packet.getData(), 0, packet
								.getLength());
						if (logmsg != null) {
							String deviceIP = packet.getAddress().toString();
							if (deviceIP.indexOf("/") != -1) {
								deviceIP = deviceIP.substring(1);
							}
							log.info("From device syslog: " + logmsg);
							logmsg = "[" + deviceIP + "]" + logmsg;
							synchronized (logList) {
								if (logList.add(logmsg)) {
									logList.notify();
								}
							}
						}
					}
				}
			}.start();
		} catch (SocketException e) {
			log.error(e);
		}
		// Handle
		new Thread() {
			@Override
			public void run() {
				log.info("Launching handle device log thread.");
				while (handelLoop) {
					String logmsg = null;
					synchronized (logList) {
						while (logList.size() == 0) {
							try {
								logList.wait();
							} catch (InterruptedException e) {
								log.error(e);
							}
						}
						logmsg = logList.removeFirst();
					}
					if (logmsg != null) {
						handle(logmsg);
					}
				}
			}
		}.start();
	}

	private void handle(String logmsg) {
		Matcher matcher = logPattern.matcher(logmsg);
		if (matcher.find()) {
			String deviceIP = matcher.group(1);
			String serviceType = matcher.group(2);
			String operater = matcher.group(3);
			String msg = matcher.group(4);
			log.info(deviceIP + " " + serviceType + " " + operater + " " + msg);

			SysLogWarningEntity sysLogWarningEntity = new SysLogWarningEntity();
			sysLogWarningEntity.setSyslogType(serviceType);
			sysLogWarningEntity.setContents(msg);
			sysLogWarningEntity.setIpValue(deviceIP);
			sysLogWarningEntity.setManageUser(operater);
			sysLogWarningEntity.setCurrentTime(new Date());
			messageSend.sendObjectMessage(Constants.DEV_SWITCHER2,
					sysLogWarningEntity, MessageNoConstants.SYSLOGMESSAGE,
					deviceIP, "");
		}
	}

	public void clear() {
		logList.clear();
		receiveLoop = false;
		handelLoop = false;
	}

	// syslog配置上载
	private MessageHandleIF logServerConfigUP = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();
					HashMap<Class, List> synMap = new HashMap<Class, List>();
					List<SysLogHostToDevEntity> syslogConfigList = deviceLogHandler
							.getSyslogConfig(deviceIp);
					String STATE = SUCCESS;
					if (syslogConfigList != null
							&& syslogConfigList.size() == 0) {
						synMap.put(SysLogHostToDevEntity.class, null);
					} else if (syslogConfigList != null) {
						synMap.put(SysLogHostToDevEntity.class,
								syslogConfigList);
					} else {
						STATE = FAIL;
					}
					messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
							MessageNoConstants.SINGLESYNCHREP, synchDevice
									.getIpvalue(), client, clientIp);
				}
				messageSend.sendTextMessageRes("上载完成", SUCCESS,
						MessageNoConstants.SYNCHFINISH, ip, client, clientIp);
			}
		}
	};

	// 在设备配置log服务器地址.如有相同hostid,则覆盖以前的hostid
	private MessageHandleIF createLogServer = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			List<SysLogHostToDevEntity> syslogConfigs = getSyslogConfigList(message);
			if (syslogConfigs != null) {
				for (SysLogHostToDevEntity sysLogHostToDevEntity : syslogConfigs) {
					boolean result = deviceLogHandler.createSyslogServer(
							sysLogHostToDevEntity.getIpValue(),
							sysLogHostToDevEntity);

					ParmRep parmRep = handleSyslogConfig(syslogConfigs, result);
					parmRep.setDescs(INSERT);
					messageSend
							.sendObjectMessageRes(
									com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
									parmRep, ip, client, clientIp);
				}
				// Save config
				saveConfig(ip, systemHandler);
			}
		}
	};

	// 删除log服务器地址
	private MessageHandleIF deleteLogServer = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			List<SysLogHostToDevEntity> syslogConfigs = getSyslogConfigList(message);
			if (syslogConfigs != null) {
				for (SysLogHostToDevEntity sysLogHostToDevEntity : syslogConfigs) {
					boolean result = deviceLogHandler.deleteSyslogServer(
							sysLogHostToDevEntity.getIpValue(),
							sysLogHostToDevEntity);
					ParmRep parmRep = handleSyslogConfig(syslogConfigs, result);
					parmRep.setDescs(DELETE);
					messageSend
							.sendObjectMessageRes(
									com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
									parmRep, ip, client, clientIp);
				}
				// Save config
				saveConfig(ip, systemHandler);
			}
		}
	};

	private ParmRep handleSyslogConfig(
			List<SysLogHostToDevEntity> syslogConfigs, boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (SysLogHostToDevEntity sysLogHostToDevEntity : syslogConfigs) {
			parmIds.add(sysLogHostToDevEntity.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(SysLogHostToDevEntity.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private List<SysLogHostToDevEntity> getSyslogConfigList(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<SysLogHostToDevEntity> syslogConfigs = null;
		try {
			syslogConfigs = (List<SysLogHostToDevEntity>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return syslogConfigs;
	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

	public void setDeviceLogHandler(DeviceLogHandler deviceLogHandler) {
		this.deviceLogHandler = deviceLogHandler;
	}

	public void setMessageQueueHandler(MessageQueueHandler messageQueueHandler) {
		this.messageQueueHandler = messageQueueHandler;
	}

	public void setSystemHandler(SystemHandler systemHandler) {
		this.systemHandler = systemHandler;
	}

}
