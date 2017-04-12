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
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
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
import com.jhw.adm.client.model.switcher.LacpPortViewModel;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.swing.MessageReceiveInter;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.TextFieldPlainDocument;
import com.jhw.adm.client.swing.UploadMessageProcessorStrategy;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.ports.LACPConfig;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(LacpPortAggrView.ID)
@Scope(Scopes.DESKTOP)
public class LacpPortAggrView extends ViewPart implements MessageReceiveInter{
	public static final String ID = "lacpPortAggrView";
	
	//上端的面板
	private final JPanel topPnl = new JPanel();
	private final JLabel portTopLbl = new JLabel();
	private final JLabel modeTopLbl = new JLabel();
	private final JLabel keyTopLbl = new JLabel();
	private final JLabel roleTopLbl = new JLabel();
	
	//中间面板
	private final JPanel mainPanel = new JPanel();
	private final JPanel centerPnl = new JPanel();
	private final JScrollPane scrollTablePnl = new JScrollPane();
	private static final String[] ROLELIST = {"主动","被动"};
	private final List<List> componentList = new ArrayList<List>();
	private final JLabel statusLbl = new JLabel();
	private final JTextField statusFld = new JTextField();
	
	//下端的按钮面板
	private final JPanel bottomPnl = new JPanel();
	private JButton uploadBtn ;
	private JButton downloadBtn;
	private JButton closeBtn = null;
	
	private MessageSender messageSender;
	
	private int portCount;
	
	private SwitchNodeEntity switchNodeEntity = null;
	
	private ButtonFactory buttonFactory;
	
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
	@Qualifier(LacpPortViewModel.ID)
	private LacpPortViewModel viewModel;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	private final MessageOfSwitchConfigProcessorStrategy messageOfSaveProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	private final UploadMessageProcessorStrategy uploadMessageProcessorStrategy = new UploadMessageProcessorStrategy();
	
	private static final Logger LOG = LoggerFactory.getLogger(LacpPortAggrView.class);
	
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
	
