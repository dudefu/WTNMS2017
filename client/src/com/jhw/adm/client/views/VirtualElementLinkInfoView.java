package com.jhw.adm.client.views;

import java.awt.FlowLayout;
import java.util.List;

import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.VirtualNodeEntity;
import com.jhw.adm.server.entity.util.VirtualType;

@Component(VirtualElementLinkInfoView.ID)
@Scope(Scopes.PROTOTYPE)
public class VirtualElementLinkInfoView implements NodeLinkInfoViewFactory {

	public static final String ID = "virtualElementLinkInfoView";
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	private JTextField startIpAddressField;
	private JTextField startNameField;
	private JTextField startTypeField;
	
	private JTextField endIpAddressField;
	private JTextField endNameField;
	private JTextField endTypeField;
	
	private JPanel startParent = new JPanel(new FlowLayout(FlowLayout.LEADING));
	private JPanel endParent = new JPanel(new FlowLayout(FlowLayout.LEADING));
	
	@Override
	public void buildPanel(boolean isStart) {
		
		JPanel virtualContainer = new JPanel(new SpringLayout());
		if(isStart) {
			virtualContainer.setBorder(BorderFactory.createTitledBorder("起始设备"));
			
			startParent.add(virtualContainer);
			
			virtualContainer.add(new JLabel("IP地址"));
			startIpAddressField = new JTextField(25);
			startIpAddressField.setEditable(false);
			virtualContainer.add(startIpAddressField);
			
			virtualContainer.add(new JLabel("网元名称"));
			startNameField = new JTextField(25);
			startNameField.setEditable(false);
			virtualContainer.add(startNameField);
			
			virtualContainer.add(new JLabel("网元类型"));
			startTypeField = new JTextField(25);
			startTypeField.setEditable(false);
			virtualContainer.add(startTypeField);
		}else {
			virtualContainer.setBorder(BorderFactory.createTitledBorder("末端设备"));
			
			endParent.add(virtualContainer);
			
			virtualContainer.add(new JLabel("IP地址"));
			endIpAddressField = new JTextField(25);
			endIpAddressField.setEditable(false);
			virtualContainer.add(endIpAddressField);
			
			virtualContainer.add(new JLabel("网元名称"));
			endNameField = new JTextField(25);
			endNameField.setEditable(false);
			virtualContainer.add(endNameField);
			
			virtualContainer.add(new JLabel("网元类型"));
			endTypeField = new JTextField(25);
			endTypeField.setEditable(false);
			virtualContainer.add(endTypeField);
		}
		
		SpringUtilities.makeCompactGrid(virtualContainer, 3, 2, 6, 6, 30, 6);
		
	}
	
	public JPanel getStartParent() {
		return startParent;
	}
	public JPanel getEndParent() {
		return endParent;
	}

	private NodeEntity nodeEntity;
	@SuppressWarnings("unchecked")
	@Override
	public void fillNode(LinkEntity linkNode, boolean isStart) {

		if(isStart) {
			nodeEntity = linkNode.getNode1();
		}else {
			nodeEntity = linkNode.getNode2();
		}
		
		String ipValue = ((VirtualNodeEntity)nodeEntity).getIpValue();
		String name = ((VirtualNodeEntity)nodeEntity).getName();
		String where = " where id = " + ((VirtualNodeEntity)nodeEntity).getType();
		List<VirtualType> virtualTypes = (List<VirtualType>)remoteServer.getService().findAll(VirtualType.class, where);
		String type = virtualTypes.get(0).getType();
		
		if(isStart) {
			startIpAddressField.setText(ipValue);
			startNameField.setText(name);
			startTypeField.setText(type);
		}else {
			endIpAddressField.setText(ipValue);
			endNameField.setText(name);
			endTypeField.setText(type);
		}
	}

}
