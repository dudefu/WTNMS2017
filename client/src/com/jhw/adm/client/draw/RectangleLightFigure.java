package com.jhw.adm.client.draw;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.Action;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.RectangleFigure;
import org.jhotdraw.geom.Geom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RectangleLightFigure extends RectangleFigure implements LightFigure {

	public RectangleLightFigure(double width, double height) {
        this(0, 0, width, height);
    }
    
    public RectangleLightFigure(double x, double y, double width, double height) {
    	super(x, y, width, height);
    	lightColor = Color.GREEN;
    }

    @Override
    protected void drawStroke(Graphics2D g) {
    }
    
    @Override
    protected void drawFill(Graphics2D g) {
		if (turnOn) {
	        Rectangle2D.Double r = getBounds();
	        double grow = AttributeKeys.getPerpendicularFillGrowth(this);
	        Geom.grow(r, grow, grow);
	        Color oldColor = g.getColor();
	        g.setColor(getLightColor());
	        g.fill(r);
	        g.setColor(oldColor);
		}
    }
    
    @Override
	public Rectangle2D.Double getDrawingArea() {
    	READ_LOCK.lock();
    	try {
    		return super.getDrawingArea();
    	} finally {
    		READ_LOCK.unlock();
    	}
    }

    @Override
    protected void invalidate() {
    	WRITE_LOCK.lock();
    	try {
    		LOG.debug("WRITE_LOCK");
	        super.invalidate();
    	} finally {
    		WRITE_LOCK.unlock();
    	}
    }
    
	@Override
	public void turnOff() {
		turnOn = false;
	}

	@Override
	public void turnOn() {
		turnOn = true;
	}

	@Override
	public void setActions(List<Action> actions) {
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		return null;
	}

	@Override
	public Action getDoubleClickAction() {
		return null;
	}

	@Override
	public LightEdit getEdit() {
		return edit;
	}

	@Override
	public void setDoubleClickAction(Action doubleClickAction) {
	}
	
	public void setEdit(LightEdit edit) {
		this.edit = edit;
	}


	public Color getLightColor() {
		return lightColor;
	}

	@Override
	public void changeColor(Color lightColor) {
		this.lightColor = lightColor;
	}

	private Color lightColor;
	private boolean turnOn;
	private LightEdit edit;

    private final ReadWriteLock LOCK = new ReentrantReadWriteLock();
    private final Lock READ_LOCK = LOCK.readLock();
    private final Lock WRITE_LOCK = LOCK.writeLock();
    
    private static final Logger LOG = LoggerFactory.getLogger(RectangleLightFigure.class);
	private static final long serialVersionUID = -3768681997974752702L;
}