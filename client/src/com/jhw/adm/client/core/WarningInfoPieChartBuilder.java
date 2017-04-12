package com.jhw.adm.client.core;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.DefaultPieDataset;

import com.jhw.adm.client.model.AlarmEvents;
import com.jhw.adm.client.model.AlarmSeverity;
import com.jhw.adm.client.model.AlarmTypeCategory;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.server.entity.util.Constants;
import com.jhw.adm.server.entity.util.TrapCountBean;

public class WarningInfoPieChartBuilder implements WarningInfoChartBuilder {

	private AlarmSeverity alarmSeverity;
	private AlarmEvents alarmEvents;
	private AlarmTypeCategory alarmTypeCategory;
	
	private static final String LEVEL = "level";
	private static final String TYPE = "type";
	
	private DefaultPieDataset dataset = null;
	private String condition = "";
	private List<TrapCountBean> trapCountBeanList = null;
	
	@Override
	public JFreeChart createChart(String name) {
		
		JFreeChart freeChart = ChartFactory.createPieChart3D("�澯��Ϣͳ��ͼ" + name, this.dataset, true, true, false);
		PiePlot plot = (PiePlot)freeChart.getPlot();   
		
		setTitleFont(freeChart);
		setSection(plot);
		setLabel(plot);
		setNoDataMessage(plot);
		setNullAndZeroValue(plot);
		plot.setBackgroundPaint(Color.WHITE);
		
		return freeChart;
	}
	
	private void setTitleFont(JFreeChart freeChart){
		TextTitle textTitle = freeChart.getTitle();
		textTitle.setFont(new Font("����",Font.BOLD,12));
		LegendTitle legendTitle = freeChart.getLegend();
		legendTitle.setItemFont(new Font("����",10,12));
		freeChart.setAntiAlias(false);
		freeChart.setBackgroundPaint(Color.WHITE);
	}
	
	private void setSection(PiePlot piePlot){
//����������ɫ
		
		for(TrapCountBean trapCountBean : this.trapCountBeanList){
			if(this.condition.toUpperCase().equals(LEVEL.toUpperCase())){
				piePlot.setSectionPaint(alarmSeverity.get(trapCountBean.getWarningLevel()).getKey(),
								alarmSeverity.getColor(trapCountBean.getWarningLevel()));
//				//��������������ʾ
//				if(trapCountBean.getWarningLevel() == Constants.URGENCY){
//					piePlot.setExplodePercent(alarmSeverity.get(
//							trapCountBean.getWarningLevel()).getKey(), 0.2D);
//				}
			}else if(this.condition.toUpperCase().equals(TYPE.toUpperCase())){
//				if(trapCountBean.getWarningLevel() == Constants.LINKDOWN){
//					piePlot.setExplodePercent(alarmEvents.get(
//							trapCountBean.getWarningLevel()).getKey(), 0.2D);
//				}
			}
		}
		//���������߿򲻿ɼ� 
		piePlot.setSectionOutlinesVisible(false);
	}
	
	private void setLabel(PiePlot piePlot){
		//����������ǩ��ʾ��ʽ���ؼ��֣�ֵ(�ٷֱ�)
//		piePlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}��{1}({2} percent)"));
		piePlot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}��{1}({2})"));
		//����������ǩ��ɫ
		piePlot.setLabelBackgroundPaint(new Color(220, 220, 220));
		piePlot.setLabelFont(new Font("����",Font.BOLD,15));
	}
	
	private void setNoDataMessage(PiePlot piePlot){
		piePlot.setNoDataMessage("�޸澯����");
		piePlot.setNoDataMessageFont(new Font("����", Font.BOLD, 14));
		piePlot.setNoDataMessagePaint(Color.RED);
	}
	
	private void setNullAndZeroValue(PiePlot piePlot) {   
        //�����Ƿ����0��nullֵ
         piePlot.setIgnoreNullValues(false);
         piePlot.setIgnoreZeroValues(false);
    }

	@Override
	public void createDataset() {
		dataset = new DefaultPieDataset();
		alarmSeverity = ClientUtils.getSpringBean(AlarmSeverity.ID);
		
		for(TrapCountBean trapCountBean : this.trapCountBeanList){
			dataset.setValue(alarmSeverity.get(trapCountBean.getWarningLevel())
					.getKey(), trapCountBean.getCount());
		}
	}
	
	public void createDatasetByType() {
		dataset = new DefaultPieDataset();
		alarmTypeCategory = ClientUtils.getSpringBean(AlarmTypeCategory.ID);

		for (TrapCountBean trapCountBean : this.trapCountBeanList) {
			dataset.setValue(alarmTypeCategory.get(trapCountBean.getWarningLevel())
					.getKey(), trapCountBean.getCount());
		}
	}
	
	public ChartPanel createChartPanel(List<TrapCountBean> trapCountBeanList,String condition){
		this.condition = condition;
		this.trapCountBeanList = trapCountBeanList;
		
		if(condition.toUpperCase().equals(LEVEL.toUpperCase())){
			createDataset();
		}else if(condition.toUpperCase().equals(TYPE.toUpperCase())){
			createDatasetByType();
		}
		
		ChartPanel chartPanel = new ChartPanel(this.createChart(""));
		
		return chartPanel;
	}
}