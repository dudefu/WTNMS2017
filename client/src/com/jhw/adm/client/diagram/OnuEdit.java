package com.jhw.adm.client.diagram;

import static com.jhw.adm.client.util.NodeUtils.getNodeText;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.jhotdraw.util.Images;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.draw.EquipmentEdit;
import com.jhw.adm.client.draw.EquipmentFigure;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.server.entity.epon.ONUEntity;
import com.jhw.adm.server.entity.tuopos.ONUTopoNodeEntity;
import com.jhw.adm.server.entity.util.AddressEntity;

public class OnuEdit extends EquipmentEdit {
	
	public OnuEdit() {
		this.imageRegistry = ClientUtils.getSpringBean(ImageRegistry.ID);
	}
	
	@Override
	public EquipmentFigure createFigure() {
		BufferedImage bufferedImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.ONU));
		EquipmentFigure figure = new EquipmentFigure(bufferedImage);
		setFigure(figure);
		updateAttributes();
		figure.setEdit(this);
		
		return figure;
	}
	
	@Override
	public EquipmentFigure restoreFigure(Object figureModel) {
		String text = StringUtils.EMPTY;
		double x = 0;
		double y = 0;
		BufferedImage bufferedImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.ONU));
		if (figureModel instanceof ONUTopoNodeEntity) {
			ONUTopoNodeEntity onuNode = (ONUTopoNodeEntity) figureModel;
			text = getNodeText(onuNode);
	    	x = onuNode.getX();
	    	y = onuNode.getY();
		}
		EquipmentFigure figure = new EquipmentFigure(bufferedImage);
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
	public ONUTopoNodeEntity createModel() {
		ONUTopoNodeEntity onuNode = new ONUTopoNodeEntity();
		ONUEntity onu = new ONUEntity();
		onu.setSyschorized(false);
		AddressEntity address = new AddressEntity();
		onu.setAddressEntity(address);
		onuNode.setOnuEntity(onu);
		onuNode.setGuid(UUID.randomUUID().toString());
		
		this.setModel(onuNode);
		return onuNode;
	}
	
	@Override
	public void updateAttributes() {
		EquipmentFigure figure = getFigure();
		Object figureModel = getModel();
		if (figureModel instanceof ONUTopoNodeEntity) {
			ONUTopoNodeEntity onuNode = (ONUTopoNodeEntity)figureModel;
			figure.willChange();
			onuNode.setModifyNode(true);
			figure.setText(getNodeText(onuNode));
			figure.fireAreaInvalidated();
			figure.changed();
		}
	}

	@Override
	public void updateModel() {
		EquipmentFigure figure = getFigure();
		Object figureModel = getModel();
		if (figureModel instanceof ONUTopoNodeEntity) {
			ONUTopoNodeEntity onuNode = (ONUTopoNodeEntity) figureModel;
			onuNode.setModifyNode(true);
			onuNode.setX(figure.getBounds().x);
			onuNode.setY(figure.getBounds().y);
		}
	}

	private final ImageRegistry imageRegistry;
	private static final Logger LOG = LoggerFactory.getLogger(OnuEdit.class);
}