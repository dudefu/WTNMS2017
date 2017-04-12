package com.jhw.adm.client.views;

import java.awt.FlowLayout;

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
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.server.entity.level3.SwitchLayer3;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;

@Component(SwitcherThreeLinkInfoView.ID)
@Scope(Scopes.PROTOTYPE)
public class SwitcherThreeLinkInfoView implements NodeLinkInfoViewFactory {

	public static final String ID = "switcherThreeLinkInfoView";
	private static final String EQUIPMENTNAME = "三层交换机";
	
	@Resource(name=DesktopAdapterManager.ID)
	private AdapterManager adapterManager;
	
	private JTextField startCategoryFld;
	private JTextField startPortFld;
	private JTextField startIPFld;
	
	private JTextField endCategoryFld;
	private JTextField endPortFld;
	private JTextField endIPFld;
	
	private final JPanel startParent = new JPanel(new FlowLayout(FlowLayout.LEADING));
	private final JPanel endParent = new JPanel(new FlowLayout(FlowLayout.LEADING));
	
	@Override
	public void buildPanel(boolean isStart) {
		
		JPanel container = new JPanel(new SpringLayout());
		if(isStart){
			container.setBorder(BorderFactory.createTitledBorder("起始设备"));
			
			startParent.add(container);
			
			container.add(new JLabel("设备类型"));
			startCategoryFld = new JTextField(25);
			startCategoryFld.setEditable(false);
			container.add(startCategoryFld);
			
			container.add(new JLabel("设备"));
			startIPFld = new JTextField(25);
			startIPFld.setEditable(false);
			container.add(startIPFld);
					
			container.add(new JLabel("端口"));
			startPortFld = new JTextField(20);
			startPortFld.setEditable(false);
			container.add(startPortFld);
		}else{
			container.setBorder(BorderFactory.createTitledBorder("末端设备"));
			
			endParent.add(container);
			
			container.add(new JLabel("设备类型"));
			endCategoryFld = new JTextField(25);
			endCategoryFld.setEditable(false);
			container.add(endCategoryFld);
			
			container.add(new JLabel("设备"));
			endIPFld = new JTextField(25);
			endIPFld.setEditable(false);
			container.add(endIPFld);
					
			container.add(new JLabel("端口"));
			endPortFld = new JTextField(20);
			endPortFld.setEditable(false);
			container.add(endPortFld);
		}

		SpringUtilities.makeCompactGrid(container, 3, 2, 6, 6, 30, 6);
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
	private NodeEntity nodeEntity;
	@Override
	public void fillNode(LinkEntity linkNode, boolean isStart) {

		if (isStart) {
			nodeEntity = linkNode.getNode1();
		} else {
			nodeEntity = linkNode.getNode2();
		}

		SwitchLayer3 switchLayer3 = (SwitchLayer3) adapterManager.getAdapter(
				nodeEntity, SwitchLayer3.class);

		if (switchLayer3 != null) {
			if (isStart) {
				startCategoryFld.setText(EQUIPMENTNAME);
				startIPFld.setText(switchLayer3.getIpValue());

				if (linkNode.getLldp() != null) {
					int localPort = linkNode.getLldp().getLocalPortNo();
					startPortFld.setText(Integer.toString(localPort));
				}
			} else {
				endCategoryFld.setText(EQUIPMENTNAME);
				endIPFld.setText(switchLayer3.getIpValue());
				
				if (linkNode.getLldp() != null) {
					int remotePort = linkNode.getLldp().getRemotePortNo();
					endPortFld.setText(Integer.toString(remotePort));
				}
			}
		}
	}
}
