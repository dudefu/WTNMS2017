package com.jhw.adm.client.draw;


public interface LinkEdit<T extends LinkFigure> extends NodeEdit<T> {
	public boolean canConnect(NodeFigure start, NodeFigure end);
	public boolean handleConnected();
}
