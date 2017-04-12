package com.jhw.adm.client.views;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.SubNetTopoNodeEntity;

@Component(SubnetLinkInfoView.ID)
@Scope(Scopes.PROTOTYPE)
public class SubnetLinkInfoView implements NodeLinkInfoViewFactory {

	public static final String ID = "subnetLinkInfoView";
	
	private JTextField subnetNameFld;
	private JPanel parent = new JPanel(new FlowLayout(FlowLayout.LEADING));
	
	@Override
	public void buildPanel(boolean isStart) {

		JPanel subnetContainer = new JPanel(new SpringLayout());
		if(isStart){
			subnetContainer.setBorder(BorderFactory.createTitledBorder("起始设备"));
		}else {
			subnetContainer.setBorder(BorderFactory.createTitledBorder("末端设备"));
		}
		parent.add(subnetContainer);
		
		subnetContainer.add(new JLabel("子网名称"));
		subnetNameFld =new JTextField(25);
		subnetNameFld.setEditable(false);
		subnetContainer.add(subnetNameFld);

		SpringUtilities.makeCompactGrid(subnetContainer, 1, 2, 6, 6, 30, 6);
	}

	public JPanel getNodePanel(){
		return parent;
	}	

	private NodeEntity nodeEntity;
	@Override
	public void fillNode(LinkEntity linkNode, boolean isStart) {

		if (isStart) {
			nodeEntity = linkNode.getNode1();
		} else {
			nodeEntity = linkNode.getNode2();
		}
		
		String subnetName = ((SubNetTopoNodeEntity)nodeEntity).getName();
		subnetNameFld.setText(subnetName);
	}
}
