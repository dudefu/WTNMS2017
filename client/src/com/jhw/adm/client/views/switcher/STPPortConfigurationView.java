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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.math.NumberUtils;
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
import com.jhw.adm.client.model.switcher.STPPortConfigModel;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.MessageReceiveInter;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.ParamConfigStrategy;
import com.jhw.adm.client.swing.ParamUploadStrategy;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.STPPortConfig;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(STPPortConfigurationView.ID)
@Scope(Scopes.DESKTOP)
public class STPPortConfigurationView extends ViewPart implements MessageReceiveInter{
	private static final long serialVersionUID = 1L;
	public static final String ID = "stpPortConfigurationView";
	//�϶˵����
	private final JPanel topPnl = new JPanel();
	private final JLabel portLbl = new JLabel();
	private final JLabel stpModeLbl = new JLabel();
	private final JLabel pathLbl = new JLabel();
	private final JLabel portPriorityLbl = new JLabel();
	private final JLabel edgePortLbl = new JLabel();
	private final JLabel p2pPortLbl = new JLabel();
	
	private final JLabel statusLbl = new JLabel();
	private final JTextField statusFld = new JTextField();
	
	//�м����
	private final JPanel mainPanel = new JPanel();
	private final JScrollPane scrollPnl = new JScrollPane();
	private final JPanel middlePnl = new JPanel();
	
	
	//���߰�ť���
	private final JPanel toolBtnPnl = new JPanel();
	private JButton uploadBtn;
	private JButton downloadBtn;
	private JButton closeBtn = null;
	
	private int portCount = 0;
	
	private ButtonFactory buttonFactory;
	private List<STPPortConfig> stpPortConfigList = null;	
	
	private final List<List> componentList = new ArrayList<List>();
	
	private static final String[] PRIORITYLIST = {"0","16","32","48","64","80","96","112"
		,"128","144","160","176","192","208","224","240"};
	
	private static final String[] P2PPORTLIST = {"true","false","auto"};
	
	private SwitchNodeEntity switchNodeEntity = null;
	private MessageSender messageSender;
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Autowired
	@Qualifier(STPPortConfigModel.ID)
	private STPPortConfigModel viewModel;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	private static final Logger LOG = LoggerFactory.getLogger(STPPortConfigurationView.class);
	
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
	
