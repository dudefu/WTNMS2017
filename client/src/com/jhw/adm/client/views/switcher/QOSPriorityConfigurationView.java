package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.DOWNLOAD;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.MessageDispatcher;
import com.jhw.adm.client.core.MessageProcessorAdapter;
import com.jhw.adm.client.core.MessageSender;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.DataStatus;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.switcher.QOSPriorityViewModel;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.UploadMessageProcessorStrategy;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.ports.QOSPriority;
import com.jhw.adm.server.entity.switchs.Priority802D1P;
import com.jhw.adm.server.entity.switchs.PriorityDSCP;
import com.jhw.adm.server.entity.switchs.PriorityTOS;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(QOSPriorityConfigurationView.ID)
@Scope(Scopes.DESKTOP)
public class QOSPriorityConfigurationView extends ViewPart {
	private static final long serialVersionUID = 1L;

	public static final String ID = "qosPriorityConfigurationView";
	
	//802.1P优先级队列
	private final JPanel priorityPnl = new JPanel();
	private final JPanel priorityListPnl = new JPanel();
	private final JLabel priorityLbl = new JLabel();
	private final JLabel queueValueLbl = new JLabel();
	private final List<List> priorityCmpList = new ArrayList<List>();
	
	private final JLabel statusPrioLbl = new JLabel();
	private final JTextField statusPrioFld = new JTextField();
	private JButton uploadPrioBtn ;
	private JButton priorityBtn;
	
	//ToS优先级队列
	private final JPanel tosPnl = new JPanel();
	private final JPanel tosListPnl = new JPanel();
	private final JLabel tosLbl = new JLabel();
	private final JLabel queueTosLbl = new JLabel();
	private final List<List> tosCmpList = new ArrayList<List>();
	
	private final JLabel statusTosLbl = new JLabel();
	private final JTextField statusTosFld = new JTextField();
	private JButton uploadTosBtn ;
	private JButton tosBtn;
	
	//DSCP优先级队列
	private final JPanel dscpPnl = new JPanel();
	private final JPanel dscpListPnl = new JPanel();
	private final JLabel dscpLbl = new JLabel();
	private final JLabel queueDscpLbl = new JLabel();
	private final List<List> dscpCmpList = new ArrayList<List>();
	
	private final JLabel statusDscpLbl = new JLabel();
	private final JTextField statusDscpFld = new JTextField();
	private JButton uploadDscpBtn ;
	private JButton dscpBtn;
	
	private final JLabel fastModifyLbal = new JLabel();
	private final JLabel fromLbl = new JLabel();
//	private JTextField fromFld = new JTextField();
	//Amend 2010.06.08 
	private final NumberField fromFld = new NumberField(2,0,0,63,true);
	private final JLabel toLbl = new JLabel();
//	private JTextField toFld = new JTextField();
	private final NumberField toFld = new NumberField(2,0,0,63,true);
	private final JLabel fastQueueLbl = new JLabel();
	private final JComboBox fastQueueCombox = new JComboBox();
	private final JLabel range = new JLabel();
	private final JButton fastBtn = new JButton(); 
	
	private final JPanel bottomPnl = new JPanel();
	
	private JButton closeBtn = null;
	
	private static final String[] QUEUELIST = {"0","1","2","3"};
	
	private final static String PRIORITYSAVE = "prioritySave";
	private final static String TOSSAVE = "tosSave";
	private final static String DSCPSAVE = "dscpSave";
	
	private final static String PRIORITYUPLOAD = "priorityUpload";
	private final static String TOSSAVEUPLOAD = "tosUpload";
	private final static String DSCPSAVEUPLOAD = "dscpUpload";
	
	private SwitchNodeEntity switchNodeEntity = null;
	private ButtonFactory buttonFactory;
	private MessageSender messageSender;
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;
	
	@Autowired
	@Qualifier(QOSPriorityViewModel.ID)
	private QOSPriorityViewModel viewModel;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	private final MessageOfSwitchConfigProcessorStrategy messageOfSaveProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	private final UploadMessageProcessorStrategy uploadMessageProcessorStrategy = new UploadMessageProcessorStrategy();
	
	private static final Logger LOG = LoggerFactory.getLogger(QOSPriorityConfigurationView.class);
	
