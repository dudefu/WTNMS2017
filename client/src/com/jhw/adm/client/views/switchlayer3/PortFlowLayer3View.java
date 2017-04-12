package com.jhw.adm.client.views.switchlayer3;

import static com.jhw.adm.client.core.ActionConstants.SAVE;
import static com.jhw.adm.client.core.ActionConstants.UPLOAD;

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
import com.jhw.adm.client.model.ListComboBoxModel;
import com.jhw.adm.client.model.PortRateCategory;
import com.jhw.adm.client.model.StringInteger;
import com.jhw.adm.client.swing.MessageOfSwitchConfigProcessorStrategy;
import com.jhw.adm.client.swing.PromptDialog;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.level3.Switch3PortFlowEntity;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.tuopos.SwitchTopoNodeLevel3;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.MessageNoConstants;

@Component(PortFlowLayer3View.ID)
@Scope(Scopes.DESKTOP)
public class PortFlowLayer3View extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "portFlowLayer3View";

	//上端的面板
	private final JPanel topPnl = new JPanel();
	private final JLabel portLbl = new JLabel();
	private final JLabel revRateLbl = new JLabel();
	private final JLabel sendRateLbl = new JLabel();
	
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
	
	private PortFlowModel portFlowModel;
	
	private SwitchLayer3 switchLayer3 = null;
	
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
	
	@Resource(name = PortRateCategory.ID)
	private PortRateCategory portRateCategory;
	
	private final MessageOfSwitchConfigProcessorStrategy messageOfSaveProcessorStrategy = new MessageOfSwitchConfigProcessorStrategy();
	
	private static final Logger LOG = LoggerFactory.getLogger(PortFlowLayer3View.class);
	
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
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,15,0,0),0,0));
		
		panel.add(revRateLbl,new GridBagConstraints(1,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,45,0,0),0,0));
		panel.add(sendRateLbl,new GridBagConstraints(2,0,1,1,0.0,0.0,
				GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,30,0,10),0,0));
		
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
		revRateLbl.setText("接收速率");
		sendRateLbl.setText("发送速率");
		
		setViewSize(480, 420);
		
		//增加对设备树选择不同的节点时的监听器
		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}
	
	private void queryData(){
		switchLayer3 = (SwitchLayer3) adapterManager
				.getAdapter(equipmentModel.getLastSelected(), SwitchLayer3.class);
		if (null == switchLayer3){
			return ;
		}
		
		String where = " where entity.switchLayer3=?";
		Object[] parms = {switchLayer3};
		List<Switch3PortFlowEntity> portFlowEntityList = (List<Switch3PortFlowEntity>)remoteServer
					.getService().findAll(Switch3PortFlowEntity.class, where, parms);
		
		//初始化中心面板
		setCenterLayout(portFlowEntityList);
		
		//设置控件的值
		setValue(portFlowEntityList);
	}
	
	/**
	 * 通过查询出的端口列表布局panel的所有控件
	 */
	private void setCenterLayout(List<Switch3PortFlowEntity> portFlowEntityList){
		componentList.clear();
		middlePnl.removeAll();
		
		if (portFlowEntityList == null){
			return;
		}
		
		portFlowModel = new PortFlowModel(portFlowEntityList);
		for(int i = 0; i < portFlowEntityList.size() ; i++){
			int portIndex = i+1;
			Switch3PortFlowEntity switch3PortFlowEntity = portFlowModel.getPortFlowEntity(portIndex);
			String portName = switch3PortFlowEntity.getPortName();
			
			JLabel portNameLbl = new JLabel();//端口
			portNameLbl.setText(portName);

			JComboBox revRateComBox = new JComboBox(new ListComboBoxModel(
					portRateCategory.toList())); //接收速率
			revRateComBox.setPreferredSize(new Dimension(80,revRateComBox.getPreferredSize().height));

			JComboBox sendRateCombox = new JComboBox(new ListComboBoxModel(
					portRateCategory.toList())); //发送速率
			sendRateCombox.setPreferredSize(new Dimension(80,sendRateCombox.getPreferredSize().height));

			JLabel portIDLbl = new JLabel();//此控件保存端口的标识 portID，不在视图上显示
			portIDLbl.setText(portIndex+"");
			
			middlePnl.add(portNameLbl,new GridBagConstraints(0,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,15,0,0),0,0));
			middlePnl.add(revRateComBox,new GridBagConstraints(1,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,65,0,0),0,0));
			middlePnl.add(sendRateCombox,new GridBagConstraints(2,i,1,1,0.0,0.0,
					GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,new Insets(0,2,0,10),0,0));
			
			List<JComponent> rowList = new ArrayList<JComponent>();
			rowList.add(0,portNameLbl); //端口
			rowList.add(1,revRateComBox); //接收速率
			rowList.add(2,sendRateCombox);//发送速率
			rowList.add(3,portIDLbl);//隐藏
			
			componentList.add(i,rowList);
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
	private void setValue(List<Switch3PortFlowEntity> portFlowEntityList){
		if(portFlowEntityList == null){
			return;
		}
		for(int row = 0 ; row < portFlowEntityList.size(); row++){
			int portIndex = row+1;//端口标识
			Switch3PortFlowEntity portFlowEntity = portFlowModel.getPortFlowEntity(portIndex);
			
			String portName = portFlowEntity.getPortName();//端口名称
			int revRate = portFlowEntity.getReceiveSpeed();//接收速率
			int sendRate = portFlowEntity.getSendSpeed();//发送速率
			
			List rowList = componentList.get(row);
			((JLabel)rowList.get(0)).setText(portName); //端口
			((JComboBox)rowList.get(1)).setSelectedItem(portRateCategory.get(revRate));//接收速率
			((JComboBox)rowList.get(2)).setSelectedItem(portRateCategory.get(sendRate));//发送速率
//			((JLabel)rowList.get(3)).setText(portIndex + "");//端口标识,隐藏
		}
	}
	
	@ViewAction(name=SAVE, icon=ButtonConstants.SAVE, desc="保存端口流量配置信息",role=Constants.MANAGERCODE)
	public void save(){
		if (null == switchLayer3){
			JOptionPane.showMessageDialog(this, "请选择交换机","提示",JOptionPane.NO_OPTION);
			return;
		}

		int result = PromptDialog.showPromptDialog(this, "请选择保存的方式",imageRegistry);
		if (result == 0){
			return;
		}
		
		List<Serializable> portRateList = getPortFlowList();
		String ipValue = switchLayer3.getIpValue();
		
		showMessageDialog();
		try {
			remoteServer.getAdmService().updateAndSetSwitchVlanPort(ipValue, MessageNoConstants.SWITCH3PORTFLOW, portRateList, 
					clientModel.getCurrentUser().getUserName(),clientModel.getLocalAddress(), Constants.DEV_SWITCHER3,result);
		} catch (JMSException e) {
			messageOfSaveProcessorStrategy.showErrorMessage("保存出现异常");
			LOG.error("PortFlowLayer3View's save() is failure:{}", e);
		}
		if(result == Constants.SYN_SERVER){
			messageOfSaveProcessorStrategy.showNormalMessage(true,Constants.SYN_SERVER);
		}
		queryData();
	}
	
	private void showMessageDialog(){
		messageOfSaveProcessorStrategy.showInitializeDialog("保存",this);
	}
	
	@ViewAction(name=UPLOAD, icon=ButtonConstants.SYNCHRONIZE, desc="向上同步端口流量信息",role=Constants.MANAGERCODE)
	public void upSynchronize(){
	}
	
	private List<Serializable> getPortFlowList(){
		List<Serializable> list = new ArrayList<Serializable>();
		for(int i = 0; i< componentList.size(); i++){
			int portIndex = i + 1;
			Switch3PortFlowEntity portFlowEntity = portFlowModel.getPortFlowEntity(portIndex);
			
			JComboBox revRateCombox = (JComboBox)componentList.get(i).get(1);
			int revRate = ((StringInteger)revRateCombox.getSelectedItem()).getValue();
			
			JComboBox sendRateCombox = (JComboBox)componentList.get(i).get(2);
			int sendRate = ((StringInteger)sendRateCombox.getSelectedItem()).getValue();

			portFlowEntity.setReceiveSpeed(revRate);
			portFlowEntity.setSendSpeed(sendRate);
			
			list.add(portFlowEntity);
		}
		
		return list;
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
	
	@Override
	public void dispose() {
		equipmentModel.removePropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, switchChangeListener);
	}
	
	
	class PortFlowModel implements Serializable{
		List<Switch3PortFlowEntity> portFlowEntities;
		public PortFlowModel(List<Switch3PortFlowEntity> portFlowEntities){
			this.portFlowEntities = portFlowEntities;
		}
		
		public Switch3PortFlowEntity getPortFlowEntity(int portIndex){
			if (portFlowEntities == null){
				return null;
			}
			Switch3PortFlowEntity portFlowEntity = null;
			for(Switch3PortFlowEntity portFlow : portFlowEntities){
				if (portFlow.getPortIndex() == portIndex){
					portFlowEntity = portFlow;
					break;
				}
			}
			
			return portFlowEntity;
		}
	}

}
