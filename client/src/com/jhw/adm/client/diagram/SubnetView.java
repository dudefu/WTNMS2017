package com.jhw.adm.client.diagram;

import static com.jhw.adm.client.util.NodeUtils.getNodeGuid;

import java.awt.BorderLayout;
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
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ApplicationConstants;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.Lockable;
import com.jhw.adm.client.core.MainMenuConstants;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.core.RemoteServer;
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
import com.jhw.adm.client.draw.PopupSelectionTool;
import com.jhw.adm.client.draw.SwitcherFigure;
//import com.jhw.adm.client.map.GISView;
import com.jhw.adm.client.model.AlarmModel;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.EquipmentRepository;
import com.jhw.adm.client.swing.BlockingDialog;
import com.jhw.adm.client.swing.JPopupButton;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.views.TopologyGroupView;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.tuopos.CarrierTopNodeEntity;
import com.jhw.adm.server.entity.tuopos.GPRSTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.SubNetTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.TopDiagramEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.GPRSEntity;
import com.jhw.adm.server.entity.util.MessageNoConstants;
import com.jhw.adm.server.entity.util.PingResult;
import com.jhw.adm.server.entity.warning.NoteEntity;
import com.jhw.adm.server.entity.warning.RmonEntity;
import com.jhw.adm.server.entity.warning.WarningEntity;

/**
 * 子网拓扑视图
 */
@Component(SubnetView.ID)
public class SubnetView extends ViewPart implements Lockable {
	
	protected SubnetView() {
		singleThreadExecutor = Executors.newSingleThreadExecutor();
	}

	@PostConstruct
	protected void initialize() {
		setLayout(new BorderLayout());
		JPanel toolPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 0));
				
		add(toolPanel, BorderLayout.NORTH);

		final JToolBar drawingBar = new JToolBar();
		final JToolBar toolBar = new JToolBar();
		final JToolBar fileBar = new JToolBar();
		final JToolBar searchBar = new JToolBar();
		drawingBar.setFloatable(false);
		toolBar.setFloatable(false);
		fileBar.setFloatable(false);
		searchBar.setFloatable(false);
				
		drawingBar.setRollover(true);
		toolBar.setRollover(true);
		fileBar.setRollover(true);
		searchBar.setRollover(true);
		
		toolButtonGroup = new ButtonGroup();

		drawing = new NetworkDrawing();
		drawingAdapter = new DrawingAdapter(drawing);
		Image alertLevelImage = imageRegistry.getImageIcon(
				ApplicationConstants.ALARM_SEVERITY).getImage();
		Image backgroundImage = imageRegistry.getImageIcon(
				ApplicationConstants.BACKGROUND).getImage();
		drawingView = new NetworkDrawingView();
		drawingView.setInvisibleConstrainer(new NetworkConstrainer());
		drawingView.setBackgroundImage(backgroundImage);