	private final MessageProcessorAdapter messageFepOffLineProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(TextMessage message){
			queryData();
		}
	};
	
	/**
	 * 视图初始化
	 */
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
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFepOffLineProcessor);
		
		initTopPnl();
		initCenterPnl();
		initBottomPnl();
		
		this.setLayout(new BorderLayout());
		this.add(topPnl,BorderLayout.NORTH);
		this.add(mainPanel,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		this.setViewSize(500,480);
		
		setResource();
	}
	
	private void initTopPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(portTopLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,0,0,0),0,0));
		panel.add(modeTopLbl,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,75,0,0),0,0));
		panel.add(keyTopLbl,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,125,0,0),0,0));
		panel.add(roleTopLbl,new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,160,0,0),0,0));

		topPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		topPnl.add(panel);
	}
	
	private void initCenterPnl(){
		JPanel listPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		listPnl.add(centerPnl);
		scrollTablePnl.getViewport().add(listPnl);
		
//		JPanel statusPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
//		JPanel panel = new JPanel(new GridBagLayout());
//		panel.add(statusLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
//				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,0),0,0));
//		panel.add(statusFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
//				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,40,0,0),0,0));
//		statusPnl.add(panel);
		
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(scrollTablePnl,BorderLayout.CENTER);
//		mainPanel.add(statusPnl,BorderLayout.SOUTH);
	
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
		uploadBtn = buttonFactory.createButton(UPLOAD);
		downloadBtn = buttonFactory.createButton(DOWNLOAD);
		closeBtn = buttonFactory.createCloseButton();
		newPanel.add(uploadBtn,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(downloadBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		JPanel rightPnl = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		rightPnl.add(newPanel);
		
		bottomPnl.setLayout(new BorderLayout());
		bottomPnl.add(leftPnl, BorderLayout.WEST);
		bottomPnl.add(rightPnl, BorderLayout.EAST);
	}
	
	private void setResource(){
		this.setTitle("LACP端口聚合");
		this.setBorder(BorderFactory.createTitledBorder("LACP配置"));
		
		portTopLbl.setText("端口");
		modeTopLbl.setText("模式");
		keyTopLbl.setText("密钥");
		roleTopLbl.setText("角色");
		
		statusLbl.setText("状态");
		statusFld.setColumns(15);
		statusFld.setEditable(false);
		statusFld.setBackground(Color.WHITE);
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}
	
	/**
	 * 查询服务端数据
	 */
	private void queryData(){
		switchNodeEntity = (SwitchNodeEntity) adapterManager.getAdapter(
				equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		
		if (null == switchNodeEntity){
			return ;
		}
		
		//布局中间的控件面板
		setCenterPnlLayout();
		
		String where = " where entity.switchNode=?";
		Object[] parms = {switchNodeEntity};
		List<LACPConfig> lacpConfigList = (List<LACPConfig>)remoteServer.getService().findAll(LACPConfig.class, where, parms);
		if (null == lacpConfigList ){
			return;
		}
		
		//设置控件的值
		setValue(lacpConfigList);
	}
	
	/**
	 * 布局中间的控件面板
	 */
	@SuppressWarnings("unchecked")
	private void setCenterPnlLayout(){
		centerPnl.setLayout(new GridBagLayout());
		
		componentList.clear();
		centerPnl.removeAll();
		portCount = switchNodeEntity.getPorts().size();
		
		for(int i = 0 ; i < portCount ; i++){
			JLabel label = new JLabel(i+1+"");

			JCheckBox modeChkBox = new JCheckBox();
			modeChkBox.setHorizontalAlignment(SwingConstants.CENTER);
			
			JTextField textFld = new JTextField();
			textFld.setHorizontalAlignment(SwingConstants.CENTER);
			textFld.setColumns(20);
			textFld.setDocument(new TextFieldPlainDocument(textFld));
			
			JComboBox roleComBox = new JComboBox();
			roleComBox.setPreferredSize(new Dimension(100,roleComBox.getPreferredSize().height));
			for(int j = 0 ; j < ROLELIST.length ; j++){
				roleComBox.addItem(ROLELIST[j]);
			}
			centerPnl.add(label,new GridBagConstraints(0,i,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,10,5,0),0,0));
			centerPnl.add(modeChkBox,new GridBagConstraints(1,i,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,80,5,0),0,0));
			centerPnl.add(textFld,new GridBagConstraints(2,i,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,80,5,0),0,0));
			centerPnl.add(roleComBox,new GridBagConstraints(3,i,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,80,5,0),0,0));
			
			List rowList = new ArrayList();
			rowList.add(0,label);
			rowList.add(1,modeChkBox);
			rowList.add(2,textFld);
			rowList.add(3,roleComBox);
			componentList.add(rowList);
		}
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				centerPnl.revalidate();
			}
		});
	}
	
	/**
	 * 设置控件的值
	 */
	private void setValue(List<LACPConfig> lacpConfigList){
		viewModel.setLacpConfigList(lacpConfigList);
		int status = Constants.ISSUEDADM;
		for (int i = 0 ; i < componentList.size(); i++){
			int port = i+1;//端口
			
			LACPConfig lacpConfig = viewModel.getLACPConfig(port);
			boolean mode = lacpConfig.isApplied();//模式
			
			String key = lacpConfig.getApplyKey(); //密钥
			
			int role = lacpConfig.getApplyRole();
			String roleStr = reverseIntToString(role);//角色
			
			((JLabel)componentList.get(i).get(0)).setText(port+"");
			((JCheckBox)componentList.get(i).get(1)).setSelected(mode);
			((JTextField)componentList.get(i).get(2)).setText(key);
			((JComboBox)componentList.get(i).get(3)).setSelectedItem(roleStr);
			
			status = lacpConfig.getIssuedTag();
		}
		
		statusFld.setText(dataStatus.get(status).getKey());
	}
	
	/**
	 * 保存操作
	 */
	@ViewAction(name=DOWNLOAD, icon=ButtonConstants.DOWNLOAD, desc="下载LACP端口配置信息",role=Constants.MANAGERCODE)
	public void download(){
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return ;
		}
		
		List<Serializable> list = getLacpConfigValues();
		for (int i = 0; i < list.size(); i++){
			LACPConfig lacpConfig = (LACPConfig)list.get(i);
			String portStr = String.valueOf(lacpConfig.getPortNo());
			String keyStr = lacpConfig.getApplyKey();
			
			if (StringUtils.isNumeric(keyStr)){
				if (NumberUtils.toInt(keyStr) < 1 || NumberUtils.toInt(keyStr) > 65535){
					JOptionPane.showMessageDialog(this, "端口" + portStr + "的key错误，只能是数字(1-65535)和auto","提示",JOptionPane.NO_OPTION);
					return ;
				}
			}
			else{
				if (!keyStr.equalsIgnoreCase("auto")){
					JOptionPane.showMessageDialog(this, "端口" + portStr + "的key错误，只能是数字(1-65535)和auto","提示",JOptionPane.NO_OPTION);
					return ;
				}
			}
		}
		
		int result = PromptDialog.showPromptDialog(this, "请选择下载的方式",imageRegistry);
		if (result == 0){
			return ;
		}
		
		//下发服务端
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		showMessageDownloadDialog();
		try {
			remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.LACPCONFIG, list, 
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
		} catch (JMSException e) {
			messageOfSaveProcessorStrategy.showErrorMessage("下载出现异常");
			LOG.error("LacpPortAggrView.save() error:{}", e);
			queryData();
		}
		if(result == Constants.SYN_SERVER){
			messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
			queryData();
		}
	}
	
	private void showMessageDownloadDialog(){
		messageOfSaveProcessorStrategy.showInitializeDialog("下载",this);
	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="上载LACP端口配置信息",role=Constants.MANAGERCODE)
	public void upload(){
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
				message.setIntProperty(Constants.MESSPARMTYPE, MessageNoConstants.SINGLESWITCHLACP);
				return message;
			}
		});
	}
	
	/**
	 * 得到控件中输入的值，组合成list
	 * @return
	 */
	private List<Serializable> getLacpConfigValues(){
		List<Serializable> lists = new ArrayList<Serializable>();
		
		for(int i = 0 ; i < componentList.size(); i++){
			int port = NumberUtils.toInt(((JLabel)componentList.get(i).get(0)).getText());
			boolean mode = ((JCheckBox)componentList.get(i).get(1)).isSelected();
			String key = ((JTextField)componentList.get(i).get(2)).getText().toLowerCase();
			if ("0".equals(key)){
				key = "auto";
			}
			
			String roleStr = ((JComboBox)componentList.get(i).get(3)).getSelectedItem().toString();
			int role = reverseStringToInt(roleStr);

			LACPConfig lacpConfig = viewModel.getLACPConfig(port);
			lacpConfig.setPortNo(port);
			lacpConfig.setApplied(mode);
			lacpConfig.setApplyKey(key);
			lacpConfig.setApplyRole(role);
			lacpConfig.setSwitchNode(switchNodeEntity);
			lacpConfig.setIssuedTag(Constants.ISSUEDADM);
			
			lists.add(lacpConfig);
		}
		
		return lists;
	}
	
	private List<Serializable> getLacpConfigValue(){
		List<Serializable> list = new ArrayList<Serializable>();
		for (int k = 0 ; k < portCount; k++){
			int portNo = k+1;
			LACPConfig lacpConfig = viewModel.getLACPConfig(portNo);
			boolean oldMode = lacpConfig.isApplied();
			String oldKey = lacpConfig.getApplyKey();
			int oldRole = lacpConfig.getApplyRole();
			
			for(int i = 0 ; i < portCount; i++){
				int port = NumberUtils.toInt(((JLabel)componentList.get(i).get(0)).getText());
				boolean newMode = ((JCheckBox)componentList.get(i).get(1)).isSelected();
				String newKey = ((JTextField)componentList.get(i).get(2)).getText().toLowerCase();
				
				String roleStr = ((JComboBox)componentList.get(i).get(3)).getSelectedItem().toString();
				int newRole = reverseStringToInt(roleStr);
				
				if (portNo == port){
					if ((oldMode != newMode)
						|| (!oldKey.equals(newKey)) 
						|| (oldRole != newRole)){
						//说明此端口的值有改变
						lacpConfig.setApplied(newMode);
						lacpConfig.setApplyKey(newKey);
						lacpConfig.setApplyRole(newRole);
						lacpConfig.setSwitchNode(switchNodeEntity);
						list.add(lacpConfig);
						break;
					}
				}
			}
		}
		
		return list;
	}

	private String reverseIntToString(int value){
		String str = "";
		switch(value){
			case 0:
				str = "主动";
				break;
			case 1:
				str = "被动";
				break;
		}
		
		return str;
	}
	
	private int reverseStringToInt(String str){
		int value = 0;
		if ("主动".equals(str)){
			value = 0;
		}
		else if ("被动".equals(str)){
			value = 1;
		}
		
		return value;
	}
	
	private void clear(){
		centerPnl.removeAll();
		componentList.clear();
	}
	/**
	 * 设备浏览器监听事件
	 * @author Administrator
	 */
	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				clear();
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
	 * 对于接收到的异步消息进行处理
	 */
	public void receive(Object object){
	}
	
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		messageDispatcher.removeProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFepOffLineProcessor);
	}
}
