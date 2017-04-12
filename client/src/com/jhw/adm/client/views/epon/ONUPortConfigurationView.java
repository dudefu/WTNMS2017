package com.jhw.adm.client.views.epon;

import static com.jhw.adm.client.core.ActionConstants.SAVE;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.math.NumberUtils;
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
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.DuplexStatus;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.ListComboBoxModel;
import com.jhw.adm.client.model.PortRateStatus;
import com.jhw.adm.client.model.StormCTypeStatus;
import com.jhw.adm.client.model.StringInteger;
import com.jhw.adm.client.model.epon.ONUPortConfigModel;
import com.jhw.adm.client.swing.MessagePromptDialog;
import com.jhw.adm.client.swing.MessageReceiveInter;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.epon.ONUEntity;
import com.jhw.adm.server.entity.epon.ONUPort;
import com.jhw.adm.server.entity.tuopos.ONUTopoNodeEntity;
import com.jhw.adm.server.entity.util.Constants;

@Component(ONUPortConfigurationView.ID)
@Scope(Scopes.DESKTOP)
public class ONUPortConfigurationView extends ViewPart implements MessageReceiveInter{
	private static final long serialVersionUID = 1L;

	public static final String ID = "ONUPortConfigurationView";

	private final JScrollPane scrollPnl = new JScrollPane();
	private final JPanel mainPnl = new JPanel();
	//上端的面板
	private final JPanel topPnl = new JPanel();
	private final JLabel portLbl = new JLabel();
	private final JLabel manageStatusLbl = new JLabel();
	private final JLabel operStatusLbl = new JLabel();
	private final JLabel duplexStatusLbl = new JLabel();
	private final JLabel rateLbl = new JLabel();
	private final JLabel flowControlLbl = new JLabel();
	private final JLabel circleLbl = new JLabel();
	private final JLabel ratelimitLbl = new JLabel();
	private final JLabel stormTypeLbl = new JLabel();
	private final JLabel stormThresholdLbl = new JLabel();
	private final JLabel controlStatusLbl = new JLabel();
	private final JLabel macLimitLbl = new JLabel();
	
	//中间面板
	private final JScrollPane centerScrllPnl = new JScrollPane();
	private final JPanel centerPnl = new JPanel();
	private final JPanel middlePnl = new JPanel();
	
	
	//工具按钮面板
	private final JPanel toolBtnPnl = new JPanel();
	private JButton saveBtn;
	private JButton synBtn;
	private JButton closeBtn = null;
	
	private int portCount = 0;
	
	private final ONUTopoNodeEntity onuTopoNodeEntity = null;
	
	private ButtonFactory buttonFactory;
	
	//
	private final List<List> componentList = new ArrayList<List>();
	
	private static final String[] PRIORITYLIST = {"0","16","32","48","64","80","96","112"
		,"128","144","160","176","192","208","224","240"};
	
	private static final String[] BOOLSTR = {"true","false"};
	
	private ONUEntity onuEntity = null;
	
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
	@Qualifier(ONUPortConfigModel.ID)
	private ONUPortConfigModel viewModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Autowired
	@Qualifier(DuplexStatus.ID)
	private DuplexStatus duplexStatus;
	
	@Autowired
	@Qualifier(PortRateStatus.ID)
	private PortRateStatus portRateStatus;
	
	@Autowired
	@Qualifier(StormCTypeStatus.ID)
	private StormCTypeStatus stormCTypeStatus;
	
	@PostConstruct
	protected void initialize() {
		init();
		queryData();
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		initTopPnl();
		initCenterPnl();
		initToolBtnPnl();
		
		mainPnl.setLayout(new BorderLayout());
		mainPnl.add(topPnl,BorderLayout.NORTH);
		mainPnl.add(centerScrllPnl,BorderLayout.CENTER);
		scrollPnl.getViewport().add(mainPnl);
		
		this.setLayout(new BorderLayout());
		this.add(scrollPnl,BorderLayout.CENTER);
		this.add(toolBtnPnl,BorderLayout.SOUTH);
		
		setResource();
	}
	
