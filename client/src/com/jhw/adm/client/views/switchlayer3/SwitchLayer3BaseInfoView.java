package com.jhw.adm.client.views.switchlayer3;

import static com.jhw.adm.client.core.ActionConstants.OK;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.ResourceConstants;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.IpAddressField;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.swing.TextFieldPlainDocument;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.level3.SwitchPortLevel3;
import com.jhw.adm.server.entity.system.AreaEntity;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.util.AddressEntity;
import com.jhw.adm.server.entity.util.Constants;

@Component(SwitchLayer3BaseInfoView.ID)
@Scope(Scopes.DESKTOP)
public class SwitchLayer3BaseInfoView extends ViewPart {
	
	private static final long serialVersionUID = -3552453200341680480L;

	public static final String ID = "switchLayer3BaseInfoView";

	private final JPanel systemPnl = new JPanel(); 
	
	//经度
	private final JLabel longitudeLbl = new JLabel();
	private final JTextField longitudeFld = new JTextField();
	
	//纬度
	private final JLabel latitudeLbl = new JLabel();
	private final JTextField latitudeFld = new JTextField();
		
	//ip地址
	private final JLabel ipAddrLbl = new JLabel();
	private final IpAddressField ipAddrFld = new IpAddressField();
	
	//mac地址
	private final JLabel macLbl = new JLabel();
	private final JLabel macValueLbl = new JLabel();
	
	//设备名称
	private final JLabel equipmentNameLabel = new JLabel("设备名称");
	private final JTextField equipmentNameField = new JTextField();
	
	//默认网关
	private final JLabel gatewayLabel = new JLabel("默认网关");
	private final IpAddressField gatewayField = new IpAddressField();
	
	private final JLabel descriptionLbl = new JLabel();
	private final JTextField descriptionFld = new JTextField();
	
	//下端的按钮面板
	private final JPanel bottomPnl = new JPanel();
	private JButton saveBtn = null;
	private JButton closeBtn = null;
	
	private ButtonFactory buttonFactory;
	
	private SwitchTopoNodeLevel3 topoNodeLevel3;
	private SwitchLayer3 switchLayer3;
	
	@Resource(name=LocalizationManager.ID)
	private LocalizationManager localizationManager;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@PostConstruct
	protected void initialize(){
		jInit();
		queryData();
	}
	
