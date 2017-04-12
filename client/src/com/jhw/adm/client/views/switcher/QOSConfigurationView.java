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

@Component(QOSConfigurationView.ID)
@Scope(Scopes.DESKTOP)
public class QOSConfigurationView extends ViewPart{
	private static final long serialVersionUID = 1L;
	public static final String ID = "qosConfigurationView";
	private JTabbedPane tabPnl = new  JTabbedPane();
	
	@Autowired
	@Qualifier(QOSSysConfigurationView.ID)
	private QOSSysConfigurationView sysView;
	
	@Autowired
	@Qualifier(QOSRateLimitView.ID)
	private QOSRateLimitView rateView;
	
	@Autowired
	@Qualifier(QOSPriorityConfigurationView.ID)
	private QOSPriorityConfigurationView priorityView;
	
	@Autowired
	@Qualifier(QOSStormControlView.ID)
	private QOSStormControlView stormView;
	
	@PostConstruct
	protected void initialize(){
		this.setTitle("QoS配置");
		
		setCloseButton(sysView.getCloseButton());
		setCloseButton(priorityView.getCloseButton());
		setCloseButton(rateView.getCloseButton());
		setCloseButton(stormView.getCloseButton());
		
		tabPnl.addTab("系统配置", sysView);
		tabPnl.addTab("优先级配置", priorityView);
		tabPnl.addTab("端口限速", rateView);
		tabPnl.addTab("风暴控制", stormView);
		
		this.setLayout(new BorderLayout());
		this.add(tabPnl,BorderLayout.CENTER);
		
		this.setViewSize(645, 540);
		this.revalidate();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		sysView.close();
		priorityView.close();
		rateView.close();
		stormView.close();
	}
}