	private final MessageProcessorAdapter messageUploadProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(TextMessage message) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			queryData();
		}
	};
	
	private final MessageProcessorAdapter messageDownloadProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(ObjectMessage message) {
			queryData();
		}
	};
	
	private final MessageProcessorAdapter messageFedOfflineProcessor = new MessageProcessorAdapter() {//前置机不在线
		@Override
		public void process(TextMessage message) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			queryData();
		}
	};
	
	@PostConstruct
	protected void initialize(){
		init();
		queryData();
	}

	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		messageSender = remoteServer.getMessageSender();
		messageDispatcher.addProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
		
		initPriorityPnl();
		initTosPnl();
		initDscpPnl();
		
		initBottomPnl();
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(priorityPnl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,-5,0,0),0,0));
//		panel.add(tosPnl,new GridBagConstraints(1,0,1,1,0.0,0.0,
//				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,0),0,0));
		JPanel topPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		topPnl.add(panel);

		this.setLayout(new BorderLayout());
		this.add(topPnl,BorderLayout.NORTH);
		this.add(dscpPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		setResource();
	}
	
	private void initPriorityPnl(){
		priorityListPnl.setLayout(new GridBagLayout());
		JScrollPane scrollPnl = new JScrollPane();
		scrollPnl.getViewport().add(priorityListPnl);
		scrollPnl.setPreferredSize(new Dimension(290,120));
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(priorityLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,0),0,0));
		panel.add(queueValueLbl,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,100,0,0),0,0));
		
		panel.add(scrollPnl,new GridBagConstraints(0,1,2,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		
		JPanel leftPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel statusPnl = new JPanel(new GridBagLayout());
		statusPnl.add(statusPrioLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,0,0),0,0));
		statusPnl.add(statusPrioFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
		leftPnl.add(statusPnl);
		
		JPanel rightPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		priorityBtn = buttonFactory.createButton(PRIORITYSAVE);
		uploadPrioBtn = buttonFactory.createButton(PRIORITYUPLOAD);
		rightPnl.add(uploadPrioBtn);
		rightPnl.add(priorityBtn);
		
		JPanel btnPanel = new JPanel(new BorderLayout());
		btnPanel.add(leftPnl,BorderLayout.WEST);
		btnPanel.add(rightPnl,BorderLayout.EAST);
		
		priorityPnl.setLayout(new BorderLayout());
		priorityPnl.add(panel,BorderLayout.CENTER);
		priorityPnl.add(btnPanel,BorderLayout.SOUTH);
	}
	
	private void initTosPnl(){
		tosListPnl.setLayout(new GridBagLayout());
		JScrollPane scrollPnl = new JScrollPane();
		scrollPnl.getViewport().add(tosListPnl);
		scrollPnl.setPreferredSize(new Dimension(290,120));
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(tosLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,10,0,0),0,0));
		panel.add(queueTosLbl,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,115,0,0),0,0));
		
		panel.add(scrollPnl,new GridBagConstraints(0,1,2,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		
		JPanel leftPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel statusPnl = new JPanel(new GridBagLayout());
		statusPnl.add(statusTosLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,0,0),0,0));
		statusPnl.add(statusTosFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
		leftPnl.add(statusPnl);
		
		JPanel rightPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		tosBtn = buttonFactory.createButton(TOSSAVE);
		uploadTosBtn = buttonFactory.createButton(TOSSAVEUPLOAD);
		rightPnl.add(uploadTosBtn);
		rightPnl.add(tosBtn);
		
		JPanel btnPanel = new JPanel(new BorderLayout());
		btnPanel.add(leftPnl,BorderLayout.WEST);
		btnPanel.add(rightPnl,BorderLayout.EAST);
		
		tosPnl.setLayout(new BorderLayout());
		tosPnl.add(panel,BorderLayout.CENTER);
		tosPnl.add(btnPanel,BorderLayout.SOUTH);
	}
	
	private void initDscpPnl(){
		fromFld.setColumns(5);
		toFld.setColumns(5);
		for (int i = 0 ; i < QUEUELIST.length; i++){
			fastQueueCombox.addItem(QUEUELIST[i]);
		}
		
		JPanel topPnl = new JPanel(new GridBagLayout());
		topPnl.add(fastModifyLbal,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		topPnl.add(fromLbl,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,30,0,0),0,0));
		topPnl.add(fromFld,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		topPnl.add(toLbl,new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		topPnl.add(toFld,new GridBagConstraints(4,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		topPnl.add(fastQueueLbl,new GridBagConstraints(5,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,15,0,0),0,0));
		topPnl.add(fastQueueCombox,new GridBagConstraints(6,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		topPnl.add(range,new GridBagConstraints(7,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		topPnl.add(fastBtn,new GridBagConstraints(8,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,15,0,0),0,0));
		
		JPanel fastPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		fastPnl.add(topPnl);
		
		dscpListPnl.setLayout(new GridBagLayout());
		
		JScrollPane scrollPnl = new JScrollPane();
		scrollPnl.getViewport().add(dscpListPnl);
		scrollPnl.setPreferredSize(new Dimension(620,135));
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(dscpLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,30,0,0),0,0));
		panel.add(queueDscpLbl,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,115,0,0),0,0));
		panel.add(new JLabel("DSCP"),new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,138,0,0),0,0));
		panel.add(new JLabel("队列值"),new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,135,0,0),0,0));
		
		panel.add(scrollPnl,new GridBagConstraints(0,1,4,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		JPanel listPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		listPnl.add(panel);
		
		JPanel leftPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		JPanel statusPnl = new JPanel(new GridBagLayout());
		statusPnl.add(statusDscpLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,0,0),0,0));
		statusPnl.add(statusDscpFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
		leftPnl.add(statusPnl);
		
		JPanel rightPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		dscpBtn = buttonFactory.createButton(DSCPSAVE);
		uploadDscpBtn = buttonFactory.createButton(DSCPSAVEUPLOAD);
		rightPnl.add(uploadDscpBtn);
		rightPnl.add(dscpBtn);
		
		JPanel btnPanel = new JPanel(new BorderLayout());
		btnPanel.add(leftPnl,BorderLayout.WEST);
		btnPanel.add(rightPnl,BorderLayout.EAST);

		dscpPnl.setLayout(new BorderLayout());
		dscpPnl.add(fastPnl,BorderLayout.NORTH);
		dscpPnl.add(listPnl,BorderLayout.CENTER);
		dscpPnl.add(btnPanel,BorderLayout.SOUTH);
	}
	
	private void initBottomPnl(){
		this.bottomPnl.setLayout(new BorderLayout());
		JPanel newPanel = new JPanel(new GridBagLayout());
		closeBtn = buttonFactory.createCloseButton();
		this.setCloseButton(closeBtn);
//		newPanel.add(uploadBtn,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.bottomPnl.add(newPanel, BorderLayout.EAST);
	}
	
	private void setResource(){
		priorityPnl.setBorder(BorderFactory.createTitledBorder(""));
		tosPnl.setBorder(BorderFactory.createTitledBorder(""));
		dscpPnl.setBorder(BorderFactory.createTitledBorder(""));
		
		priorityLbl.setText("802.1P优先级");
		queueValueLbl.setText("队列值");
		tosLbl.setText("ToS优先级");
		queueTosLbl.setText("队列值");
		dscpLbl.setText("DSCP");
		queueDscpLbl.setText("队列值");
		fastModifyLbal.setText("DSCP优先级");
		fromLbl.setText("DSCP从");
		toLbl.setText("到");
		fastQueueLbl.setText("队列");
		range.setText("(DSCP:0-63)");
		
		statusPrioLbl.setText("状态");
		statusPrioFld.setColumns(15);
		statusPrioFld.setBackground(Color.WHITE);
		statusPrioFld.setEditable(false);
		
		statusTosLbl.setText("状态");
		statusTosFld.setColumns(15);
		statusTosFld.setBackground(Color.WHITE);
		statusTosFld.setEditable(false);
		
		statusDscpLbl.setText("状态");
		statusDscpFld.setColumns(15);
		statusDscpFld.setBackground(Color.WHITE);
		statusDscpFld.setEditable(false);
		
		fastBtn.setText("快速设置");
		fastBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				fastSetDSCP();
			}
		});
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}
	
	private void queryData(){
		switchNodeEntity = (SwitchNodeEntity)adapterManager.getAdapter(
					equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		
		if (null == switchNodeEntity){
			return ;
		}
		
		String where = " where entity.switchNode=?";
		Object[] parms = {switchNodeEntity};
		List<QOSPriority> qosPriorityList = (List<QOSPriority>)remoteServer.getService().findAll(QOSPriority.class, where, parms);
		if (null == qosPriorityList || qosPriorityList.size() < 1){
			return;
		}
		
//		int issue = qosPriorityList.get(0).getPriorityDSCPs().get(0).getIssuedTag();
		QOSPriority qosPriority = qosPriorityList.get(0);
		if (null == qosPriority){
			return;
		}
		
		viewModel.setQOSPriority(qosPriority);
		setCenterLayout();
		setValue(qosPriority);
		
	}
	
	private void setCenterLayout(){
		priorityCmpList.clear();
		tosCmpList.clear();
		dscpCmpList.clear();
		
		priorityListPnl.removeAll();
		tosListPnl.removeAll();
		dscpListPnl.removeAll();
		
		QOSPriority qosPriority = viewModel.getQosPriority();
		if(null == qosPriority){
			return;
		}
		int prioEOT = qosPriority.getPriorityEOTs().size();
		int prioTOS = qosPriority.getPriorityTOSs().size();
		int prioDSCP = qosPriority.getPriorityDSCPs().size();
		
		for (int i = 0 ; i < prioEOT ; i++){
			JLabel label = new JLabel();
			JComboBox comBox = new JComboBox();
			
			comBox.setPreferredSize(new Dimension(80,comBox.getPreferredSize().height));
			for (int j = 0 ; j < QUEUELIST.length ; j++){
				comBox.addItem(QUEUELIST[j]);
			}

			priorityListPnl.add(label,new GridBagConstraints(0,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(10,0,5,120),0,0));
			priorityListPnl.add(comBox,new GridBagConstraints(1,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
			
			List<JComponent> rowList = new ArrayList<JComponent>();
			
			rowList.add(0,label);
			rowList.add(1,comBox);
			
			priorityCmpList.add(rowList);
		}
		
		for (int i = 0 ; i < prioTOS ; i++){
			JLabel label = new JLabel();
			JComboBox comBox = new JComboBox();
		
			comBox.setPreferredSize(new Dimension(80,comBox.getPreferredSize().height));
			for (int j = 0 ; j < QUEUELIST.length ; j++){
				comBox.addItem(QUEUELIST[j]);
			}

			tosListPnl.add(label,new GridBagConstraints(0,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(10,0,5,120),0,0));
			tosListPnl.add(comBox,new GridBagConstraints(1,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
			
			List<JComponent> rowList = new ArrayList<JComponent>();
			
			rowList.add(0,label);
			rowList.add(1,comBox);
			
			tosCmpList.add(rowList);
		}
		
		for (int i = 0 ; i < prioDSCP ; i++){
			
			JLabel label = new JLabel(i+"");
			
			JComboBox combox = new JComboBox();
			combox.setPreferredSize(new Dimension(80,combox.getPreferredSize().height));
			for (int j = 0 ; j < QUEUELIST.length ; j++){
				combox.addItem(QUEUELIST[j]);
			}
			
			if (0 == i%2){
				dscpListPnl.add(label,new GridBagConstraints(0,i,1,1,0.0,0.0,
						GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(10,0,5,0),0,0));
				dscpListPnl.add(combox,new GridBagConstraints(1,i,1,1,0.0,0.0,
						GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,100,5,0),0,0));
			}
			else {
				dscpListPnl.add(label,new GridBagConstraints(2,i-1,1,1,0.0,0.0,
						GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(10,120,5,0),0,0));
				dscpListPnl.add(combox,new GridBagConstraints(3,i-1,1,1,0.0,0.0,
						GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,120,5,0),0,0));
			}
			
			List<JComponent> list = new ArrayList<JComponent>();
			
			list.add(0,label);
			list.add(1,combox);
			
			dscpCmpList.add(list);
		}
		
		dscpListPnl.revalidate();
	}
	
	private void setValue(final QOSPriority qosPriority){
		int statusPrio = Constants.ISSUEDADM;
		int statusDscp = Constants.ISSUEDADM;
		int statusTos = Constants.ISSUEDADM;
		if(SwingUtilities.isEventDispatchThread()){
			
			for (int i = 0 ; i < priorityCmpList.size(); i++){
				int prio = i;
				Priority802D1P priority802D1P = viewModel.getPrioEOT(i);
				int queue = priority802D1P.getQueueValue();
				
				((JLabel)priorityCmpList.get(i).get(0)).setText(prio+"");
				((JComboBox)priorityCmpList.get(i).get(1)).setSelectedItem(queue+"");
				
				statusPrio= priority802D1P.getIssuedTag();
			}
			
			for(int j = 0 ; j < tosCmpList.size(); j++){
				int prio = j;
				PriorityTOS priorityTOS = viewModel.getPrioTOS(prio);
				int queue = priorityTOS.getQueueValue();
				
				((JLabel)tosCmpList.get(j).get(0)).setText(prio+"");
				((JComboBox)tosCmpList.get(j).get(1)).setSelectedItem(queue+"");
				statusTos = priorityTOS.getIssuedTag();
			}
			
			for(int k = 0 ; k < dscpCmpList.size(); k++){
				int prio = k;
				PriorityDSCP priorityDSCP = viewModel.getPrioDSCP(prio);
				int queue = priorityDSCP.getQueueValue();
				
				((JLabel)dscpCmpList.get(k).get(0)).setText(prio+"");
				((JComboBox)dscpCmpList.get(k).get(1)).setSelectedItem(queue+"");
				
				statusDscp = priorityDSCP.getIssuedTag();
			}
			
			statusTosFld.setText(dataStatus.get(statusTos).getKey());
			statusPrioFld.setText(dataStatus.get(statusPrio).getKey());
			statusDscpFld.setText(dataStatus.get(statusDscp).getKey());
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setValue(qosPriority);
				}
			});
		}
	}
	
	@ViewAction(name=PRIORITYSAVE, icon=ButtonConstants.DOWNLOAD, text=DOWNLOAD, desc="下载QOS 802.1优先级配置",role=Constants.MANAGERCODE)
	public void prioritySave(){
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}
		if(priorityCmpList == null || priorityCmpList.size() < 1){
			JOptionPane.showMessageDialog(this, "输入的值非法,请重新输入","提示",JOptionPane.NO_OPTION);
			return;
		}

		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
		if (result == 0){
			return;
		}
		
		//组合Priority802D1P
		List<Serializable> list = new ArrayList<Serializable>();
		for (int i = 0 ; i < priorityCmpList.size(); i++){
			JLabel label = (JLabel)priorityCmpList.get(i).get(0);
			JComboBox comBox = (JComboBox)priorityCmpList.get(i).get(1);
			
			int priority = Integer.parseInt(label.getText());
			Priority802D1P priority802D1P = viewModel.getPrioEOT(priority);
			
			int queue = comBox.getSelectedIndex();
			priority802D1P.setQueueValue(queue);
			
			priority802D1P.setIssuedTag(Constants.ISSUEDADM);
			
			list.add(priority802D1P);
		}
		
		//下发服务端
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		showMessageDialog();
		try {
			remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.QOS_PRIORITYCONFIG, list, 
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
		} catch (JMSException e) {
			messageOfSaveProcessorStrategy.showErrorMessage("保存出现异常");
			LOG.error("QOSPriorityConfigurationView.prioritySave() error:{}", e);
			queryData();
		} catch (Exception ex) {
			messageOfSaveProcessorStrategy.showErrorMessage("保存出现异常");
			LOG.error("QOSSysConfigurationView.save() error:{}", ex);
			queryData();
		} 
		if(result == Constants.SYN_SERVER){
			//当只下发到网管侧而不下发到设备侧时，客户端先暂时更新返回的消息和状态
			messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
			statusPrioFld.setText(dataStatus.get(Constants.ISSUEDADM).getKey());
		}
	}
	
	@ViewAction(name=PRIORITYUPLOAD, icon=ButtonConstants.SYNCHRONIZE, text=UPLOAD, desc="上载QOS 802.1优先级配置",role=Constants.MANAGERCODE)
	public void priorityUpload(){
		if (null == switchNodeEntity) {
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		uploadMessageProcessorStrategy.showInitializeDialog("上载", this);
		final HashSet<SynchDevice> synDeviceList = new HashSet<SynchDevice>();
		SynchDevice synchDevice = new SynchDevice();
		synchDevice.setIpvalue(switchNodeEntity.getBaseConfig().getIpValue());
		synchDevice.setModelNumber(switchNodeEntity.getDeviceModel());
		synDeviceList.add(synchDevice);
		messageSender.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage message = session.createObjectMessage();
				message.setIntProperty(Constants.MESSAGETYPE,
						MessageNoConstants.SINGLESYNCHDEVICE);
				message.setObject(synDeviceList);
				message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
				message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
				message.setIntProperty(Constants.MESSPARMTYPE, MessageNoConstants.SINGLESWITCHQOSPRIORITY802D1P);
				return message;
			}
		});
	}
	
	@ViewAction(name=TOSSAVE, icon=ButtonConstants.DOWNLOAD, text=DOWNLOAD, desc="下载QOS ToS优先级配置",role=Constants.MANAGERCODE)
	public void tosSave(){
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}
		if(tosCmpList == null || tosCmpList.size() < 1){
			JOptionPane.showMessageDialog(this, "输入的值非法,请重新输入","提示",JOptionPane.NO_OPTION);
			return;
		}

		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
		if (result == 0){
			return;
		}
		
		//组合PriorityTOS
		List<Serializable> list = new ArrayList<Serializable>();
		for (int i = 0 ; i < tosCmpList.size(); i++){
			JLabel label = (JLabel)tosCmpList.get(i).get(0);
			JComboBox comBox = (JComboBox)tosCmpList.get(i).get(1);
			
			int priority = Integer.parseInt(label.getText());
			PriorityTOS priorityTOS = viewModel.getPrioTOS(priority);
			
			int queue = comBox.getSelectedIndex();
			priorityTOS.setQueueValue(queue);
			
			priorityTOS.setIssuedTag(Constants.ISSUEDADM);
			list.add(priorityTOS);
		}
		
		//下发服务端
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		showMessageDialog();
		try {
			remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.TOS_PRIORITYCONFIG, list,
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
		} catch (JMSException e) {
			messageOfSaveProcessorStrategy.showErrorMessage("保存出现异常");
			LOG.error("QOSPriorityConfigurationView.tosSave() error:{}", e);
			queryData();
		} catch (Exception ex) {
			messageOfSaveProcessorStrategy.showErrorMessage("保存出现异常");
			LOG.error("QOSSysConfigurationView.save() error:{}", ex);
			queryData();
		} 
		if(result == Constants.SYN_SERVER){
			messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
			statusTosFld.setText(dataStatus.get(Constants.ISSUEDADM).getKey());
		}
	}
	
	@ViewAction(name=TOSSAVEUPLOAD, icon=ButtonConstants.SYNCHRONIZE, text=UPLOAD, desc="上载QOS ToS优先级配置",role=Constants.MANAGERCODE)
	public void tosUpload(){
		if (null == switchNodeEntity) {
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		uploadMessageProcessorStrategy.showInitializeDialog("上载", this);
		final HashSet<SynchDevice> synDeviceList = new HashSet<SynchDevice>();
		SynchDevice synchDevice = new SynchDevice();
		synchDevice.setIpvalue(switchNodeEntity.getBaseConfig().getIpValue());
		synchDevice.setModelNumber(switchNodeEntity.getDeviceModel());
		synDeviceList.add(synchDevice);
		messageSender.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage message = session.createObjectMessage();
				message.setIntProperty(Constants.MESSAGETYPE,
						MessageNoConstants.SINGLESYNCHDEVICE);
				message.setObject(synDeviceList);
				message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
				message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
				message.setIntProperty(Constants.MESSPARMTYPE, MessageNoConstants.SINGLESWITCHQOSPRIORITYTOS);
				return message;
			}
		});
	}
	
	@ViewAction(name=DSCPSAVE, icon=ButtonConstants.DOWNLOAD, text=DOWNLOAD, desc="下载QOS DSCP优先级配置",role=Constants.MANAGERCODE)
	public void dscpSave(){
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}
		if(dscpCmpList == null || dscpCmpList.size() < 1){
			JOptionPane.showMessageDialog(this, "输入的值非法,请重新输入","提示",JOptionPane.NO_OPTION);
			return;
		}

		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
		if (result == 0){
			return;
		}
		
		//组合PriorityDSCP
		List<Serializable> list = new ArrayList<Serializable>();
		for (int i = 0 ; i < dscpCmpList.size(); i++){
			JLabel label = (JLabel)dscpCmpList.get(i).get(0);
			JComboBox comBox = (JComboBox)dscpCmpList.get(i).get(1);
			
			int dscp = Integer.parseInt(label.getText());
			PriorityDSCP priorityDSCP = viewModel.getPrioDSCP(dscp);
			
			int queue = comBox.getSelectedIndex();
			priorityDSCP.setQueueValue(queue);
			
			priorityDSCP.setIssuedTag(Constants.ISSUEDADM);
			
			list.add(priorityDSCP);
		}
		
		//下发服务端
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		showMessageDialog();
		try {
			remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.DSCP_PRIORITYCONFIG, list, 
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
		} catch (JMSException e) {
			messageOfSaveProcessorStrategy.showErrorMessage("保存出现异常");
			LOG.error("QOSPriorityConfigurationView.dscpSave() error:{}", e);
			queryData();
		} catch (Exception ex) {
			messageOfSaveProcessorStrategy.showErrorMessage("保存出现异常");
			LOG.error("QOSPriorityConfigurationView.dscpSave() error:{}", ex);
			queryData();
		} 
		if(result == Constants.SYN_SERVER){
			messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
			statusDscpFld.setText(dataStatus.get(Constants.ISSUEDADM).getKey());
		}
	}
	
	@ViewAction(name=DSCPSAVEUPLOAD, icon=ButtonConstants.SYNCHRONIZE, text=UPLOAD, desc="上载QOS DSCP优先级配置",role=Constants.MANAGERCODE)
	public void dscpUpload(){
		if (null == switchNodeEntity) {
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		uploadMessageProcessorStrategy.showInitializeDialog("上载", this);
		final HashSet<SynchDevice> synDeviceList = new HashSet<SynchDevice>();
		SynchDevice synchDevice = new SynchDevice();
		synchDevice.setIpvalue(switchNodeEntity.getBaseConfig().getIpValue());
		synchDevice.setModelNumber(switchNodeEntity.getDeviceModel());
		synDeviceList.add(synchDevice);
		messageSender.send(new MessageCreator() {
			public Message createMessage(Session session) throws JMSException {
				ObjectMessage message = session.createObjectMessage();
				message.setIntProperty(Constants.MESSAGETYPE,
						MessageNoConstants.SINGLESYNCHDEVICE);
				message.setObject(synDeviceList);
				message.setStringProperty(Constants.MESSAGEFROM, clientModel.getCurrentUser().getUserName());
				message.setStringProperty(Constants.CLIENTIP, clientModel.getLocalAddress());
				message.setIntProperty(Constants.MESSPARMTYPE, MessageNoConstants.SINGLESWITCHQOSPRIORITYDSCP);
				return message;
			}
		});
	}
	
	private void fastSetDSCP(){
		try{
			if (null == switchNodeEntity){
				JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
				return;
			}
			
			if(!validator()){
				return;
			}

			int fromDSCP = NumberUtils.toInt(fromFld.getText().trim());
			int toDSCP = NumberUtils.toInt(toFld.getText().trim());
	 		int queue = NumberUtils.toInt((String) fastQueueCombox.getSelectedItem());
			for (int dscp = fromDSCP ; dscp < toDSCP + 1; dscp++){
				if (dscpCmpList != null){
					((JComboBox)dscpCmpList.get(dscp).get(1)).setSelectedItem(queue+"");
				}
			}
		}
		catch(Exception ee){
			LOG.error("",ee);
		}
	}
	
	private boolean validator(){
		boolean isValid = true;
		
		String fromText = fromFld.getText().trim();
		String toText = toFld.getText().trim();
		
		if(StringUtils.isBlank(fromText)){
			JOptionPane.showMessageDialog(this, "DSCP值不允许为空，请重新输入", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if(StringUtils.isBlank(toText)){
			JOptionPane.showMessageDialog(this, "DSCP值不允许为空，请重新输入", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if(NumberUtils.toInt(fromText) > NumberUtils.toInt(toText)){
			JOptionPane.showMessageDialog(this, "DSCP值输入顺序错误，必须是升序输入，请重新输入", "提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		return isValid;
	}
	
	/**
	 * 当数据下发到设备侧和网管侧时
	 * 对于接收到的异步消息进行处理
	 */
	private void showMessageDialog(){
		messageOfSaveProcessorStrategy.showInitializeDialog("保存",this);
	}

	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="向上同步QOS优先级信息",role=Constants.MANAGERCODE)
	public void upSynchronize(){
	}
	
	private void clear(){
		switchNodeEntity = null;
		priorityListPnl.removeAll();
		tosListPnl.removeAll();
		dscpListPnl.removeAll();
		priorityCmpList.clear();
		tosCmpList.clear();
		dscpCmpList.clear();
		priorityListPnl.revalidate();
		tosListPnl.revalidate();
		dscpListPnl.revalidate();
	}
	
	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			clear();
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				queryData();
			}
		}		
	};
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		messageDispatcher.removeProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
	}
}