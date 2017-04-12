package com.jhw.adm.server.interfaces;

import java.io.Serializable;
import java.util.List;

import javax.ejb.Local;
import javax.jms.JMSException;

@Local
public interface AdmServiceBeanLocal {

	/**
	 * 单个保存并设置到设备
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
	 * 批量保存并设置到设备
	 * 
	 * @param ipValue
	 * @param doNumber
	 * @param objs
	 * @throws JMSException
	 */
	public void saveAndSetting(String macValue, int mt, List<?> objs,
			String from, String clientIp,int deviceType, int SYN_TYPE) throws JMSException;

	/**
	 * 保存并广播
	 * 
	 * @param doNumber
	 * @param obj
	 * @throws JMSException
	 */
	public void saveAndBroadcasting(int mt, Serializable obj, String from,
			String clientIp,int SYN_TYPE) throws JMSException;

	/**
	 * 更新并设置设备
	 * 
	 * @param ipValue
	 * @param doNumber
	 * @param obj
	 * @throws JMSException
	 */
	public void updateAndSetting(String macValue, int mt, Serializable obj,
			String from, String clientIp, int deviceType,int SYN_TYPE) throws JMSException;

	/**
	 * 更新并广播
	 * 
	 * @param doNumber
	 * @param obj
	 * @throws JMSException
	 */
	public void updateAndBroadcasting(int mt, Serializable obj, String from,
			String clientIp,int SYN_TYPE) throws JMSException;

	/**
	 * 删除数据库并删除设备消息
	 * 
	 * @param ipValue
	 * @param doNumber
	 * @param obj
	 * @throws JMSException
	 */
	public void deleteAndSetting(String macValue, int mt, Serializable obj,
			String from, String clientIp,int deviceType, int SYN_TYPE) throws JMSException;

	/**
	 * 广播消息
	 * 
	 * @param doNumber
	 * @param obj
	 * @throws JMSException
	 */
	public void broadcastMessage(int mt, Serializable obj, String from,
			String clientIp, int SYN_TYPE) throws JMSException;

	/**
	 * 广播文本消息
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
	 * 判断是否给Fep发送消息
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
	 * 设置并保存载波机
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
	 * 删除并从新设置载波机路由
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
	 * 设置并更新载波机
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
	 * 批量删除
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
	 * 通过ip增加某设备上的参数配置
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
	 * 通过ip修改设备上的某参数配置
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
	 * 通过ip删除设备上的某参数
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
