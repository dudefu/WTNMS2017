package com.jhw.adm.client.draw;

import javax.swing.Action;

import org.jhotdraw.draw.Figure;

public interface PopupActionFilter {

	public boolean getActionEnable(Figure selectedFigure, Action action);
}
