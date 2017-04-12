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
import com.jhw.adm.client.util.NodeUtils;
import com.jhw.adm.server.entity.nets.FEPEntity;
import com.jhw.adm.server.entity.nets.StatusEntity;
import com.jhw.adm.server.entity.tuopos.FEPTopoNodeEntity;

/**
 * 前置机控制类
 */
public class FrontEndEdit extends EquipmentEdit {
	
	public FrontEndEdit() {
		imageRegistry = ClientUtils.getSpringBean(ImageRegistry.ID);
		alarmImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.ALARM));
	}

	@Override
	public EquipmentFigure createFigure() {
		BufferedImage bufferedImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.FRONT_END));
//		BufferedImage statusImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.OFFLINE));
		EquipmentFigure figure = new EquipmentFigure(bufferedImage);
//		figure.setStatusImage(statusImage);
		setFigure(figure);
		updateAttributes();
		figure.setEdit(this);
		
		return figure;
	}

	@Override
	public Object createModel() {
		FEPTopoNodeEntity frontEndNode = new FEPTopoNodeEntity();
		frontEndNode.setGuid(UUID.randomUUID().toString());
		FEPEntity frontEnd = new FEPEntity();
		frontEnd.setCode("WTFEP");
		frontEnd.setFepName(StringUtils.EMPTY);
		frontEnd.setLoginName(StringUtils.EMPTY);
		frontEnd.setLoginPassword(StringUtils.EMPTY);
		frontEnd.setIpValue(StringUtils.EMPTY);
		frontEnd.setDirectSwitchIp(StringUtils.EMPTY);
		StatusEntity statusEntity = new StatusEntity();
		frontEnd.setStatus(statusEntity);
		frontEndNode.setFepEntity(frontEnd);
		frontEndNode.setCode(frontEnd.getCode());
		this.setModel(frontEndNode);
		return frontEndNode;
	}

	@Override
	public EquipmentFigure restoreFigure(Object figureModel) {
		String text = StringUtils.EMPTY;
		double x = 0;
		double y = 0;
		BufferedImage bufferedImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.FRONT_END));
		BufferedImage statusImage = null;;
		if (figureModel instanceof FEPTopoNodeEntity) {
			FEPTopoNodeEntity frontEndNode = (FEPTopoNodeEntity) figureModel;
			text = getNodeText(frontEndNode);
	    	x = frontEndNode.getX();
	    	y = frontEndNode.getY();
	    	FEPEntity fepEntity = NodeUtils.getNodeEntity(frontEndNode).getFepEntity();
			if(null != fepEntity.getStatus() && !fepEntity.getStatus().isStatus()) {
				statusImage = alarmImage;
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
//		updateAttributes(figureModel);
		figure.setEdit(this);
		return figure;
	}

	@Override
	public void updateModel() {
		EquipmentFigure figure = getFigure();
		Object figureModel = getModel();
		if (figureModel instanceof FEPTopoNodeEntity) {
			FEPTopoNodeEntity frontEndNode = (FEPTopoNodeEntity) figureModel;
			frontEndNode.setModifyNode(true);
			frontEndNode.setX(figure.getBounds().x);
			frontEndNode.setY(figure.getBounds().y);
		}
	}

	@Override
	public void updateAttributes() {
		EquipmentFigure figure = getFigure();
		Object figureModel = getModel();
		if (figureModel instanceof FEPTopoNodeEntity) {
			FEPTopoNodeEntity frontEndNode = (FEPTopoNodeEntity) figureModel;
			BufferedImage statusImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.ALARM));
			
			if (frontEndNode.getFepEntity() != null && 
				frontEndNode.getFepEntity().getStatus() != null &&
				frontEndNode.getFepEntity().getStatus().isStatus()) {
				statusImage = null;
				LOG.debug(String.format("前置机[ID:%s;IP:%s;Code:%s]在线", 
						frontEndNode.getFepEntity().getId(), 
						frontEndNode.getFepEntity().getIpValue(),
						frontEndNode.getFepEntity().getCode()));
			}
			
			figure.willChange();
			figure.setText(getNodeText(frontEndNode));
			figure.setStatusImage(statusImage);
			figure.fireAreaInvalidated();
			figure.changed();
		}
	}
	
	private final BufferedImage alarmImage;
	private final ImageRegistry imageRegistry;
	private static final Logger LOG = LoggerFactory.getLogger(FrontEndEdit.class);
}