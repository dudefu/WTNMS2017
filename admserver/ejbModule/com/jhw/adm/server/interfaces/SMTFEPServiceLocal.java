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
	 * �ֽ���Ϣ�����͸����ǰ�û�����Ե㷢����Ϣ��ǰ�û�
	 */
	public void splitSendMessage(int mt, String from, String clientIp,
			List<PingResult> messages) throws JMSException;

	/**
	 * ���Ͷ������ĳ���豸����Ϣ��ǰ�û�
	 */
	public void sendMessage(String ipValue, int mt, String from,
			String clientIp,int deviceType, List<?> objs) throws JMSException;

	/**
	 * ���͸�ǰ�û��Ķ�����Ϣ
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
	 * ���͸�ǰ�û��Ķ�����Ϣ(�������û�����)
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
	 * ���͸�ǰ�û����ı���Ϣ
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
	 * �������ĳ���豸���ı���Ϣ���ض���ǰ�û�
	 * 
	 * @param fepCode
	 * @param messagetxt
	 * @throws JMSException
	 */
	public void sendMessage(String ipValue, int mt, String from,
			String clientIp,int deviceType, String messagetxt) throws JMSException;

	/**
	 * �������ĳ���豸�Ķ�����Ϣ���ض���ǰ�û�
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
	 * ���ڹ㲥�ı���Ϣ��ǰ�û�
	 * 
	 * @param messagetxt
	 * @throws JMSException
	 */
	public void broadcastMessage(int MESSAGETYPE, String messagetxt)
			throws JMSException;

	/**
	 * ���ڹ㲥������Ϣ��ǰ�û�
	 * 
	 * @param messagetxt
	 * @throws JMSException
	 */
	public void broadcastMessage(int MESSAGETYPE, Serializable obj)
			throws JMSException;

	/**
	 * �����ز�����Ϣ
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
	 * ������Զ���豸�Ķ�����Ϣ
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
	 * �յ�fep������Ϣ ���ط�������������Ϣ��Fep
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
	 * ����Vlan���͸�ǰ�û���Ϣ
	 * @param ipValues
	 * @param mt
	 * @param from
	 * @param clientIp
	 * @param obj
	 * @throws JMSException
	 */
	public void sendVlanMessage(String ipValues, int mt, String from,String clientIp, Serializable obj) throws JMSException;
	
	/**
	 * ����Ring���͸�ǰ�û���Ϣ
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
	 * ��ǰ�û�����syslog������Ϣ
	 * @param devEntityList
	 * @param from
	 * @param clientIp
	 * @param operateType
	 * @throws JMSException
	 */
	public void sendSysLogConfigMessage(Serializable devEntityList,String from,String clientIp,int operateType) throws JMSException;

	/**
	 * ��ǰ�û�����ɾ��syslogtoDev��Ϣ
	 * @param devEntityList
	 * @param from
	 * @param clientIp
	 * @param operateType
	 * @throws JMSException
	 */
	public void sendSysLogToDevDelMessage(Serializable devEntityList,
			String from,String clientIp,int operateType) throws JMSException;
}
