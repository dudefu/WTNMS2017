package com.jhw.adm.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import sun.awt.SunToolkit;

import com.jhw.adm.client.diagram.DiagramView;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.views.ConsoleGroupView;
import com.jhw.adm.client.views.EventConsoleView;
import com.jhw.adm.client.views.GroupView;
import com.jhw.adm.client.views.ModalContainer;
import com.jhw.adm.client.views.ViewContainer;
import com.jhw.adm.client.views.ViewPart;

/** 桌面程序窗口，也是系统的主窗口 */
@Component(DesktopWindow.ID)
public class DesktopWindow extends JXFrame {
	
	public DesktopWindow() {
		this.setMinimumSize(new Dimension(1024, 730));
		this.setIdleThreshold(30000);
		this.addPropertyChangeListener("idle", new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				LOG.info(String.format("Idle: %s", DesktopWindow.this.isIdle()));
			}
		});
	}

	private JInternalFrame createTopologyFrame() {
		TopologyFrame topologyFrame = new TopologyFrame();
		topologyFrame.setTitle("网络拓扑");
		topologyFrame.setBounds(0, 0, TOPOLOGY_FRAME_WIDTH,
				TOPOLOGY_FRAME_HEIGHT);
		desktopPane.add(topologyFrame, GroupView.TOPOLOGY_FRAME_LAYER);
		topologyFrame.add(diagramView);
		try {
			topologyFrame.setSelected(true);
			topologyFrame.setMaximum(true);
		} catch (java.beans.PropertyVetoException e) {
			LOG.error("create topology frame error", e);
		}

		topologyFrame.show();

		return topologyFrame;
	}

	/**
	 * 显示视图
	 * @param viewId 视图在容器里面的ID
	 * @param groupId 视图组在容器里面的ID
	 */
	public void showView(String viewId, String groupId) {
		if (!SwingUtilities.isEventDispatchThread()) {
			throw new RuntimeException("DesktopWindow.showView invoked not in EventDispatchThread");
		}
		if (springContext.containsBean(groupId)) {
			ViewContainer viewContainer = (ViewContainer) springContext.getBean(groupId);
			
			if (viewContainer instanceof ModalContainer) {
				ModalContainer modalContainer = (ModalContainer)viewContainer;
				ViewPart viewPart = getViewPart(viewId);
				
				if (viewPart != null) {
					viewPart.setRootFrame(this);
					activeView = viewPart;
					setMessage(String.format("打开视图(%s)", viewPart.getTitle()));
					modalContainer.addView(viewPart);
					modalContainer.open();
				}
			}
									
			if (viewContainer instanceof GroupView) {
				GroupView groupView = (GroupView)viewContainer;
				if (openedGroupView.containsKey(groupId)) {
				} else {
					addGroupView(groupView, groupView.getLayer(), groupView
							.getPlacement());
					groupView.addInternalFrameListener(new GroupFrameListner(
							groupId));
					openedGroupView.put(groupId, groupView);
				}

				try {
					if (!groupView.isShowing()) {
						groupView.deiconify();
					}
					
					if (groupView.isMaximize() && !groupView.isMaximum()) {						
						groupView.setMaximum(true);
					}
					groupView.setSelected(true);
				} catch (PropertyVetoException e) {
					LOG.error(String.format("%s.setSelected error", viewId));
				}

				ViewPart viewPart = getViewPart(viewId);
				
				if (viewPart != null) {
					viewPart.setRootFrame(this);
					activeView = viewPart;
					groupView.addView(viewPart);
					setMessage(String.format("打开视图(%s)", viewPart.getTitle()));
				}
			}
		} else {
			setErrorMessage(String.format("没找到分组视图(%s)定义", viewId));
			LOG.error(String.format("No group view named '%s' is defined",
					groupId));
		}
	}
	
	private ViewPart getViewPart(String viewId) {
		ViewPart viewPart = null;
		if (springContext.containsBean(viewId)) {
			try {
				Object viewBean = springContext.getBean(viewId);
				if (viewBean instanceof ViewPart) {
					viewPart = (ViewPart)viewBean;
				} else {
					setErrorMessage(String.format("视图(%s)必需继承ViewPart",
							viewBean.getClass().getName()));
					LOG.error(String.format(
							"View(%s) must be ViewPart subclass", viewId));
				}
			} catch (BeansException e) {
				LOG.error("getViewPart error", e);
				setErrorMessage(String.format("视图(%s)初始化异常", viewId));
			}
		} else {
			setErrorMessage(String.format("没找到视图(%s)定义", viewId));
			LOG.error(String.format("No view named '%s' is defined", viewId));
		}
		
		return viewPart;
	}

	private GroupView addGroupView(GroupView groupView, Integer layer,
			int placement) {
		int desktopWidth = desktopPane.getWidth();
		int desktopHeight = desktopPane.getHeight();
		int viewWidth = groupView.getViewWidth();
		int viewHeight = groupView.getViewHeight();
		int toolBarHeight = 30;
		int heightOffset = 40;
		int distX = (desktopWidth - viewWidth) / 2;
		int distY = (desktopHeight - viewHeight) / 2;

		if (placement == GroupView.CENTER) {
			distX = (desktopWidth - viewWidth) / 2;
			distY = (desktopHeight - viewHeight) / 2;
		} else if (placement == GroupView.RIGHT_BOTTOM) {
			distX = desktopWidth - viewWidth;
			distY = desktopHeight - viewHeight;
		} else if (placement == GroupView.RIGHT_TOP) {
			distX = desktopWidth - viewWidth;
			distY = 0 + toolBarHeight;
		} else if (placement == GroupView.TOP_CENTER) {
			distX = (desktopWidth - viewWidth) / 2;
			distY = 0 + heightOffset;
		} else if (placement == GroupView.LEFT_TOP) {
			distX = 0;
			distY = 0 + toolBarHeight;
		}	

		groupView.setBounds(distX < 0 ? 0 : distX,
				distY < 0 ? 0 : distY, viewWidth, viewHeight);

		groupView.setFrameIcon(imageIcon);
		desktopPane.add(groupView, layer);
		try {
			groupView.setClosed(false);
		} catch (PropertyVetoException e) {
			LOG.error("JInternalFrame setClosed error", e);
		}
		groupView.show();

		return groupView;
	}

	/**
	 * 关闭所有窗口
	 * */
	public void closeAllWindow() {
		List<GroupView> willClose = new ArrayList<GroupView>(openedGroupView.values());
		for (GroupView groupView : willClose) {
			groupView.close();
		}
	}

	/**
	 * 最小化所有窗口
	 * */
	public void minimizeAllWindow() {
		for (GroupView groupView : openedGroupView.values()) {
			groupView.iconify();
		}
	}
	
	/**
	 * 还原所有窗口
	 * */
	public void restoreAllWindow() {
		for (GroupView groupView : openedGroupView.values()) {
			groupView.deiconify();
		}
	}
	
	/**
	 * 打开
	 * */
	public void open() {
		if (!SwingUtilities.isEventDispatchThread()) {
			throw new RuntimeException("DesktopWindow.open invoked not in EventDispatchThread");
		}
		setVisible(true);
		afterOpen();
	}

	private void afterOpen() {
		showView(EventConsoleView.ID, ConsoleGroupView.ID);
		diagramView.showDiagram();
		SunToolkit.flushPendingEvents();
		setEquipmentNoInfo();
	}

	/**
	 * 关闭
	 */
	public void close() {
		beforeClose();
		setVisible(false);
		dispose();
	}
	
	public boolean save() {
		return diagramView.save();
	}
	
	private void beforeClose() {
		
	}

	@PostConstruct
	protected void initialize() {
		//initComponents
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		tryConnectDialog = new TryConnectDialog();
		statusBarContainer = new JPanel();
		add(statusBarContainer, BorderLayout.SOUTH);

		desktopPane = new JDesktopPane();		
		desktopPane.setBorder(BorderFactory.createEmptyBorder());
		desktopPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getViewport().setView(desktopPane);
				
		add(scrollPane, BorderLayout.CENTER);
		openedGroupView = new HashMap<String, GroupView>();
		
		createContent();
		createStatusBar(statusBarContainer);
		createTopologyFrame();

		InputMap map = desktopPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	 	map.put(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0),
 			"refreshDiagram");
	 	map.put(KeyStroke.getKeyStroke(KeyEvent.VK_F12, InputEvent.SHIFT_MASK),
			"flushPendingEventsAction");

        desktopPane.getActionMap().put("refreshDiagram", new AbstractAction() {
			private static final long serialVersionUID = -1201400064015274719L;
			@Override
			public void actionPerformed(ActionEvent e) {
				diagramView.refreshView();
			}
        });
        desktopPane.getActionMap().put("flushPendingEventsAction", new AbstractAction() {
			private static final long serialVersionUID = -1201400064015274719L;
			@Override
			public void actionPerformed(ActionEvent e) {
				SunToolkit.flushPendingEvents();
				LOG.error("flushPendingEvents");
//				LOG.error(
//						String.format("isAWTLockHeldByCurrentThread: %s; isPostEventQueueEmpty: %s", 
//								SunToolkit.isAWTLockHeldByCurrentThread(),
//								SunToolkit.isPostEventQueueEmpty()));
			}
        });
        
        clientModel.addPropertyChangeListener(ClientModel.TIMEOUNT, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				SwingUtilities.invokeLater(new Runnable() {
		            public void run() {
		            	disconnectOrTimeout();	        		
		            }
		        });
			}
        });
        
        clientModel.addPropertyChangeListener(ClientModel.DISCONNECT, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				disconnectOrTimeout();
			}
        });
        
        clientModel.addPropertyChangeListener(ClientModel.CONNECTED, new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				SwingUtilities.invokeLater(new Runnable() {
		            public void run() {
		        		serverLabel.setForeground(Color.BLACK);
		        		setMessage("服务器已经重新连接");
		        		tryConnectDialog.setVisible(false);
		            }
		        });
			}
        });
        equipmentModel.addObserver(equipmentModeObserver);
        equipmentModel.addPropertyChangeListener(EquipmentModel.SUBNET_ENTER, new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				setEquipmentNoInfo();
			}
		});
        equipmentModel.addPropertyChangeListener(EquipmentModel.EQUIPMENT_UPDATED, new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				setEquipmentNoInfo();
			}
		});
        equipmentModel.addPropertyChangeListener(EquipmentModel.EQUIPMENT_MODIFIED, new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				setEquipmentNoInfo();
			}
		});
	}
	
	private final Observer equipmentModeObserver = new Observer() {
		
		@Override
		public void update(Observable o, Object arg) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setEquipmentNoInfo();
				}
			});
		}
	};
	
	public void setEquipmentNoInfo() {
		String showMessage = "";
		String message = "";
		if(!StringUtils.isBlank(equipmentModel.getLayer2Message())){
			message = equipmentModel.getLayer2Message();
		}
		if(!StringUtils.isBlank(equipmentModel.getLayer3Message())){
			if(!StringUtils.isBlank(message)){
				message = message + " " + equipmentModel.getLayer3Message();
			}else{
				message = equipmentModel.getLayer3Message(); 
			}
		}
		if(!StringUtils.isBlank(equipmentModel.getOltMessage())){
			if(!StringUtils.isBlank(message)){
				message = message + " " + equipmentModel.getOltMessage();
			}else{
				message = equipmentModel.getOltMessage(); 
			}
		}
		if(!StringUtils.isBlank(equipmentModel.getOnuMessage())){
			if(!StringUtils.isBlank(message)){
				message = message + " " + equipmentModel.getOnuMessage();
			}else{
				message = equipmentModel.getOnuMessage(); 
			}
		}
		if(!StringUtils.isBlank(equipmentModel.getVirtualMessage())){
			if(!StringUtils.isBlank(message)){
				message = message + " " + equipmentModel.getVirtualMessage();
			}else{
				message = equipmentModel.getVirtualMessage(); 
			}
		}
		if(!StringUtils.isBlank(equipmentModel.getCarrierMessage())){
			if(!StringUtils.isBlank(message)){
				message = message + " " + equipmentModel.getCarrierMessage();
			}else{
				message = equipmentModel.getCarrierMessage(); 
			}
		}
		if(!StringUtils.isBlank(equipmentModel.getGprsMessage())){
			if(!StringUtils.isBlank(message)){
				message = message + " " + equipmentModel.getGprsMessage();
			}else{
				message = equipmentModel.getGprsMessage(); 
			}
		}
		
//		message = equipmentModel.getLayer2Message()
//				+ equipmentModel.getLayer3Message()
//				+ equipmentModel.getOltMessage()
//				+ equipmentModel.getOnuMessage()
//				+ equipmentModel.getVirtualMessage()
//				+ equipmentModel.getCarrierMessage()
//				+ equipmentModel.getGprsMessage();
		if(StringUtils.isBlank(message)){
			showMessage = DEFAULT_MESSAGE;
		}else{
			showMessage = message;
		}
		equipmentNoInfoLabel.setForeground(getModel().getMessageColor());
		equipmentNoInfoLabel.setText(showMessage);
	}
	
	private void disconnectOrTimeout() {
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
        		DesktopWindow.this.toFront();
        		DesktopWindow.this.requestFocus();
        		serverLabel.setForeground(Color.RED);
        		setErrorMessage("服务器连接已经断开");
        		ClientUtils.centerDialog(tryConnectDialog);
        		tryConnectDialog.setVisible(true);
            }
        });
	}
	
	protected void createContent() {
	}

	protected void createStatusBar(JComponent container) {
		container
				.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
		container.setLayout(new GridLayout(1, 3));

		JPanel leftPanel = new JPanel();
		leftPanel.setLayout(new FlowLayout(FlowLayout.LEADING));

		messageLabel = new JLabel("打开拓扑图成功");
		messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
		leftPanel.add(messageLabel);

		JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		equipmentNoInfoLabel = new JLabel(DEFAULT_MESSAGE);
		equipmentNoInfoLabel.setHorizontalAlignment(SwingConstants.LEFT);
		equipmentNoInfoLabel.setPreferredSize(new Dimension(400, equipmentNoInfoLabel
				.getPreferredSize().height));
		
		JSeparator js = new JSeparator(SwingConstants.VERTICAL);
		js.setPreferredSize(new Dimension(2,
				equipmentNoInfoLabel.getPreferredSize().height));
		centerPanel.add(js);
		
		centerPanel.add(equipmentNoInfoLabel);

		js = new JSeparator(SwingConstants.VERTICAL);
		js.setPreferredSize(new Dimension(2,
				equipmentNoInfoLabel.getPreferredSize().height));
		centerPanel.add(js);
		
		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new FlowLayout(FlowLayout.TRAILING));
		
		serverLabel = new JLabel(String.format("服务器(%s:%s)",
				clientModel.getServerConfig().getAddress(), clientModel.getServerConfig().getPort()));
		serverLabel.setHorizontalAlignment(SwingConstants.CENTER);
		serverLabel.setPreferredSize(new Dimension(250, serverLabel
				.getPreferredSize().height));
		rightPanel.add(serverLabel);

		js = new JSeparator(SwingConstants.VERTICAL);
		js.setPreferredSize(new Dimension(2,
				serverLabel.getPreferredSize().height));
		rightPanel.add(js);

		final JLabel usernameLabel = new JLabel();
		usernameLabel.setText(clientModel.getCurrentUser().getPersonInfo().getName());
		usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		usernameLabel.setPreferredSize(new Dimension(60, usernameLabel
				.getPreferredSize().height));
		rightPanel.add(usernameLabel);

		js = new JSeparator(SwingConstants.VERTICAL);
		js.setPreferredSize(new Dimension(2,
				serverLabel.getPreferredSize().height));
		rightPanel.add(js);

		final JLabel uerRoleLabel = new JLabel();
		uerRoleLabel.setText(clientModel.getCurrentUser().getRole().getRoleName());
		uerRoleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		uerRoleLabel.setPreferredSize(new Dimension(80, uerRoleLabel
				.getPreferredSize().height));
		rightPanel.add(uerRoleLabel);

		container.add(leftPanel);
		container.add(centerPanel);
		container.add(rightPanel);
	}

	public void setMessage(String message) {
		if (!SwingUtilities.isEventDispatchThread()) {
			throw new RuntimeException("DesktopWindow.setMessage invoked not in EventDispatchThread");
		}
		messageLabel.setForeground(getModel().getMessageColor());
		messageLabel.setText(message);
	}

	public void setErrorMessage(String message) {
		if (!SwingUtilities.isEventDispatchThread()) {
			throw new RuntimeException("DesktopWindow.setErrorMessage invoked not in EventDispatchThread");
		}
		messageLabel.setForeground(getModel().getErrorMessageColor());
		messageLabel.setText(message);
	}

	public void setImageIcon(ImageIcon imageIcon) {
		if (!SwingUtilities.isEventDispatchThread()) {
			throw new RuntimeException("DesktopWindow.setImageIcon invoked not in EventDispatchThread");
		}
		this.imageIcon = imageIcon;
		setIconImage(imageIcon.getImage());
	}

	public DesktopModel getModel() {
		return desktopModel;
	}
	
	public ViewPart getActiveView() {
		return activeView;
	}
	
	@Resource(name=DiagramView.ID)
	private DiagramView diagramView;

	@Resource(name=DesktopModel.ID)
	private DesktopModel desktopModel;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;

	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource
	private ApplicationContext springContext;

	private TryConnectDialog tryConnectDialog;
	private JLabel equipmentNoInfoLabel;
	private JLabel serverLabel;
	private JLabel messageLabel;
	private ImageIcon imageIcon;
	private JPanel statusBarContainer;
	private JDesktopPane desktopPane;
	private ViewPart activeView;
	private Map<String, GroupView> openedGroupView;

