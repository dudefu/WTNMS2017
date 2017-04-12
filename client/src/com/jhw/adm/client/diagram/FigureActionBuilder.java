package com.jhw.adm.client.diagram;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ShowViewAction;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.MainMenuConstants;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.draw.LabeledLinkFigure;
import com.jhw.adm.client.draw.NodeFigure;
//import com.jhw.adm.client.map.GISView;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.MessageProcessorStrategy;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.client.util.ThreadUtils;
import com.jhw.adm.client.views.CommentAreaInfoView;
import com.jhw.adm.client.views.ConfigureGroupView;
import com.jhw.adm.client.views.CurrentNodeDataSynchronizationView;
import com.jhw.adm.client.views.EquipmentAlarmConfirmView;
import com.jhw.adm.client.views.FrontEndInfoView;
import com.jhw.adm.client.views.GPRSView;
import com.jhw.adm.client.views.LinkDetailView;
import com.jhw.adm.client.views.SubnetPropertyAppendView;
import com.jhw.adm.client.views.TopologyGroupView;
import com.jhw.adm.client.views.VirtualNetworkElementManagementView;
import com.jhw.adm.client.views.carrier.CarrierConfigurePortView;
import com.jhw.adm.client.views.carrier.CarrierConfigureRouteView;
import com.jhw.adm.client.views.carrier.CarrierConfigureWaveBandView;
import com.jhw.adm.client.views.carrier.CarrierInfoView;
import com.jhw.adm.client.views.carrier.CarrierSystemUpgradeView;
import com.jhw.adm.client.views.carrier.CarrierTestingDialog;
import com.jhw.adm.client.views.epon.OLTBaseInfoView;
import com.jhw.adm.client.views.epon.ONUBaseInfoView;
import com.jhw.adm.client.views.epon.OpticalSplitterBaseInfoView;
import com.jhw.adm.client.views.switcher.BaseInfoView;
import com.jhw.adm.client.views.switcher.CurrentNodeSwitcherSystemUpgradeView;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.client.views.switchlayer3.SwitchLayer3BaseInfoView;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.tuopos.SubNetTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.tuopos.VirtualNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.PingResult;

@Component(FigureActionBuilder.ID)
@Scope(Scopes.PROTOTYPE)
public class FigureActionBuilder {

	@PostConstruct
	protected void initialize() {
	}
	
	/** 配置交换机图形的双击行为 */
	public void configureSwitcherFigureDoubleClickActions(NodeFigure switcherFigure) {
		ShowViewAction emulationAction = (ShowViewAction) applicationContext.getBean(ShowViewAction.ID);
		emulationAction.setViewId(EmulationView.ID);
		emulationAction.setGroupId(ConfigureGroupView.ID);
		emulationAction.putValue(Action.NAME, "网元管理");

		switcherFigure.setDoubleClickAction(emulationAction);
	}
	
	/** 配置三层交换机图形的双击行为 */
	public void configureLayer3FigureDoubleClickActions(NodeFigure switcherFigure) {
		ShowViewAction emulationAction = (ShowViewAction) applicationContext.getBean(ShowViewAction.ID);
		emulationAction.setViewId(SwitchLayer3BaseInfoView.ID);
		emulationAction.setGroupId(ConfigureGroupView.ID);
		emulationAction.putValue(Action.NAME, "基本信息");

		switcherFigure.setDoubleClickAction(emulationAction);
	}
	
	/** 配置前置机图形的双击行为 */
	public void configureFrontEndDoubleClickActions(NodeFigure frontEndFigure) {
		ShowViewAction emulationAction = (ShowViewAction) applicationContext.getBean(ShowViewAction.ID);
		emulationAction.setViewId(FrontEndInfoView.ID);
		emulationAction.setGroupId(ConfigureGroupView.ID);
		emulationAction.putValue(Action.NAME, "网元管理");

		frontEndFigure.setDoubleClickAction(emulationAction);
	}
	
	/** 配置前置机图形的右键菜单 */
	public void configureFrontEndPopupActions(NodeFigure frontEndFigure) {
		ShowViewAction basicInfoAction = (ShowViewAction) applicationContext
				.getBean(ShowViewAction.ID);
		basicInfoAction.setViewId(FrontEndInfoView.ID);
		basicInfoAction.setGroupId(ConfigureGroupView.ID);
		basicInfoAction.putValue(Action.NAME, "基本信息");
		basicInfoAction.putValue(Action.SMALL_ICON, imageRegistry
				.getImageIcon(MainMenuConstants.BASIC_INFO));
		
		List<Action> popupActions = new ArrayList<Action>();
		popupActions.add(basicInfoAction);
		frontEndFigure.setActions(popupActions);
	}
	
	/** 配置载波机图形的双击行为 */
	public void configureCarrierFigureDoubleClickActions(NodeFigure equipmentFigure) {
		ShowViewAction emulationAction = (ShowViewAction) applicationContext.getBean(ShowViewAction.ID);
		emulationAction.setViewId(CarrierInfoView.ID);
		emulationAction.setGroupId(ConfigureGroupView.ID);
		emulationAction.putValue(Action.NAME, "载频机基本信息");

		equipmentFigure.setDoubleClickAction(emulationAction);
	}
	
	/** 配置OLT图形的双击行为 */
	public void configureOltFigureDoubleClickActions(NodeFigure equipmentFigure) {
		ShowViewAction emulationAction = (ShowViewAction) applicationContext.getBean(ShowViewAction.ID);
		emulationAction.setViewId(OLTBaseInfoView.ID);
		emulationAction.setGroupId(ConfigureGroupView.ID);
		emulationAction.putValue(Action.NAME, "OLT仿真");

		equipmentFigure.setDoubleClickAction(emulationAction);
	}
	
