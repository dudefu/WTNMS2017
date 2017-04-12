package com.jhw.adm.server.interfaces;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Local;
import javax.jms.JMSException;

@Local
public interface AdmServiceBeanLocal {

	/**
	 * �������沢���õ��豸
	 * 
	 * @param ipValue
	 * @param doNumber
	 * @param obj
	 * @return
	 * @throws JMSException
	 */
	public Serializable saveAndSetting(String macValue, int mt,
			Serializable obj, String from, String clientIp,int deviceType, int SYN_TYPE)
			throws JMSException;

	/**
	 * �������沢���õ��豸
	 * 
	 * @param ipValue
	 * @param doNumber
	 * @param objs
	 * @throws JMSException
	 */
	public void saveAndSetting(String macValue, int mt, List<?> objs,
			String from, String clientIp,int deviceType, int SYN_TYPE) throws JMSException;

	/**
	 * ���沢�㲥
	 * 
	 * @param doNumber
	 * @param obj
	 * @throws JMSException
	 */
	public void saveAndBroadcasting(int mt, Serializable obj, String from,
			String clientIp,int SYN_TYPE) throws JMSException;

	/**
	 * ���²������豸
	 * 
	 * @param ipValue
	 * @param doNumber
	 * @param obj
	 * @throws JMSException
	 */
	public void updateAndSetting(String macValue, int mt, Serializable obj,
			String from, String clientIp, int deviceType,int SYN_TYPE) throws JMSException;

	/**
	 * ���²��㲥
	 * 
	 * @param doNumber
	 * @param obj
	 * @throws JMSException
	 */
	public void updateAndBroadcasting(int mt, Serializable obj, String from,
			String clientIp,int SYN_TYPE) throws JMSException;

	/**
	 * ɾ�����ݿⲢɾ���豸��Ϣ
	 * 
	 * @param ipValue
	 * @param doNumber
	 * @param obj
	 * @throws JMSException
	 */
	public void deleteAndSetting(String macValue, int mt, Serializable obj,
			String from, String clientIp,int deviceType, int SYN_TYPE) throws JMSException;

	/**
	 * �㲥��Ϣ
	 * 
	 * @param doNumber
	 * @param obj
	 * @throws JMSException
	 */
	public void broadcastMessage(int mt, Serializable obj, String from,
			String clientIp, int SYN_TYPE) throws JMSException;

	/**
	 * �㲥�ı���Ϣ
	 * 
	 * @param doNumber
	 * @param text
	 * @throws JMSException
	 */
	public void broadcastMessage(int mt, String text, String from,
			String clientIp, int SYN_TYPE) throws JMSException;

	public void updateAndSetting(String macvalue, int mt,
			List<Serializable> objs, String from, String clientIp,int deviceType, int SYN_TYPE)
			throws JMSException;

	/**
	 * �ж��Ƿ��Fep������Ϣ
	 * 
	 * @param macvalue
	 * @param mt
	 * @param objs
	 * @param from
	 * @param clientIp
	 * @param sendOrNotFep
	 * @throws JMSException
	 */
	public void updateAndSetting(String macvalue, int mt,
			List<Serializable> objs, String from, String clientIp,
			boolean sendOrNotFep, int deviceType,int SYN_TYPE) throws JMSException;

	/**
	 * ���ò������ز���
	 * 
	 * @param mt
	 * @param from
	 * @param clientIp
	 * @param obj
	 * @throws JMSException
	 */
	public void saveAndSettingCarrier(int mt, String from, String clientIp,
			int carrierCode, Serializable obj, int SYN_TYPE)
			throws JMSException;

	/**
	 * ɾ�������������ز���·��
	 * 
	 * @param mt
	 * @param from
	 * @param clientIp
	 * @param carrierCode
	 * @param obj
	 * @throws JMSException
	 */
	public void deleteAndSettingCarrier(int mt, String from, String clientIp,
			int carrierCode, Serializable obj, int SYN_TYPE)
			throws JMSException;

	/**
	 * ���ò������ز���
	 * 
	 * @param mt
	 * @param from
	 * @param clientIp
	 * @param obj
	 * @throws JMSException
	 */
	public void updateAndSettingCarrier(int mt, String from, String clientIp,
			Serializable obj, int SYN_TYPE) throws JMSException;

	/**
	 * ����ɾ��
	 * 
	 * @param macValue
	 * @param mt
	 * @param datas
	 * @param from
	 * @param clientIp
	 * @throws JMSException
	 */
	public void deleteAndSettings(String macValue, int mt, List datas,
			String from, String clientIp,int deviceType, int SYN_TYPE) throws JMSException;

	/**
	 * ͨ��ip����ĳ�豸�ϵĲ�������
	 * @param ipValue
	 * @param mt
	 * @param objs
	 * @param from
	 * @param clientIp
	 * @param SYN_TYPE
	 * @throws JMSException
	 */
	public void saveAndSettingByIP(String ipValue, int mt, List<?> objs,
			String from, String clientIp,int deviceType, int SYN_TYPE) throws JMSException;

	/**
	 * ͨ��ip�޸��豸�ϵ�ĳ��������
	 * @param ipValue
	 * @param mt
	 * @param objs
	 * @param from
	 * @param clientIp
	 * @param SYN_TYPE
	 * @throws JMSException
	 */
	public void updateAndSettingByIp(String ipValue, int mt,
			List<Serializable> objs, String from, String clientIp,int deviceType, int SYN_TYPE)
			throws JMSException;

	/**
	 * ͨ��ipɾ���豸�ϵ�ĳ����
	 * @param ipValue
	 * @param mt
	 * @param datas
	 * @param from
	 * @param clientIp
	 * @param SYN_TYPE
	 * @throws JMSException
	 */
	public void deleteAndSettingByIp(String ipValue, int mt, List datas,
			String from, String clientIp,int deviceType, int SYN_TYPE) throws JMSException;

}
