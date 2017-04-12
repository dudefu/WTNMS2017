package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.DOWNLOAD;
import static com.jhw.adm.client.core.ActionConstants.SAVE;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.Constant;
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
import com.jhw.adm.client.model.switcher.ConfigurePortViewModel;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.MessageReceiveInter;
import com.jhw.adm.client.swing.ParamConfigStrategy;
import com.jhw.adm.client.swing.ParamUploadStrategy;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.TextFieldPlainDocument;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.ports.SwitchPortEntity;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(ConfigurePortView.ID)
@Scope(Scopes.DESKTOP)
public class ConfigurePortView extends ViewPart implements MessageReceiveInter{
	public static final String ID = "configurePortView";

	private final JScrollPane optionScrllPnl = new JScrollPane();
	private final JPanel optionPnl = new JPanel();
	
	//�϶˵����
	private final JPanel topPnl = new JPanel();
	
	private final JLabel portTopLbl = new JLabel();
	private final JLabel portDesLbl = new JLabel();
	private final JLabel statusTopLbl = new JLabel();
	private final JLabel connectTopLbl = new JLabel();
	private final JLabel typeTopLbl = new JLabel();
	private final JLabel currentModeTopLbl = new JLabel();
	private final JLabel configModeTopLbl = new JLabel();
	private final JLabel flowControlTopLbl = new JLabel();
	private final JLabel discardTopLbl = new JLabel();
	
	
	private final JPanel mainPanel = new JPanel();
	private final JLabel statusLbl = new JLabel();
	private final JTextField statusFld = new JTextField();
	
	
	//�м����
	private final JScrollPane scrolPnl = new JScrollPane();
	private final JPanel centerPnl = new JPanel();
	private static final String[] STATUS = {"����","����"};
	private static final String[] PORT_MODE = {"����Ӧ","100Mȫ˫��","100M��˫��","10Mȫ˫��","10M��˫��"};
	private static final String[] DISCARD_STATE = {"None","All","Untag"};
	
	
	//�¶˵İ�ť���
	private final JPanel bottomPnl = new JPanel();
	private JButton saveBtn;
	private JButton uploadBtn;
	private JButton downloadBtn;
	private JButton closeBtn ;
	
	private SwitchNodeEntity switchNodeEntity = null;
	
	private ButtonFactory buttonFactory;
	
	//��ͼ�����еĿؼ�
	private final List<List> componentList = new ArrayList<List>();

	//��¼�˿�����
	private int portCount = 0;
	
	//��ڵ�����ģʽ�����޸�,�Ѿ��ͽ�������ͨ,��Ϊ���ʱ,����ģʽ�·�Ϊno
	private static final String GE_MODE = "no";
	
	private static final long serialVersionUID = 1L;
	
	private MessageSender messageSender;
	
	@Autowired
	@Qualifier(ConfigurePortViewModel.ID)
	private ConfigurePortViewModel viewModel;
	
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
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	private static final Logger LOG = LoggerFactory.getLogger(ConfigurePortView.class);
	
	private final MessageProcessorAdapter messageUploadProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(TextMessage message) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
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
	