	/** 配置ONU图形的双击行为 */
	public void configureOnuFigureDoubleClickActions(NodeFigure equipmentFigure) {
		ShowViewAction emulationAction = (ShowViewAction) applicationContext.getBean(ShowViewAction.ID);
		emulationAction.setViewId(ONUBaseInfoView.ID);
		emulationAction.setGroupId(ConfigureGroupView.ID);
		emulationAction.putValue(Action.NAME, "ONU基本信息");

		equipmentFigure.setDoubleClickAction(emulationAction);
	}
	
	/** 配置分光器图形的双击行为 */
	public void configureSplitterFigureDoubleClickActions(NodeFigure equipmentFigure) {
		ShowViewAction emulationAction = (ShowViewAction) applicationContext.getBean(ShowViewAction.ID);
		emulationAction.setViewId(OpticalSplitterBaseInfoView.ID);
		emulationAction.setGroupId(ConfigureGroupView.ID);
		emulationAction.putValue(Action.NAME, "分光器基本信息");

		equipmentFigure.setDoubleClickAction(emulationAction);
	}
	
	/** 配置分光器图形的右键菜单 */
	public void configureSplitterFigurePopupActions(NodeFigure equipmentFigure) {
		ShowViewAction gisAction = (ShowViewAction) applicationContext.getBean(ShowViewAction.ID);
//		gisAction.setViewId(GISView.ID);
		gisAction.setGroupId(TopologyGroupView.ID);
		gisAction.putValue(Action.NAME, "地理定位");

		ShowViewAction basicInfoAction = (ShowViewAction) applicationContext
				.getBean(ShowViewAction.ID);
		basicInfoAction.setViewId(OpticalSplitterBaseInfoView.ID);
		basicInfoAction.setGroupId(ConfigureGroupView.ID);
		basicInfoAction.putValue(Action.NAME, "基本信息");
		basicInfoAction.putValue(Action.SMALL_ICON, imageRegistry
				.getImageIcon(MainMenuConstants.BASIC_INFO));
		
		List<Action> popupActions = new ArrayList<Action>();
//		popupActions.add(gisAction);
		popupActions.add(basicInfoAction);
		equipmentFigure.setActions(popupActions);
	}
	
	/** 配置GPRS双击行为 */
	public void configureGPRSDoubleClickActions(NodeFigure equipmentFigure) {
		ShowViewAction emulationAction = (ShowViewAction) applicationContext.getBean(ShowViewAction.ID);
		emulationAction.setViewId(GPRSView.ID);
		emulationAction.setGroupId(ConfigureGroupView.ID);
		emulationAction.putValue(Action.NAME, "基本信息");

		equipmentFigure.setDoubleClickAction(emulationAction);
	}
	
	/** 配置GPRS图形的右键菜单 */
	public void configureGPRSFigurePopupActions(NodeFigure equipmentFigure) {
		ShowViewAction gisAction = (ShowViewAction) applicationContext.getBean(ShowViewAction.ID);
//		gisAction.setViewId(GISView.ID);
		gisAction.setGroupId(TopologyGroupView.ID);
		gisAction.putValue(Action.NAME, "地理定位");

		ShowViewAction basicInfoAction = (ShowViewAction) applicationContext
				.getBean(ShowViewAction.ID);
		basicInfoAction.setViewId(GPRSView.ID);
		basicInfoAction.setGroupId(ConfigureGroupView.ID);
		basicInfoAction.putValue(Action.NAME, "基本信息");
		basicInfoAction.putValue(Action.SMALL_ICON, imageRegistry
				.getImageIcon(MainMenuConstants.BASIC_INFO));
		
		List<Action> popupActions = new ArrayList<Action>();
//		popupActions.add(gisAction);
		popupActions.add(basicInfoAction);
		equipmentFigure.setActions(popupActions);
	}
	
