package com.jhw.adm.client.diagram;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.event.FigureSelectionEvent;
import org.jhotdraw.draw.event.FigureSelectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.UncategorizedJmsException;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.Constant;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.draw.EmulationalDrawingView;
import com.jhw.adm.client.draw.EmulationalSelectionTool;
import com.jhw.adm.client.draw.GlassDrawing;
import com.jhw.adm.client.draw.LightEdit;
import com.jhw.adm.client.draw.NetworkDrawing;
import com.jhw.adm.client.draw.NetworkDrawingEditor;
import com.jhw.adm.client.draw.NodeEdit;
import com.jhw.adm.client.draw.PartFigure;
import com.jhw.adm.client.model.AlarmModel;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.JCloseButton;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.client.views.CurrentNodeDataSynchronizationView;
import com.jhw.adm.client.views.EquipmentAlarmConfirmView;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.client.views.switcher.BaseInfoView;
import com.jhw.adm.client.views.switcher.ConfigurePortView;
import com.jhw.adm.client.views.switcher.CurrentNodeSwitcherSystemUpgradeView;
import com.jhw.adm.client.views.switcher.IPAddressView;
import com.jhw.adm.client.views.switcher.LLDPConfigurationView;
import com.jhw.adm.client.views.switcher.PortDetailView;
import com.jhw.adm.client.views.switcher.SerialPortDetailView;
import com.jhw.adm.server.entity.ports.SwitchPortEntity;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.DeviceType;
import com.jhw.adm.server.entity.util.EmulationEntity;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.PortSignal;
import com.jhw.adm.server.entity.util.SwitchSerialPort;
import com.jhw.adm.server.entity.warning.TrapWarningEntity;

/**
 * 交换机仿真视图
 */
@Component(EmulationView.ID)
@Scope(Scopes.DESKTOP)
public class EmulationView extends ViewPart {

	@PostConstruct
	protected void initialize() {
		
		messageSender = remoteServer.getMessageSender();
		
		setTitle("交换机仿真");
		setViewSize(860, 800);
		setLayout(new BorderLayout());
		portAlarms = new ArrayList<TrapWarningEntity>();
		buttonFactory = actionManager.getButtonFactory(this);
		initEmluatorBuilderMap();
		selectedSwitcher = (SwitchTopoNodeEntity)adapterManager.getAdapter(
				equipmentModel.getLastSelected(), SwitchTopoNodeEntity.class);
		
		if (selectedSwitcher != null) {
			selectedSwitcher = NodeUtils.getNodeEntity(selectedSwitcher);
			switchNodeEntity = selectedSwitcher.getNodeEntity();
		}
		
		JPanel topPanel = new JPanel();
		JPanel configPanel = new JPanel(new BorderLayout());
		configDesktop = new JDesktopPane();
		configPanel.add(configDesktop, BorderLayout.CENTER);
		
		JSplitPane splitPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				topPanel, configPanel);
		splitPanel.setDividerLocation(195);

		add(splitPanel, BorderLayout.CENTER);

		createTopPanel(topPanel);
		createConfigDesktop();
//		alarmModel.addPropertyChangeListener(AlarmModel.ALARM_RECEIVED, alarmMessageListener);
		
		closeButton = buttonFactory.createCloseButton();
		setCloseButton(closeButton);
		
