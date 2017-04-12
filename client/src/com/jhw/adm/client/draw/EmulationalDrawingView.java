package com.jhw.adm.client.draw;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;

import org.jhotdraw.draw.DefaultDrawingView;
import org.jhotdraw.draw.event.FigureAdapter;
import org.jhotdraw.draw.event.FigureEvent;

public class EmulationalDrawingView extends DefaultDrawingView {

	public EmulationalDrawingView() {
		setDoubleBuffered(true);
	}
	
	@Override
	protected void drawBackground(Graphics2D g) {
		Graphics2D g2 = (Graphics2D) g;
		GradientPaint p = new GradientPaint(0, 0, new Color(0x73A3D2), 0,
				getHeight(), new Color(0x1D4B8F));
		Paint oldPaint = g2.getPaint();
		Color oldColor = g2.getColor();
		g2.setPaint(p);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setColor(oldColor);
		g2.setPaint(oldPaint);
	}
	
	@Override
    public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;

		if (getGlassDrawing() != null) {
			getGlassDrawing().draw(g2d);
		}
	}

	@Override
	protected void drawCanvas(Graphics2D g) {
	}
	
	public GlassDrawing getGlassDrawing() {
		return glassDrawing;
	}

	public void setGlassDrawing(GlassDrawing glassDrawing) {
		if (glassDrawing != null) {
			this.glassDrawing = glassDrawing;
			figureAreaHandler = new FigureAreaHandler();
            this.glassDrawing.addFigureListener(figureAreaHandler);
        }
	}

	private class FigureAreaHandler extends FigureAdapter {
		@Override
		public void areaInvalidated(FigureEvent evt) {
			repaintDrawingArea(evt.getInvalidatedArea());
            invalidateDimension();
		}
	}
	
	private GlassDrawing glassDrawing;	
	private FigureAreaHandler figureAreaHandler;
	private static final long serialVersionUID = 1L;
}