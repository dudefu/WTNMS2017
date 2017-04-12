package com.jhw.adm.server.interfaces;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.epon.ONUEntity;
import com.jhw.adm.server.entity.epon.ONULLID;
import com.jhw.adm.server.entity.level3.Switch3VlanEnity;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.level3.SwitchPortLevel3;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.nets.VlanConfig;
import com.jhw.adm.server.entity.ports.LLDPInofEntity;
import com.jhw.adm.server.entity.ports.RingConfig;
import com.jhw.adm.server.entity.switchs.SNMPHost;
import com.jhw.adm.server.entity.switchs.SwitchBaseConfig;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.switchs.SysLogHostEntity;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.TopDiagramEntity;
import com.jhw.adm.server.entity.util.GPRSEntity;
import com.jhw.adm.server.entity.util.PersonBean;
import com.jhw.adm.server.entity.warning.FaultDetection;
import com.jhw.adm.server.entity.warning.SimpleWarning;
import com.jhw.adm.server.entity.warning.TimerMonitoring;
import com.jhw.adm.server.entity.warning.TrapSimpleWarning;
import com.jhw.adm.server.entity.warning.TrapWarningEntity;

@Local
public interface NMSServiceLocal {

	/**
	 * ͨ��������ͼ����ɾ���ڵ㣬��ͬ����������������һ��ɾ��
	 * 
	 * @param topentity
	 */
	public void deleteSwitch(SwitchTopoNodeEntity topentity);

	/**
	 * ͨ��SwitchNodeEntity��Ϊ������ɾ���ض��Ĳ�������
	 * 
	 * @param clazz
	 * @param entity
	 */
	public void deleteEntityBySwitch(Class<?> clazz, SwitchNodeEntity entity);
    
	/**
	 * ͨ����������ѯ�ض�����
	 * @param clazz
	 * @param entity
	 * @return
	 */
	public List<?> queryEntityBySwitch(Class<?> clazz, SwitchNodeEntity entity);
	
	/**
	 * ͨ��switch3��Ϊ������ɾ���ض��Ĳ�������
	 * @param clazz
	 * @param layer3
	 */
	public void deleteEntityBySwitch3(Class<?> clazz, SwitchLayer3 layer3);
	/**
	 * ɾ��olt�����в���
	 * 
	 * @param clazz
	 * @param entity
	 */
	public void deleteEntityByOLT(Class<?> clazz, OLTEntity entity);

	/**
	 * ɾ��onu�����в���
	 * @param clazz
	 * @param entity
	 */
	public void deleteEntityByONU(Class<?> clazz, ONUEntity entity);

	/**
	 * ���½�������ǰ�û����ӵ�lldp״̬
	 * @param lldp
	 */
	public void updateLLDP(LLDPInofEntity lldp);


	/**
	 * ɾ������
	 * 
	 * @param link
	 */
	public void deleteLink(LinkEntity link, int syn_type, String from,
			String clientIp);

	/**
	 * ˢ�����˿�ʼ��ʱ����ã�����Ϊ�����µ�����ͼ��׼��
	 */
	public void refurshTopo(Long diagramId);

	/**
	 * ����gprs�豸��Ϣ
	 * 
	 * @param entity
	 */
	public GPRSEntity updateGPRSEntity(GPRSEntity entity);

	/**
	 * ͨ��IP�����û���Ϣ
	 * 
	 * @param ip_value
	 * @return
	 */
	public List<PersonBean> findPersonInfo(String ip_value);

	/**
	 * ͨ��IP��ѯ�û���
	 * 
	 * @param ip_value
	 * @return
	 */
	public List<UserEntity> findUserInfo(String ip_value);

	/**
	 * ��ѯǰ��20�������Ƿ����
	 * 
	 * @param ip
	 * @param warnType
	 * @param level
	 * @param status
	 * @param date
	 * @return
	 */
	public List<TrapSimpleWarning> queryTrapSimpleWarning(String ip,
			int warnType, int status, Date date, int port);

	/**
	 * ��ѯҪ��ʱping������
	 * 
	 * @return
	 */
	public List<FaultDetection> queryPingResult();
	
	/**
	 * ��ѯҪ��ʱ��ص�����
	 * @return
	 */
	public List<TimerMonitoring> queryTimerMonitoring();

	/**
	 * ͨ��SwitchBaseConfig�õ�������
	 * 
	 * @param baseConfig
	 * @return
	 */
	public SwitchNodeEntity findSwitchNodeEntity(SwitchBaseConfig baseConfig);

	/**
	 * ͨ�����������VlanConfig
	 * 
	 * @param nodeEntity
	 * @return
	 */
	public VlanConfig findVlanConfig(SwitchNodeEntity nodeEntity);

	

	/**
	 * ͨ���ɣв�ѯ��
	 * 
	 * @param ip
	 * @return
	 */
	public RingConfig queryRingConfig(String ip);

	/**
	 * ͨ���ɣкͻ�ID��ѯ��
	 * 
	 * @param ip
	 * @return
	 */
	public RingConfig queryRingConfig(String ip, int ringID,boolean ringEnable);
	/**
	 * ͨ��snmpHostIP��ѯSNMP
	 * 
	 * @param hostIp
	 * @return
	 */
	public List<SNMPHost> querySNMPHost(String hostIp,String snmpVersion,String massName);

	/**
	 * ͨ��switchNodeEntity��ѯSNMP
	 * 
	 * @param hostIp
	 * @return
	 */
	public List<SNMPHost> querySNMPHost(SwitchNodeEntity switchNodeEntity);

	/**
	 * ͬ��ʱɾ������SnmpHost
	 */
	public void deleteAllSnmpHost();

	/**
	 * ��ѯ���еģӣΣͣУȣϣӣ�
	 */
	public List<SNMPHost> queryAllSNMPhost();
	
	/**
	 * ͨ��IP��ѯ���㽻����Vlan
	 * @param ipValue
	 * @return
	 */
	public List<Switch3VlanEnity> querySwitch3VlanEnity(String ipValue);
	
	/**
	 * ͨ��IP��ѯ���㽻�����˿�
	 * @param ipValue
	 * @return
	 */
	public List<SwitchPortLevel3> querySwitch3PortEnity(String ipValue);

	/**
	 * �õ������û�
	 * @return
	 */
	public UserEntity getSuperAdmin();
	
	/**
	 * ����llid�Ĳ�ͬ����
	 * @param llid
	 */
	public void saveLLID(ONULLID llid);
	
	public TopDiagramEntity findTopDiagramEntity(Long id);
	public LinkEntity queryLinkEntity(Long id);
	
	public FEPEntity findFepEntity(String code);
	
	public List<LinkEntity> queryLinkEntity(NodeEntity nodeEntity);
	
	public List<LinkEntity> queryLinkEntityByRingID(int ringNo);
	
	/**
	 * ���½���������Ϊ���ܲ�
	 * @param clazz
	 * @param entity
	 */
	public void updateEntityBySwitch(Class<?> clazz, SwitchNodeEntity entity);
	
	/**
	 * ͨ��nodeIDֱ�Ӳ�ѯ���ݿ�
	 * @param nodeID
	 * @return
	 */
	public List<UserEntity> queryUserEntityByNode(long nodeID);
	
	/**
	 * ͨ��SysLogHostEntity������IP�������˿��ж����ݿ����Ƿ��Ѿ���������¼�ˣ�
	 * ���û�оͱ���������¼
	 * ����ʱ����
	 * @param hostEntity
	 */
	public SysLogHostEntity saveSysLogHostDB(SysLogHostEntity hostEntity);
}
