package com.jhw.adm.client.diagram;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.border.IconBorder;
import org.jhotdraw.app.action.edit.SelectAllAction;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.action.ZoomEditorAction;
import org.jhotdraw.draw.event.CompositeFigureEvent;
import org.jhotdraw.draw.event.CompositeFigureListener;
import org.jhotdraw.draw.event.FigureAdapter;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.event.FigureListener;
import org.jhotdraw.draw.event.FigureSelectionEvent;
import org.jhotdraw.draw.event.FigureSelectionListener;
import org.jhotdraw.draw.event.ToolAdapter;
import org.jhotdraw.draw.event.ToolEvent;
import org.jhotdraw.draw.event.ToolListener;
import org.jhotdraw.draw.tool.Tool;
import org.jhotdraw.util.Images;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ShowViewAction;
import com.jhw.adm.client.core.ApplicationConstants;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.Lockable;
import com.jhw.adm.client.core.MainMenuConstants;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.draw.CommentAreaFigure;
import com.jhw.adm.client.draw.DeleteFigureAllAction;
import com.jhw.adm.client.draw.DeletionStrategy;
import com.jhw.adm.client.draw.EquipmentCreationTool;
import com.jhw.adm.client.draw.EquipmentFigure;
import com.jhw.adm.client.draw.LabeledLinkFigure;
import com.jhw.adm.client.draw.LinkCreationTool;
import com.jhw.adm.client.draw.LockSelectionTool;
import com.jhw.adm.client.draw.NetworkConstrainer;
import com.jhw.adm.client.draw.NetworkDrawing;
import com.jhw.adm.client.draw.NetworkDrawingEditor;
import com.jhw.adm.client.draw.NetworkDrawingView;
import com.jhw.adm.client.draw.NodeFigure;
import com.jhw.adm.client.draw.PopupActionFilter;
import com.jhw.adm.client.draw.PopupSelectionTool;
import com.jhw.adm.client.map.GISView;
import com.jhw.adm.client.model.AlarmModel;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.EquipmentRepository;
import com.jhw.adm.client.swing.BlockingDialog;
import com.jhw.adm.client.swing.JAlarmButton;
import com.jhw.adm.client.swing.JAlarmButtonModel;
import com.jhw.adm.client.swing.JCommonAlarmButtonModel;
import com.jhw.adm.client.swing.JNoticeAlarmButtonModel;
import com.jhw.adm.client.swing.JPopupButton;
import com.jhw.adm.client.swing.JSeriousAlarmButtonModel;
import com.jhw.adm.client.swing.JUrgentAlarmButtonModel;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.client.util.ThreadUtils;
import com.jhw.adm.client.views.CommonAlarmConfirmView;
import com.jhw.adm.client.views.FormGroupView;
import com.jhw.adm.client.views.NoticeAlarmConfirmView;
import com.jhw.adm.client.views.SeriousAlarmConfirmView;
import com.jhw.adm.client.views.TopologyGroupView;
import com.jhw.adm.client.views.UrgentAlarmConfirmView;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.tuopos.CarrierTopNodeEntity;
import com.jhw.adm.server.entity.tuopos.FEPTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.GPRSTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.SubNetTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.TopDiagramEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.GPRSEntity;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.PingResult;
import com.jhw.adm.server.entity.warning.NoteEntity;
import com.jhw.adm.server.entity.warning.RmonEntity;
import com.jhw.adm.server.entity.warning.WarningEntity;

/**
 * 网络拓扑视图
 */
@Component(DiagramView.ID)
public class DiagramView extends ViewPart implements Lockable {
	
	public DiagramView() {
		singleThreadExecutor = Executors.newSingleThreadExecutor(ThreadUtils.createThreadFactory(DiagramView.class.getSimpleName()));
	}