	private void initTopPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(portLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,15,0,0),0,0));
		panel.add(manageStatusLbl,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,25,0,0),0,0));
		panel.add(operStatusLbl,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,50,0,0),0,0));
		panel.add(duplexStatusLbl,new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,50,0,0),0,0));
		panel.add(rateLbl,new GridBagConstraints(4,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,50,0,0),0,0));
		panel.add(flowControlLbl,new GridBagConstraints(5,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,60,0,0),0,0));
		panel.add(circleLbl,new GridBagConstraints(6,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,65,0,0),0,0));
		panel.add(ratelimitLbl,new GridBagConstraints(7,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,60,0,0),0,0));
		panel.add(stormTypeLbl,new GridBagConstraints(8,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,43,0,0),0,0));
		panel.add(stormThresholdLbl,new GridBagConstraints(9,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,30,0,0),0,0));
		panel.add(controlStatusLbl,new GridBagConstraints(10,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,50,0,0),0,0));
		panel.add(macLimitLbl,new GridBagConstraints(11,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,50,0,0),0,0));
		
		topPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		topPnl.add(panel);
	}
	
	private void initCenterPnl(){
		middlePnl.setLayout(new GridBagLayout());

		centerPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		centerPnl.add(middlePnl);
		
		centerScrllPnl.getViewport().add(centerPnl);
	}
	
	private void initToolBtnPnl(){
		toolBtnPnl.setLayout(new BorderLayout());
		JPanel newPanel = new JPanel(new GridBagLayout());
		closeBtn = buttonFactory.createCloseButton();
		saveBtn = buttonFactory.createButton(SAVE);
		synBtn = buttonFactory.createButton(UPLOAD);
		newPanel.add(synBtn,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(saveBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		toolBtnPnl.add(newPanel,BorderLayout.EAST);
	}
	
	private void setResource(){
		this.setTitle("ONU端口配置");
		portLbl.setText("端口");
		manageStatusLbl.setText("管理状态"); 
		operStatusLbl.setText("OperStatus");
		duplexStatusLbl.setText("双工状态");
		rateLbl.setText("端口速率");
		flowControlLbl.setText("流控");
		circleLbl.setText("回环检测");
		ratelimitLbl.setText("速率限制");
		stormTypeLbl.setText("风暴控制类型");
		stormThresholdLbl.setText("风暴控制阀值");
		controlStatusLbl.setText("控制行状态");
		macLimitLbl.setText("MAC数量限制");
		
		setViewSize(695, 550);	
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, onuNodeChangeListener);
	}
	
	private void queryData(){
		clear();
		onuEntity = (ONUEntity)adapterManager.getAdapter(equipmentModel.getLastSelected(), ONUEntity.class);
		
		if(null == onuEntity){
			return;
		}
		if (null == onuEntity.getOnuPorts() || onuEntity.getOnuPorts().size() < 1){
			initDataBase();
			return;
		}
		//初始化中心面板
		setCenterLayout(onuEntity.getOnuPorts());
		setValue(onuEntity.getOnuPorts());
	}
	
	private void initDataBase(){
		List<ONUPort> dataList = new ArrayList<ONUPort>();
		for(int i = 0 ; i < 16; i++){
			int port = i+1;
			boolean portStatus = true;//端口状态管理
			int operStatus = 2;
			int portDuplexStatus = 1;//端口双工状态  1-full，2-half，3-auto
			int portRate = 2;//端口速率  1-10M，2-100M，3-1000M（ONU暂未支持）4-auto
			boolean portSCStatus = false;//端口流控状态
			boolean portRingCheck = true;//端口回环检测
			int portRateLimit = 100000;//端口速率限制  64~100000kbps
			int stormCType = 1;//风暴控制类型   1-广播，2-多播，3-未知单播
			int stormCThresholds = 65536;//风暴控制阀值  256~100000kbps
			int cRowStatus = 0;//控制行状态 
			int maxNumLimit = 255;//mac数量控制  1-63
			
			ONUPort onuPort = new ONUPort();
			onuPort.setProtNo(port);
			onuPort.setPortStatus(portStatus);
			onuPort.setOperStatus(operStatus);
			onuPort.setPortDuplexStatus(portDuplexStatus);
			onuPort.setPortRate(portRate);
			onuPort.setPortSCStatus(portSCStatus);
			onuPort.setPortRingCheck(portRingCheck);
			onuPort.setPortRateLimit(portRateLimit);
			onuPort.setStormCType(stormCType);
			onuPort.setStormCThresholds(stormCThresholds);
			onuPort.setcRowStatus(cRowStatus);
			onuPort.setMaxNumLimit(maxNumLimit);
			
			dataList.add(onuPort);
		}
		
		remoteServer.getService().saveEntities(dataList);
	}
	
	/**
	 * 通过查询出的端口列表布局panel的所有控件
	 */
	private void setCenterLayout(Set<ONUPort> onuPortSet){
		componentList.clear();
		middlePnl.removeAll();
		
		for(int i = 0 ; i < onuPortSet.size() ; i++){
			JLabel portLabel = new JLabel();//端口
			portLabel.setText("" + (i+1));
			
			JComboBox manageStatusBox = new JComboBox(); //端口管理状态
			manageStatusBox.setPreferredSize(new Dimension(80,manageStatusBox.getPreferredSize().height));
			for(int j = 0 ; j < BOOLSTR.length ; j++){
				manageStatusBox.addItem(BOOLSTR[j]);
			}
			
			NumberField operStatusFld = new NumberField(); //OperStatus
			operStatusFld.setColumns(15);
		
			JComboBox duplexStatusBox = new JComboBox(new ListComboBoxModel(duplexStatus.toList()));//端口双工状态
			duplexStatusBox.setPreferredSize(new Dimension(80,duplexStatusBox.getPreferredSize().height));
			
			JComboBox rateCombox = new JComboBox(new ListComboBoxModel(portRateStatus.toList())); //端口速率
			rateCombox.setPreferredSize(new Dimension(80,rateCombox.getPreferredSize().height));
			
			JComboBox flowControlBox = new JComboBox(); //端口流控状态
			flowControlBox.setPreferredSize(new Dimension(80,flowControlBox.getPreferredSize().height));
			for(int j = 0 ; j < BOOLSTR.length ; j++){
				flowControlBox.addItem(BOOLSTR[j]);
			}
			
			JComboBox circleBox = new JComboBox();//端口回环检测
			circleBox.setPreferredSize(new Dimension(80,circleBox.getPreferredSize().height));
			for(int j = 0 ; j < BOOLSTR.length ; j++){
				circleBox.addItem(BOOLSTR[j]);
			}
			
			NumberField ratelimitFld = new NumberField();//端口速率限制
//			ratelimitFld.setText("10000");
			ratelimitFld.setColumns(15);
			
			JComboBox stormTypeBox = new JComboBox(new ListComboBoxModel(stormCTypeStatus.toList()));//风暴控制类型
			stormTypeBox.setPreferredSize(new Dimension(80,stormTypeBox.getPreferredSize().height));

			NumberField stormThresholdFld = new NumberField();//风暴控制阀值
//			stormThresholdFld.setText("65536");
			stormThresholdFld.setColumns(15);
			
			NumberField controlStatusFld = new NumberField();//控制行状态
//			controlStatusFld.setText("0");
			controlStatusFld.setColumns(15);
			
			NumberField macLimitFld = new NumberField();//MAC数量限制
//			macLimitFld.setText("255");
			macLimitFld.setColumns(15);
			
			middlePnl.add(portLabel,new GridBagConstraints(0,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,15,0,0),0,0));
			middlePnl.add(manageStatusBox,new GridBagConstraints(1,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,28,0,0),0,0));
			middlePnl.add(operStatusFld,new GridBagConstraints(2,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,18,0,0),0,0));
			middlePnl.add(duplexStatusBox,new GridBagConstraints(3,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,18,0,0),0,0));
			middlePnl.add(rateCombox,new GridBagConstraints(4,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,18,0,0),0,0));
			middlePnl.add(flowControlBox,new GridBagConstraints(5,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,18,0,0),0,0));
			middlePnl.add(circleBox,new GridBagConstraints(6,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,18,0,0),0,0));
			middlePnl.add(ratelimitFld,new GridBagConstraints(7,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,18,0,0),0,0));
			middlePnl.add(stormTypeBox,new GridBagConstraints(8,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,18,0,0),0,0));
			middlePnl.add(stormThresholdFld,new GridBagConstraints(9,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,18,0,0),0,0));
			middlePnl.add(controlStatusFld,new GridBagConstraints(10,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,18,0,0),0,0));
			middlePnl.add(macLimitFld,new GridBagConstraints(11,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,18,0,0),0,0));
			
			List<JComponent> rowList = new ArrayList<JComponent>();
			rowList.add(0,portLabel); //端口
			rowList.add(1,manageStatusBox);//端口管理状态
			rowList.add(2,operStatusFld);//OperStatus
			rowList.add(3,duplexStatusBox);//端口双工状态
			rowList.add(4,rateCombox);//端口速率
			rowList.add(5,flowControlBox);//端口流控状态
			rowList.add(6,circleBox);//端口回环检测
			rowList.add(7,ratelimitFld);//端口速率限制
			rowList.add(8,stormTypeBox);//风暴控制类型
			rowList.add(9,stormThresholdFld);//风暴控制阀值
			rowList.add(10,controlStatusFld);//控制行状态
			rowList.add(11,macLimitFld);//MAC数量限制
			componentList.add(rowList);
		}
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				middlePnl.revalidate();
			}
		});
	}
	
	/**
	 * 设置各个控件的值
	 */
	private void setValue(Set<ONUPort> onuPortSet){
		viewModel.setOnuPortList(onuPortSet);
		for (int i = 0 ; i < onuPortSet.size(); i++){
			int port = i+1;
			ONUPort onuPort = viewModel.getValueAt(port);
			boolean portStatus = onuPort.isPortStatus();//端口状态管理
			int operStatus = onuPort.getOperStatus();
			
			StringInteger stringInteger1 = duplexStatus.get(onuPort.getPortDuplexStatus());//端口双工状态  1-full，2-half，3-auto
			StringInteger stringInteger2 = portRateStatus.get(onuPort.getPortRate());//端口速率  1-10M，2-100M，3-1000M（ONU暂未支持）4-auto
			boolean portSCStatus = onuPort.isPortSCStatus();//端口流控状态
			boolean portRingCheck = onuPort.isPortRingCheck();//端口回环检测
			int portRateLimit = onuPort.getPortRateLimit();//端口速率限制  64~100000kbps
			StringInteger stringInteger3 = stormCTypeStatus.get(onuPort.getStormCType());//风暴控制类型   1-广播，2-多播，3-未知单播

			int stormCThresholds = onuPort.getStormCThresholds();//风暴控制阀值  256~100000kbps
			int cRowStatus = onuPort.getcRowStatus();//控制行状态 
			int maxNumLimit = onuPort.getMaxNumLimit();//mac数量控制  1-63

			((JLabel)componentList.get(i).get(0)).setText(port+"");
			((JComboBox)componentList.get(i).get(1)).setSelectedItem(String.valueOf(portStatus));
			((NumberField)componentList.get(i).get(2)).setText(operStatus + "");
			((JComboBox)componentList.get(i).get(3)).setSelectedItem(stringInteger1);
			((JComboBox)componentList.get(i).get(4)).setSelectedItem(stringInteger2);
			((JComboBox)componentList.get(i).get(5)).setSelectedItem(String.valueOf(portSCStatus));
			((JComboBox)componentList.get(i).get(6)).setSelectedItem(String.valueOf(portRingCheck));
			((NumberField)componentList.get(i).get(7)).setText(portRateLimit + "");
			((JComboBox)componentList.get(i).get(8)).setSelectedItem(stringInteger3);
			((NumberField)componentList.get(i).get(9)).setText(stormCThresholds + "");
			((NumberField)componentList.get(i).get(10)).setText(cRowStatus + "");
			((NumberField)componentList.get(i).get(11)).setText(maxNumLimit + "");
		}
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				middlePnl.updateUI();
			}
		});
	}
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="保存ONU端口配置信息",role=Constants.MANAGERCODE)
	public void save(){
		if (!isValids()){
			return;
		}
		List<Serializable> dataList = getONUPortConfigList();
		remoteServer.getService().updateEntity(dataList);
		
		final String operate = "保存";
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				//打开消息提示对话框
				openMessageDialog(operate);
			}
		});
	}
	
	/**
	 * 显示结果对话框
	 */
	private void openMessageDialog(String operate){
		MessagePromptDialog messageDlg = new MessagePromptDialog(this,operate,imageRegistry,remoteServer);
		messageDlg.setMessage("操作完成");
		messageDlg.setVisible(true);
	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="向上同步ONU端口配置信息",role=Constants.MANAGERCODE)
	public void upSynchronize(){

	}
	
	private List<Serializable> getONUPortConfigList(){
		List<Serializable> list = new ArrayList<Serializable>();
		for (int i = 0 ; i < 16; i++){
			int portNo = i+1;    //端口
			ONUPort onuPort = viewModel.getValueAt(portNo);
			
			boolean oldPortStatus = onuPort.isPortStatus();//端口状态管理
			int oldOperStatus = onuPort.getOperStatus();
			int oldPortDuplexStatus = onuPort.getPortDuplexStatus();//端口双工状态  1-full，2-half，3-auto
			int oldPortRate = onuPort.getPortRate();//端口速率  1-10M，2-100M，3-1000M（ONU暂未支持）4-auto
			boolean oldPortSCStatus = onuPort.isPortSCStatus();//端口流控状态
			boolean oldPortRingCheck = onuPort.isPortRingCheck();//端口回环检测
			int oldPortRateLimit = onuPort.getPortRateLimit();//端口速率限制  64~100000kbps
			int oldStormCType =onuPort.getStormCType();//风暴控制类型   1-广播，2-多播，3-未知单播
			int oldStormCThresholds = onuPort.getStormCThresholds();//风暴控制阀值  256~100000kbps
			int oldCRowStatus = onuPort.getcRowStatus();//控制行状态 
			int oldMaxNumLimit = onuPort.getMaxNumLimit();//mac数量控制  1-63

			for (int k = 0 ; k < 16; k++){
				JLabel portLbl = (JLabel)componentList.get(k).get(0);
				int port = NumberUtils.toInt(portLbl.getText());
				
				JComboBox portStatusBox = (JComboBox)componentList.get(k).get(1);
				boolean portStatus = Boolean.valueOf(portStatusBox.getSelectedItem().toString());
				
				NumberField operStatusBox = (NumberField)componentList.get(k).get(2);
				int operStatus = NumberUtils.toInt(operStatusBox.getText());
				
				JComboBox duplexStatusBox = (JComboBox)componentList.get(k).get(3);
				int portDuplexStatus = ((StringInteger)duplexStatusBox.getSelectedItem()).getValue();
				
				JComboBox portRateBox = (JComboBox)componentList.get(k).get(4);
				int portRate = ((StringInteger)portRateBox.getSelectedItem()).getValue();
				
				JComboBox portSCStatusBox = (JComboBox)componentList.get(k).get(5);
				boolean portSCStatus = Boolean.valueOf(portSCStatusBox.getSelectedItem().toString());
				
				JComboBox portRingCheckBox = (JComboBox)componentList.get(k).get(6);
				boolean portRingCheck = Boolean.valueOf(portRingCheckBox.getSelectedItem().toString());
				
				NumberField portRateLimitFld = (NumberField)componentList.get(k).get(7);
				int portRateLimit = NumberUtils.toInt(portRateLimitFld.getText().trim());
				
				JComboBox stormCTypeBox = (JComboBox)componentList.get(k).get(8);
				int stormCType = ((StringInteger)stormCTypeBox.getSelectedItem()).getValue();
				
				NumberField stormCThresholdsFld = (NumberField)componentList.get(k).get(9);
				int stormCThresholds = NumberUtils.toInt(stormCThresholdsFld.getText());
				
				NumberField cRowStatusFld = (NumberField)componentList.get(k).get(10);
				int cRowStatus = NumberUtils.toInt(cRowStatusFld.getText());
				
				NumberField maxNumLimitFld = (NumberField)componentList.get(k).get(11);
				int maxNumLimit = NumberUtils.toInt(maxNumLimitFld.getText());
				
				if (portNo == port){
					if ((oldPortStatus != portStatus)
						|| oldOperStatus != operStatus
						|| oldPortDuplexStatus != portDuplexStatus 
						|| oldPortRate != portRate
						|| oldPortSCStatus != portSCStatus
						|| oldPortRingCheck != portRingCheck
						|| oldPortRateLimit != portRateLimit 
						|| oldStormCType != stormCType
						|| oldStormCThresholds != stormCThresholds
						|| oldCRowStatus != cRowStatus
						|| oldMaxNumLimit != maxNumLimit){
						//说明此端口的值有改变
						onuPort.setPortStatus(portStatus);
						onuPort.setOperStatus(operStatus);
						onuPort.setPortDuplexStatus(portDuplexStatus);
						onuPort.setPortRate(portRate);
						onuPort.setPortSCStatus(portSCStatus);
						onuPort.setPortRingCheck(portRingCheck);
						onuPort.setPortRateLimit(portRateLimit);
						onuPort.setStormCType(stormCType);
						onuPort.setStormCThresholds(stormCThresholds);
						onuPort.setcRowStatus(cRowStatus);
						onuPort.setMaxNumLimit(maxNumLimit);
//						onuPort.setOltonuEntity(oltOnuEntity);

						list.add(onuPort);
						break;
					}
				}
			}
		}
		
		return list;
	}
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	
	private void  clear(){
		componentList.clear();
		middlePnl.removeAll();
		viewModel.setOnuPortList(null);
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				middlePnl.revalidate();
			}
		});
	}
	
	/**
	 * 对于接收到的异步消息进行处理
	 */
	public void receive(Object object){
	}
	
	/**
	 * 设备浏览器的时间监听
	 * 当选择不同的设备时，根据eponNodeEntity,重新查询数据库
	 * @author Administrator
	 *
	 */
	private final PropertyChangeListener onuNodeChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			clear();
			if (evt.getNewValue() instanceof ONUTopoNodeEntity){
				queryData();
			}
		}
	};
	
	public boolean isValids(){
		boolean isValid = true;
		
		if (null == this.onuEntity){
			JOptionPane.showMessageDialog(this, "请选择ONU设备","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
		if (result == 0){
			isValid = false;
			return isValid;
		}
		return isValid;
	}
	
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, onuNodeChangeListener);
	}
}
