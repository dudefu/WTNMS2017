package com.jhw.adm.client.views.epon;

import static com.jhw.adm.client.core.ActionConstants.SAVE;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.swing.MessageReceiveInter;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.epon.OLTSTP;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(OLTSTPSysConfigurationView.ID)
@Scope(Scopes.DESKTOP)
public class OLTSTPSysConfigurationView extends ViewPart implements MessageReceiveInter{
	private static final long serialVersionUID = 1L;
	public static final String ID = "OLTSTPSysConfigurationView";
	
	private final JPanel configPnl = new JPanel();
	private final JLabel specLbl = new JLabel();
	private final JTextField specFld = new JTextField();
	
	private final JLabel stpMaxAgeLbl = new JLabel();
	private final JTextField stpMaxAgeFld = new JTextField();

	private final JLabel priorityLbl = new JLabel();
	private final JComboBox priorityCombox = new JComboBox();
	
	private final JLabel helloTimeLbl = new JLabel();
	private final JTextField helloTimeFld = new JTextField();
	
	private final JLabel topologyChgLbl = new JLabel();
	private final JTextField topologyChgFld = new JTextField();
	
	private final JLabel holdTimeLbl = new JLabel();
	private final JTextField holdTimeFld = new JTextField();
	
	private final JLabel topChgLbl = new JLabel();
	private final JTextField topChgFld = new JTextField();
	
	private final JLabel forwardDelayLbl = new JLabel();
	private final JTextField forwardDelayFld = new JTextField();
	
	private final JLabel desRootLbl = new JLabel();
	private final JTextField desRootFld = new JTextField();
	
	private final JLabel bridgeMaxAgeLbl = new JLabel();
	private final NumberField bridgeMaxAgeFld = new NumberField(2,0,1,40,true);
	
	private final JLabel rootPayLbl = new JLabel();
	private final JTextField rootPayFld = new JTextField();
	
	private final JLabel bridgeHelloTimeLbl = new JLabel();
	private final NumberField bridgeHelloTimeFld = new NumberField(2,0,1,10,true);
	
	private final JLabel stpRootPortLbl = new JLabel();
	private final JTextField stpRootPortFld = new JTextField();
	
	private final JLabel bridgeForwardDelayLbl = new JLabel();
	private final NumberField bridgeForwardDelayFld = new NumberField(2,0,1,30,true);
	
	private final static String[] PRIORITYLIST= {"0","4096","8192","12288","16384","20480","24576","28672"
				,"32768","36864","40960","45056","49152","53248","57344","61440"};

	private final MessageOfSwitchConfigProcessorStrategy messageOfSaveProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	private static final Logger LOG = LoggerFactory.getLogger(OLTSTPSysConfigurationView.class);
	//工具按钮面板
	private final JPanel toolBtnPnl = new JPanel();
	private JButton saveBtn ;
	private JButton synBtn;
	private JButton closeBtn = null;
	
	private ButtonFactory buttonFactory;
	
	private OLTEntity oltNodeEntity = null;
	private OLTSTP oltStp;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
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
		initConfigPnl();
		initToolBtnPnl();

		this.setLayout(new BorderLayout());
		this.add(configPnl,BorderLayout.CENTER);
		this.add(toolBtnPnl,BorderLayout.SOUTH);
		
