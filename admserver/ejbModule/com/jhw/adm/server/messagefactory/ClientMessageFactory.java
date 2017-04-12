package com.jhw.adm.server.messagefactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.StreamMessage;
import javax.jms.TextMessage;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.carriers.CarrierRouteEntity;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.message.SynchFEP;
import com.jhw.adm.server.entity.message.TopoFoundFEPs;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.nets.VlanConfig;
import com.jhw.adm.server.entity.nets.VlanEntity;
import com.jhw.adm.server.entity.nets.VlanPort;
import com.jhw.adm.server.entity.nets.VlanPortConfig;
import com.jhw.adm.server.entity.ports.LACPConfig;
import com.jhw.adm.server.entity.ports.LLDPConfig;
import com.jhw.adm.server.entity.ports.QOSPriority;
import com.jhw.adm.server.entity.ports.QOSSpeedConfig;
import com.jhw.adm.server.entity.ports.QOSStormControl;
import com.jhw.adm.server.entity.ports.QOSSysConfig;
import com.jhw.adm.server.entity.ports.RingConfig;
import com.jhw.adm.server.entity.ports.RingPortInfo;
import com.jhw.adm.server.entity.ports.SwitchPortEntity;
import com.jhw.adm.server.entity.switchs.IGMPEntity;
import com.jhw.adm.server.entity.switchs.Igmp_vsi;
import com.jhw.adm.server.entity.switchs.LinkBakConfig;
import com.jhw.adm.server.entity.switchs.MirrorEntity;
import com.jhw.adm.server.entity.switchs.Priority802D1P;
import com.jhw.adm.server.entity.switchs.PriorityDSCP;
import com.jhw.adm.server.entity.switchs.PriorityTOS;
import com.jhw.adm.server.entity.switchs.SNMPGroup;
import com.jhw.adm.server.entity.switchs.SNMPHost;
import com.jhw.adm.server.entity.switchs.SNMPMass;
import com.jhw.adm.server.entity.switchs.SNMPUser;
import com.jhw.adm.server.entity.switchs.SNMPView;
import com.jhw.adm.server.entity.switchs.SNTPConfigEntity;
import com.jhw.adm.server.entity.switchs.STPPortConfig;
import com.jhw.adm.server.entity.switchs.STPSysConfig;
import com.jhw.adm.server.entity.switchs.SwitchBaseConfig;
import com.jhw.adm.server.entity.switchs.SwitchBaseInfo;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.switchs.SwitchUser;
import com.jhw.adm.server.entity.switchs.SysLogHostEntity;
import com.jhw.adm.server.entity.switchs.SysLogHostToDevEntity;
import com.jhw.adm.server.entity.switchs.TrunkConfig;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MACMutiCast;
import com.jhw.adm.server.entity.util.MACUniCast;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.SwitchSerialPort;
import com.jhw.adm.server.entity.warning.PortRemon;
import com.jhw.adm.server.entity.warning.RTMonitorConfig;
import com.jhw.adm.server.entity.warning.RmonWarningConfig;
import com.jhw.adm.server.interfaces.ClientMessageFactoryLocal;
import com.jhw.adm.server.interfaces.CommonServiceBeanLocal;
import com.jhw.adm.server.interfaces.DataCacheLocal;
import com.jhw.adm.server.interfaces.NMSServiceLocal;
import com.jhw.adm.server.interfaces.SMTCServiceLocal;
import com.jhw.adm.server.interfaces.SMTFEPServiceLocal;
import com.jhw.adm.server.util.CacheDatas;

/**
 * ���ڴ���ӿͻ��˷��͵��������˵�������Ϣ����Ϣ����
 * 
 * @author ����
 */
@Stateless
public class ClientMessageFactory implements ClientMessageFactoryLocal {
	private Logger logger = Logger.getLogger(ClientMessageFactory.class
			.getName());
	@EJB
	private CommonServiceBeanLocal commService;
	@EJB
	private NMSServiceLocal nmsService;
	@EJB
	private SMTFEPServiceLocal smtfepService;
	@EJB
	private SMTCServiceLocal smtcService;
	@EJB
	private DataCacheLocal datacache;

