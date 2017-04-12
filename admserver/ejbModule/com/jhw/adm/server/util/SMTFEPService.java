package com.jhw.adm.server.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicConnectionFactory;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.switchs.SwitchUser;
import com.jhw.adm.server.entity.switchs.SysLogHostToDevEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.PingResult;
import com.jhw.adm.server.entity.warning.FaultDetection;
import com.jhw.adm.server.interfaces.DataCacheLocal;
import com.jhw.adm.server.interfaces.SMTCServiceLocal;
import com.jhw.adm.server.interfaces.SMTFEPServiceLocal;

/**
 * 用于从服务器端发送消息到前置机的sessionbean
 * 
 * @author 杨霄
 * 
 */
@Stateless(mappedName = "SMTFEPService")
@Local(SMTFEPServiceLocal.class)
public class SMTFEPService implements SMTFEPServiceLocal {
	@EJB
	private DataCacheLocal dataCache;

	@Resource(mappedName = "ClusteredXAConnectionFactory")
	private TopicConnectionFactory topicConnf;

	@Resource(mappedName = "topic/STFTopic")
	private Topic stfTopic;
	@EJB
	private SMTCServiceLocal smtcService;
	private static final Logger LOG = LoggerFactory
			.getLogger(SMTFEPService.class);

	public void splitSendMessage(int mt, String from, String clientIp,
			List<PingResult> messages) throws JMSException {
		Map<String, List<PingResult>> map = new HashMap<String, List<PingResult>>();
		// 把要ping的设备按照前置机分组
		for (PingResult pr : messages) {
			String ipValue = pr.getIpValue();
			String fepCode = dataCache.getFepCodeByIP(ipValue);
			if (fepCode == null) {
				pr.setStatus(3);
				smtcService.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "该设备不在前置机管理范围内");
				continue;
			} else {

				FEPEntity fepEntity = dataCache.getFEPByCode(fepCode);
				if (fepEntity != null) {
					if (fepEntity.getStatus() != null
							&& !fepEntity.getStatus().isStatus()) {

						smtcService.sendMessage(MessageNoConstants.FEPOFFLINE,
								"server", from, clientIp, "该设备所属前置机不在线");
						continue;
					}

				}
			}

			List<PingResult> data = (ArrayList) map.get(fepCode);
			if (data == null) {
				data = new ArrayList<PingResult>();
				map.put(fepCode, data);
				data.add(pr);
			} else {
				data.add(pr);
			}
		}
		// 开始ping一个前置管辖的所有ip
		Set<String> set = map.keySet();
		for (String fepCode : set) {
			ArrayList values = (ArrayList) map.get(fepCode);
			MessageProducer producer = dataCache.getProducerByCode(fepCode);
			ObjectMessage message = dataCache.getSession().createObjectMessage(
					values);
			message.setIntProperty(Constants.MESSAGETYPE, mt);
			message.setStringProperty(Constants.MESSAGEFROM, from);
			message.setStringProperty(Constants.CLIENTIP, clientIp);
			producer.send(message);
		}
	}

	public void sendVlanMessage(String ipValues, int mt, String from,
			String clientIp, Serializable obj) throws JMSException {
		String[] ipValue = ipValues.split(",");
		String fepCode = dataCache.getFepCodeByIP(ipValue[0]);
		if (fepCode == null) {
			return;
		}
		FEPEntity fepEntity = dataCache.getFEPByCode(fepCode);
		if(fepEntity==null){
			smtcService.sendMessage(MessageNoConstants.FEPOFFLINE,
					"server", from, clientIp, "该设备所属前置机不在线");
			return;
		}
		if (fepEntity.getStatus() != null&& !fepEntity.getStatus().isStatus()) {
				smtcService.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "该设备所属前置机不在线");
				return;
		}
		MessageProducer producer = dataCache.getProducerByCode(fepCode);
		ObjectMessage message = dataCache.getSession().createObjectMessage(obj);
		message.setIntProperty(Constants.MESSAGETYPE, mt);
		message.setStringProperty(Constants.MESSAGETO, ipValues);
		message.setStringProperty(Constants.MESSAGEFROM, from);
		message.setStringProperty(Constants.CLIENTIP, clientIp);
		producer.send(message);

	}

	public void sendRingMessage(String ipValues, int mt, String from,
			String clientIp, int ringID, Serializable obj) throws JMSException {

		String[] ipValue = ipValues.split(",");
		String fepCode = dataCache.getFepCodeByIP(ipValue[0]);
		if (fepCode == null) {
			return;
		}
		FEPEntity fepEntity = dataCache.getFEPByCode(fepCode);
		if(fepEntity==null){
			smtcService.sendMessage(MessageNoConstants.FEPOFFLINE,
					"server", from, clientIp, "该设备所属前置机不在线");
			return;
		}
		if (fepEntity.getStatus() != null
					&& !fepEntity.getStatus().isStatus()) {
				smtcService.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "该设备所属前置机不在线");
				return;
		}
		
		MessageProducer producer = dataCache.getProducerByCode(fepCode);
		ObjectMessage message = dataCache.getSession().createObjectMessage(obj);
		message.setIntProperty(Constants.MESSAGETYPE, mt);
		message.setStringProperty(Constants.RING_ID, ringID + "");
		message.setStringProperty(Constants.MESSAGETO, ipValues);
		message.setStringProperty(Constants.MESSAGEFROM, from);
		message.setStringProperty(Constants.CLIENTIP, clientIp);
		producer.send(message);

	}

	public void sendSNMPHostMessage(String ipValues, int mt, String from,
			String clientIp, Map map) throws JMSException {
		if (ipValues != null && !ipValues.equals("")) {
			String[] ipValue = ipValues.split(",");
			if (ipValue.length > 0) {

				String fepCode = dataCache.getFepCodeByIP(ipValue[0]);
				if (fepCode == null) {
					return;
				}
				FEPEntity fepEntity = dataCache.getFEPByCode(fepCode);
				if(fepEntity==null){
					smtcService.sendMessage(MessageNoConstants.FEPOFFLINE,
							"server", from, clientIp, "该设备所属前置机不在线");
					return;
				}
				if (fepEntity.getStatus() != null
							&& !fepEntity.getStatus().isStatus()) {
						smtcService.sendMessage(MessageNoConstants.FEPOFFLINE,
								"server", from, clientIp, "该设备所属前置机不在线");
						return;
				}
				MessageProducer producer = dataCache.getProducerByCode(fepCode);
				ObjectMessage message = dataCache.getSession()
						.createObjectMessage((Serializable) map);
				message.setIntProperty(Constants.MESSAGETYPE, mt);
				message.setStringProperty(Constants.MESSAGETO, ipValues);
				message.setStringProperty(Constants.MESSAGEFROM, from);
				message.setStringProperty(Constants.CLIENTIP, clientIp);
				producer.send(message);
			} else {
				LOG.error("配置SNMPHOST时，交换机IP为空！");
			}
		} else {
			LOG.error("配置SNMPHOST时，交换机IP为空！");
		}
	}

	public void timerFaultDetection(int mt, String from, String clientIp,
			List<FaultDetection> messages) throws JMSException {
		Map<String, List<String>> map = new HashMap<String, List<String>>();
		Date startDate = null;
		Date endDate = null;
		int timers = 0;
		int jiange = 0;
		for (FaultDetection pr : messages) {
			String ipValues = pr.getIpvlaues();
			startDate = pr.getBeginTime();
			endDate = pr.getEndTime();
			timers = pr.getPingtimes();
			jiange = pr.getJiange();
			String ips[] = ipValues.split(",");
			if (ips != null && ips.length > 0) {
				for (int i = 0; i < ips.length; i++) {
					String ipValue = ips[i];
					String fepCode = dataCache.getFepCodeByIP(ipValue);
					if (fepCode == null) {
						continue;
					}
					List<String> data = map.get(fepCode);
					if (data == null) {
						data = new ArrayList<String>();
						map.put(fepCode, data);
						data.add(ipValue);
					} else {
						data.add(ipValue);
					}
				}
			}
		}
		Date nowDate = new Date();
		if (nowDate.before(endDate) && nowDate.after(startDate)) {
			Set<String> set = map.keySet();
			for (Iterator<String> it = set.iterator(); it.hasNext();) {
				String key = it.next();
				ArrayList<String> values = (ArrayList<String>) map.get(key);
				MessageProducer producer = dataCache.getProducerByCode(key
						.toString());
				ObjectMessage message = dataCache.getSession()
						.createObjectMessage(values);
				message.setIntProperty(Constants.MESSAGETYPE, mt);
				message.setIntProperty(Constants.PING_TIMES, timers);
				message.setIntProperty(Constants.PING_JIANGE, jiange);
				message.setStringProperty(Constants.MESSAGEFROM, from);
				message.setStringProperty(Constants.CLIENTIP, clientIp);
				producer.send(message);

			}
		}
	}

	@SuppressWarnings("unchecked")
	public void sendMessageToIPs(int mt, String from, String clientIp,
			Set<SynchDevice> sds) throws JMSException {
		try {
			if (sds != null) {
				Map map = dataCache.splitIps(sds);
				Set<String> keys = map.keySet();
				if (keys == null || keys.size() == 0) {
					smtcService.sendMessage(MessageNoConstants.FEPOFFLINE,
							"server", from, clientIp,
							"没有前置机在线,请稍后再试或查看前置机网段配置！");
				} else {
					for (String code : keys) {
						FEPEntity fep = dataCache.getFEPByCode(code);
						if (fep == null) {
							continue;
						}
						boolean ok = fep.getStatus().isStatus();
						if (!ok) {
							smtcService.sendMessage(
									MessageNoConstants.FEPOFFLINE, "server",
									from, clientIp, "前置机" + fep.getCode()
											+ "不在线或查看前置机网段配置！");
							continue;
						} else {

							System.out.println("发送给前置机：  " + code);
							HashSet<SynchDevice> bsds = (HashSet<SynchDevice>) map
									.get(code);
							MessageProducer producer = dataCache
									.getProducerByCode(code);
							if (producer != null) {

								ObjectMessage message = dataCache.getSession()
										.createObjectMessage(bsds);
								message.setIntProperty(Constants.MESSAGETYPE,
										mt);
								message.setStringProperty(
										Constants.MESSAGEFROM, from);
								message.setStringProperty(Constants.CLIENTIP,
										clientIp);
								producer.send(message);
								System.out.println("同步消息已经发送!");
							} else {
								LOG.error("无法找到前置机");
							}

						}
					}
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
			// TODO: handle exception
		}

	}
	
	public void sendMessageToIPs(int parmType,int mt, String from, String clientIp,
			Set<SynchDevice> sds){
		
		try {
			if (sds != null) {
				Map map = dataCache.splitIps(sds);
				Set<String> keys = map.keySet();
				if (keys == null || keys.size() == 0) {
					smtcService.sendMessage(MessageNoConstants.FEPOFFLINE,"server", from, clientIp,"没有前置机在线,请稍后再试或查看前置机网段配置！");
				} else {
					for (String code : keys) {
						FEPEntity fep = dataCache.getFEPByCode(code);
						if (fep == null) {
							continue;
						}
						boolean ok = fep.getStatus().isStatus();
						if (!ok) {
							smtcService.sendMessage(MessageNoConstants.FEPOFFLINE, "server",from, clientIp, "前置机" + fep.getCode()
											+ "不在线或查看前置机网段配置！");
							continue;
						} else {

							HashSet<SynchDevice> bsds = (HashSet<SynchDevice>) map.get(code);
							MessageProducer producer = dataCache.getProducerByCode(code);
							if (producer != null) {
								ObjectMessage message = dataCache.getSession().createObjectMessage(bsds);
								message.setIntProperty(Constants.MESSAGETYPE,mt);
								message.setStringProperty(Constants.MESSAGEFROM, from);
								message.setStringProperty(Constants.CLIENTIP,clientIp);
								message.setIntProperty(Constants.MESSPARMTYPE, parmType);
								producer.send(message);
								System.out.println("同步消息已经发送!");
							} else {
								LOG.error("无法找到前置机");
							}

						}
					}
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
			// TODO: handle exception
		}

		
	}

	private boolean checkFep(String fepCode, String from, String clientIp) {
		boolean tag = true;
		if (fepCode == null) {
			try {
				smtcService.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "该设备不在前置机管理范围内");
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}

		FEPEntity fep = dataCache.getFEPByCode(fepCode);
		if (fep == null || !fep.getStatus().isStatus()) {
			tag = false;
		}
		if (!tag) {
			try {
				smtcService.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "前置机不在线！请稍后再试");
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 发送多条针对某个设备的消息到前置机
	 */
	public void sendMessage(String ipValue, int mt, String from,
			String clientIp, int deviceType, List<?> objs) throws JMSException {
		String fepCode = dataCache.getFepCodeByIP(ipValue);
		boolean online = checkFep(fepCode, from, clientIp);
		if (!online) {
			return;
		}
		MessageProducer producer = dataCache.getProducerByCode(fepCode);
		ObjectMessage message = dataCache.getSession().createObjectMessage(
				(ArrayList) objs);
		message.setIntProperty(Constants.MESSAGETYPE, mt);
		message.setStringProperty(Constants.MESSAGETO, ipValue);
		message.setStringProperty(Constants.MESSAGEFROM, from);
		message.setStringProperty(Constants.CLIENTIP, clientIp);
		message.setIntProperty(Constants.DEVTYPE, deviceType);
		producer.send(message);
	}

	/**
	 * 发送给前置机的对象消息
	 * 
	 * @param fepCode
	 * @param mt
	 * @param from
	 * @param obj
	 * @throws JMSException
	 */
	public void sendMessageToFEP(String fepCode, int mt, String from,
			String clientIp, Serializable obj) throws JMSException {
		boolean online = checkFep(fepCode, from, clientIp);
		if (!online) {
			return;
		}
		MessageProducer producer = dataCache.getProducerByCode(fepCode);
		ObjectMessage message = dataCache.getSession().createObjectMessage(obj);
		message.setIntProperty(Constants.MESSAGETYPE, mt);
		message.setStringProperty(Constants.AIMFEP, fepCode);
		message.setStringProperty(Constants.MESSAGEFROM, from);
		message.setStringProperty(Constants.CLIENTIP, clientIp);
		producer.send(message);
	}

	/**
	 * 发送给前置机的对象消息(交换机用户管理)
	 * 
	 * @param fepCode
	 * @param mt
	 * @param from
	 * @param obj
	 * @throws JMSException
	 */
	public void sendMessageToFEP(int mt, String from, String clientIp,
			List<SwitchUser> messages, int switchUserAction)
			throws JMSException {

		Map<String, List<SwitchUser>> map = new HashMap<String, List<SwitchUser>>();
		for (SwitchUser switchUser : messages) {
			String ipValue = switchUser.getSwitchNode().getBaseConfig()
					.getIpValue();
			String fepCode = dataCache.getFepCodeByIP(ipValue);
			if (fepCode == null) {
				smtcService.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "该设备不在前置机管理范围内");
				continue;
			} else {
				FEPEntity fepEntity = dataCache.getFEPByCode(fepCode);
				if (fepEntity != null) {
					if (fepEntity.getStatus() != null&& !fepEntity.getStatus().isStatus()) {
						smtcService.sendMessage(MessageNoConstants.FEPOFFLINE,"server", from, clientIp, "该设备所属前置机不在线");
						continue;
					}
				}
			}
			List<SwitchUser> data = (ArrayList) map.get(fepCode);
			if (data == null) {
				data = new ArrayList<SwitchUser>();
				map.put(fepCode, data);
				data.add(switchUser);
			} else {
				data.add(switchUser);
			}
		}
		Set<String> set = map.keySet();
		for (String fepCode : set) {
			ArrayList values = (ArrayList) map.get(fepCode);
			MessageProducer producer = dataCache.getProducerByCode(fepCode);
			ObjectMessage message = dataCache.getSession().createObjectMessage(values);
			message.setIntProperty(Constants.MESSAGETYPE, mt);
			message.setStringProperty(Constants.MESSAGEFROM, from);
			message.setStringProperty(Constants.CLIENTIP, clientIp);
			message.setIntProperty(Constants.SWITCHUSERS, switchUserAction);
			producer.send(message);
		}
	}

	/**
	 * 发送给前置机的文本消息
	 * 
	 * @param fepCode
	 * @param mt
	 * @param from
	 * @param txt
	 * @throws JMSException
	 */
	public void sendMessageToFEP(String fepCode, int mt, String from,
			String clientIp, String txt) throws JMSException {
		boolean online = checkFep(fepCode, from, clientIp);
		if (!online) {
			return;
		}
		MessageProducer producer = dataCache.getProducerByCode(fepCode);
		TextMessage message = dataCache.getSession().createTextMessage(txt);
		message.setIntProperty(Constants.MESSAGETYPE, mt);
		message.setStringProperty(Constants.AIMFEP, fepCode);
		message.setStringProperty(Constants.MESSAGEFROM, from);
		message.setStringProperty(Constants.CLIENTIP, clientIp);
		producer.send(message);
	}

	/**
	 * 发送针对某个设备的文本消息到特定的前置机
	 * 
	 * @param fepCode
	 * @param messagetxt
	 * @throws JMSException
	 */
	public void sendMessage(String ipValue, int mt, String from,
			String clientIp, int deviceType, String messagetxt)
			throws JMSException {
		String fepCode = dataCache.getFepCodeByIP(ipValue);
		boolean online = checkFep(fepCode, from, clientIp);
		if (!online) {
			return;
		}
		MessageProducer producer = dataCache.getProducerByCode(fepCode);
		if (producer == null) {
			return;
		}
		TextMessage message = dataCache.getSession().createTextMessage(
				messagetxt);
		message.setStringProperty(Constants.MESSAGEFROM, from);
		message.setIntProperty(Constants.MESSAGETYPE, mt);
		message.setStringProperty(Constants.MESSAGETO, ipValue);
		message.setStringProperty(Constants.CLIENTIP, clientIp);
		message.setIntProperty(Constants.DEVTYPE, deviceType);
		producer.send(message);
	}

	/**
	 * 发送针对某个设备的对象消息到特定的前置机
	 * 
	 * @param ipValue
	 * @param from
	 * @param messageType
	 * @param obj
	 * @throws JMSException
	 */
	public void sendMessage(String ipValue, int mt, String from,
			String clientIp, int deviceType, Serializable obj)
			throws JMSException {
		if(ipValue!=null&&!ipValue.equals("")){
		String fepCode = dataCache.getFepCodeByIP(ipValue);
		boolean online = checkFep(fepCode, from, clientIp);
		if (!online) {
			return;
		}
		MessageProducer producer = dataCache.getProducerByCode(fepCode);
		ObjectMessage message = dataCache.getSession().createObjectMessage(obj);
		message.setStringProperty(Constants.MESSAGEFROM, from);
		message.setIntProperty(Constants.MESSAGETYPE, mt);
		message.setStringProperty(Constants.MESSAGETO, ipValue);
		message.setStringProperty(Constants.CLIENTIP, clientIp);
		message.setIntProperty(Constants.DEVTYPE, deviceType);
		producer.send(message);
		}else{
			LOG.error("发送给前置机的设备IP为空。");
		}
	}

	/**
	 * 用于广播文本消息到前置机
	 * 
	 * @param messagetxt
	 * @throws JMSException
	 */
	public void broadcastMessage(int mt, String messagetxt) throws JMSException {
		TopicSession session = topicConnf.createTopicConnection()
				.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
		TextMessage txtMessage = session.createTextMessage(messagetxt);
		txtMessage.setIntProperty(Constants.MESSAGETYPE, mt);
		TopicPublisher publisher = session.createPublisher(stfTopic);
		publisher.publish(txtMessage);
		System.out.println("SMTFService have sent message :" + txtMessage);
		publisher.close();
	}

	/**
	 * 用于广播对象消息到前置机
	 * 
	 * @param messagetxt
	 * @throws JMSException
	 */
	public void broadcastMessage(int mt, Serializable obj) throws JMSException {
		TopicSession session = topicConnf.createTopicConnection()
				.createTopicSession(false, TopicSession.AUTO_ACKNOWLEDGE);
		ObjectMessage objMessage = session.createObjectMessage(obj);
		objMessage.setIntProperty(Constants.MESSAGETYPE, mt);
		TopicPublisher publisher = session.createPublisher(stfTopic);
		publisher.publish(objMessage);
		publisher.close();
	}

	// ======================================载波机消息发送====================================================
	public void sendCarrierMessage(int mt, String from, String clientIp,
			Serializable obj) throws JMSException {
		CarrierEntity carrier = (CarrierEntity) obj;
		int carrierCode = carrier.getCarrierCode();
		String fepCode = carrier.getFepCode();
		boolean online = checkFep(fepCode, from, clientIp);
		if (!online) {
			return;
		}
		MessageProducer producer = dataCache.getProducerByCode(fepCode);
		ObjectMessage message = dataCache.getSession().createObjectMessage(obj);
		message.setStringProperty(Constants.MESSAGEFROM, from);
		message.setIntProperty(Constants.MESSAGETYPE, mt);
		message.setIntProperty(Constants.MESSAGETO, carrierCode);
		message.setStringProperty(Constants.CLIENTIP, clientIp);
		producer.send(message);
	}

	public void sendCarrierMessage(String fepCode, int carrierCode, int mt,
			String from, String clientIp, String txt) throws JMSException {
		MessageProducer producer = dataCache.getProducerByCode(fepCode);
		boolean online = checkFep(fepCode, from, clientIp);
		if (!online) {
			return;
		}
		TextMessage message = dataCache.getSession().createTextMessage(txt);
		message.setIntProperty(Constants.MESSAGETYPE, mt);
		message.setStringProperty(Constants.MESSAGEFROM, from);
		message.setStringProperty(Constants.CLIENTIP, clientIp);
		message.setIntProperty(Constants.MESSAGETO, carrierCode);
		producer.send(message);
	}

	public void sendMsgToFep(String fepCode, int mt, String from, String txt)
			throws JMSException {

		MessageProducer producer = dataCache.getProducerByCode(fepCode);
		TextMessage message = dataCache.getSession().createTextMessage(txt);
		message.setIntProperty(Constants.MESSAGETYPE, mt);
		message.setStringProperty(Constants.MESSAGEFROM, from);
		message.setStringProperty(Constants.CLIENTIP, "");
		producer.send(message);
	}

	@SuppressWarnings("unchecked")
	public void sendStreamMessage(StreamMessage sm) throws JMSException {

		int mt = sm.getIntProperty(Constants.MESSAGETYPE);
		// int deviceType = sm.getIntProperty(Constants.DEVTYPE);
		String from = sm.getStringProperty(Constants.MESSAGEFROM);
		String clientIp = sm.getStringProperty(Constants.CLIENTIP);
		String ipVlues = sm.getStringProperty(Constants.MESSAGETO);
		if (mt == MessageNoConstants.CARRIERSYSTEMUPGRADE) {
			// 载波机升级
			StreamMessage streamMessage = dataCache.getSession()
					.createStreamMessage();
			streamMessage.setStringProperty(Constants.MESSAGEFROM, from);
			streamMessage.setStringProperty(Constants.CLIENTIP, clientIp);
			streamMessage.setIntProperty(Constants.MESSAGETYPE, mt);
			Object object = sm.readObject();
			if (object != null) {
				streamMessage.writeObject(object);
			}

			String ips[] = ipVlues.split(",");
			if (ips.length > 0) {
				Map fepIP = dataCache.splitIps(ips);
				Set<String> keys = fepIP.keySet();
				if (keys != null && keys.size() > 0) {
					for (String fepCode : keys) {
						boolean online = checkFep(fepCode, from, clientIp);
						if (!online) {
							continue;
						}
						String ip = (String) fepIP.get(fepCode);
						MessageProducer producer = dataCache
								.getProducerByCode(fepCode);
						if (producer != null) {
							streamMessage.setStringProperty(Constants.MESSAGETO, ip);
							producer.send(streamMessage);
							streamMessage.clearBody();
							streamMessage.clearProperties();
						}

					}

				}

			}

		} else if (mt == MessageNoConstants.SWITCHERUPGRATE) {
			boolean resStart = sm.getBooleanProperty(Constants.RESTART);
			this.switchUpdate(sm, mt, resStart, from, clientIp);
		}

	}

	private void switchUpdate(StreamMessage sm, int mt, boolean resStart,
			String from, String clientIp) throws JMSException {
		StreamMessage streamMessage = dataCache.getSession()
				.createStreamMessage();
		streamMessage.setStringProperty(Constants.MESSAGEFROM, sm
				.getStringProperty(Constants.MESSAGEFROM));
		streamMessage.setStringProperty(Constants.CLIENTIP, sm
				.getStringProperty(Constants.CLIENTIP));
		streamMessage.setIntProperty(Constants.MESSAGETYPE, mt);
		streamMessage.setBooleanProperty(Constants.RESTART, resStart);
		Object object = sm.readObject();
		if (object != null) {
			streamMessage.writeObject(object);
		}
		String ipVlues = sm.getStringProperty(Constants.MESSAGETO);
		String ips[] = ipVlues.split(",");
		if (ips.length > 0) {
			Map fepIP = dataCache.splitIps(ips);
			Set<String> keys = fepIP.keySet();
			if (keys != null && keys.size() > 0) {
				for (String fepCode : keys) {
					boolean online = checkFep(fepCode, from, clientIp);
					if (!online) {
						continue;
					}
					String ip = (String) fepIP.get(fepCode);
					MessageProducer producer = dataCache
							.getProducerByCode(fepCode);
					if (producer != null) {
						streamMessage.setStringProperty(Constants.MESSAGETO, ip);
						producer.send(streamMessage);
						streamMessage.clearBody();
						streamMessage.clearProperties();
						
					}
				}
			}
		}
	}

	/**
	 * 向前置机发送syslog配置消息
	 * @param devEntityList
	 * @param from
	 * @param clientIp
	 * @param operateType
	 * @throws JMSException
	 */
	public void sendSysLogConfigMessage(Serializable devEntityList,
			String from,String clientIp,int operateType) throws JMSException{
		String ip = ((List<SysLogHostToDevEntity>)devEntityList).get(0).getIpValue();
		
		String fepCode = dataCache.getFepCodeByIP(ip);
//		if (fepCode == null) {
//			return;
//		}
//		FEPEntity fepEntity = dataCache.getFEPByCode(fepCode);
//		if(fepEntity==null){
//			smtcService.sendMessage(MessageNoConstants.FEPOFFLINE,
//					"server", from, clientIp, "该设备所属前置机不在线");
//			return;
//		}
//		if (fepEntity.getStatus() != null&& !fepEntity.getStatus().isStatus()) {
//				smtcService.sendMessage(MessageNoConstants.FEPOFFLINE,
//						"server", from, clientIp, "该设备所属前置机不在线");
//				return;
//		}
		MessageProducer producer = dataCache.getProducerByCode(fepCode);
		ObjectMessage message = dataCache.getSession().createObjectMessage(devEntityList);
		message.setIntProperty(Constants.MESSAGETYPE, MessageNoConstants.SYSLOGHOSTSAVE);  //消息类型
		message.setStringProperty(Constants.MESSAGETO, "");
		message.setStringProperty(Constants.MESSAGEFROM, from);
		message.setStringProperty(Constants.CLIENTIP, clientIp);
		producer.send(message);
		
	}
	
	/**
	 * 向前置机发送删除syslogtoDev消息
	 * @param devEntityList
	 * @param from
	 * @param clientIp
	 * @param operateType
	 * @throws JMSException
	 */
	public void sendSysLogToDevDelMessage(Serializable devEntityList,
			String from,String clientIp,int operateType) throws JMSException{
		String ip = ((List<SysLogHostToDevEntity>)devEntityList).get(0).getIpValue();
		
		String fepCode = dataCache.getFepCodeByIP(ip);
//		if (fepCode == null) {
//			return;
//		}
//		FEPEntity fepEntity = dataCache.getFEPByCode(fepCode);
//		if(fepEntity==null){
//			smtcService.sendMessage(MessageNoConstants.FEPOFFLINE,
//					"server", from, clientIp, "该设备所属前置机不在线");
//			return;
//		}
//		if (fepEntity.getStatus() != null&& !fepEntity.getStatus().isStatus()) {
//				smtcService.sendMessage(MessageNoConstants.FEPOFFLINE,
//						"server", from, clientIp, "该设备所属前置机不在线");
//				return;
//		}
		MessageProducer producer = dataCache.getProducerByCode(fepCode);
		ObjectMessage message = dataCache.getSession().createObjectMessage(devEntityList);
		message.setIntProperty(Constants.MESSAGETYPE, MessageNoConstants.SYSLOGHOSTDELETE);  //消息类型
		message.setStringProperty(Constants.MESSAGETO, "");
		message.setStringProperty(Constants.MESSAGEFROM, from);
		message.setStringProperty(Constants.CLIENTIP, clientIp);
		producer.send(message);
		
	}
}