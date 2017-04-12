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
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;
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
import com.jhw.adm.client.model.switcher.Layer3LLDPPortConfigModel;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.level3.Switch3LLDPPortEntity;
import com.jhw.adm.server.entity.level3.Switch3LLDP_PEntity;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(Layer3LLDPPortConfigurationView.ID)
@Scope(Scopes.DESKTOP)
public class Layer3LLDPPortConfigurationView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "layer3LLDPPortConfigurationView";
	private static final Logger LOG = LoggerFactory.getLogger(Layer3LLDPPortConfigurationView.class);
	private MessageOfSwitchConfigProcessorStrategy messageOfSwitchConfigProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	
	//	1: 仅发送 2: 仅接收 3: 发送和接收(启用) 4: 禁用
	private static final String[] MESSAGE = {"仅发送","仅接收","启用","禁用"};
	
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
	
	@Resource(name=Layer3LLDPPortConfigModel.ID)
	private Layer3LLDPPortConfigModel viewModel;
	
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
		panel.add(new JLabel("LLDP报文"),new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,100,0,10),0,0));
		
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
	private Set<Switch3LLDPPortEntity> portLists = null;
	@SuppressWarnings("unchecked")
	private void queryData(){
		switchLayer3 = (SwitchLayer3) adapterManager.getAdapter(equipmentModel.getLastSelected(), SwitchLayer3.class);
		if(null == switchLayer3){
			return;
		}
		
		String where = " where entity.switchLayer3=?";
		Object[] parms = {switchLayer3};
		List<Switch3LLDP_PEntity> lldpList = (List<Switch3LLDP_PEntity>) remoteServer.getService().findAll(Switch3LLDP_PEntity.class, where, parms);
		if(null == lldpList || lldpList.size() == 0){
			return;
		}
		portLists = lldpList.get(0).getPortEntities();
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
			
			JComboBox message = new JComboBox();//报文
			message.setPreferredSize(new Dimension(80,message.getPreferredSize().height));
			for(int j = 0;j < MESSAGE.length;j++){
				message.addItem(MESSAGE[j]);
				if(2 == j){
					message.setSelectedItem(MESSAGE[j]);
				}
			}
			
			valuePanel.add(portNameLbl,new GridBagConstraints(0,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,15,0,10),0,0));
			valuePanel.add(message,new GridBagConstraints(1,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,50,0,10),0,0));

			List<JComponent> rowList = new ArrayList<JComponent>();
			rowList.add(0,portNameLbl);
			rowList.add(1,message);
			componentList.add(rowList);
		}
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				valuePanel.revalidate();
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private void setValue(Set<Switch3LLDPPortEntity> list){
		viewModel.setPortList(list);
		for(int row = 0 ; row < list.size(); row++){
			int portID = row + 1 ;        //端口索引
			Switch3LLDPPortEntity portEntity = viewModel.getValueAt(portID);
			
			String portName = portEntity.getPortName();
			
			if(portEntity.getlLDPPacket() > 4 || portEntity.getlLDPPacket() < 1){
				LOG.error(String.format("LLDP报文值错误:{%S}" ,portEntity.getlLDPPacket()));
			}
			
			String message = convertIntToString(portEntity.getlLDPPacket());
			List rowList = componentList.get(row);
			((JLabel)rowList.get(0)).setText(portName);
			((JComboBox)rowList.get(1)).setSelectedItem(message);
		}
	}
	
	private String convertIntToString(int type){
		String status = "";
		
		if(1 == type){
			status = MESSAGE[0];
		}else if(2 == type){
			status = MESSAGE[1];
		}else if(3 == type){
			status = MESSAGE[2];
		}else if(4 == type){
			status = MESSAGE[3];
		}
		
		return status;
	}
	
	private int convertStringToInt(String type){
		int status = -1;
		if(MESSAGE[0].equals(type)){
			status = 1;
		}
		if(MESSAGE[1].equals(type)){
			status = 2;
		}
		if(MESSAGE[2].equals(type)){
			status = 3;
		}
		if(MESSAGE[3].equals(type)){
			status = 4;
		}
		return status;
	}
	
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="保存STP端口配置信息配置",role=Constants.MANAGERCODE)
	public void save(){
		if (null == switchLayer3){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}

		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
		if (result == 0){
			return;
		}
		
		List<Serializable> layer3LLDPPortList = getLayer3LLDPPortList();
		
		showMessageDialog();
		String ipValue = switchLayer3.getIpValue();
		if(StringUtils.isBlank(ipValue)){
			messageOfSwitchConfigProcessorStrategy.showErrorMessage("保存出现异常");
			LOG.error("STPPortConfigurationView.Layer3 Switch's IP is null");
			return;
		}
		try {
			remoteServer.getAdmService().updateAndSettingByIp(ipValue, MessageNoConstants.SWITCH3LLDPPORT, layer3LLDPPortList, 
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
	
	private List<Serializable> getLayer3LLDPPortList(){
		List<Serializable> list = new ArrayList<Serializable>();
		for (int i = 0 ; i < portCount; i++){
			int portID = i+1;    //端口
			Switch3LLDPPortEntity portEntity = viewModel.getValueAt(portID);
			if(null == portEntity){
				portEntity = new Switch3LLDPPortEntity();
			}
			
			String portName = portEntity.getPortName();
			int oldMessage = portEntity.getlLDPPacket();
			
			for (int k = 0 ; k < portCount; k++){
				JLabel portNameLbl = (JLabel)componentList.get(k).get(0);
				String name = portNameLbl.getText();
				
				JComboBox receiveMessageComboBox = (JComboBox)componentList.get(k).get(1);
				int receiveMessage = convertStringToInt(receiveMessageComboBox.getSelectedItem().toString());
				
				if (portName.equals(name)){
					if ((oldMessage != receiveMessage)){
						//说明此端口的值有改变
						portEntity.setlLDPPacket(receiveMessage);
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
