package com.jhw.adm.client.draw;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import org.jhotdraw.draw.AttributeKey;
import org.jhotdraw.draw.CompositeFigure;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.tool.AbstractTool;

/**
 * 设备创建工具
 */
public class EquipmentCreationTool extends AbstractTool {


    protected Map<AttributeKey, Object> prototypeAttributes;
    /**
     * A localized name for this tool. The presentationName is displayed by the
     * UndoableEdit.
     */
    protected String presentationName;
    /**
     * Treshold for which we create a larger shape of a minimal size.
     */
    protected Dimension minimalSizeTreshold = new Dimension(2, 2);
    /**
     * We set the figure to this minimal size, if it is smaller than the
     * minimal size treshold.
     */
    protected Dimension minimalSize = new Dimension(40, 40);
    /**
     * The prototype for new figures.
     */
    protected Figure prototype;
    /**
     * The created figure.
     */
    protected Figure createdFigure;
    /**
     * If this is set to false, the CreationTool does not fire toolDone
     * after a new Figure has been created. This allows to create multiple
     * figures consecutively.
     */
    private boolean isToolDoneAfterCreation = true;
    
    /**
     * 用于注释图形的固定大小
     * @param minimalSize
     */
    public void setMinimalSize(Dimension minimalSize){
    	this.minimalSize = minimalSize;
    }
	
	private NodeEdit<? extends NodeFigure> nodeEdit;
	private static final long serialVersionUID = -7967924790849988241L;

    
    public EquipmentCreationTool(Figure prototype) {
    	this.prototype = prototype;
    }

    public Figure getPrototype() {
        return prototype;
    }

    @Override
    public void activate(DrawingEditor editor) {
        super.activate(editor);
        //getView().clearSelection();
        if (getCustomCursor() == null) {
        	getView().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } else {
        	getView().setCursor(getCustomCursor());
        }
    }
    
    private Cursor customCursor;

    public Cursor getCustomCursor() {
		return customCursor;
	}

	public void setCustomCursor(Cursor customCursor) {
		this.customCursor = customCursor;
	}

	@Override
    public void deactivate(DrawingEditor editor) {
        super.deactivate(editor);
        if (getView() != null) {
            getView().setCursor(Cursor.getDefaultCursor());
        }
        if (createdFigure != null) {
            if (createdFigure instanceof CompositeFigure) {
                ((CompositeFigure) createdFigure).layout();
            }
            createdFigure = null;
        }
    }

    @Override
	public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);
        getView().clearSelection();
        createdFigure = nodeEdit.createFigure();
        Point2D.Double p = constrainPoint(viewToDrawing(anchor));
        anchor.x = evt.getX();
        anchor.y = evt.getY();
//        createdFigure.setBounds(p, p);
        // 不设置大小，移动到当前鼠标位置
        AffineTransform at = new AffineTransform();
		at.translate(evt.getX(), evt.getY());
        createdFigure.transform(at);
        getDrawing().add(createdFigure);
    }

	public void installEdit(NodeEdit<? extends NodeFigure> nodeEdit) {
		this.nodeEdit = nodeEdit;
	}

    public void mouseDragged(MouseEvent evt) {
        if (createdFigure != null) {
        	// 鼠标拖动不设置大小
//            Point2D.Double p = constrainPoint(new Point(evt.getX(), evt.getY()));
//            createdFigure.willChange();
//            createdFigure.setBounds(
//                    constrainPoint(new Point(anchor.x, anchor.y)),
//                    p);
//            createdFigure.changed();
        }
    }

    @Override
	public void mouseReleased(MouseEvent evt) {
        if (createdFigure != null) {
            Rectangle2D.Double bounds = createdFigure.getBounds();
            if (bounds.width == 0 && bounds.height == 0) {
                getDrawing().remove(createdFigure);
                if (isToolDoneAfterCreation()) {
                    fireToolDone();
                }
            } else {
                if (Math.abs(anchor.x - evt.getX()) < minimalSizeTreshold.width &&
                        Math.abs(anchor.y - evt.getY()) < minimalSizeTreshold.height) {
                    createdFigure.willChange();
                    createdFigure.setBounds(
                            constrainPoint(new Point(anchor.x, anchor.y)),
                            constrainPoint(new Point(
                            anchor.x + (int) Math.max(bounds.width, minimalSize.width),
                            anchor.y + (int) Math.max(bounds.height, minimalSize.height))));
                    createdFigure.changed();
                }
                if (createdFigure instanceof CompositeFigure) {
                    ((CompositeFigure) createdFigure).layout();
                }
                final Figure addedFigure = createdFigure;
                final Drawing addedDrawing = getDrawing();
                getDrawing().fireUndoableEditHappened(new AbstractUndoableEdit() {

                    @Override
                    public String getPresentationName() {
                        return presentationName;
                    }

                    @Override
                    public void undo() throws CannotUndoException {
                        super.undo();
                        addedDrawing.remove(addedFigure);
                    }

                    @Override
                    public void redo() throws CannotRedoException {
                        super.redo();
                        addedDrawing.add(addedFigure);
                    }
                });
                Rectangle r = new Rectangle(anchor.x, anchor.y, 0, 0);
                r.add(evt.getX(), evt.getY());
                maybeFireBoundsInvalidated(r);
                creationFinished(createdFigure);
                createdFigure = null;
            }
        } else {
            if (isToolDoneAfterCreation()) {
                fireToolDone();
            }
        }
    }

    protected Figure getCreatedFigure() {
        return createdFigure;
    }

    protected Figure getAddedFigure() {
        return createdFigure;
    }

    /**
     * This method allows subclasses to do perform additonal user interactions
     * after the new figure has been created.
     * The implementation of this class just invokes fireToolDone.
     */
    protected void creationFinished(Figure createdFigure) {
        if (createdFigure.isSelectable()) {
            getView().addToSelection(createdFigure);
        }
        if (isToolDoneAfterCreation()) {
            fireToolDone();
        }
    }

    /**
     * If this is set to false, the CreationTool does not fire toolDone
     * after a new Figure has been created. This allows to create multiple
     * figures consecutively.
     */
    public void setToolDoneAfterCreation(boolean newValue) {
        boolean oldValue = isToolDoneAfterCreation;
        isToolDoneAfterCreation = newValue;
    }

    /**
     * Returns true, if this tool fires toolDone immediately after a new
     * figure has been created.
     */
    public boolean isToolDoneAfterCreation() {
        return isToolDoneAfterCreation;
    }

    @Override
    public void updateCursor(DrawingView view, Point p) {
        if (view.isEnabled()) {
            view.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } else {
            view.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
    }
}