	/** 配置OLT图形的右键菜单 */
	@SuppressWarnings("serial")
	public void configureOltFigurePopupActions(NodeFigure equipmentFigure) {	
		ShowViewAction gisAction = (ShowViewAction) applicationContext.getBean(ShowViewAction.ID);
//		gisAction.setViewId(GISView.ID);
		gisAction.setGroupId(TopologyGroupView.ID);
		gisAction.putValue(Action.NAME, "地理定位");

		ShowViewAction basicInfoAction = (ShowViewAction) applicationContext
				.getBean(ShowViewAction.ID);
		basicInfoAction.setViewId(OLTBaseInfoView.ID);
		basicInfoAction.setGroupId(ConfigureGroupView.ID);
		basicInfoAction.putValue(Action.NAME, "基本信息");
		basicInfoAction.putValue(Action.SMALL_ICON, imageRegistry
				.getImageIcon(MainMenuConstants.BASIC_INFO));

		ShowViewAction alertManagementAction = (ShowViewAction) applicationContext
				.getBean(ShowViewAction.ID);
		alertManagementAction.setViewId(EquipmentAlarmConfirmView.ID);
		alertManagementAction.putValue(Action.NAME, "告警管理");
		alertManagementAction.putValue(Action.SMALL_ICON, imageRegistry
				.getImageIcon(MainMenuConstants.ALERT_MANAGEMENT));
		
		Action testAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				EponTopoEntity selectedOlt = (EponTopoEntity) adapterManager.getAdapter(equipmentModel.getLastSelected(), EponTopoEntity.class);
				final ArrayList<PingResult> addressList = new ArrayList<PingResult>();				
				PingResult pingResult = new PingResult();
				pingResult.setIpValue(selectedOlt.getIpValue());
				pingResult.setStatus(0);				
				addressList.add(pingResult);
				JProgressBarModel progressBarModel = new JProgressBarModel();
				final PingProcessorStrategy strategy = new PingProcessorStrategy(progressBarModel);
				JProgressBarDialog dialog = new JProgressBarDialog(ClientUtils.getRootFrame());
				dialog.setOperation("节点测试");
				dialog.setDetermine(true);
				dialog.setModel(progressBarModel);
				dialog.setStrategy(strategy);
				dialog.run(new Task() {
					@Override
					public void run() {						
						final ClientModel clientModel = ClientUtils.getSpringBean(ClientModel.ID);
						RemoteServer remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
						MessageSender messageSender = remoteServer.getMessageSender();
						messageSender.send(new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								ObjectMessage message = session.createObjectMessage();
								message.setIntProperty(Constants.MESSAGETYPE,
										MessageNoConstants.PINGSTART);
								message.setObject(addressList);
								message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
								message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
								return message;
							}
						});
					}
				});
			
			}
		};
		testAction.putValue(Action.NAME, "节点测试");
		
		ShowViewAction emulationAction = (ShowViewAction) applicationContext.getBean(ShowViewAction.ID);
		emulationAction.setViewId(EponEmulationView.ID);
		emulationAction.setGroupId(ConfigureGroupView.ID);
		emulationAction.putValue(Action.NAME, "网元管理");

		Action telnetAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					OLTEntity selectedOlt = (OLTEntity) adapterManager
							.getAdapter(equipmentModel.getLastSelected(),
									OLTEntity.class);
					if (selectedOlt != null) {
						String selectedAddress = selectedOlt.getIpValue();
						String command = String.format("cmd.exe /c start Telnet.exe %s", selectedAddress);
						Runtime.getRuntime().exec(command);
					}
				} catch (IOException ex) {
					LOG.error("Runtime.exec error", ex);
				}
			}
		};
		telnetAction.putValue(Action.NAME, "Telnet");		
		
		Action closeEquipmentAlarmAction = new AbstractAction() {
			@Override	
			public void actionPerformed(ActionEvent e) {
				final EponTopoEntity selectedNode = 
					(EponTopoEntity)adapterManager.getAdapter(equipmentModel.getLastSelected(), EponTopoEntity.class);
				if (selectedNode != null) {
					JProgressBarModel progressBarModel = new JProgressBarModel();
					final SimpleConfigureStrategy strategy = new SimpleConfigureStrategy(StringUtils.EMPTY, progressBarModel);
					JProgressBarDialog dialog = new JProgressBarDialog(ClientUtils.getRootFrame());
					dialog.setOperation("关闭设备告警");
					dialog.setDetermine(true);
					dialog.setModel(progressBarModel);
					dialog.setStrategy(strategy);
					dialog.run(new Task() {
						@Override
						public void run() {
							List<Object> selectedNodeList = new ArrayList<Object>();
							selectedNodeList.add(selectedNode);
							NodeUtils.closeEquipmentAlarmManual(selectedNodeList);
							// 为了显示动画效果
							ThreadUtils.sleep(8);
							strategy.showNormalMessage("关闭完成");
						}
					});
				}
			}
		};
		closeEquipmentAlarmAction.putValue(Action.NAME, "关闭告警");
		
		List<Action> popupActions = new ArrayList<Action>();