		if(emluatorBuilder != null){
			// 监听返回消息并处理
			messageProcessor = new MessageProcessorAdapter() {
				@Override
				public void process(ObjectMessage message) {
					processLightSignalRep(message);
				}
			};
			messageDispatcher.addProcessor(MessageNoConstants.LIGHT_SIGNAL_REP, messageProcessor);		
//			发送获取灯信号的消息
			sendLightSignal();
		}
	}
	
	public void processLightSignalRep(ObjectMessage message) {
		Object messageEntity;
		try {
			messageEntity = message.getObject();
			if (messageEntity instanceof EmulationEntity) {
				EmulationEntity entity = (EmulationEntity) messageEntity;
//				LOG.info("Power Singal:{}", entity.getPowerSingal());
				if(entity.getPowerSingal() != 0){
					//power signal
					LightEdit powerLightEdit = emluatorBuilder.getWorkSingalMap().get(Constant.POWER);
					if(null != powerLightEdit){
						powerLightEdit.turnOn();
					}
					//run signal
					LightEdit runLightEdit = emluatorBuilder.getDataSingalMap().get(Constant.RUN);
					if(null != runLightEdit){
						runLightEdit.flash();
					}
					//alarm signal
					LightEdit alarmLightEdit = emluatorBuilder.getWorkSingalMap().get(Constant.ALAEM);
					if(null != alarmLightEdit){
						LOG.info("收到消息：AlarmSignal:{}", entity.getAlarmSignal());
						if(entity.getAlarmSignal() != 0){
							alarmLightEdit.changeColor(Color.RED);
							alarmLightEdit.turnOn();
						}else{
							alarmLightEdit.turnOff();
						}
					}
					List<PortSignal> portSignalList = new ArrayList<PortSignal>();
					for (PortSignal portSignal : entity.getPortSignals()) {
						portSignalList.add(portSignal);
					}
					for (int i = 0; i < portSignalList.size(); i++) {
						PortSignal portSignal = portSignalList.get(i);
//						LOG.info("Data Singal:{}" , portSignal.getDataSingal());
//						LOG.info("Working Signal:{}" , portSignal.getWorkingSignal());
						LightEdit dataLightEdit = emluatorBuilder.getDataSingalMap().get((int)portSignal.getPortNo());
						LightEdit workLightEdit = emluatorBuilder.getWorkSingalMap().get((int)portSignal.getPortNo());
						//work signal
						if(portSignal.getWorkingSignal() != 0){
							//data signal
							if(null != dataLightEdit){
								if(portSignal.getDataSingal() != 0){
									dataLightEdit.flash();
								}
							}
							if(null != workLightEdit){
								workLightEdit.turnOn();
							}
						}else {
							if(null != workLightEdit){
								workLightEdit.turnOff();
							}
						}
					}
				}else{
					for (LightEdit lightEdit : emluatorBuilder.getWorkSingalMap().values()) {
						lightEdit.turnOff();
					}
				}
//				LOG.info("收到消息：Entity from server", entity);
			}
		} catch (JMSException e) {
			LOG.error("processLightSignalRep() error", e);
		}
	}
	Thread sendThread;
	public void sendLightSignal(){
		sendThread = new Thread(new sendLightSignalThread());
		sendThread.start();
	}
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	private MessageSender messageSender;
	private MessageProcessorAdapter messageProcessor;
	private static final int TIME = 6500;
	private boolean isRunning = false;
	private class sendLightSignalThread implements Runnable {
		
		public sendLightSignalThread(){
			isRunning = true;
		}
		
		@Override
		public void run() {
			while (isRunning) {
				MessageCreator messageCreator = new MessageCreator() {
					@Override
					public Message createMessage(Session session)
							throws JMSException {
						TextMessage message = session.createTextMessage();
						message.setIntProperty(Constants.MESSAGETYPE,MessageNoConstants.LIGHT_SIGNAL);
						message.setIntProperty(Constants.DEVTYPE, Constants.DEV_SWITCHER2);
						message.setStringProperty(Constants.MESSAGETO,switchNodeEntity.getBaseConfig().getIpValue());
						message.setStringProperty(Constants.MESSAGEFROM,clientModel.getCurrentUser().getUserName());
						message.setStringProperty(Constants.CLIENTIP,clientModel.getLocalAddress());
						return message;
					}
				};
				try {
					messageSender.send(messageCreator);
					Thread.sleep(TIME);
				}catch (UncategorizedJmsException jms){
					LOG.error("error occur when close view during sending light signal",jms);
//					LOG.error("Close view during sending light signal");
					if(sendThread.isAlive()){
						isRunning = false;
//						sendThread.interrupt();
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					LOG.error("Send light signal error",e);
				}
			}
		}
	}
	
	private void createTopPanel(JPanel parent) {
		parent.setLayout(new BorderLayout());
		
		drawing = new NetworkDrawing();
		GlassDrawing glassDrawing = new GlassDrawing();
		drawingView = new EmulationalDrawingView();
		drawingView.setDrawing(drawing);
		drawingView.setGlassDrawing(glassDrawing);

		drawingEditor = new NetworkDrawingEditor();
		EmulationalSelectionTool selectionTool = new EmulationalSelectionTool();
		drawingEditor.setTool(selectionTool);
		drawingEditor.add(drawingView);
		drawingEditor.setActiveView(drawingView);

		parent.add(createMenuBar(), BorderLayout.PAGE_START);
		parent.add(drawingView.getComponent(), BorderLayout.CENTER);
		
		if (switchNodeEntity == null) {
			return;
		}

		emluatorBuilder = createEmluatorBuilder(switchNodeEntity.getDeviceModel());
		if (emluatorBuilder == null) {
			return;
		}
		emluatorBuilder.buildPorts(drawing, switchNodeEntity);
		emluatorBuilder.buildLights(glassDrawing, switchNodeEntity);

		drawingView.addFigureSelectionListener(new FigureSelectionListener() {
			@Override
			public void selectionChanged(FigureSelectionEvent event) {
				Set<Figure> setOfFigure = event.getNewSelection();
				for (Figure selected : setOfFigure) {
					Figure selectedFigure = selected;
					figureSelectionChanged(selectedFigure);
				}
			}
		});
	}
	
	private void initEmluatorBuilderMap() {
		emluatorBuilderMap = new HashMap<Integer, EmluatorBuilder>();
		emluatorBuilderMap.put(
				DeviceType.IETH802,
				(EmluatorBuilder)applicationContext.getBean(IETH802EmluatorBuilder.ID));
		emluatorBuilderMap.put(DeviceType.IETH804_H,
				(EmluatorBuilder) applicationContext
						.getBean(IETH804HEmluatorBuilder.ID));
		emluatorBuilderMap.put(DeviceType.IETH8008,
				(EmluatorBuilder) applicationContext
						.getBean(IETH8008HEmluatorBuilder.ID));
		emluatorBuilderMap.put(DeviceType.IETH8008_U,
				(EmluatorBuilder) applicationContext
						.getBean(IETH8008UEmluatorBuilder.ID));
		emluatorBuilderMap.put(DeviceType.IETH9307,
				(EmluatorBuilder) applicationContext
						.getBean(IETH9307EmluatorBuilder.ID));
	}
	
	private EmluatorBuilder createEmluatorBuilder(int modelNumber) {	
		EmluatorBuilder emluatorBuilder = null;
		
		if (emluatorBuilderMap.containsKey(modelNumber)) {
			emluatorBuilder = emluatorBuilderMap.get(modelNumber);
		}
		return emluatorBuilder;
	}
	
	private void figureSelectionChanged(Figure selectedFigure) {
		if (selectedFigure instanceof PartFigure) {
			NodeEdit<?> nodeEdit = ((PartFigure)selectedFigure).getEdit();
			
			if (nodeEdit == null) {
				equipmentModel.changeSelected(selectedSwitcher);
				showView(BaseInfoView.ID);
			} else {
				Object figureModel = ((PartFigure)selectedFigure).getEdit().getModel();
				if (figureModel instanceof SwitchPortEntity) {
					if (lastViewPart.getBeanName().equals(PortDetailView.ID)){
						//
					} else {
						showView(PortDetailView.ID);
					}
				}else if(figureModel instanceof SwitchSerialPort) {
					if (lastViewPart.getBeanName().equals(SerialPortDetailView.ID)) {

					} else {
						showView(SerialPortDetailView.ID);
					}
				}
				equipmentModel.changeSelected(figureModel);
			}
		}
	}
	
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu basicMenu = new JMenu("基本");
		JMenu portMenu = new JMenu("端口");
		JMenu otherMenu = new JMenu("其它");
		
		basicMenu.add(buildMenuItem("基本信息", BaseInfoView.ID));
//		basicMenu.add(buildMenuItem("MAC配置", MACConfigurationView.ID));
//		basicMenu.add(buildMenuItem("STP配置", STPConfigurationView.ID));
//		basicMenu.add(buildMenuItem("SNTP配置", SNTPConfigurationView.ID));
//		basicMenu.add(buildMenuItem("IGMP配置", IGMPConfigurationView.ID));
		basicMenu.add(buildMenuItem("LLDP管理", LLDPConfigurationView.ID));
//		basicMenu.add(buildMenuItem("QOS配置", QOSConfigurationView.ID));
		basicMenu.add(buildMenuItem("告警列表", EquipmentAlarmConfirmView.ID));
		
//		if(!this.switchNodeEntity.getType().equals(IETH8008HEmluatorBuilder.EMULATION_TYPE)){//8008无串口配置
//			portMenu.add(buildMenuItem("串口配置", SerialPortConfigView.ID));
//		}
		portMenu.add(buildMenuItem("端口配置", ConfigurePortView.ID));
//		portMenu.add(buildMenuItem("LACP端口聚合", LacpPortAggrView.ID));
//		portMenu.add(buildMenuItem("Mirror端口镜像", MirrorPortView.ID));
//		portMenu.add(buildMenuItem("Trunk端口聚合", TrunkPortAggrView.ID));
		
		otherMenu.add(buildMenuItem("软件升级", CurrentNodeSwitcherSystemUpgradeView.ID));
//		otherMenu.add(buildMenuItem("用户设置", ConfigureSwitcherUserView.ID));
		otherMenu.add(buildMenuItem("IP配置", IPAddressView.ID));
		otherMenu.add(buildMenuItem("数据上载",CurrentNodeDataSynchronizationView.ID));
		
		menuBar.add(basicMenu);
		menuBar.add(portMenu);
		menuBar.add(otherMenu);
		
		return menuBar;
	}
	
	private JMenuItem buildMenuItem(String name, String viewId) {
		JMenuItem menuItem = new JMenuItem();
		ShowConfigViewAction showViewAction = new ShowConfigViewAction(viewId);
		showViewAction.putValue(Action.NAME, name);
		menuItem.setAction(showViewAction);
		
		return menuItem;
	}
	
	private void createConfigDesktop() {
		configDesktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		showView(BaseInfoView.ID);
	}
	
	private void showView(String viewId) {
		if (lastFrame != null) {
			lastFrame.close();
		}
		
		if (lastViewPart != null) {
			//
		}
		
		if (applicationContext.containsBean(viewId)) {
			ViewPart viewPart = (ViewPart)applicationContext.getBean(viewId);
			UndecoratedFrame internalFrame = new UndecoratedFrame();
			configDesktop.add(internalFrame, 1);
			internalFrame.addView(viewPart);
			try {
				internalFrame.setSelected(true);
				internalFrame.setMaximum(true);
			} catch (java.beans.PropertyVetoException ex) {
				LOG.error("showView error", ex);
			}
			for (JButton button : viewPart.getCloseButtons()) {
				button.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						closeButton.fireActionEvent();
					}					
				});
			}
			internalFrame.show();
			lastViewPart = viewPart;
			lastFrame = internalFrame;
			String subTitle = viewPart.getTitle();
			this.setTitle("交换机仿真" + "(" + subTitle + ")" );
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		messageDispatcher.removeProcessor(MessageNoConstants.LIGHT_SIGNAL_REP, messageProcessor);
//		alarmModel.removePropertyChangeListener(AlarmModel.ALARM_RECEIVED, alarmMessageListener);
		if(null != sendThread){
			if(sendThread.isAlive()){
				isRunning = false;
			}
		}
		if (lastViewPart != null) {
			lastViewPart.close();
		}
	}
	
