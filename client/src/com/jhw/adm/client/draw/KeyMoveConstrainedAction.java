package com.jhw.adm.client.draw;

import static org.jhotdraw.draw.AttributeKeys.CANVAS_HEIGHT;
import static org.jhotdraw.draw.AttributeKeys.CANVAS_WIDTH;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.TranslationDirection;
import org.jhotdraw.draw.action.AbstractSelectedAction;
import org.jhotdraw.draw.event.TransformEdit;
import org.jhotdraw.undo.CompositeEdit;
import org.jhotdraw.util.ResourceBundleUtil;

public class KeyMoveConstrainedAction  extends AbstractSelectedAction {

	private static final long serialVersionUID = 1L;
	private final TranslationDirection dir;

    /** Creates a new instance. */
    public KeyMoveConstrainedAction(DrawingEditor editor, TranslationDirection dir) {
        super(editor);
        this.dir = dir;
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (getView().getSelectionCount() > 0) {
        
        Rectangle2D.Double r = null;
        for (Figure f : getView().getSelectedFigures()) {
            if (r == null) {
                r = f.getBounds();
            } else {
                r.add(f.getBounds());
            }
        }

        Point2D.Double p0 = new Point2D.Double(r.x, r.y);
        if (getView().getConstrainer() != null) {
            getView().getConstrainer().translateRectangle(r, dir);
        } else {
            switch (dir) {
                case NORTH:
                    r.y -= 1;
                    break;
                case SOUTH:
                    r.y += 1;
                    break;
                case WEST:
                    r.x -= 1;
                    break;
                case EAST:
                    r.x += 1;
                    break;
            }
        }

        AffineTransform tx = new AffineTransform();
        tx.translate(r.x - p0.x, r.y - p0.y);
        for (Figure f : getView().getSelectedFigures()) {
            if (f.isTransformable()) {
                f.willChange();
                f.transform(tx);
                f.changed();
            }
        }
        CompositeEdit edit;
        fireUndoableEditHappened(new TransformEdit(getView().getSelectedFigures(), tx));
        
        Rectangle invalidated = new Rectangle((int)r.x, (int)r.y, 0, 0);
//        r.add(evt.getX(), evt.getY());
        maybeFireBoundsInvalidated(invalidated);
        }
    }
    
    protected void maybeFireBoundsInvalidated(Rectangle invalidatedArea) {
        Drawing d = getDrawing();
        Rectangle2D.Double canvasBounds = new Rectangle2D.Double(0, 0, 0, 0);
        if (d.get(CANVAS_WIDTH) != null) {
            canvasBounds.width += d.get(CANVAS_WIDTH);
        }
        if (d.get(CANVAS_HEIGHT) != null) {
            canvasBounds.height += d.get(CANVAS_HEIGHT);
        }
        if (!canvasBounds.contains(invalidatedArea)) {
        	getView().getComponent().revalidate();
        	Rectangle invalidated = new Rectangle(0, 0, 0, 0);
        	for (Figure f : getView().getSelectedFigures()) {
        		invalidated = new Rectangle((int)f.getBounds().x, (int)f.getBounds().y, 0, 0);
                invalidated.add(f.getDrawingArea());
            }
        	// 滚动到最新的位置
        	getView().getComponent().scrollRectToVisible(invalidated);
        }
    }

    public static class East extends KeyMoveConstrainedAction {

        public final static String ID = "edit.moveConstrainedEast";

        public East(DrawingEditor editor) {
            super(editor, TranslationDirection.EAST);
            ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
            labels.configureAction(this, ID);
        }
    }

    public static class West extends KeyMoveConstrainedAction {

        public final static String ID = "edit.moveConstrainedWest";

        public West(DrawingEditor editor) {
            super(editor, TranslationDirection.WEST);
            ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
            labels.configureAction(this, ID);
        }
    }

    public static class North extends KeyMoveConstrainedAction {

        public final static String ID = "edit.moveConstrainedNorth";

        public North(DrawingEditor editor) {
            super(editor, TranslationDirection.NORTH);
            ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
            labels.configureAction(this, ID);
        }
    }

    public static class South extends KeyMoveConstrainedAction {

        public final static String ID = "edit.moveConstrainedSouth";

        public South(DrawingEditor editor) {
            super(editor, TranslationDirection.SOUTH);
            ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.draw.Labels");
            labels.configureAction(this, ID);
        }
    }
}