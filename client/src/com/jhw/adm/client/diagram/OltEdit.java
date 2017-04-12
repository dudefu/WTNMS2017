package com.jhw.adm.client.diagram;

import static com.jhw.adm.client.util.NodeUtils.getNodeText;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.jhotdraw.util.Images;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jhw.adm.client.core.ImageRegistry;
import com.jhw.adm.client.core.NetworkConstants;
import com.jhw.adm.client.core.RemoteServer;
import com.jhw.adm.client.draw.EquipmentEdit;
import com.jhw.adm.client.draw.EquipmentFigure;
import com.jhw.adm.client.model.epon.OLTTypeModel;
import com.jhw.adm.client.util.ClientUtils;
import com.jhw.adm.server.entity.epon.OLTEntity;
import com.jhw.adm.server.entity.tuopos.EponTopoEntity;
import com.jhw.adm.server.entity.util.AddressEntity;

public class OltEdit extends EquipmentEdit {
	
	public OltEdit() {
		this.imageRegistry = ClientUtils.getSpringBean(ImageRegistry.ID);
		this.oltTypeModel = ClientUtils.getSpringBean(OLTTypeModel.ID);
		this.remoteServer = ClientUtils.getSpringBean(RemoteServer.ID);
	}
	
	@Override
	public EquipmentFigure createFigure() {
		BufferedImage bufferedImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.SWITCHER));
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
		BufferedImage bufferedImage = Images.toBufferedImage(imageRegistry.getImage(NetworkConstants.SWITCHER));
		if (figureModel instanceof EponTopoEntity) {
			EponTopoEntity eponNode = (EponTopoEntity) figureModel;
			OLTEntity oltEntity = remoteServer.getService().getOLTByIpValue(eponNode.getIpValue());
			
			if(null != oltEntity){
				int deviceModel = oltEntity.getDeviceModel();
				String imageName = oltTypeModel.getImageName(deviceModel);
				Image image = imageRegistry.getImage(imageName);
				if (image == null) {
					LOG.error(String.format("无法找到该型号[%s]OLT的图片", deviceModel));
				} else {
					try {
						bufferedImage = Images.toBufferedImage(image);
					} catch (NullPointerException ex) {
						LOG.error(String.format("无法得到图片[%s]的ColorModel", imageName));
					}
				}
			}
			
			text = getNodeText(eponNode);
	    	x = eponNode.getX();
	    	y = eponNode.getY();
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
	public EponTopoEntity createModel() {
		EponTopoEntity eponNode = new EponTopoEntity();
		OLTEntity olt = new OLTEntity();
		olt.setSyschorized(false);
		AddressEntity address = new AddressEntity();
		olt.setAddress(address);
		eponNode.setOltEntity(olt);
		eponNode.setGuid(UUID.randomUUID().toString());
		
		this.setModel(eponNode);
		return eponNode;
	}
	
	@Override
	public void updateAttributes() {
		EquipmentFigure figure = getFigure();
		Object figureModel = getModel();
		if (figureModel instanceof EponTopoEntity) {
			EponTopoEntity oltNode = (EponTopoEntity)figureModel;
			oltNode.setModifyNode(true);
			figure.willChange();
			figure.setText(getNodeText(oltNode));
			figure.fireAreaInvalidated();
			figure.changed();
		}
	}

	@Override
	public void updateModel() {
		EquipmentFigure figure = getFigure();
		Object figureModel = getModel();
		if (figureModel instanceof EponTopoEntity) {
			EponTopoEntity oltNode = (EponTopoEntity) figureModel;
			oltNode.setModifyNode(true);
			oltNode.setX(figure.getBounds().x);
			oltNode.setY(figure.getBounds().y);
		}
	}
	
	private final RemoteServer remoteServer;
	private final OLTTypeModel oltTypeModel;
	private final ImageRegistry imageRegistry;
	private static final Logger LOG = LoggerFactory.getLogger(OltEdit.class);
}