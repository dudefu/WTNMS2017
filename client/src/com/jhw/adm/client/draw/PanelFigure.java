package com.jhw.adm.client.draw;

import static org.jhotdraw.draw.AttributeKeys.FILL_COLOR;
import static org.jhotdraw.draw.AttributeKeys.STROKE_COLOR;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.Action;

import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.GraphicalCompositeFigure;
import org.jhotdraw.draw.ImageFigure;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.NullHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 设备仿真图形
 */
public class PanelFigure extends GraphicalCompositeFigure implements NodeFigure {

	public PanelFigure() {
		this(null);
	}
	
    public PanelFigure(BufferedImage bufferedImage) {
    	set(NetworkKeys.UNDRAGABLE, true);
    	imageFigure = new PartFigure();
		imageFigure.set(FILL_COLOR, null);
		imageFigure.set(STROKE_COLOR, null);
    	setBufferedImage(bufferedImage);
		add(imageFigure);
		listOfAction = new ArrayList<Action>();
    }
    
    @Override
    public Rectangle2D.Double getDrawingArea() {
    	READ_LOCK.lock();
    	try {
    		LOG.debug("READ_LOCK");
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
	public Figure findFigureInside(Point2D.Double p) {
    	return super.findFigureInside(p);
    }

    @Override
    public Collection<Handle> createHandles(int detailLevel) {
        List<Handle> handles = new LinkedList<Handle>();
        switch (detailLevel) {
            case -1:
//                handles.add(new BoundsOutlineHandle(imageFigure, false, true));
                break;
            case 0:
            	NullHandle.addLeadHandles(this, handles);
                break;
        }
        return handles;
    }
    
    @Override
    public boolean handleMouseClick(Point2D.Double p, MouseEvent evt, DrawingView view) {
    	Action action = getDoubleClickAction();
    	String DefaulActionCommand = "DoubleClick";
    	if (action == null) return false;
    	
    	Object actionCommandValue = action.getValue(Action.ACTION_COMMAND_KEY);
		String actionCommand = actionCommandValue == null ? DefaulActionCommand
				: actionCommandValue.toString();
    	ActionEvent event = new ActionEvent(this,
				ActionEvent.ACTION_PERFORMED, actionCommand, EventQueue
						.getMostRecentEventTime(), evt.getModifiersEx());
    	action.actionPerformed(event);
        return true;
    }
    
    @Override
	public Collection<Action> getActions(Point2D.Double p) {
    	return Collections.unmodifiableList(listOfAction);
    }
    
    public void addAction(Action action) {
    	listOfAction.add(action);
    }

	@Override
	public void setActions(List<Action> actions) {
		listOfAction = actions;
	}
	
    @Override
    public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
    	double x = Math.min(anchor.x, lead.x);
        double y = Math.min(anchor.y, lead.y);
        Rectangle2D.Double bounds = new Rectangle2D.Double();
        
        AffineTransform at = new AffineTransform();
        if (bufferedImage == null) return;
		double width = bufferedImage.getWidth();
		double height = bufferedImage.getHeight();
		Point2D.Double imageAnchor = (Point2D.Double)at.transform(new Point2D.Double(x, y), new Point2D.Double(x, y));

        bounds.x = imageAnchor.x;
        bounds.y = imageAnchor.y;
        bounds.width = width;
        bounds.height = height;
        imageFigure.setBounds(bounds);
        invalidate();
    }

    @Override
    public PanelFigure clone() {
    	PanelFigure that = new PanelFigure(bufferedImage);
    	that.setBounds(
    			new Point2D.Double(getBounds().x, getBounds().y),
    			new Point2D.Double(
    						bufferedImage.getWidth(), bufferedImage.getHeight()));
        return that;
    }
    
    public double getEquipmentX() {
    	return imageFigure.getBounds().x;
    }
    
    public double getEquipmentY() {
    	return imageFigure.getBounds().y;
    }
    
    public int getEquipmentWidth() {
    	return bufferedImage == null ? 0 : bufferedImage.getWidth();
    }
    
    public int getEquipmentHeight() {
    	return bufferedImage == null ? 0 : bufferedImage.getHeight();
    }

    @Override
	public int getLayer() {
        return LAYER;
    }

	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}

    public void setBufferedImage(BufferedImage bufferedImage) {
    	if (bufferedImage == null) return;
    	
    	this.bufferedImage = bufferedImage;
		imageFigure.setBufferedImage(bufferedImage);
		AffineTransform at = new AffineTransform();
		double x = getBounds().x;
		double y = getBounds().y;
		double width = bufferedImage.getWidth();
		double height = bufferedImage.getHeight();
		Point2D.Double anchor = (Point2D.Double)at.transform(new Point2D.Double(x, y), new Point2D.Double(x, y));
		imageFigure.setBounds(anchor, 
				new Point2D.Double(width, height));
    }

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		return null;
	}

	public Action getDoubleClickAction() {
		return doubleClickAction;
	}

	public void setDoubleClickAction(Action doubleClickAction) {
		this.doubleClickAction = doubleClickAction;
	}

	@Override
	public PanelEdit getEdit() {
		return edit;
	}

	public void setEdit(PanelEdit edit) {
		this.edit = edit;
	}
	
	private PanelEdit edit;
	private Action doubleClickAction;
    private List<Action> listOfAction;

    private final ReadWriteLock LOCK = new ReentrantReadWriteLock();
    private final Lock READ_LOCK = LOCK.readLock();
    private final Lock WRITE_LOCK = LOCK.writeLock();
    
    private final ImageFigure imageFigure;
    private BufferedImage bufferedImage;
	private static final int LAYER = -1;
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(PanelFigure.class);
}