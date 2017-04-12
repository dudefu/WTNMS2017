package com.jhw.adm.client.draw;

import com.jhw.adm.client.core.Adaptable;

/**
 * ���˽ڵ�༭������<br>
 * NodeEdit ��Ӧ MVC ģʽ�еĿ���������ά������ͼ��ģ�͵Ķ�Ӧ��ϵ<br>
 * createFigure �������𴴽� NodeFigure ͼ�Σ�refreshVisuals ��������� NodeFigure ͼ�ν��и���
 */
public interface NodeEdit<F extends NodeFigure> extends Adaptable {
	public F createFigure();
	public F restoreFigure(Object model);
	public Object createModel();
	public void updateModel();
	public void updateAttributes();
	public Object getModel();
	public F getFigure();
}