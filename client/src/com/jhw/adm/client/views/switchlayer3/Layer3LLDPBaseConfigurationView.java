package com.jhw.adm.client.views.switchlayer3;

import static com.jhw.adm.client.core.ActionConstants.SAVE;

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
import com.jhw.adm.server.entity.level3.Switch3LLDP_PEntity;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(Layer3LLDPBaseConfigurationView.ID)
@Scope(Scopes.DESKTOP)
public class Layer3LLDPBaseConfigurationView extends ViewPart {

	private static final long serialVersionUID = 1L;
	public static final String ID = "layer3LLDPBaseConfigurationView";
	private static final Logger LOG = LoggerFactory.getLogger(Layer3LLDPBaseConfigurationView.class);
	
	private JPanel configurePnl = new JPanel();
	private JPanel toolBarPnl = new JPanel();
	private JButton saveBtn = null;
	private JButton closeBtn = null;
	
	private JComboBox protocolStatusComboBox = new JComboBox();
	private NumberField holdTimeFld = new NumberField(5,0,1,65535,true);
	private NumberField reinitFld = new NumberField(1, 0, 2, 5, true);
	private NumberField messagePeriodFld = new NumberField(5, 0, 1, 65534, true);
	
	private final static String[] PROTOCOL_STATUS= {"开启LLDP协议","关闭LLDP协议"};
	
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
	
	private Switch3LLDP_PEntity switch3LldpEntity = null;
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
		panel.add(new JLabel("协议状态"),new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(protocolStatusComboBox,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
		
		panel.add(new JLabel("HoldTime"),new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(holdTimeFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
		panel.add(new StarLabel("(1-65535s)"),new GridBagConstraints(2,1,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,10,0),0,0));
		
		panel.add(new JLabel("Reinit"),new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(reinitFld,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
		panel.add(new StarLabel("(2-5s)"),new GridBagConstraints(2,2,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,10,0),0,0));
		
		panel.add(new JLabel("报文发送周期"),new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,10),0,0));
		panel.add(messagePeriodFld,new GridBagConstraints(1,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,10,10,0),0,0));
		panel.add(new StarLabel("(5-65534s)"),new GridBagConstraints(2,3,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,0,10,0),0,0));
		
		initComponent();
		
		configurePnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		configurePnl.add(panel);
	}
	
	private void initComponent(){
		for(int j = 0 ; j < PROTOCOL_STATUS.length ; j++){
			protocolStatusComboBox.addItem(PROTOCOL_STATUS[j]);
			if(0 == j){
				protocolStatusComboBox.setSelectedItem(PROTOCOL_STATUS[j]);
			}
		}
		
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
		protocolStatusComboBox.setSelectedItem(null);
		holdTimeFld.setText("");
		reinitFld.setText("");
		messagePeriodFld.setText("");
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
		List<Switch3LLDP_PEntity> lldpList = (List<Switch3LLDP_PEntity>)remoteServer.getService().findAll(Switch3LLDP_PEntity.class, where, parms);
		if (null == lldpList || lldpList.size() < 1){
			return;
		}
		
		setValue(lldpList);
	}
	
	private void setValue(List<Switch3LLDP_PEntity> lldpList){
		
		this.switch3LldpEntity = lldpList.get(0);
		
		if(this.switch3LldpEntity.getProtocolState() > 1 || this.switch3LldpEntity.getProtocolState() < 0){
			LOG.error(String.format("LLDP协议状态值错误:{%S}" ,this.switch3LldpEntity.getProtocolState()));
		}
		
		String protocolStatus = convertIntToString(this.switch3LldpEntity.getProtocolState());
		int holdtime = this.switch3LldpEntity.getHoldTime();
		int reinit = this.switch3LldpEntity.getReinit();
		int messagePeriod = this.switch3LldpEntity.getSendCycle();
		
		LOG.info(String.format(
				"LLDP中协议状态：{%s},HoldTime：{%s},Reinit：{%s},报文发送周期：{%s}",
				protocolStatus, holdtime, reinit, messagePeriod));
		
		protocolStatusComboBox.setSelectedItem(protocolStatus);
		holdTimeFld.setText(String.valueOf(holdtime));
		reinitFld.setText(String.valueOf(reinit));
		messagePeriodFld.setText(String.valueOf(messagePeriod));
		
	}
	
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="保存LLDP协议基本配置",role=Constants.MANAGERCODE)
	public void save(){
		if(!isValids()){
			return;
		}
		
		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
		if (result == 0){
			return;
		}
		
		List<Serializable> list = new ArrayList<Serializable>();
		
		int protocolStatus = convertStringToInt(protocolStatusComboBox.getSelectedItem().toString());
		int holdTime = Integer.parseInt(holdTimeFld.getText()); 
		int reinit = Integer.parseInt(reinitFld.getText());
		int messagePeriod = Integer.parseInt(messagePeriodFld.getText());
		
		if(null == this.switch3LldpEntity){
			this.switch3LldpEntity = new Switch3LLDP_PEntity();
		}
		this.switch3LldpEntity.setProtocolState(protocolStatus);
		this.switch3LldpEntity.setHoldTime(holdTime);
		this.switch3LldpEntity.setReinit(reinit);
		this.switch3LldpEntity.setSendCycle(messagePeriod);
		this.switch3LldpEntity.setSwitchLayer3(this.switchLayer3);
		list.add(this.switch3LldpEntity);
		
		showMessageDialog();
		String ipValue = switchLayer3.getIpValue();
		if(StringUtils.isBlank(ipValue)){
			LOG.error("Layer3LLDPBaseConfigurationView.Layer3 Switch's IP is null");			
			messageOfSaveProcessorStrategy.showErrorMessage("保存出现异常");
			return;
		}
		try {
			remoteServer.getAdmService().updateAndSettingByIp(ipValue, MessageNoConstants.SWITCH3LLDP_P, list, 
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
	
	private String convertIntToString(int status){
		String protocolStatus = "";
		
		if(0 == status){
			protocolStatus = PROTOCOL_STATUS[0];
		}else if(1 == status){
			protocolStatus = PROTOCOL_STATUS[1];
		}
		
		return protocolStatus;
	}
	
	private int convertStringToInt(String status){
		int protocolStatus = -1;
		
		if(PROTOCOL_STATUS[0].equals(status)){
			protocolStatus = 0;
		}else if(PROTOCOL_STATUS[1].equals(status)){
			protocolStatus = 1;
		}
		
		return protocolStatus;
	}
	
	private boolean isValids(){
		boolean isValid = true;
		
		if(null == switchLayer3){
			JOptionPane.showMessageDialog(this, "请选择三层交换机","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if(null == protocolStatusComboBox.getSelectedItem() || "".equals(protocolStatusComboBox.getSelectedItem().toString())){
			JOptionPane.showMessageDialog(this, "协议类型不能为空，请选择协议类型","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		String holdTimeStr = holdTimeFld.getText().trim();
		if(StringUtils.isBlank(holdTimeStr)){
			JOptionPane.showMessageDialog(this, "HoldTime不能为空，请输入HoldTime","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}else{
			int holdTime = NumberUtils.toInt(holdTimeStr);
			if(holdTime < 1 || holdTime > 65535){
				JOptionPane.showMessageDialog(this, "HoldTime范围为：1-65535s，请输入HoldTime","提示",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		
		String reinitStr = reinitFld.getText().trim();
		if(StringUtils.isBlank(reinitStr)){
			JOptionPane.showMessageDialog(this, "Reinit不能为空，请输入Reinit","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}else{
			int reinit = NumberUtils.toInt(reinitStr);
			if(reinit < 2 || reinit > 5){
				JOptionPane.showMessageDialog(this, "Reinit范围为：2-5s，请输入Reinit","提示",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		
		String messagePeriodStr = messagePeriodFld.getText().trim();
		if(StringUtils.isBlank(messagePeriodStr)){
			JOptionPane.showMessageDialog(this, "报文发送周期不能为空，请输入报文发送周期","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}else{
			int messagePeriod = NumberUtils.toInt(messagePeriodStr);
			if(messagePeriod < 5 || messagePeriod > 65534){
				JOptionPane.showMessageDialog(this, "报文发送周期范围为：5-65534，请输入报文发送周期","提示",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		
		return isValid;
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
