package com.jhw.adm.client.diagram;

import java.awt.geom.AffineTransform;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.jhw.adm.client.draw.CommentAreaFigure;
import com.jhw.adm.client.draw.CommentEdit;
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.server.entity.tuopos.CommentTopoNodeEntity;

public class CommentAreaEdit extends CommentEdit {
	
	public CommentAreaEdit(){
		
	}
	
	@Override
	public CommentAreaFigure createFigure() {
		CommentAreaFigure figure = new CommentAreaFigure();
		setFigure(figure);
		updateAttributes();
		figure.setEdit(this);
		return figure;
	}
	
	@Override
	public Object createModel() {
		CommentTopoNodeEntity commentNode = new CommentTopoNodeEntity();
		commentNode.setGuid(UUID.randomUUID().toString());
		this.setModel(commentNode);
		return commentNode;
	}
	
	@Override
	public CommentAreaFigure restoreFigure(Object figureModel) {
		String text = StringUtils.EMPTY;
		double x = 0;
		double y = 0;
		CommentTopoNodeEntity commentNode = null;
		if (figureModel instanceof CommentTopoNodeEntity) {
			commentNode = (CommentTopoNodeEntity) figureModel;
			text = NodeUtils.getNodeText(commentNode);
	    	x = commentNode.getX();
	    	y = commentNode.getY();
		}
		CommentAreaFigure figure = new CommentAreaFigure();
		figure.willChange();
		figure.setText(text);
		figure.changed();

		AffineTransform at = new AffineTransform();
		at.translate(x, y);
		figure.transform(at);
		setFigure(figure);
		setModel(figureModel);
		figure.setEdit(this);
		return figure;
	}
	
	@Override
	public void updateModel() {
		CommentAreaFigure figure = getFigure();
		Object figureModel = getModel();
		if (figureModel instanceof CommentTopoNodeEntity) {
			CommentTopoNodeEntity commentNode = (CommentTopoNodeEntity) figureModel;
			commentNode.setModifyNode(true);
			commentNode.setX(figure.getBounds().x);
			commentNode.setY(figure.getBounds().y);
		}
	}

	@Override
	public void updateAttributes() {
		CommentAreaFigure figure = getFigure();
		Object figureModel = getModel();
		if (figureModel instanceof CommentTopoNodeEntity) {
			CommentTopoNodeEntity commentNode = (CommentTopoNodeEntity) figureModel;
			
			figure.willChange();
			figure.setText(NodeUtils.getNodeText(commentNode));
			figure.changed();
		}
	}

}