	@PostConstruct
	protected void initialize() {
		
		urgentModel = urgentAlarmButtonModel;
		seriousModel = seriousAlarmButtonModel;
		noticeModel = noticeAlarmButtonModel;
		commonModel = commonAlarmButtonModel;
		
		setLayout(new BorderLayout());
		JPanel topPanel = new JPanel(new BorderLayout());
		JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 0));
		JPanel warnPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 5, 0));
		
		topPanel.add(toolPanel, BorderLayout.CENTER);
		topPanel.add(warnPanel, BorderLayout.EAST);
				
		add(topPanel, BorderLayout.NORTH);

		final JToolBar drawingBar = new JToolBar();
		final JToolBar toolBar = new JToolBar();
		final JToolBar fileBar = new JToolBar();
		final JToolBar searchBar = new JToolBar();
		final JToolBar alarmBar = new JToolBar();
		drawingBar.setFloatable(false);
		toolBar.setFloatable(false);
		fileBar.setFloatable(false);
		searchBar.setFloatable(false);
		alarmBar.setFloatable(false);
				
		drawingBar.setRollover(true);
		toolBar.setRollover(true);
		fileBar.setRollover(true);
		searchBar.setRollover(true);
		alarmBar.setRollover(true);

		blockingDialog = new BlockingDialog();
		toolButtonGroup = new ButtonGroup();

		drawing = new NetworkDrawing();
		drawingAdapter = new DrawingAdapter(drawing);
		Image backgroundImage = imageRegistry.getImageIcon(
				ApplicationConstants.BACKGROUND).getImage();
		drawingView = new NetworkDrawingView();
		drawingView.setInvisibleConstrainer(new NetworkConstrainer());
		drawingView.setBackgroundImage(backgroundImage);
		drawingView.setDrawing(drawing);

		deletionStrategy = new DefaultDeletionStrategy(drawingView);
		
		JScrollPane scrollPane = new JScrollPane(drawingView.getComponent());
		scrollPane.setAutoscrolls(true);
		scrollPane.getHorizontalScrollBar().setUnitIncrement(30);
		scrollPane.getVerticalScrollBar().setUnitIncrement(30);
		scrollPane.setPreferredSize(new Dimension(1280, 1024));
		add(scrollPane, BorderLayout.CENTER);

		drawingEditor = new NetworkDrawingEditor();
		drawingEditor.add(drawingView);
		drawingEditor.setActiveView(drawingView);
		drawingEditor.setDeletionStrategy(deletionStrategy);
		// 系统管理员才能删除网元
        if (clientModel.getCurrentUser().getRole().getRoleCode() > Constants.ADMINCODE) {
        	drawingEditor.removeDeletion();
		}
		editorActionMap = drawingEditor.getActionMap();

		addDrawingBarButton(drawingBar, drawingEditor);
		addToolBarButton(toolBar, drawingEditor);
		addFileBarButton(fileBar, drawingEditor);
		
		final JButton searchButton = new JButton(imageRegistry
				.getImageIcon(NetworkConstants.SEARCH));
		searchButton.setToolTipText("快速定位");
		final JTextField searchField = new JTextField(15);
		searchField.setBorder(BorderFactory.createCompoundBorder(searchField.getBorder(), 
				new IconBorder(imageRegistry.getImageIcon(
				NetworkConstants.QUERY), SwingConstants.LEADING)));
		Font font = new Font("serif", Font.PLAIN, 14);
		searchField.setFont(font);
		searchBar.add(searchField);
		searchBar.add(searchButton);
		
		searchField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String key = searchField.getText();
					selectFigureByAddress(key);
				}
			}
		});
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String key = searchField.getText();
				selectFigureByAddress(key);
			}
		});
		
		addAlarmPanel(alarmBar, drawingEditor);

		int preferredHeight = searchField.getPreferredSize().height - 4;
		int preferredWidth = 5;
		JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
		separator.setPreferredSize(new Dimension(preferredWidth,
				preferredHeight));
		toolPanel.add(drawingBar);
		separator = new JSeparator(SwingConstants.VERTICAL);
		separator.setPreferredSize(new Dimension(preferredWidth,
				preferredHeight));
		toolPanel.add(toolBar);
		separator = new JSeparator(SwingConstants.VERTICAL);
		separator.setPreferredSize(new Dimension(preferredWidth,
				preferredHeight));
		toolPanel.add(fileBar);
		separator = new JSeparator(SwingConstants.VERTICAL);
		separator.setPreferredSize(new Dimension(preferredWidth,
				preferredHeight));
		toolPanel.add(searchBar);
		
		separator = new JSeparator(SwingConstants.VERTICAL);
		separator.setPreferredSize(new Dimension(preferredWidth,
				preferredHeight));
		warnPanel.add(alarmBar);
		
		messageDispatcher.addProcessor(MessageNoConstants.TOPOFINISH, finishMessageProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.GPRSMESSAGE, gprsMessageProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.CARRIERMONITORREP, carrierMessageProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.FEP_STATUSTYPE, frontEndMessageProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.PINGRES, pingMessageProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.WARNINGLINK, linkChangedMessageProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.WARNINGNODE, nodeChangedMessageProcessor);	
		
		clientModel.addPropertyChangeListener(ClientModel.TIMEOUNT, disconnectOrTimeoutListener);
		clientModel.addPropertyChangeListener(ClientModel.DISCONNECT, disconnectOrTimeoutListener);
		
		alarmModel.addPropertyChangeListener(AlarmModel.ALARM_RECEIVED, AlarmReceivedListener);
		
		equipmentModel.addObserver(EquipmentModelObserver);
		equipmentModel.addPropertyChangeListener(EquipmentModel.EQUIPMENT_UPDATED, EquipmentUpdatedListener);
		equipmentModel.addPropertyChangeListener(EquipmentModel.SUBNET_ENTER, subnetEnterListener);
		equipmentModel.addPropertyChangeListener(EquipmentModel.REQUIRE_REFRESH, requireRefreshListener);
		equipmentModel.addPropertyChangeListener(EquipmentModel.REQUIRE_SUBNET, requireSubnetListener);
		
		drawingView.addFigureSelectionListener(new FigureSelectionListener() {
			@Override
			public void selectionChanged(FigureSelectionEvent event) {
				Set<Figure> setOfFigure = event.getNewSelection();
				drawingSelectionChanged(setOfFigure);
			}
		});
		
		if (clientModel.getCurrentUser().getRole().getRoleCode() > Constants.MANAGERCODE) {
			drawingEditor.setTool(new LockSelectionTool());
			drawingEditor.setActionMap(new ActionMap());

			for (java.awt.Component c : toolBar.getComponents()) {
				c.setEnabled(false);
			}
			for (java.awt.Component c : drawingBar.getComponents()) {
				c.setEnabled(false);
			}
			for (java.awt.Component c : fileBar.getComponents()) {
				c.setEnabled(false);
			}
			subnetButton.setEnabled(true);
		}

        
        // 系统管理员才能添加网元和连线，删除子网
        if (clientModel.getCurrentUser().getRole().getRoleCode() > Constants.ADMINCODE) {
        	addingPopupButton.setEnabled(false);
        	connectionButton.setEnabled(false);
        	deleteButton.setEnabled(false);
		}
	}
	
	private static final Color URGENT_DEFAULT_COLOR = new Color(128,0,0);
	private static final Color SERIOUS_COLOR = new Color(136,105,0);
	private static final Color NOTICE_COLOR = new Color(110,110,0);
	private static final Color COMMON_COLOR = new Color(0,110,110);

	private final JAlarmButton urgentAlarmButton = new JAlarmButton(URGENT_DEFAULT_COLOR);
	private final JAlarmButton seriousAlarmButton = new JAlarmButton(SERIOUS_COLOR);
	private final JAlarmButton noticeAlarmButton = new JAlarmButton(NOTICE_COLOR);
	private final JAlarmButton commonAlarmButton = new JAlarmButton(COMMON_COLOR);
	
	@Resource(name=JUrgentAlarmButtonModel.ID)
	private JUrgentAlarmButtonModel urgentAlarmButtonModel;
	
	@Resource(name=JSeriousAlarmButtonModel.ID)
	private JSeriousAlarmButtonModel seriousAlarmButtonModel;

	@Resource(name=JNoticeAlarmButtonModel.ID)
	private JNoticeAlarmButtonModel noticeAlarmButtonModel;

	@Resource(name=JCommonAlarmButtonModel.ID)
	private JCommonAlarmButtonModel commonAlarmButtonModel;
	
	private JAlarmButtonModel urgentModel = null;
	private JAlarmButtonModel seriousModel = null;
	private JAlarmButtonModel noticeModel = null;
	private JAlarmButtonModel commonModel = null;
	
	private void addAlarmPanel(final JToolBar toolBar, final DrawingEditor editor) {
		
		urgentAlarmButton.setModel(urgentModel);
		ShowViewAction urgentAction = ClientUtils.getSpringBean(ShowViewAction.ID);
		urgentAction.setViewId(UrgentAlarmConfirmView.ID);
		urgentAction.setGroupId(FormGroupView.ID);
		urgentAlarmButton.setAction(urgentAction);
		urgentModel.setDefaultInfo();
		
		seriousAlarmButton.setModel(seriousModel);
		ShowViewAction seriousAction = ClientUtils.getSpringBean(ShowViewAction.ID);
		seriousAction.setViewId(SeriousAlarmConfirmView.ID);
		seriousAction.setGroupId(FormGroupView.ID);
		seriousAlarmButton.setAction(seriousAction);
		seriousModel.setDefaultInfo();
		
		noticeAlarmButton.setModel(noticeModel);
		ShowViewAction noticeAction = ClientUtils.getSpringBean(ShowViewAction.ID);
		noticeAction.setViewId(NoticeAlarmConfirmView.ID);
		noticeAction.setGroupId(FormGroupView.ID);
		noticeAlarmButton.setAction(noticeAction);
		noticeModel.setDefaultInfo();
		
		commonAlarmButton.setModel(commonModel);
		ShowViewAction commonAction = ClientUtils.getSpringBean(ShowViewAction.ID);
		commonAction.setViewId(CommonAlarmConfirmView.ID);
		commonAction.setGroupId(FormGroupView.ID);
		commonAlarmButton.setAction(commonAction);
		commonModel.setDefaultInfo();
		
		toolBar.add(urgentAlarmButton);
		toolBar.add(seriousAlarmButton);
		toolBar.add(noticeAlarmButton);
		toolBar.add(commonAlarmButton);

	}

	private void selectFigureByAddress(String address) {			
		Figure figure = drawingAdapter.findNodeByAddress(address);
		
		if (figure == null) {
			figure = drawingAdapter.findNodeByName(address);
		}
		
		if (figure != null) {
			drawingView.clearSelection();
			drawingView.addToSelection(figure);
		}
	}
		
	private JToggleButton addSelectionTool(
			final JToolBar toolBar, final DrawingEditor editor, final Tool selectionTool) {
		
        editor.setTool(selectionTool);
        selectionButton = new JToggleButton();

		selectionButton.setIcon(imageRegistry.getImageIcon(NetworkConstants.SELECTION));
		selectionButton.setToolTipText("选择");
		
        selectionButton.setSelected(true);
        selectionButton.addItemListener(new ToolButtonListener(selectionTool, editor));
        selectionButton.setFocusable(false);
        toolButtonGroup.add(selectionButton);
        toolBar.add(selectionButton);

        return selectionButton;
    }
	
	private void addDrawingBarButton(final JToolBar toolBar, final DrawingEditor editor) {
		selectionTool = new PopupSelectionTool();
		Collection<Action> figureActions = new ArrayList<Action>();
		Collection<Action> drawingActions = new ArrayList<Action>();
		DeleteFigureAllAction deleteAllAction = new DeleteFigureAllAction();
		deleteAllAction.setDrawingView(drawingView);
		deleteAllAction.setDeletionStrategy(deletionStrategy);
		deleteAllAction.putValue(Action.SMALL_ICON, imageRegistry.getImageIcon(NetworkConstants.DELETE));
		deleteAllAction.putValue(Action.SHORT_DESCRIPTION, "删除");
		deleteAllAction.putValue(Action.NAME, "删除");
		figureActions.add(deleteAllAction);
		SelectAllAction selectAllAction = new SelectAllAction();
		selectAllAction.putValue(Action.NAME, "全选");  
		drawingActions.add(selectAllAction);
		selectionTool.setFigureActions(figureActions);
		selectionTool.setDrawingActions(drawingActions);
		addSelectionTool(toolBar, editor, selectionTool);
		
		// 只有超级管理员才能删除网元
		selectionTool.setPopupActionFilter(new PopupActionFilter(){
			@Override
			public boolean getActionEnable(Figure selectedFigure, Action action) {
				if (action instanceof DeleteFigureAllAction) {
					if (clientModel.getCurrentUser().getRole().getRoleCode() > Constants.ADMINCODE) {
						return false;
					}
				}
				return true;
			}			
		});
        //
        addingPopupButton = new JPopupButton();
        addingPopupButton.setIcon(imageRegistry.getImageIcon(NetworkConstants.EQUIPMENT_TOOL));
        addingPopupButton.setItemFont(addingPopupButton.getFont());
        addingPopupButton.setSelected(false);
		addingPopupButton.setToolTipText("添加设备");
        addingPopupButton.setFocusable(false);
		
        Cursor customCursor = Toolkit.getDefaultToolkit().createCustomCursor(
        		imageRegistry.getImage(NetworkConstants.CURSOR_ADDING), new Point(0, 0), "CURSOR_ADDING");
        //
//        JMenuItem addSwitcherItem = new JMenuItem();
//        addSwitcherItem.setText("添加交换机");
//        addSwitcherItem.setToolTipText("添加交换机");
//        addSwitcherItem.setIcon(imageRegistry.getImageIcon(NetworkConstants.SWITCHER_TOOL));
//        BufferedImage switcherImage = Images.toBufferedImage(imageRegistry.getImageIcon(
//				NetworkConstants.SWITCHER).getImage());		
//        EquipmentCreationTool switcherTool = new EquipmentCreationTool(new EquipmentFigure(switcherImage));
//        switcherTool.setCustomCursor(customCursor);
//        switcherTool.addToolListener(toolHandler);
//        SwitcherCreationListener switcherToolListener = new SwitcherCreationListener(switcherTool, editor);
//        addSwitcherItem.addActionListener(switcherToolListener);
//        addSwitcherItem.addActionListener(menuItemListener);
//        addingPopupButton.add(addSwitcherItem);
        //
//        JMenuItem addCarrierItem = new JMenuItem();
//        addCarrierItem.setText("添加载波机");
//        addCarrierItem.setToolTipText("添加载波机");
//        addCarrierItem.setIcon(imageRegistry.getImageIcon(NetworkConstants.CARRIER_TOOL));
//        BufferedImage carrierImage = Images.toBufferedImage(imageRegistry.getImageIcon(
//				NetworkConstants.CARRIER).getImage());		
//        EquipmentCreationTool carrierTool = new EquipmentCreationTool(new EquipmentFigure(carrierImage));
//        carrierTool.setCustomCursor(customCursor);
//        carrierTool.addToolListener(toolHandler);
//        CarrierCreationListener carrierToolListener = new CarrierCreationListener(carrierTool, editor);
//        addCarrierItem.addActionListener(carrierToolListener);
//        addCarrierItem.addActionListener(menuItemListener);
//        addingPopupButton.add(addCarrierItem);
        //
//        JMenuItem addGprsItem = new JMenuItem();
//        addGprsItem.setText("添加GPRS");
//        addGprsItem.setToolTipText("添加GPRS");
//        addGprsItem.setIcon(imageRegistry.getImageIcon(NetworkConstants.GPRS_TOOL));
//        BufferedImage gprsImage = Images.toBufferedImage(imageRegistry.getImageIcon(
//				NetworkConstants.GPRS).getImage());		
//        EquipmentCreationTool gprsTool = new EquipmentCreationTool(new EquipmentFigure(gprsImage));
//        gprsTool.setCustomCursor(customCursor);
//        gprsTool.addToolListener(toolHandler);
//        GprsCreationListener gprsToolListener = new GprsCreationListener(gprsTool, editor);
//        addGprsItem.addActionListener(gprsToolListener);
//        addGprsItem.addActionListener(menuItemListener);
//        addingPopupButton.add(addGprsItem);
        //
        JMenuItem addFrontEndItem = new JMenuItem();
        addFrontEndItem.setText("添加前置机");
        addFrontEndItem.setToolTipText("添加前置机");
        addFrontEndItem.setIcon(imageRegistry.getImageIcon(NetworkConstants.FRONT_END_TOOL));
		BufferedImage frontEndImage = Images.toBufferedImage(imageRegistry.getImageIcon(
				NetworkConstants.FRONT_END).getImage());		
		EquipmentCreationTool frontEndTool = new EquipmentCreationTool(new EquipmentFigure(frontEndImage));
		frontEndTool.setCustomCursor(customCursor);
		frontEndTool.addToolListener(toolHandler);
        FrontEndCreationListener frontEndToolListener = new FrontEndCreationListener(frontEndTool, editor);
        addFrontEndItem.addActionListener(frontEndToolListener);
        addFrontEndItem.addActionListener(menuItemListener);        
        addingPopupButton.add(addFrontEndItem);
        //************************手动拓扑EPON设备演示紧急使用
//        JMenuItem addOltItem = new JMenuItem();
//        addOltItem.setText("添加OLT");
//        addOltItem.setToolTipText("添加OLT");
//        addOltItem.setIcon(imageRegistry.getImageIcon(NetworkConstants.OLT_TOOL));
//		BufferedImage oltImage = Images.toBufferedImage(imageRegistry.getImageIcon(
//				NetworkConstants.OLT_E).getImage());		
//		EquipmentCreationTool oltTool = new EquipmentCreationTool(new EquipmentFigure(oltImage));
//		oltTool.setCustomCursor(customCursor);
//		oltTool.addToolListener(toolHandler);
//		OltCreationListener oltToolListener = new OltCreationListener(oltTool, editor);
//        addOltItem.addActionListener(oltToolListener);
//        addOltItem.addActionListener(menuItemListener);        
//        addingPopupButton.add(addOltItem);
//        ////
//        JMenuItem addOnuItem = new JMenuItem();
//        addOnuItem.setText("添加ONU");
//        addOnuItem.setToolTipText("添加ONU");
//        addOnuItem.setIcon(imageRegistry.getImageIcon(NetworkConstants.ONU_TOOL));
//		BufferedImage onuImage = Images.toBufferedImage(imageRegistry.getImageIcon(
//				NetworkConstants.ONU).getImage());		
//		EquipmentCreationTool onuTool = new EquipmentCreationTool(new EquipmentFigure(onuImage));
//		onuTool.setCustomCursor(customCursor);
//		onuTool.addToolListener(toolHandler);
//		OnuCreationListener onuToolListener = new OnuCreationListener(onuTool, editor);
//		addOnuItem.addActionListener(onuToolListener);
//		addOnuItem.addActionListener(menuItemListener);        
//        addingPopupButton.add(addOnuItem);
//        ////
//        JMenuItem addSplitterItem = new JMenuItem();
//        addSplitterItem.setText("添加分光器");
//        addSplitterItem.setToolTipText("添加分光器");
//        addSplitterItem.setIcon(imageRegistry.getImageIcon(NetworkConstants.BEAMSPLITTER_TOOL));
//		BufferedImage splitterImage = Images.toBufferedImage(imageRegistry.getImageIcon(
//				NetworkConstants.BEAMSPLITTER).getImage());		
//		EquipmentCreationTool splitterTool = new EquipmentCreationTool(new EquipmentFigure(splitterImage));
//		splitterTool.setCustomCursor(customCursor);
//		splitterTool.addToolListener(toolHandler);
//		BeamsplitterCreationListener splitterToolListener = new BeamsplitterCreationListener(splitterTool, editor);
//		addSplitterItem.addActionListener(splitterToolListener);
//		addSplitterItem.addActionListener(menuItemListener); 
//        addingPopupButton.add(addSplitterItem);
//      ////
//      JMenuItem addLevel3Item = new JMenuItem();
//      addLevel3Item.setText("添加三层交换机");
//      addLevel3Item.setToolTipText("添加三层交换机");
//      addLevel3Item.setIcon(imageRegistry.getImageIcon(NetworkConstants.BEAMSPLITTER_TOOL));
//		BufferedImage level3Image = Images.toBufferedImage(imageRegistry.getImageIcon(
//				NetworkConstants.BEAMSPLITTER).getImage());		
//		EquipmentCreationTool level3Tool = new EquipmentCreationTool(new EquipmentFigure(level3Image));
//		level3Tool.setCustomCursor(customCursor);
//		level3Tool.addToolListener(toolHandler);
//		Level3CreationListener level3CreationListener = new Level3CreationListener(level3Tool, editor);
//		addLevel3Item.addActionListener(level3CreationListener);
//		addLevel3Item.addActionListener(menuItemListener); 
//      addingPopupButton.add(addLevel3Item);
        //***********************************        
        JMenuItem addSubnetItem = new JMenuItem();
        addSubnetItem.setText("添加子网");
        addSubnetItem.setToolTipText("添加子网");
        addSubnetItem.setIcon(imageRegistry.getImageIcon(NetworkConstants.SUBNET_TOOL));
		BufferedImage subnetImage = Images.toBufferedImage(imageRegistry.getImageIcon(
				NetworkConstants.SUBNET).getImage());		
		EquipmentCreationTool subnetTool = new EquipmentCreationTool(new EquipmentFigure(subnetImage));
		subnetTool.setCustomCursor(customCursor);
		subnetTool.addToolListener(toolHandler);
        SubnetCreationListener subnetToolListener = new SubnetCreationListener(subnetTool, editor);
        addSubnetItem.addActionListener(subnetToolListener);
        addSubnetItem.addActionListener(menuItemListener);        
        addingPopupButton.add(addSubnetItem);
        
        JMenuItem addVirtualElementItem = new JMenuItem();
        addVirtualElementItem.setText("添加虚拟网元");
        addVirtualElementItem.setToolTipText("添加虚拟网元");
        addVirtualElementItem.setIcon(imageRegistry.getImageIcon(NetworkConstants.SWITCHER_TOOL));
        BufferedImage virtualImage = Images.toBufferedImage(imageRegistry.getImageIcon(
				NetworkConstants.SWITCHER).getImage());
        EquipmentCreationTool virtualTool = new EquipmentCreationTool(new EquipmentFigure(virtualImage));
        virtualTool.setCustomCursor(customCursor);
        virtualTool.addToolListener(toolHandler);
        VirtualElementCreationListener virtualToolListener = new VirtualElementCreationListener(virtualTool, editor);
        addVirtualElementItem.addActionListener(virtualToolListener);
        addVirtualElementItem.addActionListener(menuItemListener);
        addingPopupButton.add(addVirtualElementItem);
        
        JMenuItem addCommentAreaItem = new JMenuItem();
        addCommentAreaItem.setText("添加注释");
        addCommentAreaItem.setToolTipText("添加注释");
        addCommentAreaItem.setIcon(imageRegistry.getImageIcon(NetworkConstants.COMMENT));
        EquipmentCreationTool commentTool = new EquipmentCreationTool(new CommentAreaFigure());
        commentTool.setMinimalSize(MINIMAL_SIZE);
        commentTool.setCustomCursor(customCursor);
        commentTool.addToolListener(toolHandler);
        CommentAreaCreationListener commentToolListener = new CommentAreaCreationListener(commentTool, editor);
        addCommentAreaItem.addActionListener(commentToolListener);
        addCommentAreaItem.addActionListener(menuItemListener);
        addingPopupButton.add(addCommentAreaItem);
        
        //        
        LabeledLinkFigure linkFigure = new LabeledLinkFigure();
        LinkCreationTool connctionTool = new LinkCreationTool(linkFigure);
        Cursor linkCustomCursor = Toolkit.getDefaultToolkit().createCustomCursor(
        		imageRegistry.getImage(NetworkConstants.CURSOR_LINK), new Point(0, 0), "CURSOR_LINK");
        connctionTool.setCustomCursor(linkCustomCursor);
        
        connectionButton = new JToggleButton();
        connectionButton.setToolTipText("添加连线");
        connectionButton.setIcon(imageRegistry.getImageIcon(
				NetworkConstants.LINK_TOOL));
        connctionTool.addToolListener(toolHandler);
        connectionButton.addItemListener(new LinkCreationListener(connctionTool, editor));
        connectionButton.setFocusable(false);
        
        toolButtonGroup.add(addingPopupButton);
        toolButtonGroup.add(connectionButton);
        
		toolBar.add(addingPopupButton);
        toolBar.add(connectionButton);
	}
	
	private void addToolBarButton(final JToolBar toolBar, final DrawingEditor editor) {
		deleteButton = new JButton();
		DeleteFigureAllAction deleteAction = new DeleteFigureAllAction();
		deleteAction.setDrawingView(drawingView);
		deleteAction.setDeletionStrategy(deletionStrategy);
		deleteAction.putValue(Action.SMALL_ICON, imageRegistry.getImageIcon(NetworkConstants.DELETE));
		deleteAction.putValue(Action.SHORT_DESCRIPTION, "删除");
		deleteAction.putValue(Action.NAME, StringUtils.EMPTY);
		deleteButton.setAction(deleteAction);
		toolBar.add(deleteButton);
        
        zoomPopupButton = new JPopupButton();
		zoomPopupButton.setText((int) (editor.getActiveView().getScaleFactor() * 100) + " %");
		zoomPopupButton.setToolTipText("缩放拓扑图");
		zoomPopupButton.setIcon(imageRegistry.getImageIcon(NetworkConstants.ZOOM_IN));
        zoomPopupButton.setFocusable(false);

        double[] factors = {8, 5, 4, 3, 2, 1.5, 1.25, 1, 0.75, 0.5, 0.25};
        for (int i = 0; i < factors.length; i++) {
            zoomPopupButton.add(
                    new ZoomEditorAction(editor, factors[i], zoomPopupButton) {
						private static final long serialVersionUID = -3821848139478757309L;
						@Override
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            super.actionPerformed(e);
                            zoomPopupButton.setText((int) (editor.getActiveView().getScaleFactor() * 100) + " %");
                        }
                    });
        }
        zoomPopupButton.setFocusable(false);        
		toolBar.add(zoomPopupButton);
		
		final JButton refreshButton = new JButton(imageRegistry
				.getImageIcon(NetworkConstants.REFRESH));
		refreshButton.setToolTipText("刷新拓扑图");
		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshDiagram();
			}
		});
		toolBar.add(refreshButton);

		lockButton = new JToggleButton();
		toolButtonGroup.add(lockButton);
		LockAction lockAction = new LockAction(this);
		lockAction.setLockImageIcon(imageRegistry.getImageIcon(NetworkConstants.LOCK));
		lockAction.setUnlockImageIcon(imageRegistry.getImageIcon(NetworkConstants.UNLOCK));
		lockAction.putValue(Action.SMALL_ICON, imageRegistry.getImageIcon(NetworkConstants.UNLOCK));
		lockAction.putValue(Action.SHORT_DESCRIPTION, "锁定拓扑图");
		lockAction.putValue(Action.NAME, StringUtils.EMPTY);
		lockButton.setAction(lockAction);
		toolBar.add(lockButton);
	}
	
