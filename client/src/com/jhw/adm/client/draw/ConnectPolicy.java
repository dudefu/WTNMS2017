package com.jhw.adm.client.draw;

/**
 * ���Ӳ���
 */
public interface ConnectPolicy {
	public boolean canConnect(NodeFigure startFigure, NodeFigure endFigure);
	public void connected(LinkFigure linkFigure);
}