//		popupActions.add(gisAction);
		popupActions.add(basicInfoAction);
		popupActions.add(alertManagementAction);
		popupActions.add(testAction);
		popupActions.add(emulationAction);
		popupActions.add(telnetAction);
		popupActions.add(closeEquipmentAlarmAction);
		
		equipmentFigure.setActions(popupActions);
	}
	//

	
	/** 配置三层交换机图形的右键菜单 */
	@SuppressWarnings("serial")
	public void configureLayer3FigurePopupActions(NodeFigure equipmentFigure) {	
		ShowViewAction gisAction = (ShowViewAction) applicationContext.getBean(ShowViewAction.ID);
//		gisAction.setViewId(GISView.ID);
		gisAction.setGroupId(TopologyGroupView.ID);
		gisAction.putValue(Action.NAME, "地理定位");
		
		ShowViewAction basicInfoAction = (ShowViewAction) applicationContext
							.getBean(ShowViewAction.ID);
		basicInfoAction.setViewId(SwitchLayer3BaseInfoView.ID);
		basicInfoAction.setGroupId(ConfigureGroupView.ID);
		basicInfoAction.putValue(Action.NAME, "基本信息");
		basicInfoAction.putValue(Action.SMALL_ICON, imageRegistry
							.getImageIcon(MainMenuConstants.BASIC_INFO));
		
		Action testAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwitchTopoNodeLevel3 selectedLayer3 = (SwitchTopoNodeLevel3) adapterManager.getAdapter(equipmentModel.getLastSelected(), SwitchTopoNodeLevel3.class);
				final ArrayList<PingResult> addressList = new ArrayList<PingResult>();				
				PingResult pingResult = new PingResult();
				pingResult.setIpValue(selectedLayer3.getIpValue());
				pingResult.setStatus(0);				
				addressList.add(pingResult);
				JProgressBarModel progressBarModel = new JProgressBarModel();
				final PingProcessorStrategy strategy = new PingProcessorStrategy(progressBarModel);
				JProgressBarDialog dialog = new JProgressBarDialog(ClientUtils.getRootFrame());
				dialog.setOperation("节点测试");
				dialog.setDetermine(true);
				dialog.setModel(progressBarModel);
				dialog.setStrategy(strategy);
				dialog.run(new Task() {
					@Override
					public void run() {						
						final ClientModel clientModel = ClientUtils.getSpringBean(ClientModel.ID);
						RemoteServer remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
						MessageSender messageSender = remoteServer.getMessageSender();
						messageSender.send(new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								ObjectMessage message = session.createObjectMessage();
								message.setIntProperty(Constants.MESSAGETYPE,
										MessageNoConstants.PINGSTART);
								message.setObject(addressList);
								message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
								message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
								return message;
							}
						});
					}
				});
			}
		};
		testAction.putValue(Action.NAME, "节点测试");

		Action telnetAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					SwitchLayer3 selectedLayer3 = (SwitchLayer3) adapterManager
							.getAdapter(equipmentModel.getLastSelected(),
									SwitchLayer3.class);
					if (selectedLayer3 == null) {
						LOG.error("三层交换机节点为空");
					} else {
						String selectedAddress = selectedLayer3.getIpValue();
						String command = String.format("cmd.exe /c start Telnet.exe %s", selectedAddress);
						Runtime.getRuntime().exec(command);
						LOG.info(String.format("Telnet 三层交换机[%s]", selectedAddress));
					}
				} catch (IOException ex) {
					LOG.error("Runtime.exec error", ex);
				}
			}
		};
		telnetAction.putValue(Action.NAME, "Telnet");
		//
		Action webManagementAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					SwitchLayer3 selectedLayer3 = 
						(SwitchLayer3)adapterManager.getAdapter(equipmentModel.getLastSelected(), SwitchLayer3.class);
					
					if (selectedLayer3 != null) {
						String selectedAddress = selectedLayer3.getIpValue();
						String command = String.format("cmd.exe /c start iexplore.exe http://%s", selectedAddress);
						Runtime.getRuntime().exec(command);
					}
				} catch (IOException ex) {
					LOG.error("Runtime.exec error", ex);
				}
			}
		};
		webManagementAction.putValue(Action.NAME, "Web管理");
		webManagementAction.putValue(Action.SMALL_ICON, imageRegistry
				.getImageIcon(MainMenuConstants.WEB_MANAGEMENT));
		
		Action closeEquipmentAlarmAction = new AbstractAction() {
			@Override	
			public void actionPerformed(ActionEvent e) {
				final SwitchTopoNodeLevel3 selectedNode = 
					(SwitchTopoNodeLevel3)adapterManager.getAdapter(equipmentModel.getLastSelected(), SwitchTopoNodeLevel3.class);
				if (selectedNode != null) {
					JProgressBarModel progressBarModel = new JProgressBarModel();
					final SimpleConfigureStrategy strategy = new SimpleConfigureStrategy(StringUtils.EMPTY, progressBarModel);
					JProgressBarDialog dialog = new JProgressBarDialog(ClientUtils.getRootFrame());
					dialog.setOperation("关闭设备告警");
					dialog.setDetermine(true);
					dialog.setModel(progressBarModel);
					dialog.setStrategy(strategy);
					dialog.run(new Task() {
						@Override
						public void run() {
							List<Object> selectedNodeList = new ArrayList<Object>();
							selectedNodeList.add(selectedNode);
							NodeUtils.closeEquipmentAlarmManual(selectedNodeList);
							// 为了显示动画效果
							ThreadUtils.sleep(8);
							strategy.showNormalMessage("关闭完成");
						}
					});
				}
			}
		};
		closeEquipmentAlarmAction.putValue(Action.NAME, "关闭告警");

		List<Action> popupActions = new ArrayList<Action>();

