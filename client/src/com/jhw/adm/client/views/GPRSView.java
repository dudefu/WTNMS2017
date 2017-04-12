package com.jhw.adm.client.views;

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
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
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
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.system.AreaEntity;
import com.jhw.adm.server.entity.tuopos.GPRSTopoNodeEntity;
import com.jhw.adm.server.entity.util.AddressEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.GPRSEntity;

@Component(GPRSView.ID)
@Scope(Scopes.DESKTOP)
public class GPRSView extends ViewPart {
	public static final String ID = "gprsView";
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(GPRSView.class);
	
	private final JPanel centerPnl = new JPanel();

	private final JLabel userIdLbl = new JLabel();//身份识别码
	private final JTextField userIdFld = new JTextField();//
	
	private final JLabel longitudeLbl = new JLabel();  //经度
	private final JTextField longitudeFld = new JTextField();
	
	private final JLabel latitudeLbl = new JLabel();  //纬度
	private final JTextField latitudeFld = new JTextField();
	
//	private final JLabel areaLbl = new JLabel();  //地区
//	private final JComboBox areaCombox = new JComboBox();
	
	private final JLabel proxyIpAddrLbl = new JLabel();//代理主机IP地址
	private final JTextField proxyIpAddrFld = new JTextField();
	
	private final JLabel proxyPortLbl = new JLabel();//代理主机端口
	private final JTextField proxyPortFld = new JTextField();
	
	private final JLabel localIpAddrLbl = new JLabel();//DTU在移动网内IP地址
	private final JTextField localIpAddrFld = new JTextField();
	
	private final JLabel localPortLbl = new JLabel();//DTU在移动网内IP端口
	private final JTextField localPortFld = new JTextField();
	
	private final JLabel dateLbl = new JLabel();//登录时间
	private final JTextField dateFld = new JTextField();
	
	private JLabel descriptionLbl = new JLabel();
	private JTextField descriptionFld = new JTextField();
	
	//Amend 2010.06.08
//	private JLabel statusLbl = new JLabel();//状态  1 在线 ，0 不在线
//	private JLabel statusIconLbl = new JLabel();
	
	
	private final JPanel bottomPnl = new JPanel();
	private JButton saveBtn;
	private JButton closeBtn;
	
	private ButtonFactory buttonFactory;
	
	private final static int ONLINE = 1;
	private final static int OFFLINE = 0;
	
	private GPRSTopoNodeEntity gprsNode;
	private GPRSEntity gprsEntity = null;
	
	@Autowired
	@Qualifier(ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Autowired
	@Qualifier(RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@PostConstruct
	protected void initialize() {
		init();
		queryData();
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this); 
		initCenterPnl();
		initBottomPnl();
		
		this.setLayout(new BorderLayout());
		this.add(centerPnl,BorderLayout.CENTER);
		this.add(bottomPnl,BorderLayout.SOUTH);
		this.setViewSize(560, 410);
		
		setResource();
//		setRegionValue();
	}
	
	private void initCenterPnl(){
		JPanel container = new JPanel(new GridBagLayout());
		
		//Amend 2010.06.08
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
		
		container.add(userIdLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,5,10,0),0,0));
		container.add(userIdFld,new GridBagConstraints(1,0,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(0,30,10,0),0,0));
		
		container.add(longitudeLbl,new GridBagConstraints(0,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,0),0,0));
		container.add(longitudeFld,new GridBagConstraints(1,1,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,10,0),0,0));
		container.add(latitudeLbl,new GridBagConstraints(3,1,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,10,0),0,0));
		container.add(latitudeFld,new GridBagConstraints(4,1,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,10,0),0,0));
		
