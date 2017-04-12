package com.jhw.adm.client.views.epon;

import static com.jhw.adm.client.core.ActionConstants.OK;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.LocalizationManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.ResourceConstants;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.epon.ONUEntity;
import com.jhw.adm.server.entity.system.AreaEntity;
import com.jhw.adm.server.entity.tuopos.ONUTopoNodeEntity;
import com.jhw.adm.server.entity.util.AddressEntity;
import com.jhw.adm.server.entity.util.Constants;

@Component(ONUBaseInfoView.ID)
@Scope(Scopes.DESKTOP)
public class ONUBaseInfoView extends ViewPart{
	private static final long serialVersionUID = 1L;
	public static final String ID = "ONUBaseInfoView";

	private final JPanel systemPnl = new JPanel(); 
	
	//版本
	private final JPanel softWarePnl = new JPanel();
	private final JLabel softWareVerLbl = new JLabel();
	private final JLabel softWareVerSetLbl = new JLabel();
	
	private final JLabel hardWareVerLbl = new JLabel();
	private final JLabel hardWareVerSetLbl = new JLabel();
	
	//硬件
	private final JPanel hardWarePnl = new JPanel();
	private final JLabel macAddrLbl = new JLabel();
	private final JLabel macAddrSetLbl = new JLabel();
	
	private final JLabel registerStatusLbl = new JLabel();
	private final JLabel registerStatusSetLbl = new JLabel();
	
	//经度
	private final JLabel longitudeLbl = new JLabel();
	private final JTextField longitudeFld = new JTextField();
	
	//纬度
	private final JLabel latitudeLbl = new JLabel();
	private final JTextField latitudeFld = new JTextField();
	
		
	//地址
	private final JLabel addressLbl = new JLabel();
	private final JTextField addressFld = new JTextField();
	
	//ONU编号
	private final JLabel sequenceNoLbl = new JLabel();
	private final JTextField sequenceNoFld = new JTextField();
	
	//ONU与OLT距离
	private final JLabel distanceLbl = new JLabel();
	private final JTextField distanceFld = new JTextField();
	
	//绑定类型
	private final JLabel bingTypeLbl = new JLabel();
	private final JTextField bingTypeFld = new JTextField();
	
	private final JLabel descriptionLbl = new JLabel();
	private final JTextField descriptionFld = new JTextField();
	
	//下端的按钮面板
	private final JPanel bottomPnl = new JPanel();
	private JButton saveBtn = null;
	private JButton closeBtn = null;
	
	private ButtonFactory buttonFactory;
	
	private ONUTopoNodeEntity onuTopoNodeEntity;
	private ONUEntity onuEntity;
	
	private static final Logger LOG = LoggerFactory.getLogger(ONUBaseInfoView.class);
	private final MessageOfSwitchConfigProcessorStrategy messageOfSaveProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	
	@Autowired
	@Qualifier(LocalizationManager.ID)
	private LocalizationManager localizationManager;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
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
		
		sequenceNoFld.setColumns(30);
		longitudeFld.setColumns(30);
		latitudeFld.setColumns(30);
		addressFld.setColumns(50);
		distanceFld.setColumns(30);
		bingTypeFld.setColumns(30);
		
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
		