//		popupActions.add(gisAction);
		popupActions.add(basicInfoAction);
		popupActions.add(testAction);
		popupActions.add(telnetAction);
		popupActions.add(webManagementAction);
		popupActions.add(closeEquipmentAlarmAction);
		
		equipmentFigure.setActions(popupActions);
	}
	//
	
	/** 配置ONU图形的右键菜单 */
	public void configureOnuFigurePopupActions(NodeFigure equipmentFigure) {	
		ShowViewAction gisAction = (ShowViewAction) applicationContext.getBean(ShowViewAction.ID);
//		gisAction.setViewId(GISView.ID);
		gisAction.setGroupId(TopologyGroupView.ID);
		gisAction.putValue(Action.NAME, "地理定位");

		ShowViewAction basicInfoAction = (ShowViewAction) applicationContext
				.getBean(ShowViewAction.ID);
		basicInfoAction.setViewId(ONUBaseInfoView.ID);
		basicInfoAction.setGroupId(ConfigureGroupView.ID);
		basicInfoAction.putValue(Action.NAME, "基本信息");
		basicInfoAction.putValue(Action.SMALL_ICON, imageRegistry
				.getImageIcon(MainMenuConstants.BASIC_INFO));

		List<Action> popupActions = new ArrayList<Action>();

//		popupActions.add(gisAction);
		popupActions.add(basicInfoAction);
		
		equipmentFigure.setActions(popupActions);
	}
	
	/** 配置连线图形的右键菜单 */
	public void configureLinkFigureDoubleClickAction(LabeledLinkFigure linkFigure) {
		ShowViewAction linkDetailAction = (ShowViewAction) applicationContext.getBean(ShowViewAction.ID);
		linkDetailAction.setViewId(LinkDetailView.ID);
		linkDetailAction.setGroupId(ConfigureGroupView.ID);
		linkDetailAction.putValue(Action.NAME, "详细信息");
		linkFigure.setDoubleClickAction(linkDetailAction);
	}
	
	/** 配置交换机图形的右键菜单 */
	public void configureSwitcherFigurePopupActions(NodeFigure equipmentFigure) {
		ShowViewAction gisAction = (ShowViewAction) applicationContext
				.getBean(ShowViewAction.ID);
//		gisAction.setViewId(GISView.ID);
		gisAction.setGroupId(TopologyGroupView.ID);
		gisAction.putValue(Action.NAME, "地理定位");

		ShowViewAction basicInfoAction = (ShowViewAction) applicationContext
				.getBean(ShowViewAction.ID);
		basicInfoAction.setViewId(BaseInfoView.ID);
		basicInfoAction.setGroupId(ConfigureGroupView.ID);
		basicInfoAction.putValue(Action.NAME, "基本信息");
		basicInfoAction.putValue(Action.SMALL_ICON, imageRegistry
				.getImageIcon(MainMenuConstants.BASIC_INFO));

		ShowViewAction alertManagementAction = (ShowViewAction) applicationContext
				.getBean(ShowViewAction.ID);
		alertManagementAction.setViewId(EquipmentAlarmConfirmView.ID);
		alertManagementAction.putValue(Action.NAME, "告警管理");
		alertManagementAction.putValue(Action.SMALL_ICON, imageRegistry
				.getImageIcon(MainMenuConstants.ALERT_MANAGEMENT));
		
		Action testAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SwitchTopoNodeEntity selectedSwitcher = (SwitchTopoNodeEntity) adapterManager.getAdapter(equipmentModel.getLastSelected(), SwitchTopoNodeEntity.class);
				final ArrayList<PingResult> addressList = new ArrayList<PingResult>();				
				PingResult pingResult = new PingResult();
				pingResult.setIpValue(selectedSwitcher.getIpValue());
				pingResult.setStatus(0);				
				addressList.add(pingResult);
				JProgressBarModel progressBarModel = new JProgressBarModel();
				final PingProcessorStrategy strategy = new PingProcessorStrategy(progressBarModel);
				JProgressBarDialog dialog = new JProgressBarDialog(ClientUtils.getRootFrame());
				dialog.setOperation("节点测试");
				dialog.setDetermine(true);
				dialog.setModel(progressBarModel);
				dialog.setStrategy(strategy);
				dialog.run(new Task() {
					@Override
					public void run() {						
						final ClientModel clientModel = ClientUtils.getSpringBean(ClientModel.ID);
						RemoteServer remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
						MessageSender messageSender = remoteServer.getMessageSender();
						messageSender.send(new MessageCreator() {
							@Override
							public Message createMessage(Session session) throws JMSException {
								ObjectMessage message = session.createObjectMessage();
								message.setIntProperty(Constants.MESSAGETYPE,
										MessageNoConstants.PINGSTART);
								message.setObject(addressList);
								message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
								message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
								return message;
							}
						});
					}
				});
			}
		};
		testAction.putValue(Action.NAME, "节点测试");
		
		ShowViewAction emulationAction = (ShowViewAction) applicationContext.getBean(ShowViewAction.ID);
		emulationAction.setViewId(EmulationView.ID);
		emulationAction.setGroupId(ConfigureGroupView.ID);
		emulationAction.putValue(Action.NAME, "网元管理");

		ShowViewAction upgradeAction = (ShowViewAction)applicationContext.getBean(ShowViewAction.ID);
		upgradeAction.setViewId(CurrentNodeSwitcherSystemUpgradeView.ID);
		upgradeAction.setGroupId(ConfigureGroupView.ID);
		upgradeAction.putValue(Action.NAME, "软件升级");
		upgradeAction.putValue(Action.SMALL_ICON, imageRegistry
				.getImageIcon(MainMenuConstants.UPGRADE));

		ShowViewAction syncAction = (ShowViewAction)applicationContext.getBean(ShowViewAction.ID);
		syncAction.setViewId(CurrentNodeDataSynchronizationView.ID);
		syncAction.setGroupId(ConfigureGroupView.ID);
		syncAction.putValue(Action.NAME, "数据上载");
		
		Action telnetAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					SwitchNodeEntity selectedSwitcher = 
						(SwitchNodeEntity)adapterManager.getAdapter(equipmentModel.getLastSelected(), SwitchNodeEntity.class);
					
					if (selectedSwitcher != null) {
						String selectedAddress = selectedSwitcher.getBaseConfig().getIpValue();
						String command = String.format("cmd.exe /c start Telnet.exe %s", selectedAddress);
						Runtime.getRuntime().exec(command);
					}
				} catch (IOException ex) {
					LOG.error("Runtime.exec error", ex);
				}
			}
		};
		telnetAction.putValue(Action.NAME, "Telnet");
		
		
		Action webManagementAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					SwitchNodeEntity selectedSwitcher = 
						(SwitchNodeEntity)adapterManager.getAdapter(equipmentModel.getLastSelected(), SwitchNodeEntity.class);
					
					if (selectedSwitcher != null) {
						String selectedAddress = selectedSwitcher.getBaseConfig().getIpValue();
						String command = String.format("cmd.exe /c start iexplore.exe http://%s", selectedAddress);
						Runtime.getRuntime().exec(command);
					}
				} catch (IOException ex) {
					LOG.error("Runtime.exec error", ex);
				}
			}
		};
		webManagementAction.putValue(Action.NAME, "Web管理");
		webManagementAction.putValue(Action.SMALL_ICON, imageRegistry
				.getImageIcon(MainMenuConstants.WEB_MANAGEMENT));
		
		Action closeEquipmentAlarmAction = new AbstractAction() {
			@Override	
			public void actionPerformed(ActionEvent e) {
				final SwitchTopoNodeEntity selectedNode = 
					(SwitchTopoNodeEntity)adapterManager.getAdapter(equipmentModel.getLastSelected(), SwitchTopoNodeEntity.class);
				if (selectedNode != null) {
					JProgressBarModel progressBarModel = new JProgressBarModel();
					final SimpleConfigureStrategy strategy = new SimpleConfigureStrategy(StringUtils.EMPTY, progressBarModel);
					JProgressBarDialog dialog = new JProgressBarDialog(ClientUtils.getRootFrame());
					dialog.setOperation("关闭设备告警");
					dialog.setDetermine(true);
					dialog.setModel(progressBarModel);
					dialog.setStrategy(strategy);
					dialog.run(new Task() {
						@Override
						public void run() {
							List<Object> selectedNodeList = new ArrayList<Object>();
							selectedNodeList.add(selectedNode);
							NodeUtils.closeEquipmentAlarmManual(selectedNodeList);
							// 为了显示动画效果
							ThreadUtils.sleep(8);
							strategy.showNormalMessage("关闭完成");
						}
					});
				}
			}
		};
		closeEquipmentAlarmAction.putValue(Action.NAME, "关闭告警");
		
		List<Action> popupActions = new ArrayList<Action>();