	@SuppressWarnings("unchecked")
	public void DealWithMessage(Message message) throws JMSException {

		if (message instanceof ObjectMessage) {
			ObjectMessage om = (ObjectMessage) message;
			try {
				int mt = message.getIntProperty(Constants.MESSAGETYPE);
				logger.info("���ܵ�Client���ظ��������˵���Ϣ��!    :" + mt);
				String from = message.getStringProperty(Constants.MESSAGEFROM);
				String clientIp = message.getStringProperty(Constants.CLIENTIP);
				Object mb = om.getObject();

				// �ж���Ϣ���ͽ�����Ӧ�Ĵ���
//				boolean b  = CacheDatas.getInstance().isRefreshing();
//				 CacheDatas.getInstance().setRefreshing(false);
				if (mb != null && mt == MessageNoConstants.TOPOSEARCH) {
					if(CacheDatas.getInstance().isRefreshing())
					CacheDatas.getInstance().setRefreshing(false);
					if (!CacheDatas.getInstance().isRefreshing()) {
						CacheDatas.getInstance().setRefreshing(true);
						TopoFoundFEPs feps = (TopoFoundFEPs) mb;
						List codes = feps.getFepCodes();
						boolean tag = true;
						for (int i = 0; i < codes.size(); i++) {
							String code = codes.get(i).toString();
							FEPEntity fep = datacache.getFEPByCode(code);
							if (fep == null || !fep.getStatus().isStatus()) {
								tag = false;
								break;
							}
						}
						if (tag) {
							Long id = feps.getRefreshDiagramId();
							if (id != null) {
								while (CacheDatas.getInstance().isWarninging()) {
									try {
										logger.info("���ڴ���澯.....");
										Thread.sleep(3000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
								if (!CacheDatas.getInstance().isWarninging()) {
									nmsService.refurshTopo(id);
								}
							}
							for (int i = 0; i < codes.size(); i++) {
								String code = codes.get(i).toString();
								smtfepService.sendMessageToFEP(code, mt, from,clientIp, feps.getMessage());
							}
						} else {
							smtcService.sendMessage(MessageNoConstants.FEPOFFLINE, "server",from, clientIp,
									"ǰ�û������ߣ����ܽ������˷��֣����Ժ�����");
							CacheDatas.getInstance().setRefreshing(false);
						}

					} else {
						smtcService.sendMessage(MessageNoConstants.REFRESHING,"server", from, clientIp,"�����ͻ������ڽ��иò���������ͬʱ�������˷���");
					}

				} else if (mb != null && mt == MessageNoConstants.SYNCHDEVICE) {
					if (!CacheDatas.getInstance().isSynchorizing()) {
						CacheDatas.getInstance().setSynchorizing(true);
						Set ss = (Set) mb;
						smtfepService.sendMessageToIPs(mt, from, clientIp, ss);
					} else {
						smtcService.sendMessage(MessageNoConstants.SYNCHORIZING, "server",from, clientIp, "�����ͻ������ڽ��иò���������ͬʱ���в���ͬ��");
					}

				} else if (mb != null&& mt == MessageNoConstants.SINGLESYNCHDEVICE) {
					int parmType = message.getIntProperty(Constants.MESSPARMTYPE);
					Set<SynchDevice> devices = (Set) mb;
					//�Ȱѿͻ���Ҫ���صĶ�����Ϊ���ܲ�
					if (parmType == MessageNoConstants.SINGLESWITCHQOSSTORM) {
						logger.info("����������QOS�籩�������أ�");
						updateSwitchParm(devices, QOSStormControl.class);
					}
					if (parmType == MessageNoConstants.SINGLESWITCHQOSPORT) {
						logger.info("����������QOS�˿��������أ�");
						updateSwitchParm(devices, QOSSpeedConfig.class);
					}
					if (parmType == MessageNoConstants.SINGLESWITCHPORT) {
						logger.info("�����������˿����أ�");
						updateSwitchParm(devices, SwitchPortEntity.class);
					}
					if (parmType == MessageNoConstants.SINGLESWITCHSTPPORT) {
						logger.info("����������STP�˿����أ�");
						updateSwitchParm(devices, STPPortConfig.class);
					}
					if (parmType == MessageNoConstants.SINGLESWITCHQOSPRIORITY802D1P) {
						logger.info("����������QOS 802D1P���أ�");
						updateSwitchParm(devices, Priority802D1P.class);
					}
					if (parmType == MessageNoConstants.SINGLESWITCHQOSPRIORITYDSCP) {
						logger.info("����������QOS DSCP���أ�");
						updateSwitchParm(devices, PriorityDSCP.class);
					}
					if (parmType == MessageNoConstants.SINGLESWITCHQOSPRIORITYTOS) {
						logger.info("����������QOS TOS���أ�");
						updateSwitchParm(devices, PriorityTOS.class);
					}
					if(parmType == MessageNoConstants.SINGLESWITCHQOSSYS){
						logger.info("����������QOS SYS���أ�");
						updateSwitchParm(devices, QOSSysConfig.class);
					}
					if(parmType == MessageNoConstants.SINGLESWITCHSNTP){
						logger.info("����������SNTP���أ�");
						updateSwitchParm(devices, SNTPConfigEntity.class);
					}
					if(parmType == MessageNoConstants.SINGLESWITCHVLAN){
						logger.info("����������VLAN���أ�");
						updateSwitchParm(devices, VlanEntity.class);
					}
					if(parmType == MessageNoConstants.SINGLESWITCHVLANPORT){
						logger.info("����������VLAN�˿����أ�");
						updateSwitchParm(devices, VlanPort.class);
					}
					if(parmType == MessageNoConstants.SINGLESWITCHUNICAST){
						logger.info("�����������������أ�");
						updateSwitchParm(devices, MACUniCast.class);
					}
					if(parmType == MessageNoConstants.SINGLESWITCHMULTICAST){
						logger.info("�����������ಥ���أ�");
						updateSwitchParm(devices, MACMutiCast.class);
					}
					if(parmType == MessageNoConstants.SINGLESWITCHIGMPPORT){
						logger.info("����������IGMP�˿����أ�");
						updateSwitchParm(devices, IGMPEntity.class);
					}
					if(parmType == MessageNoConstants.SINGLESWITCHIGMPVLANID){
						logger.info("����������IGMP Igmp_vsi�˿����أ�");
						updateSwitchParm(devices, Igmp_vsi.class);
					}
					if(parmType == MessageNoConstants.SINGLESWITCHRING){
						logger.info("����������RING�˿����أ�");
						updateSwitchParm(devices, RingConfig.class);
						updateSwitchParm(devices, RingPortInfo.class);
					}
					if(parmType==MessageNoConstants.SINGLESWITCHLINKBACKUPS){
						logger.info("������������·���ݶ˿����أ�");
						updateSwitchParm(devices, LinkBakConfig.class);
					}
					if(parmType==MessageNoConstants.SINGLESWITCHSNMPHOST){
						logger.info("����������SNMP�������أ�");
						updateSwitchParm(devices, SNMPHost.class);
					}
					if(parmType==MessageNoConstants.SINGLESWITCHSNMPVIEW){
						logger.info("����������SNMP��ͼ���أ�");
						updateSwitchParm(devices, SNMPView.class);
					}
					if(parmType==MessageNoConstants.SINGLESWITCHSNMPGROUP){
						logger.info("����SNMPȺ�����أ�");
						updateSwitchParm(devices, SNMPGroup.class);
					}
					if(parmType==MessageNoConstants.SINGLESWITCHSNMPMASS){
						logger.info("����������SNMP�������أ�");
						updateSwitchParm(devices, SNMPMass.class);
					}
					if(parmType==MessageNoConstants.SINGLESWITCHSNMPUSER){
						logger.info("����������SNMP�û����أ�");
						updateSwitchParm(devices, SNMPUser.class);
					}
					if(parmType==MessageNoConstants.SINGLESWITCHSTPSYS){
						logger.info("����������STPϵͳ���أ�");
						updateSwitchParm(devices, STPSysConfig.class);
					}
					if(parmType==MessageNoConstants.SINGLESWITCHSERIAL){
						logger.info("�����������������أ�");
						updateSwitchParm(devices, SwitchSerialPort.class);
					}
					if(parmType==MessageNoConstants.SINGLESWITCHIP){
						logger.info("����������IP���أ�");
						updateSwitchParm(devices, SwitchBaseConfig.class);
					}
					if(parmType==MessageNoConstants.SINGLESWITCHLLDP){
						logger.info("����������LLDP�������أ�");
						updateSwitchParm(devices, LLDPConfig.class);
					}
					if(parmType==MessageNoConstants.SINGLESWITCHLACP){
						logger.info("����������Lacp���أ�");
						updateSwitchParm(devices, LACPConfig.class);
					}
					if(parmType==MessageNoConstants.SINGLESWITCHPORTWARN){
						logger.info("�����������˿ڸ澯���أ�");
						updateSwitchParm(devices, RmonWarningConfig.class);
					}
					if(parmType==MessageNoConstants.SINGLESWITCHUSERADM){
						logger.info("�����������û����أ�");
						updateSwitchParm(devices, SwitchUser.class);
					}
					if(parmType==MessageNoConstants.SINGLESWITCHINFO){
						logger.info("����������������Ϣ���أ�");
						updateSwitchParm(devices, SwitchBaseInfo.class);
					}
					if(parmType==MessageNoConstants.SINGLESWITCHPORTREMON){
						logger.info("�����������˿ڸ澯���أ�");
						updateSwitchParm(devices, PortRemon.class);
					}
					if(parmType==MessageNoConstants.SINGLESWITCHMIRROR){
						logger.info("����������Mirror���أ�");
						updateSwitchParm(devices, MirrorEntity.class);
					}
					if(parmType==MessageNoConstants.SINGLESWITCHTRUNKPORT){
						logger.info("�����������˿ھۺ����أ�");
						updateSwitchParm(devices, TrunkConfig.class);
					}
					
					if (!CacheDatas.getInstance().isSynchorizing()) {
						if(parmType==MessageNoConstants.SINGLESYSLOGHOST){
							logger.info("������syslogHost�����������أ�");
							
							//�����������
							commService.deleteEntitys(SysLogHostToDevEntity.class);
							commService.deleteEntitys(SysLogHostEntity.class);
						}
						
						smtfepService.sendMessageToIPs(parmType, mt, from,clientIp, devices);
					} else {
						smtcService.sendMessage(MessageNoConstants.SYNCHORIZING, "server",from, clientIp, "�����ͻ������ڽ��иò���������ͬʱ���в�������");
					}
				} else if (mb != null && mt == MessageNoConstants.SYNCHFEP) {
					if (!CacheDatas.getInstance().isSynchorizing()) {
						SynchFEP sf = (SynchFEP) mb;
						smtfepService.sendMessageToFEP(sf.getFepCode(), mt,from, clientIp, sf);
					} else {
						smtcService.sendMessage(MessageNoConstants.SYNCHORIZING, "server",from, clientIp, "�����ͻ������ڽ��иò���������ͬʱ���в�������");
					}
				} else if (mb != null&& mt == MessageNoConstants.RTMONITORSTART) {
					RTMonitorConfig config = (RTMonitorConfig) mb;
					commService.saveEntity(config);
					int deviceType = om.getIntProperty(Constants.DEVTYPE);
					String ipValue = config.getIpValue();
					smtfepService.sendMessage(ipValue, mt, from, clientIp,deviceType, config);

				} else if (mb != null && mt == MessageNoConstants.RTMONITORSTOP) {
					RTMonitorConfig config = (RTMonitorConfig) mb;
					int deviceType = om.getIntProperty(Constants.DEVTYPE);
					String ipValue = config.getIpValue();
					smtfepService.sendMessage(ipValue, mt, from, clientIp,deviceType, config);
				} else if (mb != null && mt == MessageNoConstants.PINGSTART) {
					ArrayList datas = (ArrayList) mb;
					smtfepService.splitSendMessage(mt, from, clientIp, datas);
				} else if (mt == MessageNoConstants.CARRIERTEST
						|| mt == MessageNoConstants.CARRIERRESTART
						|| mt == MessageNoConstants.CARRIERROUTECONFIG
						|| mt == MessageNoConstants.CARRIERWAVEBANDQUERY
						|| mt == MessageNoConstants.CARRIERROUTEQUERY
						|| mt == MessageNoConstants.CARRIERPORTQUERY) {
					CarrierEntity carrier = (CarrierEntity) mb;
					String fepCode = carrier.getFepCode();
					if (fepCode == null) {
						logger.info("�ز���û������ǰ�û���");
					} else {
						FEPEntity fep = datacache.getFEPByCode(fepCode);
						if (fep == null || !fep.getStatus().isStatus()) {
							smtcService.sendMessage(MessageNoConstants.FEPOFFLINE, "server",from, clientIp, "ǰ�û������ߣ�");
							return;
						}
					}
					smtfepService.sendMessageToFEP(fepCode, mt, from, clientIp,carrier.getCarrierCode() + "");
				} else if (mt == MessageNoConstants.CARRIERROUTECLEAR) {
					CarrierEntity carrier = (CarrierEntity) mb;
					Set<CarrierRouteEntity> routeSet = carrier.getRoutes();
					int syn_type = om.getIntProperty(Constants.SYN_TYPE);
					if (syn_type == Constants.SYN_DEV) {

						String fepCode = carrier.getFepCode();
						if (fepCode == null) {
							logger.info("�ز���û������ǰ�û���");
						} else {
							FEPEntity fep = datacache.getFEPByCode(fepCode);
							if (fep == null || !fep.getStatus().isStatus()) {
								smtcService.sendMessage(MessageNoConstants.FEPOFFLINE,"server", from, clientIp, "ǰ�û������ߣ�");
								return;
							}
						}
						smtfepService.sendMessageToFEP(fepCode, mt, from,clientIp, carrier.getCarrierCode() + "");
					} else if (syn_type == Constants.SYN_SERVER) {
						carrier = commService.getCarrierByCode(carrier.getCarrierCode());
						if (routeSet != null && routeSet.size() > 0) {
							carrier.getRoutes().removeAll(carrier.getRoutes());
							commService.deleteEntities(routeSet);
						}

					} else if (syn_type == Constants.SYN_ALL) {
						carrier = commService.getCarrierByCode(carrier.getCarrierCode());
						if (routeSet != null && routeSet.size() > 0) {
							carrier.getRoutes().remove(carrier.getRoutes());
							commService.deleteEntities(routeSet);
						}
						String fepCode = carrier.getFepCode();
						if (fepCode == null) {
							logger.info("�ز���û������ǰ�û���");
						} else {
							FEPEntity fep = datacache.getFEPByCode(fepCode);
							if (fep == null || !fep.getStatus().isStatus()) {
								smtcService.sendMessage(MessageNoConstants.FEPOFFLINE,"server", from, clientIp, "ǰ�û������ߣ�");
								return;
							}
						}
						smtfepService.sendMessageToFEP(fepCode, mt, from,clientIp, carrier.getCarrierCode() + "");
					}
				} else if (mt == MessageNoConstants.SWITCHUSERMANAGE) {
					int switchusers = om.getIntProperty(Constants.SWITCHUSERS);
					ArrayList<SwitchUser> datas = (ArrayList) mb;
					List<SwitchUser> users =new ArrayList<SwitchUser>();
					if (switchusers == Constants.SWITCHUSERADD) {
						if (datas != null && datas.size() > 0) {
							for (int i = 0; i < datas.size(); i++) {
								SwitchUser switchUser =(SwitchUser) commService.updateEntity(datas.get(i));
								users.add(switchUser);
							}
							smtfepService.sendMessageToFEP(mt, from,clientIp, users, switchusers);
						}
					} else if (switchusers == Constants.SWITCHUSERUPDATE) {
						if (datas != null && datas.size() > 0) {
							for (int i = 0; i < datas.size(); i++) {
								commService.updateEntity(datas.get(i));
								smtfepService.sendMessageToFEP(mt, from,clientIp, datas, switchusers);
							}
						}

					} else if (switchusers == Constants.SWITCHUSERDEL) {
						if (datas != null && datas.size() > 0) {
							commService.deleteEntities(datas);
							smtfepService.sendMessageToFEP(mt, from, clientIp,datas, switchusers);
						}
					}
				}
			} catch (JMSException e) {
				e.printStackTrace();
			}
		} else if (message instanceof TextMessage) {

			TextMessage tm = (TextMessage) message;
			int mt = message.getIntProperty(Constants.MESSAGETYPE);
			logger.info("i have received TextMessage from client !    :" + mt);
			String from = message.getStringProperty(Constants.MESSAGEFROM);
			String clientIp = message.getStringProperty(Constants.CLIENTIP);
			String fepCode = message.getStringProperty(Constants.AIMFEP);
			int deviceType = message.getIntProperty(Constants.DEVTYPE);

			if (mt == MessageNoConstants.LIGHT_SIGNAL) {
				String ipvalue = message.getStringProperty(Constants.MESSAGETO);
				if (tm.getText() != null) {
					smtfepService.sendMessage(ipvalue, mt, from, clientIp,
							deviceType, tm.getText());
				} else {
					smtfepService.sendMessage(ipvalue, mt, from, clientIp,
							deviceType, "");
				}

			} else {
				if (tm.getText() != null) {
					smtfepService.sendMessageToFEP(fepCode, mt, from, clientIp,
							tm.getText());
				} else {
					smtfepService.sendMessageToFEP(fepCode, mt, from, clientIp,
							"");
				}
			}

		} else if (message instanceof StreamMessage) {
			StreamMessage sm = (StreamMessage) message;

			int mt = sm.getIntProperty(Constants.MESSAGETYPE);
			if (sm != null && mt == MessageNoConstants.SWITCHERUPGRATE) {
				smtfepService.sendStreamMessage(sm);
			} else if (sm != null
					&& mt == MessageNoConstants.CARRIERSYSTEMUPGRADE) {
				smtfepService.sendStreamMessage(sm);
			}
		}
	}
	private void updateSwitchParm(Set<SynchDevice> devices, Class<?> clazz) {

		if (devices.size() > 0) {
			for (SynchDevice device : devices) {
				String ipValue = device.getIpvalue();
				SwitchNodeEntity nodeEntity = commService.getSwitchByIp(ipValue);
				
				if (nodeEntity != null) {
					
					Object[] parms ={nodeEntity};
					
					
					if(clazz.getName().equals(Priority802D1P.class.getName())){
						List<QOSPriority> list =(List<QOSPriority>) commService.findAll(QOSPriority.class, "where entity.switchNode=?",parms );
						if(list.size()>0){
							for(QOSPriority qosPriority :list){
								List<Priority802D1P> priority802d1ps =qosPriority.getPriorityEOTs();
								if(priority802d1ps.size()>0){
									for(Priority802D1P priority802d1p :priority802d1ps){
										priority802d1p.setIssuedTag(Constants.ISSUEDADM);
										commService.updateEntity(priority802d1p);
									}
								}
							}
						}
						
					}else if(clazz.getName().equals(PriorityDSCP.class.getName())){
						List<QOSPriority> list =(List<QOSPriority>) commService.findAll(QOSPriority.class, "where entity.switchNode=?",parms );
						if(list.size()>0){
							for(QOSPriority qosPriority :list){
								List<PriorityDSCP> priorityDSCPs =qosPriority.getPriorityDSCPs();
								if(priorityDSCPs.size()>0){
									for(PriorityDSCP priorityDSCP :priorityDSCPs){
										priorityDSCP.setIssuedTag(Constants.ISSUEDADM);
										commService.updateEntity(priorityDSCP);
									}
								}
								
								
							}
						}
						
					}else if(clazz.getName().equals(PriorityTOS.class.getName())){
						List<QOSPriority> list =(List<QOSPriority>) commService.findAll(QOSPriority.class, "where entity.switchNode=?",parms );
						if(list.size()>0){
							for(QOSPriority qosPriority :list){
								List<PriorityTOS> priorityTOSs =qosPriority.getPriorityTOSs();
								if(priorityTOSs.size()>0){
									for(PriorityTOS priorityTOS :priorityTOSs){
										priorityTOS.setIssuedTag(Constants.ISSUEDADM);
										commService.updateEntity(priorityTOS);
									}
								}
								
							}
						}
						
					}else if(clazz.getName().equals(VlanPort.class.getName())){
						List<VlanConfig> configs =(List<VlanConfig>) commService.findAll(VlanConfig.class, "where entity.switchNode=?",parms );
						if(configs.size()>0){
							for(VlanConfig vlanConfig :configs){
								Set<VlanPort> portEntities =vlanConfig.getVlanPorts();
								if(portEntities!=null&&portEntities.size()>0){
									for(VlanPort vlanPort :portEntities){
										vlanPort.setIssuedTag(Constants.ISSUEDADM);
										commService.updateEntity(vlanPort);
									}
								}
								
							}
						}
						
					}else if(clazz.getName().equals(VlanEntity.class.getName())){
						List<VlanConfig> configs =(List<VlanConfig>) commService.findAll(VlanConfig.class, "where entity.switchNode=?",parms );
						if(configs.size()>0){
							for(VlanConfig vlanConfig :configs){
								Set<VlanEntity> vlanEntities =vlanConfig.getVlanEntitys();
								if(vlanEntities!=null&&vlanEntities.size()>0){
									for(VlanEntity vlanEntity :vlanEntities){
										Set<VlanPortConfig> portConfigs =vlanEntity.getPortConfig();
										if(portConfigs!=null&&portConfigs.size()>0){
											for(VlanPortConfig vlanPortConfig:portConfigs){
												vlanPortConfig.setIssuedTag(Constants.ISSUEDDEVICE);
												commService.updateEntity(vlanPortConfig);
											}
										}
										vlanEntity.setIssuedTag(Constants.ISSUEDADM);
										commService.updateEntity(vlanEntity);
									}
								}
								
							}
						}
					} else if(clazz.getName().equals(IGMPEntity.class.getName())){
						List<IGMPEntity> igmpEntities =(List<IGMPEntity>) commService.findAll(IGMPEntity.class, "where entity.switchNode=?",parms );
					    
						if(igmpEntities!=null&&igmpEntities.size()>0){
							
							for(IGMPEntity igmpEntity :igmpEntities){
								igmpEntity.setIssuedTag(Constants.ISSUEDADM);
								commService.updateEntity(igmpEntity);
							}
						}
						
					}else if(clazz.getName().equals(Igmp_vsi.class.getName())){
						List<IGMPEntity> igmpEntities =(List<IGMPEntity>) commService.findAll(IGMPEntity.class, "where entity.switchNode=?",parms );
						if(igmpEntities!=null&&igmpEntities.size()>0){
							
							for(IGMPEntity igmpEntity :igmpEntities){
								List<Igmp_vsi> igmpVsis =igmpEntity.getVlanIds();
								if(igmpVsis!=null&&igmpVsis.size()>0){
									for(Igmp_vsi igmpVsi :igmpVsis){
										igmpVsi.setIssuedTag(Constants.ISSUEDADM);
										commService.updateEntity(igmpVsi);
									}
									
								}
								
							}
							
						}
						
						
						
					}else if(clazz.getName().equals(SwitchBaseConfig.class.getName())){
						SwitchBaseConfig baseConfig =nodeEntity.getBaseConfig();
						baseConfig.setIssuedTag(Constants.ISSUEDADM);
						commService.updateEntity(baseConfig);
						
					}else if(clazz.getName().equals(SwitchBaseInfo.class.getName())){
						SwitchBaseInfo baseInfo =nodeEntity.getBaseInfo();
						baseInfo.setIssuedTag(Constants.ISSUEDADM);
						commService.updateEntity(baseInfo);
						
					}
					else{
					nmsService.updateEntityBySwitch(clazz, nodeEntity);
					}
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deleteVlan() {

		List<VlanEntity> delVlanEntities = new ArrayList<VlanEntity>();
		List<VlanEntity> entities = (List<VlanEntity>) commService.findAll(VlanEntity.class);
		if (entities != null) {
			a: for (VlanEntity delEntity : entities) {
				if (delEntity.getVlanConfig() == null) {
					delVlanEntities.add(delEntity);
					break a;
				}
			}
			if (delVlanEntities != null) {
				commService.deleteEntities(delVlanEntities);
			}
		}

	}
}
