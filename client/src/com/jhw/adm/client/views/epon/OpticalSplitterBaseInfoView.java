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
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.epon.EponSplitter;
import com.jhw.adm.server.entity.system.AreaEntity;
import com.jhw.adm.server.entity.tuopos.Epon_S_TopNodeEntity;
import com.jhw.adm.server.entity.util.AddressEntity;
import com.jhw.adm.server.entity.util.Constants;

@Component(OpticalSplitterBaseInfoView.ID)
@Scope(Scopes.DESKTOP)
public class OpticalSplitterBaseInfoView extends ViewPart{
	private static final long serialVersionUID = 1L;
	public static final String ID = "OpticalSplitterBaseInfoView";

	private final JPanel systemPnl = new JPanel(); 
	
	//设备名称
	private final JLabel nameLbl = new JLabel();
	private final JTextField nameFld = new JTextField();
	
	//经度
	private final JLabel longitudeLbl = new JLabel();
	private final JTextField longitudeFld = new JTextField();
	
	//纬度
	private final JLabel latitudeLbl = new JLabel();
	private final JTextField latitudeFld = new JTextField();
	
//	//区域
//	private final JLabel regionLbl = new JLabel();
//	private final JComboBox regionCombox = new JComboBox();
	
	//地址
	private final JLabel addressLbl = new JLabel();
	private final JTextField addressFld = new JTextField();
	
	private final JLabel descriptionLbl = new JLabel();
	private final JTextField descriptionFld = new JTextField();
	
	//下端的按钮面板
	private final JPanel bottomPnl = new JPanel();
	private JButton saveBtn = null;
	private JButton closeBtn = null;
	private ButtonFactory buttonFactory;
	
	private Epon_S_TopNodeEntity eponSTopNodeEntity;
	private EponSplitter eponSplitter;
	
	private static final Logger LOG = LoggerFactory.getLogger(OpticalSplitterBaseInfoView.class);
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
		
		nameFld.setColumns(20);
		longitudeFld.setColumns(20);
		latitudeFld.setColumns(20);
		addressFld.setColumns(20);
		
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
		
//		regionCombox.setSelectedItem(null);
		
		systemPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(nameLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(nameFld,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		
//		panel.add(regionLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
//		panel.add(regionCombox,new GridBagConstraints(1,1,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		
		panel.add(longitudeLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(longitudeFld,new GridBagConstraints(1,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		panel.add(latitudeLbl,new GridBagConstraints(2,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		panel.add(latitudeFld,new GridBagConstraints(3,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,10,5,0),0,0));
		
		panel.add(addressLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(addressFld,new GridBagConstraints(1,2,3,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,50,5,0),0,0));
		
		panel.add(descriptionLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,0,5,0),0,0));
		panel.add(descriptionFld,new GridBagConstraints(1,3,3,1,0.0,0.0,
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
		this.setTitle("分光器" + localizationManager.getString(ResourceConstants.BASEINFOVIEW_BASEINFO));
		systemPnl.setBorder(BorderFactory.createTitledBorder("系统"));
		
		nameLbl.setText("设备名称");
		longitudeLbl.setText("经度");
		latitudeLbl.setText("纬度");
//		regionLbl.setText("区域");
		addressLbl.setText("地址");
		descriptionLbl.setText("节点名称");
		
		nameFld.setEditable(false);
		nameFld.setBackground(Color.WHITE);
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, onuChangeListener);
	}

	private void queryData(){
		
		eponSTopNodeEntity = (Epon_S_TopNodeEntity) adapterManager.getAdapter(
				equipmentModel.getLastSelected(), Epon_S_TopNodeEntity.class);
		
		if (null == eponSTopNodeEntity){
			return ;
		}
		
		eponSplitter = eponSTopNodeEntity.getEponSplitter();
		if (null == eponSplitter){
			return;
		}

		//先清空控件中的值
		clear();
//		//向数据库查询所有的地区
//		List<AreaEntity> areaEntityList = (List<AreaEntity>)remoteServer.getService().findAll(AreaEntity.class);
//		initRegionCombox(areaEntityList);
		
		setValue();
	}
	
	@SuppressWarnings("static-access")
//	private void initRegionCombox(List<AreaEntity> areaEntityList){
//		regionCombox.removeAllItems();
//		for (int i = 0 ; i < areaEntityList.size(); i++){
//			AreaEntityObject object = new AreaEntityObject(areaEntityList.get(i));
//			regionCombox.addItem(object);
//		}
//		
//		regionCombox.setSelectedItem(null);
//	}
	
	private void setValue(){
		AddressEntity addressEntity = eponSplitter.getAddressEntity();
		
		if (null != addressEntity){
			longitudeFld.setText(addressEntity.getLongitude());
			latitudeFld.setText(addressEntity.getLatitude());
			addressFld.setText(addressEntity.getAddress());
//			for (int i = 0 ; i < regionCombox.getItemCount(); i++){
//				AreaEntityObject object = (AreaEntityObject)regionCombox.getItemAt(i);
//				if (null != addressEntity.getArea()){
//					if (object.getAreaEntity().getId().longValue() == addressEntity.getArea().getId().longValue()){
//						regionCombox.setSelectedIndex(i);
//					}
//				}
//			}
		}
		nameFld.setText(eponSTopNodeEntity.getName());
		descriptionFld.setText(eponSTopNodeEntity.getName());
	}
	
	@ViewAction(name=OK, icon=ButtonConstants.SAVE, desc="保存分光器基本信息",role=Constants.MANAGERCODE)
	public void ok(){
		if (null == eponSplitter){
			JOptionPane.showMessageDialog(this, "请选择分光器","提示",JOptionPane.NO_OPTION);
			return;
		}

		if(!isValids())
		{
			return;
		}
		
		eponSTopNodeEntity.setName(descriptionFld.getText().trim());
		
		AddressEntity addrEntity = null;
		if (eponSplitter.getAddressEntity() == null){
			addrEntity = new AddressEntity();
			eponSplitter.setAddressEntity(addrEntity);
		}
		else{
			addrEntity = eponSplitter.getAddressEntity();
		}
		addrEntity.setAddress(addressFld.getText());
		addrEntity.setLongitude(getNewString(longitudeFld.getText().trim()));
		addrEntity.setLatitude(getNewString(latitudeFld.getText().trim()));
//		if (null == regionCombox.getSelectedItem()){
//			addrEntity.setArea(null);
//		}else{
//			addrEntity.setArea(((AreaEntityObject)regionCombox.getSelectedItem()).getAreaEntity());
//		}
		eponSplitter.setName(nameFld.getText());
		// 保存实体、地址、管理员
		if (eponSplitter.getId() == null) {
			eponSplitter = (EponSplitter)remoteServer.getService().saveEntity(eponSplitter);
		} else {
			eponSplitter = (EponSplitter)remoteServer.getService().updateEntity(eponSplitter);
		}
		if (eponSTopNodeEntity.getId() == null) {
			eponSTopNodeEntity = (Epon_S_TopNodeEntity)remoteServer.getService().saveEntity(eponSTopNodeEntity);
		} else {
			eponSTopNodeEntity = (Epon_S_TopNodeEntity)remoteServer.getService().updateEntity(eponSTopNodeEntity);
		}
		eponSTopNodeEntity.setEponSplitter(eponSplitter);
		
		messageOfSaveProcessorStrategy.showInitializeDialog("保存", this);
		equipmentModel.fireEquipmentUpdated(eponSTopNodeEntity);
		messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		
	}
	
	private final PropertyChangeListener onuChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof Epon_S_TopNodeEntity){
				queryData();
			}
			else {
				eponSplitter = null;
				clear();
			}
		}
	};
	
	private void clear(){
		nameFld.setText("");
		longitudeFld.setText("");
		latitudeFld.setText("");
//		regionCombox.removeAllItems();
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