//	@Resource
//	private ApplicationContext applicationContext;
	private void addFileBarButton(final JToolBar fileBar, final DrawingEditor editor) {
//		final JButton gisButton = new JButton(imageRegistry
//				.getImageIcon(MainMenuConstants.GIS));
//		gisButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				ShowViewAction gisAction = (ShowViewAction) applicationContext.getBean(ShowViewAction.ID);
//				gisAction.setViewId(GISView.ID);
//				gisAction.setGroupId(TopologyGroupView.ID);
//				gisAction.putValue(Action.NAME, "地理定位");
//				
//				ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
//						"actionPerformed", EventQueue.getMostRecentEventTime(), e.getModifiers());
//				gisAction.actionPerformed(event);
//			}
//		});
//		gisButton.setToolTipText("地理拓扑图");
//		fileBar.add(gisButton);
		//
		subnetButton = new JButton(imageRegistry
				.getImageIcon(ButtonConstants.SYNCHRONIZE));
		subnetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (equipmentModel.getSubnet() != null && 
					equipmentModel.getSubnet().getParentNode() != null) {
					requireSubnet(equipmentModel.getSubnet().getParentNode());
				} else {
					refreshDiagram();
				}
			}
		});
		subnetButton.setToolTipText("返回");
		fileBar.add(subnetButton);
		//

		final JButton saveButton = new JButton(imageRegistry
				.getImageIcon(NetworkConstants.SAVE));
		saveButton.setToolTipText("保存拓扑图");
		fileBar.add(saveButton);
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveDrawingAdapter(equipmentModel.getDiagram());
			}
		});

		ExportImageAction exportImageAction = new ExportImageAction(drawingView);
		exportImageAction.setImageIcon(imageRegistry.getImageIcon(NetworkConstants.EXPORT));
		final JButton exportButton = new JButton();
		exportButton.setAction(exportImageAction);
		exportButton.setToolTipText("导出拓扑图");
		fileBar.add(exportButton);

		PrintImageAction printImageAction = new PrintImageAction(drawingView);
		printImageAction.setImageIcon(imageRegistry
				.getImageIcon(NetworkConstants.PRINT));
		final JButton printButton = new JButton();
		printButton.setAction(printImageAction);
		printButton.setToolTipText("打印拓扑图");
		fileBar.add(printButton);
	}
		
	@Override
	public void setLocked(final boolean lock) {
		if (!SwingUtilities.isEventDispatchThread()) {
			throw new RuntimeException("DiagramView.setLocked invoked not in EventDispatchThread");
		}
		if (lock) {
			drawingEditor.setEnabled(false);
			drawingEditor.setTool(new LockSelectionTool());
			drawingEditor.setActionMap(new ActionMap());
			addingPopupButton.setEnabled(false);
			selectionButton.setEnabled(false);
			zoomPopupButton.setEnabled(false);
			connectionButton.setEnabled(false);
			deleteButton.setEnabled(false);
		} else {
			drawingEditor.setEnabled(true);
			drawingEditor.setActionMap(editorActionMap);
			selectionButton.setEnabled(true);
			zoomPopupButton.setEnabled(true);
			addingPopupButton.setEnabled(true);
			connectionButton.setEnabled(true);
			deleteButton.setEnabled(true);
			selectionButton.setSelected(true);
		}
		
		isLocked = lock;
	}
	
	@Override
	public boolean isLocked() {
		return isLocked;
	}
	
	private void generateDiagramNode(final String fepCode) {
		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				openBlockingDialog("正在生成拓扑发现的节点");
				
				try {				
					TopDiagramEntity topDiagramEntity = equipmentRepository.findDiagram(clientModel.getCurrentUser());	
					SwitcherNodeGenerator switcherGenerator = new SwitcherNodeGenerator(equipmentRepository);
					switcherGenerator.generate(topDiagramEntity, fepCode);
					EponNodeGenerator eponNodeGenerator = new EponNodeGenerator(equipmentRepository);
					eponNodeGenerator.generate(topDiagramEntity, fepCode);
					LinkNodeGenerator linkNodeGenerator = new LinkNodeGenerator(equipmentRepository, switcherGenerator, eponNodeGenerator);
					linkNodeGenerator.generate(topDiagramEntity);
					
					topDiagramEntity.setName(equipmentModel.getDiagramName());
					saveDiagram(topDiagramEntity);
				} catch (Exception ex) {
					openBlockingDialog("生成拓扑节点的时候发生错误");
					LOG.error("生成拓扑节点的时候发生错误", ex);
					ThreadUtils.sleep(2500);
					closeBlockingDialog();
				}
			}
		};
		singleThreadExecutor.execute(runnable);
	}
	
	/**
	 * 保存拓扑图
	 */
	private void saveDrawingAdapter(final TopDiagramEntity diagram) {
		diagram.setNodes(drawingAdapter.getAllNodes());
		diagram.setLines(drawingAdapter.getAllLinks());
		
		saveDiagram(diagram);
	}
	
	/**
	 * 保存拓扑图，不显示进度
	 */
	public boolean save() {
		TopDiagramEntity topDiagram = equipmentModel.getDiagram();
		topDiagram.setNodes(drawingAdapter.getAllNodes());
		topDiagram.setLines(drawingAdapter.getAllLinks());
		TopDiagramEntity savedDiagram = equipmentRepository.saveOrUpdateDiagram(topDiagram);
		
		return savedDiagram != null;
	}
	
	/**
	 * 保存拓扑图
	 */
	private void saveDiagram(final TopDiagramEntity diagram) {
			
		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				openBlockingDialog("正在保存拓扑图...");
				
				TopDiagramEntity savedDiagram = equipmentRepository.saveOrUpdateDiagram(diagram);
				
				// 保存失败
				if (savedDiagram == null) {
					showConfirmDialog();
				} else {
					savedDiagram = equipmentRepository.findDiagram(clientModel.getCurrentUser());
					equipmentModel.updateDiagram(savedDiagram);
				}
			}
		};
		singleThreadExecutor.execute(runnable);
	}
	
	private void showConfirmDialog() {
		if (SwingUtilities.isEventDispatchThread()) {
			int selected = JOptionPane.showConfirmDialog(DiagramView.this, "拓扑图已经被他人修改，是否立即刷新？", "保存失败", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (selected == JOptionPane.OK_OPTION) {
				describeMessage("保存失败，重新加载拓扑图");
				refreshDiagram();
			} else {
				LOG.info("保存失败，取消刷新");
				closeBlockingDialog();
			}
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showConfirmDialog();
				}
			});
		}
	}
	
	private void refreshDiagram() {
		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				openBlockingDialog("正在加载拓扑图...");
				TopDiagramEntity savedDiagram = equipmentRepository.findDiagram(clientModel.getCurrentUser());
				equipmentModel.updateDiagram(savedDiagram);
			}
		};
		singleThreadExecutor.execute(runnable);
	}
	
	private void openBlockingDialog(final String text) {
		if (SwingUtilities.isEventDispatchThread()) {
			if (blockingDialog.isVisible()) {
				blockingDialog.describe(text);
			} else {
				blockingDialog.describe(text);
	    		ClientUtils.centerDialog(blockingDialog);
				blockingDialog.setVisible(true);
			}
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					openBlockingDialog(text);
				}
			});
		}
	}
	
	private void describeMessage(final String text) {
		if (SwingUtilities.isEventDispatchThread()) {
			blockingDialog.describe(text);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					describeMessage(text);
				}
			});
		}
	}
	
	private void closeBlockingDialog() {
		if (SwingUtilities.isEventDispatchThread()) {
			blockingDialog.setVisible(false);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					closeBlockingDialog();
				}
			});
		}
	}
	
	/**
	 * 显示拓扑图形
	 */
	public void showDiagram() {
		if (!SwingUtilities.isEventDispatchThread()) {
			throw new RuntimeException("DiagramView.showDiagram invoked not in EventDispatchThread");
		}
		
		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				openBlockingDialog("正在绘制拓扑图...");
				try {
					Thread.sleep(0);
					addingFigure();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} finally {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							drawingView.refreshView();
							closeBlockingDialog();
						}
					});
				}
			}
		};
		singleThreadExecutor.execute(runnable);
	}
	
	public void refreshView() {
		refreshDiagram();
	}
	
	/**
	 * 添加拓扑节点、连线
	 */
	private void addingFigure() {
		final TopDiagramEntity topDiagram = equipmentModel.getDiagram();
		drawingAdapter.addNode(
				new ArrayList<NodeEntity>(topDiagram.getNodes()), 
				new ArrayList<LinkEntity>(topDiagram.getLines()));
		
		/* 绘制完图形后再监听 */
		drawing.addCompositeFigureListener(drawingCompositeFigureListener);
		drawing.addFigureListener(drawingFigureListener);
		selectFigure();
	}
	
	/**
	 * 整个设备模型已经更新，拓扑图。
	 */
	private void refreshDrawingView() {
		describeMessage("正在刷新界面...");
		/* 刷新拓扑图，取消监听 */
		drawing.removeFigureListener(drawingFigureListener);
		drawing.removeCompositeFigureListener(drawingCompositeFigureListener);
		
		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(0);
					clearDrawingView();
					addingFigure();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} finally {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							drawingView.refreshView();
							closeBlockingDialog();
						}
					});
				}
			}
		};
		singleThreadExecutor.execute(runnable);
	}
	
	private void requireSubnet(final String subnetGuid) {
		singleThreadExecutor.execute(new Runnable() {
			@Override
			public void run() {
				openBlockingDialog("正在加载子网拓扑图...");
			}
		});
		
		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				final SubNetTopoNodeEntity filledSubnet = 
					equipmentRepository.fillSubnet(
							equipmentModel.getDiagram().getId(), subnetGuid, clientModel.getCurrentUser());
				equipmentModel.enterSubnet(filledSubnet);
			}
		};
		singleThreadExecutor.execute(runnable);
	}
	
    private void subnetEnter(final SubNetTopoNodeEntity filledSubnet) {
		/* 刷新拓扑图，取消监听 */
		drawing.removeFigureListener(drawingFigureListener);
		drawing.removeCompositeFigureListener(drawingCompositeFigureListener);
		singleThreadExecutor.execute(new Runnable() {
			@Override 
			public void run() {
				describeMessage("正在绘制子网拓扑图...");
			}
		});
		
		
		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(0);
					clearDrawingView();
					addingSubnetFigure(filledSubnet);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} finally {
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							drawingView.refreshView();
							closeBlockingDialog();
						}
					});
				}
			}
		};
		singleThreadExecutor.execute(runnable);
    }

	/**
	 * 添加子网拓扑节点、连线
	 */
	private void addingSubnetFigure(SubNetTopoNodeEntity subnet) {
		drawingAdapter.addNode(subnet.getNodes(), subnet.getLines());
		
		/* 绘制完图形后再监听 */
		drawing.addCompositeFigureListener(drawingCompositeFigureListener);
		drawing.addFigureListener(drawingFigureListener);
		selectFigure();
	}
	
	private void selectFigure(){
		//定位
		if(null != equipmentModel.getLastFixedPosition()){
			Object selected = equipmentModel.getLastFixedPosition();
			NodeEntity nodeEntity = (NodeEntity)selected;
			Figure figure = drawingAdapter.findNodeByGuid(NodeUtils.getNodeGuid(nodeEntity));
			
			if (figure != null) {
				drawingView.clearSelection();
				drawingView.addToSelection(figure);
			}
		}
	}
	
	private void clearDrawingView() {
		if (SwingUtilities.isEventDispatchThread()) {
			drawingView.clearSelection();
			drawing.removeAllChildren();
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					clearDrawingView();
				}
			});
		}
	}
	
	/**
	 * 设备信息已经更新，把设备信息更新到图形属性。
	 */
	private void equipmentUpdated(final Object updatedNode) {
		drawingAdapter.updateNode(updatedNode);
	}
	
	private void drawingSelectionChanged(final Set<Figure> setOfFigure) {
		Figure selectedFigure = null;
		for (Figure selected : setOfFigure) {
			selectedFigure = selected;
		}
		if (selectedFigure instanceof NodeFigure) {
			Object figureModel = ((NodeFigure)selectedFigure).getEdit().getModel();
			equipmentModel.changeSelected(figureModel);
		} else {
			// 没有选中，则清除原来的选择
			equipmentModel.clearSelection();
		}
	}
	
	private void drawingFigureAdded(final Figure figure) {
		drawingAdapter.createFigureIndex(figure);
		
		if (figure instanceof NodeFigure) {
			NodeFigure equipmentFigure = (NodeFigure)figure;
			Object figureModel = equipmentFigure.getEdit().getModel();
			if (figureModel instanceof NodeEntity) {
				equipmentModel.addNode((NodeEntity)figureModel);
			}
		}

		if (figure instanceof LabeledLinkFigure) {
			LabeledLinkFigure linkFigure = (LabeledLinkFigure)figure;
			Object figureModel = linkFigure.getEdit().getModel();
			if (figureModel instanceof LinkEntity) {
				equipmentModel.addLink((LinkEntity)figureModel);
			}
		}
	}
	
	private void drawingFigureRemoved(final Figure figure) {
		if (figure instanceof NodeFigure) {
			Object figureModel = ((NodeFigure)figure).getEdit().getModel();
			if (figureModel instanceof LinkEntity) {
				LinkEntity linkNode = (LinkEntity)figureModel;
				
				if (linkNode.getNode1() instanceof SubNetTopoNodeEntity ||
					linkNode.getNode2() instanceof SubNetTopoNodeEntity) {
					LOG.info("指向子网的连线不需要删除");
				} else {
					linkRemoved(linkNode);
					drawingAdapter.removeNode(linkNode.getGuid());
				}
			}
			if (figureModel instanceof NodeEntity) {
				NodeEntity nodeEntity = (NodeEntity)figureModel;
				nodeRemoved(nodeEntity);
				drawingAdapter.removeNode(NodeUtils.getNodeGuid(nodeEntity));
			}
			equipmentModel.touch();
		}
	}
	
	private void nodeRemoved(final NodeEntity nodeEntity) {
//		if (nodeEntity.getId() == null) {
		equipmentModel.removeNode(nodeEntity);
//		} else {
//			nodeEntity.setSynchorized(false);
//		}
	}
	
	private void linkRemoved(final LinkEntity linkNode) {
		if (linkNode.getId() == null) {
			equipmentModel.removeLink(linkNode);
		} else {
			linkNode.setSynchorized(false);
		}
	}

	private void alarmArrival(final Object alarmObject) {
		if (alarmObject instanceof WarningEntity) {
			WarningEntity warning = (WarningEntity)alarmObject;
		}
		if (alarmObject instanceof NoteEntity) {
			NoteEntity note = (NoteEntity)alarmObject;
			if (note.getNoteType() == Constants.LINKUP) {
			}
		}		
		if (alarmObject instanceof RmonEntity) {
			RmonEntity remonAlarm = (RmonEntity)alarmObject;
			LOG.info(String.format("设备[%s][%s]连线[%s]流量[%s]告警", 
					remonAlarm.getIpValue(), remonAlarm.getPortNo(), remonAlarm.getLinkId(), remonAlarm.getValue()));
			LabeledLinkFigure linkFigure = drawingAdapter.findLinkById(remonAlarm.getLinkId());
			if (linkFigure != null) {
				linkFigure.getEdit().showAlarm(remonAlarm.getValue());
			}
		}
	}

	private final CompositeFigureListener drawingCompositeFigureListener = new CompositeFigureListener() {
		@Override
		public void figureAdded(CompositeFigureEvent event) {
			Figure figure = event.getChildFigure();
			drawingFigureAdded(figure);
		}
		@Override
		public void figureRemoved(CompositeFigureEvent event) {
			Figure figure = event.getChildFigure();
			drawingFigureRemoved(figure);
		}
	};
	
	private final FigureListener drawingFigureListener = new FigureAdapter() {
		@Override
		public void figureChanged(FigureEvent event) {
		}
	};
	
	private final MessageProcessorAdapter finishMessageProcessor = new MessageProcessorAdapter() {
		@Override
		public void process(TextMessage message) {
			try {
				String fepCode = message.getStringProperty(Constants.MESSAGEFROM);
				LOG.info(String.format("正在生成前置机[%s]发现的节点", fepCode));
				int count = equipmentModel.discover();
				LOG.info(String.format("未完成前置机数量%s", count));
				if (count == 0) {
					generateDiagramNode(fepCode);
				}
			} catch (JMSException e) {
				LOG.error("getStringProperty(Constants.MESSAGEFROM) error", e);
			}
		}
	};
	
	private final MessageProcessorAdapter pingMessageProcessor = new MessageProcessorAdapter() {
		@Override
		public void process(ObjectMessage message) {
			try {
				Object messageObject = message.getObject();
				if (messageObject instanceof PingResult) {
					PingResult pingResult = (PingResult)messageObject;
					String address = pingResult.getIpValue();
					NodeFigure figure = drawingAdapter.findNodeByAddress(address);
					if (figure != null && figure.getEdit() instanceof SwitcherEdit) {
						SwitcherEdit switcherEdit = (SwitcherEdit)figure.getEdit();
						if (pingResult.getStatus() > 1) {
							switcherEdit.alarm();
						} else {
							switcherEdit.normal();
						}
					}
				}
			} catch (JMSException e) {
				LOG.error("message.getObject() error", e);
			}
		}
	};
		
	private final PropertyChangeListener AlarmReceivedListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(final PropertyChangeEvent evt) {
			if (evt.getNewValue() != null) {
				final Runnable runnable = new Runnable() {
					@Override
					public void run() {						
						alarmArrival(evt.getNewValue());
					}
				};
				singleThreadExecutor.execute(runnable);
			}
		}
	};
	
	private final MessageProcessorAdapter gprsMessageProcessor = new MessageProcessorAdapter() {
		@Override
		public void process(ObjectMessage message) {
			try {
				Object messageObject = message.getObject();
				if (messageObject instanceof GPRSEntity) {
					GPRSEntity gprs = (GPRSEntity)messageObject;
					Figure figure = drawingAdapter.findNodeByGuid(gprs.getGuid());
					if (figure instanceof NodeFigure) {
						NodeFigure equpmentFigure = (NodeFigure)figure;
						GPRSTopoNodeEntity gprsNode = 
							(GPRSTopoNodeEntity)equpmentFigure.getEdit().getModel();
						gprsNode.setEntity(gprs);
						equipmentRepository.updateNode(gprsNode);
						equipmentModel.fireEquipmentUpdated(gprsNode);
					}
				}
			} catch (JMSException e) {
				LOG.error("message.getObject() error", e);
			}
		}
	};
	
	private final MessageProcessorAdapter carrierMessageProcessor = new MessageProcessorAdapter() {
		@Override
		public void process(final TextMessage message) {
			try {
				String carrierCode = message.getStringProperty(Constants.MESSAGEFROM);
				String result = message.getStringProperty(Constants.MESSAGERES);
					NodeFigure equpmentFigure = drawingAdapter.findNodeByAddress(carrierCode);
					if (equpmentFigure != null) {
						CarrierTopNodeEntity carrierNode = 
							(CarrierTopNodeEntity)equpmentFigure.getEdit().getModel();
						boolean status = "S".equals(result);
						carrierNode.setStatus(BooleanUtils.toInteger(status));
						equipmentRepository.updateNode(carrierNode);
						equipmentModel.fireEquipmentUpdated(carrierNode);
					}
			} catch (JMSException e) {
				LOG.error("message.getStringProperty() error", e);
			}
		}
	};
	
	private final MessageProcessorAdapter linkChangedMessageProcessor = new MessageProcessorAdapter() {
		@Override
		public void process(final ObjectMessage message) {
			try {
				Object messageObject = message.getObject();
				if (messageObject instanceof LinkEntity) {
					final LinkEntity linkNode = (LinkEntity)messageObject;
					final Runnable runnable = new Runnable() {
						@Override
						public void run() {
							LOG.info(String.format("[%s]连线%s状态已经改变[%s]", 
									linkNode.getLineType(), NodeUtils.lldp2String(linkNode.getLldp()), linkNode.getStatus()));	
							drawingAdapter.updateNode(linkNode);
						}
					};
					singleThreadExecutor.execute(runnable);
				}
			} catch (JMSException e) {
				LOG.error("message.getStringProperty() error", e);
			}
		}
	};
	
	private final MessageProcessorAdapter nodeChangedMessageProcessor = new MessageProcessorAdapter() {
		@Override
		public void process(final ObjectMessage message) {
			try {
				Object messageObject = message.getObject();
				if (messageObject instanceof NodeEntity) {
					final NodeEntity nodeEntity = (NodeEntity)messageObject;				
					final Runnable runnable = new Runnable() {
						@Override
						public void run() {							;
							LOG.info(String.format("节点[%s]状态已经改变[%s]", 
									NodeUtils.getNodeText(nodeEntity), nodeEntity.getStatus()));	
							drawingAdapter.updateNode(nodeEntity);
						}
					};
					singleThreadExecutor.execute(runnable);
				}
			} catch (JMSException e) {
				LOG.error("message.getStringProperty() error", e);
			}
		}
	};
	
	private final MessageProcessorAdapter frontEndMessageProcessor = new MessageProcessorAdapter() {
		@Override
		public void process(final ObjectMessage message) {
			try {
				Object messageObject = message.getObject();
				if (messageObject instanceof FEPEntity) {
					FEPEntity frontEnd = (FEPEntity)messageObject;
					String address = frontEnd.getIpValue();
					NodeFigure equpmentFigure = drawingAdapter.findNodeByAddress(address);
					if (equpmentFigure != null) {
						FEPTopoNodeEntity frontEndNode = 
							(FEPTopoNodeEntity)equpmentFigure.getEdit().getModel();
						frontEndNode.setFepEntity(frontEnd);
						equipmentModel.fireEquipmentUpdated(frontEndNode);
					}
					// 前置机下线，把与它相连的载波机、GPRS全部下线
					if (!frontEnd.getStatus().isStatus()) {
						List<CarrierTopNodeEntity> listOfCarrier = equipmentModel.findAllCarrier();
						for (CarrierTopNodeEntity carrierNode : listOfCarrier) {
							carrierNode.setStatus(BooleanUtils.toInteger(false));
							equipmentRepository.updateNode(carrierNode);
							equipmentModel.fireEquipmentUpdated(carrierNode);
						}
					}
				}
			} catch (JMSException e) {
				LOG.error("message.getStringProperty() error", e);
			}
		}
	};
		
	private final PropertyChangeListener EquipmentUpdatedListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getNewValue() != null) {
				equipmentUpdated(evt.getNewValue());
			}
		}
	};
	
	private final Observer EquipmentModelObserver = new Observer() {
		@Override
		public void update(Observable o, Object arg) {
			refreshDrawingView();
		}
	};

	/**
	 * 处理添加设备完成后
	 */
	private final ToolListener toolHandler = new ToolAdapter() {
        @Override
        public void toolDone(ToolEvent event) {
    		drawingEditor.setTool(selectionTool);
        	selectionButton.setSelected(true);
        	addingPopupButton.setSelected(false);
			addingPopupButton.setIcon(imageRegistry.getImageIcon(NetworkConstants.EQUIPMENT_TOOL));
        }
    };

	/**
	 * 处理选择添加设备后
	 */
    private final ActionListener menuItemListener = new ActionListener() {
    	@Override
    	public void actionPerformed(ActionEvent e) {
    		if (e.getSource() instanceof JMenuItem) {
    			JMenuItem item = (JMenuItem)e.getSource();
    			addingPopupButton.setSelected(true);
    			addingPopupButton.setIcon(item.getIcon());
    		}
        }
    };
    
    private final PropertyChangeListener requireRefreshListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			refreshDiagram();
		}
    };
    
    private final PropertyChangeListener subnetEnterListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getNewValue() instanceof SubNetTopoNodeEntity) {
				SubNetTopoNodeEntity subnetNode = (SubNetTopoNodeEntity)evt.getNewValue();
				subnetEnter(subnetNode);
			}
		}
    };
    
    private final PropertyChangeListener requireSubnetListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			requireSubnet(evt.getNewValue().toString());
		}
    };
    
    private final PropertyChangeListener disconnectOrTimeoutListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			SwingUtilities.invokeLater(new Runnable() {
	            public void run() {
	            	disconnectOrTimeout();	        		
	            }
	        });
		}
    };
    
    private void disconnectOrTimeout() {
    	this.closeBlockingDialog();
    }

	private JButton deleteButton;
	private JToggleButton connectionButton;
	private JPopupButton addingPopupButton;
	private JToggleButton selectionButton;
	private JPopupButton zoomPopupButton;
	private PopupSelectionTool selectionTool;
	private JToggleButton lockButton;
	private JButton subnetButton;
    
	private final ExecutorService singleThreadExecutor;
	private DeletionStrategy deletionStrategy;
	private ButtonGroup toolButtonGroup;

	private ActionMap editorActionMap;
	private boolean isLocked;
	
	private NetworkDrawing drawing;
	private DrawingAdapter drawingAdapter;
	private NetworkDrawingView drawingView;
	private NetworkDrawingEditor drawingEditor;	
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;

	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=AlarmModel.ID)
	private AlarmModel alarmModel;
			
	@Resource(name=EquipmentRepository.ID)
	private EquipmentRepository equipmentRepository;
	
	@Resource(name=ImageRegistry.ID)
	private ImageRegistry imageRegistry;
		
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	private BlockingDialog blockingDialog;
	private static final Logger LOG = LoggerFactory.getLogger(DiagramView.class);
	private static final long serialVersionUID = 8258426198407699072L;
	private static final Dimension MINIMAL_SIZE = new Dimension(133, 77);
	public static final String ID = "diagramView";
}