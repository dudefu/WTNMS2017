package com.jhw.adm.comclient.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import com.jhw.adm.comclient.jms.MessageHandleIF;
import com.jhw.adm.comclient.jms.MessageQueueHandler;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.SNMPGroup;
import com.jhw.adm.server.entity.switchs.SNMPHost;
import com.jhw.adm.server.entity.switchs.SNMPMass;
import com.jhw.adm.server.entity.switchs.SNMPUser;
import com.jhw.adm.server.entity.switchs.SNMPView;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.ParmRep;

/**
 * 
 * @author xiongbo
 * 
 *         1.view config
 * 
 *         2.public config
 * 
 *         3.group config
 * 
 *         4.user config
 * 
 *         5.engineid config
 * 
 */
public class SnmpService extends BaseService {
	private MessageSend messageSend;
	private SnmpHandler snmpHandler;
	private SystemHandler systemHandler;
	private MessageQueueHandler messageQueueHandler;

	public void init() {
		// Only single parameter upload
		messageQueueHandler.addHandler(
				MessageNoConstants.SINGLESWITCHSNMPGROUP, getSnmpGroupsHandle);
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHSNMPMASS,
				getSnmpCommsHandle);
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHSNMPVIEW,
				getSnmpViewsHandle);
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHSNMPHOST,
				getSnmpHostsHandle);
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHSNMPUSER,
				getSnmpUsersHandle);
	}

	private MessageHandleIF getSnmpViewsHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();

					HashMap<Class, List> synMap = new HashMap<Class, List>();
					List<SNMPView> snmpViews = snmpHandler
							.getSnmpViews(deviceIp);
					// String mac = systemHandler.getMAC(deviceIp);
					String STATE = SUCCESS;
					if (snmpViews != null && snmpViews.size() == 0) {
						synMap.put(SNMPView.class, null);
					} else if (snmpViews != null) {
						synMap.put(SNMPView.class, snmpViews);
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
	private MessageHandleIF getSnmpCommsHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();

					HashMap<Class, List> synMap = new HashMap<Class, List>();
					List<SNMPMass> snmpComms = snmpHandler
							.getSnmpComms(deviceIp);
					// String mac = systemHandler.getMAC(deviceIp);
					String STATE = SUCCESS;
					if (snmpComms != null && snmpComms.size() == 0) {
						synMap.put(SNMPMass.class, null);
					} else if (snmpComms != null) {
						synMap.put(SNMPMass.class, snmpComms);
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
	private MessageHandleIF getSnmpGroupsHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();

					HashMap<Class, List> synMap = new HashMap<Class, List>();
					List<SNMPGroup> snmpGroups = snmpHandler
							.getSnmpGroups(deviceIp);
					// String mac = systemHandler.getMAC(deviceIp);
					String STATE = SUCCESS;
					if (snmpGroups != null && snmpGroups.size() == 0) {
						synMap.put(SNMPGroup.class, null);
					} else if (snmpGroups != null) {
						synMap.put(SNMPGroup.class, snmpGroups);
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
	private MessageHandleIF getSnmpUsersHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();

					HashMap<Class, List> synMap = new HashMap<Class, List>();
					List<SNMPUser> snmpUsers = snmpHandler
							.getSnmpUsers(deviceIp);
					// String mac = systemHandler.getMAC(deviceIp);
					String STATE = SUCCESS;
					if (snmpUsers != null && snmpUsers.size() == 0) {
						synMap.put(SNMPUser.class, null);
					} else if (snmpUsers != null) {
						synMap.put(SNMPUser.class, snmpUsers);
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
	private MessageHandleIF getSnmpHostsHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();

					HashMap<Class, List> synMap = new HashMap<Class, List>();
					List<SNMPHost> snmpHosts = snmpHandler
							.getSnmpHosts(deviceIp);
					// String mac = systemHandler.getMAC(deviceIp);
					String STATE = SUCCESS;
					if (snmpHosts != null && snmpHosts.size() == 0) {
						synMap.put(SNMPHost.class, null);
					} else if (snmpHosts != null) {
						synMap.put(SNMPHost.class, snmpHosts);
					} else {

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

	/**
	 * 批量删除SNMPHost，先删除所有的SNMPHost，在增加SNMPHost。通过key值为"old"和"new"
	 * 来得到需要删除和增加的SNMPHost列表
	 * 
	 * @param ip
	 * @param client
	 * @param clientIp
	 * @param message
	 */
	public void batchConfigSnmpHost(String ip, String client, String clientIp,
			Message message) {
		HashMap hashMap = getSNMPHostInfo(message);
		List<SNMPHost> oldSnmpHostList = (List<SNMPHost>) hashMap.get("old");
		List<SNMPHost> newSnmpHostList = (List<SNMPHost>) hashMap.get("new");

		boolean resultDel = deleteSnmpHost(oldSnmpHostList);

		boolean resultAdd = addSnmpHost(newSnmpHostList);

		// boolean result = (resultDel && resultAdd);
		boolean result = (resultDel || resultAdd);

		ParmRep parmRep = handleSNMPHost(newSnmpHostList, result);
		parmRep.setDescs(INSERT);
		messageSend.sendObjectMessageRes(
				com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
				parmRep, ip, client, clientIp);
	}

	/**
	 * 删除所有的SNMPHost
	 * 
	 * @param oldSnmpHostList
	 * @return
	 */
	private boolean deleteSnmpHost(List<SNMPHost> oldSnmpHostList) {
		boolean result = false;
		for (SNMPHost oldSnmpHost : oldSnmpHostList) {
			if (oldSnmpHost.getSwitchNode() != null) {
				String ip = oldSnmpHost.getSwitchNode().getBaseConfig()
						.getIpValue();

				boolean resultDel = snmpHandler.deleteSnmpHost(ip, oldSnmpHost);

				result = (result || resultDel);
			} else {
				result = true;
			}
		}
		return result;
	}

	/**
	 * 增加所有的SNMPHost
	 * 
	 * @param newSnmpHostList
	 * @return
	 */
	private boolean addSnmpHost(List<SNMPHost> newSnmpHostList) {
		boolean result = false;
		if (null == newSnmpHostList || newSnmpHostList.size() < 1) {
			result = true;
			return result;
		}
		for (SNMPHost newSnmpHost : newSnmpHostList) {
			String ip = newSnmpHost.getSwitchNode().getBaseConfig()
					.getIpValue();

			boolean resultDel = snmpHandler.configSnmpHost(ip, newSnmpHost);

			result = (result || resultDel);
		}
		return result;
	}

	private HashMap getSNMPHostInfo(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		HashMap hashMap = null;

		try {
			hashMap = (HashMap) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return hashMap;
	}

	public void configSnmpHost(String ip, String client, String clientIp,
			Message message) {
		SNMPHost snmpHost = getSNMPHost(message);
		if (snmpHost != null) {
			boolean result = snmpHandler.configSnmpHost(ip, snmpHost);
			List<SNMPHost> snmpHosts = new ArrayList<SNMPHost>(1);
			snmpHosts.add(snmpHost);
			ParmRep parmRep = handleSNMPHost(snmpHosts, result);
			parmRep.setDescs(INSERT);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	private SNMPHost getSNMPHost(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		SNMPHost snmpHost = null;
		try {
			snmpHost = (SNMPHost) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return snmpHost;
	}

	private ParmRep handleSNMPHost(List<SNMPHost> snmpHosts, boolean result) {
		ParmRep parmRep = new ParmRep();
		// List<Long> parmIds = new ArrayList<Long>();
		// for (SNMPHost snmpHost : snmpHosts) {
		// parmIds.add(snmpHost.getId());
		// }
		// parmRep.setParmIds(parmIds);
		parmRep.setObjects(snmpHosts);
		parmRep.setParmClass(SNMPHost.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	public SnmpHandler getSnmpHandler() {
		return snmpHandler;
	}

	public void setSnmpHandler(SnmpHandler snmpHandler) {
		this.snmpHandler = snmpHandler;
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
