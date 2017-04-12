package com.jhw.adm.server.servic;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.Remote;
import javax.jms.JMSException;

import com.jhw.adm.server.entity.nets.VlanPortConfig;
import com.jhw.adm.server.entity.ports.LLDPInofEntity;
import com.jhw.adm.server.entity.switchs.SNMPHost;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.switchs.SysLogHostEntity;
import com.jhw.adm.server.entity.switchs.SysLogHostToDevEntity;
import com.jhw.adm.server.entity.system.AreaEntity;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.SubNetTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.TopDiagramEntity;
import com.jhw.adm.server.entity.util.GPRSEntity;
import com.jhw.adm.server.entity.util.LogEntity;
import com.jhw.adm.server.entity.util.PersonBean;
import com.jhw.adm.server.entity.util.RingBean;
import com.jhw.adm.server.entity.util.RingInfo;
import com.jhw.adm.server.entity.util.SNMPGroupBean;
import com.jhw.adm.server.entity.util.SNMPHostBean;
import com.jhw.adm.server.entity.util.SNMPMassBean;
import com.jhw.adm.server.entity.util.SNMPSwitchIPBean;
import com.jhw.adm.server.entity.util.SNMPUserBean;
import com.jhw.adm.server.entity.util.SNMPViewBean;
import com.jhw.adm.server.entity.util.SwitchVlanPortInfo;
import com.jhw.adm.server.entity.util.TrapCountBean;
import com.jhw.adm.server.entity.util.TrapWarningBean;
import com.jhw.adm.server.entity.util.VlanBean;
import com.jhw.adm.server.entity.util.WarningHistoryBean;
import com.jhw.adm.server.entity.warning.FaultDetectionRecord;
import com.jhw.adm.server.entity.warning.RmonCount;
import com.jhw.adm.server.entity.warning.SimpleWarning;
import com.jhw.adm.server.entity.warning.TimerMonitoringSheet;
import com.jhw.adm.server.entity.warning.TrapSimpleWarning;
import com.jhw.adm.server.entity.warning.TrapWarningEntity;
import com.jhw.adm.server.entity.warning.WarningEntity;
import com.jhw.adm.server.entity.warning.WarningHistoryEntity;
import com.jhw.adm.server.entity.warning.WarningRecord;

@Remote
public interface NMSServiceRemote {
	
	
	/**
	 * ͨ��������ͼ����ɾ���ڵ㣬��ͬ����������������һ��ɾ��
	 * 
	 * @param topentity
	 */
	public void deleteSwitch(SwitchTopoNodeEntity topentity);

	/**
	 * ͨ��������ͼ����ɾ���ڵ㣬��ͬ���������������ݺͽڵ����ӵ���һ��ɾ��
	 * @param topentity
	 * @param list
	 */
	 public void deleteAllNode(NodeEntity nodeEntity);
	
	/**
	 * ͨ��SwitchNodeEntity��Ϊ������ɾ���ض��Ĳ�������
	 * 
	 * @param clazz
	 * @param entity
	 */
	public void deleteEntityBySwitch(Class<?> clazz, SwitchNodeEntity entity);

	
	
	

	public void updateLLDP(LLDPInofEntity lldp);

	/**
	 * ��ѯ����̽���
	 * 
	 * @param trapWarningBean
	 * @return
	 */
	public List<FaultDetectionRecord> findFaultDetection(
			TrapWarningBean trapWarningBean);

	/**
	 * ����̽������
	 * 
	 * @param trapWarningBean
	 * @return
	 */

	public long queryAllFaultDetection(TrapWarningBean trapWarningBean);

	/**
	 * ���һ����ǰ�����ܼ�ⱨ����Ϣ
	 */
	public void deleteRmonCount();

	/**
	 * ���ݼ򵥸澯��Ϣ��ѯ�����ĸ澯��Ϣ
	 * 
	 * @param sw
	 * @return
	 */
	public TrapWarningEntity findTrapEntity(SimpleWarning sw);

	/**
	 * ���ݼ򵥸澯��Ϣ��ѯ������ѯ�ĸ澯��Ϣ
	 * 
	 * @param sw
	 * @return
	 */
	public TrapSimpleWarning findTrapSimpleWarning(SimpleWarning sw);

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
	 * �澯��Ϣ��ѯ
	 * 
	 * @param startDate
	 * @param endDate
	 * @param level
	 * @param ipValue
	 * @param status
	 * @param warningType
	 * @return
	 */
	public List<SimpleWarning> findWarningInfo(TrapWarningBean trapWarningBean);

