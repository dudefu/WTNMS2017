package com.jhw.adm.client.draw;

import javax.swing.ActionMap;

import org.jhotdraw.app.action.edit.SelectAllAction;
import org.jhotdraw.draw.DefaultDrawingEditor;
import org.jhotdraw.draw.action.IncreaseHandleDetailLevelAction;
import org.jhotdraw.draw.action.MoveAction;

public class NetworkDrawingEditor extends DefaultDrawingEditor {

	public NetworkDrawingEditor() {
		super();
	}

	@Override
    protected ActionMap createActionMap() {
        ActionMap actionMap = new ActionMap();

        actionMap.put(DeleteFigureAllAction.ID, new DeleteFigureAllAction());
        actionMap.put(SelectAllAction.ID, new SelectAllAction());
        actionMap.put(IncreaseHandleDetailLevelAction.ID, new IncreaseHandleDetailLevelAction(this));

        actionMap.put(MoveAction.East.ID, new MoveAction.East(this));
        actionMap.put(MoveAction.West.ID, new MoveAction.West(this));
        actionMap.put(MoveAction.North.ID, new MoveAction.North(this));
        actionMap.put(MoveAction.South.ID, new MoveAction.South(this));
        actionMap.put(KeyMoveConstrainedAction.East.ID, new KeyMoveConstrainedAction.East(this));
        actionMap.put(KeyMoveConstrainedAction.West.ID, new KeyMoveConstrainedAction.West(this));
        actionMap.put(KeyMoveConstrainedAction.North.ID, new KeyMoveConstrainedAction.North(this));
        actionMap.put(KeyMoveConstrainedAction.South.ID, new KeyMoveConstrainedAction.South(this));

        return actionMap;
    }
	
	/**
	 * È¥³ýÉ¾³ý¿ì½Ý¼ü
	 */
	public void removeDeletion() {
		getActionMap().remove(DeleteFigureAllAction.ID);
	}
    
    public void setDeletionStrategy(DeletionStrategy startegy) {
    	((DeleteFigureAllAction)getActionMap().get(DeleteFigureAllAction.ID)).setDrawingView(getActiveView());
    	((DeleteFigureAllAction)getActionMap().get(DeleteFigureAllAction.ID)).setDeletionStrategy(startegy);
    }

	private static final long serialVersionUID = -4827732869507974680L;
}