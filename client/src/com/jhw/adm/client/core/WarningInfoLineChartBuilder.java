package com.jhw.adm.client.core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;

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
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetUtilities;

public class WarningInfoLineChartBuilder implements WarningInfoChartBuilder {

	private DefaultCategoryDataset dataset = null;
	private static final String[] ROW_KEY = {"性能数据"};
	private double[][] data = null;
	private String[] colMonthKey = null;
	
	@Override
	public JFreeChart createChart(String name) {
		
		JFreeChart freeChart = ChartFactory.createLineChart3D("性能数据分析", "", "数值",
				this.dataset, PlotOrientation.VERTICAL, true, true, false);
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
		LineAndShapeRenderer lineRenderer = (LineAndShapeRenderer) categoryPlot.getRenderer();
		lineRenderer.setBaseShapesVisible(true);
		lineRenderer.setDrawOutlines(true);
		lineRenderer.setUseFillPaint(true);
		lineRenderer.setBaseFillPaint(Color.BLACK);
		lineRenderer.setSeriesStroke(0, new BasicStroke(1.5F));
		lineRenderer.setSeriesOutlineStroke(0, new BasicStroke(1.0F));
		lineRenderer.setSeriesShape(0, new Ellipse2D.Double(-2.5D,-2.5D, 5.0D, 5.0D));
		
		lineRenderer.setItemLabelGenerator(new StandardCategoryItemLabelGenerator());
		lineRenderer.setItemLabelFont(new Font("宋体",Font.PLAIN,12));
		lineRenderer.setItemLabelPaint(Color.BLACK);
		lineRenderer.setItemLabelsVisible(true);
	}
	
	private void setLocation(CategoryPlot categoryPlot){
		categoryPlot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);//Y
		categoryPlot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);//X
	}
	
	private void setNoDataMessage(CategoryPlot categoryPlot){
		categoryPlot.setNoDataMessage("无告警信息");
		categoryPlot.setNoDataMessageFont(new Font("宋体", Font.BOLD, 14));
		categoryPlot.setNoDataMessagePaint(Color.RED);
	}
	
	@SuppressWarnings("static-access")
	@Override
	public void createDataset() {
		this.dataset = (DefaultCategoryDataset) DatasetUtilities
				.createCategoryDataset(this.ROW_KEY, this.colMonthKey,this.data);
	}
	
	public ChartPanel createChartPanel(double[][] data,String[] colMonthKey){
		this.data = data;
		this.colMonthKey = colMonthKey;
		
		createDataset();
		ChartPanel chartPanel = new ChartPanel(createChart(""));
		return chartPanel;
	}
}