	/**
	 * �澯��Ϣ����
	 * 
	 * @param startDate
	 * @param endDate
	 * @param level
	 * @param ipValue
	 * @param status
	 * @param warningType
	 * @param userName
	 * @return
	 */
	public long queryAllRowCount(TrapWarningBean trapWarningBean);

	/**
	 * ��־��ѯ
	 * 
	 * @param startDate
	 * @param endDate
	 * @param userName
	 * @param ip
	 * @param maxPageSize
	 * @param startPage
	 * @return
	 */
	public List<LogEntity> queryLogEntity(Date startDate, Date endDate,
			String userName, String ip, int maxPageSize, int startPage);

	/**
	 * ��־����
	 * 
	 * @param startDate
	 * @param endDate
	 * @param userName
	 * @param ip
	 * @return
	 */
	public long queryAllRowCount(Date startDate, Date endDate, String userName,
			String ip);

	/**
	 * ��ѯ�澯��Ϣ���Ѽ�¼
	 * 
	 * @param startDate
	 * @param endDate
	 * @param userName
	 * @param ip
	 * @param maxPageSize
	 * @param startPage
	 * @return
	 */
	public List<WarningRecord> queryWarningRecord(Date startDate, Date endDate,
			String userName, String ip, int maxPageSize, int startPage);

	/**
	 * ��ѯ�澯��Ϣ���Ѽ�¼����
	 * 
	 * @param startDate
	 * @param endDate
	 * @param userName
	 * @param ip
	 * @return
	 */
	public long queryAllWarningCount(Date startDate, Date endDate,
			String userName, String ip);
    /**
     * ��ʱ��ؼ�¼����
     * @param startDate
     * @param endDate
     * @param ipValue
     * @param portNo
     * @return
     */
	public long queryAllMonitoringSheetNum(Date startDate,Date endDate,String ipValue,int portNo);
	/**
	 * ��ѯ��ʱ��ر���
	 * @param startDate
	 * @param endDate
	 * @param ipValue
	 * @param portNo
	 * @param maxPageSize
	 * @param startPage
	 * @return
	 */
	public List<TimerMonitoringSheet> queryMonitoringSheet(Date startDate,Date endDate,String ipValue,int portNo,int maxPageSize, int startPage);
	
	
	/**
	 * ���ܼ�ر����ѯ
	 * @param startDate
	 * @param endDate
	 * @param ipValue
	 * @param portNo
	 * @param param
	 * @return
	 */
	public List<RmonCount> queryRmonCount(Date startDate, Date endDate,
			String ipValue, int portNo,String param);
	/**
	 * ��ѯ���ܼ������
	 * 
	 * @param startDate
	 * @param endDate
	 * @param ipValue
	 * @param portNo
	 * @param maxPageSize
	 * @param startPage
	 * @return
	 */
	public List<RmonCount> queryRmonCount(Date startDate, Date endDate,
			String ipValue, int portNo, int maxPageSize, int startPage);

	/**
	 * ���ܼ����������
	 * 
	 * @param startDate
	 * @param endDate
	 * @param ipValue
	 * @param portNo
	 * @return
	 */
	public long queryAllRmonCountNum(Date startDate, Date endDate,
			String ipValue, int portNo);

	/**
	 * ͨ��Ip��ѯpersonInfo
	 * 
	 * @param ip_value
	 * @return
	 */
	public List<PersonBean> findPersonInfo(String ip_value);

	/**
	 * ����һ���^������й���Ա�͸�������ϼ�����Ĺ���Ա
	 * 
	 * @param area
	 * @return
	 */
	public List<UserEntity> findUsersByArea(AreaEntity area);

	public List<UserEntity> findUserInfo(String ip_value);

	/**
	 * �����־��¼
	 */
	public void clearLogEntity();

	/**
	 * ����澯������Ϣ
	 */
	public void clearWarningRecode();

	/**
	 * ��ѯ������Vlan
	 * 
	 * @return
	 */
	public List<VlanBean> querySwitchVlanInfo();

	/**
	 * ͨ��Vlan��ѯ������˿ںͽ�������Ϣ
	 * 
	 * @param vlanBean
	 * @return
	 */
	public List<SwitchVlanPortInfo> querySwitchVlanPortInfo(VlanBean vlanBean);

	/**
	 * ɾ��һ������ǰ�ĸ澯��Ϣ
	 */
	public void deleteTrapWarningInfo();

	/**
	 * Vlan����
	 * 
	 * @param vlanBean
	 * @param nowList
	 */
	public boolean saveVlan(VlanBean vlanBean, List<VlanPortConfig> nowList,
			String clientIP, int type,String userName);

