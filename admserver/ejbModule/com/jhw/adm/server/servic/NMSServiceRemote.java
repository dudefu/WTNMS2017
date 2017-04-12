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
	 * 通过在拓扑图形上删除节点，连同交换机的所有数据一起删除
	 * 
	 * @param topentity
	 */
	public void deleteSwitch(SwitchTopoNodeEntity topentity);

	/**
	 * 通过在拓扑图形上删除节点，连同交换机的所有数据和节点连接的线一起删除
	 * @param topentity
	 * @param list
	 */
	 public void deleteAllNode(NodeEntity nodeEntity);
	
	/**
	 * 通过SwitchNodeEntity作为条件，删除特定的参数对象
	 * 
	 * @param clazz
	 * @param entity
	 */
	public void deleteEntityBySwitch(Class<?> clazz, SwitchNodeEntity entity);

	
	
	

	public void updateLLDP(LLDPInofEntity lldp);

	/**
	 * 查询故障探测表
	 * 
	 * @param trapWarningBean
	 * @return
	 */
	public List<FaultDetectionRecord> findFaultDetection(
			TrapWarningBean trapWarningBean);

	/**
	 * 故障探测表笔数
	 * 
	 * @param trapWarningBean
	 * @return
	 */

	public long queryAllFaultDetection(TrapWarningBean trapWarningBean);

	/**
	 * 清除一个月前的性能检测报表信息
	 */
	public void deleteRmonCount();

	/**
	 * 根据简单告警信息查询完整的告警信息
	 * 
	 * @param sw
	 * @return
	 */
	public TrapWarningEntity findTrapEntity(SimpleWarning sw);

	/**
	 * 根据简单告警信息查询用来查询的告警信息
	 * 
	 * @param sw
	 * @return
	 */
	public TrapSimpleWarning findTrapSimpleWarning(SimpleWarning sw);

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
	 * 告警信息查询
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
	 * 告警信息总数
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
	 * 日志查询
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
	 * 日志总数
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
	 * 查询告警信息提醒记录
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
	 * 查询告警信息提醒记录总数
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
     * 定时监控记录条数
     * @param startDate
     * @param endDate
     * @param ipValue
     * @param portNo
     * @return
     */
	public long queryAllMonitoringSheetNum(Date startDate,Date endDate,String ipValue,int portNo);
	/**
	 * 查询定时监控报表
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
	 * 性能监控报表查询
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
	 * 查询性能监测数据
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
	 * 性能监测数据总数
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
	 * 通过Ip查询personInfo
	 * 
	 * @param ip_value
	 * @return
	 */
	public List<PersonBean> findPersonInfo(String ip_value);

	/**
	 * 查找一個區域的所有管理员和该区域的上级区域的管理员
	 * 
	 * @param area
	 * @return
	 */
	public List<UserEntity> findUsersByArea(AreaEntity area);

	public List<UserEntity> findUserInfo(String ip_value);

	/**
	 * 清空日志记录
	 */
	public void clearLogEntity();

	/**
	 * 清除告警提醒信息
	 */
	public void clearWarningRecode();

	/**
	 * 查询交换机Vlan
	 * 
	 * @return
	 */
	public List<VlanBean> querySwitchVlanInfo();

	/**
	 * 通过Vlan查询其下面端口和交换机信息
	 * 
	 * @param vlanBean
	 * @return
	 */
	public List<SwitchVlanPortInfo> querySwitchVlanPortInfo(VlanBean vlanBean);

	/**
	 * 删除一个月以前的告警信息
	 */
	public void deleteTrapWarningInfo();

	/**
	 * Vlan管理
	 * 
	 * @param vlanBean
	 * @param nowList
	 */
	public boolean saveVlan(VlanBean vlanBean, List<VlanPortConfig> nowList,
			String clientIP, int type,String userName);

	/**
	 * 通过ringBean查询
	 * 
	 * @param ringBean
	 * @return
	 */
	public List<RingInfo> queryRingInfo(RingBean ringBean);

	/**
	 * 查询所以Ring下的所有端口
	 * 
	 * @return
	 */
	public List<RingInfo> queryAllRingInfo();

	/**
	 * 查询环方法
	 * 
	 * @return
	 */
	public List<RingBean> queryRingConfig();

	/**
	 * 保存环方法
	 * 
	 * @param list
	 * @param clientIP
	 * @param ringBean
	 */
	public boolean saveRing(List<RingInfo> list, String clientIP,
			RingBean ringBean, int type,String userName);

	/**
	 * 通过HOSTIP查询SNMPHost
	 * 
	 * @param hostIp
	 * @return
	 */
	public List<SNMPHost> querySNMPHost(String hostIp,String snmpVersion,String massName);

	/**
	 * 保存SNMPHOST
	 * 
	 * @param snmpHost
	 * @param clientIP
	 * @param from
	 */
	public boolean saveSNMPHost(List<SNMPHost> snmpHost, String clientIP,
			SNMPHostBean snmpHostBean, int type,String userName);

	/**
	 * SNMPHost查询
	 * 
	 * @return
	 */
	public List<SNMPHostBean> queryAllSNMPHostBean();

	/**
	 * 通过SNMPHostBean 查询其下所有的交换机
	 * 
	 * @param snmpHostBean
	 * @return
	 */
	public List<SNMPSwitchIPBean> querySwitchIPBeans(SNMPHostBean snmpHostBean);

	/**
	 * SNMP视图
	 */
	public List<SNMPViewBean> querySNMPView();

	public List<SNMPSwitchIPBean> querySNMPSwitchIPBean(
			SNMPViewBean snmpViewBean);

	/**
	 * SNMP用户
	 */
	public List<SNMPUserBean> querySNMPUserBean();

	public List<SNMPSwitchIPBean> querySNMPSwitchIPBean(
			SNMPUserBean snmpUserBean);

	/**
	 * SNMP团体
	 */
	public List<SNMPMassBean> querySNMPMassBean();

	public List<SNMPSwitchIPBean> querySNMPSwitchIPBean(
			SNMPMassBean snmpMassBean);

	/**
	 * SNMP群组
	 */
	public List<SNMPGroupBean> querySNMPGroupBean();

	public List<SNMPSwitchIPBean> querySNMPSwitchIPBean(
			SNMPGroupBean snmpGroupBean);

	/**
	 * 通过前置机获取该前置机管理下的设备，包括二层交换机、三层交换机、olt、onu
	 * 
	 * @param clazz
	 * @param seg
	 * @return
	 */
	public List findDeviceByFep(Class clazz, String code);
	/**
	 * 查询拓扑图Byid
	 * @param id
	 * @return
	 */
	public TopDiagramEntity findTopDiagramEntity(Long id);
	/**
	 * 查询图
	 * @return
	 */
	public TopDiagramEntity findAllTopDiagramEntity();
	
	/**
	 * 根据UserEntity对设备做权限管理
	 * @param userEntity
	 * @return
	 */
	public TopDiagramEntity findAllTopDiagramEntity(UserEntity userEntity);
	
	/**
	 * 查询所有电脑上可通讯的端口
	 * @return
	 */
	public List<String> queryAllSerialPort();
	/**
	 * 删除图上节点
	 * @param node
	 * @param topDID
	 */
	public void deleteNode(Object node,long topDID);
	
	/**
	 * 通过节点查找和其相关的线
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
	 * 返回子网对象包括其下节点和线
	 * @param diagramID
	 * @param nodeEntity
	 * @return
	 */
	public SubNetTopoNodeEntity getSubNetTopoNodeEntity(long diagramID,String parentNode,UserEntity userEntity);
	
	/**
	 * 查询一段时间不同级别的告警数量
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<TrapCountBean> queryTrapWarningLevel(Date startDate,Date endDate);
	/**
	 * 查询一段时间不同类别的告警数量
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<TrapCountBean> queryTrapWarningCategory(Date startDate,Date endDate);
	
	/**
	 * 查询月份的告警数量
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<TrapCountBean> queryTrapWarningMonth(Date startDate,Date endDate);
	
	
	/**
	 * 查询前5个月的告警数量
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public List<TrapCountBean> queryTrapWarningByMonth5(Date date);
	
	
	/**
	 * 告警数量最多的 前10个设备
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	 public List<TrapCountBean> queryTrapWarningTop10(Date startDate,Date endDate);
	 
	
	public List findDevice(Class clazz);
	
	/***
	 * 删除线
	 * @param linkEntity
	 */
	public void deleteLinkEntity(LinkEntity linkEntity);
	
	/**
	 * 查询设备
	 * @param ip
	 * @param nodeEntity
	 * @return
	 */
	public List<Object[]> queryDevice(String params,String name,Class<?> clazz,UserEntity userEntity);
	
	
	/**
	 * 因为UserEntity中的nodeEntity是延迟加载，所以提供方法通过userID直接查询数据库
	 * @param userID
	 * @return
	 */
	public List<NodeEntity> queryNodeEntityByUser(long userID);
	
	/**
	 * 通过nodeID直接查询数据库
	 * @param nodeID
	 * @return
	 */
	public List<UserEntity> queryUserEntityByNode(long nodeID);
	
	/**
	 * 告警消息确认操作,确认后归并到告警历史中
	 * @param warningEntities
	 */
	public void confirmWarningInfo(List<WarningEntity> warningEntities);
	
	/**
	 * 通过输入的历史告警信息得到所有的WarningEntity
	 * @param history
	 * @return
	 */
	public List<WarningEntity> queryWarningFromHistory(WarningHistoryEntity history);
	
	/**
	 * 通过WarningHistoryBean中的查询条件,
	 * 得到相应的告警历史信息的总数
	 * @param warningHistoryBean
	 * @return
	 */
	public long getAllWarningHistoryCount(WarningHistoryBean warningHistoryBean);
	
	/**
	 * 通过WarningHistoryBean中的查询条件,
	 * 得到相应的告警历史信息列表
	 * @param warningHistoryBean
	 * @return
	 */
	public List<WarningHistoryEntity> queryWarningHistory(WarningHistoryBean warningHistoryBean);
	
	/**
	 * 通过IP列表得到SwitchTopoNodeEntity
	 * @param ips
	 * @return
	 */
	public List<SwitchTopoNodeEntity> getSwitchTopoNodeByIps(List<String> ips);

	/**
	 * 通过sysLogHostEntity查询所有关联的SysLogHostToDevEntity
	 * @param sysLogHostEntity
	 * @return
	 */
	public List<SysLogHostToDevEntity> getSysLogHostToDevBySysLog(SysLogHostEntity sysLogHostEntity);
	
	/**
	 * 配置syslog主机信息
	 * @param devEntityList  syslogToDev实体信息
	 * @param from           客户端用户名
	 * @param clientIp       客户端IP
	 * @param operateType    网管侧or设备侧
	 */
	public void configSyslogHost(List<SysLogHostToDevEntity> devEntityList,String from,String clientIp,int operateType);
	
	/**
	 * 只保存到数据库中
	 * @param devEntityList
	 * @param hostEntity
	 */
	public void saveSysLogHosts(List<SysLogHostToDevEntity> devEntityList,SysLogHostEntity hostEntity);
	
	/**
	 * 删除SysLogToDevEntity
	 * @param devEntityList
	 * @param from
	 * @param clientIp
	 * @param operateType
	 */
	public void deleteSysLogToDevEntity(List<SysLogHostToDevEntity> devEntityList
			,String from,String clientIp,int operateType);
	
	/**
	 * 修改用户密码
	 * @param userEntity
	 */
	public void updateUserPwd(UserEntity userEntity);
}
