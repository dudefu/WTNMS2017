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
import com.jhw.adm.server.entity.switchs.IGMPEntity;
import com.jhw.adm.server.entity.switchs.Igmp_vsi;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.ParmRep;

/**
 * Igmp
 * 
 * @author xiongbo
 * 
 */
public class IgmpService extends BaseService {
	private static Logger log = Logger.getLogger(IgmpService.class);
	private MessageSend messageSend;
	private IgmpHandler igmpHandler;
	private SystemHandler systemHandler;
	private MessageQueueHandler messageQueueHandler;

	public void init() {
		// Only single parameter upload
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHIGMPPORT,
				getIgmpHandle);
		messageQueueHandler.addHandler(
				MessageNoConstants.SINGLESWITCHIGMPVLANID, getVlanIgmpHandle);
	}

	private MessageHandleIF getIgmpHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();
					HashMap<Class, List> synMap = new HashMap<Class, List>();
					IGMPEntity igmpEntity = igmpHandler.getIgmp(deviceIp);
					List list = new ArrayList();
					list.add(igmpEntity);
					// String mac = systemHandler.getMAC(deviceIp);
					String STATE = SUCCESS;
					if (igmpEntity != null) {
						synMap.put(IGMPEntity.class, list);
					} else {
						STATE = FAIL;
					}
					messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
							MessageNoConstants.SINGLESYNCHREP,
							synchDevice.getIpvalue(), client, clientIp);
				}
				// messageSend.sendTextMessageRes("上载完成", SUCCESS,
				// MessageNoConstants.SYNCHFINISH, ip, client, clientIp);
			}
		}

	};

	private MessageHandleIF getVlanIgmpHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();

					HashMap<Class, List> synMap = new HashMap<Class, List>();
					List<Igmp_vsi> igmpVsiList = igmpHandler
							.getVlanIgmp(deviceIp);
					// String mac = systemHandler.getMAC(deviceIp);
					String STATE = SUCCESS;
					if (igmpVsiList != null) {
						synMap.put(Igmp_vsi.class, igmpVsiList);
					} else {
						STATE = FAIL;
					}
					messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
							MessageNoConstants.SINGLESYNCHREP,
							synchDevice.getIpvalue(), client, clientIp);
				}
				// messageSend.sendTextMessageRes("上载完成", SUCCESS,
				// MessageNoConstants.SYNCHFINISH, ip, client, clientIp);
			}
		}

	};

	public void configIgmp(String ip, String client, String clientIp,
			Message message) {
		IGMPEntity igmpEntity = getIGMPEntity(message);
		if (null != igmpEntity) {
			boolean result = igmpHandler.configIgmp(ip, igmpEntity);
			ParmRep parmRep = handleIGMPEntity(igmpEntity, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	public void configVlanIgmp(String ip, String client, String clientIp,
			Message message) {
		IGMPEntity igmpEntity = getIGMPEntity(message);
		List<Igmp_vsi> igmp_vsiList = getIgmpVsis(igmpEntity);
		if (null != igmp_vsiList) {
			boolean result = igmpHandler.configVlanIgmp(ip, igmp_vsiList);
			ParmRep parmRep = handleIgmp_vsi(igmp_vsiList, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	public void getConfigIgmp(String ip, String client, Message message) {

	}

	public void getConfigVlanIgmp(String ip, String client, Message message) {

	}

	private List<Igmp_vsi> getIgmpVsis(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<Igmp_vsi> igmpVsis = null;
		try {
			igmpVsis = (List<Igmp_vsi>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return igmpVsis;
	}

	private List<Igmp_vsi> getIgmpVsis(IGMPEntity igmpEntity) {
		List<Igmp_vsi> igmpVsis = null;
		try {
			igmpVsis = igmpEntity.getVlanIds();
		} catch (Exception e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return igmpVsis;
	}

	private IGMPEntity getIGMPEntity(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		IGMPEntity igmpEntity = null;
		try {
			igmpEntity = (IGMPEntity) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return igmpEntity;
	}

	private ParmRep handleIGMPEntity(IGMPEntity igmpEntity, boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		parmIds.add(igmpEntity.getId());
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(IGMPEntity.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private ParmRep handleIgmp_vsi(List<Igmp_vsi> igmp_vsiList, boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (Igmp_vsi igmp_vsi : igmp_vsiList) {
			parmIds.add(igmp_vsi.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(Igmp_vsi.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	public IgmpHandler getIgmpHandler() {
		return igmpHandler;
	}

	public void setIgmpHandler(IgmpHandler igmpHandler) {
		this.igmpHandler = igmpHandler;
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
