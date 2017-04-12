package com.jhw.adm.comclient.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import com.jhw.adm.comclient.jms.MessageHandleIF;
import com.jhw.adm.comclient.jms.MessageQueueHandler;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.ParmRep;
import com.jhw.adm.server.entity.warning.PortRemon;
import com.jhw.adm.server.entity.warning.RmonThing;
import com.jhw.adm.server.entity.warning.RmonWarningConfig;

/**
 * 
 * @author xiongbo
 * 
 */
public class RmonService extends BaseService {
	private MessageSend messageSend;
	private RmonHandler rmonHandler;
	private SystemHandler systemHandler;
	private MessageQueueHandler messageQueueHandler;

	public void init() {
		// Only single parameter upload
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHPORTWARN,
				getRmonAlarmListHandle);
		messageQueueHandler.addHandler(
				MessageNoConstants.SINGLESWITCHEVENTGROUP,
				getRmonEventListHandle);
		messageQueueHandler.addHandler(
				MessageNoConstants.SINGLESWITCHPORTREMON,
				getRmonStatEntryHandle);
	}

	private MessageHandleIF getRmonStatEntryHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();
					HashMap<Class, List> synMap = new HashMap<Class, List>();
					List<PortRemon> rmonCounts = rmonHandler
							.getRmonStatEntry(deviceIp);
					// String mac = systemHandler.getMAC(deviceIp);
					String STATE = SUCCESS;
					if (rmonCounts != null && rmonCounts.size() == 0) {
						synMap.put(PortRemon.class, null);
					} else if (rmonCounts != null) {
						synMap.put(PortRemon.class, rmonCounts);
					} else {
						STATE = FAIL;
					}
					messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
							MessageNoConstants.SINGLESYNCHREP,
							synchDevice.getIpvalue(), client, clientIp);
				}
			}
		}

	};
	private MessageHandleIF getRmonAlarmListHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();
					HashMap<Class, List> synMap = new HashMap<Class, List>();
					List<RmonWarningConfig> rmonAlarms = rmonHandler
							.getRmonAlarmList(deviceIp);
					// String mac = systemHandler.getMAC(deviceIp);
					String STATE = SUCCESS;
					if (rmonAlarms != null && rmonAlarms.size() == 0) {
						synMap.put(RmonWarningConfig.class, null);
					} else if (rmonAlarms != null) {
						synMap.put(RmonWarningConfig.class, rmonAlarms);
					} else {
						STATE = FAIL;
					}
					messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
							MessageNoConstants.SINGLESYNCHREP,
							synchDevice.getIpvalue(), client, clientIp);
				}
			}
		}

	};
	private MessageHandleIF getRmonEventListHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();
					HashMap<Class, List> synMap = new HashMap<Class, List>();
					List<RmonThing> rmonEvents = rmonHandler
							.getRmonEventList(deviceIp);
					// String mac = systemHandler.getMAC(deviceIp);
					String STATE = SUCCESS;
					if (rmonEvents != null && rmonEvents.size() == 0) {
						synMap.put(RmonThing.class, null);
					} else if (rmonEvents != null) {
						synMap.put(RmonThing.class, rmonEvents);
					} else {
						STATE = FAIL;
					}
					messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
							MessageNoConstants.SINGLESYNCHREP,
							synchDevice.getIpvalue(), client, clientIp);
				}
			}
		}

	};

	public void addRmonStatic(String ip, String client, String clientIp,
			Message message) {
		PortRemon portRemon = getPortRemon(message);
		if (portRemon != null) {
			boolean result = rmonHandler.addRmonStatic(ip, portRemon);
			List<PortRemon> portRemons = new ArrayList<PortRemon>(1);
			portRemons.add(portRemon);
			ParmRep parmRep = handlePortRemon(portRemons, result);
			parmRep.setDescs(INSERT);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}

	}

	public void addRmonEvent(String ip, String client, String clientIp,
			Message message) {
		List<RmonThing> rmonThingList = getRmonThing(message);
		if (rmonThingList != null) {
			boolean result = true;
			for (RmonThing rmonThing : rmonThingList) {
				result = rmonHandler.addRmonEvent(ip, rmonThing);
				if (!result) {
					break;
				}
			}
			// List<RmonThing> rmonThings = new ArrayList<RmonThing>(1);
			// rmonThings.add(rmonThing);
			ParmRep parmRep = handleRmonThing(rmonThingList, result);
			parmRep.setDescs(INSERT);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	public void addRmonAlarm(String ip, String client, String clientIp,
			Message message) {
		List<RmonWarningConfig> rmonWarningConfigList = getRmonWarningConfig(message);
		if (rmonWarningConfigList != null) {
			boolean result = true;
			for (RmonWarningConfig rmonWarningConfig : rmonWarningConfigList) {
				result = rmonHandler.addRmonAlarm(ip, rmonWarningConfig);
				if (!result) {
					break;
				}
			}
			// List<RmonWarningConfig> rmonWarningConfigs = new
			// ArrayList<RmonWarningConfig>(
			// 1);
			// rmonWarningConfigs.add(rmonWarningConfig);
			ParmRep parmRep = handleRmonWarningConfig(rmonWarningConfigList,
					result);
			parmRep.setDescs(INSERT);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	public void deleteRmonAlarm(String ip, String client, String clientIp,
			Message message) {
		List<RmonWarningConfig> rmonWarningConfigList = getRmonWarningConfig(message);
		if (rmonWarningConfigList != null) {
			boolean result = true;
			for (RmonWarningConfig rmonWarningConfig : rmonWarningConfigList) {
				result = rmonHandler.deleteRmonAlarm(ip, rmonWarningConfig);
				if (!result) {
					break;
				}
			}
			// List<RmonWarningConfig> rmonWarningConfigs = new
			// ArrayList<RmonWarningConfig>(
			// 1);
			// rmonWarningConfigs.add(rmonWarningConfig);
			ParmRep parmRep = handleRmonWarningConfig(rmonWarningConfigList,
					result);
			parmRep.setDescs(DELETE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	public void deleteRmonEvent(String ip, String client, String clientIp,
			Message message) {
		List<RmonThing> rmonThingList = getRmonThing(message);
		if (rmonThingList != null) {
			boolean result = true;
			for (RmonThing rmonThing : rmonThingList) {
				result = rmonHandler.deleteRmonEvent(ip, rmonThing);
				if (!result) {
					break;
				}
			}
			// List<RmonThing> rmonThings = new ArrayList<RmonThing>(1);
			// rmonThings.add(rmonThing);
			ParmRep parmRep = handleRmonThing(rmonThingList, result);
			parmRep.setDescs(DELETE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	public void deleteRmonStatic(String ip, String client, String clientIp,
			Message message) {
		PortRemon portRemon = getPortRemon(message);
		if (portRemon != null) {
			boolean result = rmonHandler.deleteRmonStatic(ip, portRemon);
			List<PortRemon> portRemons = new ArrayList<PortRemon>(1);
			portRemons.add(portRemon);
			ParmRep parmRep = handlePortRemon(portRemons, result);
			parmRep.setDescs(DELETE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}

	}

	private PortRemon getPortRemon(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		PortRemon portRemon = null;
		try {
			portRemon = (PortRemon) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return portRemon;
	}

	private List<RmonWarningConfig> getRmonWarningConfig(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<RmonWarningConfig> rmonWarningConfigList = null;
		try {
			rmonWarningConfigList = (List<RmonWarningConfig>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return rmonWarningConfigList;
	}

	private List<RmonThing> getRmonThing(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<RmonThing> rmonThingList = null;
		try {
			rmonThingList = (List<RmonThing>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return rmonThingList;
	}

	private ParmRep handlePortRemon(List<PortRemon> portRemons, boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (PortRemon portRemon : portRemons) {
			parmIds.add(portRemon.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(PortRemon.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private ParmRep handleRmonWarningConfig(
			List<RmonWarningConfig> rmonWarningConfigs, boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (RmonWarningConfig rmonWarningConfig : rmonWarningConfigs) {
			parmIds.add(rmonWarningConfig.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(RmonWarningConfig.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private ParmRep handleRmonThing(List<RmonThing> rmonThings, boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (RmonThing rmonThing : rmonThings) {
			parmIds.add(rmonThing.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(RmonThing.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
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

	public SystemHandler getSystemHandler() {
		return systemHandler;
	}

	public void setSystemHandler(SystemHandler systemHandler) {
		this.systemHandler = systemHandler;
	}

	public MessageQueueHandler getMessageQueueHandler() {
		return messageQueueHandler;
	}

	public void setMessageQueueHandler(MessageQueueHandler messageQueueHandler) {
		this.messageQueueHandler = messageQueueHandler;
	}

}
