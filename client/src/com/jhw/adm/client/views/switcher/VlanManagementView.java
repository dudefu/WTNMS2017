package com.jhw.adm.client.views.switcher;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.views.ViewPart;
import com.jhw.adm.server.entity.util.SwitchVlanPortInfo;
import com.jhw.adm.server.entity.util.VlanBean;

@Component(VlanManagementView.ID)
@Scope(Scopes.DESKTOP)
public class VlanManagementView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "vlanManagementView";

	private JTabbedPane tabPnl = new JTabbedPane();
	
	private JPanel cardPnl = new JPanel();
	
	private CardLayout cardLayout = new CardLayout();
	
	public static final String LIST_CARD = "LIST_CARD";
	public static final String DETAIL_CARD = "DETAIL_CARD";
	
	private VlanBean selectVlanBean;
	private List<SwitchVlanPortInfo> switchVlanPortInfoList;
	
	@Autowired
	@Qualifier(VlanAddView.ID)
	private VlanAddView vlanAddView;
	
	@Autowired
	@Qualifier(VlanConfigView.ID)
	private VlanConfigView vlanConfigView;
	
	@Autowired
	@Qualifier(ConfigureVLANPortView.ID)
	private ConfigureVLANPortView vlanPortView;
	
	@PostConstruct
	protected void initialize(){
		this.setTitle("VLAN管理");
		
		cardPnl.setLayout(cardLayout);
		createContents();
		
		setCloseButton(vlanAddView.getCloseButton());
		setCloseButton(vlanConfigView.getCloseButton());
		setCloseButton(vlanPortView.getCloseButton());
		
		vlanAddView.setCardLayout(cardLayout,cardPnl,this);
		vlanConfigView.setCardLayout(cardLayout,cardPnl,this);
		
		tabPnl.addTab("VLAN ID", cardPnl);
		tabPnl.addTab("VLAN端口配置", vlanPortView);
		
		this.setLayout(new BorderLayout());
		this.add(tabPnl,BorderLayout.CENTER);
		
		setViewSize(800, 600);	
	}
	
	private void createContents() {	
		cardPnl.add(vlanAddView, LIST_CARD);
		cardPnl.add(vlanConfigView, DETAIL_CARD);
	}

	public void setSelectVlanBean(VlanBean selectVlanBean) {
		this.selectVlanBean = selectVlanBean;
	}

	public void setSwitchVlanPortInfoList(
			List<SwitchVlanPortInfo> switchVlanPortInfoList) {
		this.switchVlanPortInfoList = switchVlanPortInfoList;
	}
	
	public void refreshVlanConfigView(){
		vlanConfigView.setValue(selectVlanBean,switchVlanPortInfoList);
	}
	
	//刷新vlan中的端口table
	public void refreshAddVlanPortView(){
		vlanAddView.vlanTableAction();
	}
	
	//刷新vlan table
	public void refreshVlanTableView(){
		vlanAddView.queryData();
	}

	@Override
	public void dispose() {
		super.dispose();
		vlanAddView.close();
		vlanConfigView.close();
		vlanPortView.close();
	}
}
