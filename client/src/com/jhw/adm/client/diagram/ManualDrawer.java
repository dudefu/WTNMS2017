package com.jhw.adm.client.diagram;

import static org.jhotdraw.draw.AttributeKeys.STROKE_INNER_WIDTH_FACTOR;
import static org.jhotdraw.draw.AttributeKeys.STROKE_TYPE;

import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.LineConnectionFigure;
import org.jhotdraw.draw.layouter.LocatorLayouter;
import org.jhotdraw.draw.liner.CurvedLiner;
import org.jhotdraw.draw.liner.ElbowLiner;
import org.jhotdraw.draw.liner.SlantedLiner;
import org.jhotdraw.draw.locator.BezierLabelLocator;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.util.Images;

import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.draw.AlarmFigure;
import com.jhw.adm.client.draw.CircleLineFigure;
import com.jhw.adm.client.draw.EquipmentFigure;
import com.jhw.adm.client.draw.LabeledLinkFigure;
import com.jhw.adm.client.draw.MultiwayTree;
import com.jhw.adm.client.draw.NetworkDrawing;
import com.jhw.adm.client.draw.OrthogonalLiner;
import com.jhw.adm.client.draw.PowerLiner;
import com.jhw.adm.client.draw.ToolTipFigure;
import com.jhw.adm.client.draw.TreeLayouter;

public class ManualDrawer {
	
	public ManualDrawer(NetworkDrawing drawing, ImageRegistry imageRegistry) {
		this.drawing = drawing;
		this.imageRegistry = imageRegistry;
	}
	
