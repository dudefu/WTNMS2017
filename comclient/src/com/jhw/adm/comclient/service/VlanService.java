package com.jhw.adm.comclient.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

import com.jhw.adm.comclient.jms.MessageHandleIF;
import com.jhw.adm.comclient.jms.MessageQueueHandler;
import com.jhw.adm.comclient.jms.MessageSend;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.nets.VlanConfig;
import com.jhw.adm.server.entity.nets.VlanEntity;
import com.jhw.adm.server.entity.nets.VlanPort;
import com.jhw.adm.server.entity.nets.VlanPortConfig;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.ParmRep;

/**
 * Vlan业务处理
 * 
 * @author xiongbo
 * 
 */

public class VlanService extends BaseService {
	private static Logger log = Logger.getLogger(VlanService.class);
	private SystemHandler systemHandler;
	private MessageSend messageSend;
	private VlanHandler vlanHandler;
	private MessageQueueHandler messageQueueHandler;

	public void init() {
		// Only single parameter upload
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHVLAN,
				getVlanInfoHandle);
		messageQueueHandler.addHandler(MessageNoConstants.SINGLESWITCHVLANPORT,
				getVlanPortInfoHandle);
	}

	public void addVlan(String ip, String client, String clientIp,
			Message message) {
		VlanEntity vlanEntity = getVlanEntity(message);
		if (vlanEntity != null) {
			boolean result = vlanHandler.createVlanId(ip, vlanEntity);
			//
			List<VlanEntity> vlanEntitys = new ArrayList<VlanEntity>(1);
			vlanEntitys.add(vlanEntity);
			ParmRep parmRep = handleVlanEntity(vlanEntitys, result);
			parmRep.setDescs(INSERT);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	public void deleteVlan(String ip, String client, String clientIp,
			Message message) {
		VlanEntity vlanEntity = getVlanEntity(message);
		boolean result = false;
		if (vlanEntity != null) {
			String[] ipValue = ip.split(",");
			boolean b = vlanHandler.vlanIsOfNo(ipValue[0], vlanEntity);
			if (b == true) {
				result = vlanHandler.deleteVlan(ip, vlanEntity);
			} else {
				result = true;
			}
			//
			List<VlanEntity> vlanEntitys = new ArrayList<VlanEntity>(1);
			vlanEntitys.add(vlanEntity);
			ParmRep parmRep = handleVlanEntity(vlanEntitys, result);
			parmRep.setDescs(DELETE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	public void batchConfigVlan(String ip, String client, String clientIp,
			Message message) {
		VlanEntity vlanEntity = getVlanEntity(message);
		if (null != vlanEntity) {
			if (1 != vlanEntity.getVlanID()) {
				String[] ipValue = ip.split(",");
				// 如果vlanID已经存在，先删除
				for (int i = 0; i < ipValue.length; i++) {
					boolean b = vlanHandler.vlanIsOfNo(ipValue[i], vlanEntity);
					if (b == true) {
						deleteVlan(ipValue[i], client, clientIp, message);
					}
					// 先创建VLANID
					if (vlanEntity.getIssuedTag() == 0) {
						createVlanId(ipValue[i], vlanEntity);
					}
				}
			}
			// 在vlan上增加端口
			boolean result = addBatchVlan(vlanEntity);

			List<VlanEntity> vlanEntitys = new ArrayList<VlanEntity>(1);
			vlanEntitys.add(vlanEntity);
			ParmRep parmRep = handleVlanEntity(vlanEntitys, result);

			parmRep.setObjects(vlanEntitys);
			parmRep.getParmIds().clear();
			parmRep.setDescs(INSERT);

			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, "", client, clientIp);
		}

	}

	/**
	 * 创建VLANID
	 * 
	 * @param ip
	 * @param vlanEntity
	 */
	private boolean createVlanId(String ip, VlanEntity vlanEntity) {
		boolean result = false;
		if (null == ip || "".equals(ip)) {
			result = true;
			return result;
		} else {
			String[] ipValue = ip.split(",");
			for (int i = 0; i < ipValue.length; i++) {
				result = vlanHandler.createVlanId(ipValue[i], vlanEntity);
			}
		}

		return result;
	}

	/**
	 * 批量增加vlan
	 * 
	 * @param vlanEntity
	 * @param client
	 * @param clientIp
	 * @return
	 */
	private boolean addBatchVlan(VlanEntity vlanEntity) {
		boolean result = true;
		if (vlanEntity != null) {
			List<String> list = new ArrayList<String>();
			Iterator iterator = vlanEntity.getPortConfig().iterator();
			while (iterator.hasNext()) {
				VlanPortConfig vlanPortConfig = (VlanPortConfig) iterator
						.next();
				String ipValue = vlanPortConfig.getIpVlue();
				if (!list.contains(ipValue)) {
					list.add(ipValue);
				}
			}

			List<VlanEntity> vlanEntityList = new ArrayList<VlanEntity>();
			for (String ips : list) {
				VlanEntity entity = composeVlanEntity(vlanEntity);
				Object[] objects = vlanEntity.getPortConfig().toArray();
				for (int i = 0; i < objects.length; i++) {
					String ipValue = ((VlanPortConfig) objects[i]).getIpVlue();
					if (ips.equals(ipValue)) {
						entity.getPortConfig().add((VlanPortConfig) objects[i]);
					}
				}
				vlanEntityList.add(entity);
			}

			for (VlanEntity entity : vlanEntityList) {
				Object[] objects = entity.getPortConfig().toArray();
				String ipValue = ((VlanPortConfig) objects[0]).getIpVlue();
				result = vlanHandler.createVlanPorts(ipValue, entity);
				if (!result) {
					return result;
				}
			}
		}

		return result;
	}

	private VlanEntity composeVlanEntity(VlanEntity vlanEntity) {
		VlanEntity entity = new VlanEntity();
		entity.setId(vlanEntity.getId());
		entity.setSyschorized(vlanEntity.isSyschorized());
		entity.setVlanConfig(vlanEntity.getVlanConfig());
		entity.setVlanID(vlanEntity.getVlanID());
		entity.setVlanName(vlanEntity.getVlanName());
		entity.setPortConfig(new HashSet<VlanPortConfig>());
		return entity;
	}

	public void configVlan(String ip, String client, String clientIp,
			Message message) {
		List<VlanEntity> vlanList = getVlanEntityList(message);
		if (vlanList != null) {
			boolean result = vlanHandler.updateVlan(ip, vlanList);
			//
			ParmRep parmRep = handleVlanEntity(vlanList, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	public void configVlanPort(String ip, String client, String clientIp,
			Message message) {
		List<VlanPort> vlanPortList = getVlanPortList(message);
		if (vlanPortList != null) {
			boolean result = vlanHandler.updateVlanPort(ip, vlanPortList);
			//
			ParmRep parmRep = handleVlanPort(vlanPortList, result);
			parmRep.setDescs(UPDATE);
			messageSend.sendObjectMessageRes(
					com.jhw.adm.server.entity.util.Constants.DEV_SWITCHER2,
					parmRep, ip, client, clientIp);
		}
	}

	/**
	 * 
	 * @param ip
	 * @param client
	 * @param clientIp
	 * @param message
	 */
	public void getVlanInfo(String ip, String client, String clientIp,
			Message message) {
		Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
		if (synchDeviceSet != null) {
			for (SynchDevice synchDevice : synchDeviceSet) {
				String deviceIp = synchDevice.getIpvalue();
				int deviceType = synchDevice.getDeviceType();

				HashMap<Class, List> synMap = new HashMap<Class, List>();
				VlanConfig vlanConfig = new VlanConfig();
				List<VlanEntity> vlanList = new ArrayList<VlanEntity>();
				Set<VlanEntity> vlanSet = vlanHandler.getVlan(vlanConfig,
						deviceIp);
				if (vlanSet != null) {
					for (VlanEntity vlanEntity : vlanSet) {
						vlanList.add(vlanEntity);
					}
				}
				// String mac = systemHandler.getMAC(deviceIp);
				String STATE = SUCCESS;
				if (vlanSet != null) {
					synMap.put(VlanEntity.class, vlanList);
				} else {
					STATE = FAIL;
				}
				messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
						MessageNoConstants.SINGLESYNCHREP, synchDevice
								.getIpvalue(), client, clientIp);
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

	private MessageHandleIF getVlanInfoHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			getVlanInfo(ip, client, clientIp, message);
		}

	};

	public void getVlanPortInfo(String ip, String client, String clientIp,
			Message message) {
		Set<SynchDevice> synchDeviceSet = getSingleSynchSet(message);
		if (synchDeviceSet != null) {
			for (SynchDevice synchDevice : synchDeviceSet) {
				String deviceIp = synchDevice.getIpvalue();
				int deviceType = synchDevice.getDeviceType();

				HashMap<Class, List> synMap = new HashMap<Class, List>();
				VlanConfig vlanConfig = new VlanConfig();
				Set<VlanPort> portSet = vlanHandler.getVlanPort(vlanConfig,
						deviceIp);
				List<VlanPort> vlanPortList = new ArrayList<VlanPort>();
				if (portSet != null) {
					for (VlanPort vlanPort : portSet) {
						vlanPortList.add(vlanPort);
					}
				}
				// String mac = systemHandler.getMAC(deviceIp);
				String STATE = SUCCESS;
				if (portSet != null) {
					synMap.put(VlanPort.class, vlanPortList);
				} else {
					STATE = FAIL;
				}
				messageSend.sendObjectMessageRes(deviceType, synMap, STATE,
						MessageNoConstants.SINGLESYNCHREP, synchDevice
								.getIpvalue(), client, clientIp);
			}
			// messageSend.sendTextMessageRes("上载完成", SUCCESS,
			// MessageNoConstants.SINGLESYNCHONEFINISH, ip, client,
			// clientIp);
		}
	}

	private MessageHandleIF getVlanPortInfoHandle = new MessageHandleIF() {
		@Override
		public void doHandle(String ip, String client, String clientIp,
				Message message) {
			getVlanPortInfo(ip, client, clientIp, message);
		}

	};

	private ParmRep handleVlanEntity(List<VlanEntity> vlanEntitys,
			boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (VlanEntity vlanEntity : vlanEntitys) {
			parmIds.add(vlanEntity.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(VlanEntity.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	private ParmRep handleVlanPort(List<VlanPort> vlanPorts, boolean result) {
		ParmRep parmRep = new ParmRep();
		List<Long> parmIds = new ArrayList<Long>();
		for (VlanPort vlanPort : vlanPorts) {
			parmIds.add(vlanPort.getId());
		}
		parmRep.setParmIds(parmIds);
		parmRep.setParmClass(VlanPort.class);
		if (result) {
			parmRep.setSuccess(true);
		} else {
			parmRep.setSuccess(false);
		}
		return parmRep;
	}

	//
	private VlanEntity getVlanEntity(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		VlanEntity vlanEntity = null;
		try {
			vlanEntity = (VlanEntity) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return vlanEntity;
	}

	private List<VlanEntity> getVlanEntityList(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		// List<VlanEntity> vlanList = null;
		// try {
		// vlanList = (List<VlanEntity>) om.getObject();
		// } catch (JMSException e) {
		// log.error(getTraceMessage(e));
		// return null;
		// }

		List<VlanEntity> vlanList = new ArrayList<VlanEntity>();
		try {
			VlanEntity vlanEntity = (VlanEntity) om.getObject();
			vlanList.add(vlanEntity);
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return vlanList;
	}

	private List<VlanPort> getVlanPortList(Message message) {
		ObjectMessage om = (ObjectMessage) message;
		List<VlanPort> vlanPortList = null;
		try {
			vlanPortList = (List<VlanPort>) om.getObject();
		} catch (JMSException e) {
			log.error(getTraceMessage(e));
			return null;
		}
		return vlanPortList;
	}

	public VlanHandler getVlanHandler() {
		return vlanHandler;
	}

	public void setVlanHandler(VlanHandler vlanHandler) {
		this.vlanHandler = vlanHandler;
	}

	public SystemHandler getSystemHandler() {
		return systemHandler;
	}

	public void setSystemHandler(SystemHandler systemHandler) {
		this.systemHandler = systemHandler;
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

}
