package com.jhw.adm.client.draw;

import com.jhw.adm.server.entity.util.Constants;


public class LabeledLinkEdit implements LinkEdit<LabeledLinkFigure> {

	@Override
	public LabeledLinkFigure createFigure() {
		return null;
	}

	@Override
	public Object createModel() {
		return null;
	}


	@Override
	public LabeledLinkFigure restoreFigure(Object model) {
		return null;
	}

	@Override
	public void updateAttributes() {		
	}

	@Override
	public void updateModel() {		
	}

	@Override
	public LabeledLinkFigure getFigure() {
		return figure;
	}
	
	@Override
	public boolean canConnect(NodeFigure start, NodeFigure end) {
		return false;
	}

	@Override
	public boolean handleConnected() {
		return false;		
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		return null;
	}
	
	@Override
	public Object getModel() {
		return model;
	}
	
	public void setModel(Object model) {
		this.model = model;
	}
	
	public void setFigure(LabeledLinkFigure figure) {
		this.figure = figure;
	}
	
	public void showAlarm(long value) {
	}
	
	public void closeAlarm() {
	}
	
	public int getStatus() {
		return Constants.L_CONNECT;
	}

	private Object model;
	private LabeledLinkFigure figure;
}