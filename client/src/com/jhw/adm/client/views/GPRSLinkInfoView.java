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
import com.jhw.adm.server.entity.tuopos.GPRSTopoNodeEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.util.GPRSEntity;

@Component(GPRSLinkInfoView.ID)
@Scope(Scopes.PROTOTYPE)
public class GPRSLinkInfoView implements NodeLinkInfoViewFactory {

	public static final String ID = "gPRSLinkInfoView";
	
	private JTextField userIdFld;
	private JTextField internateAddressFld;
	private JTextField internatePortFld; 
	
	private JPanel parent = new JPanel(new FlowLayout(FlowLayout.LEADING));

	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@Override
	public void buildPanel(boolean isStart) {
		
		JPanel gprsContainer = new JPanel(new SpringLayout());
		if(isStart){
			gprsContainer.setBorder(BorderFactory.createTitledBorder("起始设备"));
		}else {
			gprsContainer.setBorder(BorderFactory.createTitledBorder("末端设备"));
		}
		parent.add(gprsContainer);
		
		gprsContainer.add(new JLabel("识别码"));
		userIdFld =new JTextField(25);
		userIdFld.setEditable(false);
		gprsContainer.add(userIdFld);
		 
		gprsContainer.add(new JLabel("主机地址"));
		internateAddressFld =new JTextField(25);
		internateAddressFld.setEditable(false);
		gprsContainer.add(internateAddressFld);
		
		gprsContainer.add(new JLabel("主机端口"));
		internatePortFld =new JTextField(25);
		internatePortFld.setEditable(false);
		gprsContainer.add(internatePortFld);
		
		SpringUtilities.makeCompactGrid(gprsContainer, 3, 2, 6, 6, 30, 6);
	}

	public JPanel getNodePanel(){
		return parent;
	}
	
	private NodeEntity nodeEntity; 
	@Override
	public void fillNode(LinkEntity linkNode, boolean isStart) {
		
		if(isStart){
			nodeEntity = linkNode.getNode1();
		}else{
			nodeEntity = linkNode.getNode2();
		}
		
		GPRSTopoNodeEntity gprsTopoNodeEntity = (GPRSTopoNodeEntity)nodeEntity;
		GPRSEntity gprsEntity = remoteServer.getService().getGPRSEntityByUserId(gprsTopoNodeEntity.getUserId());
		
		if(gprsEntity != null){
			userIdFld.setText(gprsEntity.getUserId());
			internateAddressFld.setText(gprsEntity.getInternateAddress());
			internatePortFld.setText("" + gprsEntity.getPort());
		}
	}
}
