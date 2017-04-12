package com.jhw.adm.client.diagram;

import java.awt.BorderLayout;
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
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.JCloseButton;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.client.views.epon.OLTBaseInfoView;
import com.jhw.adm.client.views.epon.OLTChipInformationView;
import com.jhw.adm.client.views.epon.OLTDBAInformationView;
import com.jhw.adm.client.views.epon.OLTMulticastView;
import com.jhw.adm.client.views.epon.OLTPortDetailView;
import com.jhw.adm.client.views.epon.OLTSTPConfigurationView;
import com.jhw.adm.client.views.epon.OLTSlotInformateionView;
import com.jhw.adm.client.views.epon.OLTVlanConfigView;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.epon.OLTPort;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.EmulationEntity;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.PortSignal;

@Component(EponEmulationView.ID)
@Scope(Scopes.DESKTOP)
public class EponEmulationView extends ViewPart {

	private static final long serialVersionUID = 1L;
	public static final String ID = "eponEmulationView";

	@Resource(name = RemoteServer.ID)
	private RemoteServer remoteServer;

	@Resource(name = ActionManager.ID)
	private ActionManager actionManager;

	@Resource(name = DesktopAdapterManager.ID)
	private AdapterManager adapterManager;

	@Resource(name = EquipmentModel.ID)
	private EquipmentModel equipmentModel;

	@Resource(name = ClientModel.ID)
	private ClientModel clientModel;

	@Resource
	private ApplicationContext applicationContext;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	private MessageSender messageSender;

	private ButtonFactory buttonFactory;
	private EponEmluatorBuilder eponEmluatorBuilder;
	private Map<String, EponEmluatorBuilder> emluatorBuilderMap;
	private EponTopoEntity selectedEpon;
	private OLTEntity eponEntity;
	private JDesktopPane configDesktop;

	private NetworkDrawing drawing;
	private EmulationalDrawingView drawingView;
	private NetworkDrawingEditor drawingEditor;

	private EponUndecoratedFrame lastFrame;
	private ViewPart lastViewPart;
	private Thread sendThread;
	private boolean isRunning = false;
	
	private static final int ONE = 1;
	private static final int TWO = 2;
	private static final int THREE = 3;

	private JCloseButton closeButton;
	private static final Logger LOG = LoggerFactory.getLogger(EponEmulationView.class);

	@PostConstruct
	public void initialize() {

		setTitle("OLT仿真");
		setViewSize(625, 770);
		setLayout(new BorderLayout());

		buttonFactory = actionManager.getButtonFactory(this);
		initEmluatorBuilderMap();

		selectedEpon = (EponTopoEntity) adapterManager.getAdapter(
				equipmentModel.getLastSelected(), EponTopoEntity.class);
		if (selectedEpon != null) {
			eponEntity = remoteServer.getService().getOLTByIpValue(selectedEpon.getIpValue());
		}

		JPanel topoPanel = new JPanel();
		JPanel configPanel = new JPanel(new BorderLayout());

		configDesktop = new JDesktopPane();
		configPanel.add(configDesktop, BorderLayout.CENTER);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				topoPanel, configPanel);
		splitPane.setDividerLocation(175);
		// splitPane.setDividerLocation(155);

		add(splitPane, BorderLayout.CENTER);

		createTopPanel(topoPanel);
		createConfigDesktop();

