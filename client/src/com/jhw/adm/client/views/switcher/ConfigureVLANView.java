package com.jhw.adm.client.views.switcher;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.swing.JTabbedPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.EquipmentModel;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.nets.VlanConfig;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;

@Component(ConfigureVLANView.ID)
@Scope(Scopes.DESKTOP)
public class ConfigureVLANView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "configureVLANView";
	
	private JTabbedPane tabPnl = new JTabbedPane();
	
	private SwitchNodeEntity switchNodeEntity = null;
	
	private VlanConfig vlanConfig = null;
		
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	
	@Autowired
	@Qualifier(ConfigureVLANMemberView.ID)
	private ConfigureVLANMemberView configureVLANMemberView;
	
	@Autowired
	@Qualifier(ConfigureVLANPortView.ID)
	private ConfigureVLANPortView configureVLANPortView;
	
	@Autowired
	@Qualifier(EquipmentModel.ID)
	private EquipmentModel equipmentModel;
	
	@PostConstruct
	protected void initialize(){
		this.setTitle("Vlan配置");
		
		setCloseButton(configureVLANMemberView.getCloseButton());
		setCloseButton(configureVLANPortView.getCloseButton());
		
		tabPnl.addTab("Vlan成员设置", configureVLANMemberView);
		tabPnl.addTab("Vlan端口配置", configureVLANPortView);
		
		this.setLayout(new BorderLayout());
		this.add(tabPnl,BorderLayout.CENTER);
		
		//增加对设备树选择不同的节点时的监听器
//		equipmentModel.addPropertyChangeListener(equipmentModel.PROP_LAST_SELECTED, new SwitchChangeListener());
		
		setViewSize(640, 550);	
	}
	
	@Override
	public void dispose() {
		super.dispose();
		configureVLANMemberView.close();
		configureVLANPortView.close();
	}
	
	public ImageRegistry getImageRegistry(){
		return imageRegistry;
	}
	
	public VlanConfig getVlanConfig(){
		return vlanConfig;
	}
	
	public SwitchNodeEntity getSwitchNodeEntity(){
		return switchNodeEntity;
	}
	
//	class SwitchChangeListener implements PropertyChangeListener{
//		public void propertyChange(PropertyChangeEvent evt){
//			if(evt.getNewValue() instanceof SwitchTopoNodeEntity){
//				SwitchTopoNodeEntity switchTopoNodeEntity = (SwitchTopoNodeEntity)evt.getNewValue();
//				switchNodeEntity = switchTopoNodeEntity.getNodeEntity();
//				queryData();
//				
////				configureVlanModel.fireTableDataChanged();
//			}
//		}
//		
//	}
}


