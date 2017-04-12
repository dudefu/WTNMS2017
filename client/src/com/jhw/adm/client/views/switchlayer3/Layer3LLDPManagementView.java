package com.jhw.adm.client.views.switchlayer3;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.JTabbedPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.views.ViewPart;

@Component(Layer3LLDPManagementView.ID)
@Scope(Scopes.DESKTOP)
public class Layer3LLDPManagementView extends ViewPart {

	private static final long serialVersionUID = 1L;
	public static final String ID = "layer3LLDPManagementView";
	private static final Logger LOG = LoggerFactory.getLogger(Layer3LLDPManagementView.class);
	
	private JTabbedPane tabbedPane = new JTabbedPane();
	
	@Resource(name=Layer3LLDPPortConfigurationView.ID)
	private Layer3LLDPPortConfigurationView portConfigurationView;
	
	@Resource(name=Layer3LLDPBaseConfigurationView.ID)
	private Layer3LLDPBaseConfigurationView baseConfigurationView;
	
	@PostConstruct
	protected void initialize(){
		this.setTitle("LLDP≈‰÷√");
		
		setCloseButton(baseConfigurationView.getCloseButton());
		setCloseButton(portConfigurationView.getCloseButton());
		
		tabbedPane.addTab("ª˘±æ≈‰÷√", baseConfigurationView);
		tabbedPane.addTab("∂Àø⁄≈‰÷√", portConfigurationView);
		
		this.setLayout(new BorderLayout());
		this.add(tabbedPane,BorderLayout.CENTER);
		
		this.setViewSize(640, 480);
	}
	
	@Override
	public void dispose(){
		super.dispose();
		baseConfigurationView.close();
		portConfigurationView.close();
	}
}
