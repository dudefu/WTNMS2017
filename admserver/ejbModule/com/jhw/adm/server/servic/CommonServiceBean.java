package com.jhw.adm.server.servic;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Table;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.LocalBinding;
import org.jboss.ejb3.annotation.RemoteBinding;

import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.carriers.CarrierRouteEntity;
import com.jhw.adm.server.entity.epon.EponSplitter;
import com.jhw.adm.server.entity.epon.OLTBaseInfo;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.epon.OLTPort;
import com.jhw.adm.server.entity.epon.ONUEntity;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.nets.IPSegment;
import com.jhw.adm.server.entity.nets.VlanConfig;
import com.jhw.adm.server.entity.nets.VlanEntity;
import com.jhw.adm.server.entity.ports.LLDPInofEntity;
import com.jhw.adm.server.entity.ports.QOSStormControl;
import com.jhw.adm.server.entity.ports.RingConfig;
import com.jhw.adm.server.entity.switchs.SNMPHost;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.switchs.SysLogHostToDevEntity;
import com.jhw.adm.server.entity.system.AreaEntity;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.tuopos.CarrierTopNodeEntity;
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
import com.jhw.adm.server.entity.util.AddressEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.GPRSEntity;
import com.jhw.adm.server.interfaces.CommonServiceBeanLocal;
import com.jhw.adm.server.util.CacheDatas;

/**
 * ���棬�޸ģ�ɾ�������ݿ�ͨ�ò�����sessionbean
 * 
 * @author ����
 * 
 */
