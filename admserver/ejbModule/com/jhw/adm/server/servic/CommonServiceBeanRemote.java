package com.jhw.adm.server.servic;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.ejb.Remote;
import javax.jms.JMSException;

import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.epon.ONUEntity;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.system.AreaEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.TopDiagramEntity;
import com.jhw.adm.server.entity.util.AddressEntity;
import com.jhw.adm.server.entity.util.GPRSEntity;

@Remote
public interface CommonServiceBeanRemote {


	/**
	 * 通过id查询某一特定的数据
	 * 
	 * @param <T>
	 * @param id
	 * @param clazz
	 * @return
	 */
	public <T> T findById(Serializable id, Class<T> clazz);


	/**
	 * 以SwitchNodeEntity为条件，查询某一特定的交换机参数类
	 * 
	 * @param sne
	 * @param clazz
	 * @return
	 */
	public List findAllBySwitch(SwitchNodeEntity sne, Class clazz);

	/**
	 * 查询某一特定实体的所有数据
	 * 
	 * @param clazz
	 * @return
	 */
	public List<?> findAll(Class<?> clazz);

	/**
	 * 条件查询某一特定的实体类数据
	 * 
	 * @param clazz
	 * @param where
	 *            :例如："where xx.id=?"
	 * @return
	 */
	public List<?> findAll(Class<?> clazz, String where);

	/**
	 * 通过jpql语句查询
	 * 
	 * @param jpql
	 * @return
	 */
	public List findAll(String jpql);

	/**
	 * 通过条件语句和条件查询特定的类实体数据
	 * 
	 * @param clazz
	 * @param where
	 * @param parms
	 * @return
	 */

	public List<?> findAll(Class<?> clazz, String where, Object[] parms);

	/**
	 * 一次保存多个实体对象,返回保存后的对象，跟保存前的差别是返回的对象有了id
	 */
	public Object saveEntities(List objs);

	/**
	 * 保存所有的对象,返回保存后的对象，跟保存前的差别是返回的对象有了id
	 */
	public Object saveEntity(Object obj);

	// ===================================================================================================================================================
	/**
	 * 通过注解获取当前对象对应的表名
	 * 
	 * @param clazz
	 * @return
	 */
	public String getTableNameByClass(Class clazz);

	/**
	 * 批量删除某一对象
	 * 
	 * @param entities
	 */
	public void deleteEntities(List<?> entities);

	/**
	 * 批量删除某一特定的数据对象
	 * 
	 * @param entities
	 */
	public void deleteEntities(Set entities);

	/**
	 * 删除某一特定的数据
	 * 
	 * @param entity
	 */
	public void deleteEntity(Object entity);

	/**
	 * 通过id删除某一特定的数据
	 * 
	 * @param clazz
	 * @param id
	 */
	public void deleteEntity(Class clazz, Long id);

	/**
	 * 更新某一特定的对象
	 * 
	 * @param entity
	 * @return
	 */
	public Object updateEntity(Object entity);

	/**
	 * 批量更新实体
	 * @param entitys
	 */
	public void updateEntities(List entitys);
	/**
	 * 执行定制的jpql语句
	 * 
	 * @param jpql
	 */
	public void execJPQL(String jpql);

	/**
	 * 删除一个类的所有数据对象
	 * 
	 * @param clazz
	 */
	public void deleteEntitys(Class clazz);

	/**
	 * 更新一个实体对象的状态，用于参数设置的时候，当设置一个参数的时候，保存数据库后再发送给前置机进行设备配置，
	 * 在保存设备之前同步状态是false，当收到前置机返回的设置成功消息后，会修改该状态，表示成功。如果没有修改，
	 * 则是数据库成功，设备配置失败。
	 * @param id
	 * @param className
	 * @param res
	 */
	public void updateEntity(Long id, String className, boolean res);

	/**
	 * 通过载波机编号获取载波机实体
	 * @param code
	 * @return
	 */
	public CarrierEntity getCarrierByCode(int code);

	/**
	 * 更新线状态
	 * @param status
	 * @param id
	 */
	public void updateLink(int status,Long id);
	
	/**
	 * 更新节点状态
	 * @param status
	 * @param id
	 */
	public void updateNode(int status,Long id);
	
	/**
	 * 通过ip和端口获取连线，该ip和端口可能是连线上的本地ip和端口或者远端ip和端口
	 * @param ipvalue
	 * @param port
	 * @return
	 */
	public Long findLinkEntityByIPPort(String ipvalue, int port);

	/**
	 * 获取onu的连线
	 * 
	 * @param ipValue
	 * @param slot
	 * @param port
	 * @param macValue
	 * @return
	 */
	public Long findOnuLinkEntityByEponInfo(String ipValue, int slot,
			int port, String macValue);