		panel.add(sequenceNoLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(sequenceNoFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		
		panel.add(bingTypeLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(bingTypeFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
				
		panel.add(longitudeLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(longitudeFld,new GridBagConstraints(1,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		panel.add(latitudeLbl,new GridBagConstraints(2,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		panel.add(latitudeFld,new GridBagConstraints(3,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,0),0,0));
		
		panel.add(distanceLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(distanceFld,new GridBagConstraints(1,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		
		panel.add(addressLbl,new GridBagConstraints(0,4,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(addressFld,new GridBagConstraints(1,4,3,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		
		panel.add(descriptionLbl,new GridBagConstraints(0,5,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(descriptionFld,new GridBagConstraints(1,5,3,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		
		systemPnl.add(panel);
		
		hardWarePnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		macAddrLbl.setPreferredSize(new Dimension(100,30));
		macAddrSetLbl.setPreferredSize(new Dimension(180,30));
		registerStatusLbl.setPreferredSize(new Dimension(100,30));
		registerStatusSetLbl.setPreferredSize(new Dimension(150,30));
		hardWarePnl.add(macAddrLbl);
		hardWarePnl.add(macAddrSetLbl);
		hardWarePnl.add(registerStatusLbl);
		hardWarePnl.add(registerStatusSetLbl);
		
		softWarePnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		softWareVerLbl.setPreferredSize(new Dimension(100,30));
		softWareVerSetLbl.setPreferredSize(new Dimension(180,30));
		hardWareVerLbl.setPreferredSize(new Dimension(100,30));
		hardWareVerSetLbl.setPreferredSize(new Dimension(150,30));
		softWarePnl.add(softWareVerLbl);
		softWarePnl.add(softWareVerSetLbl);
		softWarePnl.add(hardWareVerLbl);
		softWarePnl.add(hardWareVerSetLbl);
		
		JPanel baseInfoPnl = new JPanel();
		baseInfoPnl.setLayout(new GridBagLayout());
		baseInfoPnl.add(systemPnl,new GridBagConstraints(0,0,1,1,0.0,1.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));//
		baseInfoPnl.add(hardWarePnl,new GridBagConstraints(0,1,1,1,0.0,1.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));//
		baseInfoPnl.add(softWarePnl,new GridBagConstraints(0,2,1,1,0.0,1.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(5,5,5,5),0,0));//
		
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		saveBtn = buttonFactory.createButton(OK);
		closeBtn = buttonFactory.createCloseButton();
		bottomPnl.add(saveBtn);
		bottomPnl.add(closeBtn);
		this.setCloseButton(closeBtn);
		
		this.setLayout(new BorderLayout());
		this.add(baseInfoPnl,BorderLayout.NORTH);
		this.add(bottomPnl,BorderLayout.SOUTH);
		
		this.setViewSize(610, 500);
		
		//得到资源文件
		setResource();
	}

	private void setResource(){
		this.setTitle("ONU" + localizationManager.getString(ResourceConstants.BASEINFOVIEW_BASEINFO));
		systemPnl.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"系统"));
		hardWarePnl.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"硬件"));
		softWarePnl.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"版本"));
		
		longitudeLbl.setText("经度");
		latitudeLbl.setText("纬度");
		addressLbl.setText("地址");
		sequenceNoLbl.setText("设备编号");
		distanceLbl.setText("ONU-OLT间距");
		softWareVerLbl.setText("软件版本");
		hardWareVerLbl.setText("硬件版本");
		macAddrLbl.setText("MAC地址");
		registerStatusLbl.setText("注册状态");
		bingTypeLbl.setText("绑定类型");
		descriptionLbl.setText("节点名称");
		
		sequenceNoFld.setEditable(false);
		distanceFld.setEditable(false);
		bingTypeFld.setEditable(false);
		sequenceNoFld.setBackground(Color.WHITE);
		distanceFld.setBackground(Color.WHITE);
		bingTypeFld.setBackground(Color.WHITE);
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, onuChangeListener);
	}

	private void queryData(){
		
		onuTopoNodeEntity = (ONUTopoNodeEntity) adapterManager.getAdapter(
				equipmentModel.getLastSelected(), ONUTopoNodeEntity.class);
		
		if (null == onuTopoNodeEntity){
			return ;
		}
		
		onuEntity = NodeUtils.getNodeEntity(onuTopoNodeEntity).getOnuEntity();
		setValue();
	}
	
	
	private void setValue(){
		AddressEntity addressEntity = onuEntity.getAddressEntity();
		
		if (null != addressEntity){
			longitudeFld.setText(addressEntity.getLongitude());
			latitudeFld.setText(addressEntity.getLatitude());
			addressFld.setText(addressEntity.getAddress());
		}else{
			longitudeFld.setText("");
			latitudeFld.setText("");
			addressFld.setText("");
		}
		
		sequenceNoFld.setText("" + onuEntity.getSequenceNo());
		distanceFld.setText("" + onuEntity.getDistance());
		bingTypeFld.setText("" + onuEntity.getBingType());
		
		softWareVerSetLbl.setText(onuEntity.getSoftware_Version());
		hardWareVerSetLbl.setText(onuEntity.getHardware_Version());
		macAddrSetLbl.setText(onuEntity.getMacValue());
		registerStatusSetLbl.setText(getONUStatus(onuEntity.getNowStatus()));
		descriptionFld.setText(onuTopoNodeEntity.getName());
	}
	
	public String getONUStatus(int nowStatus){
		String status = "";
		if(nowStatus == 0){
			status = "未认证";
		}else if(nowStatus == 1){
			status = "已认证";
		}
		return status;
	}
	
	@ViewAction(name=OK, icon=ButtonConstants.SAVE, desc="保存ONU基本信息",role=Constants.MANAGERCODE)
	public void ok(){
		if (null == onuEntity){
			JOptionPane.showMessageDialog(this, "请选择ONU","提示",JOptionPane.NO_OPTION);
			return;
		}

		if(!isValids())
		{
			return;
		}
		
		if(StringUtils.isNotBlank(macAddrSetLbl.getText())){
			onuTopoNodeEntity.setMacValue(macAddrSetLbl.getText().trim());
		}
		onuTopoNodeEntity.setName(descriptionFld.getText().trim());
		
		AddressEntity addrEntity = null;
		if (onuEntity.getAddressEntity() == null){
			addrEntity = new AddressEntity();
			onuEntity.setAddressEntity(addrEntity);
		}
		else{
			addrEntity = onuEntity.getAddressEntity();
		}
		addrEntity.setAddress(addressFld.getText());
		addrEntity.setLongitude(getNewString(longitudeFld.getText().trim()));
		addrEntity.setLatitude(getNewString(latitudeFld.getText().trim()));

		// 保存实体、地址、管理员
		if (onuEntity.getId() == null) {
			onuEntity = (ONUEntity)remoteServer.getService().saveEntity(onuEntity);
		} else {
			onuEntity = (ONUEntity)remoteServer.getService().updateEntity(onuEntity);
		}
		if (onuTopoNodeEntity.getId() == null) {
			onuTopoNodeEntity = (ONUTopoNodeEntity)remoteServer.getService().saveEntity(onuTopoNodeEntity);
		} else {
			onuTopoNodeEntity = (ONUTopoNodeEntity)remoteServer.getService().updateEntity(onuTopoNodeEntity);
		}
		onuTopoNodeEntity.setOnuEntity(onuEntity);
		
		showMessageDialog();
		equipmentModel.fireEquipmentUpdated(onuTopoNodeEntity);
		messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
	}
	
	private void showMessageDialog(){
		messageOfSaveProcessorStrategy.showInitializeDialog("保存", this);
	}
	
	private final PropertyChangeListener onuChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof ONUTopoNodeEntity){
				queryData();
			}else {
				onuEntity = null;
				clear();
			}
		}
	};
	
	private void clear(){
		longitudeFld.setText("");
		latitudeFld.setText("");
		addressFld.setText("");
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
	
	@Override
	public void dispose() {
		super.dispose();
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, onuChangeListener);
	}
	
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
