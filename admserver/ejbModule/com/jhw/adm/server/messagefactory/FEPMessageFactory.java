package com.jhw.adm.server.messagefactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.carriers.CarrierPortEntity;
import com.jhw.adm.server.entity.carriers.CarrierRouteEntity;
import com.jhw.adm.server.entity.epon.OLTBaseInfo;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.epon.OLTPort;
import com.jhw.adm.server.entity.epon.ONUEntity;
import com.jhw.adm.server.entity.epon.ONULLID;
import com.jhw.adm.server.entity.epon.ONUPort;
import com.jhw.adm.server.entity.level3.Switch3VlanEnity;
import com.jhw.adm.server.entity.level3.Switch3VlanPortEntity;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.level3.SwitchPortLevel3;
import com.jhw.adm.server.entity.nets.VlanConfig;
import com.jhw.adm.server.entity.nets.VlanEntity;
import com.jhw.adm.server.entity.nets.VlanPort;
import com.jhw.adm.server.entity.nets.VlanPortConfig;
import com.jhw.adm.server.entity.ports.LLDPInofEntity;
import com.jhw.adm.server.entity.ports.QOSPriority;
import com.jhw.adm.server.entity.ports.QOSSpeedConfig;
import com.jhw.adm.server.entity.ports.RingConfig;
import com.jhw.adm.server.entity.ports.SwitchPortEntity;
import com.jhw.adm.server.entity.switchs.IGMPEntity;
import com.jhw.adm.server.entity.switchs.Igmp_vsi;
import com.jhw.adm.server.entity.switchs.Priority802D1P;
import com.jhw.adm.server.entity.switchs.PriorityDSCP;
import com.jhw.adm.server.entity.switchs.PriorityTOS;
import com.jhw.adm.server.entity.switchs.SNMPHost;
import com.jhw.adm.server.entity.switchs.SwitchBaseConfig;
import com.jhw.adm.server.entity.switchs.SwitchBaseInfo;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.switchs.SwitchRingInfo;
import com.jhw.adm.server.entity.switchs.SwitchUser;
import com.jhw.adm.server.entity.switchs.SysLogHostEntity;
import com.jhw.adm.server.entity.switchs.SysLogHostToDevEntity;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.RingEntity;
import com.jhw.adm.server.entity.tuopos.SubNetTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.EmulationEntity;
import com.jhw.adm.server.entity.util.GPRSEntity;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.ParmRep;
import com.jhw.adm.server.entity.util.PingResult;
import com.jhw.adm.server.entity.util.SwitchSerialPort;
import com.jhw.adm.server.entity.util.WarningCategoryBean;
import com.jhw.adm.server.entity.util.WarningEventDescription;
import com.jhw.adm.server.entity.util.WarningLevelBean;
import com.jhw.adm.server.entity.warning.FaultDetectionRecord;
import com.jhw.adm.server.entity.warning.NoteEntity;
import com.jhw.adm.server.entity.warning.RmonCount;
import com.jhw.adm.server.entity.warning.RmonEntity;
import com.jhw.adm.server.entity.warning.SysLogWarningEntity;
import com.jhw.adm.server.entity.warning.TimerMonitoringSheet;
import com.jhw.adm.server.entity.warning.TrapWarningEntity;
import com.jhw.adm.server.entity.warning.WarningEntity;
import com.jhw.adm.server.interfaces.CommonServiceBeanLocal;
import com.jhw.adm.server.interfaces.DataCacheLocal;
import com.jhw.adm.server.interfaces.FEPMessageFactoryLocal;
import com.jhw.adm.server.interfaces.NMSServiceLocal;
import com.jhw.adm.server.interfaces.SMTCServiceLocal;
import com.jhw.adm.server.interfaces.SendMsgAndEmailLocal;
import com.jhw.adm.server.util.CacheDatas;
import com.jhw.adm.server.util.Tools;

/**
 * ���ڴ����ǰ�û����͵��������˵�������Ϣ����Ϣ����
 * 
 * @author ����
 */
@Stateless(mappedName = "FEPMessageFactory")
public class FEPMessageFactory implements FEPMessageFactoryLocal {
	private Logger logger = Logger.getLogger(FEPMessageFactory.class.getName());
	@EJB
	private CommonServiceBeanLocal commService;
	@EJB
	private NMSServiceLocal nmsService;
	@EJB
	private SMTCServiceLocal smtcService;

	@EJB
	private DataCacheLocal datacache;

	@EJB
	private SendMsgAndEmailLocal sendMsgAndEmailLocal;

	private ExecutorService executorService = null;

	public ExecutorService getExecutorService() {
		if (executorService == null) {
			executorService = Executors.newSingleThreadExecutor();
		}
		return executorService;
	}

