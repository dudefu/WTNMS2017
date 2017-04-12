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
	 * 通过在拓扑图形上删除节点，连同交换机的所有数据一起删除
	 * 
	 * @param topentity
	 */
	public void deleteSwitch(SwitchTopoNodeEntity topentity);

	/**
	 * 通过SwitchNodeEntity作为条件，删除特定的参数对象
	 * 
	 * @param clazz
	 * @param entity
	 */
	public void deleteEntityBySwitch(Class<?> clazz, SwitchNodeEntity entity);
    
	/**
	 * 通过交换机查询特定参数
	 * @param clazz
	 * @param entity
	 * @return
	 */
	public List<?> queryEntityBySwitch(Class<?> clazz, SwitchNodeEntity entity);
	
	/**
	 * 通过switch3作为条件，删除特定的参数对象
	 * @param clazz
	 * @param layer3
	 */
	public void deleteEntityBySwitch3(Class<?> clazz, SwitchLayer3 layer3);
	/**
	 * 删除olt的所有参数
	 * 
	 * @param clazz
	 * @param entity
	 */
	public void deleteEntityByOLT(Class<?> clazz, OLTEntity entity);

	/**
	 * 删除onu的所有参数
	 * @param clazz
	 * @param entity
	 */
	public void deleteEntityByONU(Class<?> clazz, ONUEntity entity);

	/**
	 * 更新交换机跟前置机连接的lldp状态
	 * @param lldp
	 */
	public void updateLLDP(LLDPInofEntity lldp);


	/**
	 * 删除连线
	 * 
	 * @param link
	 */
	public void deleteLink(LinkEntity link, int syn_type, String from,
			String clientIp);

	/**
	 * 刷新拓扑开始的时候调用，用来为生成新的拓扑图做准备
	 */
	public void refurshTopo(Long diagramId);

	/**
	 * 更新gprs设备信息
	 * 
	 * @param entity
	 */
	public GPRSEntity updateGPRSEntity(GPRSEntity entity);

	/**
	 * 通过IP查找用户信息
	 * 
	 * @param ip_value
	 * @return
	 */
	public List<PersonBean> findPersonInfo(String ip_value);

	/**
	 * 通过IP查询用户名
	 * 
	 * @param ip_value
	 * @return
	 */
	public List<UserEntity> findUserInfo(String ip_value);

	/**
	 * 查询前后20秒数据是否存在
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
	 * 查询要定时ping的数据
	 * 
	 * @return
	 */
	public List<FaultDetection> queryPingResult();
	
	/**
	 * 查询要定时监控的数据
	 * @return
	 */
	public List<TimerMonitoring> queryTimerMonitoring();

	/**
	 * 通过SwitchBaseConfig得到交换机
	 * 
	 * @param baseConfig
	 * @return
	 */
	public SwitchNodeEntity findSwitchNodeEntity(SwitchBaseConfig baseConfig);

	/**
	 * 通过交换机获得VlanConfig
	 * 
	 * @param nodeEntity
	 * @return
	 */
	public VlanConfig findVlanConfig(SwitchNodeEntity nodeEntity);

	

	/**
	 * 通过ＩＰ查询环
	 * 
	 * @param ip
	 * @return
	 */
	public RingConfig queryRingConfig(String ip);

	/**
	 * 通过ＩＰ和环ID查询环
	 * 
	 * @param ip
	 * @return
	 */
	public RingConfig queryRingConfig(String ip, int ringID,boolean ringEnable);
	/**
	 * 通过snmpHostIP查询SNMP
	 * 
	 * @param hostIp
	 * @return
	 */
	public List<SNMPHost> querySNMPHost(String hostIp,String snmpVersion,String massName);

	/**
	 * 通过switchNodeEntity查询SNMP
	 * 
	 * @param hostIp
	 * @return
	 */
	public List<SNMPHost> querySNMPHost(SwitchNodeEntity switchNodeEntity);

	/**
	 * 同步时删除所有SnmpHost
	 */
	public void deleteAllSnmpHost();

	/**
	 * 查询所有的ＳＮＭＰＨＯＳＴ
	 */
	public List<SNMPHost> queryAllSNMPhost();
	
	/**
	 * 通过IP查询三层交换机Vlan
	 * @param ipValue
	 * @return
	 */
	public List<Switch3VlanEnity> querySwitch3VlanEnity(String ipValue);
	
	/**
	 * 通过IP查询三层交换机端口
	 * @param ipValue
	 * @return
	 */
	public List<SwitchPortLevel3> querySwitch3PortEnity(String ipValue);

	/**
	 * 得到超级用户
	 * @return
	 */
	public UserEntity getSuperAdmin();
	
	/**
	 * 保存llid的不同数据
	 * @param llid
	 */
	public void saveLLID(ONULLID llid);
	
	public TopDiagramEntity findTopDiagramEntity(Long id);
	public LinkEntity queryLinkEntity(Long id);
	
	public FEPEntity findFepEntity(String code);
	
	public List<LinkEntity> queryLinkEntity(NodeEntity nodeEntity);
	
	public List<LinkEntity> queryLinkEntityByRingID(int ringNo);
	
	/**
	 * 更新交换机参数为网管侧
	 * @param clazz
	 * @param entity
	 */
	public void updateEntityBySwitch(Class<?> clazz, SwitchNodeEntity entity);
	
	/**
	 * 通过nodeID直接查询数据库
	 * @param nodeID
	 * @return
	 */
	public List<UserEntity> queryUserEntityByNode(long nodeID);
	
	/**
	 * 通过SysLogHostEntity的主机IP和主机端口判断数据库中是否已经有这条记录了，
	 * 如果没有就保存这条记录
	 * 上载时调用
	 * @param hostEntity
	 */
	public SysLogHostEntity saveSysLogHostDB(SysLogHostEntity hostEntity);
}
