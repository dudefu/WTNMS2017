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

@Component(MACConfigurationView.ID)
@Scope(Scopes.DESKTOP)
public class MACConfigurationView extends ViewPart{
	public static final String ID = "macConfigurationView";
	private JTabbedPane tabPnl = new JTabbedPane();
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Autowired
	@Qualifier(MACUnicastConfigurationView.ID)
	private MACUnicastConfigurationView unicastView;
	
	@Autowired
	@Qualifier(MACMulticastConfigurationView.ID)
	private MACMulticastConfigurationView multicastView;
	
	@PostConstruct
	protected void initialize(){
		this.setTitle("MAC管理");
		
		setCloseButton(unicastView.getCloseButton());
		setCloseButton(multicastView.getCloseButton());
		
		tabPnl.addTab("单播", unicastView);
		tabPnl.addTab("多播", multicastView);
		
		this.setViewSize(640, 510);
		this.setLayout(new BorderLayout());
		this.add(tabPnl,BorderLayout.CENTER);
	}
	
	public ImageRegistry getImageRegistry(){
		return imageRegistry;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		unicastView.close();
		multicastView.close();
	}
}
