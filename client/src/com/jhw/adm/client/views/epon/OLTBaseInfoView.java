package com.jhw.adm.client.views.epon;

import static com.jhw.adm.client.core.ActionConstants.OK;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang.ObjectUtils;
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
import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.ResourceConstants;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.epon.OLTTypeModel;
import com.jhw.adm.client.swing.IpAddressField;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.epon.OLTBaseInfo;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.system.AreaEntity;
import com.jhw.adm.server.entity.system.UserEntity;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.util.AddressEntity;
import com.jhw.adm.server.entity.util.Constants;

@Component(OLTBaseInfoView.ID)
@Scope(Scopes.DESKTOP)
public class OLTBaseInfoView extends ViewPart{
	private static final long serialVersionUID = 1L;
	public static final String ID = "OLTBaseInfoView";

	private final JPanel systemPnl = new JPanel(); 
	
	//设备名称
	private final JLabel deviceNameLbl = new JLabel();
	private final JTextField deviceNameFld = new JTextField();
	
	//经度
	private final JLabel longitudeLbl = new JLabel();
	private final JTextField longitudeFld = new JTextField();
	
	//纬度
	private final JLabel latitudeLbl = new JLabel();
	private final JTextField latitudeFld = new JTextField();
		
	//ip地址
	private final JLabel ipAddrLbl = new JLabel();
	private final IpAddressField ipAddrFld = new IpAddressField();
	
	//类型
	private final JLabel typeLbl = new JLabel();
	private final JTextField typeCombox = new JTextField();
	
	private final JLabel descriptionLbl = new JLabel();
	private final JTextField descriptionFld = new JTextField();
	
	//下端的按钮面板
	private final JPanel bottomPnl = new JPanel();
	private JButton saveBtn = null;
	private JButton closeBtn = null;
	
	private ButtonFactory buttonFactory;
	
	private EponTopoEntity eponTopoEntity;
	private OLTEntity oltEntity;
	
	private static final Logger LOG = LoggerFactory.getLogger(OLTBaseInfoView.class);
		
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
	
	@Resource(name=OLTTypeModel.ID)
	private OLTTypeModel oltTypeModel;
	
	@PostConstruct
	protected void initialize() {
		jInit();
		queryData();
	}
	
