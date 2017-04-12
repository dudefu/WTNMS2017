package com.jhw.adm.client.views;

import java.awt.*;

import javax.annotation.PostConstruct;
import javax.swing.*;

import org.springframework.stereotype.Component;


@Component(SeverMonitoringView.ID)
public class SeverMonitoringView extends ViewPart{
	public static final String ID = "severMonitoringView";
	
	JPanel cpuPnl = new JPanel();
	JProgressBar cpuBar = null;
	
	JPanel memoryPnl = new JPanel();
	JProgressBar memoryBar = null;
	
	@PostConstruct
	protected void initialize() {
		this.setTitle("服务器监视器");
		
		cpuBar = new JProgressBar (JProgressBar.VERTICAL, 10, 100);
		cpuBar.setBackground (Color.white);
		cpuBar.setForeground (Color.GREEN);
		cpuBar.setValue (30);
		cpuBar.setString ("cpuBar");
		cpuBar.setPreferredSize(new Dimension(60,170));
		
		memoryBar = new JProgressBar (JProgressBar.VERTICAL, 10, 100);
		memoryBar.setBackground (Color.white);
		memoryBar.setForeground (Color.GREEN);
		memoryBar.setValue (80);
		memoryBar.setString ("memoryBar");
		memoryBar.setPreferredSize(new Dimension(60,170));
		
		cpuPnl.setBorder(BorderFactory.createTitledBorder("CPU使用"));
		cpuPnl.add(cpuBar);
		cpuPnl.setPreferredSize(new Dimension(100,230));
		
		memoryPnl.setBorder(BorderFactory.createTitledBorder("内存使用率"));
		memoryPnl.add(memoryBar);
		memoryPnl.setPreferredSize(new Dimension(100,230));
		
		JPanel container = new JPanel();
		container.setLayout(new SpringLayout());
		container.add(cpuPnl);
		container.add(memoryPnl);
		SpringUtilities.makeCompactGrid(container, 2, 1, 6, 6, 6, 10);
		
		this.setForeground(Color.red);
		
		this.setLayout(new FlowLayout(FlowLayout.LEADING));
		this.add(container);
		
		JPanel panel = new JPanel();
	}
	
	public void paintComponent(Graphics g){
		g.setColor(Color.GRAY);
		int x1 = 30;
		for (int i = 0 ; i <= 20; i++){
			g.drawLine(200, x1, 700, x1);
			x1=x1+10;
		}
		
		int y1 = 200;
		for (int i = 0 ; i <= 50; i++){
			g.drawLine(y1, 30, y1, 230);
			y1=y1+10;
		}
		
		g.setColor(new Color(0,189,0));
		g.drawLine(200, 230, 210, 220);
		g.drawLine(210, 220, 225, 205);
		g.drawLine(225, 205, 240, 186);
		g.drawLine(240, 186, 262, 160);
		g.drawLine(262, 160 ,286, 141);
		g.drawLine(286, 141 ,305, 190);
		g.drawLine(305, 190 ,315, 180);
		
		
		g.setColor(Color.GRAY);
		int x2 = 270;
		for (int i = 0 ; i <= 20; i++){
			g.drawLine(200, x2, 700, x2);
			x2=x2+10;
		}
		

		int y2 = 200;
		for (int i = 0 ; i <= 50; i++){
			g.drawLine(y2, 270, y2, 470);
			y2=y2+10;
		}
		
		g.setColor(new Color(0,189,0));
		g.drawLine(200, 320, 210, 310);
		g.drawLine(210, 310, 220, 304);
		g.drawLine(220, 304, 228, 301);
		g.drawLine(228, 301, 235, 296);
		g.drawLine(235, 296, 239, 292);
		g.drawLine(239, 292, 242, 297);
		g.drawLine(242, 297, 250, 300);
		g.drawLine(250, 300, 253, 302);
		g.drawLine(253, 302, 255, 305);
		g.drawLine(255, 305, 265, 303);
		g.drawLine(265, 303, 275, 304);
		g.drawLine(275, 304, 285, 300);
		g.drawLine(285, 300, 295, 296);
		g.drawLine(295, 296, 305, 302);
		g.drawLine(305, 302, 315, 308);
	}
}
