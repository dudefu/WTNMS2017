package com.jhw.adm.client.views.switchlayer3;

import static com.jhw.adm.client.core.ActionConstants.SAVE;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

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
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.swing.MessageReceiveInter;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.level3.Switch3VlanEnity;
import com.jhw.adm.server.entity.level3.Switch3VlanPortEntity;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(VlanPortConfigLayer3View.ID)
@Scope(Scopes.DESKTOP)
public class VlanPortConfigLayer3View extends ViewPart implements MessageReceiveInter{
	private static final long serialVersionUID = 1L;
	public static final String ID = "vlanPortConfigLayer3View";
	//上端的面板
	private final JPanel topPnl = new JPanel();
	private final JLabel portLbl = new JLabel();
	private final JLabel vlanLbl = new JLabel();
	private final JLabel modeLbl = new JLabel();
	private final JLabel isUntagLbl = new JLabel();
	private final JLabel isAllowLbl = new JLabel();
	
	//中间面板
	private final JScrollPane scrollPnl = new JScrollPane();
	private final JPanel middlePnl = new JPanel();
	
	
	//工具按钮面板
	private final JPanel toolBtnPnl = new JPanel();
	private JButton saveBtn;
	private JButton closeBtn = null;
	
	private ButtonFactory buttonFactory;
	
	private final List<List> componentList = new ArrayList<List>();
	
	private static final String[] MODEVALUE = {"Access","Trunk"};
	
	private static final String[] BOOLEANVALUE = {"Yes","No"};
	
	private VlanPortViewModel vlanPortModel;
	
	private int defVlanID = 1;
	
	private SwitchLayer3 switchLayer3 = null;
	
	private VlanManageLayer3View vlanManageLayer3View = null;
	
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
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	private final MessageOfSwitchConfigProcessorStrategy messageOfSaveProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	
	private static final Logger LOG = LoggerFactory.getLogger(VlanPortConfigLayer3View.class);
	
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
		
		this.setLayout(new BorderLayout());
		this.add(topPnl,BorderLayout.NORTH);
		this.add(scrollPnl,BorderLayout.CENTER);
		this.add(toolBtnPnl,BorderLayout.SOUTH);
		
