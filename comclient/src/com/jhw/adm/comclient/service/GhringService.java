package com.jhw.adm.comclient.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.commons.lang.math.NumberUtils;

import com.jhw.adm.comclient.jms.MessageHandleIF;
import com.jhw.adm.comclient.jms.MessageQueueHandler;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.ports.RingConfig;
import com.jhw.adm.server.entity.ports.RingLinkBak;
import com.jhw.adm.server.entity.ports.RingPortInfo;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.ParmRep;

/**
 * 
 * @author xiongbo
 * 
 */
public class GhringService extends BaseService {
	private MessageSend messageSend;
	private GhringHandler ghringHandler;
	private SystemHandler systemHandler;
	private MessageQueueHandler messageQueueHandler;

	public void init() {
		// Only single parameter upload
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHRING,
				getRingConfigHandle);
		messageQueueHandler.addHandler(
				MessageNoConstants.SINGLESWITCHLINKBACKUPS,
				getRingLinkBakHandle);
	}

	private MessageHandleIF getRingConfigHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();
					HashMap<Class, List> synMap = new HashMap<Class, List>();
					List<RingConfig> ringConfigList = ghringHandler
							.getRingConfig(deviceIp);
					// String mac = systemHandler.getMAC(deviceIp);
					String STATE = SUCCESS;
					synMap.put(RingConfig.class, ringConfigList);
					List<RingPortInfo> ringPortList = ghringHandler
							.getRingPort(deviceIp);
					synMap.put(RingPortInfo.class, ringPortList);
					if (ringConfigList != null && ringPortList == null) {
						STATE = FAIL;
					}
					messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
							MessageNoConstants.SINGLESYNCHREP,
							synchDevice.getIpvalue(), client, clientIp);
					// 因为服务端无法处理那么快，暂时在这里延时2秒
					try {
						Thread.sleep(TimeUnit.SECONDS.toMillis(2));
					} catch (InterruptedException e) {
						// 忽略延时中断错误
					}
				}
				messageSend.sendTextMessageRes("上载完成", SUCCESS,
						MessageNoConstants.SYNCHFINISH, ip, client, clientIp);
			}
		}

	};
	private MessageHandleIF getRingLinkBakHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();

					HashMap<Class, List> synMap = new HashMap<Class, List>();
					RingLinkBak ringLinkBak = ghringHandler
							.getRingLinkBak(deviceIp);
					List list = new ArrayList();
					list.add(ringLinkBak);
					// String mac = systemHandler.getMAC(deviceIp);
					String STATE = SUCCESS;
					if (ringLinkBak != null && ringLinkBak.getLinkId() == 0) {
						synMap.put(RingLinkBak.class, null);
					} else if (ringLinkBak != null) {
						synMap.put(RingLinkBak.class, list);
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
	private MessageHandleIF getRingPortHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();

					HashMap<Class, List> synMap = new HashMap<Class, List>();
					List<RingPortInfo> ringPortList = ghringHandler
							.getRingPort(deviceIp);
					// String mac = systemHandler.getMAC(deviceIp);
					String STATE = SUCCESS;
					if (ringPortList != null) {
						synMap.put(RingPortInfo.class, ringPortList);
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

	public void createRing(String ip, String client, String clientIp,
			Message message) {
		RingConfig ringConfig = getRingConfig(message);
		if (ringConfig != null) {
			boolean result = ghringHandler.createRing(ip, ringConfig);
			List<RingConfig> ringConfigs = new ArrayList<RingConfig>(1);
			ringConfigs.add(ringConfig);
			ParmRep parmRep = handleRingConfig(ringConfigs, result);
			parmRep.setDescs(INSERT);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	public void createRingLinkBak(String ip, String client, String clientIp,
			Message message) {
		List<RingLinkBak> ringLinkBakList = getRingLinkBak(message);
		if (ringLinkBakList != null) {
			boolean result = false;
			for (RingLinkBak ringLinkBak : ringLinkBakList) {
				result = ghringHandler.createRingLinkBak(ip, ringLinkBak);
				if (!result) {
					break;
				}
			}
			// List<RingLinkBak> ringLinkBaks = new ArrayList<RingLinkBak>(1);
			// ringLinkBaks.add(ringLinkBak);
			ParmRep parmRep = handleRingLinkBak(ringLinkBakList, result);
			parmRep.setDescs(INSERT);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	public void deleteRing(String ip, String client, String clientIp,
			Message message) {
		List<RingConfig> ringConfigs = getRingConfigs(message);
		if (ringConfigs != null) {
			int i = 0;
			boolean result = false;
			for (RingConfig ringConfig : ringConfigs) {
				result = ghringHandler.deleteRing(ip, ringConfig);
				if (!result && i == 0) {
					break;
				}
				i++;
			}
			// List<RingConfig> ringConfigs = new ArrayList<RingConfig>(1);
			// ringConfigs.add(ringConfig);
			ParmRep parmRep = handleRingConfig(ringConfigs, result);
			parmRep.setDescs(DELETE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	public void batchConfigRing(String ip, String client, String clientIp,
			Message message) {
		int ringId = 0;
		try {
			ringId = NumberUtils.toInt(message
					.getStringProperty(Constants.RING_ID));
		} catch (Exception e) {
		}
		List<Object> list = new ArrayList<Object>();
		List<RingConfig> ringConfigs = getRingConfigs(message);
		if (null != ringConfigs) {
			// 先清空ring下的所有端口
			clearRingPort(ip, ringId);
			// 在ring上增加端口
			boolean result = addBatchRing(ringConfigs);

			ParmRep parmRep = handleRingConfig(ringConfigs, result);

			parmRep.setObjects(ringConfigs);
			parmRep.getParmIds().clear();

			parmRep.setDescs(INSERT);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, "", client, clientIp);
		}
	}

	/**
	 * 
	 * @param ip
	 * @param vlanEntity
	 * @return
	 */
	private boolean clearRingPort(String ip, int ringId) {
		boolean result = false;
		if (null == ip || "".equals(ip)) {
			result = true;
			return result;
		} else {
			RingConfig ringConfig = new RingConfig();
			ringConfig.setRingID(ringId);
			String[] ipValue = ip.split(",");
			for (int i = 0; i < ipValue.length; i++) {
				result = ghringHandler.deleteRing(ipValue[i], ringConfig);
				// if (!result && i == 0) {
				// break;
				// }
			}
		}

		return result;
	}

	/**
	 * 
	 * @param ringConfigList
	 * @return
	 */
	private boolean addBatchRing(List<RingConfig> ringConfigList) {
		boolean result = true;
		for (RingConfig ringConfig : ringConfigList) {
			if (null != ringConfig) {
				result = false;
				String ip = ringConfig.getSwitchNode().getBaseConfig()
						.getIpValue();
				boolean isSuccess = ghringHandler.createRing(ip, ringConfig);
				if (isSuccess) {
					result = isSuccess;
				}
				// result = (result && isSuccess);
				// if (!result) {
				// return result;
				// }
			}
		}
		return result;
	}

	public void getRingConfig(String ip, String client, Message message) {

	}

	public void getRingLinkBak(String ip, String client, Message message) {

	}

	public void deleteRingLinkBak(String ip, String client, String clientIp,
			Message message) {
		List<RingLinkBak> ringLinkBakList = getRingLinkBak(message);
		if (ringLinkBakList != null) {
			boolean result = false;
			for (RingLinkBak ringLinkBak : ringLinkBakList) {
				result = ghringHandler.deleteRingLinkBak(ip, ringLinkBak);
			}
			// List<RingLinkBak> ringLinkBaks = new ArrayList<RingLinkBak>(1);
			// ringLinkBaks.add(ringLinkBak);
			ParmRep parmRep = handleRingLinkBak(ringLinkBakList, result);
			parmRep.setDescs(DELETE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	public void getRingPort(String ip, String client, Message message) {

	}

	private RingConfig getRingConfig(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		RingConfig ringConfig = null;
		try {
			ringConfig = (RingConfig) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return ringConfig;
	}

	private List<RingConfig> getRingConfigs(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<RingConfig> ringConfigs = null;
		try {
			ringConfigs = (List<RingConfig>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return ringConfigs;
	}

	private List<RingLinkBak> getRingLinkBak(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<RingLinkBak> ringLinkBakList = null;
		try {
			ringLinkBakList = (List<RingLinkBak>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return ringLinkBakList;
	}

	private ParmRep handleRingConfig(List<RingConfig> ringConfigs,
			boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (RingConfig ringConfig : ringConfigs) {
			parmIds.add(ringConfig.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(RingConfig.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private ParmRep handleRingLinkBak(List<RingLinkBak> ringLinkBaks,
			boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (RingLinkBak ringLinkBak : ringLinkBaks) {
			parmIds.add(ringLinkBak.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(RingLinkBak.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	public GhringHandler getGhringHandler() {
		return ghringHandler;
	}

	public void setGhringHandler(GhringHandler ghringHandler) {
		this.ghringHandler = ghringHandler;
	}

	public MessageSend getMessageSend() {
		return messageSend;
	}

	public void setMessageSend(MessageSend messageSend) {
		this.messageSend = messageSend;
	}

	public MessageQueueHandler getMessageQueueHandler() {
		return messageQueueHandler;
	}

	public void setMessageQueueHandler(MessageQueueHandler messageQueueHandler) {
		this.messageQueueHandler = messageQueueHandler;
	}

	public SystemHandler getSystemHandler() {
		return systemHandler;
	}

	public void setSystemHandler(SystemHandler systemHandler) {
		this.systemHandler = systemHandler;
	}

}
