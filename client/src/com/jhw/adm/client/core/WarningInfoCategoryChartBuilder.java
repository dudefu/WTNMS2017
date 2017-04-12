package com.jhw.adm.client.core;

import java.awt.Color;
import java.awt.Font;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer3D;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;

public class WarningInfoCategoryChartBuilder implements WarningInfoChartBuilder {

	private DefaultCategoryDataset dataset = null;
	private String[] rowKey = null;
	private double[][] data = null;
	private String[] colMonthKey = null;
	
	@Override
	public JFreeChart createChart(String name) {
		
		JFreeChart freeChart = ChartFactory.createBarChart3D(name, "", "告警数量", this.dataset, PlotOrientation.VERTICAL, true, true, false);
		CategoryPlot categoryPlot = freeChart.getCategoryPlot();
		
		setTitleFont(freeChart);
		setValueAxis(categoryPlot);
		setDomainAxis(categoryPlot);
		setRenderer(categoryPlot);
		setLocation(categoryPlot);
		setNoDataMessage(categoryPlot);
		categoryPlot.setBackgroundPaint(Color.WHITE);
		
		return freeChart;
	}

	private void setTitleFont(JFreeChart freeChart){
		TextTitle title = freeChart.getTitle();
		title.setFont(new Font("宋体",Font.BOLD,12));
		LegendTitle legendTitle = freeChart.getLegend();
		legendTitle.setItemFont(new Font("宋体",10,12));
//		freeChart.getRenderingHints().put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		freeChart.setAntiAlias(false);
		freeChart.setBackgroundPaint(Color.WHITE);
	}
	
	private void setValueAxis(CategoryPlot categoryPlot){//Y
		NumberAxis rangeAxis = (NumberAxis) categoryPlot.getRangeAxis();
		rangeAxis.setUpperMargin(0.1);
		rangeAxis.setLowerMargin(0.1);
		rangeAxis.setTickLabelFont(new Font("黑体",15,12));
		rangeAxis.setLabelFont(new Font("黑体",15,12));
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	}
	
	private void setDomainAxis(CategoryPlot categoryPlot) {
		CategoryAxis domainAxis = categoryPlot.getDomainAxis();
		domainAxis.setTickLabelFont(new Font("宋体",15,12));
		domainAxis.setLabelFont(new Font("宋体",15,12));
		domainAxis.setLowerMargin(0.1);
		domainAxis.setUpperMargin(0.1);
		domainAxis.setCategoryLabelPositions(CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0));
		domainAxis.setCategoryMargin(0.2);
	}
	
	@SuppressWarnings("deprecation")
	private void setRenderer(CategoryPlot categoryPlot){
		BarRenderer3D renderer = new BarRenderer3D();
		renderer.setBaseOutlinePaint(Color.BLACK);
		renderer.setWallPaint(Color.GRAY);
		
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesOutlinePaint(0, Color.BLACK);
		
		renderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		renderer.setItemLabelFont(new Font("宋体",Font.PLAIN,12));
		renderer.setItemLabelPaint(Color.BLACK);
		renderer.setItemLabelsVisible(true);
		
		categoryPlot.setRenderer(renderer);
	}
	
	private void setLocation(CategoryPlot categoryPlot){
		categoryPlot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);//Y
		categoryPlot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);//X
	}
	
	private void setNoDataMessage(CategoryPlot categoryPlot){
		categoryPlot.setNoDataMessage("无告警数据");
		categoryPlot.setNoDataMessageFont(new Font("宋体", Font.BOLD, 14));
		categoryPlot.setNoDataMessagePaint(Color.RED);
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void createDataset() {
		this.dataset = (DefaultCategoryDataset) DatasetUtilities
				.createCategoryDataset(this.rowKey, this.colMonthKey,this.data);
	}
	
	public ChartPanel createChartPanel(double[][] data,String[] colMonthKey,String name,String[] rowKey){
		this.data = data;
		this.colMonthKey = colMonthKey;
		this.rowKey = rowKey;
		
		createDataset();
		ChartPanel chartPanel = new ChartPanel(createChart(name));
		return chartPanel;
	}
}
