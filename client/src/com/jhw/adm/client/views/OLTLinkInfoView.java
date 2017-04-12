package com.jhw.adm.client.views;

import java.awt.FlowLayout;

import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.AdapterManager;
import com.jhw.adm.client.core.DesktopAdapterManager;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.SwitchPortCategory;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;

@Component(OLTLinkInfoView.ID)
@Scope(Scopes.PROTOTYPE)
public class OLTLinkInfoView implements NodeLinkInfoViewFactory {

	public static final String ID = "oLTLinkInfoView";
	private static final Logger LOG = LoggerFactory.getLogger(OLTLinkInfoView.class);
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Resource(name=SwitchPortCategory.ID)
	private SwitchPortCategory switchPortCategory;
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	private JTextField systemNameFld;
	private JTextField systemTypeFld;
	private JTextField slotNumberFld;
	private JTextField portNumberFld;;
	private JTextField portTypeFld;	
	
	private JTextField endSystemNameFld;
	private JTextField endSystemTypeFld;
	private JTextField endSlotNumberFld;
	private JTextField endPortNumberFld;;
	private JTextField endPortTypeFld;
	
	private final JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
	private final JPanel endPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
	
	@Override
	public void buildPanel(boolean isStart) {
		LOG.debug("构建OLT连线信息面板");

		JPanel oltContainer = new JPanel(new SpringLayout());
		if(isStart){
			oltContainer.setBorder(BorderFactory.createTitledBorder("起始设备"));

			startPanel.add(oltContainer);
			
			oltContainer.add(new JLabel("设备名称"));
			systemNameFld =new JTextField(25);
			systemNameFld.setEditable(false);
			oltContainer.add(systemNameFld);

			oltContainer.add(new JLabel("设备类型"));
			systemTypeFld =new JTextField(25);
			systemTypeFld.setEditable(false);
			oltContainer.add(systemTypeFld);
			
			oltContainer.add(new JLabel("插槽号"));
			slotNumberFld = new JTextField(25);
			slotNumberFld.setEditable(false);
			oltContainer.add(slotNumberFld);
			
			oltContainer.add(new JLabel("端口号"));
			portNumberFld = new JTextField(25);
			portNumberFld.setEditable(false);
			oltContainer.add(portNumberFld);
			
			oltContainer.add(new JLabel("端口类型"));
			portTypeFld = new JTextField(25);
			portTypeFld.setEditable(false);
			oltContainer.add(portTypeFld);
		} else {
			oltContainer.setBorder(BorderFactory.createTitledBorder("末端设备"));


			endPanel.add(oltContainer);
			
			oltContainer.add(new JLabel("设备名称"));
			endSystemNameFld =new JTextField(25);
			endSystemNameFld.setEditable(false);
			oltContainer.add(endSystemNameFld);

			oltContainer.add(new JLabel("设备类型"));
			endSystemTypeFld =new JTextField(25);
			endSystemTypeFld.setEditable(false);
			oltContainer.add(endSystemTypeFld);
			
			oltContainer.add(new JLabel("插槽号"));
			endSlotNumberFld = new JTextField(25);
			endSlotNumberFld.setEditable(false);
			oltContainer.add(endSlotNumberFld);
			
			oltContainer.add(new JLabel("端口号"));
			endPortNumberFld = new JTextField(25);
			endPortNumberFld.setEditable(false);
			oltContainer.add(endPortNumberFld);
			
			oltContainer.add(new JLabel("端口类型"));
			endPortTypeFld = new JTextField(25);
			endPortTypeFld.setEditable(false);
			oltContainer.add(endPortTypeFld);
		}
		
		SpringUtilities.makeCompactGrid(oltContainer, 5, 2, 6, 6, 30, 6);
	}

	public JPanel getStartPanel(){
		return startPanel;
	}	

	public JPanel getEndPanel(){
		return endPanel;
	}	

	private NodeEntity nodeEntity;
	@Override
	public void fillNode(LinkEntity linkNode, boolean isStart) {

		if (isStart) {
			nodeEntity = linkNode.getNode1();
		} else {
			nodeEntity = linkNode.getNode2();
		}
		
		OLTEntity oltNodeEntity = (OLTEntity) adapterManager.getAdapter(nodeEntity, OLTEntity.class);
		
		if (null != oltNodeEntity) {
			if (isStart) {
				if (oltNodeEntity.getOltBaseInfo() != null) {
					systemNameFld.setText(oltNodeEntity.getIpValue());
					systemTypeFld.setText(oltNodeEntity.getOltBaseInfo().getDeviceName());
					
					if (null != linkNode.getLldp()) {						
						int localSlotNumber = linkNode.getLldp().getLocalSlot();
						slotNumberFld.setText(Integer.toString(localSlotNumber));					
						portNumberFld.setText(Integer.toString(linkNode.getLldp().getLocalPortNo()));
						portTypeFld.setText(switchPortCategory.get(linkNode.getLldp().getLocalPortType()).getKey());
					}
				}
			} else {
				if (oltNodeEntity.getOltBaseInfo() != null) {
					endSystemNameFld.setText(oltNodeEntity.getIpValue());
					endSystemTypeFld.setText(oltNodeEntity.getOltBaseInfo().getDeviceName());
					
					if (null != linkNode.getLldp()) {						
						int remoteSlotNumber = linkNode.getLldp().getRemoteSlot();
						endSlotNumberFld.setText(Integer.toString(remoteSlotNumber));						
						endPortNumberFld.setText(Integer.toString(linkNode.getLldp().getRemotePortNo()));
						endPortTypeFld.setText(switchPortCategory.get(linkNode.getLldp().getRemotePortType()).getKey());
					}
				}
			}
		} else {
			LOG.debug("无法找到OLT实体信息");
		}

	}

}
