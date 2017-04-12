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
		this.setTitle("SNMP配置");

//		setCloseButton(panelView.getCloseButton());
//		setCloseButton(comityView.getCloseButton());
//		setCloseButton(groupView.getCloseButton());
//		setCloseButton(userView.getCloseButton());
		setCloseButton(hostView.getCloseButton());
//		setCloseButton(engineView.getCloseButton());

		tabPnl.addTab("SNMP主机", hostView);
//		tabPnl.addTab("SNMP视图", panelView);
//		tabPnl.addTab("SNMP团体设置",comityView );
//		tabPnl.addTab("SNMP群组", groupView);
//		tabPnl.addTab("SNMP用户", userView);
//		tabPnl.addTab("SNMP引擎ID",engineView);
		
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