	private void jInit(){
		buttonFactory = actionManager.getButtonFactory(this); 
		
		deviceNameFld.setColumns(50);
		longitudeFld.setColumns(20);
		latitudeFld.setColumns(20);

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
		
		panel.add(deviceNameLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(deviceNameFld,new GridBagConstraints(1,0,3,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		
		panel.add(ipAddrLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(ipAddrFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		
		panel.add(typeLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(typeCombox,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));

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
		baseInfoPnl.add(systemPnl,BorderLayout.CENTER);//

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
		setRegionValue();
	}

	private void setResource(){
		this.setTitle("OLT" + localizationManager.getString(ResourceConstants.BASEINFOVIEW_BASEINFO));
		systemPnl.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder()
				, localizationManager.getString(ResourceConstants.BASEINFOVIEW_SYSTEM)));
		
		deviceNameLbl.setText(localizationManager.getString(ResourceConstants.BASEINFOVIEW_DEVICENAME));

		longitudeLbl.setText("经度");
		latitudeLbl.setText("纬度");
		ipAddrLbl.setText("IP地址");
		typeLbl.setText("型号");
		descriptionLbl.setText("节点名称");
		
		deviceNameFld.setEditable(false);
		deviceNameFld.setBackground(Color.WHITE);
		ipAddrFld.setEditable(false);
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, oltChangeListener);
	}

	private void queryData(){
		
		eponTopoEntity = (EponTopoEntity) adapterManager.getAdapter(
				equipmentModel.getLastSelected(), EponTopoEntity.class);
		
		if(null == eponTopoEntity){
			return;
		}

		oltEntity = NodeUtils.getNodeEntity(eponTopoEntity).getOltEntity();
		setValue();
	}
	
	@SuppressWarnings("unchecked")
	private void setRegionValue(){
		//先清空控件中的值
		clear();
	}
	
	private void setValue(){		
		typeCombox.setText(oltTypeModel.get(oltEntity.getDeviceModel()).getKey());
		typeCombox.setEnabled(false);
		
		ipAddrFld.setIpAddress(oltEntity.getIpValue());
		descriptionFld.setText(eponTopoEntity.getName());
	}
	
	@ViewAction(name=OK, icon=ButtonConstants.SAVE, desc="保存OLT基本信息",role=Constants.MANAGERCODE)
	public void ok(){
		if (null == oltEntity){
			JOptionPane.showMessageDialog(this, "请选择OLT","提示",JOptionPane.NO_OPTION);
			return;
		}

		if(!isValids()){
			return;
		}
		
		OLTEntity existOltEntity = remoteServer.getService().getOLTByIpValue(ipAddrFld.getText().trim());
		
		if((existOltEntity != null) && (!ObjectUtils.equals(existOltEntity.getId(), this.oltEntity.getId()))){
			JOptionPane.showMessageDialog(this, "IP地址不能相同，请重新输入IP地址", "提示", JOptionPane.NO_OPTION);
			return;
		}
		
		eponTopoEntity.setIpValue(ipAddrFld.getText().trim());
		eponTopoEntity.setName(descriptionFld.getText().trim());
		
		this.oltEntity.setIpValue(ipAddrFld.getText().trim());//IP地址
		
		OLTBaseInfo oltBaseInfo = null;
		if(null == this.oltEntity.getOltBaseInfo()){
			oltBaseInfo = new OLTBaseInfo();
		}else{
			oltBaseInfo = this.oltEntity.getOltBaseInfo();
		}

		oltBaseInfo.setDeviceName(deviceNameFld.getText().trim());//设备名称 
		
		AddressEntity addressEntity = null;
		if(null == this.oltEntity.getAddress()){
			addressEntity = new AddressEntity();
			oltEntity.setAddress(addressEntity);
		}else{
			addressEntity = this.oltEntity.getAddress();
		}
		addressEntity.setLongitude(getNewString(longitudeFld.getText().trim()));//经纬度
		addressEntity.setLatitude(getNewString(latitudeFld.getText().trim()));
			
		this.oltEntity.setAddress(addressEntity);
		this.oltEntity.setOltBaseInfo(oltBaseInfo);
		// 保存实体、地址、管理员
		if (oltEntity.getId() == null) {
			oltEntity = (OLTEntity)remoteServer.getService().saveEntity(oltEntity);
		} else {
			oltEntity = (OLTEntity)remoteServer.getService().updateEntity(oltEntity);
		}
		if (eponTopoEntity.getId() == null) {
			eponTopoEntity = (EponTopoEntity)remoteServer.getService().saveEntity(eponTopoEntity);
		} else {
			eponTopoEntity = (EponTopoEntity)remoteServer.getService().updateEntity(eponTopoEntity);
		}
		eponTopoEntity.setOltEntity(this.oltEntity);
		
		showMessageDialog();
		equipmentModel.fireEquipmentUpdated(eponTopoEntity);
		messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
	}
	
	private final MessageOfSwitchConfigProcessorStrategy messageOfSaveProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	private void showMessageDialog(){
		messageOfSaveProcessorStrategy.showInitializeDialog("保存", this);
	}
	
	private final PropertyChangeListener oltChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof EponTopoEntity){
				queryData();
			}
			else {
				oltEntity = null;
				clear();
			}
		}
	};
	
	private void clear(){
		deviceNameFld.setText(StringUtils.EMPTY);
		longitudeFld.setText(StringUtils.EMPTY);
		latitudeFld.setText(StringUtils.EMPTY);
		typeCombox.setText(StringUtils.EMPTY);
		ipAddrFld.setIpAddress(StringUtils.EMPTY);
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
	
	class UserEntityObject
	{
		UserEntity userEntity = null;
		public UserEntityObject(UserEntity userEntity)
		{
			this.userEntity = userEntity;
		}
		
		@Override
		public String toString()
		{
			if(null == this.userEntity)
			{
				return null;
			}
			return this.userEntity.getUserName();
		}
		public UserEntity getUserEntity()
		{
			return userEntity;
		}
	}

	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, oltChangeListener);
	}
	
	//Amend 2010.06.03
	public void keyTypeControl(KeyEvent e)
	{
		char c = e.getKeyChar();
		if (c != '.' &&(c < '0' || c > '9'))
		{
			e.consume();
		}
	}
	public boolean isValids()
	{
		boolean isValid = true;
		
		String longitude = longitudeFld.getText().trim();
		String latitude = latitudeFld.getText().trim();
		
		if(longitude.indexOf(".") == 0)
		{
			JOptionPane.showMessageDialog(this, "经度必须以数字开头，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		if(longitude.indexOf(".") == 0)
		{
			JOptionPane.showMessageDialog(this, "纬度必须以数字开头，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}

		if(longitude.length() > 36 || longitude.length() > 36){
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

		if(!StringUtils.isBlank(longitude)){
			if(!NumberUtils.isNumber(longitude)){
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
		
		if(null == ipAddrFld.getText() || "".equals(ipAddrFld.getText()))
		{
			JOptionPane.showMessageDialog(this, "IP地址不能为空，请输入IP地址","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}else if(ClientUtils.isIllegal(ipAddrFld.getText())){
			JOptionPane.showMessageDialog(this, "IP地址非法，请重新输入","提示",JOptionPane.NO_OPTION);
			return false;
		}
		
		if (descriptionFld.getText().trim().length() > 50) {
			JOptionPane.showMessageDialog(this, "节点名称长度不能超过50个字符，请重新输入","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
		}
		return isValid;
	}
	public String getNewString(String oldString)
	{
		String newString = "";
		
		if(oldString.indexOf(".") < 0)//no dot
		{
			return oldString;
		}
		
		String headString = oldString.substring(0, oldString.indexOf(".") + 1);
		String tailString = oldString.substring(oldString.indexOf(".") + 1, oldString.length());
		
		if(tailString.length() == 0)//dot is the last char
		{
			newString = headString.substring(0, oldString.indexOf("."));
			return newString;
		}
		
		if(tailString.length() <= 6)
		{
			newString = oldString;
			return newString;
		}else
		{
			newString =  headString + tailString.substring(0, 6);
			return newString;
		}
	}
}
