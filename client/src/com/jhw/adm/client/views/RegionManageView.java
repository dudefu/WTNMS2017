package com.jhw.adm.client.views;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.swing.JTabbedPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.Scopes;

@Component(RegionManageView.ID)
@Scope(Scopes.DESKTOP)
public class RegionManageView extends ViewPart {
	public static final String ID = "regionManageView";
	private JTabbedPane tabPnl = new JTabbedPane();
	
	@Autowired
	@Qualifier(ImageRegistry.ID)
	private ImageRegistry imageRegistry;
	
	@Autowired
	@Qualifier(PermissionsConfigView.ID)
	private PermissionsConfigView permissionsConfigView;
	
	@Autowired
	@Qualifier(RegionConfigView.ID)
	private RegionConfigView regionConfigView;
	
	
	@PostConstruct
	protected void initialize(){
		this.setTitle("区域管理");
		
		setCloseButton(permissionsConfigView.getCloseButton());
		setCloseButton(regionConfigView.getCloseButton());
		
		tabPnl.addTab("权限分配", permissionsConfigView);
		tabPnl.addTab("区域分配", regionConfigView);
		
		this.setViewSize(680, 560);
		this.setLayout(new BorderLayout());
		this.add(tabPnl,BorderLayout.CENTER);
	}
	
	public ImageRegistry getImageRegistry(){
		return imageRegistry;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		permissionsConfigView.close();
		regionConfigView.close();
	}
}
