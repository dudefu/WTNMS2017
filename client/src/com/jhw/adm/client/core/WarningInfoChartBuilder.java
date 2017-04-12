package com.jhw.adm.client.core;

import java.util.List;

import org.jfree.chart.JFreeChart;

import com.jhw.adm.server.entity.util.TrapCountBean;

public interface WarningInfoChartBuilder {
	
	void createDataset();
	JFreeChart createChart(String name);

}
