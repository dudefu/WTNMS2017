package com.jhw.adm.server.servic;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.comm.CommPortIdentifier;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TemporalType;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.LocalBinding;
import org.jboss.ejb3.annotation.RemoteBinding;

import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.carriers.CarrierRouteEntity;
import com.jhw.adm.server.entity.epon.OLTBaseInfo;
import com.jhw.adm.server.entity.epon.OLTChipInfo;
import com.jhw.adm.server.entity.epon.OLTDBA;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.epon.OLTMulticast;
import com.jhw.adm.server.entity.epon.OLTPort;
import com.jhw.adm.server.entity.epon.OLTPortStp;
import com.jhw.adm.server.entity.epon.OLTSTP;
import com.jhw.adm.server.entity.epon.OLTSlotConfig;
import com.jhw.adm.server.entity.epon.OLTVlan;
import com.jhw.adm.server.entity.epon.ONUEntity;
import com.jhw.adm.server.entity.epon.ONULLID;
import com.jhw.adm.server.entity.epon.ONUVlan;
import com.jhw.adm.server.entity.level3.Switch3VlanEnity;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.level3.SwitchPortLevel3;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.nets.IPSegment;
import com.jhw.adm.server.entity.nets.VlanConfig;
import com.jhw.adm.server.entity.nets.VlanEntity;
import com.jhw.adm.server.entity.nets.VlanPortConfig;
import com.jhw.adm.server.entity.ports.LACPConfig;
import com.jhw.adm.server.entity.ports.LACPCountInfo;
import com.jhw.adm.server.entity.ports.LACPInfoEntity;
import com.jhw.adm.server.entity.ports.LLDPConfig;
import com.jhw.adm.server.entity.ports.LLDPCountInfo;
import com.jhw.adm.server.entity.ports.LLDPInofEntity;
import com.jhw.adm.server.entity.ports.PC8021x;
import com.jhw.adm.server.entity.ports.QOSPriority;
import com.jhw.adm.server.entity.ports.QOSSpeedConfig;
import com.jhw.adm.server.entity.ports.QOSStormControl;
import com.jhw.adm.server.entity.ports.QOSSysConfig;
import com.jhw.adm.server.entity.ports.RingConfig;
import com.jhw.adm.server.entity.ports.RingCount;
import com.jhw.adm.server.entity.ports.RingLinkBak;
import com.jhw.adm.server.entity.ports.RingPortInfo;
import com.jhw.adm.server.entity.ports.SC8021x;
import com.jhw.adm.server.entity.ports.SwitchPortEntity;
import com.jhw.adm.server.entity.switchs.IGMPEntity;
import com.jhw.adm.server.entity.switchs.Igmp_vsi;
import com.jhw.adm.server.entity.switchs.LinkBakConfig;
import com.jhw.adm.server.entity.switchs.MirrorEntity;
import com.jhw.adm.server.entity.switchs.SNMPGroup;
import com.jhw.adm.server.entity.switchs.SNMPHost;
import com.jhw.adm.server.entity.switchs.SNMPMass;
import com.jhw.adm.server.entity.switchs.SNMPUser;
import com.jhw.adm.server.entity.switchs.SNMPView;
import com.jhw.adm.server.entity.switchs.SNTPConfigEntity;
import com.jhw.adm.server.entity.switchs.STPCount;
import com.jhw.adm.server.entity.switchs.STPPortConfig;
import com.jhw.adm.server.entity.switchs.STPPortStatus;
import com.jhw.adm.server.entity.switchs.STPSysConfig;
import com.jhw.adm.server.entity.switchs.SwitchBaseConfig;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.switchs.SwitchRingInfo;
import com.jhw.adm.server.entity.switchs.SwitchUser;
import com.jhw.adm.server.entity.switchs.SysLogHostEntity;
import com.jhw.adm.server.entity.switchs.SysLogHostToDevEntity;
import com.jhw.adm.server.entity.switchs.TrunkConfig;
import com.jhw.adm.server.entity.system.AreaEntity;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.tuopos.CarrierTopNodeEntity;
import com.jhw.adm.server.entity.tuopos.CommentTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.tuopos.Epon_S_TopNodeEntity;
import com.jhw.adm.server.entity.tuopos.FEPTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.GPRSTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.ONUTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.RingEntity;
import com.jhw.adm.server.entity.tuopos.SubNetTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.tuopos.TopDiagramEntity;
import com.jhw.adm.server.entity.tuopos.VirtualNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.GPRSEntity;
import com.jhw.adm.server.entity.util.LogEntity;
import com.jhw.adm.server.entity.util.MACMutiCast;
import com.jhw.adm.server.entity.util.MACUniCast;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.PersonBean;
import com.jhw.adm.server.entity.util.RingBean;
import com.jhw.adm.server.entity.util.RingInfo;
import com.jhw.adm.server.entity.util.SNMPGroupBean;
import com.jhw.adm.server.entity.util.SNMPHostBean;
import com.jhw.adm.server.entity.util.SNMPMassBean;
import com.jhw.adm.server.entity.util.SNMPSwitchIPBean;
import com.jhw.adm.server.entity.util.SNMPUserBean;
import com.jhw.adm.server.entity.util.SNMPViewBean;
import com.jhw.adm.server.entity.util.SwitchSerialPort;
import com.jhw.adm.server.entity.util.SwitchVlanPortInfo;
import com.jhw.adm.server.entity.util.TrapCountBean;
import com.jhw.adm.server.entity.util.TrapWarningBean;
import com.jhw.adm.server.entity.util.VlanBean;
import com.jhw.adm.server.entity.util.WarningEventDescription;
import com.jhw.adm.server.entity.util.WarningHistoryBean;
import com.jhw.adm.server.entity.warning.FaultDetection;
import com.jhw.adm.server.entity.warning.FaultDetectionRecord;
import com.jhw.adm.server.entity.warning.RmonCount;
import com.jhw.adm.server.entity.warning.RmonWarningConfig;
import com.jhw.adm.server.entity.warning.SimpleWarning;
import com.jhw.adm.server.entity.warning.TimerMonitoring;
import com.jhw.adm.server.entity.warning.TimerMonitoringSheet;
import com.jhw.adm.server.entity.warning.TrapSimpleWarning;
import com.jhw.adm.server.entity.warning.TrapWarningEntity;
import com.jhw.adm.server.entity.warning.WarningEntity;
import com.jhw.adm.server.entity.warning.WarningHistoryEntity;
import com.jhw.adm.server.entity.warning.WarningRecord;
import com.jhw.adm.server.interfaces.CommonServiceBeanLocal;
import com.jhw.adm.server.interfaces.DataCacheLocal;
import com.jhw.adm.server.interfaces.NMSServiceLocal;
import com.jhw.adm.server.interfaces.SMTCServiceLocal;
import com.jhw.adm.server.interfaces.SMTFEPServiceLocal;
import com.jhw.adm.server.util.CacheDatas;
import com.jhw.adm.server.util.Tools;

/**
 * Session Bean implementation class NMSService
 */
@Stateless(mappedName = "NmsServiceBean")
@RemoteBinding(jndiBinding = "remote/NMSServiceRemote")
@LocalBinding(jndiBinding = "local/NMSServiceLocal")
public class NMSService implements NMSServiceRemote, NMSServiceLocal {

	private Logger logger = Logger.getLogger(NMSService.class.getName());

	@PersistenceContext(unitName = "adm2server")
	private EntityManager manager;
	@EJB
	private CommonServiceBeanLocal commonService;
	@EJB
	private SMTFEPServiceLocal sendMessageService;
	@Resource
	SessionContext ctx;
	@EJB
	private DataCacheLocal datacache;
	
	@EJB
	private SMTCServiceLocal smtcservice;

	public void deleteSwitch(SwitchTopoNodeEntity topentity) {
		
		SwitchNodeEntity switchNode = commonService.getSwitchByIp(topentity.getIpValue());
		commonService.deleteEntity(topentity);
		deleteEntityBySwitch(LACPConfig.class, switchNode);
		deleteEntityBySwitch(SwitchUser.class, switchNode);
		deleteEntityBySwitch(SwitchRingInfo.class, switchNode);
		deleteEntityBySwitch(MACUniCast.class, switchNode);
		deleteEntityBySwitch(MACMutiCast.class, switchNode);
		deleteEntityBySwitch(LACPCountInfo.class, switchNode);
		deleteEntityBySwitch(LACPInfoEntity.class, switchNode);
		deleteEntityBySwitch(LLDPConfig.class, switchNode);
		deleteEntityBySwitch(LLDPCountInfo.class, switchNode);
		deleteEntityBySwitch(SC8021x.class, switchNode);
		deleteEntityBySwitch(QOSPriority.class, switchNode);
		deleteEntityBySwitch(QOSSpeedConfig.class, switchNode);
		deleteEntityBySwitch(QOSStormControl.class, switchNode);
		deleteEntityBySwitch(QOSSysConfig.class, switchNode);
		deleteEntityBySwitch(RingConfig.class, switchNode);
		deleteEntityBySwitch(RingCount.class, switchNode);
		deleteEntityBySwitch(RingLinkBak.class, switchNode);
		deleteEntityBySwitch(RingPortInfo.class, switchNode);
		deleteEntityBySwitch(PC8021x.class, switchNode);
		deleteEntityBySwitch(IGMPEntity.class, switchNode);
		deleteEntityBySwitch(LinkBakConfig.class, switchNode);
		deleteEntityBySwitch(MirrorEntity.class, switchNode);
		deleteEntityBySwitch(SNMPGroup.class, switchNode);
		deleteEntityBySwitch(SNMPHost.class, switchNode);
		deleteEntityBySwitch(SNMPMass.class, switchNode);
		deleteEntityBySwitch(SNMPUser.class, switchNode);
		deleteEntityBySwitch(SNMPView.class, switchNode);
		deleteEntityBySwitch(SNTPConfigEntity.class, switchNode);
		deleteEntityBySwitch(STPCount.class, switchNode);
		deleteEntityBySwitch(STPPortConfig.class, switchNode);
		deleteEntityBySwitch(STPPortStatus.class, switchNode);
		deleteEntityBySwitch(STPSysConfig.class, switchNode);
		deleteEntityBySwitch(TrunkConfig.class, switchNode);
		// deleteEntityBySwitch(SwitchPortEntity.class, switchNode);
		deleteEntityBySwitch(VlanConfig.class, switchNode);
		deleteEntityBySwitch(RmonWarningConfig.class, switchNode);
		// deleteEntityBySwitch(SwitchSerialPort.class, switchNode);
		commonService.deleteEntity(switchNode);
	}
	
	

