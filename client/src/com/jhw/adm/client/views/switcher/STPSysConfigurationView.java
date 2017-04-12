package com.jhw.adm.client.views.switcher;

import static com.jhw.adm.client.core.ActionConstants.DOWNLOAD;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
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
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.MessageReceiveInter;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.ParamConfigStrategy;
import com.jhw.adm.client.swing.ParamUploadStrategy;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.switchs.STPSysConfig;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(STPSysConfigurationView.ID)
@Scope(Scopes.DESKTOP)
public class STPSysConfigurationView extends ViewPart implements MessageReceiveInter{
	private static final long serialVersionUID = 1L;
	public static final String ID = "stpSysConfigurationView";
	
	private final JPanel configPnl = new JPanel();
	private final JLabel priorityLbl = new JLabel();
	private final JComboBox priorityCombox = new JComboBox();
	
	private final JLabel agingLbl = new JLabel();
	private final NumberField agingFld = new NumberField(3,0,1,58,true);
//	private final JLabel agingTimeLabl = new JLabel();
	
	private final JLabel timeoutLbl = new JLabel();
	private final NumberField timeoutFld = new NumberField(2,0,1,30,true);
//	private final JLabel timeoutTimeLabl = new JLabel();
	
	private final JLabel versionLbl = new JLabel();
	private final JComboBox versionBox = new JComboBox();
	
	private final JLabel statusLbl = new JLabel();
	private final JTextField statusFld = new JTextField();
	
	private final static String[] PRIORITYLIST= {"0","4096","8192","12288","16384","20480","24576","28672"
				,"32768","36864","40960","45056","49152","53248","57344","61440"};
	
	private final static String[] VERSIONLIST= {"STP","RSTP"};
	private static final String AGEING_TIME_RANGE = "6-58s";
	private static final String DELAY_TIME_RANGE = "4-30s";
	
	private static final Logger LOG = LoggerFactory.getLogger(STPSysConfigurationView.class);
	
	//工具按钮面板
	private final JPanel toolBtnPnl = new JPanel();
	private JButton uploadBtn;
	private JButton downloadBtn;
	private JButton closeBtn = null;
	
	private STPSysConfig stpSysConfig = null;
	
	private SwitchNodeEntity switchNodeEntity = null;
	
	private ButtonFactory buttonFactory;
	
	private Object objectEntity = new Object();
	private MessageSender messageSender;
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
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
	protected void initialize() {
		init();
		queryData();
	}
	
	/**
	 * 初始化
	 */
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		messageSender = remoteServer.getMessageSender();
		messageDispatcher.addProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
		
		initConfigPnl();
		initToolBtnPnl();

		this.setLayout(new BorderLayout());
		this.add(configPnl,BorderLayout.CENTER);
		this.add(toolBtnPnl,BorderLayout.SOUTH);
		
