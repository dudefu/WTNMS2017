package com.jhw.adm.client.core;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Ellipse2D;
import java.text.SimpleDateFormat;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.jhw.adm.server.entity.warning.RmonCount;

public class PerformanceInfoSPLineChartBuilder implements WarningInfoChartBuilder {

	private TimeSeriesCollection dataset = null;
	private List<RmonCount> rmonCountBeanList = null;
	private static final String SERIES_KEY = "流量";
	
	@Override
	public JFreeChart createChart(String name) {
		
		JFreeChart freeChart = ChartFactory.createXYLineChart("性能数据分析图", "时间", "流量",
				this.dataset, PlotOrientation.VERTICAL, true, true, false);
		XYPlot xyPlot = freeChart.getXYPlot();
		
		setTitleFont(freeChart);
		setValueAxis(xyPlot);
		setDomainAxis(xyPlot);
		setRenderer(xyPlot);
		setLocation(xyPlot);
		setNoDataMessage(xyPlot);
		xyPlot.setBackgroundPaint(Color.WHITE);
		
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
	
	private void setValueAxis(XYPlot xyPlot){//Y
		NumberAxis rangeAxis = (NumberAxis) xyPlot.getRangeAxis();
		rangeAxis.setUpperMargin(0.1);
		rangeAxis.setLowerMargin(0.1);
		rangeAxis.setTickLabelFont(new Font("黑体",15,12));
		rangeAxis.setLabelFont(new Font("黑体",15,12));
		rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
	}
	
	private void setDomainAxis(XYPlot xyPlot) {
		DateAxis domainAxis = new DateAxis();
		domainAxis.setTickLabelFont(new Font("宋体",15,12));
		domainAxis.setLabelFont(new Font("宋体",15,12));
		domainAxis.setLowerMargin(0.1);
		domainAxis.setUpperMargin(0.1);
		domainAxis.setDateFormatOverride(new SimpleDateFormat("yyyyMMdd HH:mm:ss"));//日期轴日期标签的显示格式
		domainAxis.setVerticalTickLabels(true);//设置日期轴上的日期纵向显示
		xyPlot.setDomainAxis(domainAxis);
	}
	
	@SuppressWarnings("deprecation")
	private void setRenderer(XYPlot xyPlot){
		XYSplineRenderer lineRenderer = new XYSplineRenderer();
		lineRenderer.setBaseShapesVisible(true);
		lineRenderer.setDrawOutlines(true);
		lineRenderer.setUseFillPaint(true);
		lineRenderer.setBaseFillPaint(Color.BLACK);
		lineRenderer.setSeriesStroke(0, new BasicStroke(1.5F));
		lineRenderer.setSeriesOutlineStroke(0, new BasicStroke(1.0F));
		lineRenderer.setSeriesShape(0, new Ellipse2D.Double(-2.5D,-2.5D, 5.0D, 5.0D));
		
		lineRenderer.setItemLabelGenerator(new StandardXYItemLabelGenerator());
		lineRenderer.setItemLabelFont(new Font("宋体",Font.PLAIN,12));
		lineRenderer.setItemLabelPaint(Color.BLACK);
		lineRenderer.setItemLabelsVisible(true);
		
		xyPlot.setRenderer(lineRenderer);
	}
	
	private void setLocation(XYPlot xyPlot){
		xyPlot.setDomainAxisLocation(AxisLocation.BOTTOM_OR_LEFT);//Y
		xyPlot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);//X
	}
	
	private void setNoDataMessage(XYPlot xyPlot){
		xyPlot.setNoDataMessage("无性能监控数据");
		xyPlot.setNoDataMessageFont(new Font("宋体", Font.BOLD, 14));
		xyPlot.setNoDataMessagePaint(Color.RED);
	}
	
	@Override
	public void createDataset() {
		this.dataset = new TimeSeriesCollection();
		
		TimeSeries timeSeries = new TimeSeries(SERIES_KEY, Second.class);
		for(RmonCount rmonCount : this.rmonCountBeanList){
			timeSeries.add(new Second(rmonCount.getSampleTime()), rmonCount.getValue());
		}
		dataset.addSeries(timeSeries);
		dataset.setDomainIsPointsInTime(true);
	}
	
	public ChartPanel createChartPanel(List<RmonCount> rmonCountBeanList){
		this.rmonCountBeanList = rmonCountBeanList;
		
		createDataset();
		ChartPanel chartPanel = new ChartPanel(createChart(""));
		return chartPanel;
	}
}