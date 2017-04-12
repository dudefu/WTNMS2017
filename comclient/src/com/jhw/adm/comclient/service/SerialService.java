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
import com.jhw.adm.server.entity.util.SwitchSerialPort;

public class SerialService extends BaseService {
	private MessageSend messageSend;
	private SerialHandler serialHandler;
	private SystemHandler systemHandler;
	private MessageQueueHandler messageQueueHandler;

	public void init() {
		// Only single parameter upload
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHSERIAL,
				getSerialConfigHandle);
	}

	private MessageHandleIF getSerialConfigHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();
					HashMap<Class, List> synMap = new HashMap<Class, List>();
					List<SwitchSerialPort> switchSerials = serialHandler
							.getSerialConfig(deviceIp);
					// String mac = systemHandler.getMAC(deviceIp);
					String STATE = SUCCESS;
					if (switchSerials != null && switchSerials.size() == 0) {
						synMap.put(SwitchSerialPort.class, null);
					} else if (switchSerials != null) {
						synMap.put(SwitchSerialPort.class, switchSerials);
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

	public void configSerial(String ip, String client, String clientIp,
			Message message) {
		List<SwitchSerialPort> switchSerialPorts = getSwitchSerialPorts(message);
		if (switchSerialPorts != null) {
			boolean result = serialHandler.configSerial(ip,
					switchSerialPorts.get(0));
			ParmRep parmRep = handleSwitchSerialPort(switchSerialPorts, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	private List<SwitchSerialPort> getSwitchSerialPorts(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<SwitchSerialPort> serialPorts = null;
		try {
			serialPorts = (List<SwitchSerialPort>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return serialPorts;
	}

	private ParmRep handleSwitchSerialPort(List<SwitchSerialPort> serialPorts,
			boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (SwitchSerialPort switchSerialPort : serialPorts) {
			parmIds.add(switchSerialPort.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(SwitchSerialPort.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	public SerialHandler getSerialHandler() {
		return serialHandler;
	}

	public void setSerialHandler(SerialHandler serialHandler) {
		this.serialHandler = serialHandler;
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