	/**
	 * ͨ��ringBean��ѯ
	 * 
	 * @param ringBean
	 * @return
	 */
	public List<RingInfo> queryRingInfo(RingBean ringBean);

	/**
	 * ��ѯ����Ring�µ����ж˿�
	 * 
	 * @return
	 */
	public List<RingInfo> queryAllRingInfo();

	/**
	 * ��ѯ������
	 * 
	 * @return
	 */
	public List<RingBean> queryRingConfig();

	/**
	 * ���滷����
	 * 
	 * @param list
	 * @param clientIP
	 * @param ringBean
	 */
	public boolean saveRing(List<RingInfo> list, String clientIP,
			RingBean ringBean, int type,String userName);

	/**
	 * ͨ��HOSTIP��ѯSNMPHost
	 * 
	 * @param hostIp
	 * @return
	 */
	public List<SNMPHost> querySNMPHost(String hostIp,String snmpVersion,String massName);

	/**
	 * ����SNMPHOST
	 * 
	 * @param snmpHost
	 * @param clientIP
	 * @param from
	 */
	public boolean saveSNMPHost(List<SNMPHost> snmpHost, String clientIP,
			SNMPHostBean snmpHostBean, int type,String userName);

	/**
	 * SNMPHost��ѯ
	 * 
	 * @return
	 */
	public List<SNMPHostBean> queryAllSNMPHostBean();

	/**
	 * ͨ��SNMPHostBean ��ѯ�������еĽ�����
	 * 
	 * @param snmpHostBean
	 * @return
	 */
	public List<SNMPSwitchIPBean> querySwitchIPBeans(SNMPHostBean snmpHostBean);

	/**
	 * SNMP��ͼ
	 */
	public List<SNMPViewBean> querySNMPView();

	public List<SNMPSwitchIPBean> querySNMPSwitchIPBean(
			SNMPViewBean snmpViewBean);

	/**
	 * SNMP�û�
	 */
	public List<SNMPUserBean> querySNMPUserBean();

	public List<SNMPSwitchIPBean> querySNMPSwitchIPBean(
			SNMPUserBean snmpUserBean);

	/**
	 * SNMP����
	 */
	public List<SNMPMassBean> querySNMPMassBean();

	public List<SNMPSwitchIPBean> querySNMPSwitchIPBean(
			SNMPMassBean snmpMassBean);

	/**
	 * SNMPȺ��
	 */
	public List<SNMPGroupBean> querySNMPGroupBean();

	public List<SNMPSwitchIPBean> querySNMPSwitchIPBean(
			SNMPGroupBean snmpGroupBean);

	/**
	 * ͨ��ǰ�û���ȡ��ǰ�û������µ��豸���������㽻���������㽻������olt��onu
	 * 
	 * @param clazz
	 * @param seg
	 * @return
	 */
	public List findDeviceByFep(Class clazz, String code);
	/**
	 * ��ѯ����ͼByid
	 * @param id
	 * @return
	 */
	public TopDiagramEntity findTopDiagramEntity(Long id);
	/**
	 * ��ѯͼ
	 * @return
	 */
	public TopDiagramEntity findAllTopDiagramEntity();
	
	/**
	 * ����UserEntity���豸��Ȩ�޹���
	 * @param userEntity
	 * @return
	 */
	public TopDiagramEntity findAllTopDiagramEntity(UserEntity userEntity);
	
	/**
	 * ��ѯ���е����Ͽ�ͨѶ�Ķ˿�
	 * @return
	 */
	public List<String> queryAllSerialPort();
	/**
	 * ɾ��ͼ�Ͻڵ�
	 * @param node
	 * @param topDID
	 */
	public void deleteNode(Object node,long topDID);
	
	/**
	 * ͨ���ڵ���Һ�����ص���
	 * @param diagramID
	 * @param nodeEntity
	 * @return
	 */
	public List<LinkEntity> queryLinkEntity(long diagramID,NodeEntity nodeEntity);
	
	/**
	 * find child node
	 * @param diagramID
	 * @param parentNode
	 * @param userEntity
	 * @return
	 */
	public List<NodeEntity> queryNodeEntity(long diagramID,String parentNode,UserEntity userEntity);
	
	/**
	 * find child node
	 * @param diagramEntity
	 * @param nodeEntity
	 * @return
	 */
	public List<NodeEntity> queryNodeEntity(long diagramID,String parentNode);
	
	/**
	 * ������������������½ڵ����
	 * @param diagramID
	 * @param nodeEntity
	 * @return
	 */
	public SubNetTopoNodeEntity getSubNetTopoNodeEntity(long diagramID,String parentNode,UserEntity userEntity);
	
