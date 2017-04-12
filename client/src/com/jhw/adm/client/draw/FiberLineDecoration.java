package com.jhw.adm.client.draw;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.decoration.LineDecoration;

public class FiberLineDecoration implements LineDecoration {

	public FiberLineDecoration() {
		super();
	}

	@Override
	public void draw(Graphics2D g, Figure f, Point2D.Double p1, Point2D.Double p2) {
		Path2D.Double path = new Path2D.Double();
        path.moveTo(30, -30);
        path.lineTo(-30, 30);
        
        
        AffineTransform transform = new AffineTransform();
        transform.translate(p1.x, p1.y);
        transform.rotate(Math.atan2(p1.x - p2.x, p2.y - p1.y));
        path.transform(transform);

        int width = 20;
        Ellipse2D.Double ellipse = new Ellipse2D.Double(
        		transform.getTranslateX() - width /  2, transform.getTranslateY() - width /  2, 
        		width, width);
        g.draw(ellipse);
        
//        g.draw(path);
	}

	@Override
	public double getDecorationRadius(Figure f) {
		return 0.5;
	}

	@Override
	public java.awt.geom.Rectangle2D.Double getDrawingArea(Figure f,
			java.awt.geom.Point2D.Double p1, java.awt.geom.Point2D.Double p2) {
		Path2D.Double path = new Path2D.Double();
        path.moveTo(30, -30);
        path.lineTo(-30, 30);
        
        AffineTransform transform = new AffineTransform();
        transform.translate(p1.x, p1.y);
        transform.rotate(Math.atan2(p1.x - p2.x, p2.y - p1.y));
        path.transform(transform);
        
		Rectangle2D b = path.getBounds2D();
		Rectangle2D.Double area = new Rectangle2D.Double(b.getX(), b.getY(), b.getWidth(), b.getHeight());
		return area;
	}
	
	private static final long serialVersionUID = 1L;
}