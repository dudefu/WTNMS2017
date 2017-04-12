package com.jhw.adm.client.draw;

import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.LinkedList;

import org.jhotdraw.draw.Constrainer;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.event.TransformEdit;
import org.jhotdraw.draw.tool.AbstractTool;
import org.jhotdraw.draw.tool.DragTracker;
import org.jhotdraw.geom.Geom;

public class ConstrainedDragTracker extends AbstractTool implements DragTracker {

	public ConstrainedDragTracker(Figure figure) {
		anchorFigure = figure;
	}

	public ConstrainedDragTracker() {
	}

	@Override
	public void mouseMoved(MouseEvent evt) {
		updateCursor(editor.findView((Container) evt.getSource()), evt
				.getPoint());
	}

	@Override
	public void mousePressed(MouseEvent evt) {
		super.mousePressed(evt);
		DrawingView view = getView();

		if (evt.isShiftDown()) {
			view.setHandleDetailLevel(0);
			view.toggleSelection(anchorFigure);
			if (!view.isFigureSelected(anchorFigure)) {
				anchorFigure = null;
			}
		} else if (!view.isFigureSelected(anchorFigure)) {
			view.setHandleDetailLevel(0);
			view.clearSelection();
			view.addToSelection(anchorFigure);
		}

		if (!view.getSelectedFigures().isEmpty()) {

			dragRect = null;
			for (Figure f : view.getSelectedFigures()) {
				if (dragRect == null) {
					dragRect = f.getBounds();
				} else {
					dragRect.add(f.getBounds());
				}
			}

			anchorPoint = previousPoint = view.viewToDrawing(anchor);
			anchorOrigin = previousOrigin = new Point2D.Double(dragRect.x,
					dragRect.y);
		}
	}

	public void mouseDragged(MouseEvent evt) {
		DrawingView view = getView();
		if (!view.getSelectedFigures().isEmpty()) {
			if (isDragging == false) {
				isDragging = true;
				updateCursor(editor.findView((Container) evt.getSource()),
						new Point(evt.getX(), evt.getY()));
			}

			Point2D.Double currentPoint = view.viewToDrawing(new Point(evt
					.getX(), evt.getY()));

			dragRect.x += currentPoint.x - previousPoint.x;
			dragRect.y += currentPoint.y - previousPoint.y;
			Rectangle2D.Double constrainedRect = (Rectangle2D.Double) dragRect
					.clone();
			if (view.getConstrainer() != null) {
				view.getConstrainer().constrainRectangle(constrainedRect);
			}

			double maxX = anchorFigure.getBounds().x;
			double maxY = anchorFigure.getBounds().y;
			// 把选中的图形区域加起来作为拖拽区域判断
			Rectangle2D.Double dragArea = new Rectangle2D.Double(maxX, maxY, 0, 0);
			for (Figure f : view.getSelectedFigures()) {
				double minX = Math.min(dragArea.x, f.getBounds().x);
				double minY = Math.min(dragArea.y, f.getBounds().y);
				dragArea.x = minX;
				dragArea.y = minY;
				dragArea.add(f.getBounds());
//				System.out.println(
//						String.format("x: %s, y: %s, width: %s, height: %s", 
//								f.getBounds().x, f.getBounds().y, f.getBounds().width, f.getBounds().height));
			}
//			System.out.println(dragArea);
			
			// 如果当前鼠标视图外，图形直接移到边缘
			double tx = evt.getX() < 0 ? - dragArea.getBounds().x : 0;
			double ty = evt.getY() < 0 ? - dragArea.getBounds().y : 0;
			
			double diffX = constrainedRect.x - previousOrigin.x;
			double diffY = constrainedRect.y - previousOrigin.y;
			
			double willX = dragArea.getBounds().x + diffX;
			double willY = dragArea.getBounds().y + diffY;
			
			Point2D.Double dragAreaCenter = Geom.center(dragArea);
			// 如果是正向移动，鼠标必需超过图形中心
			// 如果是反向移动，图形不超过边缘即可
			if (diffX > 0) {
				if (willX > 0 && currentPoint.x > dragAreaCenter.x) {
					tx = diffX;
				}
			} else if (willX > 0) {
				tx = diffX;
			}		
			if (diffY > 0) {
				if (willY > 0 && currentPoint.y > dragAreaCenter.y) {
					ty = diffY;
				}
			} else if (willY > 0) {
				ty = diffY;
			}
			
			if (view.getSelectedFigures().size() == 1 && 
				anchorFigure.get(NetworkKeys.UNDRAGABLE) != null &&
				anchorFigure.get(NetworkKeys.UNDRAGABLE) == true) {
				//
			} else {
				for (Figure f : view.getSelectedFigures()) {
					AffineTransform at = new AffineTransform();
					at.translate(tx, ty);
					f.willChange();
					f.transform(at);
					f.changed();
				}
			}

			previousPoint = currentPoint;
			previousOrigin = new Point2D.Double(constrainedRect.x,
					constrainedRect.y);
		}
	}

	@Override
	public void mouseReleased(MouseEvent evt) {
		super.mouseReleased(evt);
		DrawingView view = getView();
		if (!view.getSelectedFigures().isEmpty()) {
			isDragging = false;
			int x = evt.getX();
			int y = evt.getY();
			updateCursor(editor.findView((Container) evt.getSource()),
					new Point(x, y));
			Point2D.Double newPoint = view.viewToDrawing(new Point(x, y));

			Collection<Figure> draggedFigures = new LinkedList<Figure>(view
					.getSelectedFigures());
			Figure dropTarget = getDrawing().findFigureExcept(newPoint,
					draggedFigures);
			if (dropTarget != null) {
				boolean snapBack = dropTarget.handleDrop(newPoint,
						draggedFigures, view);
				if (snapBack) {
					AffineTransform tx = new AffineTransform();
					tx.translate(anchorOrigin.x - previousOrigin.x,
							anchorOrigin.y - previousOrigin.y);
					Constrainer c = view.getConstrainer();
					for (Figure f : draggedFigures) {
						f.willChange();
						f.transform(tx);
						f.changed();
					}
					Rectangle r = new Rectangle(anchor.x, anchor.y, 0, 0);
					r.add(evt.getX(), evt.getY());
					maybeFireBoundsInvalidated(r);
					fireToolDone();
					return;
				}
			}

			AffineTransform tx = new AffineTransform();
			tx.translate(-anchorOrigin.x + previousOrigin.x, -anchorOrigin.y
					+ previousOrigin.y);
			if (!tx.isIdentity()) {
				getDrawing().fireUndoableEditHappened(
						new TransformEdit(draggedFigures, tx));
			}
		}
		Rectangle r = new Rectangle(anchor.x, anchor.y, 0, 0);
		r.add(evt.getX(), evt.getY());
		maybeFireBoundsInvalidated(r);
		fireToolDone();
	}

	public void setDraggedFigure(Figure f) {
		anchorFigure = f;
	}
	
	
	protected Figure anchorFigure;
	protected Rectangle2D.Double dragRect;
	protected Point2D.Double previousOrigin;
	protected Point2D.Double anchorOrigin;
	protected Point2D.Double previousPoint;
	protected Point2D.Double anchorPoint;
	private boolean isDragging;
	private static final long serialVersionUID = -8891607294415110437L;
}