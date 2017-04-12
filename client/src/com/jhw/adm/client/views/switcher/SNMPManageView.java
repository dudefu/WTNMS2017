package com.jhw.adm.client.views.switcher;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.swing.JTabbedPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.views.ViewPart;

@Component(SNMPManageView.ID)
@Scope(Scopes.DESKTOP)
public class SNMPManageView extends ViewPart{
	private static final long serialVersionUID = 1L;
	public static final String ID = "SNMPManageView";

	private final JTabbedPane tabPnl = new JTabbedPane();
	
	@Autowired
	@Qualifier(SNMPPanelView.ID)
	private SNMPPanelView panelView;
	
	@Autowired
	@Qualifier(SNMPComityView.ID)
	private SNMPComityView comityView;
	
	@Autowired
	@Qualifier(SNMPGroupView.ID)
	private SNMPGroupView groupView;
	
	@Autowired
	@Qualifier(SNMPUserView.ID)
	private SNMPUserView userView;
	
	@Autowired
	@Qualifier(SNMPHostView.ID)
	private SNMPHostView hostView;
	
	@PostConstruct
	protected void initialize(){
		this.setTitle("SNMP����");

//		setCloseButton(panelView.getCloseButton());
//		setCloseButton(comityView.getCloseButton());
//		setCloseButton(groupView.getCloseButton());
//		setCloseButton(userView.getCloseButton());
		setCloseButton(hostView.getCloseButton());
//		setCloseButton(engineView.getCloseButton());

		tabPnl.addTab("SNMP����", hostView);
//		tabPnl.addTab("SNMP��ͼ", panelView);
//		tabPnl.addTab("SNMP��������",comityView );
//		tabPnl.addTab("SNMPȺ��", groupView);
//		tabPnl.addTab("SNMP�û�", userView);
//		tabPnl.addTab("SNMP����ID",engineView);
		
		this.setLayout(new BorderLayout());
		this.add(tabPnl,BorderLayout.CENTER);
		
		this.setViewSize(800, 600);
	}
	
	@Override
	public void dispose() {
		super.dispose();
//		panelView.close();
//		comityView.close();
//		groupView.close();
//		userView.close();
		hostView.close();
//		engineView.close();
	}
}