//	public static final String DEFAULT_MESSAGE = "节点统计:无节点";
	public static final String DEFAULT_MESSAGE = "无节点";
	private static final int TOPOLOGY_FRAME_WIDTH = 1024;
	private static final int TOPOLOGY_FRAME_HEIGHT = 768;
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory
			.getLogger(DesktopWindow.class);
	
	public static final String ID = "desktopWindow";

	private class GroupFrameListner extends InternalFrameAdapter {

		public GroupFrameListner(String groupViewId) {
			this.groupViewId = groupViewId;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * javax.swing.event.InternalFrameListener#internalFrameClosed(javax
		 * .swing .event.InternalFrameEvent)
		 */
		@Override
		public void internalFrameClosed(InternalFrameEvent e) {
			if (openedGroupView.containsKey(groupViewId)) {
				GroupView groupView = openedGroupView.get(groupViewId);
				openedGroupView.remove(groupViewId);
				
				if (groupView.isMaximize()) {						
					try {
						groupView.setMaximum(false);
					} catch (PropertyVetoException ex) {
						LOG.error("groupView.setMaximum(false) error", ex);
					}
				}
				
				groupView.close();
				groupView = null;
			}

			JInternalFrame internalFrame = e.getInternalFrame();
			if (!internalFrame.isClosed()) {
				desktopPane.getDesktopManager().closeFrame(internalFrame);
			}
		}

		private final String groupViewId;
	}
	
	private class TryConnectDialog extends JDialog {
		
		public TryConnectDialog() {
			super(DesktopWindow.this);
			setModal(true);
			setSize(350, 80);
			setUndecorated(true);
			setResizable(false);
			setLayout(new BorderLayout());
			JPanel contentPanel = new JPanel();
			createContents(contentPanel);
			add(contentPanel, BorderLayout.CENTER);
		}
		
		private void createContents(JPanel parent) {
			parent.setLayout(new BorderLayout());
			parent.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			JPanel contentPanel = new JPanel();
			createContentPanel(contentPanel);
			JPanel toolPanel = new JPanel();
			createToolPanel(toolPanel);

			parent.add(contentPanel, BorderLayout.CENTER);
			parent.add(toolPanel, BorderLayout.PAGE_END);
		}
		
		private void createContentPanel(JPanel parent) {
			parent.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			parent.setLayout(new GridLayout(1, 1));
			progressBar = new JProgressBar(1, 100);
			UIManager.put("ProgressBar.repaintInterval", new Integer(10));
			UIManager.put("ProgressBar.cycleTime", new Integer(1500));
			progressBar.setStringPainted(true);
			progressBar.setIndeterminate(true);
			progressBar.setString("正在重新连接服务器...");
			parent.add(progressBar);
		}
		
		private void createToolPanel(JPanel parent) {
			parent.setBorder(BorderFactory.createLineBorder(Color.GRAY));
			parent.setLayout(new FlowLayout(FlowLayout.TRAILING));
			exitButton = new JButton("退出");
			parent.add(exitButton);
			
			exitButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
		}


		@Override
		protected void processWindowEvent(WindowEvent e) {
			super.processWindowEvent(e);

			if (e.getID() == WindowEvent.WINDOW_CLOSING) {
				System.exit(0);
			}
		}
		
		private JProgressBar progressBar;
		private JButton exitButton;
		private static final long serialVersionUID = -4433859874182308345L;
	}
}

// refrences:
// http://devbean.blog.51cto.com/448512/92426
// http://edu.codepub.com/2009/0923/15657.php
// http://leagion.javaeye.com/blog/476669
class TopologyFrame extends JInternalFrame {

	public TopologyFrame() {
		this.setBorder(BorderFactory.createEmptyBorder());
		oldUi = (BasicInternalFrameUI) getUI();
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				TopologyFrame selectedFrame = (TopologyFrame) e.getSource();
				if (selectedFrame.isMaximum()) {
					selectedFrame.hideNorthPanel();
					try {
						selectedFrame.setMaximum(true);
					} catch (PropertyVetoException ex) {
						LOG.error("setMaximum(true) error", ex);
					}
				}
			}
		});
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

	BasicInternalFrameUI oldUi = null;

	private static final Logger LOG = LoggerFactory
			.getLogger(TopologyFrame.class);
	private static final long serialVersionUID = 1L;
}