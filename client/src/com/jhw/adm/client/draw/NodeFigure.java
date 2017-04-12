package com.jhw.adm.client.draw;

import java.util.List;

import javax.swing.Action;

import org.jhotdraw.draw.Figure;

import com.jhw.adm.client.core.Adaptable;

/**
 * 拓扑节点图形，对应 MVC 模式中的视图<br>
 */
public interface NodeFigure extends Figure, Adaptable {
	public NodeEdit<? extends NodeFigure> getEdit();
	
	public Action getDoubleClickAction();
	public void setDoubleClickAction(Action doubleClickAction);
	
	public void setActions(List<Action> actions);
}