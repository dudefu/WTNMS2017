package com.jhw.adm.client.draw;


public class EquipmentEdit implements NodeEdit<EquipmentFigure> {

	@Override
	public EquipmentFigure createFigure() {
		return null;
	}

	@Override
	public Object createModel() {
		return null;
	}


	@Override
	public EquipmentFigure restoreFigure(Object model) {
		return null;
	}

	@Override
	public void updateAttributes() {
	}

	@Override
	public void updateModel() {		
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		return null;
	}

	@Override
	public EquipmentFigure getFigure() {
		return figure;
	}
	
	@Override
	public Object getModel() {
		return model;
	}
	
	public void setModel(Object model) {
		this.model = model;
	}
	
	public void setFigure(EquipmentFigure figure) {
		this.figure = figure;
	}

	private Object model;
	private EquipmentFigure figure;
}