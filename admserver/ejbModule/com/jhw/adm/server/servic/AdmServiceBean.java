package com.jhw.adm.server.servic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.JMSException;

import org.apache.log4j.Logger;
import org.jboss.ejb3.annotation.RemoteBinding;

import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.carriers.CarrierRouteEntity;
import com.jhw.adm.server.entity.level3.Switch3VlanEnity;
import com.jhw.adm.server.entity.level3.Switch3VlanPortEntity;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.interfaces.AdmServiceBeanLocal;
import com.jhw.adm.server.interfaces.CommonServiceBeanLocal;
import com.jhw.adm.server.interfaces.DataCacheLocal;
import com.jhw.adm.server.interfaces.SMTCServiceLocal;
import com.jhw.adm.server.interfaces.SMTFEPServiceLocal;

/**
 * 供客户端调用的sessionbean
 * 
 * @author 杨霄
 */
@Stateless(mappedName = "AdmServiceBean")
@RemoteBinding(jndiBinding = "remote/AdmServiceBean")
public class AdmServiceBean implements AdmServiceBeanRemote,
		AdmServiceBeanLocal {

	@EJB
	private CommonServiceBeanLocal commonService;

	@EJB
	private DataCacheLocal datacache;

	@EJB
	private SMTFEPServiceLocal sendMessageService;

	@EJB
	private SMTCServiceLocal smtcservice;
	private Logger logger = Logger.getLogger(AdmServiceBean.class.getName());

	@Override
	public Serializable saveAndSetting(String macValue, int mt,
			Serializable obj, String from, String clientIp, int deviceType,
			int SYN_TYPE) throws JMSException {
		boolean ok = datacache.isFEPOnline(datacache.getIpByMac(macValue));
		if (SYN_TYPE == Constants.SYN_ALL) {
			if (ok) {
				Serializable object = (Serializable) commonService
						.saveEntity(obj);
				String ipvalue = datacache.getIpByMac(macValue);
				sendMessageService.sendMessage(ipvalue, mt, from, clientIp,
						deviceType, object);
				return object;
			} else {
				smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "前置机不在线");
				logger.info("前置机不在线，请检查！");
				return null;
			}
		} else if (SYN_TYPE == Constants.SYN_DEV) {
			if (ok) {
				String ipvalue = datacache.getIpByMac(macValue);
				sendMessageService.sendMessage(ipvalue, mt, from, clientIp,
						deviceType, obj);
				return obj;
			} else {
				smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "前置机不在线");
				logger.info("前置机不在线，请检查！");
				return null;
			}
		} else if (SYN_TYPE == Constants.SYN_SERVER) {
			Serializable object = (Serializable) commonService.saveEntity(obj);
			return object;
		}
		return ok;

	}

	@Override
	public void saveAndSetting(String macValue, int mt, List<?> objs,
			String from, String clientIp, int deviceType, int SYN_TYPE)
			throws JMSException {
		boolean ok = datacache.isFEPOnline(datacache.getIpByMac(macValue));
		if (SYN_TYPE == Constants.SYN_ALL) {
			if (ok) {
				commonService.saveEntities(objs);
				String ipvalue = datacache.getIpByMac(macValue);
				sendMessageService.sendMessage(ipvalue, mt, from, clientIp,deviceType, objs);
			} else {
				smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "前置机不在线");
				logger.info("前置机不在线，请检查！");
			}

		} else if (SYN_TYPE == Constants.SYN_DEV) {
			if (ok) {
				String ipvalue = datacache.getIpByMac(macValue);
				sendMessageService.sendMessage(ipvalue, mt, from, clientIp,
						deviceType, objs);
			} else {
				smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "前置机不在线");
				logger.info("前置机不在线，请检查！");
			}

		} else if (SYN_TYPE == Constants.SYN_SERVER) {
			commonService.saveEntities(objs);
		}
	}

	@Override
	public void saveAndBroadcasting(int mt, Serializable obj, String from,
			String clientIp, int SYN_TYPE) throws JMSException {

		if (SYN_TYPE == Constants.SYN_ALL) {
			commonService.saveEntity(obj);
			sendMessageService.broadcastMessage(mt, obj);

		} else if (SYN_TYPE == Constants.SYN_DEV) {
			sendMessageService.broadcastMessage(mt, obj);
		} else if (SYN_TYPE == Constants.SYN_SERVER) {
			commonService.saveEntity(obj);
		}

	}

	@Override
	public void broadcastMessage(int mt, Serializable obj, String from,
			String clientIp, int SYN_TYPE) throws JMSException {

	}

	@Override
	public void broadcastMessage(int mt, String text, String from,
			String clientIp, int SYN_TYPE) throws JMSException {

	}

	@Override
	public void deleteAndSetting(String macValue, int mt, Serializable obj,
			String from, String clientIp, int deviceType, int SYN_TYPE)
			throws JMSException {
		boolean ok = datacache.isFEPOnline(datacache.getIpByMac(macValue));

		if (SYN_TYPE == Constants.SYN_ALL) {
			if (ok) {
				commonService.deleteEntity(obj);
				String ipvalue = datacache.getIpByMac(macValue);
				sendMessageService.sendMessage(ipvalue, mt, from, clientIp,
						deviceType, obj);
			} else {
				smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "前置机不在线");
				logger.info("前置机不在线，请检查！");
			}

		} else if (SYN_TYPE == Constants.SYN_DEV) {
			if (ok) {
				String ipvalue = datacache.getIpByMac(macValue);
				sendMessageService.sendMessage(ipvalue, mt, from, clientIp,
						deviceType, obj);
			} else {
				smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "前置机不在线");
				logger.info("前置机不在线，请检查！");
			}

		} else if (SYN_TYPE == Constants.SYN_SERVER) {
			commonService.deleteEntity(obj);
		}

	}

	public void deleteAndSettings(String macValue, int mt, List datas,
			String from, String clientIp, int deviceType, int SYN_TYPE)
			throws JMSException {
		boolean ok = datacache.isFEPOnline(datacache.getIpByMac(macValue));

		if (SYN_TYPE == Constants.SYN_ALL) {
			if (ok) {
				commonService.deleteEntities(datas);
				String ipvalue = datacache.getIpByMac(macValue);
				sendMessageService.sendMessage(ipvalue, mt, from, clientIp,deviceType, datas);
			} else {
				smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,"server", from, clientIp, "前置机不在线");
				logger.info("前置机不在线，请检查！");
			}

		} else if (SYN_TYPE == Constants.SYN_DEV) {
			if (ok) {

				String ipvalue = datacache.getIpByMac(macValue);
				sendMessageService.sendMessage(ipvalue, mt, from, clientIp,
						deviceType, datas);
			} else {
				smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "前置机不在线");
				logger.info("前置机不在线，请检查！");
			}

		} else if (SYN_TYPE == Constants.SYN_SERVER) {
			commonService.deleteEntities(datas);
		}

	}

	@Override
	public void updateAndBroadcasting(int mt, Serializable obj, String from,
			String clientIp, int SYN_TYPE) throws JMSException {
		if (SYN_TYPE == Constants.SYN_ALL) {
			commonService.updateEntity(obj);
			sendMessageService.broadcastMessage(mt, obj);

		} else if (SYN_TYPE == Constants.SYN_DEV) {
			sendMessageService.broadcastMessage(mt, obj);
		} else if (SYN_TYPE == Constants.SYN_SERVER) {
			commonService.updateEntity(obj);
		}

	}

	@Override
	public void updateAndSetting(String macValue, int mt, Serializable obj,
			String from, String clientIp, int deviceType, int SYN_TYPE)
			throws JMSException {
		boolean ok = datacache.isFEPOnline(datacache.getIpByMac(macValue));

		if (SYN_TYPE == Constants.SYN_ALL) {
			if (ok) {
				Serializable object = (Serializable) commonService
						.updateEntity(obj);
				String ipvalue = datacache.getIpByMac(macValue);
				sendMessageService.sendMessage(ipvalue, mt, from, clientIp,
						deviceType, object);
			} else {
				smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "前置机不在线");
				logger.info("前置机不在线，请检查！");
			}

		} else if (SYN_TYPE == Constants.SYN_DEV) {
			if (ok) {
				String ipvalue = datacache.getIpByMac(macValue);
				sendMessageService.sendMessage(ipvalue, mt, from, clientIp,
						deviceType, obj);
			} else {
				smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "前置机不在线");
				logger.info("前置机不在线，请检查！");
			}
		} else if (SYN_TYPE == Constants.SYN_SERVER) {

			commonService.updateEntity(obj);

		}

	}

	public void updateAndSetting(String macValue, int mt,
			List<Serializable> objs, String from, String clientIp,
			int deviceType, int SYN_TYPE) throws JMSException {
		boolean ok = datacache.isFEPOnline(datacache.getIpByMac(macValue));

		List<Serializable> list = new ArrayList<Serializable>();
		if (SYN_TYPE == Constants.SYN_ALL) {
			if (ok) {
				String ipvalue = datacache.getIpByMac(macValue);
				if (objs != null && objs.size() > 0) {
					for (Serializable obj : objs) {
						obj = (Serializable) commonService.updateEntity(obj);
						list.add(obj);
					}
					sendMessageService.sendMessage(ipvalue, mt, from, clientIp,deviceType, list);
				} else {

					logger.error("下发时要配置的属性为空！");
				}
			} else {
				smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "前置机不在线");
				logger.info("前置机不在线，请检查！");
			}

		} else if (SYN_TYPE == Constants.SYN_DEV) {
			if (ok) {
				String ipvalue = datacache.getIpByMac(macValue);
				sendMessageService.sendMessage(ipvalue, mt, from, clientIp,
						deviceType, objs);
			} else {
				smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "前置机不在线");
				logger.info("前置机不在线，请检查！");
			}
		} else if (SYN_TYPE == Constants.SYN_SERVER) {
			if (objs != null && objs.size() > 0) {
				for (Serializable obj : objs) {
					commonService.updateEntity(obj);
				}
			}
		}

	}

	public void updateAndSetting(String macValue, int mt,
			List<Serializable> objs, String from, String clientIp,
			boolean sendOrNotFep, int deviceType, int SYN_TYPE)
			throws JMSException {
		boolean ok = datacache.isFEPOnline(datacache.getIpByMac(macValue));
		if (ok) {
			if (sendOrNotFep) {
				this.updateAndSetting(macValue, mt, objs, from, clientIp,
						deviceType, SYN_TYPE);
			} else {
				for (Serializable obj : objs) {
					commonService.updateEntity(obj);
				}
			}
		} else {
			smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE, "server",
					from, clientIp, "前置机不在线");
			logger.info("前置机不在线，请检查！");
		}

	}

	// ========================================================================================================
	public void saveAndSettingCarrier(int mt, String from, String clientIp,
			int carrierCode, Serializable obj, int SYN_TYPE)
			throws JMSException {

		if (SYN_TYPE == Constants.SYN_ALL) {

			CarrierEntity carrier = commonService.getCarrierByCode(carrierCode);
			if (mt == MessageNoConstants.CARRIERROUTECONFIG) {
				CarrierRouteEntity route = (CarrierRouteEntity) obj;
				route.setCarrier(carrier);
				carrier.getRoutes().add(route);
			}
			commonService.updateEntity(carrier);
			sendMessageService.sendCarrierMessage(mt, from, clientIp, carrier);
		} else if (SYN_TYPE == Constants.SYN_DEV) {

			CarrierEntity carrier = commonService.getCarrierByCode(carrierCode);
			if (mt == MessageNoConstants.CARRIERROUTECONFIG) {
				CarrierRouteEntity route = (CarrierRouteEntity) obj;
				route.setCarrier(carrier);
				carrier.getRoutes().add(route);
			}

			sendMessageService.sendCarrierMessage(mt, from, clientIp, carrier);
		} else if (SYN_TYPE == Constants.SYN_SERVER) {
			CarrierEntity carrier = commonService.getCarrierByCode(carrierCode);
			if (mt == MessageNoConstants.CARRIERROUTECONFIG) {
				CarrierRouteEntity route = (CarrierRouteEntity) obj;
				route.setCarrier(carrier);
				carrier.getRoutes().add(route);
			}
			commonService.updateEntity(carrier);

		}

	}

	public void deleteAndSettingCarrier(int mt, String from, String clientIp,
			int carrierCode, Serializable obj, int SYN_TYPE)
			throws JMSException {

		if (SYN_TYPE == Constants.SYN_ALL) {
			CarrierEntity carrier = commonService.getCarrierByCode(carrierCode);
			List<CarrierRouteEntity> datas = (ArrayList) obj;
			Set<CarrierRouteEntity> delRouteEntities =new HashSet<CarrierRouteEntity>();
			if (carrier != null) {
				if (datas != null && datas.size() > 0) {
					for (CarrierRouteEntity carrierRouteEntity : datas) {
						carrierRouteEntity =commonService.findById(carrierRouteEntity.getId(), CarrierRouteEntity.class);
						delRouteEntities.add(carrierRouteEntity);
						String where = " where entity.carrierRoute=?";
						Object[] parms = { carrierRouteEntity };
						List<LinkEntity> list = (List<LinkEntity>) commonService.findAll(LinkEntity.class, where, parms);
						if (list != null && list.size() > 0) {
							LinkEntity linkEntity = list.get(0);
							linkEntity.setCarrierRoute(null);
							commonService.updateEntity(linkEntity);
						}
					}
					carrier.getRoutes().removeAll(delRouteEntities);
					commonService.deleteEntities(delRouteEntities);
				}
				commonService.updateEntity(carrier);
				sendMessageService.sendCarrierMessage(mt, from, clientIp,
						carrier);
			}

		} else if (SYN_TYPE == Constants.SYN_DEV) {

			CarrierEntity carrier = commonService.getCarrierByCode(carrierCode);
			Set<CarrierRouteEntity> routes = carrier.getRoutes();
			List datas = (ArrayList) obj;
			carrier.getRoutes().removeAll(datas);
			sendMessageService.sendCarrierMessage(mt, from, clientIp, carrier);

		} else if (SYN_TYPE == Constants.SYN_SERVER) {
			CarrierEntity carrier = commonService.getCarrierByCode(carrierCode);
			List<CarrierRouteEntity> datas = (ArrayList) obj;
			Set<CarrierRouteEntity> delRouteEntities =new HashSet<CarrierRouteEntity>();
			if (carrier != null) {
				if (datas != null && datas.size() > 0) {
					for (CarrierRouteEntity carrierRouteEntity : datas) {
						carrierRouteEntity =commonService.findById(carrierRouteEntity.getId(), CarrierRouteEntity.class);
						delRouteEntities.add(carrierRouteEntity);
						String where = " where entity.carrierRoute=?";
						Object[] parms = { carrierRouteEntity };
						List<LinkEntity> list = (List<LinkEntity>) commonService.findAll(LinkEntity.class, where, parms);
						if (list != null && list.size() > 0) {
							LinkEntity linkEntity = list.get(0);
							linkEntity.setCarrierRoute(null);
							commonService.updateEntity(linkEntity);
						}
					}
					carrier.getRoutes().removeAll(delRouteEntities);
					commonService.deleteEntities(delRouteEntities);
				}
				commonService.updateEntity(carrier);

		}
		}
	}

	public void updateAndSettingCarrier(int mt, String from, String clientIp,
			Serializable obj, int SYN_TYPE) throws JMSException {

		if (SYN_TYPE == Constants.SYN_ALL) {
			commonService.updateEntity(obj);
			sendMessageService.sendCarrierMessage(mt, from, clientIp, obj);
		} else if (SYN_TYPE == Constants.SYN_DEV) {
			sendMessageService.sendCarrierMessage(mt, from, clientIp, obj);
		} else if (SYN_TYPE == Constants.SYN_SERVER) {
			commonService.updateEntity(obj);
		}
	}

	public void saveAndSettingByIP(String ipValue, int mt, List<?> objs,
			String from, String clientIp, int deviceType, int SYN_TYPE)
			throws JMSException {
		boolean ok = datacache.canContinueIP(ipValue);
		if (SYN_TYPE == Constants.SYN_ALL) {
			if (ok) {
				if (objs != null && objs.size() > 0) {
					commonService.saveEntities(objs);
					sendMessageService.sendMessage(ipValue, mt, from, clientIp,
							deviceType, objs);
				} else {

					logger.error("下发时要配置的属性为空！");
				}
			} else {
				smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "前置机不在线");
				logger.info("前置机不在线，请检查！");

			}

		} else if (SYN_TYPE == Constants.SYN_DEV) {
			if (ok) {
				sendMessageService.sendMessage(ipValue, mt, from, clientIp,
						deviceType, objs);
			} else {
				smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "前置机不在线");
				logger.info("前置机不在线，请检查！");
			}

		} else if (SYN_TYPE == Constants.SYN_SERVER) {
			commonService.saveEntities(objs);
		}
	}

	public void updateAndSettingByIp(String ipValue, int mt,
			List<Serializable> objs, String from, String clientIp,
			int deviceType, int SYN_TYPE) throws JMSException {
		boolean ok = datacache.canContinueIP(ipValue);
		List<Serializable> list = new ArrayList<Serializable>();
		if (SYN_TYPE == Constants.SYN_ALL) {
			if (ok) {
				if (objs != null && objs.size() > 0) {
					for (Serializable obj : objs) {
						obj = (Serializable) commonService.updateEntity(obj);
						list.add(obj);
					}
					sendMessageService.sendMessage(ipValue, mt, from, clientIp,
							deviceType, list);
				} else {

					logger.error("下发时要配置的属性为空！");
				}
			} else {
				smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "前置机不在线");
				logger.info("前置机不在线，请检查！");
			}
		} else if (SYN_TYPE == Constants.SYN_DEV) {
			if (ok) {
				sendMessageService.sendMessage(ipValue, mt, from, clientIp,
						deviceType, objs);
			} else {
				smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "前置机不在线");
				logger.info("前置机不在线，请检查！");
			}
		} else if (SYN_TYPE == Constants.SYN_SERVER) {
			if (objs != null && objs.size() > 0) {
				for (Serializable obj : objs) {
					commonService.updateEntity(obj);
				}
			}
		}

	}

	public void deleteAndSettingByIp(String ipValue, int mt, List datas,
			String from, String clientIp, int deviceType, int SYN_TYPE)
			throws JMSException {
		boolean ok = datacache.canContinueIP(ipValue);

		if (SYN_TYPE == Constants.SYN_ALL) {
			if (ok) {
				commonService.deleteEntities(datas);
				sendMessageService.sendMessage(ipValue, mt, from, clientIp,deviceType, datas);
			} else {
				smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "前置机不在线");
				logger.info("前置机不在线，请检查！");
			}

		} else if (SYN_TYPE == Constants.SYN_DEV) {
			if (ok) {

				sendMessageService.sendMessage(ipValue, mt, from, clientIp,
						deviceType, datas);
			} else {
				smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "前置机不在线");
				logger.info("前置机不在线，请检查！");
			}

		} else if (SYN_TYPE == Constants.SYN_SERVER) {
			commonService.deleteEntities(datas);
		}

	}

	public void updateAndSetSwitch3Vlan(String ipValue, int mt, List obj,
			String from, String clientIp, int deviceType, int SYN_TYPE)
			throws JMSException {
		boolean ok = datacache.canContinueIP(ipValue);
		logger.info("配置三层交换机Vlan ipvalue:" + ipValue);

		List<Serializable> list = new ArrayList<Serializable>();
		if (SYN_TYPE == Constants.SYN_ALL) {
			if (ok) {
				if (obj != null && obj.size() > 0) {
					for (int i = 0; i < obj.size(); i++) {
						list.add((Serializable) commonService.updateEntity(obj
								.get(i)));
					}
					sendMessageService.sendMessage(ipValue, mt, from, clientIp,
							deviceType, list);
				} else {
					logger.info("配置三层交换机所传的Vlan为空");
				}
			} else {
				smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "前置机不在线");
				logger.info("前置机不在线，请检查！");
			}

		} else if (SYN_TYPE == Constants.SYN_DEV) {
			if (ok) {
				sendMessageService.sendMessage(ipValue, mt, from, clientIp,
						deviceType, obj);
			} else {
				smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "前置机不在线");
				logger.info("前置机不在线，请检查！");
			}
		} else if (SYN_TYPE == Constants.SYN_SERVER) {
			if (obj != null && obj.size() > 0) {
				for (int i = 0; i < obj.size(); i++) {
					commonService.updateEntity(obj.get(i));
				}
			}
		}

	}

	public void delteAndSetSwitch3Vlan(String ipValue, int mt, List obj,
			String from, String clientIp, int deviceType, int SYN_TYPE)
			throws JMSException {
		boolean ok = datacache.canContinueIP(ipValue);
		if (SYN_TYPE == Constants.SYN_ALL) {
			if (ok) {
				if (obj != null) {
					for (int i = 0; i < obj.size(); i++) {
						commonService.deleteEntity(obj.get(i));
					}

					sendMessageService.sendMessage(ipValue, mt, from, clientIp,
							deviceType, obj);
				} else {
					logger.error("要删除的Vlan为空");
				}
			} else {
				smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "前置机不在线");
				logger.info("前置机不在线，请检查！");
			}
		} else if (SYN_TYPE == Constants.SYN_DEV) {
			if (ok) {
				sendMessageService.sendMessage(ipValue, mt, from, clientIp,
						deviceType, obj);
			} else {
				smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,
						"server", from, clientIp, "前置机不在线");
				logger.info("前置机不在线，请检查！");
			}
		} else if (SYN_TYPE == Constants.SYN_SERVER) {
			if (obj != null) {
				for (int i = 0; i < obj.size(); i++) {
					commonService.deleteEntity(obj.get(i));
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void updateAndSetSwitchVlanPort(String ipValue, int mt, List datas,
			String from, String clientIp, int deviceType, int SYN_TYPE)
			throws JMSException {

		try {
			boolean ok = datacache.canContinueIP(ipValue);
			List<Serializable> list = new ArrayList<Serializable>();
			if (SYN_TYPE == Constants.SYN_ALL) {
				if (ok) {
					if (datas != null) {
						for (int i = 0; i < datas.size(); i++) {

							if (datas.get(i) instanceof Switch3VlanPortEntity) {
								Switch3VlanPortEntity portEntity = (Switch3VlanPortEntity) datas
										.get(i);
								Switch3VlanPortEntity newEntity = new Switch3VlanPortEntity();
								newEntity.copy(portEntity);
								int newVlanID = portEntity.getVlanID();

								Switch3VlanPortEntity oldEntity = commonService
										.findById(portEntity.getId(),
												Switch3VlanPortEntity.class);
								if (oldEntity != null) {
									int oldVlanID = oldEntity.getVlanID();
									if (oldVlanID != newVlanID) {
										Object[] parms = { ipValue, oldVlanID };
										List<Switch3VlanEnity> listVlanEnities = (List<Switch3VlanEnity>) commonService
												.findAll(
														Switch3VlanEnity.class,
														"where entity.ipValue=? and entity.vlanID=?",
														parms);
										if (listVlanEnities != null
												&& listVlanEnities.size() > 0) {
											Switch3VlanEnity oldVlanEnity = listVlanEnities
													.get(0);
											oldVlanEnity.getVlanPortEntities()
													.remove(oldEntity);
											commonService
													.deleteEntity(oldEntity);
											commonService
													.updateEntity(oldVlanEnity);
										}

										Object[] newParms = { ipValue,
												newVlanID };
										List<Switch3VlanEnity> newVlanEnities = (List<Switch3VlanEnity>) commonService
												.findAll(
														Switch3VlanEnity.class,
														"where entity.ipValue=? and entity.vlanID=?",
														newParms);
										if (newVlanEnities != null
												&& newVlanEnities.size() > 0) {
											Switch3VlanEnity newVlanEnity = newVlanEnities
													.get(0);
											newVlanEnity.getVlanPortEntities()
													.add(newEntity);
											commonService
													.updateEntity(newVlanEnity);
										}
									}
								}
								list.add((Serializable) commonService
										.updateEntity(datas.get(i)));

							}

						}
						sendMessageService.sendMessage(ipValue, mt, from,
								clientIp, deviceType, list);
					} else {
						logger.error("要删除的Vlan Port为空");
					}

				} else {
					smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,
							"server", from, clientIp, "前置机不在线");
					logger.info("前置机不在线，请检查！");
				}

			} else if (SYN_TYPE == Constants.SYN_DEV) {
				if (ok) {
					sendMessageService.sendMessage(ipValue, mt, from, clientIp,
							deviceType, datas);
				} else {
					smtcservice.sendMessage(MessageNoConstants.FEPOFFLINE,
							"server", from, clientIp, "前置机不在线");
					logger.info("前置机不在线，请检查！");
				}
			} else if (SYN_TYPE == Constants.SYN_SERVER) {
				if (datas != null) {
					for (int i = 0; i < datas.size(); i++) {
						commonService.updateEntity(datas.get(i));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void sendMessageToIPs(int mt, String from, String clientIp,
			Set<SynchDevice> sds) {
		try {
			sendMessageService.sendMessageToIPs(mt, from, clientIp, sds);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
