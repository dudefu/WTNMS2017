package com.jhw.adm.server.interfaces;

import java.util.Map;
import java.util.Set;

import javax.ejb.Local;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TopicPublisher;
import javax.jms.TopicSession;

import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.system.UserEntity;

@Local
public interface DataCacheLocal {

	/**
	 * 通过需要配置的设备的ip地址，获取管理该设备的前置机编号
	 * 
	 * @param ipvalue
	 * @return
	 */
	public String getFepCodeByIP(String ipvalue);

	/**
	 * 通过前置机编号获取消息发送器
	 */
	public MessageProducer getProducerByCode(String code) throws JMSException;

	public TopicPublisher getTopicPublisher() throws JMSException;
	
	public TopicSession getTopicSession() throws JMSException;
	
	/**
	 * 获取session
	 */
	public Session getSession() throws JMSException;

	public UserEntity getUser(String userName);

	public Map<String, Set> splitIps(Set<SynchDevice> sds);

	public Map<String,String> splitIps(String[] ipss);

	public String getIpByMac(String macvalue);

	public FEPEntity getFEPByCode(String code);

	public void addFEPEntity(String code, FEPEntity fep);

	/**
	 * 通过Code得到缓存里面的Fep
	 * 
	 * @return
	 */
	public Map<String, FEPEntity> getFepMap();

	public void removeFEP(String code);

	public void cacheFep(String code, String pwssword);

	/**
	 * 通过设备的ip判断前置机是否在线
	 * @param ip
	 * @return
	 */
	public boolean isFEPOnline(String ip);
	public boolean canContinueIP(String ipValue);

	public void cacheAllFeps();
	
}
