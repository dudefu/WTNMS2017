package com.jhw.adm.client.draw;

import com.jhw.adm.client.core.Adaptable;

/**
 * 拓扑节点编辑、控制<br>
 * NodeEdit 对应 MVC 模式中的控制器，它维护着视图与模型的对应关系<br>
 * createFigure 方法负责创建 NodeFigure 图形，refreshVisuals 方法负责对 NodeFigure 图形进行更新
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