@Stateless(mappedName = "CommonServiceBean")
@RemoteBinding(jndiBinding = "remote/CommonServiceBean")
@LocalBinding(jndiBinding = "local/CommonServiceBean")
public class CommonServiceBean implements CommonServiceBeanRemote,
		CommonServiceBeanLocal {

	private Logger logger = Logger.getLogger(CommonServiceBean.class.getName());
	@PersistenceContext(unitName = "adm2server")
	private EntityManager manager;
	@Resource
	SessionContext ctx;

	@Override
	public <T> T findById(Serializable id, Class<T> clazz) {
		T t = manager.find(clazz, id);
		return t;
	}
	@Override
	public List findAllBySwitch(SwitchNodeEntity sne, Class clazz) {
		String jpql = "select entity from " + clazz.getName()+ " as entity where entity.switchNode=?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, sne);
		List datas = query.getResultList();
		return datas;
	}

	@Override
	public Object saveEntity(Object obj) {
	
		try {
			if (obj != null) {
				if (obj instanceof FEPTopoNodeEntity) {
					manager.persist(obj);
					FEPEntity fepEntity = ((FEPTopoNodeEntity) obj).getFepEntity();
					String code = fepEntity.getCode();
					CacheDatas.getInstance().addFEPEntity(code, fepEntity);
				} else if(obj instanceof QOSStormControl){
					QOSStormControl control =(QOSStormControl) obj;
					if(control.getId()!=null){
						logger.info("save QOSStormControl id is not null");
						manager.merge(control);
					}else{
					manager.persist(control);
					}
				}else {
					manager.persist(obj);
				}
				this.commit();
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			logger.info(e);
		}
		return obj;
	}

	@Override
	public List<?> findAll(Class<?> clazz) {
			String jpql = "select entity from " + clazz.getName()+ " as entity ";
			Query query = manager.createQuery(jpql);
			List<?> datas = query.getResultList();
			return datas;
	}

	@Override
	public List<?> findAll(Class<?> clazz, String where) {
			String jpql = "select entity from " + clazz.getName()+ " as entity " + where;
			Query query = manager.createQuery(jpql);
			List<?> datas = query.getResultList();
			return datas;
	}

	@Override
	public List findAll(String jpql) {
			Query query = manager.createQuery(jpql);
			List<?> datas = query.getResultList();
			return datas;
	}

	@Override
	public List<?> findAll(Class<?> clazz, String where, Object[] parms) {
			String jpql = "select entity from " + clazz.getName()+ " as entity " + where;
			Query query = manager.createQuery(jpql);
			for (int i = 0; i < parms.length; i++) {
				query.setParameter(i + 1, parms[i]);
			}
			List<?> datas = query.getResultList();
			return datas;
	}

	@Override
	public Object saveEntities(List objs) {
		List list = new ArrayList();
		if (objs != null) {
			for (Object obj : objs) {
				manager.persist(obj);
				list.add(obj);
			}
		}
		return list;
	}

	@Override
	public void deleteEntities(List<?> entities) {
		if (entities != null) {
			for (Object entity : entities) {
				deleteEntity(entity);
			}
		}
	}

	@Override
	public void deleteEntities(Set entities) {
			if (entities != null) {
				for (Object entity : entities) {
					deleteEntity(entity);
				}
			}
	}

	@Override
	public void deleteEntity(Object entity) {

		try {
			if (entity != null) {
				if (entity instanceof LinkEntity) {
					LinkEntity linkEntity = (LinkEntity) entity;
					if (linkEntity.getId() != null) {
						linkEntity = manager.find(LinkEntity.class, linkEntity.getId());
						if (linkEntity != null) {
							manager.remove(linkEntity);
						}
					}
				} else if (entity instanceof FEPEntity) {
					FEPEntity fepEntity = (FEPEntity) entity;
					if (fepEntity.getId() != null) {
						fepEntity = manager.find(FEPEntity.class, fepEntity.getId());
						if (fepEntity != null) {
							manager.remove(fepEntity);
							CacheDatas.getInstance().removeFEP(fepEntity.getCode());
						}
					}
				} else if (entity instanceof NodeEntity) {
					NodeEntity nodeEntity = (NodeEntity) entity;
					if (nodeEntity.getId() != null) {
						nodeEntity = manager.find(NodeEntity.class, nodeEntity.getId());
						if (nodeEntity != null) {
							manager.remove(nodeEntity);
						}
					}
				} else if (entity instanceof CarrierEntity) {
					CarrierEntity nodeEntity = (CarrierEntity) entity;
					if (nodeEntity.getId() != null) {
						nodeEntity = manager.find(CarrierEntity.class,nodeEntity.getId());
						if (nodeEntity != null) {
							manager.remove(nodeEntity);
						}
					}
				} else if (entity instanceof CarrierRouteEntity) {
					CarrierRouteEntity routeEntity = (CarrierRouteEntity) entity;
					if (routeEntity != null) {
						if (routeEntity.getId() != null) {
							routeEntity = manager.find(CarrierRouteEntity.class, routeEntity.getId());
							if (routeEntity != null) {
								manager.remove(routeEntity);
							}
						}
					}
				} else if (entity instanceof OLTPort) {
					OLTPort oltPort = (OLTPort) entity;
					if (oltPort.getId() != null) {
						oltPort = manager.find(OLTPort.class, oltPort.getId());
						if (oltPort != null) {
							manager.remove(oltPort);
						}
					}
				} else if (entity instanceof SwitchLayer3) {
					SwitchLayer3 switchLayer3 = (SwitchLayer3) entity;
					if (switchLayer3.getId() != null) {
						switchLayer3 = manager.find(SwitchLayer3.class,switchLayer3.getId());
						if (switchLayer3 != null) {
							manager.remove(switchLayer3);
						}
					}
				} else if (entity instanceof GPRSEntity) {
					GPRSEntity gprsEntity = (GPRSEntity) entity;
					if (gprsEntity != null) {
						if (gprsEntity.getId() != null) {
							gprsEntity = manager.find(GPRSEntity.class,
									gprsEntity.getId());
							if (gprsEntity != null) {
								manager.remove(gprsEntity);
							}

						}

					}

				} else if (entity instanceof SNMPHost) {
					SNMPHost snmpHost = (SNMPHost) entity;
					if (snmpHost != null) {
						if (snmpHost.getId() != null) {
							snmpHost = manager.find(SNMPHost.class,snmpHost.getId());
							if (snmpHost != null) {
								manager.remove(snmpHost);
							}
						}
					}

				} else if (entity instanceof RingConfig) {
					RingConfig ringConfig = (RingConfig) entity;
					if (ringConfig != null) {
						if (ringConfig.getId() != null) {
							ringConfig = manager.find(RingConfig.class,ringConfig.getId());
							if (ringConfig != null) {
								manager.remove(ringConfig);
							}
						}
					}

				}else if (entity instanceof SysLogHostToDevEntity) {
					SysLogHostToDevEntity devEntity = (SysLogHostToDevEntity) entity;
					if (devEntity.getId() != null) {
						devEntity = manager.find(SysLogHostToDevEntity.class, devEntity.getId());
						if (devEntity != null) {
							manager.remove(devEntity);
						}
					}
				}else {
					Object o = manager.merge(entity);
					if (o instanceof VlanEntity) {
						VlanEntity ve = (VlanEntity) o;
						VlanConfig vc = ve.getVlanConfig();
						if (vc != null) {
							vc.getVlanEntitys().remove(ve);
						}
						manager.remove(ve);
					} else if (o instanceof IPSegment) {
						IPSegment is = (IPSegment) o;
						FEPEntity fep = is.getFepEntity();
						fep.getSegment().remove(is);
						manager.remove(is);
					} else if (o instanceof LLDPInofEntity) {
						LLDPInofEntity lldp = (LLDPInofEntity) o;
						int deviceType = lldp.getLocalDeviceType();
						if (deviceType == Constants.DEV_OLT) {
							String ipValue = lldp.getLocalIP();
							OLTEntity olt = getOLTByIpValue(ipValue);
							if (olt != null) {
								Set<LLDPInofEntity> lldps = olt.getLldpinfos();
								for (LLDPInofEntity l : lldps) {
									if (l.getId().longValue() == lldp.getId().longValue()) {
										lldps.remove(l);
										break;
									}
								}
							}
						} else if (deviceType == Constants.DEV_ONU) {
							String macValue = lldp.getLocalIP();
							ONUEntity onu = getOnuByMacValue(macValue);
							if (onu != null) {
								Set<LLDPInofEntity> lldps = onu.getLldpinfos();
								for (LLDPInofEntity l : lldps) {
									if (l.getId().longValue() == lldp.getId().longValue()) {
										lldps.remove(l);
										break;
									}
								}
							}
						} else if (deviceType == Constants.DEV_SWITCHER2) {
							String ipValue = lldp.getLocalIP();
							SwitchNodeEntity switcher = getSwitchByIp(ipValue);
							if (switcher != null) {
								Set<LLDPInofEntity> lldps = switcher.getLldpinfos();
								for (LLDPInofEntity l : lldps) {
									if (l.getId().longValue() == lldp.getId().longValue()) {
										lldps.remove(l);
										break;
									}
								}
							}
						} else if (deviceType == Constants.DEV_SWITCHER3) {

						}

					} else if (o instanceof AreaEntity) {
						AreaEntity ae = (AreaEntity) o;
						deleteArea(ae);
					} else {
						manager.remove(o);
					}
				}
			}
		} catch (Exception e) {
			logger.info("ͨ��ɾ�� " + e);
		}
	}

	public boolean deleteArea(List<AreaEntity> list) {
		boolean flag = true;
		
			if (list != null && list.size() > 0) {
				a: for (AreaEntity areaEntity : list) {
					Object[] obj = { areaEntity };
					List<AddressEntity> addressEntities = (List<AddressEntity>) findAll(AddressEntity.class, " where entity.area=?", obj);
					if (addressEntities.size() > 0) {
						flag = false;
						break a;
					}
				}
			}
		return flag;
	}

	private void deleteArea(AreaEntity areaEntity) {
		List us = findAll(UserEntity.class);
		if (us != null) {
			for (int i = 0; i < us.size(); i++) {
				UserEntity u = (UserEntity) us.get(i);
				List<AreaEntity> areas = u.getAreas();
				if (areas == null) {
					continue;
				}
				for (AreaEntity area : areas) {
					if (areaEntity.getId().longValue() == area.getId().longValue()) {
						u.getAreas().remove(area);
						break;
					}
				}
			}
		}
		manager.remove(areaEntity);
	}

	public void deleteLLDPS(List<LLDPInofEntity> lldps) {
		if (lldps != null && lldps.size() > 0) {
			for (LLDPInofEntity lldp : lldps) {
				manager.remove(lldp);
			}
		}
	}

	@Override
	public void deleteEntity(Class clazz, Long id) {
		String jpql = "delete from " + clazz.getName()+ " as entity where entity.id = ?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, id);
		query.executeUpdate();
	}

	@Override
	public void deleteEntitys(Class clazz) {
		List datas = findAll(clazz);
		deleteEntities(datas);
	}

	@Override
	public Object updateEntity(Object entity) {
		if (entity != null) {
			Object obj = null;
			if (entity instanceof FEPEntity) {
				FEPEntity fepEntity = ((FEPEntity) entity);
				String code = fepEntity.getCode();
				obj = manager.merge(fepEntity);
				CacheDatas.getInstance().addFEPEntity(code, fepEntity);
			} else {
				obj = manager.merge(entity);
			}
			this.commit();
			return obj;
		}
		return null;
	}

	@Override
	public void updateEntities(List entitys) {
		if (entitys != null) {
			for (Object obj : entitys)
				manager.merge(obj);
		}
	}

	@Override
	public void execJPQL(String jpql) {
		Query query = manager.createQuery(jpql);
		query.executeUpdate();
		manager.flush();
	}

	@Override
	public String getTableNameByClass(Class clazz) {
		String tableName = null;
		Annotation anno = clazz.getAnnotation(Table.class);
		if (anno instanceof Table) {
			Table table = (Table) anno;
			tableName = table.name();
		}
		return tableName;
	}

	private String getEntityNameByClass(Class clazz) {
		String entityName = null;
		Annotation anno = clazz.getAnnotation(Entity.class);
		if (anno instanceof Entity) {
			Entity entity = (Entity) anno;
			entityName = entity.name();
		}
		return entityName;
	}

	@Override
	public TopDiagramEntity saveDiagram(TopDiagramEntity entity) {

		logger.info("��������");
		if (entity.getLines() != null) {
			logger.info("����ͼ������:" + entity.getLines().size());
		}
		TopDiagramEntity diagram = null;
		try {
			Long id = entity.getId();
			if (id == null) {
				entity.setLastTime(System.currentTimeMillis());
				manager.persist(entity);
				diagram = entity;
			} else {
				boolean ok = compareTime(entity);
				if (!ok) {
					return null;
				}
				Set<NodeEntity> nodes = entity.getNodes();
				for (NodeEntity node : nodes) {
					boolean flag = false;
					if (node.getId() == null) {
						if (node instanceof SwitchTopoNodeEntity) {
							SwitchTopoNodeEntity topo = (SwitchTopoNodeEntity) node;
							if (topo.getIpValue() != null&& !topo.getIpValue().equals("")) {
								SwitchNodeEntity switcher = topo.getNodeEntity();
								if (switcher.getId() == null) {
									manager.persist(switcher);
								}
								flag = true;
							}

						} else if (node instanceof SwitchTopoNodeLevel3) {
							SwitchTopoNodeLevel3 topo = (SwitchTopoNodeLevel3) node;
							if (topo.getIpValue() != null&& !topo.getIpValue().equals("")) {
								SwitchLayer3 switcher3 = topo.getSwitchLayer3();
								if (switcher3.getId() == null) {
									manager.persist(switcher3);
								}
								flag = true;
							}

						} else if (node instanceof EponTopoEntity) {

							EponTopoEntity eponTopoEntity = (EponTopoEntity) node;
							OLTEntity eponEntity = eponTopoEntity.getOltEntity();
							if (eponEntity.getId() == null) {
								manager.persist(eponEntity);
							}
							flag = true;

						} else if (node instanceof ONUTopoNodeEntity) {
							ONUTopoNodeEntity onuTopoNodeEntity = (ONUTopoNodeEntity) node;
							ONUEntity oltonuEntity = onuTopoNodeEntity.getOnuEntity();
							if (oltonuEntity.getId() == null) {
								manager.persist(oltonuEntity);
							}
							flag = true;

						} else if (node instanceof Epon_S_TopNodeEntity) {
							flag = true;
						} else if (node instanceof FEPTopoNodeEntity) {
							FEPTopoNodeEntity fepTopo = (FEPTopoNodeEntity) node;
							if (fepTopo.getIpValue() != null&& !fepTopo.getIpValue().equals("")) {
								FEPEntity fep = fepTopo.getFepEntity();
								if (fep.getId() == null) {
									manager.persist(fep);
								}
								flag = true;
							}

						} else if (node instanceof CarrierTopNodeEntity) {
							CarrierTopNodeEntity carrierTopNodeEntity = (CarrierTopNodeEntity) node;
							if (carrierTopNodeEntity.getCarrierCode() != 1) {
								CarrierEntity carrierEntity = carrierTopNodeEntity.getNodeEntity();
								if (carrierEntity.getId() == null) {
									manager.persist(carrierEntity);
								}
								flag = true;
							}
						} else if (node instanceof GPRSTopoNodeEntity) {
							GPRSTopoNodeEntity gprsTopoNodeEntity = (GPRSTopoNodeEntity) node;
							if (gprsTopoNodeEntity.getUserId() != null&& !gprsTopoNodeEntity.getUserId().equals("")) {
								GPRSEntity gprsEntity = gprsTopoNodeEntity.getEntity();
								if (gprsEntity.getId() == null) {
									manager.persist(gprsEntity);
								}
								flag = true;
							}

						} else if (node instanceof RingEntity) {
							flag = true;
						}
						if (flag) {
							manager.persist(node);
						}

					} else {
						if (node instanceof SwitchTopoNodeEntity) {
							SwitchTopoNodeEntity topo = (SwitchTopoNodeEntity) node;
							SwitchNodeEntity switcher = topo.getNodeEntity();

							if (switcher != null) {
								if (node.isModifyNode()) {
									manager.merge(switcher);
								}
								Set<LLDPInofEntity> lldps = switcher.getLldpinfos();
								if (lldps != null && lldps.size() > 0) {
									for (LLDPInofEntity lldp : lldps) {
										Long nowid = lldp.getId();
										if (nowid == null) {
											manager.persist(lldp);
											nowid = lldp.getId();
										}
										lldp.setId(nowid);
									}
								}
							}
						} else if (node instanceof EponTopoEntity) {
							EponTopoEntity oltTopo = (EponTopoEntity) node;
							OLTEntity olt = oltTopo.getOltEntity();
							if (olt != null) {
								if (node.isModifyNode()) {
									manager.merge(olt);
								}
								Set<LLDPInofEntity> lldps = olt.getLldpinfos();
								if (lldps != null && lldps.size() > 0) {
									for (LLDPInofEntity lldp : lldps) {
										Long nowid = lldp.getId();
										if (nowid == null) {
											manager.persist(lldp);
											nowid = lldp.getId();
										}
										lldp.setId(nowid);
									}
								}
							}
						} else if (node instanceof CarrierTopNodeEntity) {

							CarrierTopNodeEntity carrierTopNodeEntity = (CarrierTopNodeEntity) node;
							CarrierEntity carrierEntity = carrierTopNodeEntity.getNodeEntity();
							if (carrierEntity != null) {
								if (node.isModifyNode()) {
									manager.merge(carrierEntity);
								}

							}

						} else if (node instanceof GPRSTopoNodeEntity) {
							GPRSTopoNodeEntity gprsTopoNodeEntity = (GPRSTopoNodeEntity) node;
							GPRSEntity gprsEntity = gprsTopoNodeEntity.getEntity();
							if (gprsEntity != null) {
								if (node.isModifyNode()) {
									manager.merge(gprsEntity);
								}

							}

						} else if (node instanceof EponTopoEntity) {
							EponTopoEntity eponTopoEntity = (EponTopoEntity) node;
							OLTEntity oltEntity = eponTopoEntity.getOltEntity();
							if (oltEntity != null) {
								if (node.isModifyNode()) {
									manager.merge(oltEntity);
								}
							}

						} else if (node instanceof ONUTopoNodeEntity) {
							ONUTopoNodeEntity onuTopoNodeEntity = (ONUTopoNodeEntity) node;
							ONUEntity onuEntity = onuTopoNodeEntity.getOnuEntity();
							if (onuEntity != null) {
								if (node.isModifyNode()) {
									manager.merge(onuEntity);
								}
							}

						} else if (node instanceof SwitchTopoNodeLevel3) {
							SwitchTopoNodeLevel3 switchTopoNodeLevel3 = (SwitchTopoNodeLevel3) node;
							SwitchLayer3 switchLayer3 = switchTopoNodeLevel3
									.getSwitchLayer3();
							if (switchLayer3 != null) {
								if (node.isModifyNode()) {
									manager.merge(switchLayer3);
								}
							}

						} else if (node instanceof Epon_S_TopNodeEntity) {
							Epon_S_TopNodeEntity nodeEntity = (Epon_S_TopNodeEntity) node;
							EponSplitter eponSplitter = nodeEntity
									.getEponSplitter();
							if (eponSplitter != null) {
								if (node.isModifyNode()) {
									manager.merge(eponSplitter);
								}
							}
						}
						if (node.isModifyNode()) {
							node.setModifyNode(false);
							manager.merge(node);
						}
					}

				}
				// ��ȡҪɾ��������
				List<LinkEntity> delLinks = new ArrayList<LinkEntity>();
				List<LinkEntity> updateLinks = new ArrayList<LinkEntity>();
				Set<LinkEntity> links = entity.getLines();
				for (LinkEntity link : links) {
					if (!link.isSynchorized()) {
						if (link.getId() != null) {
							delLinks.add(link);
						}
					} else {
						LLDPInofEntity lldpInofEntity = link.getLldp();

						if (lldpInofEntity != null) {
							logger.info("����LLDP  LocalIP "+ lldpInofEntity.getLocalIP()+ "LocalPortNo"+ lldpInofEntity.getLocalPortNo());
							logger.info("����LLDP   RemoteIP "+ lldpInofEntity.getRemoteIP()+ "RemotePortNo"+ lldpInofEntity.getRemotePortNo());
							logger.info("����LLDP " + lldpInofEntity+ " Link Node1 id "+ link.getNode1().getId()+ "Link Node2 id "+ link.getNode2().getId());
						}
						if (lldpInofEntity != null) {
							if (lldpInofEntity.getId() == null) {
								manager.persist(lldpInofEntity);
							}
						}
						if (link.getId() == null) {
							link.setModifyLink(false);
							manager.persist(link);
						}
						if (link.isModifyLink()) {
							link.setModifyLink(false);
							updateLinks.add(link);
						}
					}
				}
				// ɾ�����ߺͽڵ�
				if (delLinks.size() > 0) {
					links.remove(delLinks);
					for (LinkEntity linkEntity : delLinks) {
						linkEntity = findById(linkEntity.getId(),LinkEntity.class);
						manager.remove(linkEntity);
					}
				}
				if (updateLinks.size() > 0) {
					this.updateLink(updateLinks);
				}
				// ��ȡҪɾ���Ľڵ�
				List<NodeEntity> delSubNode = new ArrayList<NodeEntity>();
				Set<NodeEntity> allnodes = entity.getNodes();
				for (NodeEntity node : allnodes) {
					if (node.getId() != null) {
						if (!node.isSynchorized()) {

							if (node instanceof SubNetTopoNodeEntity) {
								List<NodeEntity> childNode = this.queryNodeEntity(id,((SubNetTopoNodeEntity) node).getGuid());
								if (childNode != null && childNode.size() > 0) {
									for (NodeEntity nodeEntity : childNode) {
										nodeEntity.setParentNode(null);
										this.updateEntity(nodeEntity);
									}
								}

							}
							delSubNode.add(node);

						}
					}
				}
				if (delSubNode.size() > 0) {
					this.deleteDeviceS(delSubNode);
				}
				if (entity.getId() != null) {
					this.updateTopDiagramEntity(entity.getName(), entity.getId(), System.currentTimeMillis());
				} else {
					logger.info("����ͼΪ��");
				}
				diagram = new TopDiagramEntity();
			}
		} catch (Exception e) {
			
			logger.info("����ͼ�쳣** " + e);
		}finally{
		CacheDatas.getInstance().setRefreshing(false);
		}
		return diagram;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateLink(List<LinkEntity> list) {
		if (list != null && list.size() > 0) {
			for (LinkEntity linkEntity : list) {
				manager.merge(linkEntity);
			}
		}
	}

	public void updateTopDiagramEntity(String name, long id, long time) {
		String sql = "update TOPDIAGRAM set name=:name ,lastTime=:time where id=:id ";
		Query query = manager.createNativeQuery(sql);
		query.setParameter("name", name);
		query.setParameter("id", id);
		query.setParameter("time", time);
		query.executeUpdate();

	}

	public void iteratorNode(NodeEntity nodeEntity, long id) {
		try {
			if (nodeEntity instanceof SubNetTopoNodeEntity) {
				List<NodeEntity> childNode = this.queryNodeEntity(id,((SubNetTopoNodeEntity) nodeEntity).getGuid());
				if (childNode != null && childNode.size() > 0) {
					for (NodeEntity node : childNode) {
						this.iteratorNode(node, id);
					}
				} else {
					List<NodeEntity> nodes = new ArrayList<NodeEntity>();
					if (nodeEntity instanceof FEPTopoNodeEntity) {
						FEPTopoNodeEntity fepTopoNodeEntity = (FEPTopoNodeEntity) nodeEntity;
						fepTopoNodeEntity.setParentNode(null);
						this.updateEntity(fepTopoNodeEntity);
					} else {
						nodes.add(nodeEntity);
						deleteDeviceS(nodes);
					}
				}

			} else {
				List<NodeEntity> nodes = new ArrayList<NodeEntity>();
				if (nodeEntity instanceof FEPTopoNodeEntity) {
					FEPTopoNodeEntity fepTopoNodeEntity = (FEPTopoNodeEntity) nodeEntity;
					fepTopoNodeEntity.setParentNode(null);
					this.updateEntity(fepTopoNodeEntity);
				} else {
					nodes.add(nodeEntity);
					deleteDeviceS(nodes);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void deleteDeviceS(List<NodeEntity> nodes) {
		try {
			for (NodeEntity node : nodes) {
				if (node instanceof CarrierTopNodeEntity) {
					CarrierTopNodeEntity carrierNode = (CarrierTopNodeEntity) node;
					CarrierEntity carrierEntity = this.getCarrierByCode(carrierNode.getCarrierCode());
					if (carrierEntity != null) {
						Set<CarrierRouteEntity> set = carrierEntity.getRoutes();
						if (set != null && set.size() > 0) {
							deleteEntities(set);
							carrierEntity.getRoutes().removeAll(carrierEntity.getRoutes());
						}

						deleteEntity(node);
						deleteEntity(carrierEntity);
					}
				} else if (node instanceof GPRSTopoNodeEntity) {
					GPRSTopoNodeEntity nodeEntity = (GPRSTopoNodeEntity) node;
					if (nodeEntity != null) {
						GPRSEntity gprsEntity = this.getGPRSEntityByUserId(nodeEntity.getUserId());
						if (nodeEntity != null) {
							deleteEntity(nodeEntity);
						}
						if (gprsEntity != null) {
							deleteEntity(gprsEntity);
						}
					}
				} else if (node instanceof FEPTopoNodeEntity) {
					FEPTopoNodeEntity fepTopoNodeEntity = (FEPTopoNodeEntity) node;
					if (fepTopoNodeEntity != null) {
						deleteEntity(fepTopoNodeEntity);
						FEPEntity fepEntity = this.findFepEntity(fepTopoNodeEntity.getCode());
						if (fepEntity != null) {
							fepEntity = manager.find(FEPEntity.class, fepEntity.getId());
							deleteEntity(fepEntity);
						}
					}
				}

				else {
					if (node.getId() != null) {
						deleteEntity(node);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean compareTime(TopDiagramEntity diagram) {
		Long id = diagram.getId();
		String jpql = "select entity.lastTime from "+ TopDiagramEntity.class.getName()+ " as entity where entity.id=?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, id);
		if (query.getResultList() != null && query.getResultList().size() > 0) {
			long oldtime = (Long) query.getResultList().get(0);
			if (oldtime == diagram.getLastTime()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void updateEntity(Long id, String className, boolean res) {
		String jpql = "select entity from " + className
				+ " as entity where entity.id=?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, id);
		Object obj = null;

		if (query.getResultList() != null && query.getResultList().size() > 0) {
			obj = query.getResultList().get(0);
		}
		if (obj != null) {
			Method m = null;
			try {
				m = obj.getClass().getDeclaredMethod("setIssuedTag", int.class);
				if (m != null) {
					m.invoke(obj, res ? Constants.ISSUEDDEVICE : Constants.ISSUEDADM);
				}
				updateEntity(obj);
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * ͨ��olt��guid�õ���֮�����ķֹ���
	 * @param oltGuid
	 * @return
	 */
	@Override
	public List findEpon_S_TopoNodeEntity(String oltGuid){
		String sql = "select splitter from Epon_S_TopNodeEntity splitter where splitter.oltGuid=?";
		Query query = manager.createQuery(sql);
		query.setParameter(1, oltGuid);
		List<Epon_S_TopNodeEntity> list = query.getResultList();
		return list;
	}

	/**
	 * ͨ��ip�Ͷ˿ڲ�ѯĳһ����
	 */
	@Override
	public Long findLinkEntityByIPPort(String ipvalue, int port) {
		Long linkId = null;
		
			String jpql0 = "select entity.id from "
					+ LinkEntity.class.getName()
					+ " as entity where entity.lldp.localIP=? and entity.lldp.localPortNo=? and entity.lldp.syschorized=true";
			String jpql1 = "select entity.id from "
					+ LinkEntity.class.getName()
					+ " as entity where entity.lldp.remoteIP=? and entity.lldp.remotePortNo=? and entity.lldp.syschorized=true";
			Query query = manager.createQuery(jpql0);
			query.setParameter(1, ipvalue);
			query.setParameter(2, port);
			List<Long> datas = query.getResultList();
			if (datas != null && datas.size() != 0) {
				linkId = (Long) datas.get(0);
			} else {
				query = manager.createQuery(jpql1);
				query.setParameter(1, ipvalue);
				query.setParameter(2, port);
				datas = query.getResultList();
				if (datas != null && datas.size() != 0) {
					linkId = (Long) datas.get(0);

				}
			}

			if (linkId == null) {
				logger.error("�޷��ҵ�ip��" + ipvalue + "   �˿ڣ�" + port + "  ������");
			}

		return linkId;
	}

	public Long findLinkEntityByIPPort(String ipvalue, int port, int portType) {
			Long linkId = null;
			String jpql0 = "select entity.id from "
					+ LinkEntity.class.getName()
					+ " as entity where entity.lldp.localIP=? and entity.lldp.localPortNo=? and entity.lldp.localPortType=? and entity.lldp.syschorized=true";
			String jpql1 = "select entity.id from "
					+ LinkEntity.class.getName()
					+ " as entity where entity.lldp.remoteIP=? and entity.lldp.remotePortNo=? and entity.lldp.remotePortType=? and entity.lldp.syschorized=true";
			Query query = manager.createQuery(jpql0);
			query.setParameter(1, ipvalue);
			query.setParameter(2, port);
			query.setParameter(3, portType);
			List<Long> datas = query.getResultList();
			if (datas != null && datas.size() != 0) {
				linkId = (Long) datas.get(0);
			} else {
				query = manager.createQuery(jpql1);
				query.setParameter(1, ipvalue);
				query.setParameter(2, port);
				query.setParameter(3, portType);
				datas = query.getResultList();
				if (datas != null && datas.size() != 0) {
					linkId = (Long) datas.get(0);

				}
			}

			if (linkId == null) {
				logger.error("�޷��ҵ�ip��" + ipvalue + "   �˿ڣ�" + port + "  ������");
			}
		return linkId;
	}

	@Override
	public LinkEntity findLinkEntity(String ipvalue, int port, int ringID) {
			LinkEntity linEntity = null;
			String jpql0 = "select entity from "
					+ LinkEntity.class.getName()
					+ " as entity where entity.lldp.localIP=? and entity.lldp.localPortNo=? and entity.lldp.syschorized=true and entity.ringID=?";
			String jpql1 = "select entity from "
					+ LinkEntity.class.getName()
					+ " as entity where entity.lldp.remoteIP=? and entity.lldp.remotePortNo=? and entity.lldp.syschorized=true and entity.ringID=?";
			Query query = manager.createQuery(jpql0);
			query.setParameter(1, ipvalue);
			query.setParameter(2, port);
			query.setParameter(3, ringID);
			List<LinkEntity> datas = query.getResultList();
			if (datas != null && datas.size() != 0) {
				linEntity = datas.get(0);
			} else {
				query = manager.createQuery(jpql1);
				query.setParameter(1, ipvalue);
				query.setParameter(2, port);
				query.setParameter(3, ringID);
				datas = query.getResultList();
				if (datas != null && datas.size() != 0) {
					linEntity = datas.get(0);

				}
			}

			if (linEntity == null) {
				logger.error("�޷��ҵ�ip��" + ipvalue + "   �˿ڣ�" + port + "  ��ʵ��");
			}
		return linEntity;
	}

	/**
	 * ͨ��olt��ip����ۡ��˿ڡ���onu��mac��ѯ����
	 * 
	 * @param ipValue
	 * @param slot
	 * @param port
	 * @param macValue
	 * @return
	 */
	@Override
	public Long findOnuLinkEntityByEponInfo(String ipValue, int slot, int port,String macValue) {
			Long linkId = null;
			String jpql = "select entity.id from "
					+ LinkEntity.class.getName()
					+ " as entity where entity.lldp.remoteIP=? and entity.lldp.remoteSlot=? and entity.lldp.remotePortNo=? and entity.lldp.localIP=?";
			Query query = manager.createQuery(jpql);
			query.setParameter(1, ipValue);
			query.setParameter(2, slot);
			query.setParameter(3, port);
			query.setParameter(4, macValue);
			List datas = query.getResultList();
			if (datas != null && datas.size() != 0) {
				linkId = (Long) datas.get(0);
				return linkId;
			}
		return null;
	}

	/**
	 * ��ȡ�����澯��
	 * 
	 * @param ipValue
	 * @param slot
	 * @param port
	 * @param forrwordIp
	 * @return
	 */
	@Override
	public Long findOLTLink(String ipValue, int slot, int port, int portType) {
			Long linkId = null;
			String jpql = "select entity.id from "
					+ LinkEntity.class.getName()
					+ " as entity where entity.lldp.localIP=? and entity.lldp.localSlot=? and entity.lldp.localPortNo=? and entity.lldp.localPortType=?";
			String jpq2 = "select entity.id from "
				+ LinkEntity.class.getName()
				+ " as entity where entity.lldp.remoteIP=? and entity.lldp.remoteSlot=? and entity.lldp.remotePortNo=? and entity.lldp.remotePortType=?";
			Query query = manager.createQuery(jpql);
			query.setParameter(1, ipValue);
			query.setParameter(2, slot);
			query.setParameter(3, port);
			query.setParameter(4, portType);
			List datas = query.getResultList();
			if (datas != null && datas.size() != 0) {
				linkId = (Long) datas.get(0);
				logger.info("local----localIP:" + ipValue + "  localSlot:" + slot + "  localPortNo:" + port + " localPortType:" + portType);
				return linkId;
			}
			else{
				logger.info("remote----remoteIP:" + ipValue + "  remoteSlot:" + slot + "  remotePortNo:" + port + " remotePortType:" + portType);
				query = manager.createQuery(jpq2);
				query.setParameter(1, ipValue);
				query.setParameter(2, slot);
				query.setParameter(3, port);
				query.setParameter(4, portType);
				datas = query.getResultList();
				if (datas != null && datas.size() != 0) {
					linkId = (Long) datas.get(0);
					return linkId;
				}
			}
		return null;
	}

	public void updateLink(int status, Long id) {
			logger.info("������״̬ ��ID��" + id);
			if (id!=null&&id > 0) {
				LinkEntity link = manager.find(LinkEntity.class, id);
				if (link != null) {
					link.setStatus(status);
					manager.merge(link);
				}
			}
		
	}
	
	/**
	 * ���½ڵ�״̬
	 * @param status
	 * @param id
	 */
	public void updateNode(int status,Long id){
		logger.info("������״̬ ��ID��" + id);
		if (id!=null&&id > 0) {
			NodeEntity node = manager.find(NodeEntity.class, id);
			if (node != null) {
				node.setStatus(status);
				manager.merge(node);
			}
		}
	}

	/**
	 * ͨ������id��ȡ���߶���
	 */
	public LinkEntity queryLinkEntity(Long id) {
		if(id!=null){
		String sql = "select c from LinkEntity c left join fetch c.node1 left join fetch c.node2 left join fetch c.lldp where c.id=:id";
		Query query = manager.createQuery(sql);
		query.setParameter("id", id);
		return (LinkEntity) query.getResultList().get(0);
		}else{
			return null;
		}
	}
	
	/**
	 * ͨ���ڵ�id��ȡ���߶���
	 */
	public NodeEntity queryNodeEntity(Long id){
//		NodeEntity nodeEntity = null;
//		if(id!=null){
//			String sql = "select c from NodeEntity c left join fetch c.node1 left join fetch c.node2 left join fetch c.lldp where c.id=:id";
//			Query query = manager.createQuery(sql);
//			query.setParameter("id", id);
//			if (query.getResultList().get(0) instanceof SwitchTopoNodeEntity){
//				SwitchTopoNodeEntity topoNodeEntity = (SwitchTopoNodeEntity)query.getResultList().get(0);
//				if (topoNodeEntity instanceof NodeEntity){
//					nodeEntity = (NodeEntity)topoNodeEntity;
//				}
//			}
//		}
		
		NodeEntity nodeEntity = null;
		if(id!=null){
			String sql = "select c from NodeEntity c where c.id=:id";
			Query query = manager.createQuery(sql);
			query.setParameter("id", id);
			if (query.getResultList() != null && query.getResultList().size() > 0){
				nodeEntity = (NodeEntity)query.getResultList().get(0);
			}
		}
		return nodeEntity;
	}
	
	/**
	 * ͨ��guid�õ�SubNetTopoNodeEntity
	 * ֻ����ͨ���������Ʋ�ѯ����ʵ��
	 * @param nodeName
	 * @return
	 */
	public SubNetTopoNodeEntity querySubNetTopoNodeEntity(String guid){
		SubNetTopoNodeEntity nodeEntity = null;
		String sql = "select c from SubNetTopoNodeEntity c where c.guid='" + guid + "'";
		Query query = manager.createQuery(sql);
		if (query.getResultList() != null && query.getResultList().size() > 0){
			nodeEntity = (SubNetTopoNodeEntity)query.getResultList().get(0);
		}
		return nodeEntity;
	}

	/**
	 * ͨ��ip��ȡ������������ͼ�νڵ�
	 * 
	 * @param ipvalue
	 * @return
	 */
	@Override
	public NodeEntity findSwitchTopoNodeByIp(String ipvalue) {
		Object obj = null;
			String jpql = "select entity from "+ SwitchTopoNodeEntity.class.getName()+ " as entity where entity.ipValue=?";
			Query query = manager.createQuery(jpql);
			query.setParameter(1, ipvalue);
			if (query.getResultList() != null&& query.getResultList().size() > 0) {
				obj = query.getResultList().get(0);
			}
			if (obj != null) {
				return (SwitchTopoNodeEntity) obj;
			}
		return null;
	}
	
	/**
	 * ͨ��ip��ȡ������Ԫ������ͼ�νڵ�
	 * 
	 * @param ipvalue
	 * @return
	 */
	public NodeEntity findVirtualNodeByIp(String ipvalue){
		Object obj = null;
		String jpq = "select entity from "+ VirtualNodeEntity.class.getName()+ " as entity where entity.ipValue=?";
		Query query = manager.createQuery(jpq);
		query.setParameter(1, ipvalue);
		if (query.getResultList() != null&& query.getResultList().size() > 0) {
			obj = query.getResultList().get(0);
		}
		if (obj != null) {
			return (VirtualNodeEntity) obj;
		}
		return null;
	}

	/**
	 * ͨ����Ż�ȡ�ز���������ͼ�νڵ�
	 * 
	 * @param code
	 * @return
	 */
	@Override
	public NodeEntity findCarrierTopoNodeByCode(String code) {
		String jpql = "select entity from "+ CarrierTopNodeEntity.class.getName()+ " as entity where entity.nodeEntity.carrierCode=?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, code);
		Object obj = query.getSingleResult();
		return (CarrierTopNodeEntity) obj;
	}

	/**
	 * ͨ��ip��ȡolt��ͼ�νڵ�
	 * 
	 * @param ipvalue
	 * @return
	 */
	@Override
	public NodeEntity findOLTTopoNodeByIp(String ipvalue) {
		Object obj = null;
		String jpql = "select entity from " + EponTopoEntity.class.getName()
				+ " as entity where entity.ipValue=?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, ipvalue);
		
		
		if (query.getResultList() != null&& query.getResultList().size() > 0) {
			obj = query.getResultList().get(0);
		}
		if (obj != null) {
			return (EponTopoEntity) obj;
		}
		
		return null;
	}

	/**
	 * ͨ��mac��ȡonu������ͼ�νڵ�
	 */
	@Override
	public NodeEntity findOnuTopoNodeByMac(String macValue) {
		try{
			String jpql = "select entity from " + ONUTopoNodeEntity.class.getName()
				+ " as entity where entity.macValue=?";
			Query query = manager.createQuery(jpql);
			query.setParameter(1, macValue);
			Object obj = query.getSingleResult();
			return (ONUTopoNodeEntity) obj;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * ͨ��ipValue��ȡ���㽻����������ͼ�νڵ�
	 */
	@Override
	public NodeEntity findSwitchTopoNodeLevel3ByIp(String ipValue) {
		Object obj = null;
		String jpql = "select entity from "+ SwitchTopoNodeLevel3.class.getName()+ " as entity where entity.ipValue=?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, ipValue);
		if (query.getResultList() != null&& query.getResultList().size() > 0) {
			obj = query.getResultList().get(0);
		}
		if (obj != null) {
			return (SwitchTopoNodeLevel3) obj;
		}
		return null;
	}

	// =========================================================================================================================================
	/**
	 * ͨ��mac��ѯonuʵ��
	 */
	@Override
	public ONUEntity getOnuByMacValue(String macValue) {
		String jpql = "select entity from " + ONUEntity.class.getName()
				+ " as entity where entity.macValue=?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, macValue);
		List datas = query.getResultList();
		{
			if (datas != null && datas.size() != 0) {
				return (ONUEntity) datas.get(0);
			} else {
				return null;
			}
		}
	}

	/**
	 * ͨ����Ż�ȡ�ز���ʵ��
	 */
	@Override
	public CarrierEntity getCarrierByCode(int code) {
		String jpql = "select entity from " + CarrierEntity.class.getName()
				+ " as entity where entity.carrierCode=?";
		Query query = manager.createQuery(jpql);
		query.setParameter(1, code);
		if (query.getResultList() != null && query.getResultList().size() > 0) {

			return (CarrierEntity) query.getResultList().get(0);
		}
		return null;
	}

	/**
	 * ͨ��ip��ѯoltʵ��
	 */
	@Override
	public OLTEntity getOLTByIpValue(String ipValue) {
		try {
			String jpql = "select entity from " + OLTEntity.class.getName()
					+ " as entity where entity.ipValue=?";
			Query query = manager.createQuery(jpql);
			query.setParameter(1, ipValue);
			List datas = query.getResultList();
			
			if (datas != null && datas.size() != 0) {
				return (OLTEntity) datas.get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error("ͨ��IP����OLT" + e);
		}
		return null;
	}

	/**
	 * ͨ��ip��ѯ������ʵ��
	 */
	@Override
	public SwitchNodeEntity getSwitchByIp(String ipvalue) {
		try {
			String jpql = "select entity from "
					+ SwitchNodeEntity.class.getName()
					+ " as entity where entity.baseConfig.ipValue = ?";
			Query query = manager.createQuery(jpql);
			query.setParameter(1, ipvalue);
			List<SwitchNodeEntity> datas = query.getResultList();
			if (datas != null && datas.size() != 0) {
				for (SwitchNodeEntity nodeEntity : datas) {
						return nodeEntity;
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error("ͨ��IP���ҽ�����" + e);
		}
		return null;
	}

	public GPRSEntity getGPRSEntityByUserId(String userId) {
		try {
			String jpql = "select entity from " + GPRSEntity.class.getName()
					+ " as entity where entity.userId = ?";
			Query query = manager.createQuery(jpql);
			query.setParameter(1, userId);
			List<GPRSEntity> datas = query.getResultList();
			if (datas != null && datas.size() != 0) {
				return datas.get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error("ͨUserIP����GPRS" + e);
		}
		return null;
	}

	public FEPEntity findFepEntity(String code) {
		try {
			String sql = "select c from FEPEntity c where c.code=:code";
			Query query = manager.createQuery(sql);
			query.setParameter("code", code);
			if (query.getResultList() != null
					&& query.getResultList().size() > 0) {
				return (FEPEntity) query.getResultList().get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error("ͨ��Code����ǰ�û�" + e);
		}
		return null;
	}

	public UserEntity findUsereEntity(String userName) {

		try {
			String sql = "select c from UserEntity c where c.userName=:userName";
			Query query = manager.createQuery(sql);
			query.setParameter("userName", userName);
			if (query.getResultList() != null&& query.getResultList().size() > 0) {
				return (UserEntity) query.getResultList().get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			logger.error("ͨ���û�������UserEntity", e);
		}
		return null;

	}

	@SuppressWarnings("unchecked")
	public OLTBaseInfo getOLTByIp(String ipvalue) {
		try {
			String jpql = "select entity.oltBaseInfo from "+ OLTEntity.class.getName()+ " as entity where entity.ipValue = ?";
			Query query = manager.createQuery(jpql);
			query.setParameter(1, ipvalue);
			List<OLTBaseInfo> datas = query.getResultList();
			if (datas != null && datas.size() != 0) {
				return datas.get(0);
			} 
		} catch (Exception e) {
			logger.error("ͨ��IP����OLT ������Ϣ" + e);
		}
		return null;
	}

	public ONUEntity getOLTONUEntity(String mac) {
		try {
			String sql = "select c from ONUEntity c where c.macValue=:mac";
			Query query = manager.createQuery(sql);
			query.setParameter("mac", mac);
			if (query.getResultList() != null&& query.getResultList().size() > 0) {
				return (ONUEntity) query.getResultList().get(0);
			} 
		} catch (Exception e) {
			logger.error("ͨ��mac����ONU" + e);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public SwitchNodeEntity getSwitchNodeByMac(String macvalue) {
		try {
			String jpql = "select entity from "
					+ SwitchNodeEntity.class.getName()
					+ " as entity where entity.baseInfo.macValue = ?";
			Query query = manager.createQuery(jpql);
			query.setParameter(1, macvalue);
			List<SwitchNodeEntity> datas = query.getResultList();
			if (datas != null && datas.size() != 0) {
				return datas.get(0);
			}
		} catch (Exception e) {
			logger.error("ͨ��Mac���ҽ�����" + e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public SwitchNodeEntity getSwitchNodeByIp(String ipValue) {
		try {
			String jpql = "select entity from "
					+ SwitchNodeEntity.class.getName()
					+ " as entity where entity.baseConfig.ipValue = ?";
			Query query = manager.createQuery(jpql);
			query.setParameter(1, ipValue);
			List<SwitchNodeEntity> datas = query.getResultList();
			if (datas != null && datas.size() != 0) {
				return datas.get(0);
			}
		} catch (Exception e) {
			logger.error("ͨ��ip���ҽ�����" + e);
		}
		return null;
	}

	@Override
	public SwitchLayer3 getSwitcher3ByIP(String ipvalue) {
		try {
			String jpql = "select entity from " + SwitchLayer3.class.getName()
					+ " as entity where entity.ipValue = ?";
			Query query = manager.createQuery(jpql);
			query.setParameter(1, ipvalue);
			List<SwitchLayer3> datas = query.getResultList();
			if (datas != null && datas.size() != 0) {
				return datas.get(0);
			} 
		} catch (Exception e) {
			logger.error("ͨ��IP�������㽻����" + e);
		}
		return null;
	}

	public FEPEntity getFEPEntityByIP(String ipValue) {

		try {
			String jpql = "select entity from " + FEPEntity.class.getName()
					+ " as entity where entity.ipValue = ?";
			Query query = manager.createQuery(jpql);
			query.setParameter(1, ipValue);
			List<FEPEntity> datas = query.getResultList();
			if (datas != null && datas.size() != 0) {
				return datas.get(0);
			} 
		} catch (Exception e) {
			logger.error("ͨ��IP����ǰ�û�" + e);
		}
		return null;
	}

	public SwitchLayer3 getSwitcher3ByMac(String macValue) {
		try {
			String jpql = "select entity from " + SwitchLayer3.class.getName()
					+ " as entity where entity.macValue = ?";
			Query query = manager.createQuery(jpql);
			query.setParameter(1, macValue);
			List<SwitchLayer3> datas = query.getResultList();
			if (datas != null && datas.size() != 0) {
				return datas.get(0);
			} 
		} catch (Exception e) {
			logger.error("ͨ��mac�������㽻����" + e);
		}
		return null;
	}

	public List<NodeEntity> queryNodeEntity(long diagramID, String partentNode) {
		List<NodeEntity> list = null;
		try {
			if (partentNode != null && !partentNode.equals("")) {
				String sql = "select c from NodeEntity c where c.topDiagramEntity.id=:diagramID and c.parentNode=:nodeEntity";
				Query query = manager.createQuery(sql);
				query.setParameter("diagramID", diagramID);
				query.setParameter("nodeEntity", partentNode);
				list = query.getResultList();
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
			e.printStackTrace();
		}
		return false;
	}

	public AddressEntity queryAddressBySwitcher3(String ipValue) {
		String sql = "select a.longitude,a.latitude from AddressEntity a,SwitchLayer3 s where a.id=s.addressID and s.ipValue=:ipValue";
		Query query = manager.createNativeQuery(sql);
		query.setParameter("ipValue", ipValue);
		List<Object[]> addressdata = query.getResultList();
		if (addressdata != null && addressdata.size() > 0) {
			Object[] obj = (Object[]) addressdata.get(0);
			AddressEntity addressEntity = new AddressEntity();
			if (obj[0] != null && !obj[0].equals("")) {
				addressEntity.setLongitude((String) obj[0]);
			}
			if (obj[1] != null && !obj[1].equals("")) {
				addressEntity.setLatitude((String) obj[1]);
			}
			return addressEntity;
		}
		return null;

	}

	public AddressEntity queryAddressByOLT(String ipValue) {
		String sql = "select a.longitude,a.latitude from AddressEntity a,OLTEntity o where a.id=o.addressID and o.ipValue=:ipValue";
		Query query = manager.createNativeQuery(sql);
		query.setParameter("ipValue", ipValue);
		List<Object[]> addressdata = query.getResultList();
		if (addressdata != null && addressdata.size() > 0) {
			Object[] obj = (Object[]) addressdata.get(0);
			AddressEntity addressEntity = new AddressEntity();
			if (obj[0] != null && !obj[0].equals("")) {
				addressEntity.setLongitude((String) obj[0]);
			}
			if (obj[1] != null && !obj[1].equals("")) {
				addressEntity.setLatitude((String) obj[1]);
			}
			return addressEntity;
		}
		return null;

	}

	public AddressEntity queryAddressByONU(String macAddress) {
		String sql = "select a.longitude,a.latitude from AddressEntity a,ONUEntity o where a.id=o.addressID and o.macValue=:macAddress";
		Query query = manager.createNativeQuery(sql);
		query.setParameter("macAddress", macAddress);
		List<Object[]> addressdata = query.getResultList();
		if (addressdata != null && addressdata.size() > 0) {
			Object[] obj = (Object[]) addressdata.get(0);
			AddressEntity addressEntity = new AddressEntity();
			if (obj[0] != null && !obj[0].equals("")) {
				addressEntity.setLongitude((String) obj[0]);
			}
			if (obj[1] != null && !obj[1].equals("")) {
				addressEntity.setLatitude((String) obj[1]);
			}
			return addressEntity;
		}
		return null;

	}

	public AddressEntity queryAddressBySwitcher2(String ipValue) {
		String sql = "select a.longitude,a.latitude from AddressEntity a,SwitchNode s,SwitchBaseConfig b where a.id=s.address_ID and s.baseConfig_ID=b.id and b.ipValue=:ipValue";
		Query query = manager.createNativeQuery(sql);
		query.setParameter("ipValue", ipValue);
		List<Object[]> addressdata = query.getResultList();
		if (addressdata != null && addressdata.size() > 0) {
			Object[] obj = (Object[]) addressdata.get(0);
			AddressEntity addressEntity = new AddressEntity();
			if (obj[0] != null && !obj[0].equals("")) {
				addressEntity.setLongitude((String) obj[0]);
			}
			if (obj[1] != null && !obj[1].equals("")) {
				addressEntity.setLatitude((String) obj[1]);
			}
			return addressEntity;
		}
		return null;
	}

	public AddressEntity queryAddressByGPRS(String userId) {
		String sql = "select a.longitude,a.latitude from AddressEntity a,GPRSEntity g where a.id=g.addressID and g.userId=:userId";
		Query query = manager.createNativeQuery(sql);
		query.setParameter("userId", userId);
		List<Object[]> addressdata = query.getResultList();
		if (addressdata != null && addressdata.size() > 0) {
			Object[] obj = (Object[]) addressdata.get(0);
			AddressEntity addressEntity = new AddressEntity();
			if (obj[0] != null && !obj[0].equals("")) {
				addressEntity.setLongitude((String) obj[0]);
			}
			if (obj[1] != null && !obj[1].equals("")) {
				addressEntity.setLatitude((String) obj[1]);
			}
			return addressEntity;
		}
		return null;
	}

	public AddressEntity queryAddressByCarrier(int carrierCode) {
		String sql = "select a.longitude,a.latitude from AddressEntity a,Carrier c where a.id=c.addressID and  c.carrierCode=:carrierCode";
		Query query = manager.createNativeQuery(sql);
		query.setParameter("carrierCode", carrierCode);
		List<Object[]> addressdata = query.getResultList();
		if (addressdata != null && addressdata.size() > 0) {
			Object[] obj = (Object[]) addressdata.get(0);
			AddressEntity addressEntity = new AddressEntity();
			if (obj[0] != null && !obj[0].equals("")) {
				addressEntity.setLongitude((String) obj[0]);
			}
			if (obj[1] != null && !obj[1].equals("")) {
				addressEntity.setLatitude((String) obj[1]);
			}
			return addressEntity;
		}
		return null;
	}

	public String getSwitchBaseConfigUsers(String ipValue) {
		String sql = "select s.userNames from SwitchBaseConfig s where s.ipValue=:ipValue";
		Query query = manager.createNativeQuery(sql);
		query.setParameter("ipValue", ipValue);
		if (query.getResultList() != null && query.getResultList().size() > 0) {
			return (String) query.getResultList().get(0);
		}
		return null;
	}

	public String getOLTUsers(String ipValue) {
		String sql = "select b.userNames from OLTEntity s,OLTBaseInfo b where s.oltBaseInfo_id=b.id and s.ipValue=:ipValue";
		Query query = manager.createNativeQuery(sql);
		query.setParameter("ipValue", ipValue);
		if (query.getResultList() != null && query.getResultList().size() > 0) {
			return (String) query.getResultList().get(0);
		}
		return null;
	}

	public List<CarrierEntity> queryCarrierEntity() {
		List<CarrierEntity> list = null;
		int type = Constants.CENTER;
		try {
			String sql = " select c from CarrierEntity c where c.type=:type";
			Query query = manager.createQuery(sql);
			query.setParameter("type", type);
			if (query.getResultList() != null&& query.getResultList().size() > 0) {
				list = query.getResultList();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	public void commit() {
		manager.flush();
	}


	
}
