package com.jhw.adm.client.views;

import static com.jhw.adm.client.core.ActionConstants.QUERY;

import java.awt.BorderLayout;
import java.awt.Dimension;
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
import javax.swing.JTextField;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jdesktop.swingx.JXDatePicker;
import org.jfree.chart.ChartPanel;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.jhw.adm.client.actions.ActionManager;
import com.jhw.adm.client.actions.ButtonFactory;
import com.jhw.adm.client.actions.ViewAction;
import com.jhw.adm.client.core.ButtonConstants;
import com.jhw.adm.client.core.PerformanceInfoSPLineChartBuilder;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.core.Scopes;
import com.jhw.adm.client.swing.NumberField;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.warning.RmonCount;

@Component(PerformanceInfoSPLineChartView.ID)
@Scope(Scopes.DESKTOP)
public class PerformanceInfoSPLineChartView extends ViewPart {
	
	private static final long serialVersionUID = 1L;
	public static final String ID = "performanceInfoSPLineChartView";
	
	private JXDatePicker fromPicker;
	private JXDatePicker toPicker;
	private JTextField equipmentField;
	private NumberField portFld;
	private JComboBox parameterBox;
	
	private ButtonFactory buttonFactory;
	private JButton queryBtn;
	
	private PerformanceInfoSPLineChartBuilder performanceInfoSPLineChartBuilder = new PerformanceInfoSPLineChartBuilder();
	private ChartPanel centerPanel = null; 
	private JPanel middlePanel = new JPanel(new BorderLayout());
	
	@Resource(name=ActionManager.ID)
	private ActionManager actionManager;
	
	@Resource(name=RemoteServer.ID)
	private RemoteServer remoteServer;
	
	@PostConstruct
	public void initialize() {
		setTitle("实时监控分析图");
		setLayout(new BorderLayout());
		createContents(this);
		iniSPLineChartGenerator();
	}
	
	private void iniSPLineChartGenerator(){
		
	}
	
	private void createContents(JPanel parent) {
		buttonFactory = actionManager.getButtonFactory(this);
		
		JPanel toolPanel = new JPanel(new GridLayout(2, 1));
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
		
		queryPanel.add(new JLabel("监控设备"));
		equipmentField = new JTextField(20);
		queryPanel.add(equipmentField);

		queryPanel.add(new JLabel("监控端口"));
		portFld = new NumberField(2,0,1,100,true);
		portFld.setPreferredSize(new Dimension(100, portFld.getPreferredSize().height));
		queryPanel.add(portFld);
		
		queryPanel.add(new JLabel("参数"));
		parameterBox = new JComboBox(new String[] { 
				Constants.packets, Constants.octets, Constants.bcast_pkts, Constants.mcast_pkts,
				Constants.crc_align, Constants.undersize, Constants.oversize, Constants.fragments,
				Constants.jabbers, Constants.collisions, Constants.pkts_64, Constants.pkts_65_127,
				Constants.pkts_128_255, Constants.pkts_256_511, Constants.pkts_512_1023, Constants.pkts_1024_1518});
		queryPanel.add(parameterBox);
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		queryBtn = buttonFactory.createButton(QUERY);
		queryBtn.setText("生成图");
		buttonPanel.add(queryBtn);
		
		toolPanel.add(queryPanel);
		toolPanel.add(buttonPanel);
		
		this.middlePanel.setBorder(BorderFactory.createTitledBorder("分析结果"));
		
		parent.add(toolPanel,BorderLayout.NORTH);
		parent.add(middlePanel, BorderLayout.CENTER);
	}
	
	@ViewAction(name=QUERY,icon=ButtonConstants.QUERY,desc="生成性能监控分析结果",role=Constants.MANAGERCODE)
	public void query(){
		Date startDate = fromPicker.getDate();
		Date endDate = toPicker.getDate();
		
		String ipValue = equipmentField.getText().trim();
		int portNum = -1;
		if(!StringUtils.isBlank(portFld.getText())){
			portNum = NumberUtils.toInt(portFld.getText());
		}
		String param = "";
		if(null != parameterBox.getSelectedItem()){
			param = parameterBox.getSelectedItem().toString();
		}
		
		List<RmonCount> rmonCountBeanList = remoteServer.getNmsService().queryRmonCount(startDate, endDate, ipValue, portNum, param);
//		for(int i = 0; i < rmonCountBeanList.size(); i++){
//			
//		}
		pieChartGenerator(rmonCountBeanList);
	}
	
	private void pieChartGenerator(List<RmonCount> rmonCountBeanList){
		middlePanel.removeAll();
		centerPanel = performanceInfoSPLineChartBuilder.createChartPanel(rmonCountBeanList);
		middlePanel.add(centerPanel, BorderLayout.CENTER);
		middlePanel.revalidate();
	}
}