//	private final PropertyChangeListener alarmMessageListener = new PropertyChangeListener(){
//		@Override
//		public void propertyChange(PropertyChangeEvent evt) {
//			WarningEntity alarmMessage = (WarningEntity) evt.getNewValue();
//			processorAlarmMessage(alarmMessage);
//		}
//	};
	
//	private void processorAlarmMessage(WarningEntity alarmMessage){
//		int portNumber = 0;
//		portNumber = alarmMessage.getPortNo();
//		//仅仅变颜色,工作灯以及数据灯
//		if(portNumber > 0){
//			LightEdit dataEdit = emluatorBuilder.getDataSingalMap().get(portNumber);
//			LightEdit workEdit = emluatorBuilder.getWorkSingalMap().get(portNumber);
//			dataEdit.changeColor(Color.RED);
//			workEdit.changeColor(Color.RED);
//		}
//	}
	private List<TrapWarningEntity> portAlarms;
//	private Map<Integer, PartFigure> figurePortIndex;	
	private SwitchTopoNodeEntity selectedSwitcher;
	private SwitchNodeEntity switchNodeEntity;
	private UndecoratedFrame lastFrame;
	private ViewPart lastViewPart;
	private JDesktopPane configDesktop;
	private NetworkDrawing drawing;
	private EmulationalDrawingView drawingView;
	private NetworkDrawingEditor drawingEditor;
	private static final int NINE = 9;
	private static final int TEN = 10;

	private ButtonFactory buttonFactory;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=AlarmModel.ID)
	private AlarmModel alarmModel;
	
	@Resource
	private ApplicationContext applicationContext;

	private Map<Integer, EmluatorBuilder> emluatorBuilderMap;
	private EmluatorBuilder emluatorBuilder;
	private JCloseButton closeButton;
	private static final Logger LOG = LoggerFactory.getLogger(EmulationView.class);
	private static final long serialVersionUID = 1L;
	public static final String ID = "emulationView";
	
	private class ShowConfigViewAction extends AbstractAction {
		
		public ShowConfigViewAction(String viewId) {
			this.viewId = viewId;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			showView(viewId);
		}
		
		private final String viewId;
		private static final long serialVersionUID = 5982915305616603364L;
	}
}

