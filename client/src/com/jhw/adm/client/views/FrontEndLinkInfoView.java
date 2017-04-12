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

import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.tuopos.FEPTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;

@Component(FrontEndLinkInfoView.ID)
@Scope(Scopes.PROTOTYPE)
public class FrontEndLinkInfoView implements NodeLinkInfoViewFactory {

	public static final String ID = "frontEndLinkInfoView";
	
	private JTextField codeFld;
	private JTextField nameFld;
	private JTextField ipAddressFld;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	private JPanel parent = new JPanel(new FlowLayout(FlowLayout.LEADING));
	
	@Override
	public void buildPanel(boolean isStart) {
		
		JPanel frontEndContainer = new JPanel(new SpringLayout());
		
		if(isStart){
			frontEndContainer.setBorder(BorderFactory.createTitledBorder("起始设备"));
		}else {
			frontEndContainer.setBorder(BorderFactory.createTitledBorder("末端设备"));
		}
		parent.add(frontEndContainer);
		
		frontEndContainer.add(new JLabel("编号"));
		codeFld = new JTextField(25);
		codeFld.setEditable(false);
		frontEndContainer.add(codeFld);
		
		frontEndContainer.add(new JLabel("名称"));
		nameFld = new JTextField(25);
		nameFld.setEditable(false);
		frontEndContainer.add(nameFld);
		
		frontEndContainer.add(new JLabel("设备地址"));
		ipAddressFld = new JTextField(20);
		ipAddressFld.setEditable(false);
		frontEndContainer.add(ipAddressFld);
		
		SpringUtilities.makeCompactGrid(frontEndContainer, 3, 2, 6, 6, 30, 6);
	}
	
	public JPanel getNodePanel(){
		return parent;
	}

	private NodeEntity nodeEntity;
	@Override
	public void fillNode(LinkEntity linkNode,  boolean isStart) {
		if(isStart){
			nodeEntity = linkNode.getNode1();
		}else{
			nodeEntity = linkNode.getNode2();
		}
		
		FEPTopoNodeEntity fepTopoNodeEntity = (FEPTopoNodeEntity) nodeEntity;
		FEPEntity fepEntity = remoteServer.getService().getFEPEntityByIP(fepTopoNodeEntity.getIpValue());
		
		if(fepEntity != null){
			codeFld.setText(fepEntity.getCode());
			nameFld.setText(fepEntity.getFepName());
			ipAddressFld.setText(fepEntity.getIpValue());
		}
	}
}
