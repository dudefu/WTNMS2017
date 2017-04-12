package com.jhw.adm.client.draw;

import java.util.List;

import javax.swing.Action;

import org.jhotdraw.draw.Figure;

import com.jhw.adm.client.core.Adaptable;

/**
 * ���˽ڵ�ͼ�Σ���Ӧ MVC ģʽ�е���ͼ<br>
 */
public interface NodeFigure extends Figure, Adaptable {
	public NodeEdit<? extends NodeFigure> getEdit();
	
	public Action getDoubleClickAction();
	public void setDoubleClickAction(Action doubleClickAction);
	
	public void setActions(List<Action> actions);
}