/**
 * 无标题栏的窗口
 */
class UndecoratedFrame extends JInternalFrame {

	public UndecoratedFrame() {
		listOfViewPart = new ArrayList<ViewPart>();
		oldUi = (BasicInternalFrameUI) getUI();
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				UndecoratedFrame selectedFrame = (UndecoratedFrame) e.getSource();
				if (selectedFrame.isMaximum()) {
					selectedFrame.hideNorthPanel();
					try {
						selectedFrame.setMaximum(true);
					} catch (PropertyVetoException ex) {
						LOG.error("UndecoratedFrame.setMaximum(true) error", ex);
					}
				}
			}
		});
	}
	
	public void addView(ViewPart viewPart) {
		listOfViewPart.add(viewPart);
		add(viewPart);
	}
	
	/**
	 * 关闭窗口
	 */
	public void close() {
		fireInternalFrameEvent(InternalFrameEvent.INTERNAL_FRAME_CLOSED);
		for (ViewPart viewPart : listOfViewPart) {
			viewPart.close();
		}
	}
	
	private void hideNorthPanel() {
		((BasicInternalFrameUI) this.getUI()).setNorthPane(null);
		putClientProperty("JInternalFrame.isPalette", Boolean.TRUE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JInternalFrame#updateUI()
	 */
	@Override
	public void updateUI() {
		super.updateUI();
	}
	private final List<ViewPart> listOfViewPart;
	private static final Logger LOG = LoggerFactory.getLogger(UndecoratedFrame.class);
	private BasicInternalFrameUI oldUi = null;
	private static final long serialVersionUID = 1L;
}