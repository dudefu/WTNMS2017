package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.QUERY;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang.StringUtils;
import org.jdesktop.swingx.JXDatePicker;
import org.jfree.chart.ChartPanel;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.core.WarningInfoCategoryChartBuilder;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.TrapCountBean;

@Component(WarningTopTenInfoCategoryChartView.ID)
@Scope(Scopes.DESKTOP)
public class WarningTopTenInfoCategoryChartView extends ViewPart {
	
	private static final long serialVersionUID = 1L;
	public static final String ID = "warningTopTenInfoCategoryChartView";
	private static final String[] ROW_KEY = {"告警设备"};
	
	private JXDatePicker fromPicker;
	private JXDatePicker toPicker;
	
	private ButtonFactory buttonFactory;
	private JButton queryBtn;
	
	private WarningInfoCategoryChartBuilder categoryChartBuilder = new WarningInfoCategoryChartBuilder();
	private ChartPanel centerPanel = null; 
	private JPanel middlePanel = new JPanel(new BorderLayout());
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@PostConstruct
	public void initialize() {
		setTitle("告警信息Top10统计图");
		setLayout(new BorderLayout());
		createContents(this);
	}
	
	private void createContents(JPanel parent) {
		buttonFactory = actionManager.getButtonFactory(this);
		
		JPanel toolPanel = new JPanel(new GridLayout(1, 1));
		toolPanel.setBorder(BorderFactory.createTitledBorder("查询条件"));
		
		JPanel queryPanel = new JPanel(new FlowLayout(FlowLayout.LEADING));
		queryPanel.add(new JLabel("开始时间"));
		fromPicker = new JXDatePicker();
		fromPicker.setFormats("yyyy-MM-dd");
		queryPanel.add(fromPicker);
		
		queryPanel.add(new JLabel("到"));
		toPicker = new JXDatePicker();
		toPicker.setFormats("yyyy-MM-dd");
		queryPanel.add(toPicker);
		
		queryBtn = buttonFactory.createButton(QUERY);
		queryBtn.setText("生成图");
		queryPanel.add(queryBtn);
		
		toolPanel.add(queryPanel);
		
		this.middlePanel.setBorder(BorderFactory.createTitledBorder("分析结果"));
		
		parent.add(toolPanel,BorderLayout.NORTH);
		parent.add(middlePanel, BorderLayout.CENTER);
	}
	
	@ViewAction(name=QUERY,icon=ButtonConstants.QUERY,desc="生成告警信息Top10分析结果",role=Constants.MANAGERCODE)
	public void query(){
		Date startDate = fromPicker.getDate();
		Date endDate = toPicker.getDate();
		
		List<TrapCountBean> trapCountBeanList = remoteServer.getNmsService().queryTrapWarningTop10(startDate, endDate);
		List<TrapCountBean> trapCountBeans = new ArrayList<TrapCountBean>();
		for(TrapCountBean trapCountBean : trapCountBeanList){
			if(!StringUtils.isBlank(trapCountBean.getIpValue())){
				trapCountBeans.add(trapCountBean);
			}
		}
		
		int size = trapCountBeans.size();
		double[][] data = new double[1][size];
		String[] colMonthKey = new String[size];
		for(int i = 0;i < size;i++){
			data[0][i] = trapCountBeans.get(i).getCount();
			colMonthKey[i] = trapCountBeans.get(i).getIpValue();
		}
		pieChartGenerator(data,colMonthKey);
	}
	
	private void pieChartGenerator(double[][] data,String[] colMonthKey){
		middlePanel.removeAll();
		centerPanel = categoryChartBuilder.createChartPanel(data, colMonthKey,"告警信息Top10统计图",this.ROW_KEY);
		middlePanel.add(centerPanel, BorderLayout.CENTER);
		middlePanel.revalidate();
	}
}