	public void drawManualLayout() {
		BufferedImage frontEndImage = Images.toBufferedImage(imageRegistry.getImageIcon(
				NetworkConstants.FRONT_END).getImage());
		BufferedImage ieth800Image = Images.toBufferedImage(imageRegistry.getImageIcon(
				NetworkConstants.IETH8026).getImage());
		BufferedImage ieth802Image = Images.toBufferedImage(imageRegistry.getImageIcon(
				NetworkConstants.IETH802).getImage());
		BufferedImage ieth804Image = Images.toBufferedImage(imageRegistry.getImageIcon(
				NetworkConstants.IETH804).getImage());
		BufferedImage ieth8008_1Image = Images.toBufferedImage(imageRegistry.getImageIcon(
				NetworkConstants.IETH8022).getImage());
		BufferedImage ieth8008_2Image = Images.toBufferedImage(imageRegistry.getImageIcon(
				NetworkConstants.IETH8018).getImage());
		BufferedImage subnetImage = Images.toBufferedImage(imageRegistry.getImageIcon(
				NetworkConstants.SUBNET).getImage());
		BufferedImage carrierImage = Images.toBufferedImage(imageRegistry.getImageIcon(
				NetworkConstants.CARRIER).getImage());
		//
		final EquipmentFigure frontEnd = new EquipmentFigure(frontEndImage);
		drawing.add(frontEnd);
        frontEnd.setText("sz01");
		AffineTransform at = new AffineTransform();
		at.translate(25, 25);
		frontEnd.transform(at);	
		
		final EquipmentFigure equipment800 = new EquipmentFigure(ieth800Image);
		drawing.add(equipment800);		
        equipment800.setText("800");
		at = new AffineTransform();
		at.translate(25, 150);
		equipment800.transform(at);
		
		final EquipmentFigure equipment802 = new EquipmentFigure(ieth802Image);
		drawing.add(equipment802);	
        equipment802.setText("802");
        at = new AffineTransform();
		at.translate(150, 150);
		equipment802.transform(at);

		final ToolTipFigure toolTipFigure4 = new ToolTipFigure("上线");
		toolTipFigure4.setFillColor(Color.CYAN);
		toolTipFigure4.stick(equipment802);
		drawing.add(toolTipFigure4);

		final EquipmentFigure equipment804 = new EquipmentFigure(ieth804Image);
		drawing.add(equipment804);
        equipment804.setText("804");
        at = new AffineTransform();
		at.translate(350, 50);
		equipment804.transform(at);

		final ToolTipFigure toolTipFigure3 = new ToolTipFigure("重启");
		toolTipFigure3.setFillColor(Color.YELLOW);
		toolTipFigure3.stick(equipment804);
		drawing.add(toolTipFigure3);

		final EquipmentFigure equipment8008_1 = new EquipmentFigure(ieth8008_1Image);
		drawing.add(equipment8008_1);
        equipment8008_1.setText("8008_1");
        at = new AffineTransform();
		at.translate(550, 150);
		equipment8008_1.transform(at);

		final ToolTipFigure toolTipFigure2 = new ToolTipFigure("超时");
		toolTipFigure2.setFillColor(Color.ORANGE);
		toolTipFigure2.stick(equipment8008_1);
		drawing.add(toolTipFigure2);

		final EquipmentFigure equipment8008_2 = new EquipmentFigure(ieth8008_2Image);
		drawing.add(equipment8008_2);
        equipment8008_2.setText("8008_2");
        at = new AffineTransform();
		at.translate(750, 50);
		equipment8008_2.transform(at);

		final ToolTipFigure toolTipFigure = new ToolTipFigure("断线");
		toolTipFigure.stick(equipment8008_2);
		drawing.add(toolTipFigure);

		final EquipmentFigure subnetFigure = new EquipmentFigure(subnetImage);
		drawing.add(subnetFigure);
		subnetFigure.setText("深圳");
        at = new AffineTransform();
		at.translate(950, 150);
		subnetFigure.transform(at);

		final EquipmentFigure carrierFigure = new EquipmentFigure(carrierImage);
		drawing.add(carrierFigure);
        at = new AffineTransform();
		at.translate(1150, 150);
		carrierFigure.transform(at);
	
        final ConnectionFigure cf = new LineConnectionFigure();
        cf.setLiner(new SlantedLiner(40));
        cf.setStartConnector(frontEnd.findConnector(Geom.center(frontEnd
				.getBounds()), cf));
        cf.setEndConnector(equipment800.findConnector(Geom.center(equipment800.getBounds()),
        		cf));
		drawing.add(cf);
		
        final ConnectionFigure cf1 = new LineConnectionFigure();
		cf1.setLiner(new PowerLiner());
		cf1.setStartConnector(equipment800.findConnector(Geom.center(equipment800
				.getBounds()), cf1));
		cf1.setEndConnector(equipment802.findConnector(Geom.center(equipment802.getBounds()),
				cf1));
		drawing.add(cf1);
		
        final LabeledLinkFigure cf2 = new LabeledLinkFigure();
        cf2.setLayouter(new LocatorLayouter());
        cf2.setLiner(new ElbowLiner());
        cf2.setStartConnector(equipment802.findConnector(Geom.center(equipment802
				.getBounds()), cf2));
        cf2.setEndConnector(equipment804.findConnector(Geom.center(equipment804.getBounds()),
        		cf2));
    
        
        final AlarmFigure alarmFigure = new AlarmFigure("负载");
        
        alarmFigure.set(LocatorLayouter.LAYOUT_LOCATOR, new BezierLabelLocator(0.6, Math.PI + Math.PI / 4, 8));
        cf2.add(alarmFigure);
        cf2.layout();
        
		drawing.add(cf2);
		
        final ConnectionFigure cf3 = new LineConnectionFigure();
        cf3.setLiner(new CurvedLiner());
        cf3.setStartConnector(equipment804.findConnector(Geom.center(equipment804
				.getBounds()), cf3));
        cf3.setEndConnector(equipment8008_1.findConnector(Geom.center(equipment8008_1.getBounds()),
        		cf3));
		drawing.add(cf3);
		
        final ConnectionFigure cf4 = new LineConnectionFigure();
        cf4.setLiner(new OrthogonalLiner());
        cf4.setStartConnector(equipment8008_1.findConnector(Geom.center(equipment8008_1
				.getBounds()), cf4));
        cf4.setEndConnector(equipment8008_2.findConnector(Geom.center(equipment8008_2.getBounds()),
        		cf4));
		drawing.add(cf4);
		
        final ConnectionFigure cf5 = new LineConnectionFigure();
        cf5.set(AttributeKeys.STROKE_DASHES, new double[]{4d});
        cf5.setStartConnector(equipment8008_2.findConnector(Geom.center(equipment8008_2
				.getBounds()), cf5));
        cf5.setEndConnector(subnetFigure.findConnector(Geom.center(subnetFigure.getBounds()),
        		cf5));
		drawing.add(cf5);
		
        final ConnectionFigure cf6 = new LineConnectionFigure();
        cf6.set(STROKE_TYPE, AttributeKeys.StrokeType.DOUBLE);
        cf6.set(STROKE_INNER_WIDTH_FACTOR, 3d);
        cf6.setStartConnector(subnetFigure.findConnector(Geom.center(subnetFigure
				.getBounds()), cf6));
        cf6.setEndConnector(carrierFigure.findConnector(Geom.center(carrierFigure.getBounds()),
        		cf6));
		drawing.add(cf6);
	}
	