		setResource();
	}
	
	private void initConfigPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(priorityLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(priorityCombox,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
		panel.add(new StarLabel(),new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,0),0,0));
		
		panel.add(agingLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,	5,10,10),0,0));
		panel.add(agingFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
//		panel.add(agingTimeLabl,new GridBagConstraints(2,1,1,1,0.0,0.0,
//				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,2,10,0),0,0));
		panel.add(new StarLabel("(" + AGEING_TIME_RANGE + ")"),new GridBagConstraints(2,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,10,0),0,0));
		
		panel.add(timeoutLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(timeoutFld,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
//		panel.add(timeoutTimeLabl,new GridBagConstraints(2,2,1,1,0.0,0.0,
//				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,2,10,0),0,0));
		panel.add(new StarLabel("(" + DELAY_TIME_RANGE + ")"),new GridBagConstraints(2,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,10,0),0,0));
		
		panel.add(versionLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(versionBox,new GridBagConstraints(1,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
		panel.add(new StarLabel(),new GridBagConstraints(2,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,0),0,0));
		
		panel.add(statusLbl,new GridBagConstraints(0,4,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,10),0,0));
		panel.add(statusFld,new GridBagConstraints(1,4,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,0,0),0,0));
		
		configPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		configPnl.add(panel);
	}

	private void initToolBtnPnl(){
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
	
	/**
	 * 设置资源文件
	 */
	private void setResource(){
		for(int i = 0 ; i < PRIORITYLIST.length; i++){
			priorityCombox.addItem(PRIORITYLIST[i]);
		}
		for(int j = 0 ; j < VERSIONLIST.length ; j++){
			versionBox.addItem(VERSIONLIST[j]);
		}
		
		agingFld.setColumns(25);
		
		priorityLbl.setText("系统优先级");
		agingLbl.setText("最大老化时间");
//		agingTimeLabl.setText("(6-200s)");
		timeoutLbl.setText("转发延时");
//		timeoutTimeLabl.setText("(4-30s)");
		versionLbl.setText("协议版本");
		
		statusLbl.setText("状态");
		statusFld.setColumns(15);
		statusFld.setEditable(false);
		statusFld.setBackground(Color.WHITE);
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}
	
	/**
	 * 设置控件的值
	 * @param stpSysConfigList
	 */
	private void setValues(List<STPSysConfig> stpSysConfigList){
		stpSysConfig = stpSysConfigList.get(0);
		final String pre = String.valueOf(stpSysConfig.getPre());//系统优先级
		String maxOldTime = String.valueOf(stpSysConfig.getMaxOldTime()); //老化时间
		String delay = String.valueOf(stpSysConfig.getTransferDelay());//转发延时
		final String version = stpSysConfig.getProtocolVersion();//协议版本
		
//		priorityCombox.setSelectedItem(pre);
		agingFld.setText(maxOldTime);
		timeoutFld.setText(delay);
//		versionBox.setSelectedItem(version.toUpperCase());
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				priorityCombox.setSelectedItem(pre);
				versionBox.setSelectedItem(version.toUpperCase());
				priorityCombox.revalidate();
				versionBox.revalidate();
			}
		});
		
		statusFld.setText(dataStatus.get(stpSysConfig.getIssuedTag()).getKey());
	}
	
	/**
	 * 从服务器查询设备的vlan信息
	 */
	@SuppressWarnings("unchecked")
	private void queryData(){
		switchNodeEntity =
			(SwitchNodeEntity)adapterManager.getAdapter(
					equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		
		if (null == switchNodeEntity){
			return ;
		}
		
		String where = " where entity.switchNode=?";
		Object[] parms = {switchNodeEntity};
		
		List<STPSysConfig> stpSysConfigList = (List<STPSysConfig>)remoteServer.getService().findAll(STPSysConfig.class, where, parms);
		if (null == stpSysConfigList || stpSysConfigList.size() <1){
			return;
		}
		//设置控件上的值
		setValues(stpSysConfigList);
	}
	
	/**
	 * 保存按钮事件
	 */
	@ViewAction(name=DOWNLOAD, icon=ButtonConstants.DOWNLOAD, desc="下载STP系统配置信息",role=Constants.MANAGERCODE)
	public void download(){
		//Amend 2010.06.03
		if(!isValids()){
			return;
		}
		
		int result = PromptDialog.showPromptDialog(this, "请选择下载的方式",imageRegistry);
		if (result == 0){
			return;
		}
		
		List<Serializable> list = new ArrayList<Serializable>();
		//组合STPSysConfig
		long preirity = Long.parseLong(priorityCombox.getSelectedItem().toString()); //系统优先级
		int aging = Integer.parseInt(agingFld.getText()); //最大老化时间
		int delay = Integer.parseInt(timeoutFld.getText());//转发延时
		String version = versionBox.getSelectedItem().toString();//协议版本
		
		if(null == stpSysConfig){
			stpSysConfig = new STPSysConfig();
		}
		stpSysConfig.setPre(preirity);
		stpSysConfig.setMaxOldTime(aging);
		stpSysConfig.setTransferDelay(delay);
		stpSysConfig.setProtocolVersion(version.toLowerCase());
		stpSysConfig.setSwitchNode(switchNodeEntity);
		stpSysConfig.setIssuedTag(Constants.ISSUEDADM);
		list.add(stpSysConfig);
		
		String macValue = switchNodeEntity.getBaseInfo().getMacValue();
		
		Task task = new DownLoadRequestTask(list, macValue, result);
		showConfigureMessageDialog(task, "下载");
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
				remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.STPSYSCONFIGSET, list, 
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(), Constants.DEV_SWITCHER2,result);
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("下载STP系统配置异常");
				queryData();
				LOG.error("STPSysConfigurationView.download() error", e);
			}
			if(this.result == Constants.SYN_SERVER){
				paramConfigStrategy.showNormalMessage(true, Constants.SYN_SERVER);
				queryData();
			}
		}
	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="上载STP系统配置信息",role=Constants.MANAGERCODE)
	public void upload(){
		if (null == switchNodeEntity) {
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		final HashSet<SynchDevice> synDeviceList = new HashSet<SynchDevice>();
		SynchDevice synchDevice = new SynchDevice();
		synchDevice.setIpvalue(switchNodeEntity.getBaseConfig().getIpValue());
		synchDevice.setModelNumber(switchNodeEntity.getDeviceModel());
		synDeviceList.add(synchDevice);
		
		Task task = new UploadRequestTask(synDeviceList, MessageNoConstants.SINGLESWITCHSTPSYS);
		showUploadMessageDialog(task, "上载STP系统配置信息");
	}
	
	private JProgressBarModel progressBarModel;
	private ParamConfigStrategy paramConfigStrategy;
	private ParamUploadStrategy uploadStrategy;
	private JProgressBarDialog dialog;
	
	private void showConfigureMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			paramConfigStrategy = new ParamConfigStrategy(operation, progressBarModel);
			dialog = new JProgressBarDialog("提示",0,1,this,operation,true);
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
			dialog = new JProgressBarDialog("提示",0,1,this,operation,true);
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

	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			synchronized(objectEntity){
				clear();
				if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
					queryData();
				}
			}
			
		}
	};
	
	private void clear(){
		agingFld.setText("");
		timeoutFld.setText("");
		switchNodeEntity = null;
	}
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	
	/**
	 * 当数据下发到设备侧和网管侧时
	 * 对于接收到的异步消息进行处理
	 */
	public void receive(Object object){
	}
	
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		messageDispatcher.removeProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFedOfflineProcessor);
//		if(null != context){
//			context.removeProcessor();
//		}
	}
	
	//Amend 2010.06.03
	public boolean isValids()
	{
		boolean isValid = true;
		
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if(null == priorityCombox.getSelectedItem() || "".equals(priorityCombox.getSelectedItem().toString()))
		{
			JOptionPane.showMessageDialog(this, "系统优先级不能为空，请选择系统优先级","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == agingFld.getText() || "".equals(agingFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "最大老化时间错误，范围是：6-58s","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		else if ((NumberUtils.toInt(agingFld.getText()) < 6)
						|| (NumberUtils.toInt(agingFld.getText()) > 200))
		{
			JOptionPane.showMessageDialog(this, "最大老化时间错误，范围是：6-58s","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == timeoutFld.getText() || "".equals(timeoutFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "转发延时错误，范围是：4-30s","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		else if((NumberUtils.toInt(timeoutFld.getText()) < 4) || (NumberUtils.toInt(timeoutFld.getText()) > 30))
		{
			JOptionPane.showMessageDialog(this, "转发延时错误，范围是：4-30s","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == versionBox.getSelectedItem() || "".equals(versionBox.getSelectedItem().toString()))
		{
			JOptionPane.showMessageDialog(this, "协议版本不能为空，请选择协议版本","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		int aging = NumberUtils.toInt(agingFld.getText());
		int foward = NumberUtils.toInt(timeoutFld.getText());
		if (aging > (2 * (foward -1 ))){
			JOptionPane.showMessageDialog(this, "2*(转发延时-1)必须大于或等于最大的老化时间","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		return isValid;
	}
	
}