	private final MessageProcessorAdapter messageFedOfflineProcessor = new MessageProcessorAdapter() {//ǰ�û�������
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
	protected void initialize() {
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
		initToolBtnPnl();
		
		this.setLayout(new BorderLayout());
		this.add(topPnl,BorderLayout.NORTH);
		this.add(scrollPnl,BorderLayout.CENTER);
		this.add(toolBtnPnl,BorderLayout.SOUTH);
		
		setResource();
	}
	
	private void initTopPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(portLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,3,0,0),0,0));
		
		panel.add(stpModeLbl,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,35,0,0),0,0));
		panel.add(pathLbl,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,35,0,10),0,0));
		panel.add(portPriorityLbl,new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,40,0,10),0,0));
		panel.add(edgePortLbl,new GridBagConstraints(4,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,43,0,10),0,0));
		panel.add(p2pPortLbl,new GridBagConstraints(5,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,45,0,10),0,0));
		
		topPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		topPnl.add(panel);
	}
	
	private void initCenterPnl(){
		middlePnl.setLayout(new GridBagLayout());

		JPanel centerPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		centerPnl.add(middlePnl);
		
		scrollPnl.getViewport().add(centerPnl);
	}
	
	private void initToolBtnPnl(){
//		toolBtnPnl.setLayout(new BorderLayout());
//		JPanel newPanel = new JPanel(new GridBagLayout());
//		uploadBtn = buttonFactory.createButton(UPLOAD);
//		downloadBtn = buttonFactory.createButton(DOWNLOAD);
//		closeBtn = buttonFactory.createCloseButton();
//		newPanel.add(uploadBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
//		newPanel.add(downloadBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
//		newPanel.add(closeBtn,new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
//		this.setCloseButton(closeBtn);
//		toolBtnPnl.add(newPanel,BorderLayout.EAST);
		
		JPanel statusPnl = new JPanel(new GridBagLayout());
		statusPnl.add(statusLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(10,5,0,0),0,0));
		statusPnl.add(statusFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(10,5,0,0),0,0));
		JPanel leftPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		leftPnl.add(statusPnl);
		
		JPanel newPanel = new JPanel(new GridBagLayout());
		uploadBtn = buttonFactory.createButton(UPLOAD);
		downloadBtn = buttonFactory.createButton(DOWNLOAD);
		closeBtn = buttonFactory.createCloseButton();
		newPanel.add(uploadBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(downloadBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		JPanel rightPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		rightPnl.add(newPanel);
		
		toolBtnPnl.setLayout(new BorderLayout());
		toolBtnPnl.add(leftPnl,BorderLayout.WEST);
		toolBtnPnl.add(rightPnl,BorderLayout.EAST);
	}
	
	private void setResource(){
		portLbl.setText("�˿�");
		stpModeLbl.setText("STPģʽ");
		pathLbl.setText("·������(0��ʾauto״̬)");
		portPriorityLbl.setText("�˿����ȼ�");
		edgePortLbl.setText("��Ե�˿�");
		p2pPortLbl.setText("P2P�˿�");
		
		statusLbl.setText("״̬");
		statusFld.setColumns(15);
		statusFld.setBackground(Color.WHITE);
		statusFld.setEditable(false);
		
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

//		if(switchNodeEntity.getPorts().size() == 0){
//			return;
//		}
		
		//�˿�����
		portCount = switchNodeEntity.getPorts().size();
		//��ʼ���������
		setCenterLayout();
		
		String where = " where entity.switchNode=?";
		Object[] parms = {switchNodeEntity};
		stpPortConfigList = (List<STPPortConfig>)remoteServer.getService().findAll(STPPortConfig.class, where, parms);
		if (null == stpPortConfigList || stpPortConfigList.size() <1){
			return;
		}
		
		//���ÿؼ���ֵ
		setValue(stpPortConfigList);
	}
	
	/**
	 * ͨ����ѯ���Ķ˿��б���panel�����пؼ�
	 */
	private void setCenterLayout(){
		componentList.clear();
		middlePnl.removeAll();
		
		for(int i = 0 ; i < portCount ; i++){
			JLabel portLbl = new JLabel();//�˿�
			portLbl.setText(""+(i+1));
			
			JCheckBox stpModeChkbox = new JCheckBox(); //stpģʽ
			
//			JTextField pathPayFld = new JTextField();//·������
			NumberField pathPayFld = new NumberField(10,0,0,2000000000,true);
			pathPayFld.setColumns(20);
			pathPayFld.setHorizontalAlignment(SwingConstants.CENTER);
			
			JComboBox priorityComBox = new JComboBox(); //�˿����ȼ�
			priorityComBox.setPreferredSize(new Dimension(80,priorityComBox.getPreferredSize().height));
			for(int j = 0 ; j < PRIORITYLIST.length ; j++){
				priorityComBox.addItem(PRIORITYLIST[j]);
			}
			
			JCheckBox edgePortChkBox = new JCheckBox(); //��Ե�˿�
			edgePortChkBox.setHorizontalAlignment(SwingConstants.CENTER);
			
			JComboBox p2pPortComBox = new JComboBox();  //P2P�˿�
			p2pPortComBox.setPreferredSize(new Dimension(80,p2pPortComBox.getPreferredSize().height));
			for(int k = 0 ; k < P2PPORTLIST.length; k++){
				p2pPortComBox.addItem(P2PPORTLIST[k]);
			}

			middlePnl.add(portLbl,new GridBagConstraints(0,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,10,0,0),0,0));
			middlePnl.add(stpModeChkbox,new GridBagConstraints(1,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,53,0,0),0,0));
			middlePnl.add(pathPayFld,new GridBagConstraints(2,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,53,0,0),0,0));
			middlePnl.add(priorityComBox,new GridBagConstraints(3,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,53,0,0),0,0));
			middlePnl.add(edgePortChkBox,new GridBagConstraints(4,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,53,0,0),0,0));
			middlePnl.add(p2pPortComBox,new GridBagConstraints(5,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,53,0,0),0,0));
			
			List<JComponent> rowList = new ArrayList<JComponent>();
			rowList.add(0,portLbl); //�˿�
			rowList.add(1,stpModeChkbox); //stpģʽ
			rowList.add(2,pathPayFld);//·������
			rowList.add(3,priorityComBox);//�˿����ȼ�
			rowList.add(4,edgePortChkBox);//��Ե�˿�
			rowList.add(5,p2pPortComBox);//P2P�˿�
			
			componentList.add(rowList);
		}
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				middlePnl.revalidate();
			}
		});
	}
	
	/**
	 * ���ø����ؼ���ֵ
	 */
	private void setValue(List<STPPortConfig> stpPortConfigList){
		viewModel.setStpPortConfigList(stpPortConfigList);
		for(int row = 0 ; row < stpPortConfigList.size(); row++){
			int port = row + 1 ;        //�˿�
			STPPortConfig stpPortConfig = viewModel.getValueAt(port);
			
			boolean stpMode = stpPortConfig.isStpModed();  //STPģʽ
			int pathPay = stpPortConfig.getLujingkaixiao(); //·������
			int preLevel = stpPortConfig.getPrelevel(); //�˿����ȼ�
			boolean edgePort = stpPortConfig.isEdgePorted();//��Ե�˿�
			int p2pPort = stpPortConfig.getP2pPort(); //P2P�˿�
			
			if (componentList.size() >= (row+1)){
				List rowList = componentList.get(row);
				((JLabel)rowList.get(0)).setText(port+""); //�˿�
				((JCheckBox)rowList.get(1)).setSelected(stpMode); //STPģʽ
				((NumberField)rowList.get(2)).setText(""+pathPay); //·������ 
				((JComboBox)rowList.get(3)).setSelectedItem(preLevel+"");//�˿����ȼ�
				((JCheckBox)rowList.get(4)).setSelected(edgePort);//��Ե�˿�
				((JComboBox)rowList.get(5)).setSelectedItem(reverseIntToString(p2pPort));//P2P�˿�
			}
			
			statusFld.setText(dataStatus.get(stpPortConfig.getIssuedTag()).getKey());
		}
	}
	
	@ViewAction(name=DOWNLOAD, icon=ButtonConstants.DOWNLOAD, desc="����STP�˿�������Ϣ",role=Constants.MANAGERCODE)
	public void download(){
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "��ѡ�񽻻���","��ʾ",JOptionPane.NO_OPTION);
			return;
		}
		
		//�õ�STPPortConfig
		List<Serializable> stpPortConfigList = getSTPPortConfigLists();
		
		for(int i = 0;i < stpPortConfigList.size(); i++){
			STPPortConfig stpPortConfig = (STPPortConfig) stpPortConfigList.get(i);
			if(stpPortConfig.getLujingkaixiao() < 0 || stpPortConfig.getLujingkaixiao() > 2000000000){
				JOptionPane.showMessageDialog(this, "�˿�" + stpPortConfig.getPortNo() + "·���������󣬷�ΧΪ��0-2000000000", "��ʾ", JOptionPane.NO_OPTION);
				return;
			}
		}

		int result = PromptDialog.showPromptDialog(this, "��ѡ�����صķ�ʽ",imageRegistry);
		if (result == 0){
			return;
		}
		
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();

		Task task = new DownLoadRequestTask(stpPortConfigList, macValue, result);
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
				remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.STPPORTCONFIGSET, list, 
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(), Constants.DEV_SWITCHER2,result);
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("����STP�˿�������Ϣ");
				queryData();
				LOG.error("STPPortConfigurationView.download() error", e);
			}
			if(this.result == Constants.SYN_SERVER){
				paramConfigStrategy.showNormalMessage(true, Constants.SYN_SERVER);
				queryData();
			}
		}
	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="����STP�˿�������Ϣ",role=Constants.MANAGERCODE)
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
		
		Task task = new UploadRequestTask(synDeviceList, MessageNoConstants.SINGLESWITCHSTPPORT);
		showUploadMessageDialog(task, "����STP�˿�������Ϣ");
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
	
	private List<Serializable> getSTPPortConfigList(){
		List<Serializable> list = new ArrayList<Serializable>();
		for (int i = 0 ; i < portCount; i++){
			int portNo = i+1;    //�˿�
			STPPortConfig stpPortConfig = viewModel.getValueAt(portNo);
			if(null == stpPortConfig){
				stpPortConfig = new STPPortConfig();
			}
			
			boolean oldStpMode = stpPortConfig.isStpModed();//STPģʽ
			int oldPathPay = stpPortConfig.getLujingkaixiao();//·������
			int oldPreLevel = stpPortConfig.getPrelevel();//�˿����ȼ�
			boolean oldEdgePort = stpPortConfig.isEdgePorted();//��Ե�˿�
			int oldP2pPort = stpPortConfig.getP2pPort();//P2P�˿�
			
			for (int k = 0 ; k < portCount; k++){
				JLabel portLbl = (JLabel)componentList.get(k).get(0);
				int port = NumberUtils.toInt(portLbl.getText());
				
				JCheckBox stpModeChkbox = (JCheckBox)componentList.get(k).get(1);
				boolean newStpMode = stpModeChkbox.isSelected();
				
				NumberField pathPayFld = (NumberField)componentList.get(k).get(2);
				//·������Ϊ�յĻ���newPathPayΪ-1
				int newPathPay = -1;
				if(!"".equals(pathPayFld.getText().trim())){
					newPathPay = NumberUtils.toInt(pathPayFld.getText());
				}

				JComboBox priorityComBox = (JComboBox)componentList.get(k).get(3);
				int newPreLevel = NumberUtils.toInt(priorityComBox.getSelectedItem().toString());
				
				JCheckBox edgePortChkBox = (JCheckBox)componentList.get(k).get(4);
				boolean newEdgePort = edgePortChkBox.isSelected();
				
				JComboBox p2pPortComBox = (JComboBox)componentList.get(k).get(5);
				int newP2pPort = reverseStringToInt(p2pPortComBox.getSelectedItem().toString());
				
				
				if (portNo == port){
					if ((oldStpMode != newStpMode)
						|| oldPathPay != newPathPay
						|| oldPreLevel != newPreLevel 
						|| oldEdgePort != newEdgePort
						|| oldP2pPort != newP2pPort){
						//˵���˶˿ڵ�ֵ�иı�
						stpPortConfig.setStpModed(newStpMode);
						stpPortConfig.setLujingkaixiao(newPathPay);
						stpPortConfig.setPrelevel(newPreLevel);
						stpPortConfig.setEdgePorted(newEdgePort);
						stpPortConfig.setP2pPort(newP2pPort);
						stpPortConfig.setSwitchNode(switchNodeEntity);

						list.add(stpPortConfig);
						break;
					}
				}
			}
		}
		
		return list;
	}
	
	private List<Serializable> getSTPPortConfigLists(){
		List<Serializable> list = new ArrayList<Serializable>();
		for (int k = 0 ; k < portCount; k++){
			int portNo = k+1;    //�˿�
			STPPortConfig stpPortConfig = viewModel.getValueAt(portNo);
			if(null == stpPortConfig){
				stpPortConfig = new STPPortConfig();
			}
				
			JCheckBox stpModeChkbox = (JCheckBox)componentList.get(k).get(1);
			boolean newStpMode = stpModeChkbox.isSelected();
				
			NumberField pathPayFld = (NumberField)componentList.get(k).get(2);
			//·������Ϊ�յĻ���newPathPayΪ-1
			int newPathPay = -1;
			if(!"".equals(pathPayFld.getText().trim())){
				newPathPay = NumberUtils.toInt(pathPayFld.getText());
			}

			JComboBox priorityComBox = (JComboBox)componentList.get(k).get(3);
			int newPreLevel = NumberUtils.toInt(priorityComBox.getSelectedItem().toString());
				
			JCheckBox edgePortChkBox = (JCheckBox)componentList.get(k).get(4);
			boolean newEdgePort = edgePortChkBox.isSelected();
				
			JComboBox p2pPortComBox = (JComboBox)componentList.get(k).get(5);
			int newP2pPort = reverseStringToInt(p2pPortComBox.getSelectedItem().toString());
	
			//˵���˶˿ڵ�ֵ�иı�
			stpPortConfig.setPortNo(portNo);
			stpPortConfig.setStpModed(newStpMode);
			stpPortConfig.setLujingkaixiao(newPathPay);
			stpPortConfig.setPrelevel(newPreLevel);
			stpPortConfig.setEdgePorted(newEdgePort);
			stpPortConfig.setP2pPort(newP2pPort);
			stpPortConfig.setIssuedTag(Constants.ISSUEDADM);
			stpPortConfig.setSwitchNode(switchNodeEntity);

			list.add(stpPortConfig);
		}
		return list;
	}
	
	private String reverseIntToString(int value){
		String str = "";
		switch(value){
			case 0:
				str = "auto";
				break;
			case 1:
				str = "true";
				break;
			case 2:
				str = "false";
				break;
		}
		
		return str;
	}
	
	private int reverseStringToInt(String str){
		int value = 0;
		if ("auto".equalsIgnoreCase(str)){
			value = 0;
		}
		else if ("true".equalsIgnoreCase(str)){
			value = 1;
		}
		else if("false".equalsIgnoreCase(str)){
			value = 2;
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
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						middlePnl.removeAll();
						middlePnl.revalidate();
					}
				});
			}
		}
	};
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	
	/**
	 * ���ڽ��յ����첽��Ϣ���д���
	 */
	public void receive(Object object){
	}
	
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		messageDispatcher.removeProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
	}
}
