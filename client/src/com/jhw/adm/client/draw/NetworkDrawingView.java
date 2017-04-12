package com.jhw.adm.client.draw;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;

import javax.swing.SwingUtilities;

import org.jhotdraw.draw.DefaultDrawingView;
import org.jhotdraw.draw.Figure;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkDrawingView extends DefaultDrawingView {

	public NetworkDrawingView() {
		setDoubleBuffered(true);
	}

	@Override
	protected void drawBackground(Graphics2D g) {
		if (SwingUtilities.isEventDispatchThread()) {
			fillBackground(g);
		} else {
			throw new RuntimeException("NetworkDrawingView.drawBackground invoked not in EventDispatchThread");
		}
	}
	
	//◊¿√Ê±≥æ∞—’…´…Ë÷√
	public void fillBackground(Graphics2D g) {
		Graphics2D g2 = g;
//		GradientPaint p = new GradientPaint(0, 0, new Color(0x73A3D2), 0,
//				getHeight(), new Color(0x1D4B8F));
		GradientPaint p = new GradientPaint(0, 0, new Color(0x3B88B5), 0,
				getHeight(), new Color(0x67A3C7));
		Paint oldPaint = g2.getPaint();
		Color oldColor = g2.getColor();
		g2.setPaint(p);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setColor(oldColor);
		g2.setPaint(oldPaint);

		if (getBackgroundImage() != null) {
			g2.drawImage(getBackgroundImage(),
					(getWidth() - getBackgroundImage().getWidth(null)) / 2,
					(getHeight() - getBackgroundImage().getHeight(null)) / 2,
					getBackgroundImage().getWidth(null), getBackgroundImage()
							.getHeight(null), this);
		}
	}
	
	public void refreshView() {
		if (SwingUtilities.isEventDispatchThread()) {
			for (Figure figure : getDrawing().getChildren()) {
		        repaintDrawingArea(figure.getDrawingArea());
				try {
					Thread.sleep(6);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
			clearView();
			setScaleFactor(getScaleFactor());
		} else {
			throw new RuntimeException("NetworkDrawingView.refreshView invoked not in EventDispatchThread");
		}
	}
	
	public void clearView() {
		if (SwingUtilities.isEventDispatchThread()) {
			setScaleFactor(getScaleFactor());
			repaintDrawingArea(viewToDrawing(getVisibleRect()));
			invalidateDimension();
			revalidate();
			repaint();
			validate();
			invalidate();
		} else {
			throw new RuntimeException("NetworkDrawingView.clearView invoked not in EventDispatchThread");
		}
	}
	
	@Override
	protected void repaintDrawingArea(final Rectangle2D.Double r) {
		if (SwingUtilities.isEventDispatchThread()) {
			super.repaintDrawingArea(r);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					repaintDrawingArea(r);
				}
			});
		}
		
    }

	@Override
	protected void drawCanvas(Graphics2D g) {
	}

	public Image getBackgroundImage() {
		return backgroundImage;
	}

	public void setBackgroundImage(Image backgroundImage) {
		if (SwingUtilities.isEventDispatchThread()) {
			this.backgroundImage = backgroundImage;
		} else {
			throw new RuntimeException("NetworkDrawingView.setBackgroundImage invoked not in EventDispatchThread");
		}
	}

	private Image backgroundImage;
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(NetworkDrawingView.class);
}