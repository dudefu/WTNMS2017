package com.jhw.adm.client.views.epon;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.swing.JTabbedPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.views.ViewPart;

@Component(OLTSTPConfigurationView.ID)
@Scope(Scopes.DESKTOP)
public class OLTSTPConfigurationView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "OLTSTPConfigurationView";

	private JTabbedPane tabPnl = new JTabbedPane();
	
	@Autowired
	@Qualifier(OLTSTPSysConfigurationView.ID)
	private OLTSTPSysConfigurationView sysView ;
	
	@Autowired
	@Qualifier(OLTSTPPortConfigurationView.ID)
	private OLTSTPPortConfigurationView portView;
	
	@PostConstruct
	protected void initialize(){
		this.setTitle("OLT STP≈‰÷√");
		
		setCloseButton(sysView.getCloseButton());
		setCloseButton(portView.getCloseButton());
		
		tabPnl.addTab("œµÕ≥≈‰÷√", sysView);
		tabPnl.addTab("∂Àø⁄≈‰÷√", portView);
		
		this.setLayout(new BorderLayout());
		this.add(tabPnl,BorderLayout.CENTER);

		setViewSize(600, 480);	
	}
	
	@Override
	public void dispose() {
		super.dispose();
		sysView.close();
		portView.close();
	}
}
