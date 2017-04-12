package com.jhw.adm.client.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ActionConstants;
import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.IpAddressField;
import com.jhw.adm.client.swing.JProgressBarDialog;
import com.jhw.adm.client.swing.JProgressBarModel;
import com.jhw.adm.client.swing.SimpleConfigureStrategy;
import com.jhw.adm.client.swing.StarLabel;
import com.jhw.adm.client.views.switcher.Task;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.VirtualNodeEntity;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.VirtualType;

@Component(VirtualNetworkElementManagementView.ID)
@Scope(Scopes.DESKTOP)
public class VirtualNetworkElementManagementView extends ViewPart {

	private static final long serialVersionUID = 1L;
	public static final String ID = "virtualNetworkElementManagementView";
	private static final Logger LOG = LoggerFactory.getLogger(VirtualNetworkElementManagementView.class);
	
	private final JPanel buttonPanel = new JPanel();
	private JButton saveButton = null;
	private JButton closeButton = null;
	
	private final JPanel virtualElementPanel = new JPanel();
	private final JPanel baseInformationPanel = new JPanel();
	
	private final IpAddressField virtualIpAddressField = new IpAddressField();
	private final JTextField virtualNameField = new JTextField();
	private final JComboBox virtualTypeComboBox = new JComboBox();
	private final JLabel virtualPictureLabel = new JLabel();
	
	private ButtonFactory buttonFactory;
	private boolean selected = false;//用于保存时区别是否选中虚拟网元,true:选中,false:未选中
	private VirtualNodeEntity virtualNodeEntity = null;
	public VirtualNodeEntity getVirtualNodeEntity() {
		return virtualNodeEntity;
	}
	public void setVirtualNodeEntity(VirtualNodeEntity virtualNodeEntity) {
		this.virtualNodeEntity = virtualNodeEntity;
	}
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	private final PropertyChangeListener networkElementChangeListener = new PropertyChangeListener() {
		
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if(evt.getNewValue() instanceof VirtualNodeEntity){
				setComponentValue();
			}else{
				setVirtualNodeEntity(null);
				clear();
				selected = false;
			}
		}
	};
	
	private void clear(){
		virtualIpAddressField.setText("");
		virtualNameField.setText("");
		virtualTypeComboBox.setSelectedItem(null);
	}
	
	@PostConstruct
	protected void initialize(){
		buttonFactory = actionManager.getButtonFactory(this);
		equipmentModel.addPropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, networkElementChangeListener);
		
		createElementPanel(virtualElementPanel);
		createButtonPanel(buttonPanel);
		
		this.setLayout(new BorderLayout());
		this.add(virtualElementPanel, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.PAGE_END);
		this.setTitle("虚拟网元管理");
		this.setViewSize(500, 400);
		
