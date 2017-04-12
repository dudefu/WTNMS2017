package com.jhw.adm.client.views.switchlayer3;

import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.level3.Switch3STPEntity;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(Layer3STPBaseConfigurationView.ID)
@Scope(Scopes.DESKTOP)
public class Layer3STPBaseConfigurationView extends ViewPart {

	private static final long serialVersionUID = 1L;
	public static final String ID = "layer3STPBaseConfigurationView";
	private static final Logger LOG = LoggerFactory.getLogger(Layer3STPBaseConfigurationView.class);
	
	private JPanel configurePnl = new JPanel();
	private JPanel toolBarPnl = new JPanel();
	private JButton saveBtn = null;
	private JButton closeBtn = null;
	
	private JComboBox protocolTypeComboBox = new JComboBox();
	private JComboBox priorityComboBox = new JComboBox();
	private NumberField helloTimeFld = new NumberField(2,0,1,10,true);
	private NumberField maxAgeTimeFld = new NumberField(2, 0, 1, 40, true);
	private NumberField forwardDelayTimeFld = new NumberField(2, 0, 1, 30, true);
	
	private final static String[] PRIORITYLIST= {"0","4096","8192","12288","16384","20480","24576","28672"
		,"32768","36864","40960","45056","49152","53248","57344","61440"};
	
	private final static String[] PROTOCOL_TYPE= {"不使能","STP","RSTP"};
	
	private ButtonFactory buttonFactory;
	private SwitchLayer3 switchLayer3;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	private Switch3STPEntity switch3stpEntity = null;
	private final MessageOfSwitchConfigProcessorStrategy messageOfSaveProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	
	@PostConstruct
	protected void initialize(){
		init();
		queryData();
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		initConfigPnl();
		initToolBtnPnl();
		
		this.setLayout(new BorderLayout());
		this.add(configurePnl,BorderLayout.CENTER);
		this.add(toolBarPnl,BorderLayout.SOUTH);
	}
	