	private void jInit(){
		buttonFactory = actionManager.getButtonFactory(this); 
		
		equipmentNameField.setColumns(40);
		longitudeFld.setColumns(20);
		latitudeFld.setColumns(20);
		
		equipmentNameField.setEditable(false);
		ipAddrFld.setEditable(false);

		longitudeFld.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent arg0) {
				keyTypeControl(arg0);
			}
			@Override
			public void keyReleased(KeyEvent arg0) {}
			@Override
			public void keyPressed(KeyEvent arg0) {}
		});
		latitudeFld.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				keyTypeControl(e);
			}
			@Override
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {}
		});
				
		systemPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(equipmentNameLabel,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(equipmentNameField,new GridBagConstraints(1,0,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));

		equipmentNameField.setDocument(new TextFieldPlainDocument(equipmentNameField, 40));
		
		panel.add(ipAddrLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(ipAddrFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		
		panel.add(gatewayLabel,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(gatewayField,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		
		panel.add(macLbl,new GridBagConstraints(2,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		panel.add(macValueLbl,new GridBagConstraints(3,2,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,0),0,0));
		
		panel.add(longitudeLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(longitudeFld,new GridBagConstraints(1,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		panel.add(latitudeLbl,new GridBagConstraints(2,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		panel.add(latitudeFld,new GridBagConstraints(3,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,0),0,0));
		
		panel.add(descriptionLbl,new GridBagConstraints(0,4,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(descriptionFld,new GridBagConstraints(1,4,3,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		
		systemPnl.add(panel);
		
		JPanel baseInfoPnl = new JPanel();
		baseInfoPnl.setLayout(new BorderLayout());
		baseInfoPnl.add(systemPnl,BorderLayout.CENTER);
		
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		saveBtn = buttonFactory.createButton(OK);
		closeBtn = buttonFactory.createCloseButton();
		bottomPnl.add(saveBtn);
		bottomPnl.add(closeBtn);
		this.setCloseButton(closeBtn);
		
		this.setLayout(new BorderLayout());
		this.add(baseInfoPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		this.setViewSize(500, 400);
		
		//得到资源文件
		setResource();
	}

	private void setResource(){
		this.setTitle("三层交换机基本信息");
		systemPnl.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()
				, localizationManager.getString(ResourceConstants.BASEINFOVIEW_SYSTEM)));
		
		longitudeLbl.setText("经度");
		latitudeLbl.setText("纬度");
		ipAddrLbl.setText("IP地址");
		macLbl.setText("MAC地址");
		descriptionLbl.setText("节点名称");
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, oltChangeListener);
	}

	private void queryData(){
		
		topoNodeLevel3 = (SwitchTopoNodeLevel3) adapterManager.getAdapter(
				equipmentModel.getLastSelected(), SwitchTopoNodeLevel3.class);
		
		if (null == topoNodeLevel3){
			return ;
		}
		
		switchLayer3 = NodeUtils.getNodeEntity(topoNodeLevel3).getSwitchLayer3();
		setValue();
	}
	
	private void setValue(){
		ipAddrFld.setIpAddress(switchLayer3.getIpValue());
		macValueLbl.setText(switchLayer3.getMacValue());
		//
		gatewayField.setText(switchLayer3.getDefGeteway());
		equipmentNameField.setText(switchLayer3.getName());
		descriptionFld.setText(topoNodeLevel3.getName());
		
		Set<SwitchPortLevel3> portSets = switchLayer3.getPorts();
		if(portSets == null) {
			return;
		}
	}
	
	@ViewAction(name=OK, icon=ButtonConstants.SAVE, desc="保存三层交换机基本信息",role=Constants.MANAGERCODE)
	public void ok(){
		if (null == switchLayer3){
			JOptionPane.showMessageDialog(this, "请选择三层交换机","提示",JOptionPane.NO_OPTION);
			return;
		}

		if (!isValids()) {
			return;
		}
		
		topoNodeLevel3.setIpValue(ipAddrFld.getText().trim());//IP地址
		topoNodeLevel3.setName(descriptionFld.getText().trim());
		
		this.switchLayer3.setIpValue(ipAddrFld.getText().trim());//IP地址
		
		AddressEntity addressEntity = null;
		if(null == switchLayer3.getAddress()){
			addressEntity = new AddressEntity();
			switchLayer3.setAddress(addressEntity);
		}else{
			addressEntity = switchLayer3.getAddress();
		}
		addressEntity.setLongitude(getNewString(longitudeFld.getText().trim()));//经纬度
		addressEntity.setLatitude(getNewString(latitudeFld.getText().trim()));
		
		switchLayer3.setAddress(addressEntity);
				
		switchLayer3.setDefGeteway(gatewayField.getText());
		switchLayer3.setName(equipmentNameField.getText());
		
		showMessageDialog();
		// 保存实体、地址、管理员
		if (switchLayer3.getId() == null) {
			switchLayer3 = (SwitchLayer3)remoteServer.getService().saveEntity(switchLayer3);
		} else {
			switchLayer3 = (SwitchLayer3)remoteServer.getService().updateEntity(switchLayer3);
		}

		if (topoNodeLevel3.getId() == null) {
			topoNodeLevel3 = (SwitchTopoNodeLevel3)remoteServer.getService().saveEntity(topoNodeLevel3);
		} else {
			topoNodeLevel3 = (SwitchTopoNodeLevel3)remoteServer.getService().updateEntity(topoNodeLevel3);
		}
		
		topoNodeLevel3.setSwitchLayer3(switchLayer3);
		equipmentModel.fireEquipmentUpdated(topoNodeLevel3);
		messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
	}
	
	private final MessageOfSwitchConfigProcessorStrategy messageOfSaveProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	private void showMessageDialog(){
		messageOfSaveProcessorStrategy.showInitializeDialog("保存", this);
	}
	
	private final PropertyChangeListener oltChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof SwitchTopoNodeLevel3){
				queryData();
			}else {
				switchLayer3 = null;
				clear();
			}
		}
	};
	
	private void clear(){
		longitudeFld.setText("");
		latitudeFld.setText("");
		ipAddrFld.setIpAddress("");
	}
	
	class AreaEntityObject {
		AreaEntity areaEntity = null;
		public AreaEntityObject(AreaEntity areaEntity){
			this.areaEntity = areaEntity;
		}
		
		@Override
		public String toString(){
			if (null == this.areaEntity){
				return null;
			}
			return this.areaEntity.getName();
		}

		public AreaEntity getAreaEntity() {
			return areaEntity;
		}
	}
	
	class UserEntityObject {
		UserEntity userEntity = null;

		public UserEntityObject(UserEntity userEntity) {
			this.userEntity = userEntity;
		}

		@Override
		public String toString() {
			if (null == this.userEntity) {
				return null;
			}
			return this.userEntity.getUserName();
		}

		public UserEntity getUserEntity() {
			return userEntity;
		}
	}

	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, oltChangeListener);
	}
	
	//Amend 2010.06.03
	public void keyTypeControl(KeyEvent e) {
		char c = e.getKeyChar();
		if (c != '.' && (c < '0' || c > '9')) {
			e.consume();
		}
	}

	public boolean isValids() {
		boolean isValid = true;
		
		if (StringUtils.isBlank(equipmentNameField.getText())) {
			JOptionPane.showMessageDialog(this, "设备名称不能为空，请输入设备名称", "提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if (equipmentNameField.getText().trim().length() > 40) {
			JOptionPane.showMessageDialog(this, "设备名称不能超过40个字符，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if (StringUtils.isBlank(ipAddrFld.getText())) {
			JOptionPane.showMessageDialog(this, "IP地址不能为空，请输入IP地址", "提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}else if(ClientUtils.isIllegal(ipAddrFld.getText())){
			JOptionPane.showMessageDialog(this, "IP地址非法，请重新输入","提示",JOptionPane.NO_OPTION);
			return false;
		}
		
		String longitude = longitudeFld.getText().trim();
		String latitude = latitudeFld.getText().trim();
		
		if(longitude.indexOf(".") == 0)
		{
			JOptionPane.showMessageDialog(this, "经度必须以数字开头，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(latitude.indexOf(".") == 0)
		{
			JOptionPane.showMessageDialog(this, "纬度必须以数字开头，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if(longitude.length() > 36 || latitude.length() > 36){
			JOptionPane.showMessageDialog(this, "经纬度长度不能超过36个字符，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if(!StringUtils.isBlank(longitude)){
			if(!NumberUtils.isNumber(longitude)){
				JOptionPane.showMessageDialog(this, "经度值仅能包含数字和点号，请重新输入","提示",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}

		if(!StringUtils.isBlank(latitude)){
			if(!NumberUtils.isNumber(latitude)){
				JOptionPane.showMessageDialog(this, "纬度值仅能包含数字和点号，请重新输入","提示",JOptionPane.NO_OPTION);
				isValid = false;
				return isValid;
			}
		}
		
		if(NumberUtils.toDouble(longitude) > 180){
			JOptionPane.showMessageDialog(this, "经度值范围为：0-180，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if(NumberUtils.toDouble(latitude) > 90){
			JOptionPane.showMessageDialog(this, "纬度值范围为：0-90，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		if (descriptionFld.getText().trim().length() > 50) {
			JOptionPane.showMessageDialog(this, "节点名称长度不能超过50个字符，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		
		return isValid;
	}

	public String getNewString(String oldString) {
		String newString = "";

		if (oldString.indexOf(".") < 0){// no dot
			return oldString;
		}

		String headString = oldString.substring(0, oldString.indexOf(".") + 1);
		String tailString = oldString.substring(oldString.indexOf(".") + 1,
				oldString.length());

		if (tailString.length() == 0){// dot is the last char
			newString = headString.substring(0, oldString.indexOf("."));
			return newString;
		}

		if (tailString.length() <= 6) {
			newString = oldString;
			return newString;
		} else {
			newString = headString + tailString.substring(0, 6);
			return newString;
		}
	}
	
}