	public void drawRingLayout() {        
        int count = 8;		
//		final CircleLineFigure circleLine = new CircleLineFigure(1050 - 20, 600 - 35, 150, 100);
        final CircleLineFigure circleLine = new CircleLineFigure(600, 600, 200, 200);
        drawing.add(circleLine);
		BufferedImage ieth802Image = Images.toBufferedImage(imageRegistry.getImageIcon(
				NetworkConstants.IETH802).getImage());
        AffineTransform at = null;
		for (int index = 0; index < count; index++) {
        	double angle = 2d*Math.PI/count*index;
        	double radius = 100;
        	double tox = radius * Math.sin(angle);
        	double toy = radius * Math.cos(angle);
        	
        	EquipmentFigure ef = new EquipmentFigure(ieth802Image);
        	ef.setText("192.168.16." + Integer.toString(index + 1));
    		drawing.add(ef);
            
	        at = new AffineTransform();
			at.translate(tox + 600 + 100, toy + 600 + 100);
			ef.transform(at);
			
//			final ConnectionFigure cf7 = new LineConnectionFigure();
//	        cf7.setStartConnector(ef.findConnector(Geom.center(ef
//					.getBounds()), cf7));
//	        cf7.setEndConnector(circleLine.findConnector(Geom.center(circleLine.getBounds()),
//	        		cf7));
//			drawing.add(cf7);
        }
	}
	public void drawTreeLayout() {
		MultiwayTree tree = new MultiwayTree();
		MultiwayTree.Node a0 = new MultiwayTree.Node("192.168.19.1");
		
		MultiwayTree.Node b0 = new MultiwayTree.Node("192.168.19.2");
		MultiwayTree.Node b1 = new MultiwayTree.Node("192.168.19.3");
		MultiwayTree.Node b2 = new MultiwayTree.Node("192.168.19.4");
		
		MultiwayTree.Node c0 = new MultiwayTree.Node("192.168.19.5");
		MultiwayTree.Node c1 = new MultiwayTree.Node("192.168.19.6");
		MultiwayTree.Node c2 = new MultiwayTree.Node("192.168.19.7");
		MultiwayTree.Node c3 = new MultiwayTree.Node("192.168.19.8");
		
		MultiwayTree.Node d0 = new MultiwayTree.Node("192.168.19.9");
		MultiwayTree.Node d1 = new MultiwayTree.Node("192.168.19.10");
		MultiwayTree.Node d2 = new MultiwayTree.Node("192.168.19.11");
		
		MultiwayTree.Node e0 = new MultiwayTree.Node("192.168.19.12");
		MultiwayTree.Node e1 = new MultiwayTree.Node("192.168.19.13");
		MultiwayTree.Node e2 = new MultiwayTree.Node("192.168.19.14");
		
		MultiwayTree.Node f0 = new MultiwayTree.Node("192.168.19.15");
		MultiwayTree.Node f1 = new MultiwayTree.Node("192.168.19.16");
		MultiwayTree.Node f2 = new MultiwayTree.Node("192.168.19.17");
		
		MultiwayTree.Node g0 = new MultiwayTree.Node("192.168.19.18");
		MultiwayTree.Node g1 = new MultiwayTree.Node("192.168.19.19");
		MultiwayTree.Node g2 = new MultiwayTree.Node("192.168.30.1");				
		MultiwayTree.Node g3 = new MultiwayTree.Node("192.168.30.2");
		MultiwayTree.Node g4 = new MultiwayTree.Node("192.168.30.3");
		MultiwayTree.Node g5 = new MultiwayTree.Node("192.168.30.4");
		MultiwayTree.Node g6 = new MultiwayTree.Node("192.168.19.25");
		MultiwayTree.Node g7 = new MultiwayTree.Node("192.168.19.26");
		
		MultiwayTree.Node h0 = new MultiwayTree.Node("192.168.19.28");
		MultiwayTree.Node h1 = new MultiwayTree.Node("192.168.19.29");
		
		tree.addNode(a0);
//		tree.addNode(a0).addChild(b0).addChild(b1).addChild(b2);
		a0.addChild(b0);
		a0.addChild(b1);
		a0.addChild(b2);
		
		b0.addChild(c0);
		b0.addChild(c1);
		b0.addChild(c2);
		b0.addChild(c3);
		
		b1.addChild(d0);
		b1.addChild(d1);
		b1.addChild(d2);
		
		b2.addChild(e0);
		b2.addChild(e1);
		b2.addChild(e2);
		
		c2.addChild(h0);
		c2.addChild(h1);
		
		d0.addChild(f0);
		d0.addChild(f1);
		d0.addChild(f2);
		
		d1.addChild(g0);
		d1.addChild(g1);
		d1.addChild(g2);
		d1.addChild(g3);
		d1.addChild(g4);
		d1.addChild(g5);
		d1.addChild(g6);
		d1.addChild(g7);
		
		TreeLayouter treeLayout = new TreeLayouter(tree);
		treeLayout.setInitY(200);
		treeLayout.layout();

		BufferedImage switcher = Images.toBufferedImage(imageRegistry.getImageIcon(
				NetworkConstants.IETH802).getImage());
		drawTree(a0, switcher, null);
	}
	