		setResource();
	}
	
	private void initTopPnl(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(portLbl,new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,35,0,0),0,0));
		
		panel.add(vlanLbl,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,100,0,0),0,0));
		panel.add(modeLbl,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,95,0,10),0,0));
		panel.add(isUntagLbl,new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,56,0,10),0,0));
		panel.add(isAllowLbl,new GridBagConstraints(4,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,32,0,10),0,0));
		
		topPnl.setLayout(new FlowLayout(FlowLayout.LEADING));
		topPnl.add(panel);
	}
	
	private void initCenterPnl(){
		middlePnl.setLayout(new GridBagLayout());

		JPanel centerPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		centerPnl.add(middlePnl);
		
		scrollPnl.getViewport().add(centerPnl);
	}
	
	private void initToolBtnPnl(){
		toolBtnPnl.setLayout(new BorderLayout());
		JPanel newPanel = new JPanel(new GridBagLayout());
		closeBtn = buttonFactory.createCloseButton();
		saveBtn = buttonFactory.createButton(SAVE);
//		newPanel.add(synBtn,new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, Constant.BUTTONINTERVAL), 0, 0));
		newPanel.add(saveBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		toolBtnPnl.add(newPanel,BorderLayout.EAST);
	}
	
	private void setResource(){
		portLbl.setText("端口");
		vlanLbl.setText("默认VLAN");
		modeLbl.setText("模式");
		isUntagLbl.setText("是否去标签");
		isAllowLbl.setText("是否允许");
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}
	
	private void queryData(){
		switchLayer3 = (SwitchLayer3) adapterManager
			.getAdapter(equipmentModel.getLastSelected(), SwitchLayer3.class);
		if (null == switchLayer3){
			return ;
		}
		
		String where = " where entity.ipValue=?";
		Object[] parms = {switchLayer3.getIpValue()};
		List<Switch3VlanPortEntity> vlanPortEntityList = (List<Switch3VlanPortEntity>)remoteServer
					.getService().findAll(Switch3VlanPortEntity.class, where, parms);
		
		//初始化中心面板
		setCenterLayout(vlanPortEntityList);
		
		//设置控件的值
		setValue(vlanPortEntityList);
	}
	
	/**
	 * 通过查询出的端口列表布局panel的所有控件
	 */
	private void setCenterLayout(List<Switch3VlanPortEntity> vlanPortEntityList){
		componentList.clear();
		middlePnl.removeAll();
		
		if (vlanPortEntityList == null){
			return;
		}
		
		vlanPortModel = new VlanPortViewModel(vlanPortEntityList);
		for(int i = 0; i < vlanPortEntityList.size() ; i++){
			int portID = i+1;
			Switch3VlanPortEntity switch3VlanPortEntity = vlanPortModel.getVlanPortEntity(portID);
			if(switch3VlanPortEntity == null){
				continue;
			}
			
			String portName = switch3VlanPortEntity.getPortName();
			
			JLabel portNameLbl = new JLabel();//端口
			portNameLbl.setText(portName);
			
			NumberField vlanFld = new NumberField(4,0,1,4094,true); //默认Vlan
			vlanFld.setColumns(10);
			vlanFld.setHorizontalAlignment(SwingConstants.CENTER);
			JLabel rangeLbl = new JLabel("(1-4094)");

			
			final JComboBox modeComBox = new JComboBox(); //模式
			modeComBox.setPreferredSize(new Dimension(80,modeComBox.getPreferredSize().height));
			for(int j = 0 ; j < MODEVALUE.length ; j++){
				modeComBox.addItem(MODEVALUE[j]);
			}
			
			final JComboBox untagCombox = new JComboBox(); //是否去标签
			untagCombox.setPreferredSize(new Dimension(60,untagCombox.getPreferredSize().height));
			for(int j = 0 ; j < BOOLEANVALUE.length ; j++){
				untagCombox.addItem(BOOLEANVALUE[j]);
			}
			
			final JComboBox allowCombox = new JComboBox();  //是否允许
			allowCombox.setPreferredSize(new Dimension(60,allowCombox.getPreferredSize().height));
			for(int k = 0 ; k < BOOLEANVALUE.length; k++){
				allowCombox.addItem(BOOLEANVALUE[k]);
			}
			
			modeComBox.addPropertyChangeListener(new PropertyChangeListener(){
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					if("Access" .equalsIgnoreCase(modeComBox.getSelectedItem().toString())){
						untagCombox.setEnabled(false);
						allowCombox.setEnabled(false);
					}
					else{
						untagCombox.setEnabled(true);
						allowCombox.setEnabled(true);
					}
				}
			});
			
			JLabel portIDLbl = new JLabel();//此控件保存端口的标识 portID，不在视图上显示
			portIDLbl.setText(portID+"");
			
			middlePnl.add(portNameLbl,new GridBagConstraints(0,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,5,0,0),0,0));
			middlePnl.add(vlanFld,new GridBagConstraints(1,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,35,0,0),0,0));
			middlePnl.add(rangeLbl,new GridBagConstraints(2,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,1,0,10),0,0));
			middlePnl.add(modeComBox,new GridBagConstraints(3,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,25,0,10),0,0));
			middlePnl.add(untagCombox,new GridBagConstraints(4,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,25,0,10),0,0));
			middlePnl.add(allowCombox,new GridBagConstraints(5,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,25,0,10),0,0));
			
			List<JComponent> rowList = new ArrayList<JComponent>();
			rowList.add(0,portNameLbl); //端口
			rowList.add(1,vlanFld); //默认Vlan
			rowList.add(2,rangeLbl);//vlan范围(1-4094)
			rowList.add(3,modeComBox);//模式
			rowList.add(4,untagCombox);//是否去标签
			rowList.add(5,allowCombox);//是否允许
			rowList.add(6,portIDLbl);//隐藏
			
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
	private void setValue(List<Switch3VlanPortEntity> vlanPortEntityList){
		if(vlanPortEntityList == null){
			return;
		}
		for(int row = 0 ; row < vlanPortEntityList.size(); row++){
			int portID = row+1;//端口标识
			Switch3VlanPortEntity vlanPortEntity = vlanPortModel.getVlanPortEntity(portID);
			if(vlanPortEntity == null){
				continue;
			}
			
			String portName = vlanPortEntity.getPortName();//端口名称
			int vlanID = vlanPortEntity.getVlanID();//默认vlan
			int mode = vlanPortEntity.getModel();//模式
			final int isUntag = vlanPortEntity.getDelTag();//是否去标签
			final int isAllow = vlanPortEntity.getAllowTag();//是否允许
			
			List rowList = componentList.get(row);
			final JComboBox modeComBox = (JComboBox)rowList.get(3);
			final JComboBox untagCombox = ((JComboBox)rowList.get(4));
			final JComboBox allowCombox = ((JComboBox)rowList.get(5));
			
			((JLabel)rowList.get(0)).setText(portName); //端口
			((NumberField)rowList.get(1)).setText(vlanID+""); //默认Vlan
			modeComBox.setSelectedItem(convertIntToString(mode));//模式
			untagCombox.setSelectedItem(convertTag(isUntag));//是否去标签
			allowCombox.setSelectedItem(convertTag(isAllow));//是否允许
//			((JLabel)rowList.get(6)).setText(portID + "");//端口标识,隐藏

			modeComBox.addItemListener(new ItemListener(){
				@Override
				public void itemStateChanged(ItemEvent e) {
					if("Access" .equalsIgnoreCase(modeComBox.getSelectedItem().toString())){
						untagCombox.setEnabled(false);
						allowCombox.setEnabled(false);
						untagCombox.setSelectedItem(convertTag(isUntag));
						allowCombox.setSelectedItem(convertTag(isAllow));
					}
					else{
						untagCombox.setEnabled(true);
						allowCombox.setEnabled(true);
					}
				}
			});
		}
	}
	
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="保存VLAN端口配置信息",role=Constants.MANAGERCODE)
	public void save(){
		if (null == switchLayer3){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}

		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
		if (result == 0){
			return;
		}
		
		for(int i = 0; i< componentList.size(); i++){
			NumberField vlanFld = (NumberField)componentList.get(i).get(1);
			if (vlanFld.getText() == null || "".equals(vlanFld.getText().trim())){
				JOptionPane.showMessageDialog(this,"请输入默认VLAN","提示", JOptionPane.NO_OPTION);
				return;
			}
			int vlanID = NumberUtils.toInt(vlanFld.getText());
			if (vlanID < 1 || vlanID > 4094){
				JOptionPane.showMessageDialog(this,"默认VLAN ID错误，范围为：0-4094","提示", JOptionPane.NO_OPTION);
				return;
			}

		}
		
		List<Serializable> vlanPortList = getVlanPortList();
		boolean hasVlan = hasVlanID(vlanPortList);
		if (!hasVlan){
			JOptionPane.showMessageDialog(this,"默认VLAN ID为" + defVlanID + "在VLAN列表中不存在，请重新输入","提示", JOptionPane.NO_OPTION);
			return;
		}
		
		String ipValue = switchLayer3.getIpValue();
		
		showMessageDialog();
		try {
			remoteServer.getAdmService().updateAndSetSwitchVlanPort(ipValue, MessageNoConstants.SWITCH3VLANPORT, vlanPortList, 
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(), Constants.DEV_SWITCHER3,result);
		} catch (JMSException e) {
			messageOfSaveProcessorStrategy.showErrorMessage("保存出现异常");
			LOG.error("VlanPortConfigLayer3View's save() is failure:{}", e);
		}
		if(result == Constants.SYN_SERVER){
			messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		}
		queryData();
		
		//更新vlan列表信息
		this.vlanManageLayer3View.queryVlanData();
	}
	
	private void showMessageDialog(){
		messageOfSaveProcessorStrategy.showInitializeDialog("保存",this);
	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="向上同步VLAN端口配置信息",role=Constants.MANAGERCODE)
	public void upSynchronize(){
	}
	
	private List<Serializable> getVlanPortList(){
		List<Serializable> list = new ArrayList<Serializable>();
		for(int i = 0; i< componentList.size(); i++){
			int portID = i + 1;
			Switch3VlanPortEntity vlanPortEntity = vlanPortModel.getVlanPortEntity(portID);
			
			NumberField vlanFld = (NumberField)componentList.get(i).get(1);
			int vlanID = NumberUtils.toInt(vlanFld.getText());
			
			JComboBox modeCombox = (JComboBox)componentList.get(i).get(3);
			int mode = convertStringToInt(modeCombox.getSelectedItem().toString());
			
			JComboBox untagCombox = (JComboBox)componentList.get(i).get(4);
			int isUntag = convertTag(untagCombox.getSelectedItem().toString());
			
			JComboBox allowCombox = (JComboBox)componentList.get(i).get(5);
			int isAllow = convertTag(allowCombox.getSelectedItem().toString());
			
			vlanPortEntity.setVlanID(vlanID);
			vlanPortEntity.setModel(mode);
			vlanPortEntity.setDelTag(isUntag);
			vlanPortEntity.setAllowTag(isAllow);
			
			list.add(vlanPortEntity);
		}
		
		return list;
	}
	
	private String convertIntToString(int value){
		String str = "";
		switch(value){
			case 1:
				str = "Access";
				break;
			case 2:
				str = "Trunk";
				break;
		}
		
		return str;
	}
	private int convertStringToInt(String str){
		int value = 0;
		if ("access".equalsIgnoreCase(str)){
			value = 1;
		}
		else if ("trunk".equalsIgnoreCase(str)){
			value = 2;
		}
		
		return value;
	}
	
	private String convertTag(int value){
		String str = "";
		switch(value){
			case 0:
				str = "No";
				break;
			case 1:
				str = "Yes";
				break;
		}

		return str;
	}
	private int convertTag(String str){
		int value = 0; 
		if("yes".equalsIgnoreCase(str)){
			value = 1;
		}
		else{
			value = 0;
		}
		return value;
	}
	
	private final PropertyChangeListener switchChangeListener = new  PropertyChangeListener(){
		public void propertyChange(PropertyChangeEvent evt){
			if(evt.getNewValue() instanceof SwitchTopoNodeLevel3){
				queryData();
			}
			else{
				switchLayer3 = null; 
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						middlePnl.removeAll();
						middlePnl.revalidate();
					}
				});
			}
		}
	};
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	public JButton getSaveButton(){
		return this.saveBtn;
	}
	
	/**
	 * 对于接收到的异步消息进行处理
	 */
	public void receive(Object object){
	}
	
	
	/**
	 * 判断端口配置的默认VLAN在vlan列表中是否已经存在，如果存在，可以下发，否则，不能下发
	 * @param vlanPortList
	 * @return
	 */
	private boolean hasVlanID(List<Serializable> vlanPortList){
		boolean bool = false;
		List<Integer> vlanIDList = getVlanIDList();
		if (vlanIDList == null || vlanIDList.size() < 1){
			return bool;
		}
		
		for(int i = 0 ;i < vlanPortList.size(); i++){
			Switch3VlanPortEntity vlanPortEntity = (Switch3VlanPortEntity)vlanPortList.get(i);
			defVlanID = vlanPortEntity.getVlanID();
			for (int vlanID : vlanIDList){
				if (vlanID == defVlanID){
					bool = true;
					break;
				}
				else{
					bool = false;
				}
			}
			
			if (!bool){
				break;
			}
		}
		
		return bool;
	}
	
	/**
	 * 查询所有的vlan
	 * @return
	 */
	private List<Integer> getVlanIDList(){
		List<Integer> vlanIDList = new ArrayList<Integer>();
		
		String where = " where entity.ipValue=?";
		Object[] parms = {switchLayer3.getIpValue()};
		List<Switch3VlanEnity> vlanEnityList = (List<Switch3VlanEnity>)remoteServer
				.getService().findAll(Switch3VlanEnity.class, where, parms);
		if (null == vlanEnityList){
			return vlanIDList;
		}
		
		for(Switch3VlanEnity vlanEntity : vlanEnityList){
			vlanIDList.add(vlanEntity.getVlanID());
		}
		
		return vlanIDList;
	}
	
	public void setVlanManageView(VlanManageLayer3View vlanManageLayer3View){
		this.vlanManageLayer3View = vlanManageLayer3View;
	}
	
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}
	
	
	class VlanPortViewModel implements Serializable{
		List<Switch3VlanPortEntity> vlanPortEntities;
		public VlanPortViewModel(List<Switch3VlanPortEntity> vlanPortEntities){
			this.vlanPortEntities = vlanPortEntities;
		}
		
		public Switch3VlanPortEntity getVlanPortEntity(int portID){
			if (vlanPortEntities == null){
				return null;
			}
			Switch3VlanPortEntity vlanPortEntity = null;
			for(Switch3VlanPortEntity vlanPort : vlanPortEntities){
				if (vlanPort.getPortID() == portID){
					vlanPortEntity = vlanPort;
					break;
				}
			}
			
			return vlanPortEntity;
		}
	}
}
