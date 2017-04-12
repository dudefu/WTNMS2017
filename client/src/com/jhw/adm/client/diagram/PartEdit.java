package com.jhw.adm.client.diagram;

import com.jhw.adm.client.draw.NodeEdit;
import com.jhw.adm.client.draw.PartFigure;

public class PartEdit implements NodeEdit<PartFigure> {

	@Override
	public PartFigure createFigure() {
		PartFigure figure = new PartFigure();
		return figure;
	}

	@Override
	public Object createModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PartFigure restoreFigure(Object model) {
		PartFigure figure = new PartFigure();
		setFigure(figure);
		setModel(model);
		figure.setEdit(this);
		return figure;
	}

	@Override
	public void updateAttributes() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateModel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public PartFigure getFigure() {
		return figure;
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setFigure(PartFigure figure) {
		this.figure = figure;
	}
	
	@Override
	public Object getModel() {
		return model;
	}

//	@Override
	public void setModel(Object model) {
		this.model = model;
	}

	private Object model;
	private PartFigure figure;
}