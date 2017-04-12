package com.jhw.adm.comclient.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.jms.MessageHandleIF;
import com.jhw.adm.comclient.jms.MessageQueueHandler;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.ports.QOSPriority;
import com.jhw.adm.server.entity.ports.QOSSpeedConfig;
import com.jhw.adm.server.entity.ports.QOSStormControl;
import com.jhw.adm.server.entity.ports.QOSSysConfig;
import com.jhw.adm.server.entity.switchs.Priority802D1P;
import com.jhw.adm.server.entity.switchs.PriorityDSCP;
import com.jhw.adm.server.entity.switchs.PriorityTOS;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.ParmRep;

/**
 * QOS
 * 
 * @author xiongbo
 * 
 */
public class QosService extends BaseService {
	private static Logger log = Logger.getLogger(QosService.class);
	private QosHandler qosHandler;
	private MessageSend messageSend;
	private SystemHandler systemHandler;
	private MessageQueueHandler messageQueueHandler;

	public void init() {
		// Only single parameter upload
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHQOSSTORM,
				getStormPortHandle);
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHQOSPORT,
				getTrafficPortHandle);
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHQOSSYS,
				getQosSysConfigHandle);
		messageQueueHandler.addHandler(
				MessageNoConstants.SINGLESWITCHQOSPRIORITY802D1P,
				getQosDot1PriorityHandle);
		messageQueueHandler.addHandler(
				MessageNoConstants.SINGLESWITCHQOSPRIORITYDSCP,
				getDscpPriorityHandle);
		messageQueueHandler.addHandler(
				MessageNoConstants.SINGLESWITCHQOSPRIORITYTOS,
				getTosPriorityHandle);
	}

	public void configSysQos(String ip, String client, String clientIp,
			Message message) {
		QOSSysConfig qosSysConfig = getQOSSysConfig(message);
		if (null != qosSysConfig) {
			boolean result = qosHandler.configQos(ip, qosSysConfig);

			List<QOSSysConfig> qosSysConfigs = new ArrayList<QOSSysConfig>();
			qosSysConfigs.add(qosSysConfig);
			ParmRep parmRep = handleQOSSysConfig(qosSysConfigs, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	public void configQosDot1Priority(String ip, String client,
			String clientIp, Message message) {
		List<Priority802D1P> priority802D1Ps = getPriority802D1P(message);
		if (null != priority802D1Ps) {
			boolean result = qosHandler.configQosDot1Priority(ip,
					priority802D1Ps);

			ParmRep parmRep = handlePriority802D1P(priority802D1Ps, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	public void configTosPriority(String ip, String client, String clientIp,
			Message message) {
		List<PriorityTOS> priorityTOSs = getPriorityTOS(message);
		if (null != priorityTOSs) {
			boolean result = qosHandler.configTosPriority(ip, priorityTOSs);

			ParmRep parmRep = handlePriorityTOS(priorityTOSs, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	public void configDscpPriority(String ip, String client, String clientIp,
			Message message) {
		List<PriorityDSCP> priorityDSCPs = getPriorityDSCP(message);
		if (null != priorityDSCPs) {
			boolean result = qosHandler.configDscpPriority(ip, priorityDSCPs);

			ParmRep parmRep = handlePriorityDSCP(priorityDSCPs, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	public void configStormControl(String ip, String client, String clientIp,
			Message message) {
		List<QOSStormControl> qosStormControls = getQOSStormControl(message);
		if (null != qosStormControls) {
			boolean result = qosHandler
					.configStormControl(ip, qosStormControls);

			ParmRep parmRep = handleQOSStormControl(qosStormControls, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	public void configStormControl_80series(String ip, String client,
			String clientIp, Message message, int deviceType) {
		List<QOSStormControl> qosStormControls = getQOSStormControl(message);
		if (null != qosStormControls) {
			boolean result = qosHandler.configStormControl_80series(ip,
					qosStormControls);
			ParmRep parmRep = handleQOSStormControl(qosStormControls, result);
			parmRep.setDescs(UPDATE);
			parmRep.setMessageType(MessageNoConstants.QOS_STORMCONTROL);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	public void configTrafficLimit(String ip, String client, String clientIp,
			Message message) {
		List<QOSSpeedConfig> qosSpeedConfigs = getQOSSpeedConfig(message);
		if (null != qosSpeedConfigs) {
			boolean result = qosHandler.configTrafficControl(ip,
					qosSpeedConfigs);

			ParmRep parmRep = handleQOSSpeedConfig(qosSpeedConfigs, result);
			parmRep.setDescs(UPDATE);
			parmRep.setMessageType(MessageNoConstants.QOS_LIMITPORT);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);

			// 设置完端口速率后，再从设备上读取端口速率值并传到服务端，修改数据库
			// ArrayList<QOSSpeedConfig> qosSpeedConfigList = (ArrayList)
			// qosHandler
			// .getTrafficControl(ip);
			// messageSend.sendObjectMessageRes(
			// com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
			// qosSpeedConfigList, SUCCESS,
			// MessageNoConstants.QOSPORTSPEEDREP, ip, client, clientIp);

		}
	}

	public void getStormPort(String ip, String client, String clientIp,
			Message message) {
		Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
		if (synchDeviceSet != null) {
			for (SynchDevice synchDevice : synchDeviceSet) {
				String deviceIp = synchDevice.getIpvalue();
				int deviceType = synchDevice.getDeviceType();
				HashMap<Class, List> synMap = new HashMap<Class, List>();
				List<QOSStormControl> stormList = qosHandler
						.getStormControl_80series(deviceIp);
				// String mac = systemHandler.getMAC(deviceIp);
				String STATE = SUCCESS;
				if (stormList != null) {
					synMap.put(QOSStormControl.class, stormList);
				} else {
					STATE = FAIL;
				}
				messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
						MessageNoConstants.SINGLESYNCHREP,
						synchDevice.getIpvalue(), client, clientIp);
			}
		}
	}

	private MessageHandleIF getStormPortHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			getStormPort(ip, client, clientIp, message);
		}

	};

	public void getTrafficPort(String ip, String client, String clientIp,
			Message message) {
		Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
		if (synchDeviceSet != null) {
			for (SynchDevice synchDevice : synchDeviceSet) {
				String deviceIp = synchDevice.getIpvalue();
				int deviceType = synchDevice.getDeviceType();
				HashMap<Class, List> synMap = new HashMap<Class, List>();
				List<QOSSpeedConfig> speedList = qosHandler
						.getTrafficControl(deviceIp);
				// String mac = systemHandler.getMAC(deviceIp);
				String STATE = SUCCESS;
				if (speedList != null) {
					synMap.put(QOSSpeedConfig.class, speedList);
				} else {
					STATE = FAIL;
				}
				messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
						MessageNoConstants.SINGLESYNCHREP,
						synchDevice.getIpvalue(), client, clientIp);
			}
		}
	}

	private MessageHandleIF getTrafficPortHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			getTrafficPort(ip, client, clientIp, message);
		}

	};

	public void getQosSysConfig(String ip, String client, String clientIp,
			Message message) {
		Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
		if (synchDeviceSet != null) {
			for (SynchDevice synchDevice : synchDeviceSet) {
				String deviceIp = synchDevice.getIpvalue();
				int deviceType = synchDevice.getDeviceType();
				HashMap<Class, List> synMap = new HashMap<Class, List>();
				List list = new ArrayList();
				QOSSysConfig qosSysConfig = qosHandler.getQosConfig(deviceIp);
				list.add(qosSysConfig);

				// String mac = systemHandler.getMAC(deviceIp);
				String STATE = SUCCESS;
				if (qosSysConfig != null) {
					synMap.put(QOSSysConfig.class, list);
				} else {
					STATE = FAIL;
				}
				messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
						MessageNoConstants.SINGLESYNCHREP,
						synchDevice.getIpvalue(), client, clientIp);
			}
		}
	}

	private MessageHandleIF getQosSysConfigHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			getQosSysConfig(ip, client, clientIp, message);
		}

	};

	/**
	 * @deprecated
	 * @param ip
	 * @param client
	 * @param clientIp
	 * @param message
	 */
	public void getQosPriorityInfo(String ip, String client, String clientIp,
			Message message) {
		Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
		if (synchDeviceSet != null) {
			for (SynchDevice synchDevice : synchDeviceSet) {
				String deviceIp = synchDevice.getIpvalue();
				int deviceType = synchDevice.getDeviceType();
				HashMap<Class, List> synMap = new HashMap<Class, List>();
				List list = new ArrayList();
				List<Priority802D1P> priority802D1Ps = qosHandler
						.getQosDot1Priority(ip);
				List<PriorityTOS> priorityTOS = qosHandler.getTosPriority(ip);
				List<PriorityDSCP> priorityDSCPs = qosHandler
						.getDscpPriority(ip);
				QOSPriority qosPriority = new QOSPriority();
				qosPriority.setPriorityEOTs(priority802D1Ps);
				qosPriority.setPriorityTOSs(priorityTOS);
				qosPriority.setPriorityDSCPs(priorityDSCPs);
				list.add(qosPriority);

				// String mac = systemHandler.getMAC(deviceIp);
				String STATE = SUCCESS;
				if (priority802D1Ps != null || priorityTOS != null
						|| priorityDSCPs != null) {
					synMap.put(QOSPriority.class, list);
				} else {
					STATE = FAIL;
				}
				messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
						MessageNoConstants.SINGLESYNCHREP,
						synchDevice.getIpvalue(), client, clientIp);
			}
		}
	}

	public void getQosDot1Priority(String ip, String client, String clientIp,
			Message message) {
		Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
		if (synchDeviceSet != null) {
			for (SynchDevice synchDevice : synchDeviceSet) {
				String deviceIp = synchDevice.getIpvalue();
				int deviceType = synchDevice.getDeviceType();
				HashMap<Class, List> synMap = new HashMap<Class, List>();
				List<Priority802D1P> priority802D1Ps = qosHandler
						.getQosDot1Priority(deviceIp);
				// String mac = systemHandler.getMAC(deviceIp);
				String STATE = SUCCESS;
				if (priority802D1Ps != null) {
					synMap.put(Priority802D1P.class, priority802D1Ps);
				} else {
					STATE = FAIL;
				}
				messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
						MessageNoConstants.SINGLESYNCHREP,
						synchDevice.getIpvalue(), client, clientIp);
			}
		}
	}

	private MessageHandleIF getQosDot1PriorityHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			getQosDot1Priority(ip, client, clientIp, message);
		}

	};

	public void getTosPriority(String ip, String client, String clientIp,
			Message message) {
		Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
		if (synchDeviceSet != null) {
			for (SynchDevice synchDevice : synchDeviceSet) {
				String deviceIp = synchDevice.getIpvalue();
				int deviceType = synchDevice.getDeviceType();
				HashMap<Class, List> synMap = new HashMap<Class, List>();
				List<PriorityTOS> priorityTOS = qosHandler
						.getTosPriority(deviceIp);
				// String mac = systemHandler.getMAC(deviceIp);
				String STATE = SUCCESS;
				if (priorityTOS != null) {
					synMap.put(PriorityTOS.class, priorityTOS);
				} else {
					STATE = FAIL;
				}
				messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
						MessageNoConstants.SINGLESYNCHREP,
						synchDevice.getIpvalue(), client, clientIp);
			}
		}
	}

	private MessageHandleIF getTosPriorityHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			getTosPriority(ip, client, clientIp, message);
		}

	};

	public void getDscpPriority(String ip, String client, String clientIp,
			Message message) {
		Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
		if (synchDeviceSet != null) {
			for (SynchDevice synchDevice : synchDeviceSet) {
				String deviceIp = synchDevice.getIpvalue();
				int deviceType = synchDevice.getDeviceType();
				HashMap<Class, List> synMap = new HashMap<Class, List>();
				List<PriorityDSCP> priorityDSCPs = qosHandler
						.getDscpPriority(deviceIp);
				// String mac = systemHandler.getMAC(deviceIp);
				String STATE = SUCCESS;
				if (priorityDSCPs != null) {
					synMap.put(PriorityDSCP.class, priorityDSCPs);
				} else {
					STATE = FAIL;
				}
				messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
						MessageNoConstants.SINGLESYNCHREP,
						synchDevice.getIpvalue(), client, clientIp);
			}
		}
	}

	private MessageHandleIF getDscpPriorityHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			getDscpPriority(ip, client, clientIp, message);
		}

	};

	// //////////////
	private QOSSysConfig getQOSSysConfig(Message message) {
		QOSSysConfig qosSysConfig = null;
		ObjectMessage om = (ObjectMessage) message;
		try {
			List<QOSSysConfig> qosSysConfigs = (List<QOSSysConfig>) om
					.getObject();
			qosSysConfig = qosSysConfigs.get(0);
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return qosSysConfig;
	}

	private List<Priority802D1P> getPriority802D1P(Message message) {
		List<Priority802D1P> priority802D1Ps = null;

		ObjectMessage om = (ObjectMessage) message;
		try {
			priority802D1Ps = (List<Priority802D1P>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}

		return priority802D1Ps;
	}

	private List<PriorityTOS> getPriorityTOS(Message message) {
		List<PriorityTOS> priorityTOSs = null;

		ObjectMessage om = (ObjectMessage) message;
		try {
			priorityTOSs = (List<PriorityTOS>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}

		return priorityTOSs;
	}

	private List<PriorityDSCP> getPriorityDSCP(Message message) {
		List<PriorityDSCP> priorityDSCPs = null;

		ObjectMessage om = (ObjectMessage) message;
		try {
			priorityDSCPs = (List<PriorityDSCP>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}

		return priorityDSCPs;
	}

	private List<QOSSpeedConfig> getQOSSpeedConfig(Message message) {
		List<QOSSpeedConfig> qosSpeedConfigs = new ArrayList<QOSSpeedConfig>();
		ObjectMessage om = (ObjectMessage) message;
		try {
			qosSpeedConfigs = (List<QOSSpeedConfig>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return qosSpeedConfigs;
	}

	private List<QOSStormControl> getQOSStormControl(Message message) {
		List<QOSStormControl> qosStormControls = null;
		ObjectMessage om = (ObjectMessage) message;
		try {
			qosStormControls = (List<QOSStormControl>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return qosStormControls;
	}

	// ////
	private ParmRep handleQOSSysConfig(List<QOSSysConfig> qosSysConfigs,
			boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (QOSSysConfig qosSysConfig : qosSysConfigs) {
			parmIds.add(qosSysConfig.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(QOSSysConfig.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private ParmRep handlePriority802D1P(List<Priority802D1P> priority802D1Ps,
			boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (Priority802D1P priority802D1P : priority802D1Ps) {
			parmIds.add(priority802D1P.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(Priority802D1P.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private ParmRep handlePriorityTOS(List<PriorityTOS> priorityTOSs,
			boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (PriorityTOS priorityTOS : priorityTOSs) {
			parmIds.add(priorityTOS.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(PriorityTOS.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private ParmRep handlePriorityDSCP(List<PriorityDSCP> priorityDSCPs,
			boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (PriorityDSCP priorityDSCP : priorityDSCPs) {
			parmIds.add(priorityDSCP.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(PriorityDSCP.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private ParmRep handleQOSSpeedConfig(List<QOSSpeedConfig> qosSpeedConfigs,
			boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (QOSSpeedConfig qosSpeedConfig : qosSpeedConfigs) {
			parmIds.add(qosSpeedConfig.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(QOSSpeedConfig.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private ParmRep handleQOSStormControl(
			List<QOSStormControl> qosStormControls, boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (QOSStormControl qosStormControl : qosStormControls) {
			parmIds.add(qosStormControl.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(QOSStormControl.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	//
	public QosHandler getQosHandler() {
		return qosHandler;
	}

	public void setQosHandler(QosHandler qosHandler) {
		this.qosHandler = qosHandler;
	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
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
