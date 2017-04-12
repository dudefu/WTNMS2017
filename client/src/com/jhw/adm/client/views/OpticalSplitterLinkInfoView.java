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

@Component(OpticalSplitterLinkInfoView.ID)
@Scope(Scopes.PROTOTYPE)
public class OpticalSplitterLinkInfoView implements NodeLinkInfoViewFactory {

	public static final String ID = "opticalSplitterLinkInfoView";
	
	private JTextField systemNameFld;
	private JPanel parent = new JPanel(new FlowLayout(FlowLayout.LEADING));
	
	@Override
	public void buildPanel(boolean isStart) {

		JPanel osContainer = new JPanel(new SpringLayout());
		if(isStart){
			osContainer.setBorder(BorderFactory.createTitledBorder("起始设备"));
		}else {
			osContainer.setBorder(BorderFactory.createTitledBorder("末端设备"));
		}
		parent.add(osContainer);
		
		osContainer.add(new JLabel("设备名称"));
		systemNameFld =new JTextField(25);
		systemNameFld.setEditable(false);
		osContainer.add(systemNameFld);
		
		SpringUtilities.makeCompactGrid(osContainer, 1, 2, 6, 6, 30, 6);
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
		if(null != nodeEntity){
			systemNameFld.setText(nodeEntity.getName());
		}
	}

}