		closeButton = buttonFactory.createCloseButton();
		setCloseButton(closeButton);
		messageSender = remoteServer.getMessageSender();
		messageDispatcher.addProcessor(MessageNoConstants.LIGHT_SIGNAL_REP, messageProcessor);
		sendThread = new Thread(new sendLightSignalThread());
		sendThread.start();
	}
	
	@Override
	public void dispose() {
		messageDispatcher.removeProcessor(MessageNoConstants.LIGHT_SIGNAL_REP, messageProcessor);
		if(sendThread.isAlive()){
			isRunning = false;
		}
		if (lastViewPart != null) {
			lastViewPart.close();
		}
	}

	private void createConfigDesktop() {

		configDesktop.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		showView(OLTBaseInfoView.ID);

	}

	private void showView(String viewId) {

		if (lastFrame != null) {
			lastFrame.close();
		}

		if (applicationContext.containsBean(viewId)) {

			ViewPart viewPart = (ViewPart) applicationContext.getBean(viewId);
			EponUndecoratedFrame internalFrame = new EponUndecoratedFrame();
			configDesktop.add(internalFrame, 1);
			internalFrame.addView(viewPart);
			try {
				internalFrame.setSelected(true);
				internalFrame.setMaximum(true);
			} catch (PropertyVetoException e) {
				LOG.error("showView error in EponEmulationView", e);
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
			this.setTitle("OLT仿真" + "(" + subTitle + ")");
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

		if (eponEntity == null || eponEntity.getOltBaseInfo() == null)
			return;

//		eponEmluatorBuilder = createEmluatorBuilder(eponEntity.getOltBaseInfo()
//				.getDeviceType());
		eponEmluatorBuilder = createEmluatorBuilder("OLT");
		if (eponEmluatorBuilder == null)
			return;
		eponEmluatorBuilder.buildPorts(drawing, eponEntity);
		eponEmluatorBuilder.buildLights(glassDrawing, eponEntity);

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

	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		JMenu basicMenu = new JMenu("基本");

		basicMenu.add(buildMenuItem("OLT基本信息", OLTBaseInfoView.ID));
		basicMenu.add(buildMenuItem("OLT槽位信息", OLTSlotInformateionView.ID));
		basicMenu.add(buildMenuItem("OLT芯片信息", OLTChipInformationView.ID));
		basicMenu.add(buildMenuItem("OLT组播配置", OLTMulticastView.ID));
		basicMenu.add(buildMenuItem("OLT VLAN配置", OLTVlanConfigView.ID));
		basicMenu.add(buildMenuItem("OLT DBA配置", OLTDBAInformationView.ID));
		basicMenu.add(buildMenuItem("OLT STP配置", OLTSTPConfigurationView.ID));

		menuBar.add(basicMenu);
		return menuBar;
	}

	private JMenuItem buildMenuItem(String name, String viewId) {
		JMenuItem menuItem = new JMenuItem();
		ShowConfigViewAction showViewAction = new ShowConfigViewAction(viewId);
		showViewAction.putValue(Action.NAME, name);
		menuItem.setAction(showViewAction);
		return menuItem;
	}

	private EponEmluatorBuilder createEmluatorBuilder(String type) {
		EponEmluatorBuilder emluatorBuilder = null;
		if (emluatorBuilderMap.containsKey(type)) {
			emluatorBuilder = emluatorBuilderMap.get(type);
		}
		return emluatorBuilder;
	}

	private void initEmluatorBuilderMap() {

		emluatorBuilderMap = new HashMap<String, EponEmluatorBuilder>();
		emluatorBuilderMap.put(OLTEmluatorBuilder.EMULATION_TYPE,
				(EponEmluatorBuilder) applicationContext
						.getBean(OLTEmluatorBuilder.ID));
	}
	
	public void processLightSignal(EmulationEntity entity) {
		if(entity.getPowerSingal() != 0){
			//power signal
			LightEdit powerLightEdit = eponEmluatorBuilder.getWorkSingalMap().get(THREE);
			powerLightEdit.turnOn();
			//Sys signal
			LightEdit sysLightEdit = eponEmluatorBuilder.getWorkSingalMap().get(TWO);
			sysLightEdit.flash();
			
			List<PortSignal> portSignalList = new ArrayList<PortSignal>();
			for (PortSignal portSignal : entity.getPortSignals()) {
				portSignalList.add(portSignal);
			}
			for (int i = 0; i < portSignalList.size(); i++) {
				PortSignal portSignal = portSignalList.get(i);
				LOG.info(String.format("Port[%s] work[%s] data[%s]", 
						portSignal.getPortNo(), portSignal.getWorkingSignal(), portSignal.getDataSingal()));
				
				if (portSignal.getPortType() == Constants.TX) {//电口
					if(portSignal.getDataSingal() != 0){
						if(portSignal.getPortName() == Constants.GIGA_PORT){//千兆电口G1~G4
							LightEdit gigaDataLightEdit = eponEmluatorBuilder
									.getDataSingalMap().get((int) portSignal.getPortNo());
							gigaDataLightEdit.flash();
						}else if(portSignal.getPortName() == Constants.FAST_PORT){//百兆电口ETH
							LightEdit fastDataLightEdit = eponEmluatorBuilder
									.getUpDataSingalMap().get((int) portSignal.getPortNo());
							fastDataLightEdit.flash();
						}
					}
				}else if (portSignal.getPortType() == Constants.PX) {//pon口
					LightEdit ponWorkLightEdit = eponEmluatorBuilder.getWorkSingalMap().get(ONE);
					LightEdit ponDataLightEdit = eponEmluatorBuilder
							.getDownDataSingalMap().get((int) portSignal.getPortNo());
					if(portSignal.getWorkingSignal() != 0){
						if(portSignal.getDataSingal() != 0){
							ponDataLightEdit.flash();
						}
						if(ponWorkLightEdit != null){
							ponWorkLightEdit.turnOn();
						}
					}else{
						if(ponWorkLightEdit != null){
							ponWorkLightEdit.turnOff();
						}
					}
				}
			}
		} else {
			for (LightEdit lightEdit : eponEmluatorBuilder.getWorkSingalMap().values()) {
				lightEdit.turnOff();
			}
		}
	} 

	private void figureSelectionChanged(Figure selectedFigure) {
		if (selectedFigure instanceof PartFigure) {
			NodeEdit<?> nodeEdit = ((PartFigure) selectedFigure).getEdit();

			if (nodeEdit == null) {
				equipmentModel.changeSelected(selectedEpon);
				showView(OLTBaseInfoView.ID);
			} else {
				Object figureModel = ((PartFigure) selectedFigure).getEdit()
						.getModel();
				if (figureModel instanceof OLTPort) {
					if (lastViewPart.getBeanName().equals(OLTPortDetailView.ID)) {
						//
					} else {
						showView(OLTPortDetailView.ID);
					}
				}
				equipmentModel.changeSelected(figureModel);
			}
		}
	}
	
	private final MessageProcessorAdapter messageProcessor = new MessageProcessorAdapter() {
		@Override
		public void process(ObjectMessage message) {
			try {
				Object messageEntity = message.getObject();
				if (messageEntity instanceof EmulationEntity) {
					EmulationEntity entity = (EmulationEntity)messageEntity;					
					processLightSignal(entity);
				}
			} catch (JMSException e) {
				LOG.error("message.getObject() error", e);
			}
		}
	};
	
	
	private class sendLightSignalThread implements Runnable {

		public sendLightSignalThread() {
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
						message.setIntProperty(Constants.MESSAGETYPE,
								MessageNoConstants.LIGHT_SIGNAL);
						message.setIntProperty(Constants.DEVTYPE,
								Constants.DEV_OLT);
						message.setStringProperty(Constants.MESSAGETO,
								eponEntity.getIpValue());
						message.setStringProperty(Constants.MESSAGEFROM,
								clientModel.getCurrentUser().getUserName());
						message.setStringProperty(Constants.CLIENTIP,
								clientModel.getLocalAddress());
						return message;
					}
				};
				try {
					messageSender.send(messageCreator);
					Thread.sleep(TIME);
				} catch (UncategorizedJmsException jms) {
					Thread.currentThread().interrupt();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					LOG.error("Send light signal error", e);
				}
			}
		}

		private static final int TIME = 6500;
	}

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
class EponUndecoratedFrame extends JInternalFrame {

	public EponUndecoratedFrame() {
		listOfViewPart = new ArrayList<ViewPart>();
		oldUi = (BasicInternalFrameUI) getUI();
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				EponUndecoratedFrame selectedFrame = (EponUndecoratedFrame) e
						.getSource();
				if (selectedFrame.isMaximum()) {
					selectedFrame.hideNorthPanel();
					try {
						selectedFrame.setMaximum(true);
					} catch (PropertyVetoException ex) {
						LOG.error(
								"EponUndecoratedFrame.setMaximum(true) error",
								ex);
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
	private static final Logger LOG = LoggerFactory.getLogger(EponUndecoratedFrame.class);
	private BasicInternalFrameUI oldUi = null;
	private static final long serialVersionUID = 1L;
}
