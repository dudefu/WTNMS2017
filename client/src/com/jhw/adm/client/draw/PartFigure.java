package com.jhw.adm.client.draw;

import static org.jhotdraw.draw.AttributeKeys.STROKE_COLOR;
import static org.jhotdraw.draw.AttributeKeys.STROKE_WIDTH;
import static org.jhotdraw.draw.AttributeKeys.TEXT_COLOR;
import static org.jhotdraw.draw.AttributeKeys.TEXT_SHADOW_COLOR;
import static org.jhotdraw.draw.AttributeKeys.TEXT_SHADOW_OFFSET;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Action;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.ImageFigure;
import org.jhotdraw.draw.handle.BoundsOutlineHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.NullHandle;
import org.jhotdraw.geom.Dimension2DDouble;

/**
 * 仿真部件图形
 */
public class PartFigure extends ImageFigure implements NodeFigure {
	
	public PartFigure() {
    	fillColor = Color.RED;
    	severities = new ArrayList<Integer>();
		set(STROKE_COLOR, null);
		set(NetworkKeys.UNDRAGABLE, true);
	}


    @Override
    protected void drawFigure(Graphics2D g) {
        drawImage(g);
//        if (get(FILL_COLOR) != null) {
//            g.setColor(get(FILL_COLOR));
            drawFill(g);
//        }

        if (get(STROKE_COLOR) != null && get(STROKE_WIDTH) > 0d) {
            g.setStroke(AttributeKeys.getStroke(this));
            g.setColor(get(STROKE_COLOR));
            drawStroke(g);
        }
        
        if (get(TEXT_COLOR) != null) {
            if (get(TEXT_SHADOW_COLOR) != null &&
                    get(TEXT_SHADOW_OFFSET) != null) {
                Dimension2DDouble d = get(TEXT_SHADOW_OFFSET);
                g.translate(d.width, d.height);
                g.setColor(get(TEXT_SHADOW_COLOR));
                drawText(g);
                g.translate(-d.width, -d.height);
            }
            g.setColor(get(TEXT_COLOR));
            drawText(g);
        }
    }

	public void showAlram(boolean show) {
		this.showAlram = show;
	}
	
	private boolean flash;

	@Override
    protected void drawFill(Graphics2D g) {	
		if (flash) {
//	        Rectangle2D.Double r = getBounds();
//	        double grow = AttributeKeys.getPerpendicularFillGrowth(this);
//	        Geom.grow(r, grow, grow);
//	        java.awt.Composite oldComposite = g.getComposite();
//			int compositeRule = AlphaComposite.SRC_OVER;
//			AlphaComposite alphaComposite = AlphaComposite.getInstance(compositeRule, 0.65f);
//			g.setComposite(alphaComposite);
	        
//	        Rectangle2D.Double fillR = new Rectangle2D.Double();
//	        fillR.x = r.x + 2;
//	        fillR.y = r.y + 2;
//	        fillR.width = 12;
//	        fillR.height = 6;
//	        Color oldColor = g.getColor();
//	        g.setColor(Color.GREEN);
//	        g.fill(fillR);
//	        g.setColor(oldColor);
//	        g.setComposite(oldComposite);
//	        drawAlarm(g);
		}
    }
	
	private void drawAlarm(Graphics2D g2d) {
        Color oldColor = g2d.getColor();
    	g2d.setColor(getFillColor());
		Rectangle2D.Double r = getBounds();
		String message = getAlarmMessage();
		Area alarmArea = createAlarmArea(message, r.x, r.y - 18);
		g2d.fill(alarmArea);
    	g2d.setColor(Color.BLACK);
		g2d.draw(alarmArea);
		if (message == null || message.length() == 0) return;
		
    	int areaX = alarmArea.getBounds().x;
    	int areaY = alarmArea.getBounds().y;
    	int areaWidth = (int)alarmArea.getBounds2D().getWidth();
    	int areaHeight = (int)alarmArea.getBounds2D().getHeight();
    	int stringWidth = g2d.getFontMetrics().stringWidth(message);
    	int stringHeight = g2d.getFontMetrics().getHeight();
    	int x = (areaWidth - stringWidth) / 2;
    	int y = (areaHeight - stringHeight + 8);
    	g2d.drawString(message, x + areaX, y + areaY);
    	g2d.setColor(oldColor);
	}
    
    protected Area createAlarmArea(String text, double x, double y) {
		int charCount = text.toCharArray().length + 2;
		int charWidth = 13;
		double DEFAULT_ARC = 15;
//		double DEFAULT_WIDTH = 40;
		double DEFAULT_HEIGHT = 18;
		RoundRectangle2D.Double roundrect = new RoundRectangle2D.Double(x, y, charWidth * charCount, DEFAULT_HEIGHT, DEFAULT_ARC, DEFAULT_ARC);
        
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
        Area area = new Area(roundrect);
        Polygon polygon = new Polygon();
		polygon.addPoint(dx + offset + 2, dy);
		polygon.addPoint(dx +  offset + 10, dy);
		polygon.addPoint(dx + offset + 6, dy + 6);
		area.add(new Area(polygon));
		
		return area;
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
                break;
        }
        return handles;
    }
	

	@Override
    public int getLayer() {
        return LAYER;
    }

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
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
	
	public void clearAlarm() {
		severities.clear();
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

    public String getAlarmMessage() {
		return alarmMessage;
	}


	public void setAlarmMessage(String alarmMessage) {
		this.alarmMessage = alarmMessage;
	}
//	@Override
//	public void addAction(Action action) {
//		// TODO Auto-generated method stub
//		
//	}

	@Override
	public void setActions(List<Action> actions) {
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Action getDoubleClickAction() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public NodeEdit<? extends NodeFigure> getEdit() {
		// TODO Auto-generated method stub
		return nodeEdit;
	}


	@Override
	public void setDoubleClickAction(Action doubleClickAction) {
		// TODO Auto-generated method stub
		
	}

//	@Override
	public void setEdit(NodeEdit<? extends NodeFigure> edit) {
		this.nodeEdit = edit;
	}

	private NodeEdit<? extends NodeFigure> nodeEdit;
	private final List<Integer>  severities;
	private int[] severityData;
	private Color fillColor;
	private String alarmMessage;
	private boolean showAlram;
	private static final int LAYER = 1;
	private static final long serialVersionUID = -2955870981169978095L;
}