/**
 * 
 */
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.ObjectMessage;
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
import com.jhw.adm.client.model.switcher.LLDPViewModel;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.ParamConfigStrategy;
import com.jhw.adm.client.swing.ParamUploadStrategy;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.message.SynchDevice;
import com.jhw.adm.server.entity.ports.LLDPConfig;
import com.jhw.adm.server.entity.ports.LLDPPortConfig;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

/**
 * @author Administrator
 *
 */

@Component(LLDPConfigurationView.ID)
@Scope(Scopes.DESKTOP)
public class LLDPConfigurationView extends ViewPart {
	private static final long serialVersionUID = 1L;

	public static final String ID = "lldpConfigurationView";

	//参数配置面板
	private final JPanel parameterPnl = new JPanel();
	private final JLabel intervalLbl = new JLabel();
	private final NumberField intervalFld = new NumberField(5,0,1,32768,true);
	
	private final JLabel holdLbl = new JLabel();
	private final NumberField holdFld = new NumberField(2,0,1,10,true);
	
	private final JLabel delayLbl = new JLabel();
	private final NumberField delayFld = new NumberField(4,0,1,8192,true);
	
	private final JLabel reinitLbl = new JLabel();
	private final NumberField reinitFld = new NumberField(2,0,1,10,true);
	
	private static final String INTERVAL_RANGE = "5-32768";
	private static final String HOLD_RANGE = "2-10";
	private static final String DELAY_RANGE = "1-8192";
	private static final String REINIT_RANGE = "1-10";
	
	//端口配置面板
	private final JScrollPane scrollPnl = new JScrollPane();
	private final JPanel portPnl = new JPanel();
	private final String[] portStatusList = {"禁用","启用","仅发送","仅接收"};
	private final List<List> componentList = new ArrayList<List>();
	
	private final JPanel statusPnl = new JPanel();
	private final JLabel statusLbl = new JLabel();
	private final JTextField statusFld = new JTextField();
	
	//下端按钮面板
	private final JPanel bottomPnl = new JPanel();
	private JButton uploadBtn;
	private JButton downloadBtn;
	private JButton closeBtn = null;
	private int portCount =0;
	
	private List<Serializable> portEntityList = null;
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
	