	private void initConfigPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(new JLabel("协议类型"),new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(protocolTypeComboBox,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
		
		panel.add(new JLabel("Spanning Tree Priority"),new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,	5,10,10),0,0));
		panel.add(priorityComboBox,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
		
		panel.add(new JLabel("Hello Time"),new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(helloTimeFld,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
		panel.add(new StarLabel("(1-10)"),new GridBagConstraints(2,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,10,0),0,0));
		
		panel.add(new JLabel("Max Age Time"),new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(maxAgeTimeFld,new GridBagConstraints(1,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
		panel.add(new StarLabel("(6-40)"),new GridBagConstraints(2,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,10,0),0,0));
		
		panel.add(new JLabel("Forward Delay Time"),new GridBagConstraints(0,4,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(forwardDelayTimeFld,new GridBagConstraints(1,4,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
		panel.add(new StarLabel("(4-30)"),new GridBagConstraints(2,4,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,10,0),0,0));
		
		initComponent();
		
		configurePnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		configurePnl.add(panel);
	}
	
	private void initComponent(){
		for(int j = 0 ; j < PROTOCOL_TYPE.length ; j++){
			protocolTypeComboBox.addItem(PROTOCOL_TYPE[j]);
			if(2 == j){
				protocolTypeComboBox.setSelectedItem(PROTOCOL_TYPE[j]);
			}
		}
		for(int i = 0 ; i < PRIORITYLIST.length; i++){
			priorityComboBox.addItem(PRIORITYLIST[i]);
			if(8 == i){
				priorityComboBox.setSelectedItem(PRIORITYLIST[i]);
			}
		}
		
		protocolTypeComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(1 == e.getStateChange()){
					if(PROTOCOL_TYPE[0].equals((String)e.getItem())){
						setEnable(false);
					}else{
						setEnable(true);
					}
				}
			}
		});
		
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, listener);
	}
	
	private void initToolBtnPnl(){
		toolBarPnl.setLayout(new BorderLayout());
		JPanel newPanel = new JPanel(new GridBagLayout());
		saveBtn = buttonFactory.createButton(SAVE);
		closeBtn = buttonFactory.createCloseButton();
		newPanel.add(saveBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		toolBarPnl.add(newPanel,BorderLayout.EAST);
	}
	
	private PropertyChangeListener listener = new PropertyChangeListener() {
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			clear();
			if(evt.getNewValue() instanceof SwitchTopoNodeLevel3){
				queryData();
			}
		}
	};
	
	private void clear(){
		protocolTypeComboBox.setSelectedItem(null);
		priorityComboBox.setSelectedItem(null);
		helloTimeFld.setText("");
		maxAgeTimeFld.setText("");
		forwardDelayTimeFld.setText("");
		switchLayer3 = null;
	}
	
	@SuppressWarnings("unchecked")
	private void queryData(){
		switchLayer3 = (SwitchLayer3) adapterManager.getAdapter(equipmentModel.getLastSelected(), SwitchLayer3.class);
		
		if(null == switchLayer3){
			return;
		}
		
		String where = " where entity.switchLayer3=?";
		Object[] parms = {switchLayer3};
		List<Switch3STPEntity> stpList = (List<Switch3STPEntity>)remoteServer.getService().findAll(Switch3STPEntity.class, where, parms);
		if (null == stpList || stpList.size() < 1){
			return;
		}
		
		setValue(stpList);
	}
	
	private void setValue(List<Switch3STPEntity> stpList){
		
		this.switch3stpEntity = stpList.get(0);
		
		if(this.switch3stpEntity.getStpType() > 3 || this.switch3stpEntity.getStpType() < 1){
			LOG.error(String.format("STP协议类型值错误:{%S}" ,this.switch3stpEntity.getStpType()));
		}
		String protocolType = convertIntToString(this.switch3stpEntity.getStpType());
		int priority = this.switch3stpEntity.getS_TreePriority();
		int helloTime = this.switch3stpEntity.getHelloTime();
		int maxAgeTime = this.switch3stpEntity.getMaxAgeTime();
		int forwordDelayTime = this.switch3stpEntity.getForwardDelTime();
		
		LOG.info(String.format(
				"STP中协议类型：{%s},Priority：{%s},HelloTime：{%s},MaxAgeTime：{%s},forwordDelayTime:{%s}",
				protocolType, priority, helloTime, maxAgeTime,forwordDelayTime));
		
		protocolTypeComboBox.setSelectedItem(protocolType);
		priorityComboBox.setSelectedItem(String.valueOf(priority));
		helloTimeFld.setText(String.valueOf(helloTime));
		maxAgeTimeFld.setText(String.valueOf(maxAgeTime));
		forwardDelayTimeFld.setText(String.valueOf(forwordDelayTime));
		
	}
	
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="保存STP基本配置信息",role=Constants.MANAGERCODE)
	public void save(){
		if(!isValids()){
			return;
		}
		
		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
		if (result == 0){
			return;
		}
		
		List<Serializable> list = new ArrayList<Serializable>();
		
		int protocolType = convertStringToInt(protocolTypeComboBox.getSelectedItem().toString());
		int priority = Integer.parseInt(priorityComboBox.getSelectedItem().toString()); 
		int helloTime = Integer.parseInt(helloTimeFld.getText()); 
		int maxAgeTime = Integer.parseInt(maxAgeTimeFld.getText());
		int forwardDelayTime = Integer.parseInt(forwardDelayTimeFld.getText());
		
		if(null == this.switch3stpEntity){
			this.switch3stpEntity = new Switch3STPEntity();
		}
		this.switch3stpEntity.setStpType(protocolType);
		this.switch3stpEntity.setS_TreePriority(priority);
		this.switch3stpEntity.setHelloTime(helloTime);
		this.switch3stpEntity.setMaxAgeTime(maxAgeTime);
		this.switch3stpEntity.setForwardDelTime(forwardDelayTime);
		this.switch3stpEntity.setSwitchLayer3(this.switchLayer3);
		list.add(this.switch3stpEntity);
		
		showMessageDialog();
		String ipValue = switchLayer3.getIpValue();
		if(StringUtils.isBlank(ipValue)){
			LOG.error("Layer3 switch's IP is null");			
			messageOfSaveProcessorStrategy.showErrorMessage("保存出现异常");
			return;
		}
		try {
			remoteServer.getAdmService().updateAndSettingByIp(ipValue, MessageNoConstants.SWITCH3STP, list, 
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(), Constants.DEV_SWITCHER3,result);
		} catch (JMSException e) {
			messageOfSaveProcessorStrategy.showErrorMessage("保存出现异常");
			LOG.error("STPSysConfigurationView's save() is failure:{}", e);
		}
		if(result == Constants.SYN_SERVER){
			messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		}
		queryData();
	}

	private void showMessageDialog(){
		messageOfSaveProcessorStrategy.showInitializeDialog("保存",this);
	}
	
	private String convertIntToString(int type){
		
		String protocolType = "";
		
		if(1 == type){
			protocolType = PROTOCOL_TYPE[0];
		}else if(2 == type){
			protocolType = PROTOCOL_TYPE[1];
		}else if(3 == type){
			protocolType = PROTOCOL_TYPE[2];
		}
		
		return protocolType;
	}
	
	private int convertStringToInt(String type){
		int protocolType = -1;
		
		if(PROTOCOL_TYPE[0].equals(type)){
			protocolType = 1;
		}else if(PROTOCOL_TYPE[1].equals(type)){
			protocolType = 2;
		}else if(PROTOCOL_TYPE[2].equals(type)){
			protocolType = 3;
		}
		
		return protocolType;
	}
	
	private boolean isValids(){
		boolean isValid = true;
		
		if(null == switchLayer3){
			JOptionPane.showMessageDialog(this, "请选择三层交换机","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if(null == protocolTypeComboBox.getSelectedItem() || "".equals(protocolTypeComboBox.getSelectedItem().toString())){
			JOptionPane.showMessageDialog(this, "协议类型不能为空，请选择协议类型","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if(null == priorityComboBox.getSelectedItem() || "".equals(priorityComboBox.getSelectedItem().toString())){
			JOptionPane.showMessageDialog(this, "Spanning Tree Priority不能为空，请选择Spanning Tree Priority","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		String helloTimeStr = helloTimeFld.getText().trim();
		if(StringUtils.isBlank(helloTimeStr)){
			JOptionPane.showMessageDialog(this, "Hello Time不能为空，请输入Hello Time","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}else{
			int helloTime = NumberUtils.toInt(helloTimeStr);
			if(helloTime < 1 || helloTime > 10){
				JOptionPane.showMessageDialog(this, "Hello Time范围为：1-10，请输入Hello Time","提示",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		
		String maxAgeTimeStr = maxAgeTimeFld.getText().trim();
		if(StringUtils.isBlank(maxAgeTimeStr)){
			JOptionPane.showMessageDialog(this, "Max Age Time不能为空，请输入Max Age Time","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}else{
			int maxAgeTime = NumberUtils.toInt(maxAgeTimeStr);
			if(maxAgeTime < 6 || maxAgeTime > 40){
				JOptionPane.showMessageDialog(this, "Max Age Time范围为：6-40，请输入Max Age Time","提示",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		
		String forwardDelayTimeStr = forwardDelayTimeFld.getText().trim();
		if(StringUtils.isBlank(forwardDelayTimeStr)){
			JOptionPane.showMessageDialog(this, "Forward Delay Time不能为空，请输入Forward Delay Time","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}else{
			int forwardDelayTime = NumberUtils.toInt(forwardDelayTimeStr);
			if(forwardDelayTime < 4 || forwardDelayTime > 30){
				JOptionPane.showMessageDialog(this, "Forward Delay Time范围为：4-30，请输入Forward Delay Time","提示",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		
		return isValid;
	}
	
	private void setEnable(boolean enable){
		priorityComboBox.setEnabled(enable);
		helloTimeFld.setEnabled(enable);
		maxAgeTimeFld.setEnabled(enable);
		forwardDelayTimeFld.setEnabled(enable);
	}
	
	@Override
	public void dispose(){
		super.dispose();
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, listener);
	}
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
}
