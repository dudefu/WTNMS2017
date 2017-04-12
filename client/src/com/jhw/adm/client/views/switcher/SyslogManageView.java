package com.jhw.adm.client.views.switcher;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.swing.JTabbedPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.views.SyslogHostView;
import com.jhw.adm.client.views.ViewPart;

@Component(SyslogManageView.ID)
@Scope(Scopes.DESKTOP)
public class SyslogManageView extends ViewPart{
	private static final long serialVersionUID = 1L;
	public static final String ID = "syslogManageView";

	private final JTabbedPane tabPnl = new JTabbedPane();
	
	@Autowired
	@Qualifier(SyslogHostView.ID)
	private SyslogHostView hostView;
	
	@PostConstruct
	protected void initialize(){
		this.setTitle("SYSLOG管理");

		setCloseButton(hostView.getCloseButton());
		tabPnl.addTab("SYSLOG主机", hostView);
		
		this.setLayout(new BorderLayout());
		this.add(tabPnl,BorderLayout.CENTER);
		
		this.setViewSize(800, 600);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		hostView.close();
	}
}
