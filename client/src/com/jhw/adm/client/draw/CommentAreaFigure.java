package com.jhw.adm.client.draw;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Action;

import org.apache.commons.lang.StringUtils;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.ConnectionFigure;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.TextAreaFigure;
import org.jhotdraw.draw.AttributeKeys.Alignment;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.connector.LocatorConnector;
import org.jhotdraw.draw.event.FigureAdapter;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.event.FigureListener;
import org.jhotdraw.draw.handle.BoundsOutlineHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.locator.RelativeLocator;

public class CommentAreaFigure extends TextAreaFigure implements NodeFigure {

	private static final long serialVersionUID = 1L;
	private static final Font TEXT_FONT = new Font("宋体", Font.PLAIN, 12);
	private static final Rectangle2D.Double bounds = new Rectangle2D.Double(0,0,133,77);

	public CommentAreaFigure(){
		this(StringUtils.EMPTY);
	}
	
	public CommentAreaFigure(String text){
		super(text);
		this.set(AttributeKeys.FONT_FACE, TEXT_FONT);
		this.set(AttributeKeys.FILL_COLOR, new Color(255, 255, 225));
		this.set(AttributeKeys.STROKE_WIDTH, 1.5d);
		this.set(AttributeKeys.TEXT_ALIGNMENT, Alignment.LEADING);
		this.setEditable(false);
		this.setConnectable(false);
		this.setTransformable(false);
		this.setBounds(bounds);
	}
	
	private Action doubleClickAction;
	private LinkedList<Connector> connectors;
	private List<Action> popupActions;
	private CommentEdit nodeEdit;
	
	private void CommentChanged(FigureEvent e) {
		if (getEdit() == null) {
		} else {
			getEdit().updateModel();
		}
	}

	private final FigureListener figureListener = new FigureAdapter() {
		@Override
		public void figureChanged(FigureEvent e) {
			CommentChanged(e);
		}
	};
	
	@Override
	public Action getDoubleClickAction() {
		return doubleClickAction;
	}

	@Override
	public void setDoubleClickAction(Action doubleClickAction) {
		this.doubleClickAction = doubleClickAction;
	}

	@Override
	public CommentEdit getEdit() {
		return nodeEdit;
	}

	public void setEdit(CommentEdit edit) {
		this.nodeEdit = edit;
		addFigureListener(figureListener);
	}
  
	@Override
	public Collection<Connector> getConnectors(ConnectionFigure prototype) {
		connectors = new LinkedList<Connector>();
		connectors.add(new LocatorConnector(this, RelativeLocator.center()));
		return Collections.unmodifiableList(connectors);
	}

	@Override
	public Collection<Handle> createHandles(int detailLevel) {
		List<Handle> handles = new LinkedList<Handle>();
		switch (detailLevel) {
			case -1:
				BoundsOutlineHandle boundsOutlineHandle = new BoundsOutlineHandle(this, false, true);
				handles.add(boundsOutlineHandle);            	
				break;
			case 0:
				ConstrainedMoveHandle.addMoveHandles(this, handles);
				break;
		}
		return handles;
	}
  
	@Override
	public boolean handleMouseClick(Point2D.Double p, MouseEvent evt,
			DrawingView view) {
		Action action = getDoubleClickAction();
		String DefaulActionCommand = "DoubleClick";
		if (action == null) {
			return false;
		}

		Object actionCommandValue = action.getValue(Action.ACTION_COMMAND_KEY);
		String actionCommand = actionCommandValue == null ? DefaulActionCommand
				: actionCommandValue.toString();
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
				actionCommand, EventQueue.getMostRecentEventTime(), evt
						.getModifiersEx());
		action.actionPerformed(event);
		return true;
	}
	
	/**
	 * 图形弹出菜单的Action集合
	 */
	@Override
	public Collection<Action> getActions(Point2D.Double p) {
		return Collections.unmodifiableList(popupActions);
	}
	
	@Override
	public void setActions(List<Action> actions) {
		popupActions = actions;
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (nodeEdit == null) {
			return null;
		} else {
			return nodeEdit.getAdapter(adapterClass);
		}
	}
	
}
