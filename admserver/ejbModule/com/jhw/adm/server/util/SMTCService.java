package com.jhw.adm.server.util;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.interfaces.DataCacheLocal;
import com.jhw.adm.server.interfaces.SMTCServiceLocal;
/**
 * 用于发送从服务器端到客户端的消息
 * 
 * @author 杨霄
 */
@Stateless(mappedName = "SMTCService")
@Local(SMTCServiceLocal.class)
public class SMTCService implements SMTCServiceLocal {
	@EJB
	private DataCacheLocal cacheLocal;
	/**
	 * 广播对象消息到客户端
	 */
	public void sendMessage(int mt, String from, String to, String clientIp,
			Serializable obj) throws JMSException {
		TopicPublisher publisher=null;
		TopicSession session=null;
		try {
			session=cacheLocal.getTopicSession();
			ObjectMessage objMessage = session.createObjectMessage(obj);
			objMessage.setIntProperty(Constants.MESSAGETYPE, mt);
			objMessage.setStringProperty(Constants.MESSAGEFROM, from);
			objMessage.setStringProperty(Constants.MESSAGETO, to);
			objMessage.setStringProperty(Constants.CLIENTIP, clientIp);
			publisher = cacheLocal.getTopicPublisher();
			publisher.publish(objMessage);
			objMessage.clearBody();
			objMessage.clearProperties();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	/**
	 * 广播告警消息对象到客户端
	 */
	public void sendWarningMessage(int mt, int warningType, String from, String to,int deviceType,Serializable obj) throws JMSException {
		TopicPublisher publisher=null;
		TopicSession session=null;
		try {
			session=cacheLocal.getTopicSession();
			ObjectMessage objMessage = session.createObjectMessage(obj);
			objMessage.setIntProperty(Constants.MESSAGETYPE, mt);
			objMessage.setIntProperty(Constants.TRAPTYPE, warningType);
			objMessage.setStringProperty(Constants.MESSAGEFROM, from);
			objMessage.setStringProperty(Constants.MESSAGETO, to);
			objMessage.setIntProperty(Constants.DEVTYPE, deviceType);
			publisher = cacheLocal.getTopicPublisher();
			publisher.publish(objMessage);
			objMessage.clearBody();
			objMessage.clearProperties();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 服务器端转发某个设备发送给客户端的消息
	 * 
	 * @param mt
	 *            :消息类型
	 * @param respon
	 *            ：操作结果：成功、失败
	 * @param from
	 *            ：消息来源：设备mac
	 * @param to
	 *            ：接收客户端的用户名
	 * @param message
	 *            ：消息内容
	 * @throws JMSException
	 */
	public void sendMessage(int mt, String respon, String from, String to,
			String clientIp, Serializable message) throws JMSException {
		TopicPublisher publisher=null;
		TopicSession session=null;
		try {
			session=cacheLocal.getTopicSession();
			ObjectMessage objMessage = session.createObjectMessage(message);
			objMessage.setIntProperty(Constants.MESSAGETYPE, mt);
			objMessage.setStringProperty(Constants.MESSAGETO, to);
			objMessage.setStringProperty(Constants.CLIENTIP, clientIp);
			objMessage.setStringProperty(Constants.MESSAGEFROM, from);
			objMessage.setStringProperty(Constants.MESSAGERES, respon);
			publisher = cacheLocal.getTopicPublisher();
			publisher.publish(objMessage);
			objMessage.clearBody();
			objMessage.clearProperties();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 广播文本消息到客户端
	 */
	
	public void sendMessage(int mt, String from, String to, String clientIp,
			String message) throws JMSException {
		TopicPublisher publisher=null;
		TopicSession session=null;
		try {
			session=cacheLocal.getTopicSession();
			TextMessage txtMessage = session.createTextMessage(message);
			txtMessage.setIntProperty(Constants.MESSAGETYPE, mt);
			txtMessage.setStringProperty(Constants.MESSAGEFROM, from);
			txtMessage.setStringProperty(Constants.MESSAGETO, to);
			txtMessage.setStringProperty(Constants.CLIENTIP, clientIp);
			publisher = cacheLocal.getTopicPublisher();
			publisher.publish(txtMessage);
			txtMessage.clearBody();
			txtMessage.clearProperties();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 服务器端转发某个设备发送给客户端的消息
	 * 
	 * @param mt
	 *            :消息类型
	 * @param respon
	 *            ：操作结果：成功、失败
	 * @param from
	 *            ：消息来源：设备mac
	 * @param to
	 *            ：接收客户端的用户名
	 * @param message
	 *            ：消息内容
	 * @throws JMSException
	 */
	public void sendMessage(int mt, String respon, String from, String to,
			String clientIp, String message) throws JMSException {
		TopicPublisher publisher=null;
		TopicSession session=null;
		try {
			session=cacheLocal.getTopicSession();
			TextMessage txtMessage = session.createTextMessage(message);
			txtMessage.setIntProperty(Constants.MESSAGETYPE, mt);
			txtMessage.setStringProperty(Constants.MESSAGETO, to);
			txtMessage.setStringProperty(Constants.CLIENTIP, clientIp);
			txtMessage.setStringProperty(Constants.MESSAGEFROM, from);
			txtMessage.setStringProperty(Constants.MESSAGERES, respon);
			publisher = cacheLocal.getTopicPublisher();
			publisher.publish(txtMessage);
			txtMessage.clearBody();
			txtMessage.clearProperties();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送心跳消息
	 */
	public  void sendHeartBeatMessage(String code, String ipValue, String message,int type)
			throws JMSException {
		TopicPublisher publisher=null;
		TopicSession session=null;
		try {
			session=cacheLocal.getTopicSession();
			TextMessage txtMessage = session.createTextMessage(message);
			txtMessage.setStringProperty(Constants.MESSAGETO, code);
			if (ipValue != null)
				txtMessage.setStringProperty(Constants.CLIENTIP, ipValue);
			txtMessage.setIntProperty(Constants.MESSAGETYPE,type);
			publisher = cacheLocal.getTopicPublisher();
			publisher.publish(txtMessage);
			txtMessage.clearBody();
			txtMessage.clearProperties();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	public void sendHeartBeatObjMeg(int type, String from, Serializable message) throws JMSException{
		TopicPublisher publisher=null;
		TopicSession session=null;
		try {
			session=cacheLocal.getTopicSession();
			ObjectMessage objMessage = session.createObjectMessage(message);
			objMessage.setIntProperty(Constants.MESSAGETYPE, type);
			objMessage.setStringProperty(Constants.MESSAGEFROM, from);
			publisher = cacheLocal.getTopicPublisher();
			publisher.publish(objMessage);
			objMessage.clearBody();
			objMessage.clearProperties();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void sendMessage(String from,String message) throws JMSException{
		TopicPublisher publisher=null;
		TopicSession session=null;
		try {
			session=cacheLocal.getTopicSession();
			 TextMessage txtMessage = session.createTextMessage(message);
		        txtMessage.setStringProperty(Constants.MESSAGEFROM, from);
		        txtMessage.setIntProperty(Constants.MESSAGETYPE,MessageNoConstants.CLIENTHEARTBEAT);
			publisher = cacheLocal.getTopicPublisher();
			publisher.publish(txtMessage);
			txtMessage.clearBody();
			txtMessage.clearProperties();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