	protected void drawTree(MultiwayTree.Node root, BufferedImage image, EquipmentFigure parent) {
		final EquipmentFigure equipment = new EquipmentFigure(image);
		equipment.setText(root.getName());
		AffineTransform at = new AffineTransform();
		at.translate(root.getX(), root.getY());
		equipment.transform(at);
		drawing.add(equipment);

		if (parent != null) {
			LabeledLinkFigure link = new LabeledLinkFigure();
//			link.setLiner(new PowerLiner());
//			link.setLiner(new OrthogonalLiner());
//			link.setLiner(new SlantedLiner());
//			link.set(AttributeKeys.STROKE_WIDTH, 1.5d);
	        link.setEndConnector(parent.findConnector(null, null));
	        link.setStartConnector(equipment.findConnector(Geom.center(equipment.getBounds()),
	        		link));
			drawing.add(link);
		}
		
		for (MultiwayTree.Node child : root.getChildren()) {
			drawTree(child, image, equipment);
		}
	}

	protected void drawStarLayout() {
		BufferedImage ieth800Image = Images.toBufferedImage(imageRegistry.getImageIcon(
				NetworkConstants.IETH8018).getImage());
				
		figureGuidIndex = new HashMap<String, Figure>();
		figureAddressIndex = new HashMap<String, Figure>();		
		
		EquipmentFigure startFigure = new EquipmentFigure(ieth800Image);
		AffineTransform at = new AffineTransform();
		double centerX = 120;
		double centerY = 700;
		at.translate(centerX, centerY);
		startFigure.transform(at);
		startFigure.setText("192.168.1.1");
		drawing.add(startFigure);
		int count = 8;
		for (int index = 0; index < count; index++) {
			String ipValue = "192.168.15." + Integer.toString(index);

			EquipmentFigure endFigure = new EquipmentFigure(ieth800Image);
			endFigure.setText(ipValue);
			figureAddressIndex.put(ipValue, endFigure);
			//
			double angle = (360d / 180d)*Math.PI/count*index;
        	double radius = 100;
        	
        	if (index == 2) {
        		radius += 200;
        	}
        	double tox = radius * Math.sin(angle) + centerX;
        	double toy = radius * Math.cos(angle) + centerY;
//        	System.out.println(String.format("[%s]tox: %s; toy: %s; angle: %s", ipValue, tox, toy, angle));
    		at = new AffineTransform();
			at.translate(tox, toy);
			endFigure.transform(at);
			
			if (index == 2) {
				roundFigure(endFigure, true);
			}
			
			final LabeledLinkFigure lineFigure = new LabeledLinkFigure();
			
			lineFigure.setStartConnector(startFigure.findConnector(Geom.center(startFigure
					.getBounds()), lineFigure));
			lineFigure.setEndConnector(endFigure.findConnector(Geom.center(endFigure.getBounds()),
					lineFigure));
			
			drawing.add(endFigure);
			drawing.add(lineFigure);
        }
	}
	
	protected void roundFigure(EquipmentFigure startFigure, boolean flow) {
		BufferedImage ieth800Image = Images.toBufferedImage(imageRegistry.getImageIcon(
				NetworkConstants.IETH8018).getImage());
		double centerX = startFigure.getBounds().x;
		double centerY = startFigure.getBounds().y;
		int count = 12;
		for (int index = 0; index < count; index++) {
			String ipValue = "192.168.28." + Integer.toString(index);

			EquipmentFigure endFigure = new EquipmentFigure(ieth800Image);
			endFigure.setText(ipValue);
			//
			double angle = 2d*Math.PI/count*index;
        	double radius = 100;
        	
//        	if (index == 6 && flow) {
//        		radius += 200;
//        	}
        	
        	double tox = radius * Math.sin(angle) + centerX;
        	double toy = radius * Math.cos(angle) + centerY;
//        	System.out.println(String.format("[%s]tox: %s; toy: %s", ipValue, tox, toy));
        	AffineTransform at = new AffineTransform();
			at.translate(tox, toy);
			endFigure.transform(at);
//			if (index == 6 && flow) {
//				roundFigure(endFigure, false);
//        	}
			final LabeledLinkFigure lineFigure = new LabeledLinkFigure();
			
			lineFigure.setStartConnector(startFigure.findConnector(Geom.center(startFigure
					.getBounds()), lineFigure));
			lineFigure.setEndConnector(endFigure.findConnector(Geom.center(endFigure.getBounds()),
					lineFigure));
			
			drawing.add(endFigure);
			drawing.add(lineFigure);
        }
	}

	private Map<String, Figure> figureAddressIndex;
	private Map<String, Figure> figureGuidIndex;
	private NetworkDrawing drawing;
	private ImageRegistry imageRegistry;
}