	@SuppressWarnings("unchecked")
	private void deleteAllLink(NodeEntity nodeEntity) {
		try {
			if (nodeEntity != null) {
				if (nodeEntity.getId() != null) {
					nodeEntity = manager.find(NodeEntity.class, nodeEntity
							.getId());
					if (nodeEntity != null) {
						Object[] parm = { nodeEntity };
						List<LinkEntity> listnode1 = (List<LinkEntity>) commonService
								.findAll(LinkEntity.class,
										" where entity.node1=?", parm);
						List<LinkEntity> listnode2 = (List<LinkEntity>) commonService
								.findAll(LinkEntity.class,
										" where entity.node2=?", parm);
						List<LinkEntity> allLink = new ArrayList<LinkEntity>();
						if (listnode1 != null && listnode1.size() > 0) {
							allLink.addAll(listnode1);
						}
						if (listnode2 != null && listnode2.size() > 0) {
							allLink.addAll(listnode2);
						}
						if (allLink != null && allLink.size() > 0) {
							for (LinkEntity linkEntity : allLink) {
								linkEntity = manager.find(LinkEntity.class,
										linkEntity.getId());
								manager.remove(linkEntity);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void deleteLinkEntity(LinkEntity linkEntity) {
		commonService.deleteEntity(linkEntity);
	}

	public void deleteAllNode(NodeEntity nodeEntity) {
		try {
			if (nodeEntity != null) {
				this.deleteAllLink(nodeEntity);
				if (nodeEntity instanceof SwitchTopoNodeEntity) {
					this.deleteSwitch((SwitchTopoNodeEntity) nodeEntity);
				} else if (nodeEntity instanceof EponTopoEntity) {
					this.deleteOLT((EponTopoEntity) nodeEntity);
				} else if (nodeEntity instanceof ONUTopoNodeEntity) {
					this.deleteONU((ONUTopoNodeEntity) nodeEntity);

				} else if (nodeEntity instanceof CarrierTopNodeEntity) {

					this.deleteCarrierEntity((CarrierTopNodeEntity) nodeEntity);
				} else if (nodeEntity instanceof GPRSTopoNodeEntity) {
					this.deleteGPRSEntity((GPRSTopoNodeEntity) nodeEntity);
				} else if (nodeEntity instanceof FEPTopoNodeEntity) {
					this.deleteFepEntity((FEPTopoNodeEntity) nodeEntity);

				} else if (nodeEntity instanceof SubNetTopoNodeEntity) {
					this
							.deleteSubNetTopoNodeEntity((SubNetTopoNodeEntity) nodeEntity);
				} else if(nodeEntity instanceof VirtualNodeEntity){
					this.deleteVirtualNodeEntity((VirtualNodeEntity)nodeEntity);
				} else if(nodeEntity instanceof CommentTopoNodeEntity){
					deleteCommentEntity((CommentTopoNodeEntity)nodeEntity);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteOLT(EponTopoEntity eponTopoEntity) {
		OLTEntity oltEntity = commonService.getOLTByIpValue(eponTopoEntity
				.getIpValue());
		commonService.deleteEntity(eponTopoEntity);
		deleteEntityByOLT(OLTMulticast.class, oltEntity);
		deleteEntityByOLT(OLTDBA.class, oltEntity);
		deleteEntityByOLT(OLTChipInfo.class, oltEntity);
		deleteEntityByOLT(OLTPortStp.class, oltEntity);
		deleteEntityByOLT(OLTVlan.class, oltEntity);
		deleteEntityByOLT(OLTSTP.class, oltEntity);
		deleteEntityByOLT(OLTSlotConfig.class, oltEntity);
		commonService.deleteEntity(oltEntity);
	}

	public void deleteONU(ONUTopoNodeEntity topoNodeEntity) {
		try {
			ONUEntity onuEntity = commonService.getOnuByMacValue(topoNodeEntity
					.getMacValue());
			if (onuEntity != null) {
				commonService.deleteEntity(topoNodeEntity);
				deleteEntityByONU(ONULLID.class, onuEntity);
				commonService.deleteEntity(onuEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 删除注释节点
	 * add by 杨霄
	 * @param comment
	 */
	public void deleteCommentEntity(CommentTopoNodeEntity commentNode){
		commonService.deleteEntity(commentNode);
	}

	public void deleteFepEntity(FEPTopoNodeEntity fepTopoNodeEntity) {

		try {
			FEPEntity fepEntity = commonService.findFepEntity(fepTopoNodeEntity
					.getCode());
			commonService.deleteEntity(fepTopoNodeEntity);
			if (fepEntity != null) {
				commonService.deleteEntity(fepEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void deleteCarrierEntity(CarrierTopNodeEntity carrierTopNodeEntity) {
		try {
			CarrierEntity carrierEntity = commonService
					.getCarrierByCode(carrierTopNodeEntity.getCarrierCode());
			if (carrierEntity != null) {
				Set<CarrierRouteEntity> set = carrierEntity.getRoutes();
				if (set != null && set.size() > 0) {
					commonService.deleteEntities(set);
				}
				carrierEntity.getRoutes().removeAll(carrierEntity.getRoutes());
				commonService.deleteEntity(carrierEntity);
			}
			if (carrierTopNodeEntity != null) {
				commonService.deleteEntity(carrierTopNodeEntity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void deleteGPRSEntity(GPRSTopoNodeEntity gprsTopoNodeEntity) {

		if (gprsTopoNodeEntity != null) {
			GPRSEntity gprsEntity = commonService
					.getGPRSEntityByUserId(gprsTopoNodeEntity.getUserId());
			gprsTopoNodeEntity = manager.find(GPRSTopoNodeEntity.class,
					gprsTopoNodeEntity.getId());
			if (gprsTopoNodeEntity != null) {
				manager.remove(gprsTopoNodeEntity);
			}
			if (gprsEntity != null) {
				manager.remove(gprsEntity);
			}
		}
	}

	private void deleteSubNetTopoNodeEntity(
			SubNetTopoNodeEntity subNetTopoNodeEntity) {
		List<NodeEntity> childNode = this.queryNodeEntity(subNetTopoNodeEntity
				.getTopDiagramEntity().getId(), subNetTopoNodeEntity.getGuid());
		if (childNode != null && childNode.size() > 0) {
			for (NodeEntity nodeEntity : childNode) {
				nodeEntity.setParentNode(null);
				commonService.updateEntity(nodeEntity);
			}
		}
		commonService.deleteEntity(subNetTopoNodeEntity);
	}

	private void deleteVirtualNodeEntity(VirtualNodeEntity virtualNodeEntity){
		commonService.deleteEntity(virtualNodeEntity);
	}
    
	public void deleteEntityBySwitch(Class<?> clazz, SwitchNodeEntity entity) {
		String jpql = "select entity from " + clazz.getName()+ " as entity where entity.switchNode=?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, entity);
		List<?> datas = query.getResultList();
        
		if (datas != null && datas.size() != 0) {
			if(clazz.getName().equals(SwitchPortEntity.class.getName())){
				entity.getPorts().removeAll(entity.getPorts());
			}
			if(clazz.getName().equals(SwitchSerialPort.class.getName())){
				entity.getSerialPorts().removeAll(entity.getSerialPorts());
			}
			for (int i = 0; i < datas.size(); i++) {
				Object obj = datas.get(i);
				if (obj != null) {
					manager.remove(obj);
					manager.flush();
				}
			}
		}
	}
	
	public void updateUserPwd(UserEntity userEntity){
		try{
			String sql = "update UserEntity as entity set entity.password=? where entity.id=?";
			Query query = manager.createQuery(sql);
			query.setParameter(1, String.valueOf(userEntity.getPassword()));
			query.setParameter(2, userEntity.getId());
			query.executeUpdate();
		}
		catch(Exception ee){
			ee.printStackTrace();
		}
	}

	public void updateEntityBySwitch(Class<?> clazz, SwitchNodeEntity entity) {
		try {
		String jpql = "select entity from " + clazz.getName()+ " as entity where entity.switchNode=?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, entity);
		List<?> datas = query.getResultList();
		if (datas != null && datas.size() != 0) {
			for (int i = 0; i < datas.size(); i++) {
				Object obj = datas.get(i);
				clazz.getDeclaredMethod("setIssuedTag",int.class).invoke(obj,new Object[] { Constants.ISSUEDADM });
				manager.merge(obj);
			}
		}
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		

	}

	public void deleteEntityBySwitch3(Class<?> clazz, SwitchLayer3 layer3) {
		String jpql = "select entity from " + clazz.getName()
				+ " as entity where entity.switchLayer3=?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, layer3);
		List<?> datas = query.getResultList();

		if (datas != null && datas.size() != 0) {
			for (int i = 0; i < datas.size(); i++) {
				Object obj = datas.get(i);
				if (obj != null) {
					manager.remove(obj);
				}
			}
		}
	}

	public void deleteEntityByOLT(Class<?> clazz, OLTEntity entity) {
		String jpql = "select entity from " + clazz.getName()
				+ " as entity where entity.oltEntity=?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, entity);
		List<?> datas = query.getResultList();

		if (datas != null && datas.size() != 0) {
			for (int i = 0; i < datas.size(); i++) {
				Object obj = datas.get(i);
				manager.remove(obj);
			}
		}
	}

	public void deleteEntityByONU(Class<?> clazz, ONUEntity entity) {
		String jpql = "select entity from " + clazz.getName()
				+ " as entity where entity.onuEntity=?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, entity);
		List<?> datas = query.getResultList();

		if (datas != null && datas.size() != 0) {
			for (int i = 0; i < datas.size(); i++) {
				Object obj = datas.get(i);
				if (obj != null) {
					manager.remove(obj);
				}
			}
		}
	}

	public void saveLLID(ONULLID llid) {
		ONULLID dbllid = null;
		String jpql = "select entity from " + ONULLID.class.getName()
				+ " as entity where entity.macValue=?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, llid.getMacValue());
		List<?> datas = query.getResultList();
		if (datas != null && datas.size() != 0) {
			dbllid = (ONULLID) datas.get(0);
		}
		if (dbllid != null) {
			dbllid.setAssuredBW(llid.getAssuredBW());
			dbllid.setDiID(llid.getDiID());
			dbllid.setPeakBW(llid.getPeakBW());
			dbllid.setPortName(llid.getPortName());
			dbllid.setStaticBW(llid.getStaticBW());
			dbllid.setOltIp(llid.getOltIp());
			manager.merge(dbllid);
		} else {
			ONUEntity onu = commonService.getOnuByMacValue(llid.getMacValue());
			llid.setOnuEntity(onu);
			manager.persist(llid);
		}
	}

	public FEPEntity findFepEntity(String code) {
		String sql = "select c from FEPEntity c where c.code=:code";
		Query query = manager.createQuery(sql);
		query.setParameter("code", code);
		if (query.getResultList() != null && query.getResultList().size() > 0) {
			return (FEPEntity) query.getResultList().get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public OLTBaseInfo getOLTByIp(String ipvalue) {
		String jpql = "select entity.oltBaseInfo from "
				+ OLTEntity.class.getName()
				+ " as entity where entity.ipValue = ?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, ipvalue);
		List<OLTBaseInfo> datas = query.getResultList();
		if (datas != null && datas.size() != 0) {
			return datas.get(0);
		} else {
			return null;
		}
	}

	public void updateLLDP(LLDPInofEntity lldp) {
		int deviceType = lldp.getRemoteDeviceType();
		String ipValue = lldp.getRemoteIP();
		int port = lldp.getRemotePortNo();
		if (deviceType == Constants.DEV_SWITCHER2) {
			SwitchNodeEntity switchNode = commonService.getSwitchByIp(ipValue);
			if (switchNode != null) {
				Set<LLDPInofEntity> lldps = switchNode.getLldpinfos();
				if (lldps != null && lldps.size() != 0) {
					for (LLDPInofEntity nlldp : lldps) {
						if (nlldp != null) {
							String nlocalIP = nlldp.getLocalIP();
							int nlocalPort = nlldp.getLocalPortNo();

							if (ipValue != null && ipValue.equals(nlocalIP)
									&& port == nlocalPort) {
								nlldp.setConnected(false);
								break;
							}
						}

					}
				}

			}
		} else if (deviceType == Constants.DEV_OLT) {

		} else if (deviceType == Constants.DEV_SWITCHER3) {

		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List findDeviceByFep(Class clazz, String code) {
		List backData = null;
		try {
			FEPEntity fepEntity = datacache.getFEPByCode(code);

			String clazzName = clazz.getName();
			String jpql = "select entity from " + clazzName + " as entity";
			Query query = manager.createQuery(jpql);
			List datas = query.getResultList();
			logger.info("刷新拓扑时查询数据库" + clazz.getName() + "********数量为："
					+ datas.size());
			backData = new ArrayList();
			if (clazzName.equals(SwitchNodeEntity.class.getName())) {
				for (int i = 0; i < datas.size(); i++) {
					SwitchNodeEntity entity = (SwitchNodeEntity) datas.get(i);
					if (entity.isSyschorized()) {
						if (entity.getBaseConfig() != null) {
							String ip = entity.getBaseConfig().getIpValue();
							boolean ok = isContainer(ip, fepEntity);
							if (ok) {
								backData.add(entity);
							} else {
								logger.error("刷新拓扑时该SwitchNodeEntity " + ip
										+ "不在前置机的管辖范围内！");
							}
						} else {
							logger.error("交换机基本配置为空");
						}
					}
				}
			} else if (clazzName.equals(SwitchLayer3.class.getName())) {
				for (int i = 0; i < datas.size(); i++) {
					SwitchLayer3 entity = (SwitchLayer3) datas.get(i);
					if (entity.isSyschorized()) {
						String ip = entity.getIpValue();
						boolean ok = isContainer(ip, fepEntity);
						if (ok) {
							backData.add(entity);
							logger.info("刷新拓扑时该SwitchLayer3 LLDP  "
									+ entity.getLldps().size());
						} else {
							logger.error("刷新拓扑时该SwitchLayer3 " + ip
									+ "不在前置机的管辖范围内！");
						}
					}
				}
			} else if (clazzName.equals(OLTEntity.class.getName())) {
				for (int i = 0; i < datas.size(); i++) {
					OLTEntity entity = (OLTEntity) datas.get(i);
					if (entity.isSyschorized()) {
						String ip = entity.getIpValue();
						boolean ok = isContainer(ip, fepEntity);
						if (ok) {
							backData.add(entity);
						} else {
							logger.error("刷新拓扑时该OLT " + ip + "不在前置机的管辖范围内！");
						}
					}
				}
			} else if (clazzName.equals(ONUEntity.class.getName())) {
				for (int i = 0; i < datas.size(); i++) {
					ONUEntity entity = (ONUEntity) datas.get(i);
					if (entity.isSyschorized()) {
						Set<LLDPInofEntity> lldps = entity.getLldpinfos();
						if (lldps != null && lldps.size() > 0) {
							for (LLDPInofEntity lldp : lldps) {
								String ip = lldp.getRemoteIP();
								boolean ok = isContainer(ip, fepEntity);
								if (ok) {
									backData.add(entity);
									break;
								}
							}
						} else {
							logger.error("ONU LLDP 为空！");
						}
					}
				}
			}
			logger.info("刷新拓扑时查询数据库结果 " + clazz.getName() + " 数量为："
					+ backData.size());

		} catch (Exception e) {
			logger.error("方法 findDeviceByFep error", e);
		}
		return backData;
	}

	@SuppressWarnings("unchecked")
	public List findDevice(Class clazz) {
		List backData = null;
		try {
			String clazzName = clazz.getName();
			String jpql = "select entity from " + clazzName
					+ " as entity where entity.syschorized=true";
			Query query = manager.createQuery(jpql);

			if (clazzName.equals(SwitchNodeEntity.class.getName())) {
				if (CacheDatas.SWITCHCOUNT >= 0) {
					query.setFirstResult(0);
					query.setMaxResults(CacheDatas.SWITCHCOUNT);
					logger.info("允许发现的二层交换机数量：" + CacheDatas.SWITCHCOUNT);
				}

			} else if (clazzName.equals(SwitchLayer3.class.getName())) {
				if (CacheDatas.SWITCH3COUNT >= 0) {
					query.setFirstResult(0);
					query.setMaxResults(CacheDatas.SWITCH3COUNT);
					logger.info("允许发现的三层交换机数量：" + CacheDatas.SWITCH3COUNT);
				}

			} else if (clazzName.equals(OLTEntity.class.getName())) {
				if (CacheDatas.OLTCOUNT >= 0) {
					query.setFirstResult(0);
					query.setMaxResults(CacheDatas.OLTCOUNT);
					logger.info("允许发现的OTL数量：" + CacheDatas.OLTCOUNT);
				}

			} else if (clazzName.equals(ONUEntity.class.getName())) {
				if (CacheDatas.ONUCOUNT >= 0) {
					query.setFirstResult(0);
					query.setMaxResults(CacheDatas.ONUCOUNT);
					logger.info("允许发现的ONU数量：" + CacheDatas.ONUCOUNT);
				}

			} else if (clazzName.equals(GPRSEntity.class.getName())) {
				if (CacheDatas.GPRSCOUNT >= 0) {

					query.setFirstResult(0);
					query.setMaxResults(CacheDatas.GPRSCOUNT);
					logger.info("允许发现的GPRS数量：" + CacheDatas.GPRSCOUNT);
				}

			} else if (clazzName.equals(CarrierEntity.class.getName())) {
				if (CacheDatas.PLCCOUNT >= 0) {

					query.setFirstResult(0);
					query.setMaxResults(CacheDatas.PLCCOUNT);
					logger.info("允许发现的载波机数量：" + CacheDatas.PLCCOUNT);
				}

			}
			backData = query.getResultList();
			logger.info("刷新拓扑时查询数据库" + clazz.getName() + "********数量为："
					+ backData.size());
		} catch (Exception e) {
			logger.error("方法 findDevice error", e);
		}
		return backData;
	}

	private boolean isContainer(String ipValue, FEPEntity fep) {
		boolean contained = false;
		List<IPSegment> segments = fep.getSegment();
		for (IPSegment seg : segments) {
			contained = CacheDatas.ipBetweenSegment(ipValue, seg);
			if (contained) {
				break;
			}
		}
		return contained;

	}

	@SuppressWarnings("unchecked")
	public List<PersonBean> findPersonInfo(String ip_value) {

		List<PersonBean> datas = null;
		try {
			String sql = "select distinct person.email,person.mobile,person.name"
					+ " from SWITCHNODE switcher,SwitchBaseConfig config,AddressEntity address,AreaEntity area,USERENTITY_AREAENTITY userarea,UserEntity userentity,PersonInfo person"
					+ " where switcher.baseconfig_id=config.id "
					+ " and switcher.address_id = address.id "
					+ " and area.id = address.areaid "
					+ " and userentity.id = userarea.USERENTITY_ID "
					+ " and person.id = userentity.personid "
					+ " and config.ipvalue=:IPVALUE";
			Query query = manager.createNativeQuery(sql);
			query.setParameter("IPVALUE", ip_value);
			List<Object[]> list = query.getResultList();

			if (list != null && list.size() > 0) {
				datas = new ArrayList<PersonBean>();
				for (Object[] obj : list) {
					String mail = (String) obj[0];
					String mobile = (String) obj[1];
					String name = (String) obj[2];
					PersonBean bean = new PersonBean();
					bean.setEmail(mail);
					bean.setMobile(mobile);
					bean.setUserName(name);
					datas.add(bean);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return datas;
	}

	@SuppressWarnings("unchecked")
	public List<UserEntity> findUserInfo(String ip_value) {

		List<UserEntity> datas = null;

		try {
			String sql = "select userentity.*"
					+ " from SWITCHNODE switcher,SwitchBaseConfig config,AddressEntity address,AreaEntity area,USERENTITY_AREAENTITY userarea,UserEntity userentity,PersonInfo person"
					+ " where switcher.baseconfig_id=config.id "
					+ " and switcher.address_id = address.id "
					+ " and area.id = address.areaid "
					+ " and userentity.id = userarea.USERENTITY_ID "
					+ " and person.id = userentity.personid "
					+ " and config.ipvalue=:IPVALUE";
			Query query = manager.createNativeQuery(sql, UserEntity.class);
			query.setParameter("IPVALUE", ip_value);
			if (query.getResultList() != null
					&& query.getResultList().size() > 0) {
				datas = query.getResultList();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return datas;
	}

	@SuppressWarnings("unchecked")
	public List<TrapSimpleWarning> queryTrapSimpleWarning(String ip,
			int warnType, int status, Date date, int port) {

		List<TrapSimpleWarning> list = null;
		try {
			Calendar nowDate = Calendar.getInstance();
			nowDate.setTime(date);
			Calendar beforeDate = Calendar.getInstance();
			beforeDate.set(nowDate.get(Calendar.YEAR), nowDate
					.get(Calendar.MONTH), nowDate.get(Calendar.DATE), nowDate
					.get(Calendar.HOUR), nowDate.get(Calendar.MINUTE), nowDate
					.get(Calendar.SECOND) - 50);

			String sql = "select c from TrapSimpleWarning c where ";

			if (ip != null && !ip.equals("")) {

				sql += " c.ipValue like :ip";
			}
			if (warnType > -1) {
				sql += " and c.warningType=:warnType";
			}
			if (status > -1) {
				sql += " and c.currentStatus=:status";
			}
			if (date != null) {
				sql += " and c.createDate>=:beforeDate";
			}
			if (port > -1) {
				sql += " and c.portNo=:port";

			}
			Query query = manager.createQuery(sql);
			if (ip != null && !ip.equals("")) {
				query.setParameter("ip", "%" + ip + "%");
			}
			if (warnType > -1) {

				query.setParameter("warnType", warnType);
			}
			if (status > -1) {
				query.setParameter("status", status);
			}
			if (date != null) {
				query.setParameter("beforeDate", beforeDate,
						TemporalType.TIMESTAMP);
			}
			if (port > -1) {
				query.setParameter("port", port);
			}

			list = query.getResultList();
			if (list != null && list.size() > 0) {
				return list;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return list;
	}

	@SuppressWarnings("unchecked")
	public TrapWarningEntity findTrapEntity(SimpleWarning sw) {
		String jpql = "select entity from " + TrapWarningEntity.class.getName()
				+ " as entity where entity.id=?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, sw.getId());
		List datas = query.getResultList();
		if (datas != null && datas.size() != 0) {
			return (TrapWarningEntity) datas.get(0);
		} else {
			return null;
		}
	}

	public TrapSimpleWarning findTrapSimpleWarning(SimpleWarning sw) {

		TrapSimpleWarning simpleWarning = manager.find(TrapSimpleWarning.class,
				sw.getId());

		return simpleWarning;
	}

	@SuppressWarnings("unchecked")
	public GPRSEntity updateGPRSEntity(GPRSEntity entity) {
		String userId = entity.getUserId();
		String jpql = "select entity from " + GPRSEntity.class.getName()
				+ " as entity where entity.userId=?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, userId);
		List<GPRSEntity> gprss = (ArrayList<GPRSEntity>) query.getResultList();
		if (gprss == null || gprss.size() == 0) {
			return null;
		} else {
			GPRSEntity gprs = gprss.get(0);
			if (gprs != null) {
				gprs.setInternateAddress(entity.getInternateAddress());
				gprs.setDescs(entity.getDescs());
				gprs.setLocalAddress(entity.getLocalAddress());
				gprs.setLocalPort(entity.getLocalPort());
				gprs.setPort(entity.getPort());
				gprs.setLogonDate(entity.getLogonDate());
				gprs.setStatus(entity.getStatus());
				gprs = manager.merge(gprs);
			}
			return gprs;
		}

	}

	/**
	 * 把查询出来的数据转换为SimpleWarning对象数据集
	 * 
	 * @param datas
	 * @return
	 */
	private List<SimpleWarning> changedata(String userName, ResultSet rs)
			throws SQLException {
		List<SimpleWarning> sws = new ArrayList<SimpleWarning>();
		while (rs.next()) {
			SimpleWarning sw = new SimpleWarning();
			long id = rs.getLong(1);// id
			String ipValue = rs.getString(2);// ip
			int warntype = rs.getInt(3);// type
			int status = rs.getInt(4);// status
			String descs = rs.getString(5);// descs
			int warnlevel = rs.getInt(6);// warnlevel
			Timestamp time = rs.getTimestamp(7);// time
			sw.setId(id);
			sw.setIpValue(ipValue);
			sw.setWarningType(warntype);
			sw.setWarningLevel(warnlevel);
			sw.setDescs(descs);
			sw.setTime(time);
			sw.setUserName(userName);
			sw.setStatus(status);
			sws.add(sw);
		}
		return sws;
	}

	public void refurshTopo(Long diagramId) {

		try {
			TopDiagramEntity diagram = findTopDiagramEntity(diagramId);
			if (diagram != null) {
				if (diagram != null) {
					this.clearLinkEntity();// 清除所有的连线
					Set<NodeEntity> nodes = diagram.getNodes();
					deleteEponVlan();
					deleteSwitchNodeAllVlan();
					deleteSwitchAllSNMPHost();
					deleteSwitchAllRing();
					// 删除节点,同时删除该节点下的交换机的上上一次的lldp信息
					List<NodeEntity> tempNodes = new ArrayList<NodeEntity>();
					for (NodeEntity node : nodes) {
						if (node instanceof SwitchTopoNodeEntity) {
							if (node.getId() != null) {
								tempNodes.add(node);
							}
							String ipvalue = ((SwitchTopoNodeEntity) node)
									.getIpValue();
							// SwitchNodeEntity switcher = commonService
							// .getSwitchByIp(ipvalue);
							Object[] parms = { ipvalue };
							String where = " where entity.baseConfig.ipValue=?";
							List<SwitchNodeEntity> entities = (List<SwitchNodeEntity>) commonService.findAll(SwitchNodeEntity.class, where,parms);
							if (entities != null && entities.size() > 0) {
								for (SwitchNodeEntity switcher : entities) {

									if (switcher != null) {
										switcher.setSyschorized(false);
										Set<SwitchPortEntity> ports = switcher.getPorts();
										Set<SwitchPortEntity> setPorts = new HashSet<SwitchPortEntity>();
										setPorts.addAll(ports);
										switcher.getPorts().removeAll(ports);
										if (setPorts.size() > 0) {
											commonService.deleteEntities(setPorts);
										}
										List<LLDPInofEntity> tempLLDPs = new ArrayList<LLDPInofEntity>();
										Set<LLDPInofEntity> lldps = switcher.getLldpinfos();
										for (LLDPInofEntity lldp : lldps) {
											if (lldp.isSyschorized()) {
												lldp.setSyschorized(false);
											} else {
												tempLLDPs.add(lldp);
											}
										}
										switcher.getLldpinfos().removeAll(tempLLDPs);
										if (tempLLDPs.size() > 0) {
											commonService.deleteLLDPS(tempLLDPs);
										}
										Set<SwitchRingInfo> switchRingInfos = switcher.getRings();
										if (switchRingInfos != null&& switchRingInfos.size() > 0) {
											switcher.getRings().removeAll(switchRingInfos);
											commonService.deleteEntities(switchRingInfos);
										}
									}

								}

							}
						} else if (node instanceof SwitchTopoNodeLevel3) {
							if (node.getId() != null) {
								tempNodes.add(node);
								SwitchTopoNodeLevel3 switchTopoNodeLevel3 = (SwitchTopoNodeLevel3) node;
								String ipValue = switchTopoNodeLevel3
										.getIpValue();
								// SwitchLayer3 switchLayer3 = commonService
								// .getSwitcher3ByIP(ipValue);

								Object[] parms = { ipValue };
								String where = " where entity.ipValue=?";
								List<SwitchLayer3> layer3 = (List<SwitchLayer3>) commonService.findAll(SwitchLayer3.class, where,parms);
								if (layer3 != null && layer3.size() > 0) {

									for (SwitchLayer3 switchLayer3 : layer3) {

										if (switchLayer3 != null) {
											switchLayer3.setSyschorized(false);
											Set<SwitchPortLevel3> ports = switchLayer3.getPorts();
											Set<SwitchPortLevel3> setPorts = new HashSet<SwitchPortLevel3>();
											setPorts.addAll(ports);
											switchLayer3.getPorts().removeAll(ports);
											if (setPorts.size() > 0) {
												commonService.deleteEntities(setPorts);
											}
											List<LLDPInofEntity> tempLLDPs = new ArrayList<LLDPInofEntity>();
											Set<LLDPInofEntity> lldps = switchLayer3
													.getLldps();
											for (LLDPInofEntity lldp : lldps) {
												if (lldp.isSyschorized()) {
													lldp.setSyschorized(false);
												} else {
													tempLLDPs.add(lldp);
												}
											}
											switchLayer3.getLldps().removeAll(
													tempLLDPs);
											if (tempLLDPs.size() > 0) {
												commonService
														.deleteLLDPS(tempLLDPs);
											}
											commonService
													.updateEntity(switchLayer3);
										}

									}
								}
							}
						} else if (node instanceof RingEntity) {
							if (node.getId() != null) {
								tempNodes.add(node);
							}
						} else if (node instanceof EponTopoEntity) {
							if (node.getId() != null) {
								tempNodes.add(node);

								String ipValue = ((EponTopoEntity) node)
										.getIpValue();
								OLTEntity olt = commonService
										.getOLTByIpValue(ipValue);
								if (olt != null) {

									olt.setSyschorized(false);
									List<LLDPInofEntity> tempLLDPs = new ArrayList<LLDPInofEntity>();
									Set<OLTPort> tempPort = new HashSet<OLTPort>();
									Set<OLTPort> ports = olt.getPorts();
									tempPort.addAll(ports);
									olt.getPorts().removeAll(ports);
									if (tempPort.size() > 0) {
										commonService.deleteEntities(tempPort);
									}

									Set<LLDPInofEntity> lldps = olt
											.getLldpinfos();
									for (LLDPInofEntity lldp : lldps) {
										if (lldp.isSyschorized()) {
											lldp.setSyschorized(false);
										} else {
											tempLLDPs.add(lldp);
										}
									}
									olt.getLldpinfos().removeAll(tempLLDPs);
									if (tempLLDPs.size() > 0) {
										commonService.deleteEntities(tempLLDPs);
									}
								}
							}

						} else if (node instanceof Epon_S_TopNodeEntity) {
							if (node.getId() != null) {
								tempNodes.add(node);
							}

						} else if (node instanceof ONUTopoNodeEntity) {
							if (node.getId() != null) {
								tempNodes.add(node);
								ONUTopoNodeEntity onuTopo = (ONUTopoNodeEntity) node;
								ONUEntity onuEntity = commonService
										.getOnuByMacValue(onuTopo.getMacValue());
								if (onuEntity != null) {
									deleteEntityByONU(ONULLID.class, onuEntity);
									commonService.deleteEntity(onuEntity);
								}

							}
						} else if (node instanceof SubNetTopoNodeEntity) {
							if (node.getId() != null) {
								tempNodes.add(node);
							}
						}
					}
					if (tempNodes.size() > 0) {
						diagram.getNodes().removeAll(tempNodes);
						for (NodeEntity nodeEntity : tempNodes) {
							if (nodeEntity != null) {
								boolean flag = false;
								if (nodeEntity instanceof SubNetTopoNodeEntity) {
									flag = this.queryCheckNodeEntity(diagramId,
											((SubNetTopoNodeEntity) nodeEntity)
													.getGuid());
								}
								this.iteratorNode(nodeEntity, diagramId);
								if (flag) {
									commonService.deleteEntity(nodeEntity);
								}
							}
						}

					}
				}
			}
		} catch (Exception e) {
			logger.error("拓扑刷新" + e);
		}

	}
    
	private void iteratorNode(NodeEntity nodeEntity, long id) {
		try {
			if (nodeEntity instanceof SubNetTopoNodeEntity) {
				List<NodeEntity> childNode = this.queryNodeEntity(id,
						((SubNetTopoNodeEntity) nodeEntity).getGuid());
				if (childNode != null && childNode.size() > 0) {
					for (NodeEntity node : childNode) {
						this.iteratorNode(node, id);
					}
				} else {
					List<NodeEntity> nodes = new ArrayList<NodeEntity>();
					if (nodeEntity instanceof FEPTopoNodeEntity) {
						FEPTopoNodeEntity fepTopoNodeEntity = (FEPTopoNodeEntity) nodeEntity;
						fepTopoNodeEntity.setParentNode(null);
						commonService.updateEntity(fepTopoNodeEntity);

					} else if (nodeEntity instanceof GPRSTopoNodeEntity) {
						GPRSTopoNodeEntity gprsTopoNodeEntity = (GPRSTopoNodeEntity) nodeEntity;
						gprsTopoNodeEntity.setParentNode(null);
						commonService.updateEntity(gprsTopoNodeEntity);

					} else if (nodeEntity instanceof CarrierTopNodeEntity) {
						CarrierTopNodeEntity carrierTopNodeEntity = (CarrierTopNodeEntity) nodeEntity;
						carrierTopNodeEntity.setParentNode(null);
						commonService.updateEntity(carrierTopNodeEntity);

					} else {
						nodes.add(nodeEntity);
						this.deleteDeviceS(nodes);
					}
				}

			} else {
				List<NodeEntity> nodes = new ArrayList<NodeEntity>();
				if (nodeEntity instanceof FEPTopoNodeEntity) {
					FEPTopoNodeEntity fepTopoNodeEntity = (FEPTopoNodeEntity) nodeEntity;
					fepTopoNodeEntity.setParentNode(null);
					commonService.updateEntity(fepTopoNodeEntity);

				} else if (nodeEntity instanceof GPRSTopoNodeEntity) {
					GPRSTopoNodeEntity gprsTopoNodeEntity = (GPRSTopoNodeEntity) nodeEntity;
					gprsTopoNodeEntity.setParentNode(null);
					commonService.updateEntity(gprsTopoNodeEntity);

				} else if (nodeEntity instanceof CarrierTopNodeEntity) {
					CarrierTopNodeEntity carrierTopNodeEntity = (CarrierTopNodeEntity) nodeEntity;
					carrierTopNodeEntity.setParentNode(null);
					commonService.updateEntity(carrierTopNodeEntity);

				} else {
					nodes.add(nodeEntity);
					deleteDeviceS(nodes);
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void deleteDeviceS(List<NodeEntity> nodes) {
		try {
			for (NodeEntity node : nodes) {
				if (node instanceof CarrierTopNodeEntity) {
					CarrierTopNodeEntity carrierNode = (CarrierTopNodeEntity) node;
					CarrierEntity carrierEntity = commonService
							.getCarrierByCode(carrierNode.getCarrierCode());
					if (carrierEntity != null) {
						Set<CarrierRouteEntity> set = carrierEntity.getRoutes();
						if (set != null && set.size() > 0) {
							commonService.deleteEntities(set);
						}
						carrierEntity.getRoutes().removeAll(
								carrierEntity.getRoutes());
						commonService.deleteEntity(node);
						commonService.deleteEntity(carrierEntity);
					}
				} else if (node instanceof GPRSTopoNodeEntity) {
					GPRSTopoNodeEntity nodeEntity = (GPRSTopoNodeEntity) node;
					if (nodeEntity != null) {
						GPRSEntity gprsEntity = commonService
								.getGPRSEntityByUserId(nodeEntity.getUserId());
						if (nodeEntity != null) {
							commonService.deleteEntity(nodeEntity);
						}
						if (gprsEntity != null) {
							commonService.deleteEntity(gprsEntity);
						}
					}
				} else {
					if (node.getId() != null) {
						commonService.deleteEntity(node);
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// =======OLT 相关操作
	@SuppressWarnings("unchecked")
	private void deleteEponVlan() {
		// delete oltvlan
		// delete onuvlan
		try {
			String jpql = "select entity from OLTVlan entity";
			Query query = manager.createQuery(jpql);
			List<OLTVlan> datas = query.getResultList();
			if (datas != null && datas.size() > 0) {
				commonService.deleteEntities(datas);
			}
			jpql = "select entity from ONUVlan entity";
			query = manager.createQuery(jpql);
			List<ONUVlan> datas1 = query.getResultList();
			if (datas1 != null && datas1.size() > 0) {
				commonService.deleteEntities(datas1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private void deleteSwitchNodeAllVlan(){
		
		try {
			
			String jpql = "select entity from VlanConfig entity";
			Query query = manager.createQuery(jpql);
			List<VlanConfig> datas = query.getResultList();
			if (datas != null && datas.size() > 0) {
				commonService.deleteEntities(datas);
			}
			
			List<VlanEntity> entities =(List<VlanEntity>) commonService.findAll(VlanEntity.class,  "where entity.vlanConfig is null");
			if(entities!=null&&entities.size()>0){
				commonService.deleteEntities(entities);
			}
			List<VlanPortConfig> portConfigs =(List<VlanPortConfig>)commonService.findAll(VlanPortConfig.class); 
			if(portConfigs!=null&&portConfigs.size()>0){
				
				commonService.deleteEntities(portConfigs);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void deleteSwitchAllSNMPHost(){
		
try {
			
			String jpql = "select entity from SNMPHost entity";
			Query query = manager.createQuery(jpql);
			List<VlanConfig> datas = query.getResultList();
			if (datas != null && datas.size() > 0) {
				commonService.deleteEntities(datas);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private void deleteSwitchAllRing(){
		
try {
			
			String jpql = "select entity from RingConfig entity";
			Query query = manager.createQuery(jpql);
			List<VlanConfig> datas = query.getResultList();
			if (datas != null && datas.size() > 0) {
				commonService.deleteEntities(datas);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public void deleteLink(LinkEntity link, int syn_type, String from,
			String clientIp) {

		NodeEntity node1 = link.getNode1();
		NodeEntity node2 = link.getNode2();
		LLDPInofEntity lldp = link.getLldp();
		if (lldp != null) {
			int deviceType = lldp.getLocalDeviceType();
			if (deviceType == Constants.DEV_OLT) {
				String ipValue = lldp.getLocalIP();
				OLTEntity oltEntity = commonService.getOLTByIpValue(ipValue);
				Set<LLDPInofEntity> lldps = oltEntity.getLldpinfos();
				for (LLDPInofEntity lldp1 : lldps) {
					if (lldp.getId().longValue() == lldp1.getId().longValue()) {
						oltEntity.getLldpinfos().remove(lldp1);
						commonService.deleteEntity(link);
						commonService.deleteEntity(lldp1);
					}
				}
			} else if (deviceType == Constants.DEV_ONU) {
				String macValue = lldp.getLocalIP();
				ONUEntity onuEntity = commonService.getOnuByMacValue(macValue);
				Set<LLDPInofEntity> lldps = onuEntity.getLldpinfos();
				for (LLDPInofEntity lldp1 : lldps) {
					if (lldp.getId().longValue() == lldp1.getId().longValue()) {
						onuEntity.getLldpinfos().remove(lldp1);
						commonService.deleteEntity(link);
						commonService.deleteEntity(lldp1);
					}
				}
			} else if (deviceType == Constants.DEV_SWITCHER2) {
				String ipValue = lldp.getLocalIP();
				SwitchNodeEntity switchEntity = commonService
						.getSwitchByIp(ipValue);
				Set<LLDPInofEntity> lldps = switchEntity.getLldpinfos();
				for (LLDPInofEntity lldp1 : lldps) {
					if (lldp.getId().longValue() == lldp1.getId().longValue()) {
						switchEntity.getLldpinfos().remove(lldp1);
						commonService.deleteEntity(link);
						commonService.deleteEntity(lldp1);
					}
				}
			} else if (deviceType == Constants.DEV_SWITCHER3) {

			}
		} else {
			if (node1 instanceof CarrierTopNodeEntity
					&& node2 instanceof CarrierTopNodeEntity) {
				CarrierRouteEntity route = link.getCarrierRoute();
				if (route != null) {
					CarrierEntity carrier = route.getCarrier();
					int carrierCode = carrier.getCarrierCode();
					CarrierEntity carrier1 = commonService
							.getCarrierByCode(carrierCode);
					Set<CarrierRouteEntity> routes = carrier1.getRoutes();
					for (CarrierRouteEntity route1 : routes) {
						if (route1.getId().longValue() == route.getId()
								.longValue()) {
							carrier1.getRoutes().remove(route1);
							commonService.deleteEntity(link);
							commonService.deleteEntity(route1);
							break;
						}
					}
					try {
						sendMessageService.sendCarrierMessage(
								MessageNoConstants.CARRIERROUTECONFIG, from,
								clientIp, carrier);
					} catch (JMSException e) {
						e.printStackTrace();
					}
				} else {
					commonService.deleteEntity(link);
				}
			}

		}
	}

	
	@SuppressWarnings("unchecked")
	public List<SimpleWarning> findWarningInfo(TrapWarningBean trapWarningBean) {
		List<SimpleWarning> listWarning = new ArrayList<SimpleWarning>();
		List<Object[]> warningList = null;
		Calendar calendar1 = null;
		Calendar calendar2 = null;
		if (trapWarningBean.getStartDate() != null) {
			calendar1 = this.setStartDate(trapWarningBean.getStartDate());

		}
		if (trapWarningBean.getEndDate() != null) {
			calendar2 = this.setEndDate(trapWarningBean.getEndDate());
		}
		try {

			if (trapWarningBean != null) {
				String sql = "select trap.sampleTime,wt.warningLevel,trap.ipValue,trap.descs,trap.warningType,trap.currentStatus from "
						+ "TrapWarningEntity trap ,warningType wt "
						+ "where wt.warningType=trap.warningType";
				if (trapWarningBean.getUserName() != null
						&& !trapWarningBean.getUserName().equals("")) {
					if (trapWarningBean.getUserName().equals("admin")) {

					} else {
						sql += " and INSTR(trap.users,:userName)<>0";
					}

				}
				if (trapWarningBean.getStartDate() != null) {
					sql += " and trap.sampleTime >=:startDate";

				}

				if (trapWarningBean.getEndDate() != null) {
					sql += " and trap.sampleTime <=:endDate";
					// Mysql
					// sql+=" and DATE_FORMAT(trap.sampleTime,'%Y%m%d') <=DATE_FORMAT(:endDate,'%Y%m%d')";
				}
				if (trapWarningBean.getLevel() >= 0) {
					sql += " and wt.warningLevel=:level";
				}
				if (trapWarningBean.getIpValue() != null
						&& !trapWarningBean.getIpValue().equals("")) {
					sql += " and trap.ipValue like :ipValue";
				}
				if (trapWarningBean.getWarningType() >= 0) {

					sql += " and wt.warningType=:warningType";
				}
				if (trapWarningBean.getStatus() >= 0) {

					sql += " and trap.currentStatus=:status";
				}
				sql += " order by trap.sampleTime desc";
				Query query = manager.createNativeQuery(sql);

				if (trapWarningBean.getStartDate() != null) {
					query.setParameter("startDate", calendar1.getTime());

				}
				if (trapWarningBean.getEndDate() != null) {
					query.setParameter("endDate", calendar2.getTime());
				}
				if (trapWarningBean.getLevel() >= 0) {
					query.setParameter("level", trapWarningBean.getLevel());

				}
				if (trapWarningBean.getIpValue() != null
						&& !trapWarningBean.getIpValue().equals("")) {
					query.setParameter("ipValue", "%"
							+ trapWarningBean.getIpValue() + "%");
				}
				if (trapWarningBean.getWarningType() >= 0) {
					query.setParameter("warningType", trapWarningBean
							.getWarningType());
				}
				if (trapWarningBean.getStatus() >= 0) {
					query.setParameter("status", trapWarningBean.getStatus());
				}

				if (trapWarningBean.getUserName() != null
						&& !trapWarningBean.getUserName().equals("")) {
					if (trapWarningBean.getUserName().equals("admin")) {

					} else {
						query.setParameter("userName", trapWarningBean
								.getUserName());
					}
				}
				if (trapWarningBean.getStartPage() > 1) {
					query.setFirstResult((trapWarningBean.getStartPage() - 1)
							* trapWarningBean.getMaxPageSize());
				} else if (trapWarningBean.getStartPage() == 1) {
					query.setFirstResult(0);
				}
				if (trapWarningBean.getMaxPageSize() > 0) {
					query.setMaxResults(trapWarningBean.getMaxPageSize());
				}
				warningList = query.getResultList();
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		if (warningList != null && warningList.size() != 0) {
			for (int i = 0; i < warningList.size(); i++) {
				SimpleWarning simpleWarning = new SimpleWarning();
				if (warningList.get(i)[0] != null) {
					simpleWarning.setTime((Date) warningList.get(i)[0]);
				}
				if (warningList.get(i)[1] instanceof BigDecimal) {
					BigDecimal bigDecimal = (BigDecimal) warningList.get(i)[1];
					if (bigDecimal != null) {
						simpleWarning.setWarningLevel(bigDecimal.intValue());
					}
				} else if (warningList.get(i)[1] instanceof BigInteger) {
					BigInteger bigInteger = (BigInteger) warningList.get(i)[1];
					if (bigInteger != null) {
						simpleWarning.setWarningLevel(bigInteger.intValue());
					}
				} else if (warningList.get(i)[1] instanceof Integer) {
					simpleWarning
							.setWarningLevel((Integer) warningList.get(i)[1]);
				}

				if (warningList.get(i)[2] != null
						&& !warningList.get(i)[2].equals("")) {
					simpleWarning.setIpValue((String) warningList.get(i)[2]);
				}
				if (warningList.get(i)[3] != null
						&& !warningList.get(i)[3].equals("")) {
					simpleWarning.setContent((String) warningList.get(i)[3]);
				}

				if (warningList.get(i)[4] instanceof BigDecimal) {
					BigDecimal bigDecimal = (BigDecimal) warningList.get(i)[4];
					if (bigDecimal != null) {
						simpleWarning.setWarningType(bigDecimal.intValue());
					}
				} else if (warningList.get(i)[4] instanceof BigInteger) {
					BigInteger bigInteger = (BigInteger) warningList.get(i)[4];
					if (bigInteger != null) {
						simpleWarning.setWarningType(bigInteger.intValue());
					}
				} else if (warningList.get(i)[4] instanceof Integer) {
					simpleWarning
							.setWarningType((Integer) warningList.get(i)[4]);
				}

				if (warningList.get(i)[5] instanceof BigDecimal) {
					BigDecimal bigDecimal = (BigDecimal) warningList.get(i)[5];
					if (bigDecimal != null) {
						simpleWarning.setStatus(bigDecimal.intValue());
					}
				} else if (warningList.get(i)[5] instanceof BigInteger) {
					BigInteger bigInteger = (BigInteger) warningList.get(i)[5];
					if (bigInteger != null) {
						simpleWarning.setStatus(bigInteger.intValue());
					}
				} else if (warningList.get(i)[5] instanceof Integer) {
					simpleWarning.setStatus((Integer) warningList.get(i)[5]);
				}

				listWarning.add(simpleWarning);
			}
		}
		return listWarning;
	}

	public long queryAllRowCount(TrapWarningBean trapWarningBean) {
		Calendar calendar1 = null;
		Calendar calendar2 = null;
		if (trapWarningBean.getStartDate() != null) {
			calendar1 = this.setStartDate(trapWarningBean.getStartDate());

		}
		if (trapWarningBean.getEndDate() != null) {
			calendar2 = this.setEndDate(trapWarningBean.getEndDate());
		}
		try {
			String sql = "select count(*) from TrapWarningEntity trap ,warningType wt "
					+ "where wt.warningType=trap.warningType";
			if (trapWarningBean.getUserName() != null
					&& !trapWarningBean.getUserName().equals("")) {
				if (trapWarningBean.getUserName().equals("admin")) {

				} else {
					sql += " and INSTR(trap.users,:userName)<>0";
				}

			}

			if (trapWarningBean.getStartDate() != null) {
				sql += " and trap.sampleTime >=:startDate";
			}
			if (trapWarningBean.getEndDate() != null) {
				sql += " and trap.sampleTime <=:endDate";
			}
			if (trapWarningBean.getLevel() >= 0) {
				sql += " and wt.warningLevel=:level";
			}
			if (trapWarningBean.getIpValue() != null
					&& !trapWarningBean.getIpValue().equals("")) {
				sql += " and trap.ipValue like :ipValue";
			}
			if (trapWarningBean.getWarningType() >= 0) {

				sql += " and wt.warningType=:warningType";
			}
			if (trapWarningBean.getStatus() >= 0) {

				sql += " and trap.currentStatus=:status";
			}

			Query query = manager.createNativeQuery(sql);

			if (trapWarningBean.getStartDate() != null) {
				query.setParameter("startDate", calendar1.getTime());

			}
			if (trapWarningBean.getEndDate() != null) {
				query.setParameter("endDate", calendar2.getTime());
			}
			if (trapWarningBean.getLevel() >= 0) {
				query.setParameter("level", trapWarningBean.getLevel());

			}
			if (trapWarningBean.getIpValue() != null
					&& !trapWarningBean.getIpValue().equals("")) {
				query.setParameter("ipValue", "%"
						+ trapWarningBean.getIpValue() + "%");
			}
			if (trapWarningBean.getWarningType() >= 0) {
				query.setParameter("warningType", trapWarningBean
						.getWarningType());
			}
			if (trapWarningBean.getStatus() >= 0) {
				query.setParameter("status", trapWarningBean.getStatus());
			}
			if (trapWarningBean.getUserName() != null
					&& !trapWarningBean.getUserName().equals("")) {
				if (trapWarningBean.getUserName().equals("admin")) {

				} else {
					query.setParameter("userName", trapWarningBean
							.getUserName());
				}

			}

			if (query.getResultList() != null
					&& query.getResultList().size() != 0) {
				long size = 0;
				if (query.getResultList().get(0) instanceof BigDecimal) {
					BigDecimal bigDecimal = (BigDecimal) query.getResultList()
							.get(0);
					if (bigDecimal != null) {

						size = bigDecimal.longValue();
					}
				} else if (query.getResultList().get(0) instanceof BigInteger) {
					BigInteger bigInteger = (BigInteger) query.getResultList()
							.get(0);
					if (bigInteger != null) {
						size = bigInteger.longValue();
					}
				}
				return size;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return -1;
	}

	/**
	 * 查询一段时间不同级别的告警数量
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TrapCountBean> queryTrapWarningLevel(Date startDate, Date endDate) {

		List<TrapCountBean> countBeans = new ArrayList<TrapCountBean>();
		try {
			Calendar calendar1 = null;
			Calendar calendar2 = null;
			if (startDate != null) {
				calendar1 = this.setStartDate(startDate);

			}
			if (endDate != null) {
				calendar2 = this.setEndDate(endDate);
			}

			String sql = "select count(wt.id),l.warningLevel from  WarningEntity wt "
				+ " right join warningLevel l on l.warningLevel=wt.warningLevel and wt.currentStatus=1 ";
			if (startDate != null) {
				sql += " and wt.createDate >=:startDate";
			}
			if (endDate != null) {
				sql += " and wt.createDate <=:endDate";
			}
		
			sql += " where 1=1 group by l.warningLevel";
			Query query = manager.createNativeQuery(sql);
			System.out.println(sql);
			if (startDate != null) {
				query.setParameter("startDate", calendar1.getTime());
			}
			if (endDate != null) {
				query.setParameter("endDate", calendar2.getTime());
			}
			
			if (query.getResultList() != null
					&& query.getResultList().size() > 0) {
				List<Object[]> listObj = query.getResultList();
				for (int i = 0; i < listObj.size(); i++) {
					TrapCountBean countBean = new TrapCountBean();
					if (listObj.get(i)[0] instanceof BigDecimal) {
						BigDecimal bigDecimal = (BigDecimal) listObj.get(i)[0];
						if (bigDecimal != null) {
							countBean.setCount(bigDecimal.longValue());
						}
					} else if (listObj.get(i)[0] instanceof BigInteger) {
						BigInteger bigInteger = (BigInteger) listObj.get(i)[0];
						if (bigInteger != null) {
							countBean.setCount(bigInteger.longValue());
						}
					}

					if (listObj.get(i)[1] instanceof BigDecimal) {
						BigDecimal bigDecimal = (BigDecimal) listObj.get(i)[1];
						if (bigDecimal != null) {
							countBean.setWarningLevel(bigDecimal.intValue());
						}
					} else if (listObj.get(i)[1] instanceof BigInteger) {
						BigInteger bigInteger = (BigInteger) listObj.get(i)[1];
						if (bigInteger != null) {
							countBean.setWarningLevel(bigInteger.intValue());
						}
					} else if (listObj.get(i)[1] instanceof Integer) {
						countBean.setWarningLevel((Integer) listObj.get(i)[1]);
					}
					countBeans.add(countBean);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return countBeans;
	}

	/**
	 * 根据告警类别进行统计(设备告警,端口告警,协议告警,性能告警,网管告警)
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<TrapCountBean> queryTrapWarningCategory(Date startDate, Date endDate) {

		List<TrapCountBean> countBeans = new ArrayList<TrapCountBean>();
		try {
			Calendar calendar1 = null;
			Calendar calendar2 = null;
			if (startDate != null) {
				calendar1 = this.setStartDate(startDate);

			}
			if (endDate != null) {
				calendar2 = this.setEndDate(endDate);
			}

			String sql = "select  count(t.id),wc.warningCategory from warningcategory wc left join WarningEntity t on wc.warningCategory = t.warningCategory and t.currentStatus=1 ";
			if (startDate != null) {
				sql += " and t.createDate >=:startDate";
			}
			if (endDate != null) {
				sql += " and t.createDate <=:endDate";
			}
			sql += " where 1=1  group by wc.warningCategory";
			Query query = manager.createNativeQuery(sql);
			if (startDate != null) {
				query.setParameter("startDate", calendar1.getTime());
			}
			if (endDate != null) {
				query.setParameter("endDate", calendar2.getTime());
			}
			if (query.getResultList() != null
					&& query.getResultList().size() > 0) {
				List<Object[]> listObj = query.getResultList();
				for (int i = 0; i < listObj.size(); i++) {
					TrapCountBean countBean = new TrapCountBean();
					if (listObj.get(i)[0] instanceof BigDecimal) {
						BigDecimal bigDecimal = (BigDecimal) listObj.get(i)[0];
						if (bigDecimal != null) {
							countBean.setCount(bigDecimal.longValue());
						}
					} else if (listObj.get(i)[0] instanceof BigInteger) {
						BigInteger bigInteger = (BigInteger) listObj.get(i)[0];
						if (bigInteger != null) {
							countBean.setCount(bigInteger.longValue());
						}
					}

					if (listObj.get(i)[1] instanceof BigDecimal) {
						BigDecimal bigDecimal = (BigDecimal) listObj.get(i)[1];
						if (bigDecimal != null) {
							countBean.setWarningLevel(bigDecimal.intValue());
						}
					} else if (listObj.get(i)[1] instanceof BigInteger) {
						BigInteger bigInteger = (BigInteger) listObj.get(i)[1];
						if (bigInteger != null) {
							countBean.setWarningLevel(bigInteger.intValue());
						}
					} else if (listObj.get(i)[1] instanceof Integer) {
						countBean.setWarningLevel((Integer) listObj.get(i)[1]);
					}
					countBeans.add(countBean);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return countBeans;

	}
	
	public List<TrapCountBean> queryTrapWarningByMonth5(Date date){
		List<TrapCountBean> countBeans = new ArrayList<TrapCountBean>();
		try {
			Calendar calendar = null;

			if (date != null) {
				calendar = this.setStartDate(date);

			}

			String sql = "select count(t.id),month(t.createDate),year(t.createDate) from  WarningEntity t where t.currentStatus=1 ";
			if (date != null) {
				sql += " and t.createDate >=:date";
			}

			sql += " group by month(t.createDate),year(t.createDate)  order by year(t.createDate),month(t.createDate) desc ";
			
			Query query = manager.createNativeQuery(sql);
			query.setFirstResult(0);
			query.setMaxResults(5);
			
			if (date != null) {
				query.setParameter("date", calendar.getTime());
			}

			if (query.getResultList() != null
					&& query.getResultList().size() > 0) {
				List<Object[]> listObj = query.getResultList();
				for (int i = 0; i < listObj.size(); i++) {
					TrapCountBean countBean = new TrapCountBean();
					if (listObj.get(i)[0] instanceof BigDecimal) {
						BigDecimal bigDecimal = (BigDecimal) listObj.get(i)[0];
						if (bigDecimal != null) {
							countBean.setCount(bigDecimal.longValue());
						}
					} else if (listObj.get(i)[0] instanceof BigInteger) {
						BigInteger bigInteger = (BigInteger) listObj.get(i)[0];
						if (bigInteger != null) {
							countBean.setCount(bigInteger.longValue());
						}
					} else if (listObj.get(i)[0] instanceof Long) {
						countBean.setCount((Long) listObj.get(i)[0]);
					}

					if (listObj.get(i)[1] instanceof BigDecimal) {
						BigDecimal bigDecimal = (BigDecimal) listObj.get(i)[1];
						if (bigDecimal != null) {
							countBean.setMonth(bigDecimal.intValue());
						}
					} else if (listObj.get(i)[1] instanceof BigInteger) {
						BigInteger bigInteger = (BigInteger) listObj.get(i)[1];
						if (bigInteger != null) {
							countBean.setMonth(bigInteger.intValue());
						}
					} else if (listObj.get(i)[1] instanceof Integer) {
						countBean.setMonth((Integer) listObj.get(i)[1]);
					}
					if (listObj.get(i)[2] instanceof BigDecimal) {
						BigDecimal bigDecimal = (BigDecimal) listObj.get(i)[2];
						if (bigDecimal != null) {
							countBean.setYear(bigDecimal.intValue());
						}
					} else if (listObj.get(i)[2] instanceof BigInteger) {
						BigInteger bigInteger = (BigInteger) listObj.get(i)[2];
						if (bigInteger != null) {
							countBean.setYear(bigInteger.intValue());
						}
					} else if (listObj.get(i)[2] instanceof Integer) {
						countBean.setYear((Integer) listObj.get(i)[2]);
					}
					countBeans.add(countBean);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return countBeans;
	}

	public List<TrapCountBean> queryTrapWarningMonth(Date startDate,
			Date endDate) {

		List<TrapCountBean> countBeans = new ArrayList<TrapCountBean>();
		try {
			Calendar calendar1 = null;
			Calendar calendar2 = null;
			if (startDate != null) {
				calendar1 = this.setStartDate(startDate);

			}
			if (endDate != null) {
				calendar2 = this.setEndDate(endDate);
			}

			String sql = "select count(t.id),month(t.createDate),year(t.createDate) from  WarningEntity t where 1=1 ";
			if (startDate != null) {
				sql += " and t.createDate >=:startDate";
			}
			if (endDate != null) {
				sql += " and t.createDate <=:endDate";
			}
			sql += " group by month(t.createDate),year(t.createDate)  order by year(t.createDate),month(t.createDate) asc ";
			Query query = manager.createQuery(sql); 
			
			if (startDate != null) {
				query.setParameter("startDate", calendar1.getTime());
			}
			if (endDate != null) {
				query.setParameter("endDate", calendar2.getTime());
			}
			if (query.getResultList() != null
					&& query.getResultList().size() > 0) {
				List<Object[]> listObj = query.getResultList();
				for (int i = 0; i < listObj.size(); i++) {
					TrapCountBean countBean = new TrapCountBean();
					if (listObj.get(i)[0] instanceof BigDecimal) {
						BigDecimal bigDecimal = (BigDecimal) listObj.get(i)[0];
						if (bigDecimal != null) {
							countBean.setCount(bigDecimal.longValue());
						}
					} else if (listObj.get(i)[0] instanceof BigInteger) {
						BigInteger bigInteger = (BigInteger) listObj.get(i)[0];
						if (bigInteger != null) {
							countBean.setCount(bigInteger.longValue());
						}
					} else if (listObj.get(i)[0] instanceof Long) {
						countBean.setCount((Long) listObj.get(i)[0]);
					}

					if (listObj.get(i)[1] instanceof BigDecimal) {
						BigDecimal bigDecimal = (BigDecimal) listObj.get(i)[1];
						if (bigDecimal != null) {
							countBean.setMonth(bigDecimal.intValue());
						}
					} else if (listObj.get(i)[1] instanceof BigInteger) {
						BigInteger bigInteger = (BigInteger) listObj.get(i)[1];
						if (bigInteger != null) {
							countBean.setMonth(bigInteger.intValue());
						}
					} else if (listObj.get(i)[1] instanceof Integer) {
						countBean.setMonth((Integer) listObj.get(i)[1]);
					}
					if (listObj.get(i)[2] instanceof BigDecimal) {
						BigDecimal bigDecimal = (BigDecimal) listObj.get(i)[2];
						if (bigDecimal != null) {
							countBean.setYear(bigDecimal.intValue());
						}
					} else if (listObj.get(i)[2] instanceof BigInteger) {
						BigInteger bigInteger = (BigInteger) listObj.get(i)[2];
						if (bigInteger != null) {
							countBean.setYear(bigInteger.intValue());
						}
					} else if (listObj.get(i)[2] instanceof Integer) {
						countBean.setYear((Integer) listObj.get(i)[2]);
					}
					countBeans.add(countBean);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return countBeans;

	}

	public List<TrapCountBean> queryTrapWarningTop10(Date startDate,
			Date endDate) {
		List<TrapCountBean> countBeans = new ArrayList<TrapCountBean>();
		try {
			Calendar calendar1 = null;
			Calendar calendar2 = null;
			if (startDate != null) {
				calendar1 = this.setStartDate(startDate);

			}
			if (endDate != null) {
				calendar2 = this.setEndDate(endDate);
			}

			String sql = "select count(t.id),t.ipValue from WarningEntity t where t.currentStatus=1 ";
			if (startDate != null) {
				sql += " and t.createDate >=:startDate";
			}
			if (endDate != null) {
				sql += " and t.createDate <=:endDate";
			}
			sql += " group by t.ipValue  order by count(t.id) desc";
			Query query = manager.createNativeQuery(sql);
			query.setFirstResult(0);
			query.setMaxResults(10);
			if (startDate != null) {
				query.setParameter("startDate", calendar1.getTime());
			}
			if (endDate != null) {
				query.setParameter("endDate", calendar2.getTime());
			}
			if (query.getResultList() != null
					&& query.getResultList().size() > 0) {
				List<Object[]> listObj = query.getResultList();
				for (int i = 0; i < listObj.size(); i++) {
					TrapCountBean countBean = new TrapCountBean();
					if (listObj.get(i)[0] instanceof BigDecimal) {
						BigDecimal bigDecimal = (BigDecimal) listObj.get(i)[0];
						if (bigDecimal != null) {
							countBean.setCount(bigDecimal.longValue());
						}
					} else if (listObj.get(i)[0] instanceof BigInteger) {
						BigInteger bigInteger = (BigInteger) listObj.get(i)[0];
						if (bigInteger != null) {
							countBean.setCount(bigInteger.longValue());
						}
					}
					if (listObj.get(i)[1] instanceof String) {

						countBean.setIpValue((String) listObj.get(i)[1]);
					}

					countBeans.add(countBean);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return countBeans;
	}

	@SuppressWarnings("unchecked")
	public List<UserEntity> findUsersByArea(AreaEntity area) {
		List<UserEntity> users = new ArrayList<UserEntity>();
		String jpql0 = "select entity from " + UserEntity.class.getName()
				+ " as entity where entity.userName='admin'";
		Query query0 = manager.createQuery(jpql0);
		UserEntity user0 = (UserEntity) query0.getResultList().get(0);
		if (user0 != null) {
			users.add(user0);
		}

		String jpql = "select entity from " + UserEntity.class.getName()
				+ " as entity where :area member of entity.areas";
		Query query = manager.createQuery(jpql);
		if (area != null && !area.equals("")) {
			query.setParameter("area", area);
			List<UserEntity> datas1 = query.getResultList();
			for (UserEntity ue : datas1) {
				String name = ue.getUserName();
				if (name.equals("admin")) {
					datas1.remove(ue);
					break;
				}
			}
			if (datas1 != null && datas1.size() != 0) {
				users.addAll(datas1);
			}
		}

		AreaEntity superarea = area.getSuperArea();
		if (superarea != null) {
			String jpql2 = "select entity from " + UserEntity.class.getName()
					+ " as entity where :area member of entity.areas";
			Query query2 = manager.createQuery(jpql2);
			query2.setParameter("area", superarea);
			List<UserEntity> datas2 = query.getResultList();
			boolean contains = false;
			if (datas2 != null && datas2.size() != 0) {
				for (UserEntity u : datas2) {
					for (UserEntity u1 : users) {
						if (u.getUserName().equals(u1.getUserName())) {
							contains = true;
							break;
						}
					}
					if (!contains) {
						users.add(u);
					}
				}
				// users.addAll(datas2);
			}
		}
		return users;
	}

	@SuppressWarnings("unchecked")
	public List<LogEntity> queryLogEntity(Date startDate, Date endDate,
			String userName, String ip, int maxPageSize, int startPage) {
		Calendar calendar1 = null;
		Calendar calendar2 = null;
		if (startDate != null) {
			calendar1 = this.setStartDate(startDate);

		}
		if (endDate != null) {
			calendar2 = this.setEndDate(endDate);
		}
		String sql = "select c from LogEntity c where 1=1";
		if (startDate != null) {
			sql += " and c.doTime >=:startDate";
		}
		if (endDate != null) {
			sql += " and c.doTime <=:endDate";
		}
		if (userName != null && !userName.equals("")) {

			sql += " and c.userName=:userName";
		}
		if (ip != null && !ip.equals("")) {
			sql += " and c.clientIp=:ip";

		}
		sql += " order by c.doTime desc";
		Query query = manager.createQuery(sql);
		if (startPage > 1) {
			query.setFirstResult((startPage - 1) * maxPageSize);
		} else if (startPage == 1) {
			query.setFirstResult(0);
		}

		if (maxPageSize > 0) {
			query.setMaxResults(maxPageSize);
		}
		if (startDate != null) {

			query.setParameter("startDate", calendar1.getTime());

		}
		if (endDate != null) {
			query.setParameter("endDate", calendar2.getTime());

		}
		if (userName != null && !userName.equals("")) {

			query.setParameter("userName", userName);
		}
		if (ip != null && !ip.equals("")) {

			query.setParameter("ip", ip);
		}
		List<LogEntity> list = null;
		try {
			list = query.getResultList();
		} catch (Exception e) {
			list = null;
		}
		return list;
	}

	public long queryAllRowCount(Date startDate, Date endDate, String userName,
			String ip) {

		Calendar calendar1 = null;
		Calendar calendar2 = null;
		if (startDate != null) {
			calendar1 = this.setStartDate(startDate);

		}
		if (endDate != null) {
			calendar2 = this.setEndDate(endDate);
		}
		String sql = "select count(c)  from LogEntity c where 1=1";
		if (startDate != null) {
			sql += " and c.doTime >=:startDate";
		}
		if (endDate != null) {
			sql += " and c.doTime <=:endDate";
		}
		if (userName != null && !userName.equals("")) {

			sql += " and c.userName=:userName";
		}
		if (ip != null && !ip.equals("")) {
			sql += " and c.clientIp=:ip";

		}

		Query query = manager.createQuery(sql);
		if (startDate != null) {
			query.setParameter("startDate", calendar1.getTime());

		}
		if (endDate != null) {
			query.setParameter("endDate", calendar2.getTime());

		}
		if (userName != null && !userName.equals("")) {

			query.setParameter("userName", userName);
		}
		if (ip != null && !ip.equals("")) {

			query.setParameter("ip", ip);
		}
		try {
			if (query.getResultList() != null
					&& query.getResultList().size() != 0) {
				Long size = null;
				size = (Long) query.getResultList().get(0);
				return size;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;

	}

	public void clearLogEntity() {
		try {
			String sql = "TRUNCATE table LogEntity";
			Query query = manager.createNativeQuery(sql);
			query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clearLinkEntity() {

		try {
			String sql = "TRUNCATE TABLE TOPLINK";
			Query query = manager.createNativeQuery(sql);
			query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public List<WarningRecord> queryWarningRecord(Date startDate, Date endDate,
			String userName, String ip, int maxPageSize, int startPage) {

		Calendar calendar1 = null;
		Calendar calendar2 = null;
		if (startDate != null) {
			calendar1 = this.setStartDate(startDate);

		}
		if (endDate != null) {
			calendar2 = this.setEndDate(endDate);
		}
		String sql = "select c from WarningRecord c where 1=1";

		if (startDate != null) {
			sql += " and c.time>=:startDate";
		}
		if (endDate != null) {
			sql += " and c.time <=:endDate";

		}
		if (userName != null && !userName.equals("")) {

			sql += " and c.personInfo.name=:userName";
		}
		if (ip != null && !ip.equals("")) {
			sql += " and c.ipValue like :ip";

		}

		sql += " order by c.time desc";
		Query query = manager.createQuery(sql);
		if (startPage > 1) {
			query.setFirstResult((startPage - 1) * maxPageSize);
		} else if (startPage == 1) {
			query.setFirstResult(0);
		}

		if (maxPageSize > 0) {
			query.setMaxResults(maxPageSize);
		}
		if (startDate != null) {
			query.setParameter("startDate", calendar1.getTime());

		}
		if (endDate != null) {
			query.setParameter("endDate", calendar2.getTime());

		}
		if (userName != null && !userName.equals("")) {

			query.setParameter("userName", userName);
		}
		if (ip != null && !ip.equals("")) {

			query.setParameter("ip", "%" + ip + "%");
		}
		List<WarningRecord> list = null;
		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

	public long queryAllWarningCount(Date startDate, Date endDate,
			String userName, String ip) {
		Calendar calendar1 = null;
		Calendar calendar2 = null;
		if (startDate != null) {
			calendar1 = this.setStartDate(startDate);

		}
		if (endDate != null) {
			calendar2 = this.setEndDate(endDate);
		}
		String sql = "select count(c)  from WarningRecord c where 1=1";

		if (startDate != null) {
			sql += " and c.time >=:startDate";

		}
		if (endDate != null) {
			sql += " and c.time <=:endDate";
		}
		if (userName != null && !userName.equals("")) {

			sql += " and c.personInfo.name=:userName";
		}
		if (ip != null && !ip.equals("")) {
			sql += " and c.ipValue like :ip";

		}
		Query query = manager.createQuery(sql);
		if (startDate != null) {
			query.setParameter("startDate", calendar1.getTime());

		}
		if (endDate != null) {
			query.setParameter("endDate", calendar2.getTime());

		}
		if (userName != null && !userName.equals("")) {

			query.setParameter("userName", userName);
		}
		if (ip != null && !ip.equals("")) {

			query.setParameter("ip", "%" + ip + "%");
		}
		try {
			if (query.getResultList() != null
					&& query.getResultList().size() != 0) {
				Long size = (Long) query.getResultList().get(0);
				return size;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;

	}

	public void clearWarningRecode() {
		try {
			String sql = "TRUNCATE table WarningRecord";
			Query query = manager.createNativeQuery(sql);
			query.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<RmonCount> queryRmonCount(Date startDate, Date endDate,
			String ipValue, int portNo, String param) {
		Calendar calendar1 = null;
		Calendar calendar2 = null;
		List<RmonCount> list = null;
		try {
			list = new ArrayList<RmonCount>();
			if (startDate != null) {
				calendar1 = this.setStartDate(startDate);
			}
			if (endDate != null) {
				calendar2 = this.setEndDate(endDate);
			}
			String sql = "select distinct c from RmonCount c where 1=1 ";
			if (startDate != null) {
				sql += " and c.sampleTime >=:startDate";
			}
			if (endDate != null) {
				sql += " and c.sampleTime <=:endDate";
			}
			if (ipValue != null && !ipValue.equals("")) {
				sql += " and c.ipValue like :ipValue";
			}
			if (portNo > -1) {
				sql += " and c.portNo=:portNo";
			}
			if (param != null && !param.equals("")) {
				sql += " and c.param=:param";
			}
			sql += " order by c.sampleTime asc";
			Query query = manager.createQuery(sql);

			if (startDate != null) {
				query.setParameter("startDate", calendar1.getTime());

			}
			if (endDate != null) {
				query.setParameter("endDate", calendar2.getTime());
			}
			if (ipValue != null && !ipValue.equals("")) {
				query.setParameter("ipValue", "%" + ipValue + "%");
			}
			if (portNo > -1) {
				query.setParameter("portNo", portNo);
			}
			if (param != null && !param.equals("")) {
				query.setParameter("param", param);
			}
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

	public List<RmonCount> queryRmonCount(Date startDate, Date endDate,
			String ipValue, int portNo, int maxPageSize, int startPage) {
		Calendar calendar1 = null;
		Calendar calendar2 = null;
		List<RmonCount> list = null;
		try {
			list = new ArrayList<RmonCount>();
			if (startDate != null) {
				calendar1 = this.setStartDate(startDate);
			}
			if (endDate != null) {
				calendar2 = this.setEndDate(endDate);
			}
			String sql = "select distinct c from RmonCount c where 1=1 ";
			if (startDate != null) {
				sql += " and c.sampleTime >=:startDate";
			}
			if (endDate != null) {
				sql += " and c.sampleTime <=:endDate";
			}
			if (ipValue != null && !ipValue.equals("")) {
				sql += " and c.ipValue like :ipValue";
			}
			if (portNo > -1) {
				sql += " and c.portNo=:portNo";
			}
			sql += " order by c.sampleTime desc";
			Query query = manager.createQuery(sql);
			if (startPage > 1) {
				query.setFirstResult((startPage - 1) * maxPageSize);
			} else if (startPage == 1) {
				query.setFirstResult(0);
			}
			if (maxPageSize > 0) {
				query.setMaxResults(maxPageSize);
			}
			if (startDate != null) {
				query.setParameter("startDate", calendar1.getTime());

			}
			if (endDate != null) {
				query.setParameter("endDate", calendar2.getTime());
			}
			if (ipValue != null && !ipValue.equals("")) {
				query.setParameter("ipValue", "%" + ipValue + "%");
			}
			if (portNo > -1) {
				query.setParameter("portNo", portNo);
			}

			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;

	}

	public long queryAllRmonCountNum(Date startDate, Date endDate,
			String ipValue, int portNo) {

		Calendar calendar1 = null;
		Calendar calendar2 = null;
		try {
			if (startDate != null) {
				calendar1 = this.setStartDate(startDate);
			}
			if (endDate != null) {
				calendar2 = this.setEndDate(endDate);
			}
			String sql = "select count(c) from RmonCount c where 1=1 ";
			if (startDate != null) {
				sql += " and c.sampleTime >=:startDate";
			}
			if (endDate != null) {
				sql += " and c.sampleTime <=:endDate";
			}
			if (ipValue != null && !ipValue.equals("")) {
				sql += " and c.ipValue like :ipValue";
			}
			if (portNo > -1) {
				sql += " and c.portNo=:portNo";
			}

			Query query = manager.createQuery(sql);
			if (startDate != null) {
				query.setParameter("startDate", calendar1.getTime());

			}
			if (endDate != null) {
				query.setParameter("endDate", calendar2.getTime());
			}
			if (ipValue != null && !ipValue.equals("")) {
				query.setParameter("ipValue", "%" + ipValue + "%");
			}
			if (portNo > -1) {
				query.setParameter("portNo", portNo);
			}
			Long size = null;
			if (query.getResultList() != null
					&& query.getResultList().size() != 0) {
				size = (Long) query.getResultList().get(0);
				return size;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public List<TimerMonitoringSheet> queryMonitoringSheet(Date startDate,
			Date endDate, String ipValue, int portNo, int maxPageSize,
			int startPage) {
		Calendar calendar1 = null;
		Calendar calendar2 = null;
		List<TimerMonitoringSheet> list = null;
		try {
			list = new ArrayList<TimerMonitoringSheet>();
			if (startDate != null) {
				calendar1 = this.setStartDate(startDate);
			}
			if (endDate != null) {
				calendar2 = this.setEndDate(endDate);
			}
			String sql = "select c from TimerMonitoringSheet c where 1=1 ";
			if (startDate != null) {
				sql += " and c.createDate >=:startDate";
			}
			if (endDate != null) {
				sql += " and c.createDate <=:endDate";
			}
			if (ipValue != null && !ipValue.equals("")) {
				sql += " and c.device like :ipValue";
			}
			if (portNo > -1) {
				sql += " and c.portNo=:portNo";
			}
			sql += " order by c.createDate desc";
			Query query = manager.createQuery(sql);
			if (startPage > 1) {
				query.setFirstResult((startPage - 1) * maxPageSize);
			} else if (startPage == 1) {
				query.setFirstResult(0);
			}
			if (maxPageSize > 0) {
				query.setMaxResults(maxPageSize);
			}
			if (startDate != null) {
				query.setParameter("startDate", calendar1.getTime());

			}
			if (endDate != null) {
				query.setParameter("endDate", calendar2.getTime());
			}
			if (ipValue != null && !ipValue.equals("")) {
				query.setParameter("ipValue", "%" + ipValue + "%");
			}
			if (portNo > -1) {
				query.setParameter("portNo", portNo);
			}

			list = query.getResultList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;

	}

	public long queryAllMonitoringSheetNum(Date startDate, Date endDate,
			String ipValue, int portNo) {

		Calendar calendar1 = null;
		Calendar calendar2 = null;
		try {
			if (startDate != null) {
				calendar1 = this.setStartDate(startDate);
			}
			if (endDate != null) {
				calendar2 = this.setEndDate(endDate);
			}
			String sql = "select count(c)  from TimerMonitoringSheet c where 1=1 ";
			if (startDate != null) {
				sql += " and c.createDate >=:startDate";
			}
			if (endDate != null) {
				sql += " and c.createDate <=:endDate";
			}
			if (ipValue != null && !ipValue.equals("")) {
				sql += " and c.device like :ipValue";
			}
			if (portNo > -1) {
				sql += " and c.portNo=:portNo";
			}

			Query query = manager.createQuery(sql);
			if (startDate != null) {
				query.setParameter("startDate", calendar1.getTime());

			}
			if (endDate != null) {
				query.setParameter("endDate", calendar2.getTime());
			}
			if (ipValue != null && !ipValue.equals("")) {
				query.setParameter("ipValue", "%" + ipValue + "%");
			}
			if (portNo > -1) {
				query.setParameter("portNo", portNo);
			}
			Long size = null;
			if (query.getResultList() != null
					&& query.getResultList().size() != 0) {
				size = (Long) query.getResultList().get(0);
				return size;
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return -1;
	}

	private Calendar setStartDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DATE), 0, 0, 0);
		return calendar;
	}

	private Calendar setEndDate(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
				calendar.get(Calendar.DATE), 23, 59, 59);
		return calendar;
	}

	@SuppressWarnings("unchecked")
	public List<FaultDetection> queryPingResult() {
		List<FaultDetection> pingResults = null;
		try {
			String sql = "select c from FaultDetection c ";
			Query query = manager.createQuery(sql);
			pingResults = query.getResultList();
		} catch (Exception e) {
			pingResults = null;
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pingResults;
	}

	// ====================================================Vlan and Ring
	@SuppressWarnings("unchecked")
	public List<VlanBean> querySwitchVlanInfo() {
		List<VlanBean> list = null;
		List<VlanBean> vlanBenasBeans = new ArrayList<VlanBean>();
		try {
			list = new ArrayList<VlanBean>();
			String sql = "select c.vlanID,c.vlanName from VlanEntity c  group by c.vlanID,c.vlanName order by c.issuedTag,c.vlanID asc";
			Query query = manager.createQuery(sql);
			manager.setFlushMode(FlushModeType.COMMIT);
			manager.flush();
			List<Object[]> listObj = query.getResultList();
			if (listObj != null && listObj.size() > 0) {
				for (int i = 0; i < listObj.size(); i++) {
					String vlanID = "";
					if (listObj.get(i)[0] instanceof BigDecimal) {
						BigDecimal bigDecimal = (BigDecimal) listObj.get(i)[0];
						if (bigDecimal != null) {
							vlanID = bigDecimal.intValue() + "";
						}
					} else if (listObj.get(i)[0] instanceof BigInteger) {
						BigInteger bigInteger = (BigInteger) listObj.get(i)[0];
						if (bigInteger != null) {
							vlanID = bigInteger.intValue() + "";
						}
					} else if (listObj.get(i)[0] instanceof Integer) {
						vlanID = listObj.get(i)[0] + "";
					}
					String vlanName = (String) listObj.get(i)[1];
					VlanBean vlanBean = new VlanBean();
					vlanBean.setVlanID(vlanID);
					vlanBean.setIssuedTag(Constants.ISSUEDADM);
					List<Integer> vlanIssueds =queryVlanIssued(Integer.parseInt(vlanID));
					if(vlanIssueds!=null){
					  a:for(Integer vlanIssued :vlanIssueds){
						   if(vlanIssued==Constants.ISSUEDADM){
							   vlanBean.setIssuedTag(Constants.ISSUEDADM);
							   break a;
						   }else{
							   vlanBean.setIssuedTag(Constants.ISSUEDDEVICE);
						   }
					   }
					}
					vlanBean.setVlanName(vlanName);
					list.add(vlanBean);
				}
				for (VlanBean bean : list) {
					boolean flag = false;
					a: for (VlanBean vlanBean : vlanBenasBeans) {
						if ((bean.getVlanID()).equals(vlanBean.getVlanID())) {
							flag = true;
							break a;
						}
					}
					if (!flag) {
						vlanBenasBeans.add(bean);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return vlanBenasBeans;
	}
    private  List<Integer> queryVlanIssued(int vlanID){
    	
    	String sql ="select c.issuedTag from  VlanEntity c where c.vlanID=:vlanID and c.vlanConfig is not null";
    	Query query =manager.createQuery(sql);
    	query.setParameter("vlanID", vlanID);
    	List<Integer> vlanIssueds =query.getResultList();
    	return vlanIssueds;
    }
	
	
	@SuppressWarnings("unchecked")
	public List<VlanEntity> queryVlanEntity(VlanBean vlanBean) {

		List<VlanEntity> list = null;
		try {
			if (vlanBean != null) {
				String sql = "select c from VlanEntity c left join fetch c.portConfig where c.vlanID=:vlanID and c.vlanConfig is not null";
				Query query = manager.createQuery(sql);
                if(!vlanBean.getVlanID().equals("")){
				query.setParameter("vlanID", Integer.parseInt(vlanBean
						.getVlanID()));
				list = query.getResultList();
                }
			}
		} catch (Exception e) {
			list = null;
			e.printStackTrace();
		}
		return list;

	}

	@SuppressWarnings("unchecked")
	public List<RingConfig> queryRingConfig(RingBean ringBean) {

		List<RingConfig> list = null;
		try {
			if (ringBean != null) {
				String sql = "select c from RingConfig c where c.ringID=:ringID and c.ringEnable=:ringEnable and c.switchNode is not null";
				Query query = manager.createQuery(sql);
				query.setParameter("ringID", ringBean.getRingID());
				query.setParameter("ringEnable", ringBean.isRingEnable());
				if (query.getResultList() != null
						&& query.getResultList().size() > 0) {
					list = query.getResultList();
				}
			}
		} catch (Exception e) {
			list = null;
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;

	}

	@SuppressWarnings("unchecked")
	public List<RingBean> queryRingConfig() {
		String sql = "select  c.ringID,c.ringEnable from RingConfig c group by c.ringID,c.ringEnable  order by c.issuedTag,c.ringID asc";
		Query query = manager.createQuery(sql);
		List<Object[]> list = query.getResultList();
		List<RingBean> ringBeans = new ArrayList<RingBean>();
		List<RingBean> beans = new ArrayList<RingBean>();
		int ringID = 0;
		int ringType = 0;
		boolean flag=false;
		if (list != null && list.size() > 0) {

			for (Object[] object : list) {
				RingBean ringBean = new RingBean();
				if (object[0] instanceof Integer) {
					if (object[0] != null) {
						ringID = (Integer) object[0];
						ringBean.setRingID(ringID);
					}

				} 
				if (object[1] instanceof Boolean) {
					if (object[1] != null) {
						ringBean.setRingEnable((Boolean) object[1]);
					}
				}
				ringBean.setRingName("");
				ringBean.setIssuedTag(Constants.ISSUEDADM);
				ringBeans.add(ringBean);
				List<RingConfig> configs = this.queryRingConfig(ringBean);
				if (configs != null && configs.size() > 0) {
					a:for (RingConfig config : configs) {
						ringType = config.getRingType();
						if (config.getIssuedTag() == Constants.ISSUEDADM) {
							flag = true;
							break a;
						}else{
							flag =false;
						}
					}
					for (RingBean ring : ringBeans) {
						if ((ring.getRingID()) == (configs.get(0).getRingID())&& ring.isRingEnable() == configs.get(0).isRingEnable()) {
							ring.setRingType(ringType);
							if (!flag) {
								ring.setIssuedTag(Constants.ISSUEDDEVICE);
							}else{
								ring.setIssuedTag(Constants.ISSUEDADM);
							}
							beans.add(ring);
							break;
						}
					}

				} else  {
					
					List<RingConfig> listNull =(List<RingConfig>) commonService.findAll(RingConfig.class, " where entity.ringID='"+ringBean.getRingID()+"' and entity.switchNode is null");
					if(listNull!=null){
						ringBean.setRingType(listNull.get(0).getRingType());
					}
					beans.add(ringBean);
				}

			}

			return beans;
		} else {

			return null;
		}

	}

	public List<RingInfo> queryAllRingInfo() {
		List<RingBean> listBean = this.queryRingConfig();
		List<RingInfo> allRingInfo = new ArrayList<RingInfo>();
		if (listBean != null && listBean.size() > 0) {
			for (RingBean ringBean : listBean) {
				List<RingInfo> singleRingInfo = this.queryRingInfo(ringBean);
				if (singleRingInfo != null && singleRingInfo.size() > 0) {
					allRingInfo.addAll(singleRingInfo);
				}
			}

		}
		return allRingInfo;
	}

	public List<RingInfo> queryRingInfo(RingBean ringBean) {
		List<RingConfig> configs = this.queryRingConfig(ringBean);
		List<RingInfo> ringInfos = null;
		try {
			if (configs != null && configs.size() > 0) {
				ringInfos = new ArrayList<RingInfo>();
				for (RingConfig config : configs) {
					SwitchNodeEntity switchNodeEntity = config.getSwitchNode();
					if (switchNodeEntity != null) {
						SwitchBaseConfig baseConfig = switchNodeEntity
								.getBaseConfig();
						String ip = baseConfig.getIpValue();
						String port1 = config.getPort1() + ",";
						String port2 = port1 + config.getPort2();
						RingInfo ringInfo = new RingInfo();
						ringInfo.setPort1(config.getPort1());
						ringInfo.setPort2(config.getPort2());
						ringInfo.setPort1Type(config.getPort1Type());
						ringInfo.setPort2Type(config.getPort2Type());
						ringInfo.setIp(ip);
						ringInfo.setPorts(port2);
						ringInfo.setRingType(config.getRingType());
						ringInfo.setSysType(config.getSystemType());
						ringInfos.add(ringInfo);
					}

				}

			}

			if (ringInfos != null && ringInfos.size() > 0) {

				return ringInfos;
			} else {
				return null;
			}

		} catch (Exception e) {
			return null;
			// TODO: handle exception
		}

	}

	public List<SwitchVlanPortInfo> querySwitchVlanPortInfo(VlanBean vlanBean) {

		List<SwitchVlanPortInfo> switchVlanPortInfos = null;
		SwitchVlanPortInfo info =null;
		try {
			if(vlanBean!=null){
			Object[] params = { vlanBean.getVlanID() };
			List<VlanPortConfig> list = (List<VlanPortConfig>) commonService.findAll(VlanPortConfig.class, "where entity.vlanID=? ",params);
			Set<String> ipValues = new HashSet<String>();
			if (list != null && list.size() > 0) {
				switchVlanPortInfos = new ArrayList<SwitchVlanPortInfo>();
				for (VlanPortConfig vlanPortConfig : list) {
					ipValues.add(vlanPortConfig.getIpVlue());
				}
				for (String ipValue : ipValues) {
					info = new SwitchVlanPortInfo();
					info.setIpVlue(ipValue);
					String tagPort = "";
					String untagPort = "";
					for (VlanPortConfig vlanPortConfig : list) {
						if (vlanPortConfig.getIpVlue().equals(ipValue)) {
							if (vlanPortConfig.getPortTag() == 'T') {
								tagPort += vlanPortConfig.getPortNo() + ",";
							} else {
								untagPort += vlanPortConfig.getPortNo() + ",";
							}
						}
					}
					if (tagPort.length() > 0) {
						info.setTagPort(tagPort.substring(0,tagPort.length() - 1));
					}
					if (untagPort.length() > 0) {
						info.setUntagPort(untagPort.substring(0, untagPort.length() - 1));
					}
					switchVlanPortInfos.add(info);
				}
			}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return switchVlanPortInfos;
	}

	public void deleteRmonCount() {

		try {
			Calendar calendar = Calendar.getInstance();
			calendar.set(calendar.get(Calendar.YEAR), calendar
					.get(Calendar.MONTH), 1);
			Date oldDate = calendar.getTime();
			Date nowDate = new Date();
			Calendar calendar1 = this.setStartDate(oldDate);
			Calendar calendar2 = this.setEndDate(nowDate);
			String sql = "delete from RmonCount where id not in (select c.id from RmonCount c where c.sampleTime >=:oldDate and c.sampleTime <=:nowDate)";
			Query query = manager.createQuery(sql);
			query.setParameter("oldDate", calendar1.getTime());
			query.setParameter("nowDate", calendar2.getTime());
			query.executeUpdate();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void deleteTrapWarningInfo() {

		try {
			Calendar calendar = Calendar.getInstance();
			calendar.set(calendar.get(Calendar.YEAR), calendar
					.get(Calendar.MONTH), 1);
			Date oldDate = calendar.getTime();
			Date nowDate = new Date();
			Calendar calendar1 = this.setStartDate(oldDate);
			Calendar calendar2 = this.setEndDate(nowDate);
			String sql = "delete from TrapWarningEntity where id not in (select c.id from TrapWarningEntity c where c.sampleTime >=:oldDate and c.sampleTime <=:nowDate)";
			Query query = manager.createQuery(sql);
			query.setParameter("oldDate", calendar1.getTime());
			query.setParameter("nowDate", calendar2.getTime());
			query.executeUpdate();

			String sql1 = "delete from RmonEntity where id not in (select c.id from RmonEntity c where c.createDate >=:oldDate and c.createDate <=:nowDate)";
			Query query1 = manager.createQuery(sql1);
			query1.setParameter("oldDate", calendar1.getTime());
			query1.setParameter("nowDate", calendar2.getTime());
			query1.executeUpdate();

			String sql2 = "delete from WarningEntity where id not in (select c.id from WarningEntity c where c.createDate >=:oldDate and c.createDate <=:nowDate)";
			Query query2 = manager.createQuery(sql2);
			query2.setParameter("oldDate", calendar1.getTime());
			query2.setParameter("nowDate", calendar2.getTime());
			query2.executeUpdate();

			String sql3 = "delete from NoteEntity where id not in (select c.id from NoteEntity c where c.createDate >=:oldDate and c.createDate <=:nowDate)";
			Query query3 = manager.createQuery(sql3);
			query3.setParameter("oldDate", calendar1.getTime());
			query3.setParameter("nowDate", calendar2.getTime());
			query3.executeUpdate();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	public   SwitchBaseConfig getSwitchByIp(String ipvalue) {
		String jpql = "select entity from " + SwitchBaseConfig.class.getName()
				+ " as entity where entity.ipValue = ?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, ipvalue);
		List<SwitchBaseConfig> datas = query.getResultList();
		if (datas != null && datas.size() != 0) {
			return datas.get(0);
		} else {
			return null;
		}
	}

	private List<String> saveVlanServer(VlanBean vlanBean,List<VlanPortConfig> nowList) {
		List<String> strings = this.queryIPS(vlanBean.getVlanID());
		List<VlanEntity> list = this.queryVlanEntity(vlanBean);
		Set<VlanPortConfig> all = new HashSet<VlanPortConfig>();
		boolean flag = false;
		boolean vlanFlag = false;
		if (list != null && list.size() > 0) {
			for (VlanEntity vlanEntity : list) {
				Set<VlanPortConfig> configs = vlanEntity.getPortConfig();
				if (configs != null && configs.size() > 0) {
					all.addAll(configs);
				}
			}
			// 删除Vlan中的端口
			if (all.size() > 0) {
				for (VlanPortConfig portConfig : all) {
					String ipValue = portConfig.getIpVlue();
					int port = portConfig.getPortNo();
					String vlanID = portConfig.getVlanID();
					List<VlanPortConfig> newportConfigList = this.findVlanPortConfig(ipValue, port, vlanBean.getVlanID());
					VlanPortConfig newportConfig = null;
					if (newportConfigList != null&& newportConfigList.size() > 0) {
						newportConfig = newportConfigList.get(0);
					}

					if (nowList != null && nowList.size() > 0) {
						flag = false;
						a: for (VlanPortConfig configPortConfig : nowList) {
							configPortConfig.setVlanID(vlanBean.getVlanID());
							if (ipValue.equals(configPortConfig.getIpVlue())&& (port == configPortConfig.getPortNo())
									&& vlanID.equals(configPortConfig.getVlanID())) {
								flag = true;
								break a;
							}
						}
					}
					if (!flag) {
						SwitchNodeEntity nodeEntity = commonService.getSwitchByIp(ipValue);
						if (nodeEntity != null) {
							VlanConfig vlanConfig = this.findVlanConfig(nodeEntity);
							Set<VlanEntity> set = vlanConfig.getVlanEntitys();
							if (set != null && set.size() > 0) {
								a: for (VlanEntity vlanEntity : set) {
									if ((vlanEntity.getVlanID() + "").equals(vlanBean.getVlanID())) {
										if (newportConfig != null) {
											vlanEntity.getPortConfig().remove(newportConfig);
											vlanEntity.setIssuedTag(Constants.ISSUEDADM);
											manager.merge(vlanEntity);
											manager.remove(newportConfig);
											break a;
										}
									}
								}
							}
							List<VlanPortConfig> vlanPortConfigList = this.findVlanPortConfig(ipValue, -1, vlanID);
							if (vlanPortConfigList == null|| vlanPortConfigList.size() == 0) {
								List<IGMPEntity> igmpEntities = (List<IGMPEntity>) queryEntityBySwitch(IGMPEntity.class, nodeEntity);
								if (igmpEntities != null) {
									List<Igmp_vsi> igmpVsis = igmpEntities.get(0).getVlanIds();
									List<Igmp_vsi> delIgmpVsis = new ArrayList<Igmp_vsi>();
									if (igmpVsis != null) {
										for (Igmp_vsi igmpVsi : igmpVsis) {
											if (igmpVsi.getVlanId().equals(vlanBean.getVlanID())) {
												delIgmpVsis.add(igmpVsi);
											}
										}
										igmpEntities.get(0).getVlanIds().removeAll(delIgmpVsis);
										commonService.deleteEntities(delIgmpVsis);
									}
								}
							}

						} else {
							logger.error("Vlan 操作时 交换机为空");
						}
					}
				}
			}
		}
		// 查看是否有新增端口到Vlan中
		if (nowList != null && nowList.size() > 0) {
			for (VlanPortConfig vlanPortConfig1 : nowList) {
				String ipValue = vlanPortConfig1.getIpVlue();
				int port = vlanPortConfig1.getPortNo();
				vlanPortConfig1.setVlanID(vlanBean.getVlanID());
				List<VlanPortConfig> newportConfigList = this.findVlanPortConfig(ipValue, port, vlanBean.getVlanID());
				VlanPortConfig newportConfig = null;
				if (newportConfigList != null && newportConfigList.size() > 0) {
					newportConfig = newportConfigList.get(0);
				}
				if (newportConfig == null) {
					SwitchNodeEntity nodeEntity = commonService.getSwitchByIp(ipValue);
					VlanConfig vlanConfig = this.findVlanConfig(nodeEntity);
					if (vlanConfig == null) {
						
						VlanEntity	vlanEntity = new VlanEntity();
						vlanEntity.setVlanID(Integer.parseInt(vlanBean.getVlanID()));
						vlanEntity.setVlanName(vlanBean.getVlanName());
						vlanEntity.setIssuedTag(Constants.ISSUEDADM);
						vlanConfig = new VlanConfig();
						vlanConfig.setSwitchNode(nodeEntity);
						if (vlanConfig.getVlanEntitys() == null) {
							Set<VlanEntity> set = new HashSet<VlanEntity>();
							set.add(vlanEntity);
							vlanConfig.setVlanEntitys(set);
						} else {
							vlanConfig.getVlanEntitys().add(vlanEntity);
						}
						vlanEntity.setVlanConfig(vlanConfig);
						if (vlanEntity.getPortConfig() == null) {
							Set<VlanPortConfig> configs = new HashSet<VlanPortConfig>();
							configs.add(vlanPortConfig1);
							vlanEntity.setPortConfig(configs);
						} else {
							vlanEntity.getPortConfig().add(vlanPortConfig1);
						}
						manager.persist(vlanConfig);

					} else {
						Set<VlanEntity> set = vlanConfig.getVlanEntitys();
						if (set != null && set.size() > 0) {
							a: for (VlanEntity vlanEntity : set) {
								if ((vlanEntity.getVlanID() + "").trim().equals(vlanBean.getVlanID())) {
									manager.persist(vlanPortConfig1);
									if (vlanEntity.getPortConfig() == null) {
										Set<VlanPortConfig> configs = new HashSet<VlanPortConfig>();
										configs.add(vlanPortConfig1);
										vlanEntity.setPortConfig(configs);
									} else {
										Set<VlanPortConfig> setConfigs = vlanEntity.getPortConfig();
										setConfigs.add(vlanPortConfig1);
										vlanEntity.setPortConfig(setConfigs);
									}
									manager.merge(vlanEntity);
									vlanFlag = true;
									break a;
								} else {
									vlanFlag = false;
								}
							}
							if (!vlanFlag) {
								VlanEntity vlanEntity = new VlanEntity();
								vlanEntity.setVlanID(Integer.parseInt(vlanBean.getVlanID()));
								vlanEntity.setVlanName(vlanBean.getVlanName());
								vlanEntity.setIssuedTag(Constants.ISSUEDADM);
								if (vlanEntity.getPortConfig() == null) {
									Set<VlanPortConfig> configs = new HashSet<VlanPortConfig>();
									configs.add(vlanPortConfig1);
									vlanEntity.setPortConfig(configs);
								} else {
									vlanEntity.getPortConfig().add(vlanPortConfig1);
								}
								vlanEntity.setVlanConfig(vlanConfig);
								manager.persist(vlanEntity);
								vlanConfig.getVlanEntitys().add(vlanEntity);
								manager.merge(vlanConfig);

							}

						} else {
							
							VlanEntity vlanEntity = new VlanEntity();
							
							vlanEntity.setVlanID(Integer.parseInt(vlanBean.getVlanID()));
							vlanEntity.setIssuedTag(Constants.ISSUEDADM);
							vlanEntity.setVlanName(vlanBean.getVlanName());
							if (vlanEntity.getPortConfig() == null) {
								Set<VlanPortConfig> portConfigs = new HashSet<VlanPortConfig>();
								portConfigs.add(vlanPortConfig1);
								vlanEntity.setPortConfig(portConfigs);
							} else {
								vlanEntity.getPortConfig().add(vlanPortConfig1);
							}
							vlanEntity.setVlanConfig(vlanConfig);
							manager.persist(vlanEntity);
							Set<VlanEntity> setEntities = new HashSet<VlanEntity>();
							setEntities.add(vlanEntity);
							vlanConfig.setVlanEntitys(setEntities);
							manager.merge(vlanConfig);
						}

					}
				} else {
					newportConfig.setPortTag(vlanPortConfig1.getPortTag());
					manager.merge(newportConfig);
					if(list!=null&&list.size()>0){
						for(VlanEntity vlanEntity :list){
							if((vlanEntity.getVlanID()+"").equals(newportConfig.getVlanID())){
								vlanEntity.setIssuedTag(Constants.ISSUEDADM);
									manager.merge(vlanEntity);
								}							
						}
					}
				}

			}
		} 
		return strings;
	}
    
	
	
	public List<?> queryEntityBySwitch(Class<?> clazz, SwitchNodeEntity entity) {
		String jpql = "select entity from " + clazz.getName()
				+ " as entity where entity.switchNode=?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, entity);
		List<?> datas = query.getResultList();
		return datas;
	}


	private boolean saveVlanFep(VlanBean vlanBean,
			List<VlanPortConfig> nowList, String clientIP,
			List<String> strings, String userName) throws JMSException {

		boolean flag = false;
		String ips = "";
		if (strings != null && strings.size() > 0) {
			for (String str : strings) {
				flag = true;
				ips += str + ",";
			}
		} else {
			flag = false;
		}
		VlanEntity vlanEntity = new VlanEntity();
		vlanEntity.setVlanID(Integer.parseInt(vlanBean.getVlanID()));
		if (vlanEntity.getPortConfig() == null) {
			Set<VlanPortConfig> setport = new HashSet<VlanPortConfig>();
			setport.addAll(nowList);
			vlanEntity.setPortConfig(setport);
		}

		if (flag) {
			sendMessageService
					.sendVlanMessage(ips, MessageNoConstants.ALL_VLAN,
							userName, clientIP, vlanEntity);
			flag = true;

		} else {
			if (nowList != null && nowList.size() > 0) {
				for (VlanPortConfig config : nowList) {
					ips += config.getIpVlue() + ",";

				}
				sendMessageService.sendVlanMessage(ips,
						MessageNoConstants.ALL_VLAN, userName, clientIP,
						vlanEntity);
				flag = true;
			} else {
				flag = false;
			}

		}

		return flag;
	}

	public boolean saveVlan(VlanBean vlanBean, List<VlanPortConfig> nowList,
			String clientIP, int type, String userName) {
		boolean flag = false;
		try {
			if (type == Constants.SYN_ALL) {
				List<String> oldDelIps = this.saveVlanServer(vlanBean, nowList);
				if (nowList.size() == 0 && oldDelIps.size() == 0) {
					flag = false;
				} else {
					flag = this.saveVlanFep(vlanBean, nowList, clientIP,
							oldDelIps, userName);
				}

			} else if (type == Constants.SYN_SERVER) {
				this.saveVlanServer(vlanBean, nowList);
			}
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return flag;
	}

	public VlanConfig findVlanConfig(SwitchNodeEntity nodeEntity) {
		String sql = "select c from VlanConfig c where c.switchNode=:nodeEntity";
		Query query = manager.createQuery(sql);
		query.setParameter("nodeEntity", nodeEntity);
		if (query.getResultList() != null && query.getResultList().size() > 0) {
			return (VlanConfig) query.getResultList().get(0);
		} else {
			return null;
		}

	}

	public List<VlanPortConfig> findVlanPortConfig(String ipValue, int port,
			String vlanID) {
		try {
			String sql = "select c from VlanPortConfig c where c.ipVlue=:ipValue and c.vlanID=:vlanID";
			if (port > -1) {

				sql += " and c.portNo=:port";
			}
			Query query = manager.createQuery(sql);
			query.setParameter("ipValue", ipValue);
			if (port > -1) {
				query.setParameter("port", port);
			}
			query.setParameter("vlanID", vlanID);
			if (query.getResultList() != null
					&& query.getResultList().size() > 0) {
				return query.getResultList();
			} else {
				return null;
			}
		} catch (Exception e) {
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private List<String> queryIPS(String vlanID) {
		List<String> list = null;
		try {
			String sql = "select distinct c.ipVlue from vlanPortConfig c where c.vlanID=:vlanID";
			Query query = manager.createNativeQuery(sql);
			query.setParameter("vlanID", vlanID);
			list = query.getResultList();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public SwitchNodeEntity findSwitchNodeEntity(SwitchBaseConfig baseConfig) {

		String sql = "select c from SwitchNodeEntity c where c.baseConfig=:baseConfig";
		Query query = manager.createQuery(sql);
		query.setParameter("baseConfig", baseConfig);
		if (query.getResultList() != null && query.getResultList().size() > 0) {
			return (SwitchNodeEntity) query.getResultList().get(0);
		} else {
			return null;
		}

	}

	public boolean getFepStatus(String ipValue) {
		String code = datacache.getFepCodeByIP(ipValue);
		boolean ok = false;
		if (code != null) {
			FEPEntity fep = datacache.getFEPByCode(code);
			if (fep != null) {
				ok = fep.getStatus().isStatus();
			}
		}
		return ok;
	}

	private List<RingConfig> saveRingServer(List<RingInfo> list,RingBean ringBean) {
		List<RingConfig> ringConfigs = this.queryRingConfig(ringBean);
		List<RingConfig> oldringConfigs = new ArrayList<RingConfig>();
		boolean flag = false;
		if (ringConfigs != null && ringConfigs.size() > 0) {
			oldringConfigs.addAll(ringConfigs);
			// 查询是否有交换机从当前环中 移除
			for (RingConfig ringConfig : ringConfigs) {
				SwitchNodeEntity switchNodeEntity = ringConfig.getSwitchNode();
				if (switchNodeEntity != null) {
					SwitchBaseConfig baseConfig = switchNodeEntity.getBaseConfig();
					String ipValue = baseConfig.getIpValue();
					if (list != null && list.size() > 0) {
						flag = false;
						a: for (RingInfo ringInfo : list) {
							if (ringInfo.getIp().equals(ipValue)) {
								flag = true;
								break a;
							}
						}
					}
					if (!flag) {
						manager.remove(ringConfig);
						RingConfig config =new RingConfig();
						config.setIssuedTag(Constants.ISSUEDADM);
						config.setRingEnable(ringConfig.isRingEnable());
						config.setRingID(ringConfig.getRingID());
						config.setRingType(ringConfig.getRingType());
						config.setSystemType(ringConfig.getSystemType());
						manager.persist(config);
					}
				}

			}
		}
		// 判断是否有新的交换机加入到环中有则新增 无则更新
		if (list != null && list.size() > 0) {
			for (RingInfo ringInfo : list) {
				String ip = ringInfo.getIp();
				RingConfig ringConfig = this.queryRingConfig(ip, ringBean.getRingID(),ringBean.isRingEnable());
				if (ringConfig != null) {

					ringConfig.setPort1(ringInfo.getPort1());
					ringConfig.setPort2(ringInfo.getPort2());
					ringConfig.setPort1Type(ringInfo.getPort1Type());
					ringConfig.setPort2Type(ringInfo.getPort2Type());
					ringConfig.setSystemType(ringInfo.getSysType());
					ringConfig.setIssuedTag(Constants.ISSUEDADM);
					
					manager.merge(ringConfig);
				} else {
					SwitchNodeEntity switchNodeEntity = commonService.getSwitchByIp(ip);
					RingConfig newConfig = new RingConfig();
					newConfig.setPort1(ringInfo.getPort1());
					newConfig.setPort2(ringInfo.getPort2());
					newConfig.setPort1Type(ringInfo.getPort1Type());
					newConfig.setPort2Type(ringInfo.getPort2Type());
					newConfig.setSystemType(ringInfo.getSysType());
					newConfig.setIssuedTag(Constants.ISSUEDADM);
					if (switchNodeEntity != null) {
						newConfig.setSwitchNode(switchNodeEntity);
					}
					newConfig.setRingID(ringBean.getRingID());
					newConfig.setRingType(ringBean.getRingType());
					newConfig.setRingEnable(ringBean.isRingEnable());
					manager.persist(newConfig);

				}
			}
		}
		return oldringConfigs;
	}

	private boolean saveRingFep(List<RingInfo> list, String clientIP,RingBean ringBean, List<RingConfig> oldringConfigs, String userName)throws JMSException {
		ArrayList<RingConfig> arrayList = new ArrayList<RingConfig>();
		String ips = "";
		boolean flag = false;
		if (list != null && list.size() > 0) {
			for (RingInfo ringInfo : list) {
				RingConfig ringConfig = new RingConfig();
				SwitchBaseConfig baseConfig = new SwitchBaseConfig();
				baseConfig.setIpValue(ringInfo.getIp());
				SwitchNodeEntity switchNodeEntity = new SwitchNodeEntity();
				switchNodeEntity.setBaseConfig(baseConfig);
				ringConfig.setPort1(ringInfo.getPort1());
				ringConfig.setPort2(ringInfo.getPort2());
				ringConfig.setPort1Type(ringInfo.getPort1Type());
				ringConfig.setPort2Type(ringInfo.getPort2Type());
				ringConfig.setSystemType(ringInfo.getSysType());
				ringConfig.setSwitchNode(switchNodeEntity);
				ringConfig.setRingID(ringBean.getRingID());
				ringConfig.setRingType(ringBean.getRingType());
				ringConfig.setRingEnable(true);
				arrayList.add(ringConfig);
			}
		}
		if (oldringConfigs != null && oldringConfigs.size() > 0) {

			for (RingConfig ringConfig : oldringConfigs) {
				SwitchNodeEntity switchNodeEntity = ringConfig.getSwitchNode();
				if (switchNodeEntity != null) {
					SwitchBaseConfig baseConfig = switchNodeEntity.getBaseConfig();
					ips += baseConfig.getIpValue() + ",";
					flag = true;
				} else {
					flag = false;
				}
			}
		}

		if (flag) {
			sendMessageService.sendRingMessage(ips,MessageNoConstants.ALL_RING, userName, clientIP, ringBean.getRingID(), arrayList);
			flag = true;
		} else {
			if (list != null && list.size() > 0) {
				a: for (RingInfo ringInfo : list) {
					ips += ringInfo.getIp() + ",";
					break a;
				}
				sendMessageService.sendRingMessage(ips,MessageNoConstants.ALL_RING, userName, clientIP,ringBean.getRingID(), arrayList);
				flag = true;
			} else {
				flag = false;
			}
		}
		return flag;
	}

	public boolean saveRing(List<RingInfo> list, String clientIP,
			RingBean ringBean, int type, String userName) {
		boolean flag = false;
		try {
			if (type == Constants.SYN_ALL) {
				List<RingConfig> oldringConfigs = this.saveRingServer(list,ringBean);
				this.saveRingFep(list, clientIP, ringBean,oldringConfigs, userName);
			} else if (type == Constants.SYN_SERVER) {
				this.saveRingServer(list, ringBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	@SuppressWarnings("unchecked")
	public List<ONUTopoNodeEntity> queryOnuTopoNodeEntity(ONUEntity onuEntity) {
		String sql = "select c from ONUTopoNodeEntity c where c.onuEntity=:onuEntity";

		Query query = manager.createQuery(sql);
		query.setParameter("onuEntity", onuEntity);
		if (query.getResultList() != null && query.getResultList().size() > 0) {
			return query.getResultList();
		} else {
			return null;
		}
	}

	public RingConfig queryRingConfig(String ip, int ringID,boolean ringEnable) {
		String sql = "select c from RingConfig c where c.switchNode.baseConfig.ipValue=:ip and c.ringID=:ringID and c.ringEnable=:ringEnable";
		Query query = manager.createQuery(sql);
		query.setParameter("ip", ip);
		query.setParameter("ringID", ringID);
		query.setParameter("ringEnable", ringEnable);
		if (query.getResultList() != null && query.getResultList().size() > 0) {
			return (RingConfig) query.getResultList().get(0);
		} else {
			return null;
		}

	}

	@Override
	public RingConfig queryRingConfig(String ip) {

		String sql = "select c from RingConfig c where c.switchNode.baseConfig.ipValue=:ip";
		Query query = manager.createQuery(sql);
		query.setParameter("ip", ip);
		if (query.getResultList() != null && query.getResultList().size() > 0) {
			return (RingConfig) query.getResultList().get(0);
		} else {
			return null;
		}
	}

	// *****snmp
	@SuppressWarnings("unchecked")
	public List<SNMPHost> querySNMPHost(SwitchNodeEntity switchNodeEntity) {
		String sql = "select c from SNMPHost c where c.switchNode=:switchNodeEntity";
		Query query = manager.createQuery(sql);
		query.setParameter("switchNodeEntity", switchNodeEntity);
		if (query.getResultList() != null && query.getResultList().size() > 0) {

			return query.getResultList();
		} else {

			return null;
		}

	}

	@SuppressWarnings("unchecked")
	public List<SNMPHost> querySNMPHost(String hostIp,String snmpVersion,String massName) {
			String sql = "select c from SNMPHost c where c.hostIp=:hostIp and c.snmpVersion=:snmpVersion and c.massName=:massName and c.switchNode is not null";
			Query query = manager.createQuery(sql);
			query.setParameter("hostIp", hostIp);
			query.setParameter("snmpVersion", snmpVersion);
			query.setParameter("massName", massName);
			if (query.getResultList() != null
					&& query.getResultList().size() > 0) {
				return query.getResultList();
			} else {
				return null;
			}
	}

	@SuppressWarnings("unchecked")
	public List<SNMPHostBean> queryAllSNMPHostBean() {

		String sql = "select c.hostIp,c.snmpVersion ,c.massName from SNMPHost c group by c.hostIp,c.snmpVersion ,c.massName order by c.issuedTag";
		Query query = manager.createNativeQuery(sql);
		List<Object[]> list = query.getResultList();
		List<SNMPHostBean> hostBeans = new ArrayList<SNMPHostBean>();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				String hostIp = (String) list.get(i)[0];
				String snmpVersion = (String) list.get(i)[1];
				String massName = (String) list.get(i)[2];
				SNMPHostBean bean = new SNMPHostBean();
				bean.setIssuedTag(Constants.ISSUEDADM);
				List<SNMPHost> hosts = this.querySNMPHost(hostIp,snmpVersion,massName);
				if(hosts!=null&&hosts.size()>0){
				a:for(SNMPHost snmpHost :hosts){
					if(snmpHost.getIssuedTag()==Constants.ISSUEDADM){
						bean.setIssuedTag(Constants.ISSUEDADM);
						break a;
					}else{
						bean.setIssuedTag(Constants.ISSUEDDEVICE);
					}
				}
				}
				bean.setHostIp(hostIp);
				bean.setMassName(massName);
				bean.setSnmpVersion(snmpVersion);
				hostBeans.add(bean);
			}
			
		}
		
		
		return hostBeans;

	}

	public List<SNMPSwitchIPBean> querySwitchIPBeans(SNMPHostBean snmpHostBean) {

		List<SNMPHost> allHosts = this.querySNMPHost(snmpHostBean.getHostIp(),snmpHostBean.getSnmpVersion(),snmpHostBean.getMassName());
		List<SNMPSwitchIPBean> list = new ArrayList<SNMPSwitchIPBean>();
		if (allHosts != null && allHosts.size() > 0) {
			for (SNMPHost snmpHost : allHosts) {
				if (snmpHost.getSwitchNode() != null) {
					String ip = snmpHost.getSwitchNode().getBaseConfig()
							.getIpValue();
					SNMPSwitchIPBean ipBean = new SNMPSwitchIPBean();
					ipBean.setIpValue(ip);
					list.add(ipBean);
				}
			}
		}
		return list;

	}

	private boolean saveSNMPHostFep(List<SNMPHost> snmpHost, String clientIP,
			SNMPHostBean snmpHostBean, List<SNMPHost> oldSNMPHost,
			String userName) throws JMSException {
		Map<String, List<SNMPHost>> map = new HashMap<String, List<SNMPHost>>();
		String ipValues = "";

		boolean flag = false;
		if (oldSNMPHost != null && oldSNMPHost.size() > 0) {
			a: for (SNMPHost host : oldSNMPHost) {
				if (host.getSwitchNode() != null) {
					flag = true;
					String ip = host.getSwitchNode().getBaseConfig()
							.getIpValue();
					ipValues = ip + ",";
					break a;
				}
			}
		}
		map.put("new", snmpHost);
		map.put("old", oldSNMPHost);

		if (flag) {
			sendMessageService.sendSNMPHostMessage(ipValues,
					MessageNoConstants.ALL_SNMPHOST, userName, clientIP, map);
			flag = true;
		} else {
			if (snmpHost != null && snmpHost.size() > 0) {
				a: for (SNMPHost host : snmpHost) {
					if (host.getSwitchNode() != null) {
						String ip = host.getSwitchNode().getBaseConfig().getIpValue();
						ipValues = ip + ",";
						break a;
					}
				}
				sendMessageService.sendSNMPHostMessage(ipValues,MessageNoConstants.ALL_SNMPHOST, userName, clientIP,map);
				flag = true;
			} else {
				flag = false;
			}

		}
		return flag;
	}

	private List<SNMPHost> saveSNMPHostServer(List<SNMPHost> snmpHost,String clientIP, SNMPHostBean snmpHostBean) {
		String hostIP = snmpHostBean.getHostIp();
		List<SNMPHost> old2Ips = new ArrayList<SNMPHost>();
		List<SNMPHost> oldSNMPHost = this.querySNMPHost(hostIP,snmpHostBean.getSnmpVersion(),snmpHostBean.getMassName());
		String snmpVersion = "";
		String massName = "";
		if (oldSNMPHost != null && oldSNMPHost.size() > 0) {
			old2Ips.addAll(oldSNMPHost);
			for (SNMPHost host : oldSNMPHost) {
				snmpVersion = host.getSnmpVersion();
				massName = host.getMassName();
				manager.remove(host);
			}
			SNMPHost newHost = new SNMPHost();
			newHost.setHostIp(hostIP);
			newHost.setMassName(massName);
			newHost.setSnmpVersion(snmpVersion);
			manager.persist(newHost);
		}
		if (snmpHost != null && snmpHost.size() > 0) {
			for (SNMPHost newhost : snmpHost) {
				if (newhost.getSwitchNode() != null) {
					SwitchNodeEntity switchNodeEntity = null;
					if (newhost.getSwitchNode().getId() == null) {
						switchNodeEntity = commonService.getSwitchByIp(newhost.getSwitchNode().getBaseConfig().getIpValue());
					} else {
						switchNodeEntity = manager.find(SwitchNodeEntity.class,newhost.getSwitchNode().getId());
					}
					if (switchNodeEntity != null) {
						newhost.setSwitchNode(switchNodeEntity);
					}
					manager.persist(newhost);
				} else {
					logger.error(newhost.getHostIp() + "关联的交换机为null 不能配置");
				}
			}
			manager.flush();
		}
		return old2Ips;
	}

	public boolean saveSNMPHost(List<SNMPHost> snmpHost, String clientIP,
			SNMPHostBean snmpHostBean, int type, String userName) {
		boolean tag = false;
		try {

			if (type == Constants.SYN_ALL) {
				List<SNMPHost> olds = this.saveSNMPHostServer(snmpHost,
						clientIP, snmpHostBean);
				tag = this.saveSNMPHostFep(snmpHost, clientIP, snmpHostBean,
						olds, userName);
			} else if (type == Constants.SYN_SERVER) {
				this.saveSNMPHostServer(snmpHost, clientIP, snmpHostBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("保存SNMP" + e);
		}
		return tag;
	}

	@SuppressWarnings("unchecked")
	public void deleteAllSnmpHost() {
			String sql = "select c from SNMPHost c ";
			Query query = manager.createQuery(sql);

			if (query.getResultList() != null
					&& query.getResultList().size() > 0) {
				List<SNMPHost> hosts = query.getResultList();
				for (SNMPHost snmpHost : hosts) {
					manager.remove(snmpHost);
				}
			}
	}

	@SuppressWarnings("unchecked")
	public List<SNMPHost> queryAllSNMPhost() {
		List<SNMPHost> hosts = null;
		try {
			String sql = "select c from SNMPHost c ";
			Query query = manager.createQuery(sql);

			if (query.getResultList() != null&& query.getResultList().size() > 0) {
				hosts = query.getResultList();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hosts;
	}

	@SuppressWarnings("unchecked")
	public List<SNMPViewBean> querySNMPView() {
		List<SNMPViewBean> beans = new ArrayList<SNMPViewBean>();
		String sql = "select distinct c.viewName,c.OIDName,c.viewType from SNMPView c order by c.issuedTag asc";
		Query query = manager.createQuery(sql);

		if (query.getResultList() != null && query.getResultList().size() > 0) {
			List<Object[]> list = query.getResultList();
			for (int i = 0; i < list.size(); i++) {
				String viewName = (String) list.get(i)[0];
				String OIDName = (String) list.get(i)[1];
				String viewType = (String) list.get(i)[2];
				SNMPViewBean bean = new SNMPViewBean();
				bean.setOIDName(OIDName);
				bean.setViewName(viewName);
				bean.setViewType(viewType);
				bean.setIssuedTag(Constants.ISSUEDDEVICE);
				List<SNMPView> snmpViews = this.querySNMPView(bean);
				if(snmpViews!=null&&snmpViews.size()>0){
					a:for(SNMPView snmpView :snmpViews){
						if(snmpView.getIssuedTag()==Constants.ISSUEDADM){
							bean.setIssuedTag(Constants.ISSUEDADM);
							break a;
						}
					}
				}
				beans.add(bean);
			}

		}
		return beans;

	}

	public List<SNMPSwitchIPBean> querySNMPSwitchIPBean(
			SNMPViewBean snmpViewBean) {
		List<SNMPView> snmpViews = this.querySNMPView(snmpViewBean);
		List<SNMPSwitchIPBean> snmpSwitchIPBeans = new ArrayList<SNMPSwitchIPBean>();
		if (snmpViews != null && snmpViews.size() > 0) {

			for (SNMPView snmpView : snmpViews) {
				if (snmpView.getSwitchNode() != null) {
					String ip = snmpView.getSwitchNode().getBaseConfig().getIpValue();
					SNMPSwitchIPBean bean = new SNMPSwitchIPBean();
					bean.setIpValue(ip);
					snmpSwitchIPBeans.add(bean);
				}

			}
		}
		return snmpSwitchIPBeans;

	}

	@SuppressWarnings("unchecked")
	private List<SNMPView> querySNMPView(SNMPViewBean snmpViewBean) {
		String sql = "select c from SNMPView c where c.viewName=:viewName and c.OIDName=:OIDName and c.viewType=:viewType";
		Query query = manager.createQuery(sql);
		query.setParameter("viewName", snmpViewBean.getViewName());
		query.setParameter("OIDName", snmpViewBean.getOIDName());
		query.setParameter("viewType", snmpViewBean.getViewType());

		if (query.getResultList() != null && query.getResultList().size() > 0) {
			return query.getResultList();
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<SNMPUserBean> querySNMPUserBean() {

		List<SNMPUserBean> beans = new ArrayList<SNMPUserBean>();
		String sql = "select distinct c.userName,c.snmpGroup,c.version from SNMPUser c order by c.issuedTag asc";
		Query query = manager.createQuery(sql);
		if (query.getResultList() != null && query.getResultList().size() > 0) {
			List<Object[]> list = query.getResultList();
			for(int i=0;i<list.size();i++){
				
				String  snmpTag =(String) list.get(i)[2] ;
				String group =(String) list.get(i)[1];
				String user =(String) list.get(i)[0];
				SNMPUserBean snmpUserBean = new SNMPUserBean();
				snmpUserBean.setSnmpGroup(group);
				snmpUserBean.setUserName(user);
				snmpUserBean.setSnmpTag(snmpTag);
				snmpUserBean.setIssuedTag(Constants.ISSUEDDEVICE);
				List<SNMPUser> snmpUsers = this.querySNMPUser(snmpUserBean);
				if(snmpUsers!=null&&snmpUsers.size()>0){
					a:for(SNMPUser snmpUser:snmpUsers){
						if(snmpUser.getIssuedTag()==Constants.ISSUEDADM){
							snmpUserBean.setIssuedTag(Constants.ISSUEDADM);
							break a;
						}
					}
				}
				beans.add(snmpUserBean);
			}

		}
		return beans;
	}

	public List<SNMPSwitchIPBean> querySNMPSwitchIPBean(
			SNMPUserBean snmpUserBean) {
		List<SNMPUser> snmpUsers = this.querySNMPUser(snmpUserBean);
		List<SNMPSwitchIPBean> snmpSwitchIPBeans = new ArrayList<SNMPSwitchIPBean>();
		if (snmpUsers != null && snmpUsers.size() > 0) {
			for (SNMPUser snmpUser : snmpUsers) {
				if (snmpUser.getSwitchNode() != null) {
					String ip = snmpUser.getSwitchNode().getBaseConfig().getIpValue();
					SNMPSwitchIPBean bean = new SNMPSwitchIPBean();
					bean.setIpValue(ip);
					snmpSwitchIPBeans.add(bean);
				}
			}
		}
		return snmpSwitchIPBeans;

	}

	@SuppressWarnings("unchecked")
	private List<SNMPUser> querySNMPUser(SNMPUserBean snmpUserBean) {
		String sql = "select c from SNMPUser c where c.userName=:userName";
		Query query = manager.createQuery(sql);
		query.setParameter("userName", snmpUserBean.getUserName());
		if (query.getResultList() != null && query.getResultList().size() > 0) {
			return query.getResultList();
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<SNMPGroupBean> querySNMPGroupBean() {

		String sql = "select distinct c.groupName ,c.securityModel,c.securityLevel from SNMPGroup c order by c.issuedTag asc";
		Query query = manager.createQuery(sql);
		List<Object[]> list = query.getResultList();
		List<SNMPGroupBean> groupBeans = new ArrayList<SNMPGroupBean>();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				String groupName = (String) list.get(i)[0];
				String mode = (String) list.get(i)[1];
				SNMPGroupBean groupBean = new SNMPGroupBean();
				groupBean.setGroupName(groupName);
				groupBean.setSecurityModel(mode);
				groupBean.setIssuedTag(Constants.ISSUEDDEVICE);
				groupBean.setSecurityLevel((Integer) list.get(i)[2]);
				List<SNMPGroup> groups = this.querySNMPGroup(groupBean);
				if(groups!=null&&groups.size()>0){
					a:for(SNMPGroup group :groups){
						if(group.getIssuedTag()==Constants.ISSUEDADM){
							groupBean.setIssuedTag(Constants.ISSUEDADM);
							break a;
						}
					}
				}
				groupBeans.add(groupBean);
				
			}

		}
		return groupBeans;
	}

	public List<SNMPSwitchIPBean> querySNMPSwitchIPBean(
			SNMPGroupBean snmpGroupBean) {
		List<SNMPGroup> groups = this.querySNMPGroup(snmpGroupBean);
		List<SNMPSwitchIPBean> ipBeans = new ArrayList<SNMPSwitchIPBean>();
		if (groups != null && groups.size() > 0) {
			for (SNMPGroup snmpGroup : groups) {
				if (snmpGroup.getSwitchNode() != null) {
					SNMPSwitchIPBean bean = new SNMPSwitchIPBean();
					bean.setIpValue(snmpGroup.getSwitchNode().getBaseConfig()
							.getIpValue());
					ipBeans.add(bean);
				}
			}

		}
		return ipBeans;
	}

	@SuppressWarnings("unchecked")
	private List<SNMPGroup> querySNMPGroup(SNMPGroupBean snmpGroupBean) {
		String sql = "select c from SNMPGroup c where c.groupName=:groupName and  c.securityModel=:securityModel and c.securityLevel=:securityLevel";
		Query query = manager.createQuery(sql);
		query.setParameter("groupName", snmpGroupBean.getGroupName());
		query.setParameter("securityModel", snmpGroupBean.getSecurityModel());
		query.setParameter("securityLevel", snmpGroupBean.getSecurityLevel());
		if (query.getResultList() != null && query.getResultList().size() > 0) {
			return query.getResultList();
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public List<SNMPMassBean> querySNMPMassBean() {

		String sql = "select distinct c.massName ,c.massView,c.massRight from SNMPMass c order by c.issuedTag asc";
		Query query = manager.createQuery(sql);
		List<Object[]> list = query.getResultList();
		List<SNMPMassBean> beans = new ArrayList<SNMPMassBean>();
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {

				SNMPMassBean bean = new SNMPMassBean();
				bean.setMassName((String) list.get(i)[0]);
				bean.setMassView((String) list.get(i)[1]);
				bean.setMassRight((String) list.get(i)[2]);
				bean.setIssuedTag(Constants.ISSUEDDEVICE);
				List<SNMPMass> masses = this.querySNMPMass(bean);
				if(masses!=null&&masses.size()>0){
					a:for(SNMPMass snmpMass :masses){
						if(snmpMass.getIssuedTag()==Constants.ISSUEDADM){
							bean.setIssuedTag(Constants.ISSUEDADM);
							break a;
						}
						
					}
				}
				beans.add(bean);
			}
		}
		return beans;
	}

	public List<SNMPSwitchIPBean> querySNMPSwitchIPBean(
			SNMPMassBean snmpMassBean) {
		List<SNMPMass> masses = this.querySNMPMass(snmpMassBean);
		List<SNMPSwitchIPBean> ipBeans = new ArrayList<SNMPSwitchIPBean>();
		if (masses != null && masses.size() > 0) {
			for (SNMPMass snmpMass : masses) {
				if (snmpMass.getSwitchNode() != null) {
					SNMPSwitchIPBean bean = new SNMPSwitchIPBean();
					bean.setIpValue(snmpMass.getSwitchNode().getBaseConfig()
							.getIpValue());
					ipBeans.add(bean);
				}
			}

		}
		return ipBeans;
	}

	@SuppressWarnings("unchecked")
	private List<SNMPMass> querySNMPMass(SNMPMassBean snmpMassBean) {
		String sql = "select c from SNMPMass c where c.massName=:massName and c.massView=:massView and c.massRight=:massRight";
		Query query = manager.createQuery(sql);
		query.setParameter("massName", snmpMassBean.getMassName());
		query.setParameter("massView", snmpMassBean.getMassView());
		query.setParameter("massRight", snmpMassBean.getMassRight());
		if (query.getResultList() != null && query.getResultList().size() > 0) {
			return query.getResultList();
		} else {
			return null;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<FaultDetectionRecord> findFaultDetection(
			TrapWarningBean trapWarningBean) {

		Calendar calendar1 = null;
		Calendar calendar2 = null;
		if (trapWarningBean.getStartDate() != null) {
			calendar1 = this.setStartDate(trapWarningBean.getStartDate());

		}
		if (trapWarningBean.getEndDate() != null) {
			calendar2 = this.setEndDate(trapWarningBean.getEndDate());
		}
		String sql = "select c from FaultDetectionRecord c where 1=1";

		if (trapWarningBean.getStartDate() != null) {
			sql += " and c.createDate>=:startDate";
		}
		if (trapWarningBean.getEndDate() != null) {
			sql += " and c.createDate <=:endDate";

		}
		if (trapWarningBean.getStatus() == 0
				|| trapWarningBean.getStatus() == 1) {
			sql += " and c.status=:status";
		}
		if (trapWarningBean.getIpValue() != null
				&& !trapWarningBean.getIpValue().equals("")) {
			sql += " and c.device like :ip";

		}
		sql += " order by c.createDate desc";
		Query query = manager.createQuery(sql);
		if (trapWarningBean.getStartPage() > 1) {
			query.setFirstResult((trapWarningBean.getStartPage() - 1)
					* trapWarningBean.getMaxPageSize());
		} else if (trapWarningBean.getStartPage() == 1) {
			query.setFirstResult(0);
		}

		if (trapWarningBean.getMaxPageSize() > 0) {
			query.setMaxResults(trapWarningBean.getMaxPageSize());
		}
		if (trapWarningBean.getStartDate() != null) {
			query.setParameter("startDate", calendar1.getTime());

		}
		if (trapWarningBean.getEndDate() != null) {
			query.setParameter("endDate", calendar2.getTime());

		}
		if (trapWarningBean.getStatus() == 0
				|| trapWarningBean.getStatus() == 1) {
			query.setParameter("status", trapWarningBean.getStatus());
		}
		if (trapWarningBean.getIpValue() != null
				&& !trapWarningBean.getIpValue().equals("")) {

			query.setParameter("ip", "%" + trapWarningBean.getIpValue() + "%");
		}
		List<FaultDetectionRecord> list = null;
		try {
			list = query.getResultList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public long queryAllFaultDetection(TrapWarningBean trapWarningBean) {
		Calendar calendar1 = null;
		Calendar calendar2 = null;
		if (trapWarningBean.getStartDate() != null) {
			calendar1 = this.setStartDate(trapWarningBean.getStartDate());

		}
		if (trapWarningBean.getEndDate() != null) {
			calendar2 = this.setEndDate(trapWarningBean.getEndDate());
		}
		String sql = "select count(c)   from FaultDetectionRecord c where 1=1";

		if (trapWarningBean.getStartDate() != null) {
			sql += " and c.createDate>=:startDate";
		}
		if (trapWarningBean.getEndDate() != null) {
			sql += " and c.createDate <=:endDate";

		}
		if (trapWarningBean.getStatus() == 0
				|| trapWarningBean.getStatus() == 1) {
			sql += " and c.status=:status";
		}
		if (trapWarningBean.getIpValue() != null
				&& !trapWarningBean.getIpValue().equals("")) {
			sql += " and c.device like :ip";

		}
		sql += " order by c.createDate desc";
		Query query = manager.createQuery(sql);
		if (trapWarningBean.getStartDate() != null) {
			query.setParameter("startDate", calendar1.getTime());

		}
		if (trapWarningBean.getEndDate() != null) {
			query.setParameter("endDate", calendar2.getTime());

		}
		if (trapWarningBean.getStatus() == 0
				|| trapWarningBean.getStatus() == 1) {
			query.setParameter("status", trapWarningBean.getStatus());
		}
		if (trapWarningBean.getIpValue() != null
				&& !trapWarningBean.getIpValue().equals("")) {

			query.setParameter("ip", "%" + trapWarningBean.getIpValue() + "%");
		}

		try {
			Long size = null;
			if (query.getResultList() != null
					&& query.getResultList().size() != 0) {
				size = (Long) query.getResultList().get(0);
				return size;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public TopDiagramEntity findTopDiagramEntity(Long id) {
		try {
			String sql = "select c from TopDiagramEntity c left join fetch c.nodes  where c.id=:id";
			Query query = manager.createQuery(sql);
			query.setParameter("id", id);
			if (query.getResultList() != null
					&& query.getResultList().size() > 0) {
				return (TopDiagramEntity) query.getResultList().get(0);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public List<NodeEntity> queryNodeEntity(long diagramID, String parentNode) {
		List<NodeEntity> list = null;
		try {
			if (parentNode != null && !parentNode.equals("")) {
				String sql = "select c from NodeEntity c where c.topDiagramEntity.id=:diagramID and c.parentNode=:nodeEntity" ;
				Query query = manager.createQuery(sql);
				query.setParameter("diagramID", diagramID);
				query.setParameter("nodeEntity", parentNode);
				if (query.getResultList() != null
						&& query.getResultList().size() > 0) {
					list = query.getResultList();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public List<NodeEntity> queryNodeEntity(long diagramID, String parentNode, UserEntity userEntity) {
		List<NodeEntity> list = null;
		try {
			if (parentNode != null && !parentNode.equals("")) {
				
				String sql = "";
				//如果具有超级管理员权限,不过滤设备
				int roleCode = userEntity.getRole().getRoleCode();
				if (roleCode == Constants.ADMINCODE){
					sql = "select c from NodeEntity c where c.topDiagramEntity.id=:diagramID and c.parentNode=:nodeEntity" ;
				}
				else{
					sql = "select distinct c from NodeEntity c ,UserEntity user where c.topDiagramEntity.id=:diagramID and c.parentNode=:nodeEntity" 
						+ " and user.id=:userID and user in elements(c.users)";
				}
				
				Query query = manager.createQuery(sql);
				query.setParameter("diagramID", diagramID);
				query.setParameter("nodeEntity", parentNode);
				if (roleCode != Constants.ADMINCODE){
					query.setParameter("userID", userEntity.getId());
				}
				
				//第一步:只是查出了子网下面的设备，但是当为区域管理员时，由于在用户管理中子网不能设置到用户中，
				//所以并不包含子网
				if (query.getResultList() != null
						&& query.getResultList().size() > 0) {
					list = query.getResultList();
					
					List<NodeEntity> tempNodeList = new ArrayList<NodeEntity>();
					for(NodeEntity entit : list){
						if (entit instanceof EponTopoEntity){//通过EponTopoEntity找到与之连接的分光器
							EponTopoEntity eponTopoEntity = (EponTopoEntity)entit;
							String oltGuid = eponTopoEntity.getGuid();
							List<Epon_S_TopNodeEntity> splitterList = commonService.findEpon_S_TopoNodeEntity(oltGuid);
							if (splitterList != null && splitterList.size() > 0){
								for(Epon_S_TopNodeEntity splitter : splitterList){
									tempNodeList.add(splitter);
								}
							}
						}
					}
					list.addAll(tempNodeList);	
				}
				else{
					list = new ArrayList<NodeEntity>();
				}
				
				//第二步:查询所有的nodeentity,得到其中的子网,并把它加入到list中
				if (roleCode != Constants.ADMINCODE){
					String sql1 = "select c from NodeEntity c where c.topDiagramEntity.id=:diagramID and c.parentNode=:nodeEntity" ;
					Query query1 = manager.createQuery(sql1);
					query1.setParameter("diagramID", diagramID);
					query1.setParameter("nodeEntity", parentNode);
					List allList = query1.getResultList();
					if (allList != null && allList.size() > 0){
						for(int k = 0 ; k < allList.size(); k++){
							if (allList.get(k) instanceof SubNetTopoNodeEntity){
								list.add((NodeEntity)allList.get(k));
							}
							else if (allList.get(k) instanceof CommentTopoNodeEntity){
								list.add((NodeEntity)allList.get(k));
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	private boolean queryCheckNodeEntity(long diagramID, String parentNode) {

		try {
			if (parentNode != null && !parentNode.equals("")) {
				String sql = "select count(*) from NodeEntity c where c.topDiagramEntity.id=:diagramID and c.parentNode=:nodeEntity";
				Query query = manager.createQuery(sql);
				query.setParameter("diagramID", diagramID);
				query.setParameter("nodeEntity", parentNode);
				Long size = null;
				if (query.getResultList() != null
						&& query.getResultList().size() > 0) {
					size = (Long) query.getResultList().get(0);
					if (size != null) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			this.logger.error("queryCheckNodeEntity",e);
		}
		return false;
	}

	public SubNetTopoNodeEntity getSubNetTopoNodeEntity(long diagramID,
			String parentNode,UserEntity userEntity) {
		SubNetTopoNodeEntity netTopoNodeEntity = null;
		try {
			Object[] parms = { parentNode };
			List<SubNetTopoNodeEntity> list = (List<SubNetTopoNodeEntity>) commonService
					.findAll(SubNetTopoNodeEntity.class,
							" where entity.guid=?", parms);
			if (list != null && list.size() > 0) {
				netTopoNodeEntity = list.get(0);
				List<NodeEntity> nodeList = this.queryNodeEntity(diagramID,
						parentNode,userEntity);
				List<LinkEntity> allLink = new ArrayList<LinkEntity>();
				if (nodeList != null && nodeList.size() > 0) {
					for (NodeEntity node : nodeList) {

						List<LinkEntity> linkEntities = queryLinkEntity(
								diagramID, node);
						if (linkEntities != null && linkEntities.size() > 0) {
							for (LinkEntity linkEntity : linkEntities) {
								if (!allLink.contains(linkEntity)) {
									allLink.add(linkEntity);
								}
							}
						}
					}
				}
				if (nodeList != null && nodeList.size() > 0) {
					netTopoNodeEntity.setNodes(nodeList);
				}
				if (allLink.size() > 0) {
					netTopoNodeEntity.setLines(allLink);
				}

			}

			return netTopoNodeEntity;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<LinkEntity> queryLinkEntity(long diagramID,
			NodeEntity nodeEntity) {
		List<LinkEntity> list = null;
		try {
			if (nodeEntity.getId() != null) {
				String sql = "select c from LinkEntity c left join fetch c.lldp left join fetch c.node1 left join fetch c.node2 where c.topDiagramEntity.id=:diagramID and c.node1=:nodeEntity or c.node2=:nodeEntity";
				Query query = manager.createQuery(sql);
				query.setParameter("nodeEntity", nodeEntity);
				query.setParameter("diagramID", diagramID);
				list = query.getResultList();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<LinkEntity> queryLinkEntity(NodeEntity nodeEntity) {

		List<LinkEntity> list = null;
		try {
			if (nodeEntity.getId() != null) {
				String sql = "select c from LinkEntity c where  c.node1=:nodeEntity or c.node2=:nodeEntity";
				Query query = manager.createQuery(sql);
				query.setParameter("nodeEntity", nodeEntity);
				list = query.getResultList();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<LinkEntity> queryLinkEntityByRingID(int ringNo) {

		try {
			String sql = "select c from LinkEntity c left join fetch c.lldp where c.ringID=:ringNo";
			Query query = manager.createQuery(sql);
			query.setParameter("ringNo", ringNo);
			if (query.getResultList() != null&& query.getResultList().size() > 0) {
				return query.getResultList();
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error("查询线：", e);
			return null;
		}

	}

	public LinkEntity queryLinkEntity(Long id) {
		try {
			String sql = "select c from LinkEntity c left join fetch c.carrierRoute where c.id=:id";
			Query query = manager.createQuery(sql);
			query.setParameter("id", id);
			if (query.getResultList() != null
					&& query.getResultList().size() > 0) {
				return (LinkEntity) query.getResultList().get(0);
			}
		} catch (Exception e) {
			logger.error("查询线：", e);
		}
		return null;
	}

	
//	@Override
//	public TopDiagramEntity findAllTopDiagramEntity() {
//		try {		
//			String sql = "select  c from TopDiagramEntity  c left join fetch c.nodes s left join fetch c.lines l left join fetch l.carrierRoute left join fetch l.lldp  left join fetch l.node1 left join fetch l.node2 where  s.parentNode is null or s.parentNode=''";
//			Query query = manager.createQuery(sql);
//			if (query.getResultList() != null
//					&& query.getResultList().size() > 0) {
//				TopDiagramEntity diagramEntity = (TopDiagramEntity) query
//						.getResultList().get(0);
//				Set<LinkEntity> set = diagramEntity.getLines();
//				Set<LinkEntity> setNew = new HashSet<LinkEntity>();
//				if (set != null && set.size() > 0) {
//					for (LinkEntity linkEntity : set) {
//						if (linkEntity.getNode1() != null) {
//							if (linkEntity.getNode1().getParentNode() == null
//									|| linkEntity.getNode2().getParentNode() == null) {
//								setNew.add(linkEntity);
//							}
//						}
//					}
//					diagramEntity.setLines(setNew);
//				}
//				return diagramEntity;
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
	
	@Override
	public TopDiagramEntity findAllTopDiagramEntity() {
		try {
			long start  =System.currentTimeMillis();
			String sql = "select  c from TopDiagramEntity  c ";
//			String sql ="select c.* from topnode c  left join topnode_areaentity t on t.topnode_id=c.id  left join areaentity a on t.areas_id = a.id where a.id ='3'";
			Query query = manager.createQuery(sql);
			Set<NodeEntity> nodeset =new HashSet<NodeEntity>();
			Set<LinkEntity> linkset =new HashSet<LinkEntity>();
			if (query.getResultList() != null&& query.getResultList().size() > 0) {
				TopDiagramEntity diagramEntity = (TopDiagramEntity) query.getResultList().get(0);
				for(NodeEntity nodeEntity :diagramEntity.getNodes()){
					if(nodeEntity.getParentNode()==null){
						nodeset.add(nodeEntity);
					}
				}
				for(LinkEntity linkEntity : diagramEntity.getLines()){
					if(linkEntity.getNode1().getParentNode()==null||linkEntity.getNode2().getParentNode()==null){
						
						linkset.add(linkEntity);
					}
				}
				diagramEntity.setLines(linkset);
				diagramEntity.setNodes(nodeset);
				long end  =System.currentTimeMillis();
				logger.info("end -start "+(end-start));
				return diagramEntity;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * 根据UserEntity对设备做权限管理
	 * @param userEntity
	 * @return
	 */
	@Override
	public TopDiagramEntity findAllTopDiagramEntity(UserEntity userEntity) {
		try {
			long start  =System.currentTimeMillis();
			String sql = "select  c from TopDiagramEntity  c ";
//			String sql ="select c.* from topnode c  left join topnode_areaentity t on t.topnode_id=c.id  left join areaentity a on t.areas_id = a.id where a.id ='3'";
			Query query = manager.createQuery(sql);
			Set<NodeEntity> nodeset =new HashSet<NodeEntity>();
			Set<LinkEntity> linkset =new HashSet<LinkEntity>();
			if (query.getResultList() != null&& query.getResultList().size() > 0) {
				TopDiagramEntity diagramEntity = (TopDiagramEntity) query.getResultList().get(0);
				for(NodeEntity nodeEntity :diagramEntity.getNodes()){
					if(nodeEntity.getParentNode()==null){
						nodeset.add(nodeEntity);
					}
				}
				for(LinkEntity linkEntity : diagramEntity.getLines()){
					if(linkEntity.getNode1().getParentNode()==null||linkEntity.getNode2().getParentNode()==null){
						linkset.add(linkEntity);
					}
				}
				diagramEntity.setLines(linkset);
				diagramEntity.setNodes(nodeset);
				long end  =System.currentTimeMillis();
				logger.info("end -start "+(end-start));
				
				diagramEntity = fiterTopDiagramEntity(diagramEntity,userEntity);

				return diagramEntity;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	/*
	 * 过滤TopDiagramEntity
	 */
	private TopDiagramEntity fiterTopDiagramEntity(TopDiagramEntity diagramEntity,UserEntity userEntity){
		//如果具有超级管理员权限,不过滤设备
		boolean isSuper = isSuperAdmin(userEntity);
		if (isSuper){
			try{
				Iterator iterators = diagramEntity.getNodes().iterator();
				List<NodeEntity> temps = new ArrayList<NodeEntity>();
				while(iterators.hasNext()){
					NodeEntity entity = (NodeEntity)iterators.next();
					if (entity instanceof Epon_S_TopNodeEntity){
						String oltGuid = ((Epon_S_TopNodeEntity)entity).getOltGuid();
						if(!hasOLT(oltGuid,diagramEntity.getNodes())){
							temps.add(entity);
						}
					}
				}
				
				for (int i = 0 ; i < temps.size() ; i++){
					diagramEntity.getNodes().remove(temps.get(i));
				}
			}
			catch(Exception ee){
				this.logger.error("判断列表中是否有所属的OLT设备的方法异常",ee);
			}
			
			return diagramEntity;
		}
		
		long userID = userEntity.getId();
		
		List<NodeEntity> nodeList = null;
		List<NodeEntity> tempNodeList = new ArrayList<NodeEntity>();
		String sql = "select distinct node from UserEntity user,NodeEntity node where user.id="+ userID 
										+ " and user in elements(node.users)";
		Query query = manager.createQuery(sql);
		if (query != null && query.getResultList().size() > 0) {
			nodeList =  query.getResultList();
		}

		if (nodeList == null){
			nodeList = new ArrayList<NodeEntity>();
		}
		else{
			for(NodeEntity entit : nodeList){
				if (entit instanceof EponTopoEntity){//通过EponTopoEntity找到与之连接的分光器
					EponTopoEntity eponTopoEntity = (EponTopoEntity)entit;
					String oltGuid = eponTopoEntity.getGuid();
					List<Epon_S_TopNodeEntity> splitterList = commonService.findEpon_S_TopoNodeEntity(oltGuid);
					if (splitterList != null && splitterList.size() > 0){
						for(Epon_S_TopNodeEntity splitter : splitterList){
							tempNodeList.add(splitter);
						}
					}
				}
			}
			
			nodeList.addAll(tempNodeList);
		}

		//第一步:
		//把FEPTopoNodeEntity放入到nodeList中
		//把SubNetTopoNodeEntity放入到nodeList中,并且SubNetTopoNodeEntity的父节点为空
		//把CommentTopoNodeEntity放入到nodeList中
		Iterator iterator = diagramEntity.getNodes().iterator();
		while(iterator.hasNext()){
			NodeEntity entity = (NodeEntity)iterator.next();
			if (entity instanceof FEPTopoNodeEntity){
				nodeList.add(entity);
			}
			else if (entity instanceof SubNetTopoNodeEntity){
				if (entity.getParentNode() == null){
					nodeList.add(entity);
				}
			}
			else if (entity instanceof CommentTopoNodeEntity){
				nodeList.add(entity);
			}
		}

		//第二步:清空diagramEntity.getNodes()中所有的NodeEntity
		diagramEntity.getNodes().clear();

		//第三步:把nodeList放入到diagramEntity.getNodes()中,并且此节点的父节点必须为空。
		for (int i = nodeList.size() -1; i>=0; i--){
			if (nodeList.get(i).getParentNode() != null){
				nodeList.remove(i);
			}
		}
		
		//第四步：
		try{
			for(int i = nodeList.size() -1; i>=0; i--){
				if (nodeList.get(i) instanceof Epon_S_TopNodeEntity){
					String oltGuid = ((Epon_S_TopNodeEntity)nodeList.get(i)).getOltGuid();
					if(!hasOLT(oltGuid,nodeList)){
						nodeList.remove(i);
					}
				}
			}
		}
		catch(Exception ee){
			this.logger.error("判断列表中是否有所属的OLT设备的方法异常",ee);
		}
		
		diagramEntity.getNodes().addAll(nodeList);

		return diagramEntity;
	}
	/**
	 * 通过传入ONU的oltGuid，判断列表中是否有所属的OLT设备
	 * @param oltGuid
	 * @param nodeList
	 * @return
	 */
	private boolean hasOLT(String oltGuid,List<NodeEntity> nodeList){
		boolean bool = false;
		for (int i = 0 ; i < nodeList.size(); i++){
			if (nodeList.get(i) instanceof EponTopoEntity){
				String guid = ((EponTopoEntity)(nodeList.get(i))).getGuid();
				if (guid.equalsIgnoreCase(oltGuid)){
					bool = true;
					break;
				}
			}
		}
		
		return bool;
	}
	
	private boolean hasOLT(String oltGuid,Set<NodeEntity> nodeSet){
		boolean bool = false;
		for (NodeEntity nodeEntity : nodeSet){
			if (nodeEntity instanceof EponTopoEntity){
				String guid = ((EponTopoEntity)nodeEntity).getGuid();
				if (guid.equalsIgnoreCase(oltGuid)){
					bool = true;
					break;
				}
			}
		}
		
		return bool;
	}
	
	public List<String> queryAllSerialPort() {

		List<String> list = new ArrayList<String>();
		for (Enumeration<CommPortIdentifier> en = CommPortIdentifier.getPortIdentifiers(); en.hasMoreElements();) {
			CommPortIdentifier portId = en.nextElement();
			String name = portId.getName();
			logger.info("所有串口：" + name);
			if (name.startsWith("COM")) {
				list.add(portId.getName());
			}
		}
		logger.info("COM 串口数量:" + list.size());
		return list;
	}

	public List<TimerMonitoring> queryTimerMonitoring() {
		try {
			String sql = "select c from TimerMonitoring c";
			Query query = manager.createQuery(sql);
			if (query.getResultList() != null
					&& query.getResultList().size() > 0) {
				return query.getResultList();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void deleteNode(Object node, long topDID) {
		try {
			TopDiagramEntity diagramEntity = manager.find(
					TopDiagramEntity.class, topDID);
			if (node instanceof FEPTopoNodeEntity) {
				FEPTopoNodeEntity nodeEntity = (FEPTopoNodeEntity) node;
				if (nodeEntity.getId() > 0) {

					if (diagramEntity.getNodes() != null
							&& diagramEntity.getNodes().size() > 0) {
						for (NodeEntity entity : diagramEntity.getNodes()) {
							if (entity.getId().equals(nodeEntity.getId())) {
								diagramEntity.getNodes().remove(entity);
								manager.merge(diagramEntity);
								break;
							}
						}

					}
					nodeEntity = manager.merge(nodeEntity);
					manager.remove(nodeEntity);

				}

			}
		} catch (Exception e) {
			logger.error("删除节点", e);
		}

	}

	public List<Switch3VlanEnity> querySwitch3VlanEnity(String ipValue) {
		String sql = "select c from Switch3VlanEnity c where c.ipValue=:ipValue";
		Query query = manager.createQuery(sql);
		query.setParameter("ipValue", ipValue);
		if (query != null && query.getResultList().size() > 0) {
			return query.getResultList();
		}
		return null;
	}
	
	public List<SwitchPortLevel3> querySwitch3PortEnity(String ipValue) {
		String sql = "select c from SwitchPortLevel3 c where c.switchLayer3Ip=:ipValue";
		Query query = manager.createQuery(sql);
		query.setParameter("ipValue", ipValue);
		if (query != null && query.getResultList().size() > 0) {
			return query.getResultList();
		}
		return null;
	}
	
	/**
	 * 判断是否是超级管理员
	 * @param userEntity
	 * @return
	 */
	private boolean isSuperAdmin(UserEntity userEntity){
		boolean bool = true;//超级管理员
		
		if (userEntity == null || userEntity.getRole() == null){
			bool = true;
		}
		
		//如果具有超级管理员权限,不过滤设备
		int roleCode = userEntity.getRole().getRoleCode();
		if (roleCode == Constants.ADMINCODE){
			bool = true;
		}
		else{
			bool = false;
		}
		
		return bool;
	}
	
	public UserEntity getSuperAdmin(){
		UserEntity userEntity = null;
		String sql = "select user from UserEntity user,RoleEntity role where user.role=role and user.role.roleCode=" 
			+ Constants.ADMINCODE; 
		Query query = manager.createQuery(sql);
		if (query.getResultList() != null && query.getResultList().size() > 0){
			userEntity = (UserEntity)query.getResultList().get(0);
		}

		return userEntity;
	}
	
	@SuppressWarnings("unchecked")
	public List<Object[]> queryDevice(String params,String name,Class<?> clazz,UserEntity userEntity){
		boolean flag =false;
		List<Object[]> list =new ArrayList<Object[]>();
		String sql="";
		if(clazz!=null){
		boolean isSuper = isSuperAdmin(userEntity);	//true:超级管理员  false:非超级管理员
		
		if(clazz.getName() .equals(SwitchTopoNodeEntity.class.getName())){
			//s.baseInfo.deviceName
			if(isSuper){
				sql ="select c.ipValue,s.deviceModel,c.name,c from SwitchTopoNodeEntity c,SwitchNodeEntity s where s.baseConfig.ipValue=c.ipValue" ;
			}
			else{
				sql ="select distinct c.ipValue,s.deviceModel,c.name,c from SwitchTopoNodeEntity c,SwitchNodeEntity s ,UserEntity user where s.baseConfig.ipValue=c.ipValue" 
					+ " and user in elements(c.users)";
			}
			
			if(params!=null&&!params.equals("")){
				sql +=" and c.ipValue like :params";
			}
			if(name!=null&&!name.equals("")){
				
				sql+=" and c.name like :name";
			}
		}else if(clazz.getName() .equals(SwitchTopoNodeLevel3.class.getName())){
			//s.name
			if(isSuper){
				sql ="select c.ipValue,s.deviceModel,c.name,c from SwitchTopoNodeLevel3 c,SwitchLayer3 s where s.ipValue=c.ipValue";
			}
			else{
				sql ="select distinct c.ipValue,s.deviceModel,c.name,c from SwitchTopoNodeLevel3 c,SwitchLayer3 s ,UserEntity user where s.ipValue=c.ipValue" 
					+ " and user in elements(c.users)";
			}
			
			if(params!=null&&!params.equals("")){
				sql +=" and c.ipValue like :params";
			}
			if(name!=null&&!name.equals("")){
				
				sql+=" and c.name	like :name";
			}
		}else if(clazz.getName() .equals(EponTopoEntity.class.getName())){
			//s.oltBaseInfo.deviceName
			if(isSuper){
				sql ="select c.ipValue,s.deviceModel,c.name,c from EponTopoEntity c,OLTEntity s where s.ipValue=c.ipValue";
			}
			else{
				sql ="select distinct c.ipValue,s.deviceModel,c.name,c from EponTopoEntity c,OLTEntity s ,UserEntity user where s.ipValue=c.ipValue" 
					+ " and user in elements(c.users)";
			}
			
			if(params!=null&&!params.equals("")){
				sql +=" and c.ipValue like :params";
			}
			if(name!=null&&!name.equals("")){
				
				sql+=" and c.name	like :name";
			}
		}else if(clazz.getName() .equals(GPRSTopoNodeEntity.class.getName())){
			if(isSuper){
				sql ="select c.userId,'',c.name,c from GPRSTopoNodeEntity c where 1=1";
			}
			else{
				sql ="select distinct c.userId,'',c.name,c from GPRSTopoNodeEntity c ,UserEntity user where 1=1" 
					+ " and user in elements(c.users)";
			}
			
			if(params!=null&&!params.equals("")){
				sql +=" and c.userId like :params";
			}
			if(name!=null&&!name.equals("")){
				
				sql+=" and c.name like :name";
			}
		}else if(clazz.getName() .equals(CarrierTopNodeEntity.class.getName())){
			if(isSuper){
				sql ="select c.carrierCode,c.carrierType,c.name,c from CarrierTopNodeEntity c where 1=1";
			}
			else{
				sql ="select distinct c.carrierCode,c.carrierType,c.name,c from CarrierTopNodeEntity c ,UserEntity user where 1=1" 
					+ " and user in elements(c.users)";
			}
			
			if(params!=null&&!params.equals("")){
				sql +=" and c.carrierCode=:params";
			}
			if(name!=null&&!name.equals("")){
				
				sql+=" and c.name like :name";
			}
			flag =true;
			
		}else if(clazz.getName() .equals(ONUTopoNodeEntity.class.getName())){
			if(isSuper){
				sql ="select c.macValue,'ONU',c.name,c from ONUTopoNodeEntity c where 1=1" ;
			}
			else{
				sql ="select distinct c.macValue,'ONU',c.name,c from ONUTopoNodeEntity c ,UserEntity user where 1=1" 
					+ " and user in elements(c.users)";
			}
			
			if(params!=null&&!params.equals("")){
				sql +=" and c.macValue like :params";
			}
			if(name!=null&&!name.equals("")){
				
				sql+=" and c.name like :name";
			}
		}
		else if(clazz.getName() .equals(VirtualNodeEntity.class.getName())){
			if(isSuper){
				sql ="select c.ipValue,s.type,c.name,c from VirtualNodeEntity c,VirtualType s where s.id=c.type";
			}
			else{
				sql ="select distinct c.ipValue,s.type,c.name,c from VirtualNodeEntity c,VirtualType s ,UserEntity user where s.id=c.type" 
					+ " and user in elements(c.users)";
			}
			
			if(params!=null&&!params.equals("")){
				sql +=" and c.ipValue like :params";
			}
			if(name!=null&&!name.equals("")){
				
				sql+=" and c.name like :name";
			}
		}
		
			Query query = manager.createQuery(sql);

			if (flag) {

				if (params != null && !params.equals("")) {
					if (Tools.isNumeric(params)) {
						query.setParameter("params", Integer.parseInt(params));
					} else {
						query.setParameter("params", null);
					}
				}

			} else {
				if (params != null && !params.equals("")) {
					query.setParameter("params", "%" + params + "%");
				}
			}
			if (name != null && !name.equals("")) {
				query.setParameter("name", "%" + name + "%");
			}
			if (query.getResultList() != null&& query.getResultList().size() > 0) {
				list.addAll(query.getResultList());
			}
		}
		if(clazz==null){
			List<Object[]> singleSwitchTopoNodeEntity =queryDevice(params, name, SwitchTopoNodeEntity.class,userEntity);
			List<Object[]> singleSwitchTopoNodeLevel3 =queryDevice(params, name, SwitchTopoNodeLevel3.class,userEntity);
			List<Object[]> singleEponTopoEntity =queryDevice(params, name, EponTopoEntity.class,userEntity);
			List<Object[]> singleGPRSTopoNodeEntity =queryDevice(params, name, GPRSTopoNodeEntity.class,userEntity);
			List<Object[]> singleCarrierTopNodeEntity =queryDevice(params, name, CarrierTopNodeEntity.class,userEntity);
			List<Object[]> singleONUTopoNodeEntity =queryDevice(params, name, ONUTopoNodeEntity.class,userEntity);
			List<Object[]> singleVirtualNodeEntity =queryDevice(params, name, VirtualNodeEntity.class,userEntity);
			list.addAll(singleSwitchTopoNodeEntity);
			list.addAll(singleSwitchTopoNodeLevel3);
			list.addAll(singleEponTopoEntity);
			list.addAll(singleGPRSTopoNodeEntity);
			list.addAll(singleCarrierTopNodeEntity);
			list.addAll(singleONUTopoNodeEntity);
			list.addAll(singleVirtualNodeEntity);
		}
		
		return list;
		
	}
	
	/**
	 * 因为UserEntity中的nodeEntity是延迟加载，所以提供方法通过userID直接查询数据库
	 * @param userID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<NodeEntity> queryNodeEntityByUser(long userID){
//		String sql1 = "select distinct node from UserEntity user,NodeEntity node where user.id="+ userID 
//						+ " and node in elements(user.nodes)";
		String sql1 = "select distinct node from UserEntity user,NodeEntity node where user.id="+ userID 
		+ " and user in elements(node.users)";
		Query query = manager.createQuery(sql1);
		if (query != null && query.getResultList().size() > 0) {
			return query.getResultList();
		}
		return null;
	}
	
	/**
	 * 通过nodeID直接查询数据库
	 * @param nodeID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<UserEntity> queryUserEntityByNode(long nodeID){
		String sql1 = "select distinct user from UserEntity user,NodeEntity node where node.id="+ nodeID 
		+ " and node in elements(user.nodes)";
		Query query = manager.createQuery(sql1);
		if (query != null && query.getResultList().size() > 0) {
			return query.getResultList();
		}
		return null;
	}
	
	/**
	 * 告警消息确认操作
	 * 归并规则：
	 * 1：根据设备IP地址、告警类别和告警事件，如果数据库中有此历史记录
	 * 更新表WarningHistoryEntity中的发生时间、确认时间、频次三个字段
	 * 然后再更新表WarningEntity
	 * 
	 *:2：根据设备IP地址、告警类别和告警事件，如果数据库中没有此历史记录
	 * 在表WarningHistoryEntity中增加一条记录,然后再更新表WarningEntity
	 * 
	 * 按照设备IP地址与告警类别确认后归并到告警历史中
	 * @param warningEntities
	 */
	public synchronized void confirmWarningInfo(List<WarningEntity> warningEntities){
		if (warningEntities == null || warningEntities.size() < 1){
			return;
		}
		
		for(WarningEntity warningEntity : warningEntities){
			/**
			 * 通过传入的WarningEntity的id到数据库中查询相应的数据,
			 * 如果数据库中这条记录的确认状态为已确认的话,那么就不用
			 * 更新告警历史的字段值了。
			 */
			String sql2 = "select w from WarningEntity w where w.id=" + warningEntity.getId();
			Query query2 = manager.createQuery(sql2);
			if (query2 != null && query2.getResultList().size() > 0){
				WarningEntity entity = (WarningEntity)query2.getResultList().get(0);
				if (entity.getCurrentStatus() == Constants.CONFIRM){
					continue;
				}
			}
			
			///////////
			String ip = warningEntity.getIpValue();
			int warningCategory = warningEntity.getWarningCategory();
			int warningEvent = warningEntity.getWarningEvent();
			
			String sql = "select distinct history from WarningHistoryEntity history where history.ipValue='" 
				+ ip +"' and history.warningCategory=" + warningCategory + " and history.warningEvent=" + warningEvent; 
			
			Query query = manager.createQuery(sql);
			//当没有在数据库中查询到时，在WarningHistoryEntity中增加此记录
			if (query == null || query.getResultList().size() < 1) {
				WarningHistoryEntity history = new WarningHistoryEntity();
				history.setIpValue(warningEntity.getIpValue());     //ip地址
//				history.setNodeName(warningEntity.getNodeName());   //告警来源
				history.setNodeName(warningEntity.getIpValue());   //考虑到节点名称会改变,所以告警来源也用ip地址
				
				history.setWarningEvent(warningEntity.getWarningEvent());//告警事件
				history.setWarningLevel(warningEntity.getWarningLevel());//告警级别
				history.setWarningCategory(warningEntity.getWarningCategory()); //告警类别
				history.setFirstTime(warningEntity.getCreateDate()); //首次告警时间
				history.setLastTime(warningEntity.getCreateDate()); //最近告警时间
				
				WarningEventDescription eventDescription = WarningEventDescription.getInstance();
				String description = eventDescription.getWarningEventDescription(warningEntity.getWarningEvent());
				history.setContent(description);  //告警内容
				
				history.setWarningCount(1);  //因为是重新增加一条,所以告警次数为1
				history.setLastConfirmTime(warningEntity.getConfirmTime());//最近的确认时间
				
				commonService.saveEntity(history);//增加一条历史记录
				
				//再查找新增的这条历史记录,得到ID值
				Query query1 = manager.createQuery(sql);
				if (query1 != null && query1.getResultList().size() > 0) {
					WarningHistoryEntity history1 = (WarningHistoryEntity)query1.getResultList().get(0);
					Long warningHistoryID = history1.getId();
					warningEntity.setWarningHistoryID(warningHistoryID);
					warningEntity.setCurrentStatus(Constants.CONFIRM);
					
					commonService.updateEntity(warningEntity);  //更新告警记录
				}
			}
			else{
				//如果已经有此记录的话，更新最近发生时间或首次发生时间、确认时间、频次
				WarningHistoryEntity history = (WarningHistoryEntity)query.getResultList().get(0);
				
				//得到告警历史的首次发生时间
				Date firstTime = history.getFirstTime();
				//得到告警历史的最近发生时间
				Date lastTime = history.getLastTime();
				//告警产生时间
				Date createDate = warningEntity.getCreateDate();
				
				//假如告警产生时间小于告警历史的首次发生时间,就把
				//告警产生时间设置成告警历史的首次发生时间。
				//否则,就把告警产生时间设置成告警历史的最近发生时间
				if(createDate.getTime() < firstTime.getTime()){
					history.setFirstTime(createDate);    //首次发生时间
				}
				else if (createDate.getTime() > lastTime.getTime()){
					history.setLastTime(createDate); //最近发生时间
				}
				else{
					//不更新history的首次发生时间和最近发生时间
				}
				history.setLastConfirmTime(warningEntity.getConfirmTime());//最近的确认时间
				history.setWarningCount(history.getWarningCount() + 1) ;//告警频次
				commonService.updateEntity(history);  //更新历史记录
				
				
				Long warningHistoryID = history.getId();
				warningEntity.setWarningHistoryID(warningHistoryID);
				warningEntity.setCurrentStatus(Constants.CONFIRM);
				commonService.updateEntity(warningEntity);  //更新告警记录
			}
		}
	}
	
	/**
	 * 通过输入的历史告警信息得到所有的WarningEntity
	 * @param history
	 * @return
	 */
	public synchronized List<WarningEntity> queryWarningFromHistory(WarningHistoryEntity history){
		String where = " where entity.warningHistoryID=" + history.getId();
		List<WarningEntity> warningEntities = (List<WarningEntity>)commonService.findAll(WarningEntity.class, where);
		
		return warningEntities; 
	}
	
	/**
	 * 通过WarningHistoryBean中的查询条件,
	 * 得到相应的告警历史信息的总数
	 * @param warningHistoryBean
	 * @return
	 */
	@Override
	public long getAllWarningHistoryCount(WarningHistoryBean warningHistoryBean){
		long count  = 0l;
		if (null == warningHistoryBean){
			return count;
		}
		
		Query query = null;
		String sql = "";
		if (warningHistoryBean.getSimpleConfirmTimeStr() == null){ //高级查询
			sql = "select count(w) from WarningHistoryEntity w where 1=1";
			
			Calendar startFirstTimeCal = null;
			Calendar endFirstTimeCal = null;
			
			Calendar startLastTimeCal = null;
			Calendar endLastTimeCal = null;
			
			Calendar startLastConfirmTimeCal = null;
			Calendar endLastConfirmTimeCal = null;
			
			if (warningHistoryBean.getStartFirstTime() != null) {
				startFirstTimeCal = this.setStartDate(warningHistoryBean.getStartFirstTime());
				sql += " and w.firstTime>=:startFirstTime";
				
			}
			if (warningHistoryBean.getEndFirstTime() != null) {
				endFirstTimeCal = this.setEndDate(warningHistoryBean.getEndFirstTime());
				sql += " and w.firstTime<=:endFirstTime";
			}
			if (warningHistoryBean.getStartLastTime() != null){
				startLastTimeCal = this.setStartDate(warningHistoryBean.getStartLastTime());
				sql += " and w.lastTime>=:startLastTime";
			}
			if (warningHistoryBean.getEndLastTime() != null){
				endLastTimeCal = this.setEndDate(warningHistoryBean.getEndLastTime());
				sql += " and w.lastTime<=:endLastTime";
			}
			if (warningHistoryBean.getStartLastConfirmTime() != null){
				startLastConfirmTimeCal = this.setStartDate(warningHistoryBean.getStartLastConfirmTime());
				sql += " and w.lastConfirmTime>=:startLastConfirmTime";
			}
			if (warningHistoryBean.getEndLastConfirmTime() != null){
				endLastConfirmTimeCal = this.setEndDate(warningHistoryBean.getEndLastConfirmTime());
				sql += " and w.lastConfirmTime<=:endLastConfirmTime";
			}
			
			if (warningHistoryBean.getWarningSouce() != null 
					&& !("".equals(warningHistoryBean.getWarningSouce()))){
				sql += " and w.nodeName like :warningSouce";
			}
			
			if (containWarningEvent(warningHistoryBean.getWarningEvent())){
				sql += " and w.warningEvent=:warningEvent";
			}
			
			if(warningHistoryBean.getWarningCount() > 0){
				sql += " and w.warningCount=:warningCount";
			}
			
			
			sql += " order by w.lastConfirmTime desc";
			query = manager.createQuery(sql);
			
			if (warningHistoryBean.getStartFirstTime() != null) {
				query.setParameter("startFirstTime", startFirstTimeCal.getTime());
			}
			if (warningHistoryBean.getEndFirstTime() != null) {
				query.setParameter("endFirstTime", endFirstTimeCal.getTime());
			}
			
			if (warningHistoryBean.getStartLastTime() != null){
				query.setParameter("startLastTime", startLastTimeCal.getTime());
			}
			if (warningHistoryBean.getEndLastTime() != null){
				query.setParameter("endLastTime", endLastTimeCal.getTime());
			}
			
			if (warningHistoryBean.getStartLastConfirmTime() != null){
				query.setParameter("startLastConfirmTime", startLastConfirmTimeCal.getTime());
			}
			if (warningHistoryBean.getEndLastConfirmTime() != null){
				query.setParameter("endLastConfirmTime", endLastConfirmTimeCal.getTime());
			}
			
			if (warningHistoryBean.getWarningSouce() != null 
					&& !("".equals(warningHistoryBean.getWarningSouce()))){
				query.setParameter("warningSouce", "%" + warningHistoryBean.getWarningSouce() + "%");
			}
			
			if (containWarningEvent(warningHistoryBean.getWarningEvent())){
				query.setParameter("warningEvent", warningHistoryBean.getWarningEvent());
			}
			
			if(warningHistoryBean.getWarningCount() > 0){
				query.setParameter("warningCount", warningHistoryBean.getWarningCount());
			}
		}
		else{//简单查询
			if(warningHistoryBean.getSimpleConfirmTimeStr().equals(Constants.WEEKLY)){//本周
				sql = "select count(w) from WarningHistoryEntity w where year(w.lastConfirmTime) = year(curdate()) " +
						"and month(w.lastConfirmTime) = month(curdate()) and week(w.lastConfirmTime) = week(curdate())";
			}
			else if (warningHistoryBean.getSimpleConfirmTimeStr().equals(Constants.MONTHLY)){//本月
				sql = "select count(w) from WarningHistoryEntity w where year(w.lastConfirmTime) = year(curdate()) " +
						"and month(w.lastConfirmTime) = month(curdate())";	
			}
			else if (warningHistoryBean.getSimpleConfirmTimeStr().equals(Constants.QUARTERLY)){//本季度
				sql = "select count(w) from WarningHistoryEntity w where year(w.lastConfirmTime) = year(curdate()) " +
						"and quarter(w.lastConfirmTime) = quarter(curdate())";
			}
			else if (warningHistoryBean.getSimpleConfirmTimeStr().equals(Constants.YEARLY)){//本年
				sql = "select count(w) from WarningHistoryEntity w where year(w.lastConfirmTime) = year(curdate())";
			}
			query = manager.createQuery(sql);
		}
		
		try{
			if (query.getResultList() != null 
					&& (query.getResultList().size() > 0)){
				count = (Long)query.getResultList().get(0);
			}
		}
		catch(Exception e){
			this.logger.error("getAllWarningHistoryCount() is error",e);
		}
		
		return count;
	}
	
	/**
	 * 通过WarningHistoryBean中的查询条件,
	 * 得到相应的告警历史信息列表
	 * @param warningHistoryBean
	 * @return
	 */
	@Override
	public List<WarningHistoryEntity> queryWarningHistory(WarningHistoryBean warningHistoryBean){
		List<WarningHistoryEntity> warningHistoryEntities = null;
		
		if (null == warningHistoryBean){
			return warningHistoryEntities = new ArrayList<WarningHistoryEntity>();
		}
		
		String sql = "";
		Query query = null;
		if (warningHistoryBean.getSimpleConfirmTimeStr() == null){ //高级查询
			sql = "select w from WarningHistoryEntity w where 1=1";
			
			Calendar startFirstTimeCal = null;
			Calendar endFirstTimeCal = null;
			
			Calendar startLastTimeCal = null;
			Calendar endLastTimeCal = null;
			
			Calendar startLastConfirmTimeCal = null;
			Calendar endLastConfirmTimeCal = null;
			
			if (warningHistoryBean.getStartFirstTime() != null) {
				startFirstTimeCal = this.setStartDate(warningHistoryBean.getStartFirstTime());
				sql += " and w.firstTime>=:startFirstTime";
				
			}
			if (warningHistoryBean.getEndFirstTime() != null) {
				endFirstTimeCal = this.setEndDate(warningHistoryBean.getEndFirstTime());
				sql += " and w.firstTime<=:endFirstTime";
			}
			if (warningHistoryBean.getStartLastTime() != null){
				startLastTimeCal = this.setStartDate(warningHistoryBean.getStartLastTime());
				sql += " and w.lastTime>=:startLastTime";
			}
			if (warningHistoryBean.getEndLastTime() != null){
				endLastTimeCal = this.setEndDate(warningHistoryBean.getEndLastTime());
				sql += " and w.lastTime<=:endLastTime";
			}
			if (warningHistoryBean.getStartLastConfirmTime() != null){
				startLastConfirmTimeCal = this.setStartDate(warningHistoryBean.getStartLastConfirmTime());
				sql += " and w.lastConfirmTime>=:startLastConfirmTime";
			}
			if (warningHistoryBean.getEndLastConfirmTime() != null){
				endLastConfirmTimeCal = this.setEndDate(warningHistoryBean.getEndLastConfirmTime());
				sql += " and w.lastConfirmTime<=:endLastConfirmTime";
			}
			
			if (warningHistoryBean.getWarningSouce() != null 
					&& !("".equals(warningHistoryBean.getWarningSouce()))){
				sql += " and w.nodeName like :warningSouce";
			}
			
			if (containWarningEvent(warningHistoryBean.getWarningEvent())){
				sql += " and w.warningEvent=:warningEvent";
			}
			
			if(warningHistoryBean.getWarningCount() > 0){
				sql += " and w.warningCount=:warningCount";
			}
			
			sql += " order by w.lastConfirmTime desc";
			query = manager.createQuery(sql);
			if (warningHistoryBean.getStartPage() > 1) {
				query.setFirstResult((warningHistoryBean.getStartPage() - 1)
						* warningHistoryBean.getMaxPageSize());
			} else if (warningHistoryBean.getStartPage() == 1) {
				query.setFirstResult(0);
			}

			if (warningHistoryBean.getMaxPageSize() > 0) {
				query.setMaxResults(warningHistoryBean.getMaxPageSize());
			}

			if (warningHistoryBean.getStartFirstTime() != null) {
				query.setParameter("startFirstTime", startFirstTimeCal.getTime());
			}
			if (warningHistoryBean.getEndFirstTime() != null) {
				query.setParameter("endFirstTime", endFirstTimeCal.getTime());
			}
			
			if (warningHistoryBean.getStartLastTime() != null){
				query.setParameter("startLastTime", startLastTimeCal.getTime());
			}
			if (warningHistoryBean.getEndLastTime() != null){
				query.setParameter("endLastTime", endLastTimeCal.getTime());
			}
			
			if (warningHistoryBean.getStartLastConfirmTime() != null){
				query.setParameter("startLastConfirmTime", startLastConfirmTimeCal.getTime());
			}
			if (warningHistoryBean.getEndLastConfirmTime() != null){
				query.setParameter("endLastConfirmTime", endLastConfirmTimeCal.getTime());
			}
			
			if (warningHistoryBean.getWarningSouce() != null 
					&& !("".equals(warningHistoryBean.getWarningSouce()))){
				query.setParameter("warningSouce", "%" + warningHistoryBean.getWarningSouce() + "%");
			}
			
			if (containWarningEvent(warningHistoryBean.getWarningEvent())){
				query.setParameter("warningEvent", warningHistoryBean.getWarningEvent());
			}
			
			if(warningHistoryBean.getWarningCount() > 0){
				query.setParameter("warningCount", warningHistoryBean.getWarningCount());
			}
		}
		else{//简单查询
			if(warningHistoryBean.getSimpleConfirmTimeStr().equals(Constants.WEEKLY)){//本周
				sql = "select w from WarningHistoryEntity w where year(w.lastConfirmTime) = year(curdate()) " +
						"and month(w.lastConfirmTime) = month(curdate()) and week(w.lastConfirmTime) = week(curdate())";
			}
			else if (warningHistoryBean.getSimpleConfirmTimeStr().equals(Constants.MONTHLY)){//本月
				sql = "select w from WarningHistoryEntity w where year(w.lastConfirmTime) = year(curdate()) " +
						"and month(w.lastConfirmTime) = month(curdate())";	
			}
			else if (warningHistoryBean.getSimpleConfirmTimeStr().equals(Constants.QUARTERLY)){//本季度
				sql = "select w from WarningHistoryEntity w where year(w.lastConfirmTime) = year(curdate()) " +
						"and quarter(w.lastConfirmTime) = quarter(curdate())";
			}
			else if (warningHistoryBean.getSimpleConfirmTimeStr().equals(Constants.YEARLY)){//本年
				sql = "select w from WarningHistoryEntity w where year(w.lastConfirmTime) = year(curdate())";
			}
			
			sql += " order by w.lastConfirmTime desc";
			
			query = manager.createQuery(sql);
			if (warningHistoryBean.getStartPage() > 1) {
				query.setFirstResult((warningHistoryBean.getStartPage() - 1)
						* warningHistoryBean.getMaxPageSize());
			} else if (warningHistoryBean.getStartPage() == 1) {
				query.setFirstResult(0);
			}

			if (warningHistoryBean.getMaxPageSize() > 0) {
				query.setMaxResults(warningHistoryBean.getMaxPageSize());
			}
		}
		
		try{
			warningHistoryEntities = query.getResultList();
		}
		catch(Exception e){
			this.logger.error("queryWarningHistory() is error",e);
		}

		return warningHistoryEntities;
	}
	
	private boolean containWarningEvent(int warningEvent){
		boolean bool = false;
		switch(warningEvent){
			case Constants.COLDSTART:{
				bool = true;
				break;
			}
			case Constants.WARMSTART:{
				bool = true;
				break;
			}
			case Constants.LINKDOWN:{
				bool = true;
				break;
			}
			case Constants.LINKUP:{
				bool = true;
				break;
			}
			case Constants.AUTHENTICATIONFAILURE:{
				bool = true;
				break;
			}
			case Constants.EGPNEIGHORLOSS:{
				bool = true;
				break;
			}
			case Constants.ENTERPRISESPECIFIC:{
				bool = true;
				break;
			}
			case Constants.REMONTHING:{
				bool = true;
				break;
			}
			case Constants.PINGOUT:{
				bool = true;
				break;
			}
			case Constants.PINGIN:{
				bool = true;
				break;
			}
			case Constants.DATABASE_DISCONNECT:{
				bool = true;
				break;
			}
			case Constants.DATABASE_CONNECT:{
				bool = true;
				break;
			}
			case Constants.FEP_DISCONNECT:{
				bool = true;
				break;
			}
			case Constants.FEP_CONNECT:{
				bool = true;
				break;
			}
			case Constants.SYSLOG:{
				bool = true;
				break;
			}
			case Constants.OTHERWARNING:{
				bool = true;
				break;
			}
			default:{
				bool = false;
				break;
			}
		}
		
		return bool;
	}
	
	/**
	 * 通过IP列表得到SwitchTopoNodeEntity
	 * @param ips
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<SwitchTopoNodeEntity> getSwitchTopoNodeByIps(List<String> ips){
		List<SwitchTopoNodeEntity> listnodeList = null;
		if (ips.size() > 0){
			String sql1 = "select node from SwitchTopoNodeEntity node where node.ipValue in (:ipValues)";
			Query query1 = manager.createQuery(sql1); 
			query1.setParameter("ipValues",ips); 
			if (null != query1){
				listnodeList = (List<SwitchTopoNodeEntity>)query1.getResultList();
			}
		}
		
		return listnodeList;
	}
	
	/**
	 * 通过sysLogHostEntity查询所有关联的SysLogHostToDevEntity
	 * @param sysLogHostEntity
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public List<SysLogHostToDevEntity> getSysLogHostToDevBySysLog(SysLogHostEntity sysLogHostEntity){
		List<SysLogHostToDevEntity> devEntities = null;

		if (sysLogHostEntity != null){
			String sql = "select dev from SysLogHostToDevEntity dev where dev.sysLogHostEntity.id=" + sysLogHostEntity.getId();
			Query query = manager.createQuery(sql);
			if (query != null && query.getResultList().size() > 0) {
				devEntities = (List<SysLogHostToDevEntity>)query.getResultList();
			}
		}
		
		return devEntities;
	}
	
	/**
	 * 只保存到数据库中
	 * @param devEntityList
	 * @param hostEntity
	 */
	@Override
	@SuppressWarnings("unchecked")
	public synchronized void saveSysLogHosts(List<SysLogHostToDevEntity> devEntityList,SysLogHostEntity hostEntity){
		try{
			//如果devEntityList为null时，只保存hostEntity
			if (null == devEntityList || devEntityList.size() < 0){
				String sql = "select host from SysLogHostEntity host where host.hostIp='" + hostEntity.getHostIp() 
								+ "'" + " and host.hostPort=" + hostEntity.getHostPort();
				Query query = manager.createQuery(sql);
				SysLogHostEntity entity = (SysLogHostEntity)query.getSingleResult();
				if(entity == null){
					commonService.saveEntity(hostEntity);
				}
			}
			else{
				//第一步 :在SysLogHostToDevEntity中找到数据库中ip地址和主机ID相同的记录，如果有删除数据库中的这条数据
				for(SysLogHostToDevEntity devEntity : devEntityList){
					String sql = "select dev from SysLogHostToDevEntity dev where dev.ipValue='" + devEntity.getIpValue()
								+ "'" + " and dev.hostID=" + devEntity.getHostID();
					Query query = manager.createQuery(sql);
					List<?> devs = query.getResultList();
					if ((devs != null) && (devs.size() >0)){
						commonService.deleteEntities(devs);
						commonService.commit();
					}
				}
				
				//第二步 :声明entity
				SysLogHostEntity entity = hostEntity;
				
				//第三步 :根据SysLogHostEntity的hostIp和hostPort到数据库中查询，如果没有就把这条记录增加，有的话就不管它
				String sql1= "select host from SysLogHostEntity host where host.hostIp='" + hostEntity.getHostIp() 
					+ "'" + " and host.hostPort=" + hostEntity.getHostPort();
				Query query1 = manager.createQuery(sql1);
				List<?> datas = query1.getResultList();
				if (datas == null || datas.size() < 1){
					entity = (SysLogHostEntity)commonService.saveEntity(hostEntity);
					commonService.commit();
				}

				//第四步 :再把有ID的对象entity赋予devEntityList
				for(int count = 0 ; count< devEntityList.size() ; count++){
					SysLogHostToDevEntity entitys = devEntityList.get(count);
					entitys.setSysLogHostEntity(hostEntity);
					if (entitys.getId() == null){
						commonService.saveEntity(entitys);
						
//						entitys.setSysLogHostEntity(null);
//						commonService.saveEntity(entitys);
//						
//						entitys.setSysLogHostEntity(hostEntity);
//						commonService.updateEntity(entitys);
					}
					else{
						commonService.updateEntity(entitys);
					}
				}

			}
		}
		catch(Exception ee){
			ee.printStackTrace();
		}
	}
	
	/**
	 * 删除SysLogToDevEntity
	 * @param devEntityList
	 * @param from
	 * @param clientIp
	 * @param operateType
	 */
	@Override
	@SuppressWarnings("unchecked")
	public synchronized void deleteSysLogToDevEntity(List<SysLogHostToDevEntity> devEntityList,String from,String clientIp,int operateType){
		try{
			if (null == devEntityList || devEntityList.size() < 0){
				return;
			}
			if (operateType == Constants.SYN_ALL) {
				//第一步:判断前置机是否在线
				String ip = devEntityList.get(0).getIpValue();
				boolean isFepOnline = datacache.isFEPOnline(ip);
				if (!isFepOnline){
					smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE, "server",from, clientIp, "前置机不在线");
					return;
				}
				
				//第二步:删除数据库
				commonService.deleteEntities(devEntityList);
				
				//第三步:发送删除消息到前置机
				try {
					Serializable serializable = (Serializable)devEntityList;
					sendMessageService.sendSysLogToDevDelMessage(serializable,from,clientIp,operateType);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
			else if(operateType == Constants.SYN_SERVER){
				commonService.deleteEntities(devEntityList);
			}
		}
		catch(Exception ee){
			
		}
		
		
	}

	/**
	 * 配置syslog主机信息
	 * @param devEntityList  syslogToDev实体信息
	 * @param from           客户端用户名
	 * @param clientIp       客户端IP
	 * @param operateType    网管侧or设备侧
	 */
	@Override
	@SuppressWarnings("unchecked")
	public synchronized void configSyslogHost(List<SysLogHostToDevEntity> devEntityList,String from,String clientIp,int operateType){
		try{
			if (null == devEntityList || devEntityList.size() < 0){
				this.logger.error("List<SysLogHostToDevEntity> devEntityList实体为null,请客户端重新检查");
				return;
			}
			
			//第一步:判断前置机是否在线
			String ip = devEntityList.get(0).getIpValue();
			boolean isFepOnline = datacache.isFEPOnline(ip);
			if (!isFepOnline){
				smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE, "server",from, clientIp, "前置机不在线");
				return;
			}
			
			//第二步 :在SysLogHostToDevEntity中找到数据库中ip地址和主机ID相同的记录，如果有删除数据库中的这条数据
			for(SysLogHostToDevEntity devEntity : devEntityList){
				String sql = "select dev from SysLogHostToDevEntity dev where dev.ipValue='" + devEntity.getIpValue()
							+ "'" + " and dev.hostID=" + devEntity.getHostID();
				Query query = manager.createQuery(sql);
				List<?> devs = query.getResultList();
				if ((devs != null) && (devs.size() >0)){
					commonService.deleteEntities(devs);
					commonService.commit();
				}
				
			}
			
			//第三步:得到SysLogHostEntity
			SysLogHostEntity hostEntity = devEntityList.get(0).getSysLogHostEntity();
			
			//第四步:根据SysLogHostEntity的hostIp和hostPort到数据库中查询，如果没有就把这条记录增加，有的话就不管它
			String sql1= "select host from SysLogHostEntity host where host.hostIp='" + hostEntity.getHostIp() 
							+ "'" + " and host.hostPort=" + hostEntity.getHostPort();
			Query query1 = manager.createQuery(sql1);
			List<?> hosts = query1.getResultList();
			if (hosts == null || hosts.size() < 1){
				//返回的hostEntity有ID
				hostEntity = (SysLogHostEntity)commonService.saveEntity(hostEntity);
				commonService.commit();
			}

			//第五步:根据网管侧还是设备侧判断是否发给前置机，并且保存devEntityList
			if (operateType == Constants.SYN_ALL) {
				for(int i = 0 ; i< devEntityList.size() ; i++){
					//再把有ID的对象hostEntity赋予devEntityList
					devEntityList.get(i).setSysLogHostEntity(hostEntity);
				}
				
				//保存数据库,返回的列表中已经有ID了。
				ArrayList listss =new ArrayList();
				for(int count = 0 ; count< devEntityList.size() ; count++){
					SysLogHostToDevEntity entitys = devEntityList.get(count);
					entitys.setSysLogHostEntity(hostEntity);
					if (entitys.getId() == null){
//						entitys.setSysLogHostEntity(null);
//						commonService.saveEntity(entitys);
//						
//						entitys.setSysLogHostEntity(hostEntity);
//						Object object = commonService.updateEntity(entitys);
						
						Object object = commonService.saveEntity(entitys);
						listss.add(object);
					}
					else{
						Object object = commonService.updateEntity(entitys);
						listss.add(object);
					}
				}
				
				Serializable serializable = (Serializable)listss;
				
				
				//向前置机发送消息
				try {
					sendMessageService.sendSysLogConfigMessage(serializable,from,clientIp,operateType);
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
			else if(operateType == Constants.SYN_SERVER){			
				for(int count = 0 ; count< devEntityList.size() ; count++){
					SysLogHostToDevEntity entitys = devEntityList.get(count);
					entitys.setSysLogHostEntity(hostEntity);
					if (entitys.getId() == null){
						commonService.saveEntity(entitys);
					}
					else{
						commonService.updateEntity(entitys);
					}
				}
			}	
		}
		catch(Exception ee){
			ee.printStackTrace();
		}
	}
	
	public synchronized void deleteSyslogHost(List<SysLogHostToDevEntity> devEntityList,String from,String clientIp,int operateType){
		
	}
	
	/**
	 * 通过SysLogHostEntity的主机IP和主机端口判断数据库中是否已经有这条记录了，
	 * 如果没有就保存这条记录
	 * 上载时调用
	 * @param hostEntity
	 */
	@Override
	@SuppressWarnings("unchecked")
	public SysLogHostEntity saveSysLogHostDB(SysLogHostEntity hostEntity){
		SysLogHostEntity entity = hostEntity;
		String sql = "select host from SysLogHostEntity host where host.hostIp=:hostIp and host.hostPort=:hostPort";
		Query query = manager.createQuery(sql);
		query.setParameter("hostIp", hostEntity.getHostIp());
		query.setParameter("hostPort", hostEntity.getHostPort());
		
		//说明数据库中没有这条主机记录，需要保存该信息
		if (query == null || query.getResultList().size() < 1){
			entity = (SysLogHostEntity)commonService.saveEntity(hostEntity);
			commonService.commit();
		}
		else{
			entity = (SysLogHostEntity)query.getResultList().get(0);
		}
		
		return entity;
	}
}