//		container.add(areaLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,0),0,0));
//		container.add(areaCombox,new GridBagConstraints(1,2,2,1,0.0,0.0,
//				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,10,0),0,0));

		container.add(proxyIpAddrLbl,new GridBagConstraints(0,2,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,0),0,0));
		container.add(proxyIpAddrFld,new GridBagConstraints(1,2,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,10,0),0,0));
		
		container.add(proxyPortLbl,new GridBagConstraints(0,3,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,0),0,0));
		container.add(proxyPortFld,new GridBagConstraints(1,3,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,10,0),0,0));
		
		container.add(localIpAddrLbl,new GridBagConstraints(0,4,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,0),0,0));
		container.add(localIpAddrFld,new GridBagConstraints(1,4,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,10,0),0,0));
		
		container.add(localPortLbl,new GridBagConstraints(0,5,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,0),0,0));
		container.add(localPortFld,new GridBagConstraints(1,5,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,10,0),0,0));
		
		container.add(dateLbl,new GridBagConstraints(0,6,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,0),0,0));
		container.add(dateFld,new GridBagConstraints(1,6,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,10,0),0,0));
		
		container.add(descriptionLbl,new GridBagConstraints(0,7,1,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,5,10,0),0,0));
		container.add(descriptionFld,new GridBagConstraints(1,7,2,1,0.0,0.0,
				GridBagConstraints.CENTER,GridBagConstraints.HORIZONTAL,new Insets(5,30,10,0),0,0));
		
		centerPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		centerPnl.setBorder(BorderFactory.createTitledBorder("GPRS"));
		centerPnl.add(container);
	}
	
	private void initBottomPnl(){
		bottomPnl.setLayout(new FlowLayout(FlowLayout.RIGHT));
		saveBtn = buttonFactory.createButton(OK);
		closeBtn = buttonFactory.createCloseButton();
		bottomPnl.add(saveBtn);
		bottomPnl.add(closeBtn);
		this.setCloseButton(closeBtn);
	}
	
	private void setResource(){
		this.setTitle("GPRS");
		userIdLbl.setText("身份识别码");
		longitudeLbl.setText("经度");
		latitudeLbl.setText("纬度");
//		areaLbl.setText("地区");
		proxyIpAddrLbl.setText("代理主机IP地址");
		proxyPortLbl.setText("代理主机端口");
		localIpAddrLbl.setText("本地IP地址");
		localPortLbl.setText("本地端口");
		dateLbl.setText("登录时间");
		descriptionLbl.setText("节点名称");
//		statusLbl.setText("状态");
		
		userIdFld.setColumns(20);
		longitudeFld.setColumns(20);
		latitudeFld.setColumns(20);
		
		proxyIpAddrFld.setEditable(false);
		proxyPortFld.setEditable(false);
		localIpAddrFld.setEditable(false);
		localPortFld.setEditable(false);
		dateFld.setEditable(false);
		proxyIpAddrFld.setBackground(Color.WHITE);
		proxyPortFld.setBackground(Color.WHITE);
		localIpAddrFld.setBackground(Color.WHITE);
		localPortFld.setBackground(Color.WHITE);
		dateFld.setBackground(Color.WHITE);
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, gprsChangeListener);
	}
	
	private void queryData(){
		gprsNode = (GPRSTopoNodeEntity)adapterManager.getAdapter(
				equipmentModel.getLastSelected(), GPRSTopoNodeEntity.class);
		
		if(null == gprsNode){
			return;
		}
		
		gprsEntity = NodeUtils.getNodeEntity(gprsNode).getEntity();
		setValue();
	}
	