	@SuppressWarnings("unchecked")
	public void DealWithMessage(Message message) throws JMSException {
		if (message instanceof ObjectMessage) {
			ObjectMessage om = (ObjectMessage) message;
			try {

				int mt = message.getIntProperty(Constants.MESSAGETYPE);
				String messageTo = message
						.getStringProperty(Constants.MESSAGETO);
				String clientIp = message.getStringProperty(Constants.CLIENTIP);
				String res = message.getStringProperty(Constants.MESSAGERES);
				String messageFrom = message
						.getStringProperty(Constants.MESSAGEFROM);
				Object mb = om.getObject();
				// �ж���Ϣ���ͽ�����Ӧ�Ĵ���
				if (mt == MessageNoConstants.TOPONODE) {// ���ص����˽ڵ�
					CacheDatas.getInstance().setRefreshing(true);
					int devType = om.getIntProperty(Constants.DEVTYPE);
					if (1 <= devType && devType <= 100) {
						devType = Constants.DEV_SWITCHER2;

					} else if (101 <= devType && devType <= 200) {
						devType = Constants.DEV_SWITCHER3;
					} else if (201 <= devType && devType <= 300) {
						devType = Constants.DEV_OLT;
					}
					if (devType == Constants.DEV_SWITCHER2) {
						SwitchNodeEntity entity = (SwitchNodeEntity) mb;
						buildTopo(entity);
						String ipvalue = entity.getBaseConfig().getIpValue();
						String txt = "���˷������㽻������" + ipvalue;
						logger.info("ǰ�û�������Ϣ�����˷������㽻����  IPΪ:" + ipvalue);
						
						smtcService.sendMessage(MessageNoConstants.TOPOSEARCHONEFINSH, ipvalue,messageTo, clientIp, txt);
					} else if (devType == Constants.DEV_SWITCHER3) {
						if (mb instanceof SwitchLayer3) {
							SwitchLayer3 w3 = (SwitchLayer3) mb;
							Set<LLDPInofEntity> set = w3.getLldps();
							if (set != null) {
								for (LLDPInofEntity entity : set) {
									logger.info(" ���㽻���� LLDP localIP "
											+ entity.getLocalIP());
									logger.info(" ���㽻���� LLDP RemoteIP "
											+ entity.getRemoteIP());
								}
							}
							SwitchLayer3 oldLayer3 = commService.getSwitcher3ByMac(w3.getMacValue());

							if (oldLayer3 != null) {
								oldLayer3.copy(w3);
								oldLayer3.setSyschorized(true);
								Set<LLDPInofEntity> lldpInofEntities = oldLayer3
										.getLldps();
								Set<SwitchPortLevel3> oldPortLevel3s = oldLayer3
										.getPorts();
								Set<SwitchPortLevel3> newPortLevel3s = w3
										.getPorts();
								Set<LLDPInofEntity> newInofEntities = w3
										.getLldps();
								if (lldpInofEntities != null
										&& lldpInofEntities.size() > 0) {
									oldLayer3.getLldps().removeAll(
											lldpInofEntities);
									commService
											.deleteEntities(lldpInofEntities);
								}
								if (newInofEntities != null
										&& newInofEntities.size() > 0) {
									oldLayer3.setLldps(newInofEntities);
								}
								if (oldPortLevel3s != null
										&& oldPortLevel3s.size() > 0) {
									oldLayer3.getPorts().removeAll(
											oldPortLevel3s);
								}
								if (newPortLevel3s != null
										&& newPortLevel3s.size() > 0) {
									oldLayer3.setPorts(newPortLevel3s);
								}
								commService.updateEntity(oldLayer3);
							} else {
								w3.setSyschorized(true);
								commService.saveEntity(w3);

							}
							String txt = "���˷������㽻����:" + w3.getIpValue();
							logger.info("ǰ�û�������Ϣ�����˷������㽻������" + w3.getIpValue());
							smtcService.sendMessage(MessageNoConstants.TOPOSEARCHONEFINSH,null, messageTo, clientIp, txt);
						}

					} else if (devType == Constants.DEV_OLT) {
						OLTEntity eponEntity = (OLTEntity) mb;
						buildOLTTop(eponEntity);
						String txt = "����OLT " + eponEntity.getIpValue();
						logger.info("ǰ�û�������Ϣ�����˷��� OLT�� IPΪ:"
								+ eponEntity.getIpValue());
						smtcService.sendMessage(
								MessageNoConstants.TOPOSEARCHONEFINSH, null,
								messageTo, clientIp, txt);
					} else if (devType == Constants.DEV_ONU) {
						Set datas = (Set) mb;
						if (datas != null) {
							for (Object obj : datas) {
								if (obj instanceof ONUEntity) {
									ONUEntity onu = (ONUEntity) obj;
									logger.info("ǰ�û�������Ϣ�����˷���ONU��MACΪ:"+ onu.getMacValue());
									bulidOnuTop(onu);
								}
							}
						}
					}
				} else if (mt == MessageNoConstants.SYNCHREP) {// ���ص�ͬ������
					int deviceType = om.getIntProperty(Constants.DEVTYPE);
					String ipValue = "";
					String txt = "";

					if (1 <= deviceType && deviceType <= 100) {
						deviceType = Constants.DEV_SWITCHER2;
					} else if (101 <= deviceType && deviceType <= 200) {
						deviceType = Constants.DEV_SWITCHER3;
					} else if (201 <= deviceType && deviceType <= 300) {
						deviceType = Constants.DEV_OLT;
					}
					if (deviceType == Constants.DEV_OLT) {
						if (res.equals("F")) {
							txt = "OLT��" + messageFrom + " ��������ʧ�ܣ�";
						} else {
							txt = "OLT��" + messageFrom + " �������سɹ���";
							saveOLTSynchData(messageFrom, (Map) om.getObject());
						}
						smtcService.sendMessage(
								MessageNoConstants.SYNCHONEFINISH, res,
								messageFrom, messageTo, clientIp, txt);
					} else if (deviceType == Constants.DEV_SWITCHER2) {
						ipValue = message.getStringProperty(Constants.MESSAGEFROM);
						if (ipValue != null && !ipValue.equals("")) {
							if (res.equals("F")) {
								txt = "��������" + ipValue + " ��������ʧ�ܣ�";
								logger.info(txt);
							} else {
								txt = "��������" + ipValue + " �������سɹ���";
								logger.info(txt);
								if (ipValue == null || ipValue.equals("")) {
									logger.warn("������ͬ��IP Ϊ�գ�");
								}
								saveSynchData(ipValue, (Map) om.getObject());
							}
							smtcService.sendMessage(
									MessageNoConstants.SYNCHONEFINISH, res,
									ipValue, messageTo, clientIp, txt);

						} else {

							logger.warn("������ͬ��Mac Ϊ�գ�");
						}
					} else if (deviceType == Constants.DEV_SWITCHER3) {
						ipValue = message.getStringProperty(Constants.MESSAGEFROM);
						if (ipValue != null && !ipValue.equals("")) {
							if (res.equals("F")) {
								txt = "���㽻������" + ipValue + " ��������ʧ�ܣ�";
								logger.info(txt);
							} else {
								txt = "���㽻������" + ipValue + " �������سɹ���";
								logger.info(txt);
								saveSynchSwitch3Data(ipValue, (Map) om
										.getObject());
							}
							smtcService.sendMessage(
									MessageNoConstants.SYNCHONEFINISH, res,
									ipValue, messageTo, clientIp, txt);
						} else {
							logger.warn("���㽻����ͬ��IP Ϊ�գ�");
						}

					} else if (deviceType == Constants.DEV_ONU) {
						ONUEntity onu = commService
								.getOnuByMacValue(messageFrom);
					}

				} else if (mt == MessageNoConstants.SINGLESYNCHREP) {

					int deviceType = om.getIntProperty(Constants.DEVTYPE);
					logger.warn("�豸���ͣ�" + deviceType);
					String ipValue = "";
					String txt = "";

					if (1 <= deviceType && deviceType <= 100) {
						deviceType = Constants.DEV_SWITCHER2;
					} else if (101 <= deviceType && deviceType <= 200) {
						deviceType = Constants.DEV_SWITCHER3;
					} else if (201 <= deviceType && deviceType <= 300) {
						deviceType = Constants.DEV_OLT;
					}
					if (deviceType == Constants.DEV_OLT) {
						if (res.equals("F")) {
							txt = "OLT��" + messageFrom + " ��������ʧ�ܣ�";
						} else {
							txt = "OLT��" + messageFrom + " �������سɹ���";
							saveOLTSynchData(messageFrom, (Map) om.getObject());
						}
						smtcService.sendMessage(
								MessageNoConstants.SINGLESYNCHONEFINISH, res,
								messageFrom, messageTo, clientIp, txt);
					} else if (deviceType == Constants.DEV_SWITCHER2) {
//						String macvalue = message
//								.getStringProperty(Constants.MESSAGEFROM);
						ipValue = message.getStringProperty(Constants.MESSAGEFROM);
						if (ipValue == null || ipValue.equals("")) {
							logger.warn("����������MacΪ�գ�");
							txt = "���������ߣ���������ʧ�ܣ�";
							logger.info(txt);
							smtcService.sendMessage(MessageNoConstants.SINGLESYNCHONEFINISH,res, ipValue, messageTo, clientIp, txt);
							return;
						}
						if (res.equals("F")) {
//							ipValue = datacache.getIpByMac(macvalue);
							txt = "��������" + ipValue + " ��������ʧ�ܣ�";
							logger.info(txt);
						} else {
//							ipValue = datacache.getIpByMac(macvalue);
							txt = "��������" + ipValue + " �������سɹ���";
							logger.info(txt);
							if (ipValue == null || ipValue.equals("")) {
								logger.warn("����������IPΪ�գ�");
							}
							
							saveSynchData(ipValue, (Map) om.getObject());
						}
						try {
							Thread.sleep(1000);
							smtcService.sendMessage(MessageNoConstants.SINGLESYNCHONEFINISH,res, ipValue, messageTo, clientIp, txt);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

					} else if (deviceType == Constants.DEV_SWITCHER3) {
						ipValue = message.getStringProperty(Constants.MESSAGEFROM);
						if (ipValue != null && !ipValue.equals("")) {
							if (res.equals("F")) {
								txt = "���㽻������" + ipValue + " ��������ʧ�ܣ�";
								logger.info(txt);
							} else {
								txt = "���㽻������" + ipValue + " �������سɹ���";
								logger.info(txt);
								saveSynchSwitch3Data(ipValue, (Map) om
										.getObject());
							}
							smtcService.sendMessage(
									MessageNoConstants.SINGLESYNCHONEFINISH,
									res, ipValue, messageTo, clientIp, txt);
						} else {
							logger.warn("���㽻��������IPΪ�գ�");
						}

					} else if (deviceType == Constants.DEV_ONU) {
						ONUEntity onu = commService
								.getOnuByMacValue(messageFrom);
					}

				}

				else if (mt == MessageNoConstants.OUTTIMEEXCEPTION) {
					String macvalue = message
							.getStringProperty(Constants.MESSAGEFROM);
					LLDPInofEntity entity = (LLDPInofEntity) mb;
					nmsService.updateLLDP(entity);
				} else if (mt == MessageNoConstants.TRAPMESSAGE) {
					dealwithTraps(mb);
				} 
				else if (mt == MessageNoConstants.SYSLOGMESSAGE){
					dealwithSyslog(mb);
				}
				else if (mt == MessageNoConstants.RTMONITORRES) {
					List rcs = (ArrayList) mb;
					// commService.saveEntities(rcs);
					smtcService.sendMessage(mt, messageFrom, messageTo,
							clientIp, (ArrayList) rcs);
				} else if (mt == MessageNoConstants.PINGRES) {
					PingResult pr = (PingResult) mb;
					smtcService.sendMessage(mt, messageFrom, messageTo,
							clientIp, pr);
				} else if (mt == MessageNoConstants.CARRIERROUTEQUERYREP) {
					CarrierEntity carrier = (CarrierEntity) mb;
					int code = Integer.parseInt(messageFrom);
					CarrierEntity entity = updateCarrierRoute(code, carrier);
					smtcService.sendMessage(mt, messageFrom, messageTo,
							clientIp, entity);
				} else if (mt == MessageNoConstants.CARRIERWAVEBANDQUERYREP) {
					CarrierEntity carrier = (CarrierEntity) mb;
					CarrierEntity carrierOld = commService
							.getCarrierByCode(carrier.getCarrierCode());
					if (carrierOld != null) {
						carrierOld.setWaveBand1(carrier.getWaveBand1());
						carrierOld.setWaveBand2(carrier.getWaveBand2());
						carrierOld.setTimeout1(carrier.getTimeout1());
						commService.updateEntity(carrierOld);
						smtcService.sendMessage(mt, messageFrom, messageTo,
								clientIp, carrierOld);
					}
				} else if (mt == MessageNoConstants.CARRIERPORTQUERYREP) {
					CarrierEntity carrier = (CarrierEntity) mb;
					int code = Integer.parseInt(messageFrom);
					CarrierEntity entity = updateCarrierPort(code, carrier);
					smtcService.sendMessage(mt, messageFrom, messageTo,
							clientIp, entity);
				} else if (mt == MessageNoConstants.GPRSMESSAGE) {
					GPRSEntity gprs = (GPRSEntity) mb;
					gprs = nmsService.updateGPRSEntity(gprs);
					if (gprs != null) {
						smtcService.sendMessage(mt, messageFrom, messageTo,
								clientIp, gprs);
					}
				} else if (mt == MessageNoConstants.QOSPORTSPEEDREP) {

					List<QOSSpeedConfig> list = (List<QOSSpeedConfig>) mb;
					if (list != null && list.size() > 0) {
						SwitchNodeEntity switchNodeEntity = commService
								.getSwitchByIp(messageFrom);
						if (switchNodeEntity != null) {
							nmsService.deleteEntityBySwitch(
									QOSSpeedConfig.class, switchNodeEntity);
							for (QOSSpeedConfig speedConfig : list) {
								speedConfig.setSwitchNode(switchNodeEntity);
								commService.saveEntity(speedConfig);
							}
						}

					}

				} else if (mt == MessageNoConstants.PARMREP) {
					ParmRep rep = (ParmRep) mb;
					List<Long> ids = rep.getParmIds();
					List<Object> objs = rep.getObjects();
					String name = rep.getParmClass().getName();
					boolean success = rep.isSuccess();
					String desc = rep.getDescs();
					if (ids != null && ids.size() > 0) {
						for (int i = 0; i < ids.size(); i++) {
							Long id = ids.get(i);
							if (id != null) {
								if (!desc.equals("D")) {
									commService.updateEntity(id, name, success);
								}
							}
						}
					}
					if (objs != null && objs.size() > 0) {
						for (Object obj : objs) {
							if (obj instanceof VlanEntity) {
								VlanEntity entity = (VlanEntity) obj;
								Set<VlanPortConfig> set = entity
										.getPortConfig();
								if (set != null && set.size() > 0) {
									for (VlanPortConfig config : set) {
										SwitchNodeEntity nodeEntity = commService
												.getSwitchByIp(config
														.getIpVlue());
										VlanConfig vlanConfig = nmsService
												.findVlanConfig(nodeEntity);
										Set<VlanEntity> sets = vlanConfig
												.getVlanEntitys();
										if (sets != null && sets.size() > 0) {
											for (VlanEntity vlanEntity : sets) {
												if (vlanEntity.getVlanID() == entity
														.getVlanID()) {
													if (!desc.equals("D")) {
														vlanEntity
																.setSyschorized(success);
														vlanEntity
																.setIssuedTag(success ? Constants.ISSUEDDEVICE
																		: Constants.ISSUEDADM);
														this
																.saveIGMPByVlanID(
																		nodeEntity,
																		vlanEntity
																				.getVlanID()
																				+ "");
													}
													commService
															.updateEntity(vlanEntity);
												}
												Object[] parms = { vlanEntity
														.getVlanID() };
												List<VlanEntity> configs = (List<VlanEntity>) commService
														.findAll(
																VlanEntity.class,
																"where entity.vlanID=? and entity.vlanConfig is null",
																parms);
												if (configs != null
														&& configs.size() > 0) {
													commService
															.deleteEntities(configs);
												}
											}

										}
									}

								}

							} else if (obj instanceof RingConfig) {
								RingConfig config = (RingConfig) obj;
								String ip = config.getSwitchNode()
										.getBaseConfig().getIpValue();
								List<RingConfig> configs = (List<RingConfig>) commService
										.findAll(
												RingConfig.class,
												" where entity.ringID='"
														+ config.getRingID()
														+ "' and  entity.switchNode.baseConfig.ipValue='"
														+ ip + "'");

								if (configs != null) {

									for (RingConfig ringConfig : configs) {
										if (!desc.equals("D")) {
											ringConfig.setSyschorized(success);
											ringConfig
													.setIssuedTag(success ? Constants.ISSUEDDEVICE
															: Constants.ISSUEDADM);
											ringConfig.setRingEnable(config
													.isRingEnable());
										}
										commService.updateEntity(ringConfig);
										Object[] parms = { ringConfig
												.getRingID() };
										List<RingConfig> delConfigs = (List<RingConfig>) commService
												.findAll(
														RingConfig.class,
														"where entity.ringID=? and entity.switchNode is null ",
														parms);
										if (delConfigs != null
												&& delConfigs.size() > 0) {
											if (delConfigs.size() > 0) {
												commService
														.deleteEntities(delConfigs);
											}
										}
									}

								}

							} else if (obj instanceof SNMPHost) {
								SNMPHost snmpHost = (SNMPHost) obj;
								String hostIP = snmpHost.getHostIp();
								List<SNMPHost> list = nmsService.querySNMPHost(
										hostIP, snmpHost.getSnmpVersion(),
										snmpHost.getMassName());
								if (list != null && list.size() > 0) {
									for (SNMPHost host : list) {
										if (!desc.equals("D")) {
											host.setSyschorized(success);
											host
													.setIssuedTag(success ? Constants.ISSUEDDEVICE
															: Constants.ISSUEDADM);
										}
										commService.updateEntity(host);
										Object[] parms = { hostIP };
										List<SNMPHost> snmpHostsAll = (List<SNMPHost>) commService
												.findAll(
														SNMPHost.class,
														" where entity.hostIp=? and entity.switchNode is null",
														parms);
										if (snmpHostsAll != null
												&& snmpHostsAll.size() > 0) {
											if (snmpHostsAll.size() > 0) {
												commService
														.deleteEntities(snmpHostsAll);
											}
										}

									}
								}
							} else if (obj instanceof Switch3VlanEnity) {
								Switch3VlanEnity switch3VlanEnity = (Switch3VlanEnity) obj;
								if (!desc.equals("D")) {
									switch3VlanEnity.setSyschorized(success);
									switch3VlanEnity
											.setIssuedTag(success ? Constants.ISSUEDDEVICE
													: Constants.ISSUEDADM);
								} else {
									switch3VlanEnity.setSyschorized(false);
									switch3VlanEnity
											.setIssuedTag(Constants.ISSUEDADM);
								}
								commService.updateEntity(switch3VlanEnity);
							}
						}
					}

					smtcService.sendMessage(mt, messageFrom, messageTo,
							clientIp, rep);
				} else if (mt == MessageNoConstants.SWITCHUSERUPDATE) {
					Map<SwitchUser, Boolean> map = (Map<SwitchUser, Boolean>) mb;
					if (map != null) {
						Set<SwitchUser> key = map.keySet();
						Iterator it = key.iterator();
						while (it.hasNext()) {
							SwitchUser switchUser = (SwitchUser) it.next();
							boolean result = map.get(switchUser);
							switchUser
									.setIssuedTag(result ? Constants.ISSUEDDEVICE
											: Constants.ISSUEDADM);
							commService.updateEntity(switchUser);
						}
					}
					smtcService.sendMessage(mt, messageFrom, messageTo,
							clientIp, (Serializable) mb);

				} else if (mt == MessageNoConstants.SWITCHUSERADD) {
					Map<SwitchUser, Boolean> map = (Map<SwitchUser, Boolean>) mb;
					if (map != null) {
						Set<SwitchUser> key = map.keySet();
						Iterator it = key.iterator();
						while (it.hasNext()) {
							SwitchUser switchUser = (SwitchUser) it.next();
							boolean result = map.get(switchUser);
							switchUser
									.setIssuedTag(result ? Constants.ISSUEDDEVICE
											: Constants.ISSUEDADM);
							commService.updateEntity(switchUser);
						}
					}
					smtcService.sendMessage(mt, messageFrom, messageTo,
							clientIp, (Serializable) mb);

				} else if (mt == MessageNoConstants.LIGHT_SIGNAL_REP) {
					logger.info("���յ�ǰ�û����ص��źŵƷ�������");
					smtcService.sendMessage(mt, messageFrom, messageTo,
							clientIp, (EmulationEntity) mb);
				} else if (mt == MessageNoConstants.PING_TIMER) {
					FaultDetectionRecord record = (FaultDetectionRecord) om
							.getObject();
					commService.updateEntity(record);
					PingResult pr = new PingResult();
					pr.setIpValue(record.getDevice());
					if (record.getStatus() == 1) {
						pr.setStatus(1);
					} else if (record.getStatus() == 0) {
						pr.setStatus(2);
					}
					pr.setTime(record.getCreateDate());
					smtcService.sendMessage(MessageNoConstants.PINGRES,
							messageFrom, messageTo, clientIp, pr);

				} else if (mt == MessageNoConstants.MONITORRING_TIMER) {

					List count = (ArrayList) om.getObject();
					if (count != null && count.size() > 0) {
						for (int i = 0; i < count.size(); i++) {
							RmonCount rmonCount = (RmonCount) count.get(i);
							TimerMonitoringSheet monitoringSheet = new TimerMonitoringSheet();
							monitoringSheet.setCreateDate(rmonCount.getSampleTime());
							monitoringSheet.setDevice(rmonCount.getIpValue());
							monitoringSheet.setPortNo(rmonCount.getPortNo());
							monitoringSheet.setValue(rmonCount.getValue());
							monitoringSheet.setParam(rmonCount.getParam());
							commService.saveEntity(monitoringSheet);
						}
					}
				} else if (mt == MessageNoConstants.EPON_INFO) {
					OLTEntity eponEntity = (OLTEntity) mb;
					commService.saveEntity(eponEntity);

				} else if (mt == MessageNoConstants.SWITCHRECEIVE) {
					Map<String, SwitchBaseInfo> map = (Map) mb;
					Set<String> set = map.keySet();
					Iterator<String> it = set.iterator();
					while (it.hasNext()) {
						String ip = it.next();
						SwitchBaseInfo baseInfo = map.get(ip);
						SwitchNodeEntity nodeEntity = commService
								.getSwitchByIp(ip);
						nodeEntity.getBaseInfo().copy(baseInfo);
						commService.updateEntity(nodeEntity.getBaseInfo());
					}
				}
			} catch (JMSException e) {
				e.printStackTrace();
			}
		} else if (message instanceof TextMessage) {
			TextMessage tm = (TextMessage) message;
			String messageTo = message.getStringProperty(Constants.MESSAGETO);
			String clientIp = message.getStringProperty(Constants.CLIENTIP);
			int mt = message.getIntProperty(Constants.MESSAGETYPE);
			String res = message.getStringProperty(Constants.MESSAGERES);
			String messageFrom = message
					.getStringProperty(Constants.MESSAGEFROM);
			String txt = tm.getText();
			// resΪ�գ��豸�������͵���Ϣ����澯�ȣ�res��Ϊ�գ���Ϊǰ�û����͵�ǰ��ĳ�������Ľ����Ϣ����ɹ���ʧ�ܡ�
			if (res == null) {
				smtcService.sendMessage(mt, messageFrom, messageTo, clientIp,
						txt);

			} else {
				if (mt == MessageNoConstants.TOPOFINISH) {
					logger.info("���˽�����");
					smtcService.sendMessage(mt, res, messageFrom, messageTo,
							clientIp, txt);

				} else if (mt == MessageNoConstants.SYNCHFINISH) {
					// �����������غ������豸���ط���һ���Ľ�����Ϣ��
					logger
							.error("���ؽ�������������������������������������������������������������������������������������");
					try {
						int timeOut = getTimeOut();
						Thread.sleep(timeOut * 1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
					smtcService.sendMessage(mt, res, messageFrom, messageTo,
							clientIp, txt);
					CacheDatas.getInstance().setSynchorizing(false);
				} else if (mt == MessageNoConstants.CARRIERROUTEFINISH) {
					smtcService.sendMessage(mt, res, messageFrom, messageTo,
							clientIp, txt);
				} else if (mt == MessageNoConstants.ONE_SWITCHER_UPGRADEREP) {
					CacheDatas.getInstance().setRefreshing(true);
					smtcService.sendMessage(mt, res, messageFrom, messageTo,
							clientIp, txt);
				} else if (mt == MessageNoConstants.SWITCHUPGRADEFAIL) {
					smtcService.sendMessage(mt, res, messageFrom, messageTo,
							clientIp, txt);
				} else if (mt == MessageNoConstants.ALL_SWITCHER_UPGRADEREP) {
					smtcService.sendMessage(mt, res, messageFrom, messageTo,
							clientIp, txt);
				} else if (mt == MessageNoConstants.CARRIERPORTCONFIGREP) {
					smtcService.sendMessage(mt, res, messageFrom, messageTo,
							clientIp, txt);
				} else if (mt == MessageNoConstants.CARRIERWAVEBANDCONFIGREP) {
					smtcService.sendMessage(mt, res, messageFrom, messageTo,
							clientIp, txt);
				} else {
					smtcService.sendMessage(mt, res, messageFrom, messageTo,
							clientIp, txt);
				}
			}
		}
	}

	/**
	 * ˢ������ͼ��������豸���ڣ����޸ĸ��豸��lldp�б�͸��豸�Ļ���������Ϣ
	 * 
	 * @param entity
	 */
	private void buildTopo(SwitchNodeEntity entity) {
		try {
			SwitchNodeEntity currentSwitch = entity;
			SwitchNodeEntity switchDB = null;
			String macvalue = currentSwitch.getBaseInfo().getMacValue();
			SwitchBaseConfig config = currentSwitch.getBaseConfig();
			if (config != null) {
				if (macvalue != null) {
					switchDB = commService.getSwitchNodeByMac(macvalue);
				}
			}

			if (switchDB != null) {
				switchDB.setSyschorized(true);
				switchDB.setType(currentSwitch.getType());
				switchDB.setDeviceModel(currentSwitch.getDeviceModel());

				SwitchBaseConfig baseconfig = switchDB.getBaseConfig();
				if (baseconfig != null) {
					baseconfig.copy(currentSwitch.getBaseConfig());
					baseconfig.setIssuedTag(Constants.ISSUEDDEVICE);
				} else {
					SwitchBaseConfig switchBaseConfig = new SwitchBaseConfig();
					switchBaseConfig.copy(currentSwitch.getBaseConfig());
					switchBaseConfig.setIssuedTag(Constants.ISSUEDDEVICE);
					switchDB.setBaseConfig(switchBaseConfig);
				}

				SwitchBaseInfo baseInfo = switchDB.getBaseInfo();
				if (baseInfo != null) {
					baseInfo.copy(currentSwitch.getBaseInfo());
					baseInfo.setCosVersion(currentSwitch.getBaseInfo().getCosVersion());
					baseInfo.setIssuedTag(Constants.ISSUEDDEVICE);
				} else {
					SwitchBaseInfo switchBaseInfo = new SwitchBaseInfo();
					switchBaseInfo.setIssuedTag(Constants.ISSUEDDEVICE);
					switchBaseInfo.copy(currentSwitch.getBaseInfo());
					switchDB.setBaseInfo(switchBaseInfo);
				}

				Set<LLDPInofEntity> currentLLdps = currentSwitch.getLldpinfos();

				if (currentLLdps != null && currentLLdps.size() > 0) {
					for (LLDPInofEntity inofEntity : currentLLdps) {
						inofEntity.setIssuedTag(Constants.ISSUEDDEVICE);
					}
					switchDB.setLldpinfos(currentLLdps);

				}
				Object[] parms = { switchDB };
				List<SwitchPortEntity> switchPortEntities = (List<SwitchPortEntity>) commService
						.findAll(SwitchPortEntity.class,
								"where entity.switchNode=?", parms);
				if (switchPortEntities != null && switchPortEntities.size() > 0) {
					switchDB.getPorts().removeAll(switchPortEntities);
					commService.deleteEntities(switchPortEntities);
					logger.info("���˷��� SwitchPortEntity old������"
							+ switchPortEntities.size());

				}
				Set<SwitchPortEntity> nports = currentSwitch.getPorts();
				if (nports != null && nports.size() > 0) {
					logger.warn("���˷��� SwitchPortEntity ������" + nports.size());
					if (switchDB.getBaseConfig() != null) {
						logger.warn("���˷��� ������IP��"
								+ switchDB.getBaseConfig().getIpValue());
					}
					for (SwitchPortEntity port : nports) {
						port.setIssuedTag(Constants.ISSUEDDEVICE);
						port.setSwitchNode(switchDB);
					}
					switchDB.setPorts(nports);
				}

				Set<SwitchSerialPort> delTempOldsps = new HashSet<SwitchSerialPort>();
				delTempOldsps.addAll(switchDB.getSerialPorts());

				Set<SwitchSerialPort> nsps = currentSwitch.getSerialPorts();
				if (nsps != null && nsps.size() > 0) {
					for (SwitchSerialPort sp : nsps) {
						sp.setIssuedTag(Constants.ISSUEDDEVICE);
						sp.setSwitchNode(switchDB);
					}
				}
				if (switchDB.getSerialPorts() != null) {
					switchDB.getSerialPorts().removeAll(
							switchDB.getSerialPorts());
				}
				switchDB.setSerialPorts(nsps);

				Set<SwitchRingInfo> delOldrings = new HashSet<SwitchRingInfo>();
				delOldrings.addAll(switchDB.getRings());

				Set<SwitchRingInfo> newrings = currentSwitch.getRings();
				if (newrings != null && newrings.size() >= 0) {

					for (SwitchRingInfo ring : newrings) {
						ring.setIssuedTag(Constants.ISSUEDDEVICE);
						ring.setSwitchNode(switchDB);
					}
				}
				switchDB.getRings().removeAll(switchDB.getRings());
				switchDB.setRings(newrings);
				commService.updateEntity(switchDB);
				this.deleteSwitchNodeInfo(switchDB, delTempOldsps, delOldrings);
			} else {
				SwitchBaseConfig baseConfig = currentSwitch.getBaseConfig();
				baseConfig.setIssuedTag(Constants.ISSUEDDEVICE);
				SwitchBaseInfo baseInfo = currentSwitch.getBaseInfo();
				baseInfo.setIssuedTag(Constants.ISSUEDDEVICE);
				Set<SwitchPortEntity> entities = currentSwitch.getPorts();
				if (entities != null) {
					for (SwitchPortEntity portEntity : entities) {
						portEntity.setIssuedTag(Constants.ISSUEDDEVICE);
					}
				}
				Set<SwitchSerialPort> serialPorts = currentSwitch
						.getSerialPorts();
				if (serialPorts != null) {
					for (SwitchSerialPort serialPort : serialPorts) {
						serialPort.setIssuedTag(Constants.ISSUEDDEVICE);
					}
				}
				Set<LLDPInofEntity> currentLLdps = currentSwitch.getLldpinfos();
				if (currentLLdps != null) {
					for (LLDPInofEntity lldpInofEntity : currentLLdps) {
						lldpInofEntity.setIssuedTag(Constants.ISSUEDDEVICE);
					}
				}
				Set<SwitchRingInfo> newrings = currentSwitch.getRings();
				if (newrings != null) {
					for (SwitchRingInfo ringInfo : newrings) {
						ringInfo.setIssuedTag(Constants.ISSUEDDEVICE);
					}
				}
				commService.saveEntity(currentSwitch);
			}
		} catch (Exception e) {
			logger.error("���˷��� �������㽻���� ��" + e);
		}
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void deleteSwitchNodeInfo(SwitchNodeEntity nodeEntity,
			Set<SwitchSerialPort> tempOldsps, Set<SwitchRingInfo> oldrings) {

		if (tempOldsps.size() > 0) {
			nodeEntity.getSerialPorts().removeAll(tempOldsps);
			if (tempOldsps.size() > 0) {
				commService.deleteEntities(tempOldsps);
			}
		}
		if (oldrings.size() > 0) {
			nodeEntity.getRings().removeAll(oldrings);
			if (oldrings.size() >= 0) {
				commService.deleteEntities(oldrings);
			}
		}

	}
	
	

	private void buildOLTTop(OLTEntity olt) {
		Tools.isNotNull(olt, "���˷���ʱ�������OLTʵ��Ϊ�գ�");

		String ipValue = olt.getIpValue();
		OLTEntity dbolt = commService.getOLTByIpValue(ipValue);
		if (dbolt == null) {
			commService.saveEntity(olt);
		} 
		else {
			Tools.isNotNull(olt.getLldpinfos(), "���˷���ʱ�������olt.getLldpinfos()Ϊ�գ�");
			Tools.isNotNull(olt.getPorts(), "���˷���ʱ�������olt.getPorts()Ϊ�գ�");
			
			dbolt.setSyschorized(true);
			OLTBaseInfo baseconfig = dbolt.getOltBaseInfo();
			baseconfig.copy(olt.getOltBaseInfo());
			Set<LLDPInofEntity> currentLLdps = olt.getLldpinfos();
			if (currentLLdps != null) {
				dbolt.setLldpinfos(currentLLdps);
			}
			Set<OLTPort> nports = olt.getPorts();
			if (nports != null && currentLLdps.size() > 0) {
				for (OLTPort port : nports) {
					port.setOltEntity(dbolt);
				}
			}
			dbolt.setPorts(nports);
			dbolt.copy(olt);
			commService.updateEntity(dbolt);
		}
	}

	private void bulidOnuTop(ONUEntity onuEntity) {
		Tools.isNotNull(onuEntity,"���˷���ʱ�������ONUʵ��Ϊ�գ�");
		
		ONUEntity dbonu = commService.getOnuByMacValue(onuEntity.getMacValue());
		if (dbonu == null) {
			commService.saveEntity(onuEntity);
		} else {
			Tools.isNotNull(onuEntity.getLldpinfos(),"���˷���ʱ�������onuEntity.getLldpinfos()Ϊ�գ�");
			Tools.isNotNull(onuEntity.getOnuPorts(),"���˷���ʱ�������onuEntity.getOnuPorts()Ϊ�գ�");
			
			dbonu.setSyschorized(true);
			dbonu.copy(onuEntity);
			Set<LLDPInofEntity> links = onuEntity.getLldpinfos();
			if (links != null) {
				dbonu.getLldpinfos().addAll(links);
				
				this.logger.info("���˷���ONU��lldp������" + links.size());
				for(LLDPInofEntity lldpInofEntity : links){
					this.logger.info("���˷���ONU��lldp��Ϣ---localIP��" + lldpInofEntity.getLocalIP());
					this.logger.info("���˷���ONU��lldp��Ϣ---remoteIP��" + lldpInofEntity.getRemoteIP());
				}
			}
			else{
				this.logger.info("���˷���ONU��lldp����Ϊ0" );
			}
			Set<ONUPort> nports = onuEntity.getOnuPorts();
			if (nports != null) {
				for (ONUPort port : nports) {
					port.setOltonuEntity(dbonu);
				}
			}
		}
	}

	private void buildLLDP(List<LLDPInofEntity> lldps) {
		try {
			logger.info("ǰ�û�������Ϣ�����˷��� lldp ��������Ϊ��" + lldps.size());
			for (LLDPInofEntity lldp : lldps) {
				String localIP = lldp.getLocalIP();
				Thread.sleep(1000);
				SwitchLayer3 switcher = commService.getSwitcher3ByIP(localIP);
				logger.info("���˷���  ͨ�� localIP :" + localIP + "��ѯswitcher :"
						+ switcher);
				if (switcher != null) {
					commService.saveEntity(lldp);
					logger.info("���˷���  lldp LocalIPΪ��" + lldp.getLocalIP());
					logger.info("���˷���  lldp RemoteIPΪ��" + lldp.getRemoteIP());
					if (switcher.getLldps() == null) {
						Set<LLDPInofEntity> set = new HashSet<LLDPInofEntity>();
						set.add(lldp);
						switcher.setLldps(set);
					} else {
						switcher.getLldps().add(lldp);
					}
					commService.updateEntity(switcher);
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ˢ��ĳһ�豸�Ĳ�������ɾ�����豸�Ĳ�����Ȼ�����±���
	 * 
	 * @param macvalue
	 * @param datas
	 */
	@SuppressWarnings("unchecked")
	private synchronized void saveSynchData(String ipValue, Map datas) {
//		SwitchNodeEntity switchDB = commService.getSwitchNodeByMac(macvalue);
		SwitchNodeEntity switchDB = commService.getSwitchNodeByIp(ipValue);
		if (switchDB != null && datas != null) {
			Set keys = datas.keySet();
			configSwitchParm(keys, switchDB, datas);
			try {

				for (Iterator it = keys.iterator(); it.hasNext();) {
					Class key = (Class) it.next();
					List values = (ArrayList) datas.get(key);
					if (values != null) {
						for (int j = 0; j < values.size(); j++) {
							Object obj = values.get(j);
							if (obj instanceof SwitchBaseInfo) {
								SwitchBaseInfo old = switchDB.getBaseInfo();
								if (old != null) {
									old.copy((SwitchBaseInfo) obj);
									old.setIssuedTag(Constants.ISSUEDDEVICE);
									commService.updateEntity(old);
								} else {
									old = new SwitchBaseInfo();
									old.copy((SwitchBaseInfo) obj);
									old.setIssuedTag(Constants.ISSUEDDEVICE);
									commService.updateEntity(old);
									switchDB.setBaseInfo(old);
									commService.updateEntity(switchDB);
								}

							} else if (obj instanceof SwitchBaseConfig) {
								SwitchBaseConfig old = switchDB.getBaseConfig();
								if (old != null) {
									old.copy((SwitchBaseConfig) obj);
									old.setIssuedTag(Constants.ISSUEDDEVICE);
									commService.updateEntity(old);
								} else {
									old = new SwitchBaseConfig();
									old.copy((SwitchBaseConfig) obj);
									old.setIssuedTag(Constants.ISSUEDDEVICE);
									commService.updateEntity(old);
									switchDB.setBaseConfig(old);
									commService.updateEntity(switchDB);
								}
							} else if (obj instanceof Priority802D1P) {// ��������ʱ��߲�����
							} else if (obj instanceof PriorityDSCP) {
							} else if (obj instanceof PriorityTOS) {
							} else if (obj instanceof VlanEntity) {
							} else if (obj instanceof VlanPort) {
							} else if (obj instanceof Igmp_vsi) {
							} else if (obj instanceof RingConfig) {
							} else if (obj instanceof VlanConfig) {
								VlanConfig vlanConfig = (VlanConfig) obj;
								vlanConfig.setSwitchNode(switchDB);
								Set<VlanEntity> vlanEntities = vlanConfig
										.getVlanEntitys();
								if (vlanEntities != null) {
									for (VlanEntity vlanEntity : vlanEntities) {
										vlanEntity
												.setIssuedTag(Constants.ISSUEDDEVICE);
										vlanEntity.setVlanName(vlanEntity
												.getVlanID()
												+ "");
									}
								}
								Set<VlanPort> vlanPorts = vlanConfig
										.getVlanPorts();
								if (vlanPorts != null) {
									for (VlanPort vlanPort : vlanPorts) {
										vlanPort
												.setIssuedTag(Constants.ISSUEDDEVICE);
									}
								}
								commService.saveEntity(vlanConfig);

							} else if (obj instanceof QOSPriority) {
								QOSPriority qosPriority = (QOSPriority) obj;
								qosPriority.setSwitchNode(switchDB);
								List<Priority802D1P> priority802d1ps = qosPriority
										.getPriorityEOTs();
								List<PriorityDSCP> priorityDSCPs = qosPriority
										.getPriorityDSCPs();
								List<PriorityTOS> priorityTOSs = qosPriority
										.getPriorityTOSs();
								if (priority802d1ps != null) {
									for (Priority802D1P priority802d1p : priority802d1ps) {
										priority802d1p
												.setIssuedTag(Constants.ISSUEDDEVICE);
									}
								}
								if (priorityDSCPs != null) {
									for (PriorityDSCP priorityDSCP : priorityDSCPs) {
										priorityDSCP
												.setIssuedTag(Constants.ISSUEDDEVICE);
									}
								}
								if (priorityTOSs != null) {
									for (PriorityTOS priorityTOS : priorityTOSs) {
										priorityTOS
												.setIssuedTag(Constants.ISSUEDDEVICE);
									}
								}
								commService.saveEntity(qosPriority);

							} else if (obj instanceof IGMPEntity) {
								IGMPEntity igmpEntity = (IGMPEntity) obj;
								if (igmpEntity.getVlanIds() == null) {
									// ˵���ǵ���IGMPport����
								} else {
									// ˵����ͬ������IGMPport
									igmpEntity.setSwitchNode(switchDB);
									igmpEntity
											.setIssuedTag(Constants.ISSUEDDEVICE);
									if (igmpEntity.getVlanIds().size() > 0) {
										for (Igmp_vsi igmpVsi : igmpEntity
												.getVlanIds()) {
											igmpVsi
													.setIssuedTag(Constants.ISSUEDDEVICE);
										}
									}
									commService.saveEntity(igmpEntity);
								}

							} else if (obj instanceof SwitchSerialPort) {
								SwitchSerialPort serialPort = (SwitchSerialPort) obj;
								serialPort.setIssuedTag(Constants.ISSUEDDEVICE);
								serialPort.setSwitchNode(switchDB);
								if (switchDB.getSerialPorts() == null) {
									Set<SwitchSerialPort> ports = new HashSet<SwitchSerialPort>();
									ports.add(serialPort);
									switchDB.setSerialPorts(ports);
								} else {
									switchDB.getSerialPorts().add(serialPort);
								}
								commService.updateEntity(switchDB);
							}
							else if (obj instanceof SysLogHostToDevEntity){
								SysLogHostToDevEntity devEntity = (SysLogHostToDevEntity)obj;
								
								SysLogHostEntity hostEntity = devEntity.getSysLogHostEntity();
								//����SysLogHostEntity
								hostEntity = nmsService.saveSysLogHostDB(hostEntity);

								devEntity.setSysLogHostEntity(hostEntity);
								devEntity.setIssuedTag(Constants.ISSUEDDEVICE);
								
								if (devEntity.getId() == null){
									//����SysLogHostToDevEntity
									commService.saveEntity(devEntity);
								}
//								else{
//									commService.updateEntity(devEntity);
//								}
							}
							else {
								key.getDeclaredMethod("setSwitchNode",SwitchNodeEntity.class).invoke(obj,new Object[] { switchDB });
								key.getDeclaredMethod("setIssuedTag",int.class).invoke(obj,new Object[] { Constants.ISSUEDDEVICE });
								commService.saveEntity(obj);
							}
						}
					}
				}
			} catch (Exception exception) {
				exception.printStackTrace();
			}

		}
	}
	
	/**
	 * ��switchLayer3���и���,�Զ˿ڽ���ɾ��
	 * @param switchLayer3
	 * @param datas
	 */
	private void configSwitchLayer3Para(SwitchLayer3 switchLayer3, Map datas){
		try{
			Set keys = datas.keySet();
			
			Set<SwitchPortLevel3> portlevel3Data = new HashSet<SwitchPortLevel3>();
			for (Iterator it = keys.iterator(); it.hasNext();) {
				Class key = (Class) it.next();
				List values = (ArrayList) datas.get(key);
				if (values != null) {
					for (int j = 0; j < values.size(); j++) {
						Object obj = values.get(j);
						if (obj instanceof SwitchPortLevel3) {
							switchLayer3.setPorts(null);
							commService.updateEntity(switchLayer3);
							
							deleteSwitch3Port(switchLayer3);
						}
						else if(obj instanceof Switch3VlanEnity){
							deleteSwitch3Vlan(switchLayer3);
							deleteSwitch3(keys, switchLayer3);
						}
						else{
						}
					}
				}
			}
		}
		catch(Exception ee){
			ee.printStackTrace();
		}
	}

	/**
	 * ˢ��ĳһ�豸�Ĳ�������ɾ�����豸�Ĳ�����Ȼ�����±���
	 * 
	 * @param ipValue
	 * @param datas
	 */
	@SuppressWarnings("unchecked")
	private void saveSynchSwitch3Data(String ipValue, Map datas) {
		try{
			SwitchLayer3 switchLayer3 = commService.getSwitcher3ByIP(ipValue);
			if (switchLayer3 != null && datas != null) {
				this.logger.info("���㽻����switchLayer3��Ϊ��!" );
				configSwitchLayer3Para(switchLayer3,datas);
				
				//
				Set keys = datas.keySet();
				if(keys.size() < 1){
					this.logger.info("****************keysΪ��");
				}
				
				Set<SwitchPortLevel3> portlevel3Data = new HashSet<SwitchPortLevel3>();
				for (Iterator it = keys.iterator(); it.hasNext();) {
					Class key = (Class) it.next();
					List values = (ArrayList) datas.get(key);
					if (values != null) {
						for (int j = 0; j < values.size(); j++) {
							Object obj = values.get(j);
							if (obj instanceof SwitchPortLevel3) {
								this.logger.info("����˿ںţ�" + ((SwitchPortLevel3) obj).getPortNo() 
										+ " ����˿����ƣ�" + ((SwitchPortLevel3) obj).getPortName());
								((SwitchPortLevel3) obj).setIssuedTag(Constants.ISSUEDDEVICE);
								SwitchPortLevel3 entity = (SwitchPortLevel3)commService.saveEntity(obj);
								this.logger.info("****************����˶˿�" );
								portlevel3Data.add(entity);
							} 
							else if (obj instanceof Switch3VlanEnity) {
								commService.saveEntity(obj);
							} 
							else{
//								key.getDeclaredMethod("setSwitchLayer3",SwitchLayer3.class).invoke(obj,
//											new Object[] { switchLayer3 });
//								key.getDeclaredMethod("setIssuedTag",int.class).invoke(obj,new Object[] { Constants.ISSUEDDEVICE });
//								commService.saveEntity(obj);
							}
						}
					}
					else{
						this.logger.info("****************valuesΪ��" );
					}
				}
				
				if (portlevel3Data != null && portlevel3Data.size() > 0){
					switchLayer3.setPorts(portlevel3Data);
					commService.updateEntity(switchLayer3);
					this.logger.info("****************����switchLayer3" );
				}
			}
		}
		catch(Exception ee){
			ee.printStackTrace();
		}
		

//		if (switchLayer3 != null && datas != null) {
//			this.logger.info("���㽻����switchLayer3��Ϊ��!" );
//			deleteSwitch3Vlan(switchLayer3);
//			Set keys = datas.keySet();
//			deleteSwitch3(keys, switchLayer3);
//			for (Iterator it = keys.iterator(); it.hasNext();) {
//				Class key = (Class) it.next();
//				List values = (ArrayList) datas.get(key);
//				if (values != null) {
//					for (int j = 0; j < values.size(); j++) {
//						Object obj = values.get(j);
//						if (obj instanceof Switch3VlanEnity) {
//							commService.saveEntity(obj);
//						} 
//						if (obj instanceof SwitchPortLevel3) {
//							commService.saveEntity(obj);
//						} 
//						else {	
//							try {
//								key.getDeclaredMethod("setSwitchLayer3",
//										SwitchLayer3.class).invoke(obj,
//										new Object[] { switchLayer3 });
//								key.getDeclaredMethod("setIssuedTag",int.class).invoke(obj,new Object[] { Constants.ISSUEDDEVICE });
//								commService.saveEntity(obj);
//							} catch (IllegalArgumentException e) {
//								e.printStackTrace();
//							} catch (SecurityException e) {
//								e.printStackTrace();
//							} catch (IllegalAccessException e) {
//								e.printStackTrace();
//							} catch (InvocationTargetException e) {
//								e.printStackTrace();
//							} catch (NoSuchMethodException e) {
//								e.printStackTrace();
//							}
//
//						}
//					}
//				}
//			}
//		}
	}

	/**
	 * ͬ��OLT����
	 * 
	 * @param ipValue
	 * @param datas
	 */
	@SuppressWarnings("unchecked")
	private void saveOLTSynchData(String ipValue, Map datas) {
		OLTEntity olt = commService.getOLTByIpValue(ipValue);
		if (olt != null && datas != null) {
			Set keys = datas.keySet();
			deleteOLT(keys, olt);
			for (Iterator it = keys.iterator(); it.hasNext();) {
				Class key = (Class) it.next();
				if (key.getName().equals(ONULLID.class.getName())) {
					List values = (ArrayList) datas.get(key);
					for (int j = 0; j < values.size(); j++) {
						ONULLID llid = (ONULLID) values.get(j);
						nmsService.saveLLID(llid);
					}
				} else {
					List values = (ArrayList) datas.get(key);
					Set<OLTPort> oltPortData = new HashSet<OLTPort>();
					if (values != null) {
						for (int j = 0; j < values.size(); j++) {
							Object obj = values.get(j);
							try {
								if (obj instanceof OLTPort) {
									this.logger.info("OLT�˿ںţ�" + ((OLTPort) obj).getProtNo()
											+ " OLT�˿����ƣ�" + ((OLTPort) obj).getPortName());
									((OLTPort) obj).setIssuedTag(Constants.ISSUEDDEVICE);
									((OLTPort) obj).setOltEntity(olt);
									OLTPort entitys = (OLTPort)commService.saveEntity(obj);
									this.logger.info("****************����˶˿�" );
									oltPortData.add(entitys);
								}
								else{
									key.getDeclaredMethod("setOltEntity",OLTEntity.class).invoke(obj,new Object[] { olt });
									key.getDeclaredMethod("setIssuedTag",int.class).invoke(obj,new Object[] { Constants.ISSUEDDEVICE });
									commService.saveEntity(obj);
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
						
						if (oltPortData != null && oltPortData.size() > 0){
							olt.setPorts(oltPortData);
							commService.updateEntity(olt);
						}	
					}
				}
			}
		}
	}

	private CarrierEntity updateCarrierRoute(int code, CarrierEntity entity) {
		CarrierEntity carrier = commService.getCarrierByCode(code);
		if (carrier != null) {
			carrier.setVersion(entity.getVersion());
			Set<CarrierRouteEntity> currentRoutes = entity.getRoutes();
			Set<CarrierRouteEntity> oldRoutes = carrier.getRoutes();
			Set<CarrierRouteEntity> delRoutes = new HashSet<CarrierRouteEntity>();
			Set<CarrierRouteEntity> delCurrentRoutes = new HashSet<CarrierRouteEntity>();
			if (oldRoutes != null && oldRoutes.size() > 0) {

				for (CarrierRouteEntity carrierRouteEntity : oldRoutes) {

					String where = " where entity.carrierRoute=?";
					Object[] parms = { carrierRouteEntity };
					List<LinkEntity> list = (List<LinkEntity>) commService.findAll(LinkEntity.class, where, parms);
					if (list != null && list.size() > 0) {
						for (CarrierRouteEntity routeEntity : currentRoutes) {
							if (routeEntity.getCarrierCode() == carrierRouteEntity
									.getCarrierCode()
									&& routeEntity.getPort() == carrierRouteEntity
											.getPort()) {
								delCurrentRoutes.add(routeEntity);
							}
						}
						continue;
					}
					delRoutes.add(carrierRouteEntity);
				}

				if (delRoutes.size() > 0) {
					carrier.getRoutes().removeAll(delRoutes);
					commService.deleteEntities(delRoutes);
				}
				if (delCurrentRoutes.size() > 0) {
					currentRoutes.removeAll(delCurrentRoutes);

				}
			}
			for (CarrierRouteEntity cport : currentRoutes) {
				cport.setCarrier(carrier);
			}
			carrier.getRoutes().addAll(currentRoutes);
			commService.updateEntity(carrier);
		}
		return carrier;
	}

	private CarrierEntity updateCarrierPort(int code, CarrierEntity entity) {
		CarrierEntity carrier = commService.getCarrierByCode(code);
		if (carrier != null) {
			Set<CarrierPortEntity> currentPorts = entity.getPorts();
			Set<CarrierPortEntity> oldPorts = carrier.getPorts();
			for (CarrierPortEntity cport : currentPorts) {
				cport.setCarrier(carrier);
			}
			List<CarrierPortEntity> tempPorts = new ArrayList<CarrierPortEntity>();
			for (Iterator<CarrierPortEntity> it = oldPorts.iterator(); it
					.hasNext();) {
				tempPorts.add(it.next());
			}
			boolean ok = carrier.getPorts().removeAll(oldPorts);
			if (ok) {
				commService.deleteEntities(tempPorts);
			}
			carrier.setPorts(currentPorts);
			carrier = (CarrierEntity) commService.updateEntity(carrier);
		}
		return carrier;
	}

	@SuppressWarnings("unchecked")
	public Integer queryWarningLevel(int warningEvent) {
		WarningLevelBean category = WarningLevelBean.getInstance();
		Integer level = category.getWarningLevel(warningEvent);
		if (level == null){
			level = -1;
		}
		return level;
		
		//�������ݿ��в�ѯ��
//		Integer level = null;
//		String where = " where entity.warningType=?";
//		Object[] parms = { warningType };
//		List<WarningType> list = (List<WarningType>) commService.findAll(
//				WarningType.class, where, parms);
//		if (list != null && list.size() > 0) {
//			WarningType type = list.get(0);
//			level = type.getWarningLevel();
//		}
//
//		return level;
	}
	
	private synchronized void dealwithSyslog(Object om) throws JMSException{
		try {
			if (CacheDatas.getInstance().isRefreshing()) {
				logger.info("�������˷��ֲ�����澯..");
			} else {
				if (om != null) {
					CacheDatas.getInstance().setWarninging(true);
					
					int deviceType = 0;
					
					SysLogWarningEntity syslogEntity = (SysLogWarningEntity) om;
					logger.warn("syslog�澯ʱ�䣺" + syslogEntity.getCurrentTime());

					int warningEvent = Constants.SYSLOG; //�澯�¼�(syslogû�и澯�¼�,���������)
					syslogEntity.setWarningEvent(warningEvent);
					
					logger.warn("�澯�¼���" + warningEvent);
					
					String ipValue = syslogEntity.getIpValue();        //ip��ַ
					
					commService.saveEntity(syslogEntity); //����SysLogWarningEntity
					
					//����ֻ�ж��㽻������syslog
					NodeEntity nodeEntity = commService.findSwitchTopoNodeByIp(ipValue);
					if (nodeEntity == null){
						nodeEntity = commService.findVirtualNodeByIp(ipValue);
						if(null != nodeEntity){
							deviceType = 0;
						}
					}
					else{
						deviceType = Constants.DEV_SWITCHER2;
					}
					
				
					if (nodeEntity != null){
						WarningEntity warningEntity = copyWarningInfo(syslogEntity,nodeEntity.getId());
						warningEntity.setDeviceType(deviceType);
						commService.saveEntity(warningEntity);  //����WarningEntity
						warningEntity.setNodeId(nodeEntity.getId());
						
						//��ͻ��˷��͸澯
						try {
							smtcService.sendWarningMessage(MessageNoConstants.WARNING, warningEvent,
													ipValue, "", -1, warningEntity);
						} catch (JMSException e) {
							logger.error("",e);
						}
					}
					
					/////////���û�����email�Ͷ���///////////
					int warningLevel = queryWarningLevel(warningEvent);
					Long nodeId = nodeEntity.getId();
					int portNo = 0;
//					List<UserEntity> userEntities = getUserByIp(nodeId);
//					NodeEntity entity = commService.queryNodeEntity(nodeId);
					Set<UserEntity> userEntities = nodeEntity.getUsers();
					
					UserEntity superUser = nmsService.getSuperAdmin();
					if (superUser != null){
						userEntities.add(nmsService.getSuperAdmin());
					}
					
					if(userEntities == null || userEntities.size() < 1 ){
						logger.error("syslog���豸" + ipValue + "û�����û������Բ�����Ż��ʼ�֪ͨ..");
					}
					else{
						logger.info("syslog ������豸���û�����" + userEntities.size());
						for(UserEntity userEntity : userEntities){
							logger.info("syslog�澯�û�����" + userEntity.getUserName());
							sendMsgAndEmailLocal.sendMsgAndEmail(
									userEntity, ipValue, warningEvent,portNo, warningLevel,syslogEntity.getContents());
						}
					}
					/////////////////////////////////////////////
					
					CacheDatas.getInstance().setWarninging(false);
				}
			}
		} catch (Exception e) {
			CacheDatas.getInstance().setWarninging(false);
			logger.error("���ܸ澯��", e);
		}
	}
	
	/**
	 * ����
	 * Constants.COLDSTART(������)
	 * Constants.WARMSTART(������)  
	 * Constants.LINKUP
	 * 
	 * @param trapEntity
	 */
	private void dealwithWarningLinkUp(TrapWarningEntity trapEntity){// ���������Ϣ:��������������������linkup
		int portNo = trapEntity.getPortNo();  //�ϱ��澯���豸�˿�
		int portType = trapEntity.getPortType(); //�˿�����
		int warningEvent = trapEntity.getWarningEvent(); //�澯�¼�
		
		int deviceType = trapEntity.getDeviceType(); //�豸����
		Long nodeId = null;   //�豸�Ľڵ�ID
		Long linkId = null;   //�豸������ID
		
		String ipValue = "";  //�澯�豸��IP��ַ
		
		//��ΪLINKUPʱ����Ҫ�ҵ�nodeID��linkID
		if (warningEvent == Constants.LINKUP) {//LINKUP
			logger.info("LINKUP�澯");
			if (deviceType == Constants.DEV_SWITCHER2) {//���㽻����
				ipValue = trapEntity.getIpValue();  
				
				NodeEntity nodeEntity = commService.findSwitchTopoNodeByIp(ipValue);
				if(nodeEntity == null){
					nodeEntity = commService.findVirtualNodeByIp(ipValue);
					if(null != nodeEntity){
						deviceType = 0;
					}
				}
				
				//��ȡnodeId
				if(null != nodeEntity){
					nodeId = nodeEntity.getId();
				}
				
				//���nodeId��Ϊnull,��ȡlinkId
				if (nodeId != null){
					linkId = commService.findLinkEntityByIPPort(ipValue, portNo);
				}
				
				//����������仰������ΪʲôҪ������,ΪʲôҪȡusers��ֵ
				String users = commService.getSwitchBaseConfigUsers(ipValue);
				trapEntity.setUsers(users);
				logger.info("LinkUp ���㽻�����澯 IP:" + ipValue);
			} 
			else if (deviceType == Constants.DEV_ONU) {  //onu�豸
				int slot = trapEntity.getSlotNum(); 
				ipValue = trapEntity.getIpValue();
				String mac = trapEntity.getWarnOnuMac();  //onu��mac��ַ
				NodeEntity nodeEntity = commService.findOnuTopoNodeByMac(mac);
				if(nodeEntity == null){
					nodeEntity = commService.findVirtualNodeByIp(ipValue);
					if(null != nodeEntity){
						deviceType = 0;
					}
				}
				
				//��ȡnodeId
				if(null != nodeEntity){
					nodeId = nodeEntity.getId();
				}
				
				//���nodeId��Ϊnull,��ȡlinkId
				if (nodeId != null){
					linkId = commService.findOnuLinkEntityByEponInfo(ipValue,slot, portNo, mac);
				}
				logger.info("LinkUp  ONU�澯");
			} else if (deviceType == Constants.DEV_OLT) {  //olt�豸
				int slot = trapEntity.getSlotNum();
				ipValue = trapEntity.getIpValue(); //�澯�豸��IP��ַ
				
				NodeEntity nodeEntity = commService.findOLTTopoNodeByIp(ipValue);
				if(nodeEntity == null){
					nodeEntity = commService.findVirtualNodeByIp(ipValue);
					if(null != nodeEntity){
						deviceType = 0;
					}
				}
				
				//��ȡnodeId
				if(null != nodeEntity){
					nodeId = nodeEntity.getId();
				}
				
				//���nodeId��Ϊnull,��ȡlinkId
				if (nodeId != null){
					linkId = commService.findOLTLink(ipValue, slot,portNo,portType);
				}

				//?????ͬ��
				String users = commService.getOLTUsers(ipValue);
				trapEntity.setUsers(users);
				logger.info("LinkUp OLT�澯");
			} else if (deviceType == Constants.DEV_SWITCHER3) { //���㽻����
				String mac = trapEntity.getIpValue(); //�����㽻���� �˷������ص���mac��ַ
				logger.info("LinkUp ���㽻�����澯 mac:" + mac);
				
				/**
				 * ͨ����������㽻������mac��ַ�õ�switchLayer3,
				 * Ȼ��ͨ��switchLayer3��ipValue��SwitchTopoNodeLevel3
				 * �е�ipValue�����õ�SwitchTopoNodeLevel3
				 */
				SwitchLayer3 switchLayer3 = commService.getSwitcher3ByMac(mac);
				NodeEntity nodeEntity = null;
				if (switchLayer3 != null) {
					ipValue = switchLayer3.getIpValue();
					trapEntity.setUsers(switchLayer3.getName());
					trapEntity.setIpValue(ipValue);
					
					nodeEntity = commService.findSwitchTopoNodeLevel3ByIp(ipValue);
					if(nodeEntity == null){
						nodeEntity = commService.findVirtualNodeByIp(ipValue);
						if(null != nodeEntity){
							deviceType = 0;
						}
					}
					logger.info("LinkUp  ���㽻�����澯 IP:" + ipValue);
				}
				
				//��ȡnodeId
				if(null != nodeEntity){
					nodeId = nodeEntity.getId();
				}
				
				//���nodeId��Ϊnull,��ȡlinkId
				if (nodeId != null){
					linkId = commService.findLinkEntityByIPPort(ipValue, portNo, portType);
				}
			}
			
			commService.updateEntity(trapEntity); //����TrapWarningEntity
		} 
		else {//��Ϊ���������������ȸ澯ʱ��ֻ��Ҫ�ҵ�nodeID
			NodeEntity node = null;
			if (deviceType == Constants.DEV_SWITCHER2) {
				ipValue = trapEntity.getIpValue();
				String users = commService.getSwitchBaseConfigUsers(ipValue);
				trapEntity.setUsers(users);
				node = commService.findSwitchTopoNodeByIp(ipValue);
				if(node == null){
					node = commService.findVirtualNodeByIp(ipValue);
					if(null != node){
						deviceType = 0;
					}
				}
				
				//ȡ��nodeId
				if(null != node){
					nodeId = node.getId();
				}
			} else if (deviceType == Constants.DEV_ONU) {
				ipValue = trapEntity.getWarnOnuMac();
				node = commService.findOnuTopoNodeByMac(ipValue);
				if(node == null){
					node = commService.findVirtualNodeByIp(ipValue);
					if(null != node){
						deviceType = 0;
					}
				}
				
				//ȡ��nodeId
				if(null != node){
					nodeId = node.getId();
				}
			} else if (deviceType == Constants.DEV_OLT) {
				ipValue = trapEntity.getIpValue();
				String users = commService.getOLTUsers(ipValue);
				trapEntity.setUsers(users);
				node = commService.findOLTTopoNodeByIp(ipValue);
				if(node == null){
					node = commService.findVirtualNodeByIp(ipValue);
					if(null != node){
						deviceType = 0;
					}
				}
				
				//ȡ��nodeId
				if(null != node){
					nodeId = node.getId();
				}
			} else if (deviceType == Constants.DEV_SWITCHER3) {
				SwitchLayer3 switchLayer3 = commService.getSwitcher3ByMac(ipValue);
				if (switchLayer3 != null) {
					ipValue = switchLayer3.getIpValue();
					trapEntity.setUsers(switchLayer3.getName());
					trapEntity.setIpValue(ipValue);
					trapEntity.setIpValue(switchLayer3.getIpValue());
					
					node = commService.findSwitchTopoNodeLevel3ByIp(ipValue);
					if(node == null){
						node = commService.findVirtualNodeByIp(ipValue);
						if(null != node){
							deviceType = 0;
						}
					}
					
					//ȡ��nodeId
					if(null != node){
						nodeId = node.getId();
					}
				}
			}
			commService.updateEntity(trapEntity); //����TrapWarningEntity
		}
		
		//��nodeId��Ϊnullʱ,����node�澯
		if(null != nodeId){
			commService.updateNode(Constants.NORMAL, nodeId); //����NodeEntity��״̬ΪNORMAL
			trapEntity.setDeviceType(deviceType);
			WarningEntity warningEntity = copyWarningInfo(trapEntity,nodeId);
			warningEntity.setIpValue(ipValue);
			warningEntity.setNodeId(nodeId);
			commService.saveEntity(warningEntity);//����WarningEntity
			
			//��ͻ��˷��͸澯
			try {
				smtcService.sendWarningMessage(MessageNoConstants.WARNING, warningEvent,
										ipValue, "", deviceType, warningEntity);
			} catch (JMSException e) {
				logger.error("",e);
			}
			
			NodeEntity nodeEntity = commService.queryNodeEntity(nodeId);
			if (null != nodeEntity){
				this.warningNode(nodeEntity,MessageNoConstants.WARNINGNODE,warningEvent, ipValue, deviceType);
				this.warningSubNet(nodeEntity,MessageNoConstants.WARNINGNODE,warningEvent, ipValue, deviceType);
			}
		}

		if (linkId != null) {
			commService.updateLink(Constants.L_CONNECT, linkId);   //����LinkEntity��״̬ΪL_CONNECT
			
			//��nodeId��Ϊnull,����ͻ��˷��澯,�����ظ���
			if (nodeId == null){  
				trapEntity.setDeviceType(deviceType);
				WarningEntity warningEntity = copyWarningInfo(trapEntity,nodeId);
				warningEntity.setIpValue(ipValue);
				warningEntity.setNodeId(linkId);
				commService.saveEntity(warningEntity);  //����WarningEntity
				
				try {//��ͻ��˷��͸澯
					smtcService.sendWarningMessage(MessageNoConstants.WARNING, warningEvent,
											ipValue, "", deviceType, warningEntity);
				} catch (JMSException e) {
					logger.error("",e);
				}
			}
			LinkEntity linkEntity = commService.queryLinkEntity(linkId);
			if (linkEntity != null) {
				linkEntity.setStatus(Constants.L_CONNECT);
				this.warningLink(linkEntity,
						MessageNoConstants.WARNINGLINK,
						warningEvent, ipValue, deviceType);
				this.warningBlockLink(linkEntity,
						MessageNoConstants.WARNINGLINK,
						warningEvent, ipValue, deviceType);
				logger.info("linkUP�澯");
			} else {
				logger.info("LinkUP ��ʵ��û�ҵ�");
			}
		}

		if (nodeId != null || linkId != null){
			//////////////////////
			int warningLevel = queryWarningLevel(warningEvent);
			
//			List<UserEntity> userEntities = getUserByIp(nodeId);
			NodeEntity nodeEntity = commService.queryNodeEntity(nodeId);
			Set<UserEntity> userEntities = nodeEntity.getUsers();
			
			UserEntity superUser = nmsService.getSuperAdmin();
			if (superUser != null){
				userEntities.add(nmsService.getSuperAdmin());
			}
			
			if(userEntities == null || userEntities.size() < 1 ){
				logger.error("�����ߵ��豸" + ipValue + "û�����û������Բ�����Ż��ʼ�֪ͨ..");
			}
			else{
				logger.info("LinkUp ������豸���û�����" + userEntities.size());
				for(UserEntity userEntity : userEntities){
					logger.info("LinkUp�澯�û�����" + userEntity.getUserName());
					sendMsgAndEmailLocal.sendMsgAndEmail(
							userEntity, ipValue, warningEvent,portNo, warningLevel,"");
				}
			}
		}
	}
	
	/**
	 * /**
	 * ����
	 * Constants.LINKDOWN
	 * 
	 * @param trapEntity
	 */
	private void dealwithWarningLinkdown(TrapWarningEntity trapEntity){//�澯��Ϣ��linkdown
		logger.info("LINKDOWN�澯");
		
		int portNo = trapEntity.getPortNo();  //�ϱ��澯���豸�˿�
		int portType = trapEntity.getPortType(); //�˿�����
		int warningEvent = trapEntity.getWarningEvent(); //�澯�¼�
		
		int deviceType = trapEntity.getDeviceType(); //�豸����
		Long nodeId = null;   //�豸�Ľڵ�ID
		Long linkId = null;   //�豸������ID
		
		String ipValue = "";  //�澯�豸��IP��ַ

		if (deviceType == Constants.DEV_SWITCHER2) {
			ipValue = trapEntity.getIpValue();  
			
			NodeEntity nodeEntity = commService.findSwitchTopoNodeByIp(ipValue);
			if(nodeEntity == null){
				nodeEntity = commService.findVirtualNodeByIp(ipValue);
				if(null != nodeEntity){
					deviceType = 0;
				}
			}
			//��ȡnodeId
			if(null != nodeEntity){
				nodeId = nodeEntity.getId();
			}
			
			//���nodeId��Ϊnull,��ȡlinkId
			if (nodeId != null){
				linkId = commService.findLinkEntityByIPPort(ipValue, portNo);
			}
			
			//����������仰������ΪʲôҪ������,ΪʲôҪȡusers��ֵ
			String users = commService.getSwitchBaseConfigUsers(ipValue);
			trapEntity.setUsers(users);
			
			logger.info("LinkDown ������ ID��" + linkId);
			logger.info("LINKDOWN ���㽻�����澯 IP��" + ipValue);
		} else if (deviceType == Constants.DEV_ONU) {
			int slot = trapEntity.getSlotNum(); 
			ipValue = trapEntity.getIpValue();
			String mac = trapEntity.getWarnOnuMac();  //onu��mac��ַ
			NodeEntity nodeEntity = commService.findOnuTopoNodeByMac(mac);
			if(nodeEntity == null){
				nodeEntity = commService.findVirtualNodeByIp(ipValue);
				if(null != nodeEntity){
					deviceType = 0;
				}
			}
			
			//��ȡnodeId
			if(null != nodeEntity){
				nodeId = nodeEntity.getId();
			}
			
			//���nodeId��Ϊnull,��ȡlinkId
			if (nodeId != null){
				linkId = commService.findOnuLinkEntityByEponInfo(ipValue,slot, portNo, mac);
			}
			
			logger.info("LinkDown  ONU�� ID��" + linkId);
		} else if (deviceType == Constants.DEV_OLT) {
			int slot = trapEntity.getSlotNum();
			ipValue = trapEntity.getIpValue(); //�澯�豸��IP��ַ
			
			NodeEntity nodeEntity = commService.findOLTTopoNodeByIp(ipValue);
			if(nodeEntity == null){
				nodeEntity = commService.findVirtualNodeByIp(ipValue);
				if(null != nodeEntity){
					deviceType = 0;
				}
			}
			
			//��ȡnodeId
			if(null != nodeEntity){
				nodeId = nodeEntity.getId();
			}
			
			//���nodeId��Ϊnull,��ȡlinkId
			if (nodeId != null){
				linkId = commService.findOLTLink(ipValue, slot,portNo,portType);
			}

			//?????ͬ��
			String users = commService.getOLTUsers(ipValue);
			trapEntity.setUsers(users);
			
			logger.info("LinkDown OLT�� ID��" + linkId);
		} else if (deviceType == Constants.DEV_SWITCHER3) {
			String mac = trapEntity.getIpValue(); //�����㽻���� �˷������ص���mac��ַ
			logger.info("LinkDown ���㽻�����澯 mac:" + mac);
			
			/**
			 * ͨ����������㽻������mac��ַ�õ�switchLayer3,
			 * Ȼ��ͨ��switchLayer3��ipValue��SwitchTopoNodeLevel3
			 * �е�ipValue�����õ�SwitchTopoNodeLevel3
			 */
			SwitchLayer3 switchLayer3 = commService.getSwitcher3ByMac(mac);
			NodeEntity nodeEntity = null;
			if (switchLayer3 != null) {
				ipValue = switchLayer3.getIpValue();
				trapEntity.setUsers(switchLayer3.getName());
				trapEntity.setIpValue(ipValue);
				
				nodeEntity = commService.findSwitchTopoNodeLevel3ByIp(ipValue);
				if(nodeEntity == null){
					nodeEntity = commService.findVirtualNodeByIp(ipValue);
					if(null != nodeEntity){
						deviceType = 0;
					}
				}
				logger.info("LinkDown  ���㽻�����澯 IP:" + ipValue);
			}
			
			//��ȡnodeId
			if(null != nodeEntity){
				nodeId = nodeEntity.getId();
			}
			
			//���nodeId��Ϊnull,��ȡlinkId
			if (nodeId != null){
				linkId = commService.findLinkEntityByIPPort(ipValue, portNo, portType);
			}
			logger.info("LinkDown ������ ID��" + linkId);

		}
		commService.updateEntity(trapEntity);
		
		
		//��nodeId��Ϊnullʱ,����node�澯
		if(null != nodeId){
			NodeEntity nodeEntity = commService.queryNodeEntity(nodeId);
			commService.updateNode(Constants.HASWARNING, nodeId); //����NodeEntity��״̬HASWARNING
			
			trapEntity.setDeviceType(deviceType);
			WarningEntity warningEntity = copyWarningInfo(trapEntity,nodeId);
			warningEntity.setIpValue(ipValue);
			warningEntity.setNodeId(nodeId);
			commService.saveEntity(warningEntity);//����WarningEntity
			
			//��ͻ��˷��͸澯
			try {
				smtcService.sendWarningMessage(MessageNoConstants.WARNING, warningEvent,
										ipValue, "", deviceType, warningEntity);
			} catch (JMSException e) {
				logger.info("",e);
			}

			if (null != nodeEntity){
				this.warningNode(nodeEntity,MessageNoConstants.WARNINGNODE,warningEvent, ipValue, deviceType);
				this.warningSubNet(nodeEntity,MessageNoConstants.WARNINGNODE,warningEvent, ipValue, deviceType);
			}
		}
		else{
			logger.info("LinkDown node�ڵ�û�ҵ�");
		}

		if (linkId != null) {
			commService.updateLink(Constants.L_UNCONNECT, linkId);   //����LinkEntity��״̬ΪL_CONNECT
			
			//��nodeId��Ϊnull,����ͻ��˷��澯,�����ظ���
			if (nodeId == null){   
				trapEntity.setDeviceType(deviceType);
				WarningEntity warningEntity = copyWarningInfo(trapEntity,nodeId);
				warningEntity.setIpValue(ipValue);
				warningEntity.setNodeId(linkId);
				commService.saveEntity(warningEntity);  //����WarningEntity
				
				try {//��ͻ��˷��͸澯
					smtcService.sendWarningMessage(MessageNoConstants.WARNING, warningEvent,
											ipValue, "", deviceType, warningEntity);
				} catch (JMSException e) {
					logger.error("",e);
				}
			}
			
			LinkEntity linkEntity = commService.queryLinkEntity(linkId);
			if (linkEntity != null) {
				linkEntity.setStatus(Constants.L_UNCONNECT);
				this.warningLink(linkEntity,
						MessageNoConstants.WARNINGLINK,
						warningEvent, ipValue, deviceType);
				this.warningBlockLink(linkEntity,
						MessageNoConstants.WARNINGLINK,
						warningEvent, ipValue, deviceType);
				logger.info("linkDown�澯");
			} else {
				logger.info("LinkDown ��ʵ��û�ҵ�");
			}
		}
		else{
			logger.info("LinkDown ��û�ҵ�");
		}

		if (nodeId != null || linkId != null){
			//////////////////////
			int warningLevel = queryWarningLevel(warningEvent);
			
			NodeEntity nodeEntity = commService.queryNodeEntity(nodeId);
			Set<UserEntity> userEntities = nodeEntity.getUsers();
			
			UserEntity superUser = nmsService.getSuperAdmin();
			if (superUser != null){
				userEntities.add(nmsService.getSuperAdmin());
			}
			
			if(userEntities == null || userEntities.size() < 1 ){
				logger.error("�����ߵ��豸" + ipValue + "û�����û������Բ�����Ż��ʼ�֪ͨ..");
			}
			else{
				logger.info("LinkDown ������豸���û�����" + userEntities.size());
				for(UserEntity userEntity : userEntities){
					logger.info("LinkDown�澯�û�����" + userEntity.getUserName());
					sendMsgAndEmailLocal.sendMsgAndEmail(
							userEntity, ipValue, warningEvent,portNo, warningLevel,"");
				}
			}
		}
	}
	
	/**
	 * �����澯�����Ϣ
	 * @param trapEntity
	 */
	private void dealwithWarningRmon(TrapWarningEntity trapEntity){   // �����澯�����Ϣ
		logger.error("�����澯");
		int portNo = trapEntity.getPortNo();  //�ϱ��澯���豸�˿�
		int portType = trapEntity.getPortType(); //�˿�����
		int warningEvent = trapEntity.getWarningEvent(); //�澯�¼�
		
		int deviceType = trapEntity.getDeviceType(); //�豸����
		Long nodeId = null;   //�豸�Ľڵ�ID
		Long linkId = null;   //�豸������ID
		
		String ipValue = "";  //�澯�豸��IP��ַ

		if (deviceType == Constants.DEV_SWITCHER2) {
			ipValue = trapEntity.getIpValue();  
			
			NodeEntity nodeEntity = commService.findSwitchTopoNodeByIp(ipValue);
			if(nodeEntity == null){
				nodeEntity = commService.findVirtualNodeByIp(ipValue);
				if(null != nodeEntity){
					deviceType = 0;
				}
			}
			
			//��ȡnodeId
			if(null != nodeEntity){
				nodeId = nodeEntity.getId();
			}
			
			//���nodeId��Ϊnull,��ȡlinkId
			if (nodeId != null){
				linkId = commService.findLinkEntityByIPPort(ipValue, portNo);
			}
			
			//����������仰������ΪʲôҪ������,ΪʲôҪȡusers��ֵ
			String users = commService.getSwitchBaseConfigUsers(ipValue);
			trapEntity.setUsers(users);
		} else if (deviceType == Constants.DEV_ONU) {
			int slot = trapEntity.getSlotNum(); 
			ipValue = trapEntity.getIpValue();
			String mac = trapEntity.getWarnOnuMac();  //onu��mac��ַ
			NodeEntity nodeEntity = commService.findOnuTopoNodeByMac(mac);
			if(nodeEntity == null){
				nodeEntity = commService.findVirtualNodeByIp(ipValue);
				if(null != nodeEntity){
					deviceType = 0;
				}
			}
			
			//��ȡnodeId
			if(null != nodeEntity){
				nodeId = nodeEntity.getId();
			}
			
			//���nodeId��Ϊnull,��ȡlinkId
			if (nodeId != null){
				linkId = commService.findOnuLinkEntityByEponInfo(ipValue,slot, portNo, mac);
			}
		} else if (deviceType == Constants.DEV_OLT) {
			int slot = trapEntity.getSlotNum();
			ipValue = trapEntity.getIpValue(); //�澯�豸��IP��ַ
			
			NodeEntity nodeEntity = commService.findOLTTopoNodeByIp(ipValue);
			if(nodeEntity == null){
				nodeEntity = commService.findVirtualNodeByIp(ipValue);
				if(null != nodeEntity){
					deviceType = 0;
				}
			}
			
			//��ȡnodeId
			if(null != nodeEntity){
				nodeId = nodeEntity.getId();
			}
			
			//���nodeId��Ϊnull,��ȡlinkId
			if (nodeId != null){
				linkId = commService.findOLTLink(ipValue, slot,portNo,portType);
			}

			//?????ͬ��
			String users = commService.getOLTUsers(ipValue);
			trapEntity.setUsers(users);
		} else if (deviceType == Constants.DEV_SWITCHER3) {
			String mac = trapEntity.getIpValue(); //�����㽻���� �˷������ص���mac��ַ
			logger.info("Rmon ���㽻�����澯 mac:" + mac);
			
			/**
			 * ͨ����������㽻������mac��ַ�õ�switchLayer3,
			 * Ȼ��ͨ��switchLayer3��ipValue��SwitchTopoNodeLevel3
			 * �е�ipValue�����õ�SwitchTopoNodeLevel3
			 */
			SwitchLayer3 switchLayer3 = commService.getSwitcher3ByMac(mac);
			NodeEntity nodeEntity = null;
			if (switchLayer3 != null) {
				ipValue = switchLayer3.getIpValue();
				trapEntity.setUsers(switchLayer3.getName());
				trapEntity.setIpValue(ipValue);
				
				nodeEntity = commService.findSwitchTopoNodeLevel3ByIp(ipValue);
				if(nodeEntity == null){
					nodeEntity = commService.findVirtualNodeByIp(ipValue);
					if(null != nodeEntity){
						deviceType = 0;
					}
				}
				logger.info("Rmon  ���㽻�����澯 IP:" + ipValue);
			}
			
			//��ȡnodeId
			if(null != nodeEntity){
				nodeId = nodeEntity.getId();
			}
			
			//���nodeId��Ϊnull,��ȡlinkId
			if (nodeId != null){
				linkId = commService.findLinkEntityByIPPort(ipValue, portNo, portType);
			}
		}

		if (nodeId != null){
			trapEntity.setDeviceType(deviceType);
			WarningEntity warningEntity = copyWarningInfo(trapEntity,nodeId);
			warningEntity.setIpValue(ipValue);
			
			commService.saveEntity(warningEntity);
			commService.updateEntity(trapEntity);
			try {
				smtcService.sendWarningMessage(MessageNoConstants.WARNING, warningEvent,
										ipValue, "", deviceType, warningEntity);
			} catch (JMSException e) {
				this.logger.error("",e);
			}
			
			/////////////////////////////////////////
			int warningLevel = queryWarningLevel(warningEvent);
			
			NodeEntity nodeEntity = commService.queryNodeEntity(nodeId);
			Set<UserEntity> userEntities = nodeEntity.getUsers();
			
			UserEntity superUser = nmsService.getSuperAdmin();
			if (superUser != null){
				userEntities.add(nmsService.getSuperAdmin());
			}
			
			if(userEntities == null || userEntities.size() < 1 ){
				logger.error("�����ߵ��豸" + ipValue + "û�����û������Բ�����Ż��ʼ�֪ͨ..");
			}
			else{
				logger.info("Rmon ������豸���û�����" + userEntities.size());
				for(UserEntity userEntity : userEntities){
					logger.info("Rmon�澯�û�����" + userEntity.getUserName());
					sendMsgAndEmailLocal.sendMsgAndEmail(
							userEntity, ipValue, warningEvent,portNo, warningLevel,"");
				}
			}
		}
	}
	
	/**
	 * Ping�澯�����Ϣ
	 * @param trapEntity
	 */
	private void dealwithWarningPing(TrapWarningEntity trapEntity){   //  Ping�澯�����Ϣ
		logger.info("ping��ͨ�澯");
		int portNo = trapEntity.getPortNo();  //�ϱ��澯���豸�˿�
		int portType = trapEntity.getPortType(); //�˿�����
		int warningEvent = trapEntity.getWarningEvent(); //�澯�¼�
		
		int deviceType = 0; //�豸����
		
		String ipValue = trapEntity.getIpValue();  //�澯�豸��IP��ַ
		
		Long nodeId = null;   //�豸�Ľڵ�ID
		NodeEntity nodeEntity = null;
		
		//����
		if (nodeEntity == null){
			nodeEntity = commService.findSwitchTopoNodeByIp(ipValue);  
			if (nodeEntity != null){
				deviceType = Constants.DEV_SWITCHER2;
			}
		}

		//onu
		if (nodeEntity == null){
			String mac = trapEntity.getWarnOnuMac();  //onu��mac��ַ
			if (mac != null && !("".equals(mac))){
				nodeEntity = commService.findOnuTopoNodeByMac(mac);
				if (nodeEntity != null){
					deviceType = Constants.DEV_ONU;
				}
			}
		}
		
		//olt
		if (nodeEntity == null){
			nodeEntity = commService.findOLTTopoNodeByIp(ipValue);
			if (nodeEntity != null){
				deviceType = Constants.DEV_OLT;
			}
		}
		
		if(nodeEntity == null){
			nodeEntity = commService.findVirtualNodeByIp(ipValue);
			//û���豸����
		}
		
		//����
		if (nodeEntity == null){
			String mac = trapEntity.getIpValue(); //�����㽻���� �˷������ص���mac��ַ
			
			/**
			 * ͨ����������㽻������mac��ַ�õ�switchLayer3,
			 * Ȼ��ͨ��switchLayer3��ipValue��SwitchTopoNodeLevel3
			 * �е�ipValue�����õ�SwitchTopoNodeLevel3
			 */
			SwitchLayer3 switchLayer3 = commService.getSwitcher3ByMac(mac);
			if (switchLayer3 != null) {
				ipValue = switchLayer3.getIpValue();
				trapEntity.setUsers(switchLayer3.getName());
				trapEntity.setIpValue(ipValue);
				
				nodeEntity = commService.findSwitchTopoNodeLevel3ByIp(ipValue);
				if(nodeEntity != null){
					deviceType = Constants.DEV_SWITCHER3;
				}
			}
		}

		if (nodeEntity != null){
			nodeId = nodeEntity.getId();
			
			
			if (warningEvent == Constants.PINGOUT){
				commService.updateNode(Constants.HASWARNING, nodeId); //����NodeEntity��״̬HASWARNING
			}	
			else{
				commService.updateNode(Constants.NORMAL, nodeId); //����NodeEntity��״̬NORMAL
			}
			
			trapEntity.setDeviceType(deviceType);
			WarningEntity warningEntity = copyWarningInfo(trapEntity,nodeId);
			warningEntity.setNodeId(nodeId);
			commService.saveEntity(warningEntity);
			commService.updateEntity(trapEntity);

			try {
				smtcService.sendWarningMessage(
						MessageNoConstants.WARNING, warningEvent,
						trapEntity.getIpValue(), "", deviceType,
						warningEntity);
			} catch (JMSException e) {
				this.logger.error("",e);
			}
			
			this.warningNode(nodeEntity,MessageNoConstants.WARNINGNODE,warningEvent, ipValue, deviceType);
			this.warningSubNet(nodeEntity,MessageNoConstants.WARNINGNODE,warningEvent, ipValue, deviceType);

			/////////////////////////////////////////
			int warningLevel = queryWarningLevel(warningEvent);
			
//			List<UserEntity> userEntities = getUserByIp(nodeId);
//			NodeEntity entity = commService.queryNodeEntity(nodeId);
			Set<UserEntity> userEntities = nodeEntity.getUsers();
			
			UserEntity superUser = nmsService.getSuperAdmin();
			if (superUser != null){
				userEntities.add(nmsService.getSuperAdmin());
			}
			
			if(userEntities == null || userEntities.size() < 1 ){
				logger.error("��ʱPing���豸" + ipValue + "û�����û������Բ�����Ż��ʼ�֪ͨ..");
			}
			else{
				logger.info("Not Ping ������豸���û�����" + userEntities.size());
				for(UserEntity userEntity : userEntities){
					logger.info("Not Ping �澯�û�����" + userEntity.getUserName());
					sendMsgAndEmailLocal.sendMsgAndEmail(
							userEntity, ipValue, warningEvent,portNo, warningLevel,"");
				}
			}
		}
	}
	
	/**
	 * �����յ��ĸ澯��Ϣ
	 * 
	 * @param trapType
	 * @param om
	 * @throws JMSException
	 */
	private synchronized void dealwithTraps(Object om) throws JMSException {

		try {
			if (CacheDatas.getInstance().isRefreshing()) {
				logger.info("�������˷��ֲ�����澯..");
			} else {
				if (om != null) {
					CacheDatas.getInstance().setWarninging(true);
					TrapWarningEntity warning = (TrapWarningEntity) om;
					logger.warn("ǰ�û��澯ʱ�䣺" + warning.getSampleTime());
					logger.warn("�豸���ͣ�"
									+ (warning.getDeviceType() == Constants.DEV_SWITCHER2 ? "���㽻����" : ""));
					logger.warn("�豸���ͣ�"
									+ (warning.getDeviceType() == Constants.DEV_SWITCHER3 ? "���㽻����" : ""));
					logger.warn("�豸���ͣ�"
									+ (warning.getDeviceType() == Constants.DEV_OLT ? "OLT" : ""));
					logger.warn("�豸���ͣ�"
									+ (warning.getDeviceType() == Constants.DEV_ONU ? "ONU" : ""));
					logger.warn("�˿����ͣ�" + warning.getPortType());
					int warningEvent = warning.getWarningEvent(); //�澯�¼�
					logger.warn("�澯�¼���" + warningEvent);
					
					if (warningEvent == Constants.COLDSTART
							|| warningEvent == Constants.WARMSTART
							|| warningEvent == Constants.LINKUP) {// ���������Ϣ:��������������������linkup   
						dealwithWarningLinkUp(warning);
					} 
					else if (warningEvent == Constants.LINKDOWN) {// �澯��Ϣ��linkdown   
						dealwithWarningLinkdown(warning);
					} 
					else if (warningEvent == Constants.REMONTHING) {// �����澯�����Ϣ
						dealwithWarningRmon(warning);
					} 
					else if (warningEvent == Constants.PINGOUT 
							|| warningEvent == Constants.PINGIN) {
						dealwithWarningPing(warning);
					}

					CacheDatas.getInstance().setWarninging(false);
				}
			}
		} catch (Exception e) {
			CacheDatas.getInstance().setWarninging(false);
			logger.error("���ܸ澯��", e);
		}
	}
	
	/**
	 * ����NodeEntity��״̬SHUTDOWN��NORMAL
	 * @param nodeId
	 */
	private boolean  hasNodeLink (NodeEntity nodeEntity){
		boolean hasLink = false;  //�˽ڵ�������

		List<LinkEntity> linkEntities = nmsService.queryLinkEntity(nodeEntity);
		if(linkEntities == null || linkEntities.size() < 1){
			hasLink = false;  //�˽ڵ�û������
		}
		else{
			for (LinkEntity entity : linkEntities) {
				if (entity.getStatus() == Constants.L_CONNECT){
					hasLink = true;
					break;
				}
			}
		}
		
		return hasLink;
	}
	
	/**
	 * �����յ��ĸ澯��Ϣ
	 * 
	 * @param trapType
	 * @param om
	 * @throws JMSException
	 */
	private synchronized void dealwithTrap(Object om) throws JMSException {

		try {
			if (CacheDatas.getInstance().isRefreshing()) {
				logger.info("�������˷��ֲ�����澯..");
			} else {
				if (om != null) {
					CacheDatas.getInstance().setWarninging(true);
					TrapWarningEntity warning = (TrapWarningEntity) om;
					logger.warn("ǰ�û��澯ʱ�䣺" + warning.getSampleTime());
					logger
							.warn("�豸���ͣ�"
									+ (warning.getDeviceType() == Constants.DEV_SWITCHER2 ? "���㽻����"
											: ""));
					logger
							.warn("�豸���ͣ�"
									+ (warning.getDeviceType() == Constants.DEV_SWITCHER3 ? "���㽻����"
											: ""));
					logger
							.warn("�豸���ͣ�"
									+ (warning.getDeviceType() == Constants.DEV_OLT ? "OLT"
											: ""));
					logger
							.warn("�豸���ͣ�"
									+ (warning.getDeviceType() == Constants.DEV_ONU ? "ONU"
											: ""));
					logger.warn("�˿����ͣ�" + warning.getPortType());
					int deviceType = warning.getDeviceType();
					String[] uns = null;
					int warningEvent = warning.getWarningEvent(); //�澯�¼�
					logger.warn("�澯���ͣ�" + warningEvent);
					int warningLevel = queryWarningLevel(warningEvent);
					String ipValue = null;
					Long linkId = null;

					if (warningEvent == Constants.COLDSTART
							|| warningEvent == Constants.WARMSTART
							|| warningEvent == Constants.LINKUP) {// ���������Ϣ:��������������������linkup
						Long targetDiagramId = null;
						int portNo = warning.getPortNo();
						int portType = warning.getPortType();
						if (warningEvent == Constants.LINKUP) {
							logger.info("LINKUP�澯");
							ipValue = warning.getIpValue();
							uns = getUNsByIp(ipValue, deviceType);
							if (uns != null) {
								logger.info("LinkUp �豸���û�����" + uns.length);
							}
							if (deviceType == Constants.DEV_SWITCHER2) {
								linkId = commService.findLinkEntityByIPPort(
										ipValue, portNo);
								String users = commService
										.getSwitchBaseConfigUsers(ipValue);
								warning.setUsers(users);
								logger.info("LinkUp ���㽻�����澯 IP:" + ipValue);

							} else if (deviceType == Constants.DEV_ONU) {
								int slot = warning.getSlotNum();
								String mac = warning.getWarnOnuMac();
								linkId = commService
										.findOnuLinkEntityByEponInfo(ipValue,
												slot, portNo, mac);
								logger.info("LinkUp  ONU�澯");
							} else if (deviceType == Constants.DEV_OLT) {
								int slot = warning.getSlotNum();
								ipValue = warning.getIpValue();
								linkId = commService.findOLTLink(ipValue, slot,
										portNo,portType);
								String users = commService.getOLTUsers(ipValue);
								warning.setUsers(users);
								logger.info("LinkUp OLT�澯");
							} else if (deviceType == Constants.DEV_SWITCHER3) {
								logger.info("LinkUp ���㽻�����澯 mac:" + ipValue);
								SwitchLayer3 switchLayer3 = commService
										.getSwitcher3ByMac(ipValue);
								if (switchLayer3 != null) {
									ipValue = switchLayer3.getIpValue();
									warning.setUsers(switchLayer3.getName());
									warning.setIpValue(ipValue);
									logger
											.info("LinkUp  ���㽻�����澯 IP:"
													+ ipValue);
								}
								linkId = commService.findLinkEntityByIPPort(
										ipValue, portNo, portType);
							}
							if (linkId != null) {
								targetDiagramId = linkId;
							}
							commService.updateEntity(warning);
						} else {
							NodeEntity node = null;
							if (deviceType == Constants.DEV_SWITCHER2) {
								ipValue = warning.getIpValue();
								String users = commService
										.getSwitchBaseConfigUsers(ipValue);
								warning.setUsers(users);
								node = commService
										.findSwitchTopoNodeByIp(ipValue);
							} else if (deviceType == Constants.DEV_ONU) {
								ipValue = warning.getWarnOnuMac();
								node = commService
										.findOnuTopoNodeByMac(ipValue);
							} else if (deviceType == Constants.DEV_OLT) {
								ipValue = warning.getIpValue();
								String users = commService.getOLTUsers(ipValue);
								warning.setUsers(users);
								node = commService.findOLTTopoNodeByIp(ipValue);
							} else if (deviceType == Constants.DEV_SWITCHER3) {
								SwitchLayer3 switchLayer3 = commService
										.getSwitcher3ByMac(ipValue);
								if (switchLayer3 != null) {
									ipValue = switchLayer3.getIpValue();
									warning.setUsers(switchLayer3.getName());
									warning.setIpValue(ipValue);
									warning.setIpValue(switchLayer3
											.getIpValue());
								}
							}
							if (node != null) {
								node.setStatus(1);
								targetDiagramId = node.getId();
							}
							commService.updateEntity(warning);
						}

						if (targetDiagramId != null) {

							commService.updateLink(Constants.L_CONNECT, linkId);
							NoteEntity swarning = copyNoteInfo(warning);
//							WarningEntity swarning = copyWarningInfo(warning);
							swarning.setIpValue(ipValue);
							swarning.setTargetDiagramId(targetDiagramId);
							commService.saveEntity(swarning);
							smtcService.sendWarningMessage(
									MessageNoConstants.WARNING, warningEvent,
									ipValue, "", deviceType, swarning);
							LinkEntity linkEntity = commService
									.queryLinkEntity(linkId);
							if (linkEntity != null) {
								linkEntity.setStatus(Constants.L_CONNECT);
								this.warningLink(linkEntity,
										MessageNoConstants.WARNINGLINK,
										warningEvent, ipValue, deviceType);
								this.warningBlockLink(linkEntity,
										MessageNoConstants.WARNINGLINK,
										warningEvent, ipValue, deviceType);
								this.warningNode(linkEntity,
										MessageNoConstants.WARNINGNODE,
										warningEvent, ipValue, deviceType);
								this.warningSubNet(linkEntity,
										MessageNoConstants.WARNINGNODE,
										warningEvent, ipValue, deviceType);
								logger.info("linkUP�澯");
							} else {
								logger.info("LinkUP ��ʵ��û�ҵ�");
							}
							if (uns != null && uns.length != 0) {
								for (String userName : uns) {
									UserEntity userEntity = datacache
											.getUser(userName);
									if (userEntity != null) {
										logger.info("LinkUp�澯�û�����" + userName);
									}
									sendMsgAndEmailLocal.sendMsgAndEmail(
											userEntity, ipValue, warningEvent,
											portNo, warningLevel,"");
								}
							} else {
								logger.error("�����ߵ��豸" + ipValue
										+ "û�����û������Բ�����Ż��ʼ�֪ͨ..");
							}

						}

					} else if (warningEvent == Constants.LINKDOWN) {// �澯��Ϣ��linkdown
						logger.info("LINKDOWN�澯");
						ipValue = warning.getIpValue();
						int portNo = warning.getPortNo();
						int portType = warning.getPortType();
						uns = getUNsByIp(ipValue, deviceType);
						if (uns != null) {
							logger.info("LinkDown �豸���û�����" + uns.length);
						}
						if (deviceType == Constants.DEV_SWITCHER2) {
							linkId = commService.findLinkEntityByIPPort(
									ipValue, portNo);
							String users = commService
									.getSwitchBaseConfigUsers(ipValue);
							warning.setUsers(users);
							logger.info("LinkDown ������ ID��" + linkId);
							logger.info("LINKDOWN ���㽻�����澯 IP��" + ipValue);
						} else if (deviceType == Constants.DEV_ONU) {
							int slot = warning.getSlotNum();
							String mac = warning.getWarnOnuMac();
							linkId = commService.findOnuLinkEntityByEponInfo(
									ipValue, slot, portNo, mac);
							logger.info("LinkDown  ONU�� ID��" + linkId);
							logger.info("LINKDOWN ONU�澯 ");
						} else if (deviceType == Constants.DEV_OLT) {
							int slot = warning.getSlotNum();
							linkId = commService.findOLTLink(ipValue, slot,
									portNo,portType);
							String users = commService.getOLTUsers(ipValue);
							warning.setUsers(users);
							logger.info("LinkDown OLT�� ID��" + linkId);
							logger.info("LINKDOWN OLT�澯 ");
						} else if (deviceType == Constants.DEV_SWITCHER3) {
							SwitchLayer3 switchLayer3 = commService
									.getSwitcher3ByMac(ipValue);
							logger.info("LINKDOWN ���㽻�����澯 mac�� " + ipValue);
							if (switchLayer3 != null) {
								ipValue = switchLayer3.getIpValue();
								if (switchLayer3.getManagerEntity() != null) {
									warning.setUsers(switchLayer3
											.getManagerEntity().getName());
								}
								warning.setIpValue(switchLayer3.getIpValue());
								logger.info("LINKDOWN ���㽻�����澯 IP�� " + ipValue);
							}
							linkId = commService.findLinkEntityByIPPort(
									ipValue, portNo, portType);
							logger.info("LinkDown ������ ID��" + linkId);

						}
						int warningCategory = WarningCategoryBean.getInstance().getWarningCategory(warning.getWarningEvent());
						warning.setWarningCategory(warningCategory);
						commService.updateEntity(warning);
						if (linkId != null) {

							commService.updateLink(Constants.L_UNCONNECT,
									linkId);
							WarningEntity warningEntity = copyWarningInfo(warning);
							warningEntity.setIpValue(ipValue);
							warningEntity.setLinkId(linkId);
							commService.saveEntity(warningEntity);
							smtcService.sendWarningMessage(
									MessageNoConstants.WARNING, warningEvent,
									ipValue, "", deviceType, warningEntity);

							LinkEntity linkEntity = commService
									.queryLinkEntity(linkId);
							if (linkEntity != null) {
								linkEntity.setStatus(Constants.L_UNCONNECT);
								this.warningLink(linkEntity,
										MessageNoConstants.WARNINGLINK,
										warningEvent, ipValue, deviceType);
								this.warningBlockLink(linkEntity,
										MessageNoConstants.WARNINGLINK,
										warningEvent, ipValue, deviceType);
								this.warningNode(linkEntity,
										MessageNoConstants.WARNINGNODE,
										warningEvent, ipValue, deviceType);
								this.warningSubNet(linkEntity,
										MessageNoConstants.WARNINGNODE,
										warningEvent, ipValue, deviceType);
								logger.info("linkDown�澯");

							} else {
								logger.info("LinkDown ��ʵ��û�ҵ�");
							}
							if (uns != null && uns.length != 0) {
								for (String userName : uns) {
									UserEntity userEntity = datacache
											.getUser(userName);
									if (userEntity != null) {
										logger.info("LindDown�澯֪ͨ�û�����"
												+ userEntity.getUserName());
									}
									sendMsgAndEmailLocal.sendMsgAndEmail(
											userEntity, ipValue, warningEvent,
											portNo, warningLevel,"");

								}
							} else {
								logger.error("�߰ε��澯���豸" + ipValue
										+ "û�����û������Բ�����Ż��ʼ�֪ͨ..");

							}

						} else {
							logger.info("LinkDown ��û�ҵ�");
						}

					} else if (warningEvent == Constants.REMONTHING) {// �����澯�����Ϣ
						logger.error("�����澯");
						ipValue = warning.getIpValue();
						int portNo = warning.getPortNo();
						int portType = warning.getPortType();
						uns = getUNsByIp(ipValue, deviceType);
						if (uns != null) {
							logger.info("����   �豸���û�����" + uns.length);
						}
						if (deviceType == Constants.DEV_SWITCHER2) {
							ipValue = warning.getIpValue();
							portNo = warning.getPortNo();
							linkId = commService.findLinkEntityByIPPort(
									ipValue, portNo);
							String users = commService
									.getSwitchBaseConfigUsers(ipValue);
							warning.setUsers(users);
						} else if (deviceType == Constants.DEV_ONU) {
							int slot = warning.getSlotNum();
							portNo = warning.getPortNo();
							ipValue = warning.getWarnOnuMac();
							String mac = warning.getWarnOnuMac();
							linkId = commService.findOnuLinkEntityByEponInfo(
									ipValue, slot, portNo, mac);
						} else if (deviceType == Constants.DEV_OLT) {
							ipValue = warning.getIpValue();
							int slot = warning.getSlotNum();
							portNo = warning.getPortNo();
							linkId = commService.findOLTLink(ipValue, slot,
									portNo,portType);
							String users = commService.getOLTUsers(ipValue);
							warning.setUsers(users);
						} else if (deviceType == Constants.DEV_SWITCHER3) {
							SwitchLayer3 switchLayer3 = commService
									.getSwitcher3ByMac(ipValue);
							if (switchLayer3 != null) {
								ipValue = switchLayer3.getIpValue();
								if (switchLayer3.getManagerEntity() != null) {
									warning.setUsers(switchLayer3
											.getManagerEntity().getName());
								}
								warning.setIpValue(ipValue);
								linkId = commService.findLinkEntityByIPPort(
										ipValue, portNo, portType);
							}

						}
						RmonEntity rmon = new RmonEntity();
						rmon.setCreateDate(warning.getSampleTime());
						rmon.setCurrentStatus(warning.getCurrentStatus());
						rmon.setDescs(warning.getDescs());
						rmon.setDeviceName(warning.getDeviceName());
						rmon.setIpValue(warning.getIpValue());
						rmon.setParamName(warning.getParamName());
						rmon.setPortNo(warning.getPortNo());
						rmon.setValue(warning.getValue());
						rmon.setWarningType(warningEvent);
						rmon.setDeviceType(deviceType);
						if (linkId != null) {
							rmon.setLinkId(linkId);
						}
						commService.saveEntity(rmon);
						commService.updateEntity(warning);
						smtcService.sendWarningMessage(
								MessageNoConstants.WARNING, warningEvent,
								ipValue, "", deviceType, rmon);
						if (uns != null && uns.length != 0) {
							for (String userName : uns) {
								UserEntity userEntity = datacache
										.getUser(userName);
								if (userEntity != null) {
									logger.info("�����澯֪ͨ�û�����" + userName);
								}
								sendMsgAndEmailLocal.sendMsgAndEmail(
										userEntity, ipValue, warningEvent,
										portNo, warningLevel,"");
							}
						} else {
							logger.error("�����澯���豸" + ipValue
									+ "û�����û������Բ�����Ż��ʼ�֪ͨ..");
						}

					} else if (warningEvent == Constants.PINGOUT) {
						logger.info("ping��ͨ�澯");
						WarningEntity warningEntity = copyWarningInfo(warning);
						warningEntity.setWarningLevel(1);
						String[] users = null;
						int type = 0;
						users = getUNsByIp(warningEntity.getIpValue(),
								Constants.DEV_SWITCHER2);

						if (users != null && users.length > 0) {
							type = Constants.DEV_SWITCHER2;
						} else {
							users = getUNsByIp(warningEntity.getIpValue(),
									Constants.DEV_SWITCHER3);
							if (users != null && users.length > 0) {
								type = Constants.DEV_SWITCHER3;
							} else {
								users = getUNsByIp(warningEntity.getIpValue(),
										Constants.DEV_OLT);
								if (users != null && users.length > 0) {
									type = Constants.DEV_OLT;
								}
							}
						}
						warningEntity.setWarningEvent(Constants.LINKDOWN);
						warningEntity.setDeviceType(type);
						warningEntity.setDescs("��ͨ");
						smtcService.sendWarningMessage(
								MessageNoConstants.WARNING, Constants.LINKDOWN,
								warning.getIpValue(), "", deviceType,
								warningEntity);
						if (users != null) {
							logger.info("Not Ping �豸���û�����" + users.length);
						}
						if (users != null && users.length > 0) {
							for (String userName : users) {
								UserEntity ue = datacache.getUser(userName);
								if (ue != null) {
									logger.info("Not Ping �豸���û�����" + userName);
								}
								sendMsgAndEmailLocal.sendMsgAndEmail(ue,
										warning.getIpValue(), warningEvent, 0,
										warningLevel,"");

							}
						} else {
							logger.error("��ʱPing���豸" + ipValue
									+ "û�����û������Բ�����Ż��ʼ�֪ͨ..");
						}
					}
					CacheDatas.getInstance().setWarninging(false);
				}
			}
		} catch (Exception e) {
			CacheDatas.getInstance().setWarninging(false);
			logger.error("���ܸ澯��", e);
		}
	}

	/**
	 * ͨ�������nodeID�õ����й�����豸��UserEntity
	 * @param nodeID
	 * @return
	 */
	private List<UserEntity> getUserByIp(Long nodeID){
		List<UserEntity> userEntities = null;
		try{
			if (nodeID != null){
				userEntities= (List<UserEntity>)nmsService.queryUserEntityByNode(nodeID);
			}
		}
		catch(Exception e){
			this.logger.error("",e);
		}
		return userEntities;
	}
	
	private String[] getUNsByIp(String ipValue, int deviceType) {
		String userNames = null;
		if (deviceType == Constants.DEV_SWITCHER2) {
			SwitchNodeEntity switcher = commService.getSwitchByIp(ipValue);
			if (switcher != null) {
				userNames = switcher.getBaseConfig().getUserNames();
			}
		} else if (deviceType == Constants.DEV_OLT) {
			OLTEntity olt = commService.getOLTByIpValue(ipValue);
			if (olt != null) {
				userNames = olt.getOltBaseInfo().getUserNames();
			}
		} else if (deviceType == Constants.DEV_ONU) {
			OLTEntity olt = commService.getOLTByIpValue(ipValue);
			if (olt != null) {
				userNames = olt.getOltBaseInfo().getUserNames();
			}
		} else if (deviceType == Constants.DEV_SWITCHER3) {

			SwitchLayer3 switchLayer3 = commService.getSwitcher3ByMac(ipValue);
			if (switchLayer3 != null) {
				if (switchLayer3.getManagerEntity() != null) {
					userNames = switchLayer3.getManagerEntity().getName();
				}
			}
			if (userNames != null) {
				return userNames.split(",");
			}
		}
		if (userNames != null) {
			return userNames.split(";");
		} else {
			return null;
		}
	}

	private NoteEntity copyNoteInfo(TrapWarningEntity entity) {
		NoteEntity warning = new NoteEntity();
		if (entity != null) {
			warning.setDescs(entity.getDescs());
			warning.setDeviceName(entity.getDeviceName());
			warning.setIpValue(entity.getIpValue());
			warning.setNoteType(entity.getWarningEvent());
			warning.setCreateDate(entity.getSampleTime());
			warning.setPortNo(entity.getPortNo());

			warning.setPortType(entity.getPortType());
			warning.setDeviceType(entity.getDeviceType());
			warning.setSlotNum(entity.getSlotNum());
			warning.setOnuSequenceNo(entity.getOnuSequenceNo());
			warning.setWarnOnuMac(entity.getWarnOnuMac());
		}
		return warning;
	}

	/**
	 * ȡ��TrapWarningEntity�е�ֵ,��ϳ�WarningEntity
	 * @param entity
	 * @param nodeId
	 * @return
	 */
	private WarningEntity copyWarningInfo(Object object,Long nodeId) {
		WarningEntity warning = new WarningEntity();
		
		if (object != null) {
			if (object instanceof TrapWarningEntity){
				TrapWarningEntity entity = (TrapWarningEntity) object;
																									
				//�澯����
				WarningEventDescription eventDescription = WarningEventDescription.getInstance();
				String description = eventDescription.getWarningEventDescription(entity.getWarningEvent());
				String descs = entity.getDescs();
				//System.out.println(descs);
				if(descs.length()>145){
					if("54:68:65:20:63:75:72:72:65:6e:74:20:74:65:6d:70:65:72:61:74:75:72:65:20:6f:66:20:73:77:69:74:63:68:20:69:73:20:68:69:67:68:65:72:20:74:68:61:6e:20".equals(descs.substring(0,146))){
						String str = toStrHex(descs);
						warning.setDescs(String.format(description, str));
					}
				}else{
					warning.setDescs(String.format(description, descs));
				}
				
				//�澯��Դ
				String warningSource = "";
				NodeEntity nodeEntity = null;
				if(nodeId != null){
					nodeEntity = commService.queryNodeEntity(nodeId);
					if (nodeEntity != null){
						if (nodeEntity.getName() == null || "".equals(nodeEntity.getName().trim())){
							warningSource = entity.getIpValue();
						}
						else{
							warningSource = nodeEntity.getName();
						}
					}
				}
				warning.setNodeName(warningSource);
				
				//����ID
				Long subnetId = null;
				//��������
				String subnetName = "";
				if (nodeEntity != null){
					String guid = nodeEntity.getParentNode();
					if (guid != null){
						NodeEntity subnetEntity = commService.querySubNetTopoNodeEntity(guid);
						if (subnetEntity != null){
							subnetName = subnetEntity.getName();
							subnetId = subnetEntity.getId();
						}
					}
					else{
						subnetName = nodeEntity.getTopDiagramEntity().getName();
					}
				}
				warning.setSubnetName(subnetName);
				warning.setSubnetId(subnetId);
				
				warning.setIpValue(entity.getIpValue());
				warning.setWarningEvent(entity.getWarningEvent());
				warning.setCreateDate(entity.getSampleTime());
				warning.setPortNo(entity.getPortNo());
				
				/**
				 * ���
				 */
				int warningCategory = WarningCategoryBean.getInstance().getWarningCategory(entity.getWarningEvent());
				warning.setWarningCategory(warningCategory);
				
				/**
				 * ����
				 */
				int warningLevel = WarningLevelBean.getInstance().getWarningLevel(entity.getWarningEvent());
				warning.setWarningLevel(warningLevel);
				
				warning.setCurrentStatus(Constants.UNCONFIRM);

				warning.setPortType(entity.getPortType());
				warning.setDeviceType(entity.getDeviceType());
				warning.setSlotNum(entity.getSlotNum());
				warning.setOnuSequenceNo(entity.getOnuSequenceNo());
				warning.setWarnOnuMac(entity.getWarnOnuMac());
			}
			else if (object instanceof SysLogWarningEntity){
				SysLogWarningEntity entity = (SysLogWarningEntity) object;

				String contents = "[%s][%s]%s";
				//�澯����
				warning.setDescs(String.format(contents, entity.getSyslogType(),entity.getManageUser(),entity.getContents()));
				
				//�澯��Դ
				String warningSource = "";
				NodeEntity nodeEntity = null;
				if(nodeId != null){
					nodeEntity = commService.queryNodeEntity(nodeId);
					if (nodeEntity != null){
						if (nodeEntity.getName() == null || "".equals(nodeEntity.getName().trim())){
							warningSource = entity.getIpValue();
						}
						else{
							warningSource = nodeEntity.getName();
						}
					}
				}
				warning.setNodeName(warningSource);
				
				//����ID
				Long subnetId = null;
				//��������
				String subnetName = "";
				if (nodeEntity != null){
					String guid = nodeEntity.getParentNode();
					if (guid != null){
						NodeEntity subnetEntity = commService.querySubNetTopoNodeEntity(guid);
						if (subnetEntity != null){
							subnetName = subnetEntity.getName();
							subnetId = subnetEntity.getId();
						}
					}
					else{
						subnetName = nodeEntity.getTopDiagramEntity().getName();
					}
				}
				warning.setSubnetName(subnetName);
				warning.setSubnetId(subnetId);
				
				warning.setIpValue(entity.getIpValue());
				warning.setWarningEvent(entity.getWarningEvent());
				warning.setCreateDate(entity.getCurrentTime());
				
				/**
				 * ���
				 */
				int warningCategory = WarningCategoryBean.getInstance().getWarningCategory(entity.getWarningEvent());
				warning.setWarningCategory(warningCategory);
				
				/**
				 * ����
				 */
				int warningLevel = WarningLevelBean.getInstance().getWarningLevel(entity.getWarningEvent());
				warning.setWarningLevel(warningLevel);
				
				warning.setCurrentStatus(Constants.UNCONFIRM);
			}
		}
		return warning;
	}
	/**
	 * 
	 * @param str
	 * @return
	 * 
	 * ������ʮ���ֽ�ת��Ϊ�ַ�����toStrHex(String str) �� toStringHex(String s)
	 */
	public String toStrHex(String str){
		String[] s = str.split(":");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length - 4; i++) {
			sb.append(s[i]);
		}
		String str1 = toStringHex(sb.toString()) + "\u2103 !";
		return str1;
	}
	public static String toStringHex(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(
						i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(baKeyword, "utf-8");// UTF-16le:Not
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}

	private WarningEntity copyWarningInfo(TrapWarningEntity entity) {
		WarningEntity warning = new WarningEntity();
		if (entity != null) {
			warning.setDescs(entity.getDescs());
			warning.setNodeName(entity.getDeviceName());
			warning.setIpValue(entity.getIpValue());
			warning.setWarningEvent(entity.getWarningEvent());
			warning.setCreateDate(entity.getSampleTime());
			warning.setPortNo(entity.getPortNo());
			
			/**
			 * ���
			 */
			int warningCategory = WarningCategoryBean.getInstance().getWarningCategory(entity.getWarningEvent());
			warning.setWarningCategory(warningCategory);
			
			/**
			 * ����
			 */
			int warningLevel = WarningLevelBean.getInstance().getWarningLevel(entity.getWarningEvent());
			warning.setWarningLevel(warningLevel);
			
			warning.setCurrentStatus(Constants.UNCONFIRM);

			warning.setPortType(entity.getPortType());
			warning.setDeviceType(entity.getDeviceType());
			warning.setSlotNum(entity.getSlotNum());
			warning.setOnuSequenceNo(entity.getOnuSequenceNo());
			warning.setWarnOnuMac(entity.getWarnOnuMac());
		}
		return warning;
	}
	
	/**
	 * ȫ������ʱɾ�����㽻�������������ò���,������������ʱ���½�����������״̬
	 * 
	 * @param datas
	 * @param entity
	 */
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private synchronized void configSwitchParm(Set keys,SwitchNodeEntity entity, Map datas) {
		try {

			if (keys != null && keys.size() != 0) {
				for (Iterator it = keys.iterator(); it.hasNext();) {
					Class clazz = (Class) it.next();
					if (clazz.getName()
							.equals(SwitchBaseConfig.class.getName())) {
					} else if (clazz.getName().equals(
							SwitchBaseInfo.class.getName())) {
					} else if (clazz.getName().equals(
							Priority802D1P.class.getName())) {
						Object[] parms = { entity };
						List<QOSPriority> list = (List<QOSPriority>) commService
								.findAll(QOSPriority.class,
										"where entity.switchNode=?", parms);
						List<Priority802D1P> priority802d1ps = (List<Priority802D1P>) datas
								.get(clazz);
						if (priority802d1ps.size() > 0) {
							for (Priority802D1P priority802d1p : priority802d1ps) {
								priority802d1p
										.setIssuedTag(Constants.ISSUEDDEVICE);
							}

							if (list.size() > 0) {
								for (QOSPriority qosPriority : list) {
									qosPriority.getPriorityEOTs().removeAll(
											qosPriority.getPriorityEOTs());
									qosPriority
											.setPriorityEOTs(priority802d1ps);
									commService.updateEntity(qosPriority);
								}
							} else {

								QOSPriority qosPriority = new QOSPriority();
								qosPriority.setSwitchNode(entity);
								qosPriority.setPriorityEOTs(priority802d1ps);
								commService.saveEntity(qosPriority);

							}
						}

					} else if (clazz.getName().equals(
							PriorityDSCP.class.getName())) {
						Object[] parms = { entity };
						List<QOSPriority> list = (List<QOSPriority>) commService
								.findAll(QOSPriority.class,
										"where entity.switchNode=?", parms);
						List<PriorityDSCP> priorityDSCPs = (List<PriorityDSCP>) datas
								.get(clazz);
						if (priorityDSCPs != null && priorityDSCPs.size() > 0) {
							for (PriorityDSCP priorityDSCP : priorityDSCPs) {
								priorityDSCP
										.setIssuedTag(Constants.ISSUEDDEVICE);
							}

							if (list != null && list.size() > 0) {
								for (QOSPriority qosPriority : list) {
									if (qosPriority.getPriorityDSCPs() != null
											&& qosPriority.getPriorityDSCPs()
													.size() > 0) {
										commService.deleteEntities(qosPriority
												.getPriorityDSCPs());
									}
									qosPriority.getPriorityDSCPs().removeAll(
											qosPriority.getPriorityDSCPs());
									qosPriority.setPriorityDSCPs(priorityDSCPs);
									commService.updateEntity(qosPriority);
								}
							} else {

								QOSPriority qosPriority = new QOSPriority();
								qosPriority.setSwitchNode(entity);
								qosPriority.setPriorityDSCPs(priorityDSCPs);
								commService.saveEntity(qosPriority);

							}
						}
					} else if (clazz.getName().equals(
							PriorityTOS.class.getName())) {
						Object[] parms = { entity };
						List<QOSPriority> list = (List<QOSPriority>) commService
								.findAll(QOSPriority.class,
										"where entity.switchNode=?", parms);
						List<PriorityTOS> priorityTOSs = (List<PriorityTOS>) datas
								.get(clazz);
						if (priorityTOSs != null && priorityTOSs.size() > 0) {
							for (PriorityTOS priorityTOS : priorityTOSs) {
								priorityTOS
										.setIssuedTag(Constants.ISSUEDDEVICE);
							}

							if (list != null && list.size() > 0) {
								for (QOSPriority qosPriority : list) {
									qosPriority.getPriorityTOSs().removeAll(
											qosPriority.getPriorityTOSs());
									qosPriority.setPriorityTOSs(priorityTOSs);
									commService.updateEntity(qosPriority);
								}
							} else {
								QOSPriority qosPriority = new QOSPriority();
								qosPriority.setSwitchNode(entity);
								qosPriority.setPriorityTOSs(priorityTOSs);
								commService.saveEntity(qosPriority);

							}
						}

					} else if (clazz.getName().equals(VlanEntity.class.getName())) {
						entity =commService.findById(entity.getId(), SwitchNodeEntity.class);
						Object[] parms = { entity };
						List<VlanConfig> list = (List<VlanConfig>) commService.findAll(VlanConfig.class,"where entity.switchNode=?", parms);
						List<VlanEntity> vlanEntities = (List<VlanEntity>) datas.get(clazz);
						Set<VlanEntity> vlanSets = new HashSet<VlanEntity>();
						vlanSets.addAll(vlanEntities);
						if (list != null && list.size() > 0) {
							VlanConfig vlanConfig = list.get(0);
							this.deleteVlan(vlanConfig, entity);
							
							Thread.sleep(500);
							// �������ص�Vlan���浽���ݿ���
							this.saveVlan(vlanSets, vlanConfig, entity);

						} else {
							// ͨ��Vlan���������뽻�����Ĺ�ϵ
							if (vlanSets.size() > 0) {

								VlanConfig config = new VlanConfig();
								config.setSwitchNode(entity);
								for (VlanEntity vlanEntity : vlanSets) {
									vlanEntity.setVlanName(vlanEntity.getVlanID()+ "");
									Set<VlanPortConfig> portConfigs = vlanEntity.getPortConfig();
									if (portConfigs != null&& portConfigs.size() > 0) {
										for (VlanPortConfig vlanPortConfig : portConfigs) {
											vlanPortConfig.setIssuedTag(Constants.ISSUEDDEVICE);
										}
									}
									vlanEntity.setIssuedTag(Constants.ISSUEDDEVICE);
									vlanEntity.setVlanConfig(config);
								}
								config.setVlanEntitys(vlanSets);
								commService.saveEntity(config);
							}

						}

					} else if (clazz.getName().equals(VlanPort.class.getName())) {
						Object[] parms = { entity };
						List<VlanConfig> list = (List<VlanConfig>) commService.findAll(VlanConfig.class,"where entity.switchNode=?", parms);
						List<VlanPort> portEntities = (List<VlanPort>) datas.get(clazz);
						Set<VlanPort> portSets = new HashSet<VlanPort>();
						if (portEntities.size() > 0) {
							if (list.size() > 0) {
								VlanConfig vlanConfig = list.get(0);
								for (VlanPort vlanPort : portEntities) {
									vlanPort.setIssuedTag(Constants.ISSUEDDEVICE);
									vlanPort.setVlanConfig(vlanConfig);
									portSets.add(vlanPort);
								}
								Set<VlanPort> vlanPorts = vlanConfig.getVlanPorts();
								Set<VlanPort> delVlanPorts = new HashSet<VlanPort>();
								if (vlanPorts != null && vlanPorts.size() > 0) {
									for (VlanPort port : vlanPorts) {
										port.setVlanConfig(null);
										commService.updateEntity(port);
										delVlanPorts.add(port);
									}
									commService.deleteEntities(delVlanPorts);
									vlanConfig.getVlanPorts().removeAll(vlanConfig.getVlanPorts());
								}
								vlanConfig.setVlanPorts(portSets);
								commService.updateEntity(vlanConfig);

							} else {
								VlanConfig vlanConfig = new VlanConfig();
								vlanConfig.setSwitchNode(entity);
								for (VlanPort vlanPort : portEntities) {
									vlanPort.setIssuedTag(Constants.ISSUEDDEVICE);
									vlanPort.setVlanConfig(vlanConfig);
									portSets.add(vlanPort);
								}
								vlanConfig.setVlanPorts(portSets);
								commService.saveEntity(vlanConfig);
							}
						}

					} else if (clazz.getName().equals(IGMPEntity.class.getName())) {
						Object[] parms = { entity };
						List<IGMPEntity> igmpEntities = (List<IGMPEntity>) commService.findAll(IGMPEntity.class,
										"where entity.switchNode=?", parms);
						List<IGMPEntity> entities = (List<IGMPEntity>) datas.get(clazz);
						if (entities != null && entities.size() > 0) {
							if (igmpEntities != null && igmpEntities.size() > 0) {
								IGMPEntity igmpEntity = entities.get(0);
								if (igmpEntity.getVlanIds() != null) {
									nmsService.deleteEntityBySwitch(clazz,entity);
								} else {
									igmpEntities.get(0).setApplied(igmpEntity.isApplied());
									igmpEntities.get(0).setPorts(igmpEntity.getPorts());
									igmpEntities.get(0).setIssuedTag(Constants.ISSUEDDEVICE);
									commService.updateEntity(igmpEntities.get(0));
								}

							} else {
								IGMPEntity dataEntity = entities.get(0);
								dataEntity.setIssuedTag(Constants.ISSUEDDEVICE);
								dataEntity.setSwitchNode(entity);
								commService.saveEntity(dataEntity);
							}
						}
					} else if (clazz.getName().equals(Igmp_vsi.class.getName())) {
						List<Igmp_vsi> igmpVsis = (List<Igmp_vsi>) datas
								.get(clazz);
						if (igmpVsis != null && igmpVsis.size() > 0) {
							for (Igmp_vsi igmpVsi : igmpVsis) {
								igmpVsi.setIssuedTag(Constants.ISSUEDDEVICE);
							}
							Object[] parms = { entity };
							List<IGMPEntity> igmpEntities = (List<IGMPEntity>) commService.findAll(IGMPEntity.class,"where entity.switchNode=?", parms);
							IGMPEntity igmpEntity = null;
							if (igmpEntities != null && igmpEntities.size() > 0) {
								igmpEntity = igmpEntities.get(0);
								igmpEntity.getVlanIds().removeAll(
										igmpEntity.getVlanIds());
								igmpEntity.setVlanIds(igmpVsis);
								commService.updateEntity(igmpEntity);
							} else {
								igmpEntity = new IGMPEntity();
								igmpEntity.setSwitchNode(entity);
								igmpEntity.setVlanIds(igmpVsis);
								commService.saveEntity(igmpEntity);

							}
						}

					} else if (clazz.getName().equals(RingConfig.class.getName())) {
						entity =commService.findById(entity.getId(), SwitchNodeEntity.class);
						Object[] parms = { entity };
						List<RingConfig> list = (List<RingConfig>) commService.findAll(RingConfig.class,"where entity.switchNode=?", parms);
						List<RingConfig> ringConfigs = (List<RingConfig>) datas.get(clazz);
						List<RingConfig> delRing = new ArrayList<RingConfig>();
						if (list != null && list.size() > 0) {
							for (RingConfig ringConfig : list) {
								
							RingConfig nullRing = new RingConfig();
							nullRing.setRingEnable(ringConfig.isRingEnable());
							nullRing.setRingID(ringConfig.getRingID());
							nullRing.setRingType(ringConfig.getRingType());
							nullRing.setSystemType(nullRing.getSystemType());
							if(nullRing.getId()==null){
							commService.saveEntity(nullRing);
							}else{
								logger.info("���ر���RingʱID��Ϊ��");
							}
							}

							commService.deleteEntities(list);
						}
						
						Thread.sleep(500);
						if (ringConfigs != null) {
							for (RingConfig ringConfig : ringConfigs) {
								ringConfig.setSwitchNode(entity);
								ringConfig.setIssuedTag(Constants.ISSUEDDEVICE);
								Object[] objs = { ringConfig.getRingID() };
								List<RingConfig> entities = (List<RingConfig>) commService.findAll(RingConfig.class,
												"where entity.ringID=? and entity.switchNode is null",objs);
								if (entities != null && entities.size() > 0) {
									delRing.clear();

									for (RingConfig delconfig : entities) {
										if (delconfig.getId() != null) {
											delRing.add(delconfig);
										}
									}
									commService.deleteEntities(delRing);
								}
								commService.saveEntity(ringConfig);
							}
						}
					} else if (clazz.getName().equals(SNMPHost.class.getName())) {
						
						entity =commService.findById(entity.getId(), SwitchNodeEntity.class);
						Object[] parms = { entity };
						List<SNMPHost> list = (List<SNMPHost>) commService.findAll(SNMPHost.class,"where entity.switchNode=?", parms);
						List<SNMPHost> snmpConfigs = (List<SNMPHost>) datas.get(clazz);
						List<SNMPHost> delHosts = new ArrayList<SNMPHost>();
						if (list != null && list.size() > 0) {

							for (SNMPHost snmpHost : list) {
								SNMPHost nullSNMPHost = new SNMPHost();
								nullSNMPHost.setHostIp(snmpHost.getHostIp());
								nullSNMPHost.setSnmpVersion(snmpHost.getSnmpVersion());
								nullSNMPHost.setMassName(snmpHost.getMassName());
								if(nullSNMPHost.getId()==null){
								commService.saveEntity(nullSNMPHost);
								}
							}
							commService.deleteEntities(list);

						}
						Thread.sleep(500);
						if (snmpConfigs != null) {
							for (SNMPHost host : snmpConfigs) {
								host.setSwitchNode(entity);
								host.setIssuedTag(Constants.ISSUEDDEVICE);
								Object[] objs = { host.getHostIp(),host.getSnmpVersion(),host.getMassName() };
								List<SNMPHost> entities = (List<SNMPHost>) commService.findAll(SNMPHost.class,"where entity.hostIp=? and entity.snmpVersion=? and entity.massName=? and entity.switchNode is null",objs);
								if (entities != null && entities.size() > 0) {
									delHosts.clear();
									for (SNMPHost delHost : entities) {
										if (delHost.getId() != null) {
											delHosts.add(delHost);
										} else {
											logger.info("Ҫɾ����SNMPHostΪ����״̬");
										}
									}
									commService.deleteEntities(delHosts);
								}
								if(host.getId()==null){
									commService.saveEntity(host);
								}else{
									logger.info("���ر���HostIPʱID��Ϊ��");
								}
							}
						}
					} 
					else if (clazz.getName().equals(SysLogHostToDevEntity.class.getName())) {
						//�����������
					}
					else {
						nmsService.deleteEntityBySwitch(clazz, entity);
					}
					commService.commit();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void deleteVlan(VlanConfig vlanConfig, SwitchNodeEntity entity) {

		Set<VlanEntity> setEntities = vlanConfig.getVlanEntitys();
		Set<VlanEntity> delEntities = new HashSet<VlanEntity>();
		// ��ɾ�����ݿ�ԭ�ȴ��ڵ�Vlan
		if (setEntities != null) {
			for (VlanEntity vlanEntity : setEntities) {

				vlanEntity.setVlanConfig(null);
				delEntities.add(vlanEntity);
				
				// ��һ�����豸û������Vlan
				VlanEntity nullVlan = new VlanEntity();
				nullVlan.setVlanID(vlanEntity.getVlanID());
				nullVlan.setVlanName(vlanEntity.getVlanName());
				if(nullVlan.getId()==null){
				commService.saveEntity(nullVlan);
				}
				// ɾ���͸�vlan ��ص�Igmp_vsi
				this.deleteIgmp_vsi(entity, vlanEntity);

			}
			if (delEntities.size() > 0) {
				vlanConfig.getVlanEntitys().removeAll(delEntities);
				commService.deleteEntities(delEntities);
			}

		}
	}

	public synchronized void deleteIgmp_vsi(SwitchNodeEntity entity,
			VlanEntity vlanEntity) {

		List<IGMPEntity> igmpEntities = (List<IGMPEntity>) nmsService.queryEntityBySwitch(IGMPEntity.class, entity);
		if (igmpEntities != null && igmpEntities.size() > 0) {
			List<Igmp_vsi> igmpVsis = igmpEntities.get(0).getVlanIds();
			List<Igmp_vsi> delIgmpVsis = new ArrayList<Igmp_vsi>();
			if (igmpVsis != null) {
				for (Igmp_vsi igmpVsi : igmpVsis) {
					if (igmpVsi.getVlanId().equals(vlanEntity.getVlanID())) {
						delIgmpVsis.add(igmpVsi);
					}
				}
				igmpEntities.get(0).getVlanIds().removeAll(delIgmpVsis);
				commService.deleteEntities(delIgmpVsis);
			}
		}

	}

	public synchronized void saveVlan(Set<VlanEntity> vlanSets,
			VlanConfig vlanConfig, SwitchNodeEntity entity) {

		if (vlanSets.size() > 0) {

			List<VlanEntity> delNnull = new ArrayList<VlanEntity>();
			for (VlanEntity vlanEntity : vlanSets) {
				vlanEntity.setVlanName(vlanEntity.getVlanID() + "");
				Set<VlanPortConfig> portConfigs = vlanEntity.getPortConfig();
				if (portConfigs != null && portConfigs.size() > 0) {
					for (VlanPortConfig vlanPortConfig : portConfigs) {
						
						 vlanPortConfig.setIssuedTag(Constants.ISSUEDDEVICE);
					}
				}
				vlanEntity.setIssuedTag(Constants.ISSUEDDEVICE);
				vlanEntity.setVlanConfig(vlanConfig);
				if(vlanEntity.getId()==null){
				commService.saveEntity(vlanEntity);
				}else{
					logger.info("���ر���VlanʱID��Ϊ��");
				}
				vlanConfig.getVlanEntitys().add(vlanEntity);
				delNnull.add(vlanEntity);

				this.saveIGMPByVlanID(entity, vlanEntity.getVlanID() + "");
			}
			commService.updateEntity(vlanConfig);

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			deleteVlan(delNnull);

		}

	}

	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public synchronized void deleteVlan(List<VlanEntity> list) {

		for (VlanEntity vlanEntity : list) {
			Object[] parms = { vlanEntity.getVlanID() };
			List<VlanEntity> entities = (List<VlanEntity>) commService.findAll(
					VlanEntity.class,
					"where entity.vlanID=? and entity.vlanConfig is null",
					parms);
			if (entities != null && entities.size() > 0) {
				commService.deleteEntities(entities);
			}
		}

	}

	/**
	 * ɾ��ĳ���㽻�������������ò���
	 * 
	 * @param datas
	 * @param entity
	 */
	@SuppressWarnings("unchecked")
	private void deleteSwitch3(Set datas, SwitchLayer3 switchLayer3) {
		if (datas != null && datas.size() != 0) {
			for (Iterator it = datas.iterator(); it.hasNext();) {
				Class clazz = (Class) it.next();
				if (clazz.getName().equals(Switch3VlanEnity.class.getName())) {
				} else if (clazz.getName().equals(
						Switch3VlanPortEntity.class.getName())) {
				} else {
					nmsService.deleteEntityBySwitch3(clazz, switchLayer3);
				}
			}
		}
	}

	private void deleteSwitch3Vlan(SwitchLayer3 switchLayer3) {
		List<Switch3VlanEnity> vlanEnities = nmsService
				.querySwitch3VlanEnity(switchLayer3.getIpValue());
		if (vlanEnities != null) {
			commService.deleteEntities(vlanEnities);
		}

	}
	
	private void deleteSwitch3Port(SwitchLayer3 switchLayer3) {
		List<SwitchPortLevel3> portEnities = nmsService
			.querySwitch3PortEnity(switchLayer3.getIpValue());
		if (portEnities != null) {
			commService.deleteEntities(portEnities);
		}
	}

	/**
	 * ɾ��ĳһolt�Ĳ���
	 * 
	 * @param datas
	 * @param entity
	 */
	@SuppressWarnings("unchecked")
	private void deleteOLT(Set datas, OLTEntity entity) {
		if (datas != null && datas.size() != 0) {
			for (Iterator it = datas.iterator(); it.hasNext();) {
				Class clazz = (Class) it.next();
				if (!clazz.getName().equals(ONULLID.class.getName())){
					//begin  modifier:wuzhongwei
					if (clazz.getName().equals(OLTPort.class.getName())){
						entity.setPorts(null);
						commService.updateEntity(entity);
					}	
					//end
					nmsService.deleteEntityByOLT(clazz, entity);
				}	
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void warningBlockLink(LinkEntity linkEntity, int mt,
			int warningEvent, String ipValue, int deviceType) {

		Object[] parms = { linkEntity.getRingID() };
		logger.info("����ţ�" + linkEntity.getRingID() + "�澯��ID��"
				+ linkEntity.getId());
		List<RingEntity> ringEntities = (List<RingEntity>) commService.findAll(
				RingEntity.class, " where entity.ringNo=?", parms);
		if (ringEntities != null) {
			for (RingEntity ringEntity : ringEntities) {
				String blockIP = ringEntity.getIp_Value();
				int blockport = ringEntity.getPortNo();
				logger.info("������ IP��" + blockIP + "�����˿ڣ�" + blockport + "����ţ�"
						+ ringEntity.getRingNo());
				LinkEntity entity = commService.findLinkEntity(blockIP,
						blockport, ringEntity.getRingNo());
				if (entity != null) {
					logger.info("��������״̬��" + entity.getStatus() + "");
					logger.info("����LLDP ��" + lldp2String(entity.getLldp()));
					if (warningEvent == Constants.LINKDOWN) {
						if (entity.getStatus() != Constants.L_UNCONNECT) {
							entity.setStatus(Constants.L_CONNECT);
							commService.updateEntity(entity);
							try {
								smtcService.sendWarningMessage(mt, warningEvent,
										ipValue, "", deviceType, entity);
								logger.info("�������߱�Ϊ���ӷ�����Ϣ�ɹ�");
							} catch (JMSException e) {
								logger.error("��������", e);
							}
						}
					} else {

						entity.setStatus(Constants.L_BLOCK);
						commService.updateEntity(entity);
						try {
							smtcService.sendWarningMessage(mt, warningEvent,
									ipValue, "", deviceType, entity);
							logger.info("������������Ϣ�ɹ�");
						} catch (JMSException e) {
							logger.error("��������", e);
						}

					}

				} else {

					logger.warn("δ�ҵ�������������");
				}
			}
		} else {
			logger.warn("δ�ҵ���");
		}
	}

	private void warningLink(LinkEntity linkEntity, int mt, int warningEvent,
			String ipValue, int deviceType) {

		try {
			smtcService.sendWarningMessage(mt, warningEvent, ipValue, "",
					deviceType, linkEntity);
			logger.info("�߸澯������Ϣ�ɹ�");
		} catch (JMSException e) {
			logger.error("��LinkDown or LinkUP��", e);
		}

	}

	private void warningSubNet(LinkEntity linkEntity, int mt, int warningEvent,
			String ipValue, int deviceType) {
		try {
			if (null != linkEntity){
				NodeEntity node1 = linkEntity.getNode1();
				NodeEntity node2 = linkEntity.getNode2();

				if (node1 != null) {
					this.warningSubNet(node1, mt, warningEvent, ipValue, deviceType);
				}
				if (node2 != null) {
					this.warningSubNet(node2, mt, warningEvent, ipValue, deviceType);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	private void warningSubNet(NodeEntity nodeEntity, int mt, int warningEvent,
			String ipValue, int deviceType) {		
		try {
			if (nodeEntity.getParentNode() != null && !nodeEntity.getParentNode().equals("")) {
				Object[] parms = { nodeEntity.getParentNode() };
				String where = " where entity.guid=?";
				List<SubNetTopoNodeEntity> listTopoNodeEntities = (List<SubNetTopoNodeEntity>) commService
							.findAll(SubNetTopoNodeEntity.class, where, parms);
				SubNetTopoNodeEntity netTopoNodeEntity = null;
				if (listTopoNodeEntities != null && listTopoNodeEntities.size() > 0) {
					netTopoNodeEntity = listTopoNodeEntities.get(0);
					if (warningEvent == Constants.LINKUP) {
						if (iteratorNode(netTopoNodeEntity, netTopoNodeEntity
									.getTopDiagramEntity().getId())) {
							netTopoNodeEntity.setStatus(Constants.HASWARNING);
						} else {
							netTopoNodeEntity.setStatus(Constants.NORMAL);
						}
					} 
					else if(warningEvent == Constants.PINGIN){
						netTopoNodeEntity.setStatus(Constants.NORMAL);
					}
					else if (warningEvent == Constants.PINGOUT){
						netTopoNodeEntity.setStatus(Constants.HASWARNING);
					}
					else {
						netTopoNodeEntity.setStatus(Constants.HASWARNING);
					}
					commService.updateEntity(netTopoNodeEntity);
					
					//�ݹ���ã��ҳ��豸���в������������������״̬������
					warningSubNet(netTopoNodeEntity,mt,warningEvent,ipValue,deviceType);
				} 
				
				if(netTopoNodeEntity != null){
					smtcService.sendWarningMessage(mt, warningEvent, ipValue,
							"", deviceType, netTopoNodeEntity);
					logger.info("��������:" + netTopoNodeEntity.getName() + "����״̬��"
							+ netTopoNodeEntity.getStatus());
					logger.info("�����澯�����ɹ�");
				}
//				else (netTopoNodeEntity == null){
//						logger.warn("δ�ҵ����� GuiD:" + nodeEntity.getParentNode());
//				}
			} 
			else {
				logger.info("�ڵ�ID:" + nodeEntity.getId() + "����������");
			}
		} catch (JMSException e) {
			logger.error("����error:", e);
		}
	}

	public boolean iteratorNode(NodeEntity nodeEntity, long id) {
		boolean flag = false;
		try {
			if (nodeEntity instanceof SubNetTopoNodeEntity) {
				SubNetTopoNodeEntity topoNodeEntity = (SubNetTopoNodeEntity) nodeEntity;
				List<NodeEntity> childNode = commService.queryNodeEntity(id,
						topoNodeEntity.getGuid());
				if (childNode != null && childNode.size() > 0) {
					b: for (NodeEntity node : childNode) {
						if (node.getStatus() == Constants.HASWARNING) {
							flag = true;
						} else {
							List<LinkEntity> list = nmsService
									.queryLinkEntity(node);
							if (list != null && list.size() > 0) {
								a: for (LinkEntity linkEntity : list) {
									if (linkEntity.getStatus() == Constants.L_UNCONNECT) {
										flag = true;
										break a;
									}
								}
							} else {
								flag = this.iteratorNode(node, id);
							}
						}
						if (flag) {
							break b;
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	private void warningNode(Object object, int mt, int warningEvent,
			String ipValue, int deviceType) {
		try {
			if (object instanceof NodeEntity){
				NodeEntity nodeEntity = (NodeEntity)object;
				if (nodeEntity != null){
					if (warningEvent == Constants.LINKDOWN 
							|| warningEvent == Constants.PINGOUT ) {
						int status = nodeEntity.getStatus(); //�豸״̬
						if (status == Constants.HASWARNING){
							smtcService.sendWarningMessage(mt, warningEvent,
									ipValue, "", deviceType, nodeEntity);
							logger.info("LinkDown");
							logger.info(ipValue + " �ڵ�澯��Ϣ���ͳɹ�");
						}
					}
					else if (warningEvent == Constants.LINKUP 
							|| warningEvent == Constants.PINGIN){
						int status = nodeEntity.getStatus(); //�豸״̬
						if (status == Constants.NORMAL){
							smtcService.sendWarningMessage(mt, warningEvent, ipValue,
									"", deviceType, nodeEntity);
							logger.info("LinkUP");
							logger.info(ipValue + " �ڵ�澯��Ϣ���ͳɹ�");
						}
					}
				}
			}
			else if (object instanceof LinkEntity){
				LinkEntity linkEntity = (LinkEntity)object;
				NodeEntity node1 = linkEntity.getNode1();
				NodeEntity node2 = linkEntity.getNode2();
				boolean flag1 = false;
				boolean flag2 = false;
				if (warningEvent == Constants.LINKDOWN) {
					List<LinkEntity> linkEntities1 = nmsService
							.queryLinkEntity(node1);
					if (linkEntities1 != null) {
						for (LinkEntity entity : linkEntities1) {
							logger.info("��ID:" + linkEntity.getId()
									+ "����node1�ڵ�IDΪ" + node1.getId() + "������״̬:"
									+ entity.getStatus());
							if (entity.getId() == linkEntity.getId()) {
								continue;
							} else if (entity.getStatus() == Constants.L_CONNECT) {
								flag1 = true;
								break;
							}
						}
					}
					List<LinkEntity> linkEntities2 = nmsService
							.queryLinkEntity(node2);
					if (linkEntities2 != null) {
						for (LinkEntity entity : linkEntities2) {
							logger.info("��ID:" + linkEntity.getId()
									+ "����node2�ڵ�IDΪ" + node2.getId() + "������״̬:"
									+ entity.getStatus());
							if (entity.getId() == linkEntity.getId()) {
								continue;
							} else if (entity.getStatus() == Constants.L_CONNECT) {
								flag2 = true;
								break;
							}
						}
					}
					if (!flag1) {
						if (node1 != null) {
							node1.setStatus(Constants.HASWARNING);
							commService.updateEntity(node1);
							smtcService.sendWarningMessage(mt, warningEvent,
									ipValue, "", deviceType, node1);
							logger.info("LinkDown");
							logger.info("�ڵ�澯��Ϣ���ͳɹ�");
						}
					}
					if (!flag2) {
						if (node2 != null) {
							node2.setStatus(Constants.HASWARNING);
							commService.updateEntity(node2);
							smtcService.sendWarningMessage(mt, warningEvent,
									ipValue, "", deviceType, node2);
							logger.info("LinkDown");
							logger.info("�ڵ�澯��Ϣ���ͳɹ�");
						}
					}

				} else if (warningEvent == Constants.LINKUP) {

					if (node1 != null) {
						node1.setStatus(Constants.NORMAL);
						commService.updateEntity(node1);
						smtcService.sendWarningMessage(mt, warningEvent, ipValue,
								"", deviceType, node1);
						logger.info("LinkUP");
						logger.info("�ڵ�澯��Ϣ���ͳɹ�");
					}
					if (node2 != null) {
						node2.setStatus(Constants.NORMAL);
						commService.updateEntity(node2);
						smtcService.sendWarningMessage(mt, warningEvent, ipValue,
								"", deviceType, node2);
						logger.info("LinkUP");
						logger.info("�ڵ�澯��Ϣ���ͳɹ�");
					}
				}
			}
			

		} catch (JMSException e) {
			e.printStackTrace();
		}

	}

	public static String lldp2String(LLDPInofEntity lldp) {
		String text = "LLDP is null";

		if (lldp != null) {
			text = String.format("[%s][%s:%s.%s.%s] -> [%s:%s.%s.%s]", lldp
					.getId(), lldp.getLocalIP(), lldp.getLocalPortNo(), lldp
					.getLocalPortType(), lldp.getLocalSlot(), lldp
					.getRemoteIP(), lldp.getRemotePortNo(), lldp
					.getRemotePortType(), lldp.getRemoteSlot());
		}
		return text;
	}

	public void saveIGMPByVlanID(SwitchNodeEntity nodeEntity, String vlanID) {
		boolean flag = false;
		List<IGMPEntity> igmpEntities = (List<IGMPEntity>) nmsService
				.queryEntityBySwitch(IGMPEntity.class, nodeEntity);
		if (igmpEntities != null && igmpEntities.size() > 0) {
			IGMPEntity igmpEntity = igmpEntities.get(0);
			List<Igmp_vsi> igmpVsis = igmpEntity.getVlanIds();
			if (igmpVsis != null) {
				for (Igmp_vsi igmpVsi : igmpVsis) {
					if (igmpVsi.getVlanId().equals(vlanID)) {
						flag = true;
						break;
					}
				}
				if (!flag) {
					Igmp_vsi igmpVsi = new Igmp_vsi();
					igmpVsi.setQuerier(false);
					igmpVsi.setSyschorized(true);
					igmpVsi.setSnooping(true);
					igmpVsi.setVlanId(vlanID);
					igmpVsi.setIssuedTag(Constants.ISSUEDDEVICE);
					commService.saveEntity(igmpVsi);
					igmpEntity.getVlanIds().add(igmpVsi);
					commService.updateEntity(igmpEntity);
				}
			} else {
				List<Igmp_vsi> newIgmpVsis = new ArrayList<Igmp_vsi>();
				Igmp_vsi igmpVsi = new Igmp_vsi();
				igmpVsi.setQuerier(false);
				igmpVsi.setSyschorized(true);
				igmpVsi.setSnooping(true);
				igmpVsi.setVlanId(vlanID);
				igmpVsi.setIssuedTag(Constants.ISSUEDDEVICE);
				commService.saveEntity(igmpVsi);
				newIgmpVsis.add(igmpVsi);
				igmpEntity.setVlanIds(newIgmpVsis);
				commService.updateEntity(igmpEntity);

			}
		} else {
			IGMPEntity igmpEntity = new IGMPEntity();
			igmpEntity.setSwitchNode(nodeEntity);
			igmpEntity.setSyschorized(true);
			igmpEntity.setApplied(true);
			List<Igmp_vsi> vsis = new ArrayList<Igmp_vsi>();
			Igmp_vsi igmpVsi = new Igmp_vsi();
			igmpVsi.setQuerier(false);
			igmpVsi.setSyschorized(true);
			igmpVsi.setSnooping(true);
			igmpVsi.setVlanId(vlanID);
			igmpVsi.setIssuedTag(Constants.ISSUEDDEVICE);
			vsis.add(igmpVsi);
			commService.updateEntity(igmpEntity);

		}

	}

	public int getTimeOut() throws IOException {

		String path = System.getProperty("user.dir");
		File file = new File(path + "/timeout.properties");
		if (file.exists()) {
			InputStream in = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(in);
			String timeOutStr = properties.getProperty("timeout");
			if (timeOutStr != null && !timeOutStr.equals("")) {
				return Integer.parseInt(timeOutStr);
			}
			in.close();
		}
		return 0;
	}
}
