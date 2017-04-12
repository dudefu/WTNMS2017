package com.jhw.adm.client.draw;

public class CommentEdit implements NodeEdit<CommentAreaFigure> {

	@Override
	public CommentAreaFigure createFigure() {
		return null;
	}

	@Override
	public Object createModel() {
		return null;
	}

	@Override
	public CommentAreaFigure restoreFigure(Object model) {
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
	public CommentAreaFigure getFigure() {
		return figure;
	}
	
	@Override
	public Object getModel() {
		return model;
	}
	
	public void setModel(Object model) {
		this.model = model;
	}
	
	public void setFigure(CommentAreaFigure figure) {
		this.figure = figure;
	}

	private Object model;
	private CommentAreaFigure figure;

}
