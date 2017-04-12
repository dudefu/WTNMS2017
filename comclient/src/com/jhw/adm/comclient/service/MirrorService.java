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
import com.jhw.adm.server.entity.switchs.MirrorEntity;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.ParmRep;

/**
 * æµœÒ
 * 
 * @author xiongbo
 * 
 */
public class MirrorService extends BaseService {
	private static Logger log = Logger.getLogger(MirrorService.class);
	private MirrorHandler mirrorHandler;
	private MessageSend messageSend;
	private SystemHandler systemHandler;
	private MessageQueueHandler messageQueueHandler;

	public void init() {
		// Only single parameter upload
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHMIRROR,
				getMirrorConfigHandle);
	}

	private MessageHandleIF getMirrorConfigHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();

					HashMap<Class, List> synMap = new HashMap<Class, List>();
					MirrorEntity mirrorEntity = mirrorHandler
							.getMirrorConfig(deviceIp);
					List list = new ArrayList();
					list.add(mirrorEntity);
					// String mac = systemHandler.getMAC(deviceIp);
					String STATE = SUCCESS;
					if (mirrorEntity != null) {
						synMap.put(MirrorEntity.class, list);
					} else {
						STATE = FAIL;
					}
					messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
							MessageNoConstants.SINGLESYNCHREP,
							synchDevice.getIpvalue(), client, clientIp);
				}
				messageSend.sendTextMessageRes("…œ‘ÿÕÍ≥…", SUCCESS,
						MessageNoConstants.SYNCHFINISH, ip, client, clientIp);
			}
		}

	};

	//
	public void configMirror(String ip, String client, String clientIp,
			Message message) {
		List<MirrorEntity> mirrorEntityList = getMirrorEntity(message);
		if (mirrorEntityList != null) {
			boolean result = mirrorHandler.configMirror(ip,
					mirrorEntityList.get(0));
			List<MirrorEntity> mirrorEntitys = new ArrayList<MirrorEntity>(0);
			mirrorEntitys.add(mirrorEntityList.get(0));
			ParmRep parmRep = handleMirrorEntity(mirrorEntitys, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);

			// if (result) {
			// messageSend.sendTextMessageRes("Mirror≈‰÷√≥…π¶", SUCCESS,
			// MessageNoConstants.MIRRORUPDATE, ip, client, clientIp);
			// } else {
			// messageSend.sendTextMessageRes("Mirror≈‰÷√ ß∞‹", FAIL,
			// MessageNoConstants.MIRRORUPDATE, ip, client, clientIp);
			// }
		}
	}

	private List<MirrorEntity> getMirrorEntity(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<MirrorEntity> mirrorEntityList = null;
		try {
			mirrorEntityList = (List<MirrorEntity>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return mirrorEntityList;
	}

	private ParmRep handleMirrorEntity(List<MirrorEntity> mirrorEntitys,
			boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (MirrorEntity mirrorEntity : mirrorEntitys) {
			parmIds.add(mirrorEntity.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(MirrorEntity.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	public MirrorHandler getMirrorHandler() {
		return mirrorHandler;
	}

	public void setMirrorHandler(MirrorHandler mirrorHandler) {
		this.mirrorHandler = mirrorHandler;
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