//		setComponentEnabled(false);
		setComponentValue();
	}
	
	/**
	 * set component value after initialize this view
	 */
	private void setComponentValue() {
		VirtualNodeEntity virtualNodeEntity = (VirtualNodeEntity) adapterManager.getAdapter(
				equipmentModel.getLastSelected(), VirtualNodeEntity.class);
		setVirtualNodeEntity(virtualNodeEntity);
		
		if(null == getVirtualNodeEntity()){
			return;
		}
		
		selected = true;
		
		virtualIpAddressField.setText(virtualNodeEntity.getIpValue());
		virtualNameField.setText(virtualNodeEntity.getName());
		for(int i = 0; i < virtualTypeComboBox.getItemCount(); i++){
			VirtualElementObject object = (VirtualElementObject)virtualTypeComboBox.getItemAt(i);
			if (object.getValue().getId() == virtualNodeEntity.getType()){
				virtualTypeComboBox.setSelectedIndex(i);
			}
		}
	}

	/**
	 * create virtual network elements' base information panel
	 * @param parent
	 */
	private void createElementPanel(JPanel parent) {
		parent.setLayout(new GridLayout(1, 1));
		
		createBaseInformationPanel(baseInformationPanel);
		initializeComponentValue();
		
		parent.add(baseInformationPanel);
	}
	
	private void createBaseInformationPanel(JPanel parent) {
		parent.setLayout(new FlowLayout(FlowLayout.LEADING));
		parent.setBorder(BorderFactory.createTitledBorder(BorderFactory
				.createLineBorder(new JTable().getGridColor()),"基本信息"));
		
		virtualIpAddressField.setColumns(25);
		virtualNameField.setColumns(25);
		
		JPanel panel = new JPanel(new GridBagLayout());
		
		panel.add(new JLabel("IP地址"), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,new Insets(0, 0, 5, 0), 0, 0));
		panel.add(virtualIpAddressField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,new Insets(0, 20, 5, 0), 0, 0));
		panel.add(new StarLabel(), new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,new Insets(0, 10, 5, 0), 0, 0));
		
		panel.add(new JLabel("节点名称"), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,new Insets(0, 0, 5, 0), 0, 0));
		panel.add(virtualNameField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,new Insets(0, 20, 5, 0), 0, 0));
		
		panel.add(new JLabel("网元类型"), new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,new Insets(0, 0, 5, 0), 0, 0));
		panel.add(virtualTypeComboBox, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,new Insets(0, 20, 5, 0), 0, 0));
		panel.add(new JLabel("图例"), new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,new Insets(0, 20, 5, 0), 0, 0));
		panel.add(virtualPictureLabel, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,new Insets(0, 20, 5, 0), 0, 0));
		
		parent.add(panel);
	}
	
	/**
	 * initialize component value when initialize this view
	 */
	@SuppressWarnings("unchecked")
	private void initializeComponentValue() {
		virtualTypeComboBox.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED){
					VirtualElementObject object = (VirtualElementObject) virtualTypeComboBox.getSelectedItem();
					VirtualType virtualType = object.getValue();
					ImageIcon imageIcon = getIamgeIcon(virtualType.getBytes());
					resetLabelImageIcon(imageIcon);
				}
			}
		});
		
		List<VirtualType> virtualTypeList = (List<VirtualType>) remoteServer.getService().findAll(VirtualType.class);
		for(VirtualType virtualType : virtualTypeList){
			VirtualElementObject object = new VirtualElementObject(virtualType);
			virtualTypeComboBox.addItem(object);
		}
		if(virtualTypeList.size() > 0){
			virtualTypeComboBox.setSelectedIndex(0);
		}
		virtualTypeComboBox.setEditable(false);
	}
	
	/**
	 * get an ImageIcon for JLabel
	 * @param imageName
	 * @return ImageIcon
	 */
	private ImageIcon getIamgeIcon(byte[] images){
		return new ImageIcon(images);
	}

	protected void resetLabelImageIcon(final ImageIcon imageIcon) {
		if(SwingUtilities.isEventDispatchThread()){
			this.virtualPictureLabel.removeAll();
			this.virtualPictureLabel.setIcon(imageIcon);
		}else {
			SwingUtilities.invokeLater(new Runnable() {
				
				@Override
				public void run() {
					resetLabelImageIcon(imageIcon);
				}
			});
		}
	}

	/**
	 * create button panel
	 * @param parent
	 */
	private void createButtonPanel(JPanel parent) {
		parent.setLayout(new FlowLayout(FlowLayout.TRAILING));
		
		saveButton = buttonFactory.createButton(ActionConstants.SAVE);
		closeButton = buttonFactory.createCloseButton();
		this.setCloseButton(closeButton);
		
		parent.add(saveButton);
		parent.add(closeButton);
	}
	
	@ViewAction(name=ActionConstants.SAVE,icon=ButtonConstants.SAVE,desc="保存虚拟网元的信息",role=Constants.MANAGERCODE)
	public void save(){
		if(selected == false){
			JOptionPane.showMessageDialog(this, "请选择虚拟网元","提示",JOptionPane.NO_OPTION);
			return;
		}
		
		VirtualNodeEntity nodeEntity = this.getVirtualNodeEntity();
		if(null == nodeEntity){
			nodeEntity = new VirtualNodeEntity();
		}
		String ipAddress = virtualIpAddressField.getText().trim();
		if(!isValids(ipAddress,nodeEntity)){
			return;
		}
		
		nodeEntity.setIpValue(ipAddress);
		nodeEntity.setName(virtualNameField.getText().trim());
		nodeEntity.setType(((VirtualElementObject)virtualTypeComboBox.getSelectedItem()).getValue().getId());
		
		Task task = new RequestTask(nodeEntity);
		showMessageDialog(task);
	}
	
	private class RequestTask implements Task{

		private VirtualNodeEntity virtualNodeEntity;
		public RequestTask(VirtualNodeEntity virtualNodeEntity){
			this.virtualNodeEntity = virtualNodeEntity;
		}
		
		@Override
		public void run() {
			try{
				if(virtualNodeEntity.getId() == null){
					this.virtualNodeEntity = (VirtualNodeEntity) remoteServer.getService().saveEntity(this.virtualNodeEntity);
				}else{
					this.virtualNodeEntity = (VirtualNodeEntity) remoteServer.getService().updateEntity(this.virtualNodeEntity);
				}
			}catch(Exception e){
				strategy.showErrorMessage("保存虚拟网元异常");
				LOG.error("Error occur when saving virtual element!", e);
			}
			setVirtualNodeEntity(this.virtualNodeEntity);
			equipmentModel.fireEquipmentUpdated(this.virtualNodeEntity);
			strategy.showNormalMessage("保存虚拟网元成功");
		}
	}
	
	private JProgressBarModel progressBarModel;
	private SimpleConfigureStrategy strategy ;
	private JProgressBarDialog dialog;
	private void showMessageDialog(final Task task){
		if(SwingUtilities.isEventDispatchThread()){
			progressBarModel = new JProgressBarModel();
			strategy = new SimpleConfigureStrategy("保存虚拟网元", progressBarModel);
			dialog = new JProgressBarDialog("提示",0,1,this,"保存虚拟网元",true);
			dialog.setModel(progressBarModel);
			dialog.setStrategy(strategy);
			dialog.run(task);
		}else{
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showMessageDialog(task);
				}
			});
		}
	}

	@SuppressWarnings("unchecked")
	private boolean isValids(String compareIp,VirtualNodeEntity nodeEntity) {
		
		if(StringUtils.isBlank(compareIp)){
			JOptionPane.showMessageDialog(this, "IP地址不能为空，请重新输入", "提示", JOptionPane.NO_OPTION);
			return false;
		}
		
		String type = virtualNameField.getText().trim();
		if(type.length() > 50 ){
			JOptionPane.showMessageDialog(this, "节点名称长度不能超过50个字符，请重新输入", "提示", JOptionPane.NO_OPTION);
			return false;
		}
		
		String[] parms = {compareIp};
		//compare with the virtual network elements
		List<VirtualNodeEntity> list = (List<VirtualNodeEntity>) remoteServer.getService().findAll(VirtualNodeEntity.class, " where entity.ipValue = ?", parms);
		if(list.size() > 0){
			VirtualNodeEntity entity = list.get(0);
			if((entity != null) && (!ObjectUtils.equals(nodeEntity.getId(), entity.getId()))){
;				JOptionPane.showMessageDialog(this, "IP地址不能重复，请重新输入IP地址", "提示", JOptionPane.NO_OPTION);
				return false;
			}
		}
		//compare with auto-refresh 2-layer-switchers
		SwitchNodeEntity switchNodeEntity = remoteServer.getService().getSwitchByIp(compareIp);
		if(null != switchNodeEntity){
			JOptionPane.showMessageDialog(this, "IP地址不能重复，请重新输入IP地址", "提示", JOptionPane.NO_OPTION);
			return false;
		}
		//compare with auto-refresh 3-layer-swtichers
		SwitchLayer3 switchLayer3 = remoteServer.getService().getSwitcher3ByIP(compareIp);
		if(null != switchLayer3){
			JOptionPane.showMessageDialog(this, "IP地址不能重复，请重新输入IP地址", "提示", JOptionPane.NO_OPTION);
			return false;
		}
		//compare with auto-refresh olts
		OLTEntity oltEntity = remoteServer.getService().getOLTByIpValue(compareIp);
		if(null != oltEntity){
			JOptionPane.showMessageDialog(this, "IP地址不能重复，请重新输入IP地址", "提示", JOptionPane.NO_OPTION);
			return false;
		}
		//compare with FEP
		FEPEntity fepEntity = remoteServer.getService().getFEPEntityByIP(compareIp);
		if(null != fepEntity){
			JOptionPane.showMessageDialog(this, "IP地址不能重复，请重新输入IP地址", "提示", JOptionPane.NO_OPTION);
			return false;
		}
		return true;
	}
	
	private class VirtualElementObject{
		VirtualType virtualType = null;
		public VirtualElementObject(VirtualType virtualType){
			this.virtualType = virtualType;
		}
		
		@Override
		public String toString(){
			if(null == virtualType){
				return null;
			}
			
			return virtualType.getType().trim();
		}
		
		public VirtualType getValue(){
			return this.virtualType;
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		equipmentModel.removePropertyChangeListener(EquipmentModel.PROP_LAST_SELECTED, networkElementChangeListener);
	}

}