	/**
	 * ��ѯһ��ʱ�䲻ͬ����ĸ澯����
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<TrapCountBean> queryTrapWarningLevel(Date startDate,Date endDate);
	/**
	 * ��ѯһ��ʱ�䲻ͬ���ĸ澯����
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<TrapCountBean> queryTrapWarningCategory(Date startDate,Date endDate);
	
	/**
	 * ��ѯ�·ݵĸ澯����
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<TrapCountBean> queryTrapWarningMonth(Date startDate,Date endDate);
	
	
	/**
	 * ��ѯǰ5���µĸ澯����
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<TrapCountBean> queryTrapWarningByMonth5(Date date);
	
	
	/**
	 * �澯�������� ǰ10���豸
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	 public List<TrapCountBean> queryTrapWarningTop10(Date startDate,Date endDate);
	 
	
	public List findDevice(Class clazz);
	
	/***
	 * ɾ����
	 * @param linkEntity
	 */
	public void deleteLinkEntity(LinkEntity linkEntity);
	
	/**
	 * ��ѯ�豸
	 * @param ip
	 * @param nodeEntity
	 * @return
	 */
	public List<Object[]> queryDevice(String params,String name,Class<?> clazz,UserEntity userEntity);
	
	
	/**
	 * ��ΪUserEntity�е�nodeEntity���ӳټ��أ������ṩ����ͨ��userIDֱ�Ӳ�ѯ���ݿ�
	 * @param userID
	 * @return
	 */
	public List<NodeEntity> queryNodeEntityByUser(long userID);
	
	/**
	 * ͨ��nodeIDֱ�Ӳ�ѯ���ݿ�
	 * @param nodeID
	 * @return
	 */
	public List<UserEntity> queryUserEntityByNode(long nodeID);
	
	/**
	 * �澯��Ϣȷ�ϲ���,ȷ�Ϻ�鲢���澯��ʷ��
	 * @param warningEntities
	 */
	public void confirmWarningInfo(List<WarningEntity> warningEntities);
	
	/**
	 * ͨ���������ʷ�澯��Ϣ�õ����е�WarningEntity
	 * @param history
	 * @return
	 */
	public List<WarningEntity> queryWarningFromHistory(WarningHistoryEntity history);
	
	/**
	 * ͨ��WarningHistoryBean�еĲ�ѯ����,
	 * �õ���Ӧ�ĸ澯��ʷ��Ϣ������
	 * @param warningHistoryBean
	 * @return
	 */
	public long getAllWarningHistoryCount(WarningHistoryBean warningHistoryBean);
	
	/**
	 * ͨ��WarningHistoryBean�еĲ�ѯ����,
	 * �õ���Ӧ�ĸ澯��ʷ��Ϣ�б�
	 * @param warningHistoryBean
	 * @return
	 */
	public List<WarningHistoryEntity> queryWarningHistory(WarningHistoryBean warningHistoryBean);
	
	/**
	 * ͨ��IP�б�õ�SwitchTopoNodeEntity
	 * @param ips
	 * @return
	 */
	public List<SwitchTopoNodeEntity> getSwitchTopoNodeByIps(List<String> ips);

	/**
	 * ͨ��sysLogHostEntity��ѯ���й�����SysLogHostToDevEntity
	 * @param sysLogHostEntity
	 * @return
	 */
	public List<SysLogHostToDevEntity> getSysLogHostToDevBySysLog(SysLogHostEntity sysLogHostEntity);
	
	/**
	 * ����syslog������Ϣ
	 * @param devEntityList  syslogToDevʵ����Ϣ
	 * @param from           �ͻ����û���
	 * @param clientIp       �ͻ���IP
	 * @param operateType    ���ܲ�or�豸��
	 */
	public void configSyslogHost(List<SysLogHostToDevEntity> devEntityList,String from,String clientIp,int operateType);
	
	/**
	 * ֻ���浽���ݿ���
	 * @param devEntityList
	 * @param hostEntity
	 */
	public void saveSysLogHosts(List<SysLogHostToDevEntity> devEntityList,SysLogHostEntity hostEntity);
	
	/**
	 * ɾ��SysLogToDevEntity
	 * @param devEntityList
	 * @param from
	 * @param clientIp
	 * @param operateType
	 */
	public void deleteSysLogToDevEntity(List<SysLogHostToDevEntity> devEntityList
			,String from,String clientIp,int operateType);
	
	/**
	 * �޸��û�����
	 * @param userEntity
	 */
	public void updateUserPwd(UserEntity userEntity);
}
