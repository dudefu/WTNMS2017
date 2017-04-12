package com.jhw.adm.client.draw;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.decoration.LineDecoration;

public class SerialLineDecoration implements LineDecoration {

	public SerialLineDecoration() {
		super();
	}

	@Override
	public void draw(Graphics2D g, Figure f, Point2D.Double p1, Point2D.Double p2) {
        AffineTransform transform = new AffineTransform();
        transform.translate(p1.x, p1.y);
        int width = 8;
        int height = 8;
        double x = transform.getTranslateX() - width / 2;
        double y = transform.getTranslateY() - height / 2;
        Rectangle2D.Double ellipse = new Rectangle2D.Double(
        		x, y, 
        		width, height);
        g.fill(ellipse);
	}

	@Override
	public double getDecorationRadius(Figure f) {
		return 0.5;
	}

	@Override
	public java.awt.geom.Rectangle2D.Double getDrawingArea(Figure f,
			java.awt.geom.Point2D.Double p1, java.awt.geom.Point2D.Double p2) {
		AffineTransform transform = new AffineTransform();
        transform.translate(p1.x, p1.y);
        int width = 8;
        int height = 8;
        double x = transform.getTranslateX() - width / 2;
        double y = transform.getTranslateY() - height / 2;
        Rectangle2D.Double ellipse = new Rectangle2D.Double(
        		x, y, 
        		width, height);
		return ellipse;
	}
	
	private static final long serialVersionUID = 1L;
}