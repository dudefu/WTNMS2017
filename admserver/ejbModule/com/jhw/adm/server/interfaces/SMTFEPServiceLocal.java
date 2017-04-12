package com.jhw.adm.server.interfaces;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Local;
import javax.jms.JMSException;
import javax.jms.StreamMessage;

import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.SwitchUser;
import com.jhw.adm.server.entity.util.PingResult;
import com.jhw.adm.server.entity.warning.FaultDetection;

@Local
public interface SMTFEPServiceLocal {

	/**
	 * 分解消息，发送给多个前置机，点对点发送消息到前置机
	 */
	public void splitSendMessage(int mt, String from, String clientIp,
			List<PingResult> messages) throws JMSException;

	/**
	 * 发送多条针对某个设备的消息到前置机
	 */
	public void sendMessage(String ipValue, int mt, String from,
			String clientIp,int deviceType, List<?> objs) throws JMSException;

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
			String clientIp, Serializable obj) throws JMSException;

	
	/**
	 * 发送给前置机的对象消息(交换机用户管理)
	 * 
	 * @param fepCode
	 * @param mt
	 * @param from
	 * @param obj
	 * @throws JMSException
	 */
	public void sendMessageToFEP(int mt, String from,
			String clientIp, List<SwitchUser> messages,int switchUserAction) throws JMSException;
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
			String clientIp, String txt) throws JMSException;

	/**
	 * 发送针对某个设备的文本消息到特定的前置机
	 * 
	 * @param fepCode
	 * @param messagetxt
	 * @throws JMSException
	 */
	public void sendMessage(String ipValue, int mt, String from,
			String clientIp,int deviceType, String messagetxt) throws JMSException;

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
			String clientIp,int deviceType, Serializable obj) throws JMSException;

	/**
	 * 用于广播文本消息到前置机
	 * 
	 * @param messagetxt
	 * @throws JMSException
	 */
	public void broadcastMessage(int MESSAGETYPE, String messagetxt)
			throws JMSException;

	/**
	 * 用于广播对象消息到前置机
	 * 
	 * @param messagetxt
	 * @throws JMSException
	 */
	public void broadcastMessage(int MESSAGETYPE, Serializable obj)
			throws JMSException;

	/**
	 * 发送载波机消息
	 * 
	 * @param carrierCode
	 * @param mt
	 * @param from
	 * @param clientIp
	 * @param obj
	 * @throws JMSException
	 */
	public void sendCarrierMessage(int mt, String from, String clientIp,
			Serializable obj) throws JMSException;

	public void sendCarrierMessage(String fepCode, int carrierCode, int mt,
			String from, String clientIp, String txt) throws JMSException;

	public void sendStreamMessage(StreamMessage sm) throws JMSException;

	/**
	 * 发送针对多个设备的对象消息
	 * @param mt
	 * @param from
	 * @param clientIp
	 * @param obj
	 * @throws JMSException
	 */
	public void sendMessageToIPs(int mt, String from, String clientIp,
			Set<SynchDevice> sds) throws JMSException;
	
	
	public void sendMessageToIPs(int parmType,int mt, String from, String clientIp,
			Set<SynchDevice> sds) throws JMSException;
	
	/**
	 * 收到fep启动消息 返回服务器端启动消息给Fep
	 * @param fepCode
	 * @param mt
	 * @param from
	 * @param txt
	 * @throws JMSException
	 */
	public void sendMsgToFep(String fepCode, int mt,String from,String txt)throws JMSException;
		
		
	public void timerFaultDetection (int mt, String from, String clientIp,
			List<FaultDetection> messages) throws JMSException ;
	/**
	 * 配置Vlan发送给前置机消息
	 * @param ipValues
	 * @param mt
	 * @param from
	 * @param clientIp
	 * @param obj
	 * @throws JMSException
	 */
	public void sendVlanMessage(String ipValues, int mt, String from,String clientIp, Serializable obj) throws JMSException;
	
	/**
	 * 配置Ring发送给前置机消息
	 * @param ipValues
	 * @param mt
	 * @param from
	 * @param clientIp
	 * @param obj
	 * @throws JMSException
	 */
	public void sendRingMessage(String ipValues, int mt, String from,String clientIp,int ringID, Serializable obj) throws JMSException;
	
	public void sendSNMPHostMessage(String ipValues,int mt,String from,String clientIp,Map map)throws JMSException;
	
	/**
	 * 向前置机发送syslog配置消息
	 * @param devEntityList
	 * @param from
	 * @param clientIp
	 * @param operateType
	 * @throws JMSException
	 */
	public void sendSysLogConfigMessage(Serializable devEntityList,String from,String clientIp,int operateType) throws JMSException;

	/**
	 * 向前置机发送删除syslogtoDev消息
	 * @param devEntityList
	 * @param from
	 * @param clientIp
	 * @param operateType
	 * @throws JMSException
	 */
	public void sendSysLogToDevDelMessage(Serializable devEntityList,
			String from,String clientIp,int operateType) throws JMSException;
}
