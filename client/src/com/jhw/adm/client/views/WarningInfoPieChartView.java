package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.QUERY;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
import com.jhw.adm.client.core.WarningInfoPieChartBuilder;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.TrapCountBean;

@Component(WarningInfoPieChartView.ID)
@Scope(Scopes.DESKTOP)
public class WarningInfoPieChartView extends ViewPart {
	
	private static final long serialVersionUID = 1L;
	public static final String ID = "warningInfoPieChartView";
	
	private JXDatePicker fromPicker;
	private JXDatePicker toPicker;
	
	private ButtonFactory buttonFactory;
	private JButton queryBtn;
	private JComboBox chartType = null;
	
	private WarningInfoPieChartBuilder pieChartBuilder = new WarningInfoPieChartBuilder();
	private ChartPanel centerPanel = null; 
	private static final String[] TYPE = {"饼状图（按告警级别）","饼状图（按告警类型）"};
	private JPanel middlePanel = new JPanel(new BorderLayout());
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@PostConstruct
	public void initialize() {
		setTitle("告警信息统计图");
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
		
		queryPanel.add(new JLabel("图类别"));
		chartType = new JComboBox();
		setComboxItem();
		chartType.setEditable(false);
		queryPanel.add(chartType);
		
//		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		queryBtn = buttonFactory.createButton(QUERY);
		queryBtn.setText("生成图");
		queryPanel.add(queryBtn);
		
		toolPanel.add(queryPanel);
//		toolPanel.add(buttonPanel);
		
		this.middlePanel.setBorder(BorderFactory.createTitledBorder("分析结果"));
		
		parent.add(toolPanel,BorderLayout.NORTH);
		parent.add(middlePanel, BorderLayout.CENTER);
	}
	
	private void setComboxItem(){
		chartType.removeAllItems();
		for(String item : TYPE){
			chartType.addItem(item);
		}
		chartType.setSelectedItem(TYPE[0]);
	}
	
	@ViewAction(name=QUERY,icon=ButtonConstants.QUERY,desc="生成告警信息分析结果",role=Constants.MANAGERCODE)
	public void query(){
		Date startDate = fromPicker.getDate();
		Date endDate = toPicker.getDate();
		
		if(chartType.getSelectedItem().toString().equals(TYPE[0])){
			List<TrapCountBean> trapCountbeanList = remoteServer
					.getNmsService().queryTrapWarningLevel(startDate, endDate);
			pieChartGenerator(trapCountbeanList,"level");
		}else if(chartType.getSelectedItem().toString().equals(TYPE[1])){
			List<TrapCountBean> trapCountbeanList = remoteServer
					.getNmsService().queryTrapWarningCategory(startDate, endDate);
			pieChartGenerator(trapCountbeanList,"type");
		}
	}
	
	private void pieChartGenerator(List<TrapCountBean> trapCountbeanList,String type){
		middlePanel.removeAll();
		centerPanel = pieChartBuilder.createChartPanel(trapCountbeanList, type);
		middlePanel.add(centerPanel, BorderLayout.CENTER);
		middlePanel.revalidate();
	}
}