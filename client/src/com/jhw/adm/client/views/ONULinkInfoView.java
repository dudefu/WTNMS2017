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
import com.jhw.adm.server.entity.epon.ONUEntity;
import com.jhw.adm.server.entity.tuopos.LinkEntity;
import com.jhw.adm.server.entity.tuopos.NodeEntity;
import com.jhw.adm.server.entity.tuopos.ONUTopoNodeEntity;

@Component(ONULinkInfoView.ID)
@Scope(Scopes.PROTOTYPE)
public class ONULinkInfoView implements NodeLinkInfoViewFactory {

	public static final String ID = "oNULinkInfoView";
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	private JTextField macValueFld;//MAC value
	private JTextField sequenceNoFld;
	private JTextField portNoFld;
	private JTextField portTypeFld;
	private JPanel parent = new JPanel(new FlowLayout(FlowLayout.LEADING));
	
	@Override
	public void buildPanel(boolean isStart) {

		JPanel gprsContainer = new JPanel(new SpringLayout());
		if(isStart){
			gprsContainer.setBorder(BorderFactory.createTitledBorder("起始设备"));
		}else {
			gprsContainer.setBorder(BorderFactory.createTitledBorder("末端设备"));
		}
		parent.add(gprsContainer);
		
		gprsContainer.add(new JLabel("编号"));
		sequenceNoFld =new JTextField(25);
		sequenceNoFld.setEditable(false);
		gprsContainer.add(sequenceNoFld);
		
		gprsContainer.add(new JLabel("MAC地址"));
		macValueFld =new JTextField(25);
		macValueFld.setEditable(false);
		gprsContainer.add(macValueFld);
		
		gprsContainer.add(new JLabel("端口号"));
		portNoFld =new JTextField(25);
		portNoFld.setEditable(false);
		gprsContainer.add(portNoFld);
		
		gprsContainer.add(new JLabel("端口类型"));
		portTypeFld =new JTextField(25);
		portTypeFld.setEditable(false);
		gprsContainer.add(portTypeFld);
		
		SpringUtilities.makeCompactGrid(gprsContainer, 4, 2, 6, 6, 30, 6);
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
		
		ONUEntity onuNodeEntity = remoteServer.getService().getOnuByMacValue(
				((ONUTopoNodeEntity) nodeEntity).getMacValue());
		
		if(null != onuNodeEntity){
			if(isStart){
				sequenceNoFld.setText(Integer.toString(onuNodeEntity.getSequenceNo()));
				macValueFld.setText(onuNodeEntity.getMacValue());
				
				if(null != linkNode.getLldp()){
					int localPort = linkNode.getLldp().getLocalPortNo();
					portNoFld.setText(Integer.toString(localPort));
					portTypeFld.setText("PON");
				}
			}else{
				sequenceNoFld.setText(Integer.toString(onuNodeEntity.getSequenceNo()));
				macValueFld.setText(onuNodeEntity.getMacValue());
				
				if(null != linkNode.getLldp()){
					int remotePort = linkNode.getLldp().getLocalPortNo();
					portNoFld.setText(Integer.toString(remotePort));
					portTypeFld.setText("PON");
				}
			}
		}
	}

}
