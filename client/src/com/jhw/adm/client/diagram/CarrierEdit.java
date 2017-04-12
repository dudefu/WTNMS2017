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
import com.jhw.adm.client.draw.CarrierFigure;
import com.jhw.adm.client.draw.EquipmentEdit;
import com.jhw.adm.client.draw.EquipmentFigure;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.server.entity.carriers.CarrierEntity;
import com.jhw.adm.server.entity.tuopos.CarrierTopNodeEntity;

public class CarrierEdit extends EquipmentEdit {
	
	public CarrierEdit() {
		imageRegistry = ClientUtils.getSpringBean(ImageRegistry.ID);
	}

	@Override
	public CarrierFigure createFigure() {
		BufferedImage bufferedImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.CARRIER));
		CarrierFigure figure = new CarrierFigure(bufferedImage);
		BufferedImage statusImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.OFFLINE));
		figure.setStatusImage(statusImage);
		setFigure(figure);
		figure.setEdit(this);
		
		return figure;
	}

	@Override
	public CarrierTopNodeEntity createModel() {
		CarrierTopNodeEntity carrierNode = new CarrierTopNodeEntity();
		carrierNode.setStatus(BooleanUtils.toInteger(false));
		carrierNode.setSynchorized(true);
		carrierNode.setGuid(UUID.randomUUID().toString());
		carrierNode.setCarrierCode(1);
		CarrierEntity carrier = new CarrierEntity();
		int defCategory = 2;
		carrier.setType(defCategory);
		UUID uudi = UUID.randomUUID();
		carrier.setGuid(uudi.toString());
		carrier.setCarrierCode(carrierNode.getCarrierCode());
		
//		生成默认的路由
//		CarrierRouteEntity route = new CarrierRouteEntity();
//		route.setCarrierCode(ApplicationConstants.ADM_SOURCE_ID);
//		route.setPort(CarrierPort.DEFAULT_PORT);
//		route.setCarrier(carrier);
//		
//		Set<CarrierRouteEntity> setOfRoute = new HashSet<CarrierRouteEntity>();
//		setOfRoute.add(route);
//		carrier.setRoutes(setOfRoute);
		
		carrierNode.setNodeEntity(carrier);
		this.setModel(carrierNode);
		return carrierNode;
	}

	@Override
	public CarrierFigure restoreFigure(Object figureModel) {
		String text = StringUtils.EMPTY;
		double x = 0;
		double y = 0;
		BufferedImage bufferedImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.CARRIER));
		BufferedImage statusImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.OFFLINE));
		if (figureModel instanceof CarrierTopNodeEntity) {
			CarrierTopNodeEntity carrierNode = (CarrierTopNodeEntity) figureModel;

	    	text = getNodeText(carrierNode);
	    	x = carrierNode.getX();
	    	y = carrierNode.getY();
	    	
	    	if (BooleanUtils.toBoolean(carrierNode.getStatus())) {
	    		statusImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.ONLINE));
	    	}
//	    	statusImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.ONLINE));
		}
		CarrierFigure figure = new CarrierFigure(bufferedImage);
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
		if (figureModel instanceof CarrierTopNodeEntity) {
			CarrierTopNodeEntity carrierNode = (CarrierTopNodeEntity) figureModel;
			carrierNode.setModifyNode(true);
			carrierNode.setX(figure.getBounds().x);
			carrierNode.setY(figure.getBounds().y);
		}
	}

	@Override
	public void updateAttributes() {
		EquipmentFigure figure = getFigure();
		Object figureModel = getModel();
		if (figureModel instanceof CarrierTopNodeEntity) {
			CarrierTopNodeEntity carrierNode = (CarrierTopNodeEntity) figureModel;
			BufferedImage statusImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.OFFLINE));
			
			if (BooleanUtils.toBoolean(carrierNode.getStatus())) {
	    		statusImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.ONLINE));
	    	}
//	    	statusImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.ONLINE));
			
			figure.willChange();
			figure.setText(getNodeText(carrierNode));
			figure.setStatusImage(statusImage);
//			figure.fireAreaInvalidated();
			figure.changed();
		}
	}
	
	private final ImageRegistry imageRegistry;
	private static final Logger LOG = LoggerFactory.getLogger(CarrierEdit.class);
}