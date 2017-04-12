package com.jhw.adm.server.interfaces;

import java.io.Serializable;

import javax.ejb.Local;
import javax.jms.JMSException;

@Local
public interface SMTCServiceLocal {
	/**
	 * 广播对象消息到客户端
	 */
	public void sendMessage(int mt, String from, String to,String clientIp, Serializable obj)
			throws JMSException;

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
	public void sendMessage(int mt, String respon, String from, String to,String clientIp,
			Serializable message) throws JMSException;

	/**
	 * 广播文本消息到客户端
	 */
	public void sendMessage(int mt, String from, String to,String clientIp, String message)
			throws JMSException;

	/**
	 * 服务器端转发某个设备发送给客户端的消息
	 * 
	 * @param mt
	 *            :消息类型
	 * @param respon
	 *            :操作结果 ：成功、失败
	 * @param from
	 *            :消息来源 ：设备mac
	 * @param to
	 *            :接收客户端的用户名
	 * @param message
	 *            :消息内容
	 * @throws JMSException
	 */
	public void sendMessage(int mt, String respon, String from, String to,String clientIp,
			String message) throws JMSException;

	/**
	 * 发送心跳消息
	 */
	public void sendHeartBeatMessage(String code, String ipValue, String message,int type)
			throws JMSException;
	
	/**
	 * 给Client发送对象消息 判别前置机是否启动
	 * @param type
	 * @param from
	 * @param clientIp
	 * @param message
	 * @throws JMSException
	 */
	public void sendHeartBeatObjMeg(int type, String from,Serializable message) throws JMSException;
	
	public void sendMessage(String from,String message)throws JMSException;
	/**
	 * 广播告警对象信息
	 * @param mt
	 * @param warningType
	 * @param from
	 * @param to
	 * @param obj
	 * @throws JMSException
	 */
	public void sendWarningMessage(int mt, int warningType, String from, String to,int deviceType,Serializable obj) throws JMSException;
}
