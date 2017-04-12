package com.jhw.adm.client.views.switchlayer3;

import static com.jhw.adm.client.core.ActionConstants.SAVE;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.ClientModel;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.model.switcher.Layer3STPPortConfigModel;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.level3.Switch3STPEntity;
import com.jhw.adm.server.entity.level3.Switch3STPPortEntity;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(Layer3STPPortConfigurationView.ID)
@Scope(Scopes.DESKTOP)
public class Layer3STPPortConfigurationView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "layer3STPPortConfigurationView";
	private static final Logger LOG = LoggerFactory.getLogger(Layer3STPPortConfigurationView.class);
	private MessageOfSwitchConfigProcessorStrategy messageOfSwitchConfigProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	
	private static final String[] PROTOCOL_STATUS = {"不使能","使能"};
	private static final String[] PRIORITY = {"0","16","32","48","64","80","96","112"
		,"128","144","160","176","192","208","224","240"};
	private static final String[] PORT_ATTRIBUTE = {"自动检测","强制生效","强制无效"};
	
	private JPanel titlePanel = new JPanel();
	private JPanel valuePanel = new JPanel();
	private final JScrollPane scrollPnl = new JScrollPane();
	private JPanel buttonPanel = new JPanel();
	
	private ButtonFactory buttonFactory;
	private JButton saveBtn = new JButton();
	private JButton closeBtn = new JButton();
	
	private SwitchLayer3 switchLayer3 = null;
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@Resource(name=DesktopAdapterManager.ID)
	private DesktopAdapterManager adapterManager;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=Layer3STPPortConfigModel.ID)
	private Layer3STPPortConfigModel viewModel;
	
	@Resource(name=ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Resource(name=ClientModel.ID)
	private ClientModel clientModel;
	
	@PostConstruct
	protected void initialize(){
		init();
		queryData();
	}
	
	private void init(){
		buttonFactory = actionManager.getButtonFactory(this);
		initializeTitlePanel();
		initializeValuePanel();
		initializeButtonPanel();
		
		this.setLayout(new BorderLayout());
		this.add(titlePanel, BorderLayout.NORTH);
		this.add(scrollPnl, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);
		
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, listener);
	}
	
	private PropertyChangeListener listener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			clearComponent();
			if(evt.getNewValue() instanceof SwitchTopoNodeLevel3){
				queryData();
			}
		}
	};
	
	private void clearComponent(){
		switchLayer3 = null; 
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				valuePanel.removeAll();
				valuePanel.revalidate();
			}
		});
	}
	
	private void initializeTitlePanel(){
		JPanel panel = new JPanel(new GridBagLayout());
		panel.add(new JLabel("端口"),new GridBagConstraints(0,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,50,0,10),0,0));
		panel.add(new JLabel("协议状态"),new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,100,0,10),0,0));
		panel.add(new JLabel("优先级(0-255)"),new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,45,0,10),0,0));
		panel.add(new JLabel("路径成本(0-200000000)"),new GridBagConstraints(3,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,50,0,10),0,0));
		panel.add(new JLabel("边缘端口属性"),new GridBagConstraints(4,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,55,0,10),0,0));
		
		titlePanel.setLayout(new FlowLayout(FlowLayout.LEADING));
		titlePanel.add(panel);
	}
	
	private void initializeValuePanel(){
		valuePanel.setLayout(new GridBagLayout());
		JPanel centerPnl = new JPanel(new FlowLayout(FlowLayout.LEADING));
		centerPnl.add(valuePanel);
		scrollPnl.getViewport().add(centerPnl);
	}
	
	private void initializeButtonPanel(){
		buttonPanel.setLayout(new BorderLayout());
		JPanel newPanel = new JPanel(new GridBagLayout());
		closeBtn = buttonFactory.createCloseButton();
		saveBtn = buttonFactory.createButton(SAVE);
		newPanel.add(saveBtn,new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 0), 0, 0));
		newPanel.add(closeBtn,new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 10, 3), 0, 0));
		this.setCloseButton(closeBtn);
		buttonPanel.add(newPanel,BorderLayout.EAST);
	}
	
	private int portCount = 0;
	private Set<Switch3STPPortEntity> portLists = null;
	@SuppressWarnings("unchecked")
	private void queryData(){
		switchLayer3 = (SwitchLayer3) adapterManager.getAdapter(equipmentModel.getLastSelected(), SwitchLayer3.class);
		if(null == switchLayer3){
			return;
		}
		
		String where = " where entity.switchLayer3=?";
		Object[] parms = {switchLayer3};
		List<Switch3STPEntity> stpEntity = (List<Switch3STPEntity>) remoteServer.getService().findAll(Switch3STPEntity.class, where, parms);
		if(null == stpEntity || stpEntity.size() == 0){
			return;
		}
		portLists = stpEntity.get(0).getPortEntities();
		portCount = portLists.size();
		setCenterLayout();
		if (null == portLists || portLists.size() <1){
			return;
		}
		
		setValue(portLists);
	}
	
	private List<List> componentList = new ArrayList<List>();
	private void setCenterLayout(){
		componentList.clear();
		valuePanel.removeAll();
		
		for(int i = 0 ; i < portCount ; i++){
			JLabel portNameLbl = new JLabel();//端口名称
			
			JComboBox protocolStatus = new JComboBox();//协议状态
			protocolStatus.setPreferredSize(new Dimension(80,protocolStatus.getPreferredSize().height));
			for(int j = 0;j < PROTOCOL_STATUS.length;j++){
				protocolStatus.addItem(PROTOCOL_STATUS[j]);
				if(1 == j){
					protocolStatus.setSelectedItem(PROTOCOL_STATUS[j]);
				}
			}
			JComboBox priority = new JComboBox(); //优先级
			priority.setPreferredSize(new Dimension(80,priority.getPreferredSize().height));
			for(int j = 0 ; j < PRIORITY.length ; j++){
				priority.addItem(PRIORITY[j]);
				if(8 == j){
					priority.setSelectedItem(PRIORITY[j]);
				}
			}
			
			NumberField pathPayFld = new NumberField(10,0,0,2000000000,true);//路径成本
			pathPayFld.setColumns(20);
			pathPayFld.setHorizontalAlignment(SwingConstants.CENTER);
			
			JComboBox portAttribute = new JComboBox(); //边缘端口属性
			portAttribute.setPreferredSize(new Dimension(80,portAttribute.getPreferredSize().height));
			for(int j = 0 ; j < PORT_ATTRIBUTE.length ; j++){
				portAttribute.addItem(PORT_ATTRIBUTE[j]);
				if(2 == j){
					portAttribute.setSelectedItem(PORT_ATTRIBUTE[j]);
				}
			}
			
			valuePanel.add(portNameLbl,new GridBagConstraints(0,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,15,0,10),0,0));
			valuePanel.add(protocolStatus,new GridBagConstraints(1,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,50,0,10),0,0));
			valuePanel.add(priority,new GridBagConstraints(2,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,30,0,10),0,0));
			valuePanel.add(pathPayFld,new GridBagConstraints(3,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,50,0,10),0,0));
			valuePanel.add(portAttribute,new GridBagConstraints(4,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,50,0,10),0,0));

			List<JComponent> rowList = new ArrayList<JComponent>();
			rowList.add(0,portNameLbl);
			rowList.add(1,protocolStatus);
			rowList.add(2,priority);
			rowList.add(3,pathPayFld);
			rowList.add(4,portAttribute);
			componentList.add(rowList);
		}
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				valuePanel.revalidate();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private void setValue(Set<Switch3STPPortEntity> list){
		viewModel.setPortList(list);
		for(int row = 0 ; row < list.size(); row++){
			int portID = row + 1 ;        //端口索引
			Switch3STPPortEntity portEntity = viewModel.getValueAt(portID);
			
			String portName = portEntity.getPortName();
			if(portEntity.getStatus() > 2 || portEntity.getStatus() < 1){
				LOG.error(String.format("STP端口协议状态值错误:{%S}" ,portEntity.getStatus()));
			}
			String protocolStatus = convertIntToString(portEntity.getStatus(),2);
			String priority = String.valueOf(portEntity.getPriorityLevel());
			String pathCost = String.valueOf(portEntity.getPathCost());
			if(portEntity.getEdgePort() > 2 || portEntity.getEdgePort() < 0){
				LOG.error(String.format("STP端口边缘端口属性值错误:{%S}" ,portEntity.getEdgePort()));
			}
			String attribute = convertIntToString(portEntity.getEdgePort(),3);
			
			List rowList = componentList.get(row);
			((JLabel)rowList.get(0)).setText(portName);
			((JComboBox)rowList.get(1)).setSelectedItem(protocolStatus);
			((JComboBox)rowList.get(2)).setSelectedItem(priority);
			((NumberField)rowList.get(3)).setText(pathCost);
			((JComboBox)rowList.get(4)).setSelectedItem(attribute);
		}
	}
	
	private String convertIntToString(int attribute,int paramNumber){
		String portAttribute = "";
		if(2 == paramNumber){
			if(1 == attribute){
				portAttribute = PROTOCOL_STATUS[0];
			}else if(2 == attribute){
				portAttribute = PROTOCOL_STATUS[1];
			}
		}else if(3 == paramNumber){
			if(0 == attribute){
				portAttribute = PORT_ATTRIBUTE[2];
			}else if(1 == attribute){
				portAttribute = PORT_ATTRIBUTE[1];
			}else if(2 == attribute){
				portAttribute = PORT_ATTRIBUTE[0];
			}
		}
		
		return portAttribute;
	}

	private int convertStringToInt(String type,int paramNumber){
		int status = -1;
		if(2 == paramNumber){
			if(PROTOCOL_STATUS[1].equals(type)){
				status = 2;
			}
			if(PROTOCOL_STATUS[0].equals(type)){
				status = 1;
			}
		}else if(3 == paramNumber){
			if(PORT_ATTRIBUTE[0].equals(type)){
				status = 2;
			}
			if(PORT_ATTRIBUTE[1].equals(type)){
				status = 1;
			}
			if(PORT_ATTRIBUTE[2].equals(type)){
				status = 0;
			}
		}
		return status;
	}
	
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="保存STP端口配置信息配置",role=Constants.MANAGERCODE)
	public void save(){
		if (null == switchLayer3){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}

		List<Serializable> layer3STPPortList = getLayer3STPPortList();
		for(int i = 0;i < layer3STPPortList.size(); i++){
			Switch3STPPortEntity portEntity = (Switch3STPPortEntity) layer3STPPortList.get(i);
			if(portEntity.getPathCost() < 0 || portEntity.getPathCost() > 200000000){
				JOptionPane.showMessageDialog(this, "端口" + portEntity.getPortName() + "路径开销错误，范围为：0-200000000", "提示", JOptionPane.NO_OPTION);
				return;
			}
		}

		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
		if (result == 0){
			return;
		}
		
		showMessageDialog();
		String ipValue = switchLayer3.getIpValue();
		if(StringUtils.isBlank(ipValue)){
			messageOfSwitchConfigProcessorStrategy.showErrorMessage("保存出现异常");
			LOG.error("STPPortConfigurationView.Layer3 Switch's IP is null");
			return;
		}
		
		try {
			remoteServer.getAdmService().updateAndSettingByIp(ipValue, MessageNoConstants.SWITCH3STPPORT, layer3STPPortList, 
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(), Constants.DEV_SWITCHER3,result);
		} catch (JMSException e) {
			messageOfSwitchConfigProcessorStrategy.showErrorMessage("保存出现异常");
			LOG.error("STPPortConfigurationView's save() is failure:{}", e);
		}
		if(result == Constants.SYN_SERVER){
			messageOfSwitchConfigProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		}
		queryData();
	}
	
	private void showMessageDialog(){
		messageOfSwitchConfigProcessorStrategy.showInitializeDialog("保存",this);
	}
	
	private List<Serializable> getLayer3STPPortList(){
		List<Serializable> list = new ArrayList<Serializable>();
		for (int i = 0 ; i < portCount; i++){
			int portID = i+1;    //端口
			Switch3STPPortEntity portEntity = viewModel.getValueAt(portID);
			if(null == portEntity){
				portEntity = new Switch3STPPortEntity();
			}
			
			String portName = portEntity.getPortName();
			int oldStatus = portEntity.getStatus();
			int oldPriority = portEntity.getPriorityLevel();
			int oldPathCost = portEntity.getPathCost();
			int oldAttribute = portEntity.getEdgePort();
			
			for (int k = 0 ; k < portCount; k++){
				JLabel portNameLbl = (JLabel)componentList.get(k).get(0);
				String name = portNameLbl.getText();
				
				JComboBox statusComboBox = (JComboBox)componentList.get(k).get(1);
				int status = convertStringToInt(statusComboBox.getSelectedItem().toString(),2);
				
				JComboBox priorityComboBox = (JComboBox)componentList.get(k).get(2);
				int priority = Integer.parseInt(priorityComboBox.getSelectedItem().toString());
				
				NumberField pathPayFld = (NumberField)componentList.get(k).get(3);
				//路径开销为空的话，newPathPay为-1
				int pathCost = -1;
				if(!"".equals(pathPayFld.getText().trim())){
					pathCost = NumberUtils.toInt(pathPayFld.getText());
				}

				JComboBox attributeComboBox = (JComboBox)componentList.get(k).get(4);
				int attribute = convertStringToInt(attributeComboBox.getSelectedItem().toString(),3);
				
				if (portName.equals(name)){
					if ((oldStatus != status)
						|| oldPriority != priority
						|| oldPathCost != pathCost 
						|| oldAttribute != attribute){
						//说明此端口的值有改变
						portEntity.setStatus(status);
						portEntity.setPriorityLevel(priority);
						portEntity.setPathCost(pathCost);
						portEntity.setEdgePort(attribute);

						list.add(portEntity);
						break;
					}
				}
			}
		}
		return list;
	}
	
	public JButton getCloseButton(){
		return this.closeBtn;
	}
	
	@Override
	public void dispose(){
		super.dispose();
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, listener);
	}
}
