package com.jhw.adm.client.views.switcher;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.swing.JTabbedPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.model.switcher.GHRINGConfViewModel;
import com.jhw.adm.client.views.ViewPart;

@Component(GHRINGConfigurationView.ID)
@Scope(Scopes.DESKTOP)
public class GHRINGConfigurationView extends ViewPart{
	public static final String ID = "ghRINGConfigurationView";
	
	private JTabbedPane tabPnl = new JTabbedPane();
	
	@Autowired
	@Qualifier(GHRINGPortConfigurationView.ID)
	private GHRINGPortConfigurationView ghRINGPortConfigurationView;
	
	@Autowired
	@Qualifier(GHRINGLinkBakupView.ID)
	private GHRINGLinkBakupView ghRINGLinkBakupView;
	
	@Autowired
	@Qualifier(GHRINGPortInfoView.ID)
	private GHRINGPortInfoView ghRINGPortInfoView;
	
	@Autowired
	@Qualifier(GHRINGPortStatisticsView.ID)
	private GHRINGPortStatisticsView ghRINGPortStatisticsView;
	
	@Autowired
	@Qualifier(GHRINGConfViewModel.ID)
	private GHRINGConfViewModel viewModel;
	
	@PostConstruct
	protected void initialize(){
		this.setTitle("WT-RING����");
		
		setCloseButton(ghRINGPortConfigurationView.getCloseButton());
		setCloseButton(ghRINGLinkBakupView.getCloseButton());
//		setCloseButton(ghRINGPortInfoView.getCloseButton());
//		setCloseButton(ghRINGPortStatisticsView.getCloseButton());
		
		tabPnl.addTab("�˿�����", ghRINGPortConfigurationView);
		tabPnl.addTab("��·����", ghRINGLinkBakupView);
		tabPnl.addTab("�˿���Ϣ", ghRINGPortInfoView);
		tabPnl.addTab("�˿�ͳ��", ghRINGPortStatisticsView);
		
		this.setLayout(new BorderLayout());
		this.add(tabPnl,BorderLayout.CENTER);
		
		this.setViewSize(740, 580);	
	}
	
	@Override
	public void dispose() {
		super.dispose();
		ghRINGPortConfigurationView.close();
		ghRINGLinkBakupView.close();
		ghRINGPortInfoView.close();
		ghRINGPortStatisticsView.close();
	}
}
