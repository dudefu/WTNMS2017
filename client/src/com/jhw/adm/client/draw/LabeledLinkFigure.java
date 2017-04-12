package com.jhw.adm.client.draw;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.swing.Action;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.LabeledLineConnectionFigure;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.event.FigureAdapter;
import org.jhotdraw.draw.event.FigureEvent;
import org.jhotdraw.draw.event.FigureListener;
import org.jhotdraw.draw.handle.BezierOutlineHandle;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.geom.BezierPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 带标签的连线图形
 */
public class LabeledLinkFigure extends LabeledLineConnectionFigure implements
		LinkFigure {

	public LabeledLinkFigure() {
		super();
		set(AttributeKeys.STROKE_WIDTH, 4d);
		popupActions = new ArrayList<Action>();
	}

	@Override
	public Collection<Handle> createHandles(int detailLevel) {
		ArrayList<Handle> handles = new ArrayList<Handle>(getNodeCount());
		switch (detailLevel) {
		case -1:
			handles.add(new BezierOutlineHandle(this, true));
			break;
		case 0:
			handles.add(new BezierOutlineHandle(this));
			break;
		}
		return handles;
	}

	@Override
	public boolean handleMouseClick(Point2D.Double p, MouseEvent evt,
			DrawingView view) {
		Action action = getDoubleClickAction();
		String DefaulActionCommand = "DoubleClick";
		if (action == null)
			return false;

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
	 * 弹出菜单的Action集合
	 */
	@Override
	public Collection<Action> getActions(Point2D.Double p) {
		return Collections.unmodifiableList(popupActions);
	}

	/**
	 * 添加弹出菜单Action
	 */
	public void addAction(Action action) {
		popupActions.add(action);
	}

	@Override
	public void setActions(List<Action> actions) {
		popupActions = actions;
	}

	@Override
	public void draw(Graphics2D g) {
		READ_LOCK.lock();
		try {
			super.draw(g);
		} catch (NullPointerException e) {
			LOG.error("super.draw() error", e);
//			this.invalidate();
		} finally {
			READ_LOCK.unlock();
		}
	}

	@Override
	public Rectangle2D.Double getDrawingArea() {
		Rectangle2D.Double drawingArea = new Rectangle2D.Double();
		READ_LOCK.lock();
		try {
			drawingArea = super.getDrawingArea();
		} catch (NullPointerException ex) {
			LOG.error("super.getDrawingArea() error", ex);
		} catch (IndexOutOfBoundsException ex) {
			LOG.error("super.getDrawingArea() error", ex);
		} finally {
			READ_LOCK.unlock();
		}

		return drawingArea;
	}

	@Override
	public void invalidate() {
		WRITE_LOCK.lock();
		try {
			super.invalidate();
		} finally {
			WRITE_LOCK.unlock();
		}
	}

	@Override
	public void validate() {
		WRITE_LOCK.lock();
		try {
			super.validate();
		} finally {
			WRITE_LOCK.unlock();
		}
	}

	@Override
	protected void drawCaps(Graphics2D g) {
		super.drawCaps(g);
		
		if (getFddiImage() != null) {
			drawFddiImage(g);
		}
		
		if (flux > 0) {
			drawFlux(g);
		}

		if (linkDown) {
			drawLinkDown(g);
		} else if (block) {
			drawBlock(g);
		}
	}
	
	private void drawFddiImage(Graphics2D g) {
		BezierPath cp = getCappedPath();

		AffineTransform transform = new AffineTransform();
		transform.translate(cp.getCenter().x, cp.getCenter().y);
		int width = fddiImage.getWidth();
		int height = fddiImage.getHeight();
		double x = transform.getTranslateX() - width / 2;
		double y = transform.getTranslateY() - height / 2;	
		g.drawImage(fddiImage, (int) x, (int) y,
				fddiImage.getWidth(), fddiImage.getHeight(), null);
		
	}

	private void drawBlock(Graphics2D g) {
		BezierPath cp = getCappedPath();

		AffineTransform transform = new AffineTransform();
		transform.translate(cp.getCenter().x, cp.getCenter().y);
		int width = 15;
		int height = 15;
		double x = transform.getTranslateX() - width / 2;
		double y = transform.getTranslateY() - height / 2;
		Color oldColor = g.getColor();
		Stroke oldStroke = g.getStroke();
		g.setColor(Color.RED);
		g.setStroke(new BasicStroke(2));
		g.drawLine((int) x, (int) y + height / 2, (int) x + width, (int) y
				+ height / 2);
		Ellipse2D.Double ellipse = new Ellipse2D.Double(x, y, width, height);
		g.draw(ellipse);

		g.setStroke(oldStroke);
		g.setColor(oldColor);
	}

	private void drawLinkDown(Graphics2D g) {
		if (getNodeCount() > 0) {
			BezierPath cp = getCappedPath();
	
			AffineTransform transform = new AffineTransform();
			transform.translate(cp.getCenter().x, cp.getCenter().y);
			int width = 15;
			int height = 15;
			double x = transform.getTranslateX() - width / 2;
			double y = transform.getTranslateY() - height / 2;
			Color oldColor = g.getColor();
			g.setColor(Color.RED);
			g.drawLine((int) x, (int) y, (int) x + width, (int) y + height);
			g.drawLine((int) x, (int) y + height, (int) x + width, (int) y);
	
			g.setColor(oldColor);
		}
	}

	private void drawFlux(Graphics2D g) {		
		BezierPath cp = getCappedPath();

		AffineTransform transform = new AffineTransform();
		transform.translate(cp.getCenter().x, cp.getCenter().y);
		int width = 15;
		int height = 15;
		double x = transform.getTranslateX() - width / 2;
		double y = transform.getTranslateY() - height / 2;
		Color oldColor = g.getColor();
		g.setColor(Color.BLACK);
		g.drawString(Long.toString(flux), (int) x, (int) y);
		g.setColor(oldColor);
	}

	public Action getDoubleClickAction() {
		return doubleClickAction;
	}

	public void setDoubleClickAction(Action doubleClickAction) {
		this.doubleClickAction = doubleClickAction;
	}

	@Override
	public NodeFigure getStartFigure() {
		return (NodeFigure) super.getStartFigure();
	}

	@Override
	public NodeFigure getEndFigure() {
		return (NodeFigure) super.getEndFigure();
	}

	@Override
	public boolean canConnect(Connector start, Connector end) {
		if (getEdit() == null) {
			return super.canConnect(start, end);
		} else {
			return getEdit().canConnect((NodeFigure) start.getOwner(),
					(NodeFigure) end.getOwner());
		}
	}

	@Override
	public void handleConnect(Connector start, Connector end) {
		super.handleConnect(start, end);
		if (getEdit() == null) {

		} else {
			getEdit().updateModel();
		}
	}

	public void addFlux(long value) {
		if (value > 0) {
			set(AttributeKeys.STROKE_WIDTH, 4d);
			set(AttributeKeys.STROKE_COLOR, Color.RED);
			flux = value;
		} else {
			set(AttributeKeys.STROKE_WIDTH, 4d);
			set(AttributeKeys.STROKE_COLOR, Color.BLACK);
			flux = 0;
		}
	}
	
	public void block() {
		block = true;
		linkDown = false;
	}
	
	public void unblock() {
		block = false;
		linkDown = false;
	}
	
	public void linkUp() {
		block = false;
		linkDown = false;
	}
	
	public void linkDown() {
		block = false;
		linkDown = true;
	}

	public Color getStrokeColor() {
		return strokeColor;
	}

	public void setStrokeColor(Color strokeColor) {
		this.strokeColor = strokeColor;
	}

	@Override
	public LabeledLinkEdit getEdit() {
		return nodeEdit;
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		if (nodeEdit == null) {
			return null;
		} else {
			return nodeEdit.getAdapter(adapterClass);
		}
	}
	
	public void setEdit(LabeledLinkEdit edit) {
		this.nodeEdit = edit;
		this.addFigureListener(figureListener);
	}

	private void LinkChanged(FigureEvent e) {
		if (getEdit() == null) {
		} else {
			getEdit().updateModel();
		}
	}

	private final FigureListener figureListener = new FigureAdapter() {
		@Override
		public void figureChanged(FigureEvent e) {
			LinkChanged(e);
		}
	};

	public BufferedImage getFddiImage() {
		return fddiImage;
	}

	public void setFddiImage(BufferedImage fddiImage) {
		try {
			WRITE_LOCK.lock();
			this.fddiImage = fddiImage;
		} finally {
			WRITE_LOCK.unlock();
		}
	}

	private volatile boolean block;
	private volatile boolean linkDown;
	private long flux;
	private BufferedImage fddiImage;
	private LabeledLinkEdit nodeEdit;
	private Color strokeColor;
	private List<Action> popupActions;
	private Action doubleClickAction;
	private static final ReadWriteLock LOCK = new ReentrantReadWriteLock();
    private static final Lock READ_LOCK = LOCK.readLock();
    private static final Lock WRITE_LOCK = LOCK.writeLock();
	private static final Logger LOG = LoggerFactory
			.getLogger(LabeledLinkFigure.class);
	private static final long serialVersionUID = 1L;
	
	public static final int LINK_NORMAL = 1;
}