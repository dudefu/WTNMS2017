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
import com.jhw.adm.server.entity.util.MACMutiCast;
import com.jhw.adm.server.entity.util.MACUniCast;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.ParmRep;

/**
 * 单播,多播
 * 
 * @author xiongbo
 * 
 */

public class MacService extends BaseService {
	private static Logger log = Logger.getLogger(MacService.class);
	private MacHandler macHandler;
	private MessageSend messageSend;
	private SystemHandler systemHandler;
	private MessageQueueHandler messageQueueHandler;

	public void init() {
		// Only single parameter upload
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHUNICAST,
				getUnicastHandle);
		messageQueueHandler.addHandler(
				MessageNoConstants.SINGLESWITCHMULTICAST, getMulticastHandle);
	}

	private MessageHandleIF getUnicastHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();

					HashMap<Class, List> synMap = new HashMap<Class, List>();
					// List<MACUniCast> uniCastList = macHandler
					// .getUnicast(deviceIp);
					List<MACUniCast> uniCastList = macHandler
							.getUnicast_batch(deviceIp);
					// String mac = systemHandler.getMAC(deviceIp);
					String STATE = SUCCESS;
					if (uniCastList != null && uniCastList.size() > 0) {
						synMap.put(MACUniCast.class, uniCastList);
					} else {
						STATE = FAIL;
					}
					messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
							MessageNoConstants.SINGLESYNCHREP,
							synchDevice.getIpvalue(), client, clientIp);
				}
				messageSend.sendTextMessageRes("上载完成", SUCCESS,
						MessageNoConstants.SYNCHFINISH, ip, client, clientIp);
			}
		}

	};
	private MessageHandleIF getMulticastHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();

					HashMap<Class, List> synMap = new HashMap<Class, List>();
					List<MACMutiCast> mutiCastList = macHandler
							.getMulticast(deviceIp);
					// String mac = systemHandler.getMAC(deviceIp);
					String STATE = SUCCESS;
					if (mutiCastList != null && mutiCastList.size() == 0) {
						synMap.put(MACMutiCast.class, null);
					} else if (mutiCastList != null) {
						synMap.put(MACMutiCast.class, mutiCastList);
					} else {
						STATE = FAIL;
					}
					messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
							MessageNoConstants.SINGLESYNCHREP,
							synchDevice.getIpvalue(), client, clientIp);
				}
				messageSend.sendTextMessageRes("上载完成", SUCCESS,
						MessageNoConstants.SYNCHFINISH, ip, client, clientIp);
			}
		}

	};

	/**
	 * Including config AgingTime
	 * 
	 * @param ip
	 * @param mACUniCast
	 */
	public void createUnicast(String ip, String client, String clientIp,
			Message message) {
		List<MACUniCast> mACUniCastList = getUniCastList(message);
		if (mACUniCastList != null) {
			boolean result = false;
			for (MACUniCast mACUniCast : mACUniCastList) {
				result = macHandler.createUnicast(ip, mACUniCast);
				if (!result) {
					break;
				}
			}
			// List<MACUniCast> macUniCasts = new ArrayList<MACUniCast>(1);
			// macUniCasts.add(mACUniCast);
			ParmRep parmRep = handleMACUniCast(mACUniCastList, result);
			parmRep.setDescs(INSERT);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);

		}
	}

	public void deleteUnicast(String ip, String client, String clientIp,
			Message message) {
		List<MACUniCast> mACUniCastList = getUniCastList(message);
		if (mACUniCastList != null) {
			boolean result = false;
			for (MACUniCast mACUniCast : mACUniCastList) {
				result = macHandler.deleteUnicast(ip, mACUniCast);
				if (!result) {
					break;
				}
			}
			// List<MACUniCast> macUniCasts = new ArrayList<MACUniCast>(1);
			// macUniCasts.add(mACUniCast);
			ParmRep parmRep = handleMACUniCast(mACUniCastList, result);
			parmRep.setDescs(DELETE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);

			// if (result) {
			// messageSend.sendTextMessageRes("单播删除成功", SUCCESS,
			// MessageNoConstants.MACUNIDEL, ip, client, clientIp);
			// } else {
			// messageSend.sendTextMessageRes("单播删除失败", FAIL,
			// MessageNoConstants.MACUNIDEL, ip, client, clientIp);
			// }
		}
	}

	public void createMulticast(String ip, String client, String clientIp,
			Message message) {
		List<MACMutiCast> mACMutiCastList = getMutiCastList(message);
		if (mACMutiCastList != null) {
			boolean result = false;
			for (MACMutiCast mACMutiCast : mACMutiCastList) {
				result = macHandler.createMulticast(ip, mACMutiCast);
				if (!result) {
					break;
				}
			}
			// List<MACMutiCast> macMutiCasts = new ArrayList<MACMutiCast>(1);
			// macMutiCasts.add(mACMutiCast);
			ParmRep parmRep = handleMACMutiCast(mACMutiCastList, result);
			parmRep.setDescs(INSERT);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);

			// if (result) {
			// messageSend.sendTextMessageRes("多播添加成功", SUCCESS,
			// MessageNoConstants.MACMUTINEW, ip, client, clientIp);
			// } else {
			// messageSend.sendTextMessageRes("多播添加失败", FAIL,
			// MessageNoConstants.MACMUTINEW, ip, client, clientIp);
			// }
		}
	}

	public void deleteMulticast(String ip, String client, String clientIp,
			Message message) {
		List<MACMutiCast> mACMutiCastList = getMutiCastList(message);
		if (mACMutiCastList != null) {
			boolean result = false;
			for (MACMutiCast mACMutiCast : mACMutiCastList) {
				result = macHandler.deleteMulticast(ip, mACMutiCast);
				if (!result) {
					break;
				}
			}
			// List<MACMutiCast> macMutiCasts = new ArrayList<MACMutiCast>(1);
			// macMutiCasts.add(mACMutiCast);
			ParmRep parmRep = handleMACMutiCast(mACMutiCastList, result);
			parmRep.setDescs(DELETE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);

			// if (result) {
			// messageSend.sendTextMessageRes("多播删除成功", SUCCESS,
			// MessageNoConstants.MACMUTIDEL, ip, client, clientIp);
			// } else {
			// messageSend.sendTextMessageRes("多播删除失败", FAIL,
			// MessageNoConstants.MACMUTIDEL, ip, client, clientIp);
			// }
		}
	}

	//

	private List<MACUniCast> getUniCastList(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<MACUniCast> mACUniCastList = null;
		try {
			mACUniCastList = (List<MACUniCast>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return mACUniCastList;
	}

	private List<MACMutiCast> getMutiCastList(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<MACMutiCast> mACMutiCastList = null;
		try {
			mACMutiCastList = (List<MACMutiCast>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return mACMutiCastList;
	}

	private ParmRep handleMACUniCast(List<MACUniCast> macUniCasts,
			boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (MACUniCast macUniCast : macUniCasts) {
			parmIds.add(macUniCast.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(MACUniCast.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private ParmRep handleMACMutiCast(List<MACMutiCast> macMutiCasts,
			boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (MACMutiCast macMutiCast : macMutiCasts) {
			parmIds.add(macMutiCast.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(MACMutiCast.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	public MacHandler getMacHandler() {
		return macHandler;
	}

	public void setMacHandler(MacHandler macHandler) {
		this.macHandler = macHandler;
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
