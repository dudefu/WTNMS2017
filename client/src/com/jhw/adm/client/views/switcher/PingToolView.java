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

@Component(PingToolView.ID)
@Scope(Scopes.DESKTOP)
public class PingToolView extends ViewPart{
	public static final String ID = "pingToolView";
	
	private JTabbedPane tabPnl = new JTabbedPane();
	
	@Autowired
	@Qualifier(PingToolRealtimeView.ID)
	private PingToolRealtimeView realtimeView;
	
	@Autowired
	@Qualifier(PingToolTimingView.ID)
	private PingToolTimingView timingView;
	
	@PostConstruct
	protected void initialize(){
		this.setTitle("SNMP配置");

		//setCloseButton(realtimeView.getCloseButton());
		setCloseButton(timingView.getCloseButton());

		
		tabPnl.addTab("实时Ping工具", realtimeView);
		tabPnl.addTab("定时Ping工具",timingView );
		
		this.setLayout(new BorderLayout());
		this.add(tabPnl,BorderLayout.CENTER);
		
		this.setViewSize(640, 680);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		realtimeView.close();
		timingView.close();
	}
}
