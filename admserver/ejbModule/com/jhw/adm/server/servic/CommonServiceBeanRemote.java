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
	 * ͨ��id��ѯĳһ�ض�������
	 * 
	 * @param <T>
	 * @param id
	 * @param clazz
	 * @return
	 */
	public <T> T findById(Serializable id, Class<T> clazz);


	/**
	 * ��SwitchNodeEntityΪ��������ѯĳһ�ض��Ľ�����������
	 * 
	 * @param sne
	 * @param clazz
	 * @return
	 */
	public List findAllBySwitch(SwitchNodeEntity sne, Class clazz);

	/**
	 * ��ѯĳһ�ض�ʵ�����������
	 * 
	 * @param clazz
	 * @return
	 */
	public List<?> findAll(Class<?> clazz);

	/**
	 * ������ѯĳһ�ض���ʵ��������
	 * 
	 * @param clazz
	 * @param where
	 *            :���磺"where xx.id=?"
	 * @return
	 */
	public List<?> findAll(Class<?> clazz, String where);

	/**
	 * ͨ��jpql����ѯ
	 * 
	 * @param jpql
	 * @return
	 */
	public List findAll(String jpql);

	/**
	 * ͨ����������������ѯ�ض�����ʵ������
	 * 
	 * @param clazz
	 * @param where
	 * @param parms
	 * @return
	 */

	public List<?> findAll(Class<?> clazz, String where, Object[] parms);

	/**
	 * һ�α�����ʵ�����,���ر����Ķ��󣬸�����ǰ�Ĳ���Ƿ��صĶ�������id
	 */
	public Object saveEntities(List objs);

	/**
	 * �������еĶ���,���ر����Ķ��󣬸�����ǰ�Ĳ���Ƿ��صĶ�������id
	 */
	public Object saveEntity(Object obj);

	// ===================================================================================================================================================
	/**
	 * ͨ��ע���ȡ��ǰ�����Ӧ�ı���
	 * 
	 * @param clazz
	 * @return
	 */
	public String getTableNameByClass(Class clazz);

	/**
	 * ����ɾ��ĳһ����
	 * 
	 * @param entities
	 */
	public void deleteEntities(List<?> entities);

	/**
	 * ����ɾ��ĳһ�ض������ݶ���
	 * 
	 * @param entities
	 */
	public void deleteEntities(Set entities);

	/**
	 * ɾ��ĳһ�ض�������
	 * 
	 * @param entity
	 */
	public void deleteEntity(Object entity);

	/**
	 * ͨ��idɾ��ĳһ�ض�������
	 * 
	 * @param clazz
	 * @param id
	 */
	public void deleteEntity(Class clazz, Long id);

	/**
	 * ����ĳһ�ض��Ķ���
	 * 
	 * @param entity
	 * @return
	 */
	public Object updateEntity(Object entity);

	/**
	 * ��������ʵ��
	 * @param entitys
	 */
	public void updateEntities(List entitys);
	/**
	 * ִ�ж��Ƶ�jpql���
	 * 
	 * @param jpql
	 */
	public void execJPQL(String jpql);

	/**
	 * ɾ��һ������������ݶ���
	 * 
	 * @param clazz
	 */
	public void deleteEntitys(Class clazz);

	/**
	 * ����һ��ʵ������״̬�����ڲ������õ�ʱ�򣬵�����һ��������ʱ�򣬱������ݿ���ٷ��͸�ǰ�û������豸���ã�
	 * �ڱ����豸֮ǰͬ��״̬��false�����յ�ǰ�û����ص����óɹ���Ϣ�󣬻��޸ĸ�״̬����ʾ�ɹ������û���޸ģ�
	 * �������ݿ�ɹ����豸����ʧ�ܡ�
	 * @param id
	 * @param className
	 * @param res
	 */
	public void updateEntity(Long id, String className, boolean res);

	/**
	 * ͨ���ز�����Ż�ȡ�ز���ʵ��
	 * @param code
	 * @return
	 */
	public CarrierEntity getCarrierByCode(int code);

	/**
	 * ������״̬
	 * @param status
	 * @param id
	 */
	public void updateLink(int status,Long id);
	
	/**
	 * ���½ڵ�״̬
	 * @param status
	 * @param id
	 */
	public void updateNode(int status,Long id);
	
	/**
	 * ͨ��ip�Ͷ˿ڻ�ȡ���ߣ���ip�Ͷ˿ڿ����������ϵı���ip�Ͷ˿ڻ���Զ��ip�Ͷ˿�
	 * @param ipvalue
	 * @param port
	 * @return
	 */
	public Long findLinkEntityByIPPort(String ipvalue, int port);

	/**
	 * ��ȡonu������
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
	 * ��ȡ�����澯��
	 * 
	 * @param ipValue
	 * @param slot
	 * @param port
	 * @param forrwordIp
	 * @return
	 */
	public Long findOLTLink(String ipValue, int slot, int port, int portType);

	/**
	 * ͨ��mac��ַ��ȡonuʵ��
	 * @param macValue
	 * @return
	 */
	public ONUEntity getOnuByMacValue(String macValue);

	/**
	 * ͨ��ip��ȡoltʵ��
	 * @param ipValue
	 * @return
	 */
	public OLTEntity getOLTByIpValue(String ipValue);

	/**
	 * ͨ��ip��ȡ������ʵ��
	 * @param ipvalue
	 * @return
	 */
	public SwitchNodeEntity getSwitchByIp(String ipvalue);

	/**
	 * ͨ��ip��ȡ������������ͼ�νڵ�
	 * 
	 * @param ipvalue
	 * @return
	 */
	public NodeEntity findSwitchTopoNodeByIp(String ipvalue);
	
	/**
	 * ͨ��ip��ȡ������Ԫ������ͼ�νڵ�
	 * 
	 * @param ipvalue
	 * @return
	 */
	public NodeEntity findVirtualNodeByIp(String ipvalue);

	/**
	 * ͨ����Ż�ȡ�ز���������ͼ�νڵ�
	 * 
	 * @param code
	 * @return
	 */
	public NodeEntity findCarrierTopoNodeByCode(String code);

	/**
	 * ͨ��ip��ȡolt��ͼ�νڵ�
	 * 
	 * @param ipvalue
	 * @return
	 */
	public NodeEntity findOLTTopoNodeByIp(String ipvalue);

	/**
	 * ͨ��mac��ȡonu������ͼ�νڵ�
	 */
	public NodeEntity findOnuTopoNodeByMac(String macValue);
	
	/**
	 * ͨ��ipValue��ȡ���㽻����������ͼ�νڵ�
	 */
	public NodeEntity findSwitchTopoNodeLevel3ByIp(String ipValue);
	
	/**
	 * �����������ɵ�ͼ��
	 * @param entity
	 * @return
	 */
	public TopDiagramEntity saveDiagram(TopDiagramEntity entity);
	/**
	 * ͨ��userId��ѯGPRS
	 * @param userId
	 * @return
	 */
	public GPRSEntity getGPRSEntityByUserId(String userId);
	/**
	 * ͨ��ǰ�û�IP��ѯǰ�û�
	 * @param ipValue
	 * @return
	 */
	public FEPEntity getFEPEntityByIP(String ipValue);
	
	/**
	 * ͨ��macֵ��ȡSwitchNodeEntity����
	 * 
	 * @param macvalue
	 * @return
	 */
	public SwitchNodeEntity getSwitchNodeByMac(String macvalue);

	/**
	 * ͨ��ip��ȡSwitchNodeEntity����
	 * 
	 * @param ipValue
	 * @return
	 */
	public SwitchNodeEntity getSwitchNodeByIp(String ipValue);
	
	/**
	 * ͨ��mac�ҵ�OLTONUEntity
	 * 
	 * @param mac
	 * @return
	 */
	public ONUEntity getOLTONUEntity(String mac);
	
	/**
	 * ͨ��ǰ�û�Code��ѯFep
	 * 
	 * @param code
	 * @return
	 */
	public FEPEntity findFepEntity(String code);
	
	/**
	 * ͨ��IP�������㽻����
	 * @param ipvalue
	 * @return
	 */
	public SwitchLayer3 getSwitcher3ByIP(String ipvalue);
	/**
	 * ��ѯ3�㽻������γ��
	 * @param ipValue
	 * @return
	 */
	public AddressEntity  queryAddressBySwitcher3(String ipValue);
	/**
	 * ��ѯ�ز�����γ��
	 * @param ipValue
	 * @return
	 */
	public AddressEntity  queryAddressByCarrier(int carrierCode);
	/**
	 * ��ѯGPRS��γ��
	 * @param ipValue
	 * @return
	 */
	public AddressEntity queryAddressByGPRS(String userId);
	/**
	 * ��ѯ2�㽻������γ��
	 * @param ipValue
	 * @return
	 */
	public AddressEntity queryAddressBySwitcher2(String ipValue);
	/**
	 * ��ѯOLT��γ��
	 * @param ipValue
	 * @return
	 */
	public AddressEntity queryAddressByOLT(String ipValue);
	/**
	 * ��ѯONU��γ��
	 * @param ipValue
	 * @return
	 */
	public AddressEntity queryAddressByONU(String macAddress);
	
	public boolean deleteArea(List<AreaEntity> list);
	/**
	 * ��ѯ�����ز���
	 * @return
	 */
	public List<CarrierEntity> queryCarrierEntity();
}
