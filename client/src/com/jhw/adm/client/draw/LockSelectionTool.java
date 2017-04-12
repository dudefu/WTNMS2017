package com.jhw.adm.client.draw;

import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.tool.DragTracker;

public class LockSelectionTool extends PopupSelectionTool {
 
    @Override
	protected DragTracker getDragTracker(Figure f) {
    	if (dragTracker == null) {
			dragTracker = new LockDragTracker();
		}
		dragTracker.setDraggedFigure(f);
		return dragTracker;
	}

    private DragTracker dragTracker;
	private static final long serialVersionUID = -5477554962116584118L;
}