	/**
	 * 获取上联告警线
	 * 
	 * @param ipValue
	 * @param slot
	 * @param port
	 * @param forrwordIp
	 * @return
	 */
	public Long findOLTLink(String ipValue, int slot, int port, int portType);

	/**
	 * 通过mac地址获取onu实体
	 * @param macValue
	 * @return
	 */
	public ONUEntity getOnuByMacValue(String macValue);

	/**
	 * 通过ip获取olt实体
	 * @param ipValue
	 * @return
	 */
	public OLTEntity getOLTByIpValue(String ipValue);

	/**
	 * 通过ip获取交换机实体
	 * @param ipvalue
	 * @return
	 */
	public SwitchNodeEntity getSwitchByIp(String ipvalue);

	/**
	 * 通过ip获取交换机的拓扑图形节点
	 * 
	 * @param ipvalue
	 * @return
	 */
	public NodeEntity findSwitchTopoNodeByIp(String ipvalue);
	
	/**
	 * 通过ip获取虚拟网元的拓扑图形节点
	 * 
	 * @param ipvalue
	 * @return
	 */
	public NodeEntity findVirtualNodeByIp(String ipvalue);

	/**
	 * 通过编号获取载波机的拓扑图形节点
	 * 
	 * @param code
	 * @return
	 */
	public NodeEntity findCarrierTopoNodeByCode(String code);

	/**
	 * 通过ip获取olt的图形节点
	 * 
	 * @param ipvalue
	 * @return
	 */
	public NodeEntity findOLTTopoNodeByIp(String ipvalue);

	/**
	 * 通过mac获取onu的拓扑图形节点
	 */
	public NodeEntity findOnuTopoNodeByMac(String macValue);
	
	/**
	 * 通过ipValue获取三层交换机的拓扑图形节点
	 */
	public NodeEntity findSwitchTopoNodeLevel3ByIp(String ipValue);
	
	/**
	 * 保存拓扑生成的图形
	 * @param entity
	 * @return
	 */
	public TopDiagramEntity saveDiagram(TopDiagramEntity entity);
	/**
	 * 通过userId查询GPRS
	 * @param userId
	 * @return
	 */
	public GPRSEntity getGPRSEntityByUserId(String userId);
	/**
	 * 通过前置机IP查询前置机
	 * @param ipValue
	 * @return
	 */
	public FEPEntity getFEPEntityByIP(String ipValue);
	
	/**
	 * 通过mac值获取SwitchNodeEntity对象
	 * 
	 * @param macvalue
	 * @return
	 */
	public SwitchNodeEntity getSwitchNodeByMac(String macvalue);

	/**
	 * 通过ip获取SwitchNodeEntity对象
	 * 
	 * @param ipValue
	 * @return
	 */
	public SwitchNodeEntity getSwitchNodeByIp(String ipValue);
	
	/**
	 * 通过mac找到OLTONUEntity
	 * 
	 * @param mac
	 * @return
	 */
	public ONUEntity getOLTONUEntity(String mac);
	
	/**
	 * 通过前置机Code查询Fep
	 * 
	 * @param code
	 * @return
	 */
	public FEPEntity findFepEntity(String code);
	
	/**
	 * 通过IP查找三层交换机
	 * @param ipvalue
	 * @return
	 */
	public SwitchLayer3 getSwitcher3ByIP(String ipvalue);
	/**
	 * 查询3层交换机经纬度
	 * @param ipValue
	 * @return
	 */
	public AddressEntity  queryAddressBySwitcher3(String ipValue);
	/**
	 * 查询载波机经纬度
	 * @param ipValue
	 * @return
	 */
	public AddressEntity  queryAddressByCarrier(int carrierCode);
	/**
	 * 查询GPRS经纬度
	 * @param ipValue
	 * @return
	 */
	public AddressEntity queryAddressByGPRS(String userId);
	/**
	 * 查询2层交换机经纬度
	 * @param ipValue
	 * @return
	 */
	public AddressEntity queryAddressBySwitcher2(String ipValue);
	/**
	 * 查询OLT经纬度
	 * @param ipValue
	 * @return
	 */
	public AddressEntity queryAddressByOLT(String ipValue);
	/**
	 * 查询ONU经纬度
	 * @param ipValue
	 * @return
	 */
	public AddressEntity queryAddressByONU(String macAddress);
	
	public boolean deleteArea(List<AreaEntity> list);
	/**
	 * 查询中心载波机
	 * @return
	 */
	public List<CarrierEntity> queryCarrierEntity();
}
