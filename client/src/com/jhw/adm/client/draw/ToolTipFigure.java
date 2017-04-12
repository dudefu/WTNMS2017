package com.jhw.adm.client.draw;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Action;

import org.jhotdraw.draw.AbstractAttributedFigure;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.event.FigureAdapter;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.handle.BoundsOutlineHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.NullHandle;
import org.jhotdraw.geom.Geom;

/**
 * 工具提示图形，用于显示告警信息
 */
public class ToolTipFigure extends AbstractAttributedFigure {

    public ToolTipFigure(String text) {
        this(text, 0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public ToolTipFigure(String text, double x, double y, double width, double height) {
    	this.text = text;
    	fillColor = Color.RED;
    	severities = new ArrayList<Integer>();
    	set(NetworkKeys.UNDRAGABLE, true);
    	setConnectable(false);
    	createArea(text, x, y, width, height);
    }
    
    protected void createArea(String text, double x, double y, double width, double height) {
		int charCount = text.toCharArray().length + 2;
    	roundrect = new RoundRectangle2D.Double(x, y, charWidth * charCount, height, DEFAULT_ARC, DEFAULT_ARC);
        
        double grow = AttributeKeys.getPerpendicularDrawGrowth(this);
        roundrect.x -= grow;
        roundrect.y -= grow;
        roundrect.width += grow * 2;
        roundrect.height += grow * 2;
        roundrect.arcwidth += grow * 2;
        roundrect.archeight += grow * 2;
        
        int dx = (int)roundrect.x;
        int dy = (int)roundrect.y + (int)roundrect.height;
        int offset = (int)(roundrect.width * 0.1);
        area = new Area(roundrect);
        Polygon polygon = new Polygon();
		polygon.addPoint(dx + offset + 2, dy);
		polygon.addPoint(dx +  offset + 10, dy);
		polygon.addPoint(dx + offset + 6, dy + 6);
		area.add(new Area(polygon));
    }
    
    protected void drawFill(Graphics2D g) {
    	Area r = (Area) area.clone();
        
        if (r.getBounds().width > 0 && r.getBounds().height > 0) {
        	Color oldColor = g.getColor();
        	g.setColor(getFillColor());
            g.fill(area);
    		g.setColor(oldColor); 
        }
    }

    protected void drawStroke(Graphics2D g) {
    	Area r = (Area) area.clone();
        
        if (r.getBounds().width > 0 && r.getBounds().height > 0) {
        	Color oldColor = g.getColor();
        	g.setColor(Color.BLACK);
            g.draw(area);
    		g.setColor(oldColor); 
        }
    }
    
    protected void drawText(Graphics2D g) {
    	String drawText = getText();
    	if (drawText == null || drawText.length() == 0) return;
    	
    	int areaX = area.getBounds().x;
    	int areaY = area.getBounds().y;
    	int areaWidth = (int)roundrect.width;
    	int areaHeight = (int)roundrect.height;
    	int stringWidth = g.getFontMetrics().stringWidth(drawText);
    	int stringHeight = g.getFontMetrics().getHeight();
    	int x = (areaWidth - stringWidth) / 2;
    	int y = (areaHeight - stringHeight + 13);
    	g.drawString(drawText, x + areaX, y + areaY);
    }
    
    @Override
    public Rectangle2D.Double getBounds() {
        return (Rectangle2D.Double) area.getBounds2D();
    }

    @Override
    public Rectangle2D.Double getDrawingArea() {
        Rectangle2D.Double r = (Rectangle2D.Double) area.getBounds2D();
        double grow = AttributeKeys.getPerpendicularHitGrowth(this) + 2;
        Geom.grow(r, grow, grow);

        return r;
    }
    
    @Override
    public boolean contains(Point2D.Double p) {
    	Area r = (Area) area.clone();
        return r.contains(p);
    }
    
    @Override
    public boolean handleMouseClick(Point2D.Double p, MouseEvent evt, DrawingView view) {
    	//evt.getClickCount() >= 2 && evt.getButton() == MouseEvent.BUTTON1;
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
    public Collection<Handle> createHandles(int detailLevel) {
        List<Handle> handles = new LinkedList<Handle>();
        switch (detailLevel) {
            case -1:
            	handles.add(new BoundsOutlineHandle(this, false, true));
                break;
            case 0:
            	NullHandle.addLeadHandles(this, handles);
//            	handles.add(new CloseHandle(this));
                break;
        }
        return handles;
    }
    
    public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
    	double x = Math.min(anchor.x, lead.x);
        double y = Math.min(anchor.y, lead.y);
        double width = DEFAULT_WIDTH;
        double height = DEFAULT_HEIGHT;

    	createArea(getText(), x, y, width, height);
    }

    /**
     * 转换、移动图形
     * @param tx 转换的数据
     * @see AffineTransform
     */
    @Override
    public void transform(AffineTransform tx) {
        Point2D.Double anchor = getStartPoint();
        Point2D.Double lead = getEndPoint();
        setBounds(
                (Point2D.Double) tx.transform(anchor, anchor),
                (Point2D.Double) tx.transform(lead, lead));
    }

    @Override
    public void restoreTransformTo(Object geometry) {
    	super.restoreAttributesTo(geometry);
    }

    public Object getTransformRestoreData() {
        return area.clone();
    }

    public ToolTipFigure clone() {
    	ToolTipFigure that = (ToolTipFigure) super.clone();
        that.area = (Area)this.area.clone();
        return that;
    }
    
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		int areaX = area.getBounds().x;
    	int areaY = area.getBounds().y;
    	int areaWidth = (int)roundrect.width;
    	int areaHeight = (int)roundrect.height;
    	
    	createArea(getText(), areaX, areaY, areaWidth, areaHeight);
    	fireFigureChanged();
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	public Figure getStickedFigure() {
		return stickedFigure;
	}

	public void stick(Figure stickedFigure) {
		if (stickedFigure != null) {
			stickedFigure.addFigureListener(new FigureAdapter() {
				@Override
				public void figureChanged(FigureEvent e) {
					stickedFigureChanged(e);
				}
			});
		}
		this.stickedFigure = stickedFigure;
		changeLocation(stickedFigure);
	}
	
	private void stickedFigureChanged(FigureEvent e) {
		changeLocation(e.getFigure());
	}
	
	private void changeLocation(Figure figure) {		
		double x = figure.getBounds().x;
		double y = figure.getBounds().y;
		double width = figure.getBounds().getWidth();
		Rectangle2D.Double bounds = new Rectangle2D.Double();
        
        bounds.x = x + width / 2;
        bounds.y = y - getBounds().getHeight();
        bounds.width = getBounds().getWidth();
        bounds.height = getBounds().getHeight();
        bounds.x = bounds.x > 0 ? bounds.x : 0;
        bounds.y = bounds.y > 0 ? bounds.y : 0;
        willChange();
        setBounds(bounds);
        changed();
	}

    public int getLayer() {
        return LAYER;
    }

	public Action getDoubleClickAction() {
		return doubleClickAction;
	}

	public void setDoubleClickAction(Action doubleClickAction) {
		this.doubleClickAction = doubleClickAction;
	}

	/**
	 * 返回级别最高的告警
	 */
	public int getSeverity() {
		int severity = -1;
		sortSeverity();
		if (severityData.length > 0) {
			severity = severityData[0];
		}
		return severity;
	}
	
	private void sortSeverity() {
		severityData = new int[severities.size()];
		
		for (int i = 0; i < severities.size(); i++) {
			severityData[i] = severities.get(i);
		}
		
		selectionSort(severityData);
		severities.clear();
	}
	
	private void selectionSort(int[] data) {
		for (int out = 0; out < data.length; out++) {
			int min = out;
			for (int in = out + 1; in < data.length; in++) {
				if (data[in] < data[min]) {
					min = in;
				}
			}
			
			if (min != out) {
				int temp = data[out];
				data[out] = data[min];
				data[min] = temp;
			}
		}
	}
	
	public void addSeverity(int severity) {
		severities.add(severity);
	}

	private List<Integer>  severities;
	private int[] severityData;
	private Action doubleClickAction;
	private Figure stickedFigure;
	private Color fillColor;
	private String text;
	private Area area;
	private RoundRectangle2D.Double roundrect;
	private static final int LAYER = 1;
	private static final int charWidth = 13;
	private static final double DEFAULT_ARC = 15;
	private static final double DEFAULT_WIDTH = 40;
	private static final double DEFAULT_HEIGHT = 18;
	private static final long serialVersionUID = 1L;
}