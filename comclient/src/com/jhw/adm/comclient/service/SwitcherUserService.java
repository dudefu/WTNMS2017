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
import com.jhw.adm.server.entity.switchs.SwitchUser;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.ParmRep;

public class SwitcherUserService extends BaseService {
	private MessageSend messageSend;
	private SwitcherUserHandle switcherUserHandle;
	private SystemHandler systemHandler;
	private MessageQueueHandler messageQueueHandler;

	public void init() {
		// Only single parameter upload
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHUSERADM,
				getSwitcherUserHandle);
	}

	private MessageHandleIF getSwitcherUserHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
			if (synchDeviceSet != null) {
				for (SynchDevice synchDevice : synchDeviceSet) {
					String deviceIp = synchDevice.getIpvalue();
					int deviceType = synchDevice.getDeviceType();
					HashMap<Class, List> synMap = new HashMap<Class, List>();
					List<SwitchUser> switchUsers = switcherUserHandle
							.getSwitcherUser(deviceIp);
					// String mac = systemHandler.getMAC(deviceIp);
					String STATE = SUCCESS;
					if (switchUsers != null && switchUsers.size() == 0) {
						synMap.put(SwitchUser.class, null);
					} else if (switchUsers != null) {
						synMap.put(SwitchUser.class, switchUsers);
					} else {
						STATE = FAIL;
					}
					messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
							MessageNoConstants.SINGLESYNCHREP, deviceIp,
							client, clientIp);
				}
				messageSend.sendTextMessageRes("…œ‘ÿÕÍ≥…", SUCCESS,
						MessageNoConstants.SYNCHFINISH, ip, client, clientIp);
			}
		}

	};

	public void addSwitcherUser(String ip, String client, String clientIp,
			Message message) {
		HashMap<SwitchUser, Boolean> hashMap = new HashMap<SwitchUser, Boolean>();
		List<SwitchUser> switchUsers = getSwitcherUser(message);
		if (switchUsers != null) {
			boolean result = false;
			for (SwitchUser switchUser : switchUsers) {
				String ipValue = switchUser.getSwitchNode().getBaseConfig()
						.getIpValue();
				boolean isSuccess = switcherUserHandle.addUser(switchUser);
				result = (isSuccess || result);
				hashMap.put(switchUser, isSuccess);
			}

			messageSend
					.sendObjectMessage(
							com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
							hashMap, MessageNoConstants.SWITCHUSERADD, client,
							clientIp);

		}
	}

	public void deleteSwitcherUser(String ip, String client, String clientIp,
			Message message) {
		List<SwitchUser> switchUsers = getSwitcherUser(message);
		if (switchUsers != null) {
			boolean result = false;
			for (SwitchUser switchUser : switchUsers) {
				boolean isSuccess = switcherUserHandle.deleteUser(switchUser);
				result = (isSuccess || result);
			}

			ParmRep parmRep = handleSwitchUser(switchUsers, result);
			parmRep.setObjects(switchUsers);
			parmRep.getParmIds().clear();
			parmRep.setDescs(INSERT);

			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, "", client, clientIp);
		}
	}

	public void modifyUserPwd(String ip, String client, String clientIp,
			Message message) {
		HashMap<SwitchUser, Boolean> hashMap = new HashMap<SwitchUser, Boolean>();
		List<SwitchUser> switchUsers = getSwitcherUser(message);
		if (switchUsers != null) {
			boolean result = false;
			for (SwitchUser switchUser : switchUsers) {
				String ipValue = switchUser.getSwitchNode().getBaseConfig()
						.getIpValue();
				boolean isSuccess = switcherUserHandle
						.modifyUserPwd(switchUser);
				result = (isSuccess || result);
				hashMap.put(switchUser, isSuccess);
			}

			messageSend.sendObjectMessage(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					hashMap, MessageNoConstants.SWITCHUSERUPDATE, client,
					clientIp);

		}
	}

	private List<SwitchUser> getSwitcherUser(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<SwitchUser> switchUsers = null;
		try {
			switchUsers = (List<SwitchUser>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return switchUsers;
	}

	private ParmRep handleSwitchUser(List<SwitchUser> switchUsers,
			boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (SwitchUser switchUser : switchUsers) {
			parmIds.add(switchUser.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(SwitchUser.class);
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

	public SwitcherUserHandle getSwitcherUserHandle() {
		return switcherUserHandle;
	}

	public void setSwitcherUserHandle(SwitcherUserHandle switcherUserHandle) {
		this.switcherUserHandle = switcherUserHandle;
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
