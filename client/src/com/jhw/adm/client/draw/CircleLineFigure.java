package com.jhw.adm.client.draw;

import static org.jhotdraw.draw.AttributeKeys.STROKE_COLOR;
import static org.jhotdraw.draw.AttributeKeys.STROKE_WIDTH;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.Action;

import org.jhotdraw.draw.EllipseFigure;
import org.jhotdraw.draw.event.FigureAdapter;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.event.FigureListener;

/**
 * »·ÏßÍ¼ÐÎ
 */
public class CircleLineFigure extends EllipseFigure implements NodeFigure {

	public CircleLineFigure() {
		this(0, 0, 200, 150);
	}

	public CircleLineFigure(double x, double y, double width, double height) {
		super(x, y, width, height);
		set(STROKE_WIDTH, 5d);
		set(STROKE_COLOR, Color.GRAY);
	}

	@Override
	protected void drawFill(Graphics2D g) {
		// don't fill...
	}

	@Override
	public int getLayer() {
        return LAYER;
    }

	@Override
	public void setActions(List<Action> actions) {
	}
	
	@Override
	public Action getDoubleClickAction() {
		return null;
	}

	@Override
	public NodeEdit getEdit() {
		return nodeEdit;
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		return null;
	}
	
	@Override
	public void setDoubleClickAction(Action doubleClickAction) {
		
	}

	public void setEdit(NodeEdit edit) {
		this.nodeEdit = edit;
		addFigureListener(figureListener);
	}
		
	private final FigureListener figureListener = new FigureAdapter() {
		@Override
		public void figureChanged(FigureEvent e) {
		}
	};
	
	private NodeEdit nodeEdit;
	private static final int LAYER = 0;
	private static final long serialVersionUID = 1L;
}