	private final MessageProcessorAdapter messageFedOfflineProcessor = new MessageProcessorAdapter() {//ǰ�û�������
		@Override
		public void process(TextMessage message) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
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
		
		initTopPnl();
		initCenterPnl();
		initBottomPnl();
		
		optionPnl.setLayout(new BorderLayout());
		optionPnl.add(topPnl,BorderLayout.NORTH);
		optionPnl.add(scrolPnl,BorderLayout.CENTER);
		
		optionScrllPnl.getViewport().add(optionPnl);
		
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(optionScrllPnl,BorderLayout.CENTER);
		
		this.setLayout(new BorderLayout());
		this.add(mainPanel,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		this.setViewSize(685,480);
		
		setResource();
	}
	
	private void initTopPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(portTopLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		panel.add(portDesLbl,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,95,0,0),0,0));
		panel.add(statusTopLbl,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,125,0,0),0,0));
		panel.add(connectTopLbl,new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,45,0,0),0,0));
		panel.add(typeTopLbl,new GridBagConstraints(4,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,20,0,0),0,0));
		panel.add(currentModeTopLbl,new GridBagConstraints(5,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,35,0,0),0,0));
		panel.add(configModeTopLbl,new GridBagConstraints(6,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,55,0,0),0,0));
		panel.add(flowControlTopLbl,new GridBagConstraints(7,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,60,0,0),0,0));
		panel.add(discardTopLbl,new GridBagConstraints(8,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,70,0,0),0,0));
		topPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		topPnl.add(panel);
	}
	
	private void initCenterPnl(){
		centerPnl.setLayout(new GridBagLayout());
		
		JPanel portListPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		portListPnl.add(centerPnl);
		
		scrolPnl.getViewport().add(portListPnl);
	}
	
	private void initBottomPnl(){
		JPanel statusPnl = new JPanel(new GridBagLayout());
		statusPnl.add(statusLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(10,5,0,0),0,0));
		statusPnl.add(statusFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(10,5,0,0),0,0));
		JPanel leftPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		leftPnl.add(statusPnl);
		
		JPanel newPanel = new JPanel(new GridBagLayout());
		saveBtn = buttonFactory.createButton(SAVE);
		uploadBtn = buttonFactory.createButton(UPLOAD);
		downloadBtn = buttonFactory.createButton(DOWNLOAD);
		closeBtn = buttonFactory.createCloseButton();