//	private void setRegionValue(){
//		//向数据库查询所有的地区
//		List<AreaEntity> areaEntityList = (List<AreaEntity>)remoteServer.getService().findAll(AreaEntity.class);
//		if (areaEntityList != null){
//			areaCombox.removeAllItems();
//			for (int i = 0 ; i < areaEntityList.size(); i++){
//				AreaEntityObject object = new AreaEntityObject(areaEntityList.get(i));
//				areaCombox.addItem(object);
//			}
//		}
//
//	}
	
	private void setValue(){
//		areaCombox.setSelectedItem(null);
		
		String userId = gprsEntity.getUserId();//身份识别码
		String longitude = "";//经度
		String latitude = "";//纬度
		AreaEntity areaEntity = null;
		if (null != gprsEntity.getAddress()){
			longitude = gprsEntity.getAddress().getLongitude(); //经度
			latitude = gprsEntity.getAddress().getLatitude();//纬度
			areaEntity = gprsEntity.getAddress().getArea(); //地区
		}
		
		String internalAddr = gprsEntity.getInternateAddress();//代理主机IP地址
		int internalPort = gprsEntity.getPort();//代理主机端口
		String localAddr = gprsEntity.getLocalAddress();//
		int localPort = gprsEntity.getLocalPort();
		String date = gprsEntity.getLogonDate();
		
		userIdFld.setText(userId);
		longitudeFld.setText(longitude);
		latitudeFld.setText(latitude);
		
//		for (int i = 0 ; i < areaCombox.getItemCount(); i++){
//			AreaEntityObject object = (AreaEntityObject)areaCombox.getItemAt(i);
//			if (null != areaEntity){
//				if (object.getAreaEntity().getId().longValue() == areaEntity.getId().longValue()){
//					areaCombox.setSelectedIndex(i);
//				}
//			}
//		}
		
		proxyIpAddrFld.setText(internalAddr);
		proxyPortFld.setText(internalPort + "");
		localIpAddrFld.setText(localAddr);
		localPortFld.setText(localPort+"");
		dateFld.setText(date);
		descriptionFld.setText(gprsNode.getName());
	}
	
	@ViewAction(name=OK, icon=ButtonConstants.SAVE,desc="保存GPRS信息",role=Constants.MANAGERCODE)
	public void ok(){
		if (!isValids()){
			return;
		}
		
		gprsNode.setUserId(userIdFld.getText().trim());
		gprsNode.setName(descriptionFld.getText().trim());
		
		AddressEntity addrEntity = null;
		if (gprsEntity.getAddress() == null){
			addrEntity = new AddressEntity();
		}else{
			addrEntity = gprsEntity.getAddress();
		}
		
		gprsEntity.setUserId(userIdFld.getText().trim());
		addrEntity.setLongitude(getNewString(this.longitudeFld.getText().trim().toString()));//经度
		addrEntity.setLatitude(getNewString(this.latitudeFld.getText().trim().toString()));//纬度
//		if (null == areaCombox.getSelectedItem()){
//			addrEntity.setArea(null);
//		}
//		else{
//			addrEntity.setArea(((AreaEntityObject)areaCombox.getSelectedItem()).getAreaEntity());
//		}
		
		gprsEntity.setAddress(addrEntity);

		Task task = new RequestTask();
		showMessageDialog(task, "保存");
	}
	
	private class RequestTask implements Task{
		
		public RequestTask(){
			//
		}
		
		@Override
		public void run() {
			try{
				// 保存实体、地址、管理员
				if (gprsEntity.getId() == null) {
					gprsEntity = (GPRSEntity)remoteServer.getService().saveEntity(gprsEntity);
				} else {
					gprsEntity = (GPRSEntity)remoteServer.getService().updateEntity(gprsEntity);
				}
				if (gprsNode.getId() == null) {
					gprsNode = (GPRSTopoNodeEntity)remoteServer.getService().saveEntity(gprsNode);
				} else {
					gprsNode = (GPRSTopoNodeEntity)remoteServer.getService().updateEntity(gprsNode);
				}
			}catch(Exception e){
				strategy.showErrorMessage("保存网管侧异常");
				LOG.error("SysLogHostEntity.append() error", e);
			}
			gprsNode.setEntity(gprsEntity);
			equipmentModel.fireEquipmentUpdated(gprsNode);
			strategy.showNormalMessage("保存网管侧成功");
		}
	}
	
	private JProgressBarModel progressBarModel;
	private SimpleConfigureStrategy strategy ;
	private JProgressBarDialog dialog;
	private void showMessageDialog(final Task task, final String operation){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			strategy = new SimpleConfigureStrategy(operation, progressBarModel);
			dialog = new JProgressBarDialog("提示",0,1,this,operation,true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(strategy);
			dialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showMessageDialog(task,operation);
				}
			});
		}
	}
	
	private boolean isValids(){
		boolean isValid = true;
		if (null == gprsEntity){
			JOptionPane.showMessageDialog(this, "请选择GPRS设备","提示",JOptionPane.NO_OPTION);
			isValid = false;
			return isValid;
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
		
		return isValid;
	}
	
	public void keyTypeControl(KeyEvent e)
	{
		char c = e.getKeyChar();
		if (c != '.' &&(c < '0' || c > '9'))
		{
			e.consume();
		}
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
	private final PropertyChangeListener gprsChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof GPRSTopoNodeEntity){
				queryData();
			}
			else {
				gprsEntity = null;
				clear();
			}
		}
	};

	private void clear(){
		userIdFld.setText("");
		longitudeFld.setText("");
		latitudeFld.setText("");
//		areaCombox.removeAllItems();
//		areaCombox.setSelectedItem(null);
		proxyIpAddrFld.setText("");
		proxyPortFld.setText("");
		localIpAddrFld.setText("");
		localPortFld.setText("");
		dateFld.setText("");
//		statusIconLbl.setText("");
	}
	
	private Icon reverseIntToIcon(int value){
		Icon icon = null;
		if (ONLINE == value){
			icon = imageRegistry.getImageIcon(NetworkConstants.ONLINE);
		}
		else if (OFFLINE == value){
			icon = imageRegistry.getImageIcon(NetworkConstants.OFFLINE);
		}
		
		return icon;
	}
	
	
	class AreaEntityObject {
		AreaEntity areaEntity = null;
		public AreaEntityObject(AreaEntity areaEntity){
			this.areaEntity = areaEntity;
		}
		
		@Override
		public String toString(){
			return this.areaEntity.getName();
		}

		public AreaEntity getAreaEntity() {
			return areaEntity;
		}
	}
}