		setResource();
	}
	
	private void initConfigPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(specLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,0,0),0,0));
		panel.add(specFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,0,0),0,0));
		panel.add(stpMaxAgeLbl,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,30,0,0),0,0));
		panel.add(stpMaxAgeFld,new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,0,0),0,0));
		
		panel.add(priorityLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,5,0,0),0,0));
		panel.add(priorityCombox,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,10,0,0),0,0));
		panel.add(helloTimeLbl,new GridBagConstraints(2,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,30,0,0),0,0));
		panel.add(helloTimeFld,new GridBagConstraints(3,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,10,0,0),0,0));
		
		panel.add(topologyChgLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,5,0,0),0,0));
		panel.add(topologyChgFld,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,10,0,0),0,0));
		panel.add(holdTimeLbl,new GridBagConstraints(2,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,30,0,0),0,0));
		panel.add(holdTimeFld,new GridBagConstraints(3,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,10,0,0),0,0));
		
		panel.add(topChgLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,5,0,0),0,0));
		panel.add(topChgFld,new GridBagConstraints(1,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,10,0,0),0,0));
		panel.add(forwardDelayLbl,new GridBagConstraints(2,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,30,0,0),0,0));
		panel.add(forwardDelayFld,new GridBagConstraints(3,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,10,0,0),0,0));
		
		panel.add(desRootLbl,new GridBagConstraints(0,4,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,5,0,0),0,0));
		panel.add(desRootFld,new GridBagConstraints(1,4,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,10,0,0),0,0));
		panel.add(bridgeMaxAgeLbl,new GridBagConstraints(2,4,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,30,0,0),0,0));
		panel.add(bridgeMaxAgeFld,new GridBagConstraints(3,4,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,10,0,0),0,0));
		panel.add(new JLabel("(6-40s)"),new GridBagConstraints(4,4,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,3,0,0),0,0));
		panel.add(new StarLabel(),new GridBagConstraints(5,4,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,5,0,0),0,0));
		
		panel.add(rootPayLbl,new GridBagConstraints(0,5,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,5,0,0),0,0));
		panel.add(rootPayFld,new GridBagConstraints(1,5,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,10,0,0),0,0));
		panel.add(bridgeHelloTimeLbl,new GridBagConstraints(2,5,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,30,0,0),0,0));
		panel.add(bridgeHelloTimeFld,new GridBagConstraints(3,5,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,10,0,0),0,0));
		panel.add(new JLabel("(1-10s)"),new GridBagConstraints(4,5,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,3,0,0),0,0));
		panel.add(new StarLabel(),new GridBagConstraints(5,5,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,5,0,0),0,0));
		
		panel.add(stpRootPortLbl,new GridBagConstraints(0,6,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,5,0,0),0,0));
		panel.add(stpRootPortFld,new GridBagConstraints(1,6,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,10,0,0),0,0));
		panel.add(bridgeForwardDelayLbl,new GridBagConstraints(2,6,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,30,0,0),0,0));
		panel.add(bridgeForwardDelayFld,new GridBagConstraints(3,6,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,10,0,0),0,0));
		panel.add(new JLabel("(4-30s)"),new GridBagConstraints(4,6,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,3,0,0),0,0));
		panel.add(new StarLabel(),new GridBagConstraints(5,6,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(15,5,0,0),0,0));
		
		configPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		configPnl.add(panel);
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
	
	/**
	 * 设置资源文件
	 */
	private void setResource(){
		for(int i = 0 ; i < PRIORITYLIST.length; i++){
			priorityCombox.addItem(PRIORITYLIST[i]);
		}

		specFld.setColumns(23);
		stpMaxAgeFld.setColumns(23);
		helloTimeFld.setColumns(23);
		topologyChgFld.setColumns(23);
		holdTimeFld.setColumns(23);
		topChgFld.setColumns(23);
		forwardDelayFld.setColumns(23);
		desRootFld.setColumns(23);
		bridgeMaxAgeFld.setColumns(23);
		rootPayFld.setColumns(23);
		bridgeHelloTimeFld.setColumns(23);
		stpRootPortFld.setColumns(23);
		bridgeForwardDelayFld.setColumns(23);
		
		specFld.setEditable(false);
		stpMaxAgeFld.setEditable(false);
		helloTimeFld.setEditable(false);
		topologyChgFld.setEditable(false);
		holdTimeFld.setEditable(false);
		topChgFld.setEditable(false);
		forwardDelayFld.setEditable(false);
		desRootFld.setEditable(false);
		stpRootPortFld.setEditable(false);
		rootPayFld.setEditable(false);
		
		specLbl.setText("Specification");
		stpMaxAgeLbl.setText("StpMaxAge");
		priorityLbl.setText("优先级");
		helloTimeLbl.setText("HelloTime");
		topologyChgLbl.setText("topologyChange");
		holdTimeLbl.setText("HoldTime");
		topChgLbl.setText("TopChange");
		forwardDelayLbl.setText("ForwardDelay");
		desRootLbl.setText("DesignatedRoot");
		bridgeMaxAgeLbl.setText("BridgeMaxAge");
		rootPayLbl.setText("根路径开销");
		bridgeHelloTimeLbl.setText("BridgeHelloTime");
		stpRootPortLbl.setText("StpRootPort");
		bridgeForwardDelayLbl.setText("BridgeForwardDelay");
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, eponNodeChangeListener);
	}
	
	/**
	 * 从服务器查询设备信息
	 */
	private void queryData(){
		oltNodeEntity = (OLTEntity)adapterManager.getAdapter(equipmentModel.getLastSelected(), OLTEntity.class);
		
		if (null == oltNodeEntity){
			return ;
		}
		
		String where = " where entity.oltEntity=?";
		Object[] parms = {oltNodeEntity};
		
		List<OLTSTP> oltSTPList = (List<OLTSTP>)remoteServer.getService().findAll(OLTSTP.class,where,parms);
		if (null == oltSTPList || oltSTPList.size() < 1){
			return;
		}
		
		oltStp = oltSTPList.get(0);
		setValue();
	}
	
	/**
	 * 设置控件的值
	 * @param stpSysConfigList
	 */
	private void setValue(){
		int specifacation = oltStp.getSpecifacation();
		int stpMaxAge = oltStp.getStpMaxAge();
		int priority = oltStp.getPriority();
		int helloTime = oltStp.getHelloTime();
		String topologyChange = oltStp.getTopologyChange();
		int holdTime = oltStp.getHoldTime();
	    int topChanges = oltStp.getTopChanges();
	    int forwardDelay = oltStp.getForwardDelay();//4-30
	    String designatedRoot = oltStp.getDesignatedRoot();
	    int bridgeMaxAge = oltStp.getBridgeMaxAge();//6-40桥最大生存期
	    int rootExpenses = oltStp.getRootExpenses(); //根路径开销
	    int bridgeHelloTime = oltStp.getBridgeHelloTime();//1-10
	    int stpRootPort = oltStp.getStpRootPort();
	    int bridgewardDelay = oltStp.getBridgewardDelay();
	    
	    specFld.setText(specifacation+"");
	    stpMaxAgeFld.setText(stpMaxAge + "");
	    priorityCombox.setSelectedItem(priority+"");
	    helloTimeFld.setText(helloTime+"");
	    topologyChgFld.setText(topologyChange);
	    holdTimeFld.setText(holdTime + "");
	    topChgFld.setText(topChanges + "");
	    forwardDelayFld.setText(forwardDelay + "");
	    desRootFld.setText(designatedRoot);
	    bridgeMaxAgeFld.setText(bridgeMaxAge + "");
	    rootPayFld.setText(rootExpenses + "");
	    bridgeHelloTimeFld.setText(bridgeHelloTime + "");
	    stpRootPortFld.setText(stpRootPort + "");
	    bridgeForwardDelayFld.setText(bridgewardDelay + "");

	}
	
	/**
	 * 保存按钮事件
	 */
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="保存OLT STP系统配置信息",role=Constants.MANAGERCODE)
	public void save(){
		if(!isValids()){
			return;
		}
		
		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
		if (result == 0){
			return;
		}
		
		if (null == oltStp){
			oltStp = new OLTSTP();
		}
		int specifacation = Integer.parseInt(specFld.getText().trim());
		int stpMaxAge = Integer.parseInt(stpMaxAgeFld.getText().trim());
		int priority = Integer.parseInt(priorityCombox.getSelectedItem().toString());
		int helloTime = Integer.parseInt(helloTimeFld.getText().trim());
		String topologyChange = topologyChgFld.getText().trim();
		int holdTime = Integer.parseInt(holdTimeFld.getText().trim());
	    int topChanges = Integer.parseInt(topChgFld.getText().trim());
	    int forwardDelay = Integer.parseInt(forwardDelayFld.getText().trim());//4-30
	    String designatedRoot = desRootFld.getText().trim();
	    int bridgeMaxAge = Integer.parseInt(bridgeMaxAgeFld.getText());//6-40桥最大生存期
	    int rootExpenses = Integer.parseInt(rootPayFld.getText().trim());
	    int bridgeHelloTime = Integer.parseInt(bridgeHelloTimeFld.getText());//1-10
	    int stpRootPort = Integer.parseInt(stpRootPortFld.getText().trim());
	    int bridgewardDelay = Integer.parseInt(bridgeForwardDelayFld.getText());
	    
	    List<Serializable> oltStpList = new ArrayList<Serializable>();
	    oltStp.setSpecifacation(specifacation);
	    oltStp.setStpMaxAge(stpMaxAge);
	    oltStp.setPriority(priority);
	    oltStp.setHelloTime(helloTime);
	    oltStp.setTopologyChange(topologyChange);
	    oltStp.setHoldTime(holdTime);
	    oltStp.setTopChanges(topChanges);
	    oltStp.setForwardDelay(forwardDelay);
	    oltStp.setDesignatedRoot(designatedRoot);
	    oltStp.setBridgeMaxAge(bridgeMaxAge);
	    oltStp.setRootExpenses(rootExpenses);
	    oltStp.setBridgeHelloTime(bridgeHelloTime);
	    oltStp.setStpRootPort(stpRootPort);
	    oltStp.setBridgewardDelay(bridgewardDelay);
		oltStp.setBridgeMaxAge(bridgeMaxAge);
	    oltStp.setBridgeHelloTime(bridgeHelloTime);
	    oltStp.setBridgewardDelay(bridgewardDelay);
	    oltStpList.add(oltStp);
	    
	    String ipValue = oltNodeEntity.getIpValue();
	    showMessageDialog();
	    
	    try {
			remoteServer.getAdmService().updateAndSettingByIp(ipValue,
					MessageNoConstants.OLTSTPCONFIG, oltStpList,
					clientModel.getCurrentUser().getUserName(),
					clientModel.getLocalAddress(), Constants.DEV_OLT, result);
		} catch (JMSException e) {
			messageOfSaveProcessorStrategy.showErrorMessage("保存出现异常");
			LOG.error("OLTSTPSysConfigurationView.save() is failure:{}", e);
		}
	    
	    if(result == Constants.SYN_SERVER){
			messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		}
		queryData();
	}
	
	private void showMessageDialog(){
		messageOfSaveProcessorStrategy.showInitializeDialog("保存",this);
	}

	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="向上同步EPON STP系统配置信息",role=Constants.MANAGERCODE)
	public void upSynchronize(){
		
	}

	private void clear(){
		specFld.setText("");
		stpMaxAgeFld.setText("");
		priorityCombox.setSelectedItem("");
		helloTimeFld.setText("");
		topologyChgFld.setText("");
		holdTimeFld.setText("");
		topChgFld.setText("");
		forwardDelayFld.setText("");
		desRootFld.setText("");
		bridgeMaxAgeFld.setText("");
		rootPayFld.setText("");
		bridgeHelloTimeFld.setText("");
		stpRootPortFld.setText("");
		bridgeForwardDelayFld.setText("");
	}
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}

	public void receive(Object object){
	}
	
	@Override
	public void dispose() {
		super.dispose();
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, eponNodeChangeListener);
	}
	
	/**
	 * 设备浏览器的时间监听
	 * 当选择不同的设备时，根据eponNodeEntity,重新查询数据库
	 * @author Administrator
	 *
	 */
	private final PropertyChangeListener eponNodeChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			clear();
			if(evt.getNewValue() instanceof EponTopoEntity){
				queryData();
			}
		}
	};
	
	public boolean isValids(){
		boolean isValid = true;
		
		if (null == oltNodeEntity){
			JOptionPane.showMessageDialog(this, "请选择EPON设备","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if(null == priorityCombox.getSelectedItem() || "".equals(priorityCombox.getSelectedItem().toString()))
		{
			JOptionPane.showMessageDialog(this, "系统优先级不能为空，请选择系统优先级","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == bridgeMaxAgeFld.getText() || "".equals(bridgeMaxAgeFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "BridgeMaxAge错误，范围是：6-40","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		else if ((NumberUtils.toInt(bridgeMaxAgeFld.getText()) < 6)
						|| (NumberUtils.toInt(bridgeMaxAgeFld.getText()) > 40))
		{
			JOptionPane.showMessageDialog(this, "BridgeMaxAge错误，范围是：6-40","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(null == bridgeHelloTimeFld.getText() || "".equals(bridgeHelloTimeFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "BridgeHelloTime错误，范围是：1-10","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		else if((NumberUtils.toInt(bridgeHelloTimeFld.getText()) < 1) || (NumberUtils.toInt(bridgeHelloTimeFld.getText()) > 10))
		{
			JOptionPane.showMessageDialog(this, "BridgeHelloTime错误，范围是：1-10","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if(null == bridgeForwardDelayFld.getText() || "".equals(bridgeForwardDelayFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "BridgeForwardDelay错误，范围是：4-30","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		else if((NumberUtils.toInt(bridgeHelloTimeFld.getText()) < 4) || (NumberUtils.toInt(bridgeHelloTimeFld.getText()) > 30))
		{
			JOptionPane.showMessageDialog(this, "BridgeForwardDelay错误，范围是：4-30","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		return isValid;
	}
	
}
