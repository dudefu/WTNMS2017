package com.jhw.adm.client.views;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.JTabbedPane;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.Scopes;

@Component(RealtimePerformanceView.ID)
@Scope(Scopes.DESKTOP)
public class RealtimePerformanceView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "realtimePerformanceView";
	
	private final JTabbedPane tabPnl = new JTabbedPane();
	
	@Resource(name=RealtimePerformStatisticsView.ID)
	private RealtimePerformStatisticsView statisticsView;
	
	@Resource(name=RealtimePerformConfigView.ID)
	private RealtimePerformConfigView configView;
	
	@PostConstruct
	protected void initialize(){
		setTitle("实时性能监控");
		tabPnl.addTab("实时性能监控", statisticsView);
		tabPnl.addTab("实时性能配置", configView);
		this.setLayout(new BorderLayout());
		this.add(tabPnl,BorderLayout.CENTER);
		setViewSize(825, 600);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		statisticsView.close();
		configView.close();
	}
}