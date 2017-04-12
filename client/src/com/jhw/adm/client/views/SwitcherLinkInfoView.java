package com.jhw.adm.client.views;

import java.awt.FlowLayout;
import java.util.Set;

import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.SwitchPortCategory;
import com.jhw.adm.server.entity.ports.SwitchPortEntity;
import com.jhw.adm.server.entity.switchs.SwitchNodeEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;

@Component(SwitcherLinkInfoView.ID)
@Scope(Scopes.PROTOTYPE)
public class SwitcherLinkInfoView implements NodeLinkInfoViewFactory {

	public static final String ID = "switcherLinkInfoView";
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	@Resource(name=SwitchPortCategory.ID)
	private SwitchPortCategory switchPortCategory;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	private JTextField startNameField;
	private JTextField startCategoryFld;
	private JTextField startPortFld;
	private JTextField startPortCategoryFld;
	
	private JTextField endNameField;
	private JTextField endCategoryFld;
	private JTextField endPortFld;
	private JTextField endPortCategoryFld;
	
	private JPanel startParent = new JPanel(new FlowLayout(FlowLayout.LEADING));
	private JPanel endParent = new JPanel(new FlowLayout(FlowLayout.LEADING));
	
	@Override
	public void buildPanel(boolean isStart) {
		
		JPanel container = new JPanel(new SpringLayout());
		if(isStart){
			container.setBorder(BorderFactory.createTitledBorder("起始设备"));
			
			startParent.add(container);
			
			container.add(new JLabel("名称"));
			startNameField = new JTextField(25);
			startNameField.setEditable(false);
			container.add(startNameField);
			
			container.add(new JLabel("设备类型"));
			startCategoryFld = new JTextField(25);
			startCategoryFld.setEditable(false);
			container.add(startCategoryFld);
					
			container.add(new JLabel("端口"));
			startPortFld = new JTextField(25);
			startPortFld.setEditable(false);
			container.add(startPortFld);
			
			container.add(new JLabel("端口类型"));
			startPortCategoryFld = new JTextField(25);
			startPortCategoryFld.setEditable(false);
			container.add(startPortCategoryFld);
		}else{
			container.setBorder(BorderFactory.createTitledBorder("末端设备"));
			
			endParent.add(container);
			
			container.add(new JLabel("名称"));
			endNameField = new JTextField(25);
			endNameField.setEditable(false);
			container.add(endNameField);
			
			container.add(new JLabel("设备类型"));
			endCategoryFld = new JTextField(25);
			endCategoryFld.setEditable(false);
			container.add(endCategoryFld);
					
			container.add(new JLabel("端口"));
			endPortFld = new JTextField(20);
			endPortFld.setEditable(false);
			container.add(endPortFld);
			
			container.add(new JLabel("端口类型"));
			endPortCategoryFld = new JTextField(20);
			endPortCategoryFld.setEditable(false);
			container.add(endPortCategoryFld);
		}

		SpringUtilities.makeCompactGrid(container, 4, 2, 6, 6, 30, 6);
	}

	public JPanel getStartPanel(){
		return startParent;
	}
	
	public JPanel getEndPanel(){
		return endParent;
	}
	
	public void setEditable(){
		startPortFld.setEditable(true);
		endPortFld.setEditable(true);
	}
	
	public String getNewStartPort(){
		return startPortFld.getText();
	}
	public String getNewEndPort(){
		return endPortFld.getText();
	}
	
	private NodeEntity nodeEntity;
	@Override
	public void fillNode(LinkEntity linkNode, boolean isStart) {

		if (isStart) {
			nodeEntity = linkNode.getNode1();
		} else {
			nodeEntity = linkNode.getNode2();
		}

		SwitchNodeEntity switchNodeEntity = (SwitchNodeEntity) adapterManager
				.getAdapter(nodeEntity, SwitchNodeEntity.class);

		if (switchNodeEntity != null) {
			if (isStart) {
				if (switchNodeEntity.getBaseConfig() != null
						&& switchNodeEntity.getBaseInfo() != null) {
					
					startNameField.setText(switchNodeEntity.getBaseConfig().getIpValue());
					startCategoryFld.setText(switchNodeEntity.getType());

					if (linkNode.getLldp() != null) {
						Set<SwitchPortEntity> listOfPort = switchNodeEntity.getPorts();
						int localPort = linkNode.getLldp().getLocalPortNo();
						SwitchPortEntity startPort = null;
						for (SwitchPortEntity port : listOfPort) {
							if (localPort == port.getPortNO()) {
								startPort = port;
								break;
							}
						}
						startPortFld.setText(Integer.toString(localPort));
						if (startPort != null) {
							startPortCategoryFld.setText(switchPortCategory
									.get(startPort.getType()).getKey());
						}
					}
				}
			} else {
				if (switchNodeEntity.getBaseConfig() != null
						&& switchNodeEntity.getBaseInfo() != null) {
					
					endNameField.setText(switchNodeEntity.getBaseConfig().getIpValue());
					endCategoryFld.setText(switchNodeEntity.getType());
					
					if (linkNode.getLldp() != null) {
						Set<SwitchPortEntity> listOfPort = switchNodeEntity.getPorts();
						int remotePort = linkNode.getLldp().getRemotePortNo();
						SwitchPortEntity endPort = null;
						for (SwitchPortEntity port : listOfPort) {
							if (remotePort == port.getPortNO()) {
								endPort = port;
								break;
							}
						}
						endPortFld.setText(Integer.toString(remotePort));
						if (endPort != null) {
							endPortCategoryFld.setText(switchPortCategory
									.get(endPort.getType()).getKey());
						}
					}
				}
			}
		}
	}
}
