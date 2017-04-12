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

@Component(OLTDBAInformationView.ID)
@Scope(Scopes.DESKTOP)
public class OLTDBAInformationView extends ViewPart{
	private static final long serialVersionUID = 1L;
	public static final String ID = "OLTDBAInformationView";
	
	private JTabbedPane tabPnl = new JTabbedPane();
	
	@Autowired
	@Qualifier(OLTDBAConfigView.ID)
	private OLTDBAConfigView oltDBAConfigView ;
	
	
	@PostConstruct
	protected void initialize(){
		this.setTitle("OLT DBA≈‰÷√");
		
		setCloseButton(oltDBAConfigView.getCloseButton());
		
		tabPnl.addTab("DBA≈‰÷√", oltDBAConfigView);
		
		this.setLayout(new BorderLayout());
		this.add(tabPnl,BorderLayout.CENTER);
		
		setViewSize(600, 480);	
	}
		
	@Override
	public void dispose() {
		super.dispose();
		oltDBAConfigView.close();
	}
}
