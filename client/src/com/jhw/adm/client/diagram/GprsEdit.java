package com.jhw.adm.client.diagram;

import static com.jhw.adm.client.util.NodeUtils.getNodeText;

import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.UUID;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.jhotdraw.util.Images;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.draw.EquipmentEdit;
import com.jhw.adm.client.draw.EquipmentFigure;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.server.entity.tuopos.GPRSTopoNodeEntity;
import com.jhw.adm.server.entity.util.GPRSEntity;

public class GprsEdit extends EquipmentEdit {
	
	public GprsEdit() {
		this.imageRegistry = ClientUtils.getSpringBean(ImageRegistry.ID);
		this.remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
	}
	
	@Override
	public EquipmentFigure createFigure() {
		BufferedImage bufferedImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.GPRS));
		EquipmentFigure figure = new EquipmentFigure(bufferedImage);
		BufferedImage statusImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.OFFLINE));
		figure.setStatusImage(statusImage);
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
		BufferedImage bufferedImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.GPRS));
		BufferedImage statusImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.OFFLINE));
		if (figureModel instanceof GPRSTopoNodeEntity) {
			GPRSTopoNodeEntity gprsNode = (GPRSTopoNodeEntity) figureModel;
	    	text = getNodeText(gprsNode);
	    	x = gprsNode.getX();
	    	y = gprsNode.getY();
	    	
	    	GPRSEntity gprsEntity = remoteServer.getService().getGPRSEntityByUserId(gprsNode.getUserId());
	    	if(null != gprsEntity){
	    		if (BooleanUtils.toBoolean(gprsEntity.getStatus())) {
	    			statusImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.ONLINE));
	    		}
	    	}
		}
		EquipmentFigure figure = new EquipmentFigure(bufferedImage);		
		figure.willChange();
		figure.setStatusImage(statusImage);
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
	public GPRSTopoNodeEntity createModel() {
		GPRSTopoNodeEntity gprsNode = new GPRSTopoNodeEntity();
		gprsNode.setGuid(UUID.randomUUID().toString());
		GPRSEntity gprs = new GPRSEntity();
		gprs.setGuid(UUID.randomUUID().toString());
		
		gprs.setUserId("13000000000");
		gprsNode.setEntity(gprs);
		this.setModel(gprsNode);
		return gprsNode;
	}
	
	@Override
	public void updateAttributes() {
		EquipmentFigure figure = getFigure();
		Object figureModel = getModel();
		if (figureModel instanceof GPRSTopoNodeEntity) {
			GPRSTopoNodeEntity gprsNode = (GPRSTopoNodeEntity) figureModel;
			BufferedImage statusImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.OFFLINE));

			if (BooleanUtils.toBoolean(gprsNode.getEntity().getStatus())) {
				statusImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.ONLINE));
			}
			figure.willChange();
			figure.setText(getNodeText(gprsNode));
			figure.setStatusImage(statusImage);
			figure.fireAreaInvalidated();
			figure.changed();
		}
	}

	@Override
	public void updateModel() {
		EquipmentFigure figure = getFigure();
		Object figureModel = getModel();
		if (figureModel instanceof GPRSTopoNodeEntity) {
			GPRSTopoNodeEntity gprsNode = (GPRSTopoNodeEntity) figureModel;
			gprsNode.setModifyNode(true);
			gprsNode.setX(figure.getBounds().x);
			gprsNode.setY(figure.getBounds().y);
		}
	}

	private final ImageRegistry imageRegistry;
	private final RemoteServer remoteServer;
	private static final Logger LOG = LoggerFactory.getLogger(GprsEdit.class);
}