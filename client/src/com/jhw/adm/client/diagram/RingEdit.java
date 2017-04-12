package com.jhw.adm.client.diagram;

import java.awt.geom.AffineTransform;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhw.adm.client.draw.CircleLineFigure;
import com.jhw.adm.client.draw.NodeEdit;
import com.jhw.adm.server.entity.tuopos.RingEntity;

public class RingEdit implements NodeEdit<CircleLineFigure> {
	
	public RingEdit() {
	}
	
	@Override
	public RingEntity createModel() {
		RingEntity ringNode = new RingEntity();
		ringNode.setRing_guid(UUID.randomUUID().toString());
		this.setModel(ringNode);
		return ringNode;
	}
	
	@Override
	public CircleLineFigure createFigure() {
		CircleLineFigure figure = new CircleLineFigure();
		setFigure(figure);
		figure.setEdit(this);
		
		return figure;
	}
		
	@Override
	public CircleLineFigure restoreFigure(Object figureModel) {
		double x = 0;
		double y = 0;
		if (figureModel instanceof RingEntity) {
			RingEntity ringNode = (RingEntity) figureModel;
	    	x = ringNode.getX();
	    	y = ringNode.getY();
		}
		CircleLineFigure figure = new CircleLineFigure();

		AffineTransform at = new AffineTransform();
		at.translate(x, y);
		figure.transform(at);
		
		setFigure(figure);
		setModel(figureModel);
		figure.setEdit(this);
		return figure;
	}

	@Override
	public void updateAttributes() {
	}

	@Override
	public void updateModel() {
		CircleLineFigure figure = getFigure();
		Object figureModel = getModel();
		if (figureModel instanceof RingEntity) {
			RingEntity ringNode = (RingEntity) figureModel;
			ringNode.setX(figure.getBounds().x);
			ringNode.setY(figure.getBounds().y);
		}
	}


	@Override
	public CircleLineFigure getFigure() {
		return figure;
	}

	@Override
	public Object getAdapter(Class<?> adapterClass) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setFigure(CircleLineFigure figure) {
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
	private CircleLineFigure figure;
	private static final Logger LOG = LoggerFactory.getLogger(RingEdit.class);
}