//		drawingView.setAlarmSeverityImage(alertLevelImage);
		drawingView.setDrawing(drawing);

		deletionStrategy = new DefaultDeletionStrategy(drawingView);
		
		JScrollPane scrollPane = new JScrollPane(drawingView.getComponent());
		scrollPane.getHorizontalScrollBar().setUnitIncrement(30);
		scrollPane.getVerticalScrollBar().setUnitIncrement(30);
		scrollPane.setPreferredSize(new Dimension(getWidth(), getHeight()));
		
		add(scrollPane, BorderLayout.CENTER);

		drawingEditor = new NetworkDrawingEditor();
		drawingEditor.add(drawingView);
		drawingEditor.setActiveView(drawingView);
		drawingEditor.setDeletionStrategy(deletionStrategy);
		editorActionMap = drawingEditor.getActionMap();

		addDrawingBarButton(drawingBar, drawingEditor);
		addToolBarButton(toolBar, drawingEditor);
		addFileBarButton(fileBar, drawingEditor);
		
		final JButton searchButton = new JButton(imageRegistry
				.getImageIcon(NetworkConstants.SEARCH));
		searchButton.setToolTipText("快速定位");
		final JTextField searchField = new JTextField(20);
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
					Figure figure = drawingAdapter.findNodeByAddress(key);
					if (figure != null) {
						drawingView.clearSelection();
						drawingView.addToSelection(figure);
					}
				}
			}
		});
		searchButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String key = searchField.getText();				
				Figure figure = drawingAdapter.findNodeByAddress(key);
				if (figure != null) {
					drawingView.clearSelection();
					drawingView.addToSelection(figure);
				}
			}
		});

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
		
		messageDispatcher.addProcessor(MessageNoConstants.GPRSMESSAGE, gprsMessageProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.CARRIERMONITORREP, carrierMessageProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.PINGRES, pingMessageProcessor);
		
		clientModel.addPropertyChangeListener(ClientModel.TIMEOUNT, disconnectOrTimeoutListener);
		clientModel.addPropertyChangeListener(ClientModel.DISCONNECT, disconnectOrTimeoutListener);
		
		alarmModel.addPropertyChangeListener(AlarmModel.ALARM_RECEIVED, AlarmReceivedListener);
		
		equipmentModel.addObserver(EquipmentModelObserver);
		equipmentModel.addPropertyChangeListener(EquipmentModel.EQUIPMENT_UPDATED, EquipmentUpdatedListener);
		drawingView.addFigureSelectionListener(new FigureSelectionListener() {
			@Override
			public void selectionChanged(FigureSelectionEvent event) {
				Set<Figure> setOfFigure = event.getNewSelection();
				drawingSelectionChanged(setOfFigure);
			}
		});
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
		DeleteFigureAllAction deleteAction = new DeleteFigureAllAction();
		deleteAction.setDrawingView(drawingView);
		deleteAction.setDeletionStrategy(deletionStrategy);
		deleteAction.putValue(Action.SMALL_ICON, imageRegistry.getImageIcon(NetworkConstants.DELETE));
		deleteAction.putValue(Action.SHORT_DESCRIPTION, "删除");
		deleteAction.putValue(Action.NAME, "删除");
		figureActions.add(deleteAction);
		SelectAllAction selectAllAction = new SelectAllAction();
		selectAllAction.putValue(Action.NAME, "全选");
		drawingActions.add(selectAllAction);
		selectionTool.setFigureActions(figureActions);
		selectionTool.setDrawingActions(drawingActions);
		addSelectionTool(toolBar, editor, selectionTool);
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
        JMenuItem addSwitcherItem = new JMenuItem();
        addSwitcherItem.setText("添加交换机");
        addSwitcherItem.setToolTipText("添加交换机");
        addSwitcherItem.setIcon(imageRegistry.getImageIcon(NetworkConstants.SWITCHER_TOOL));
        BufferedImage switcherImage = Images.toBufferedImage(imageRegistry.getImageIcon(
				NetworkConstants.SWITCHER).getImage());		
        EquipmentCreationTool switcherTool = new EquipmentCreationTool(new EquipmentFigure(switcherImage));
        switcherTool.setCustomCursor(customCursor);
        switcherTool.addToolListener(toolHandler);
        SwitcherCreationListener switcherToolListener = new SwitcherCreationListener(switcherTool, editor);
        addSwitcherItem.addActionListener(switcherToolListener);
        addSwitcherItem.addActionListener(menuItemListener);
        addingPopupButton.add(addSwitcherItem);
        //
        JMenuItem addCarrierItem = new JMenuItem();
        addCarrierItem.setText("添加载波机");
        addCarrierItem.setToolTipText("添加载波机");
        addCarrierItem.setIcon(imageRegistry.getImageIcon(NetworkConstants.CARRIER_TOOL));
        BufferedImage carrierImage = Images.toBufferedImage(imageRegistry.getImageIcon(
				NetworkConstants.CARRIER).getImage());		
        EquipmentCreationTool carrierTool = new EquipmentCreationTool(new EquipmentFigure(carrierImage));
        carrierTool.setCustomCursor(customCursor);
        carrierTool.addToolListener(toolHandler);
        CarrierCreationListener carrierToolListener = new CarrierCreationListener(carrierTool, editor);
        addCarrierItem.addActionListener(carrierToolListener);
        addCarrierItem.addActionListener(menuItemListener);
        addingPopupButton.add(addCarrierItem);
        //
        JMenuItem addGprsItem = new JMenuItem();
        addGprsItem.setText("添加GPRS");
        addGprsItem.setToolTipText("添加GPRS");
        addGprsItem.setIcon(imageRegistry.getImageIcon(NetworkConstants.GPRS_TOOL));
        BufferedImage gprsImage = Images.toBufferedImage(imageRegistry.getImageIcon(
				NetworkConstants.GPRS).getImage());		
        EquipmentCreationTool gprsTool = new EquipmentCreationTool(new EquipmentFigure(gprsImage));
        gprsTool.setCustomCursor(customCursor);
        gprsTool.addToolListener(toolHandler);
        GprsCreationListener gprsToolListener = new GprsCreationListener(gprsTool, editor);
        addGprsItem.addActionListener(gprsToolListener);
        addGprsItem.addActionListener(menuItemListener);
        addingPopupButton.add(addGprsItem);
        //
        JMenuItem addOLTItem = new JMenuItem();
        addOLTItem.setText("添加OLT");
        addOLTItem.setToolTipText("添加OLT");
        addOLTItem.setIcon(imageRegistry.getImageIcon(NetworkConstants.OLT_TOOL));
		BufferedImage oltImage = Images.toBufferedImage(imageRegistry.getImageIcon(
				NetworkConstants.SWITCHER).getImage());		
		EquipmentCreationTool oltTool = new EquipmentCreationTool(new EquipmentFigure(oltImage));
		oltTool.setCustomCursor(customCursor);
		oltTool.addToolListener(toolHandler);
		OltCreationListener oltToolListener = new OltCreationListener(oltTool, editor);
        addOLTItem.addActionListener(oltToolListener);
        addOLTItem.addActionListener(menuItemListener);
        addingPopupButton.add(addOLTItem);
        //
        JMenuItem addSplitterItem = new JMenuItem();
        addSplitterItem.setText("添加分光器");
        addSplitterItem.setToolTipText("添加分光器");
        addSplitterItem.setIcon(imageRegistry.getImageIcon(NetworkConstants.BEAMSPLITTER_TOOL));
        BufferedImage splitterImage = Images.toBufferedImage(imageRegistry.getImageIcon(
				NetworkConstants.BEAMSPLITTER).getImage());		
        EquipmentCreationTool splitterTool = new EquipmentCreationTool(new EquipmentFigure(splitterImage));
        splitterTool.setCustomCursor(customCursor);
        splitterTool.addToolListener(toolHandler);
        BeamsplitterCreationListener splitterToolListener = new BeamsplitterCreationListener(splitterTool, editor);
        addSplitterItem.addActionListener(splitterToolListener);
        addSplitterItem.addActionListener(menuItemListener);
        addingPopupButton.add(addSplitterItem);
        //        
        JMenuItem addONUItem = new JMenuItem();
        addONUItem.setText("添加ONU");
        addONUItem.setToolTipText("添加ONU");
        addONUItem.setIcon(imageRegistry.getImageIcon(NetworkConstants.ONU_TOOL));
		BufferedImage onuImage = Images.toBufferedImage(imageRegistry.getImageIcon(
				NetworkConstants.ONU).getImage());		
		EquipmentCreationTool onuTool = new EquipmentCreationTool(new EquipmentFigure(onuImage));
		onuTool.setCustomCursor(customCursor);
		onuTool.addToolListener(toolHandler);
        OnuCreationListener onuToolListener = new OnuCreationListener(onuTool, editor);
        addONUItem.addActionListener(onuToolListener);
        addONUItem.addActionListener(menuItemListener);        
        addingPopupButton.add(addONUItem);
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
	
	private void addFileBarButton(final JToolBar fileBar, final DrawingEditor editor) {
		final JButton openButton = new JButton(imageRegistry
				.getImageIcon(MainMenuConstants.GIS));
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ShowViewAction gisAction = (ShowViewAction) applicationContext.getBean(ShowViewAction.ID);
//				gisAction.setViewId(GISView.ID);
				gisAction.setGroupId(TopologyGroupView.ID);
				gisAction.putValue(Action.NAME, "地理定位");
				
				ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
						"actionPerformed", EventQueue.getMostRecentEventTime(), e.getModifiers());
				gisAction.actionPerformed(event);
			}
		});
		openButton.setToolTipText("地理拓扑图");
		fileBar.add(openButton);

		final JButton saveButton = new JButton(imageRegistry
				.getImageIcon(NetworkConstants.SAVE));
		saveButton.setToolTipText("保存拓扑图");
		fileBar.add(saveButton);
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saveDiagram(equipmentModel.getDiagram());
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
        
        final JPopupButton zoomPopupButton = new JPopupButton();
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

		final JToggleButton lockButton = new JToggleButton();
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
		
	@Override
	public void setLocked(final boolean lock) {
		if (!SwingUtilities.isEventDispatchThread()) {
			throw new RuntimeException("DiagramView.setLocked invoked not in EventDispatchThread");
		}
		if (lock) {
			drawingEditor.setTool(new LockSelectionTool());
			drawingEditor.setActionMap(new ActionMap());
			addingPopupButton.setEnabled(false);
			selectionButton.setEnabled(false);
			connectionButton.setEnabled(false);
			deleteButton.setEnabled(false);
		} else {
			drawingEditor.setActionMap(editorActionMap);
			selectionButton.setEnabled(true);
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
					equipmentModel.updateDiagram(savedDiagram);
				}
			}
		};
		singleThreadExecutor.execute(runnable);
	}
	
	private void showConfirmDialog() {
		if (SwingUtilities.isEventDispatchThread()) {
			int selected = JOptionPane.showConfirmDialog(SubnetView.this, "拓扑图已经被他人修改，是否立即刷新？", "保存失败", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
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

		blockingDialog = new BlockingDialog();
		final Runnable runnable = new Runnable() {
			@Override
			public void run() {
				openBlockingDialog("正在绘制拓扑图...");
				try {
					Thread.sleep(500);
					addingFigure();
					Thread.sleep(500);
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
	 * 添加拓扑节点、连线
	 */
	private void addingFigure() {
		SubNetTopoNodeEntity subnetNode = 
			(SubNetTopoNodeEntity)adapterManager.getAdapter(equipmentModel.getLastSelected(), SubNetTopoNodeEntity.class);
		
		if (subnetNode != null) {
			//
		}
		final TopDiagramEntity topDiagram = equipmentModel.getDiagram();
//		drawingAdapter.addDiagram(topDiagram);
		
		/* 绘制完图形后再监听 */
		drawing.addFigureListener(drawingFigureListener);
		drawing.addCompositeFigureListener(drawingCompositeFigureListener);
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
//					Thread.sleep(300);
					addingFigure();
//					Thread.sleep(500);
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
	
	private void clearDrawingView() {
		if (SwingUtilities.isEventDispatchThread()) {
			drawingView.clearSelection();
//			drawing.removeAllChildren();
			drawing.removeAllChildren();
//			drawingView.clearView();
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
				linkRemoved(linkNode);
			}
			if (figureModel instanceof NodeEntity) {
				NodeEntity nodeEntity = (NodeEntity)figureModel;
				nodeRemoved(nodeEntity);
			}
		}
	}
	
	private void nodeRemoved(final NodeEntity nodeEntity) {
		if (nodeEntity.getId() == null) {
			equipmentModel.removeNode(nodeEntity);
		} else if (!nodeEntity.isSynchorized()) {
			equipmentModel.removeNode(nodeEntity);
//			equipmentRepository.deleteNode(nodeEntity);
			Long diagramId = equipmentModel.getDiagram().getId();
			remoteServer.getNmsService().deleteNode(nodeEntity, diagramId);
		} else {
			nodeEntity.setSynchorized(false);
		}
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
			linkDownAlarm(warning);
		}
		if (alarmObject instanceof NoteEntity) {
			NoteEntity note = (NoteEntity)alarmObject;
			if (note.getNoteType() == Constants.LINKUP) {
				linkUpAlarm(note);
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
	
	private void linkDownAlarm(final WarningEntity warning) {
	}
	
	/**
	 * 检查交换机所有的连线是否全部断开
	 */
	private void checkSwitcherShutdown(final SwitchTopoNodeEntity switcherNode) {		
		boolean shutdown = drawingAdapter.isSwticherShutdown(switcherNode);
		switcherNode.setStatus(BooleanUtils.toInteger(!shutdown));		
		String nodeGuid = getNodeGuid(switcherNode);
		NodeFigure nodeFigure = drawingAdapter.findNodeByGuid(nodeGuid);
		
		if (nodeFigure instanceof SwitcherFigure) {
			SwitcherFigure switcherFigure = (SwitcherFigure)nodeFigure;
			if (shutdown) {
				switcherFigure.getEdit().alarm();
			} else {
				switcherFigure.getEdit().normal();
			}
		}
		equipmentRepository.updateNode(switcherNode);
	}
	
	private void linkUpAlarm(final NoteEntity note) {
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
			equipmentModel.touch();
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
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getNewValue() != null) {
				alarmArrival(evt.getNewValue());
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
	private PopupSelectionTool selectionTool;
    
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
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource
	private ApplicationContext applicationContext;
	
	private BlockingDialog blockingDialog;

	private static final Logger LOG = LoggerFactory.getLogger(SubnetView.class);
	private static final long serialVersionUID = 3825036253168612535L;
	public static final String ID = "subnetView";
}