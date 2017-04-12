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
import com.jhw.adm.server.entity.util.RingBean;
import com.jhw.adm.server.entity.util.RingInfo;

@Component(RingManagementView.ID)
@Scope(Scopes.DESKTOP)
public class RingManagementView extends ViewPart {
	private static final long serialVersionUID = 1L;
	public static final String ID = "ringManagementView";
	
	private JTabbedPane tabPnl = new JTabbedPane();
	
	private JPanel cardPnl = new JPanel();
	
	private CardLayout cardLayout = new CardLayout();
	
	private RingBean selectRingBean;
	
	private List<RingInfo> ringInfoList;
	
	public static final String LIST_CARD = "LIST_CARD";
	public static final String DETAIL_CARD = "DETAIL_CARD";
	
	@Autowired
	@Qualifier(RingAddView.ID)
	private RingAddView ringAddView;
	
	@Autowired
	@Qualifier(RingConfigView.ID)
	private RingConfigView ringConfigView;
	
	@Autowired
	@Qualifier(GHRINGLinkBakupView.ID)
	private GHRINGLinkBakupView linkBakupView;
	
	@Autowired
	@Qualifier(GHRINGPortInfoView.ID)
	private GHRINGPortInfoView ringPortInfoView;
	
	@Autowired
	@Qualifier(GHRINGPortStatisticsView.ID)
	private GHRINGPortStatisticsView ringPortStatisticsView;
	
	@PostConstruct
	protected void initialize(){
		this.setTitle("WT-Ring管理");
		
		cardPnl.setLayout(cardLayout);
		createContents();
		
		setCloseButton(ringAddView.getCloseButton());
		setCloseButton(ringConfigView.getCloseButton());
		setCloseButton(linkBakupView.getCloseButton());
		
		ringAddView.setCardLayout(cardLayout,cardPnl,this);
		ringConfigView.setCardLayout(cardLayout,cardPnl,this);
		
		tabPnl.addTab("端口配置", cardPnl);
		tabPnl.addTab("主备链路", linkBakupView);
		tabPnl.addTab("端口信息",ringPortInfoView);
		tabPnl.addTab("信息统计", ringPortStatisticsView);
		
		this.setLayout(new BorderLayout());
		this.add(tabPnl,BorderLayout.CENTER);
		
		setViewSize(800, 600);
	}
	
	public void setSelectRingBean(RingBean selectRingBean) {
		this.selectRingBean = selectRingBean;
	}
	
	public void setRinginfoList(List<RingInfo> ringInfoList){
		this.ringInfoList = ringInfoList;
	}
	
	public void refreshRingConfigView(){
		ringConfigView.setValue(selectRingBean,ringInfoList);
	}
	
	public void refreshAddRingView(){
		ringAddView.ringTableAction();
	}
	
	//刷新ring table
	public void refreshRingTableView(){
		ringAddView.queryData();
	}
	
	private void createContents() {	
		cardPnl.add(ringAddView, LIST_CARD);
		cardPnl.add(ringConfigView, DETAIL_CARD);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		ringAddView.close();
		ringConfigView.close();
		linkBakupView.close();
		ringPortInfoView.close();
		ringPortStatisticsView.close();
	}

}
