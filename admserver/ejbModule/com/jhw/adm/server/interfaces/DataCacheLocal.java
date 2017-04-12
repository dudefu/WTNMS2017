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
	 * ͨ����Ҫ���õ��豸��ip��ַ����ȡ������豸��ǰ�û����
	 * 
	 * @param ipvalue
	 * @return
	 */
	public String getFepCodeByIP(String ipvalue);

	/**
	 * ͨ��ǰ�û���Ż�ȡ��Ϣ������
	 */
	public MessageProducer getProducerByCode(String code) throws JMSException;

	public TopicPublisher getTopicPublisher() throws JMSException;
	
	public TopicSession getTopicSession() throws JMSException;
	
	/**
	 * ��ȡsession
	 */
	public Session getSession() throws JMSException;

	public UserEntity getUser(String userName);

	public Map<String, Set> splitIps(Set<SynchDevice> sds);

	public Map<String,String> splitIps(String[] ipss);

	public String getIpByMac(String macvalue);

	public FEPEntity getFEPByCode(String code);

	public void addFEPEntity(String code, FEPEntity fep);

	/**
	 * ͨ��Code�õ����������Fep
	 * 
	 * @return
	 */
	public Map<String, FEPEntity> getFepMap();

	public void removeFEP(String code);

	public void cacheFep(String code, String pwssword);

	/**
	 * ͨ���豸��ip�ж�ǰ�û��Ƿ�����
	 * @param ip
	 * @return
	 */
	public boolean isFEPOnline(String ip);
	public boolean canContinueIP(String ipValue);

	public void cacheAllFeps();
	
}
