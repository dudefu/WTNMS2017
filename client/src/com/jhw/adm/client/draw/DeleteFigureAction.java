package com.jhw.adm.client.draw;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractAction;

import org.jhotdraw.beans.WeakPropertyChangeListener;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.gui.EditableComponent;

public class DeleteFigureAction extends AbstractAction {
	
    public DeleteFigureAction() {
        super(ID);
    }
    
	@Override
	public void actionPerformed(ActionEvent e) {
        if (drawingView != null && drawingView.isEnabled() && canDelete()) {
            if (drawingView instanceof org.jhotdraw.gui.EditableComponent) {
                ((EditableComponent) drawingView).delete();
            } else {
            	Toolkit.getDefaultToolkit().beep();
            }
        }
	}
	
	private boolean canDelete() {
		return deletionStrategy == null ? false : deletionStrategy.canDelete();
	}
	
    public DeletionStrategy getDeletionStrategy() {
		return deletionStrategy;
	}

	public void setDeletionStrategy(DeletionStrategy deletionStrategy) {
		this.deletionStrategy = deletionStrategy;
	}

    public DrawingView getDrawingView() {
		return drawingView;
	}

	public void setDrawingView(DrawingView drawingView) {
		this.drawingView = drawingView;
        if (drawingView != null) {
            propertyHandler = new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (evt.getPropertyName().equals("enabled")) {
                        setEnabled((Boolean) evt.getNewValue());
                    }
                }
            };
            drawingView.addPropertyChangeListener(new WeakPropertyChangeListener(propertyHandler));
        }
	}
	
	private DeletionStrategy deletionStrategy;
	private DrawingView drawingView;
	private PropertyChangeListener propertyHandler;
	private static final long serialVersionUID = 8527094665854311244L;
    public final static String ID = "edit.delete";
}