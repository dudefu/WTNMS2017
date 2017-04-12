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

@Component(ConfigureSNMPView.ID)
@Scope(Scopes.DESKTOP)
public class ConfigureSNMPView extends ViewPart{
	public static final String ID = "configureSNMPView";
	
	private JTabbedPane tabPnl = new JTabbedPane();
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Autowired
	@Qualifier(SNMPPanelView.ID)
	private SNMPPanelView panelView;
	
	@Autowired
	@Qualifier(SNMPComityView.ID)
	private SNMPComityView comityView;
	
	@Autowired
	@Qualifier(SNMPGroupView.ID)
	private SNMPGroupView groupView;
	
	@Autowired
	@Qualifier(SNMPUserView.ID)
	private SNMPUserView userView;
	
	@Autowired
	@Qualifier(SNMPHostView.ID)
	private SNMPHostView hostView;
	
//	@Autowired
//	@Qualifier(SNMPEngineIDView.ID)
//	private SNMPEngineIDView engineView;
	
	@PostConstruct
	protected void initialize(){
		this.setTitle("SNMP配置");
//		panelView = new SNMPPanelView();
//		panelView.getCloseButton().setIcon(imageRegistry.getImageIcon(ButtonConstants.CLOSE));
//		panelView.getSaveButton().setIcon(imageRegistry.getImageIcon(ButtonConstants.SAVE));
//		panelView.getDelButton().setIcon(imageRegistry.getImageIcon(ButtonConstants.DELETE));
		
//		comityView =  new SNMPComityView();
//		comityView.getCloseButton().setIcon(imageRegistry.getImageIcon(ButtonConstants.CLOSE));
//		comityView.getSaveButton().setIcon(imageRegistry.getImageIcon(ButtonConstants.SAVE));
//		comityView.getDelButton().setIcon(imageRegistry.getImageIcon(ButtonConstants.DELETE));
		
//		groupView =  new SNMPGroupView();
//		groupView.getCloseButton().setIcon(imageRegistry.getImageIcon(ButtonConstants.CLOSE));
//		groupView.getSaveButton().setIcon(imageRegistry.getImageIcon(ButtonConstants.SAVE));
//		groupView.getDelButton().setIcon(imageRegistry.getImageIcon(ButtonConstants.DELETE));
		
//		userView = new SNMPUserView() ;
//		userView.getCloseButton().setIcon(imageRegistry.getImageIcon(ButtonConstants.CLOSE));
//		userView.getSaveButton().setIcon(imageRegistry.getImageIcon(ButtonConstants.SAVE));
//		userView.getDelButton().setIcon(imageRegistry.getImageIcon(ButtonConstants.DELETE));
		
//		hostView = new SNMPHostView() ;
//		hostView.getCloseButton().setIcon(imageRegistry.getImageIcon(ButtonConstants.CLOSE));
//		hostView.getSaveButton().setIcon(imageRegistry.getImageIcon(ButtonConstants.SAVE));
//		hostView.getDelButton().setIcon(imageRegistry.getImageIcon(ButtonConstants.DELETE));
		
//		engineView = new SNMPEngineIDView();
//		engineView.getCloseButton().setIcon(imageRegistry.getImageIcon(ButtonConstants.CLOSE));
//		engineView.getSaveButton().setIcon(imageRegistry.getImageIcon(ButtonConstants.SAVE));

		setCloseButton(panelView.getCloseButton());
		setCloseButton(comityView.getCloseButton());
		setCloseButton(groupView.getCloseButton());
		setCloseButton(userView.getCloseButton());
		setCloseButton(hostView.getCloseButton());
//		setCloseButton(engineView.getCloseButton());
		
		tabPnl.addTab("SNMP视图", panelView);
		tabPnl.addTab("SNMP团体设置",comityView );
		tabPnl.addTab("SNMP群组", groupView);
		tabPnl.addTab("SNMP用户", userView);
		tabPnl.addTab("SNMP主机", hostView);
//		tabPnl.addTab("SNMP引擎ID",engineView);
		
		this.setLayout(new BorderLayout());
		this.add(tabPnl,BorderLayout.CENTER);
		
		this.setViewSize(640, 480);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		panelView.close();
		comityView.close();
		groupView.close();
		userView.close();
		hostView.close();
//		engineView.close();
	}
}
