package com.jhw.adm.client.views.switchlayer3;

import java.awt.BorderLayout;

import javax.annotation.PostConstruct;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.views.ViewPart;

/*
 * 单个配置
 */

@Component(VlanManageLayer3View.ID)
@Scope(Scopes.DESKTOP)
public class VlanManageLayer3View extends ViewPart {

	private static final long serialVersionUID = 1L;
	public static final String ID = "vlanManageLayer3View";

	private JTabbedPane tabPnl = new JTabbedPane();

	@Autowired
	@Qualifier(VlanAddLayer3View.ID)
	private VlanAddLayer3View vlanAddView;

	@Autowired
	@Qualifier(VlanPortConfigLayer3View.ID)
	private VlanPortConfigLayer3View vlanPortView;
	
	@PostConstruct
	protected void initialize(){
		this.setTitle("Vlan管理");
		
		setCloseButton(vlanAddView.getCloseButton());
		setCloseButton(vlanPortView.getCloseButton());
		
		tabPnl.addTab("Vlan ID", vlanAddView);
		tabPnl.addTab("Vlan端口", vlanPortView);
		
		this.setLayout(new BorderLayout());
		this.add(tabPnl,BorderLayout.CENTER);
		
		vlanPortView.setVlanManageView(this);
		
		setViewSize(580, 500);	
	}
	
	public void queryVlanData(){
		SwingUtilities.invokeLater(new Runnable(){
			public void run(){
				vlanAddView.queryData();
			}
		});
	}
	
	@Override
	public void dispose() {
		super.dispose();
		vlanAddView.close();
		vlanPortView.close();
	}

}