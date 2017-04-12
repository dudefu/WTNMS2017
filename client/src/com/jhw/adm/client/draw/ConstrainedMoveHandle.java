package com.jhw.adm.client.draw;

import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Collection;

import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.event.TransformEdit;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.handle.HandleAttributeKeys;
import org.jhotdraw.draw.handle.LocatorHandle;
import org.jhotdraw.draw.locator.Locator;
import org.jhotdraw.draw.locator.RelativeLocator;

public class ConstrainedMoveHandle extends LocatorHandle {

    /**
     * The previously handled x and y coordinates.
     */
    private Point2D.Double oldPoint;

    /** Creates a new instance. */
    public ConstrainedMoveHandle(Figure owner, Locator locator) {
        super(owner, locator);
    }

    /**
     * Creates handles for each corner of a
     * figure and adds them to the provided collection.
     */
    static public void addMoveHandles(Figure f, Collection<Handle> handles) {
        handles.add(southEast(f));
        handles.add(southWest(f));
        handles.add(northEast(f));
        handles.add(northWest(f));
    }

    /**
     * Draws this handle.
     * <p>
     * If the figure is transformable, the handle is drawn as a filled rectangle.
     * If the figure is not transformable, the handle is drawn as an unfilled
     * rectangle.
     */
    @Override
    public void draw(Graphics2D g) {
        if (getOwner().isTransformable()) {
            drawRectangle(g,
                    getEditor().getHandleAttribute(HandleAttributeKeys.MOVE_HANDLE_FILL_COLOR),
                    getEditor().getHandleAttribute(HandleAttributeKeys.MOVE_HANDLE_STROKE_COLOR));
        } else {
            drawRectangle(g,
                    getEditor().getHandleAttribute(HandleAttributeKeys.NULL_HANDLE_FILL_COLOR),
                    getEditor().getHandleAttribute(HandleAttributeKeys.NULL_HANDLE_STROKE_COLOR));
        }
    }

    /**
     * Returns a cursor for the handle. 
     * 
     * @return Returns a move cursor, if the figure
     * is transformable. Returns a default cursor otherwise. 
     */
    @Override
    public Cursor getCursor() {
        return Cursor.getPredefinedCursor(
                getOwner().isTransformable() ? Cursor.MOVE_CURSOR : Cursor.DEFAULT_CURSOR);
    }

    public void trackStart(Point anchor, int modifiersEx) {
        oldPoint = view.getConstrainer().constrainPoint(view.viewToDrawing(anchor));
    }

    public void trackStep(Point anchor, Point lead, int modifiersEx) {
        Figure f = getOwner();
        if (f.isTransformable() && getEditor().isEnabled()) {
            Point2D.Double newPoint = view.getConstrainer().constrainPoint(view.viewToDrawing(lead));
            AffineTransform tx = new AffineTransform();
            double diffX = newPoint.x - oldPoint.x;
            double diffY = newPoint.y - oldPoint.y;
            
            double willX = f.getBounds().x + diffX;
			double willY = f.getBounds().y + diffY;

			if (willX > 0 && willY > 0) {
	            tx.translate(diffX, diffY);
	            f.willChange();
	            f.transform(tx);
	            f.changed();
			}

            oldPoint = newPoint;
        }
    }

    public void trackEnd(Point anchor, Point lead, int modifiersEx) {
        if (getOwner().isTransformable() && getEditor().isEnabled()) {
            AffineTransform tx = new AffineTransform();
            tx.translate(lead.x - anchor.x, lead.y - anchor.y);
            fireUndoableEditHappened(
                    new TransformEdit(getOwner(), tx));
        }
    }

    @Override
    public void keyPressed(KeyEvent evt) {
        Figure f = getOwner();
        if (f.isTransformable() && getEditor().isEnabled()) {
            AffineTransform tx = new AffineTransform();

            switch (evt.getKeyCode()) {
                case KeyEvent.VK_UP:
                    tx.translate(0, -1);
                    evt.consume();
                    break;
                case KeyEvent.VK_DOWN:
                    tx.translate(0, +1);
                    evt.consume();
                    break;
                case KeyEvent.VK_LEFT:
                    tx.translate(-1, 0);
                    evt.consume();
                    break;
                case KeyEvent.VK_RIGHT:
                    tx.translate(+1, 0);
                    evt.consume();
                    break;
            }
            f.willChange();
            f.transform(tx);
            f.changed();
            fireUndoableEditHappened(
                    new TransformEdit(f, tx));
        }

    }

    static public Handle south(
            Figure owner) {
        return new ConstrainedMoveHandle(owner, RelativeLocator.south());
    }

    static public Handle southEast(
            Figure owner) {
        return new ConstrainedMoveHandle(owner, RelativeLocator.southEast());
    }

    static public Handle southWest(
            Figure owner) {
        return new ConstrainedMoveHandle(owner, RelativeLocator.southWest());
    }

    static public Handle north(
            Figure owner) {
        return new ConstrainedMoveHandle(owner, RelativeLocator.north());
    }

    static public Handle northEast(
            Figure owner) {
        return new ConstrainedMoveHandle(owner, RelativeLocator.northEast());
    }

    static public Handle northWest(
            Figure owner) {
        return new ConstrainedMoveHandle(owner, RelativeLocator.northWest());
    }

    static public Handle east(
            Figure owner) {
        return new ConstrainedMoveHandle(owner, RelativeLocator.east());
    }

    static public Handle west(
            Figure owner) {
        return new ConstrainedMoveHandle(owner, RelativeLocator.west());
    }
}
