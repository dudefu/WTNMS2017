package com.jhw.adm.server.interfaces;

import java.io.Serializable;

import javax.ejb.Local;
import javax.jms.JMSException;

@Local
public interface SMTCServiceLocal {
	/**
	 * �㲥������Ϣ���ͻ���
	 */
	public void sendMessage(int mt, String from, String to,String clientIp, Serializable obj)
			throws JMSException;

	/**
	 * ��������ת��ĳ���豸���͸��ͻ��˵���Ϣ
	 * 
	 * @param mt
	 *            :��Ϣ����
	 * @param respon
	 *            ������������ɹ���ʧ��
	 * @param from
	 *            ����Ϣ��Դ���豸mac
	 * @param to
	 *            �����տͻ��˵��û���
	 * @param message
	 *            ����Ϣ����
	 * @throws JMSException
	 */
	public void sendMessage(int mt, String respon, String from, String to,String clientIp,
			Serializable message) throws JMSException;

	/**
	 * �㲥�ı���Ϣ���ͻ���
	 */
	public void sendMessage(int mt, String from, String to,String clientIp, String message)
			throws JMSException;

	/**
	 * ��������ת��ĳ���豸���͸��ͻ��˵���Ϣ
	 * 
	 * @param mt
	 *            :��Ϣ����
	 * @param respon
	 *            :������� ���ɹ���ʧ��
	 * @param from
	 *            :��Ϣ��Դ ���豸mac
	 * @param to
	 *            :���տͻ��˵��û���
	 * @param message
	 *            :��Ϣ����
	 * @throws JMSException
	 */
	public void sendMessage(int mt, String respon, String from, String to,String clientIp,
			String message) throws JMSException;

	/**
	 * ����������Ϣ
	 */
	public void sendHeartBeatMessage(String code, String ipValue, String message,int type)
			throws JMSException;
	
	/**
	 * ��Client���Ͷ�����Ϣ �б�ǰ�û��Ƿ�����
	 * @param type
	 * @param from
	 * @param clientIp
	 * @param message
	 * @throws JMSException
	 */
	public void sendHeartBeatObjMeg(int type, String from,Serializable message) throws JMSException;
	
	public void sendMessage(String from,String message)throws JMSException;
	/**
	 * �㲥�澯������Ϣ
	 * @param mt
	 * @param warningType
	 * @param from
	 * @param to
	 * @param obj
	 * @throws JMSException
	 */
	public void sendWarningMessage(int mt, int warningType, String from, String to,int deviceType,Serializable obj) throws JMSException;
}