	@Autowired
	@Qualifier(LLDPViewModel.ID)
	private LLDPViewModel viewModel;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=MessageDispatcher.ID)
	private MessageDispatcher messageDispatcher;
	
	@Resource(name=DataStatus.ID)
	private DataStatus dataStatus;
	
	private MessageSender messageSender;
	
	private static final Logger LOG = LoggerFactory.getLogger(LLDPConfigurationView.class);
	
	private final MessageProcessorAdapter messageUploadProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(TextMessage message) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				LOG.error("sleep error", e);
			}
			queryData();
		}
	};
	
	private final MessageProcessorAdapter messageFepOffLineProcessor = new MessageProcessorAdapter(){
		@Override
		public void process(TextMessage message){
			queryData();
		}
	};
	
	private final MessageProcessorAdapter messageDownloadProcessor = new MessageProcessorAdapter(){
		@Override		
		public void process(ObjectMessage message){
			queryData();
		}
	};
	
	@PostConstruct
	protected void initialize(){
		messageSender = remoteServer.getMessageSender();
		messageDispatcher.addProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.addProcessor(MessageNoConstants.FEPOFFLINE, messageFepOffLineProcessor);
		init();
		queryData();
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		initParameterPnl();
		initPortPnl();
		initBottomPnl();

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(parameterPnl,BorderLayout.NORTH);
		panel.add(scrollPnl,BorderLayout.CENTER);
		panel.add(statusPnl,BorderLayout.SOUTH);
		
		this.setLayout(new BorderLayout());
		this.add(panel,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		this.setViewSize(600,480);
		
		setResource();
	}
	
	private void initParameterPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(intervalLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,50),0,0));
		panel.add(intervalFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(new StarLabel("秒" + "(" + INTERVAL_RANGE + ")"),new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,5),0,0));
		
		panel.add(holdLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,50),0,0));
		panel.add(holdFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(new StarLabel("次" + "(" + HOLD_RANGE + ")"),new GridBagConstraints(2,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,5),0,0));
		
		panel.add(delayLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,50),0,0));
		panel.add(delayFld,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(new StarLabel("秒" + "(" + DELAY_RANGE + ")"),new GridBagConstraints(2,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,5),0,0));
		
		panel.add(reinitLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,50),0,0));
		panel.add(reinitFld,new GridBagConstraints(1,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(new StarLabel("秒" + "(" + REINIT_RANGE + ")"),new GridBagConstraints(2,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,5),0,0));
		
		parameterPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		parameterPnl.add(panel);
	}
	
	/**
	 * 初始化端口面板
	 */
	private void initPortPnl(){
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		panel.add(portPnl);
		scrollPnl.getViewport().add(panel);
//		scrollPnl.setPreferredSize(new Dimension(300,220));
	}
	
	/**
	 * 初始化底部按钮面板
	 */
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
	
	/**
	 * 设置视图中各个控件的资源文件
	 */
	private void setResource(){
		this.setTitle("LLDP配置");
		parameterPnl.setBorder(BorderFactory.createTitledBorder("参数配置"));
		scrollPnl.setBorder(BorderFactory.createTitledBorder("端口配置"));
//		statusPnl.setBorder(BorderFactory.createTitledBorder("状态"));
		
		intervalFld.setColumns(25);
		holdFld.setColumns(25);
		delayFld.setColumns(25);
		reinitFld.setColumns(25);
		
		intervalLbl.setText("Tx Interval");
		holdLbl.setText("Tx Hold");
		delayLbl.setText("Tx Delay");
		reinitLbl.setText("Tx Reinit");
		
		statusLbl.setText("状态");
		statusFld.setColumns(15);
		statusFld.setEditable(false);
		statusFld.setBackground(Color.WHITE);
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}
	
	/**
	 * 向服务端下发查询命令
	 */
	@SuppressWarnings("unchecked")
	private void queryData(){
		switchNodeEntity = (SwitchNodeEntity) adapterManager.getAdapter(
				equipmentModel.getLastSelected(), SwitchNodeEntity.class);
		
		if (null == switchNodeEntity){
			return;
		}
		portCount = switchNodeEntity.getPorts().size();
//		viewModel.setPortEntity(switchNodeEntity.getPorts());
		//设置端口面板
		setPortPnlLayout();
		clear();
		
		String where = " where entity.switchNode=?";
		Object[] parms = {switchNodeEntity};
		List<LLDPConfig> lldpConfigList = (List<LLDPConfig>)remoteServer.getService().findAll(LLDPConfig.class, where, parms);
		if (null == lldpConfigList || lldpConfigList.size() < 1){
			setPortValue();
			return;
		}
		
		LLDPConfig lldpConfig = lldpConfigList.get(0);
		if (null == lldpConfig){
			return;
		}
		
		viewModel.setPortEntity(lldpConfig.getLldpPortConfigs());
		setValue(lldpConfig);
	}
	
	/**
	 * 根据端口个数布局端口面板
	 */
	private void setPortPnlLayout(){
		componentList.clear();
		portPnl.removeAll();
		portPnl.setLayout(new GridBagLayout());
		for (int i = 0 ; i < portCount; i++){
			JLabel label1 = new JLabel("端口"+ (i+1));
			JComboBox combox1 = new JComboBox();
			combox1.setPreferredSize(new Dimension(80,combox1.getPreferredSize().height));
			for (int k = 0 ; k < portStatusList.length; k++){
				combox1.addItem(portStatusList[k]);
			}
			
			portPnl.add(label1,new GridBagConstraints(0,i,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,60),0,0));
			portPnl.add(combox1,new GridBagConstraints(1,i,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,100),0,0));
			
			List<JComponent> rowList1 = new ArrayList<JComponent>();
			rowList1.add(label1);
			rowList1.add(combox1);
			componentList.add(rowList1);
			
			i++;
			
			JLabel label2 = new JLabel("端口"+ (i+1));
			JComboBox combox2 = new JComboBox();
			combox2.setPreferredSize(new Dimension(80,combox1.getPreferredSize().height));
			for (int k = 0 ; k < portStatusList.length; k++){
				combox2.addItem(portStatusList[k]);
			}
			
			portPnl.add(label2,new GridBagConstraints(2,i-1,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,60),0,0));
			portPnl.add(combox2,new GridBagConstraints(3,i-1,1,1,0.0,0.0,
					GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
			
			List<JComponent> rowList2 = new ArrayList<JComponent>();
			rowList2.add(label2);
			rowList2.add(combox2);
			componentList.add(rowList2);
		}
		
		portPnl.revalidate();
	}
	
	private void setPortValue(){
		for (int i = 0 ; i<portCount; i++){
			int port = i+1;
			LLDPPortConfig portEntity = viewModel.getLLDPPortEntity(port);
			if(portEntity != null){
				int lldpWork = portEntity.getLldpWork();
				
				((JLabel)componentList.get(i).get(0)).setText("端口"+ port);
				((JComboBox)componentList.get(i).get(1)).setSelectedItem(reverseIntToString(lldpWork));
			}
		}
	}
	
	/**
	 * 根据查询得到的值设置到视图的控件中
	 * @param lldpConfig
	 */
	private void setValue(LLDPConfig lldpConfig){
		viewModel.setLldpConfig(lldpConfig);
		
		int tx_Interval = lldpConfig.getTx_Interval();
		int tx_Hold = lldpConfig.getTx_Hold();
		int tx_Delay = lldpConfig.getTx_Delay();
		int tx_Reinit = lldpConfig.getTx_Reinit();
		String status = dataStatus.get(lldpConfig.getIssuedTag()).getKey();
		intervalFld.setText(tx_Interval + "");
		holdFld.setText(tx_Hold + "");
		delayFld.setText(tx_Delay +"");
		reinitFld.setText(tx_Reinit +"");
		statusFld.setText(status);
		
		for (int i = 0 ; i<portCount; i++){
			int port = i+1;
			LLDPPortConfig portEntity = viewModel.getLLDPPortEntity(port);
			int lldpWork = portEntity.getLldpWork();
			
			((JLabel)componentList.get(i).get(0)).setText("端口"+ port);
			((JComboBox)componentList.get(i).get(1)).setSelectedItem(reverseIntToString(lldpWork));
		}
	}
	
	/**
	 * 保存操作
	 */
	@ViewAction(name=DOWNLOAD, icon=ButtonConstants.DOWNLOAD, desc="下载LLDP配置信息",role=Constants.MANAGERCODE)
	public void download(){
		//Amend 2010.06.04
		if(!isValids()){
			return;
		}
		
		int result = PromptDialog.showPromptDialog(this, "请选择下载的方式",imageRegistry);
		if (result == 0){
			return ;
		}
		
		//组合LLDPConfig
		LLDPConfig lldpConfig = getLLDPConfig();
		
		List<Serializable> list = new ArrayList<Serializable>();
		list.add(lldpConfig);
		
		remoteServer.getService().updateEntity(lldpConfig.getSwitchNode());
		
		//下发服务端
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
				remoteServer.getAdmService().updateAndSetting(macValue, MessageNoConstants.LLDPCONFIG, list,
						clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(),Constants.DEV_SWITCHER2,result);
			}catch(Exception e){
				paramConfigStrategy.showErrorMessage("下载LLDP配置信息异常");
				queryData();
				LOG.error("LLDPConfigurationView.save() error", e);
			}
			if(this.result == Constants.SYN_SERVER){
				paramConfigStrategy.showNormalMessage(true, Constants.SYN_SERVER);
				queryData();
			}
		}
	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="上载LLDP配置信息",role=Constants.MANAGERCODE)
	public void upload(){
		
		if(null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		final HashSet<SynchDevice> synDeviceList = new HashSet<SynchDevice>();
		SynchDevice synchDevice = new SynchDevice();
		synchDevice.setIpvalue(switchNodeEntity.getBaseConfig().getIpValue());
		synchDevice.setModelNumber(switchNodeEntity.getDeviceModel());
		synDeviceList.add(synchDevice);
		
		Task task = new UploadRequestTask(synDeviceList, MessageNoConstants.SINGLESWITCHLLDP);
		showUploadMessageDialog(task, "上载LLDP配置信息");
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
	
	/**
	 * 得到修改后视图中值
	 * @return
	 */
	private LLDPConfig getLLDPConfig(){
		LLDPConfig lldpConfig = viewModel.getLldpConfig();
		if(null == lldpConfig){
			lldpConfig = new LLDPConfig();
		}
		int tx_Interval = getInputVlue(intervalFld.getText().trim());
		int tx_Hold = getInputVlue(holdFld.getText().trim());
		int tx_Delay = getInputVlue(delayFld.getText().trim());
		int tx_Reinit = getInputVlue(reinitFld.getText().trim());
		lldpConfig.setTx_Interval(tx_Interval);
		lldpConfig.setTx_Hold(tx_Hold);
		lldpConfig.setTx_Delay(tx_Delay);
		lldpConfig.setTx_Reinit(tx_Reinit);
		lldpConfig.setIssuedTag(Constants.ISSUEDADM);
		
		portEntityList = new ArrayList<Serializable>();
		
		Set<LLDPPortConfig> portEntitySet = new LinkedHashSet<LLDPPortConfig>();
		for (int i = 0 ; i < portCount; i++){
			List rowList = componentList.get(i);
			//Amend 2010.06.08
//			int port = Integer.parseInt(((JLabel)rowList.get(0)).getText().trim());
			int port = Integer.parseInt(((JLabel) rowList.get(0)).getText().substring(
									2,((JLabel) rowList.get(0)).getText().trim().length()));
			int lldpWork = reverseStringToInt(((JComboBox)rowList.get(1)).getSelectedItem().toString());
			LLDPPortConfig portEntity = viewModel.getLLDPPortEntity(port);
			if(null == portEntity){
				portEntity = new LLDPPortConfig();
			}
			portEntity.setLldpWork(lldpWork);
			portEntitySet.add(portEntity);
			
			//保存到list中，以备下发成功后调用
			portEntityList.add(portEntity);
		}
		
		lldpConfig.setLldpPortConfigs(portEntitySet);
		lldpConfig.setSwitchNode(switchNodeEntity);
		
		return lldpConfig;
	}
	
	private void clear(){
		intervalFld.setText("");
		holdFld.setText("");
		delayFld.setText("");
		reinitFld.setText("");
	}
	
	/**
	 * 把输入框中的值转化为整形，如果为非数值，则默认下发零
	 * @param str
	 * @return
	 */
	private int getInputVlue(String str){
		int value = 0;
		if (StringUtils.isNumeric(str)){
			value = NumberUtils.toInt(str);;
		}
		return value;
	}
	
	/**
	 * 把传入的整形值转化为字符串
	 * @param lldpWork
	 * @return
	 */
	private String reverseIntToString(int lldpWork){
		String value = "";
		switch(lldpWork){
			case 0:
				value = "禁用";
				break;
			case 1:
				value = "启用";
				break;
			case 2:
				value = "仅发送";
				break;
			case 3:
				value = "仅接收";
				break;
		}
		return value;
	}
	
	/**
	 * 把传入的字符串转化为整型值
	 * @param value
	 * @return
	 */
	private int reverseStringToInt(String value){
		int lldpWork = 0;
		if ("禁用".equals(value)){
			lldpWork = 0;
		}
		else if ("启用".equals(value)){
			lldpWork = 1;
		}
		else if ("仅发送".equals(value)){
			lldpWork = 2;
		}
		else if ("仅接收".equals(value)){
			lldpWork = 3;
		}
		
		return lldpWork;
	}
	
	/**
	 * 设备浏览器监听事件
	 * @author Administrator
	 */
	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
				queryData();
			}else{
				clear();
				switchNodeEntity = null;
				componentList.clear();
				portPnl.removeAll();
				portPnl.revalidate();
			}
		}		
	};
	
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
		messageDispatcher.removeProcessor(MessageNoConstants.SINGLESYNCHONEFINISH, messageUploadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.PARMREP, messageDownloadProcessor);
		messageDispatcher.removeProcessor(MessageNoConstants.FEPOFFLINE, messageFepOffLineProcessor);
	}
	
	//Amend 2010.06.04
	public boolean isValids(){
		boolean isValid = true;
		
		if (null == switchNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if (null == intervalFld.getText() || "".equals(intervalFld.getText())) {
			JOptionPane.showMessageDialog(this, "Tx Interval错误，范围是：5-32768",
					"提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		} else if ((NumberUtils.toInt(intervalFld.getText()) < 5)
				|| (NumberUtils.toInt(intervalFld.getText()) > 32768)) {
			JOptionPane.showMessageDialog(this, "Tx Interval错误，范围是：5-32768",
					"提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if (null == holdFld.getText() || "".equals(holdFld.getText())) {
			JOptionPane.showMessageDialog(this, "Tx Hold错误，范围是：2-10", "提示",
					JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		} else if ((NumberUtils.toInt(holdFld.getText()) < 2)
				|| (NumberUtils.toInt(holdFld.getText()) > 10)) {
			JOptionPane.showMessageDialog(this, "Tx Hold错误，范围是：2-10", "提示",
					JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if (null == delayFld.getText() || "".equals(delayFld.getText())) {
			JOptionPane.showMessageDialog(this, "Tx Delay错误，范围是：1-8192", "提示",
					JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		} else if ((NumberUtils.toInt(delayFld.getText()) < 1)
				|| (NumberUtils.toInt(delayFld.getText()) > 8192)) {
			JOptionPane.showMessageDialog(this, "Tx Delay错误，范围是：1-8192", "提示",
					JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if (null == reinitFld.getText() || "".equals(reinitFld.getText())) {
			JOptionPane.showMessageDialog(this, "Tx Reinit错误，范围是：1-10", "提示",
					JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		} else if ((NumberUtils.toInt(reinitFld.getText()) < 1)
				|| (NumberUtils.toInt(reinitFld.getText()) > 10)) {
			JOptionPane.showMessageDialog(this, "Tx Reinit错误，范围是：1-10", "提示",
					JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}

		int interval = NumberUtils.toInt(intervalFld.getText());
		int delay = NumberUtils.toInt(delayFld.getText());
		if ((interval / delay) < 4) {
			JOptionPane.showMessageDialog(this, "Interval值必须不小于Delay值的四倍",
					"提示", JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}

		return isValid;
	}

}
