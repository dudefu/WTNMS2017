package com.jhw.adm.client.draw;

import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.LinkedList;

import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.draw.tool.AbstractTool;
import org.jhotdraw.draw.tool.Tool;

/**
 * 仿真图选择工具
 */
public class EmulationalSelectionTool extends AbstractTool {

	/**
	 * MessageConstant for the name of the selectBehindEnabled property.
	 */
	public final static String SELECT_BEHIND_ENABLED_PROPERTY = "selectBehindEnabled";
	/**
	 * Represents the state of the selectBehindEnabled property. By default,
	 * this property is set to true.
	 */
	private boolean isSelectBehindEnabled = true;

	/** Creates a new instance. */
	public EmulationalSelectionTool() {
	}

	public void setSelectBehindEnabled(boolean newValue) {
		boolean oldValue = isSelectBehindEnabled;
		isSelectBehindEnabled = newValue;
		firePropertyChange(SELECT_BEHIND_ENABLED_PROPERTY, oldValue, newValue);
	}

	public boolean isSelectBehindEnabled() {
		return isSelectBehindEnabled;
	}

	@Override
	public void activate(DrawingEditor editor) {
		super.activate(editor);
	}

	public void deactivate(DrawingEditor editor) {
		super.deactivate(editor);
	}

	public void keyPressed(KeyEvent e) {
		if (getView() != null && getView().isEnabled()) {
		}
	}

	public void keyReleased(KeyEvent evt) {
		if (getView() != null && getView().isEnabled()) {
		}
	}

	public void keyTyped(KeyEvent evt) {
		if (getView() != null && getView().isEnabled()) {
		}
	}

	public void mouseClicked(MouseEvent evt) {
		if (getView() != null && getView().isEnabled()) {
		}
	}

	public void mouseDragged(MouseEvent evt) {
		if (getView() != null && getView().isEnabled()) {
		}
	}

	public void mouseEntered(MouseEvent evt) {
		super.mouseEntered(evt);
	}

	public void mouseExited(MouseEvent evt) {
		super.mouseExited(evt);
	}

	@Override
	public void mouseMoved(MouseEvent evt) {
//		tracker.mouseMoved(evt);
		clearRubberBand();
        Point point = evt.getPoint();
        DrawingView view = editor.findView((Container) evt.getSource());
        updateCursor(view, point);
        if (view == null || editor.getActiveView() != view) {
            clearHoverHandles();
        } else {
            // Search first, if one of the selected figures contains
            // the current mouse location, and is selectable. 
            // Only then search for other
            // figures. This search sequence is consistent with the
            // search sequence of the SelectionTool.
            Figure figure = null;
            Point2D.Double p = view.viewToDrawing(point);
            if (figure == null) {
                figure = view.getDrawing().findFigureInside(viewToDrawing(point));
                
                while (figure != null && !figure.isSelectable()) {
                    figure = view.getDrawing().findFigureBehind(p, figure);
                }
            }

            updateHoverHandles(view, figure);
        }
	}

    private void clearRubberBand() {
        if (!rubberband.isEmpty()) {
            fireAreaInvalidated(rubberband);
            rubberband.width = -1;
        }
    }

    protected void clearHoverHandles() {
        updateHoverHandles(null, null);
    }

    protected void updateHoverHandles(DrawingView view, Figure f) {
        if (f != hoverFigure) {
            Rectangle r = null;
            if (hoverFigure != null) {
                for (Handle h : hoverHandles) {
                    if (r == null) {
                        r = h.getDrawingArea();
                    } else {
                        r.add(h.getDrawingArea());
                    }
                    h.setView(null);
                    h.dispose();
                }
                hoverHandles.clear();
            }
            hoverFigure = f;
            if (hoverFigure != null && f.isSelectable()) {
                hoverHandles.addAll(hoverFigure.createHandles(-1));
                for (Handle h : hoverHandles) {
                    h.setView(view);
                    if (r == null) {
                        r = h.getDrawingArea();
                    } else {
                        r.add(h.getDrawingArea());
                    }
                }
            }
            if (r != null) {
                r.grow(1, 1);
                fireAreaInvalidated(r);
            }
        }
    }
    
    private Rectangle rubberband = new Rectangle();
    private LinkedList<Handle> hoverHandles = new LinkedList<Handle>();
    private Figure hoverFigure = null;

	@Override
	public void mouseReleased(MouseEvent evt) {
		if (getView() != null && getView().isEnabled()) {
		}
	}

	@Override
	public void draw(Graphics2D g) {
		if (hoverHandles.size() > 0 && !getView().isFigureSelected(hoverFigure)) {
            for (Handle h : hoverHandles) {
                h.draw(g);
            }
        }
	}

	@Override
	public void mousePressed(MouseEvent evt) {
		if (getView() != null && getView().isEnabled()) {
			super.mousePressed(evt);
			DrawingView view = getView();
			Handle handle = view.findHandle(anchor);
			Tool newTracker = null;
			if (handle != null) {
//				newTracker = getHandleTracker(handle);
			} else {
				Figure figure;
				Drawing drawing = view.getDrawing();
				Point2D.Double p = view.viewToDrawing(anchor);
				if (isSelectBehindEnabled()
						&& (evt.getModifiersEx() & (InputEvent.ALT_DOWN_MASK | InputEvent.CTRL_DOWN_MASK)) != 0) {
					// Select a figure behind the current selection
					figure = view.findFigure(anchor);
					while (figure != null && !figure.isSelectable()) {
						figure = drawing.findFigureBehind(p, figure);
					}
					HashSet<Figure> ignoredFigures = new HashSet<Figure>(view
							.getSelectedFigures());
					ignoredFigures.add(figure);
					Figure figureBehind = view.getDrawing().findFigureBehind(
							view.viewToDrawing(anchor), ignoredFigures);
					if (figureBehind != null) {
						figure = figureBehind;
					}
				} else {
					figure = null;
					if (figure == null) {
						figure = view.getDrawing().findFigureInside(viewToDrawing(anchor));
						while (figure != null && !figure.isSelectable()) {
							figure = drawing.findFigureBehind(p, figure);
						}
					}
				}
				
				if (figure != null) {
					if (evt.isShiftDown()) {
						view.setHandleDetailLevel(0);
						view.toggleSelection(figure);
						if (!view.isFigureSelected(figure)) {
//							anchorFigure = null;
						}
					} else if (!view.isFigureSelected(figure)) {
						view.setHandleDetailLevel(0);
						view.clearSelection();
						view.addToSelection(figure);
					}
				}
			}
			
		}
	}

	/**
	 * Returns true, if this tool lets the user interact with handles.
	 * <p>
	 * Handles may draw differently, if interaction is not possible.
	 * 
	 * @return True, if this tool supports interaction with the handles.
	 */
	public boolean supportsHandleInteraction() {
		return true;
	}

	private static final long serialVersionUID = -467641498789051549L;
}