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
import com.jhw.adm.server.entity.epon.OLTPort;
import com.jhw.adm.server.entity.level3.SwitchPortLevel3;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.ports.SwitchPortEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.ParmRep;

/**
 * 端口
 * 
 * @author xiongbo
 * 
 */
public class PortService extends BaseService {
	private static Logger log = Logger.getLogger(PortService.class);
	private MessageSend messageSend;
	private PortHandler portHandler;
	private SystemHandler systemHandler;
	private MessageQueueHandler messageQueueHandler;

	public void init() {
		// Only single parameter upload
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHPORT,
				getPortInfoHandle);
		// Layer3/OLT端口状态信息
		messageQueueHandler
				.addHandler(MessageNoConstants.SINGLESWITCHLAYER3PORT,
						portState_layer3_olt);
		messageQueueHandler.addHandler(MessageNoConstants.SINGLEOLTPORT,
				portState_layer3_olt);
	}

	public void configPort(String ip, String client, String clientIp,
			Message message) {
		List<SwitchPortEntity> portEntityList = getPortEntityList(message);
		if (portEntityList != null) {
			boolean result = portHandler.configPort(ip, portEntityList);
			ParmRep parmRep = handlePortEntity(portEntityList, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);

			if (result) {
				messageSend.sendTextMessageRes("端口修改成功", SUCCESS,
						MessageNoConstants.PORTSET, ip, client, clientIp);
			} else {
				messageSend.sendTextMessageRes("端口修改失败", FAIL,
						MessageNoConstants.PORTSET, ip, client, clientIp);
			}
		}
	}

	private List<SwitchPortEntity> getPortEntityList(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<SwitchPortEntity> portEntityList = null;
		try {
			portEntityList = (List<SwitchPortEntity>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return portEntityList;
	}

	private ParmRep handlePortEntity(List<SwitchPortEntity> portEntitys,
			boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (SwitchPortEntity portEntity : portEntitys) {
			parmIds.add(portEntity.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(SwitchPortEntity.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	public void getPortInfo(String ip, String client, String clientIp,
			Message message) {
		Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
		if (synchDeviceSet != null) {
			for (SynchDevice synchDevice : synchDeviceSet) {
				String deviceIp = synchDevice.getIpvalue();
				int deviceType = synchDevice.getDeviceType();
				HashMap<Class, List> synMap = new HashMap<Class, List>();
				Set<SwitchPortEntity> portSet = portHandler
						.getPortConfig(deviceIp);
				List<SwitchPortEntity> portList = new ArrayList<SwitchPortEntity>();
				if (portSet != null) {
					for (SwitchPortEntity switchPortEntity : portSet) {
						portList.add(switchPortEntity);
					}
				}
				// String mac = systemHandler.getMAC(deviceIp);
				String STATE = SUCCESS;
				if (portSet != null) {
					synMap.put(SwitchPortEntity.class, portList);
				} else {
					STATE = FAIL;
				}

				messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
						MessageNoConstants.SINGLESYNCHREP, synchDevice
								.getIpvalue(), client, clientIp);
			}
		}
	}

	public void getPortState_layer3_olt(String ip, String client,
			String clientIp, Message message) {
		Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
		if (synchDeviceSet != null) {
			for (SynchDevice synchDevice : synchDeviceSet) {
				String deviceIp = synchDevice.getIpvalue();
				int deviceType = synchDevice.getDeviceType();
				HashMap<Class, List> synMap = new HashMap<Class, List>();
				String STATE = SUCCESS;
				log.info("***Port state modelNumber：" + deviceType);
				if (deviceType == Constants.DEV_SWITCHER3) {
					List<SwitchPortLevel3> portStateList = portHandler
							.getPortState_layer3(deviceIp, "public");
					// log.info("portStateList：" + portStateList);
					if (portStateList != null) {
						synMap.put(SwitchPortLevel3.class, portStateList);
						// log.info("***Port state info：");
						// for (SwitchPortLevel3 switchPortLevel3 :
						// portStateList) {
						// log.info(switchPortLevel3.getIpValue() + " "
						// + switchPortLevel3.getPortName() + " "
						// + switchPortLevel3.getRate());
						// }
					} else {
						STATE = FAIL;
					}
				} else if (deviceType == Constants.DEV_OLT) {
					List<OLTPort> portStateList = portHandler.getPortState_olt(
							deviceIp, "public");
					log.info("portStateList：" + portStateList);
					if (portStateList != null) {
						synMap.put(OLTPort.class, portStateList);
						log.info("***Port state info：");
						for (OLTPort oltPort : portStateList) {
							log.info(oltPort.getPortIP() + " "
									+ oltPort.getPortName() + " "
									+ oltPort.getRate());
						}
					} else {
						STATE = FAIL;
					}
				}
				messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
						MessageNoConstants.SINGLESYNCHREP, synchDevice
								.getIpvalue(), client, clientIp);
			}
		}
	}

	private MessageHandleIF getPortInfoHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			getPortInfo(ip, client, clientIp, message);
		}

	};
	private MessageHandleIF portState_layer3_olt = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			getPortState_layer3_olt(ip, client, clientIp, message);
		}

	};

	public PortHandler getPortHandler() {
		return portHandler;
	}

	public void setPortHandler(PortHandler portHandler) {
		this.portHandler = portHandler;
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
