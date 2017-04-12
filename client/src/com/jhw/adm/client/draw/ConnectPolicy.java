package com.jhw.adm.client.draw;

/**
 * Á¬½Ó²ßÂÔ
 */
public interface ConnectPolicy {
	public boolean canConnect(NodeFigure startFigure, NodeFigure endFigure);
	public void connected(LinkFigure linkFigure);
}