//		newPanel.add(saveBtn,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(uploadBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(downloadBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		JPanel rightPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		rightPnl.add(newPanel);
		
		bottomPnl.setLayout(new BorderLayout());
		bottomPnl.add(leftPnl, BorderLayout.WEST);
		bottomPnl.add(rightPnl, BorderLayout.EAST);
	}

	private void setResource(){
		this.setTitle("�˿�����");
//		this.setBorder(BorderFactory.createTitledBorder("�˿�����"));
		
		portTopLbl.setText("�˿�");
		portDesLbl.setText("����");
		statusTopLbl.setText("״̬");
		connectTopLbl.setText("����״̬");
		typeTopLbl.setText("����");
		currentModeTopLbl.setText("��ǰģʽ");
		configModeTopLbl.setText("����ģʽ");
		flowControlTopLbl.setText("����");
		discardTopLbl.setText("����");
		
		statusLbl.setText("״̬");
		statusFld.setColumns(15);
		statusFld.setEditable(false);
		statusFld.setBackground(Color.WHITE);
		
		//���Ӷ��豸��ѡ��ͬ�Ľڵ�ʱ�ļ�����
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}
	
	private void queryData(){
		switchNodeEntity =
			(SwitchNodeEntity)adapterManager.getAdapter(
					equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		
		if (null == switchNodeEntity){
			return ;
		}
		
		//�˿�����
		portCount = switchNodeEntity.getPorts().size();
		//��ʼ���������
		setCenterLayout();

		Set<SwitchPortEntity> portEntitySet = switchNodeEntity.getPorts();
		
		//���ÿؼ���ֵ
		setValue(portEntitySet);
		
	}
	
	private void setCenterLayout(){
		componentList.clear();
		centerPnl.removeAll();
		
		for(int i = 0 ; i < portCount ; i++){
			List<JComponent> rowList = new ArrayList<JComponent>();
			
			JLabel portLbl = new JLabel();
			
			JTextField portDesFld = new JTextField();
			portDesFld.setColumns(25);
			portDesFld.setDocument(new TextFieldPlainDocument(portDesFld, 24));
			
			JComboBox statusCombox = new JComboBox();
			for (int k = 0 ; k <STATUS.length; k++){
				statusCombox.addItem(STATUS[k]);
				
			}
			statusCombox.setPreferredSize(new Dimension(80,statusCombox.getPreferredSize().height));
			
			JLabel connectStatus = new JLabel();
			
			JLabel portTypeLbl = new JLabel();
			
			JLabel currentModeLbl = new JLabel();
//			currentModeLbl.setPreferredSize(new Dimension(60,20));
			
			JComboBox configModeCombox = new JComboBox();
			for(int k = 0 ; k < PORT_MODE.length; k++){
				configModeCombox.addItem(PORT_MODE[k]);
			}
			
			JComboBox flowControlCombox = new JComboBox();
			for(int k = 0 ; k < STATUS.length; k++){
				flowControlCombox.addItem(STATUS[k]);
			}
			flowControlCombox.setPreferredSize(new Dimension(80,flowControlCombox.getPreferredSize().height));
			
			JComboBox discardCombox = new JComboBox();
			for(int k = 0 ; k < DISCARD_STATE.length; k++){
				discardCombox.addItem(DISCARD_STATE[k]);
			}
			discardCombox.setPreferredSize(new Dimension(80,discardCombox.getPreferredSize().height));
			
			centerPnl.add(portLbl,new GridBagConstraints(0,i,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,10,5,0),0,0));
			centerPnl.add(portDesFld,new GridBagConstraints(1,i,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,35,5,0),0,0));	
			centerPnl.add(statusCombox,new GridBagConstraints(2,i,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,35,5,0),0,0));
			centerPnl.add(connectStatus,new GridBagConstraints(3,i,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,0),0,0));
			centerPnl.add(portTypeLbl,new GridBagConstraints(4,i,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,0),0,0));
			centerPnl.add(currentModeLbl,new GridBagConstraints(5,i,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,0),0,0));
			centerPnl.add(configModeCombox,new GridBagConstraints(6,i,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,5,0),0,0));
			centerPnl.add(flowControlCombox,new GridBagConstraints(7,i,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,15,5,0),0,0));
			centerPnl.add(discardCombox,new GridBagConstraints(8,i,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,15,5,0),0,0));
			
			rowList.add(0,portLbl); //�˿�
			rowList.add(1,portDesFld); //�˿�����
			rowList.add(2,statusCombox); //״̬
			rowList.add(3,connectStatus);//����״̬
			rowList.add(4,portTypeLbl);//����
			rowList.add(5,currentModeLbl);//��ǰģʽ
			rowList.add(6,configModeCombox);//����ģʽ
			rowList.add(7,flowControlCombox);//����
			rowList.add(8,discardCombox); //����
			
			componentList.add(rowList);
		}
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				centerPnl.revalidate();
			}
		});
	}
	
	/**
	 * ���ø����ؼ���ֵ
	 */
	private void setValue(Set<SwitchPortEntity> portEntitySet){
		viewModel.setDataSet(portEntitySet);
		
//		List<List> dataList = new ArrayList<List>();
//		Iterator iterator = portEntitySet.iterator();
		int status = Constants.ISSUEDADM;
		for (int row = 0 ; row < portEntitySet.size(); row++){
			int port = row+1;
			SwitchPortEntity switchPortEntity = viewModel.getValueAt(port);
			
			String portDesc = switchPortEntity.getDescs();
			boolean worked = switchPortEntity.isWorked();//״̬
			boolean connected = switchPortEntity.isConnected();//����״̬
			int type = switchPortEntity.getType(); //����
			String currentMode = switchPortEntity.getCurrentMode().toLowerCase();//��ǰģʽ
			String configMode =  switchPortEntity.getConfigMode().toLowerCase(); //����ģʽ
			boolean flowControl = switchPortEntity.isFlowControl(); //����
			String abandonSetting = switchPortEntity.getAbandonSetting(); //����
			
			List rowList = componentList.get(row);
			((JLabel)rowList.get(0)).setText(port+""); //�˿�
			((JTextField)rowList.get(1)).setText(portDesc); //�˿�����
			((JComboBox)rowList.get(2)).setSelectedItem(reverseWorkedToString(worked));//״̬
			((JLabel)rowList.get(3)).setText(reverseConnectToString(connected));//����״̬
			((JLabel)rowList.get(4)).setText(reverseTypeToString(type));//����
			if (type == Constants.TX ){//���
				((JComboBox)rowList.get(6)).setEnabled(true);
			}else{//���˿�����Ϊ���ʱ������ģʽֻ��������Ӧ����������ģʽ�ؼ��һ�
				((JComboBox)rowList.get(6)).setEnabled(false);
			}
			
			((JLabel)rowList.get(5)).setText(reverseConfigModeToUpper(currentMode));//��ǰģʽ
			((JComboBox)rowList.get(6)).setSelectedItem(reverseConfigModeToUpper(configMode));//����ģʽ
			((JComboBox)rowList.get(7)).setSelectedItem(reverseFlowToString(flowControl));//����
			((JComboBox)rowList.get(8)).setSelectedItem(reverseCase(abandonSetting));//����
			
			status = switchPortEntity.getIssuedTag();
		}
		
		statusFld.setText(dataStatus.get(status).getKey());
	}

	@ViewAction(name=DOWNLOAD, icon=ButtonConstants.DOWNLOAD, desc="���ض˿�������Ϣ",role=Constants.MANAGERCODE)
	public void download(){
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "��ѡ�񽻻���","��ʾ",JOptionPane.NO_OPTION);
			return;
		}

		int result = PromptDialog.showPromptDialog(this, "��ѡ�����صķ�ʽ",imageRegistry);
		if (result == 0){
			return;
		}
		
		List<Serializable> list = getChangedDataLists();
		
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();

		Task task = new DownLoadRequestTask(list, macValue, result);
		showConfigureMessageDialog(task, "����");
	}
	
	private class DownLoadRequestTask implements Task{

		private List<Serializable> list = null;
		private String macValue = "";
		private int result = 0;
		public DownLoadRequestTask(List<Serializable> list, String macValue, int result){
			this.list = list;
			this.macValue = macValue;
			this.result = result;
		}
		
		@Override
		public void run() {
			try{
				remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.PORTSET, list, 
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("���ض˿�������Ϣ�쳣");
				queryData();
				LOG.error("ConfigurePortView.download() error", e);
			}
			if(this.result == Constants.SYN_SERVER){
				paramConfigStrategy.showNormalMessage(true, Constants.SYN_SERVER);
				queryData();
			}
		}
	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="���ض˿�������Ϣ",role=Constants.MANAGERCODE)
	public void upload(){
		if (null == switchNodeEntity) {
			JOptionPane.showMessageDialog(this, "��ѡ�񽻻���","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		
		final HashSet<SynchDevice> synDeviceList = new HashSet<SynchDevice>();
		SynchDevice synchDevice = new SynchDevice();
		synchDevice.setIpvalue(switchNodeEntity.getBaseConfig().getIpValue());
		synchDevice.setModelNumber(switchNodeEntity.getDeviceModel());
		synDeviceList.add(synchDevice);
		
		Task task = new UploadRequestTask(synDeviceList, MessageNoConstants.SINGLESWITCHPORT);
		showUploadMessageDialog(task, "���ض˿�������Ϣ");
	}
	
	private JProgressBarModel progressBarModel;
	private ParamConfigStrategy paramConfigStrategy;
	private ParamUploadStrategy uploadStrategy;
	private JProgressBarDialog dialog;
	
	private void showConfigureMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			paramConfigStrategy = new ParamConfigStrategy(operation, progressBarModel);
			dialog = new JProgressBarDialog("��ʾ",0,1,this,operation,true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(paramConfigStrategy);
			dialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showConfigureMessageDialog(task, operation);
				}
			});
		}
	}
	
	private void showUploadMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			uploadStrategy = new ParamUploadStrategy(operation, progressBarModel,true);
			dialog = new JProgressBarDialog("��ʾ",0,1,this,operation,true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(uploadStrategy);
			dialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showUploadMessageDialog(task, operation);
				}
			});
		}
	}
	
	private List<Serializable> getChangedDataLists(){
		List<Serializable> list = new ArrayList<Serializable>();
			
		for (int k = 0 ; k < portCount; k++){
			JLabel portLbl = (JLabel)componentList.get(k).get(0);
			int port = Integer.parseInt(portLbl.getText());
				
			SwitchPortEntity portEntity = viewModel.getValueAt(port);
				
			JTextField portDesFld = (JTextField)componentList.get(k).get(1);
			String newPortDes = portDesFld.getText().trim();
				
			JComboBox statusCombox = (JComboBox)componentList.get(k).get(2);
			boolean newWorked = reverseWorkedToBoolean(statusCombox.getSelectedItem().toString());
				
			JComboBox configModeCombox = (JComboBox)componentList.get(k).get(6);
			String newConfigMode = reverseConfigModeToLower(configModeCombox.getSelectedItem().toString());
				
			JComboBox flowCombox = (JComboBox)componentList.get(k).get(7);
			boolean newFlow = reverseFlowToBoolean(flowCombox.getSelectedItem().toString());
				
			JComboBox abandonCombox = (JComboBox)componentList.get(k).get(8);
			String newAbandon = abandonCombox.getSelectedItem().toString().toLowerCase();
				

			portEntity.setDescs(newPortDes);
			portEntity.setWorked(newWorked);
			if (portEntity.getType() == Constants.TX){
				portEntity.setConfigMode(newConfigMode);
			}
			else{
				portEntity.setConfigMode(GE_MODE);
			}
			portEntity.setFlowControl(newFlow);
			portEntity.setAbandonSetting(newAbandon);
			portEntity.setSwitchNode(switchNodeEntity);
			portEntity.setIssuedTag(Constants.ISSUEDADM);
						
			list.add(portEntity);

		}
		return list;
	}
	
	private List<Serializable> getChangedDataList(){
		List<Serializable> list = new ArrayList<Serializable>();
		
		for (int i = 0 ; i < portCount; i++){
			int portNo = i+1;
			SwitchPortEntity portEntity = viewModel.getValueAt(portNo);
			
			String oldPortDes = portEntity.getDescs();
			if (oldPortDes == null){
				oldPortDes = "";
			}
			
			boolean oldWorked = portEntity.isWorked(); //״̬
			String oldConfigMode = portEntity.getConfigMode().toLowerCase();//����ģʽ
			boolean oldFlow = portEntity.isFlowControl();//����
			String oldAbandon = portEntity.getAbandonSetting();//����
			
			for (int k = 0 ; k < portCount; k++){
				JLabel portLbl = (JLabel)componentList.get(k).get(0);
				int port = Integer.parseInt(portLbl.getText());
				
				JTextField portDesFld = (JTextField)componentList.get(k).get(1);
				String newPortDes = portDesFld.getText().trim();
				
				JComboBox statusCombox = (JComboBox)componentList.get(k).get(2);
				boolean newWorked = reverseWorkedToBoolean(statusCombox.getSelectedItem().toString());
				
				JComboBox configModeCombox = (JComboBox)componentList.get(k).get(6);
				String newConfigMode = reverseConfigModeToLower(configModeCombox.getSelectedItem().toString());
				
				JComboBox flowCombox = (JComboBox)componentList.get(k).get(7);
				boolean newFlow = reverseFlowToBoolean(flowCombox.getSelectedItem().toString());
				
				JComboBox abandonCombox = (JComboBox)componentList.get(k).get(8);
				String newAbandon = abandonCombox.getSelectedItem().toString().toLowerCase();
				
				if (portNo == port){
					if ((oldWorked != newWorked)
						|| (!newConfigMode.equals(oldConfigMode)) 
						|| (oldFlow != newFlow) 
						|| (!oldAbandon.equalsIgnoreCase(newAbandon))
						|| (!newPortDes.equalsIgnoreCase(oldPortDes))){
						//˵���˶˿ڵ�ֵ�иı�
						portEntity.setWorked(newWorked);
						if (portEntity.getType() == Constants.TX){
							portEntity.setConfigMode(newConfigMode);
						}
						else{
							portEntity.setConfigMode(GE_MODE);
						}
						portEntity.setFlowControl(newFlow);
						portEntity.setAbandonSetting(newAbandon);
						portEntity.setSwitchNode(switchNodeEntity);
						portEntity.setIssuedTag(Constants.ISSUEDADM);
						
						list.add(portEntity);
						break;
					}
				}
			}
		}
		return list;
	}
	
	private String reverseWorkedToString(boolean bool){
		if (bool){
			return "����";
		}
		else{
			return "����";
		}
	}
	
	private boolean reverseWorkedToBoolean(String str){
		boolean bool = false;
		if (str.equals("����")){
			bool = true;
		}
		else if (str.equals("����")){
			bool = false;
		}
		return bool;
	}
	
	private String reverseConnectToString(boolean bool){
		if (bool){
			return "����";
		}
		else{
			return "�Ͽ�";
		}
	}
	
	private boolean reverseConnectToBoolean(String str){
		boolean bool = false;
		if (str.equals("����")){
			bool = true;
		}
		else if (str.equals("�Ͽ�")){
			bool = false;
		}
		return bool;
	}
	
	private String reverseTypeToString(int type){
		String str ="";
		if (type == Constants.TX){
			str = "���";
		}
		else if (type ==  Constants.FX){
			str = "���";
		}
		return str;
	}
	
	private int reverseTypeToInt(String str){
		int type = 0;
		if ("���".equals(str)){
			type = Constants.TX;
		}
		else if ("���".equals(str)){
			type = Constants.FX;
		}
		return type;
	}
	
	private String reverseFlowToString(boolean flow){
		String str = "";
		if (flow){
			str = "����";
		}
		else{
			str = "����";
		}
		return str;
	}
	
	private boolean reverseFlowToBoolean(String str){
		boolean bool = false;
		if ("����".equals(str)){
			bool = true;
		}
		else if ("����".equals(str)){
			bool = false;
		}
		
		return bool;
	}
	
	private String reverseCase(String str){
		String value = "";
		if ("none".equals(str)){
			value = "None";
		}
		else if ("all".equals(str)){
			value = "All";
		}
		else if ("untag".equals(str)){
			value = "Untag";
		}
		return value;
	}
	
	private String reverseConfigModeToUpper(String str){
		String value = "";
		if ("auto".equals(str)){
			value = "����Ӧ";
		}
		else if ("100fdx".equals(str)){
			value = "100Mȫ˫��";
		}
		else if ("100hdx".equals(str)){
			value = "100M��˫��";
		}
		
		else if ("10fdx".equals(str)){
			value = "10Mȫ˫��";
		}
		else if ("10hdx".equals(str)){
			value = "10M��˫��";
		}
		
		return value;
	}
	
	private String reverseConfigModeToLower(String str){
		String value = "";
		if ("����Ӧ".equals(str)){
			value = "auto";
		}
		else if ("100Mȫ˫��".equals(str)){
			value = "100fdx";
		}
		else if ("100M��˫��".equals(str)){
			value = "100hdx";
		}
		
		else if ("10Mȫ˫��".equals(str)){
			value = "10fdx";
		}
		else if ("10M��˫��".equals(str)){
			value = "10hdx";
		}
		
		return value;
	}
	
	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				queryData();
			}
			else{
				switchNodeEntity = null;
				centerPnl.removeAll();
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						centerPnl.revalidate();
					}
				});
			}
		}		
	};
	
	/**
	 * ���ڽ��յ����첽��Ϣ���д���
	 */
	public void receive(Object object){
	}
	
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		messageDispatcher.removeProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
	}
}