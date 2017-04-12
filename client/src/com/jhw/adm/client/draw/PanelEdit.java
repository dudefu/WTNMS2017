package com.jhw.adm.client.draw;


public class PanelEdit implements NodeEdit<PanelFigure> {

	@Override
	public PanelFigure createFigure() {
		return null;
	}

	@Override
	public Object createModel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PanelFigure getFigure() {
		return figure;
	}

	@Override
	public Object getModel() {
		return model;
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PanelFigure restoreFigure(Object model) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateAttributes() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateModel() {
		// TODO Auto-generated method stub
		
	}
	
	public void setModel(Object model) {
		this.model = model;
	}
	
	public void setFigure(PanelFigure figure) {
		this.figure = figure;
	}

	private Object model;
	private PanelFigure figure;
}
