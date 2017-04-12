/**
 * 
 */
package com.jhw.adm.client.views.switcher;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.swing.JTabbedPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.views.ViewPart;

/**
 * @author Administrator
 *
 */

@Component(STPConfigurationView.ID)
@Scope(Scopes.DESKTOP)
public class STPConfigurationView extends ViewPart{
	public static final String ID = "stpConfigurationView";
	
	private JTabbedPane tabPanel = new JTabbedPane();
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Autowired
	@Qualifier(STPSysConfigurationView.ID)
	private STPSysConfigurationView sysView;
	
	@Autowired
	@Qualifier(STPPortConfigurationView.ID)
	private STPPortConfigurationView portView;
	
	@Autowired
	@Qualifier(STPDataStatisticsView.ID)
	private STPDataStatisticsView dataView;
	
	
	@PostConstruct
	protected void initialize(){
		this.setTitle("STP配置");
		
//		sysView = new OLTSTPSysConfigurationView();
//		sysView.getCloseButton().setIcon(imageRegistry.getImageIcon(ButtonConstants.CLOSE));
//		sysView.getSaveButton().setIcon(imageRegistry.getImageIcon(ButtonConstants.SAVE));
//		
//		portView = new OLTSTPPortConfigurationView();
//		portView.getCloseButton().setIcon(imageRegistry.getImageIcon(ButtonConstants.CLOSE));
//		portView.getSaveButton().setIcon(imageRegistry.getImageIcon(ButtonConstants.SAVE));
		
		setCloseButton(sysView.getCloseButton());
		setCloseButton(portView.getCloseButton());
//		setCloseButton(dataView.getCloseButton());
		
		tabPanel.addTab("系统配置", sysView);
		tabPanel.addTab("端口配置", portView);
		tabPanel.addTab("数据统计", dataView);
		
		this.setLayout(new BorderLayout());
		this.add(tabPanel,BorderLayout.CENTER);
		
		this.setViewSize(640, 480);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		sysView.close();
		portView.close();
//		dataView.close();
	}
}