//		popupActions.add(gisAction);
		popupActions.add(basicInfoAction);
		popupActions.add(alertManagementAction);
		popupActions.add(testAction);
		popupActions.add(emulationAction);
		popupActions.add(upgradeAction);
		popupActions.add(syncAction);
		popupActions.add(telnetAction);
		popupActions.add(webManagementAction);
		popupActions.add(closeEquipmentAlarmAction);
		
		equipmentFigure.setActions(popupActions);
	}
	
	/** 配置载波机图形的右键菜单 */
	public void configureCarrierFigurePopupActions(NodeFigure equipmentFigure) {
		if (applicationContext.containsBean(ShowViewAction.ID)) {
			ShowViewAction gisAction = (ShowViewAction) applicationContext
					.getBean(ShowViewAction.ID);
//			gisAction.setViewId(GISView.ID);
			gisAction.setGroupId(TopologyGroupView.ID);
			gisAction.putValue(Action.NAME, "地理定位");

			ShowViewAction basicInfoAction = (ShowViewAction) applicationContext
					.getBean(ShowViewAction.ID);
			basicInfoAction.setViewId(CarrierInfoView.ID);
			basicInfoAction.setGroupId(ConfigureGroupView.ID);
			basicInfoAction.putValue(Action.NAME, "基本信息");
			basicInfoAction.putValue(Action.SMALL_ICON, imageRegistry
					.getImageIcon(MainMenuConstants.BASIC_INFO));
		
			Action testAction = new AbstractAction() {
				@Override
				public void actionPerformed(ActionEvent e) {
					CarrierTestingDialog dialog = new CarrierTestingDialog();
					ClientUtils.centerDialog(dialog);
					dialog.run();
				}
			};
			testAction.putValue(Action.NAME, "节点测试");
			
			ShowViewAction upgradeAction = (ShowViewAction)applicationContext.getBean(ShowViewAction.ID);
			upgradeAction.setViewId(CarrierSystemUpgradeView.ID);
			upgradeAction.setGroupId(ConfigureGroupView.ID);
			upgradeAction.putValue(Action.NAME, "软件升级");
			upgradeAction.putValue(Action.SMALL_ICON, imageRegistry
					.getImageIcon(MainMenuConstants.UPGRADE));
			
			ShowViewAction configureRouteAction = (ShowViewAction)applicationContext.getBean(ShowViewAction.ID);
			configureRouteAction.setViewId(CarrierConfigureRouteView.ID);
			configureRouteAction.setGroupId(ConfigureGroupView.ID);
			configureRouteAction.putValue(Action.NAME, "路由设置");
			configureRouteAction.putValue(Action.SMALL_ICON, imageRegistry
					.getImageIcon(MainMenuConstants.ROUTE_CONFIG));
			
			ShowViewAction configurePortAction = (ShowViewAction)applicationContext.getBean(ShowViewAction.ID);
			configurePortAction.setViewId(CarrierConfigurePortView.ID);
			configurePortAction.setGroupId(ConfigureGroupView.ID);
			configurePortAction.putValue(Action.NAME, "端口设置");
			configurePortAction.putValue(Action.SMALL_ICON, imageRegistry
					.getImageIcon(MainMenuConstants.PORT_CONFIG));
			
			ShowViewAction configureWaveBandAction = (ShowViewAction)applicationContext.getBean(ShowViewAction.ID);
			configureWaveBandAction.setViewId(CarrierConfigureWaveBandView.ID);
			configureWaveBandAction.setGroupId(ConfigureGroupView.ID);
			configureWaveBandAction.putValue(Action.NAME, "波段设置");
			configureWaveBandAction.putValue(Action.SMALL_ICON, imageRegistry
					.getImageIcon(MainMenuConstants.WAVE_BAND_SETUP));

			List<Action> popupActions = new ArrayList<Action>();
			
//			popupActions.add(gisAction);
			popupActions.add(basicInfoAction);
			popupActions.add(testAction);
//			popupActions.add(configureRouteAction);
//			popupActions.add(configurePortAction);
//			popupActions.add(configureWaveBandAction);
//			popupActions.add(upgradeAction);
			
			equipmentFigure.setActions(popupActions);
		}
	}
	
	/** 配置虚拟网元的右键菜单*/
	public void configureVirtualElementFigurePopupActions(NodeFigure virtualFigure) { 
		ShowViewAction basicInfoAction = (ShowViewAction) applicationContext.getBean(ShowViewAction.ID);
		basicInfoAction.setViewId(VirtualNetworkElementManagementView.ID);
		basicInfoAction.setGroupId(ConfigureGroupView.ID);
		basicInfoAction.putValue(Action.NAME, "基本信息");
		basicInfoAction.putValue(Action.SMALL_ICON, imageRegistry.getImageIcon(MainMenuConstants.BASIC_INFO));
		
		Action telnetAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					VirtualNodeEntity selectedVirtualNode = 
						(VirtualNodeEntity)adapterManager.getAdapter(equipmentModel.getLastSelected(), VirtualNodeEntity.class);
					
					if (selectedVirtualNode != null) {
						String selectedAddress = selectedVirtualNode.getIpValue();
						String command = String.format("cmd.exe /c start Telnet.exe %s", selectedAddress);
						Runtime.getRuntime().exec(command);
					}
				} catch (IOException ex) {
					LOG.error("Runtime.exec error", ex);
				}
			}
		};
		telnetAction.putValue(Action.NAME, "Telnet");
		
		
		Action webManagementAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					VirtualNodeEntity selectedVirtualNode = 
						(VirtualNodeEntity)adapterManager.getAdapter(equipmentModel.getLastSelected(), VirtualNodeEntity.class);
					
					if (selectedVirtualNode != null) {
						String selectedAddress = selectedVirtualNode.getIpValue();
						String command = String.format("cmd.exe /c start iexplore.exe http://%s", selectedAddress);
						Runtime.getRuntime().exec(command);
					}
				} catch (IOException ex) {
					LOG.error("Runtime.exec error", ex);
				}
			}
		};
		webManagementAction.putValue(Action.NAME, "Web管理");
		webManagementAction.putValue(Action.SMALL_ICON, imageRegistry
				.getImageIcon(MainMenuConstants.WEB_MANAGEMENT));
		
		Action closeEquipmentAlarmAction = new AbstractAction() {
			@Override	
			public void actionPerformed(ActionEvent e) {
				final VirtualNodeEntity selectedNode = 
					(VirtualNodeEntity)adapterManager.getAdapter(equipmentModel.getLastSelected(), VirtualNodeEntity.class);
				if (selectedNode != null) {
					JProgressBarModel progressBarModel = new JProgressBarModel();
					final SimpleConfigureStrategy strategy = new SimpleConfigureStrategy(StringUtils.EMPTY, progressBarModel);
					JProgressBarDialog dialog = new JProgressBarDialog(ClientUtils.getRootFrame());
					dialog.setOperation("关闭设备告警");
					dialog.setDetermine(true);
					dialog.setModel(progressBarModel);
					dialog.setStrategy(strategy);
					dialog.run(new Task() {
						@Override
						public void run() {
							List<Object> selectedNodeList = new ArrayList<Object>();
							selectedNodeList.add(selectedNode);
							NodeUtils.closeEquipmentAlarmManual(selectedNodeList);
							// 为了显示动画效果
							ThreadUtils.sleep(8);
							strategy.showNormalMessage("关闭完成");
						}
					});
				}
			}
		};
		closeEquipmentAlarmAction.putValue(Action.NAME, "关闭告警");
		
		List<Action> popupActions = new ArrayList<Action>();
		popupActions.add(basicInfoAction);
		popupActions.add(telnetAction);
		popupActions.add(webManagementAction);
		popupActions.add(closeEquipmentAlarmAction);
		virtualFigure.setActions(popupActions);
	}
	
	/** 配置虚拟网元的双击行为*/
	public void configureVirtualElementFigureDoubleClickActions(NodeFigure virtualFigure) {
		ShowViewAction baseInfoAction = (ShowViewAction) applicationContext.getBean(ShowViewAction.ID);
		baseInfoAction.setViewId(VirtualNetworkElementManagementView.ID);
		baseInfoAction.setGroupId(ConfigureGroupView.ID);
		baseInfoAction.putValue(Action.NAME, "基本信息");

		virtualFigure.setDoubleClickAction(baseInfoAction);
	}
	
	/** 配置注释图形的双击行为*/
	public void configureCommentFigureDoubleClickActions(NodeFigure commentFigure) {
		ShowViewAction commentInfoActin = (ShowViewAction) applicationContext.getBean(ShowViewAction.ID);
		commentInfoActin.setViewId(CommentAreaInfoView.ID);
		commentInfoActin.setGroupId(ConfigureGroupView.ID);
		commentInfoActin.putValue(Action.NAME, "注释信息");
		
		commentFigure.setDoubleClickAction(commentInfoActin);
	}
	
	/** 配置注释图形的右键菜单 */
	public void configureCommentFigurePopupActions(NodeFigure equipmentFigure) {
		ShowViewAction commentInfoActin = (ShowViewAction) applicationContext.getBean(ShowViewAction.ID);
		commentInfoActin.setViewId(CommentAreaInfoView.ID);
		commentInfoActin.setGroupId(ConfigureGroupView.ID);
		commentInfoActin.putValue(Action.NAME, "编辑");
		
		List<Action> popupActions = new ArrayList<Action>();
		popupActions.add(commentInfoActin);
		equipmentFigure.setActions(popupActions);
	}
	
	/** 配置子网图形的右键菜单 */
	public void configureSubnetFigurePopupActions(NodeFigure equipmentFigure) {
		Action queryAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SubNetTopoNodeEntity selectedSubnet = 
					(SubNetTopoNodeEntity)adapterManager.getAdapter(equipmentModel.getLastSelected(), SubNetTopoNodeEntity.class);
				equipmentModel.requireSubnet(selectedSubnet.getGuid());
			}
		};
		queryAction.putValue(Action.NAME, "进入子网");
		
		ShowViewAction editAction = (ShowViewAction)applicationContext.getBean(ShowViewAction.ID);
		editAction.setViewId(SubnetPropertyAppendView.ID);
		editAction.setGroupId(ConfigureGroupView.ID);
		editAction.putValue(Action.NAME, "编辑子网");
		editAction.putValue(Action.SMALL_ICON, imageRegistry
				.getImageIcon(MainMenuConstants.SUBNET));
		
		List<Action> popupActions = new ArrayList<Action>();
		popupActions.add(queryAction);
		popupActions.add(editAction);
		equipmentFigure.setActions(popupActions);
	}
	
	/** 配置子网图形的双击行为 */
	public void configureSubnetFigureDoubleClickActions(NodeFigure equipmentFigure) {
		Action queryAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SubNetTopoNodeEntity selectedSubnet = 
					(SubNetTopoNodeEntity)adapterManager.getAdapter(equipmentModel.getLastSelected(), SubNetTopoNodeEntity.class);
				if (selectedSubnet == null) {
					LOG.warn("没有选中子网或者选择了多个节点");
				} else {
					equipmentModel.requireSubnet(selectedSubnet.getGuid());
				}
			}
		};
		queryAction.putValue(Action.NAME, "进入子网");

		equipmentFigure.setDoubleClickAction(queryAction);
	}
		
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Resource
	private ApplicationContext applicationContext;
	
	private static final Logger LOG = LoggerFactory.getLogger(FigureActionBuilder.class);
	public static final String ID = "figureActionBuilder";
	
	private class PingProcessorStrategy implements MessageProcessorStrategy {
		
		public PingProcessorStrategy(JProgressBarModel model) {
			this.model = model;
			messageDispatcher = ClientUtils.getSpringBean(MessageDispatcher.ID);
			equipmentModel = ClientUtils.getSpringBean(EquipmentModel.ID);
			adapterManager = ClientUtils.getSpringBean(DesktopAdapterManager.ID);

			messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, fepOfflineMessage);
			messageDispatcher.addProcessor(MessageNoConstants.PINGRES, pingResultProcessor);
		}
		
		@Override
		public void dealTimeOut() {
		}

		@Override
		public void processorMessage() {
		}

		@Override
		public void removeProcessor() {
			messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, fepOfflineMessage);
			messageDispatcher.removeProcessor(MessageNoConstants.PINGRES, pingResultProcessor);
		}
		

		
		public void showNormalMessage(final String message){
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					model.setDetail(message + "|" + JProgressBarModel.NORMAL);
					model.setProgress(1);
					model.setEnabled(true);
					model.setDetermine(new Boolean(false));
				}
			});
		}
		
		public void showErrorMessage(final String errorMessage){
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					model.setDetail(errorMessage + "|" + JProgressBarModel.FAILURE);
					model.setProgress(1);
					model.setEnabled(true);
					model.setDetermine(new Boolean(false));
				}
			});
		}
		
		private String getStatusText(int status) {
			String text = "还未开始";
			switch (status) {
				case Constants.PINGIN: text = "成功";break;
				case Constants.PINGOUT: text = "失败/超时";break;
			}
			
			return text;
		}
		
		private String getTargetAddress() {
			String targetAddress = StringUtils.EMPTY;
			Object lastSelected = equipmentModel.getLastSelected();

			if (lastSelected instanceof SwitchTopoNodeEntity) {
				SwitchTopoNodeEntity selectedSwitcher = (SwitchTopoNodeEntity) adapterManager.getAdapter(equipmentModel.getLastSelected(), SwitchTopoNodeEntity.class);
				targetAddress = selectedSwitcher.getIpValue();
			} else if (lastSelected instanceof EponTopoEntity) {
				EponTopoEntity selectedOlt = (EponTopoEntity) adapterManager.getAdapter(equipmentModel.getLastSelected(), EponTopoEntity.class);
				targetAddress = selectedOlt.getIpValue();
			} else if (lastSelected instanceof SwitchTopoNodeLevel3) {
				SwitchTopoNodeLevel3 selectedLayer3 = (SwitchTopoNodeLevel3)lastSelected;
				targetAddress = selectedLayer3.getIpValue();
			}
			
			return targetAddress;
		}
		
		private final MessageProcessorAdapter fepOfflineMessage = new MessageProcessorAdapter() {
			@Override
			public void process(TextMessage message) {
				try {
					showErrorMessage(message.getText());
				} catch (JMSException e) {
					LOG.error("message.getText() error", e);
				}
			}
		};
		
		private final MessageProcessorAdapter pingResultProcessor = new MessageProcessorAdapter() {
			@Override
			public void process(ObjectMessage message) {
				try {
					Object messageObject = message.getObject();
					if (messageObject instanceof PingResult) {
						PingResult pingResult = (PingResult)messageObject;
						String address = pingResult.getIpValue();
						
						if (address.equals(getTargetAddress())) {
							if (pingResult.getStatus() == Constants.PINGIN) {
								showNormalMessage(String.format("PING %s %s", address, getStatusText(pingResult.getStatus())));
							}
							if (pingResult.getStatus() == Constants.PINGOUT) {
								showErrorMessage(String.format("PING %s %s", address, getStatusText(pingResult.getStatus())));
							}
						}
					}
				} catch (JMSException e) {
					LOG.error("message.getText() error", e);
				}
			}
		};

		private final JProgressBarModel model;
		private final AdapterManager adapterManager;
		private final EquipmentModel equipmentModel;
		private final MessageDispatcher messageDispatcher;
	}
}