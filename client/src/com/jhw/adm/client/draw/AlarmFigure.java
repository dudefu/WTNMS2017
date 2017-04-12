package com.jhw.adm.client.draw;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.jhotdraw.draw.AbstractAttributedFigure;
import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.NullHandle;
import org.jhotdraw.geom.Geom;

/** 
 * 告警图形
  */
public class AlarmFigure extends AbstractAttributedFigure {

	public AlarmFigure(String text) {
		this(text, 0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	}

	public AlarmFigure(String text, double x, double y, double width,
			double height) {
		this.text = text;
		setConnectable(false);
		createArea(text, x, y, width, height);

//		Timer timer = new Timer();
//		timer.schedule(new TimerTask() {
//			@Override
//			public void run() {
//				willChange();
//				alpha = !alpha;
//				changed();
//			}
//		}, 1000, 1000);
	}

	protected void createArea(String text, double x, double y, double width,
			double height) {
		int charCount = text.toCharArray().length;
		roundrect = new RoundRectangle2D.Double(x, y, CHAR_WIDTH * charCount,
				height, DEFAULT_ARC, DEFAULT_ARC);

		double grow = AttributeKeys.getPerpendicularDrawGrowth(this);
		roundrect.x -= grow;
		roundrect.y -= grow;
		roundrect.width += grow * 2;
		roundrect.height += grow * 2;
		roundrect.arcwidth += grow * 2;
		roundrect.archeight += grow * 2;

		area = new Area(roundrect);
	}

//	public void draw(Graphics2D g) {
//		if (alpha) {
//			super.draw(g);
//		} else {
//			java.awt.Composite oldComposite = g.getComposite();
//			// AlphaComposite.Clear
//			// 源排斥目标法合成规则
//			int compositeRule = AlphaComposite.SRC_OVER;
//			AlphaComposite alphaComposite = AlphaComposite.getInstance(compositeRule, 0.35f);
//			g.setComposite(alphaComposite);
//			super.draw(g);
//			g.setComposite(oldComposite);
//		}
//	}

	@Override
	protected void drawFill(Graphics2D g) {
//		Area r = (Area) area.clone();
//
//		if (r.getBounds().width > 0 && r.getBounds().height > 0) {
//			Color oldColor = g.getColor();
//			// change to get(FILL_COLOR)
//			g.setColor(Color.magenta);
//			g.fill(area);
//			g.setColor(oldColor);
//		}
	}

	@Override
	protected void drawStroke(Graphics2D g) {
//		Area r = (Area) area.clone();
//
//		if (r.getBounds().width > 0 && r.getBounds().height > 0) {
//			Color oldColor = g.getColor();
//			g.setColor(Color.BLACK);
//			g.draw(area);
//			g.setColor(oldColor);
//		}
	}

	@Override
	protected void drawText(Graphics2D g) {
		String drawText = getText();
		if (drawText == null || drawText.length() == 0)
			return;

		int areaX = area.getBounds().x;
		int areaY = area.getBounds().y;
		int areaWidth = (int) roundrect.width;
		int areaHeight = (int) roundrect.height;
		int stringWidth = g.getFontMetrics().stringWidth(drawText);
		int stringHeight = g.getFontMetrics().getHeight();
		int x = (areaWidth - stringWidth) / 2;
		int y = (areaHeight - stringHeight + 13);
		Font oldFont = g.getFont();
		Color oldColor = g.getColor();
		g.setFont(new Font("宋体", Font.BOLD, 12));
		g.setColor(Color.RED);
		g.drawString(drawText, x + areaX, y + areaY);
		g.setColor(oldColor);
		g.setFont(oldFont);
	}

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

	public boolean contains(Point2D.Double p) {
		Area r = (Area) area.clone();
		return r.contains(p);
	}

	@Override
	public Collection<Handle> createHandles(int detailLevel) {
		List<Handle> handles = new LinkedList<Handle>();
		switch (detailLevel) {
		case -1:
			// handles.add(new BoundsOutlineHandle(this, false, true));
			break;
		case 0:
			// MoveHandle.addMoveHandles(this, handles);
			NullHandle.addLeadHandles(this, handles);
			break;
		}
		return handles;
	}

	@Override
	public void setBounds(Point2D.Double anchor, Point2D.Double lead) {
		double x = Math.min(anchor.x, lead.x);
		double y = Math.min(anchor.y, lead.y);
		double width = DEFAULT_WIDTH;
		double height = DEFAULT_HEIGHT;

		createArea(getText(), x, y, width, height);
	}

	/**
	 * Transforms the figure.
	 * 
	 * @param tx
	 *            The transformation.
	 */
	public void transform(AffineTransform tx) {
		Point2D.Double anchor = getStartPoint();
		Point2D.Double lead = getEndPoint();
		setBounds((Point2D.Double) tx.transform(anchor, anchor),
				(Point2D.Double) tx.transform(lead, lead));
	}

	public void restoreTransformTo(Object geometry) {
	}

	public Object getTransformRestoreData() {
		return area.clone();
	}

	@Override
	public AlarmFigure clone() {
		AlarmFigure that = (AlarmFigure) super.clone();
		that.area = (Area) this.area.clone();
		return that;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		int areaX = area.getBounds().x;
		int areaY = area.getBounds().y;
		int areaWidth = (int) roundrect.width;
		int areaHeight = (int) roundrect.height;

		createArea(getText(), areaX, areaY, areaWidth, areaHeight);
		fireFigureChanged();
	}

	private boolean alpha;
	private String text;
	private Area area;
	private RoundRectangle2D.Double roundrect;
	private static final int CHAR_WIDTH = 12; // == Font.size ??
	private static final double DEFAULT_ARC = 15;
	private static final double DEFAULT_WIDTH = 40;
	private static final double DEFAULT_HEIGHT = 18;
	private static final long serialVersionUID = 1L;
}